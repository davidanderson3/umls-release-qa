<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ns0="urn:ihe:iti:svs:2008"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:java="http://www.java.com/"
    exclude-result-prefixes="java xs">
    <xsl:output method = "text"  encoding = "utf-8"></xsl:output>
    <xsl:template match = "ns0:RetrieveMultipleValueSetsResponse">
          
        <xsl:text>OID|ValueSetName|Member OID|Member OID Name&#10;</xsl:text>
        <xsl:for-each select = "ns0:DescribedValueSet">
            <xsl:variable name = "oid" select = "@ID"/>
            <xsl:variable name = "valueSetName" select = "@displayName"/>
            <xsl:variable name = "definition" select = "ns0:Definition"/>
            <xsl:variable name = "version" select = "@version"/>
            <xsl:if test = "ns0:Definition">
                <xsl:choose>
                    <xsl:when test = "not(matches($definition,'[\),\(]{3}'))">
                        <xsl:value-of select = "string-join(($oid,$valueSetName,$version,normalize-space(translate($definition,'()',''))),'|')"/><xsl:text>&#10;</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name = "definitions" select = "tokenize($definition,'[\),\(]{3}')"></xsl:variable>
                        <xsl:for-each select="$definitions">
                            <xsl:variable name = "memberOid" select = "substring-before(normalize-space(translate(.,'()','')),':')"/>
                            <xsl:variable name = "memberOidName" select = "substring-after(normalize-space(translate(.,'()','')),':')"/>         
                            <xsl:value-of select = "string-join(($oid,$version,$valueSetName,$memberOid,$memberOidName),'|')"/><xsl:text>&#10;</xsl:text>
                        </xsl:for-each>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test = "not(ns0:Definition)"><xsl:text>||</xsl:text></xsl:if>
            <xsl:value-of select = "ns0:Group[@displayName='CMS eMeasure ID']/ns0:Keyword"/><xsl:text>|</xsl:text>
            <xsl:value-of select = "count(ns0:Group[@displayName='CMS eMeasure ID']/ns0:Keyword)"/><xsl:text>|</xsl:text>
            <xsl:value-of select = "count(ns0:Group[@displayName='CATEGORY']/ns0:Keyword)"/><xsl:text>|</xsl:text>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>