<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : scheduler-page.xsl
    Created on : 12. Mai 2003, 17:26
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:page="http://www.lenya.org/2003/cms-page"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:sch="http://www.lenya.org/2002/sch"
    >

<xsl:param name="document-uri"/>
<xsl:param name="area"/>
<xsl:param name="context-prefix"/>
<xsl:param name="publication-id"/>

<xsl:variable name="uri-prefix" select="concat($context-prefix, '/', $publication-id)"/>
<xsl:variable name="uri" select="concat($uri-prefix, '/', $area, '/', $document-uri)"/>


<!-- navigation menu -->
<xsl:template name="navigation-menu">
  <div class="menu">
    <xsl:variable name="menu-separator" select="'&#160;&#160;|&#160;&#160;'"/>
    <a class="menu-item" href="{$uri-prefix}/authoring/{$document-uri}">Back to page</a>
    <xsl:value-of select="$menu-separator"/>
    <a class="menu-item">
      <xsl:attribute name="href">
        <xsl:text/><xsl:value-of select="$uri"/>?<xsl:call-template name="parameters-as-request-parameters"/>
        <xsl:text/>
      </xsl:attribute>Refresh</a>
    
    <xsl:value-of select="$menu-separator"/>
    <a class="menu-item" href="{$context-prefix}/scheduler-servlet?lenya.usecase=schedule-page&amp;lenya.step=showscreen">Servlet</a>
  </div>
</xsl:template>

  
<xsl:template match="/sch:scheduler">
  <page:page>
    <page:title>Scheduler</page:title>
    
    <page:body>
      <xsl:call-template name="navigation-menu"/>
      <xsl:apply-templates select="sch:exception"/>
    
      <ul>
        <li><strong>Publication:</strong>&#160;&#160;<xsl:value-of select="$publication-id"/></li>
        <li><strong>Document:</strong>&#160;&#160;<xsl:value-of select="$document-uri"/></li>
      </ul>
    
      <table class="scheduler-job" border="0" cellpadding="0" cellspacing="0">

        <tr> 
          <th><strong>Add new job</strong></th>
          <th>Task</th>
          <th>Day</th>
          <th>Time</th>
          <th>&#160;</th>
          <th>&#160;</th>
        </tr>
        <xsl:call-template name="schedulerForm"/>

        <tr>
          <td colspan="6" class="scheduler-empty">&#160;</td>
        </tr>

        <tr>
          <th><strong>Edit existing jobs</strong></th>
          <th>Task</th>
          <th>Day</th>
          <th>Time</th>
          <th>&#160;</th>
          <th>&#160;</th>
        </tr>

        <xsl:if test="not(sch:publication/sch:jobs/sch:job)">
        <tr><td colspan="6">No active jobs.</td></tr>
        </xsl:if>
        
        <xsl:apply-templates select="sch:publication/sch:jobs/sch:job"/>

      </table>
    </page:body>
    
  </page:page>
  
</xsl:template>


<xsl:template match="sch:job">
  <tr>
    <form method="GET">

      <td>
        &#160;
        <!-- hidden input fields for parameters -->
        <xsl:call-template name="parameters-as-inputs"/>
        <xsl:apply-templates select="sch:parameter"/>
      </td>
      <td>
        <xsl:call-template name="tasks">
          <xsl:with-param name="current-task-id"
              select="sch:task/sch:parameter[@name='id']/@value"/>
        </xsl:call-template>
      </td>
      <xsl:choose>
        <xsl:when test="sch:trigger">
          <xsl:apply-templates select="sch:trigger"/>
          <td>
            <input type="submit" name="Action" value="Modify"/>
          </td>
        </xsl:when>
        <xsl:otherwise>
          <td colspan="2">
            <p>The job date has expired.</p>
          </td>
          <td>&#160;</td>
        </xsl:otherwise>
      </xsl:choose>
      <td>
        <input type="submit" name="Action" value="Delete"/>
      </td>
    </form>
  </tr>
