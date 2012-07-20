<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "2.0">
	<xsl:output method = "html" omit-xml-declaration = "yes" indent = "yes" use-character-maps="cm1" doctype-public = "-//W3C//DTD XHTML 1.0 Transitional//EN"  encoding="utf-8" />
	<xsl:character-map name="cm1">
		<xsl:output-character character="&#160;" string="&amp;nbsp;"/>
		<xsl:output-character character="&#233;" string="&amp;eacute;"/>
		<xsl:output-character character="&#177;" string="&amp;plusmn;"/>
	</xsl:character-map>
	<xsl:variable name = "featured"/>
	<xsl:template match = "document">
		<html>
			<head>
				<link rel="stylesheet" type="text/css" href="styles/mu.css"/>
				<meta name="ncbitoggler" content="openedAppendText: ' (click to close)', closedAppendText: ' (click to open)'"/>
				<script type="text/javascript" src="http://ncbi.nlm.nih.gov/core/jig/1.5.2/js/jig.min.js"></script>
			</head>
			<body>
				<div class = "top rounded-content-block">
				<div class = "intro">
				<p>The National Library of Medicine provides free access to vocabulary standards, applications, and related tools that can be used to meet US EHR certification criteria and to achieve Meaningful Use of EHRs.
				Below are resources either created by or supported by NLM that can be used for providing patient-specific education materials, e-prescribing, and creating, exchanging, and interpreting standardized lists of problems, medications, and test results.
				</p>
				</div>
				</div>
				
				
				<xsl:apply-templates select = "contexts"/>
				
				<div class = "footer">
					<p>* Resource requires a <a href = "https://uts.nlm.nih.gov//license.html" target = "_blank">UMLS&#174; Metathesaurus&#174; License</a></p>
					<p>&#8224; Resource requires a <a href = "https://loinc.org/" target = "_blank">LOINC account</a></p>
				</div>
			</body>

		</html>	
	</xsl:template>

	<!--process contexts - drugs, problems,etc -->
	<xsl:template match = "contexts">
		<xsl:for-each select = "context">

			<a name = "{@id}"/>
			<div class = "context-image">
				<xsl:apply-templates select = "image"/>
				<p class = "caption"><xsl:value-of select = "@name"/></p>
			</div>
			<div class = "context rounded-content-block">

				<xsl:apply-templates select = "resources"/>
			</div>
			
		</xsl:for-each>
	</xsl:template>
	<!--/process contexts - drugs, problems,etc -->

	<!--process each resources under each context element - SNOMEDCT, LOINC, RxNorm, etc-->
	<xsl:template match = "resources">
		<!--<h2><xsl:value-of select = "@type"/></h2>-->
		<xsl:for-each select = "resource">
			<div class = "{@type} rounded-content-block">
				<xsl:if test = "@logo">
					<img class = "logo" align = "bottom" src = "images/{@logo}" border = "0"/>
				</xsl:if>
				<h3><xsl:value-of select = "@name"/></h3>
				<xsl:apply-templates select = "description"/>
				<xsl:apply-templates select = "properties"/>
				<xsl:apply-templates select = "products"/>
			</div>
		</xsl:for-each>	
	</xsl:template>
	<!--/process each resources under each context element - SNOMEDCT, LOINC, RxNorm, etc-->

	<!-- process all descriptions at all levels-->
	<xsl:template match = "description">
		<div class = "description medium">
			<xsl:value-of select = "." disable-output-escaping="yes"/>
		</div>
	</xsl:template>
	<!--/process all descriptions at all levels-->

	<!--process images-->
	<xsl:template match = "image">
		<img src = "images/{@name}" alt = "{@alt}"/>	
	</xsl:template>
	<!--/process images-->
	<!--
	<xsl:template match = "properties">
		<div class = "properties rounded-content-block">
			<table class = "property-table">
				<xsl:for-each select = "property">
					<tr>
						<td valign = "top"><xsl:value-of select = "@name"/></td>
						<td valign = "top">
							<xsl:choose>
								<xsl:when test = "@type eq 'url'">
									<a href = "{@website}"><xsl:value-of select = "."/></a>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select = "."/>
								</xsl:otherwise>
							</xsl:choose>
						</td>
					</tr>
				</xsl:for-each>
			</table>
		</div>
	</xsl:template>

	-->
	<!--
	<xsl:template match = "properties">
		<table class = "property-table">
			<tr>
				<xsl:apply-templates select = "property" mode = "build-headers"/>
			</tr>
			<tr>
				<xsl:apply-templates select = "property" mode = "build-rows"/>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match = "property" mode = "build-headers">

		<xsl:for-each select = ".">
			<th align = "left"><xsl:value-of select = "@name"/></th>
		</xsl:for-each>

	</xsl:template>

	<xsl:template match = "property" mode = "build-rows">
		<xsl:for-each select = ".">
			<td valign = "top"><xsl:value-of select = "."/></td>
		</xsl:for-each>
	</xsl:template>
	-->

	<xsl:template match = "properties">
		<ul class = "property-list medium">
			<xsl:for-each select = "property">
				<!--
				<xsl:choose>
					<xsl:when test = "@type eq 'url'">
						<li><b><xsl:value-of select = "@name"/></b>: <a href = "{@website}"><xsl:value-of select = "."/></a></li>
					</xsl:when>
					<xsl:otherwise>
						<li><b><xsl:value-of select = "@name"/></b>: <xsl:value-of select = "."/></li>
					</xsl:otherwise>
				</xsl:choose>
				-->              <xsl:choose>
					<xsl:when test = "@url and @name eq 'Web Resource'">
						<li><b><a href = "{@url}" target = "_blank"><xsl:value-of select = "."/><xsl:if test = "@external = 'y'">&#160;<img src = "images/exit_arrow.png"/></xsl:if></a></b></li>
					</xsl:when> 
					<xsl:when test = "@url">
						<li><b><xsl:value-of select = "@name"/></b>: <a href = "{@url}" target = "_blank"><xsl:value-of select = "."/><xsl:if test = "@external = 'y'">&#160;<img src = "images/exit_arrow.png"/></xsl:if></a></li>
					</xsl:when>
					<xsl:otherwise>
						<li><b><xsl:value-of select = "@name"/></b>: <xsl:value-of select = "."/></li>
					</xsl:otherwise>
				</xsl:choose>

			</xsl:for-each>
		</ul>
	</xsl:template>

	<xsl:template match = "products">
		<xsl:apply-templates select = "extensions"/>
		<xsl:apply-templates select = "subsets"/>
		<xsl:apply-templates select = "valuesets"/>
		<xsl:apply-templates select = "mappings"/>
		<xsl:apply-templates select = "apis"/>

	</xsl:template>

	<xsl:template match = "extensions">
		<h4><a class = "jig-ncbitoggler small">Helpful Extensions <span class="ui-ncbitoggler-appended-text small">(click to open) </span></a></h4>
		<div class = "extensions">
			<xsl:for-each select = "extension">
				<div class = "extension rounded-content-block">
					<h5><xsl:value-of select = "@name"/></h5>
					<xsl:apply-templates select = "description"/>
					<xsl:apply-templates select = "properties"/>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>
	<xsl:template match = "mappings">
		<h4><a class = "jig-ncbitoggler small">Helpful Mappings <span class="ui-ncbitoggler-appended-text small ">(click to open) </span></a></h4>
		<div class = "mappings">
			<xsl:for-each select = "mapping">
				<!--
				<xsl:if test = "@featured = 'y'">
					<xsl:value-of select = "concat($featured, '&lt;p&gt;',@name,'&lt;/p&gt;')"/>
				</xsl:if>-->
				<div class = "mapping rounded-content-block">
					<h5><xsl:value-of select = "@name"/></h5>
					<xsl:apply-templates select = "description"/>
					<xsl:apply-templates select = "properties"/>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>
	<xsl:template match = "subsets">
		<h4><a class = "jig-ncbitoggler small">Helpful Subsets <span class="ui-ncbitoggler-appended-text small">(click to open) </span></a></h4>
		<div class = "subsets">
			<xsl:for-each select = "subset">
				<div class = "subset rounded-content-block">
					<h5><xsl:value-of select = "@name"/></h5>
					<xsl:apply-templates select = "description"/>
					<xsl:apply-templates select = "properties"/>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>
	<xsl:template match = "valuesets">
		<h4><a class = "jig-ncbitoggler small">Helpful Value Sets <span class="ui-ncbitoggler-appended-text small">(click to open) </span></a></h4>
		<div class = "valuesets">
			<xsl:for-each select = "valueset">
				<div class = "valueset rounded-content-block">
					<h5><xsl:value-of select = "@name"/></h5>
					<xsl:apply-templates select = "description"/>
					<xsl:apply-templates select = "properties"/>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>
	<xsl:template match = "apis">
		<h4><a class = "jig-ncbitoggler small">Helpful APIs <span class="ui-ncbitoggler-appended-text small">(click to open) </span></a></h4>
		<div class = "apis">
			<xsl:for-each select = "api">
				<div class = "api rounded-content-block">
					<h5><xsl:value-of select = "@name"/></h5>
					<xsl:apply-templates select = "description"/>
					<xsl:apply-templates select = "properties"/>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>

</xsl:stylesheet>
