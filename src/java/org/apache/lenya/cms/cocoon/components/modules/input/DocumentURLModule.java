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
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.publication.DocumentHelper;

/**
 * <p>
 * This module constructs the document url taking into account difference in the language .version
 * being created and used.
 * </p>
 * <p>
 * Example:
 * <code>{document-url:{page-envelope:area}:{page-envelope:document-id}:{page-envelope:document-language}}</code>
 * </p>
 * 
 * @version: $Id$
 */
public class DocumentURLModule extends AbstractPageEnvelopeModule implements Serviceable {

    private ServiceManager manager;

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        String url;

        final String[] attributes = name.split(":");

        if (attributes.length < 3) {
            throw new ConfigurationException("Invalid number of parameters: " + attributes.length
                    + ". Expected 3 (area, document-id, language)");
        }

        final String area = attributes[0];
        final String documentId = attributes[1];
        final String language = attributes[2];

        try {
            DocumentHelper helper = new DocumentHelper(objectModel);
            url = helper.getDocumentUrl(documentId, area, language);
        } catch (Exception e) {
            throw new ConfigurationException("Resolving attribute [" + name + "] failed: ", e);
        }

        return url;
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