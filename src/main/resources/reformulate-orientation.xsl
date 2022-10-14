<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:md="http://www.loc.gov/mods/v3"
               xmlns:mix="http://www.loc.gov/mix/v10"
               xmlns:xml="http://www.w3.org/XML/1998/namespace"
               version="1.0">

    <!--
    author Jacob Larsen (jac@kb.dk)

    value can be either of :

        <xsd:enumeration value="normal*"/>
        <xsd:enumeration value="normal, image flipped"/>
        <xsd:enumeration value="normal, rotated 180"/>
        <xsd:enumeration value="normal, image flipped, rotated 180"/>
        <xsd:enumeration value="normal, image flipped, rotated cw 90"/>
        <xsd:enumeration value="normal, rotated ccw 90"/>
        <xsd:enumeration value="normal, image flipped, rotated ccw 90"/>
        <xsd:enumeration value="normal, rotated cw 90"/>
        <xsd:enumeration value="unknown"/>

    -->

    <xsl:param name="variable" select="'orientation'" />
    <xsl:param name="value"    select="''"      />

    <xsl:output encoding="UTF-8"
                method="xml"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="md:mods">
        <md:mods>
            <xsl:apply-templates/>

            <md:extension>
                <mix:mix>
                    <mix:orientationType>
                        <xsl:value-of select="$value"/>
                    </mix:orientationType>
                </mix:mix>
            </md:extension>
        </md:mods>
    </xsl:template>

    <!-- remove old value -->
    <xsl:template match="md:extension/mix:mix/mix:orientationType">
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:transform>