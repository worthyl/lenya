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

package org.apache.lenya.lucene;

import java.io.File;

import org.apache.lenya.xml.DOMUtil;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.XPath;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class IndexConfiguration {
    static Logger log = Logger.getLogger(IndexConfiguration.class);
    private String configurationFilePath;
    private String update_index_type;
    private String index_dir;
    private String htdocs_dump_dir;
    private Class indexerClass;

    /**
     * Creates a new IndexConfiguration object.
     *
     * @param configurationFilePath DOCUMENT ME!
     */
    public IndexConfiguration(String configurationFilePath) {
        this.configurationFilePath = configurationFilePath;

        try {
            File configFile = new File(configurationFilePath);
            Document document = DocumentHelper.readDocument(configFile);
            configure(document.getDocumentElement());
        } catch (Exception e) {
            log.error("Cannot load publishing configuration! ", e);
            System.err.println("Cannot load publishing configuration! " + e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: org.apache.lenya.lucene.IndexConfiguration lucene.xconf");

            return;
        }

        IndexConfiguration ic = new IndexConfiguration(args[0]);
        String parameter;

        parameter = ic.getUpdateIndexType();
        System.out.println("Index type: " + parameter);

        parameter = ic.getIndexDir();
        System.out.println("Index dir: " + parameter);
        System.out.println("Index dir (resolved): " + ic.resolvePath(parameter));

        parameter = ic.getHTDocsDumpDir();
        System.out.println("htdocs_dump: " + parameter);
        System.out.println("htdocs_dump (resolved): " + ic.resolvePath(parameter));

        System.out.println("Indexer class: " + ic.getIndexerClass());
    }

    /**
     * DOCUMENT ME!
     *
     * @param configuration DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void configure(Element root) throws Exception {
        DOMUtil du = new DOMUtil();
        update_index_type = du.getAttributeValue(root, new XPath("update-index/@type"));
        index_dir = du.getAttributeValue(root, new XPath("index-dir/@src"));
        htdocs_dump_dir = du.getAttributeValue(root, new XPath("htdocs-dump-dir/@src"));

        String indexerClassName = du.getAttributeValue(root, new XPath("indexer/@class"));
        indexerClass = Class.forName(indexerClassName);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getUpdateIndexType() {
        log.debug(".getUpdateIndexType(): " + update_index_type);

        return update_index_type;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getIndexDir() {
        log.debug(".getIndexDir(): " + index_dir);

        return index_dir;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHTDocsDumpDir() {
        log.debug(".getHTDocsDumpDir(): " + htdocs_dump_dir);

        return htdocs_dump_dir;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Class getIndexerClass() {
        log.debug(".getIndexerClass(): " + indexerClass);

        return indexerClass;
    }

    /**
     * DOCUMENT ME!
     *
     * @param path DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String resolvePath(String path) {

		// nothing to do if we already have an absolute pathname
		if ( new File(path) .isAbsolute() ) {
			return path;
		}

		// from the Java API doc:  "A canonical pathname is both absolute and unique."
		// however we may get an exception while converting a path to it's canonical form
		try {
			String configDir = new File(configurationFilePath) .getAbsoluteFile() .getParent();
			return new File(configDir, path) .getCanonicalPath();

		} catch (java.io.IOException e) {
			// FIXME: maybe this Exception should be thrown to the caller ?
			e.printStackTrace();
			return null;
		}

    }
}
