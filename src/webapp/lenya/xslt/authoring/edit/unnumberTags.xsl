<?xml version="1.0"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: unnumberTags.xsl,v 1.4 2004/03/13 12:42:05 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="xml" version="1.0" indent="yes"/>

<xsl:template match="/">
  <xsl:apply-templates select="*"/>
</xsl:template>

<!-- FIXME: there seems to be something wrong!!! (Xalan?) if something is written in front of Copy, then it works, else it doesn't ... -->
<xsl:template match="*|text()">
  <xsl:copy>
    <xsl:copy-of select="@*[name()!='tagID']"/>
    <xsl:apply-templates select="*|text()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
