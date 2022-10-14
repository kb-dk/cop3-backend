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

  <xsl:param name="content_context" select="''"/>
  <xsl:param name="image_context" select="''"/>
  <xsl:param name="edition_name" select="''"/>
  <xsl:param name="description" select="''"/>
  <xsl:param name="base_uri" select="''"/>
  <xsl:param name="uri" select="''"/>

  <xsl:output encoding="UTF-8"
	      method="xml"/>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="rss">
    <rss xmlns:xlink="http://www.w3.org/1999/xlink" 
	 xmlns:atom="http://www.w3.org/2005/Atom" 
	 xmlns:opensearch="http://a9.com/-/spec/opensearch/1.1/" 
	 xmlns:tei="http://www.tei-c.org/ns/1.0" 
	 xmlns:dc="http://purl.org/dc/elements/1.1/" 
	 xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#"
	 xmlns:georss="http://www.georss.org/georss"
	 xmlns:xml="http://www.w3.org/XML/1998/namespace"
	 version="2.0">
      <xsl:apply-templates/>
    </rss>
  </xsl:template>

  <xsl:template match="md:mods">
    <item>
      <title>
	<xsl:value-of select="$edition_name"/>
      </title>
      <link><xsl:value-of select="concat($base_uri,$uri)"/></link>
      <description>
	<xsl:value-of select="$description"/>
      </description>
      <mods xmlns="http://www.loc.gov/mods/v3">
	<identifier type="uri"><xsl:value-of select="$uri"/></identifier>
	<identifier type="uri" displayLabel="image">
	  <xsl:value-of select="md:identifier[@displayLabel='image']"/>
	</identifier>
	<identifier type="uri" displayLabel="thumbnail">
	  <xsl:value-of select="md:identifier[@displayLabel='thumbnail']"/>
	</identifier>
      </mods>
    </item>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:transform>
