<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	       xmlns:md="http://www.loc.gov/mods/v3" 
	       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	       xmlns:t="http://www.tei-c.org/ns/1.0" 
	       xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#"
	       xmlns:xml="http://www.w3.org/XML/1998/namespace"
	       xmlns:georss="http://www.georss.org/georss"
	       xmlns:xlink="http://www.w3.org/1999/xlink" 
	       version="1.0">

  <xsl:param name="latitude"  select="''"/>
  <xsl:param name="longitude" select="''"/>
  <xsl:param name="content_context" select="''"/>
  <xsl:param name="image_context" select="''"/>

  <xsl:param name="count"          select="count(//md:relatedItem[md:identifier[not(@displayLabel)]])" />
  <xsl:param name="read_direction" select="md:mods/md:physicalDescription/md:note[@type='pageOrientation']"/>


  <xsl:output encoding="UTF-8"
	      method="xml"
	      indent="yes"/>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="md:mods">
    <head>
      <title>Table of contents</title>
      <dateCreated>Mon, 27 Feb 2006 12:09:48 GMT</dateCreated>
      <dateModified>Mon, 27 Feb 2006 12:11:44 GMT</dateModified>
      <ownerName>Det Kongelige Bibliotek</ownerName>
    </head>
    <body>
      <xsl:attribute name="pageOrientation">
	<xsl:value-of select="$read_direction"/>
      </xsl:attribute>
      <outline xml:lang="en" text="Table of contents">
	<xsl:apply-templates select="md:relatedItem[md:identifier[not(@displayLabel)]]"/>
      </outline>
    </body>
  </xsl:template>

  <xsl:template match="md:relatedItem">

    <xsl:element name="outline">
      <xsl:attribute name="sequence">
	<xsl:value-of
	    select="1 + count(ancestor::md:relatedItem[md:identifier[not(@displayLabel)]]|preceding::md:relatedItem[md:identifier[not(@displayLabel)]])"/>
      </xsl:attribute>

      <xsl:attribute name="n">
	<xsl:choose>
	  <xsl:when test="$read_direction = 'LTR'">
	    <xsl:value-of
		select="1 + count(ancestor::md:relatedItem[md:identifier]|preceding::md:relatedItem[md:identifier[not(@displayLabel)]])"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:value-of 
		select="$count - count(ancestor::md:relatedItem[md:identifier]|preceding::md:relatedItem[md:identifier[not(@displayLabel)]])"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:attribute>
      <xsl:if test="md:titleInfo/@xml:lang">
	<xsl:attribute name="xml:lang">
	  <xsl:value-of select="md:titleInfo/@xml:lang"/>
	</xsl:attribute>
      </xsl:if>
      <xsl:attribute name="htmlUrl">
	<xsl:value-of select="md:identifier"/>
      </xsl:attribute>
      <xsl:if test="md:titleInfo">
	<xsl:attribute name="text">
	  <xsl:value-of select="normalize-space(md:titleInfo)"/>
	</xsl:attribute>
      </xsl:if>
      <xsl:choose>
	<xsl:when test="$read_direction = 'LTR'">
	  <xsl:apply-templates select="md:relatedItem[@type='constituent']">
	    <xsl:sort order="ascending" select="md:identifier[not(@displayLabel)]"/>
	  </xsl:apply-templates>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:apply-templates select="md:relatedItem[@type='constituent']">
	    <xsl:sort order="descending" select="md:identifier[not(@displayLabel)]"/>
	  </xsl:apply-templates>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:element>
  </xsl:template>

</xsl:transform>
