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

/* $Id$  */

package org.apache.lenya.cms.task;

import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.util.NamespaceMap;
import org.apache.log4j.Logger;

/**
 * Task Notification
 * @deprecated Use the usecase framework instead.
 */
public class Notifier extends ParameterWrapper {

    private static Logger log = Logger.getLogger(Notifier.class);

    /**
     * <code>PREFIX</code> Notification namespace prefix
     */
    public static final String PREFIX = "notification";
    /**
     * <code>TARGET</code> Notification target
     */
    public static final String TARGET = "notification";
    /**
     * <code>PARAMETER_TO</code> To Parameter
     */
    public static final String PARAMETER_TO = "tolist";
    /**
     * <code>PARAMETER_FROM</code> From Parameter
     */
    public static final String PARAMETER_FROM = "from";

    private TaskManager taskManager;

    /**
     * Ctor.
     * @param _taskManager The task manager.
     * @param parameters The task wrapper parameters.
     */
    public Notifier(TaskManager _taskManager, Map parameters) {
        super(parameters);
        this.taskManager = _taskManager;
    }

    /**
     * Sends the notification message.
     * @param taskParameters The task parameters.
     * @throws ExecutionException when something went wrong.
     */
    public void sendNotification(TaskParameters taskParameters) throws ExecutionException {

        if (getMap().isEmpty()) {
            log.info("Not sending notification: no parameters provided.");
        } else if ("".equals(get(PARAMETER_TO).trim())) {
            log.info("Not sending notification: empty notification.tolist parameter.");
        } else {
            log.info("Sending notification");

            Task task = this.taskManager.getTask(TaskManager.ANT_TASK);

            Parameters params = new Parameters();

            params.setParameter(AntTask.TARGET, TARGET);

            String[] keys = { Task.PARAMETER_PUBLICATION_ID, Task.PARAMETER_CONTEXT_PREFIX,
                    Task.PARAMETER_SERVER_PORT, Task.PARAMETER_SERVER_URI,
                    Task.PARAMETER_SERVLET_CONTEXT };

            for (int i = 0; i < keys.length; i++) {
                params.setParameter(keys[i], taskParameters.get(keys[i]));
            }

            NamespaceMap mailMap = new NamespaceMap(PREFIX);
            mailMap.putAll(getMap());
            NamespaceMap propertiesMap = new NamespaceMap(AntTask.PROPERTIES_PREFIX);
            propertiesMap.putAll(mailMap.getPrefixedMap());

            Map prefixMap = propertiesMap.getPrefixedMap();
            String key;
            String value;
            Map.Entry entry;

            for (Iterator iter = prefixMap.entrySet().iterator(); iter.hasNext();) {
                entry = (Map.Entry) iter.next();
                key = (String) entry.getKey();
                value = (String) entry.getValue();
                String trimmedValue = value.replace((char) 160, ' ');
                trimmedValue = trimmedValue.trim();
                if (log.isDebugEnabled()) {
                    log.debug("Trimming value [" + value + "] to [" + trimmedValue + "]");
                    log.debug("First character: [" + value.charAt(0) + "] = ["
                            + (int) value.charAt(0) + "]");
                }
                params.setParameter(key, trimmedValue);
            }

            try {
                task.parameterize(params);
            } catch (ParameterException e) {
                throw new ExecutionException(e);
            }
            log.info("    Executing notification target ...");
            try {
                task.execute(taskParameters.get(Task.PARAMETER_SERVLET_CONTEXT));
            } catch (Exception e) {
                log.error("Error during notification: ", e);
            }
            log.info("    Notification target executed.");
        }
    }

    /**
     * Returns the task manager.
     * @return A task manager.
     */
    protected TaskManager getTaskManager() {
        return this.taskManager;
    }

    /**
     * @see org.apache.lenya.cms.task.ParameterWrapper#getPrefix()
     */
    public String getPrefix() {
        return PREFIX;
    }

    /**
     * @see org.apache.lenya.cms.task.ParameterWrapper#getRequiredKeys()
     */
    protected String[] getRequiredKeys() {
        String[] requiredKeys = {};
        return requiredKeys;
    }

}