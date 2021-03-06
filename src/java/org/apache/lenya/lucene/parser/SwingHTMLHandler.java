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

package org.apache.lenya.lucene.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

import org.apache.log4j.Logger;

public class SwingHTMLHandler extends ParserCallback {
    Logger log = Logger.getLogger(SwingHTMLHandler.class);

    /** 
     * Creates a new instance of SwingHTMLHandler
     */
    public SwingHTMLHandler() {
        debug("\n\n\n\n\nCreating " + getClass().getName());

        // index everything by default
        startIndexing();
    }

    private TagStack tagStack = new TagStack();

    protected TagStack getStack() {
        return tagStack;
    }

    private StringBuffer titleBuffer = new StringBuffer();
    private StringBuffer keywordsBuffer = new StringBuffer();

    /**
     *
     */
    protected void appendToTitle(char[] data) {
        titleBuffer.append(data);
    }

    /**
     * Get title
     *
     * @return DOCUMENT ME!
     */
    public String getTitle() {
        debug("\n\nTitle: " + titleBuffer.toString());

        return titleBuffer.toString();
    }

    /**
     * Get keywords
     *
     * @return DOCUMENT ME!
     */
    public String getKeywords() {
        log.debug("Keywords: " + keywordsBuffer.toString());

        return keywordsBuffer.toString();
    }

    private StringBuffer contentsBuffer = new StringBuffer();

