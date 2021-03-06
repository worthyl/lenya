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
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.RelaxNG;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 *
 */
public class OneFormEditorSaveAction
    extends AbstractConfigurableAction
    implements ThreadSafe {
    Logger log = Logger.getLogger(OneFormEditorSaveAction.class);

    /**
     * Save data to temporary file
     *
     * @param redirector a <code>Redirector</code> value
     * @param resolver a <code>SourceResolver</code> value
     * @param objectModel a <code>Map</code> value
     * @param source a <code>String</code> value
     * @param parameters a <code>Parameters</code> value
     *
     * @return a <code>Map</code> value
     *
     * @exception Exception if an error occurs
     */
    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String source,
        Parameters parameters)
        throws Exception {

        HttpRequest request = (HttpRequest)ObjectModelHelper.getRequest(objectModel);

        // Get namespaces
        String namespaces = removeRedundantNamespaces(request.getParameter("namespaces"));
        log.debug(namespaces);

        // Aggregate content
        String encoding = request.getCharacterEncoding();
        String content =
            "<?xml version=\"1.0\" encoding=\""
                + encoding
                + "\"?>\n"
                + addNamespaces(namespaces, request.getParameter("content"));

        // Save file temporarily
        File sitemap = new File(new URL(resolver.resolveURI("").getURI()).getFile());
        File file =
            new File(
                sitemap.getAbsolutePath()
                    + File.separator
                    + parameters.getParameter("file"));

        File parentFile = new File(file.getParent());
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
		FileOutputStream fileoutstream = new FileOutputStream(file);
        Writer writer = new OutputStreamWriter(fileoutstream, encoding);
        writer.write(content, 0, content.length());
        writer.close();

        // Validate
        File schema =
            new File(
                sitemap.getAbsolutePath()
                    + File.separator
                    + parameters.getParameter("schema"));
        if (schema.isFile()) {
            
            try {
                DocumentHelper.readDocument(file);
            }
            catch (SAXException e) {
                log.info("Wellformedness check failed: " + e.getMessage());
                Map hmap = new HashMap();
                hmap.put("message", "Document is not well-formed: " + e.getMessage());
                return hmap;
            }
            
            String message = RelaxNG.validate(schema, file);
            if (message != null) {
                log.info("RELAX NG Validation failed: " + message);
                Map hmap = new HashMap();
                hmap.put("message", "RELAX NG Validation failed: " + message);
                return hmap;
            }
        } else {
            log.warn(
                "Will not be validated. No such schema: " + schema.getAbsolutePath());
        }

        return null;
    }

    /**
     * Remove redundant namespaces
     */
    private String removeRedundantNamespaces(String namespaces) {
        String[] namespace = namespaces.split(" ");

        String ns = "";
        for (int i = 0; i < namespace.length; i++) {
            if (ns.indexOf(namespace[i]) < 0) {
                ns = ns + " " + namespace[i];
            } else {
                log.debug("Redundant namespace: " + namespace[i]);
            }
        }
        return ns;
    }

    /**
     * Add namespaces
     */
    private String addNamespaces(String namespaces, String content) {
        int i = content.indexOf(">");
        return content.substring(0, i) + " " + namespaces + content.substring(i);
    }
}
