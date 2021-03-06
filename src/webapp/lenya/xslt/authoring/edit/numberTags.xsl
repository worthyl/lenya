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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="xml" version="1.0" indent="yes"/>

<xsl:template match="/">
  <xsl:apply-templates select="*">
    <xsl:with-param name="parentID" select="'tag'"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="*|text()|@*">
  <xsl:param name="parentID"/>
  <xsl:variable name="thisID" select="concat($parentID,'.', count(preceding-sibling::*))"/>
  <xsl:copy>
    <xsl:attribute name="tagID"><xsl:value-of select="$thisID"/></xsl:attribute>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates select="*|text()|@*">
      <xsl:with-param name="parentID" select="$thisID"/>
    </xsl:apply-templates>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
