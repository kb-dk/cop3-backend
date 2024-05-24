<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:oa="http://www.openarchives.org/OAI/2.0/"
               xmlns:ese="http://www.europeana.eu/schemas/ese/"
               xmlns:dc="http://purl.org/dc/elements/1.1/"
               xmlns:dcterms="http://purl.org/dc/terms/"
               xmlns:md="http://www.loc.gov/mods/v3"
               xmlns:h="http://www.w3.org/1999/xhtml"
               xmlns:crypto="http://exslt.org/crypto"
               xmlns:exsl="http://exslt.org/common"
               xmlns:tei="http://www.tei-c.org/ns/1.0"
               extension-element-prefixes="exsl crypto"
               exclude-result-prefixes="oa ese dc dcterms md h"
               version="1.0">

    <xsl:param name="resolution">
        <xsl:text>europeana</xsl:text>
    </xsl:param>
    <xsl:param name="iiif_thumb_nail">
        <xsl:text>/full/!400,/0/native.jpg</xsl:text>
    </xsl:param>
    <xsl:param name="iiif_square_thumb_nail">
        <xsl:text>/100,100,1000,1000/!100,100/0/native.jpg</xsl:text>
    </xsl:param>
    <xsl:param name="raw_mods" select="''"/>
    <xsl:param name="comments" select="''"/>


    <xsl:param name="types">
        <ul>
            <li>books</li>
            <li>serials</li>
            <li>manus</li>
            <li>sheetmusic</li>
            <li>maps</li>
            <li>images</li>
            <li>phonograms</li>
            <li>multimedia</li>
            <li>iamedia</li>
            <li>varia</li>
            <li>pamphlets</li>
            <li>letters</li>
        </ul>
    </xsl:param>

    <xsl:param name="medias" select="exsl:node-set($types)"/>

    <xsl:param name="full_iiif_scaling">
        <xsl:text>/full/full/0/native.jpg</xsl:text>
    </xsl:param>

    <xsl:param name="iiif_scaling">
        <xsl:choose>
            <xsl:when test="$resolution = 'europeana'">
                <xsl:text>/full/!400,/0/native.jpg</xsl:text>
            </xsl:when>
            <xsl:when test="$resolution = 'full'">
                <xsl:text>/full/full/0/native.jpg</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>/full/!250,/0/native.jpg</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:param>

    <xsl:param name="metadata_context" select="''"/>
    <xsl:param name="url_prefix" select="''"/>
    <xsl:param name="internal_url_prefix" select="''"/>
    <xsl:param name="content_context" select="''"/>

    <xsl:output encoding="UTF-8"/>

    <xsl:template match="/">
        <add>
            <xsl:apply-templates select="//md:mods"/>
        </add>
    </xsl:template>

    <xsl:template match="md:mods">
        <doc>
            <xsl:variable name="glob_id">
                <xsl:for-each select="md:recordInfo/md:recordIdentifier">
                    <xsl:value-of select="substring-after(.,$metadata_context)"/>
                </xsl:for-each>
            </xsl:variable>


            <xsl:call-template name="extract">
                <xsl:with-param name="local_id">
                    <xsl:value-of select="$glob_id"/>
                </xsl:with-param>
            </xsl:call-template>

            <xsl:apply-templates select="//processing-instruction()"/>

        </doc>
    </xsl:template>


    <xsl:template name="extract">

        <xsl:param name="local_id"/>

        <xsl:variable name="processedMods" select="."/>

        <xsl:variable name="lang" select="md:recordInfo/md:languageOfCataloging/md:languageTerm"/>

        <xsl:variable name="collection">
            <xsl:for-each select="md:recordInfo/md:recordIdentifier">
                <xsl:call-template name="my_identifier">
                    <xsl:with-param name="par">
                        <xsl:value-of select="substring-before(.,'/object')"/>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:for-each>
        </xsl:variable>

        <xsl:element name="field">
            <xsl:attribute name="name">id</xsl:attribute>
            <xsl:value-of select="$local_id"/>
        </xsl:element>

        <xsl:for-each select="md:genre">
            <xsl:variable name="language"><xsl:value-of select="./@xml:lang"/></xsl:variable>
            <xsl:if test="contains(.,'Skråfoto') or contains(.,'Lodfoto') or contains(.,'Protokolside')">
                <xsl:element name="field">
                    <xsl:attribute name="name">luftfo_type_ssim</xsl:attribute>
                    <xsl:value-of select="."/>
                </xsl:element>
                <xsl:element name="field">
                    <xsl:attribute name="name">luftfo_type_tdsim</xsl:attribute>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:if>
            <xsl:element name="field">
                <xsl:attribute name="name">dc_type_ssim</xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
            <xsl:element name="field">
                <xsl:attribute name="name">dc_type_t<xsl:call-template name="string_lang">
                    <xsl:with-param name="language" select="$language"/>
                </xsl:call-template>sim</xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>

        <xsl:element name="field">
            <xsl:attribute name="name">cataloging_language_ssi</xsl:attribute>
            <xsl:value-of select="$lang"/>
        </xsl:element>
        <xsl:element name="field">
            <xsl:attribute name="name">full_title_tsi</xsl:attribute>
            <xsl:for-each select="md:titleInfo">
                <xsl:for-each select="md:title">
                    <xsl:value-of select="normalize-space(.)"/>
                    <xsl:text>
                        </xsl:text>
                </xsl:for-each>
            </xsl:for-each>
        </xsl:element>

        <xsl:for-each select="md:titleInfo">
            <xsl:variable name="language"><xsl:value-of select="./@xml:lang"/></xsl:variable>
            <xsl:element name="field">
                <xsl:attribute name="name">title_t<xsl:call-template name="string_lang">
                    <xsl:with-param name="language" select="$language"/>
                </xsl:call-template>sim</xsl:attribute>
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:for-each>

        <xsl:element name="field">
            <xsl:attribute name="name">author_tsim</xsl:attribute>
            <xsl:if test="position() &lt; last()">
                <xsl:text>; </xsl:text>
            </xsl:if>
            <xsl:for-each select="md:name[md:role/md:roleTerm[@type='text']='creator'
                                            or md:role/md:roleTerm[@type='text']='cre'
                                            or md:role/md:roleTerm[@type='text']='author']">
                <xsl:call-template name="name"/>
                <xsl:if test="position() &lt; last()">
                    <xsl:text>; </xsl:text>
                </xsl:if>
            </xsl:for-each>
        </xsl:element>

        <xsl:element name="field">
            <xsl:attribute name="name">author_nasim</xsl:attribute>
            <xsl:if test="position() &lt; last()">
                <xsl:text>; </xsl:text>
            </xsl:if>
            <xsl:for-each select="md:name[md:role/md:roleTerm[@type='text']='creator'
                                            or md:role/md:roleTerm[@type='text']='cre'
                                            or md:role/md:roleTerm[@type='text']='author']">
                <xsl:call-template name="name"/>
                <xsl:if test="position() &lt; last()">
                    <xsl:text>; </xsl:text>
                </xsl:if>
            </xsl:for-each>
        </xsl:element>

        <xsl:variable name="rest_url" select="substring-after($local_id, '/')"/>
        <xsl:variable name="first" select="substring-before($rest_url, '/')"/>
        <xsl:element name="field">
            <xsl:attribute name="name">medium_ssi</xsl:attribute>
            <xsl:value-of select="$first"/>
        </xsl:element>

        <xsl:for-each select="md:name[md:role/md:roleTerm[@type='text']='creator' or
		                    md:role/md:roleTerm[@type='code']='cre' or
		                    md:role/md:roleTerm[@type='code']='aut']">
            <xsl:if test="position()=1">
                <xsl:element name="field">
                    <xsl:attribute name="name">creator_ssi</xsl:attribute>
                    <xsl:call-template name="name"/>
                </xsl:element>
            </xsl:if>
            <xsl:element name="field">
                <xsl:attribute name="name">creator_display_tsim</xsl:attribute>
                <xsl:choose>
                    <xsl:when test="md:displayForm"><xsl:value-of select="md:displayForm"/></xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="md:namePart[@type='family']"/>
                        <xsl:if test="md:namePart[@type='given']">
                            <xsl:text>, </xsl:text>
                            <xsl:value-of select="md:namePart[@type='given']"/>
                        </xsl:if>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:element>
            <xsl:element name="field">
                <xsl:attribute name="name">creator_tsim</xsl:attribute>
                <xsl:value-of select="md:namePart[@type='family']"/>
                <xsl:if test="md:namePart[@type='given']">
                    <xsl:text>, </xsl:text>
                    <xsl:value-of select="md:namePart[@type='given']"/>
                </xsl:if>
            </xsl:element>
            <xsl:element name="field">
                <xsl:attribute name="name">creator_nasim</xsl:attribute>
                <xsl:value-of select="md:namePart[@type='family']"/>
                <xsl:if test="md:namePart[@type='given']">
                    <xsl:text>, </xsl:text>
                    <xsl:value-of select="md:namePart[@type='given']"/>
                </xsl:if>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:name[md:role/md:roleTerm[@type='text']!='creator' and
		                    md:role/md:roleTerm[@type='code']!='cre' and
		                    md:role/md:roleTerm[@type='code']!='aut']">
            <xsl:if test="position()=1">
                <xsl:element name="field">
                    <xsl:attribute name="name">contributor_ssi</xsl:attribute>
                    <xsl:call-template name="name"/>
                </xsl:element>
            </xsl:if>
            <xsl:element name="field">
                <xsl:attribute name="name">contributor_tsim</xsl:attribute>
                <xsl:call-template name="name"/>
            </xsl:element>
            <xsl:element name="field">
                <xsl:attribute name="name">contributor_nasim</xsl:attribute>
                <xsl:call-template name="name"/>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:originInfo/md:publisher">
            <xsl:if test="position()=1">
                <xsl:element name="field">
                    <xsl:attribute name="name">publisher_ssi
                    </xsl:attribute>
                    <xsl:call-template name="name"/>
                </xsl:element>
            </xsl:if>
            <xsl:element name="field">
                <xsl:attribute name="name">publisher_tsim</xsl:attribute>
                <xsl:call-template name="name"/>
            </xsl:element>
            <xsl:element name="field">
                <xsl:attribute name="name">publisher_nasim</xsl:attribute>
                <xsl:value-of select="."></xsl:value-of>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:abstract|
		       md:tableOfContents |
		       md:note">
            <xsl:if test="./text()">
                <xsl:element name="field">
                    <xsl:attribute name="name">description_tsim</xsl:attribute>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>

        <xsl:for-each select="md:physicalDescription">
            <xsl:for-each select="md:extent | md:form | md:internetMediaType ">
                <xsl:element name="field">
                    <xsl:attribute name="name">format_tsim</xsl:attribute>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>
        </xsl:for-each>

        <xsl:for-each select="md:genre">
            <xsl:element name="field">
                <xsl:attribute name="name">type_tsim</xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:language/md:languageTerm">
            <xsl:element name="field">
                <xsl:attribute name="name">language_tsim</xsl:attribute>
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:originInfo/md:publisher">
            <xsl:element name="field">
                <xsl:attribute name="name">publisher_tsim</xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:accessCondition">
            <xsl:element name="field">
                <xsl:attribute name="name">rights_tsim</xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:subject[md:geographic]">
            <xsl:variable name="language"><xsl:value-of select="./@xml:lang"/></xsl:variable>
            <xsl:for-each select="md:geographic">
                <xsl:element name="field">
                    <xsl:attribute name="name">coverage_t<xsl:call-template name="string_lang">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>sim</xsl:attribute>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>
        </xsl:for-each>

        <xsl:for-each select="md:hierarchicalGeographic">
            <xsl:for-each select="md:continent |
                    md:country   |
                    md:provence  |
                    md:region    |
                    md:state     |
                    md:territory |
                    md:county    |
                    md:city      |
                    md:island    |
                    md:area">
                <xsl:variable name="language"><xsl:value-of select="./@xml:lang"/></xsl:variable>
                <xsl:element name="field">
                    <xsl:attribute name="name">coverage_t<xsl:call-template name="string_lang">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>sim
                    </xsl:attribute>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>
        </xsl:for-each>

        <xsl:for-each select="md:subject/md:topic">
            <xsl:element name="field">
                <xsl:attribute name="name">subject_tsim</xsl:attribute>
                <xsl:value-of select="."/>
                <xsl:if test="position()!=last()">--</xsl:if>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:subject/md:occupation">
            <xsl:variable name="language"><xsl:value-of select="./@xml:lang"/></xsl:variable>
            <xsl:element name="field">
                <xsl:attribute name="name">subject_t<xsl:call-template
                            name="string_lang">
                        <xsl:with-param name="language" select="$language"/>
                    </xsl:call-template>sim</xsl:attribute>
                <xsl:value-of select="."/>
                <xsl:if test="position()!=last()">--</xsl:if>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:subject/md:name">
            <xsl:variable name="language"><xsl:value-of select="./@xml:lang"/></xsl:variable>
            <xsl:variable name="name"><xsl:call-template name="name"/></xsl:variable>
            <xsl:element name="field">
                <xsl:attribute name="name">subject_t<xsl:call-template
                            name="string_lang">
                        <xsl:with-param name="language" select="$language"/>
                    </xsl:call-template>sim</xsl:attribute>
                <xsl:value-of select="$name"/>
            </xsl:element>
            <xsl:choose>
                <xsl:when test="md:displayForm"><xsl:value-of select="md:displayForm"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="$name"/></xsl:otherwise>
            </xsl:choose>
            <xsl:for-each select="md:displayForm">
                <xsl:element name="field">
                    <xsl:attribute name="name">
                        subject_displayForm_tsim
                    </xsl:attribute>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>
        </xsl:for-each>

        <xsl:for-each select="md:typeOfResource">
            <xsl:element name="field">
                <xsl:attribute name="name">type_tsim</xsl:attribute>
                <xsl:if test="@collection='yes'">
                    <xsl:text>Collection</xsl:text>
                </xsl:if>
                <xsl:if test=". ='software' and ../md:genre='database'">
                    <xsl:text>DataSet</xsl:text>
                </xsl:if>
                <xsl:if test=".='software' and ../md:genre='online system or service'">
                    <xsl:text>Service</xsl:text>
                </xsl:if>
                <xsl:if test=".='software'">
                    <xsl:text>Software</xsl:text>
                </xsl:if>
                <xsl:if test=".='cartographic material'">
                    <xsl:text>Image</xsl:text>
                </xsl:if>
                <xsl:if test=".='multimedia'">
                    <xsl:text>InteractiveResource</xsl:text>
                </xsl:if>
                <xsl:if test=".='moving image'">
                    <xsl:text>MovingImage</xsl:text>
                </xsl:if>
                <xsl:if test=".='three-dimensional object'">
                    <xsl:text>PhysicalObject</xsl:text>
                </xsl:if>
                <xsl:if test="starts-with(.,'sound recording')">
                    <xsl:text>Sound</xsl:text>
                </xsl:if>
                <xsl:if test=".='still image'">
                    <xsl:text>StillImage</xsl:text>
                </xsl:if>
                <xsl:if test=". ='text'">
                    <xsl:text>Text</xsl:text>
                </xsl:if>
                <xsl:if test=".='notated music'">
                    <xsl:text>Text</xsl:text>
                </xsl:if>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:subject/md:cartographics/*[1]">
            <xsl:element name="field">
                <xsl:attribute name="name">dcterms_spatial</xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:originInfo/md:dateIssued  |
		                      md:originInfo/md:dateCreated |
		                      md:originInfo/md:dateCaptured |
		                      md:originInfo/md:dateOther">
            <xsl:if test="position() = 1">
                <xsl:element name="field">
                    <xsl:attribute name="name">pub_dat_tsi</xsl:attribute>
                    <xsl:choose>
                        <xsl:when test="substring-before(.,'-')">
                            <xsl:value-of select="substring-before(.,'-')"/>
                        </xsl:when>
                        <xsl:when test="substring-after(.,'-')">
                            <xsl:value-of select="substring-after(.,'-')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="."/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:element>
                <xsl:element name="field">
                    <xsl:attribute name="name">pub_dat_display_tsi</xsl:attribute>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>

        <xsl:for-each select="md:originInfo/md:dateCreated">
            <xsl:element name="field">
                <xsl:attribute name="name">readable_dat_string_tsim</xsl:attribute>
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:originInfo/md:place/md:placeTerm">
            <xsl:element name="field">
                <xsl:attribute name="name">origin_place_tsim</xsl:attribute>
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:name/tei:residence">
            <xsl:element name="field">
                <xsl:attribute name="name">person_residence_tsim</xsl:attribute>
                <xsl:value-of select="tei:settlement"/>
                <xsl:text> (</xsl:text><xsl:value-of select="tei:country"/>)
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:identifier[@type='local'][1]">
            <xsl:element name="field">
                <xsl:attribute name="name">local_id_ssi</xsl:attribute>
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
            <xsl:element name="field">
                <xsl:attribute name="name">local_id_fngsi</xsl:attribute>
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:location/md:physicalLocation[@displayLabel='Shelf Mark']">
            <xsl:element name="field">
                <xsl:attribute name="name">shelf_mark_tdsim</xsl:attribute>
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="md:extension">
            <xsl:for-each select="h:div">

                <xsl:for-each select="h:a[not(contains(@href,'/editions/')) and @xml:lang='da']">
                    <xsl:element name="field">
                        <xsl:attribute name="name">subject_topic_id_ssim</xsl:attribute>
                        <xsl:value-of
                                select="concat($collection,'/subj',substring-after(substring-before(@href,'/da'),'subj'))"/>
                    </xsl:element>
                </xsl:for-each>

                <xsl:for-each select="h:a[not(contains(@href,'/editions/'))]">
                    <xsl:variable name="language"><xsl:value-of select="./@xml:lang"/></xsl:variable>
                    <xsl:element name="field">
                        <xsl:attribute name="name">subject_topic_facet_t<xsl:call-template name="string_lang">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>sim</xsl:attribute>
                        <xsl:value-of select="normalize-space(.)"/>
                    </xsl:element>
                </xsl:for-each>

            </xsl:for-each>
        </xsl:for-each>

        <xsl:apply-templates select="md:mods/processing-instruction()"/>

        <xsl:comment>This is mods directly out of oracle</xsl:comment>
        <xsl:element name="field">
            <xsl:attribute name="name">mods_ts</xsl:attribute>
            <xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
            <xsl:value-of select="$raw_mods" disable-output-escaping="yes"/>
            <xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
        </xsl:element>

        <xsl:comment>This is mods directly out of cop backend</xsl:comment>
        <xsl:element name="field">
            <xsl:attribute name="name">processed_mods_ts</xsl:attribute>
            <xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
            <xsl:copy-of select="$processedMods"/>
            <xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
        </xsl:element>

        <xsl:if test="md:physicalDescription/md:note[@type='pageOrientation']">
            <xsl:element name="field">
                <xsl:attribute name="name">read_direction_ssi</xsl:attribute>
                <xsl:value-of select="md:physicalDescription/md:note[@type='pageOrientation']"/>
            </xsl:element>
        </xsl:if>

        <xsl:variable name="sort_direction">
            <xsl:choose>
                <xsl:when test="contains(md:physicalDescription/md:note[@type='pageOrientation'],'RTL')">
                    descending
                </xsl:when>
                <xsl:otherwise>ascending</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:comment>
            <xsl:value-of select="$sort_direction"/>
        </xsl:comment>

        <xsl:for-each select="md:relatedItem[not(@type='original') and md:identifier]">
            <xsl:call-template name="make_page_field"/>
            <xsl:for-each select=".//md:relatedItem[@type='constituent' and md:identifier]">
                <xsl:sort order="{$sort_direction}"
                          data-type="number"
                          select="count(preceding::md:relatedItem[md:identifier])"/>
                <xsl:call-template name="make_page_field"/>
            </xsl:for-each>
        </xsl:for-each>

        <xsl:for-each select="md:identifier[@displayLabel='image']">
            <xsl:element name="field">
                <xsl:attribute name="name">full_image_url_ssm</xsl:attribute>
                <xsl:choose>
                    <xsl:when test="contains(.,'www.kb.dk/imageService')">
                        <xsl:value-of select="concat('http://kb-images.kb.dk',
				  substring-after(substring-before(.,'.jpg'),'imageService'),
				  $full_iiif_scaling)"/>
                    </xsl:when>
                    <xsl:when test="contains(.,'full/!400')">
                        <xsl:value-of select="concat(substring-before(.,'/full/full'),$full_iiif_scaling)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="normalize-space(.)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:element>

            <xsl:element name="field">
                <xsl:attribute name="name">thumbnail_square_url_ssm</xsl:attribute>
                <xsl:choose>
                    <xsl:when test="contains(.,'www.kb.dk/imageService')">
                        <xsl:value-of select="concat('http://kb-images.kb.dk',
				  substring-after(substring-before(.,'.jpg'),'imageService'),
				  $iiif_square_thumb_nail)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="normalize-space(.)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:element>

            <xsl:element name="field">
                <xsl:attribute name="name">mods_uri_ssim</xsl:attribute>
                <xsl:value-of select="concat($url_prefix,$local_id,'?format=mods')"/>
            </xsl:element>

            <xsl:element name="field">
                <xsl:attribute name="name">thumbnail_url_ssm</xsl:attribute>
                <xsl:choose>
                    <xsl:when test="contains(.,'www.kb.dk/imageService')">
                        <xsl:value-of select="concat('http://kb-images.kb.dk',
				  substring-after(substring-before(.,'.jpg'),'imageService'),
				  $iiif_thumb_nail)"/>
                    </xsl:when>
                    <xsl:when test="contains(.,'full/!250')">
                        <xsl:value-of select="concat(substring-before(.,'/full/!250'),$iiif_thumb_nail)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat(substring-before(.,'/full/full'),$iiif_thumb_nail)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:element>

        </xsl:for-each>

        <!-- some Europeana stuff don't know if it is used anymore-->

        <!-- we have difficulties with videos and audios -->
        <field name="ese_type_tsim">
            <xsl:choose>
                <xsl:when test="contains(md:recordInfo/md:recordIdentifier,'images')">IMAGE</xsl:when>
                <xsl:when test="contains(md:recordInfo/md:recordIdentifier,'maps')">IMAGE</xsl:when>
                <xsl:otherwise>TEXT</xsl:otherwise>
            </xsl:choose>
        </field>
        <field name="ese_rights_tsim">http://creativecommons.org/licenses/by-nc-nd/4.0/</field>
        <field name="ese_dataProvider_tsim">The Royal Library: The National Library of Denmark and Copenhagen University Library</field>
        <field name="ese_isShownBy_tsim">
            <xsl:value-of select="md:identifier[@displayLabel='image']"/>
        </field>
        <field name="ese_isShownAt_tsim">
            <xsl:choose>
                <xsl:when test="contains(md:recordInfo/md:recordIdentifier,'/images/luftfo')">
                    <xsl:value-of select="concat(md:recordInfo/md:recordIdentifier,'/')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat(md:recordInfo/md:recordIdentifier,'/en/')"/>
                </xsl:otherwise>
            </xsl:choose>
        </field>
    </xsl:template>

    <xsl:template name="make_page_field">
        <xsl:element name="field">
            <xsl:attribute name="name">content_metadata_image_iiif_info_ssm</xsl:attribute>
            <xsl:choose>
                <xsl:when test="./md:identifier[@displayLabel='iiif']">
                    <xsl:value-of select="./md:identifier[@displayLabel='iiif']"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="img">
                        <xsl:choose>
                            <xsl:when test="contains(./md:identifier,'.tif')">
                                <xsl:value-of select="substring-before(./md:identifier,'.tif')"/>
                            </xsl:when>
                            <xsl:when test="contains(./md:identifier,'.TIF')">
                                <xsl:value-of select="substring-before(./md:identifier,'.TIF')"/>
                            </xsl:when>
                            <xsl:when test="contains(./md:identifier,'.jp2')">
                                <xsl:value-of select="substring-before(./md:identifier,'.jp2')"/>
                            </xsl:when>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:choose>
                        <xsl:when test="contains(./md:identifier,'http:')">
                            <xsl:value-of select="concat($img,'/info.json')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat('http://kb-images.kb.dk/',$img,'/info.json')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>

    <xsl:template name="name">
        <xsl:variable name="name">
            <xsl:for-each select="md:namePart[not(@type)]">
                <xsl:value-of select="."/>
                <xsl:text> </xsl:text>
            </xsl:for-each>
            <xsl:value-of select="md:namePart[@type='family']"/>
            <xsl:if test="md:namePart[@type='given']">
                <xsl:text>, </xsl:text>
                <xsl:value-of select="md:namePart[@type='given']"/>
            </xsl:if>
            <xsl:if test="md:namePart[@type='date']">
                <xsl:text>, </xsl:text>
                <xsl:value-of select="md:namePart[@type='date']"/>
                <xsl:text/>
            </xsl:if>
            <xsl:for-each select="md:role[md:roleTerm[@type='text']!='creator']">
                <xsl:text> (</xsl:text>
                <xsl:value-of select="normalize-space(.)"/>
                <xsl:text>) </xsl:text>
            </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="normalize-space($name)"/>
    </xsl:template>

    <xsl:template name="my_identifier">

        <xsl:param name="par" select="''"/>

        <xsl:variable name="my_id">
            <xsl:for-each select="$medias//li">
                <xsl:if test="contains($par,.)">
                    <xsl:value-of select="concat('/',.,substring-after(normalize-space($par),.))"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="substring($my_id,string-length($my_id),1) = '/'">
                <xsl:value-of select="substring($my_id,1,string-length($my_id)-1)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$my_id"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="string_lang">
        <xsl:param name="language"/>
        <xsl:choose>
            <xsl:when test="contains($language,'en')">e</xsl:when>
            <xsl:when test="contains($language,'da')">d</xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="''"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="processing-instruction('cobject_id')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_id</xsl:with-param>
            <xsl:with-param name="type">ssi</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('cobject_type')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_type</xsl:with-param>
            <xsl:with-param name="type">ssi</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('cobject_title')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_title</xsl:with-param>
            <xsl:with-param name="type">ssi</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('cobject_edition')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_edition</xsl:with-param>
            <xsl:with-param name="type">ssi</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('cobject_random_number')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_random_number</xsl:with-param>
            <xsl:with-param name="type">dbsi</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('cobject_not_before')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_not_before</xsl:with-param>
            <xsl:with-param name="type">dtsi</xsl:with-param>
            <xsl:with-param name="suffix">T23:59:59Z</xsl:with-param>
        </xsl:call-template>
    </xsl:template>


    <xsl:template match="processing-instruction('cobject_not_after')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_not_after</xsl:with-param>
            <xsl:with-param name="type">dtsi</xsl:with-param>
            <xsl:with-param name="suffix">T23:59:59Z</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('cobject_building')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_building</xsl:with-param>
            <xsl:with-param name="type">ssim</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_building</xsl:with-param>
            <xsl:with-param name="type">tsim</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('cobject_location')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_location</xsl:with-param>
            <xsl:with-param name="type">ssim</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_location</xsl:with-param>
            <xsl:with-param name="type">tsim</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_location</xsl:with-param>
            <xsl:with-param name="type">tsi</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('cobject_correctness')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_correctness</xsl:with-param>
            <xsl:with-param name="type">isi</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('cobject_interestingness')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_interestingness</xsl:with-param>
            <xsl:with-param name="type">isi</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('cobject_person')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_person</xsl:with-param>
            <xsl:with-param name="type">ssim</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_person</xsl:with-param>
            <xsl:with-param name="type">tsim</xsl:with-param>
        </xsl:call-template>
        <!-- There are regularly more than one person per record.
         This isn't the place to implement population regulation -->
        <!-- xsl:call-template name="make_cobject_field">
          <xsl:with-param name="name">cobject_person</xsl:with-param>
          <xsl:with-param name="type">tsi</xsl:with-param>
        </xsl:call-template -->
    </xsl:template>

    <xsl:template match="processing-instruction('cobject_annotation_comments')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_annotation_comments</xsl:with-param>
            <xsl:with-param name="type">tds</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('cumulus_catalog')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cumulus_catalog</xsl:with-param>
            <xsl:with-param name="type">ssi</xsl:with-param>
        </xsl:call-template>
    </xsl:template>


    <xsl:template match="processing-instruction('cobject_last_modified')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_last_modified</xsl:with-param>
            <xsl:with-param name="type">lsi</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('cobject_last_modified_by')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">cobject_last_modified_by</xsl:with-param>
            <xsl:with-param name="type">ssi</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('ccs_ready')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">ccs_ready</xsl:with-param>
            <xsl:with-param name="type">bsi</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('area_area')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">area_area</xsl:with-param>
            <xsl:with-param name="type">tsim</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('area_cadastre')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">area_cadastre</xsl:with-param>
            <xsl:with-param name="type">tsim</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('area_parish')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">area_parish</xsl:with-param>
            <xsl:with-param name="type">tsim</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('area_building')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">area_building</xsl:with-param>
            <xsl:with-param name="type">tsim</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('citySection_zipcode')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">citySection_zipcode</xsl:with-param>
            <xsl:with-param name="type">tsim</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('citySection_housenumber')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">citySection_housenumber</xsl:with-param>
            <xsl:with-param name="type">tsim</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="processing-instruction('citySection_street')">
        <xsl:call-template name="make_cobject_field">
            <xsl:with-param name="name">citySection_street</xsl:with-param>
            <xsl:with-param name="type">tsim</xsl:with-param>
        </xsl:call-template>
    </xsl:template>


    <xsl:template name="make_cobject_field">
        <xsl:param name="name" select="'noname'"/>
        <xsl:param name="type" select="'tdsim'"/>
        <xsl:param name="suffix" select="''"/>
        <xsl:element name="field">
            <xsl:attribute name="name"><xsl:value-of select="$name"/>_<xsl:value-of select="$type"/>
            </xsl:attribute>
            <xsl:value-of select="."/><xsl:value-of select="$suffix"/>
        </xsl:element>
    </xsl:template>

</xsl:transform>
