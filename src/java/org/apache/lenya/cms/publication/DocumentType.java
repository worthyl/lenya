/*
$Id: DocumentType.java,v 1.8 2003/07/23 13:21:10 gregor Exp $
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
package org.apache.lenya.cms.publication;

import org.apache.lenya.cms.authoring.ParentChildCreatorInterface;


/**
 * A document type.
 *
 * @author <a href="mailto:andreas.hartmann@wyona.org">Andreas Hartmann</a>
 */
public class DocumentType {
    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/doctypes/1.0";
    public static final String DEFAULT_PREFIX = "dt";

    /** Creates a new instance of DocumentType
     * 
     * @param name the name of the document type
     * 
     */
    protected DocumentType(String name) {
        assert name != null;
        this.name = name;
    }

    private String name;

    /**
    * Returns the name of this document type.
     * @return A string value.
     */
    public String getName() {
        return name;
    }

    private ParentChildCreatorInterface creator = null;

	/**
	 * Get the creator for this document type.
	 * 
	 * @return a <code>ParentChildCreatorInterface</code>
	 */
    public ParentChildCreatorInterface getCreator() {
        return creator;
    }

	/**
	 * Set the creator
	 * 
	 * @param creator a <code>ParentChildCreatorInterface</code>
	 */
    protected void setCreator(ParentChildCreatorInterface creator) {
        assert creator != null;
        this.creator = creator;
    }

    private String workflowFile = null;

    /**
     * Returns if this document type has a workflow definition.
     * 
     * @return A boolean value.
     */
    public boolean hasWorkflow() {
        return workflowFile != null;
    }

	/**
	 * Get the file name of the workflow file.
	 * 
	 * @return a <code>String</code>
	 * 
	 * @throws DocumentTypeBuildException if the document type has no workflow
	 */
    public String getWorkflowFileName() throws DocumentTypeBuildException {
        if (!hasWorkflow()) {
            throw new DocumentTypeBuildException("The document type '" + getName() +
                "' has no workflow!");
        }

        return workflowFile;
    }

	/**
	 * Set the file name of the workflow file.
	 * 
	 * @param string the new file name
	 */
    public void setWorkflowFileName(String string) {
        assert string != null;
        workflowFile = string;
    }

    /** (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getName();
    }
}
