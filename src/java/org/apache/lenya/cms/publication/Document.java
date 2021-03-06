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

import java.io.File;
import java.util.Date;

/**
 * A CMS document.
 */
public interface Document {
    
    String NAMESPACE = "http://apache.org/cocoon/lenya/document/1.0";
    String DEFAULT_PREFIX = "lenya";
    
    /**
     * Returns the document ID of this document.
     * @return the document-id of this document.
     */
    String getId();
    
    /**
     * Returns the document name of this document.
     * @return the document-name of this document.
     */
    String getName();
    
    /**
     * Instead of returning the full document-id for this
     * document it just returns the id of the particular 
     * node, basically the basename of the document-id.
     * 
     * @return the node id, i.e. the basename of the document-id
     */
    String getNodeId();

    /**
     * Returns the publication this document belongs to.
     * @return A publication object.
     */
    Publication getPublication();

    /**
     * Returns the complete URL of this document in the info area:<br/>
     * /{publication-id}/info-{area}{document-id}{language-suffix}.{extension}
     * @return A string.
     */
    String getCompleteInfoURL();

    /**
     * Returns the complete URL of this document:<br/>
     * /{publication-id}/{area}{document-id}{language-suffix}.{extension}
     * @return A string.
     */
    String getCompleteURL();

    /**
     * Returns the complete URL of this document without 
     * the language-suffix: 
     * /{publication-id}/{area}{document-id}.{extension}
     * The URL always starts with a slash (/).
     * @return A string.
     */
    String getCompleteURLWithoutLanguage();

    /**
     * Returns the URL of this document:
     * {document-id}{language-suffix}.{extension}
     * The URL always starts with a slash (/).
     * @return A string.
     */
    String getDocumentURL();

	/**
	 * Returns the dublin core class for this document.
	 * @return A DublinCore object.
	 */
	DublinCore getDublinCore();

    /**
     * Returns the language of this document.
     * Each document has one language associated to it. 
     * @return A string denoting the language.
     */
    String getLanguage();

	/**
	 * Returns all the languages this document is available in.
     * A document has one associated language (@see Document#getLanguage)
     * but there are possibly a number of other languages for which a 
     * document with the same document-id is also available in. 
     * 
 	 * @return An array of strings denoting the languages.
     * 
     * @throws DocumentException if an error occurs
	 */
	String[] getLanguages() throws DocumentException;

	/**
	 * Get the navigation label associated with this document 
	 * for the language.
	 * 
	 * @return the label String
	 * 
	 * @throws DocumentException if an error occurs
	 */
	String getLabel() throws DocumentException;

	/**
	 * Returns the date of the last modification of this document.
	 * @return A date denoting the date of the last modification.
	 */
	Date getLastModified();

    /**
     * Returns the area this document belongs to.
     * @return The area.
     */
    String getArea();

    /**
     * Returns the file for this document.
     * @return A file object.
     */
    File getFile();

    /**
     * Returns the extension in the URL.
     * @return A string.
     */
    String getExtension();
    
    /**
     * Check if a document with the given document-id, language and in the given
     * area actually exists.
     * 
     * @return true if the document exists, false otherwise
     * 
     * @throws DocumentException if an error occurs
     */
    boolean exists() throws DocumentException;
    
    /**
     * Check if a document exists with the given document-id and the given area
     * independently of the given language.
     * 
     * @return true if a document with the given document-id and area exists,
     * null otherwise
     * 
     * @throws DocumentException if an error occurs
     */
    boolean existsInAnyLanguage() throws DocumentException;
    
}
