<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:col="http://apache.org/cocoon/lenya/collection/1.0"
  xmlns:ci="http://apache.org/cocoon/include/1.0">
  
  <xsl:param name="uuid"/>
  <xsl:param name="language"/>
  
  <xsl:template match="col:collection">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:choose>
        <xsl:when test="@type = 'children'">
          <xsl:variable name="allLanguages">
            <xsl:choose>
              <xsl:when test="@allLanguages"><xsl:value-of select="@allLanguages"/></xsl:when>
              <xsl:otherwise>false</xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <ci:include src="cocoon://modules/collection/collectionWithChildren/{$allLanguages}/{$uuid}/{$language}.xml" select="*/*"/>
        </xsl:when>
        <xsl:when test="@type = 'link'">
          <ci:include src="{@href}" select="*/*"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:copy-of select="col:document"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>
  
  
</xsl:stylesheet>