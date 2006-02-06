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
package org.apache.lenya.cms.jcr;

import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.Publication;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.metadata.MetaData;

/**
 * JCR publication.
 */
public class JCRPublication implements Publication {

    private String pubId;
    private JCRSession session;

    /**
     * Ctor.
     * @param session The session.
     * @param pubId The publication ID.
     */
    public JCRPublication(JCRSession session, String pubId) {
        this.pubId = pubId;
        this.session = session;
    }

    public String getPublicationId() {
        return this.pubId;
    }

    protected JCRSession getSession() {
        return this.session;
    }

    public Area getArea(String area) throws RepositoryException {
        return getSession().getArea(this, area);
    }

    public Area addArea(String area) throws RepositoryException {
        return getSession().addArea(this, area);
    }

    public boolean existsArea(String area) throws RepositoryException {
        return getSession().existsArea(this, area);
    }
    
    protected static final String INTERNAL_AREA = "internal";
    
    protected Area getInternalArea() throws RepositoryException {
        if (!existsArea(INTERNAL_AREA)) {
            addArea(INTERNAL_AREA);
        }
        return getArea(INTERNAL_AREA);
    }

    public MetaData getMetaData(String elementSet) throws RepositoryException {
        AreaProxy internalArea = (AreaProxy) getInternalArea();
        return internalArea.getMetaData(elementSet);
    }

}
