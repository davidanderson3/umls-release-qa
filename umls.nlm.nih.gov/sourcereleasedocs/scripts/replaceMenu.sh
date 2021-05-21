#!/bin/bash
# run bash, navigate to the root directory (/sourcereleasedocs) and run bash scripts/replaceMenu.sh
# run from the base directory (.../sourcereleasedocs/)
while IFS='|' read -r VCUI RCUI VSAB RSAB SON SF SVER VSTART VEND IMETA RMETA SLC SCC SRL TFR CFR CXTY TTYL ATNL LAT CENC CURVER SABIN SSN SCIT; do
if [ -f jbake/content/"$RSAB"/index.html ] 
then 
sed '/<!--menu-->/q' jbake/content/"$RSAB"/index.html > jbake/content/"$RSAB"/index.temp1
awk '/<!--endmenu-->/{y=1}y' jbake/content/"$RSAB"/index.html > jbake/content/"$RSAB"/index.temp2
echo '<div class="row">
 <div class="container-fluid navbar navbar-default vocab-docs-navbar" role="navigation">
  <div class="btn-group navbar-btn visible-*" role="group">
   <a class="btn btn-md" role="button" href="index.html">Synopsis</a>' > jbake/content/"$RSAB"/menu 
fi;
if [ -f jbake/content/"$RSAB"/metadata.html ] 
then 
sed '/<!--menu/q' jbake/content/"$RSAB"/metadata.html > jbake/content/"$RSAB"/metadata.temp1
awk '/endmenu-->/{y=1}y' jbake/content/"$RSAB"/metadata.html > jbake/content/"$RSAB"/metadata.temp2
echo '   <a class="btn btn-md" role="button" href="metadata.html">Metadata</a>' >> jbake/content/"$RSAB"/menu 
fi;
if [ -f jbake/content/"$RSAB"/stats.html ] 
then 
sed '/<!--menu/q' jbake/content/"$RSAB"/stats.html > jbake/content/"$RSAB"/stats.temp1
awk '/endmenu-->/{y=1}y' jbake/content/"$RSAB"/stats.html > jbake/content/"$RSAB"/stats.temp2
echo '   <a class="btn btn-md" role="button" href="stats.html">Statistics</a>' >> jbake/content/"$RSAB"/menu 
fi;
if [ -f jbake/content/"$RSAB"/sourcerepresentation.html ] 
then 
sed '/<!--menu/q' jbake/content/"$RSAB"/sourcerepresentation.html > jbake/content/"$RSAB"/sourcerepresentation.temp1
awk '/endmenu-->/{y=1}y' jbake/content/"$RSAB"/sourcerepresentation.html > jbake/content/"$RSAB"/sourcerepresentation.temp2
echo '   <a class="btn btn-md" role="button" href="sourcerepresentation.html">Source Representation</a>' >> jbake/content/"$RSAB"/menu 
fi;
if [ -f jbake/content/"$RSAB"/metarepresentation.html ] 
then 
sed '/<!--menu/q' jbake/content/"$RSAB"/metarepresentation.html > jbake/content/"$RSAB"/metarepresentation.temp1
awk '/endmenu-->/{y=1}y' jbake/content/"$RSAB"/metarepresentation.html > jbake/content/"$RSAB"/metarepresentation.temp2
echo '   <a class="btn btn-md" role="button" href="metarepresentation.html">Metathesaurus Representation</a>' >> jbake/content/"$RSAB"/menu 
fi;
echo '  </div>
 </div>
</div>
' >> jbake/content/"$RSAB"/menu 
echo '
<div class="border-top border-bottom p-2">
   <div class="pl-2">
      <strong>Search '$RSAB' in the UMLS Metathesaurus</strong> 
   </div>
   <div class="row pt-2 px-4">
      <form class="form-inline" action="https://uts.nlm.nih.gov/uts/umls/searchResults" method="GET" target="_blank">
         <div class="input-group">
            <input class="form-control" type="text" name="searchString" value=""/>
            <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
            <input id="'$RSAB'" name="vocabulary" type="hidden" value="'$RSAB'">
            <input id="returnType" name="returnType" type="hidden" value="code">
         </div>
      </form>
   </div>
   <div class="pl-2 pt-2">
      (requires a free <a href="https://uts.nlm.nih.gov/uts/signup-login" target="_blank">UMLS license</a>)
   </div>
</div>
' >> jbake/content/"$RSAB"/index.search;
if [ -f jbake/content/"$RSAB"/index.html ]
then
cat jbake/content/"$RSAB"/index.temp1 jbake/content/"$RSAB"/menu jbake/content/"$RSAB"/index.search jbake/content/"$RSAB"/index.temp2 > jbake/content/"$RSAB"/index.html
fi;
if [ -f jbake/content/"$RSAB"/metadata.html ]
then
cat jbake/content/"$RSAB"/metadata.temp1 jbake/content/"$RSAB"/menu jbake/content/"$RSAB"/metadata.temp2 > jbake/content/"$RSAB"/metadata.html
fi;
if [ -f jbake/content/"$RSAB"/stats.html ]
then
cat jbake/content/"$RSAB"/stats.temp1 jbake/content/"$RSAB"/menu jbake/content/"$RSAB"/stats.temp2 > jbake/content/"$RSAB"/stats.html
fi;
if [ -f jbake/content/"$RSAB"/sourcerepresentation.html ]
then
cat jbake/content/"$RSAB"/sourcerepresentation.temp1 jbake/content/"$RSAB"/menu jbake/content/"$RSAB"/sourcerepresentation.temp2 > jbake/content/"$RSAB"/sourcerepresentation.html
fi;
if [ -f jbake/content/"$RSAB"/metarepresentation.html ]
then
cat jbake/content/"$RSAB"/metarepresentation.temp1 jbake/content/"$RSAB"/menu jbake/content/"$RSAB"/metarepresentation.temp2 > jbake/content/"$RSAB"/metarepresentation.html
fi;
find jbake/content/ -type f ! -name '*.html' -delete
done < MRSAB_all.txt

