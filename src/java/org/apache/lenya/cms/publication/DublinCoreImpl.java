/*
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Access dublin core meta data in documents.
 * This class uses the dublin core specification from 2003-03-04.
 *
 * @author <a href="mailto:gregor@apache.org">Gregor J. Rothfuss</a>
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 * @version $Id: DublinCoreImpl.java,v 1.12 2004/02/21 13:44:43 gregor Exp $
 */
public class DublinCoreImpl {
    private Document cmsdocument;
    private File infofile;

    private Map elements = new HashMap();
    private Map terms = new HashMap();

    private static final String META = "meta";

    private static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";
    private static final String DC_PREFIX = "dc";

    public static final String[] ELEMENTS =
        {
            DublinCore.ELEMENT_TITLE,
            DublinCore.ELEMENT_CREATOR,
            DublinCore.ELEMENT_SUBJECT,
            DublinCore.ELEMENT_DESCRIPTION,
            DublinCore.ELEMENT_PUBLISHER,
            DublinCore.ELEMENT_CONTRIBUTOR,
            DublinCore.ELEMENT_DATE,
            DublinCore.ELEMENT_TYPE,
            DublinCore.ELEMENT_FORMAT,
            DublinCore.ELEMENT_IDENTIFIER,
            DublinCore.ELEMENT_SOURCE,
            DublinCore.ELEMENT_LANGUAGE,
            DublinCore.ELEMENT_RELATION,
            DublinCore.ELEMENT_COVERAGE,
            DublinCore.ELEMENT_RIGHTS };

    // Dublin Core Terms

    private static final String DCTERMS_NAMESPACE = "http://purl.org/dc/terms/";
    private static final String DCTERMS_PREFIX = "dcterms";

    public static final String[] TERMS =
        {
            DublinCore.TERM_AUDIENCE,
            DublinCore.TERM_ALTERNATIVE,
            DublinCore.TERM_TABLEOFCONTENTS,
            DublinCore.TERM_ABSTRACT,
            DublinCore.TERM_CREATED,
            DublinCore.TERM_VALID,
            DublinCore.TERM_EXTENT,
            DublinCore.TERM_AVAILABLE,
            DublinCore.TERM_ISSUED,
            DublinCore.TERM_MODIFIED,
            DublinCore.TERM_EXTENT,
            DublinCore.TERM_MEDIUM,
            DublinCore.TERM_ISVERSIONOF,
            DublinCore.TERM_HASVERSION,
            DublinCore.TERM_ISREPLACEDBY,
            DublinCore.TERM_REPLACES,
            DublinCore.TERM_ISREQUIREDBY,
            DublinCore.TERM_REQUIRES,
            DublinCore.TERM_ISPARTOF,
            DublinCore.TERM_HASPART,
            DublinCore.TERM_ISREFERENCEDBY,
            DublinCore.TERM_REFERENCES,
            DublinCore.TERM_ISFORMATOF,
            DublinCore.TERM_HASFORMAT,
            DublinCore.TERM_CONFORMSTO,
            DublinCore.TERM_SPATIAL,
            DublinCore.TERM_TEMPORAL,
            DublinCore.TERM_MEDIATOR,
            DublinCore.TERM_DATEACCEPTED,
            DublinCore.TERM_DATECOPYRIGHTED,
            DublinCore.TERM_DATESUBMITTED,
            DublinCore.TERM_EDUCATIONLEVEL,
            DublinCore.TERM_ACCESSRIGHTS,
            DublinCore.TERM_BIBLIOGRAPHICCITATION };

    /** 
     * Creates a new instance of Dublin Core
     * 
     * @param aDocument the document for which the Dublin Core instance is created.
     * 
     * @throws DocumentException if an error occurs
     */
    protected DublinCoreImpl(Document aDocument) throws DocumentException {
        this.cmsdocument = aDocument;
        infofile =
            cmsdocument.getPublication().getPathMapper().getFile(
                cmsdocument.getPublication(),
                cmsdocument.getArea(),
                cmsdocument.getId(),
                cmsdocument.getLanguage());
        loadValues();
    }

