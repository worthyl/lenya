/*
 * $Id: IterativeHTMLCrawler.java,v 1.11 2003/03/04 19:44:56 gregor Exp $
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
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.search.crawler;

import websphinx.RobotExclusion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.StringTokenizer;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.11 $
 */
public class IterativeHTMLCrawler {
    java.util.Vector urlsToCrawl;
    java.util.TreeSet urlsToCrawlLowerCase;
    String url_list_file = "url_file.txt";
    String html_dump_directory = "html_dump";
    private String rootURL;

    private String[] scopeURL;
    private RobotExclusion robot;

    /**
     * Creates a new IterativeHTMLCrawler object.
     *
     * @param url_list_file DOCUMENT ME!
     * @param html_dump_directory DOCUMENT ME!
     * @param userAgent DOCUMENT ME!
     */
    public IterativeHTMLCrawler(String url_list_file, String html_dump_directory, String userAgent) {
        this.url_list_file = url_list_file;
        this.html_dump_directory = html_dump_directory;

        robot = new RobotExclusion(userAgent);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: IterativeHTMLCrawler crawler.xconf");

            return;
        }

        try {
            CrawlerEnvironment ce = new CrawlerEnvironment(args[0]);
            new IterativeHTMLCrawler(ce.resolvePath(ce.getURIList()),
                ce.resolvePath(ce.getHTDocsDumpDir()), ce.getUserAgent()).crawl(new URL(
                    ce.getBaseURL()), ce.getScopeURL());
        } catch (MalformedURLException e) {
            System.err.println(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param start DOCUMENT ME!
     * @param scope DOCUMENT ME!
     */
    public void crawl(URL start, String scope) {
        scopeURL = new String[1];
        scopeURL[0] = scope;

        String seedURL = start.toString();
        this.rootURL = seedURL.substring(0, seedURL.indexOf("/", 8));

        urlsToCrawl = new java.util.Vector();
        urlsToCrawlLowerCase = new java.util.TreeSet();

        String currentURLPath = start.toString().substring(0, start.toString().lastIndexOf("/"));

        try {
            System.out.println(".crawl(): Start crawling at: " + start);
            if (addURL(start.getFile(), currentURLPath) != null) {
                dumpHTDoc(start);
            }
            else {
                System.err.println(".crawl(): Start URL has not been dumped: " + start);
            }
        } catch (MalformedURLException e) {
            System.err.println(".crawl(): ERROR: " + e);
        }

        int currentPosition = 0;

        while (currentPosition < urlsToCrawl.size()) {
            URL currentURL = (URL) urlsToCrawl.elementAt(currentPosition);
            currentURLPath = currentURL.toString().substring(0,
                    currentURL.toString().lastIndexOf("/"));

            System.out.println(".crawl(): INFO: Current Array Size: " + urlsToCrawl.size() +
                ", Current Position: " + currentPosition + ", Current URL: " +
                currentURL.toString());

            java.util.List urlsWithinPage = parsePage(currentURL.toString());

            if (urlsWithinPage != null) {
                java.util.Iterator iterator = urlsWithinPage.iterator();

                while (iterator.hasNext()) {
                    String urlCandidate = (String) iterator.next();

                    try {
                        URL urlToCrawl = null;

                        if ((urlToCrawl = addURL(urlCandidate, currentURLPath)) != null) {
                            dumpHTDoc(urlToCrawl);
                        }
                    } catch (MalformedURLException e) {
                        System.err.println(".crawl(): ERROR: " + e);
                    }
                }
            }

            currentPosition = currentPosition + 1;
        }

        try {
            java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileOutputStream(
                        url_list_file));

            for (int i = 0; i < urlsToCrawl.size(); i++) {
                out.println("" + (URL) urlsToCrawl.elementAt(i));
            }

            out.close();
        } catch (java.io.FileNotFoundException e) {
            System.err.println(".crawl(): ERROR: " + e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param urlCandidate DOCUMENT ME!
     * @param currentURLPath DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws MalformedURLException DOCUMENT ME!
     */
    public URL addURL(String urlCandidate, String currentURLPath)
        throws MalformedURLException {

        URL url = new URL(parseHREF(urlCandidate, urlCandidate.toLowerCase(), currentURLPath)); //completeURL(currentURL,urlCandidate)  new URL(currentURLPath+"/"+urlCandidate);

        if (filterURL(urlCandidate, currentURLPath, urlsToCrawlLowerCase)) {
            if (!robot.disallowed(url)) {
                urlsToCrawl.add(url);
                urlsToCrawlLowerCase.add(url.toString().toLowerCase());
                System.out.println(".addURL(): INFO: URL added: " + url);

                return url;
            } else {
                System.out.println(".addURL(): INFO: Disallowed by robots.txt: " + urlCandidate);
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param urlString DOCUMENT ME!
     *
     * @return ok, 404
     */
    public java.util.List parsePage(String urlString) {

        String status = "ok";

        try {
            URL currentURL = new java.net.URL(urlString);
            String currentURLPath = urlString.substring(0, urlString.lastIndexOf("/"));
            HttpURLConnection httpCon = (HttpURLConnection) currentURL.openConnection();

            httpCon.setRequestProperty("User-Agent", "Wyona Lucene Crawler");

            httpCon.connect();

            long lastModified = httpCon.getLastModified();

            if (httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String contentType = httpCon.getContentType();

                if (contentType.indexOf("text/html") != -1) {
                    return handleHTML(httpCon);
                } else if (contentType.indexOf("application/pdf") != -1) {
                    handlePDF(httpCon);
                } else {
                    status = "Not an excepted content type : " + contentType;
                }
            } else {
                status = "bad";
            }

            httpCon.disconnect();
        } catch (java.net.MalformedURLException mue) {
            status = mue.toString();
        } catch (java.net.UnknownHostException uh) {
            status = uh.toString(); // Mark as a bad URL
        } catch (java.io.IOException ioe) {
            status = ioe.toString(); // Mark as a bad URL
        } catch (Exception e) {
            status = e.toString(); // Mark as a bad URL
        }

        //return status;
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param httpCon DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws java.io.IOException DOCUMENT ME!
     */
    public static java.util.List handleHTML(HttpURLConnection httpCon)
        throws java.io.IOException {
        ContentHandler handler = new HTMLHandler();
        handler.parse(httpCon.getInputStream());

        if (handler.getRobotFollow()) {
            java.util.List links = handler.getLinks();
	    return links;
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param httpCon DOCUMENT ME!
     */
    public void handlePDF(HttpURLConnection httpCon) {
        System.err.println(".handlePDF(): Not handled yet!");
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     * @param currentURLPath DOCUMENT ME!
     * @param links DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean filterURL(String url, String currentURLPath, java.util.TreeSet links) {

        String urlLowCase = url.toLowerCase();

        if (!(urlLowCase.startsWith("http://") || urlLowCase.startsWith("https://"))) {
            url = parseHREF(url, urlLowCase, currentURLPath);

            if (url != null) {
                urlLowCase = url.toLowerCase();
            }
        }

        if ((url != null) && inScope(url)) {
            if (!links.contains(urlLowCase)) {
                return true;
            }
        } else {
            System.out.println(".filterURL(): Not in scope: "+url);
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     * @param urlLowCase DOCUMENT ME!
     * @param currentURLPath DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String parseHREF(String url, String urlLowCase, String currentURLPath) {
        if (urlLowCase.startsWith("http://") || urlLowCase.startsWith("https://")) {
            return url;
        }

        // Looks for incomplete URL and completes them
        if (urlLowCase.startsWith("/")) {
            url = rootURL + url;
        } else if (urlLowCase.startsWith("./")) {
            url = currentURLPath + url.substring(1, url.length());
        } else if (urlLowCase.startsWith("../")) {
            int back = 1;

            while (urlLowCase.indexOf("../", back * 3) != -1)
                back++;

            int pos = currentURLPath.length();
            int count = back;

            while (count-- > 0) {
                pos = currentURLPath.lastIndexOf("/", pos) - 1;
            }

            url = currentURLPath.substring(0, pos + 2) + url.substring(3 * back, url.length());
        } else if (urlLowCase.startsWith("javascript:")) {
            // handle javascript:...
            System.err.println(".parseHREF(): WARN: \"javascript:\" is not implemented yet!");
            url = null;
        } else if (urlLowCase.startsWith("#")) {
            System.err.println(".parseHREF(): WARN: \"#\" (anchor) will be irgnored!");
            // internal anchor... ignore.
            url = null;
        } else if (urlLowCase.startsWith("mailto:")) {
            System.err.println(".parseHREF(): WARN: \"mailto:\" is not a URL to be followed!");
            // handle mailto:...
            url = null;
        } else {
            url = currentURLPath + "/" + url;
        }

        // strip anchor if exists otherwise crawler may index content multiple times
        // links to the same url but with unique anchors would be considered unique
        // by the crawler when they should not be
        if (url != null) {
            int i;

            if ((i = url.indexOf("#")) != -1) {
                url = url.substring(0, i);
            }
        }

        return url;
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean inScope(String url) {
        for (int i = 0; i < scopeURL.length; i++) {
            if (url.startsWith(scopeURL[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param child DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws MalformedURLException DOCUMENT ME!
     */
    public URL completeURL(URL parent, String child) throws MalformedURLException {
        return parent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     */
    public void dumpHTDoc(URL url) {
        String ext = getExtension(url);

        String filename = html_dump_directory + url.getFile();
        File file = new File(filename);

        if (filename.charAt(filename.length() - 1) == '/') {
            file = new File(filename + "index.html");
            ext = getExtension(file);
        }

        if (ext.equals("html") || ext.equals("htm") || ext.equals("txt") || ext.equals("pdf")) {
            try {
                File parent = new File(file.getParent());

                if (!parent.exists()) {
                    parent.mkdirs();
                }

                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                java.io.InputStream in = httpConnection.getInputStream();
                BufferedInputStream bin = new BufferedInputStream(in);
                BufferedReader reader = new BufferedReader(new InputStreamReader(bin));

		java.io.FileWriter fw = new java.io.FileWriter(file);
		int i;
		while ((i = reader.read()) != -1) {
		    fw.write(i);
		}
		
		fw.close();
		
		bin.close();
		in.close();
		httpConnection.disconnect();

                System.out.println(".dumpHTDoc(): INFO: URL dumped: " + url);
            } catch (Exception e) {
                System.err.println(".dumpHTDoc(): ERROR: " + e);
                System.out.println(".dumpHTDoc(): ERROR: URL not dumped: " + url);
            }
        } else {
            System.out.println(".dumpHTDoc(): INFO: URL not dumped: " + url);
        }
    }

    /**
     *
     */
    public void saveToFile(String filename, byte[] bytes)
        throws FileNotFoundException, IOException {
        File file = new File(filename);

        if (filename.charAt(filename.length() - 1) == '/') {
            file = new File(filename + "index.html");
        }

        File parent = new File(file.getParent());

        if (!parent.exists()) {
            System.out.println(".saveToFile(): Directory will be created: " +
                parent.getAbsolutePath());
            parent.mkdirs();
        }

        FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
        out.write(bytes);
        out.close();
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getExtension(URL url) {
        return getExtension(new File(url.getPath()));
    }

    /**
     * DOCUMENT ME!
     *
     * @param file DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getExtension(File file) {
        StringTokenizer st = new StringTokenizer(file.getPath(), ".");
        String extension = null;

        while (st.hasMoreElements()) {
            extension = st.nextToken();
        }

        return extension;
    }
}
