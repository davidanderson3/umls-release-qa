#!/bin/bash 
# run bash, navigate to the root directory (/sourcereleasedocs) and run bash scripts/replaceUpdatedDate.sh
# important - set the base directory to whichever directory contains SAB directories 
# set a value for the CREATEDATE variable before running
BASEDIR='jbake/content/current/'
UPDATEDATE='2017-05-08'
while IFS=',' read -r RSAB DIRECTORY; do
sed -i "/updated=/c\updated=$UPDATEDATE" "$BASEDIR""$DIRECTORY"/index.html;
sed -i "/updated=/c\updated=$UPDATEDATE" "$BASEDIR""$DIRECTORY"/Metadata.html;
sed -i "/updated=/c\updated=$UPDATEDATE" "$BASEDIR""$DIRECTORY"/sourcerepresentation.html;
sed -i "/updated=/c\updated=$UPDATEDATE" "$BASEDIR""$DIRECTORY"/metarepresentation.html;
done < rsab-directory-map.csv 
