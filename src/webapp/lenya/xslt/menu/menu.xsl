<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="menu">
<html>

<head>
<title>Lenya</title>
<script type="text/javascript" src="/lenya/lenya/menu/menu.js">
</script>

<link type="text/css" rel="stylesheet" href="/lenya/lenya/menu/menu.css" />
<script type="text/javascript" src="/lenya/lenya/menu/netscape.js">
</script>
</head>

<body bgcolor="#ffffff" leftmargin="0" marginwidth="0" topmargin="0" marginheight="0">


<noscript>
<div style="color:#FF9900; padding:4px; font:14px arial; background:#000000">
<b>This site requires Javascript to be turned on.</b>
</div>
</noscript>



<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
<td background="/lenya/lenya/menu/images/frame-bg_oben.gif" width="13" height="4">
<img src="/lenya/lenya/menu/images/frame-bg_oben.gif" width="13" height="4" /></td>
<td background="/lenya/lenya/menu/images/frame-bg_oben.gif" width="200" height="4">
<img src="/lenya/lenya/menu/images/frame-bg_oben.gif" width="200" height="4" /></td>
<td background="/lenya/lenya/menu/images/frame-bg_oben.gif" height="4" width="286">
<img src="/lenya/lenya/menu/images/frame-bg_oben.gif" width="286" height="4" /></td>
<td background="/lenya/lenya/menu/images/frame-bg_oben.gif" height="4" width="97">
<img src="/lenya/lenya/menu/images/frame-bg_oben.gif" width="97" height="4" /></td>
<td background="/lenya/lenya/menu/images/frame-bg_oben.gif" height="4" width="4"><img
src="/lenya/lenya/menu/images/frame-bg_oben.gif" width="4" height="4" /></td>
</tr>

<tr>
<td rowspan="2" valign="top" align="right" background="/lenya/lenya/menu/images/grau-bg.gif">
  <img src="/lenya/lenya/menu/images/blau_anfang_oben.gif" />
</td>
<td background="/lenya/lenya/menu/images/grau-bg2.gif">
  <a href="{$context_prefix}/admin/">
    <img border="0" src="/lenya/lenya/menu/images/admin_active.png" />
  </a>
  <a href="{$context_prefix}/info/">
    <img border="0" src="/lenya/lenya/menu/images/info_active.gif" />
  </a>
  <img src="/lenya/lenya/menu/images/authoring_active.gif" />
  <a target="_blank" href="{$context_prefix}{live_uri}">
    <img border="0" src="/lenya/lenya/menu/images/live_inactive.gif" />
  </a>
</td>

<td align="right" colspan="2" background="/lenya/lenya/menu/images/grau-bg2.gif">
<font color="#ffffff" size="-2" face="verdana">
  <xsl:apply-templates select="workflow-state"/>
  User Id: <b><xsl:value-of select="current_username"/></b> | Server Time: <b><xsl:value-of select="server_time"/></b> &#160;&#160;&#160;
</font>
</td>
<td background="/lenya/lenya/menu/images/grau-bg.gif" height="4" width="2"><img
src="/lenya/lenya/menu/images/grau-bg.gif" width="2" height="4" /></td>
</tr>

<tr>
<td colspan="3" background="/lenya/lenya/menu/images/unten.gif"><img border="0"
src="/lenya/lenya/menu/images/unten.gif" /></td>
<td valign="top" rowspan="2" colspan="2" 
background="/lenya/lenya/menu/images/grau-bg.gif"><img border="0"
src="/lenya/lenya/menu/images/lenya_unten.gif" /></td>
</tr>

<tr valign="top">
  <td width="13" background="/lenya/lenya/menu/images/menu-bg.gif">
    <img border="0" src="/lenya/lenya/menu/images/menu_bg_anfang2.gif" />
  </td>
  <td colspan="3" valign="top" background="/lenya/lenya/menu/images/menu-bg.gif">
  <div id="navTop">
  <div id="navTopBG">
    <xsl:apply-templates select="menus/menu" mode="nav"/>
  </div>
  </div>
  </td>
</tr>
</table>

<xsl:apply-templates select="menus/menu" mode="menu"/>

<script type="text/javascript">
 initialize(); 
</script>
</body>
</html>
</xsl:template>





<xsl:template match="workflow-state">
  Workflow State: <b><xsl:apply-templates/></b> |
</xsl:template>



<xsl:template match="menu" mode="nav">
  <div style="float:left; width:1px"><img src="/lenya/lenya/menu/images/grau.gif" width="1" height="21" /></div>

<div style="float:left; width:10px">&#160;</div>

<div id="nav{@label}" class="click" style="float:left; width:46px">
<font size="-1" face="verdana"><b>&#160;<xsl:value-of select="@name"/></b></font>
</div>

  <div style="float:left; width:46px">&#160;</div>
<div style="float:left; width:1px"><img src="/lenya/lenya/menu/images/grau.gif" width="1" height="21" /></div>
</xsl:template>







<xsl:template match="menu" mode="menu">
  <div id="menu{@label}" class="menuOutline">
  <div class="menuBg">
  <xsl:for-each select="block">
          <xsl:for-each select="item">
            <xsl:choose>
              <xsl:when test="@href">
                <a class="mI"><xsl:attribute name="href"><xsl:value-of select="normalize-space(@href)"/></xsl:attribute><xsl:value-of select="."/></a>
              </xsl:when>
              <xsl:otherwise>
                <span class="mI"><xsl:value-of select="."/></span>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>

    <xsl:if test="position() != last()">
      <div style="height: 2px; background-color: #EEEEEE; background-repeat: repeat; background-attachment: scroll; background-image: url('/lenya/lenya/menu/images/dotted.gif'); background-position: 0% 50%">
      <div style="background:#EEEEEE"><img src="/lenya/lenya/menu/images/pixel.gif" height="1" alt="" />
      </div>
      </div>
    </xsl:if>
  </xsl:for-each>
  </div>
  </div>
</xsl:template>





</xsl:stylesheet>

