/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id$  */

package org.apache.lenya.lucene.index;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.document.DateField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;

public class IndexIterator {
    
    private static Logger log = Logger.getLogger(IndexIterator.class);
    
    /**
     * Creates a new instance of IndexItertor
     */
    public IndexIterator(String index, FileFilter filter) {
        this.filter = filter;
        this.index = index;
    }

    private String index;

    protected String getIndex() {
        return index;
    }

    private FileFilter filter;

    /**
     * @return FileFilter
     */
    protected FileFilter getFilter() {
        return filter;
    }

    private List handlers = new ArrayList();

    /**
     * DOCUMENT ME!
     *
     * @param handler DOCUMENT ME!
     */
    public void addHandler(IndexIteratorHandler handler) {
        if (!handlers.contains(handler)) {
            handlers.add(handler);
        }
    }

    protected void handleFile(File file) {
        for (Iterator i = handlers.iterator(); i.hasNext();) {
            IndexIteratorHandler handler = (IndexIteratorHandler) i.next();
            handler.handleFile(getReader(), file);
        }
    }

    protected void handleStaleDocument(Term term) {
        for (Iterator i = handlers.iterator(); i.hasNext();) {
            IndexIteratorHandler handler = (IndexIteratorHandler) i.next();
            handler.handleStaleDocument(getReader(), term);
        }
    }

    protected void handleUnmodifiedDocument(Term term, File file) {
        for (Iterator i = handlers.iterator(); i.hasNext();) {
            IndexIteratorHandler handler = (IndexIteratorHandler) i.next();
            handler.handleUnmodifiedDocument(getReader(), term, file);
        }
    }

    protected void handleNewDocument(Term term, File file) {
        for (Iterator i = handlers.iterator(); i.hasNext();) {
            IndexIteratorHandler handler = (IndexIteratorHandler) i.next();
            handler.handleNewDocument(getReader(), term, file);
        }
    }

    private IndexReader reader;

    protected IndexReader getReader() {
        return reader;
    }

