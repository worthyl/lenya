/*
$Id: WGet.java,v 1.26 2003/07/23 13:21:40 gregor Exp $
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
package org.apache.lenya.net;

import org.apache.log4j.Category;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.Iterator;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 0.1
 */
public class WGet {
    static Category log = Category.getInstance(WGet.class);
    String directory_prefix = null;

    /**
     * Creates a new WGet object.
     */
    public WGet() {
        directory_prefix = System.getProperty("user.dir");
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: org.apache.lenya.net.WGet [URL] -P/home/lenya/download");

            return;
        }

        try {
            WGet wget = new WGet();

            for (int i = 0; i < args.length; i++) {
                if (args[i].indexOf("-P") == 0) {
                    wget.setDirectoryPrefix(args[i].substring(2)); // -P/home/lenya/download, 2: remove "-P"
                }
            }

            byte[] response = wget.download(new URL(args[0]), "s/\\/lenya\\/oscom//g", "");
        } catch (MalformedURLException e) {
            System.err.println(e);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * -P
     *
     * @param directory_prefix DOCUMENT ME!
     */
    public void setDirectoryPrefix(String directory_prefix) {
        this.directory_prefix = directory_prefix;
    }

    /**
     * @param url The url of the resource to download
     * @param prefixSubstitute Regexp which shall be replaced
     * @param substituteReplacement Replacement of the regexp
     *
     * @return bytes of downloaded resource
     *
     * @throws IOException URL might not exist
     */
    public byte[] download(URL url, String prefixSubstitute, String substituteReplacement)
        throws IOException {
        log.debug(".download(): " + url + " " + prefixSubstitute + " " + substituteReplacement);

        return downloadUsingHttpClient(url, prefixSubstitute, substituteReplacement);
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     * @param prefixSubstitute DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public byte[] downloadUsingHttpClient(URL url, String prefixSubstitute,
        String substituteReplacement) {
        log.debug(".downloadUsingHttpClient(): " + url);

        byte[] sresponse = null;

        try {
            sresponse = getResource(url);

            File file = new File(createFileName(url, prefixSubstitute, substituteReplacement));

            saveToFile(file.getAbsolutePath(), sresponse);

            substitutePrefix(file.getAbsolutePath(), prefixSubstitute, substituteReplacement);
        } catch (MalformedURLException e) {
            log.error(".downloadUsingHttpClient(): " + e);
        } catch (FileNotFoundException e) {
            log.error(".downloadUsingHttpClient(): " + e);
        } catch (IOException e) {
            log.error(".downloadUsingHttpClient(): " + e);
        }

        List links = null;

        try {
            links = getLinks(url);
        } catch (IOException ioe) {
            log.error(".downloadUsingHttpClient(): " + ioe);
        }

        if (links != null) {
            Iterator iterator = links.iterator();

            while (iterator.hasNext()) {
                String link = (String) iterator.next();

                try {
                    URL child_url = new URL(org.apache.lenya.util.URLUtil.complete(url.toString(),
                                link));

                    byte[] child_sresponse = getResource(child_url);
                    saveToFile(createFileName(child_url, prefixSubstitute, substituteReplacement),
                        child_sresponse);
                } catch (Exception e) {
                    log.error(".downloadUsingHttpClient(): " + e);
                }
            }
        }

        return sresponse;
    }

    /**
     *
     */
    public byte[] getResource(URL url) throws IOException {
        log.debug(".getResource(): " + url);

        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        InputStream in = httpConnection.getInputStream();
        byte[] buffer = new byte[1024];
        int bytes_read;
        ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();

        while ((bytes_read = in.read(buffer)) != -1) {
            bufferOut.write(buffer, 0, bytes_read);
        }

        byte[] sresponse = bufferOut.toByteArray();
        httpConnection.disconnect();

        return sresponse;
    }

    /*
    //FIXME: This is an unfinished attempt at getting the links via cocoon.
    public URL[] getLinksViaCocoon(URL url) throws IOException, HttpException {
        log.debug(".getLinksViaCocoon(): "+url);

        java.io.BufferedReader br = null;
        try {
            String linkViewQuery = "cocoon-view=links";
            String sURL = url.getFile();
            URL links = new URL(url, sURL + ((sURL.indexOf("?") == -1) ? "?" : "&") + linkViewQuery);
            java.net.URLConnection links_url_connection = links.openConnection();
            java.io.InputStream is = links_url_connection.getInputStream();
            br = new java.io.BufferedReader(new java.io.InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                log.debug(".getLinksViaCocoon(): Link: "+line);
            }
        }
        catch(Exception e){
            log.error(".getLinksViaCocoon(): "+e);
        }

        return null;
    }
    */
    public List getLinks(URL url) throws IOException {
        log.debug(".getLinks(): Get links from " + url);

        List links = null;

        try {
            org.apache.lenya.util.HTML html = new org.apache.lenya.util.HTML(url.toString());
            links = html.getImageSrcs(false);
        } catch (Exception e) {
            log.error(".getLinks() Exception 423432: " + e);
        }

        if (links != null) {
            log.debug(".getLinks(): Number of links found: " + links.size());
        }

        return links;
    }

    /**
     * DOCUMENT ME!
     *
     * @param filename DOCUMENT ME!
     * @param prefixSubstitute DOCUMENT ME!
     * @param substituteReplacement DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public byte[] substitutePrefix(String filename, String prefixSubstitute,
        String substituteReplacement) throws IOException {
        try {
            File file = new File(filename);
            String command = "/usr/bin/sed --expression=s/" + escapeSlashes(prefixSubstitute) +
                "/" + escapeSlashes(substituteReplacement) + "/g " + file.getAbsolutePath();

            byte[] wget_response_sed = runProcess(command);

            java.io.ByteArrayInputStream bain = new java.io.ByteArrayInputStream(wget_response_sed);
            FileOutputStream fout = new FileOutputStream(file.getAbsolutePath());
            int bytes_read = 0;
            byte[] buffer = new byte[1024];

            while ((bytes_read = bain.read(buffer)) != -1) {
                fout.write(buffer, 0, bytes_read);
            }
        } catch (Exception e) {
            log.error(".substitutePrefix(): " + e);
        }

        return null;
    }

    /**
     * Escape slashes
     *
     * @return String with escaped slashes
     */
    public String escapeSlashes(String string) {
        StringBuffer buffer = new StringBuffer("");

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '/') {
                buffer.append("\\/");
            } else {
                buffer.append(string.charAt(i));
            }
        }

        return buffer.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return "-P: " + directory_prefix;
    }

