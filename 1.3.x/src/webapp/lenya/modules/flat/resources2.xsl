<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
<!-- Group into flat structure -->

<xsl:key name="ids" match="resource" use="@id" />

<xsl:template match="/resources">
<content>
<xsl:for-each select="resource[generate-id() = generate-id(key('ids', @id)[1])]">
    <xsl:sort select="@id" />
<resource id="{@id}" type="{@type}">
      <xsl:element name="translation">
      <xsl:attribute name="language">en</xsl:attribute>
    <xsl:for-each select="key('ids', @id)">
         <file>
         <xsl:apply-templates select="@*|node()" mode="resource"/>
         </file>
    </xsl:for-each>

       </xsl:element>
</resource>
  </xsl:for-each>
</content>
</xsl:template>

<xsl:template match="@type" mode="resource"/>
<xsl:template match="@id" mode="resource"/>
<xsl:template match="@idl" mode="resource"/>

<xsl:template match="resource">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()" mode="resource"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="@*|node()" mode="resource">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()" mode="resource"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="@*|node()" priority="-1"/>

</xsl:stylesheet> 