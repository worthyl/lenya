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
package org.apache.lenya.cms.publication;

import javax.servlet.http.HttpServletRequest;

import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryManager;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.springframework.util.Assert;

public class RepositoryImpl implements Repository {

    private RepositoryManager repositoryManager;

    public Session getSession(HttpServletRequest request) {
        Assert.notNull(request, "request");
        try {
            org.apache.lenya.cms.repository.Session repoSession = RepositoryUtil.getSession(
                    this.repositoryManager, request);
            return new SessionImpl(this, repoSession);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRepositoryManager(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public Session startSession(Identity identity, boolean modifiable) {
        org.apache.lenya.cms.repository.Session repoSession;
        try {
            repoSession = this.repositoryManager.createSession(identity, modifiable);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        return new SessionImpl(this, repoSession);
    }

}