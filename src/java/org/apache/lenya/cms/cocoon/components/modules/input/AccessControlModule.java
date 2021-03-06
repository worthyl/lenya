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

/* $Id$  */

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.ItemManager;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.ac.impl.PolicyAuthorizer;

/**
 * Input module for access control attributes.
 */
public class AccessControlModule extends AbstractInputModule implements Serviceable {

    public static final String USER_ID = "user-id";
    public static final String USER_NAME = "user-name";
    public static final String USER_EMAIL = "user-email";
    public static final String IP_ADDRESS = "ip-address";
    public static final String ROLE_IDS = "role-ids";

    public static final String USER_MANAGER = "user-manager";
    public static final String GROUP_MANAGER = "group-manager";
    public static final String ROLE_MANAGER = "role-manager";
    public static final String IP_RANGE_MANAGER = "iprange-manager";

    /**
      * The names of the AccessControlModule parameters.
      */
    public static final String[] PARAMETER_NAMES =
        {
            IP_ADDRESS,
            USER_ID,
            USER_NAME,
            USER_EMAIL,
            ROLE_IDS,
            USER_MANAGER,
            GROUP_MANAGER,
            ROLE_MANAGER,
            IP_RANGE_MANAGER };

    /**
     *
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {

        Request request = ObjectModelHelper.getRequest(objectModel);
        Session session = request.getSession();
        Object value = null;

        if (!Arrays.asList(PARAMETER_NAMES).contains(name)) {
            throw new ConfigurationException("The attribute [" + name + "] is not supported!");
        }

        if (session != null) {
            Identity identity = (Identity) session.getAttribute(Identity.class.getName());
            if (identity != null) {
                if (name.equals(USER_ID)) {
                    User user = identity.getUser();
                    if (user != null) {
                        value = user.getId();
                    }
                } else if (name.equals(USER_NAME)) {
                    User user = identity.getUser();
                    if (user != null) {
                        value = user.getName();
                    }
                } else if (name.equals(USER_EMAIL)) {
                    User user = identity.getUser();
                    if (user != null) {
                        value = user.getEmail();
                    }
                } else if (name.equals(IP_ADDRESS)) {
                    Machine machine = identity.getMachine();
                    if (machine != null) {
                        value = machine.getIp();
                    }
                } else if (name.equals(ROLE_IDS)) {
                    try {
                        Role[] roles = PolicyAuthorizer.getRoles(request);
                        StringBuffer roleIds = new StringBuffer();
                        for (int i = 0; i < roles.length; i++) {
                            if (i > 0) {
                                roleIds.append(",");
                            }
                            roleIds.append(roles[i].getId());
                        }
                        value = roleIds;
                    } catch (AccessControlException e) {
                        throw new ConfigurationException(
                            "Obtaining value for attribute [" + name + "] failed: ", e);
                    }
                }
            }
        }

        if (name.equals(USER_MANAGER)
            || name.equals(GROUP_MANAGER)
            || name.equals(ROLE_MANAGER)
            || name.equals(IP_RANGE_MANAGER)) {
            value = getItemManager(request, name);
        }
        return value;
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        return Arrays.asList(PARAMETER_NAMES).iterator();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel)};

        return objects;
    }

    /**
     * Returns the item manager for a certain name.
     * @param request The request.
     * @param name The name of the manager ({@link #USER_MANAGER},
     * {@link #ROLE_MANAGER}, {@link #GROUP_MANAGER}, or {@link IP_RANGE_MANAGER}
     * @return An item manager.
     * @throws ConfigurationException when something went wrong.
     */
    protected ItemManager getItemManager(Request request, String name)
        throws ConfigurationException {
        AccessController accessController = null;
        ServiceSelector selector = null;
        AccessControllerResolver resolver = null;
        ItemManager itemManager = null;

        try {
            selector = (ServiceSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");
            resolver =
                (AccessControllerResolver) selector.select(
                    AccessControllerResolver.DEFAULT_RESOLVER);

            String requestURI = request.getRequestURI();
            String context = request.getContextPath();
            if (context == null) {
                context = "";
            }
            String url = requestURI.substring(context.length());
            accessController = resolver.resolveAccessController(url);

            AccreditableManager accreditableManager =
                ((DefaultAccessController) accessController).getAccreditableManager();

            if (name.equals(USER_MANAGER)) {
                itemManager = accreditableManager.getUserManager();
            } else if (name.equals(GROUP_MANAGER)) {
                itemManager = accreditableManager.getGroupManager();
            } else if (name.equals(ROLE_MANAGER)) {
                itemManager = accreditableManager.getRoleManager();
            } else if (name.equals(IP_RANGE_MANAGER)) {
                itemManager = accreditableManager.getIPRangeManager();
            }

        } catch (Exception e) {
            throw new ConfigurationException("Obtaining item manager failed: ", e);
        } finally {
            if (selector != null) {
                if (resolver != null) {
                    if (accessController != null) {
                        resolver.release(accessController);
                    }
                    selector.release(resolver);
                }
                manager.release(selector);
            }
        }

        return itemManager;
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
