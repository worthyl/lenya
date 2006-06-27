<?xml version="1.0" encoding="iso-8859-1"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id$ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0">
  
<xsl:param name="publication-id"/>

<!-- insert requested job group element if it does not exist -->
<xsl:template match="sch:scheduler">
	<xsl:copy>
		<xsl:copy-of select="@*"/>
		<xsl:if test="not(sch:job-group[@name = $publication-id])">
			<sch:job-group name="{$publication-id}"/>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>


<!-- remove other publications -->
<xsl:template match="sch:job-group[@name != $publication-id]"/>

    
<!-- Identity transformation -->
<xsl:template match="@*|*">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>  


</xsl:stylesheet>