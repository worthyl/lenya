/*
 * $Id: CrawlerEnvironment.java,v 1.6 2003/03/04 17:46:47 gregor Exp $
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
package org.lenya.search.crawler;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.apache.log4j.Category;

import java.io.File;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 */
public class CrawlerEnvironment implements Configurable {
    static Category log = Category.getInstance(CrawlerEnvironment.class);
    private String configurationFilePath;
    private String base_url;
    private String user_agent;
    private String scope_url;
    private String uri_list;
    private String htdocs_dump_dir;

    /**
     * Creates a new CrawlerEnvironment object.
     *
     * @param configurationFilePath DOCUMENT ME!
     */
    public CrawlerEnvironment(String configurationFilePath) {
        this.configurationFilePath = configurationFilePath;

        File configurationFile = new File(configurationFilePath);

        try {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            Configuration configuration = builder.buildFromFile(configurationFile);
            configure(configuration);
        } catch (Exception e) {
            log.error("Cannot load publishing configuration! ", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println(
                "Usage: org.lenya.search.crawler.CrawlerEnvironment crawler.xconf [-name <name>]");

            return;
        }

        CrawlerEnvironment ce = new CrawlerEnvironment(args[0]);
        String parameter;

        String name = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-name")) {
                if ((i + 1) < args.length) {
                    name = args[i + 1];
                }
            }
        }

        if (name != null) {
            if (name.equals("htdocs-dump-dir")) {
                parameter = ce.getHTDocsDumpDir();
                System.out.println(ce.resolvePath(parameter));
            } else {
                System.out.println("No such element: " + name);
            }
        } else {
            parameter = ce.getBaseURL();
            System.out.println(parameter);

            parameter = ce.getScopeURL();
            System.out.println(parameter);

            parameter = ce.getUserAgent();
            System.out.println(parameter);

            parameter = ce.getURIList();
            System.out.println(parameter);
            System.out.println(ce.resolvePath(parameter));

            parameter = ce.getHTDocsDumpDir();
            System.out.println(parameter);
            System.out.println(ce.resolvePath(parameter));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param configuration DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration configuration)
        throws ConfigurationException {
        base_url = configuration.getChild("base-url").getAttribute("href");
        scope_url = configuration.getChild("scope-url").getAttribute("href");
        user_agent = configuration.getChild("user-agent").getValue(null);
        uri_list = configuration.getChild("uri-list").getAttribute("src");
        htdocs_dump_dir = configuration.getChild("htdocs-dump-dir").getAttribute("src");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getBaseURL() {
        log.debug(".getBaseURL(): " + base_url);

        return base_url;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getScopeURL() {
        log.debug(".getScopeURL(): " + scope_url);

        return scope_url;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getUserAgent() {
        log.debug(".getUserAgent(): " + user_agent);

        return user_agent;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getURIList() {
        log.debug(".getURIList(): " + uri_list);

        return uri_list;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHTDocsDumpDir() {
        log.debug(".getHTDocsDumpDir(): " + htdocs_dump_dir);

        return htdocs_dump_dir;
    }

    /**
     * DOCUMENT ME!
     *
     * @param path DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String resolvePath(String path) {
        if (path.indexOf(File.separator) == 0) {
            return path;
        }

        return org.apache.avalon.excalibur.io.FileUtil.catPath(configurationFilePath, path);
    }
}
