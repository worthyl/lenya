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
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.transaction.Operation;
import org.apache.lenya.transaction.UnitOfWork;
import org.apache.lenya.transaction.UnitOfWorkImpl;

/**
 * Super class for operation-based input modules.
 * 
 * @version $Id$
 */
public class OperationModule extends AbstractInputModule implements Operation, Serviceable,
        Initializable, Contextualizable {

    /**
     * Ctor.
     */
    public OperationModule() {
        super();
    }

    private UnitOfWork unitOfWork;

    private DocumentIdentityMap documentIdentityMap;

    private Request request;

    protected DocumentIdentityMap getDocumentIdentityMap() {
        if (this.documentIdentityMap == null) {
            Session session = RepositoryUtil.getSession(this.request, getLogger());
            this.documentIdentityMap = new DocumentIdentityMap(session, this.manager, getLogger());
        }
        return this.documentIdentityMap;
    }

    /**
     * Retrieves a unit-of-work, which gives the operation access to business objects affected by
     * the operation.
     * 
     * @return a UnitOfWork, the interface to access the objects
     * @throws ServiceException if the unit-of-work component can not be initialized by the
     *             component framework
     * 
     * @see org.apache.lenya.transaction.Operation#getUnitOfWork()
     */
    public UnitOfWork getUnitOfWork() throws ServiceException {
        if (this.unitOfWork == null) {
            if (getLogger().isDebugEnabled())
                getLogger().debug("OperationModule.getUnitOfWork() does not yet have instance.");

            this.unitOfWork = new UnitOfWorkImpl(getDocumentIdentityMap().getIdentityMap(),
                    getLogger());
        }

        return this.unitOfWork;
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

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (this.manager != null) {
            if (this.unitOfWork != null) {
                this.manager.release(this.unitOfWork);
            }
        }
    }

    /**
     * @see org.apache.lenya.transaction.Operation#setUnitOfWork(org.apache.lenya.transaction.UnitOfWork)
     */
    public void setUnitOfWork(UnitOfWork unit) {
        this.unitOfWork = unit;
    }

    public void contextualize(Context context) throws ContextException {
        this.request = ContextHelper.getRequest(context);
    }

}