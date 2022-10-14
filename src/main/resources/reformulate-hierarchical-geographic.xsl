<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	       xmlns:md="http://www.loc.gov/mods/v3" 
	       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	       xmlns:t="http://www.tei-c.org/ns/1.0" 
	       xmlns:xlink="http://www.w3.org/1999/xlink" 
	       xmlns:xml="http://www.w3.org/XML/1998/namespace"
	       version="1.0">

  <!--
      author David Grove Jørgensen (dgj@kb.dk)
      version $Revision$, last modified $Date$ by $Author$
      $Id$
  -->

  <xsl:param name="variable" select="''" />
  <xsl:param name="value"    select="''"      />
  <xsl:param name="language" select="'da'"    />

  <!--
      <md:area        displayLabel="lokalitet"      areaType="area">Fanø</md:area>  
      <md:city>København</md:city>
      <md:area        displayLabel="sogn"           areaType="parish">Vesterbro Sogn</md:area>
      <md:area        displayLabel="Bygningsnavn"   areaType="building">Sorte Diamant</md:area>
      <md:citySection displayLabel="vejnavn"        citySectionType="street">H.C. Andersens Boulevard</md:citySection>
      <md:citySection displayLabel="husnummer"      citySectionType="housenumber">42</md:citySection>
      <md:citySection displayLabel="postnummer"     citySectionType="zipcode">DK-1553</md:citySection>
      <md:area        displayLabel="matrikelnummer" areaType="cadastre">1341234123</md:area>
  -->  


  <xsl:output encoding="UTF-8"
	      method="xml"/>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="md:mods">
    <md:mods>
      <xsl:apply-templates/>
      <xsl:if test="not(md:subject/md:hierarchicalGeographic[node()])">
	<md:subject>
	  <xsl:call-template name="hierarchical"/>
	</md:subject>
      </xsl:if>
    </md:mods>
  </xsl:template>

  <xsl:template match="md:subject">
    <xsl:variable name="content">
      <xsl:value-of select="."/>
    </xsl:variable>
    <xsl:if test="string-length(normalize-space($content)) &gt; 0">
      <md:subject>
	<xsl:if test="md:hierarchicalGeographic/node()">
	  <xsl:attribute name="xml:lang">
	    <xsl:value-of select="$language"/>
	  </xsl:attribute>
	</xsl:if>
	<xsl:apply-templates/>
      </md:subject>
    </xsl:if>
  </xsl:template>

  <xsl:template match="md:hierarchicalGeographic[node()]">
    <xsl:call-template name="hierarchical"/>
  </xsl:template>

  <xsl:template name="hierarchical">
    <md:hierarchicalGeographic>
      <xsl:choose>
	<xsl:when test="$variable = 'area'">
	  <md:area displayLabel="lokalitet"
		   areaType="area">
	    <xsl:value-of select="$value"/>
	  </md:area>  
	</xsl:when>
	<xsl:when test="$variable = 'city'">
	  <md:city>
	    <xsl:value-of select="$value"/>
	  </md:city>
	</xsl:when>
	<xsl:when test="$variable = 'parish'">
	  <md:area displayLabel="sogn"
		   areaType="parish">
	    <xsl:value-of select="$value"/>
	  </md:area>
	</xsl:when>
	<xsl:when test="$variable = 'building'">
	  <md:area displayLabel="Bygningsnavn" 
		   areaType="building">
	    <xsl:value-of select="$value"/>
	  </md:area>
	</xsl:when>
	<xsl:when test="$variable = 'street'">
	  <md:citySection displayLabel="vejnavn"
			  citySectionType="street">
	    <xsl:value-of select="$value"/>
	  </md:citySection>
	</xsl:when>
	<xsl:when test="$variable = 'housenumber'">
	  <md:citySection displayLabel="husnummer"
			  citySectionType="housenumber">
	    <xsl:value-of select="$value"/>
	  </md:citySection>
	</xsl:when>
	<xsl:when test="$variable = 'zipcode'">
	  <md:citySection displayLabel="postnummer"     
			  citySectionType="zipcode">
	    <xsl:value-of select="$value"/>
	  </md:citySection>
	</xsl:when>
	<xsl:when test="$variable = 'cadastre'">	
	  <md:area   
	      displayLabel="matrikelnummer" 
	      areaType="cadastre">
	    <xsl:value-of select="$value"/>
	  </md:area>
	</xsl:when>
      </xsl:choose>

      <xsl:if test="not($variable = 'building')">
	<xsl:for-each select="md:area[not(@areaType)]">
	  <md:area displayLabel="bygningsnavn" 
		   areaType="building">
	    <xsl:apply-templates/>
	  </md:area>
	</xsl:for-each>
      </xsl:if>

      <xsl:for-each select="md:area[@areaType and not(@areaType=$variable)]">
	<md:area>
	  <xsl:apply-templates select="@*"/>
	  <xsl:value-of select="."/>
	</md:area>
      </xsl:for-each>

      <xsl:for-each select="md:citySection[not(@citySectionType = $variable)]">
	<md:citySection>
	  <xsl:apply-templates select="@*"/>
	  <xsl:value-of select="."/>
	</md:citySection>
      </xsl:for-each>

      <xsl:if test="not($variable = 'city')">
	<xsl:for-each select="md:city">
	  <md:city>
	    <xsl:value-of select="."/>
	  </md:city>
	</xsl:for-each>
      </xsl:if>

    </md:hierarchicalGeographic>
  </xsl:template>
  
  <xsl:template match="md:city|md:area[@areaType]|md:citySection|md:city"></xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:transform>
