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
package org.apache.lenya.cms.ac.usecases;

import org.apache.lenya.ac.User;

/**
 * Usecase to delete a user.
 *
 * @version $Id:$
 */
public class DeleteUser extends AccessControlUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        String userId = getParameterAsString(UserProfile.USER_ID);
        User user = getUserManager().getUser(userId);
        if (user == null) {
            throw new RuntimeException("User [" + userId + "] not found.");
        }
        
        getUserManager().remove(user);
        user.delete();
    }
}
