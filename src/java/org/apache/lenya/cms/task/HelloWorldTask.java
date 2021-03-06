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

package org.apache.lenya.cms.task;


/**
 * A simple task to demonstrate the task behaviour. It outputs a string to the log4j log file. The
 * following parameter can be provided:<br/
 * ><code><strong>text</strong></code>: the string to be printed<br/
 * > If the parameter is omitted, "Hello World" is printed.
 */
public class HelloWorldTask extends AbstractTask {
    /**
     * Creates a new instance of HelloWorldTask
     */
    public HelloWorldTask() {
    }

    /**
     * Execute the task. All parameters must have been set with init().
     *
     * @param path DOCUMENT ME!
     * 
     * @throws ExecutionException if the exection fails
     */
    public void execute(String path) throws ExecutionException {
    }
}
