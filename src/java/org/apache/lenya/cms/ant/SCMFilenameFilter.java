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

package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

/**
 * Filter to exclude SCM files, such as CVS and .svn.
 * A set of filenames (provided as comma-separated list) is excluded.
 */
public class SCMFilenameFilter implements FilenameFilter {
    
    private List excludes;

    /**
     * Ctor.
     * @param excludes A comma-separated list of file names, e.g. CVS,.svn
     */
    public SCMFilenameFilter(String excludes) {
        this.excludes = Arrays.asList(excludes.split(","));
    }

    /**
     *  (non-Javadoc)
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept(File dir, String name) {
        return !this.excludes.contains(name);
    }
}
