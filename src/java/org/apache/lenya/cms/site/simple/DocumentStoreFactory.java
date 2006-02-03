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
package org.apache.lenya.cms.site.simple;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.transaction.Identifiable;
import org.apache.lenya.transaction.IdentifiableFactory;
import org.apache.lenya.transaction.IdentityMap;

/**
 * Factory for sitetree objects.
 * 
 * @version $Id$
 */
public class DocumentStoreFactory extends AbstractLogEnabled implements IdentifiableFactory {

    protected ServiceManager manager;

    /**
     * Ctor.
     * @param manager The service manager.
     * @param logger The logger.
     */
    public DocumentStoreFactory(ServiceManager manager, Logger logger) {
        this.manager = manager;
        ContainerUtil.enableLogging(this, logger);
    }

    /**
     * @see org.apache.lenya.transaction.IdentifiableFactory#build(org.apache.lenya.transaction.IdentityMap,
     *      java.lang.String)
     */
    public Identifiable build(IdentityMap map, String key) throws Exception {
        String[] snippets = key.split(":");
        String publicationId = snippets[0];
        String area = snippets[1];

        Publication publication = PublicationUtil.getPublication(this.manager, publicationId);

        DocumentIdentityMap docMap = new DocumentIdentityMap(map, this.manager, getLogger());
        DocumentStore store = new DocumentStore(this.manager,
                docMap,
                publication,
                area,
                getLogger());

        return store;
    }

    /**
     * @see org.apache.lenya.transaction.IdentifiableFactory#getType()
     */
    public String getType() {
        return DocumentStore.IDENTIFIABLE_TYPE;
    }

}