/*
 * $Id: DefaultSiteTree.java,v 1.3 2003/05/12 12:57:19 egli Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.publication;

import org.apache.log4j.Category;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;

public class DefaultSiteTree
    implements SiteTree {
    static Category log = Category.getInstance(DefaultSiteTree.class);
    
    public static final String NAMESPACE_URI = "http://www.lenya.org/2003/sitetree";
    public static final String NODE_NAME = "node";
    public static final String LABEL_NAME = "label";

    private Document document = null;
    private File treefile = null;

    public DefaultSiteTree(String treefilename)
	throws ParserConfigurationException, SAXException, IOException {
 	this(new File(treefilename));
    }

    public DefaultSiteTree(File treefile)
	throws ParserConfigurationException, SAXException, IOException {
        // Read tree
	this.treefile = treefile;
	document = DocumentHelper.readDocument(treefile);
    }

    public void addNode(String parentid, String id, Label[] labels) {
	addNode(parentid, id, labels, null, null, false);
    }

    protected Node findNode(Node node, List ids) {
	if (ids.size() < 1) {
	    return node;
	} else {
	    NodeList nodes = node.getChildNodes();
	    for (int i = 0; i < nodes.getLength(); i++) {
		NamedNodeMap attributes = nodes.item(i).getAttributes();
		if (attributes != null) {
		    Node idAttribute = attributes.getNamedItem("id");
		    if (idAttribute != null && idAttribute.getNodeValue().equals(ids.get(0))) {
			return findNode(nodes.item(i), ids.subList(1, ids.size()));
		    }
		}
	    }
	}
	// node wasn't found
	return null;
    }

    public void addNode(String parentid, String id, Label[] labels,
			String href, String suffix, boolean link) {
        // Get parent element
        StringTokenizer st = new StringTokenizer(parentid, "/");
	ArrayList ids = new ArrayList();
	while (st.hasMoreTokens()) {
	    ids.add(st.nextToken());
	}

	Element root = document.getDocumentElement();
	
	NamespaceHelper helper = new NamespaceHelper(NAMESPACE_URI, "", document);
	
	Element elements[] = helper.getChildren(root);
	
	Node parentNode = findNode(root, ids);

        if (parentNode == null) {
            log.error("No nodes: " + parentid + ". No child added");
	    
            return;
        }
	
	log.debug("PARENT ELEMENT: " + parentNode);
	
//         // Check if child already exists
//         String newChildXPath = xpath_string + "/" + "node";
//         log.debug("CHECK: " + newChildXPath);
	
//         if (doc.selectSingleNode(newChildXPath + "[@id='" + id + "']") != null) {
//             log.error("Exception: XPath exists: " + newChildXPath + "[@id='" + id + "']");
//             log.error("No child added");
	    
//             return;
//         }

        // Add node
	Element child = helper.createElement(NODE_NAME);
	child.setAttribute("id", id);
 	if (href != null && href.length() > 0) {
 	    child.setAttribute("href", href);
 	}
	if (suffix != null && suffix.length() > 0) {
	    child.setAttribute("suffix", suffix);
	}
	if (link == true) {
	    child.setAttribute("link", "true");
	}
	for (int i = 0; i < labels.length; i++) {
	    String labelName = labels[i].getLabel();
	    Element label = helper.createElement(LABEL_NAME, labelName);
	    String labelLanguage = labels[i].getLanguage();
	    if (labelLanguage != null && labelLanguage.length() > 0) {
		label.setAttribute("xml:lang", labelLanguage);
	    }
	    child.appendChild(label);
	}

	parentNode.appendChild(child);
	log.debug("Tree has been modified: " + root);
    }

    public void addNode(Node node) {
        // Get parent element
	String parentId = "FIXME:"; // node.getParentId();
        StringTokenizer st = new StringTokenizer(parentId, "/");
	ArrayList ids = new ArrayList();
	while (st.hasMoreTokens()) {
	    ids.add(st.nextToken());
	}

	Element root = document.getDocumentElement();
	
	Node parentNode = findNode(root, ids);

        if (parentNode == null) {
            log.error("No nodes: " + parentId + ". No child added");
	    
            return;
        }
	
	log.debug("PARENT ELEMENT: " + parentNode);
	
//         // Check if child already exists
//         String newChildXPath = xpath_string + "/" + "node";
//         log.debug("CHECK: " + newChildXPath);
	
//         if (doc.selectSingleNode(newChildXPath + "[@id='" + id + "']") != null) {
//             log.error("Exception: XPath exists: " + newChildXPath + "[@id='" + id + "']");
//             log.error("No child added");
	    
//             return;
//         }

        // Add node
	parentNode.appendChild(node.cloneNode(true));
	log.debug("Tree has been modified: " + root);
    }

    public void deleteNode(String id) {}

    public Node getNode(String documentId) {
        StringTokenizer st = new StringTokenizer(documentId, "/");
	ArrayList ids = new ArrayList();
	while (st.hasMoreTokens()) {
	    ids.add(st.nextToken());
	}

	Element root = document.getDocumentElement();
	
	return findNode(root, ids);
    }

    public void serialize()
	throws IOException,
	       TransformerConfigurationException,
	       TransformerException  {
	DocumentHelper.writeDocument(document, treefile);
    }

    public static void main(String[] args) {
	try {
	    DefaultSiteTree sitetree = new DefaultSiteTree(args[0]);
	    Label label = new Label("Foo", null);
	    Label[] labels = { label };
	    
	    sitetree.addNode("/tutorial", "foo", labels);
	    
	    Label label_de = new Label("Qualit�t", "de");
	    Label label_en = new Label("Quality", "en");
	    Label[] labels2 = { label_de, label_en  };
	    sitetree.addNode("/tutorial/features", "here", labels2);
	    
	    sitetree.serialize();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
