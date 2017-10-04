<!DOCTYPE html>  
<html lang="en">
  <head>
    <meta charset="utf-8"/>
    <title><#if (content.title)??><#escape x as x?xml>${content.title}</#escape><#else>UMLS Terminology Services REST API Technical Documentation</#if></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="DC.Title" content = "<#if (content.title)??><#escape x as x?xml>${content.title}</#escape><#else>UMLS Terminology Services REST API Technical Documentation</#if>"/>
    <meta name="DC.Publisher" content="U.S. National Library of Medicine" />
    <meta name="DC.Subject.Keyword" content="Unified Medical Language System API, UMLS REST API, UMLS API, Terminology Service, Biomedical Terminology Endpoint">
    <meta name="DC.Rights" content="Public Domain" />
    <meta name="DC.Language" content="eng" />
    <meta name="generator" content="JBake">
    <meta http-equiv="X-UA-Compatible" content="IE=edge;IE=9;IE=8;"/>

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
	<script type="text/javascript">
<!--
function MM_jumpMenu(targ,selObj,restore){ //v3.0
  eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
  if (restore) selObj.selectedIndex=0;
}
//-->
</script>

<script type = "text/javascript" src = "/scripts/jquery-min.js" language = "javascript"></script>
<script type = "text/javascript" src = "//www.ncbi.nlm.nih.gov/core/jig/1.14.8/js/jig.min.js" language = "javascript"></script>


<!--[if IE]>
   <script type="text/javascript" src="/scripts/PIE.js"></script>
<![endif]--> 
<script type= "text/javascript">   
var $ = jQuery.noConflict();
</script>

<script type = "text/javascript">
$(function() {
  if($.browser.msie) {

    $('.jig-tabs li').tabs().each(function() {

     PIE.attach(this);
    });

   $('.jig-tabs ul').css('height','2.7em');
  }
});
</script>
<script type = "text/javascript">
$(document).ready(function() {

$('a.jig-ncbitoggler').click(function(event) {
  //$(this).parent().next().css('background-color','red');
  var sourcecontainer = $(this).parent().next();
  $(sourcecontainer).animate({'background-color' : '#ff7373'}, 'fast');
  $(sourcecontainer).animate({'background-color' : '#ffffff'}, 'slow');
  
  //alert("you clicked me!");
  
});
});
</script>
<script type = "text/javascript">
$(document).ready(function() {

$('.ajaxlink').each(function() {

$(this).attr('tabindex','0');
});

  $('a.ajaxlink').click(function(event) {
    var myLink = $(this).attr("href");
    //alert(myLink.substr(1));
   $('div.content').each(function() {
      
      if ($(this).attr('id') == myLink.substr(1)) {
	$(this).attr('style','display:block');
	//alert($(this).attr('id'));
      }
       
       else {
	$(this).attr('style','display:none');
	
       }
       
   });  
     
  });
});
</script>

<script type = "text/javascript">
$(document).ready(function() {
 $('a.expand').click(function(event) {

  //alert ($(this).parent(".content").attr("id"));
  var myDiv = $(this).parents(".content").attr("id");
     

  $('#'+myDiv).find('.jig-ncbitoggler').each(function() {
     
   $(this).ncbitoggler('open');

     });
  

});
});

</script>

<script type = "text/javascript">
$(document).ready(function() {
 $('a.collapse').click(function(event) {

  //alert ($(this).parent(".content").attr("id"));
  var myDiv = $(this).parents(".content").attr("id");
     

  $('#'+myDiv).find('.jig-ncbitoggler').each(function() {
     
   $(this).ncbitoggler('close');

     });
  

});
});

</script>




<!--<link rel="stylesheet" type="text/css" href="//www.ncbi.nlm.nih.gov/portal/portal3rc.fcgi/1197446/css/72892/63266/66675/3316/31862/33955/30039/3251/1235402/12930/67324/3499/14534.css" /> -->


<style type="text/css">

.ui-widget-header{


border:none !important;
}

.ui-tabs-selected {

border:solid 1px #ccc !important;

}


.content-footnote{

float:left;
width:100%;
}


h4{
color:black !important;

}

.jig-tabs{
   
   
border:none;    
font-size:90%;
}

.jig-tabs img {
float:left;

}

