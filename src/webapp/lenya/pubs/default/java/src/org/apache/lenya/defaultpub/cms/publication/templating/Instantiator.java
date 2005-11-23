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
package org.apache.lenya.defaultpub.cms.publication.templating;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.lang.Boolean;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.impl.FileSource;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.cocoon.components.search.Index;
import org.apache.cocoon.components.search.IndexException;
import org.apache.cocoon.components.search.IndexStructure;
import org.apache.cocoon.components.search.components.AnalyzerManager;
import org.apache.cocoon.components.search.components.IndexManager;
import org.apache.cocoon.components.search.fieldmodel.DateFieldDefinition;
import org.apache.cocoon.components.search.fieldmodel.FieldDefinition;
import org.apache.cocoon.components.search.utils.SourceHelper;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;


/**
 * Instantiate the publication.
 * 
 * @version $Id$
 */
public class Instantiator extends AbstractLogEnabled implements
        org.apache.lenya.cms.publication.templating.Instantiator, Serviceable {

    protected static final String[] sourcesToCopy = { "publication.xml",
            "config/publication.xconf", "config/index_manager_index.xconf", "config/index_manager.xconf", "config/ac/passwd/", "config/ac/ac.xconf",
            "config/ac/policies/", "config/ac/usecase-policies.xml",
            "config/workflow/workflow.xml", "content/" };

    /**
     * @see org.apache.lenya.cms.publication.templating.Instantiator#instantiate(org.apache.lenya.cms.publication.Publication,
     *      java.lang.String, java.lang.String)
     */
    public void instantiate(Publication template, String newPublicationId, String name)
            throws Exception {

        SourceResolver resolver = null;
        Source publicationsSource = null;
        ModifiableSource metaSource = null;
        ModifiableSource configSource = null;
        ModifiableSource indexSource = null;
        ModifiableSource indexerSource = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            publicationsSource = resolver.resolveURI("context://"
                    + Publication.PUBLICATION_PREFIX_URI);
            String publicationsUri = publicationsSource.getURI();

            for (int i = 0; i < sourcesToCopy.length; i++) {

                String source = sourcesToCopy[i];
                if (source.endsWith("/")) {
                    copyDirSource(template, newPublicationId, resolver, publicationsUri, source);
                } else {
                    copySource(template, newPublicationId, resolver, publicationsUri, source);
                }
            }

            metaSource = (ModifiableSource) resolver.resolveURI(publicationsUri + "/"
                    + newPublicationId + "/publication.xml");
            Document metaDoc = DocumentHelper.readDocument(metaSource.getInputStream());
            NamespaceHelper helper = new NamespaceHelper("http://apache.org/cocoon/lenya/publication/1.0",
                    "lenya",
                    metaDoc);
            Element nameElement = helper.getFirstChild(metaDoc.getDocumentElement(), "name");
            DocumentHelper.setSimpleElementText(nameElement, name);

            save(metaDoc, metaSource);

            // RGE: Soc addition
            // First, patch the xconf patchfile with the new publication name

            String indexDir = publicationsUri + newPublicationId + "/work/lucene/index";
            indexDir = indexDir.substring(5);
		    
            indexSource = (ModifiableSource) resolver.resolveURI(publicationsUri + "/" + newPublicationId + "/config/index_manager_index.xconf");
            Document indexDoc = DocumentHelper.readDocument(indexSource.getInputStream());
            NamespaceHelper indexHelper = new NamespaceHelper(null,"xconf",indexDoc);
	    
            indexerSource = (ModifiableSource) resolver.resolveURI(publicationsUri + "/" + newPublicationId + "/config/index_manager.xconf");
            Document indexerDoc = DocumentHelper.readDocument(indexerSource.getInputStream());
            NamespaceHelper indexerHelper = new NamespaceHelper(null,"xconf",indexerDoc);

            Element indexManagerElement = indexerHelper.getFirstChild(indexerDoc.getDocumentElement(),"index_manager");
            Element indexerElement = indexerHelper.getFirstChild(indexManagerElement,"indexer");

            Element xconfIndexElement = indexDoc.getDocumentElement();
            xconfIndexElement.setAttribute("unless","/cocoon/index_manager/indexes/index[@id = '"+newPublicationId+"-live' or @id = '"+newPublicationId+"-authoring']");
 	    
            Element[] indexElement = indexHelper.getChildren(indexDoc.getDocumentElement(), "index");
	    
            indexElement[0].setAttribute("id",newPublicationId+"-live");
            indexElement[0].setAttribute("directory", indexDir+"/live/index");
            indexElement[1].setAttribute("id",newPublicationId+"-authoring");
            indexElement[1].setAttribute("directory", indexDir+"/authoring/index");

            save(indexDoc, indexSource);
            
            // Second, configure the index and add it to the IndexManager

            IndexManager indexM = (IndexManager) manager.lookup(IndexManager.ROLE);
	    
            Element structure = indexHelper.getFirstChild(indexElement[0], "structure");
            Element[] fields = indexHelper.getChildren(structure, "field");
	    
            IndexStructure docdecl = new IndexStructure();

            for (int j = 0; j < fields.length; j++) {

                FieldDefinition fielddecl = null;

                // field id attribute
                String id_field = fields[j].getAttribute("id");

                // field type attribute
                String typeS = fields[j].getAttribute("type");
                int type = FieldDefinition.stringTotype(typeS);
                try {
                    fielddecl = FieldDefinition.create(id_field, type);
                } catch (IllegalArgumentException e) {
                    throw new ConfigurationException("field " + id_field + " type " + typeS, e);
                }

                // field store attribute
                boolean store;
                Boolean BoolStore = new Boolean(fields[j].getAttribute("storetext"));
                store = BoolStore.booleanValue();
		    
                fielddecl.setStore(store);

                // field dateformat attribute
                if (fielddecl.getType() == FieldDefinition.DATE) {
                    String dateformat_field = fields[j].getAttribute("dateformat");
                    ((DateFieldDefinition) fielddecl).setDateFormat(new SimpleDateFormat(dateformat_field));
                }

                docdecl.addFieldDef(fielddecl);
            }
	    
            Index indexLive = new Index();
            Index indexAuthoring = new Index();
            indexLive.setID(newPublicationId+"-live");
            indexAuthoring.setID(newPublicationId+"-authoring");
            indexLive.setIndexer(indexerElement.getAttribute("role"));
            indexAuthoring.setIndexer(indexerElement.getAttribute("role"));
            indexLive.setDirectory(indexDir+"/live/index");
            indexAuthoring.setDirectory(indexDir+"/authoring/index");
            indexLive.setDefaultAnalyzerID(indexElement[0].getAttribute("analyzer"));
            indexAuthoring.setDefaultAnalyzerID(indexElement[1].getAttribute("analyzer"));
            indexLive.setManager(manager);
            indexAuthoring.setManager(manager);
            indexLive.setStructure(docdecl);
            indexAuthoring.setStructure(docdecl);

            indexM.addIndex(indexLive);
            indexM.addIndex(indexAuthoring);
            manager.release(indexM);
	    // TODO: release all objects!

	    // RGE: End Soc addition

            configSource = (ModifiableSource) resolver.resolveURI(publicationsUri + "/"
                    + newPublicationId + "/config/publication.xconf");
            DefaultConfiguration config = (DefaultConfiguration) new DefaultConfigurationBuilder().build(configSource.getInputStream());
            DefaultConfiguration templatesConfig = (DefaultConfiguration) config.getChild("templates",
                    false);
            if (templatesConfig == null) {
                templatesConfig = new DefaultConfiguration("templates");
                config.addChild(templatesConfig);
            }
            DefaultConfiguration templateConfig = new DefaultConfiguration("template");
            templateConfig.setAttribute("id", template.getId());
            templatesConfig.addChild(templateConfig);
            OutputStream oStream = configSource.getOutputStream();
            new DefaultConfigurationSerializer().serialize(oStream, config);
            if (oStream != null) {
                oStream.flush();
                try {
                    oStream.close();
                } catch (Throwable t) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Exception closing output stream: ", t);
                    }
                    throw new RuntimeException("Could not write document: ", t);
                }
            }

        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
                if (publicationsSource != null) {
                    resolver.release(publicationsSource);
                }
                if (metaSource != null) {
                    resolver.release(metaSource);
                }
                if (configSource != null) {
                    resolver.release(configSource);
                }
                if (indexSource != null) {
                    resolver.release(indexSource);
                }

            }
        }
    }

    protected void copySource(Publication template, String publicationId, SourceResolver resolver,
            String publicationsUri, String source) throws MalformedURLException, IOException {
        Source templateSource = null;
        ModifiableSource targetSource = null;
        try {
            templateSource = resolver.resolveURI(publicationsUri + "/" + template.getId() + "/"
                    + source);
            targetSource = (ModifiableSource) resolver.resolveURI(publicationsUri + "/"
                    + publicationId + "/" + source);

            org.apache.lenya.cms.cocoon.source.SourceUtil.copy(templateSource, targetSource, false);
        } finally {
            if (templateSource != null) {
                resolver.release(templateSource);
            }
            if (targetSource != null) {
                resolver.release(targetSource);
            }
        }
    }

    protected void copyDirSource(Publication template, String publicationId,
            SourceResolver resolver, String publicationsUri, String source)
            throws MalformedURLException, IOException {
        FileSource directory = new FileSource(publicationsUri + "/" + template.getId() + "/"
                + source);
        Collection files = directory.getChildren();
        for (Iterator iter = files.iterator(); iter.hasNext();) {
            FileSource filesource = (FileSource) iter.next();
            if (filesource.isCollection()) {
                copyDirSource(template, publicationId, resolver, publicationsUri, source + "/"
                        + filesource.getName());
            } else {
                copySource(template, publicationId, resolver, publicationsUri, source + "/"
                        + filesource.getName());
            }
        }
    }

    protected void save(Document metaDoc, ModifiableSource metaSource) throws IOException,
            TransformerConfigurationException, TransformerException {
        OutputStream oStream = metaSource.getOutputStream();
        DocumentHelper.writeDocument(metaDoc, new OutputStreamWriter(oStream));
        if (oStream != null) {
            oStream.flush();
            try {
                oStream.close();
            } catch (Throwable t) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Exception closing output stream: ", t);
                }
                throw new RuntimeException("Could not write document: ", t);
            }
        }
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}