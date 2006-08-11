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
package org.apache.lenya.cms.site.usecases;

import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.publication.ResourceWrapperTest;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;

/**
 * Link rewriter test for the "resource" resource type.
 */
public class ResourceLinkRewriterTest extends LinkRewriterTest {

    /**
     * Check if the link rewriter doesn't try to rewrite links in resources.
     * @throws Exception 
     */
    public void testResourceLinkRewriting() throws Exception {
        String documentId = "/testResourceLinkRewriting";
        
        Session session = RepositoryUtil.getSession(getManager(), getRequest());
        DocumentFactory factory = DocumentUtil.createDocumentFactory(getManager(), session);

        Publication pub = PublicationUtil.getPublication(getManager(), "test");
        
        
        ResourceWrapperTest.createResource(factory, pub, documentId, getManager(), getLogger());
        
        super.testLinkRewriter();
        
    }
    
}
