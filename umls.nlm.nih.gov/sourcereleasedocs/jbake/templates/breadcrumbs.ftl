<#if (content.uri)?? && content.uri?starts_with("soap/")>
<span class = "breadcrumbs">
  <#if content.uri?matches("soap/home.html")><a href = "https://www.nlm.nih.gov/research/umls">UMLS Home</a> &raquo; <a href="https://documentation.uts.nlm.nih.gov">API Technical Documentation</a> &raquo; SOAP API
  <#else><a href = "https://www.nlm.nih.gov/research/umls">UMLS Home</a> &raquo; <a href="https://documentation.uts.nlm.nih.gov">API Technical Documentation</a> &raquo; <a href = "/soap/home.html">SOAP API</a> &raquo; <b>${content.title}</b>
  </#if>
</span>
</#if>
<#if (content.uri)?? && content.uri?starts_with("rest/")>
<span class = "breadcrumbs">
  <#if content.uri?matches("rest/home.html")><a href = "https://www.nlm.nih.gov/research/umls">UMLS Home</a> &raquo; <a href="https://documentation.uts.nlm.nih.gov">API Technical Documentation</a> &raquo; REST API 
  <#else><a href = "https://www.nlm.nih.gov/research/umls">UMLS Home</a> &raquo; <a href="https://documentation.uts.nlm.nih.gov">API Technical Documentation</a> &raquo; <a href = "/rest/home.html">REST API </a> &raquo; <b>${content.title}</b>
  </#if>
</span>
</#if>