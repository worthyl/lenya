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
package org.apache.lenya.cms.authoring;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;


/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DocumentCreatorTest extends TestCase {
    /**
     * Constructor.
     * @param test The test to invoke.
     */
    public DocumentCreatorTest(String test) {
        super(test);
    }

    /**
     * The main program.
     * The parameters are set from the command line arguments.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        args = PublicationHelper.extractPublicationArguments(args);
        TestRunner.run(getSuite());
    }

    /**
     * Returns the test suite.
     * @return A Test object.
     */
    public static Test getSuite() {
        return new TestSuite(DocumentCreatorTest.class);
    }

    /**
     * Tests whatever you want.
     * @throws CreatorException when something went wrong.
     * @throws ParserConfigurationException when something went wrong.
     * @throws SAXException when something went wrong.
     * @throws IOException when something went wrong.
     */
    public void testCreator()
        throws CreatorException, ParserConfigurationException, SAXException, IOException {
        Publication publication = PublicationHelper.getPublication();
        DocumentCreator creator = new DocumentCreator();
        File authoringDirectory = new File(publication.getDirectory(), AUTHORING_DIR);

        creator.create(publication, authoringDirectory, TREE_FILE, PARENT_ID, CHILD_ID, CHILD_NAME,
            CHILD_TYPE, DOCUMENT_TYPE);

        File documentFile = new File(authoringDirectory, CREATED_FILE);
        assertTrue(documentFile.exists());
        System.out.println("File was created: " + documentFile.getAbsolutePath());

        File sitetreeFile = new File(authoringDirectory, TREE_FILE);

        SiteTree sitetree = new DefaultSiteTree(sitetreeFile);
        SiteTreeNode node = sitetree.getNode(PARENT_ID + "/" + CHILD_ID);
        assertNotNull(node);
        System.out.println("Sitetree node was created: " + node.getId() + " (label: " +
            node.getLabel("de") + ")");
    }

    protected static final String AUTHORING_DIR = "content" + File.separator + "authoring";
    protected static final String TREE_FILE = "sitetree.xml";
    protected static final String PARENT_ID = "/tutorial";
    protected static final String CHILD_ID = "test-document";
    protected static final String CHILD_NAME = "Test Document";
    protected static final String CHILD_TYPE = "leaf";
    protected static final String DOCUMENT_TYPE = "simple";
    protected static final String CREATED_FILE = "tutorial/test-document/index.xml";

    /** @see junit.framework.TestCase#setUp() */
    protected void setUp() throws Exception {
        if (PublicationHelper.getPublication() == null) {
            String[] args = { "D:\\Development\\build\\tomcat-4.1.24\\webapps\\lenya", "test" };
            PublicationHelper.extractPublicationArguments(args);
        }
    }
}
