/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.site.usecases;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.authoring.ParentChildCreatorInterface;
import org.apache.lenya.cms.metadata.LenyaMetaData;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.transaction.Transactionable;

/**
 * Abstract superclass for usecases to create a document.
 * 
 * @version $Id$
 */
public abstract class Create extends AbstractUsecase {

    protected static final String LANGUAGE = "language";
    protected static final String LANGUAGES = "languages";
    protected static final String DOCUMENT_ID = "documentId";

    /**
     * Ctor.
     */
    public Create() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();

        if (!getArea().equals(Publication.AUTHORING_AREA)) {
            addErrorMessage("This usecase can only be invoked in the authoring area!");
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getObjectsToLock()
     */
    protected Transactionable[] getObjectsToLock() throws UsecaseException {
        try {
            SiteStructure structure = SiteUtil.getSiteStructure(this.manager, getSourceDocument());
            Transactionable[] nodes = { structure.getRepositoryNode() };
            return nodes;
        } catch (SiteException e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        String navigationTitle = getParameterAsString(DublinCore.ELEMENT_TITLE);

        if (navigationTitle.equals("")) {
            addErrorMessage("The navigation title is required.");
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        Document document = createDocument();

        if (getLogger().isDebugEnabled())
            getLogger().debug("Create.doExecute() got document instance, now notifying document manager");

        DocumentManager documentManager = null;
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            documentManager.add(document);

            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(document.getPublication()
                    .getSiteManagerHint());
            siteManager.setLabel(document, getParameterAsString(DublinCore.ELEMENT_TITLE));
        } finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }

        if (getLogger().isDebugEnabled())
            getLogger().debug("Create.doExecute() done notifying document manager, now setting meta data");

        setMetaData(document);

        setTargetURL(document.getCanonicalWebappURL());
    }

    /**
     * Creates a document.
     * @return A document.
     * @throws Exception if an error occurs.
     */
    protected Document createDocument() throws Exception {
        if (getLogger().isDebugEnabled())
            getLogger().debug("createDocument() called");

        Document usecaseDocument = getSourceDocument();
        String newDocumentId = getNewDocumentId();
        String navigationTitle = getParameterAsString(DublinCore.ELEMENT_TITLE);
        String documentTypeName = getDocumentTypeName();
        String language = getParameterAsString(LANGUAGE);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("createDocument() read parameters:");
            getLogger().debug("    UsecaseDocument document:   [" + usecaseDocument.getId() + "]");
            getLogger().debug("    Child document:    [" + newDocumentId + "]");
            getLogger().debug("    Language:          [" + language + "]");
            getLogger().debug("    Document Type:     [" + documentTypeName + "]");
            getLogger().debug("    Navigation Title:  [" + navigationTitle + "]");
        }

        Publication publication = usecaseDocument.getPublication();
        DocumentIdentityMap map = usecaseDocument.getIdentityMap();
        String area = usecaseDocument.getArea();

        /*
         * Get an instance of Document.
         * This will (ultimately) be created by the implementation for
         * the DocumentBuilder role.
         */
        if (getLogger().isDebugEnabled())
            getLogger().debug("createDocument() creating Document instance");
        Document document = map.get(publication, area, newDocumentId, language);
        Transactionable[] nodes = document.getRepositoryNodes();
        for (int i = 0; i < nodes.length; i++) {
            nodes[i].lock();
        }

        if (getLogger().isDebugEnabled())
            getLogger().debug("createDocument() looking up a DocumentTypeBuilder so that we can call the creator");

        /*
         * Create an instance of DocumentType, and then
         * use the creator for this DocumentType to actually
         * physically create a document of this type.
         */
        DocumentTypeBuilder documentTypeBuilder = null;
        DocumentType documentType = null;
        try {
            documentTypeBuilder = (DocumentTypeBuilder) this.manager.lookup(DocumentTypeBuilder.ROLE);

            documentType = documentTypeBuilder.buildDocumentType(documentTypeName, publication);

            String parentId = "";
            Document parentDocument = getParentDocument(document);
            if (parentDocument != null)
                parentId = parentDocument.getId().substring(1);

            String childId = document.getName();
            ParentChildCreatorInterface creator = documentType.getCreator();
            creator.create(
                getInitialContentsURI(usecaseDocument, documentType),
                new File(publication.getContentDirectory(area), parentId),
                childId,
                ParentChildCreatorInterface.BRANCH_NODE,
                navigationTitle,
                language,
                Collections.EMPTY_MAP);
        } 
        finally {
            if (documentTypeBuilder != null) {
                this.manager.release(documentTypeBuilder);
            }
        }

        return document;
    }