    protected void appendToContents(char[] data) {
        contentsBuffer.append(data);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Reader getReader() {
        debug("\nContents: " + contentsBuffer.toString());

        return new StringReader(contentsBuffer.toString());
    }

    private boolean indexing;

    protected boolean isIndexing() {
        return indexing;
    }

    protected void startIndexing() {
        indexing = true;
    }

    protected void stopIndexing() {
        indexing = false;
    }

    //-------------------------------------------------------------------------
    // Tag handling
    //-------------------------------------------------------------------------

    /**
     * Handles a start tag.
     */
    public void handleStartTag(Tag tag, MutableAttributeSet attributes, int pos) {
        getStack().push(tag);

        // append whitespace
        if (!contentsBuffer.toString().endsWith(" ")) {
            contentsBuffer.append(" ");
        }

        if (tag.equals(HTML.Tag.META)) {
            handleMetaTag(attributes);
        }

        if (tag.equals(HTML.Tag.TITLE)) {
            handleTitleStartTag();
        }

        if (isTagInitialized() && tag.equals(getLuceneTag())) {
            handleLuceneStartTag(tag, attributes);
        }
    }

    /**
     * Handles an end tag.
     */
    public void handleEndTag(Tag tag, int pos) {
        // append whitespace
        if (!contentsBuffer.toString().endsWith(" ")) {
            contentsBuffer.append(" ");
        }

        if (isTagInitialized() && tag.equals(getLuceneTag())) {
            handleLuceneEndTag();
        }

        if (tag.equals(HTML.Tag.TITLE)) {
            handleTitleEndTag();
        }

        try {
            getStack().pop();
        } catch (TagStack.UnderflowException e) {
            log(e);
        }
    }

    //-------------------------------------------------------------------------
    // Title
    //-------------------------------------------------------------------------
    private boolean titleParsing;

    protected boolean isTitleParsing() {
        return titleParsing;
    }

    protected void startTitleParsing() {
        titleParsing = true;
    }

    protected void stopTitleParsing() {
        titleParsing = false;
    }

    protected void handleTitleStartTag() {
        startTitleParsing();
    }

    protected void handleTitleEndTag() {
        stopTitleParsing();
    }

    //-------------------------------------------------------------------------
    // Lucene metag tags
    //-------------------------------------------------------------------------
    public static final String LUCENE_TAG_NAME = "lucene-tag-name";
    public static final String LUCENE_CLASS_VALUE = "lucene-class-value";
    private HTML.Tag luceneTag = null;

    /**
     * Sets the tag name used to avoid indexing.
     */
    protected void setLuceneTag(HTML.Tag tag) {
        debug("Lucene tag:         " + tag);
        luceneTag = tag;
    }

    /**
     * Returns the tag name used to avoid indexing.
     */
    protected HTML.Tag getLuceneTag() {
        return luceneTag;
    }

    private String luceneClassValue = null;

    /**
     * Sets the value for the <code>class</code> attribute used to avoid indexing.
     */
    protected void setLuceneClassValue(String value) {
        debug("Lucene class value: " + value);
        luceneClassValue = value;
    }

    /**
     * Returns the value for the <code>class</code> attribute used to avoid indexing.
     */
    protected String getLuceneClassValue() {
        return luceneClassValue;
    }

    /**
     * Returns if the Lucene META tags are provided.
     */
    protected boolean isTagInitialized() {
        return (getLuceneTag() != null) && (getLuceneClassValue() != null);
    }

    /**
     * Handles a META tag. This method checks for the Lucene configuration tags.
     */
    protected void handleMetaTag(MutableAttributeSet attributes) {
        Object nameObject = attributes.getAttribute(HTML.Attribute.NAME);
        Object valueObject = attributes.getAttribute(HTML.Attribute.VALUE);

        if ((nameObject != null) && (valueObject != null)) {
            String name = (String) nameObject;
            log.debug("Meta tag found: name = " + name);

            if (name.equals(LUCENE_TAG_NAME)) {
                String tagName = (String) valueObject;
                HTML.Tag tag = HTML.getTag(tagName.toLowerCase());
                setLuceneTag(tag);
            }

            if (name.equals(LUCENE_CLASS_VALUE)) {
                setLuceneClassValue((String) valueObject);
            }
        }

        Object contentObject = attributes.getAttribute(HTML.Attribute.CONTENT);
        if ((nameObject != null) && (contentObject != null)) {
            String name = (String) nameObject;
            log.debug("Meta tag found: name = " + name);
            if (name.equals("keywords")) {
                log.debug("Keywords found ...");
                keywordsBuffer = new StringBuffer((String) contentObject);
            }
        }

        // do not index everything if tags are provided
        if (isTagInitialized()) {
            stopIndexing();
        }
    }

    //-------------------------------------------------------------------------
    // Lucene index control tags
    //-------------------------------------------------------------------------
    private TagStack luceneStack = new TagStack();

    protected TagStack getLuceneStack() {
        return luceneStack;
    }

    /**
     * Handles a Lucene index control start tag.
     */
    protected void handleLuceneStartTag(HTML.Tag tag, MutableAttributeSet attributes) {
        Object valueObject = attributes.getAttribute(HTML.Attribute.CLASS);

        if (valueObject != null) {
            String value = (String) valueObject;

            if (value.equals(getLuceneClassValue())) {
                getLuceneStack().push(tag);
                debug("");
                debug("---------- Starting indexing ----------");
                startIndexing();
            }
        }
    }

    /**
     * Handles a Lucene index control end tag.
     */
    protected void handleLuceneEndTag() {
        try {
            HTML.Tag stackTag = getStack().top();

            if (!getLuceneStack().isEmpty()) {
                HTML.Tag luceneTag = getLuceneStack().top();

                if (stackTag == luceneTag) {
                    debug("");
                    debug("---------- Stopping indexing ----------");
                    getLuceneStack().pop();
                    stopIndexing();
                }
            }
        } catch (TagStack.UnderflowException e) {
            log("Lucene index control tag not closed!", e);
        }
    }

    /**
     * Handles an end tag.
     */
    public void handleSimpleTag(Tag tag, MutableAttributeSet attributes, int pos) {
        handleStartTag(tag, attributes, pos);
        handleEndTag(tag, pos);
    }

    //-------------------------------------------------------------------------
    // Text handling
    //-------------------------------------------------------------------------
    public void handleText(char[] data, int pos) {
        //String string = new String(data);
        //System.out.println(indent + string.substring(0, Math.min(20, string.length())) + " ...");
        if (isDebug) {
            System.out.println(".handleText(): data: " + new String(data));
        }

        /*
                if (data[0] == '>') {
                   throw new IllegalStateException();
                   }
        */
        if (isIndexing() || isTitleParsing()) {
            appendToContents(data);
        }

        if (isTitleParsing()) {
            appendToTitle(data);
        }
    }

    //-------------------------------------------------------------------------
    // Logging
    //-------------------------------------------------------------------------
    private boolean isDebug = false;

    /**
     * Logs a message.
     */
    protected void debug(String message) {
        if (isDebug) {
            System.out.println(message);
        }
    }

    /**
     * Logs an exception.
     */
    protected void log(Exception e) {
        log("", e);
    }

    /**
     * Logs an exception with a message.
     */
    protected void log(String message, Exception e) {
        System.out.print(getClass().getName() + ": " + message + " ");
        e.printStackTrace(System.out);
    }

    /**
     * DOCUMENT ME!
     */
    public static class TagStack {
        private List tags = new ArrayList();

        /**
         * DOCUMENT ME!
         *
         * @param tag DOCUMENT ME!
         */
        public void push(HTML.Tag tag) {
            tags.add(0, tag);
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         *
         * @throws UnderflowException DOCUMENT ME!
         */
        public HTML.Tag pop() throws UnderflowException {
            HTML.Tag tag = top();
            tags.remove(tag);

            return tag;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         *
         * @throws UnderflowException DOCUMENT ME!
         */
        public HTML.Tag top() throws UnderflowException {
            HTML.Tag tag = null;

            if (!tags.isEmpty()) {
                tag = (HTML.Tag) tags.get(0);
            } else {
                throw new UnderflowException();
            }

            return tag;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public boolean isEmpty() {
            return tags.isEmpty();
        }

        /**
         * DOCUMENT ME!
         */
        public void dump() {
            System.out.print("stack: ");

            for (Iterator i = tags.iterator(); i.hasNext();) {
                System.out.print(i.next() + ", ");
            }

            System.out.println("");
        }

        /**
         * DOCUMENT ME!
         */
        public static class UnderflowException extends Exception {
            /**
             * Creates a new UnderflowException object.
             */
            public UnderflowException() {
                super("Stack underflow");
            }
        }
    }
}
