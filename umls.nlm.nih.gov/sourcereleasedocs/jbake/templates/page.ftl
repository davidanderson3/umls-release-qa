<#include "header.ftl">
<div class="container-fluid bg-primary">
<div class="container">
	<#include "uts-documentation-menu.ftl">
 </div>
 </div>

	<div class = "container-fluid breadcrumbs-container">
	<div class="container">
	<#include "breadcrumbs.ftl">
	</div>
	</div>
	<div class = "container-fluid">
	<div class = "container">

	
	<h1><#escape x as x?xml>${content.title}</#escape></h1>
	${content.body}
	</div><!--end row-->
	</div><!--end container--> 
<#include "footer.ftl">