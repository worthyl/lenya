<?xml version="1.0"?>
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
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<xsl:param name="configfile"/>
<xsl:param name="context"/>

<xsl:template match="xhtml:head/xhtml:title">
  <title><xsl:value-of select="$configfile"/></title>
</xsl:template>

<xsl:template match="xhtml:body/@onload">
  <xsl:attribute name="onload">bxe_start('<xsl:value-of select="$configfile"/>')</xsl:attribute>
</xsl:template>

<xsl:template match="xhtml:head/xhtml:link/@href">
  <xsl:attribute name="href"><xsl:value-of select="$context"/>/bxeng/css/editor.css</xsl:attribute>
</xsl:template>

<xsl:template match="xhtml:head/xhtml:script/@src">
  <xsl:attribute name="src"><xsl:value-of select="$context"/>/bxeng/bxeLoader.js</xsl:attribute>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
   
</xsl:stylesheet> 
