var $nlm = jQuery.noConflict();
        /* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
        /* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
        /* If changed please update                      */
        /* htdocs/hmd/about/exhibition/scripts/master.js */
        /* htdocs/scripts/masterfull.js                  */
        /* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
        /* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
$nlm(document).ready(function(){


var menuCounter = 0;
	$nlm.ajax({
		type: "GET",
				url: "/sitenav.xml",
				dataType: "xml",
				success: function(xml) {
					$nlm(xml).find('nav').each(function(){
					    menuCounter = menuCounter+1;
					var topnav = $nlm(this).find('topnav').text();
                                        //alert(topnav);
					var content = '<a href="#" id="menu'+menuCounter+'">'+topnav+'</a><ul id="submenu'+menuCounter+'" class="subnav">';
						$nlm(this).find('subnav').each(function(){
							var subtitle = $nlm(this).find('subtitle').text();
							var location = $nlm(this).find('location').text();
							    content += '<li><a href="'+location+'">'+subtitle+'</a></li>';
						});
					content += '</ul>';
					$nlm('<li></li>').html(content).appendTo('.topnav');
					});
	addMenuBehaviors();
	}
			});
	
	
	
});


function loadScript(url, callback){

    var script = document.createElement("script");
	script.type = "text/javascript";
        script.async = true;

    if (script.readyState){  //IE
        script.onreadystatechange = function(){
            if (script.readyState == "loaded" ||
		script.readyState == "complete"){
                script.onreadystatechange = null;
                callback();
            }
        };
    } else {  //Others
        script.onload = function(){
            callback();
        };
    }

    script.src = url;
    var x = document.getElementsByTagName("script")[0];
    x.parentNode.insertBefore(script, x);

    return script;

} // loadScript

loadScript("https://www.nlm.nih.gov/core/nlm-autocomplete/1.0/nlm-autocomplete.min.js", function() {
        $nlm(document).ready(function() {
            nlm.autocomplete.add({element: "#search", dictionary: "nlm-ac-dictionary", width: 0});
        });
});


if("PIE" in window) {
	$nlm(function() {
		$nlm('#main-body').each(function() {PIE.attach(this);});
		$nlm('.search-input').each(function() {PIE.attach(this);});
		$nlm('ul.topnav').each(function() {PIE.attach(this);});
		$nlm('#barbranding').each(function() {PIE.attach(this);});
		$nlm('#footer').each(function() {PIE.attach(this);});
		$nlm('#footer-sub').each(function() {PIE.attach(this);});
		$nlm('#portal').each(function() {PIE.attach(this);});
	});
}


function addMenuBehaviors()
{


$nlm("ul.topnav li a").hover(function()
{ //When trigger is clicked...
//Following events are applied to the subnav itself (moving subnav up and down)

 $nlm(this).parent().find("ul.subnav").stop(true, true).delay(200).slideDown(200); //Drop down the subnav on click *** Changed to Hover

 $nlm(this).parent().hover(function() {
}, function()
{

  $nlm(this).parent().find("ul.subnav").stop(true, true).slideUp('fast'); //When the mouse hovers out of the subnav, move it back up

 });

//Following events are applied to the trigger (Hover events for the trigger)
 }).hover(function()
 {
  $nlm(this).addClass("subhover"); //On hover over, add class "subhover"
 }, function()
 {	//On Hover Out
  $nlm(this).removeClass("subhover"); //On hover out, remove class "subhover"
 });

 $nlm("ul.topnav li a").focus(function() { //When trigger is clicked...
//Following events are applied to the subnav itself (moving subnav up and down)
 $nlm(this).parent().find("ul.subnav").slideDown('fast').show(); //Drop down the subnav on click *** Changed to Hover
 $nlm(this).parent().focus						(function() {
 }, function()
 {
  $nlm(this).parent().find("ul.subnav").slideUp('fast').hide(); //When the mouse hovers out of the subnav, move it back up
 });

});

$nlm("input#search.search-input").focus(function()
{
 $nlm('ul.topnav li ul#submenu1').css('display', 'none');
 $nlm('ul.topnav li ul#submenu2').css('display', 'none');
 $nlm('ul.topnav li ul#submenu3').css('display', 'none');
 $nlm('ul.topnav li ul#submenu4').css('display', 'none');
 $nlm('ul.topnav li ul#submenu5').css('display', 'none');
});

$nlm("div#breadcrumb-div p a").focus(function()
{
 $nlm('ul.topnav li ul#submenu1').css('display', 'none');
 $nlm('ul.topnav li ul#submenu2').css('display', 'none');
 $nlm('ul.topnav li ul#submenu3').css('display', 'none');
 $nlm('ul.topnav li ul#submenu4').css('display', 'none');
 $nlm('ul.topnav li ul#submenu5').css('display', 'none');
});

$nlm("ul.topnav li a").focus(function()
{
 $nlm(this).addClass("subhover"); //On hover over, add class "subhover"
 currentID = this.id;

 if (currentID=='menu1')
 {
  $nlm('ul.topnav li ul#submenu2').css('display', 'none');
  $nlm('ul.topnav li ul#submenu3').css('display', 'none');
  $nlm('ul.topnav li ul#submenu4').css('display', 'none');
  $nlm('ul.topnav li ul#submenu5').css('display', 'none');
 }

 if (currentID=='menu2')
 {
  $nlm('ul.topnav li ul#submenu1').css('display', 'none');
  $nlm('ul.topnav li ul#submenu3').css('display', 'none');
  $nlm('ul.topnav li ul#submenu4').css('display', 'none');
  $nlm('ul.topnav li ul#submenu5').css('display', 'none');
 }

 if (currentID=='menu3')
 {
  $nlm('ul.topnav li ul#submenu1').css('display', 'none');
  $nlm('ul.topnav li ul#submenu2').css('display', 'none');
  $nlm('ul.topnav li ul#submenu4').css('display', 'none');
  $nlm('ul.topnav li ul#submenu5').css('display', 'none');
 }

 if (currentID=='menu4')
 {
  $nlm('ul.topnav li ul#submenu1').css('display', 'none');
  $nlm('ul.topnav li ul#submenu2').css('display', 'none');
  $nlm('ul.topnav li ul#submenu3').css('display', 'none');
  $nlm('ul.topnav li ul#submenu5').css('display', 'none');
 }

 if (currentID=='menu5')
 {
  $nlm('ul.topnav li ul#submenu1').css('display', 'none');
  $nlm('ul.topnav li ul#submenu2').css('display', 'none');
  $nlm('ul.topnav li ul#submenu3').css('display', 'none');
  $nlm('ul.topnav li ul#submenu4').css('display', 'none');
 }

});

}

function openPopup(POPUP)
  {
  var popup =  window.open(POPUP,'popup','resizable=yes,scrollbars=yes,width=400,height=600');
  popup.focus();
  }


//
// JavaScript loader
// based on http://friendlybit.com/js/lazy-loading-asyncronous-javascript/
// and http://www.nczonline.net/blog/2009/07/28/the-best-way-to-load-external-javascript/
//




//
// Load AddThis
//
/*
$nlm(window).load(function() {
    loadScript(location.protocol + "//s7.addthis.com/js/300/addthis_widget.js#username=nationallibraryofmedicine&title=National Library of Medicine", function() {
        // call AddThis API here rather than rely on class based configuration?
    });
});
*/
