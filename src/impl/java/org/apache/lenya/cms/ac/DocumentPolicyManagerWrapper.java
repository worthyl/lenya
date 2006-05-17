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

package org.apache.lenya.cms.ac;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.InheritingPolicyManager;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;

/**
 * A PolicyManager which is capable of mapping all URLs of a document to the appropriate canonical
 * URL, e.g. <code>/foo/bar_de.print.html</code> is mapped to <code>/foo/bar</code>.
 */
public class DocumentPolicyManagerWrapper extends AbstractLogEnabled implements
        InheritingPolicyManager, Serviceable, Configurable, Disposable {

    /**
     * Ctor.
     */
    public DocumentPolicyManagerWrapper() {
        // do nothing
    }

    private InheritingPolicyManager policyManager;
    private ServiceSelector policyManagerSelector;

    /**
     * Returns the URI which is used to obtain the policy for a webapp URL.
     * @param webappUrl The webapp URL.
     * @return A string.
     * @throws AccessControlException when something went wrong.
     */
    protected String getPolicyURL(String webappUrl) throws AccessControlException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Resolving policy for webapp URL [" + webappUrl + "]");
        }

        Publication publication = getPublication(webappUrl);
        String url = null;
        ContextUtility contextUtility = null;
        try {
            contextUtility = (ContextUtility) serviceManager.lookup(ContextUtility.ROLE);
            Session session = RepositoryUtil.getSession(contextUtility.getRequest(), getLogger());
            DocumentIdentityMap map = new DocumentIdentityMap(session,
                    getServiceManager(),
                    getLogger());
            if (map.isDocument(webappUrl)) {
                Document document = map.getFromURL(webappUrl);
                if (document.existsInAnyLanguage()) {
                    url = "/" + document.getArea() + document.getId();
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("    Document exists");
                        getLogger().debug("    Document ID: [" + document.getId() + "]");
                    }
                }
            }
        } catch (ServiceException e) {
            throw new AccessControlException("Error looking up ContextUtility component", e);
        } catch (Exception e) {
            throw new AccessControlException(e);
        } finally {
            if (contextUtility != null) {
                serviceManager.release(contextUtility);
            }
        }

        if (url == null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("    Document does not exist.");
            }
            url = webappUrl.substring(("/" + publication.getId()).length());
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("    Using URL: [" + url + "]");
        }
        return url;
    }

    /**
     * Returns the publication for a certain URL.
     * @param url The webapp url.
     * @return A publication.
     * @throws AccessControlException when the publication could not be created.
     */
    protected Publication getPublication(String url) throws AccessControlException {
        getLogger().debug("Building publication");

        try {
            return PublicationUtil.getPublicationFromUrl(this.serviceManager, url);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }
    }

    private ServiceManager serviceManager;

    /**
     * Returns the service manager.
     * @return A service manager.
     */
    protected ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.serviceManager = manager;
    }

    /**
     * @return Returns the policyManager.
     */
    public InheritingPolicyManager getPolicyManager() {
        return this.policyManager;
    }

    /**
     * @param _policyManager The policyManager to set.
     */
    public void setPolicyManager(InheritingPolicyManager _policyManager) {
        this.policyManager = _policyManager;
    }

    public Policy buildURLPolicy(AccreditableManager controller, String url)
            throws AccessControlException {
        return getPolicyManager().buildURLPolicy(controller, getPolicyURL(url));
    }

    public Policy buildSubtreePolicy(AccreditableManager controller, String url)
            throws AccessControlException {
        return getPolicyManager().buildSubtreePolicy(controller, getPolicyURL(url));
    }

    public Policy[] getPolicies(AccreditableManager controller, String url)
            throws AccessControlException {
        return getPolicyManager().getPolicies(controller, getPolicyURL(url));
    }

    public void saveURLPolicy(String url, Policy policy) throws AccessControlException {
        getPolicyManager().saveURLPolicy(getPolicyURL(url), policy);

    }

    public void saveSubtreePolicy(String url, Policy policy) throws AccessControlException {
        getPolicyManager().saveSubtreePolicy(getPolicyURL(url), policy);
    }

    /**
     * @see org.apache.lenya.ac.PolicyManager#getPolicy(org.apache.lenya.ac.AccreditableManager,
     *      java.lang.String)
     */
    public Policy getPolicy(AccreditableManager controller, String url)
            throws AccessControlException {
        return getPolicyManager().getPolicy(controller, getPolicyURL(url));
    }

    /**
     * @see org.apache.lenya.ac.PolicyManager#accreditableRemoved(org.apache.lenya.ac.AccreditableManager,
     *      org.apache.lenya.ac.Accreditable)
     */
    public void accreditableRemoved(AccreditableManager manager, Accreditable accreditable)
            throws AccessControlException {
        getPolicyManager().accreditableRemoved(manager, accreditable);

    }

    String ELEMENT_POLICY_MANAGER = "policy-manager";
    String ATTRIBUTE_TYPE = "type";

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException {
        Configuration policyManagerConfiguration = configuration.getChild(this.ELEMENT_POLICY_MANAGER,
                false);
        if (policyManagerConfiguration != null) {
            String type = null;
            try {
                type = policyManagerConfiguration.getAttribute(this.ATTRIBUTE_TYPE);

                this.policyManagerSelector = (ServiceSelector) getServiceManager().lookup(PolicyManager.ROLE
                        + "Selector");

                PolicyManager _policyManager = (PolicyManager) this.policyManagerSelector.select(type);

                if (!(_policyManager instanceof InheritingPolicyManager)) {
                    throw new AccessControlException("The " + getClass().getName()
                            + " can only be used with an "
                            + InheritingPolicyManager.class.getName() + ".");
                }

                DefaultAccessController.configureOrParameterize(_policyManager,
                        policyManagerConfiguration);
                setPolicyManager((InheritingPolicyManager) _policyManager);
            } catch (final ConfigurationException e1) {
                throw new ConfigurationException("Obtaining policy manager for type [" + type
                        + "] failed: ", e1);
            } catch (final ServiceException e1) {
                throw new ConfigurationException("Obtaining policy manager for type [" + type
                        + "] failed: ", e1);
            } catch (final ParameterException e1) {
                throw new ConfigurationException("Obtaining policy manager for type [" + type
                        + "] failed: ", e1);
            } catch (final AccessControlException e1) {
                throw new ConfigurationException("Obtaining policy manager for type [" + type
                        + "] failed: ", e1);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (this.policyManagerSelector != null) {
            if (getPolicyManager() != null) {
                this.policyManagerSelector.release(getPolicyManager());
            }
            getServiceManager().release(this.policyManagerSelector);
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Disposing [" + this + "]");
        }

    }

    /**
     * @see org.apache.lenya.ac.PolicyManager#accreditableAdded(org.apache.lenya.ac.AccreditableManager,
     *      org.apache.lenya.ac.Accreditable)
     */
    public void accreditableAdded(AccreditableManager manager, Accreditable accreditable)
            throws AccessControlException {
        getPolicyManager().accreditableAdded(manager, accreditable);
    }
}