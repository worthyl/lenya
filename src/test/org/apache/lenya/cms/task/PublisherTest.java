/*
$Id: PublisherTest.java,v 1.5 2003/08/08 17:05:33 egli Exp $
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
package org.apache.lenya.cms.task;

import junit.framework.Test;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.cms.publication.Publication;

import java.io.File;
import java.io.IOException;


/**
 * @author andreas
 *
 */
public class PublisherTest extends AntTaskTest {

    /**
     * Create a test.
     * 
     * @param test the test
     */
    public PublisherTest(String test) {
        super(test);
    }

    /**
     * Creates a test suite.
     * 
     * @return a test
     */
    public static Test getSuite() {
        return new TestSuite(PublisherTest.class);
    }

    /**
     * The main program for the PublisherTest class
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        AntTaskTest.initialize(args);
        TestRunner.run(getSuite());
    }

    public static final String DOCUMENT_ID = "/tutorial";
    public static final String DOCUMENT_LANGUAGE = "en";

	/**
	 *  (non-Javadoc)
	 * @see org.apache.lenya.cms.task.AntTaskTest#evaluateTest()
	 */
    protected void evaluateTest() throws IOException {
        Publication pub = PublicationHelper.getPublication();
        DocumentIdToPathMapper mapper = pub.getPathMapper();
        File publishedFile = mapper.getFile(pub, Publication.LIVE_AREA, DOCUMENT_ID, DOCUMENT_LANGUAGE);
        System.out.println("Path of file to publish: " + publishedFile);
        assertTrue(publishedFile.exists());
        System.out.println("Published file exists: " + publishedFile.getCanonicalPath());
    }

	/**
	 *  (non-Javadoc)
	 * @see org.apache.lenya.cms.task.AntTaskTest#getTaskParameters()
	 */
    protected Parameters getTaskParameters() {
        Parameters parameters = super.getTaskParameters();
        parameters.setParameter("properties.publish.documentid", DOCUMENT_ID);
        parameters.setParameter("properties.publish.language", DOCUMENT_LANGUAGE);
        Publication pub = PublicationHelper.getPublication();
        DocumentIdToPathMapper mapper = pub.getPathMapper();
        parameters.setParameter("properties.publish.sources", mapper.getPath(DOCUMENT_ID, DOCUMENT_LANGUAGE));

        return parameters;
    }

	/**
	 *  (non-Javadoc)
	 * @see org.apache.lenya.cms.task.AntTaskTest#getTarget()
	 */
    protected String getTarget() {
        return "publish";
    }
}
