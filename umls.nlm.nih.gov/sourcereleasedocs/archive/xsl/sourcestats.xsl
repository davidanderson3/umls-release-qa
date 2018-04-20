<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "2.0" >
<xsl:variable name="lcletters">abcdefghijklmnopqrstuvwxyz</xsl:variable>
<xsl:variable name="ucletters">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>


<xsl:output method = "xml" omit-xml-declaration = "yes" indent = "yes" encoding = "utf-8" use-character-maps="cm1" doctype-public = "-//W3C//DTD XHTML 1.0 Transitional//EN"/>
<xsl:character-map name="cm1">
    <xsl:output-character character="&#160;" string="&amp;nbsp;"/>
    <xsl:output-character character="&#252;" string="&amp;uuml;"/>
    <xsl:output-character character="&#227;" string="&amp;atilde;"/>
    <xsl:output-character character="&#34;"  string="&amp;quot;"/>
</xsl:character-map>

<xsl:template match = "document">
<!--<xsl:variable name = "rsab" select = "@rsab"/>
<title><xsl:value-of select ="@rsab"/></title>
-->
<!--build menu for each page -->
<xsl:variable name = "sampletype" select = "@samples"/>
<div class="section-3" id = "maincontent">
      <ul id="menu">
         <li id="nav-1">
            <a href="index.html">Synopsis</a>
         </li>
	 <li id = "nav-2">
	 <a href="mrsab.html">MRSAB.RRF</a>
	 </li>
         <li id="nav-3">
            <a href="sourcemetastats.html">Statistics and Sample Data</a>
            <ul id="subnav-3">
               <!-- Determine type of samples for sub-navigation highlighting IMPORTANT!!! -->
	       <xsl:choose>
               <xsl:when test = "$sampletype eq 'Term Types'">
	       <li>
               <a href="termtypes.html" id = "ttys">Terms and term types</a>
	       </li>
	       </xsl:when>
               <xsl:otherwise>
	       <li>
	       <a href="termtypes.html">Terms and term types</a>
	       </li>
	       </xsl:otherwise>
	       </xsl:choose>
               <!-- Determine type of samples for sub-navigation highlighting IMPORTANT!!! -->
	       <xsl:choose>
               <xsl:when test = "$sampletype eq 'Attributes'">
	       <li>
               <a href="attributes.html" id = "atns">Attributes</a>
	       </li>
	       </xsl:when>
               <xsl:otherwise>
	       <li>
	       <a href="attributes.html">Attributes</a>
	       </li>
	       </xsl:otherwise>
	       </xsl:choose>
	        <!-- Determine type of samples for sub-navigation highlighting IMPORTANT!!! -->
	       <xsl:choose>
               <xsl:when test = "$sampletype eq 'Relationships'">
	       <li>
               <a href="relationships.html" id = "rels">Relationships</a>
	       </li>
	       </xsl:when>
               <xsl:otherwise>
	       <li>
	       <a href="relationships.html">Relationships</a>
	       </li>
	       </xsl:otherwise>
	       </xsl:choose>
	       <!-- Determine type of samples for sub-navigation highlighting IMPORTANT!!! -->
	       <xsl:choose>
               <xsl:when test = "$sampletype eq 'Semantic Types'">
	       <li>
               <a href="semtypes.html" id = "stys">Semantic Types</a>
	       </li>
	       </xsl:when>
               <xsl:otherwise>
	       <li>
	       <a href="semtypes.html">Semantic Types</a>
	       </li>
	       </xsl:otherwise>
	       </xsl:choose>
	       <!-- Determine type of samples for sub-navigation highlighting IMPORTANT!!! -->
	       <xsl:choose>
               <xsl:when test = "$sampletype eq 'Overlap'">
	       <li>
               <a href="overlap.html" id = "ovr">Source Overlap</a>
	       </li>
	       </xsl:when>
               <xsl:otherwise>
	       <li>
	       <a href="overlap.html">Source Overlap</a>
	       </li>
	       </xsl:otherwise>
	       </xsl:choose>
              </ul>
         </li>
	 <li id = "nav-4">
	 <a href="representation.html">Representation</a>
	    <ul id = "subnav-4">
	    <li><a href = "sourcerepresentation.html">Source Representation</a></li>
	    <li><a href = "metarepresentation.html">Metathesaurus Representation</a></li>
	   </ul>
	 </li>
      </ul>
