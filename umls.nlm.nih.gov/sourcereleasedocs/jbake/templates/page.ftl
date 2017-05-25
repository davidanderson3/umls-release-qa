<#include "header.ftl">
	<#include "uts-documentation-menu.ftl">
	<div class = "row">
	<div class = "container-fluid breadcrumbs-container">
	<#include "breadcrumbs.ftl">
	</div>
	</div>
	<div class = "row">
	<div class = "container-fluid">
	<#if (content.uri)??  && content.uri?starts_with("rest/")>
	<br/>
	<p class = "red"><b>Current REST API Version: ${config.restapi_version}</b></p>
        <p class = "notice">
	We welcome your feedback on our <a href = "https://support.nlm.nih.gov/ics/support/ticketnewwizard.asp?style=classic&deptID=28054&category=umls">customer service form</a>.
	Please use &quot;UMLS REST API feedback&quot; in your subject line.<br/>
	Gotta question for other UMLS users?  Join the <a href = "https://list.nih.gov/cgi-bin/wa.exe?A0=umlsusers-l" target = "_blank">UMLS listserv</a>.<br/>
	Check out the <a href = "/rest/rest-api-cookbook/postman.html">Postman sample collections</a>, or <a href = "https://github.com/HHS/uts-rest-api">code samples in Python, Java, and Perl on Github</a> to help you get started using the UMLS REST API.
	</p>
	
	</#if>
	<#if (content.uri)??  && content.uri?starts_with("soap/")>
	<br/>
	<p class = "red"><b>Current SOAP API Version: ${config.soap_api_version}</b></p>
	</#if>
	
	<h1><#escape x as x?xml>${content.title}</#escape></h1>
	${content.body}
	</div><!--end row-->
	</div><!--end container--> 
<#include "footer.ftl">