    /**
     * Loads the dublin core values from the XML file.
     * @throws DocumentException when something went wrong.
     */
    protected void loadValues() throws DocumentException {

        if (infofile.exists()) {
            org.w3c.dom.Document doc = null;
            try {
                doc = DocumentHelper.readDocument(infofile);
            } catch (ParserConfigurationException e) {
                throw new DocumentException(e);
            } catch (SAXException e) {
                throw new DocumentException(e);
            } catch (IOException e) {
                throw new DocumentException(e);
            }

            // FIXME: what if "lenya:meta" element doesn't exist yet?
            // Currently the element is inserted.
            Element metaElement = getMetaElement(doc);

            String[] namespaces = { DC_NAMESPACE, DCTERMS_NAMESPACE };
            String[] prefixes = { DC_PREFIX, DCTERMS_PREFIX };
            String[][] arrays = { ELEMENTS, TERMS };
            Map[] maps = { elements, terms };

            for (int type = 0; type < 2; type++) {
                NamespaceHelper helper = new NamespaceHelper(namespaces[type], prefixes[type], doc);
                String[] elementNames = arrays[type];
                for (int i = 0; i < elementNames.length; i++) {
                    Element[] children = helper.getChildren(metaElement, elementNames[i]);
                    String[] values = new String[children.length];
                    for (int valueIndex = 0; valueIndex < children.length; valueIndex++) {
                        values[valueIndex] =
                            DocumentHelper.getSimpleElementText(children[valueIndex]);
                    }
                    maps[type].put(elementNames[i], values);
                }
            }
        }

    }

    /**
     * Save the meta data.
     *
     * @throws DocumentException if the meta data could not be made persistent.
     */
    public void save() throws DocumentException {
        org.w3c.dom.Document doc = null;
        try {
            doc = DocumentHelper.readDocument(infofile);
        } catch (ParserConfigurationException e) {
            throw new DocumentException(e);
        } catch (SAXException e) {
            throw new DocumentException(e);
        } catch (IOException e) {
            throw new DocumentException(e);
        }

        Element metaElement = getMetaElement(doc);

        String[] namespaces = { DC_NAMESPACE, DCTERMS_NAMESPACE };
        String[] prefixes = { DC_PREFIX, DCTERMS_PREFIX };
        String[][] arrays = { ELEMENTS, TERMS };
        Map[] maps = { elements, terms };

        for (int type = 0; type < 2; type++) {
            NamespaceHelper helper = new NamespaceHelper(namespaces[type], prefixes[type], doc);
            String[] elementNames = arrays[type];
            for (int i = 0; i < elementNames.length; i++) {
                Element[] children = helper.getChildren(metaElement, elementNames[i]);
                for (int valueIndex = 0; valueIndex < children.length; valueIndex++) {
                    metaElement.removeChild(children[valueIndex]);
                }
                String[] values = (String[]) maps[type].get(elementNames[i]);
                for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
                    Element valueElement =
                        helper.createElement(elementNames[i], values[valueIndex]);
                    metaElement.appendChild(valueElement);
                }
            }
        }

