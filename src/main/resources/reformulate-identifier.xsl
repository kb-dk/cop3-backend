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
    version $Revision: 1852 $, last modified $Date: 2012-04-13 15:12:39 +0200 (fr, 13 apr 2012) $ by $Author: slu $
    $Id: reformulate-building.xsl 1852 2012-04-13 13:12:39Z slu $
    -->

    <xsl:param name="variable" select="'pdfidentifier'" />
    <xsl:param name="value"    select="''"      />
    <xsl:param name="displayLabel"     select="''" />
    <xsl:param name="language" select="'da'"    />

    <xsl:output encoding="UTF-8"
                method="xml"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="md:mods">
        <md:mods>
            <xsl:apply-templates/>
            <xsl:element name="md:identifier">
                <xsl:attribute name="xml:lang">
                    <xsl:value-of select="$language"/>
                </xsl:attribute>
                <xsl:attribute name="type">uri</xsl:attribute>
                <xsl:attribute name="displayLabel"><xsl:value-of select="$displayLabel"/></xsl:attribute>
                <xsl:value-of select="$value"/>
            </xsl:element>
        </md:mods>
    </xsl:template>

    <xsl:template match="md:mods/md:identifier[@displayLabel=$displayLabel]">
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:transform>