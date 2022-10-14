<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	       xmlns:o="http://a9.com/-/spec/opensearch/1.1/"
	       version="1.0">

  <xsl:param name="short_name" 
	     select="''"/>

  <xsl:param name="long_name" 
	     select="''"/>

  <xsl:param name="description" 
	     select="''"/>

  <xsl:param name="developer" 
	     select="''"/>

  <xsl:param name="contact" 
	     select="''"/>

  <xsl:param name="language" 
	     select="''"/>

  <xsl:template match="/">
    <OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/">
      <xsl:apply-templates select="o:OpenSearchDescription"/>
    </OpenSearchDescription>
  </xsl:template>

  <xsl:template xmlns="http://a9.com/-/spec/opensearch/1.1/" match="o:OpenSearchDescription">
    <ShortName><xsl:value-of select="$short_name"/></ShortName>
    <LongName><xsl:value-of select="$long_name"/></LongName>
    <Description><xsl:value-of select="$description"/></Description>
    <Tags>
      Det_Kongelige_Bibliotek Digitized_objects
    </Tags>
    <Developer><xsl:value-of select="$developer"/></Developer>
    <Contact><xsl:value-of select="$contact"/></Contact>
    <xsl:element name="Url">
      <xsl:attribute name="type">application/rss+xml</xsl:attribute>
      <xsl:attribute name="template">http://www.kb.dk:80/cop/syndication/manus/musman/2010/dec/viser/da/da/?page={startPage?}&amp;itemsPerPage={count}&amp;query={searchTerms}</xsl:attribute>
    </xsl:element>
    <xsl:element name="Url">
      <xsl:attribute name="type">text/html</xsl:attribute>
      <xsl:attribute name="template">http://www.kb.dk/manus/musman/2010/dec/viser/da/?page={startPage?}&amp;itemsPerPage={count}&amp;query={searchTerms}</xsl:attribute>
    </xsl:element>
    <Image height="42" width="219" type="image/gif">http://www.kb.dk/image_client_static/default/img/kbLogo.gif</Image>
    <Attribution>
      Det Kongelige Bibliotek,
      Nationalbibliotek og Københavns Universitetsbibliotek.
      Postbox 2149,
      DK-1016 København K,
      (+45) 33 47 47 47
    </Attribution>
    <SyndicationRight>open</SyndicationRight>
    <AdultContent>false</AdultContent>
    <Language><xsl:value-of select="$language"/></Language>
    <OutputEncoding>UTF-8</OutputEncoding>
    <InputEncoding>UTF-8</InputEncoding>
    <Query role="example" searchTerms="esbjerg"/>
  </xsl:template>

</xsl:transform>
