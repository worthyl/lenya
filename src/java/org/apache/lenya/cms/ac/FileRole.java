/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.ac;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;

import java.io.File;


/**
 * @author egli
 *
 *
 */
public class FileRole extends Role implements Item {
    /**
    * Creates a new file role.
    * @param configurationDirectory The configuration directory.
    * @param roleName The role name.
    */
    public FileRole(File configurationDirectory, String roleName) {
        setName(roleName);
        setConfigurationDirectory(configurationDirectory);
    }

    public static final String GROUP = "role";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String CLASS_ATTRIBUTE = "class";

    /**
     * Creates a new FileRole object.
     */
    public FileRole() {
    }

    /**
     * Configure this instance of <code>FileRole</code>
     *
     * @param config containing the role details
     * @throws ConfigurationException if the <code>FileRole</code> could not be configured
     */
    public void configure(Configuration config) throws ConfigurationException {
        setName(config.getAttribute(NAME_ATTRIBUTE));
    }

    /**
     * Save the role
     *
     * @throws AccessControlException if the save fails
     */
    public void save() throws AccessControlException {
        DefaultConfigurationSerializer serializer = new DefaultConfigurationSerializer();
        Configuration config = createConfiguration();
        File xmlPath = getConfigurationDirectory();
        File xmlfile = new File(xmlPath, getName() + RoleManager.SUFFIX);

        try {
            serializer.serializeToFile(xmlfile, config);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }
    }

    /**
     * Create a configuration containing the role details
     *
     * @return a <code>Configuration</code>
     */
    private Configuration createConfiguration() {
        DefaultConfiguration config = new DefaultConfiguration(GROUP);
        config.setAttribute(NAME_ATTRIBUTE, getName());
        config.setAttribute(CLASS_ATTRIBUTE, this.getClass().getName());

        return config;
    }

    private File configurationDirectory;

    /**
     * Returns the configuration directory.
     * @return A file object.
     */
    public File getConfigurationDirectory() {
        return configurationDirectory;
    }

    /**
     * @see org.apache.lenya.cms.ac.Item#setConfigurationDirectory(java.io.File)
     */
    public void setConfigurationDirectory(File file) {
        configurationDirectory = file;
    }
}
