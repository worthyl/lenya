/*
 * $Id: JobDataMapWrapper.java,v 1.4 2003/03/04 17:46:35 gregor Exp $
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
package org.lenya.cms.scheduler;

import org.apache.avalon.framework.parameters.Parameters;

import org.quartz.JobDataMap;


/**
 * DOCUMENT ME!
 *
 * @author ah
 */
public class JobDataMapWrapper {
    public static final String SEPARATOR = ".";
    private JobDataMap map;
    private String prefix;

    /**
     * Creates a new JobDataMapWrapper object.
     *
     * @param prefix DOCUMENT ME!
     */
    public JobDataMapWrapper(String prefix) {
        this(new JobDataMap(), prefix);
    }

    /**
     * Creates a new instance of JobDataMapWrapper
     *
     * @param map DOCUMENT ME!
     * @param prefix DOCUMENT ME!
     */
    public JobDataMapWrapper(JobDataMap map, String prefix) {
        this.map = map;
        this.prefix = prefix;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JobDataMap getMap() {
        return map;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Parameters getParameters() {
        Parameters parameters = new Parameters();
        String[] names = (String[]) getMap().keySet().toArray(new String[getMap().size()]);

        for (int i = 0; i < names.length; i++) {
            if (names[i].startsWith(getPrefix() + SEPARATOR)) {
                parameters.setParameter(getShortName(getPrefix(), names[i]), getMap().getString(names[i]));
            }
        }

        return parameters;
    }

    /**
     * DOCUMENT ME!
     *
     * @param key DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public void put(String key, String value) {
        map.put(getFullName(prefix, key), value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param key DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String get(String key) {
        String[] names = (String[]) getMap().keySet().toArray(new String[getMap().size()]);

        for (int i = 0; i < names.length; i++) {
            String name = names[i];

            if (name.equals(getFullName(getPrefix(), key))) {
                return getMap().getString(names[i]);
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param prefix DOCUMENT ME!
     * @param key DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getFullName(String prefix, String key) {
        return prefix + SEPARATOR + key;
    }

    /**
     * DOCUMENT ME!
     *
     * @param prefix DOCUMENT ME!
     * @param key DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getShortName(String prefix, String key) {
        return key.substring(prefix.length() + SEPARATOR.length());
    }
}
