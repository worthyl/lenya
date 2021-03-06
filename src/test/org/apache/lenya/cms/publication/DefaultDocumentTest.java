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

package org.apache.lenya.cms.publication;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.lenya.cms.PublicationHelper;


/**
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DefaultDocumentTest extends TestCase {
    /**
     * Constructor.
     * @param test The test.
     */
    public DefaultDocumentTest(String test) {
        super(test);
    }

    /**
     * The main program.
     * The parameters are set from the command line arguments.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        TestRunner.run(getSuite());
    }

    /**
     * Returns the test suite.
     * @return A test suite.
     */
    public static Test getSuite() {
        return new TestSuite(DefaultDocumentTest.class);
    }

    protected static final DocumentTestSet[] testSets = {
        new DocumentTestSet("/index.html", "/index", Publication.AUTHORING_AREA, "en", "html"),
        new DocumentTestSet("/index_en.htm", "/index", Publication.AUTHORING_AREA, "en", "htm"),
        new DocumentTestSet("/index_de.html", "/index", Publication.AUTHORING_AREA, "de", "html")
    };

    /**
     * Tests a document test set.
     * @param testSet The test set.
     * @throws DocumentBuildException when something went wrong.
     */
    protected void doDocumentTest(DocumentTestSet testSet)
        throws DocumentBuildException {
        Document document = getDocument(testSet);
        System.out.println("ID:           " + document.getId());
        System.out.println("Area:         " + document.getArea());
        System.out.println("Language:     " + document.getLanguage());
        System.out.println("Document URL: " + document.getDocumentURL());
        System.out.println("Complete URL: " + document.getCompleteURL());
        System.out.println("Extension:    " + document.getExtension());

        Publication publication = PublicationHelper.getPublication();
        assertEquals(document.getPublication(), publication);
        assertEquals(document.getId(), testSet.getId());
        assertEquals(document.getArea(), testSet.getArea());
        assertEquals(document.getLanguage(), testSet.getLanguage());
        assertEquals(document.getDocumentURL(), testSet.getUrl());
        assertEquals(document.getCompleteURL(),
            "/" + publication.getId() + "/" + document.getArea() + testSet.getUrl());
        assertEquals(document.getExtension(), testSet.getExtension());

        System.out.println("-----------------------------------------------");
    }

    /**
     * Tests the default document.
     * @throws DocumentBuildException when something went wrong.
     */
    public void testDefaultDocument() throws DocumentBuildException {
        for (int i = 0; i < testSets.length; i++) {
            doDocumentTest(testSets[i]);
        }
    }

    /**
     * Returns the test document for a given test set.
     * @param testSet A document test set.
     * @return A document.
     * @throws DocumentBuildException when something went wrong.
     */
    protected Document getDocument(DocumentTestSet testSet)
        throws DocumentBuildException {
        DefaultDocument document = new DefaultDocument(PublicationHelper.getPublication(),
                testSet.getId(), testSet.getArea());
        document.setDocumentURL(testSet.getUrl());
        document.setLanguage(testSet.getLanguage());
        document.setExtension(testSet.getExtension());

        return document;
    }

    /**
     * Utility class to store test data for a document.
     */
    protected static class DocumentTestSet {
        private String url;
        private String id;
        private String extension;
        private String area;
        private String language;

        /**
         * Ctor.
         * @param url The url.
         * @param id The ID.
         * @param area The area.
         * @param language The language.
         * @param extension The extension.
         */
        public DocumentTestSet(String url, String id, String area, String language, String extension) {
            this.url = url;
            this.id = id;
            this.area = area;
            this.language = language;
            this.extension = extension;
        }

        /**
         * @return The area.
         */
        public String getArea() {
            return area;
        }

        /**
         * @return The extension.
         */
        public String getExtension() {
            return extension;
        }

        /**
         * @return The ID.
         */
        public String getId() {
            return id;
        }

        /**
         * @return The language.
         */
        public String getLanguage() {
            return language;
        }

        /**
         * @return The URL.
         */
        public String getUrl() {
            return url;
        }
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        if (PublicationHelper.getPublication() == null) {
            String[] args = { "D:\\Development\\build\\tomcat-4.1.24\\webapps\\lenya", "test" };
            PublicationHelper.extractPublicationArguments(args);
        }
    }
}
