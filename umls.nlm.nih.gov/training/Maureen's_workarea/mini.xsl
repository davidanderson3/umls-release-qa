<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "2.0"
xmlns:sourcereleasedocs="http://www.nlm.nih.gov/research/umls/sourcereleasedocs/">
<xsl:output method = "xml" omit-xml-declaration = "yes" indent = "yes" use-character-maps="cm1" doctype-public = "-//W3C//DTD XHTML 1.0 Transitional//EN"  encoding="utf-8" />
<xsl:character-map name="cm1">
<xsl:output-character character="&#160;" string="&amp;nbsp;"/>
<xsl:output-character character="&#233;" string="&amp;eacute;"/>
<xsl:output-character character="&#177;" string="&amp;plusmn;"/>
</xsl:character-map>

  <xsl:template match = "categories">

  <table border="1" summary="Table of UMLS resources" color="blue" bgcolor="black">
      <caption color="green">UMLS Training Resources</caption>       
      <thead color="green">
        <tr>
          <th scope="col">Category</th>
          <th>Title</th>
          <th>Date</th>
          <th>Runtime</th>
          <th>Format</th>
        </tr>
      </thead>
      
      <tbody>
        <xsl:for-each select="category">
          <xsl:sort select="@rank"/>
          <xsl:apply-templates select="resources"/>
        </xsl:for-each>
      </tbody>
    </table>	
  </xsl:template>
  
  <xsl:template match = "resources">
    <xsl:for-each select="resource">
      <tr>
        <td><xsl:value-of select="../../@name" /></td>
        <td><xsl:value-of select="Title" /></td>
        <td><xsl:value-of select="Date" /></td>
        <td><xsl:value-of select="Minutes" />&#160;minutes</td>
        <td><xsl:value-of select="Format" /></td>            
      </tr>     
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>