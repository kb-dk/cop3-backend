<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:exslt="http://exslt.org/common">

    <xsl:output encoding="UTF-8"
                indent="yes"/>


    <xsl:key name="node" match="outline" use="@id"/>

    <xsl:param name="start_node_id" select="''"/>
    <xsl:param name="mode" select="'deep'" />
    <xsl:param name="base_uri" select="'http://navigation/blabla/'"/>
    <xsl:param name="html_base_uri" select="'http://gui/blabla/'"/>
    <xsl:param name="rss_base_uri" select="'http://syndication/blabla/'"/>

    <xsl:template match="/">
        <opml version="2.0">
            <xsl:apply-templates/>
        </opml>
    </xsl:template>

    <xsl:template match="head">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="body">
        <body>
            <xsl:element name="outline">
                <xsl:attribute name="xmlUrl">/cop/editions/</xsl:attribute>
                <xsl:attribute name="htmlUrl">/editions/any/2009/jul/editions/</xsl:attribute>
                <xsl:attribute name="url">/cop/navigation/</xsl:attribute>
                <xsl:attribute name="text">Hjem</xsl:attribute>
                <xsl:attribute name="text-en">Home</xsl:attribute>
                <xsl:choose>
                    <xsl:when test="$start_node_id">
                        <xsl:variable name="my_kids">
                            <xsl:for-each select="key('node',$start_node_id)/outline">
                                <xsl:element name="outline">
                                    <xsl:call-template name="links"/>
                                    <xsl:copy-of select="@*"/>
                                    <xsl:copy-of select="processing-instruction()"/>
                                </xsl:element>
                            </xsl:for-each>
                        </xsl:variable>
                        <xsl:apply-templates mode="parents" select="key('node',$start_node_id)">
                            <xsl:with-param name="me_and_my_kids"  select="$my_kids"  />
                        </xsl:apply-templates>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:element>
        </body>
    </xsl:template>

    <xsl:template mode="parents" match="outline">
        <xsl:param name="me_and_my_kids"     select="''"/>
        <xsl:variable name="outline">
            <xsl:element name="outline">
                <xsl:call-template name="links"/>
                <xsl:copy-of select="@*"/>
                <xsl:copy-of select="processing-instruction()"/>
                <xsl:copy-of select="exslt:node-set($me_and_my_kids)" />
            </xsl:element>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="name(..) = 'body'">
                <xsl:copy-of select="exslt:node-set($outline)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates mode="parents" select="..">
                    <xsl:with-param name="me_and_my_kids"    select="$outline"/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="links">
        <xsl:if test="not(@xmlUrl)">
            <xsl:attribute name="xmlUrl">
                <xsl:value-of select="concat($rss_base_uri,'/subject',@nodeId,'/')"/>
            </xsl:attribute>
        </xsl:if>
        <xsl:if test="not(@htmlUrl)">
            <xsl:attribute name="htmlUrl">
                <xsl:value-of select="concat($html_base_uri,'/subject',@nodeId,'/')"/>
            </xsl:attribute>
        </xsl:if>
        <xsl:if test="not(@url)">
            <xsl:attribute name="url">
                <xsl:value-of select="concat($base_uri,'/subject',@nodeId,'/')"/>
            </xsl:attribute>
        </xsl:if>
    </xsl:template>


</xsl:transform>
