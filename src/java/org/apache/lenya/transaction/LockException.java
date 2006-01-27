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
package org.apache.lenya.transaction;

/**
 * Lock exception.
 *
 * @version $Id:$
 */
public class LockException extends TransactionException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Ctor.
     */
    public LockException() {
        super();
    }
    /**
     * Ctor.
     * @param message The message.
     */
    public LockException(String message) {
        super(message);
    }
    /**
     * Ctor.
     * @param message The message.
     * @param cause The cause.
     */
    public LockException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Ctor.
     * @param cause The cause.
     */
    public LockException(Throwable cause) {
        super(cause);
    }
}