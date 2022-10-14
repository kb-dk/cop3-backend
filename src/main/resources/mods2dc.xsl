<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
		xmlns:md="http://www.loc.gov/mods/v3" 
		xmlns:dc="http://purl.org/dc/elements/1.1/" 
		xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		exclude-result-prefixes="md"	>

  <!-- 
       This stylesheet transforms MODS version 3.2 records and collections of
       records to simple Dublin Core (DC) records, based on the Library of
       Congress' MODS to simple DC mapping
       <http://www.loc.gov/standards/mods/mods-dcsimple.html>
		
       The stylesheet will transform a collection of MODS 3.2 records into
       simple Dublin Core (DC) as expressed by the SRU DC schema
       http://www.loc.gov/standards/sru/dc-schema.xsd

       The stylesheet will transform a single MODS 3.2 record into simple
       Dublin Core (DC) as expressed by the OAI DC schema
       http://www.openarchives.org/OAI/2.0/oai_dc.xsd
		
       Because MODS is more granular than DC, transforming a given MODS
       element or subelement to a DC element frequently results in less
       precise tagging, and local customizations of the stylesheet may be
       necessary to achieve desired results.

       This stylesheet makes the following decisions in its interpretation of
       the MODS to simple DC mapping:
	
       When the roleTerm value associated with a name is creator, then name
       maps to dc:creator.

       When there is no roleTerm value associated with name, or the roleTerm
       value associated with name is a value other than creator, then name
       maps to dc:contributor

       Start and end dates are presented as span dates in dc:date and in
       dc:coverage

       When the first subelement in a subject wrapper is topic, subject
       subelements are strung together in dc:subject with hyphens separating
       them 

       Some subject subelements, i.e., geographic, temporal,
       hierarchicalGeographic, and cartographics, are also parsed into
       dc:coverage The subject subelement geographicCode is dropped in the
       transform

	
Revision 1.1	2007-05-18 <tmee@loc.gov>
		Added modsCollection conversion to DC SRU
		Updated introductory documentation
	
