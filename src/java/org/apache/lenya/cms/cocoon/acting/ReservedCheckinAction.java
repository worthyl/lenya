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

package org.apache.lenya.cms.cocoon.acting;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.rc.FileReservedCheckInException;


/**
 * Checkin document
 */
public class ReservedCheckinAction extends RevisionControllerAction {
    /**
     * Checkin document
     *
     * @param redirector DOCUMENT ME!
     * @param resolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param src DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     *
     * @return HashMap with checkin parameters
     *
     * @throws Exception DOCUMENT ME!
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src, Parameters parameters) throws Exception {
        super.act(redirector, resolver, objectModel, src, parameters);

        HashMap actionMap = new HashMap();

        boolean backup = true;
	if (parameters.getParameter("backup", "true").equals("false")) backup = false;
        log.debug("Backup: " + backup);

        try {
            getRc().reservedCheckIn(getFilename(), getUsername(), backup);
        } catch (FileReservedCheckInException e) {
            actionMap.put("exception", "fileReservedCheckInException");
            actionMap.put("filename", getFilename());
            actionMap.put("checkType", e.getTypeString());
            
            String userId = e.getUsername();
            if (userId != null && !userId.equals("")) {
                User user = getUser(objectModel, userId);
                if (user != null) {
                    actionMap.put("userFullName", user.getName());
                }
                actionMap.put("user", userId);
            }
            
            actionMap.put("date", e.getDate());
            getLogger().warn(e.getMessage());

            return actionMap;
        } catch (Exception e) {
            actionMap.put("exception", "genericException");
            actionMap.put("filename", getFilename());
            actionMap.put("message", e.getMessage());
            getLogger().warn("The document " + getFilename() + " couldn't be checked in");

            return actionMap;
        }

        return null;
    }
}
