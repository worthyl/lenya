<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:param name="projectid"/>

<xsl:include href="../../../../../../../stylesheets/cms/Page/xopus/root.xsl"/>

<xsl:template match="cmsbody">
  <xsl:apply-templates select="oscom"/>
</xsl:template>

<xsl:include href="../../html_authoring.xsl"/>

<xsl:template name="body">
 <div id="article_xopus" xopus="true" autostart="true">
<br /><br /><br /><br />
<b>............ LOADING EDITOR PROJECT ............</b>
 	<xml>
		<pipeline xml="editor-matrix/{$projectid}.xml" xsd="matrix.xsd">
			<view id="defaultView" default="true">
				<transform xsl="Page/Matrix/Authoring/body_editor_xopus2.xsl"></transform>
			</view>
			<view id="treeView">
				<transform xsl="Page/Home/Authoring/tree.xsl"></transform>
			</view>
		</pipeline>
    </xml>
 </div>
</xsl:template>
 
</xsl:stylesheet>