<!--/build menu for each page -->
<xsl:choose>
    <xsl:when test = "samples/sample">
<div id = "counts">

<!--<span class = "sectionlabel">Counts</span><br/>-->
<h2>Counts</h2>
<xsl:choose>
<xsl:when test = "$sampletype eq 'Relationships' or $sampletype eq 'Term Types' or $sampletype eq 'Attributes'">
<p class = "jumptags">&#160;(skip to:&#160;<a href = "#explain">notes</a>&#160;&#160;<a href = "#samples">samples</a>)</p>
</xsl:when>
<xsl:when test = "$sampletype eq 'Semantic Types' or $sampletype eq 'Overlap'">
<p class = "jumptags">&#160;(skip to:&#160;<a href = "#explain">notes</a>)</p>
</xsl:when>
</xsl:choose>
<div id = "placeholder" style = "display:none;">placeholder</div>
<xsl:apply-templates select = "samples"/>
</div>

<div id = "explain">
<!--determine what kind of data we are sampling, and then write the explanatory comments -->
<xsl:choose>
<!-- Explanation area for term types -->
<xsl:when test = "$sampletype eq 'Term Types'">
<!--<span class = "sectionlabel">Background</span><br/>-->
<h2>Notes</h2>
<ul>
   <li>A term type indicates the role an atom plays in its source</li>
   <li>An atom is the smallest unit of naming in a source; that is, a specific string with specific code values and identifiers from a specific source.</li>
   <li>Every atom has a separate row in MRCONSO.RRF. Every atom is assigned a term type.</li>
   <li>Term types are assigned based on source documentation or NLM understanding of the source.</li>
   <li>Sample data are provided for every term type in the source (see below).</li>
   <li>See <a href = "http://www.nlm.nih.gov/research/umls/metab3.html#mrdoc_TTY" target = "_blank">Appendix B3</a> for a list of all term type abbreviations and their full names.</li>
   <li>See <a href = "http://www.nlm.nih.gov/research/umls/metab5.html#sb5_0" target = "_blank">Appendix B.5</a> for a list of sources and their associated term types, listed in default order of precedence.</li>
   <li>See <a href = "http://www.ncbi.nlm.nih.gov/books/NBK9684/#ch02.I23_Concepts_Concept_Names_and_Thei" target = "_blank">Section 2.3 of the UMLS Reference Manual</a> for more information on terms and term types in the UMLS Metathesaurus.</li>
</ul>

</xsl:when>
<!--/Explanation area for term types -->

<!-- Explanation area for attributes -->
<xsl:when test = "$sampletype eq 'Attributes'">
<!--<span class = "sectionlabel">Background</span><br/>-->
<h2>Notes</h2>
<ul>
   <li>Attributes included every discrete piece of information about a concept, an atom, or a relationship that is not part of the basic Metathesaurus <a href = "http://www.nlm.nih.gov/research/umls/meta2.html#s2_2" target = "_blank">concept structure</a> or distributed in one of the <a href = "http://www.nlm.nih.gov/research/umls/meta2.html#s2_3" target = "_blank">relationship files</a>.</li>
   <li>Attribute Names (ATN) are based on source documentation or NLMs understanding of the source.</li>
   <li>Sample attribute values (ATV) are provided for each ATN in the source.</li>
   <li>Sample data highlight examples of ATNs and Attribute Values (ATV) in MRSAT.RRF.</li>
   <li>See <a href= 'http://www.nlm.nih.gov/research/umls/metab2.html#sb2_0' target = "_blank"><b>Appendix B.2</b></a> for a complete list of attribute names.</li>
   <li>See <a href= 'http://www.ncbi.nlm.nih.gov/books/NBK9684/#ch02.I25_Attributes_and_Attribute_Identi' target = "_blank"> Section 2.5 of the UMLS Reference Manual</a> for more information on attributes in the UMLS Metathesaurus.</li>
</ul>
</xsl:when>
<!--/Explanation area for attributes -->


