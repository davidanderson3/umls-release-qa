<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "2.0"
xmlns:sourcereleasedocs="http://www.nlm.nih.gov/research/umls/sourcereleasedocs/">
<xsl:output method = "xml" omit-xml-declaration = "yes" indent = "yes" use-character-maps="cm1" doctype-public = "-//W3C//DTD XHTML 1.0 Transitional//EN"  encoding="utf-8" />
<xsl:character-map name="cm1">
<xsl:output-character character="&#160;" string="&amp;nbsp;"/>
<xsl:output-character character="&#233;" string="&amp;eacute;"/>
<xsl:output-character character="&#177;" string="&amp;plusmn;"/>
</xsl:character-map>


<!-- provides functionality for writing 'source' vs. 'sources' during counting -->
<xsl:function name="sourcereleasedocs:countSources">
 <xsl:param name = "sourceCount"/>
  <xsl:choose>
    <xsl:when test = "$sourceCount gt 1">
      <xsl:value-of select = "'sources'"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select = "'source'"/>
    </xsl:otherwise>
 </xsl:choose>
</xsl:function>

<xsl:template match = "document">
    
<div id="source-doc-wrapper">
<div id="source-doc-intro">
<p>Choose a source by browsing one of the presentation tabs below</p>
</div>

<div class="jig-tabs">

<ul> 
<li><a class="ajaxlink" title="alphabet" href="#tabs-1">Alphabetical List</a></li> 
<li><a class="ajaxlink" title="category" href="#tabs-2">Restriction Categories*</a><a href="https://uts.nlm.nih.gov/help/license/licensecategoryhelp.html" target="_blank"><img style="width: 14px; height: 14px;" src="https://uts.nlm.nih.gov//images/help.png" alt="Restriction Category Help" /></a></li>
<!--<li><a class="ajaxlink" title="language" href="#tabs-3">Meaningful Use Categories*</a><a href="/research/umls/sourcereleasedocs/meaningful_use_help.html" target="_blank"><img style="width: 14px; height: 14px;" src="https://uts.nlm.nih.gov//images/help.png" alt="Meaningful Use Category Help" /></a></li>-->
<li><a class="ajaxlink" title="language" href="#tabs-4">Content Categories*</a></li>
<li><a class="ajaxlink" title="language" href="#tabs-5">Languages</a></li>
</ul>

<!--begin iterating through xml document -->
<div id = "tabs-1" class = "content">
  <h3>Alphabetical List</h3>
<xsl:apply-templates select = "letters"/>
</div>
<div id = "tabs-2" class = "content">
  <h3>Restriction Categories</h3>
<xsl:apply-templates select = "restrictions"/>
<div class = "content-footnote"><p><em>*as of 2011AA UMLS.  <br/>Source vocabularies in Category 4 are free for use in the United States. Category 3 rules apply for all other uses</em></p></div>
</div>
<!--
<div id = "tabs-3" class = "content">
  <h3>Meaningful Use Categories</h3>
<xsl:apply-templates select = "muCategories"/>
<div class = "content-footnote"><p><em>*as of 28 July 2010 (subject to change)</em></p></div>
</div>
-->
<div id = "tabs-4" class = "content">
<h3>Content Categories</h3>
<xsl:apply-templates select = "contentCategories"/>
<div class = "content-footnote"><p><em>*Content Categories come from either MeSH Headings or MeSH Entry Terms.  Only the most frequently updated sources in the Metathesaurus are categorized, <br/>and some sources may belong to more than one category.  Foreign translations have not been categorized.</em></p></div>
</div>

<div id = "tabs-5" class = "content">
  <h3>Languages</h3>
<xsl:apply-templates select = "languages"/>
</div>


</div><!-- end jig-tabs-->
</div><!-- end source-doc-wrapper-->

</xsl:template>
<!--end document element-->



<!-- letters area of document -->
<xsl:template match = "letters">
  <div class="limbox smalllimbox leftlimbox">
  <a href = "#" class = "expand">Expand All</a>&#160;&#160;<a href = "#" class = "collapse">Collapse All</a>
  <div class="limboxcontent">
    <xsl:apply-templates select = "letter"/>
  </div>
  </div>
</xsl:template>


<xsl:template match = "letter">
  <xsl:if test = "count(sources/source) gt 0">
  <h5><a class="jig-ncbitoggler" href="#"><xsl:value-of select = "@group"/>&#160;&#160;<span class = "count"><xsl:value-of select = "count(sources/source)"/>&#160;<xsl:value-of select = "sourcereleasedocs:countSources(count(sources/source))"/></span></a></h5>
  <div class = "sourcecontainer">
  <table border = "0" summary = "Table of sources from {@group}">
        <tr><th scope = "col">Source</th><th>Last Updated</th></tr>
            <xsl:apply-templates select = "sources"/>
  </table>
  </div>
  </xsl:if>
</xsl:template>
<!-- end letters area of document -->


<!--restriction area of document -->
<xsl:template match = "restrictions">
  <div class="limbox smalllimbox leftlimbox">
  <a href = "#" class = "expand">Expand All</a>&#160;&#160;<a href = "#" class = "collapse">Collapse All</a>
  <div class="limboxcontent">
    <xsl:apply-templates select = "restriction"/>
  </div>
  </div>
