<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ns0="urn:ihe:iti:svs:2008"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:java="http://www.java.com/"
    exclude-result-prefixes="java xs">
    <xsl:output method = "text"  encoding = "utf-8"></xsl:output>
    <xsl:template match = "ns0:RetrieveMultipleValueSetsResponse">
        
        <!--<xsl:value-of select = "count(ns0:DescribedValueSet)"/>-->
        <xsl:apply-templates select = "ns0:DescribedValueSet"/>
        
    </xsl:template>
    
    <xsl:template match = "ns0:DescribedValueSet">
        <xsl:variable name = "oid" select = "@ID"/>
        <xsl:variable name = "valueSetName" select = "@displayName"/>
        <xsl:variable name = "valueSetVersion" select = "@version"/>
        <xsl:for-each select = "ns0:ConceptList/ns0:Concept">
            <xsl:value-of select = "$oid"/><xsl:text>|</xsl:text>
            <xsl:value-of select = "$valueSetName"/><xsl:text>|</xsl:text>
            <!--<xsl:value-of select = "$valueSetVersion"/><xsl:text>|</xsl:text>-->
            <xsl:value-of select = "@code"/><xsl:text>|</xsl:text>
            <xsl:value-of select = "@displayName"/><xsl:text>|</xsl:text>
            <xsl:value-of select = "@codeSystemName"/><xsl:text>|</xsl:text>
            <xsl:value-of select = "@codeSystemVersion"/>
            <xsl:text>&#10;</xsl:text>
        </xsl:for-each>   
    </xsl:template>  
</xsl:stylesheet>