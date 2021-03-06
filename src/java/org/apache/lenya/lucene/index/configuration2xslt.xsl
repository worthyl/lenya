<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsl-out="http://apache.org/cocoon/lenya/alias"
    xmlns:luc="http://apache.org/cocoon/lenya/lucene/1.0"
    >

<xsl:namespace-alias stylesheet-prefix="xsl-out" result-prefix="xsl"/>    
<xsl:preserve-space elements="*"/>

<xsl:output method="xml" indent="yes" encoding="ISO-8859-1" />
    
<xsl:template match="luc:document">
  <xsl-out:stylesheet version="1.0">
  
    <xsl-out:param name="filename"/>
    
    <xsl:apply-templates select="luc:variable"/>
  
    <xsl-out:template match="/">
      <luc:document>
        <xsl-out:attribute name="filename"><xsl-out:value-of select="$filename"/></xsl-out:attribute>
      
        <xsl:for-each select="luc:field">
          <luc:field name="{@name}" type="{@type}">
            <xsl:for-each select="namespace">
              <xsl:attribute name="{@prefix}:dummy" namespace="{.}"/>
            </xsl:for-each>
            <xsl:apply-templates select="@xpath"/>
            <xsl:apply-templates/>
            <!--<xsl:apply-templates select="xpath"/>-->
          </luc:field>
        </xsl:for-each>
      
      </luc:document>
    </xsl-out:template>
  
  </xsl-out:stylesheet>
</xsl:template>


<xsl:template match="luc:variable">
  <xsl-out:variable name="{@name}" select="{@value}"/>
</xsl:template>


<xsl:template match="@xpath">
  <xsl-out:value-of select="{.}"/>
</xsl:template>

<xsl:template match="xpath">
  <xsl-out:value-of select="{.}"/>
  <xsl:if test="@default">
    <xsl-out:if test="not({.})">
      <xsl:value-of select="@default"/>
    </xsl-out:if>
  </xsl:if>
</xsl:template>

<xsl:template match="text">
  <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="namespace"/>

</xsl:stylesheet> 