</xsl:template>

<xsl:template match = "restriction">
    <xsl:choose>
    <xsl:when test = "child::letters">
    <h5><a class="jig-ncbitoggler" href="#"><xsl:value-of select = "@level"/>&#160;&#160;<span class = "count"><xsl:value-of select = "count(letters/letter/sources/source)"/> sources</span></a></h5>
    <div class = "sourcecontainer">
    <xsl:apply-templates select = "letters/letter"/>
    </div>
  </xsl:when>
  <xsl:otherwise>
  <h5><a class="jig-ncbitoggler" href="#"><xsl:value-of select = "@level"/>&#160;&#160;<span class = "count"><xsl:value-of select = "count(sources/source)"/> sources</span></a></h5>
  <div class = "sourcecontainer">
    <table border = "0" summary = "Table of restriction {@level} sources">
        <tr><th scope = "col">Source</th><th>Last Updated</th></tr>
            <xsl:apply-templates select = "sources"/>
    </table>
  </div>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>
<!-- end restriction area of document -->


<!--language area of document -->

<xsl:template match = "languages">
  
<div class="limbox smalllimbox leftlimbox">
  <a href = "#" class = "expand">Expand All</a>&#160;&#160;<a href = "#" class = "collapse">Collapse All</a>
  <div class="limboxcontent">
    <xsl:apply-templates>
      <xsl:sort select = "count(child::sources)"/>
      <xsl:sort select = "@type"/>
    </xsl:apply-templates>
  </div>
  </div>
</xsl:template>


<xsl:template match = "language">
    
<xsl:choose>
   <xsl:when test = "child::letters">
    <h5><a class="jig-ncbitoggler" href="#"><xsl:value-of select = "@type"/>&#160;&#160;<span class = "count"><xsl:value-of select = "count(letters/letter/sources/source)"/>&#160;<xsl:value-of select = "sourcereleasedocs:countSources(count(letters/letter/sources/source))"/></span></a></h5>
    <div class = "sourcecontainer">
    <xsl:apply-templates select = "letters/letter"/>
    </div>
  </xsl:when>
  <xsl:otherwise>
  <h5><a class="jig-ncbitoggler" href="#"><xsl:value-of select = "@type"/>&#160;&#160;<span class = "count"><xsl:value-of select = "count(sources/source)"/>&#160;<xsl:value-of select = "sourcereleasedocs:countSources(count(sources/source))"/></span></a></h5>
  <div class = "sourcecontainer">
    <table border = "0" summary = "Table of {@type} language sources">
        <tr><th scope = "col">Source</th><th>Last Updated</th></tr>
            <xsl:apply-templates select = "sources"/>
    </table>
  </div>
  </xsl:otherwise>
</xsl:choose>
</xsl:template>
<!-- end language area of document -->

<xsl:template match = "muCategories">
  <div class="limbox smalllimbox leftlimbox">
  <a href = "#" class = "expand">Expand All</a>&#160;&#160;<a href = "#" class = "collapse">Collapse All</a>
  <div class="limboxcontent">
    <!--<xsl:apply-templates select = "categories"/>-->
    <xsl:apply-templates select = "categories"/>

  </div>
  </div>
</xsl:template>

<xsl:template match = "contentCategories">
  <div class="limbox smalllimbox leftlimbox">
  <a href = "#" class = "expand">Expand All</a>&#160;&#160;<a href = "#" class = "collapse">Collapse All</a>
  <div class="limboxcontent">
    <xsl:apply-templates select = "categories"/>
  </div>
  </div>
</xsl:template>


<xsl:template match = "categories">
    <xsl:for-each select = "category">
        <xsl:sort select = "count(sources/source)" order = "descending"/>
        <xsl:sort select = "@name"/>
        <h5><a class="jig-ncbitoggler" href="#"><xsl:value-of select = "@name"/>&#160;&#160;<span class = "count"><xsl:value-of select = "count(sources/source)"/>&#160;<xsl:value-of select = "sourcereleasedocs:countSources(count(sources/source))"/></span></a></h5>
        <div class = "sourcecontainer">
        <table summary = "Table of source in the @name category">
        <tr><th scope = "col">Source</th><th>Last Updated</th></tr>
            <xsl:apply-templates select = "sources"/>
        </table>
        </div>  
    </xsl:for-each>   
</xsl:template>


<xsl:template match = "sources">
    <xsl:for-each select = "source">
    <xsl:sort select = "." order = "ascending"/>
    <xsl:variable name = "anchortext" select = "replace(.,'.','blah')"/>
    <tr>
     <!--added fn:translate() function to get rid of periods in source names in anchor tags.  Display text will still include period (ie HL7V2.5) -->
        <td valign = "top"><a href = "http://www.nlm.nih.gov/research/umls/sourcereleasedocs/current/{translate(.,'.','')}" target = "_blank"><xsl:value-of select = "."/> (<xsl:value-of select = "@ssn"/>)</a></td>
        <td valign = "top"><xsl:value-of select = "@imeta"/></td>
    </tr>
    </xsl:for-each>   
</xsl:template>


</xsl:stylesheet>

