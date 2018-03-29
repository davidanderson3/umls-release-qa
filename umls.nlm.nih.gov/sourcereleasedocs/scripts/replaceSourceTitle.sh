#!/bin/bash
# This script replaces the title field in the jbake metadata. It uses MRSAB.RRF to insert the RSAB and SSN into the title field. 
# make sure the BASEDIR variable is set for your local environment
BASEDIR='/Users/andersondm2/umlsdoc/umls.nlm.nih.gov/sourcereleasedocs/' 
while IFS='|' read -r VCUI RCUI VSAB RSAB SON SF SVER VSTART VEND IMETA RMETA SLC SCC SRL TFR CFR CXTY TTYL ATNL LAT CENC CURVER SABIN SSN SCIT; do 
sed -i "/title=/c\title=$RSAB ($SSN) - Synopsis" "$BASEDIR"jbake/content/"$RSAB"/index.html;
sed -i "/title=/c\title=$RSAB ($SSN) - Metadata" "$BASEDIR"jbake/content/"$RSAB"/metadata.html;
sed -i "/title=/c\title=$RSAB ($SSN) - Statistics" "$BASEDIR"jbake/content/"$RSAB"/stats.html;
sed -i "/title=/c\title=$RSAB ($SSN) - Source Representation" "$BASEDIR"jbake/content/"$RSAB"/sourcerepresentation.html;
sed -i "/title=/c\title=$RSAB ($SSN) - Metathesaurus Representation" "$BASEDIR"jbake/content/"$RSAB"/metarepresentation.html;
done < "$BASEDIR"MRSAB.RRF 