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
    $Id: reformulate-person.xsl 1852 2012-04-13 13:12:39Z slu $
    -->

    <xsl:param name="variable" select="'name'" />
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
                <md:genre>
                    <xsl:attribute name="xml:lang">da</xsl:attribute>
                    <xsl:value-of select="$value"/>
                </md:genre>
        </md:mods>
    </xsl:template>


    <xsl:template match="md:genre">
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:transform>