<!--Explanation area for semantic types -->
<xsl:when test = "$sampletype eq 'Semantic Types'">
<h2>Notes</h2>
<ul>
<li>Semantic Types are a set of 135 broad subject areas that provide a consistent categorization of concepts represented in the Metathesaurus.  Semantic Types are arranged into two mutually exclusive hierarchies, <a href = "http://www.nlm.nih.gov/research/umls/META3_current_semantic_types.html" target = "_blank"> Entity and Event</a>.</li>
<li>Semantic Types help summarize the content of a source.</li>
<li>Each Metathesaurus concept is assigned at least one semantic type, many have more than one.</li>
<li>The report displays the most frequently assigned semantic types in descending order, up to a total of 1%.</li>
<li>The 'Count' field below is the number of concepts assigned a specific semantic type and containing at least one atom from the source.</li>
<li>The 'Percentage' field below is the count relative to the total number of concepts that contain an atom from that source.</li>
<li>The MRSTY file contains one row for each Semantic Type assigned to each concept.  All Metathesaurus concepts have at least one entry in this file.</li>
<li>See <a href = "http://www.ncbi.nlm.nih.gov/books/NBK9679/" target = "_blank">Chapter 5 of the UMLS Reference Manual</a> to read more about the Semantic Network</li>
</ul>
</xsl:when>
<!--/Explanation area for semantic types -->

<!--Explanation area for source overlap -->
<xsl:when test = "$sampletype eq 'Overlap'">
<h2>Notes</h2>
<ul>
<li>Source overlap provides a high level comparison of content and meaning between the reported source and other sources in the Metathesaurus.</li>
<li>The report displays source overlap of 1% or greater.</li>
<li>Number of shared concepts: number of concepts that contain at least one atom from each of two compared sources.</li>
<li>Percentage Overlap: percentage of shared concepts relative to the total number of concepts for this source.</li>
</ul>
</xsl:when>
<!--/Explanation area for source overlap -->


<!-- Explanation area for relationships -->
<xsl:when test = "$sampletype eq 'Relationships'">
<!--<span class = "sectionlabel">Background</span><br/>-->
<h2>Notes</h2>
<p>Sample data are taken from MRREL.RRF.  Associated strings(str) from MRCONSO.RRF are also included</p>
<ul>
    <li>See relationships definitions below.</li>
    <li>Every relationship has a separate row in MRREL.RRF.</li>
    <li>Relationships are assigned based on source documentation or NLM understanding of the source.</li>
    <li>Metathesaurus relationships in MRREL.RRF are read from <b>right to left</b>.</li>
    <li>All relationships in MRREL.RRF are expressed in both directions, for example: 'has_ingredient/ingredient_of'. Inverse relationships in each pair are indicated on this page.</li>
    <li>An asterisk (*) in the relationship attribute column indicates that no relationship attribute (RELA) was assigned.</li>
    <li>Every even row (indicated by orange colored rel/rela pairs) indicates that the current row is an inverse relationship of the row above it.</li>
    <li>See Section <a href = "http://www.ncbi.nlm.nih.gov/books/NBK9684/#ch02.I24_Relationships_and_Relationship" target = "_blank">2.4 of the UMLS Reference Manual</a> for more information on Relationships in the UMLS Metathesaurus.</li>
</ul>
<!--<span class = "sectionlabel">Relationship Definitions</span><br/>-->
<h2>Relationship Definitions</h2>
<ul>
   <li><b>AQ</b>: allowed qualifier</li>
   <li><b>CHD</b>: has child (narrower hierarchical term)</li>
   <li><b>DEL</b>: deleted concept</li>
   <li><b>PAR</b>: has parent (broader hierarchical term)</li>
   <li><b>QB</b>: can be qualifier by</li>
   <li><b>RB</b>: has a broader relationship</li>
   <li><b>RL</b>: has similar or like relationship </li>
   <li><b>RN</b>: has narrower relationship </li>
   <li><b>RO</b>: has relationship other than synonymous, narrower or broader</li>
   <li><b>RQ</b>: related and possibly synonymous</li>
   <li><b>SIB</b>: has sibling</li>
   <li><b>SY</b>: source-asserted synonymy</li>
