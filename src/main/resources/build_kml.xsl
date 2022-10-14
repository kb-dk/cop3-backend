<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:md="http://www.loc.gov/mods/v3"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xmlns:t="http://www.tei-c.org/ns/1.0"
               xmlns:xlink="http://www.w3.org/1999/xlink"
               xmlns:atom="http://www.w3.org/2005/Atom"
               xmlns="http://www.opengis.net/kml/2.2"
               exclude-result-prefixes="xsl md xsi t xlink"
               version="1.0">

    <xsl:param name="latitude" select="''"/>
    <xsl:param name="longitude" select="''"/>
    <xsl:param name="content_context" select="''"/>
    <xsl:param name="image_context" select="''"/>
    <xsl:param name="correctness" select="''"/>
    <xsl:param name="interestingness" select="''" />

    <xsl:output encoding="UTF-8" method="xml"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="md:mods">
        <Placemark>
            <xsl:attribute name="id">
                <xsl:value-of select="concat('object',substring-after(normalize-space(md:recordInfo/md:recordIdentifier),'object'))"/>
            </xsl:attribute>
            <name>
                <xsl:choose>
                    <xsl:when test="md:titleInfo/md:title">
                        <xsl:value-of select="normalize-space(md:titleInfo/md:title)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="normalize-space(md:subject/md:name/md:namePart)"/> (<xsl:value-of
                            select="normalize-space(md:originInfo/md:dateCreated)"/>)
                    </xsl:otherwise>
                </xsl:choose>
            </name>
            <styleUrl>#balloon-style</styleUrl>
            <xsl:element name="atom:link">
                <xsl:attribute name="href">
                    <xsl:value-of select="concat($content_context,normalize-space(md:recordInfo/md:recordIdentifier))"/>
                </xsl:attribute>
            </xsl:element>

            <xsl:if test="$latitude and $longitude">
                <Point>
                    <coordinates>
                        <xsl:value-of select="concat($longitude,',',$latitude)"/>
                    </coordinates>
                </Point>
            </xsl:if>

            <ExtendedData>
                <Data name="subjectLink">
                    <value>
                        <xsl:value-of select="concat($content_context,normalize-space(md:recordInfo/md:recordIdentifier))"/>
                    </value>
                </Data>
                <Data name="subjectName">
                    <value>
                        <xsl:value-of select="normalize-space(md:subject/md:name/md:namePart)"/>
                    </value>
                </Data>
                <Data name="subjectCreatorName">
                    <value>
                        <xsl:value-of select="normalize-space(md:name[@type='personal']/md:namePart)"/>
                    </value>
                </Data>
                <Data name="subjectCreationDate">
                    <value>
                        <xsl:value-of select="normalize-space(md:originInfo/md:dateCreated)"/>
                    </value>
                </Data>
                <Data name="subjectGenre">
                    <value>
                        <xsl:value-of select="normalize-space(md:genre)"/>
                    </value>
                </Data>
                <Data name="subjectNote">
                    <value>
                        <xsl:value-of select="normalize-space(md:note)"/>
                    </value>
                </Data>
                <Data name="subjectGeographic">
                    <value>
                        <xsl:value-of select="normalize-space(md:subject/md:geographic)"/>
                    </value>
                </Data>
                <Data name="subjectImageSrc">
                    <value>
                        <xsl:value-of select="normalize-space(md:identifier[@type='uri'][@displayLabel='image'])"/>
                    </value>
                </Data>
                <Data name="subjectThumbnailSrc">
                    <value>
                        <xsl:value-of select="normalize-space(md:identifier[@type='uri'][@displayLabel='thumbnail'])"/>
                    </value>
                </Data>
                <Data name="recordCreationDate">
                    <value>
                        <xsl:value-of select="normalize-space(md:recordInfo/md:recordCreationDate)"/>
                    </value>
                </Data>
                <Data name="recordChangeDate">
                    <value>
                        <xsl:value-of select="normalize-space(md:recordInfo/md:recordChangeDate)"/>
                    </value>
                </Data>
                 <Data name="correctness">
                    <value>
                         <xsl:value-of select="normalize-space($correctness)"/>

                    </value>
                </Data>
                <Data name="interestingness">
                    <value>
                        <xsl:value-of select="normalize-space($interestingness)"/>

                    </value>
                </Data>
            </ExtendedData>

        </Placemark>

    </xsl:template>

</xsl:transform>
