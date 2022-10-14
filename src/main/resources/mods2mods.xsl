<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
		xmlns:java="http://xml.apache.org/xalan/java"
		xmlns:mods="http://www.loc.gov/mods/v3" 
		xmlns:md="http://www.loc.gov/mods/v3"
		xmlns:xlink="http://www.w3.org/1999/xlink"
		exclude-result-prefixes="mods java" version="1.0" >

<!--
   Used to customize the mods record and also make it more palatable for
   remote use
-->

  <xsl:param name="url_prefix" select="'surdeg'"/>
  <xsl:output encoding="UTF-8" method="xml" omit-xml-declaration="yes"/>
  
  <xsl:template match="/">
    <md:mods
	xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-3.xsd">
      <md:identifier type="uri">
	<xsl:variable name="lang">
	  <xsl:choose>
	   <xsl:when test="mods:mods/mods:recordInfo/mods:languageOfCataloging/mods:languageTerm/text()">
	     <xsl:value-of select="mods:mods/mods:recordInfo/mods:languageOfCataloging/mods:languageTerm"/>
	   </xsl:when>
	   <xsl:otherwise>da</xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>
	<xsl:value-of select="concat($url_prefix,mods:mods/mods:recordInfo/mods:recordIdentifier,'/',$lang,'/')"/>
      </md:identifier>
      <xsl:apply-templates select="mods:mods/*"/>
    </md:mods>
  </xsl:template>

  <xsl:template match="mods:subject[@valueURI]">
    <xsl:element name="md:subject">
      <xsl:if test="@authority">
	<xsl:attribute name="authority">
	  <xsl:value-of select="@authority"/>
	</xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xlink:href">
	<xsl:value-of select="@valueURI"/>
      </xsl:attribute>
      <md:topic>
	<xsl:choose>
	  <xsl:when test="mods:topic">
	    <xsl:apply-templates select="mods:topic"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:apply-templates/>
	  </xsl:otherwise>
	</xsl:choose>
      </md:topic>
    </xsl:element>
  </xsl:template>

  
  <xsl:template match="mods:identifier[not(@displayLabel)]"/>
  <xsl:template match="mods:extension"/>

  <xsl:template match="mods:relatedItem[@type='original']">
    <md:relatedItem type="original">
      <md:identifier><xsl:value-of select="mods:identifier"/></md:identifier>
    </md:relatedItem>
  </xsl:template>

  <xsl:template match="mods:relatedItem"/>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
