<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "2.0" >

<xsl:output method = "xml" omit-xml-declaration = "yes" indent = "yes"  doctype-public = "-//W3C//DTD XHTML 1.0 Transitional//EN"  encoding="utf-8" />

<xsl:template match = "document">

<div class="section-2">
      <ul id="menu">
      <li id="nav-1"><a href="index.html">Synopsis</a></li>
      <li id="nav-2"><a href="mrsab.html">MRSAB.RRF</a></li>
      <li id="nav-3"><a href="sourcemetastats.html">Statistics and Sample Data</a><ul id="subnav-3">
            <li><a href="termtypes.html" id="ttys">Terms and term types</a></li>
            <li><a href="attributes.html">Attributes</a></li>
            <li><a href="relationships.html">Relationships</a></li>
	    <li><a href="semtypes.html">Semantic Types</a></li>
	    <li><a href="overlap.html">Source Overlap</a></li>
         </ul>
      </li>
     
	 <li id = "nav-4">
	 <a href="representation.html">Representation</a>
	    <ul id = "subnav-4">
	    <li id = "sourcerep"><a href = "sourcerepresentation.html">Source Representation</a></li>
	    <li id = "umlsrep"><a href = "metarepresentation.html">Metathesaurus Representation</a></li>
	   </ul>
	 </li>
   </ul>
<div id = "mrsab">
<span class = "sectionlabel">MRSAB.RRF</span>
<table class = "mrsabviewer" cellspacing = "10" summary = "Table of fields and their respective values in MRSAB.RRF file from the UMLS Metathesaurus">
<tr><th scope = "col">Field</th><th scope = "col">Value</th></tr>
<xsl:for-each select = "field">
<xsl:variable name = "fieldtype" select = "@type"/>

<xsl:choose>
   <xsl:when test = "$fieldtype eq 'SLC'">
   <tr class = "slcsccscit">
   <td valign = "top" class = "field"><a href = "../../mrsabfields.html" target = "_blank" title = "{@definition}"><xsl:value-of select = "@type"/></a></td>
   <td valign = "top" class = "slcsccscit"><xsl:apply-templates select = "subfield"/></td>
   </tr>
   </xsl:when>
   <xsl:when test = "$fieldtype eq 'SCC'">
   <tr class = "slcsccscit">
   <td valign = "top" class = "field"><a href = "../../mrsabfields.html" target = "_blank" title = "{@definition}"><xsl:value-of select = "@type"/></a></td>
   <td valign = "top" class = "slcsccscit"><xsl:apply-templates select = "subfield"/></td>
   </tr>
   </xsl:when>
   <xsl:when test = "$fieldtype eq 'SCIT'">
   <tr class = "slcsccscit">
   <td valign = "top" class = "field"><a href = "../../mrsabfields.html" target = "_blank" title = "{@definition}"><xsl:value-of select = "@type"/></a></td>
   <td valign = "top" class = "slcsccscit"><xsl:apply-templates select = "subfield"/></td>
   </tr>
   </xsl:when>
   <xsl:otherwise>
   <tr>
   <td valign = "top" class = "field"><a href = "../../mrsabfields.html" target = "_blank" title = "{@definition}"><xsl:value-of select = "@type"/></a></td>
   <td valign = "top" class = "data"><xsl:value-of select = "."/></td>
   </tr>
   </xsl:otherwise>
</xsl:choose>
</xsl:for-each>
</table>
</div>
</div>
</xsl:template>



<xsl:template match = "subfield">
<xsl:for-each select = ".">
<b><xsl:value-of select = "@type"/></b>:  <xsl:value-of select = "." disable-output-escaping = "yes"/><br/>
</xsl:for-each>
</xsl:template>






</xsl:stylesheet>