</ul>
</xsl:when>
<!--/Explanation area for relationships -->
<xsl:otherwise>
<p></p>
</xsl:otherwise>
</xsl:choose>
<!--/determine what kind of data we are sampling, and then write the explanatory comments -->
</div>





<xsl:choose>

<xsl:when test = "$sampletype eq 'Relationships' or $sampletype eq 'Term Types' or $sampletype eq 'Attributes'">
<div id = "samples">
<!--<span class = "sectionlabel">Sample Data</span><br/>-->
<h3>Sample Data</h3>
<xsl:apply-templates select = "samples/sample"/>

</div>

</xsl:when>
<xsl:otherwise><p></p>
</xsl:otherwise>
</xsl:choose>

</xsl:when>
<xsl:otherwise>   
    <!--if there are no counts of anything, let user know -->
<div class = "warning">
 <p>There are no <xsl:value-of select = "translate($sampletype,$ucletters,$lcletters)"/> data associated with this source</p>   
</div>
   
</xsl:otherwise>
</xsl:choose>
</div>

</xsl:template>


<xsl:template match = "samples">
<xsl:variable name = "sampletype" select = "../@samples"/>

<table summary = "Metadata Stats: This is a by-source tabulation of various data types in the UMLS Metathesaurus">
<xsl:apply-templates select = "labels"/>


<xsl:for-each select = "sample">

<xsl:choose>
<xsl:when test = "$sampletype eq 'Term Types'">
<tr>
<!--Original way of generating sample links and jump tags.<td class = 'metalabel'><a href = "#{@id}"><xsl:value-of select = "translate(@samplename,$lcletters,$ucletters)"/></a></td>-->
<td class = 'metalabel' valign = 'top' scope = 'row'><a href = "#{@id}" title = "Samples of {@samplename} term types. If you are using the tab key for navigation, press Enter or Return to go to sample data" onkeyup = "checkKeyType('{@id}',event);" onclick = "document.getElementById('placeholder').innerHTML = '';document.getElementById('placeholder').style.display = 'none';new Effect.Appear('placeholder');drawContentBox('{@id}',event,this.innerHTML);return false;"><xsl:value-of select = "translate(@samplename,$lcletters,$ucletters)"/></a><noscript><a href="#{@id}"></a></noscript></td>
<td class = 'definition' valign = 'top'><xsl:value-of select = "@definition"/></td>
<td class = 'count' valign = 'top'><xsl:value-of select = "@count"/></td>
</tr>
</xsl:when>
<xsl:when test = "$sampletype eq 'Attributes'">
<tr>
<!--Original way of generating sample links and jump tags.<td class = 'metalabel'><a href = "#{@id}"><xsl:value-of select = "translate(@samplename,$lcletters,$ucletters)"/></a></td>-->
<td class = 'metalabel' valign = 'top' scope = 'row'><a href = "#{@id}" title = "Samples of {@samplename} attributes. If you are using the tab key for navigation, press Enter or Return to go to sample data" onkeyup = "checkKeyType('{@id}',event);" onclick = "document.getElementById('placeholder').innerHTML = '';document.getElementById('placeholder').style.display = 'none';new Effect.Appear('placeholder');drawContentBox('{@id}',event,this.innerHTML);return false;"><xsl:value-of select = "translate(@samplename,$lcletters,$ucletters)"/></a><noscript><a href="#{@id}"></a></noscript></td>
<td class = 'definition' valign = 'top'><xsl:value-of select = "@definition"/></td>
<td class = 'count' valign = 'top'><xsl:value-of select = "@count"/></td>
</tr>
</xsl:when>
<xsl:when test = "$sampletype eq 'Relationships'">
<xsl:variable name = "mid_relname" select = "translate(@id, '?', '_')"/>
<tr>
<!--Original way of generating sample links and jump tags.<td class = 'metalabel'><a href = "#{@id}"><xsl:value-of select = "translate(@samplename,$lcletters,$ucletters)"/></a></td>-->
<td class = 'metalabel' valign = 'top' scope = 'row'><a href = "#{$mid_relname}" title = "Samples of {@samplename} relationships. If you are using the tab key for navigation, press Enter or Return to go to sample data" onkeyup = "checkKeyType('{$mid_relname}',event);" onclick = "document.getElementById('placeholder').innerHTML = '';document.getElementById('placeholder').style.display = 'none';new Effect.Appear('placeholder');drawContentBox('{$mid_relname}',event,this.innerHTML);return false;"><xsl:value-of select = "translate(@samplename,$lcletters,$ucletters)"/></a><noscript><a href="#{$mid_relname}"></a></noscript></td>
<td class = 'attribute' valign = 'top'><xsl:value-of select = "@attribute"/></td>
<td class = 'count' valign = 'top'><xsl:value-of select = "@count"/></td>
</tr>
</xsl:when>

