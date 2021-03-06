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

importClass(Packages.org.apache.lenya.cms.cocoon.flow.FlowHelper);
importClass(Packages.org.apache.excalibur.source.SourceResolver);
importClass(Packages.org.apache.lenya.cms.cocoon.source.SourceUtil);

/**
 * Provides general flow functions for document editing.
 * @version $Id$
 */

/**
 * Saves a Source to the document in the current PageEnvelope.
 * TODO: Use nobby's new usecase fw in 1.4 branch. Exception handling e.g. display appropriate error pages.
 * @param sourceUri An URI of a Source providing the edited document data
 * @param useBuffer If "true", the source Source is read into a buffer before it is written to its final destination.
 * @param workflowEvent Name of the workflow event to trigger. Default is "edit".
 * @param noWorkflow If true, no workflow event will be triggered. Default is false.
 * @param noCheckin If true, the current document is not checked in. Default is false.
 * @param backup If true, a new revision is created on checkin. Default is true.
 * @param status Default int value is 204. Used to set the response status.
 * @param noStatus If true, then no response status will be set.     
 */
function editDocument() {
    try {
        var flowHelper = new FlowHelper();
        var resolver = cocoon.getComponent(SourceResolver.ROLE);
        var document = flowHelper.getPageEnvelope(cocoon).getDocument();
        var dstUri = flowHelper.getDocumentHelper(cocoon).getSourceUri(document);
        var wfFactory = Packages.org.apache.lenya.cms.workflow.WorkflowFactory.newInstance();
        
        SourceUtil.copy(resolver, cocoon.parameters["sourceUri"], dstUri, _getParameter("useBuffer", "false") == "true");

        if(_getParameter("noCheckin", "false") == "false")
            flowHelper.reservedCheckIn(cocoon, _getParameter("backup", "true") == "true");

        var hasWorkflow = wfFactory.hasWorkflow(document);
        if(hasWorkflow)
            flowHelper.triggerWorkflow(cocoon, _getParameter("workflowEvent", "edit"));

        if(_getParameter("noStatus", "false") == "false")
            cocoon.sendStatus(_getParameter("status", 204));
        else
            cocoon.redirectTo(_getParameter("redirectUrl", "FIXME"));
        
    } catch (exception) {
        cocoon.log.error("Can not edit doucment.", exception);
    } finally {
        if(resolver != null)
            cocoon.releaseComponent(resolver);
    }
}

function _getParameter(name, defaultValue) {
    if(cocoon.parameters[name])
        return cocoon.parameters[name];
    else
        return defaultValue;
}
