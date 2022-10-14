<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform version="1.0"
	       xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="node_id"  select="'id42'"/>
  <xsl:param name="text"     select="'42 42 42 42 42'"/>
  <xsl:param name="html_uri" select="'/images/billed/2010/okt/billeder/'"/>


  <xsl:template match="/opml">
    <opml version="2.0">
      <xsl:apply-templates select="head"/>
      <xsl:apply-templates select="body"/>
    </opml>
  </xsl:template>

  <xsl:template match="body">
    <body>
      <xsl:apply-templates select="outline"/>
    </body>
  </xsl:template>

  <xsl:template match="outline[@id='0']">
    <outline id="0" text="Hjem" text-en="Home" htmlUrl="/editions/any/2009/jul/editions">
      <xsl:apply-templates select="outline"/>
      <xsl:element name="outline">
	<xsl:attribute name="nodeId">
	  <xsl:value-of select="$node_id"/>
	</xsl:attribute>
	<xsl:attribute name="text">
	  <xsl:value-of select="$text"/>
	</xsl:attribute>
	<xsl:attribute name="htmlUrl">
	  <xsl:value-of select="$html_uri"/>
	</xsl:attribute>
      </xsl:element>
    </outline>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:transform>
