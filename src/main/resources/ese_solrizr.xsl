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

  
  <xsl:param name="resolution"><xsl:text>europeana</xsl:text></xsl:param>
  <xsl:param name="iiif_thumb_nail"><xsl:text>/full/!400,/0/native.jpg</xsl:text></xsl:param>
  <xsl:param name="iiif_square_thumb_nail"><xsl:text>/100,100,1000,1000/!100,100/0/native.jpg</xsl:text></xsl:param>
  <xsl:param name="raw_mods" select="''"/>
  <xsl:param name="spotlight_exhibition" select="''"/>
  <xsl:param name="comments" select ="''" />


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

  <xsl:param name="full_iiif_scaling"><xsl:text>/full/full/0/native.jpg</xsl:text></xsl:param>

  <xsl:param name="iiif_scaling">
    <xsl:choose>
      <xsl:when test="$resolution = 'europeana'"><xsl:text>/full/!400,/0/native.jpg</xsl:text></xsl:when>
      <xsl:when test="$resolution = 'full'"><xsl:text>/full/full/0/native.jpg</xsl:text></xsl:when>
      <xsl:otherwise><xsl:text>/full/!250,/0/native.jpg</xsl:text></xsl:otherwise>
    </xsl:choose>
  </xsl:param>

  <xsl:param name="metadata_context" select="''" />
  <xsl:param name="url_prefix" select="''" />
  <xsl:param name="internal_url_prefix" select="''" />
  <xsl:param name="content_context" select="''" />

  <xsl:output encoding="UTF-8"/>

  <xsl:template match="/">
    <add>
      <xsl:apply-templates select="//ese:record"/>
    </add> 
  </xsl:template>

  <xsl:template match="ese:record">
    <doc>
      <xsl:variable name="glob_id">
	<xsl:for-each select="//ese:isShownAt">
	  <xsl:call-template name="my_identifier">
	    <xsl:with-param name="par" select="."/>
	  </xsl:call-template>
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
    
    <xsl:variable name="mods" select="document(concat($internal_url_prefix,$local_id,'?format=mods'))"/>

    <xsl:variable name="lang" select="$mods//md:languageOfCataloging/md:languageTerm"/>

    <xsl:variable name="collection">
      <xsl:for-each select="$mods//md:mods/md:recordInfo/md:recordIdentifier">
	<xsl:call-template name="my_identifier">
	  <xsl:with-param name="par">
	    <xsl:value-of select="substring-before(.,'/object')"/>
	  </xsl:with-param>
	</xsl:call-template>
      </xsl:for-each>
    </xsl:variable>

    <xsl:for-each select="$mods//md:mods/md:recordInfo/md:recordIdentifier">
      <xsl:element name="field">
	<xsl:attribute name="name">id</xsl:attribute>
	<xsl:call-template name="my_identifier">
	  <xsl:with-param name="par" select="."/>
	</xsl:call-template>
      </xsl:element>
    </xsl:for-each>

    <xsl:for-each select="$mods//md:mods/md:genre">
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
	<xsl:attribute name="name">dc_type_t<xsl:call-template name="string_lang"/>sim</xsl:attribute>
	<xsl:value-of select="."/>
      </xsl:element>
    </xsl:for-each>

    <xsl:element name="field">
      <xsl:attribute name="name">cataloging_language_ssi</xsl:attribute>
      <xsl:value-of select="$lang"/>
    </xsl:element>

    <xsl:element name="field">
      <xsl:attribute name="name">full_title_tsi</xsl:attribute>
      <xsl:for-each select="$mods//md:mods">
	<xsl:for-each select="md:titleInfo">
	  <xsl:for-each select="md:title">
	    <xsl:value-of select="normalize-space(.)"/>
	    <xsl:text>
	    </xsl:text>
	  </xsl:for-each>
	</xsl:for-each>
      </xsl:for-each>
    </xsl:element>

    <xsl:for-each select="$mods//md:mods/md:titleInfo">
      <xsl:element name="field">
	<xsl:attribute name="name">title_t<xsl:call-template name="string_lang"/>sim</xsl:attribute>
	<xsl:value-of select="normalize-space(.)"/>
      </xsl:element>
    </xsl:for-each>

    <xsl:element name="field">
      <xsl:attribute name="name">author_tsim</xsl:attribute><xsl:if test="position() &lt; last()"><xsl:text>; </xsl:text></xsl:if>
      <xsl:for-each select="dc:creator">
	<xsl:value-of select="."/><xsl:if test="position() &lt; last()"><xsl:text>; </xsl:text></xsl:if>
      </xsl:for-each>
    </xsl:element>

    <xsl:element name="field">
        <xsl:attribute name="name">author_nasim</xsl:attribute><xsl:if test="position() &lt; last()"><xsl:text>; </xsl:text></xsl:if>
        <xsl:for-each select="dc:creator">
            <xsl:value-of select="."/><xsl:if test="position() &lt; last()"><xsl:text>; </xsl:text></xsl:if>
        </xsl:for-each>
    </xsl:element>

    <xsl:variable name="rest_url" select="substring-after($local_id, '/')" />
    <xsl:variable name="first" select="substring-before($rest_url, '/')" />
    <xsl:element name="field">
        <xsl:attribute name="name">medium_ssi</xsl:attribute>
        <xsl:value-of select="$first"/>
    </xsl:element>

    <xsl:for-each select="dc:creator|dc:description|dc:format|dc:type|dc:language|dc:contributor|dc:publisher|dc:rights|dc:coverage|dc:subject">
      <xsl:if test="./text()">
	<xsl:element name="field">
	  <xsl:attribute name="name"><xsl:value-of select="local-name(.)"/>_t<xsl:call-template name="string_lang"/>sim</xsl:attribute>
	  <xsl:value-of select="."/>
	</xsl:element>
      </xsl:if>
    </xsl:for-each>

    <xsl:for-each select="dc:creator|dc:contributor|dc:publisher">
      <xsl:if test="./text()">
	<xsl:element name="field">
	  <xsl:attribute name="name"><xsl:value-of select="local-name(.)"/>_nasim</xsl:attribute>
	  <xsl:value-of select="."/>
	</xsl:element>
      </xsl:if>
    </xsl:for-each>

    <xsl:for-each select="dc:creator[1]|dc:contributor[1]|dc:publisher[1]">
      <xsl:if test="./text()">
	<xsl:element name="field">
	  <xsl:attribute name="name"><xsl:value-of select="local-name(.)"/>_ssi</xsl:attribute>
	  <xsl:value-of select="."/>
	</xsl:element>
      </xsl:if>
    </xsl:for-each>

    <xsl:for-each select="dcterms:spatial[1]">
      <xsl:element name="field">
	<xsl:attribute name="name">dcterms_spatial</xsl:attribute>
	<xsl:value-of select="normalize-space(.)"/>
      </xsl:element>
    </xsl:for-each>

    <xsl:for-each select="dc:date[number(.) = number(.)][1]">
      <xsl:element name="field">
	<xsl:attribute name="name">pub_dat_tsi</xsl:attribute>
	<xsl:value-of select="number(.)" /> <!-- concat(normalize-space(.),'-12-31T23:59:59Z')"/ -->
      </xsl:element>
    </xsl:for-each>

    <xsl:for-each select="$mods//md:mods/md:originInfo/md:dateCreated">
      <xsl:element name="field">
	<xsl:attribute name="name">readable_dat_string_tsim</xsl:attribute>
	<xsl:value-of select="normalize-space(.)"/>
      </xsl:element>
    </xsl:for-each>

    <xsl:for-each select="$mods//md:mods/md:originInfo/md:place/md:placeTerm">
      <xsl:element name="field">
	<xsl:attribute name="name">origin_place_tsim</xsl:attribute>
	<xsl:value-of select="normalize-space(.)"/>
      </xsl:element>
    </xsl:for-each>

    <xsl:for-each select="$mods//md:mods/md:name/tei:residence">
      <xsl:element name="field">
	<xsl:attribute name="name">person_residence_tsim</xsl:attribute>
	<xsl:value-of select="tei:settlement"/>
	<xsl:text> (</xsl:text><xsl:value-of select="tei:country"/>)
      </xsl:element>
    </xsl:for-each>

    <xsl:for-each select="$mods//md:mods/md:identifier[@type='local'][1]">
      <xsl:element name="field">
	<xsl:attribute name="name">local_id_ssi</xsl:attribute>
	<xsl:value-of select="normalize-space(.)"/>
      </xsl:element>
      <xsl:element name="field">
        <xsl:attribute name="name">local_id_fngsi</xsl:attribute>
        <xsl:value-of select="normalize-space(.)"/>
      </xsl:element>
    </xsl:for-each>

    <xsl:for-each select="$mods//md:location/md:physicalLocation[@displayLabel='Shelf Mark']">
      <xsl:element name="field">
	<xsl:attribute name="name">shelf_mark_tdsim</xsl:attribute>
	<xsl:value-of select="normalize-space(.)"/>
      </xsl:element>
    </xsl:for-each>

    <xsl:for-each select="$mods//md:mods/md:extension">
      <xsl:for-each  select="h:div">

	<xsl:for-each  select="h:a[not(contains(@href,'/editions/')) and @xml:lang='da']">
	  <xsl:element name="field">
	    <xsl:attribute name="name">subject_topic_id_ssim</xsl:attribute>
	    <xsl:value-of select="concat($collection,'/subj',substring-after(substring-before(@href,'/da'),'subj'))"/>
	  </xsl:element>
	</xsl:for-each>

	<xsl:for-each  select="h:a[not(contains(@href,'/editions/'))]">
	  <xsl:element name="field">
	    <xsl:attribute name="name">subject_topic_facet_t<xsl:call-template name="string_lang"/>sim</xsl:attribute>
	    <xsl:value-of select="normalize-space(.)"/>
	  </xsl:element>
	</xsl:for-each>

      </xsl:for-each>
    </xsl:for-each>

    <xsl:apply-templates select="$mods//md:mods/processing-instruction()"/>

    <xsl:for-each select="ese:type|ese:rights|ese:dataProvider">
      <xsl:element name="field">
	<xsl:attribute name="name">ese_<xsl:value-of select="local-name(.)"/>_t<xsl:call-template name="string_lang"/>sim</xsl:attribute>
	<xsl:value-of select="normalize-space(.)"/>
      </xsl:element>
    </xsl:for-each>

    <xsl:for-each select="ese:isShownBy">
      <xsl:element name="field">
	<xsl:attribute name="name">ese_<xsl:value-of select="local-name(.)"/>_t<xsl:call-template name="string_lang"/>sim</xsl:attribute>
	<xsl:choose>
	  <xsl:when test="contains(.,'www.kb.dk/imageService')">
	    <xsl:value-of select="concat('http://kb-images.kb.dk',
				  substring-after(substring-before(.,'.jpg'),'imageService'),
				  $iiif_scaling)"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:value-of select="normalize-space(.)"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:element>
    </xsl:for-each>

    <xsl:for-each select="ese:isShownBy">

