<?xml version="1.0" encoding="iso-8859-1"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://purl.org/atom/ns#"
>

<xsl:param name="relative2root"/>
<xsl:param name="contextprefix"/>

<xsl:template match="cmsbody">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<link rel="service.edit" type="application/x.atom+xml" href="introspection.xml" title="AtomAPI"/>
<link rel="stylesheet" type="text/css" href="{$contextprefix}/blog/live/css/styles.css" title="default css"/>
<meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
<title>
  <xsl:value-of select="echo:feed/echo:title"/>
</title>
</head>

<body>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<td colspan="2" id="title">
  <a href="{$relative2root}/index.html">
    <xsl:value-of select="echo:feed/echo:title"/>
  </a>
</td>
</tr>
<tr>
<td colspan="2" id="subtitle">
    XML and beyond <!-- subtitle has been removed from Atom specification -->
</td>
</tr>

<tr>
<td valign="top" id="content">
    <xsl:if test="not(echo:feed/echo:entry)">
    <p>
    No entries yet!
    </p>
    <h3>Add new entry</h3>
    <p>
    To create a new entry click on the <strong>File</strong> menu above and select the menu item <strong>Add new entry</strong>. Enter <strong>id</strong> and <strong>title</strong> and a new entry will be created.
    </p>
    <h3>Edit entry</h3>
    <p>
    To edit an entry click on the title of the entry. On the entry page click on the <strong>File</strong> menu and select the menu item <strong>Edit with ...</strong>.
    </p>
    <h3>Publish entry</h3>
    <p>
    To publish an entry click on the <strong>File</strong> menu and select the menu item <strong>Publish</strong>.
    </p>
    </xsl:if>

    <xsl:apply-templates select="echo:feed/echo:entry"/>
</td>

<td valign="top" id="sidebar">

<xsl:apply-templates select="sidebar/block" mode="atom"/>
</td>
</tr>

<tr>
<td colspan="2" id="footer">
Copyright &#169; 2003-2005 The Apache Software Foundation. All rights reserved.
</td>
</tr>
</table>
</body>
</html>
</xsl:template>

<xsl:template match="block" mode="atom">
<div class="sidebar-title"><xsl:value-of select="title"/></div>
<xsl:apply-templates select="content" mode="sidebar"/>
</xsl:template>

<xsl:template match="content" mode="sidebar">
<div class="sidebar-content">
<xsl:copy-of select="@*|node()"/>
</div>
</xsl:template>
 
</xsl:stylesheet>
