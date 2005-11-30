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

import org.apache.lenya.cms.jcr.mapping.AbstractNodeProxy;
import org.apache.lenya.cms.jcr.mapping.Path;
import org.apache.lenya.cms.jcr.mapping.PathElement;
import org.apache.lenya.cms.jcr.mapping.PropertyPathElement;
import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.Content;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Site;

/**
 * Area proxy.
 */
public class AreaProxy extends AbstractNodeProxy implements Area {

    protected static final String NODE_NAME = "lenya:publication";
    protected static final String NODE_TYPE = "lnt:publication";
    protected static final String ID_PROPERTY = "lenya:id";

    public Content getContent() throws RepositoryException {
        Path path = getAbsolutePath().append(getPathElement(ContentProxy.NODE_NAME));
        if (getRepository().containsProxy(path)) {
            return (Content) getRepository().getProxy(path);
        } else {
            return (Content) getRepository().addByName(getAbsolutePath(),
                    ContentProxy.NODE_TYPE,
                    ContentProxy.class.getName(),
                    ContentProxy.NODE_NAME);
        }
    }

    public Site getSite() throws RepositoryException {
        Path path = getAbsolutePath().append(getPathElement(SiteProxy.NODE_NAME));
        if (getRepository().containsProxy(path)) {
            return (Site) getRepository().getProxy(path);
        } else {
            return (Site) getRepository().addByName(getAbsolutePath(),
                    SiteProxy.NODE_TYPE,
                    SiteProxy.class.getName(),
                    SiteProxy.NODE_NAME);
        }
    }

    public void clear() throws RepositoryException {
        // TODO Auto-generated method stub

    }

    public Path getAbsolutePath() throws RepositoryException {
        return AreaProxy.getPath(getPublicationId());
    }

    protected static Path getPath(String pubId) throws RepositoryException {
        return new Path(getPathElement(NODE_NAME, ID_PROPERTY, pubId));
    }

    /**
     * @return The ID of the publication this node belongs to.
     * @throws RepositoryException if an error occurs.
     */
    protected String getPublicationId() throws RepositoryException {
        try {
            return getNode().getProperty(ID_PROPERTY).getString();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public PathElement getPathElement() throws RepositoryException {
        return new PropertyPathElement(NODE_NAME, ID_PROPERTY, getPublicationId());
    }

}
