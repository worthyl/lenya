/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

/* $Id$ */

package org.apache.lenya.cms.cocoon.generation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.Cookie;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.cocoon.generation.ServletGenerator;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.excalibur.xml.sax.SAXParser;
import org.apache.lenya.cms.cocoon.generation.Configuration;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ProxyGenerator extends ServletGenerator implements Parameterizable {
    private static Logger log = Logger.getLogger(ProxyGenerator.class);

    private static String trustStore = null;
    private static String trustStorePassword = null;    

    public ProxyGenerator() {
        Configuration conf = new Configuration();
        trustStore = conf.trustStore;
        trustStorePassword = conf.trustStorePassword;
        log.debug("loaded ProxyGenerator Config: " + "trustStore="+trustStore + " trustStorePassword=" + trustStorePassword);
    }

    /**
     * No parameters implemented.
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters parameters) throws ParameterException {
    }

    /**
     * @see org.apache.cocoon.generation.Generator#generate()
     */
    public void generate() throws SAXException {
        Request request = (Request) objectModel.get(ObjectModelHelper.REQUEST_OBJECT);

        log.debug("\n----------------------------------------------------------------"
                + "\n- Request: (" + request.getClass().getName() + ") at port "
                + request.getServerPort()
                + "\n----------------------------------------------------------------");

        String submitMethod = request.getMethod();

        SAXParser parser = null;

        try {
            // DEBUG
            if (submitMethod.equals("POST")) {
                // FIXME: Andreas
                if (request instanceof HttpRequest) {
                    InputStream is = intercept(((HttpRequest) request).getInputStream());
                }
            }

            URL url = createURL(request);

            // Forward "InputStream", Parameters, QueryString to Servlet
            HttpMethod httpMethod = null;

            if (submitMethod.equals("POST")) {
                httpMethod = new PostMethod();

                Enumeration params = request.getParameterNames();

                while (params.hasMoreElements()) {
                    String paramName = (String) params.nextElement();
                    String[] paramValues = request.getParameterValues(paramName);

                    for (int i = 0; i < paramValues.length; i++) {
                        ((PostMethod) httpMethod).setParameter(paramName, paramValues[i]);
                    }
                }
            } else if (submitMethod.equals("GET")) {
                httpMethod = new GetMethod();
                httpMethod.setQueryString(request.getQueryString());
            }

            // Copy/clone Cookies
            Cookie[] cookies = request.getCookies();
            org.apache.commons.httpclient.Cookie[] transferedCookies = null;

            if (cookies != null) {
                transferedCookies = new org.apache.commons.httpclient.Cookie[cookies.length];

                for (int i = 0; i < cookies.length; i++) {
                    boolean secure = false; // http: false, https: true
                    transferedCookies[i] = new org.apache.commons.httpclient.Cookie(url.getHost(),
                            cookies[i].getName(), cookies[i].getValue(), url.getFile(), null,
                            secure);
                }
            }

            // Initialize HttpClient
            HttpClient httpClient = new HttpClient();

            // Set cookies
            if ((transferedCookies != null) && (transferedCookies.length > 0)) {
                HttpState httpState = new HttpState();
                httpState.addCookies(transferedCookies);
                httpClient.setState(httpState);
            }

            // DEBUG cookies
            // Send request to servlet
            httpMethod.setRequestHeader("Content-type", "text/plain");
            httpMethod.setPath(url.getPath());

            // FIXME
            for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
                String name = (String) e.nextElement();
                httpMethod.addRequestHeader(name, request.getHeader(name));
                log.debug("Header Name=" + name + "value=" + request.getHeader(name));
            }

            HostConfiguration hostConfiguration = new HostConfiguration();
            hostConfiguration.setHost(url.getHost(), url.getPort(), url.getProtocol());

            log.debug("\n----------------------------------------------------------------"
                    + "\n- Starting session at URI: " + url + "\n- Host:                    "
                    + url.getHost() + "\n- Port:                    " + url.getPort()
                    + "\n----------------------------------------------------------------");

            if (trustStore != null) {
            	System.setProperty("javax.net.ssl.trustStore", trustStore);
            }            
            if (trustStorePassword != null) {
            	System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);     
            }

            int result = httpClient.executeMethod(hostConfiguration, httpMethod);

            log.debug("\n----------------------------------------------------------------"
                    + "\n- Result:                    " + result
                    + "\n----------------------------------------------------------------");

            // Handle GZIP Content-Encoding
            InputStream is = null;
            Header contentEncodingHeader = httpMethod.getResponseHeader("Content-Encoding");
            if (contentEncodingHeader != null && contentEncodingHeader.getValue().equalsIgnoreCase("gzip")) {
               is = new GZIPInputStream(httpMethod.getResponseBodyAsStream());
               log.debug("The content type is gzip.");
            } else {
                is = httpMethod.getResponseBodyAsStream();
            }

            // Return XML
            InputSource input = new InputSource(is);
            parser = (SAXParser) this.manager.lookup(SAXParser.ROLE);
            parser.parse(input, this.xmlConsumer);
            httpMethod.releaseConnection();
        } catch (SAXException e) {
            throw e;
        } catch (Exception e) {
            getLogger().error("Generation failed: ", e);
            throw new SAXException(e);
        } finally {
            this.manager.release((Component) parser);
        }
    }

    /**
     * Log input stream for debugging
     * 
     * @param in an <code>InputStream</code> value
     * 
     * @return an <code>InputStream</code> value
     * 
     * @exception Exception if an error occurs
     */
    private InputStream intercept(InputStream in) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes_read;
        ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();

        while ((bytes_read = in.read(buffer)) != -1) {
            bufferOut.write(buffer, 0, bytes_read);
        }

        return new ByteArrayInputStream(bufferOut.toByteArray());
    }

    private URL createURL(Request request) throws MalformedURLException {
        URL url = null;

        try {
            url = new URL(this.source);
            log.debug(".createURL(): " + url);
        } catch (MalformedURLException e) {
            url = new URL(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + this.source);
            log.debug(".createURL(): Add localhost and port: " + url);
        }
        return url;
    }
/*
    private void attribute(AttributesImpl attr, String name, String value) {
        attr.addAttribute("", name, name, "CDATA", value);
    }

    private void start(String name, AttributesImpl attr) throws SAXException {
        super.contentHandler.startElement(URI, name, name, attr);
        attr.clear();
    }

    private void end(String name) throws SAXException {
        super.contentHandler.endElement(URI, name, name);
    }

    private void data(String data) throws SAXException {
        super.contentHandler.characters(data.toCharArray(), 0, data.length());
    }*/
}
