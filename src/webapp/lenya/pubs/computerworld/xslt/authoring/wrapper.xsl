<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="../../../../../../stylesheets/cms/Page/root-dhtml.xsl"/>

<!--
<xsl:template match="/">
    <xsl:apply-templates select="wyona/cmsbody/html"/>
 </xsl:template>
-->
<xsl:template match="wyona/cmsbody">
    <xsl:apply-templates/>
 </xsl:template>

<xsl:include href="../today.xsl"/>
<xsl:include href="../navigation.xsl"/>
<xsl:include href="small-preview.xsl"/>

<xsl:template match="span[@id = 'content']">
    <!-- Process the headlines here... -->
    <xsl:apply-templates select="/wyona/wrapper/html/body"/>
</xsl:template>

<!-- Replace Page title -->
<xsl:template match="head/title">
   <title><xsl:value-of select="/wyona/wrapper/html/head/title"/></title>
</xsl:template>

<xsl:template match="@*|*">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
