<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://apache.org/cocoon/lenya/ac/1.0">
  
  <xsl:template match="cocoon">
    <xsl:apply-templates select="usecases"/>
  </xsl:template>

  <xsl:template match="usecases">
    <usecases>
      <xsl:apply-templates select="component-instance"/>
    </usecases>
  </xsl:template>
  
  <xsl:template match="usecases/component-instance">
    <usecase id="{@name}"><role id="admin"/></usecase>
  </xsl:template>

</xsl:stylesheet>