    /**
     * Iterate over all files within directory
     *
     * @param dumpDirectory Directory over which shall be iterated
     */
    public void iterate(File dumpDirectory) {
        log.info("Iterating files (" + dumpDirectory + ")");

        try {
            reader = IndexReader.open(getIndex());

            TermEnum iterator = enumerateUIDs(getReader());

	    // TODO: Should be configurable
	    boolean sort = false;

	    if (sort) {
                File[] files = getFiles(dumpDirectory);

                for (int i = 0; i < files.length; i++) {
                    iterateFiles(iterator, files[i], dumpDirectory);
                }
            } else {
                log.debug("Do not sort files ...");
                traverse(iterator, dumpDirectory, dumpDirectory);
            }

            // iterate the rest of stale documents
            while ((iterator.term() != null) && iterator.term().field().equals("uid")) {
                handleStaleDocument(iterator.term());
                iterator.next();
            }

            iterator.close();
            reader.close();
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     *
     */
    protected void iterateFiles(TermEnum iterator, File file, File dumpDirectory)
        throws IOException {
        String uid = createUID(file, dumpDirectory);
        log.debug("-----------------------------------------------------");
        log.debug("[file]  file uid: " + uid2url(uid));

        handleFile(file);

        // handle all terms with a smaller uid than the modified file and delete their documents
        while (isStale(iterator.term(), uid)) {
            log.debug("[stale] term uid: " + uid2url(iterator.term().text()));
            handleStaleDocument(iterator.term());
            iterator.next();
        }

        // handle un-modified file
        if (hasEqualUID(iterator.term(), uid)) {
            log.debug("[unmod] term uid: " + uid2url(iterator.term().text()));
            handleUnmodifiedDocument(iterator.term(), file);
            iterator.next();
        }
        // handle new file
        else {
            if (iterator.term() != null) {
                log.debug("[new]   term uid: " + uid2url(iterator.term().text()));
                handleNewDocument(iterator.term(), file);
            }
        }
    }

    /**
     * Returns an term enumerator beginning with the first term that represents a UID field.
     */
    protected TermEnum enumerateUIDs(IndexReader reader) {
        TermEnum tEnum = null;

        try {
            tEnum = reader.terms(new Term("uid", ""));
        } catch (IOException e) {
            log.error("Term enumeration failed: ", e);
        }
            
        return tEnum;
    }

    /**
     * Returns if the term is not null and decribes a UID field.
     */
    protected static boolean isUIDTerm(Term term) {
        return (term != null) && term.field().equals("uid");
    }

    /**
     * Returns <code>true</code> if the file described by uid has a bigger UID than the
     * file described by the existing UID term.
     */
    protected static boolean isStale(Term term, String uid) {
        return isUIDTerm(term) && (term.text().compareTo(uid) < 0);
    }

    /**
     * Returns <code>true</code> if the file described by uid has the same UID as the
     * file described by the existing UID term.
     */
    protected static boolean hasEqualUID(Term term, String uid) {
        return isUIDTerm(term) && term.text().equals(uid);
    }

    /**
     * Create a unique id
     *
     * @param file file to index
     * @param dumpDir dump directory
     *
     * @return id
     */
    public static String createID(File file, File dumpDir) {
        if (dumpDir.getPath().length() <= file.getPath().length()) {
            String id = file.getPath().substring(dumpDir.getPath().length());
            //id = id.replace(File.separatorChar, '\u0000'));
            return id;
        } else {
            log.warn("Length of dumping directory is less than length of file name! Absolute path is being returned as id.");
            return file.getAbsolutePath();
        }
    }

    /**
     * Append path and date into a string in such a way that lexicographic sorting gives the same
     * results as a walk of the file hierarchy.  Thus null (\u0000) is used both to separate
     * directory components and to separate the path from the date.
     *
     * @param file DOCUMENT ME!
     * @param htdocsDumpDir DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String createUID(File file, File htdocsDumpDir) {
        String requestURI = file.getPath().substring(htdocsDumpDir.getPath().length());
        String uid = requestURI.replace(File.separatorChar, '\u0000') + "\u0000" +
            DateField.timeToString(file.lastModified());

        return uid;
    }

    /**
     * Converts a UID to a URL string.
     */
    public static String uid2url(String uid) {
        String url = uid.replace('\u0000', '/'); // replace nulls with slashes
        String timeString = uid.substring(uid.lastIndexOf("\u0000") + 1);
        Date date = DateField.stringToDate(timeString);
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");

        return url.substring(0, url.lastIndexOf('/')) + " " + format.format(date);
    }

    /**
     * Get Files and sorts by alphabet?
     */
    public File[] getFiles(File dumpDirectory) {
        List files = new ArrayList();
        collectFiles(dumpDirectory, files);
        Collections.sort(files);

        Map uidToFile = new HashMap();

        String[] uids = new String[files.size()];

        for (int i = 0; i < uids.length; i++) {
            uids[i] = createUID((File) files.get(i), dumpDirectory);
            uidToFile.put(uids[i], files.get(i));
        }

        Arrays.sort(uids);

        File[] fileArray = new File[uids.length];

        for (int i = 0; i < uids.length; i++) {
            File file = (File) uidToFile.get(uids[i]);
            log.debug(file);
            fileArray[i] = file;
        }

        return fileArray;
    }

    /**
     * Collect files
     */
    protected void collectFiles(File file, List files) {
        if (file.isDirectory()) {
            log.debug("Apply filter " + getFilter().getClass().getName() + " to: " + file);
            File[] fileArray = file.listFiles(getFilter());

            for (int i = 0; i < fileArray.length; i++) {
                collectFiles(fileArray[i], files);
            }
        } else {
            files.add(file);
        }
    }

    /**
     * Traverse directory
     */
    protected void traverse(TermEnum iterator, File file, File dumpDirectory) throws IOException {
        if (file.isDirectory()) {
            log.debug("Apply filter " + getFilter().getClass().getName() + " to: " + file);
            File[] fileArray = file.listFiles(getFilter());

            for (int i = 0; i < fileArray.length; i++) {
                traverse(iterator, fileArray[i], dumpDirectory);
            }
        } else {
            log.debug(file);
            iterateFiles(iterator, file, dumpDirectory);
        }
    }
}
