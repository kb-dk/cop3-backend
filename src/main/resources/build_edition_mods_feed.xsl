<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	       xmlns:md="http://www.loc.gov/mods/v3" 
	       xmlns="http://www.loc.gov/mods/v3" 
	       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	       xmlns:t="http://www.tei-c.org/ns/1.0" 
	       xmlns:xlink="http://www.w3.org/1999/xlink" 
	       xmlns:xml="http://www.w3.org/XML/1998/namespace"
	       exclude-result-prefixes="md"
	       version="1.0">

  <xsl:param name="content_context" select="''"/>
  <xsl:param name="image_context" select="''"/>
  <xsl:param name="edition_name" select="''"/>
  <xsl:param name="uri" select="''"/>
  <xsl:param name="description" select="''"/>


  <xsl:output encoding="UTF-8"
	      method="xml"/>

  <xsl:template match="/">
    <xsl:apply-templates select="md:modsCollection" />
    <xsl:comment>we've hit document root</xsl:comment>
  </xsl:template> 


  <xsl:template match="md:modsCollection">
    <modsCollection
	xmlns="http://www.loc.gov/mods/v3" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:t="http://www.tei-c.org/ns/1.0" 
	xmlns:xlink="http://www.w3.org/1999/xlink" 
	xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-3.xsd">
      <xsl:apply-templates/>
      <xsl:comment>We've hit modsCollection</xsl:comment>
      <xsl:call-template name="build_record"/>
    </modsCollection>
  </xsl:template>

  <xsl:template name="build_record">
   
    <mods>
      <identifier type="uri"><xsl:value-of select="$uri"/></identifier>
      <titleInfo xml:lang="en">
	<title><xsl:value-of select="$edition_name"/></title>
      </titleInfo>
      <name xml:lang="en" type="corporate">
	<namePart>The Royal Library, Copenhagen</namePart>
	<role>
	  <roleTerm>creator</roleTerm>
	</role>
      </name>
      <name xml:lang="da" type="corporate">
	<namePart>Det Kongelige Bibliotek</namePart>
	<role>
	  <roleTerm>creator</roleTerm>
	</role>
      </name>

      <note xml:lang="en"><xsl:value-of select="$description"/></note>

      <identifier 
	  type="uri" 
	  displayLabel="thumbnail">http://example.org/image.jpg</identifier>
    </mods>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:transform>
