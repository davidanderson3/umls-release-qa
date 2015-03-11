<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ns0="urn:ihe:iti:svs:2008" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:java="http://www.java.com/"
    exclude-result-prefixes="java xs">
    <xsl:output method = "text"  encoding = "utf-8"></xsl:output>
    <!-- each row is a list of measures, number of measures, QDM categories, and # of QDM categories per value set -->
    <xsl:template match = "ns0:RetrieveMultipleValueSetsResponse">
        <xsl:for-each select = "ns0:DescribedValueSet">
            <xsl:value-of select = "@ID"/><xsl:text>|</xsl:text>
            <xsl:value-of select = "@displayName"/><xsl:text>|</xsl:text>
            <xsl:value-of select = "string-join((ns0:Group[@displayName='CMS eMeasure ID']/ns0:Keyword),',')"/><xsl:text>|</xsl:text>
            <xsl:value-of select = "count(ns0:Group[@displayName='CMS eMeasure ID']/ns0:Keyword)"/><xsl:text>|</xsl:text>
            <xsl:value-of select = "string-join((distinct-values(ns0:Group[@displayName='CATEGORY']/ns0:Keyword)),',')"/><xsl:text>|</xsl:text>
            <xsl:value-of select = "count(distinct-values(ns0:Group[@displayName='CATEGORY']/ns0:Keyword))"/><xsl:text>|</xsl:text>
            <xsl:text>&#10;</xsl:text>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>