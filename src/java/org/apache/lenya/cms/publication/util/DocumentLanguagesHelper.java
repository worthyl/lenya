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

/* $Id$  */

package org.apache.lenya.cms.publication.util;

import java.util.Map;

import org.apache.cocoon.ProcessingException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;

/**
 * Helper class for the policy GUI.
 */
public class DocumentLanguagesHelper {

    private PageEnvelope pageEnvelope = null;
    private DocumentIdentityMap identityMap;

    /**
     * Create a new DocumentlanguageHelper.
     * @param map The identity map.
     * @param objectModel the objectModel
     * @throws ProcessingException if the page envelope could not be created.
     */
    public DocumentLanguagesHelper(DocumentIdentityMap map, Map objectModel)
            throws ProcessingException {
        try {
            this.identityMap = map;
            this.pageEnvelope = PageEnvelopeFactory.getInstance().getPageEnvelope(map, objectModel);
        } catch (PageEnvelopeException e) {
            throw new ProcessingException(e);
        }
    }

    /**
     * Compute the URL for a given language and the parameters given in the contructor.
     * @param language the language
     * @return the url for the given language
     * @throws ProcessingException if the document for the given language could not be created.
     */
    public String getUrl(String language) throws ProcessingException {
        Document doc = getDocument(language);
        return this.pageEnvelope.getContext() + doc.getCanonicalWebappURL();
    }

    /**
     * Create a document for a given language and the parameters given in the contructor.
     * @param language the language
     * @return the document with the given language
     * @throws ProcessingException if the document for the given language could not be created.
     */
    protected Document getDocument(String language) throws ProcessingException {
        Document document;
        try {
            document = this.identityMap.getLanguageVersion(this.pageEnvelope.getDocument(),
                    language);
        } catch (DocumentBuildException e) {
            throw new ProcessingException(e);
        }
        return document;
    }
}