<xsl:when test = "$sampletype eq 'Semantic Types'">
<tr>
<td class = 'metalabel' valign = 'top' scope = 'row'><xsl:value-of select = "translate(@samplename,$lcletters,$ucletters)"/></td>
<td class = 'count' valign = 'top'><xsl:value-of select = "@count"/></td>
<td class = 'count' valign = 'top'><xsl:value-of select = "@percentage"/></td>
</tr>
</xsl:when>

<xsl:when test = "$sampletype eq 'Overlap'">
<tr>
<td class = 'metalabel' valign = 'top' scope = 'row'><xsl:value-of select = "translate(@samplename,$lcletters,$ucletters)"/></td>
<td class = 'count' valign = 'top'><xsl:value-of select = "@count"/></td>
<td class = 'count' valign = 'top'><xsl:value-of select = "@percentage"/></td>
</tr>
</xsl:when>


<xsl:otherwise>
<xsl:text></xsl:text>
</xsl:otherwise>
</xsl:choose>
</xsl:for-each>
</table>
</xsl:template>


<!-- handle each sample type and build a table from it -->
<xsl:template match = "samples/sample">
    
<xsl:variable name = "mid_relname" select = "translate(@id, '?', '_')"/>
    
<!--<div class = "sample" id = "$relname"><h4><xsl:value-of select = "@samplename"/></h4>&#160;&#160;<a href = "#counts">(return to top)</a>-->
<div class = "sample" id = "{$mid_relname}"><h4><xsl:value-of select = "@samplename"/></h4>&#160;&#160;<a href = "#counts">(return to top)</a>
<table summary = "This table provides sample rows for {$mid_relname} data elements in the UMLS Metathesaurus">
<tr>
<!--builds headers for each sample table -->
<xsl:apply-templates select = "row[1]"/>
<!--/builds headers for each sample table -->
</tr>

<!-- determines whether the sample is a standard sample row or an inverse relationship row for mrrel -->
<xsl:for-each select = "row">
<xsl:variable name = "rowtype" select = "@type"/>
<xsl:choose>
<xsl:when test = "$rowtype eq 'standard'">
<tr class = "samplerow">
<xsl:apply-templates select = "field"/>
</tr>
</xsl:when>
<xsl:when test = "$rowtype eq 'inverse'">
<tr class = "inversesamplerow">
<xsl:apply-templates select = "field"/>
</tr>
</xsl:when>
</xsl:choose>
</xsl:for-each>
<!-- /determines whether the sample is a standard sample row or an inverse relationship row for mrrel -->
</table>
</div>
</xsl:template>
<!-- /handle each sample type and build a table from it -->




<!-- displays each field in sample row -->
<xsl:template match = 'field'>
<td class = "{translate(@type,$ucletters,$lcletters)}" valign = "top"><xsl:value-of select = "." disable-output-escaping="yes"/></td>
</xsl:template>
<!-- /displays each field in sample row -->



<!--builds labels for counts area table -->
<xsl:template match = 'labels'>
<tr>
<xsl:for-each select = 'label'>
<th class = '{@type}' scope = 'col'><xsl:value-of select = "." disable-output-escaping="yes"/></th>
</xsl:for-each>
</tr>
</xsl:template>
<!--/builds labels for counts area table -->



<!--builds headers for each sample table -->
<xsl:template match = "row[1]">
<xsl:for-each select = "field">
<th scope = 'col'>
<xsl:value-of select = "@type"/>
</th>
</xsl:for-each>
</xsl:template>
<!--/builds headers for each sample table -->



</xsl:stylesheet>
