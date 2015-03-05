<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ns0="urn:ihe:iti:svs:2008"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:java="http://www.java.com/"
    exclude-result-prefixes="java xs">
<xsl:output method = "text"  encoding = "utf-8"></xsl:output>
 
<xsl:template match = "ns0:RetrieveMultipleValueSetsResponse">

        <xsl:for-each select="ns0:DescribedValueSet">
            <xsl:variable name = "cs" select = "ns0:ConceptList/ns0:Concept/@codeSystemName"/>
            <xsl:value-of select = "@ID"/><xsl:text>|</xsl:text>
            <!--<xsl:value-of select = "@displayName"/><xsl:text>|</xsl:text>-->
            <!--<xsl:value-of select = "count(ns0:ConceptList/ns0:Concept)"/><xsl:text>|</xsl:text>-->
            <xsl:for-each select = "$cs">
                <xsl:sort select = "current()"/>
                <xsl:if test="generate-id() = generate-id($cs[. = current()][1])">
                    <xsl:value-of select = "."/>
                    <xsl:text>:</xsl:text><xsl:value-of select = "count($cs[.=current()])"/><xsl:text> </xsl:text>
                </xsl:if>
            </xsl:for-each>
            <xsl:text>&#10;</xsl:text>
            <!--<xsl:text>|</xsl:text>
            <xsl:value-of select = "ns0:Group[@displayName='CMS eMeasure ID']/ns0:Keyword"/><xsl:text>|</xsl:text>
            <xsl:value-of select = "count(ns0:Group[@displayName='CMS eMeasure ID']/ns0:Keyword)"/><xsl:text>|</xsl:text>
            <xsl:value-of select = "count(ns0:Group[@displayName='CATEGORY']/ns0:Keyword)"/><xsl:text>|</xsl:text>
            <xsl:text>&#10;</xsl:text>-->
        </xsl:for-each>

</xsl:template>
</xsl:stylesheet>