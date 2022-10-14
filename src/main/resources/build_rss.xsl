<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:md="http://www.loc.gov/mods/v3"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xmlns:t="http://www.tei-c.org/ns/1.0"
               xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#"
               xmlns:xml="http://www.w3.org/XML/1998/namespace"
               xmlns:georss="http://www.georss.org/georss"
               xmlns:xlink="http://www.w3.org/1999/xlink"
               xmlns:x="urn:x"
               xmlns:exslt="http://exslt.org/common"
               version="1.0">

    <xsl:param name="latitude"  select="''"/>
    <xsl:param name="longitude" select="''"/>
    <xsl:param name="content_context" select="''"/>
    <xsl:param name="record_id" select="''"/>
    <xsl:param name="image_context" select="''"/>
    <xsl:param name="language" select="'da'"/>
    <xsl:param name="keywords" select="''"/>

    <xsl:output encoding="UTF-8"
                method="xml"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="md:mods">

        <item>
            <title>
                <xsl:apply-templates select="md:titleInfo/md:title/node()"/>
            </title>
            <link>
                <xsl:value-of select="concat($content_context,normalize-space(md:recordInfo/md:recordIdentifier),'/',normalize-space($language),'/')"/>
            </link>
            <description>
                <xsl:value-of select="md:note"/>
            </description>
            <md:mods>
                <xsl:element name="md:identifier">
                    <xsl:attribute name="type">uri</xsl:attribute>
                    <xsl:value-of select="concat($content_context,$record_id,'/',$language,'/')"/>
                </xsl:element>

                <xsl:apply-templates/>
                <md:subject>
                    <md:cartographics>
                        <md:coordinates><xsl:value-of select="concat($latitude,',',$longitude)"/></md:coordinates>
                    </md:cartographics>
                </md:subject>
                <xsl:apply-templates select="exslt:node-set($keywords)/x:subject/*" />
            </md:mods>
            <georss:point><xsl:value-of select="concat($latitude,' ',$longitude)"/></georss:point>
            <geo:lat><xsl:value-of select="$latitude"/></geo:lat>
            <geo:long><xsl:value-of select="$longitude"/></geo:long>
        </item>

    </xsl:template>

    <xsl:template match="md:subject[md:cartographics]">
    </xsl:template>
    <!--
        <xsl:template match="md:mods/md:identifier[@type='uri' and not(@displayLabel)]">
        <md:identifier type="uri">
        <xsl:value-of select="concat($content_context,.)"/>
        </md:identifier>
        </xsl:template>
    -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="x:topic">
        <md:subject>
            <md:topic>
                <xsl:value-of select="." />
            </md:topic>
        </md:subject>
    </xsl:template>

    <xsl:template match="text()">
        <xsl:value-of select="normalize-space(.)" />
    </xsl:template>

</xsl:transform>
