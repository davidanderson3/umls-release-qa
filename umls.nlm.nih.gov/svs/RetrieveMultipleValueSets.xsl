<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ns0="urn:ihe:iti:svs:2008"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:java="http://www.java.com/"
    exclude-result-prefixes="java xs">
    <xsl:output method = "text"  encoding = "utf-8"></xsl:output>
    <xsl:key name = "groups" match = "ns0:Group" use = "@displayName"/>
    <xsl:template match = "ns0:RetrieveMultipleValueSetsResponse">
    <xsl:variable name="newline" select="normalize-space()"/>        
        
        <xsl:result-document href = "value-set-code-counts.txt">
            <!-- show number of codes in each value set  -->
            <xsl:text>OID|ValueSetName|NumberOfCodes&#10;</xsl:text>
            <xsl:for-each select = "ns0:DescribedValueSet">
                <xsl:variable name = "oid" select = "@ID"/>
                <xsl:variable name = "valueSetName" select = "@displayName"/>
                <xsl:variable name = "expansionVersion" select = "@version"/>
                <xsl:variable name = "definitionVersion" select = "ns0:RevisionDate"/>
                <xsl:variable name = "codeCount" select = "string(count(ns0:ConceptList/child::*))"/>
                <xsl:value-of select = "string-join(($oid,$valueSetName,$codeCount),'|')"/>
                <xsl:text>&#10;</xsl:text>  
            </xsl:for-each>  
        </xsl:result-document>
        
        <xsl:result-document href = "value-set-measure-counts.txt">
            <!-- how many measures are using a given value set ? -->
            <xsl:text>OID|ValueSetName|NumberofCMSMeasures&#10;</xsl:text>
             <xsl:for-each select="ns0:DescribedValueSet">
                 <xsl:sort select = "count(distinct-values(ns0:Group[@displayName='CMS eMeasure ID']/ns0:Keyword))" order = "descending"/>
                 <xsl:value-of select = "@ID"/><xsl:text>|</xsl:text>
                 <xsl:value-of select = "@displayName"/><xsl:text>|</xsl:text>
                 <xsl:value-of select = "count(distinct-values(ns0:Group[@displayName='CMS eMeasure ID']/ns0:Keyword))"/>
                 <xsl:text>&#10;</xsl:text> 
             </xsl:for-each>
        </xsl:result-document>
        
        
        <xsl:result-document href = "measure-counts.txt">
            <!-- how many value sets are in a given measure? -->
            <xsl:text>CMS Measure ID|NumberofOIDs&#10;</xsl:text>
            <xsl:variable name = "cms" select = "ns0:DescribedValueSet/ns0:Group[@displayName='CMS eMeasure ID']/ns0:Keyword"/>
                <xsl:for-each select = "$cms">
                    <xsl:sort select = "current()"/>
                    <xsl:if test="generate-id() = generate-id($cms[. = current()][1])">
                        <xsl:value-of select = "."/><xsl:text>|</xsl:text>
                        <xsl:value-of select = "count($cms[.=current()])"/><xsl:text>&#10;</xsl:text>
                    </xsl:if>                
                </xsl:for-each>

        </xsl:result-document>
        
    
        <xsl:result-document href = "value-set-codes.txt">
            <xsl:text>OID|ValueSetName|Source|Version|Code|Term|CodeSystemName|CodeSystemVersion|CodeSystemOID&#10;</xsl:text>
            <!-- produce value set "codes" file -->
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
        
        
        <xsl:result-document href = "value-set-metadata.txt">
        <xsl:text>OID|Version|Source|Type|Definition|MUType|MeasureTitle|QDM|NQF|CMSId&#10;</xsl:text>  
            <!-- produce value set "usage" file -->
            <xsl:for-each select = "ns0:DescribedValueSet">
                
                <xsl:variable name = "oid" select = "@ID"/>
                <xsl:variable name = "version" select = "@version"/>
                <xsl:variable name = "source" select = "ns0:Source"/>
                <xsl:variable name = "definition" select = "ns0:Definition"/>
                <xsl:variable name = "type" select = "ns0:Type"/>
                <xsl:variable name = "record">
                    <xsl:if test = "not($definition)"><xsl:value-of select = "string-join(($oid,$version,$source,$type,''),'|')"/></xsl:if>
                    <xsl:if test = "$definition"><xsl:value-of select = "string-join(($oid,$version,$source,$type,$definition),'|')"/></xsl:if>
                </xsl:variable>
                
                <!--don't include the memberOf groups-->
                <xsl:for-each-group select = "ns0:Group[not(@displayName eq 'memberOf')]" group-by = "@ID">
                    <xsl:value-of select = "concat($record,'|')"/>
                    <xsl:for-each select="current-group()">
                        <xsl:choose>
                           <xsl:when test = "@displayName eq 'eMeasure Title'"><xsl:apply-templates select = "ns0:Keyword" mode = "singular"/></xsl:when>
                           <xsl:when test = "@displayName eq 'CMS eMeasure ID'"><xsl:apply-templates select = "ns0:Keyword" mode = "singular"/></xsl:when>
                           <xsl:when test = "@displayName eq 'CATEGORY' and count(ns0:Keyword) &gt; 1"><xsl:apply-templates select = "ns0:Keyword" mode = "multiple"/></xsl:when>
                            <xsl:when test = "@displayName eq 'CATEGORY' and count(ns0:Keyword) = 1"><xsl:apply-templates select = "ns0:Keyword" mode = "singular"/></xsl:when>
                            <xsl:when test = "@displayName eq 'Meaningful Use Measures'"><xsl:apply-templates select = "ns0:Keyword" mode = "singular"/></xsl:when>
                            <xsl:when test = "@displayName eq 'NQF Number' and count(ns0:Keyword) &gt; 0"><xsl:apply-templates select = "ns0:Keyword" mode = "singular"/></xsl:when>
                            <xsl:when test = "@displayName eq 'NQF Number' and count(ns0:Keyword)  = 0"><xsl:text>|</xsl:text></xsl:when>
                        </xsl:choose>
                    </xsl:for-each>
                    <xsl:text>&#10;</xsl:text>    
                </xsl:for-each-group>
            </xsl:for-each>
        </xsl:result-document>
        
        
</xsl:template>  

<xsl:template match = "ns0:Keyword" mode = "singular">
<xsl:value-of select = "concat(.,'|')"/>
</xsl:template>
    
<xsl:template match = "ns0:Keyword" mode = "multiple">
<xsl:if test = "count(following-sibling::*) &gt; 0"><xsl:value-of select = "concat(.,',')"/></xsl:if>
<xsl:if test = "count(following-sibling::*) = 0"><xsl:value-of select = "."/><xsl:text>|</xsl:text></xsl:if>
</xsl:template>   
 
</xsl:stylesheet>