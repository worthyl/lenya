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

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Concrete implementation of the <code>SiteTreeNode</code> interface.
 * 
 * @see org.apache.lenya.cms.publication.SiteTreeNode
 */
public class SiteTreeNodeImpl implements SiteTreeNode {
    private static Logger log = Logger.getLogger(SiteTreeNodeImpl.class);
    public static final String ID_ATTRIBUTE_NAME = "id";
    public static final String VISIBLEINNAV_ATTRIBUTE_NAME="visibleinnav";
    public static final String HREF_ATTRIBUTE_NAME = "href";
    public static final String SUFFIX_ATTRIBUTE_NAME = "suffix";
    public static final String LINK_ATTRIBUTE_NAME = "link";
    public static final String LANGUAGE_ATTRIBUTE_NAME = "xml:lang";
    public static final String NODE_NAME = "node";
    public static final String LABEL_NAME = "label";

    private Node node = null;
    private DefaultSiteTree sitetree = null;

    /**
     * Creates a new SiteTreeNodeImpl object.
     *
     * @param node the node which is to be wrapped by this SiteTreeNode 
     */
    public SiteTreeNodeImpl(Node node, DefaultSiteTree sitetree) {
        this.node = node;
        this.sitetree = sitetree;
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getParentId()
     * @deprecated use getParent().getId() instead
     */
    public String getParentId() {
        Node parent = node.getParentNode();
        String parentId = null;

        if (parent == null) {
            parentId = "/";
        } else {
            synchronized(sitetree) {
                NamedNodeMap attributes = parent.getAttributes();
                if (attributes == null) {
                    parentId = "/";
                } else {
                    Node idAttribute = attributes.getNamedItem(ID_ATTRIBUTE_NAME);

                    if (idAttribute == null) {
                        parentId = "/";
                    } else {
                        parentId = idAttribute.getNodeValue();
                    }
                    
                }
            }
        }
        return parentId;
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getId()
     */
    public String getId() {
        String id = null;
        synchronized (sitetree) {
            if (node == node.getOwnerDocument().getDocumentElement()) {
                id = "";
            } else {
                id = node.getAttributes().getNamedItem(ID_ATTRIBUTE_NAME).getNodeValue(); 
            }
        }
        return id;
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getAbsoluteParentId()
     * @deprecated use getParent().getAbsoluteId() instead
     */
    public String getAbsoluteParentId() {
        SiteTreeNode parent = this.getParent();
        return (parent == null) ? "" : parent.getAbsoluteId();
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getAbsoluteId()
     */
    public String getAbsoluteId() {
        String absoluteId = "";
        Node currentNode = node;
        
        NamedNodeMap attributes = null;
        Node idAttribute = null;

        synchronized (sitetree) {
            while (currentNode != null) {
                attributes = currentNode.getAttributes();
    
                if (attributes == null) {
                    break;
                }
    
                idAttribute = attributes.getNamedItem(ID_ATTRIBUTE_NAME);
    
                if (idAttribute == null) {
                    break;
                }
    
                absoluteId = "/" + idAttribute.getNodeValue() + absoluteId;
                currentNode = currentNode.getParentNode();
            }
        }
        return absoluteId;
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getLabels()
     */
    public Label[] getLabels() {
        ArrayList labels = new ArrayList();

        synchronized(sitetree) {
            NodeList children = node.getChildNodes();
    
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
    
                if ((child.getNodeType() == Node.ELEMENT_NODE)
                    && child.getNodeName().equals(LABEL_NAME)) {
                    String labelName = DocumentHelper.getSimpleElementText((Element) child);
                    String labelLanguage = null;
                    Node languageAttribute =
                        child.getAttributes().getNamedItem(LANGUAGE_ATTRIBUTE_NAME);
    
                    if (languageAttribute != null) {
                        labelLanguage = languageAttribute.getNodeValue();
                    }
    
                    Label label = new Label(labelName, labelLanguage);
                    
                    Node hrefAttribute =
                        child.getAttributes().getNamedItem(HREF_ATTRIBUTE_NAME);
    
                    if (hrefAttribute != null) {
                        label.setHref(hrefAttribute.getNodeValue());
                    }
                    labels.add(label);
                }
            }
        }
        return (Label[]) labels.toArray(new Label[labels.size()]);
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getLabel(java.lang.String)
     */
    public Label getLabel(String xmlLanguage) {
        Label label = null;
        Label[] labels = getLabels();
        String language = null;

        for (int i = 0; i < labels.length; i++) {
            language = labels[i].getLanguage();

            // FIXME: This expression is too complicated 
            // considering there can no longer be any labels with
            // a null language, i.e. each label must have a language.
            if ((((xmlLanguage == null) || (xmlLanguage.equals(""))) && (language == null))
                || ((language != null) && (language.equals(xmlLanguage)))) {
                label = labels[i];

                break;
            }
        }

        return label;
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#addLabel(org.apache.lenya.cms.publication.Label)
     */
    public void addLabel(Label label) {
        if (getLabel(label.getLanguage()) == null) {
            // only add the label if there is no label with the same language yet.

            NamespaceHelper helper = getNamespaceHelper();
            Element labelElem = helper.createElement(SiteTreeNodeImpl.LABEL_NAME, label.getLabel());

            labelElem.setAttribute(SiteTreeNodeImpl.LANGUAGE_ATTRIBUTE_NAME, label.getLanguage());
            if (label.getHref() != null) {
                labelElem.setAttribute(SiteTreeNodeImpl.HREF_ATTRIBUTE_NAME, label.getHref());
            }
            synchronized (sitetree) {
                node.insertBefore(labelElem, node.getFirstChild());
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#removeLabel(org.apache.lenya.cms.publication.Label)
     */
    public void removeLabel(Label label) {
        if (getLabel(label.getLanguage()) != null) {
            // this node doesn't contain this label

            synchronized (sitetree) {
                NodeList children = node.getChildNodes();

                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
    
                    if ((child.getNodeType() == Node.ELEMENT_NODE)
                        && child.getNodeName().equals(LABEL_NAME)
                        && child.getFirstChild().getNodeValue().equals(label.getLabel())) {
    
                        Node languageAttribute =
                            child.getAttributes().getNamedItem(LANGUAGE_ATTRIBUTE_NAME);
    
                        if (languageAttribute != null
                            && languageAttribute.getNodeValue().equals(label.getLanguage())) {
                            node.removeChild(child);
                            break;
                        }
                    }
                }
            }
        }
    }
 
    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#hasLink()
     */
    public boolean visibleInNav() {
        boolean isVisibleInNav = true;
        synchronized (sitetree) {
            Node attribute = node.getAttributes().getNamedItem(VISIBLEINNAV_ATTRIBUTE_NAME);
    
            if (attribute != null) {
                isVisibleInNav = attribute.getNodeValue().equals("true");
            }
        }
        return isVisibleInNav;
    }
 
    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getHref()
     */
    public String getHref() {
        String href = null;
        synchronized (sitetree) {
            Node attribute = node.getAttributes().getNamedItem(HREF_ATTRIBUTE_NAME);

            if (attribute != null) {
                href = attribute.getNodeValue();
            }
        }
        return href;
    }
   
    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getSuffix()
     */
    public String getSuffix() {
        String suffix = null;
        synchronized (sitetree) {
            Node attribute = node.getAttributes().getNamedItem(SUFFIX_ATTRIBUTE_NAME);

            if (attribute != null) {
                suffix = attribute.getNodeValue();
            }
        }
        return suffix;
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#hasLink()
     */
    public boolean hasLink() {
        boolean hasLink = false;
        synchronized (sitetree) {
            Node attribute = node.getAttributes().getNamedItem(LINK_ATTRIBUTE_NAME);
    
            if (attribute != null) {
                hasLink = attribute.getNodeValue().equals("true");
            }
        }
        return hasLink;
    }
    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getChildren()
     */
    public SiteTreeNode[] getChildren() {
        List childElements = new ArrayList();
        synchronized (sitetree) {
            NamespaceHelper helper = getNamespaceHelper();
            Element[] elements = helper.getChildren((Element) node, SiteTreeNodeImpl.NODE_NAME);
    
            for (int i = 0; i < elements.length; i++) {
                SiteTreeNode newNode = new SiteTreeNodeImpl(elements[i], sitetree);
                childElements.add(newNode);
            }
        }
        return (SiteTreeNode[]) childElements.toArray(new SiteTreeNode[childElements.size()]);
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#removeChildren()
     */
    public SiteTreeNode[] removeChildren() {
        List childElements = new ArrayList();
        synchronized (sitetree) {
            NamespaceHelper helper = getNamespaceHelper();
            Element[] elements = helper.getChildren((Element) node, SiteTreeNodeImpl.NODE_NAME);
            for (int i = 0; i < elements.length; i++) {
                node.removeChild(elements[i]);
                SiteTreeNode newNode = new SiteTreeNodeImpl(elements[i], sitetree);
                childElements.add(newNode);
            }
        }
        return (SiteTreeNode[]) childElements.toArray(new SiteTreeNode[childElements.size()]);
    }

    /* (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#deleteChildren()
     */
    public void deleteChildren() {
        synchronized (sitetree) {
            NamespaceHelper helper = getNamespaceHelper();
            Element[] elements = helper.getChildren((Element) node, SiteTreeNodeImpl.NODE_NAME);
            for (int i = 0; i < elements.length; i++) {
                node.removeChild(elements[i]);
            }
        }
    }
    
    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getChildren()
     */
    public SiteTreeNode[] getNextSiblings() {
        List siblingElements = new ArrayList();

        synchronized (sitetree) {
            NamespaceHelper helper = getNamespaceHelper();
            Element[] elements = helper.getNextSiblings((Element) node, SiteTreeNodeImpl.NODE_NAME);
    
            for (int i = 0; i < elements.length; i++) {
                SiteTreeNode newNode = new SiteTreeNodeImpl(elements[i], sitetree);
                siblingElements.add(newNode);
            }
        }

        return (SiteTreeNode[]) siblingElements.toArray(new SiteTreeNode[siblingElements.size()]);
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getNextSiblingDocumentId()
     */
    public String getNextSiblingDocumentId() {
        SiteTreeNode[] siblings = getNextSiblings();
        if (siblings != null && siblings.length > 0) {
            return siblings[0].getAbsoluteId();
        } else {
            return null;
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#accept(org.apache.lenya.cms.publication.SiteTreeNodeVisitor)
     */
    public void accept(SiteTreeNodeVisitor visitor) throws DocumentException {
        visitor.visitSiteTreeNode(this);
    }

    /**
     * (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#acceptSubtree(org.apache.lenya.cms.publication.SiteTreeNodeVisitor)
     */
    public void acceptSubtree(SiteTreeNodeVisitor visitor) throws DocumentException {
        this.accept(visitor);
        SiteTreeNode[] children = this.getChildren();
        if (children == null) {
            log.info("The node " + this.getId() + " has no children");
            return;
        } else {
            for (int i = 0; i < children.length; i++) {
                children[i].acceptSubtree(visitor);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#acceptSubtree(org.apache.lenya.cms.publication.SiteTreeNodeVisitor)
     */
    public void acceptReverseSubtree(SiteTreeNodeVisitor visitor) throws DocumentException {
        List orderedNodes = this.postOrder();
        for (int i = 0; i < orderedNodes.size(); i++) {
            SiteTreeNode node = (SiteTreeNode) orderedNodes.get(i);
            node.accept(visitor);
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#postOrder()
     */
    public List postOrder() {
        List list = new ArrayList();
        SiteTreeNode[] children = this.getChildren();
        for (int i = 0; i < children.length; i++) {
            List orderedChildren = children[i].postOrder();
            list.addAll(orderedChildren);
        }
        list.add(this);
        return list;
    }
    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#setLabel(org.apache.lenya.cms.publication.Label)
     */
    public void setLabel(Label label) {
        Label existingLabel = getLabel(label.getLanguage());
        if (existingLabel != null) {
            removeLabel(existingLabel);
        }
        addLabel(label);
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#setLabel(org.apache.lenya.cms.publication.Label)
     */
    public void setNodeAttribute (String attributeName, String attributeValue) {
        synchronized (sitetree) {
            Element element = (Element) node;
            element.setAttribute(attributeName, attributeValue);
        }
    }
    
    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getChildren(java.lang.String)
     */
    public SiteTreeNode[] getChildren(String language) {
        SiteTreeNode[] children = getChildren();
        List languageChildren = new ArrayList();

        for (int i = 0; i < children.length; i++) {
            if (children[i].getLabel(language) != null) {
                languageChildren.add(children[i]);
            }
        }

        return (SiteTreeNode[]) languageChildren.toArray(new SiteTreeNode[languageChildren.size()]);
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getParent()
     */
    public SiteTreeNode getParent() {
        SiteTreeNode parent = null;

        synchronized (sitetree) {
            Node parentNode = node.getParentNode();
            if (parentNode.getNodeType() == Node.ELEMENT_NODE
                && parentNode.getLocalName().equals(NODE_NAME)) {
                parent = new SiteTreeNodeImpl(parentNode, sitetree);
            }
        }

        return parent;
    }

    /**
     * Returns the namespace helper of the sitetree XML document.
     * @return A namespace helper.
     */
    protected NamespaceHelper getNamespaceHelper() {
        NamespaceHelper helper =
            new NamespaceHelper(SiteTree.NAMESPACE_URI, "", node.getOwnerDocument());
        return helper;
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getParent(java.lang.String)
     */
    public SiteTreeNode getParent(String language) {
        SiteTreeNode parent = getParent();

        Label label = parent.getLabel(language);
        if (label == null) {
            parent = null;
        }

        return parent;
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#preOrder()
     */
    public List preOrder() {
        List list = new ArrayList();
        list.add(this);
        SiteTreeNode[] children = this.getChildren();
        for (int i = 0; i < children.length; i++) {
            List orderedChildren = children[i].preOrder();
            list.addAll(orderedChildren);
        }
        return list;
    }
    
    protected DefaultSiteTree getDefaultSiteTree() {
        return this.sitetree;
    }
}
