/*
 * $Id: TaskSequence.java,v 1.7 2003/03/04 19:44:55 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
/*
 * TaskSequence.java
 *
 * Created on November 6, 2002, 6:30 PM
 */
package org.lenya.cms.task;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log4j.Category;


/**
 * A TaskSequence contains of multiple tasks that are executed successively.
 *
 * @author ah
 */
public class TaskSequence
        extends AbstractTask {
    
    static Category log = Category.getInstance(TaskSequence.class);

    // keeps the task order
    private Task[] tasks;
    private TaskManager taskManager;

    /**
     * DOCUMENT ME!
     *
     * @param configuration DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void init(Configuration configuration) throws ConfigurationException {
        taskManager = new TaskManager();
        taskManager.configure(configuration);

        // create task list
        Configuration[] taskConfigurations = configuration.getChildren(TaskManager.TASK_ELEMENT);
        tasks = new Task[taskConfigurations.length];

        // set task IDs
        for (int i = 0; i < tasks.length; i++) {
            String taskId = taskConfigurations[i].getAttribute(TaskManager.TASK_ID_ATTRIBUTE);
            tasks[i] = taskManager.getTask(taskId);
            log.debug("Adding task '" + taskId + "' to sequence.");
        }
    }

    /**
     * Returns the tasks in this sequence.
     *
     * @return DOCUMENT ME!
     */
    public Task[] getTasks() {
        return (Task[]) tasks.clone();
    }

    /**
     * Returns the TaskManager that is used to manage the tasks of this TaskSequence.
     *
     * @return DOCUMENT ME!
     */
    protected TaskManager getTaskManager() {
        return taskManager;
    }

    /**
     * Returns the ID of a specific Task.
     *
     * @param task DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IllegalStateException DOCUMENT ME!
     */
    public String getTaskId(Task task) {
        String[] taskIds = getTaskManager().getTaskIds();

        for (int j = 0; j < taskIds.length; j++) {
            if (getTaskManager().getTask(taskIds[j]) == task) {
                return taskIds[j];
            }
        }

        throw new IllegalStateException("Task-ID for " + task + " not found!");
    }

    /**
     * Executes the tasks.
     *
     * @param path DOCUMENT ME!
     */
    public void execute(String path)
            throws ExecutionException {
        try {
            Task[] tasks = getTasks();

            for (int i = 0; i < tasks.length; i++) {
                Task task = tasks[i];
                String taskId = getTaskId(task);
                log.debug("Executing task '" + taskId + "'");

                // create task parameters
                Parameters taskParameters = new Parameters();
                String[] names = getParameters().getNames();

                for (int parIndex = 0; parIndex < names.length; parIndex++) {
                    String name = names[parIndex];
                    boolean useParameter = true;

                    if (useParameter) {
                        taskParameters.setParameter(name, getParameters().getParameter(name));
                    }
                }

                // execute task
                task.parameterize(taskParameters);
                task.execute(path);
            }
        } catch (Exception e) {
            log.error("Cannot execute TaskSequence: ", e);
        }
    }
}
