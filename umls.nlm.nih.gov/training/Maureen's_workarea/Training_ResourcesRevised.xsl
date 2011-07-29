<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "2.0"
xmlns:sourcereleasedocs="http://www.nlm.nih.gov/research/umls/sourcereleasedocs/">
<xsl:output method = "xml" omit-xml-declaration = "yes" indent = "yes" use-character-maps="cm1" doctype-public = "-//W3C//DTD XHTML 1.0 Transitional//EN"  encoding="utf-8" />
<xsl:character-map name="cm1">
<xsl:output-character character="&#160;" string="&amp;nbsp;"/>
<xsl:output-character character="&#233;" string="&amp;eacute;"/>
<xsl:output-character character="&#177;" string="&amp;plusmn;"/>
</xsl:character-map>


<!-- provides functionality for writing 'source' vs. 'sources' during counting -->
<!--<xsl:function name="sourcereleasedocs:countSources">
 <xsl:param name = "sourceCount"/>
  <xsl:choose>
    <xsl:when test = "$sourceCount gt 1">
      <xsl:value-of select = "'sources'"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select = "'source'"/>
    </xsl:otherwise>
 </xsl:choose>
</xsl:function> -->

<xsl:template match = "categories">
<!-- Remove the following section before inserting into Teamsite  -->
  <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
      <link href="/styles/reset.css" rel="stylesheet" type="text/css" media="all" />
      <link href="/styles/master.css" rel="stylesheet" type="text/css" media="screen" />
      <!--Call jQuery-->
      <script type="text/javascript" src="/scripts/jquery-min.js"></script>
      
      <!--[if lte IE 8]>
        <script type="text/javascript" src="/scripts/PIE.js"></script>
        <![endif]-->
      
      <script type="text/javascript" src="/scripts/master.js"></script>
      
      <!--[if lte IE 8]>
        <link href="/styles/ie.css" rel="stylesheet" media="screen">
        <![endif]-->
      
      <script type = "text/javascript" src = "/scripts/jquery-min.js" language = "javascript"></script>
      <script type = "text/javascript" src = "http://www.ncbi.nlm.nih.gov/core/jig/1.5.2/js/jig.min.js" language = "javascript"></script>
      <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.11/jquery-ui.min.js" language = "javascript" ></script>
      
      <!--[if IE]>
        <script type="text/javascript" src="/scripts/PIE.js"></script>
        <![endif]--> 
      <script type= "text/javascript">   
var $ = jQuery.noConflict();
</script>
      
      <script type = "text/javascript"> 
$(document).ready(function() {
 
$('a.jig-ncbitoggler').click(function(event) {
  //$(this).parent().next().css('background-color','red');
  var sourcecontainer = $(this).parent().next();
  $(sourcecontainer).animate({'background-color' : '#ff7373'}, 'fast');
  $(sourcecontainer).animate({'background-color' : '#ffffff'}, 'slow');
  
  //alert("you clicked me!");
  
});
});
</script>
      <script type = "text/javascript"> 
$(document).ready(function() {
 
  $('a.ajaxlink').click(function(event) {
    var myLink = $(this).attr("href");
    //alert(myLink.substr(1));
   $('div.content').each(function() {
      
      if ($(this).attr('id') == myLink.substr(1)) {
	$(this).attr('style','display:block');
	//alert($(this).attr('id'));
      }
       
       else {
	$(this).attr('style','display:none');
	
       }
       
   });  
     
  });
});
</script>
      
      <script type = "text/javascript"> 
$(document).ready(function() {
 $('a.expand').click(function(event) {
 
  //alert ($(this).parent(".content").attr("id"));
  var myDiv = $(this).parents(".content").attr("id");
     
 
  $('#'+myDiv).find('.jig-ncbitoggler').each(function() {
     
   $(this).ncbitoggler('open');
 
     });
  
 
});
});
 
</script>
      
      <script type = "text/javascript"> 
$(document).ready(function() {
 $('a.collapse').click(function(event) {
 
  //alert ($(this).parent(".content").attr("id"));
  var myDiv = $(this).parents(".content").attr("id");
     
 
  $('#'+myDiv).find('.jig-ncbitoggler').each(function() {
     
   $(this).ncbitoggler('close');
 
     });
  
 
});
});
 
</script>
      
      
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
      
      
      
      <title>UMLS Training Resources</title>
    </head>
