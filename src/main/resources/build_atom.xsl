<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	       xmlns:md="http://www.loc.gov/mods/v3" 
	       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	       xmlns:t="http://www.tei-c.org/ns/1.0" 
	       xmlns:xlink="http://www.w3.org/1999/xlink" 
	       xmlns:dc="http://purl.org/dc/elements/1.1/"
	       xmlns="http://www.w3.org/2005/Atom" 
	       version="1.0">

  <xsl:param name="latitude"  select="''"/>
  <xsl:param name="longitude" select="''"/>
  <xsl:param name="content_context" select="''"/>
  <xsl:param name="image_context" select="''"/>

  <xsl:output encoding="UTF-8"
	      method="xml"/>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="md:mods">
    <entry>
      <author>
	<name>
	  <xsl:apply-templates select="md:name"/>
	</name>
      </author>
      <title>
	<xsl:apply-templates select="md:titleInfo"/>
      </title>
      <xsl:element name="link">
	<xsl:attribute name="href">
	  <xsl:value-of select="md:identifier[@type='uri' and not(@displayLabel)][1]"/>
	</xsl:attribute>
      </xsl:element>
      <summary>
	<xsl:value-of select="md:note"/>
      </summary>
      <content type="xhtml">
	<div xmlns="http://www.w3.org/1999/xhtml">
	  <xsl:element name="a">
	    <xsl:attribute name="href">
	      <xsl:value-of select="md:identifier[@type='uri'][1]"/>
	    </xsl:attribute>
	    <xsl:element name="img">
	      <xsl:attribute name="src">
		<xsl:value-of
		    select="md:identifier[@type='uri'][@displayLabel='thumbnail']"/>
	      </xsl:attribute>
	    </xsl:element>
	  </xsl:element>
	  <xsl:element name="p">
	    <xsl:value-of select="md:note"/>
	  </xsl:element>
	</div>
      </content>

      <dc:date>2011</dc:date>
      <category label="thelibrary" term="The Library" />
      <category label="stories" term="Stories" />
      <updated>2011-01-12T07:59:21+01:00</updated>
      <id>
	<xsl:value-of 
	    select="concat($content_context,md:identifier[@type='uri' and not(@displayLabel)][1])"/></id>
    </entry>
  </xsl:template>


</xsl:transform>
