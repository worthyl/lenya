/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.lenya.ac.impl;

import junit.textui.TestRunner;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.PublicationHelper;

public class IdentityTest extends AccessControlTest {

    /**
     * Ctor.
     * @param test The test.
     */
    public IdentityTest(String test) {
        super(test);
    }

    /**
     * The main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        TestRunner.run(IdentityTest.class);
    }
    
    public static final String USER_ID = "lenya";

    /**
     * Tests the identity.
     * 
     * @throws AccessControlException if an error occurs
     */
    public void testIdentity() throws AccessControlException {
        Identity identity = new Identity();
        User user = getAccessController().getAccreditableManager().getUserManager().getUser(USER_ID);
        System.out.println("Adding user to identity: [" + user + "]");
        identity.addIdentifiable(user);
        
        assertSame(user, identity.getUser());
    }

}
