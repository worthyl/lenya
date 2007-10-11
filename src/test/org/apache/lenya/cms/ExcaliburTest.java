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

/* $Id: ExcaliburTest.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.cms;

import java.io.InputStream;

import org.apache.avalon.excalibur.testcase.ExcaliburTestCase;

public abstract class ExcaliburTest extends ExcaliburTestCase {

    /**
     * @param test The test.
     */
    public ExcaliburTest(String test) {
        super(test);
    }

    public static final String CONFIGURATION = "/" +
        ExcaliburTest.class.getPackage().getName().replace('.', '/') + "/lenya.xtest";

    /**
     * @see org.apache.avalon.excalibur.testcase.ExcaliburTestCase#prepare()
     */
    protected void prepare() throws Exception {
        System.out.println(CONFIGURATION);
        InputStream stream = ExcaliburTest.class.getResourceAsStream(CONFIGURATION);
        prepare(stream);
    }

}