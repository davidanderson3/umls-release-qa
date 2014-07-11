<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ns0="urn:ihe:iti:svs:2008"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:java="http://www.java.com/"
    exclude-result-prefixes="java xs">
    <xsl:output method = "text"  encoding = "utf-8"></xsl:output>
    <xsl:template match = "ns0:RetrieveMultipleValueSetsResponse">
        
        <!--<xsl:value-of select = "count(ns0:DescribedValueSet)"/>-->
        <xsl:text>OID|Source|Type|Definition|member_of|EH/EP|QDM Category|NQF#|CMS MeasureId&#10;</xsl:text>
        <xsl:apply-templates select = "ns0:DescribedValueSet"/>
        
    </xsl:template>
    <xsl:template match = "ns0:DescribedValueSet">
        <xsl:variable name = "oid" select = "@ID"/>
        <xsl:variable name = "source" select = "ns0:Source/node()"/>
        <xsl:variable name = "definition" select = "ns0:Definition/node()"/>
        <xsl:variable name = "type" select = "ns0:Type/node()"/>
        <xsl:variable name = "displayName" select = "ns0:Group/@displayName"/>
        
        
        <xsl:variable name = "record">
            <xsl:value-of select = "string-join(($oid,$source,$type),'|')"/>
        </xsl:variable>
        
        <xsl:for-each-group select = "ns0:Group" group-by = "@ID">
            <xsl:sort select = "$oid"/>
            <xsl:variable name = "id" select = "@ID"/>
            <!--<xsl:value-of select = "$id"/><xsl:text>|</xsl:text>-->
            <xsl:value-of select = "$record"/><xsl:text>|</xsl:text>
            <xsl:value-of select = "$definition"/><xsl:text>|</xsl:text>
            <xsl:for-each select="current-group()">
                
                <xsl:if test = "@displayName eq 'memberOf'"><xsl:value-of select = "ns0:Keyword"/><xsl:text>||||</xsl:text></xsl:if>
                <xsl:if test = "@displayName eq 'Meaningful Use Measures'"><xsl:value-of select = "ns0:Keyword"/><xsl:text>|</xsl:text></xsl:if>
                <xsl:if test = "@displayName eq 'CATEGORY'"><xsl:value-of select = "ns0:Keyword"/><xsl:text>|</xsl:text></xsl:if>
                <xsl:if test = "@displayName eq 'NQF Number'"><xsl:value-of select = "ns0:Keyword"/><xsl:text>|</xsl:text></xsl:if>
                <xsl:if test = "@displayName eq 'CMS eMeasure ID'"><xsl:value-of select = "ns0:Keyword"/></xsl:if>
                
            </xsl:for-each>
            <xsl:text>&#10;</xsl:text>    
        </xsl:for-each-group>
    </xsl:template>
</xsl:stylesheet>