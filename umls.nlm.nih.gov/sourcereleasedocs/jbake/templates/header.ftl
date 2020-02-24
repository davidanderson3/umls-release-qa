<!DOCTYPE html>
<html lang="en">

<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <link rel="stylesheet" href="https://www.nlm.nih.gov/home_assets/css/nlm_main.css?20200206">
  <link href="https://fonts.googleapis.com/css?family=Roboto:300,400,400i,500,500i,700,900" rel="stylesheet">
  <link rel="schema.DC" href="http://purl.org/dc/elements/1.1/" title="The Dublin Core metadata Element Set" />
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.0.10/css/all.css" integrity="sha384-+d0P83n9kaQMCwj8F4RJB66tzIwOKmrdb46+porD/OvrJ+37WqIM7UoBtwHO6Nlg" crossorigin="anonymous">
  <link href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/local-uts.css?version=1.2" rel="stylesheet" type="text/css">
  <title>
    <#if (content.title)??>UMLS Metathesaurus - <#escape x as x?xml>${content.title}</#escape><#else>UMLS Vocabulary Documentation</#if>
  </title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="DC.Title" content="<#if (content.title)??>UMLS Metathesaurus - <#escape x as x?xml>${content.title}</#escape><#else>UMLS Vocabulary Documentation</#if>" />
  <meta name="DC.Publisher" content="U.S. National Library of Medicine" />
  <meta name="DC.Subject.Keyword" content="Unified Medical Language System, Vocabularies, Terminologies, <#escape x as x?xml>${content.title}</#escape>">
  <meta name="DC.Rights" content="Public Domain" />
  <meta name="DC.Language" content="eng" />
  <meta name="generator" content="JBake">
  <meta name="NLM.Contact.Email" content="nlmumlscustserv@mail.nlm.nih.gov" />
      <!-- Google Tag Manager -->
  <script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
'//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
})(window,document,'script','dataLayer','GTM-MT6MLL');</script>
      <!-- End Google Tag Manager -->
   <#if (content.redirect)??>
    <script type="text/javascript" language="javascript">
      window.location.replace("${content.redirect}");
    </script>
  </#if>
</head>

<body onload="prettyPrint();hljs.initHighlighting();">
  <noscript><iframe src="//www.googletagmanager.com/ns.html?id= GTM-MT6MLL " height="0" width="0" style="display:none;visibility:hidden" title="googletagmanager"></iframe></noscript>
  <header>
<div class="container-fluid bg-primary">
    <div class="container branding">
      <div class="row container">
        <div class="col-lg-4 col-md-6 col-sm-6 col-xs-12 pb-md-0 pb-2 pl-3 pl-sm-0">
      <a href="/" id="anch_1"><img src="https://www.nlm.nih.gov/images/NLM_White.png" class="img-fluid" alt="NLM logo"></a>
        </div>
        <div class="col-lg-4 offset-lg-4 col-md-6 col-sm-6 col-xs-12 pt-xl-4 pt-lg-3 pt-md-4 pt-sm-3 pt-xs-0">
          <form method="get" action="//vsearch.nlm.nih.gov/vivisimo/cgi-bin/query-meta" target="_self" name="searchForm" id="searchForm2" class="form-inline mb-3 mb-md-0 mt-0 mt-xl-2">
            <input type="text" name="query" placeholder="Search NLM" aria-label="Search" class="form-control col-10 ui-autocomplete-input" id="search2" autocomplete="off">
            <input type="hidden" name="v:project" value="nlm-main-website">
            <button class="btn btn-search" type="submit" aria-label="Search NLM"><i class="fas fa-search"></i></button>
          </form>
        </div>  
      </div>  
    </div>  
  </div>
  </header>   