</xsl:template>


  <!-- ============================================================= -->
  <!--   Generate the necessary form to schedule new jobs -->
  <!-- ============================================================= -->
  <xsl:template name="schedulerForm">
    <tr>
      <form method="GET">
        
        <!-- hidden input fields for parameters -->
        
        <td>
          <xsl:call-template name="parameters-as-inputs"/>
          &#160;
        </td>
        
        <!-- task selection combobox -->
	<td><xsl:call-template name="tasks"/></td>
        
	<td>
	  <font size="2">
	    <select name="trigger.startDay">
	      <xsl:call-template name="generateSelectionNames">
		<xsl:with-param name="currentValue" select="1"/>
		<xsl:with-param name="selectedValue" select="/sch:scheduler/sch:current-date/sch:day"/>
		<xsl:with-param name="maxValue" select="31"/>
	      </xsl:call-template>
	    </select>
	    <select name="trigger.startMonth">
	      <xsl:call-template name="generateSelectionNames">
		<xsl:with-param name="currentValue" select="1"/>
		<xsl:with-param name="selectedValue"
                    select="/sch:scheduler/sch:current-date/sch:month"/>
		<xsl:with-param name="maxValue" select="12"/>
	      </xsl:call-template>
	    </select>
	    <select name="trigger.startYear">
              <xsl:variable name="year"><xsl:value-of select="/sch:scheduler/sch:current-date/sch:year"/></xsl:variable>
              <xsl:call-template name="generateSelectionNames">
                <xsl:with-param name="currentValue" select="$year"/>
                <xsl:with-param name="selectedValue" select="$year"/>
                <xsl:with-param name="maxValue" select="$year + 2"/>
              </xsl:call-template>
	    </select>
	  </font>
	</td>
	<td>
	  <font size="2">
	    <input name="trigger.startHour" type="text" size="2" maxlength="2">
              <xsl:attribute name="value">
                <xsl:value-of select="format-number(/sch:scheduler/sch:current-date/sch:hour, '00')"/>
              </xsl:attribute>
            </input>
	    :
	    <input name="trigger.startMin" type="text" size="2" maxlength="2">
              <xsl:attribute name="value">
                <xsl:value-of select="format-number(/sch:scheduler/sch:current-date/sch:minute, '00')"/>
              </xsl:attribute>
            </input>
	  </font>
	</td>
        <td>&#160;</td>
	<td>
	  <input type="submit" name="Action" value="Add"/>
	</td>
      </form>
    </tr>
  </xsl:template>
  
  <!-- ============================================================= -->
  <!--   Generate numbers from 1 to maxValue for a <select> and select a -->
  <!--   given value -->
  <!-- ============================================================= -->
  <xsl:template name="generateSelectionNames">
    <xsl:param name="currentValue"/>
    <xsl:param name="selectedValue"/>
    <xsl:param name="maxValue"/>
    <xsl:choose>
      <xsl:when test="$currentValue = $selectedValue">
	<option>
	  <xsl:attribute name="selected"/>
	  <xsl:value-of select="$currentValue"/>
	</option>
      </xsl:when>
      <xsl:otherwise>
	<option><xsl:value-of select="$currentValue"/></option>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="$currentValue &lt; $maxValue">
      <xsl:call-template name="generateSelectionNames">
	<xsl:with-param name="currentValue" select="$currentValue + 1"/> 
	<xsl:with-param name="selectedValue" select="$selectedValue"/> 
	<xsl:with-param name="maxValue" select="$maxValue"/> 
      </xsl:call-template>
    </xsl:if>
  </xsl:template>


  <!-- ============================================================= -->
  <!-- create hidden inputs for all request parameters -->
  <!-- ============================================================= -->
  <xsl:template name="parameters-as-inputs">
<!--    <ul>-->
    <input type="hidden" name="lenya.usecase" value="schedule-page"/>
    <input type="hidden" name="lenya.step" value="showscreen"/>
    
    <xsl:for-each select="/sch:scheduler/sch:parameters/sch:parameter">
      <xsl:if test="not(starts-with(@name, 'job.'))">
      <xsl:if test="not(starts-with(@name, 'trigger.'))">
      <xsl:if test="not(starts-with(@name, 'task.id'))">
      <xsl:if test="not(translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'action')">
        <input type="hidden" name="{@name}" value="{@value}"/>
<!--        <li>Parameter: <xsl:value-of select="@name"/> = <xsl:value-of select="@value"/></li>-->
      </xsl:if>
      </xsl:if>
      </xsl:if>
      </xsl:if>
    </xsl:for-each>