.jig-tabs li a {

text-decoration:none !important;
outline:none;
}

.jig-tabs ul {
    
background:none;
border-top:none;
border-left:none;
border-right:none;
height:2.5em;
width:100%;
    
}


.ui-tabs-selected{

filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#78a22f', endColorstr='#e7eeeb'); /* for IE 5.5 - 7*/
-ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorstr='#78a22f', endColorstr='#e7eeeb')" !important; /* for IE 8*/
background: -moz-linear-gradient(bottom, #78a22f, #e7eeeb)!important; /* For Mozilla/Gecko (Firefox) */
background: -webkit-gradient(linear, left top, left bottom, from(#78a22f), to(#e7eeeb)) !important; /* For WebKit (Safari, Google Chrome) */    


}



.limboxcontent h5{

position:relative;

}

.count{

font-size:.8em;
color:#7a7a7a;
font-weight:bold;
position:absolute;
right:1%;

}




h5 a {
text-decoration:none !important;
outline:none;

}


.sourcecontainer table{

width:100%;
border:none!important;

}

.sourcecontainer table th{

text-align:left;
border:none!important;

width:100%;

}

.sourcecontainer table td{
border:none!important;
color:#254062!important;

}

.expand, .collapse{
font-size: 80%;

}

.limbox {
    border:none;
    position:relative;
    left:-2%;
    /*background-color: white;*/
    
}

.limboxcontent {
    font-weight: bold;
    padding-bottom: 1em;
    padding-left: 1em;
    padding-right: 1em;
    padding-top: 1em;
    text-align: left;
}
.smalllimbox {
    width: 46%;
}
.leftlimbox {
    float: left;
}

.smalllimbox .limboxcontent {
    height: auto;
    margin-bottom: 1px;
    margin-left: 0;
    margin-right: 0;
    margin-top: 1px;
    max-height: 95px;
    overflow-x: auto;
    overflow-y: auto;
    padding-bottom: 1px;
    padding-left: 4px;
    padding-right: 0;
    padding-top: 1px;
}
.smalllimbox .limboxcontent span {
    display: block;
}

.smalllimbox .limboxcontent {
    height: auto;
    max-height: none;
}
.limboxcontent .jig-ncbitoggler span, .limboxcontent span.num-sel {
    display: inline;
}

.limboxcontent h5 {
    -moz-background-clip: border;
    -moz-background-origin: padding;
    -moz-background-size: auto auto;
    background-attachment: scroll;
    background-color: #EEEEEE;
    background-image: none;
    background-position: 0 0;
    background-repeat: repeat;
    border-bottom-color: #CCCCCC;
    border-bottom-style: solid;
    border-bottom-width: 1px;
    border-left-color-ltr-source: physical;
    border-left-color-rtl-source: physical;
    border-left-color-value: #CCCCCC;
    border-left-style-ltr-source: physical;
    border-left-style-rtl-source: physical;
    border-left-style-value: solid;
    border-left-width-ltr-source: physical;
    border-left-width-rtl-source: physical;
    border-left-width-value: 1px;
    border-right-color-ltr-source: physical;
    border-right-color-rtl-source: physical;
    border-right-color-value: #CCCCCC;
    border-right-style-ltr-source: physical;
    border-right-style-rtl-source: physical;
    border-right-style-value: solid;
    border-right-width-ltr-source: physical;
    border-right-width-rtl-source: physical;
    border-right-width-value: 1px;
    border-top-color: #CCCCCC;
    border-top-style: solid;
    border-top-width: 1px;
    font-size: 1em;
    margin-bottom: 0.5em;
    margin-top: 0.5em;
    position: relative;
    width: 99%;
}





</style>
        
    <!-- Google Tag Manager -->
<script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
'//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
})(window,document,'script','dataLayer','GTM-MT6MLL');</script>
<!-- End Google Tag Manager -->
    
    
   <#if (content.redirect)??>
   <script type = "text/javascript" language = "javascript">
	window.location.replace("${content.redirect}");
   </script>
   </#if>
  </head>
  <body onload="prettyPrint();hljs.initHighlighting();">
     <div class="container-fluid">
      <!--header to hold banner + top navigation area -->
      <!-- responsive design header using bootstrap css framework -->
   