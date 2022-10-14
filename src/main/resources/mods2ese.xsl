<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:md="http://www.loc.gov/mods/v3"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
		xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exsl="http://exslt.org/common"
                exclude-result-prefixes="md exsl" >

  <!--
      This stylesheet transforms MODS version 3.2 records and collections of
      records to ESE records. It is based on the Library of Congress' MODS to
      simple DC mapping which has to be present in the same directory.

  -->

  <xsl:include href="mods2dc.xsl"/>

  <xsl:strip-space elements="*" />

  <xsl:output encoding="UTF-8"
	      indent="yes"
	      method="xml"/>

  <xsl:param name="url_prefix" select="''"/>

  <xsl:param name="english_image_formats">
    <properties>
      <entry key="Akvarel">painting</entry>
      <entry key="Dia">photograph</entry>
      <entry key="Digital optagelse">photograph</entry>
      <entry key="Grafik">graphics</entry>
      <entry key="Fotografi">photograph</entry>
      <entry key="Fotogravure">graphics</entry>
      <entry key="Negativ">photograph</entry>
      <entry key="Portkort">postcard</entry>
      <entry key="Plakat">poster</entry>
      <entry key="Tegning">drawing</entry>
      <entry key="Tryk">graphics</entry>
      <entry key="Silhuet">drawing</entry>
    </properties>
  </xsl:param>

  <xsl:param name="image_formats"
	     select="exsl:node-set($english_image_formats)"/>


  <xsl:template match="/">
    <xsl:for-each select="//md:mods">
      <xsl:call-template name="oai_record"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="oai_record">
    <xsl:variable name="cataloging_language">
      <xsl:value-of
	  select="md:recordInfo/md:languageOfCataloging/md:languageTerm"/>
    </xsl:variable>


    <record xmlns="http://www.europeana.eu/schemas/ese/"
	    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	    xmlns:dc="http://purl.org/dc/elements/1.1/"
	    xmlns:dcterms="http://purl.org/dc/terms/"
	    xmlns:europeana="http://www.europeana.eu/schemas/ese/"
	    xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
	    xsi:schemaLocation="http://www.europeana.eu/schemas/ese/
				http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd">

      <xsl:apply-templates>
	<xsl:with-param name="cataloging_language" select="$cataloging_language"/>
      </xsl:apply-templates>

      <xsl:if test="not(md:titleInfo)">
	<xsl:comment>Europeana hack - if no title exists we provide one</xsl:comment>
	<xsl:call-template name="insertTitleWhenBlank" />
      </xsl:if>

      <xsl:comment>europeana:object is for a low resolution rendition of the
      object. As of writing this code we thought that 450 px width was
      pretty low resolution.</xsl:comment>

      <europeana:object>
          <xsl:value-of select="md:identifier[@displayLabel='image']"/>
      </europeana:object>

      <europeana:provider>The Royal Library: The National Library of Denmark and Copenhagen University Library</europeana:provider>

      <!-- we have difficulties with videos and audios -->
      <europeana:type>
	<xsl:choose>
	  <xsl:when test="contains(md:recordInfo/md:recordIdentifier,'images')">IMAGE</xsl:when>
	  <xsl:when test="contains(md:recordInfo/md:recordIdentifier,'maps')">IMAGE</xsl:when>
	  <xsl:otherwise>TEXT</xsl:otherwise>
	</xsl:choose>
      </europeana:type>

      <europeana:rights>http://creativecommons.org/licenses/by-nc-nd/4.0/</europeana:rights>

      <europeana:dataProvider>The Royal Library: The National Library of Denmark and Copenhagen University Library</europeana:dataProvider>

      <xsl:comment>This is pretty high resolution.</xsl:comment>
      <europeana:isShownBy>
	<xsl:value-of select="md:identifier[@displayLabel='image']"/>
      </europeana:isShownBy>

      <!--
	  europeana:isShownAt contains the URL for the item in
	  its original context.  There are two versions in the
	  original - the Danish URL and the English;
	  the English version is used. -->

      <europeana:isShownAt>
	<xsl:choose>
	  <xsl:when test="contains(md:recordInfo/md:recordIdentifier,'/images/luftfo')">
	    <xsl:value-of select="concat($url_prefix,md:recordInfo/md:recordIdentifier,'/')"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:value-of select="concat($url_prefix,md:recordInfo/md:recordIdentifier,'/en/')"/>
	  </xsl:otherwise>
	</xsl:choose>
      </europeana:isShownAt>


    </record>
  </xsl:template>

  <xsl:template name="insertTitleWhenBlank">
    <dc:title>
      <xsl:choose>
	<xsl:when test="md:name/md:role/md:roleTerm[@type='text']='creator' or md:name/md:role/md:roleTerm[@type='code']='cre' ">
	  <xsl:value-of select="md:name/md:namePart" />
	</xsl:when>
      </xsl:choose>
      <xsl:choose>
	<xsl:when test="md:originInfo/md:dateCreated[@point='start']">
	  <xsl:text> - </xsl:text>
	  <xsl:value-of select="md:originInfo/md:dateCreated[@point='start']"/>
	  <xsl:text> - </xsl:text>
	</xsl:when>
	<xsl:when test="md:originInfo/md:dateCreated[@point='end']">
	  <xsl:value-of select="md:originInfo/md:dateCreated[@point='end']"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:text> - </xsl:text>
	  <xsl:value-of select="md:originInfo/md:dateCreated"/>
	</xsl:otherwise>
      </xsl:choose>
    </dc:title>
  </xsl:template>


  <xsl:template match="md:recordInfo">

    <!-- The dcterms doesn't support xml:lang attribute. It is a
         functional requirement of the COP, but it's OK for us
         to export only the English version -->

    <dc:identifier xsi:type="dcterms:URI">
      <xsl:choose>
	<xsl:when test="contains(md:recordIdentifier,'/images/luftfo')">
	  <xsl:value-of select="concat($url_prefix,md:recordIdentifier,'/')"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="concat($url_prefix,md:recordIdentifier,'/en/')"/>
	</xsl:otherwise>
      </xsl:choose>
    </dc:identifier>

  </xsl:template>

  <xsl:template name="add_lang">
    <xsl:param name="cataloging_language" select="'da'"/>
    <xsl:attribute name="xml:lang">
      <xsl:choose>
	<xsl:when test="string-length(@xml:lang) &gt; 0">
	  <xsl:value-of select="@xml:lang"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:choose>
	    <xsl:when test="string-length($cataloging_language) &gt; 0">
	      <xsl:value-of select="$cataloging_language"/>
	    </xsl:when>
	    <xsl:otherwise>da</xsl:otherwise>
	  </xsl:choose>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </xsl:template>


  <xsl:template match="md:dateIssued  |
		       md:dateCreated |
		       md:dateCaptured |
		       md:dateOther">
    <xsl:choose>
      <xsl:when test="contains(.,'-')">
	<xsl:if test="substring-before(.,'-')">
	  <dc:date>
	    <xsl:value-of select="substring-before(.,'-')"/>
	  </dc:date>
	</xsl:if>
	<xsl:if test="substring-after(.,'-')">
	  <dc:date>
	    <xsl:value-of select="substring-after(.,'-')"/>
	  </dc:date>
	</xsl:if>
      </xsl:when>
      <xsl:otherwise>
	<dc:date>
	  <xsl:value-of select="."/>
	</dc:date>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="md:subject[@valueURI]">
    <xsl:element name="dc:subject">
      <xsl:choose>
	<xsl:when test="md:topic">
	  <xsl:value-of select="md:topic"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="."/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:element>
    <xsl:element name="dc:subject">
      <xsl:value-of select="substring-before(substring-after(@valueURI,'subjects/'),'.html')"/>
    </xsl:element>
    <xsl:element name="dc:subject">
      <xsl:value-of select="@valueURI"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="md:subject[md:topic |
		       md:name  |
		       md:occupation | 
		       md:geographic | 
		       md:hierarchicalGeographic | 
		       md:cartographics | 
		       md:temporal] ">

    <xsl:param name="cataloging_language" select="'da'"/>

    <xsl:param name="language">
      <xsl:choose>
	<xsl:when test="@xml:lang/text()">
	  <xsl:value-of select="@xml:lang"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="$cataloging_language"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:param>

    <xsl:for-each select="md:topic">
      <dc:subject>
	<xsl:call-template name="add_lang">
	  <xsl:with-param name="cataloging_language" select="$language" />
	</xsl:call-template>
	<xsl:value-of select="."/>
      </dc:subject>
    </xsl:for-each>

    <xsl:for-each select="md:occupation">
      <dc:subject>
	<xsl:call-template name="add_lang">
	  <xsl:with-param name="cataloging_language" select="$language" />
	</xsl:call-template>
	<xsl:value-of select="."/>
      </dc:subject>
    </xsl:for-each>

    <xsl:for-each select="md:name">
      <xsl:processing-instruction name="cobject_person">
	<xsl:value-of select="." />
      </xsl:processing-instruction>
      <dc:subject>
	<xsl:call-template name="add_lang">
	  <xsl:with-param name="cataloging_language" select="$language" />
	</xsl:call-template>
	<xsl:value-of select="."/>
      </dc:subject>
    </xsl:for-each>

    <xsl:for-each select="md:titleInfo/md:title">
      <dc:subject>
	<xsl:call-template name="add_lang">
	  <xsl:with-param name="cataloging_language" select="$language" />
	</xsl:call-template>
	<xsl:value-of select="md:titleInfo/md:title"/>
      </dc:subject>
    </xsl:for-each>

    <xsl:comment>Luftfoto stuff</xsl:comment>
    <xsl:for-each select="md:hierarchicalGeographic">
      <xsl:for-each select="md:area[@areaType='area' and @displayLabel='lokalitet'] |
			    md:area[@areaType='cadastre' and @displayLabel='matrikelnummer'] |
			    md:area[@areaType='parish' and @displayLabel='sogn'] |
			    md:area[@areaType='building' and @displayLabel='Bygningsnavn'] |
			    md:citySection[@citySectionType='zipcode'] |
			    md:citySection[@citySectionType='housenumber'] |
			    md:citySection[@citySectionType='street']" >

	<xsl:processing-instruction name="{concat(local-name(.),'_',@areaType,@citySectionType)}">
	  <xsl:value-of select="." />
	</xsl:processing-instruction>
      </xsl:for-each>
    </xsl:for-each>

    <xsl:for-each select="md:geographic">
      <dc:coverage>
	<xsl:call-template name="add_lang">
	  <xsl:with-param name="cataloging_language" select="$language" />
	</xsl:call-template>
	<xsl:value-of select="."/>
      </dc:coverage>
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
	<dc:coverage>
	  <xsl:call-template name="add_lang">
	    <xsl:with-param name="cataloging_language" select="$language" />
	  </xsl:call-template>
	  <xsl:value-of select="."/>
	</dc:coverage>
      </xsl:for-each>
    </xsl:for-each>

    <xsl:for-each select="md:cartographics/*">
      <dcterms:spatial>
	<xsl:value-of select="."/>
      </dcterms:spatial>
    </xsl:for-each>

    <xsl:for-each select="md:temporal">
      <dc:coverage>
	<xsl:call-template name="add_lang">
	  <xsl:with-param name="cataloging_language" select="$language" />
	</xsl:call-template>
	<xsl:value-of select="."/>
      </dc:coverage>
    </xsl:for-each>

  </xsl:template>

  <xsl:template match="md:genre">
    <dc:type>
      <xsl:value-of select="."/>
    </dc:type>
  </xsl:template>

  <xsl:template match="md:typeOfResource">
    <xsl:if test="@collection='yes'">
      <dc:type>Collection</dc:type>
    </xsl:if>
    <xsl:choose>
      <xsl:when test=". ='software' and ../md:genre='database'">
	<dc:type>DataSet</dc:type>
      </xsl:when>
      <xsl:when test=".='software' and ../md:genre='online system or service'">
	<dc:type>Service</dc:type>
      </xsl:when>
      <xsl:when test=".='software'">
	<dc:type>Software</dc:type>
      </xsl:when>
      <xsl:when test=".='cartographic material'">
	<dc:type>Image</dc:type>
      </xsl:when>
      <xsl:when test=".='multimedia'">
	<dc:type>InteractiveResource</dc:type>
      </xsl:when>
      <xsl:when test=".='moving image'">
	<dc:type>MovingImage</dc:type>
      </xsl:when>
      <xsl:when test=".='three-dimensional object'">
	<dc:type>PhysicalObject</dc:type>
      </xsl:when>
      <xsl:when test="starts-with(.,'sound recording')">
	<dc:type>Sound</dc:type>
      </xsl:when>
      <xsl:when test=".='still image'">
	<dc:type>StillImage</dc:type>
      </xsl:when>
      <xsl:when test=". ='text'">
	<dc:type>Text</dc:type>
      </xsl:when>
      <xsl:when test=".='notated music'">
	<dc:type>Text</dc:type>
      </xsl:when>
      <xsl:otherwise>
	<dc:type><xsl:value-of select="."/></dc:type>
	<xsl:choose>
	  <xsl:when test=". = 'Kort'">
	    <dc:type>map</dc:type>
	  </xsl:when>
	</xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="md:physicalDescription">
    <xsl:param name="cataloging_language" select="'da'"/>

    <xsl:if test="md:extent">
      <dc:format>
	<xsl:value-of select="md:extent"/>
      </dc:format>
    </xsl:if>

    <xsl:for-each select="md:form">
      <xsl:choose>
	<xsl:when test="@type='technique'">
	  <xsl:variable name="type">
	    <xsl:value-of select="."/>
	  </xsl:variable>
	  <xsl:if test="$image_formats/properties/entry[@key=$type]">
	    <dc:type xml:lang="en">
	      <xsl:value-of select="$image_formats/properties/entry[@key=$type]"/>
	    </dc:type>
	  </xsl:if>
	  <dc:type>
	    <xsl:call-template name="add_lang">
	      <xsl:with-param name="cataloging_language" select="$cataloging_language" />
	    </xsl:call-template>
	    <xsl:value-of select="."/>
	  </dc:type>
	</xsl:when>
	<xsl:otherwise>
	  <dc:format>
	    <xsl:value-of select="."/>
	  </dc:format>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>

    <xsl:if test="md:internetMediaType">
      <dc:format>
	<xsl:value-of select="md:internetMediaType"/>
      </dc:format>
    </xsl:if>
  </xsl:template>

  <xsl:template match="md:identifier"/>

  <xsl:template match="md:relatedItem" />

</xsl:stylesheet>
