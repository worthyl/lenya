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
package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.ac.impl.PolicyAuthorizer;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Proxy;
import org.apache.lenya.cms.publication.Publication;

/**
 * Input module for getting the proxied URL of a document.
 * 
 * <p>
 * Usage: <code>{proxy-url:{area}:{document-id}:{language}}</code>
 * </p>
 * 
 * <p>
 * If there are no proxy settings in the file conf/publication.xconf, the values of the request
 * parameters 'server name' and 'port' will be used to construct the URL.
 * </p>
 * 
 * @version $Id:$
 */
public class ProxyUrlModule extends AbstractInputModule implements Serviceable {

    private ServiceManager manager;

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        ServiceSelector serviceSelector = null;
        PolicyManager policyManager = null;
        AccessControllerResolver acResolver = null;
        AccreditableManager accreditableManager = null;

        // Get parameters
        final String[] attributes = name.split(":");

        if (attributes.length < 3) {
            throw new ConfigurationException("Invalid number of parameters: " + attributes.length
                    + ". Expected area, document-id, language.");
        }

        final String area = attributes[0];
        final String documentId = attributes[1];
        final String language = attributes[2];

        String value = null;
        try {
            PageEnvelope envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
            Publication publication = envelope.getPublication();

            DocumentBuilder builder = publication.getDocumentBuilder();

            // Create canonical URL
            String canonicalUrl = builder
                    .buildCanonicalUrl(publication, area, documentId, language);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Created canonicalURL: " + canonicalUrl);
            }

            // Get proxy for document
            serviceSelector = (ServiceSelector) this.manager.lookup(AccessControllerResolver.ROLE
                    + "Selector");
            acResolver = (AccessControllerResolver) serviceSelector
                    .select(AccessControllerResolver.DEFAULT_RESOLVER);

            AccessController accessController = acResolver.resolveAccessController(canonicalUrl);
            if (accessController instanceof DefaultAccessController) {
                DefaultAccessController defaultAccessController = (DefaultAccessController) accessController;
                accreditableManager = defaultAccessController.getAccreditableManager();
                Authorizer[] authorizers = defaultAccessController.getAuthorizers();
                for (int i = 0; i < authorizers.length; i++) {
                    if (authorizers[i] instanceof PolicyAuthorizer) {
                        PolicyAuthorizer policyAuthorizer = (PolicyAuthorizer) authorizers[i];
                        policyManager = policyAuthorizer.getPolicyManager();
                    }
                }
            }

            Policy policy = policyManager.getPolicy(accreditableManager, canonicalUrl);

            Document doc = builder.buildDocument(publication, canonicalUrl);

            Proxy proxy = doc.getPublication().getProxy(doc, policy.isSSLProtected());

            if (proxy != null) {
                value = proxy.getURL(doc);
            } else {
                // Take server name and port from request.
                Request request = ObjectModelHelper.getRequest(objectModel);
                value = request.getContextPath() + doc.getCompleteURL();
            }

        } catch (Exception e) {
            throw new ConfigurationException("Obtaining value for [" + name + "] failed: ", e);
        }
        return value;
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration,
     *      java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        return Collections.EMPTY_SET.iterator();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel) };
        return objects;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }
}