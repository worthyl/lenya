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

/* $Id$  */

package org.apache.lenya.cms.site.tree;

import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.tree.DefaultSiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;

import junit.framework.TestCase;

/**
 * Tests the site tree
 */
public class SiteTreeNodeImplTest extends TestCase {

    private SiteTreeNode node = null;

    /**
     * Constructor.
     * @param test The test.
     */
    public SiteTreeNodeImplTest(String test) {
        super(test);
    }

    /**
     * The main program.
     * The parameters are set from the command line arguments.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(SiteTreeNodeImplTest.class);
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        DefaultSiteTree siteTree = new DefaultSiteTree("testTree");
        Label label = new Label("Foo", "en");
        Label[] fooLabels = { label };
        siteTree.addNode("/foo", fooLabels, null, null, false);
        label = new Label("Bar", "en");
        Label label_de = new Label("Stab", "de");
        Label[] barLabels = { label, label_de };
        siteTree.addNode(
            "/foo/bar",
            barLabels,
            "http://exact.biz",
            "suffix",
            true);

        this.node = siteTree.getNode("/foo/bar");
    }

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test getAbsoluteId
     * 
     */
    final public void testGetAbsoluteId() {
        assertEquals(this.node.getAbsoluteId(), "/foo/bar");
    }

    /**
     * Test getId
     *
     */
    final public void testGetId() {
        assertEquals(this.node.getId(), "bar");
    }

    /**
     * Test getLabels
     *
     */
    final public void testGetLabels() {
        assertEquals(this.node.getLabels().length, 2);
        for (int i = 0; i < this.node.getLabels().length; i++) {
            Label label = this.node.getLabels()[i];
            Label label1 = new Label("Bar", "en");
            Label label2 = new Label("Stab", "de");
            assertTrue(label.equals(label1) || label.equals(label2));
        }
    }

    /**
     * Test getLabel
     *
     */
    final public void testGetLabel() {
        Label label = this.node.getLabel("en");
        assertNotNull(label);
        assertEquals(label.getLabel(), "Bar");
    }

    /**
     * Test addLabel 
     *
     */
    final public void testAddLabel() {
        Label label = new Label("Barolo", "it");
        this.node.addLabel(label);
        label = this.node.getLabel("it");
        assertNotNull(label);
        assertEquals(label.getLabel(), "Barolo");
        label = this.node.getLabel("ch");
        assertNull(label);
    }

    /**
     * Test removeLabel
     *
     */
    final public void testRemoveLabel() {
        Label label = new Label("Bar", "en");
        assertNotNull(this.node.getLabel("en"));
        this.node.removeLabel(label);
        assertNull(this.node.getLabel("en"));
    }

    /**
     * Test getHref
     *
     */
    final public void testGetHref() {
        assertEquals(this.node.getHref(), "http://exact.biz");
    }

    /**
     * Test getSuffix
     * 
     *
     */
    final public void testGetSuffix() {
        assertEquals(this.node.getSuffix(), "suffix");
    }

    /**
     * Test hasLink
     *
     */
    final public void testHasLink() {
        assertTrue(this.node.hasLink());
    }

}