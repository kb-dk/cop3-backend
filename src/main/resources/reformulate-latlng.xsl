<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:md="http://www.loc.gov/mods/v3"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xmlns:t="http://www.tei-c.org/ns/1.0"
               xmlns:xlink="http://www.w3.org/1999/xlink"
               xmlns:xml="http://www.w3.org/XML/1998/namespace"
               version="1.0">

    <!--
    author Andreas <XML-König> Borchsenius Westh (abwe@kb.dk)
    version 1Mill.
    -->

    <xsl:param name="variable" select="'latlng'"/>
    <xsl:param name="value" select="''"/>
    <xsl:param name="language" select="'da'"/>

    <xsl:output encoding="UTF-8"
                method="xml"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="md:mods">
        <md:mods>
            <xsl:apply-templates/>
            <md:subject>
                <xsl:attribute name="xml:lang">
                    <xsl:value-of select="$language"/>
                </xsl:attribute>
                <md:cartographics>
                    <md:coordinates>
                        <xsl:value-of select="$value"/>
                    </md:coordinates>
                </md:cartographics>
            </md:subject>
        </md:mods>
    </xsl:template>
    <!---
    <md:subject xmlns:java="http://xml.apache.org/xalan/java" xmlns:mix="http://www.loc.gov/mix/v10">
            <md:cartographics>
                <md:coordinates>
                    LAT LNG
                </md:coordinates>
           </md:cartographics>
       </md:subject>
    -->
    <xsl:template match="md:subject/md:cartographics/md:coordinates">
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:transform>