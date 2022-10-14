<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	       xmlns:md="http://www.loc.gov/mods/v3" 
	       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	       xmlns:t="http://www.tei-c.org/ns/1.0" 
	       xmlns:xlink="http://www.w3.org/1999/xlink" 
	       xmlns:xml="http://www.w3.org/XML/1998/namespace"
	       version="1.0">

<!--
author Sigfrid Lundberg (slu@kb.dk)
version $Revision$, last modified $Date$ by $Author$
$Id$
-->

  <xsl:param name="variable" select="'note'" />
  <xsl:param name="value"    select="''"      />
  <xsl:param name="language" select="'da'"    />

  <xsl:output encoding="UTF-8"
	      method="xml"/>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="md:mods">
    <md:mods>
      <xsl:apply-templates/>
      <md:note>
	<xsl:attribute name="xml:lang">
	  <xsl:value-of select="$language"/>
	</xsl:attribute>
	<xsl:value-of select="$value"/>
      </md:note>
    </md:mods>
  </xsl:template>

  <xsl:template match="md:note">
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:transform>
