<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:param name="channel"/>
<xsl:param name="section"/>
<xsl:param name="year"/>

<xsl:template match="/" xmlns:xi="http://www.w3.org/2001/XInclude">
  <Page> 
    <Content>
<!--      FirstColumn-->
          <MainNavigation>
            <Channels> 
             <xi:include xml:base="cocoon:" href="navigation.xml#xpointer(/FirstColumn/MainNavigation/Channels/Channel[@name='{$channel}'])"/>
            </Channels> 
          </MainNavigation>   
<!--      End FirstColumn-->
      <MainColumn>
        <xsl:apply-templates select="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0"/>
      </MainColumn>
    </Content>
  </Page>
</xsl:template>                                                                                                                             

<xsl:template match="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0">
<section type="{$section}">
  <type><xsl:value-of select="$section"/></type>
  <articles xmlns:xi="http://www.w3.org/2001/XInclude">
    <xsl:for-each select="dir:directory">
      <article href="{@name}">
      <xi:include xml:base="cocoon:" href="{$channel}/{$section}/{$year}/{@name}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head)"/>
      </article>
    </xsl:for-each>
  </articles>
</section>
</xsl:template>

</xsl:stylesheet>
