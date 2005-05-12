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

package org.apache.lenya.cms.authoring;

import java.io.File;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * Interface for creation of hierarchical documents
 * @version $Id$
 */
public interface ParentChildCreatorInterface {
    /**
     * Constant for a branch node. Branch nodes are somewhat related
     * to the concept of collections in WebDAV. They are not the same
     * however.
     *
     */
    short BRANCH_NODE = 1;

    /**
     * Constant for a leaf node. Leaf nodes are somewhat related to
     * the concept of resources in WebDAV. They are not the same
     * however.
     *
     */
    short LEAF_NODE = 0;

    /**
     * Configures the Creator, based on a configuration file.
     * 
     * @param doctypeConf A configuration.
     * @param manager the service manager
     * @param logger A logger
     */
    void init(Configuration doctypeConf, ServiceManager manager, Logger logger);

    /**
     * Return the type of node this creator will create. It can be
     * either <code>BRANCH_NODE</code> or <code>LEAF_NODE</code>. An
     * implementation can simply return the input parameter (which can
     * be used to pass in a request parameter) or choose to ignore it.
     *
     * @param childType a <code>short</code> value
     * @return a <code>short</code> value (either <code>BRANCH_NODE</code> or <code>LEAF_NODE</code>)
     * @exception Exception if an error occurs
     */
    short getChildType(short childType) throws Exception;

    /**
     * Describe <code>getChildName</code> method here.
     *
     * @param childname a <code>String</code> value
     * @return a <code>String</code> value
     * @exception Exception if an error occurs
     */
    String getChildName(String childname) throws Exception;

    /**
     * Describe <code>generateTreeId</code> method here.
     *
     * @param childId a <code>String</code> value
     * @param childType a <code>short</code> value
     * @return a <code>String</code> value
     * @exception Exception if an error occurs
     */
    String generateTreeId(String childId, short childType) throws Exception;

    /**
     * Create a new document.
     *
     * @param initialContentsURI the URI where initial content for this document can be found.
     * @param parentDir in which directory the document is to be created.
     * @param childId the document id of the new document
     * @param childType the type of the new document.
     * @param childName the name of the new document.
     * @param language for which the document is created.
     * @param parameters additional parameters that can be used when creating the child
     * 
     * @exception Exception if an error occurs
     */
    void create(
        String initialContentsURI,
        File parentDir,
        String childId,
        short childType,
        String childName,
        String language,
        Map parameters)
        throws Exception;

}
