<?xml version="1.0"?>

<xsl:stylesheet version="1.0" 
  xmlns:cinclude="http://apache.org/cocoon/include/1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="BX_xmlfile"/>
<xsl:param name="BX_xhtmlfile"/>
<xsl:param name="BX_xslfile"/>
<xsl:param name="BX_validationfile"/>
<xsl:param name="css"/>
<xsl:param name="BX_exitdestination"/>
<xsl:param name="contextmenufile"/>

<xsl:template match="/config">
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>

  <cinclude:include src="{$contextmenufile}"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="files/input/file[@name = 'BX_xmlfile']">
  <file name="BX_xmlfile"><xsl:value-of select="$BX_xmlfile"/></file>
</xsl:template>

<xsl:template match="files/input/file[@name = 'BX_xhtmlfile']">
  <xsl:if test="$BX_xhtmlfile">
    <file name="BX_xhtmlfile"><xsl:value-of select="$BX_xhtmlfile"/></file>
  </xsl:if>

</xsl:template>

<xsl:template match="files/input/file[@name = 'BX_xslfile']">
  <xsl:if test="$BX_xslfile">
    <file name="BX_xslfile"><xsl:value-of select="$BX_xslfile"/></file>
  </xsl:if>
</xsl:template>

<xsl:template match="files/input/file[@name = 'BX_validationfile']">
  <xsl:if test="$BX_validationfile">
    <file name="BX_validationfile"><xsl:value-of select="$BX_validationfile"/></file>
  </xsl:if>

</xsl:template>

<xsl:template match="files/output/file[@name = 'BX_exitdestination']">
  <file name="BX_exitdestination"><xsl:value-of select="$BX_exitdestination"/></file>
</xsl:template>

<xsl:template match="files/css/file">
  <file><xsl:value-of select="$css"/></file>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>

  </xsl:copy>
</xsl:template>
   
</xsl:stylesheet>