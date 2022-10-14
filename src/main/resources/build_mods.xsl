<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:md="http://www.loc.gov/mods/v3"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xmlns:t="http://www.tei-c.org/ns/1.0"
               xmlns:xlink="http://www.w3.org/1999/xlink"
               xmlns:xml="http://www.w3.org/XML/1998/namespace"
               xmlns:x="urn:x"
               xmlns:exslt="http://exslt.org/common"
               version="1.0">

    <!--
    author Sigfrid Lundberg (slu@kb.dk)
    version $Revision$, last modified $Date$ by $Author$
    $Id$
    -->

    <xsl:param name="cobject_id" select="''"/>
    <xsl:param name="cobject_type" select="''"/>
    <xsl:param name="cobject_title" select="''"/>
    <xsl:param name="cobject_edition" select="''"/>
    <xsl:param name="cobject_bookmark" select="''"/>
    <xsl:param name="cobject_random_number" select="''"/>
    <xsl:param name="cobject_likes" select="''"/>
    <xsl:param name="cobject_not_before" select="''"/>
    <xsl:param name="cobject_not_after" select="''"/>
    <xsl:param name="cobject_building" select="''"/>
    <xsl:param name="cobject_location" select="''"/>
    <xsl:param name="cobject_person" select="''"/>
    <xsl:param name="cobject_last_modified" select="''"/>
	<xsl:param name="cobject_last_modified_by" select="''"/>
	<xsl:param name="ccs_ready" select="''"/>
	<xsl:param name="cumulus_catalog" select="''"/>


	<xsl:param name="latitude"        select="''"/>
    <xsl:param name="longitude"       select="''"/>
    <xsl:param name="last_modified"   select="''"/>
    <xsl:param name="content_context" select="''"/>
    <xsl:param name="metadata_context" select="''"/>
    <xsl:param name="image_context"   select="''"/>
    <xsl:param name="keywords" select="''"/>
	<xsl:param name="comments" select="''"/>


	<xsl:param name="correctness" select="''"/>
    <xsl:param name="interestingness" select="''" />


    <xsl:output encoding="UTF-8"
                method="xml"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="md:mods">
        <md:mods>
            <xsl:apply-templates/>
            <md:subject>
                <md:cartographics>
                    <md:coordinates><xsl:value-of select="concat($latitude,',',$longitude)"/></md:coordinates>
                </md:cartographics>
            </md:subject>
            <xsl:apply-templates select="exslt:node-set($keywords)/x:subject/*" />

	    <xsl:if test="$cobject_id">
	      <xsl:processing-instruction name="cobject_id">
		<xsl:value-of select="$cobject_id" />
	      </xsl:processing-instruction>
	    </xsl:if>

	    <xsl:if test="$correctness">
	      <xsl:processing-instruction name="cobject_correctness">
		<xsl:value-of select="$correctness"/>
	      </xsl:processing-instruction>
	    </xsl:if>

	    <xsl:if test="$interestingness">
	      <xsl:processing-instruction name="cobject_interestingness">
		<xsl:value-of select="$interestingness"/>
	      </xsl:processing-instruction>
	    </xsl:if>

	    <xsl:if test="$cobject_type">
	      <xsl:processing-instruction name="cobject_type">
		<xsl:value-of select="$cobject_type"/>
	      </xsl:processing-instruction>
	    </xsl:if>

	    <xsl:if test="$cobject_title">
	      <xsl:processing-instruction name="cobject_title">
		<xsl:value-of select="$cobject_title"/>
	      </xsl:processing-instruction>
	    </xsl:if>

	    <xsl:if test="$cobject_edition">
	      <xsl:processing-instruction name="cobject_edition">
		<xsl:value-of select="$cobject_edition"/>
	      </xsl:processing-instruction>
	    </xsl:if>

	    <xsl:if test="$cobject_bookmark">
	      <xsl:processing-instruction name="cobject_bookmark">
		<xsl:value-of select="$cobject_bookmark"/>
	      </xsl:processing-instruction>
	    </xsl:if>

	    <xsl:if test="$cobject_random_number">
	      <xsl:processing-instruction name="cobject_random_number">
		<xsl:value-of select="$cobject_random_number"/>
	      </xsl:processing-instruction>
	    </xsl:if>

	    <xsl:if test="$cobject_likes">
	      <xsl:processing-instruction name="cobject_likes">
		<xsl:value-of select="$cobject_likes"/>
	      </xsl:processing-instruction>
	    </xsl:if>

		<xsl:if test="$comments">
			<xsl:processing-instruction name="cobject_annotation_comments">
				<xsl:value-of select="$comments"/>
			</xsl:processing-instruction>
		</xsl:if>

	    <xsl:choose>
	      <xsl:when test="md:originInfo/md:dateCreated/@t:notBefore">
		<xsl:processing-instruction name="cobject_not_before">
		  <xsl:value-of select="md:originInfo/md:dateCreated/@t:notBefore"/>
		</xsl:processing-instruction>
	      </xsl:when>
	      <xsl:otherwise>
		<xsl:if test="$cobject_not_before">
		  <xsl:processing-instruction name="cobject_not_before">
		    <xsl:value-of select="$cobject_not_before" />
		  </xsl:processing-instruction>
		</xsl:if>
	      </xsl:otherwise>
	    </xsl:choose>

	    <xsl:choose>
	      <xsl:when test="md:originInfo/md:dateCreated/@t:notAfter">
		<xsl:processing-instruction name="cobject_not_after">
		  <xsl:value-of select="md:originInfo/md:dateCreated/@t:notAfter"/>
		</xsl:processing-instruction>
	      </xsl:when>
	      <xsl:otherwise>
		<xsl:if test="$cobject_not_after">
		  <xsl:processing-instruction name="cobject_not_after">
		    <xsl:value-of select="$cobject_not_after" />
		  </xsl:processing-instruction>
		</xsl:if>
	      </xsl:otherwise>
	    </xsl:choose>

	    <xsl:if test="$cobject_building">
	      <xsl:processing-instruction name="cobject_building">
		<xsl:value-of select="$cobject_building" />
	      </xsl:processing-instruction>
	    </xsl:if>

	    <xsl:if test="$cobject_location">
	      <xsl:processing-instruction name="cobject_location">
		<xsl:value-of select="$cobject_location" />
	      </xsl:processing-instruction>
	    </xsl:if>

	    <xsl:if test="$cobject_person">
	      <xsl:processing-instruction name="cobject_person">
		<xsl:value-of select="$cobject_person"/>
	      </xsl:processing-instruction>
	    </xsl:if>

	    <xsl:if test="$cobject_last_modified" >
	      <xsl:processing-instruction name="cobject_last_modified">
		<xsl:value-of select="$cobject_last_modified" />
	      </xsl:processing-instruction>
	    </xsl:if>

			<xsl:if test="$cobject_last_modified_by" >
				<xsl:processing-instruction name="cobject_last_modified_by">
					<xsl:value-of select="$cobject_last_modified_by" />
				</xsl:processing-instruction>
			</xsl:if>
			<xsl:if test="$ccs_ready" >
				<xsl:processing-instruction name="ccs_ready">
					<xsl:value-of select="$ccs_ready" />
				</xsl:processing-instruction>
			</xsl:if>

			<xsl:if test="$cumulus_catalog" >
				<xsl:processing-instruction name="cumulus_catalog">
					<xsl:value-of select="$cumulus_catalog" />
				</xsl:processing-instruction>
			</xsl:if>


        </md:mods>
    </xsl:template>

    <xsl:template match="md:recordInfo">
        <md:recordInfo>
            <xsl:comment> hit recordInfo </xsl:comment>
            <xsl:apply-templates/>
        </md:recordInfo>
    </xsl:template>

    <xsl:template match="md:recordIdentifier">
        <xsl:comment> hit recordIdentifier </xsl:comment>
        <md:recordIdentifier>
            <xsl:value-of select="concat($metadata_context,normalize-space(.))"/>
        </md:recordIdentifier>
    </xsl:template>

    <xsl:template match="md:recordChangeDate">
        <xsl:choose>
            <xsl:when test="$last_modified">
                <md:recordChangeDate encoding="w3cdtf">
                    <xsl:value-of select="$last_modified"/>
                </md:recordChangeDate>
            </xsl:when>
            <xsl:otherwise>
                <md:recordChangeDate encoding="w3cdtf">
                    <xsl:apply-templates/>
                </md:recordChangeDate>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="md:subject[md:cartographics]">
    </xsl:template>

    <xsl:template match="md:mods/md:identifier[@type='uri' and not(@displayLabel)]">
        <md:identifier type="uri">
            <xsl:value-of select="concat($content_context,.)"/>
        </md:identifier>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="x:topic">
        <md:subject>
            <md:topic>
                <xsl:value-of select="." />
            </md:topic>
        </md:subject>
    </xsl:template>

    <xsl:template match="text()">
        <xsl:value-of select="normalize-space(.)" />
    </xsl:template>

</xsl:transform>
