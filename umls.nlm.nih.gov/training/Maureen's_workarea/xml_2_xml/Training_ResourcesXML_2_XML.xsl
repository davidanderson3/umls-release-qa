<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>

 <xsl:template match="/">
<xsl:element name="root">
 <xsl:for-each select="row">
  
						<xsl:element name="Resources">
							
							<xsl:element name="Resource">
<!--							 <xsl:attribute name="Project"></xsl:attribute> -->
<!--						     <xsl:value-of select="/row/Heading0"/> -->
							 <!--							 <xsl:attribute name="Current"></xsl:attribute> -->
							 <!--							     <xsl:value-of select="/row/Heading9"/> -->

													 <xsl:element name="Category">
													 <xsl:value-of select="row/Heading1"/>
													 </xsl:element>
													 
													 <xsl:element name="Subcategory">
													 <xsl:value-of select="row/Heading2"/>
													 </xsl:element>
													 
													 <xsl:element name="Title">
													 <xsl:value-of select="row/Heading3"/>
													 </xsl:element>
													 
													 <xsl:element name="URL">
													 <xsl:value-of select="row/Heading4"/>
													 </xsl:element>
													 
													 <xsl:element name="Date">
													 <xsl:value-of select="row/Heading5"/>
													 </xsl:element>
													 
													 <xsl:element name="Runtime">
													 <xsl:value-of select="row/Heading6"/>
													 </xsl:element>
													 
													 <xsl:element name="Format">
													 <xsl:value-of select="row/Heading7"/>
													 </xsl:element>
													 
													 <xsl:element name="Requires">
													 <xsl:value-of select="row/Heading8"/>
													 </xsl:element>
							
							
							
							


							</xsl:element> <!--end of Resource element-->
						</xsl:element> <!--end of Resources element-->
 </xsl:for-each>
</xsl:element> <!-- end of root element-->
 </xsl:template>

				
</xsl:stylesheet>