<!--    </ul>-->
  </xsl:template>
  
  <!-- ============================================================= -->
  <!-- create new request parameters for all request parameters -->
  <!-- ============================================================= -->
  <xsl:template name="parameters-as-request-parameters">
    <xsl:text>lenya.usecase=schedule-page&amp;lenya.step=showscreen&amp;</xsl:text>
    
    <xsl:for-each select="/sch:scheduler/sch:parameters/sch:parameter">
      <xsl:if test="not(starts-with(@name, 'job.'))">
      <xsl:if test="not(starts-with(@name, 'trigger.'))">
      <xsl:if test="not(starts-with(@name, 'task.id'))">
      <xsl:if test="not(translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'action')">
        <xsl:if test="position() &gt; 1">
          <xsl:text>&amp;</xsl:text>
        </xsl:if>
        <xsl:value-of select="concat(@name, '=', @value)"/>
        <xsl:text/>
      </xsl:if>
      </xsl:if>
      </xsl:if>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  
  <!-- ============================================================= -->
  <!-- Create ComboBox entries for all available tasks -->
  <!-- ============================================================= -->
  <xsl:template name="tasks">
    <xsl:param name="current-task-id"/>
      <select name="task.id">
      <!--
        <xsl:attribute name="selected">
          <xsl:value-of select="/sch:scheduler/sch:tasks/sch:task[@id = $current-task-id]/@label"/>
        </xsl:attribute>
        -->
        <xsl:for-each select="/sch:scheduler/sch:tasks/sch:task">
          <option value="{@id}">
            <xsl:if test="@id = $current-task-id">
              <xsl:attribute name="selected"/>
            </xsl:if>
            <xsl:value-of select="label"/>
          </option>
        </xsl:for-each>
      </select>
  </xsl:template>

  <xsl:template match="sch:trigger">
    <td>
      <font size="2"> 
        <xsl:apply-templates select="sch:parameter[@name='day']"/>
        <xsl:apply-templates select="sch:parameter[@name='month']"/>
        <xsl:apply-templates select="sch:parameter[@name='year']"/>
      </font>
    </td>
    <td>
      <font size="2"> 
        <xsl:apply-templates select="sch:parameter[@name='hour']"/>
        : 
        <xsl:apply-templates select="sch:parameter[@name='minute']"/>
      </font>
    </td>
  </xsl:template>
  
  <xsl:template match="sch:parameter[@name='day']">
    <select name="trigger.startDay">
      <xsl:call-template name="generateSelectionNames">
	<xsl:with-param name="currentValue" select="1"/> 
	<xsl:with-param name="selectedValue" select="@value"/>
	<xsl:with-param name="maxValue" select="31"/>
      </xsl:call-template>      
    </select>
  </xsl:template>

  <xsl:template match="sch:parameter[@name='month']">
    <select name="trigger.startMonth">
      <xsl:call-template name="generateSelectionNames">
	<xsl:with-param name="currentValue" select="1"/> 
	<xsl:with-param name="selectedValue" select="@value"/>
	<xsl:with-param name="maxValue" select="12"/>
      </xsl:call-template>      
    </select>
  </xsl:template>

  <xsl:template match="sch:parameter[@name='year']">
    <select name="trigger.startYear">
      <xsl:call-template name="generateSelectionNames">
	<xsl:with-param name="currentValue" select="@value"/> 
	<xsl:with-param name="selectedValue" select="@value"/>
	<xsl:with-param name="maxValue" select="@value + 2"/>
      </xsl:call-template>      
    </select>
  </xsl:template>

  <xsl:template match="sch:parameter[@name='hour']">
      <input type="text" name="trigger.startHour" size="2" maxlength="2">
      <xsl:attribute name="value">
	<xsl:value-of select="format-number(@value, '00')"/>
      </xsl:attribute>
    </input>
  </xsl:template>

  <xsl:template match="sch:parameter[@name='minute']">
    <input type="text" name="trigger.startMin" size="2" maxlength="2">
      <xsl:attribute name="value">
	<xsl:value-of select="format-number(@value, '00')"/>
      </xsl:attribute>
    </input>
  </xsl:template>

  <!-- job id -->  
  <xsl:template match="sch:parameter[@name='id']">
    <input type="hidden" name="job.id" value="{@value}"/>
  </xsl:template>

<xsl:template match="sch:exception">
<font color="red">EXCEPTION: <xsl:value-of select="@type"/></font> (check the log files)
</xsl:template>

</xsl:stylesheet> 
