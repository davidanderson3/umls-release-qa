<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "2.0"
xmlns:sourcereleasedocs="http://www.nlm.nih.gov/research/umls/sourcereleasedocs/">
<xsl:output method = "xml" omit-xml-declaration = "yes" indent = "yes" use-character-maps="cm1" doctype-public = "-//W3C//DTD XHTML 1.0 Transitional//EN"  encoding="utf-8" />
<xsl:character-map name="cm1">
<xsl:output-character character="&#160;" string="&amp;nbsp;"/>
<xsl:output-character character="&#233;" string="&amp;eacute;"/>
<xsl:output-character character="&#177;" string="&amp;plusmn;"/>
</xsl:character-map>





<div id="source-doc-wrapper">
    <h1>UMLS Training Resources</h1>
    <div id="source-doc-intro">
      <p>Click on a category to view available resources.</p>
    </div>
    <a href="#" class="expand">Expand All</a>&amp;nbsp;&amp;nbsp;<a href="#" class="collapse">Collapse All</a>
		

  <table border="1" summary="Table of UMLS resources">
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
        </xsl:for-each>
      </tbody>
    </table>	
		
</xsl:stylesheet>
					
          <tr>
            <!--   <td class="ui-widget-content"><xsl:value-of select="category/resources/resource/Project" /></td>-->
            <h3><a class="jig-ncbitoggler ui-ncbitoggler">
              <span class="ui-ncbitoggler-master-text"><td class="ui-widget-content"><xsl:value-of select="category" /></td></span>
              <!--    <span class="ui-icon ui-icon-triangle-1-e"></span>-->
            </a></h3>
            <div class="ui-ncbi-toggler-slave">
<!--          <td class="ui-widget-content"><xsl:value-of select="category/resources/resource/Subcategory" /></td> -->
              <td class="ui-widget-content"><xsl:value-of select="category/resources/resource/Title" /></td>
              <td class="ui-widget-content"><xsl:value-of select="category/resources/resource/Format" /></td>
              <td class="ui-widget-content"><xsl:value-of select="category/resources/resource/Minutes" /></td>
              <td class="ui-widget-content"><xsl:value-of select="category/resources/resource/Date" /></td>
<!--          <td class="ui-widget-content"><xsl:value-of select="category/resources/resource/Requires" /></td>-->
            </div>
          </tr>
          
        </xsl:for-each>
      </tbody>
    </table>				
					
</div><!-- end source-doc-wrapper-->
</xsl:template>
<!--end document element-->

 <!-- <h5><a class="jig-ncbitoggler" href="#">
				 <xsl:value-of select = "@type"/>&#160;&#160;<span class = "count">
				 <xsl:value-of select = "count(resources/resource)"/>&#160;<xsl:value-of select = "sourcereleasedocs:countSources(count(resources/resource))"/>
				 </span></a></h5>
  <div class = "sourcecontainer">
    <table border = "0" summary = "Table of {@type} language sources">
    <tr><th scope = "col">Source</th><th>Last Updated</th></tr>
    <xsl:apply-templates select = "resources"/>
    </table>
  </div>-->



<xsl:template match = "resources">
    <xsl:for-each select = "resource">
    <xsl:sort select = "." order = "ascending"/>
    <tr>
        <td valign = "top"><a href = "http://www.nlm.nih.gov/research/umls/sourcereleasedocs/current/{.}" target = "_blank"><xsl:value-of select = "."/> (<xsl:value-of select = "@ssn"/>)</a></td>
        <td valign = "top"><xsl:value-of select = "@imeta"/></td>
    </tr>
    </xsl:for-each>   
</xsl:template>

<xsl:template match = "contentCategories">
  <div class="limbox smalllimbox leftlimbox">
  <a href = "#" class = "expand">Expand All</a>&#160;&#160;<a href = "#" class = "collapse">Collapse All</a>
  <div class="limboxcontent">
    <xsl:apply-templates select = "categories"/>
  </div>
  </div>
</xsl:template>

<!--<xsl:template match = "categories">
    <xsl:for-each select = "category">
        <xsl:sort select = "count(resources/resource)" order = "descending"/>
        <xsl:sort select = "@name"/>
        <h5><a class="jig-ncbitoggler" href="#"><xsl:value-of select = "@name"/>&#160;&#160;<span class = "count"><xsl:value-of select = "count(sources/source)"/>&#160;<xsl:value-of select = "sourcereleasedocs:countSources(count(sources/source))"/></span></a></h5>
        <div class = "sourcecontainer">
        <table summary = "Training Resources in the @name category">
        <tr><th scope = "col">Source</th><th>Last Updated</th></tr>
            <xsl:apply-templates select = "sources"/>
        </table>
        </div>  
    </xsl:for-each>   
</xsl:template>-->


</xsl:stylesheet>