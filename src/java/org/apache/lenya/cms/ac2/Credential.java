/*
$Id: Credential.java,v 1.4 2003/07/23 13:21:23 gregor Exp $
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
package org.apache.lenya.cms.ac2;

import org.apache.lenya.cms.ac.Role;

import java.util.HashSet;
import java.util.Set;


/**
 * A credential assigns a set of {@link Role}s to an {@link Accreditable}.
 *
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public class Credential {
    private Accreditable accreditable;
    private Set roles = new HashSet();

    /**
     * Creates a new credential object.
     * @param accreditable The accreditable.
     */
    public Credential(Accreditable accreditable) {
        setAccreditable(accreditable);
    }

    /**
     * Sets the accreditable for this credential.
     * @param accreditable The accreditable.
     */
    protected void setAccreditable(Accreditable accreditable) {
        assert accreditable != null;
        this.accreditable = accreditable;
    }

    /**
     * Returns all roles of this credential.
     *
     * @return An array of roles.
     */
    public Role[] getRoles() {
        return (Role[]) roles.toArray(new Role[roles.size()]);
    }

    /**
     * Adds a role to this credential.
     * @param role The role to add.
     */
    public void addRole(Role role) {
        assert role != null;
        assert !roles.contains(role);
        roles.add(role);
    }

    /**
     * Removes a role from this credential.
     * @param role The role to remove.
     */
    public void removeRole(Role role) {
        assert (role != null) && roles.contains(role);
        roles.remove(role);
    }

    /**
     * Returns the accreditable of this credential.
     * @return An accreditable.
     */
    public Accreditable getAccreditable() {
        return accreditable;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "[credential of: " + getAccreditable() + "]";
    }
}
