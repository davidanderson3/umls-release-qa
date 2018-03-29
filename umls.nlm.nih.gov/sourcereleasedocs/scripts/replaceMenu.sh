#!/bin/bash
# run bash, navigate to the root directory (/sourcereleasedocs) and run bash scripts/replaceMenu.sh
# make sure the BASEDIR variable is set for your local environment
BASEDIR='/Users/andersondm2/umlsdoc/umls.nlm.nih.gov/sourcereleasedocs/'
while IFS='|' read -r VCUI RCUI VSAB RSAB SON SF SVER VSTART VEND IMETA RMETA SLC SCC SRL TFR CFR CXTY TTYL ATNL LAT CENC CURVER SABIN SSN SCIT; do
if [ -f "$BASEDIR"jbake/content/"$RSAB"/index.html ] 
then 
sed '/<!--menu-->/q' "$BASEDIR"jbake/content/"$RSAB"/index.html > "$BASEDIR"jbake/content/"$RSAB"/index.temp1
awk '/<!--endmenu-->/{y=1}y' "$BASEDIR"jbake/content/"$RSAB"/index.html > "$BASEDIR"jbake/content/"$RSAB"/index.temp2
echo '<div class="row">
 <div class="container-fluid navbar navbar-default" role="navigation">
  <div class="btn-group navbar-btn visible-*" role="group">
   <a class="btn btn-md" role="button" href="index.html">Synopsis</a>' > "$BASEDIR"jbake/content/"$RSAB"/menu 
fi;
if [ -f "$BASEDIR"jbake/content/"$RSAB"/metadata.html ] 
then 
sed '/<!--menu/q' "$BASEDIR"jbake/content/"$RSAB"/metadata.html > "$BASEDIR"jbake/content/"$RSAB"/metadata.temp1
awk '/endmenu-->/{y=1}y' "$BASEDIR"jbake/content/"$RSAB"/metadata.html > "$BASEDIR"jbake/content/"$RSAB"/metadata.temp2
echo '   <a class="btn btn-md" role="button" href="metadata.html">Metadata</a>' >> "$BASEDIR"jbake/content/"$RSAB"/menu 
fi;
if [ -f "$BASEDIR"jbake/content/"$RSAB"/stats.html ] 
then 
sed '/<!--menu/q' "$BASEDIR"jbake/content/"$RSAB"/stats.html > "$BASEDIR"jbake/content/"$RSAB"/stats.temp1
awk '/endmenu-->/{y=1}y' "$BASEDIR"jbake/content/"$RSAB"/stats.html > "$BASEDIR"jbake/content/"$RSAB"/stats.temp2
echo '   <a class="btn btn-md" role="button" href="stats.html">Statistics</a>' >> "$BASEDIR"jbake/content/"$RSAB"/menu 
fi;
if [ -f "$BASEDIR"jbake/content/"$RSAB"/sourcerepresentation.html ] 
then 
sed '/<!--menu/q' "$BASEDIR"jbake/content/"$RSAB"/sourcerepresentation.html > "$BASEDIR"jbake/content/"$RSAB"/sourcerepresentation.temp1
awk '/endmenu-->/{y=1}y' "$BASEDIR"jbake/content/"$RSAB"/sourcerepresentation.html > "$BASEDIR"jbake/content/"$RSAB"/sourcerepresentation.temp2
echo '   <a class="btn btn-md" role="button" href="sourcerepresentation.html">Source Representation</a>' >> "$BASEDIR"jbake/content/"$RSAB"/menu 
fi;
if [ -f "$BASEDIR"jbake/content/"$RSAB"/metarepresentation.html ] 
then 
sed '/<!--menu/q' "$BASEDIR"jbake/content/"$RSAB"/metarepresentation.html > "$BASEDIR"jbake/content/"$RSAB"/metarepresentation.temp1
awk '/endmenu-->/{y=1}y' "$BASEDIR"jbake/content/"$RSAB"/metarepresentation.html > "$BASEDIR"jbake/content/"$RSAB"/metarepresentation.temp2
echo '   <a class="btn btn-md" role="button" href="metarepresentation.html">Metathesaurus Representation</a>' >> "$BASEDIR"jbake/content/"$RSAB"/menu 
fi;
echo '  </div>
 </div>
</div>' >> "$BASEDIR"jbake/content/"$RSAB"/menu;
if [ -f "$BASEDIR"jbake/content/"$RSAB"/index.html ]
then
cat "$BASEDIR"jbake/content/"$RSAB"/index.temp1 "$BASEDIR"jbake/content/"$RSAB"/menu "$BASEDIR"jbake/content/"$RSAB"/index.temp2 > "$BASEDIR"jbake/content/"$RSAB"/index.html
fi;
if [ -f "$BASEDIR"jbake/content/"$RSAB"/metadata.html ]
then
cat "$BASEDIR"jbake/content/"$RSAB"/metadata.temp1 "$BASEDIR"jbake/content/"$RSAB"/menu "$BASEDIR"jbake/content/"$RSAB"/metadata.temp2 > "$BASEDIR"jbake/content/"$RSAB"/metadata.html
fi;
if [ -f "$BASEDIR"jbake/content/"$RSAB"/stats.html ]
then
cat "$BASEDIR"jbake/content/"$RSAB"/stats.temp1 "$BASEDIR"jbake/content/"$RSAB"/menu "$BASEDIR"jbake/content/"$RSAB"/stats.temp2 > "$BASEDIR"jbake/content/"$RSAB"/stats.html
fi;
if [ -f "$BASEDIR"jbake/content/"$RSAB"/sourcerepresentation.html ]
then
cat "$BASEDIR"jbake/content/"$RSAB"/sourcerepresentation.temp1 "$BASEDIR"jbake/content/"$RSAB"/menu "$BASEDIR"jbake/content/"$RSAB"/sourcerepresentation.temp2 > "$BASEDIR"jbake/content/"$RSAB"/sourcerepresentation.html
fi;
if [ -f "$BASEDIR"jbake/content/"$RSAB"/metarepresentation.html ]
then
cat "$BASEDIR"jbake/content/"$RSAB"/metarepresentation.temp1 "$BASEDIR"jbake/content/"$RSAB"/menu "$BASEDIR"jbake/content/"$RSAB"/metarepresentation.temp2 > "$BASEDIR"jbake/content/"$RSAB"/metarepresentation.html
fi;
rm "$BASEDIR"jbake/content/"$RSAB"/index.temp1;
rm "$BASEDIR"jbake/content/"$RSAB"/metadata.temp1;
rm "$BASEDIR"jbake/content/"$RSAB"/sourcerepresentation.temp1;
rm "$BASEDIR"jbake/content/"$RSAB"/metarepresentation.temp1;
rm "$BASEDIR"jbake/content/"$RSAB"/index.temp2;
rm "$BASEDIR"jbake/content/"$RSAB"/metadata.temp2;
rm "$BASEDIR"jbake/content/"$RSAB"/sourcerepresentation.temp2;
rm "$BASEDIR"jbake/content/"$RSAB"/metarepresentation.temp2;
rm "$BASEDIR"jbake/content/"$RSAB"/stats.temp1;
rm "$BASEDIR"jbake/content/"$RSAB"/stats.temp2;
rm "$BASEDIR"jbake/content/"$RSAB"/menu;
done < "$BASEDIR"MRSAB.RRF

