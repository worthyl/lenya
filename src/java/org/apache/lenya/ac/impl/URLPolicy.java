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

package org.apache.lenya.ac.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;

/**
 * A policy at a certain URL. The final policy is computed by merging the subtree
 * policies of all ancestor-or-self directories with the URL policy of the actual URL.  
 */
public class URLPolicy implements Policy {

    /**
     * Returns the resulting policy for a certain URL.
     * @param controller The acccess controller.
     * @param url The URL.
     * @param manager The policy manager.
     */
    public URLPolicy(AccreditableManager controller, String url, InheritingPolicyManager manager) {
        assert url != null;
        this.url = url;

        assert manager != null;
        policyManager = manager;

        assert controller != null;
        this.accreditableManager = controller;
    }

    private String url;
    private InheritingPolicyManager policyManager;
    private AccreditableManager accreditableManager;
    private Policy[] policies = null;

    /**
     * Obtains the policies from the policy manager.
     * This method is expensive and therefore only called when needed.
     * @throws AccessControlException when something went wrong.
     */
    protected void obtainPolicies() throws AccessControlException {
        if (policies == null) {
            policies = getPolicyManager().getPolicies(getAccreditableManager(), getUrl());
        }
    }

    static final String[] VISITOR_ROLES = { "visitor", "visit" };
    static final String[] ADMINISTRATOR_ROLES = { "administrator", "admin", "organize" };
    static final String[] AUTHOR_ROLES = { "author", "edit" };

    /**
     * @see org.apache.lenya.ac.Policy#getRoles(org.apache.lenya.ac.Identity)
     */
    public Role[] getRoles(Identity identity) throws AccessControlException {
        obtainPolicies();
        Set roles = new HashSet();

        // no policies defined: return "visit" or "visitor" role
        if (isEmpty()) {
            Role visitorRole = getVisitorRole(getAccreditableManager());
            if (visitorRole != null) {
                roles.add(visitorRole);
            }
        } else {
            for (int i = 0; i < policies.length; i++) {
                addRoles(policies[i], identity, roles);
            }
        }
        return (Role[]) roles.toArray(new Role[roles.size()]);
    }

    /**
     * Returns the visitor role.
     * @param manager The accreditable manager.
     * @return A role.
     * @throws AccessControlException when something went wrong.
     */
    public static Role getVisitorRole(AccreditableManager manager) throws AccessControlException {
        Role visitorRole = null;
        for (int i = 0; i < VISITOR_ROLES.length; i++) {
            Role role = manager.getRoleManager().getRole(VISITOR_ROLES[i]);
            if (role != null) {
                visitorRole = role;
            }
        }
        return visitorRole;
    }

    /**
     * Returns the administrator role.
     * @param manager The accreditable manager.
     * @return A role.
     * @throws AccessControlException when something went wrong.
     */
    public static Role getAdministratorRole(AccreditableManager manager) throws AccessControlException {
        Role administratorRole = null;
        for (int i = 0; i < ADMINISTRATOR_ROLES.length; i++) {
            Role role = manager.getRoleManager().getRole(ADMINISTRATOR_ROLES[i]);
            if (role != null) {
                administratorRole = role;
            }
        }
        return administratorRole;
    }

    /**
     * Returns the author role.
     * @param manager The accreditable manager.
     * @return A role.
     * @throws AccessControlException when something went wrong.
     */
    public static Role getAuthorRole(AccreditableManager manager) throws AccessControlException {
        Role administratorRole = null;
        for (int i = 0; i < AUTHOR_ROLES.length; i++) {
            Role role = manager.getRoleManager().getRole(AUTHOR_ROLES[i]);
            if (role != null) {
                administratorRole = role;
            }
        }
        return administratorRole;
    }

    /**
     * Adds the roles of an identity of a policy to a role set.
     * @param policy The policy.
     * @param identity The identity.
     * @param roles The role set.
     * @throws AccessControlException when something went wrong.
     */
    protected void addRoles(Policy policy, Identity identity, Set roles)
        throws AccessControlException {
        roles.addAll(Arrays.asList(policy.getRoles(identity)));
    }

    /**
     * Returns the URL of this policy.
     * @return The URL of this policy.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the policy builder.
     * @return A policy builder.
     */
    public InheritingPolicyManager getPolicyManager() {
        return policyManager;
    }

    /**
     * Returns the access controller.
     * @return An access controller.
     */
    public AccreditableManager getAccreditableManager() {
        return accreditableManager;
    }

    /**
     * The URL policy requires SSL protection if one of its
     * member policies requires SSL protection.
     * @see org.apache.lenya.ac.Policy#isSSLProtected()
     */
    public boolean isSSLProtected() throws AccessControlException {
        obtainPolicies();

        boolean ssl = false;

        int i = 0;
        while (!ssl && i < policies.length) {
            ssl = ssl || policies[i].isSSLProtected();
            i++;
        }

        return ssl;
    }

    /**
     * @see org.apache.lenya.ac.Policy#isEmpty()
     */
    public boolean isEmpty() throws AccessControlException {
        boolean empty = true;

        int i = 0;
        while (empty && i < policies.length) {
            empty = empty && policies[i].isEmpty();
            i++;
        }

        return empty;
    }

}
