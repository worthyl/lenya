/*
$Id: AssetUploadAction.java,v 1.1 2003/08/22 19:04:33 egli Exp $
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
package org.apache.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.servlet.multipart.Part;

import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.ResourcesManager;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The class <code>AssetUploadAction</code> implements an action that allows for
 * asset upload. An asset upload consists of a file upload plus a file creation
 * for the meta data of the asset.
 *
 * @author <a href="mailto:egli@apache.org">Christian Egli</a>
 */
public class AssetUploadAction extends AbstractConfigurableAction {

    public static final String UPLOADASSET_PARAM_NAME = "properties.asset.file";
    public static final String UPLOADASSET_PARAM_PREFIX = "properties.asset.";

    public static final String UPLOADASSET_RETURN_FILESIZE = "mime-type";
    public static final String UPLOADASSET_RETURN_MIMETYPE = "file-size";
    
    // optional parameters for meta data according to dublin core
    public static final String[] DUBLIN_CORE_PARAMETERS =
        {
            "title",
            "creator",
            "subject",
            "description",
            "publisher",
            "contributor",
            "date",
            "type",
            "format",
            "identifier",
            "source",
            "language",
            "relation",
            "coverage",
            "rights" };

    /**
     * Retrieve the file from the request and store it in the
     * corresponding resources directory, create a meta file and
     * insert an image tag in the requesting document.
     *
     * @param redirector a <code>Redirector</code> value
     * @param resolver a <code>SourceResolver</code> value
     * @param objectModel a <code>Map</code> value
     * @param source a <code>String</code> value
     * @param parameters a <code>Parameters</code> value
     *
     * @return a <code>Map</code> containing the referer or null if
     * the upload failed.
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

        HashMap results = new HashMap();
        
        Request request = ObjectModelHelper.getRequest(objectModel);

        PageEnvelope pageEnvelope =
            PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);

        byte[] buf = new byte[4096];

        for (Enumeration enum = request.getParameterNames(); enum.hasMoreElements();) {
            String param = (String)enum.nextElement();
            getLogger().debug(param + ": " + request.getParameter(param) + " [" + request.get(param) + "]");
        }

        // optional parameters for the meta file which contains dublin
        // core information.
        HashMap dublinCoreParams = new HashMap();

        for (int i = 0; i < DUBLIN_CORE_PARAMETERS.length; i++) {
            String paramName = DUBLIN_CORE_PARAMETERS[i];
            String paramValue = request.getParameter(UPLOADASSET_PARAM_PREFIX + paramName);

            if (paramValue == null) {
                paramValue = "";
            }

            dublinCoreParams.put(paramName, paramValue);
        }

        Iterator iter = dublinCoreParams.keySet().iterator();

        while (iter.hasNext()) {
            String paramName = (String)iter.next();
            getLogger().debug(
                paramName + ": " + dublinCoreParams.get(paramName));
        }

        // upload the file to the uploadDir
        Part part = (Part)request.get(UPLOADASSET_PARAM_NAME);

        String mimeType = part.getMimeType();
        int fileSize = part.getSize();

        results.put(UPLOADASSET_RETURN_MIMETYPE, mimeType);
        results.put(UPLOADASSET_RETURN_FILESIZE, new Integer(fileSize));
        
        dublinCoreParams.put("format", mimeType);
        dublinCoreParams.put("extent", Integer.toString(fileSize));

        // FIXME: write fileSize into dc meta data
        
        ResourcesManager resourcesMgr =
            new ResourcesManager(pageEnvelope.getDocument());
        File assetFile = new File(resourcesMgr.getPath(), part.getFileName());

        if (!resourcesMgr.getPath().exists()) {
            resourcesMgr.getPath().mkdirs();
        }

        assetFile.createNewFile();

        FileOutputStream out = new FileOutputStream(assetFile);
        InputStream in = part.getInputStream();
        int read = in.read(buf);

        while (read > 0) {
            out.write(buf, 0, read);
            read = in.read(buf);
        }

        // create an extra file containing the meta description for
        // the image.
        File metaDataFile =
            new File(resourcesMgr.getPath(), part.getFileName() + ".meta");
        createMetaData(metaDataFile, dublinCoreParams);

        return Collections.unmodifiableMap(results);
    }

    /**
     * Create the meta data file given the dublin core parameters.
     *
     * @param metaDataFile the file where the meta data file is to be created
     * @param dublinCoreParams a <code>Map</code> containing the dublin core values
     *
     * @exception IOException if an error occurs
     */
    protected void createMetaData(
        File metaDataFile,
        HashMap dublinCoreParams)
        throws IOException {

        assert(metaDataFile.getParentFile().exists());
        
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("dc:metadata");

        Iterator iter = dublinCoreParams.keySet().iterator();

        while (iter.hasNext()) {
            String tagName = (String)iter.next();
            String tagValue = (String)dublinCoreParams.get(tagName);
            root.addElement(tagName).addText(tagValue);
        }

        OutputStream out =
            new BufferedOutputStream(
                new FileOutputStream(metaDataFile));

        XMLWriter writer = new XMLWriter(out, OutputFormat.createPrettyPrint());
        writer.write(document);
        writer.close();
    }
}
