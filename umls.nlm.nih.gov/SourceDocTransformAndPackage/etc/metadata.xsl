<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:java="http://www.java.com/"
    exclude-result-prefixes="java xs">
    
    <xsl:variable name="lcletters">abcdefghijklmnopqrstuvwxyz</xsl:variable>
    <xsl:variable name="ucletters">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
    
    <xsl:function name="java:file-exists" xmlns:file="java.io.File" as="xs:boolean">
        <xsl:param name="file" as="xs:string"/>
        <xsl:param name="base-uri" as="xs:string"/>
        
        <xsl:variable name="absolute-uri" select="resolve-uri($file, $base-uri)" as="xs:anyURI"/>
        <xsl:sequence select="file:exists(file:new($absolute-uri))"/>
    </xsl:function>    
    
    <xsl:output method = "html" omit-xml-declaration = "yes" indent = "yes" encoding = "utf-8" use-character-maps="cm1" doctype-public = "-//W3C//DTD XHTML 1.0 Transitional//EN"/>
    <xsl:character-map name="cm1">
        <xsl:output-character character="&#160;" string="&amp;nbsp;"/>
        <xsl:output-character character="&#252;" string="&amp;uuml;"/>
        <xsl:output-character character="&#227;" string="&amp;atilde;"/>
        <xsl:output-character character="&#34;"  string="&amp;quot;"/>
        
    </xsl:character-map>
    
    <!--  end process -->
    
    <xsl:template name="main">
        <xsl:variable name = "base">/Users/steveemrick/sourcereleasedocs</xsl:variable>
        <xsl:variable name = "release">2013AB</xsl:variable>
        <xsl:variable name = "output">metadata.html</xsl:variable>
        <xsl:for-each select = "collection('/Users/steveemrick/sourcereleasedocs/2013AB?select=metadata.xml;recurse=yes')">
            <xsl:variable name = "sab" select = "tokenize(document-uri(.), '/')[last()-1]"/>
            <xsl:result-document href="{string-join(($base,$release,$sab,$output),'/')}">
                <xsl:apply-templates select="."/>   
            </xsl:result-document>
        </xsl:for-each>
    </xsl:template>
    
    
    <xsl:template match="document">

        
        
        <!--<html>
            <head>
                <script type = "text/javascript" src = "http://www.ncbi.nlm.nih.gov/core/jig/1.5.2/js/jig.min.js" language = "javascript"></script>
                <meta name="ncbitoggler" content="indicator: 'plus-minus-big'"/>
            </head>-->
            
            
            <!--begin tabbed navigation area-->
            <xsl:choose>
            <xsl:when test="java:file-exists('sourcerepresentation.html', base-uri())">
            
            <div class="section-2">
                <ul id="menu">
                    <li id="nav-1"><a href="index.html">Synopsis</a> </li>
                    <li id="nav-2"><a href="metadata.html" id = "metadata">Source Metadata</a></li>
                    <li id="nav-3"><a href="stats.html">Statistics</a></li>
                    <li id="nav-4"><a href="samples.html">Samples</a></li>
                    <li id="nav-5"><a href="sourcerepresentation.html">Source Representation</a></li>
                    <li id="nav-6"><a href="metarepresentation.html">Metathesaurus Representation</a></li>   
                </ul>
            </div>
            </xsl:when>
            <xsl:otherwise>
                    <div class="section-2">
                        <ul id="menu">
                            <li id="nav-1"><a href="index.html">Synopsis</a> </li>
                            <li id="nav-2"><a href="metadata.html" id = "metadata">Source Metadata</a></li>
                            <li id="nav-3"><a href="stats.html">Statistics</a></li>
                            <li id="nav-4"><a href="samples.html">Samples</a></li>
                           <!-- <li id="nav-5"><a href="sourcerepresentation.html">Source Representation</a></li>
                            <li id="nav-6"><a href="metarepresentation.html">Metathesaurus Representation</a></li>-->
                        </ul>
                    </div>
            </xsl:otherwise>
            </xsl:choose>
            <!-- end tabbed navigation area -->
            
            <!--  only process nodes that have content, not just a header. Also, default expand the concept information node on page load with ncbitoggler-open option -->
            <xsl:for-each select = "section">
              
                   
                        <h4>
                           <xsl:value-of select = "@name"/>
                        </h4>
                        <div>
                            <table class = "metadata-table">
                                <xsl:apply-templates select = "row"/>
                            </table>
                        </div>
                 
                 
                   
            </xsl:for-each>
       <!-- </html> end HTML document -->
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
                        <xsl:if test = "node() != 'null'">
                        <tr>
                        <td><xsl:value-of select = "@name"/></td>
                        <td><xsl:value-of select = "." disable-output-escaping="yes"/></td>
                        </tr>
                        </xsl:if>
                    </xsl:for-each>
                </tr>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
    
    
    
    
</xsl:stylesheet>