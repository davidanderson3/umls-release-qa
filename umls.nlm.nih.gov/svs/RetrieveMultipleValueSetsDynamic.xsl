<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ns0="urn:ihe:iti:svs:2008"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:java="http://www.java.com/"
    exclude-result-prefixes="java xs">
    <xsl:output method = "text"  encoding = "utf-8"></xsl:output>
    <xsl:key name = "groups" match = "ns0:Group" use = "@displayName"/>
    <xsl:template match = "ns0:RetrieveMultipleValueSetsResponse">

        <!-- show number of codes in each value set  -->
        <xsl:result-document href = "value-set-code-counts-by-source.txt">
            <xsl:text>OID|ValueSetName|CodeSystemName|NumberOfCodes&#10;</xsl:text>
            <xsl:for-each select = "ns0:DescribedValueSet">
                <xsl:variable name = "cs" select = "ns0:ConceptList/ns0:Concept/@codeSystemName"/>
                <xsl:for-each select = "$cs">
                    <xsl:sort select = "current()"/>
                    <xsl:if test="generate-id() = generate-id($cs[. = current()][1])">
                        <xsl:value-of select = "ancestor::*/@ID"/><xsl:text>|</xsl:text>
                        <xsl:value-of select = "ancestor::ns0:DescribedValueSet/@displayName"/><xsl:text>|</xsl:text>
                        <xsl:value-of select = "."/><xsl:text>|</xsl:text>
                        <xsl:value-of select = "count($cs[.=current()])"/><xsl:text>&#10;</xsl:text>
                    </xsl:if>                
                </xsl:for-each>
            </xsl:for-each>  
        </xsl:result-document>

        <!-- produce value set "codes" file -->
        <xsl:result-document href = "value-set-codes.txt">
            <xsl:text>OID|ValueSetName|Version|Code|Descriptor|CodeSystemName|CodeSystemVersion|CodeSystemOID&#10;</xsl:text>
            <xsl:for-each select = "ns0:DescribedValueSet">
                <xsl:variable name = "oid" select = "@ID"/>
                <xsl:variable name = "valueSetName" select = "@displayName"/>
                <xsl:variable name = "version" select = "@version"/>
                <xsl:for-each select = "ns0:ConceptList/ns0:Concept">
                    <xsl:variable name = "code" select = "@code"/>
                    <xsl:variable name = "displayName" select = "@displayName"/>
                    <xsl:variable name = "codeSystemName" select = "@codeSystemName"/>
                    <xsl:variable name = "codeSystemVersion" select = "@codeSystemVersion"/>
                    <xsl:variable name = "codeSystem" select = "@codeSystem"/>
                    <xsl:value-of select = "string-join(($oid,$valueSetName,$version,$code,$displayName,$codeSystemName,$codeSystemVersion,$codeSystem),'|')"/>
                    <xsl:text>&#10;</xsl:text>
                </xsl:for-each>
            </xsl:for-each>
        </xsl:result-document>
        
        <!-- produce value set definitions file -->
        <xsl:result-document href = "value-set-definitions.txt">
            <xsl:text>OID|ValueSetName|Version|Definition&#10;</xsl:text>
            <xsl:for-each select = "ns0:DescribedValueSet">
               <xsl:variable name = "oid" select = "@ID"/>
               <xsl:variable name = "valueSetName" select = "@displayName"/>
               <xsl:variable name = "version" select = "@version"/>
               <xsl:variable name = "definition" select = "ns0:Definition"/>
                <xsl:if test = "$definition">
                    <xsl:value-of select = "string-join(($oid,$valueSetName,$version,$definition),'|')"/><xsl:text>&#10;</xsl:text>
                </xsl:if>
            </xsl:for-each>
        </xsl:result-document>

</xsl:template>  
</xsl:stylesheet>