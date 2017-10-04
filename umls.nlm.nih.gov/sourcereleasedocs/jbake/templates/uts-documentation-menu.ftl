<#assign categories = ['Retrieving UMLS Data', 'Retrieving Source-Asserted Data', 'REST API Cookbook']>
<div class = "row header">
  <div class="container-fluid">
     <div class = "row">
        <div class = "col-md-6 col-sm-8 col-xs-12">
            <a href = "https://www.nlm.nih.gov"><img class= "nlm-logo img-responsive" alt="National Library of Medicine" style="margin-right:1em;" src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>nlm-logo.png"></a>
        </div>
        <div class = "col-md-6 col-sm-8 col-xs-12" id = "search-form">
         <form method="get" action="//vsearch.nlm.nih.gov/vivisimo/cgi-bin/query-meta" target="_self" name="searchForm" id="searchForm">
         <label for="search" class="hidden">Search</label>
         <input type="text" name="query" value="Search" class="search-input" id="search"/>
         <input type="hidden" name="v:project" value="nlm-main-website" />
         </form>
       </div>
       <div class = "col-md-6 col-sm-8 col-xs-12">
           <a class = "pull-right white" style = "margin-right:1em;" href="https://support.nlm.nih.gov/ics/support/ticketnewwizard.asp?style=classic&deptID=28054&category=umls"><b>Contact NLM</b></a>
       </div>
    </div>
  </div>
</div>
<div class = "row nlm-dropdown-menu">
  <div class = "container-fluid">
      <div id = "nav">
         <ul class = "topnav">
         <li></li>
         </ul>
      </div>
  </div>
</div>
<div class = "row uts-nav">
   <div class="col-md-8 col-sm-8 col-xs-12">
      <a href="${content.rootpath}"><img class= "img-responsive pull-left" style = "padding-top:.2em; padding-left:1em; margin-right:1em;" alt="UTS Logo" src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>umls-xs.gif"></a>
      <p class = "orange uts-title">UMLS Source Vocabulary Documentation</p>
   </div>
   <div class="col-md-4 col-sm-8 col-xs-12 umls-menu">
     <a href = "https://www.nlm.nih.gov/research/umls/quickstart.html">UMLS Quick Start Guide</a>&nbsp;<b>|</b>&nbsp;
     <a href = "https://www.nlm.nih.gov/research/umls/faq_main.html">FAQs</a>&nbsp;<b>|</b>&nbsp;
     <a href = "https://support.nlm.nih.gov/ics/support/ticketnewwizard.asp?style=classic&deptID=28054&category=umls">Customer Support</a>
   </div>
</div>
<!-- only build the REST Menu if we're in the REST API -->
<#if (content.uri)??  && content.uri?starts_with("rest/")>
<div class = "row">
  <div class = "container-fluid navbar navbar-default" role = "navigation">
   <div class="btn-group navbar-btn visible-*" role="group">
    <#list published_pages?sort_by('navorder') as page>
    <#if !(page.category)?? && (page.uri != "rest/home.html" && page.uri?starts_with("rest/"))><#if (content.rootpath)??><a class = "btn btn-md" role = "button" href = "${content.rootpath}${page.uri}">${page.title}</a><#else></#if></#if>
    </#list>
      <#list categories as cat>
      <div class="btn-group" role="group">
        <button type="button" class="btn btn-md dropdown-toggle" data-toggle="dropdown" aria-expanded="false">${cat}<span class = "caret"></span></button>    
         <ul class = "dropdown-menu" role = "menu">
          <#list published_pages?sort_by('navorder') as page>
           <#if (page.category)?? && page.category = cat><li><#if (content.rootpath)??><a href = "${content.rootpath}${page.uri}">${page.title}</a></li><#else></#if></#if>
          </#list>
         </ul>
      </div><!--//btn-group-->
     </#list>  
   </div>
 </div>
</div>
</#if>