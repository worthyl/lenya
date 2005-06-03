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
package org.apache.lenya.cms.site.usecases;

import java.util.Arrays;

import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.site.usecases.SiteUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.DocumentWorkflowable;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowManager;

/**
 * Usecase to display the overview tab in the site area for a document.
 * 
 * @version $Id$
 */
public class Overview extends SiteUsecase {

    protected static final String STATE = "state";
    protected static final String ISLIVE = "isLive";

    /**
     * Ctor.
     */
    public Overview() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        WorkflowManager resolver = null;
        try {
            // read parameters from Dublin Core meta-data
            MetaData dc = getSourceDocument().getMetaDataManager().getDublinCoreMetaData();
            setParameter(DublinCore.ELEMENT_TITLE, dc.getFirstValue(DublinCore.ELEMENT_TITLE));
            setParameter(DublinCore.ELEMENT_DESCRIPTION, dc
                    .getFirstValue(DublinCore.ELEMENT_DESCRIPTION));

            // read parameters from document attributes
            setParameter("languages", getSourceDocument().getLanguages());
            setParameter("lastmodified", getSourceDocument().getLastModified());
            setParameter("resourcetype", getSourceDocument().getResourceType());

            DocumentWorkflowable workflowable = new DocumentWorkflowable(getSourceDocument(),
                    getLogger());
            resolver = (WorkflowManager) this.manager.lookup(WorkflowManager.ROLE);
            if (resolver.hasWorkflow(workflowable)) {
                Workflow workflow = resolver.getWorkflowSchema(workflowable);
                String[] variableNames = workflow.getVariableNames();
                Version latestVersion = workflowable.getLatestVersion();
                Boolean isLive = null;
                if (latestVersion != null) {
                    setParameter(STATE, latestVersion.getState());
                    if (Arrays.asList(variableNames).contains(ISLIVE)) {
                        isLive = Boolean.valueOf(latestVersion.getValue(ISLIVE));
                    }
                } else {
                    setParameter(STATE, workflow.getInitialState());
                    if (Arrays.asList(variableNames).contains(ISLIVE)) {
                        isLive = Boolean.valueOf(workflow.getInitialValue(ISLIVE));
                    }
                }
                setParameter(ISLIVE, isLive);
            } else {
                setParameter(STATE, "");
            }

        } catch (final Exception e) {
            addErrorMessage("Could not read a value. See log files for details.");
            getLogger().error("Could not read value for Overview usecase. ", e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    /**
     * Validates the request parameters.
     * @throws UsecaseException if an error occurs.
     */
    void validate() throws UsecaseException {
        // do nothing
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        validate();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
    }
}