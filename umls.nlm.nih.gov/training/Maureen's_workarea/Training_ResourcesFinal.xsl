<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "2.0"
  xmlns:trainingresources="http://www.nlm.nih.gov/research/umls/">
<xsl:output method = "xml" omit-xml-declaration = "yes" indent = "yes" use-character-maps="cm1" doctype-public = "-//W3C//DTD XHTML 1.0 Transitional//EN"  encoding="utf-8" />
<xsl:character-map name="cm1">
<xsl:output-character character="&#160;" string="&amp;nbsp;"/>
<xsl:output-character character="&#233;" string="&amp;eacute;"/>
<xsl:output-character character="&#177;" string="&amp;plusmn;"/>
</xsl:character-map>

<!-- provides functionality for writing 'resource' vs. 'resources' during counting -->
  <xsl:function name="trainingresources:countResources">
    <xsl:param name = "resourceCount"/>
    <xsl:choose>
      <xsl:when test = "$resourceCount gt 1">
        <xsl:value-of select = "'resources'"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select = "'resource'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

<!--categories area of document -->

<xsl:template match = "categories">
<div id="source-doc-wrapper">
      <div id="source-doc-intro">
        <p>Click on a category to browse available UMLS training resources.</p>
      </div> <!--end of source-doc-intro -->
 
  <div id= "expandcollapse" class="limbox smalllimbox leftlimbox">
  <a href = "#" class = "expand">Expand All</a>&#160;&#160;<a href = "#" class = "collapse">Collapse All</a>
  <div class="limboxcontent">
        <xsl:for-each select="category">
          <xsl:sort select="@rank"/>
          <h5><a class="jig-ncbitoggler" href="#"><xsl:value-of select = "@name"/>&#160;&#160;<span class = "count"><xsl:value-of select = "count(resources/resource)"/>&#160;<xsl:value-of select = "trainingresources:countResources(count(resources/resource))"/></span></a></h5>          
          <xsl:apply-templates select="resources"/>
        </xsl:for-each>
  </div> <!--end of limboxcontent -->
  </div> <!--limbox smalllimbox leftlimbox -->
  </div>
</xsl:template><!--end categories area of document -->



<!--resources area of document -->
<xsl:template match = "resources">
<div class = "sourcecontainer">
    <table summary = "Table UMLS Training Resources">
        <tr>
						<th>Title</th>
          <th>Runtime</th>
						<th>Format</th>
				</tr>
        <xsl:for-each select="resource">
				    <tr>
				    <td width="75%"><a href = "{URL}"><xsl:value-of select="Title" /></a><span class="resourceruntime"><xsl:value-of select="Minutes[. !='']" /></span></td>
				    <td width="10%"><span class="resourceruntime"><xsl:value-of select="Runtime[. !='']" /></span></td>
				    <td width ="15%"><span class="resourceformat"><xsl:value-of select="UserFormat" /></span></td>            
      			</tr>     
    		</xsl:for-each>
    </table>
</div><!--end sourcecontainer -->

</xsl:template> <!-- end resources area of document -->


</xsl:stylesheet>