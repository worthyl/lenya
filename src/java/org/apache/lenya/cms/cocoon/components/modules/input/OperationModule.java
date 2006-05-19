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
package org.apache.lenya.cms.cocoon.components.modules.input;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;

/**
 * Super class for operation-based input modules.
 * 
 * @version $Id$
 */
public class OperationModule extends AbstractInputModule implements Serviceable,
        Initializable, Contextualizable {

    /**
     * Ctor.
     */
    public OperationModule() {
        super();
    }

    private DocumentIdentityMap documentIdentityMap;

    private Request request;

    protected DocumentIdentityMap getDocumentIdentityMap() {
        if (this.documentIdentityMap == null) {
            try {
                Session session = RepositoryUtil.getSession(this.manager, this.request);
                this.documentIdentityMap = DocumentUtil.createDocumentIdentityMap(this.manager, session);
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }
        return this.documentIdentityMap;
    }

    protected ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception {
        // do nothing
    }

    public void contextualize(Context context) throws ContextException {
        this.request = ContextHelper.getRequest(context);
    }

}