    /**
     * @return the id of the new document being created in the usecase
     */
    protected abstract String getNewDocumentId();

    /**
     * @param newDocument the new document being created in the usecase
     * @return the new document's parent
     */
    protected abstract Document getParentDocument(Document newDocument) throws DocumentBuildException;

    /**
     * If there is a reference document from which to copy contents, 
     * pass this as parameter. If there is no such document, the document 
     * type will be used instead to read a sample content.
     *
     * @param referenceDocument the document to use as reference for the initial contents
     * @param type the type of resource to be created
     */
    protected abstract String getInitialContentsURI(Document referenceDocument, DocumentType type);

    /**
     * @return The type of the created document.
     */
    protected abstract String getDocumentTypeName();

    /**
     * Sets the meta data of the created document.
     * @param document The document.
     * @throws DocumentException if an error occurs.
     */
    protected void setMetaData(Document document) throws DocumentException {

        Map dcMetaData = new HashMap();
        dcMetaData.put(DublinCore.ELEMENT_TITLE,
                getParameterAsString(DublinCore.ELEMENT_TITLE));
        dcMetaData.put(DublinCore.ELEMENT_CREATOR,
                getParameterAsString(DublinCore.ELEMENT_CREATOR));
        dcMetaData.put(DublinCore.ELEMENT_PUBLISHER,
                getParameterAsString(DublinCore.ELEMENT_PUBLISHER));
        dcMetaData.put(DublinCore.ELEMENT_SUBJECT,
                getParameterAsString(DublinCore.ELEMENT_SUBJECT));
        dcMetaData.put(DublinCore.ELEMENT_DATE, 
                getParameterAsString(DublinCore.ELEMENT_DATE));
        dcMetaData.put(DublinCore.ELEMENT_RIGHTS,
                getParameterAsString(DublinCore.ELEMENT_RIGHTS));
        dcMetaData.put(DublinCore.ELEMENT_LANGUAGE, 
                getParameterAsString(LANGUAGE));

        Map lenyaMetaData = new HashMap(2);
        lenyaMetaData.put(LenyaMetaData.ELEMENT_RESOURCE_TYPE, getDocumentTypeName());
        lenyaMetaData.put(LenyaMetaData.ELEMENT_CONTENT_TYPE, "xml");

        document.getMetaDataManager().setMetaData(dcMetaData, lenyaMetaData, null);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        Map objectModel = ContextHelper.getObjectModel(getContext());
        Request request = ObjectModelHelper.getRequest(objectModel);
        Session session = request.getSession(false);
        Identity identity = (Identity) session.getAttribute(Identity.class.getName());
        User user = identity.getUser();
        if (user != null) {
            setParameter(DublinCore.ELEMENT_CREATOR, user.getId());
        } else {
            setParameter(DublinCore.ELEMENT_CREATOR, "");
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        setParameter(DublinCore.ELEMENT_DATE, format.format(new GregorianCalendar().getTime()));

    }

    /**
     * @return The source document or <code>null</code> if the usecase was not invoked on a
     *         document.
     */
    protected Document getSourceDocument() {
        Document document = null;
        String url = getSourceURL();
        try {
            if (getDocumentIdentityMap().isDocument(url)) {
                document = getDocumentIdentityMap().getFromURL(url);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return document;
    }

    /**
     * @return The area without the "info-" prefix.
     */
    public String getArea() {
        URLInformation info = new URLInformation(getSourceURL());
        return info.getArea();
    }

    private Publication publication;

    /**
     * Access to the current publication. 
     * Use this when the publication is not yet known in the usecase: 
     * e.g. when creating a global asset. When adding a resource or a child
     * to a document, access the publication via that document's interface
     * instead.
     *
     * @return the publication in which the use-case is being executed
     */
    protected Publication getPublication() {
        if (this.publication == null) {
            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            try {
                this.publication = factory.getPublication(this.manager, getSourceURL());
            } catch (PublicationException e) {
                throw new RuntimeException(e);
            }
        }
        return this.publication;
    }
}
