#!/bin/bash 
# Updates the 'updated' date in the jbake metadata. Should match the latest release date. 
# set a value for the UPDATEDATE variable before running
# make sure the BASEDIR variable is set for your local environment
BASEDIR='/Users/andersondm2/umlsdoc/umls.nlm.nih.gov/sourcereleasedocs/' 
UPDATEDATE='2018-05-07'
while IFS='|' read -r VCUI RCUI VSAB RSAB SON SF SVER VSTART VEND IMETA RMETA SLC SCC SRL TFR CFR CXTY TTYL ATNL LAT CENC CURVER SABIN SSN SCIT; do
sed -i "/updated=/c\updated=$UPDATEDATE" "$BASEDIR"jbake/content/"$RSAB"/index.html;
sed -i "/updated=/c\updated=$UPDATEDATE" "$BASEDIR"jbake/content/"$RSAB"/metadata.html;
sed -i "/updated=/c\updated=$UPDATEDATE" "$BASEDIR"jbake/content/"$RSAB"/stats.html;
sed -i "/updated=/c\updated=$UPDATEDATE" "$BASEDIR"jbake/content/"$RSAB"/sourcerepresentation.html;
sed -i "/updated=/c\updated=$UPDATEDATE" "$BASEDIR"jbake/content/"$RSAB"/metarepresentation.html;
done < "$BASEDIR"MRSAB_all.txt 
