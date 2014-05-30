<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    
    <xsl:variable name="lcletters">abcdefghijklmnopqrstuvwxyz</xsl:variable>
    <xsl:variable name="ucletters">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
    
    
    <xsl:output method = "html" omit-xml-declaration = "yes" indent = "yes" encoding = "utf-8" use-character-maps="cm1" doctype-public = "-//W3C//DTD XHTML 1.0 Transitional//EN"/>
    <xsl:character-map name="cm1">
        <xsl:output-character character="&#160;" string="&amp;nbsp;"/>
        <xsl:output-character character="&#252;" string="&amp;uuml;"/>
        <xsl:output-character character="&#227;" string="&amp;atilde;"/>
        <xsl:output-character character="&#34;"  string="&amp;quot;"/>
        
    </xsl:character-map>
    
    <!--  end process -->
    
    <xsl:template name="main">
        <xsl:variable name = "base">file:///C:/Users/emricks/sourcereleasedocs</xsl:variable>
        <xsl:variable name = "release">2014AA</xsl:variable>
        <xsl:variable name = "output">samples.html</xsl:variable>
        <xsl:for-each select = "collection('file:///C:/Users/emricks/sourcereleasedocs/2014AA?select=samples.xml;recurse=yes')">
            <xsl:variable name = "sab" select = "tokenize(document-uri(.), '/')[last()-1]"/>
            <xsl:result-document href="{string-join(($base,$release,$sab,$output),'/')}">
                <xsl:apply-templates select="."/>   
            </xsl:result-document>
        </xsl:for-each>
    </xsl:template>
    
    
    <xsl:template match="document">
        
        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <script type = "text/javascript" src = "http://www.ncbi.nlm.nih.gov/core/jig/1.5.2/js/jig.min.js" language = "javascript"></script>
                <meta name="ncbitoggler" content="indicator: 'plus-minus-big'"/>
            </head>
            
            
            <!--begin tabbed navigation area-->
            <div class="section-3">
                <ul id="menu">
                    <li id="nav-1"> <a href="index.html">Synopsis</a> </li>
                    <li id="nav-2"> <a href="metadata.html">Source Metadata</a></li>
                    <li id="nav-3"> <a href="stats.html">Statistics</a></li>
                    <li id="nav-5"> <a href="samples.html" id = "samples">Samples</a></li>
                    <li id="nav-4"> <a href="representation.html">Representation</a>
                        <ul id="subnav-4">
                            <li><a href="sourcerepresentation.html">Source Representation</a></li>
                            <li><a href="metarepresentation.html">Metathesaurus Representation</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
            <!-- end tabbed navigation area -->
            
            <!--  only process nodes that have content, not just a header. Also, default expand the concept information node on page load with ncbitoggler-open option -->
            <xsl:for-each select = "section">
                <xsl:choose>
                    <xsl:when test = "position() eq 1">
                        <h4>
                            <a class="jig-ncbitoggler-open"><xsl:value-of select = "@name"/></a>
                        </h4>
                        <div>
                            <table valign = "top" cellspacing = "2" cellpadding = "2">
                                <xsl:apply-templates select = "row"/>
                            </table>
                        </div>
                    </xsl:when>
                    
                    <xsl:when test = "position() ne 1 and count(row) &gt; 1">
                        <h4>
                            <a class="jig-ncbitoggler"><xsl:value-of select = "@name"/></a>
                        </h4>
                        <div>
                            <table valign = "top" cellspacing = "2" cellpadding = "2">
                                <xsl:apply-templates select = "row"/>
                            </table>
                        </div>
                    </xsl:when>
                    
                </xsl:choose>
            </xsl:for-each>
        </html> <!-- end HTML document -->
    </xsl:template><!--  end xsl main template -->
    
    
    
    <xsl:template match = "row">
        
        <xsl:choose>
            <xsl:when test = "@header">
                <tr>
                    <xsl:for-each select = "field">
                        <th><xsl:value-of select = "."/></th>
                    </xsl:for-each>
                </tr>
            </xsl:when>
            <xsl:otherwise>
                <tr>
                    <xsl:for-each select = "field">
                        <td><xsl:value-of select = "." disable-output-escaping="yes"/></td>
                    </xsl:for-each>
                </tr>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
    
    
    
    
</xsl:stylesheet>