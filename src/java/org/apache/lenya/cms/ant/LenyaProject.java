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
package org.apache.lenya.cms.ant;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.tools.ant.Project;

/**
 * Ant project that provides access to the service manager.
 * 
 * @version $Id:$
 */
public class LenyaProject extends Project {

    private ServiceManager manager;
    
    /**
     * Ctor.
     * @param manager The service manager.
     */
    public LenyaProject(ServiceManager manager) {
        this.manager = manager;
    }
    
    /**
     * @return The service manager.
     */
    public ServiceManager getServiceManager() {
        return this.manager;
    }
    
}
