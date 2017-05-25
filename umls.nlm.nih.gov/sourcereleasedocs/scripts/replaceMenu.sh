#!/bin/bash
# run bash, navigate to the root directory (/sourcereleasedocs) and run bash scripts/replaceDate.sh
# important - set the base directory to whichever directory contains SAB directories 
# set a value for the CREATEDATE variable before running
BASEDIR='jbake/content/current/'
while IFS=',' read -r RSAB DIRECTORY; do
if [ -f "$BASEDIR""$DIRECTORY"/index.html ] 
then 
sed '/<!--menu-->/q' "$BASEDIR""$DIRECTORY"/index.html > "$BASEDIR""$DIRECTORY"/index.temp1
awk '/<!--endmenu-->/{y=1}y' "$BASEDIR""$DIRECTORY"/index.html > "$BASEDIR""$DIRECTORY"/index.temp2
echo '<div class="row">
 <div class="container-fluid navbar navbar-default" role="navigation">
  <div class="btn-group navbar-btn visible-*" role="group">
   <a class="btn btn-md" role="button" href="index.html">Synopsis</a>' > "$BASEDIR""$DIRECTORY"/menu 
fi;
if [ -f "$BASEDIR""$DIRECTORY"/metadata.html ] 
then 
sed '/<!--menu/q' "$BASEDIR""$DIRECTORY"/metadata.html > "$BASEDIR""$DIRECTORY"/metadata.temp1
awk '/endmenu-->/{y=1}y' "$BASEDIR""$DIRECTORY"/metadata.html > "$BASEDIR""$DIRECTORY"/metadata.temp2
echo '   <a class="btn btn-md" role="button" href="metadata.html">Source Metadata</a>' >> "$BASEDIR""$DIRECTORY"/menu 
fi;
if [ -f "$BASEDIR""$DIRECTORY"/sourcerepresentation.html ] 
then 
sed '/<!--menu/q' "$BASEDIR""$DIRECTORY"/sourcerepresentation.html > "$BASEDIR""$DIRECTORY"/sourcerepresentation.temp1
awk '/endmenu-->/{y=1}y' "$BASEDIR""$DIRECTORY"/sourcerepresentation.html > "$BASEDIR""$DIRECTORY"/sourcerepresentation.temp2
echo '   <a class="btn btn-md" role="button" href="sourcerepresentation.html">Source Representation</a>' >> "$BASEDIR""$DIRECTORY"/menu 
fi;
if [ -f "$BASEDIR""$DIRECTORY"/metarepresentation.html ] 
then 
sed '/<!--menu/q' "$BASEDIR""$DIRECTORY"/metarepresentation.html > "$BASEDIR""$DIRECTORY"/metarepresentation.temp1
awk '/endmenu-->/{y=1}y' "$BASEDIR""$DIRECTORY"/metarepresentation.html > "$BASEDIR""$DIRECTORY"/metarepresentation.temp2
echo '   <a class="btn btn-md" role="button" href="metarepresentation.html">Metathesaurus Representation</a>' >> "$BASEDIR""$DIRECTORY"/menu 
fi;
echo '  </div>
 </div>
</div>' >> "$BASEDIR""$DIRECTORY"/menu;
if [ -f "$BASEDIR""$DIRECTORY"/index.html ]
then
cat "$BASEDIR""$DIRECTORY"/index.temp1 "$BASEDIR""$DIRECTORY"/menu "$BASEDIR""$DIRECTORY"/index.temp2 > "$BASEDIR""$DIRECTORY"/index.html
fi;
if [ -f "$BASEDIR""$DIRECTORY"/metadata.html ]
then
cat "$BASEDIR""$DIRECTORY"/metadata.temp1 "$BASEDIR""$DIRECTORY"/menu "$BASEDIR""$DIRECTORY"/metadata.temp2 > "$BASEDIR""$DIRECTORY"/metadata.html
fi;
if [ -f "$BASEDIR""$DIRECTORY"/sourcerepresentation.html ]
then
cat "$BASEDIR""$DIRECTORY"/sourcerepresentation.temp1 "$BASEDIR""$DIRECTORY"/menu "$BASEDIR""$DIRECTORY"/sourcerepresentation.temp2 > "$BASEDIR""$DIRECTORY"/sourcerepresentation.html
fi;
if [ -f "$BASEDIR""$DIRECTORY"/metarepresentation.html ]
then
cat "$BASEDIR""$DIRECTORY"/metarepresentation.temp1 "$BASEDIR""$DIRECTORY"/menu "$BASEDIR""$DIRECTORY"/metarepresentation.temp2 > "$BASEDIR""$DIRECTORY"/metarepresentation.html
fi;
rm "$BASEDIR""$DIRECTORY"/index.temp1;
rm "$BASEDIR""$DIRECTORY"/metadata.temp1;
rm "$BASEDIR""$DIRECTORY"/sourcerepresentation.temp1;
rm "$BASEDIR""$DIRECTORY"/metarepresentation.temp1;
rm "$BASEDIR""$DIRECTORY"/index.temp2;
rm "$BASEDIR""$DIRECTORY"/metadata.temp2;
rm "$BASEDIR""$DIRECTORY"/sourcerepresentation.temp2;
rm "$BASEDIR""$DIRECTORY"/metarepresentation.temp2;
rm "$BASEDIR""$DIRECTORY"/menu;
done < rsab-directory-map.csv 

