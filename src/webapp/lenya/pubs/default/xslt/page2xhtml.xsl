<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : page2xhtml.xsl
    Created on : 11. April 2003, 11:09
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:page="http://www.lenya.org/2003/page"
    >
    
<xsl:param name="root"/>
    
<xsl:template match="/page:page">
  <html>
    <head>
      <link rel="stylesheet" href="{$root}/css/page.css" mime-type="text/css"/>
    </head>
    <body>
      <xsl:apply-templates select="node()"/>
    </body>
  </html>
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
