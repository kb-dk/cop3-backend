<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:exslt="http://exslt.org/common">

    <xsl:output encoding="UTF-8"
                indent="yes"/>


    <xsl:key name="node" match="outline" use="@id"/>

    <xsl:param name="start_node_id" select="''"/>
    <xsl:param name="edition_id" select="''"/>
    <xsl:param name="mode" select="'deep'" />
    <xsl:param name="base_uri" select="'http://navigation/blabla/'"/>
    <xsl:param name="html_base_uri" select="'http://gui/blabla/'"/>
    <xsl:param name="rss_base_uri" select="'http://syndication/blabla/'"/>

    <xsl:template match="/">
      <add>
	<xsl:apply-templates />
      </add>
    </xsl:template>

    <xsl:template match="head">
    </xsl:template>

    <xsl:template match="body">
      <xsl:element name="doc">
	<xsl:element name="field"><xsl:attribute name="name">id</xsl:attribute>/editions/any/2009/jul/editions</xsl:element>
	<xsl:element name="field"><xsl:attribute name="name">node_tdsi</xsl:attribute>Hjem</xsl:element>
	<xsl:element name="field"><xsl:attribute name="name">node_tesi</xsl:attribute>Home</xsl:element>
      </xsl:element>
      <xsl:apply-templates mode="traverse" select="outline">
	<xsl:with-param name="daddy">/editions/any/2009/jul/editions</xsl:with-param>
      </xsl:apply-templates>
    </xsl:template>


    <xsl:template mode="traverse"  match="outline">
      <xsl:param name="daddy" select="''"/>
      <xsl:element name="doc">
          <xsl:element name="field"><xsl:attribute name="name">medium_ssi</xsl:attribute>categories</xsl:element>
	<xsl:if test="$daddy">
	  <xsl:element name="field">
	    <xsl:attribute name="name">parent_ssi</xsl:attribute>
	    <xsl:value-of select="$daddy"/>
	  </xsl:element>
	</xsl:if>
	<xsl:call-template name="links"/>
	<xsl:apply-templates mode="path" select="parent::outline"/>
      </xsl:element>
      <xsl:apply-templates mode="traverse" select="outline">
	<xsl:with-param name="daddy"><xsl:value-of select="concat($edition_id,'/subject',@nodeId)"/></xsl:with-param>
      </xsl:apply-templates>
    </xsl:template>

    <xsl:template mode="path" match="outline">
      <xsl:element name="field">
	<xsl:attribute name="name">bread_crumb_ssim</xsl:attribute>
	<xsl:value-of select="concat($edition_id,'/subject',@nodeId)"/>
      </xsl:element>
      <xsl:apply-templates mode="path" select="parent::outline"/>
    </xsl:template>

    <xsl:template name="links">
      <xsl:element name="field">
	<xsl:attribute name="name">id</xsl:attribute>
	<xsl:value-of select="concat($edition_id,'/subject',@nodeId)"/>
      </xsl:element>
      <xsl:if test="@text">
	<xsl:element name="field">
	  <xsl:attribute name="name">node_tdsi</xsl:attribute>
	  <xsl:value-of select="@text"/>
	</xsl:element>
      </xsl:if>
      <xsl:if test="@text-en">
	<xsl:element name="field">
	  <xsl:attribute name="name">node_tesi</xsl:attribute>
	  <xsl:value-of select="@text-en"/>
	</xsl:element>
      </xsl:if>
    </xsl:template>


</xsl:transform>
