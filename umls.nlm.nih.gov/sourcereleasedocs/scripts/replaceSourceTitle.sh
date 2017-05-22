#!/bin/bash
# This script replaces the title field in the jbake metadata. It uses rsab-directory-map.csv to get from the 
# directory name to the RSAB, which is inserted in the title field. 
# run bash, navigate to the root directory (/sourcereleasedocs) and run bash scripts/replaceSourceTitle.sh
# important - set the base directory to whichever directory contains SAB directories 
BASEDIR='jbake/content/current/' 
while IFS=',' read -r RSAB DIRECTORY; do 
sed -i "/title=/c\title=$RSAB - Synopsis" "$BASEDIR""$DIRECTORY"/index.html;
sed -i "/title=/c\title=$RSAB - Source Metadata" "$BASEDIR""$DIRECTORY"/Metadata.html;
sed -i "/title=/c\title=$RSAB - Source Representation" "$BASEDIR""$DIRECTORY"/sourcerepresentation.html;
sed -i "/title=/c\title=$RSAB - Metathesaurus Representation" "$BASEDIR""$DIRECTORY"/metarepresentation.html;
done < rsab-directory-map.csv