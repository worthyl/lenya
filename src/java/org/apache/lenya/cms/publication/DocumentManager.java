/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
package org.apache.lenya.cms.publication;

import org.apache.lenya.cms.publication.util.DocumentSet;

/**
 * Helper to manage documents. It takes care of workflow, attachments etc.
 * 
 * @version $Id:$
 */
public interface DocumentManager {
    
    /**
     * The Avalon component role.
     */
    String ROLE = DocumentManager.class.getName();

    /**
     * Copies a document from one location to another location.
     * @param sourceDocument The document to copy.
     * @param destinationDocument The destination document.
     * @throws PublicationException if a document which destinationDocument
     *             depends on does not exist.
     */
    void copy(Document sourceDocument, Document destinationDocument)
            throws PublicationException;

    /**
     * Copies a document to another area.
     * @param sourceDocument The document to copy.
     * @param destinationArea The destination area.
     * @throws PublicationException if a document which the destination document
     *             depends on does not exist.
     */
    void copyToArea(Document sourceDocument, String destinationArea)
            throws PublicationException;

    /**
     * Copies a document set to another area.
     * @param documentSet The document set to copy.
     * @param destinationArea The destination area.
     * @throws PublicationException if a document which one of the destination
     *             documents depends on does not exist.
     */
    void copyToArea(DocumentSet documentSet, String destinationArea)
            throws PublicationException;

    /**
     * Adds a document to the publication.
     * @param document The document.
     * @throws PublicationException if the document is already contained.
     */
    void add(Document document) throws PublicationException;

    /**
     * Deletes a document.
     * @param document The document to delete.
     * @throws PublicationException when something went wrong.
     */
    void delete(Document document) throws PublicationException;

    /**
     * Moves a document from one location to another.
     * @param sourceDocument The source document.
     * @param destinationDocument The destination document.
     * @throws PublicationException if a document which the destination document
     *             depends on does not exist.
     */
    void move(Document sourceDocument, Document destinationDocument)
            throws PublicationException;

    /**
     * Checks if a document can be created. This is the case if the document ID
     * is valid and the document does not yet exist.
     * @param identityMap The identity map to use.
     * @param area The area.
     * @param parent The parent of the document or <code>null</code> if the
     *            document has no parent.
     * @param nodeId The node ID.
     * @param language The language.
     * @return An array of error messages. The array is empty if the document
     *         can be created.
     * @throws DocumentBuildException if an error occurs.
     * @throws DocumentException if an error occurs.
     */
    String[] canCreate(DocumentIdentityMap identityMap, String area, Document parent,
            String nodeId, String language) throws DocumentBuildException, DocumentException;

    /**
     * Checks if the document does already exist. If it does, returns a
     * non-existing document with a similar document ID. If it does not, the
     * original document is returned.
     * @param document The document.
     * @return A document.
     * @throws DocumentBuildException if the new document could not be built.
     */
    Document getAvailableDocument(Document document) throws DocumentBuildException;

    /**
     * Moves a document to another location, incl. all requiring documents. If a
     * sitetree is used, this means that the whole subtree is moved.
     * @param source The source document.
     * @param target The target document.
     * @throws PublicationException if an error occurs.
     */
    void moveAll(Document source, Document target) throws PublicationException;

    /**
     * Moves all language versions of a document to another location.
     * @param source The source.
     * @param target The target.
     * @throws PublicationException if the documents could not be moved.
     */
    void moveAllLanguageVersions(Document source, Document target)
            throws PublicationException;

    /**
     * Copies a document to another location, incl. all requiring documents. If
     * a sitetree is used, this means that the whole subtree is copied.
     * @param source The source document.
     * @param target The target document.
     * @throws PublicationException if an error occurs.
     */
    void copyAll(Document source, Document target) throws PublicationException;

    /**
     * Copies all language versions of a document to another location.
     * @param source The source.
     * @param target The target.
     * @throws PublicationException if the documents could not be copied.
     */
    void copyAllLanguageVersions(Document source, Document target)
            throws PublicationException;
    
    /**
     * Deletes a document, incl. all requiring documents. If
     * a sitetree is used, this means that the whole subtree is deleted.
     * @param document The document.
     * @throws PublicationException if an error occurs.
     */
    void deleteAll(Document document) throws PublicationException;
    
    /**
     * Deletes all language versions of a document.
     * @param document The document.
     * @throws PublicationException if the documents could not be copied.
     */
    void deleteAllLanguageVersions(Document document) throws PublicationException;
    
    /**
     * Deletes a set of documents.
     * @param documents The documents.
     * @throws PublicationException if an error occurs.
     */
    void delete(DocumentSet documents) throws PublicationException;

}