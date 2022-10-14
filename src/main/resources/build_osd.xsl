<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:md="http://www.loc.gov/mods/v3"
               xmlns:t="http://www.tei-c.org/ns/1.0"
               version="1.0">

    <xsl:output method="text"
		media-type="application/json"
		encoding="UTF-8"/>

    <xsl:param name="latitude" select="''"/>
    <xsl:param name="longitude" select="''"/>
    <xsl:param name="content_context" select="''"/>
    <xsl:param name="image_context" select="''"/>
    <xsl:param name="correctness" select="''"/>
    <xsl:param name="interestingness" select="''" />
    <xsl:param name="osd_id" select="'kbOSDInstance'" />
    <xsl:param name="showNavigator" select="'true'"/>
    <xsl:param name="initialPage" select="'1'"/>
    <xsl:param name="defaultZoomLevel" select="'0'"/>
    <xsl:param name="sequenceMode" select="'true'" />

<!-- 
"id":"kbOSDInstance","showNavigator":true,"rtl":false,"initialPage":"1","defaultZoomLevel":"0","sequenceMode":true,"indexPage":[{"title":"Bondens Tyende","page":"41"}
-->
    <xsl:template match="/"><shit><xsl:text>{</xsl:text>
      "id":"<xsl:value-of select="$osd_id"/>",
      "showNavigator":<xsl:value-of select="$showNavigator"/>,
      "initialPage":<xsl:value-of select="$initialPage"/>,
      "defaultZoomLevel":<xsl:value-of select="$defaultZoomLevel" />,
      "sequenceMode":<xsl:value-of select="$sequenceMode" />,
      "indexPage":[],
      <xsl:variable name="page_orientation">
	<xsl:for-each select="//md:physicalDescription[md:note[@type='pageOrientation']]">
	  <xsl:choose>
	    <xsl:when test="contains(md:note,'RTL')">"rtl":true</xsl:when>
	    <xsl:otherwise>"rtl":false</xsl:otherwise>
	    </xsl:choose>,
	</xsl:for-each>
      </xsl:variable>
      <xsl:value-of select="$page_orientation"/>
      <xsl:variable name="order">
	<xsl:choose>
	  <xsl:when test="contains($page_orientation,'true')">descending</xsl:when><xsl:otherwise>ascending</xsl:otherwise>
	</xsl:choose>
      </xsl:variable>
      "tileSources":[<xsl:for-each select="/md:modsCollection/md:mods/md:relatedItem[md:identifier]">
      <xsl:for-each select="md:identifier[text()]|md:relatedItem/md:identifier[text()]">
	<xsl:sort select="." data-type="text"  order="{$order}"/>
	"<xsl:value-of select="concat('http://kb-images.kb.dk/',substring-before(.,'.tif'),'/info.json')"/>"<xsl:if test="position() &lt; last()">,</xsl:if></xsl:for-each></xsl:for-each><xsl:text>]
}</xsl:text></shit></xsl:template>

</xsl:transform>