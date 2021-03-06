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

package org.apache.lenya.cms.workflow;

import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.State;
import org.apache.lenya.workflow.impl.Version;

public class CMSVersion extends Version {

    /**
     * Returns the user ID.
     * @return A string.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     * @param userId A user ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Ctor.
     * @param event The event.
     * @param state The state.
     */
    public CMSVersion(Event event, State state) {
        super(event, state);
    }
    
    private String userId;

}
