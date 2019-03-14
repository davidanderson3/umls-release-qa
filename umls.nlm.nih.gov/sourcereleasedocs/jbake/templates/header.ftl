<!DOCTYPE html>
<html lang="en">

<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <link rel="stylesheet" href="https://www.nlm.nih.gov/home_assets/css/nlm_main.css">
  <link href="https://fonts.googleapis.com/css?family=Roboto:300,400,400i,500,500i,700,900" rel="stylesheet">
  <link rel="schema.DC" href="http://purl.org/dc/elements/1.1/" title="The Dublin Core metadata Element Set" />
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.0.10/css/all.css" integrity="sha384-+d0P83n9kaQMCwj8F4RJB66tzIwOKmrdb46+porD/OvrJ+37WqIM7UoBtwHO6Nlg" crossorigin="anonymous">
  <link href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/local-uts.css" rel="stylesheet" type="text/css">
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
    <div class="container-fluid  bg-primary">
      <div class="container top_row">
        <div class="row">
          <div class="col"> <a href="https://www.hhs.gov/" class="text-white"><img src="https://www.nlm.nih.gov/images/HHS_White.png" class="hhslogo float-left img-fluid d-none d-sm-block"  alt="HHS logo"/>
          <p class="float-left mt-1 mb-0"><small> U.S. Department of Health & Human Services</small></p></a>
          </div>

        </div>
      </div>
    </div>
    <div class="container-fluid">
      <div class="container branding">
        <div class="row">
          <div class="col-xl-1 col-md-1 col-2 my-md-2 mt-3 pt-md-1 pt-lg-0 pr-0"><a href="https://www.nih.gov/"><img src="https://www.nlm.nih.gov/images/NIH_Black.png" class="nih_logo img-fluid" alt="NIH logo"/></a> </div>
          <div class="col-lg-4 col-md-3 col-10   ml-0 ">
            <p class="h4 mt-xl-4 mt-lg-3 pt-lg-0 mt-md-0 pt-md-2 pt-3 mb-0"><a href="https://www.nlm.nih.gov/">U.S. National Library of Medicine</a></p>

          </div>
          <div class="col-lg-3 col-md-3">
            <ul class="list-inline mt-xl-3 mt-md-3 mt-3 social_media d-none d-sm-block   float-right">
              <li class="list-inline-item"><a href="https://www.facebook.com/nationallibraryofmedicine"><img class="img-fluid" src="https://www.nlm.nih.gov/images/Facebook_B.svg" alt="Facebook"/></a></li>
              <li class="list-inline-item"><a href="https://twitter.com/nlm_news"><img class="img-fluid" src="https://www.nlm.nih.gov/images/Twitter_B.svg" alt="Twitter"/></a></li>
              <li class="list-inline-item"><a href="https://www.youtube.com/user/NLMNIH"><img class="img-fluid" src="https://www.nlm.nih.gov/images/YouTube_B.svg" alt="YouTube"/></a></li>
            </ul>
          </div>
          <div class="col-lg-4 col-md-5 pt-xl-3 pt-lg-2 pt-md-2">

            <form method="get" action="//vsearch.nlm.nih.gov/vivisimo/cgi-bin/query-meta" target="_self" name="searchForm" id="searchForm" class="form-inline">
              <input type="text" name="query" placeholder="Search for" aria-label="Search" class="form-control col-md-9 col-lg-10 col-9 my-2 my-md-0" id="search" autocomplete="off">
              <input type="hidden" name="v:project" value="nlm-main-website">
              <button class="btn btn-search" type="submit"><i class="fas fa-2x fa-search"></i></button>
            </form>
          </div>

        </div>
      </div>
    </div>
  </header>   