Version 1.0	2007-05-04 Tracy Meehleib <tmee@loc.gov>

  -->

   <xsl:param name="url_prefix" select="''"/>

   <xsl:output encoding="UTF-8" method="xml"/>

	
  <xsl:template match="/">
    <xsl:for-each select="md:mods">
      <oai_dc:dc 
	  xmlns:dc="http://purl.org/dc/elements/1.1/" 
	  xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ 
			      http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
	<xsl:apply-templates/>
      </oai_dc:dc>
    </xsl:for-each>
    </xsl:template>

  <xsl:template match="md:recordInfo">
    <dc:identifier xml:lang="da" xsi:type="dcterms:URI">
      <xsl:value-of select="concat($url_prefix,md:recordIdentifier,'/da/')"/>
    </dc:identifier>
    <dc:identifier xml:lang="en" xsi:type="dcterms:URI">
      <xsl:value-of select="concat($url_prefix,md:recordIdentifier,'/en/')"/>
    </dc:identifier>
  </xsl:template>

  <xsl:template match="md:titleInfo">
    <dc:title>
      <xsl:value-of select="md:nonSort"/>
      <xsl:if test="md:nonSort">
	<xsl:text> </xsl:text>
      </xsl:if>
      <xsl:value-of select="md:title"/>
      <xsl:if test="md:subTitle">
	<xsl:text>: </xsl:text>
	<xsl:value-of select="md:subTitle"/>
      </xsl:if>
      <xsl:if test="md:partNumber">
	<xsl:text>. </xsl:text>
	<xsl:value-of select="md:partNumber"/>
      </xsl:if>
      <xsl:if test="md:partName">
	<xsl:text>. </xsl:text>
	<xsl:value-of select="md:partName"/>
      </xsl:if>
    </dc:title>
  </xsl:template>

  <xsl:template match="md:name">
    <xsl:choose>
      <xsl:when 
	  test="md:role/md:roleTerm[@type='text']='creator' or 
		md:role/md:roleTerm[@type='code']='cre' or
		md:role/md:roleTerm[@type='code']='aut' ">
	<dc:creator>
	  <xsl:call-template name="name"/>
	</dc:creator>
      </xsl:when>
      <xsl:when 
	  test="md:role/md:roleTerm[@type='text']='last-modified-by'">
      </xsl:when>
      <xsl:otherwise>
	<dc:contributor>
	  <xsl:call-template name="name"/>
	</dc:contributor>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="md:classification">
    <dc:subject>
      <xsl:value-of select="."/>
    </dc:subject>
  </xsl:template>

  <xsl:template match="md:subject[md:topic | 
		                    md:name  |
				    md:occupation | 
				    md:geographic | 
				    md:hierarchicalGeographic | 
				    md:cartographics | 
				    md:temporal] ">
    <dc:subject>
      <xsl:for-each select="md:topic">
	<xsl:value-of select="."/>
	<xsl:if test="position()!=last()">--</xsl:if>
      </xsl:for-each>
      
      <xsl:for-each select="md:occupation">
	<xsl:value-of select="."/>
	<xsl:if test="position()!=last()">--</xsl:if>
      </xsl:for-each>

      <xsl:for-each select="md:name">
	<xsl:call-template name="name"/>
      </xsl:for-each>
    </dc:subject>

    <xsl:for-each select="md:titleInfo/md:title">
      <dc:subject>
	<xsl:value-of select="md:titleInfo/md:title"/>
      </dc:subject>
    </xsl:for-each>

    <xsl:for-each select="md:geographic">
      <dc:coverage>
	<xsl:value-of select="."/>
      </dc:coverage>
    </xsl:for-each>

    <xsl:for-each select="md:hierarchicalGeographic">
      <dc:coverage>
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
	  <xsl:value-of select="."/>
	  <xsl:if test="position()!=last()">--</xsl:if>
	</xsl:for-each>
      </dc:coverage>
    </xsl:for-each>

    <xsl:for-each select="md:cartographics/*">
      <dc:coverage>
	<xsl:value-of select="."/>
      </dc:coverage>
    </xsl:for-each>

    <xsl:if test="md:temporal">
      <dc:coverage>
	<xsl:for-each select="md:temporal">
	  <xsl:value-of select="."/>
	  <xsl:if test="position()!=last()">-</xsl:if>
	</xsl:for-each>
      </dc:coverage>
    </xsl:if>

    <xsl:if test="*[1][local-name()='topic'] and *[local-name()!='topic']">
      <dc:subject>
	<xsl:for-each select="*[local-name()!='cartographics' and local-name()!='geographicCode' and local-name()!='hierarchicalGeographic'] ">
	  <xsl:value-of select="."/>
	  <xsl:if test="position()!=last()">--</xsl:if>
	</xsl:for-each>
      </dc:subject>
    </xsl:if>
  </xsl:template>

  <xsl:template match="md:abstract        | 
		       md:tableOfContents |
		       md:note">
    <dc:description>
      <xsl:value-of select="."/>
    </dc:description>
  </xsl:template>

  <xsl:template match="md:originInfo">
    <xsl:apply-templates select="md:dateIssued|md:dateCreated|md:dateCaptured|md:dateOther"/>
    <xsl:for-each select="md:publisher">
      <dc:publisher>
	<xsl:value-of select="."/>
      </dc:publisher>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="md:dateIssued  | 
		       md:dateCreated |
		       md:dateCaptured">
    <dc:date>
      <xsl:choose>
	<xsl:when test="@point='start'">
	  <xsl:value-of select="."/>
	  <xsl:text> - </xsl:text>
	</xsl:when>
	<xsl:when test="@point='end'">
	  <xsl:value-of select="."/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="."/>
	</xsl:otherwise>
      </xsl:choose>
    </dc:date>
  </xsl:template>

  <xsl:template match="md:genre">
    <xsl:choose>
      <xsl:when test="@authority='dct'">
	<dc:type>
	  <xsl:value-of select="."/>
	</dc:type>
	<xsl:for-each select="md:typeOfResource">
	  <dc:type>
	    <xsl:value-of select="."/>
	  </dc:type>
	</xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
	<dc:type>
	  <xsl:value-of select="."/>
	</dc:type>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="md:typeOfResource">
    <xsl:if test="@collection='yes'">
      <dc:type>Collection</dc:type>
    </xsl:if>
    <xsl:if test=". ='software' and ../md:genre='database'">
      <dc:type>DataSet</dc:type>
    </xsl:if>
    <xsl:if test=".='software' and ../md:genre='online system or service'">
      <dc:type>Service</dc:type>
    </xsl:if>
    <xsl:if test=".='software'">
      <dc:type>Software</dc:type>
    </xsl:if>
    <xsl:if test=".='cartographic material'">
      <dc:type>Image</dc:type>
    </xsl:if>
    <xsl:if test=".='multimedia'">
      <dc:type>InteractiveResource</dc:type>
    </xsl:if>
    <xsl:if test=".='moving image'">
      <dc:type>MovingImage</dc:type>
    </xsl:if>
    <xsl:if test=".='three-dimensional object'">
      <dc:type>PhysicalObject</dc:type>
    </xsl:if>
    <xsl:if test="starts-with(.,'sound recording')">
      <dc:type>Sound</dc:type>
    </xsl:if>
    <xsl:if test=".='still image'">
      <dc:type>StillImage</dc:type>
    </xsl:if>
    <xsl:if test=". ='text'">
      <dc:type>Text</dc:type>
    </xsl:if>
    <xsl:if test=".='notated music'">
      <dc:type>Text</dc:type>
    </xsl:if>
  </xsl:template>


  <xsl:template match="md:physicalDescription">
    <xsl:if test="md:extent">
      <dc:format>
	<xsl:value-of select="md:extent"/>
      </dc:format>
    </xsl:if>
    <xsl:if test="md:form">
      <dc:format>
	<xsl:value-of select="md:form"/>
      </dc:format>
    </xsl:if>
    <xsl:if test="md:internetMediaType">
      <dc:format>
	<xsl:value-of select="md:internetMediaType"/>
      </dc:format>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="md:mimeType">
    <dc:format>
      <xsl:value-of select="."/>
    </dc:format>
  </xsl:template>

  <xsl:template match="md:identifier">
    <xsl:variable name="type">
      <xsl:value-of
	  select="translate(@type,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
    </xsl:variable>
    <xsl:variable name="display">
      <xsl:value-of
	  select="translate(@displayLabel,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="contains('uri', $type) and contains('thumbnail', $display)">
    <dc:identifier xsi:type="kb:thumbnail" >
          <xsl:value-of select="." />
    </dc:identifier>
      </xsl:when>
      <xsl:when test="contains('uri', $type) and contains('image', $display)">
    <dc:identifier xsi:type="kb:image" >
          <xsl:value-of select="." />
    </dc:identifier>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="md:location">
    <xsl:for-each select="md:url">
      <dc:identifier>
	<xsl:value-of select="."/>
      </dc:identifier>
    </xsl:for-each>
  </xsl:template>
  
  <xsl:template match="md:language">
    <xsl:for-each select="md:languageTerm">
      <dc:language>
	<xsl:value-of select="normalize-space(.)"/>
      </dc:language>
    </xsl:for-each>
  </xsl:template>


  <xsl:template match="md:relatedItem[md:titleInfo  | 
		                        md:name       |
					md:identifier |
					md:location]"/>

  <!--xsl:template match="md:relatedItem[md:titleInfo  | 
		                        md:name       |
					md:identifier |
					md:location]">
    <xsl:choose>
      <xsl:when test="@type='original'">
	<dc:source>
	  <xsl:for-each select="md:titleInfo/md:title | md:identifier | md:location/md:url">
	    <xsl:if test="normalize-space(.)!= ''">
	      <xsl:value-of select="."/>
	      <xsl:if test="position()!=last()">-/-</xsl:if>
	    </xsl:if>
	  </xsl:for-each>
	</dc:source>
      </xsl:when>
      <xsl:when test="@type='series'"/>
      <xsl:otherwise>
	<dc:relation>
	  <xsl:for-each select="md:titleInfo/md:title | md:identifier | md:location/md:url">
	    <xsl:if test="normalize-space(.)!= ''">
	      <xsl:value-of select="."/>
	      <xsl:if test="position()!=last()">-/-</xsl:if>
	    </xsl:if>
	  </xsl:for-each>
	</dc:relation>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template-->

  <xsl:template match="md:accessCondition">
    <dc:rights>
      <xsl:value-of select="."/>
    </dc:rights>
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
      <xsl:if test="md:displayForm">
	<xsl:text> (</xsl:text>
	<xsl:value-of select="md:displayForm"/>
	<xsl:text>) </xsl:text>
      </xsl:if>
      <xsl:for-each select="md:role[md:roleTerm[@type='text']!='creator']">
	<xsl:text> (</xsl:text>
	<xsl:value-of select="normalize-space(.)"/>
	<xsl:text>) </xsl:text>
      </xsl:for-each>
    </xsl:variable>
    <xsl:value-of select="normalize-space($name)"/>
  </xsl:template>

  <xsl:template match="md:dateIssued[@point='start']   |
		       md:dateCreated[@point='start']  |
		       md:dateCaptured[@point='start'] |
		       md:dateOther[@point='start'] ">

    <xsl:variable name="dateName" select="local-name()"/>
    <dc:date>
      <xsl:value-of select="."/>-<xsl:value-of select="../*[local-name()=$dateName][@point='end']"/>
    </dc:date>
  </xsl:template>
	
  <xsl:template match="md:temporal[@point='start']  ">
    <xsl:value-of select="."/>-<xsl:value-of select="../md:temporal[@point='end']"/>
  </xsl:template>
  
  <xsl:template match="md:temporal[@point!='start' and @point!='end']  ">
    <xsl:value-of select="."/>
  </xsl:template>
	
  <!-- suppress all else:-->
  <xsl:template match="*"/>
	
</xsl:stylesheet>
