/*
 * $Id: XercesParser.java,v 1.4 2003/02/20 13:40:42 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.wyona.xml.parser;

import org.apache.xerces.dom.*;
import org.apache.xerces.parsers.DOMParser;

import org.w3c.dom.*;

import org.wyona.xml.DOMWriter;

import java.io.*;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner, wyona
 * @version 0.5.5
 */
public class XercesParser implements Parser {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        Parser parser = new XercesParser();

        if (args.length != 1) {
            System.err.println("Usage: java " + parser.getClass().getName() + " example.xml");

            return;
        }

        Document doc = null;

        try {
            doc = parser.getDocument(args[0]);
        } catch (Exception e) {
            System.err.println(e);
        }

        new DOMWriter(new PrintWriter(System.out)).print(doc);
        System.out.println("");

        Document document = parser.getDocument();
        Element michi = parser.newElementNode(document, "Employee");
        michi.setAttribute("Id", "michi");
        michi.appendChild(parser.newTextNode(document, "Michi"));

        Element employees = parser.newElementNode(document, "Employees");
        employees.appendChild(parser.newTextNode(document, "\n"));
        employees.appendChild(michi);
        employees.appendChild(parser.newTextNode(document, "\n"));
        document.appendChild(employees);
        new DOMWriter(new PrintWriter(System.out)).print(document);
        System.out.println("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param filename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Document getDocument(String filename) throws Exception {
        DOMParser parser = new DOMParser();

        org.xml.sax.InputSource in = new org.xml.sax.InputSource(filename);
        parser.parse(in);

        return parser.getDocument();
    }

    /**
     * DOCUMENT ME!
     *
     * @param is DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Document getDocument(InputStream is) throws Exception {
        DOMParser parser = new DOMParser();
        org.xml.sax.InputSource in = new org.xml.sax.InputSource(is);
        parser.parse(in);

        return parser.getDocument();
    }

    /**
     * Creates a document from a reader.
     *
     * @param is DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Document getDocument(Reader reader) throws Exception {
        DOMParser parser = new DOMParser();
        org.xml.sax.InputSource in = new org.xml.sax.InputSource(reader);
        parser.parse(in);

        return parser.getDocument();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document getDocument() {
        return new DocumentImpl();
    }

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element newElementNode(Document document, String name) {
        return new ElementImpl((DocumentImpl) document, name);
    }

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Text newTextNode(Document document, String data) {
        return new TextImpl((DocumentImpl) document, data);
    }

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Comment newCommentNode(Document document, String data) {
        return new CommentImpl((DocumentImpl) document, data);
    }
}
