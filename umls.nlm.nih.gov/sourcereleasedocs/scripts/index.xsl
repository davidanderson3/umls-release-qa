<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "2.0" >
<xsl:output method = "xml" omit-xml-declaration = "yes" indent = "yes" use-character-maps="cm1" doctype-public = "-//W3C//DTD XHTML 1.0 Transitional//EN"  encoding="utf-8" />
<xsl:character-map name="cm1">
<xsl:output-character character="&#160;" string="&amp;nbsp;"/>
<xsl:output-character character="&#233;" string="&amp;eacute;"/>
<xsl:output-character character="&#177;" string="&amp;plusmn;"/>
</xsl:character-map>

<xsl:template match = "document">
<div id = "source-doc-update">
<span class = "sectionlabel">Sources added or updated for <xsl:value-of select = "@release"/></span><br/>
<table class = "updatedsources" width = "80%" cellspacing = "10" summary = "Sources updated or added in the UMLS Metathesuarus for the {@release} release">
<tr>
<th scope = "col">Source Abbreviation (RSAB)</th>
<th scope = "col">Source Name</th>
<th scope = "col">Last Update in Metathesaurus</th>
</tr>
<xsl:apply-templates select = "sources"/>
</table>
</div>
<div id = "source-doc-legend">
<span class = "symbol">&#177;</span>:&#160;Indicates LNC subsource<br/>
<span class = "symbol">&#177;&#177;</span>:&#160;Indicates RxNorm subsource<br/>
<span class = "symbol">*</span>:&#160;Indicates foreign translation<br/>
<p>Send comments and suggestions to <a href ="http://apps.nlm.nih.gov/mainweb/siebel/nlm/index.cfm" target = "_blank">NLM Customer Service</a>.</p> 
</div>
<!-- /Editable Text Content --> 
</xsl:template>

<xsl:template match = "sources">
    
    <xsl:for-each select = "source">
    <xsl:sort select = "vsab" order = "ascending"/>
    <xsl:variable name = "updated" select = "@updated"/>
     <tr>
        <xsl:choose>
            <xsl:when test =  "$updated eq '2010AB'">
            <td class = "vsab current"><xsl:apply-templates select = "vsab"/></td>
            </xsl:when>
            <xsl:otherwise>
            <td class = "vsab"><xsl:apply-templates select = "vsab"/></td>   
            </xsl:otherwise>
        </xsl:choose>
     
     <td class = "son"><xsl:value-of select = "son"/></td>
     <td><xsl:value-of select = "@updated"/></td>
     </tr>
    </xsl:for-each>
</xsl:template>

<!--determine if vsab belongs to lnc, rxnorm, or is a foreign translation -->
<xsl:template match = "vsab">
<xsl:for-each select = ".">
<xsl:variable name = "link_to" select = "../@link_to"/>
<xsl:variable name = "type" select = "@type"/>
<xsl:variable name = "live" select = "@live"/>
<xsl:choose>
<xsl:when test = "$type eq 'lncsubsource' and $live eq 'yes'">
<a href = "{../../$link_to}/{@rsab}" target = "_blank"><xsl:value-of select = "@rsab"/></a>&#160;&#160;<span class = "symbol">&#177;</span>
</xsl:when>
<xsl:when test = "$type eq 'lncsubsource' and $live eq 'no'">
<xsl:value-of select = "@rsab"/>&#160;&#160;<span class = "symbol">&#177;</span>
</xsl:when>

<xsl:when test = "$type eq 'rxnormsubsource' and $live eq 'yes'">
<a href = "{../../$link_to}/{@rsab}"><xsl:value-of select = "@rsab"/></a>&#160;&#160;<span class = "symbol">&#177;&#177;</span>
</xsl:when>
<xsl:when test = "$type eq 'rxnormsubsource' and $live eq 'no'">
<xsl:value-of select = "@rsab"/>&#160;&#160;<span class = "symbol">&#177;&#177;</span>
</xsl:when>


<xsl:when test = "$type eq 'foreigntranslation' and $live eq 'yes'">
<a href = "{../../$link_to}/{@rsab}"><xsl:value-of select = "@rsab"/></a>&#160;&#160;<span class = "symbol">*</span>
</xsl:when>
<xsl:when test = "$type eq 'foreigntranslation' and $live eq 'no'">
<xsl:value-of select = "@rsab"/>&#160;&#160;<span class = "symbol">*</span>
</xsl:when>

<xsl:when test = "$type eq 'none' and $live eq 'no'">
<xsl:value-of select = "@rsab"/>
</xsl:when>

<xsl:otherwise>
<a href = "{../../$link_to}/{@rsab}"><xsl:value-of select = "@rsab"/></a>
</xsl:otherwise>



</xsl:choose>
</xsl:for-each>
</xsl:template>
<!-- /determine if vsab belongs to lnc, rxnorm, or is a foreign translation -->


</xsl:stylesheet>

