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

package org.apache.lenya.cms.publication;

import java.util.ArrayList;
import java.util.List;

/**
 * @version $Id$
 */
public final class PublicationHelper {

    private Publication publication;

    public PublicationHelper(Publication publication) {
        this.publication = publication;
    }

    /**
     * Returns all documents of a publication.
     * @param area
     * @return
     * @see Document
     * @see Publication
     */
    public Document[] getAllDocuments(String area, String language) throws DocumentException {
        try {
            List allNodes = getPublication().getTree(area).getNode("/").preOrder();
            Document[] documents = new Document[allNodes.size()-1];
        
            for(int i=1; i<allNodes.size(); i++) {
                documents[i-1] = DefaultDocumentBuilder.getInstance().createDocument(getPublication(), area,
                        ((SiteTreeNode)allNodes.get(i)).getAbsoluteId(), language);
            }
            return documents;
        } catch(SiteTreeException e) {
            throw new DocumentException("Can not access sitetree to get document ids.", e);
        } catch(DocumentBuildException e) {
            throw new DocumentException("Can not build document from id obtained from sitetree.", e);
        }        
    }

    /**
     * Returns all documents of a publication (all language versions).
     * @param area
     * @return
     * @see Document
     * @see Publication
     */
    public Document[] getAllDocuments(String area) throws DocumentException {
        try {
            List allNodes = getPublication().getTree(area).getNode("/").preOrder();
            ArrayList documents = new ArrayList();
            DocumentBuilder builder = this.publication.getDocumentBuilder();
            
            for (int i=1; i<allNodes.size(); i++) {
                SiteTreeNode node = (SiteTreeNode)allNodes.get(i);
                Label[] labels = node.getLabels();
                
                for (int j=0; j<labels.length; j++) {
                    String url = builder.buildCanonicalUrl(this.publication, 
                            area, node.getAbsoluteId(), labels[j].getLanguage());
                    Document document = builder.buildDocument(this.publication, url);
                    documents.add(document);
                }
            }
            return (Document[])documents.toArray(new Document[documents.size()]);
        } catch(SiteTreeException e) {
            throw new DocumentException("Can not access sitetree to get document ids.", e);
        } catch(DocumentBuildException e) {
            throw new DocumentException("Can not build document from id obtained from sitetree.", e);
        }        
    }

    /**
     * @return Returns the publication.
     */
    public Publication getPublication() {
        return publication;
    }
}
