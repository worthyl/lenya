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

package org.apache.lenya.cms.cocoon.acting;

import java.io.File;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.rc.RCEnvironment;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.lenya.util.ServletHelper;
import org.apache.log4j.Logger;

public class RevisionControllerAction extends AbstractAction implements Serviceable {
    Logger log = Logger.getLogger(RevisionControllerAction.class);

    private String rcmlDirectory = null;
    private String backupDirectory = null;
    private RevisionController rc = null;
    private String username = null;
    private String filename = null;
    private Document document = null;
    private ServiceManager manager;

    /**
     * DOCUMENT ME!
     *
     * @param redirector DOCUMENT ME!
     * @param resolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param src DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String src,
        Parameters parameters)
        throws Exception {
        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error(".act(): No request object");

            return null;
        }

        PageEnvelope envelope = null;
        Publication publication = null;

        try {
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
            publication = envelope.getPublication();
            document = envelope.getDocument();
        } catch (Exception e) {
            getLogger().error("Resolving page envelope failed: ", e);
            throw e;
        }

        //get Parameters for RC
        String publicationPath = publication.getDirectory().getAbsolutePath();
        RCEnvironment rcEnvironment =
            RCEnvironment.getInstance(publication.getServletContext().getAbsolutePath());
        rcmlDirectory = rcEnvironment.getRCMLDirectory();
        rcmlDirectory = publicationPath + File.separator + rcmlDirectory;
        backupDirectory = rcEnvironment.getBackupDirectory();
        backupDirectory = publicationPath + File.separator + backupDirectory;

        // Initialize Revision Controller
        rc = new RevisionController(rcmlDirectory, backupDirectory, publicationPath);
        getLogger().debug("revision controller" + rc);

        // /Initialize Revision Controller
        // Get session
        Session session = request.getSession(false);

        if (session == null) {
            getLogger().error(".act(): No session object");

            return null;
        }

        Identity identity = (Identity) session.getAttribute(Identity.class.getName());
        getLogger().debug(".act(): Identity: " + identity);

        //FIXME: hack because of the uri for the editor bitflux. The filename cannot be get from the page-envelope 

        String documentid = document.getId();
        int bx = documentid.lastIndexOf("-bxeng");

        if (bx > 0) {
            String language = document.getLanguage();

            int l = documentid.length();
            int bxLength = "-bxeng".length();
            int lang = documentid.lastIndexOf("_", bx);
            int langLength = bx - lang;

            if (bx > 0 && bx + bxLength <= l) {
                documentid = documentid.substring(0, bx) + documentid.substring(bx + bxLength, l);

                if (lang > 0 && langLength + lang < l) {
                    language = documentid.substring(lang + 1, lang + langLength);
                    documentid =
                        documentid.substring(0, lang)
                            + documentid.substring(lang + langLength, l - bxLength);
                }
            }

            DocumentBuilder builder = publication.getDocumentBuilder();

            String srcUrl =
                builder.buildCanonicalUrl(publication, document.getArea(), documentid, language);
            Document srcDoc = builder.buildDocument(publication, srcUrl);
            File newFile = srcDoc.getFile();
            filename = newFile.getAbsolutePath();

        } else {
            filename = document.getFile().getAbsolutePath();
        }

        filename = filename.substring(publicationPath.length());
        log.debug("Filename: " + filename);

        username = null;

        if (identity != null) {
            User user = identity.getUser();
            if (user != null) {
                username = user.getId();
            }
        } else {
            getLogger().error(".act(): No identity yet");
        }

        getLogger().debug(".act(): Username: " + username);

        return null;
    }

    /**
     * Get the Document
     * 
     * @return the document
     */
    protected Document getDocument() {
        return document;
    }

    /**
	 * @deprecated getDocument()
     * Get the filename.
     * 
     * @return the filename
     */
    protected String getFilename() {
        return filename;
    }

    /**
     * Get the revision controller.
     * 
     * @return the revision controller
     */
    protected RevisionController getRc() {
        return rc;
    }

    /**
     * Get the user name.
     * 
     * @return the user name
     */
    protected String getUsername() {
        return username;
    }

    protected User getUser(Map objectModel, String userId) throws Exception {
        ServiceSelector selector = null;
        AccessControllerResolver resolver = null;
        DefaultAccessController accessController = null;

        Request request = ObjectModelHelper.getRequest(objectModel);

        Map result = null;

        try {
            selector =
                (ServiceSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");
                
            getLogger().debug("Resolving AC resolver for type [" + AccessControllerResolver.DEFAULT_RESOLVER + "]");
            resolver =
                (AccessControllerResolver) selector.select(
                    AccessControllerResolver.DEFAULT_RESOLVER);
            getLogger().debug("Resolved AC resolver [" + resolver + "]");

            String webappUrl = ServletHelper.getWebappURI(request);
            accessController = (DefaultAccessController) resolver.resolveAccessController(webappUrl);
            UserManager userMgr = accessController.getAccreditableManager().getUserManager();
            User user = userMgr.getUser(userId);
            if (user == null) {
                getLogger().warn("User [" + userId + "] does not exist.");
            }
            return user;

        } finally {
            if (selector != null) {
                if (resolver != null) {
                    selector.release(resolver);
                }
                manager.release(selector);
            }
        }
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }
}