        try {
            DocumentHelper.writeDocument(doc, infofile);
        } catch (TransformerConfigurationException e) {
            throw new DocumentException(e);
        } catch (TransformerException e) {
            throw new DocumentException(e);
        } catch (IOException e) {
            throw new DocumentException(e);
        }

    }

    /**
     * Returns the Lenya meta data element.
     * @param doc The XML document.
     * @return A DOM element.
     */
    protected Element getMetaElement(org.w3c.dom.Document doc) throws DocumentException {
        NamespaceHelper namespaceHelper =
            new NamespaceHelper(PageEnvelope.NAMESPACE, PageEnvelope.DEFAULT_PREFIX, doc);
        Element documentElement = doc.getDocumentElement();
        Element metaElement = namespaceHelper.getFirstChild(documentElement, META);

        if (metaElement == null) {
            metaElement = namespaceHelper.createElement(META);
            Element[] children = namespaceHelper.getChildren(documentElement);
            if (children.length == 0) {
                documentElement.appendChild(metaElement);
            } else {
                documentElement.insertBefore(metaElement, children[0]);
            }
        }

        return metaElement;
    }

    /**
     * Returns the first value for a certain key.
     * @param key The key.
     * @return A string.
     */
    public String getFirstValue(String key) throws DocumentException {
        String value = null;
        String[] values = getElementOrTerm(key);
        if (values.length > 0) {
            value = values[0];
        }
        return value;
    }

    /**
     * Returns the element or term values, resp.,  for a certain key.
     * @param key The key.
     * @return An array of strings.
     */
    protected String[] getElementOrTerm(String key) throws DocumentException {
        String[] values;

        List elementList = Arrays.asList(ELEMENTS);
        List termList = Arrays.asList(TERMS);
        if (elementList.contains(key)) {
            values = (String[]) elements.get(key);
        } else if (termList.contains(key)) {
            values = (String[]) terms.get(key);
        } else {
            throw new DocumentException(
                "The key [" + key + "] does not refer to a dublin core element or term!");
        }
        if (values == null) {
            values = new String[0];
        }
        return values;
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#getValues(java.lang.String)
     */
    public String[] getValues(String key) throws DocumentException {
        return getElementOrTerm(key);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#addValue(java.lang.String, java.lang.String)
     */
    public void addValue(String key, String value) throws DocumentException {
        String[] existingValues = getElementOrTerm(key);
        List list = new ArrayList(Arrays.asList(existingValues));
        list.add(value);
        String[] newValues = (String[]) list.toArray(new String[list.size()]);

        List elementList = Arrays.asList(ELEMENTS);
        List termList = Arrays.asList(TERMS);
        if (elementList.contains(key)) {
            elements.put(key, newValues);
        } else if (termList.contains(key)) {
            terms.put(key, newValues);
        } else {
            throw new DocumentException(
                "The key [" + key + "] does not refer to a dublin core element or term!");
        }
    }
	
    /**
     * @see org.apache.lenya.cms.publication.DublinCore#setValue(java.lang.String, java.lang.String)
     */
    public void setValue(String key, String value) throws DocumentException {
        String[] newValues = { value };
        
        List elementList = Arrays.asList(ELEMENTS);
        List termList = Arrays.asList(TERMS);
        if (elementList.contains(key)) {
            elements.put(key, newValues);
        } else if (termList.contains(key)) {
            terms.put(key, newValues);
        } else {
            throw new DocumentException(
                "The key [" + key + "] does not refer to a dublin core element or term!");
        }
    }
    
	/**
	 * @see org.apache.lenya.cms.publication.DublinCore#addValues(java.lang.String, java.lang.String[])
	 */
	public void addValues(String key, String[] values) throws DocumentException {
		for (int i = 0; i < values.length; i++) {
            addValue(key,values[i]);
        }
	}
	
    /**
     * @see org.apache.lenya.cms.publication.DublinCore#removeValue(java.lang.String, java.lang.String)
     */
    public void removeValue(String key, String value) throws DocumentException {
        String[] existingValues = getElementOrTerm(key);
        List list = new ArrayList(Arrays.asList(existingValues));

        if (!list.contains(value)) {
            throw new DocumentException(
                "The key [" + key + "] does not contain the value [" + value + "]!");
        }

        list.remove(value);
        String[] newValues = (String[]) list.toArray(new String[list.size()]);

        List elementList = Arrays.asList(ELEMENTS);
        List termList = Arrays.asList(TERMS);
        if (elementList.contains(key)) {
            elements.put(key, newValues);
        } else if (termList.contains(key)) {
            terms.put(key, newValues);
        } else {
            throw new DocumentException(
                "The key [" + key + "] does not refer to a dublin core element or term!");
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#removeAllValues(java.lang.String)
     */
    public void removeAllValues(String key) throws DocumentException {
        List elementList = Arrays.asList(ELEMENTS);
        List termList = Arrays.asList(TERMS);
        if (elementList.contains(key)) {
            elements.put(key, new String[0]);
        } else if (termList.contains(key)) {
            terms.put(key, new String[0]);
        } else {
            throw new DocumentException(
                "The key [" + key + "] does not refer to a dublin core element or term!");
        }
    }
    
	/**
	 * @see org.apache.lenya.cms.publication.DublinCore#replaceBy(org.apache.lenya.cms.publication.DublinCore)
	 */
	public void replaceBy(DublinCore other) throws DocumentException {
		for (int i = 0; i < ELEMENTS.length; i++) {
            String key = ELEMENTS[i];
            removeAllValues(key);
            addValues(key, other.getValues(key));
        }
        for (int i = 0; i < TERMS.length; i++) {
            String key = TERMS[i];
            removeAllValues(key);
            addValues(key, other.getValues(key));
        }
	}

}
