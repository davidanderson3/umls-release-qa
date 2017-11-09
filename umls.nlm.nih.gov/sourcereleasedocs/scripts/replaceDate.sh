#!/bin/bash
# run bash, navigate to the root directory (/sourcereleasedocs) and run bash scripts/replaceDate.sh
# important - set the base directory to whichever directory contains SAB directories 
# set a value for the CREATEDATE variable before running
BASEDIR='jbake/content/'
CREATEDATE='2017-11-06'
while IFS='|' read -r RSAB DIRECTORY SSN; do
sed -i "/date=/c\date=$CREATEDATE" "$BASEDIR""$DIRECTORY"/index.html;
sed -i "/date=/c\date=$CREATEDATE" "$BASEDIR""$DIRECTORY"/metadata.html;
sed -i "/date=/c\date=$CREATEDATE" "$BASEDIR""$DIRECTORY"/stats.html;
sed -i "/date=/c\date=$CREATEDATE" "$BASEDIR""$DIRECTORY"/sourcerepresentation.html;
sed -i "/date=/c\date=$CREATEDATE" "$BASEDIR""$DIRECTORY"/metarepresentation.html;
done < directorymap.txt