<!--  
"full_image_url_ssm":["http://kb-images.kb.dk/online_master_arkiv_6/non-archival/Maps/KORTSA/FR5_ATLAS/fr5_bind49/fr549057/full/full/0/native.jpg"]

"thumbnail_url_ssm":["http://kb-images.kb.dk/online_master_arkiv_6/non-archival/Maps/KORTSA/FR5_ATLAS/fr5_bind49/fr549057/full/!400,/0/native.jpg"]
-->


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
    </xsl:for-each>

    <xsl:if test="$url_prefix and $local_id">
      <xsl:element name="field">
	<xsl:attribute name="name">ese_isShownAt_tsim</xsl:attribute>
	<xsl:value-of select="$local_id"/>
      </xsl:element>
    </xsl:if>

    <xsl:for-each select="ese:isShownAt">
      <xsl:element name="field">
	<xsl:attribute name="name">mods_uri_ssim</xsl:attribute>
	<xsl:value-of select="concat($url_prefix,$local_id,'?format=mods')"/>
      </xsl:element>
    </xsl:for-each>

    <xsl:comment>This is mods directly out of oracle</xsl:comment>
    <xsl:element name="field">
      <xsl:attribute name="name">mods_ts</xsl:attribute>
      <xsl:text disable-output-escaping="yes" >&lt;![CDATA[</xsl:text>
        <xsl:value-of select="$raw_mods" disable-output-escaping="yes"/>
      <xsl:text disable-output-escaping="yes" >]]&gt;</xsl:text>
    </xsl:element>

    <xsl:comment>This is mods directly out of cop backend</xsl:comment>
    <xsl:element name="field">
      <xsl:attribute name="name">processed_mods_ts</xsl:attribute>
      <xsl:text disable-output-escaping="yes" >&lt;![CDATA[</xsl:text>
      <xsl:copy-of select="$mods/md:modsCollection/md:mods" />
      <xsl:text disable-output-escaping="yes" >]]&gt;</xsl:text>
    </xsl:element>

    <xsl:if test="$mods//md:mods/md:physicalDescription/md:note[@type='pageOrientation']">
      <xsl:element name="field">
	<xsl:attribute name="name">read_direction_ssi</xsl:attribute>
	<xsl:value-of select="$mods//md:mods/md:physicalDescription/md:note[@type='pageOrientation']"/>
      </xsl:element>
    </xsl:if>

    <xsl:if test="$spotlight_exhibition">
      <xsl:element name="field">
	<xsl:attribute name="name">spotlight_exhibition_ssi</xsl:attribute>
	<xsl:value-of select="$spotlight_exhibition"/>
      </xsl:element>
    </xsl:if>

    <xsl:variable name="sort_direction">
      <xsl:choose>               
	<xsl:when test="contains($mods//md:mods/md:physicalDescription/md:note[@type='pageOrientation'],'RTL')">descending</xsl:when>
	<xsl:otherwise>ascending</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:comment>
      <xsl:value-of select="$sort_direction"/>
    </xsl:comment>

    <xsl:choose>
      <xsl:when test="$mods//md:mods/md:relatedItem[md:identifier]">
	<xsl:for-each select="$mods//md:mods/md:relatedItem[md:relatedItem[@type='constituent'] and md:identifier]">
	  <xsl:call-template name="make_page_field"/>
	  <xsl:for-each select=".//md:relatedItem[@type='constituent' and md:identifier]">
	    <xsl:sort order="{$sort_direction}" 
		      data-type="number"
		      select="count(preceding::md:relatedItem[md:identifier])"/>
	    <xsl:call-template name="make_page_field"/> 
	  </xsl:for-each>
	</xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
	<xsl:for-each select="ese:isShownBy">
	  <xsl:element name="field">
	    <xsl:attribute name="name">content_metadata_image_iiif_info_ssm</xsl:attribute>
	    <xsl:value-of select="concat('http://kb-images.kb.dk',
				  substring-after(substring-before(.,'.jpg'),'imageService'),
				  '/info.json')"/>
	  </xsl:element>
	</xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
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
    <xsl:choose>
      <xsl:when test="contains(@xml:lang,'en')">e</xsl:when>
      <xsl:when test="contains(@xml:lang,'da')">d</xsl:when>
      <xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
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
      <xsl:attribute name="name"><xsl:value-of select="$name"/>_<xsl:value-of select="$type"/></xsl:attribute>
      <xsl:value-of select="."/><xsl:value-of select="$suffix"/>
    </xsl:element>
  </xsl:template>

</xsl:transform>
