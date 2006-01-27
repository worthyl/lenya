/*
 * Copyright  1999-2005 The Apache Software Foundation
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

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.RepositorySource;
import org.apache.lenya.cms.metadata.MetaDataManager;
import org.apache.lenya.cms.metadata.MetaDataOwner;
import org.apache.lenya.cms.repository.Node;

/**
 * A resource (asset).
 * 
 * @version $Id$
 */
public class Resource extends AbstractLogEnabled implements MetaDataOwner {

    private Document document;
    private String name;
    private ServiceManager manager;
    private MetaDataManager metaDataManager;

    /**
     * Ctor.
     * @param document The document the resource belongs to.
     * @param name The name.
     * @param manager The service manager.
     * @param _logger The logger.
     */
    public Resource(Document document, String name, ServiceManager manager, Logger _logger) {
        ContainerUtil.enableLogging(this, _logger);
        this.document = document;
        this.name = name;
        this.manager = manager;
    }

    /**
     * @return The document.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * @see org.apache.lenya.cms.metadata.MetaDataOwner#getMetaDataManager()
     */
    public MetaDataManager getMetaDataManager() {
        if (this.metaDataManager == null) {
            SourceResolver resolver = null;
            RepositorySource source = null;
            try {
                resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                source = (RepositorySource) resolver.resolveURI(getSourceURI());
                this.metaDataManager = source.getNode().getMetaDataManager();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (resolver != null) {
                    if (source != null) {
                        resolver.release(source);
                    }
                    this.manager.release(resolver);
                }
            }
        }
        return metaDataManager;
    }

    /**
     * @return The source URI.
     */
    public String getSourceURI() {
        String srcUri = getResourcesURI() + "/" + getName();
        return srcUri;
    }

    /**
     * @return The directory URI where the resources are located.
     */
    public String getBaseURI() {
        String resourcesUri = getResourcesURI().substring(Node.LENYA_PROTOCOL.length());
        return "context://" + resourcesUri;
    }

    protected String getResourcesURI() {
        String resourcesUri = document.getPublication().getSourceURI() + "/"
                + ResourcesManager.RESOURCES_PREFIX + "/" + document.getArea() + document.getId();
        return resourcesUri;
    }

    /**
     * @return The repository nodes that represent this resource.
     */
    public Node[] getRepositoryNodes() {
        Node[] nodes = new Node[1];
        SourceResolver resolver = null;
        RepositorySource documentSource = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            documentSource = (RepositorySource) resolver.resolveURI(getSourceURI());
            nodes[0] = documentSource.getNode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (documentSource != null) {
                    resolver.release(documentSource);
                }
                this.manager.release(resolver);
            }
        }
        return nodes;
    }

    /**
     * @return The content length of the source.
     * @throws SourceNotFoundException if the source does not exist.
     */
    public long getContentLength() throws SourceNotFoundException {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(getSourceURI());
            if (source.exists()) {
                return source.getContentLength();
            } else {
                throw new SourceNotFoundException("The source [" + getSourceURI()
                        + "] does not exist!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }
    
    /**
     * @return The last modification date of the source.
     * @throws SourceNotFoundException if the source does not exist.
     */
    public long getLastModified() throws SourceNotFoundException {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(getSourceURI());
            if (source.exists()) {
                return source.getLastModified();
            } else {
                throw new SourceNotFoundException("The source [" + getSourceURI()
                        + "] does not exist!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }
    
    /**
     * @return The mime type of the source.
     * @throws SourceNotFoundException if the source does not exist.
     */
    public String getMimeType() throws SourceNotFoundException {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(getSourceURI());
            if (source.exists()) {
                return source.getMimeType();
            } else {
                throw new SourceNotFoundException("The source [" + getSourceURI()
                        + "] does not exist!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }
    
}