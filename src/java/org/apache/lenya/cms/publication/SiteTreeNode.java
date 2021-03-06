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

package org.apache.lenya.cms.publication;

import java.util.List;

/**
 * This interface is a wrapper around the more general w3c.Node which
 * hides some details which are irrelevant for site tree nodes. It basically
 * delegates everything to the Node.
 */
public interface SiteTreeNode {

    /**
     * Get the parent-id of this node.
     * 
     * @return the parent-id.
     */
    String getParentId();
    
    /**
     * Returns the parent node of this node.
     * @return A sitetree node.
     */
    SiteTreeNode getParent();

    /**
     * Returns the parent node of this node or null if the parent has no label for the given language.
     * @param language A language string.
     * @return A sitetree node.
     */
    SiteTreeNode getParent(String language);

    /**
     * Get the absolute id of this node.
     * 
     * @return  the absolute id.
     */
    String getAbsoluteId();

    /**
     * Get the absolute parent-id of this node.
     * 
     * @return  the absolute parent-id.
     */
    String getAbsoluteParentId();

    /**
     * Get the id of this node.
     * 
     * @return the node id.
     */
    String getId();

    /**
     * Get all labels for this node (independent of their language attribute).
     * 
     * @return an <code>Array</code> of labels.
     */
    Label[] getLabels();

    /**
     * Get the label for a specific language.
     * 
     * @param xmlLanguage the language for which the label is requested.
     * 
     * @return a <code>Label</code> if there is one for the given language, 
     * null otherwise. 
     */
    Label getLabel(String xmlLanguage);

    /**
     * Add a label to this node iff the node does not have this label already.
     * 
     * @param label the label to be added.
     */
    void addLabel(Label label);

    /**
     * Remove a label from this node.
     * 
     * @param label the label to be removed.
     */
    void removeLabel(Label label);

    /**
     * Check whether this node is visible in the navigation 
     * 
     * @return true if this node is visible. The method should also
     * return true if the attribute is not set. That means a node missing 
     * this attribute becomes visible by default.
     */
    boolean visibleInNav();

    /**
     * Get the href of this node.
     * 
     * @return the href.
     * @deprecated use the href attribute of the label instead
     */
    String getHref();

    /**
     * Get the suffix of this node.
     * 
     * @return the suffix.
     */
    String getSuffix();

    /**
     * Check whether this node has a link.
     * 
     * @return true if this node has a link.
     */
    boolean hasLink();

    /**
     * Get the sitetreenodes, which are children of this node
     * 
     * @return the children.
     */
    SiteTreeNode[] getChildren();

    /**
     * Get the sitetreenodes, which are children of this node
     * and contain a label for the given language.
     * 
     * @param language A language string.
     * @return the children.
     */
    SiteTreeNode[] getChildren(String language);

    /**
     * Remove the children of the node
     * 
     * @return the removed node
     * @deprecated Use deleteChildren() instead
     */
    SiteTreeNode[] removeChildren();
    
    /**
     * Remove the children of this node.
     */
    void deleteChildren() throws SiteTreeException;

	/**
	 * Get the sitetreenodes, which are the siblings following this node
	 * 
	 * @return the children.
	 */
	SiteTreeNode[] getNextSiblings();

    /**
	 * @return string. The document-id corresponding to the next sibling node.
	 */
	String getNextSiblingDocumentId();

    /**
     * Call the visit method of the visitor, that mean
     * the operation that shall be perfoemed on this node
     * (Visitor pattern)
     * @param visitor The visitor.
     * 
     * @throws DocumentException if an error occurs
     */
    void accept(SiteTreeNodeVisitor visitor) throws DocumentException;

    /**
     * Traverse the node ant its children and call the
     * accept method.
     * @param visitor The visitor.
     * 
     * @throws DocumentException if an error occurs
     */
    void acceptSubtree(SiteTreeNodeVisitor visitor) throws DocumentException;

    /**
     * Traverse in a reverse way the node ant its children and call the
     * accept method.
     * @param visitor The visitor.
     * 
     * @throws DocumentException if an error occurs
	 */
	void acceptReverseSubtree(SiteTreeNodeVisitor visitor) throws DocumentException; 
 
	/**
     * Sets a label of an this node. If the label does not exist, it is added.
     * Otherwise, the existing label is replaced.
     * 
     * @param label the label to add
     */
    void setLabel(Label label);
 
    /**
     * Sets an attribute of this node. If the attribute already exists its value will be overwritten
     * 
     * @param attributeName name of the attribute
     * @param attributeValue the value of the respective attribute
     */
    void setNodeAttribute (String attributeName, String attributeValue);

    /**
     * Give a list of the children and this node in a pre order way
     * @return The list
     */
    List preOrder();
    
	/**
     * Give a list of the children and this node in a post order way
	 * @return The list
	 */
	List postOrder();
    
}
