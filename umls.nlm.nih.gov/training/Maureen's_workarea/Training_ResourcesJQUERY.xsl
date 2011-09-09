<?xml version="1.0" encoding="utf-8"?><!-- DWXMLSource="file:///C|/Documents and Settings/puttaswamygowr/My Documents/XSLT/Training_Resources.xml" -->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="utf-8" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>
    

<xsl:template match="/">

<html>
<head>
      <meta name="ncbigrid" content="isSortable: true, columnTypes: ['str', 'str','str','date','num', 'str','str','str'],sortColumn: 1"/>    
	     <script type="text/javascript" src="http://www.ncbi.nlm.nih.gov/core/jig/1.5.2/js/jig.min.js" language="javascript"></script>   
      <style type="text/css">
            div.ui-ncbigrid-scroll {
                width: 100px;
                font-size: 85%; /* override default font-size of grid, do not override default font-family. That is an NCBI standard */
            }
      </style>

</head>
  <body>
  <h2></h2>
  <div class="jig-ncbigrid-scroll ui-ncbigrid-scroll ui-grid ui-widget ui-widget-content ui-corner-all">

   
  <div class="ui-ncbigrid-inner-div">
   
   <table style="border: 1; color: #9acd32;">
     <caption>UMLS Training</caption>       
     <thead>
     <tr>
      <th>Resource</th>
      <th>Category</th>
      <th>Subcategory</th>
      <th>Title</th>
      <th>Date</th>
      <th>Runtime</th>
      <th>Format</th>
      <th>Requires</th>
     </tr>
      </thead>
      
      <tbody>
  <xsl:for-each select="root/Resources/Resource">
  <xsl:sort select="Title"/>
  
  <tr>
  <td><xsl:value-of select="@Project" /></td>
  
 <!-- <xsl:attribute name="Project">
  <xsl:value-of select="@Project" />
  </xsl:attribute>
  <xsl:attribute name="UMLS">
  <xsl:value-of select="UMLS" />
  </xsl:attribute>-->
  
  
  
  
   <td><xsl:value-of select="Category" /></td>
   <td><xsl:value-of select="Subcategory" /></td>
   <td><xsl:value-of select="Title" /></td>
   <td><xsl:value-of select="Date" /></td>
   <td><xsl:value-of select="Runtime" /></td>
   <td><xsl:value-of select="Format" /></td>
   <td><xsl:value-of select="Requires" /></td>
  </tr>
  
</xsl:for-each>
</tbody>
</table>
</div>
</div>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