<body>
<!-- Remove the above section before inserting into Teamsite  -->
<div id="source-doc-wrapper">
    <h1>UMLS Training Resources</h1>
    <div id="source-doc-intro">
      <p>Click on a category to view available resources.</p>
    </div>
    <a href="#" class="expand">Expand All</a>&amp;nbsp;&amp;nbsp;<a href="#" class="collapse">Collapse All</a>

  <table border="1" summary="ncbitoggler table of UMLS resources">
      <caption>UMLS Training Resources</caption>       
      <thead>
        <tr>
          <!--      <th>Resource</th>-->
          <th scope="col">Category</th>
          <th>Subcategory</th>
          <th>Title</th>
          <th>Date</th>
          <th>Runtime</th>
          <th>Format</th>
          <th>Requires</th>
        </tr>
      </thead>
      
      <tbody>
        <xsl:for-each select="category">
          <xsl:sort select="@rank"/>
          
          <tr>
            <!--   <td class="ui-widget-content"><xsl:value-of select="@Project" /></td>-->
            <h3><a class="jig-ncbitoggler ui-ncbitoggler">
              <span class="ui-ncbitoggler-master-text"><td class="ui-widget-content"><xsl:value-of select="category" /></td></span>
              <!--    <span class="ui-icon ui-icon-triangle-1-e"></span>-->
            </a></h3>
            <div class="ui-ncbi-toggler-slave">
<!--          <td class="ui-widget-content"><xsl:value-of select="Subcategory" /></td> -->
              <td class="ui-widget-content"><xsl:value-of select="Title" /></td>
              <td class="ui-widget-content"><xsl:value-of select="Format" /></td>
              <td class="ui-widget-content"><xsl:value-of select="Minutes" /></td>
              <td class="ui-widget-content"><xsl:value-of select="Date" /></td>
<!--          <td class="ui-widget-content"><xsl:value-of select="Requires" /></td>-->
            </div>
          </tr>
          
        </xsl:for-each>
      </tbody>
    </table>
    

<!--begin iterating through xml document
<div id = "tabs-1" class = "content">
  <h3>Alphabetical List</h3>
<xsl:apply-templates select = "letters"/>
</div>
<div id = "tabs-2" class = "content">
  <h3>Restriction Categories</h3>
<xsl:apply-templates select = "restrictions"/>
<div class = "content-footnote"><p><em>*as of 2011AA UMLS.  <br/>Source vocabularies in Category 4 are free for use in the United States. Category 3 rules apply for all other uses</em></p></div>
</div>
<div id = "tabs-3" class = "content">
  <h3>Meaningful Use Categories</h3>
<xsl:apply-templates select = "muCategories"/>
<div class = "content-footnote"><p><em>*as of 28 July 2010 (subject to change)</em></p></div>
</div>
<div id = "tabs-4" class = "content">
<h3>Content Categories</h3>
<xsl:apply-templates select = "contentCategories"/>
<div class = "content-footnote"><p><em>*Content Categories come from either MeSH Headings or MeSH Entry Terms.  Only the most frequently updated sources in the Metathesaurus are categorized, <br/>and some sources may belong to more than one category.  Foreign translations have not been categorized.</em></p></div>
</div>
<div id = "tabs-5" class = "content">
  <h3>Languages</h3>
<xsl:apply-templates select = "languages"/>
</div> -->


</div><!-- end source-doc-wrapper-->

</body>
  </html>

</xsl:template>
<!--end category element-->



<!-- letters area of document -->
<xsl:template match = "letters">
  <div class="limbox smalllimbox leftlimbox">
  <a href = "#" class = "expand">Expand All</a>&#160;&#160;<a href = "#" class = "collapse">Collapse All</a>
  <div class="limboxcontent">
    <xsl:apply-templates select = "letter"/>
  </div>
  </div>
</xsl:template>

<!--
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
</xsl:template> -->
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

<!--
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
</xsl:template> -->
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

<!--
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
-->
  
<!--
<xsl:template match = "sources">
    <xsl:for-each select = "source">
    <xsl:sort select = "." order = "ascending"/>
    <tr>
        <td valign = "top"><a href = "http://www.nlm.nih.gov/research/umls/sourcereleasedocs/current/{.}" target = "_blank"><xsl:value-of select = "."/> (<xsl:value-of select = "@ssn"/>)</a></td>
        <td valign = "top"><xsl:value-of select = "@imeta"/></td>
    </tr>
    </xsl:for-each>   
</xsl:template>
-->


</xsl:stylesheet>

