<!DOCTYPE html>  
<html lang="en">
  <head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge" /> 
      <!-- Google Tag Manager -->
<script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
'//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
})(window,document,'script','dataLayer','GTM-MT6MLL');</script>
<!-- End Google Tag Manager -->
    <meta charset="utf-8"/>
    <title><#if (content.title)??><#escape x as x?xml>UMLS Metathesaurus - ${content.title}</#escape><#else>UMLS Metathesaurus Vocabulary Documentation</#if></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="DC.Title" content = "<#if (content.title)??><#escape x as x?xml>UMLS Metathesaurus - ${content.title}</#escape><#else>UMLS Metathesaurus Vocabulary Documentation</#if>"/>
    <meta name="DC.Publisher" content="U.S. National Library of Medicine" />
    <meta name="DC.Rights" content="Public Domain" />
    <meta name="DC.Language" content="eng" />
    <meta name="generator" content="JBake">
	<meta name="NLM.Contact.Email" content="nlmumlscustserv@mail.nlm.nih.gov" />

    <!-- Le styles -->
    <link href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/nlm-dropdown.css" rel="stylesheet" type="text/css">
    <link href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/asciidoctor.css" rel="stylesheet" type="text/css">
    <link href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/prettify.css" rel="stylesheet" type="text/css">
    <link href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/uts-docs.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.6/styles/default.min.css" type="text/css">
    <link rel="stylesheet" href="https://www.nlm.nih.gov/core/nlm-autocomplete/1.0/nlm-autocomplete.css" type="text/css">
    <link rel="stylesheet" href="https://www.nlm.nih.gov/core/jquery-ui/1.8/jquery-ui.css" type="text/css">
    <link href="//cdn.datatables.net/1.10.3/css/jquery.dataTables.css" rel="stylesheet" />
    <link href="//cdn.datatables.net/responsive/1.0.2/css/dataTables.responsive.css" rel="stylesheet" />


    <script src="//code.jquery.com/jquery-1.11.3.min.js" type="text/javascript"></script>
    <script src="//code.jquery.com/ui/1.11.3/jquery-ui.min.js" type="text/javascript"></script>
    <script src="//code.jquery.com/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/master.js" type="text/javascript"></script>
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/bootstrap.min.js" type="text/javascript"></script>
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/prettify.js" type="text/javascript"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.6/highlight.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="//cdn.datatables.net/1.10.3/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="//cdn.datatables.net/responsive/1.0.2/js/dataTables.responsive.min.js"></script>
    
    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/html5shiv.min.js" type="text/javascript"></script>
    <![endif]-->
    <link rel="shortcut icon" href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>favicon.ico">
    
    <script type = "text/javascript" language = "javascript">
      var $ = jQuery.noConflict();
      
      $(document).ready(function() {
        
       $("th").attr("scope","col");
       
        $("input#search.search-input").click(function(){
          $(this).attr("value","");
          
        });
        
        $("input#search.search-input").blur(function(){
          $(this).attr("value","Search");
          
        });
        
       
      });
      
      
       
    </script>

   <#if (content.redirect)??>
   <script type = "text/javascript" language = "javascript">
	window.location.replace("${content.redirect}");
   </script>
   </#if>
  </head>
  <body onload="prettyPrint();hljs.initHighlighting();">
  <noscript><iframe src="//www.googletagmanager.com/ns.html?id= GTM-MT6MLL " height="0" width="0" style="display:none;visibility:hidden" title="googletagmanager"></iframe></noscript>
     <div class="container-fluid">
      <!--header to hold banner + top navigation area -->
      <!-- responsive design header using bootstrap css framework -->
   
