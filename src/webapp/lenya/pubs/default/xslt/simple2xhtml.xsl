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
    xmlns:simple="http://apache.org/cocoon/lenya/doctypes/simple-document/1.0"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    exclude-result-prefixes="simple lenya"
    >

<xsl:include href="lenya-header.xsl"/>

        
<xsl:template match="/">
  <div id="body">
    <h1 bxe_xpath="/simple:simple-document/lenya:meta/dc:title"><xsl:apply-templates select="simple:simple-document/lenya:meta/dc:title"/></h1>
    <xsl:apply-templates select="simple:simple-document/simple:body/simple:subtitle"/>
    <xsl:apply-templates select="simple:simple-document/lenya:header/lenya:abstract"/>
    <div bxe_xpath="/simple:simple-document/simple:body">
      <xsl:apply-templates select="simple:simple-document/simple:body/*[local-name() != 'subtitle']"/>
    </div>
  </div>
</xsl:template>

<!-- XHTML-like tags -->
<!--<xsl:template match="simple:thead |simple:tbody | simple:tfoot">-->
<xsl:template match="simple:thead | simple:tfoot">
  <xsl:element name="{local-name()}">
    <xsl:attribute name="class">simple</xsl:attribute>
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>
<!-- /XHTML-like tags -->

<xsl:template match="simple:p">
  <xsl:element name="{local-name()}">
    <xsl:attribute name="class">simple</xsl:attribute>
    <xsl:apply-templates/>
  </xsl:element>
    <!-- FIXME: uncomment the following to get image upload red dots between paragraphs -->
<!--   <a href="?lenya.usecase=upload&amp;lenya.step=showscreen&amp;xpath=/simple-document/body/p[{count(preceding-sibling::simple:p)+1}]"> -->
<!--     <img src="/lenya/lenya/images/util/reddot.gif" alt="Upload Image" border="0"/> -->
<!--   </a> -->
</xsl:template>
<!-- /XHTML-like tags -->

<!-- inline markup -->
<xsl:template match="simple:bold">
  <strong><xsl:apply-templates/></strong>
</xsl:template>

<xsl:template match="simple:emphasize">
  <em><xsl:apply-templates/></em>
</xsl:template>
<!-- /inline markup -->

<!-- tables -->

<xsl:template match="simple:informaltable">
  <table class="simple" border="0" cellspacing="1">
    <xsl:apply-templates/>
  </table>
</xsl:template>

<xsl:template match="simple:tgroup">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="simple:row">
  <tr>
    <xsl:apply-templates/>
  </tr>
</xsl:template>

<xsl:template match="simple:entry">
  <td class="simple"><xsl:apply-templates/></td>
</xsl:template>

<!-- /tables -->

<!-- lists -->

<xsl:template match="simple:itemizedlist">
  <ul><xsl:apply-templates/></ul>
</xsl:template>

<xsl:template match="simple:listitem">
  <li><xsl:apply-templates/></li>
</xsl:template>

<!-- /lists -->

<!-- headlines -->

<xsl:template match="simple:subtitle">
  <h2><xsl:apply-templates/></h2>
</xsl:template>

<xsl:template match="simple:crossheading">
  <div class="crossheading">
    <xsl:apply-templates/>
  </div>
</xsl:template>

<xsl:template match="simple:*[starts-with(local-name(), 'hl')]">
  <xsl:element name="h{substring(local-name(), 3)}">
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>

<!-- /headlines -->


<!-- media -->
<xsl:template match="simple:media[@media-type = 'image']">
  <div class="media">
    <p>
    <img class="simple"
        src="{simple:media-reference/@source}"
        alt="{simple:media-reference/@alternate-text}"/>
    </p>
    <xsl:apply-templates select="simple:caption"/>
  </div>
</xsl:template>
<!-- /media -->

<xsl:template match="simple:caption">
  <div class="caption">
    <xsl:apply-templates/>
  </div>
</xsl:template>

<!-- links -->
<xsl:template match="simple:ulink">
  <a href="{@url}"><xsl:apply-templates/></a>
</xsl:template>
<!-- /link -->

<xsl:template match="text()">
  <xsl:copy/>
</xsl:template>


</xsl:stylesheet> 