    /**
     *
     */
    public void saveToFile(String filename, byte[] bytes)
        throws FileNotFoundException, IOException {
        File file = new File(filename);
        File parent = new File(file.getParent());

        if (!parent.exists()) {
            log.warn(".saveToFile(): Directory will be created: " + parent.getAbsolutePath());
            parent.mkdirs();
        }

        FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
        out.write(bytes);
        out.close();
    }

    /**
     * @param url URL of resource, which has been downloaded and shall be saved
     * @return Absolute substituted filename
     */
    public String createFileName(URL url, String prefixSubstitute, String substituteReplacement) {
        File file = new File(directory_prefix + File.separator + url.getFile());

        return file.getAbsolutePath().replaceAll(prefixSubstitute, substituteReplacement);
    }

    /*
    //FIXME: This is an unfinished attempt at using an internal grep
    //like method to rewrite the URI when statically exporting the
    //publication. At the moment we simply use sed (which of course
    //doesn't work on some platforms)
    public String grep(String pattern, String string) {
        try {
            RE regexp = new RE(pattern);
            if (regexp.match(string)) {
                log.debug("Pattern matched");
                for (int i=0; i<regexp.getParenCount(); i++) {
                    log.debug("Parenthesis: " + regexp.getParen(i));
                }
            } else{
                log.debug("Pattern did not match");
            }
            return "";
        } catch (RESyntaxException e) {
            log.error(e);
            return null;
        }
    }
    */
    public byte[] runProcess(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);

        java.io.InputStream in = process.getInputStream();
        byte[] buffer = new byte[1024];
        int bytes_read = 0;
        java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();

        while ((bytes_read = in.read(buffer)) != -1) {
            baout.write(buffer, 0, bytes_read);
        }

        if (baout.toString().length() > 0) {
            log.debug(".runProcess(): %%%InputStream:START" + baout.toString() +
                "END:InputStream%%%");
        }

        java.io.InputStream in_e = process.getErrorStream();
        java.io.ByteArrayOutputStream baout_e = new java.io.ByteArrayOutputStream();

        while ((bytes_read = in_e.read(buffer)) != -1) {
            baout_e.write(buffer, 0, bytes_read);
        }

        if (baout_e.toString().length() > 0) {
            log.error(".runProcess(): ###ErrorStream:START" + baout_e.toString() +
                "END:ErrorStream###");
        }

        return baout.toByteArray();
    }
}
