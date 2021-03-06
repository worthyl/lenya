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

package org.apache.lenya.cms.cocoon.task;

import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.impl.PolicyAuthorizer;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.task.DefaultTaskWrapper;
import org.apache.lenya.cms.task.ExecutionException;
import org.apache.lenya.cms.task.Notifier;
import org.apache.lenya.cms.task.TaskWrapperParameters;
import org.apache.lenya.cms.task.WorkflowInvoker;
import org.apache.lenya.util.NamespaceMap;
import org.apache.lenya.util.ServletHelper;
import org.apache.log4j.Logger;

/**
 * Task wrapper to be used from Cocoon components.
 */
public class CocoonTaskWrapper extends DefaultTaskWrapper {

	private static Logger log = Logger.getLogger(CocoonTaskWrapper.class);

	/**
	 * Ctor to be called from a Cocoon component.
	 * @param objectModel A Cocoon object model.
	 * @param parameters A parameters object.
	 * @throws ExecutionException when something went wrong.
	 */
	public CocoonTaskWrapper(Map objectModel, Parameters parameters) throws ExecutionException {

		log.debug("Creating CocoonTaskWrapper");

		Publication publication;
		try {
			publication = PublicationFactory.getPublication(objectModel);
		} catch (PublicationException e) {
			throw new ExecutionException(e);
		}
		Request request = ObjectModelHelper.getRequest(objectModel);

        initialize(parameters, publication, request);
	}
    
    /**
     * Ctor.
     */
    protected CocoonTaskWrapper() {
    }

    /**
     * Initializes this wrapper.
     * @param parameters The task parameters.
     * @param publication The publication.
     * @param request The request.
     * @throws ExecutionException when something went wrong.
     */
    protected void initialize(Parameters parameters, Publication publication, Request request)
        throws ExecutionException {
        setNotifying(request);
        
        Parameters taskParameters = extractTaskParameters(parameters, publication, request);
        getTaskParameters().parameterize(taskParameters);
        
        String taskId = request.getParameter(TaskWrapperParameters.TASK_ID);
        taskId = parameters.getParameter(TaskWrapperParameters.TASK_ID, taskId);
        
        String webappUrl = ServletHelper.getWebappURI(request);
        initialize(taskId, publication, webappUrl, taskParameters);
        
        String eventName = request.getParameter(WorkflowInvoker.EVENT_REQUEST_PARAMETER);
        if (eventName == null) {
        	eventName = request.getParameter(WorkflowInvoker.LENYA_EVENT_REQUEST_PARAMETER);
        }
        if (eventName != null) {
        	Session session = request.getSession(false);
        	if (session == null) {
        		log.debug("No session found - not enabling workflow handling.");
        	} else {
        		Identity identity = Identity.getIdentity(session);
        		if (identity == null) {
        			log.debug("No identity found - not enabling workflow handling.");
        		} else {
        			log.debug("Identity found - enabling workflow handling.");
        			Role[] roles;
        			try {
        				roles = PolicyAuthorizer.getRoles(request);
        			} catch (AccessControlException e) {
        				throw new ExecutionException(e);
        			}
        			setWorkflowAware(eventName, identity, roles);
        		}
        	}
        }
        
    }

	/**
	 * Enables notification if the corresponding request parameters exist.
	 * @param request The request.
	 */
	protected void setNotifying(Request request) {

		log.debug("Trying to initialize notification ...");

		Map requestParameters = ServletHelper.getParameterMap(request);

        if (log.isDebugEnabled()) {
            log.debug("    Request parameters:");
            for (Iterator iter = requestParameters.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                log.debug("\t[" + entry.getKey() + "] = [" + entry.getValue() + "]");
            }
        }

		NamespaceMap notificationMap = new NamespaceMap(requestParameters, Notifier.PREFIX);

        if (log.isDebugEnabled()) {
            log.debug("    Notification parameters:");
            for (Iterator iter = notificationMap.getMap().entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                log.debug("\t[" + entry.getKey() + "] = [" + entry.getValue() + "]");
            }
        }

		if (notificationMap.getMap().isEmpty()) {
			log.debug("    No notification parameters found.");
		} else {
			log.debug("    Initializing notification");
            
            Identity identity = Identity.getIdentity(request.getSession());
            User user = identity.getUser();
            String eMail = user.getEmail();
            notificationMap.put(Notifier.PARAMETER_FROM, eMail);
            log.debug("    Setting from address [" + Notifier.PARAMETER_FROM + "] = [" + eMail + "]");

			String toKey = NamespaceMap.getFullName(Notifier.PREFIX, Notifier.PARAMETER_TO);
			String[] toValues = request.getParameterValues(toKey);

			if (toValues == null) {
				throw new IllegalStateException("You must specify at least one [notification.tolist] request parameter!");
			}
            StringBuffer buf = new StringBuffer();
			for (int i = 0; i < toValues.length; i++) {
				if (i > 0 && buf.length() > 0) {
					buf.append(",");
				}
                if (log.isDebugEnabled()) {
                    log.debug("    Adding notification address [" + toValues[i].trim() + "]");
                }
				buf.append(toValues[i].trim());
			}
			notificationMap.put(Notifier.PARAMETER_TO, buf.toString());
			setNotifying(notificationMap);
		}
	}

}
