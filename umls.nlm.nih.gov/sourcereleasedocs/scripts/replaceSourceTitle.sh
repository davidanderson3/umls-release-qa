#!/bin/bash
# This script replaces the title field in the jbake metadata. It uses MRSAB.RRF to insert the RSAB and SSN into the title field. 
# run from the base directory (.../sourcereleasedocs/)
while IFS='|' read -r VCUI RCUI VSAB RSAB SON SF SVER VSTART VEND IMETA RMETA SLC SCC SRL TFR CFR CXTY TTYL ATNL LAT CENC CURVER SABIN SSN SCIT; do 
[ -f jbake/content/"$RSAB"/index.html ] && sed -i "/title=/c\title=$RSAB ($SSN) - Synopsis" jbake/content/"$RSAB"/index.html;
[ -f jbake/content/"$RSAB"/metadata.html ] && sed -i "/title=/c\title=$RSAB ($SSN) - Metadata" jbake/content/"$RSAB"/metadata.html;
[ -f jbake/content/"$RSAB"/stats.html ] && sed -i "/title=/c\title=$RSAB ($SSN) - Statistics" jbake/content/"$RSAB"/stats.html;
[ -f jbake/content/"$RSAB"/sourcerepresentation.html ] && sed -i "/title=/c\title=$RSAB ($SSN) - Source Representation" jbake/content/"$RSAB"/sourcerepresentation.html;
[ -f jbake/content/"$RSAB"/metarepresentation.html ] && sed -i "/title=/c\title=$RSAB ($SSN) - Metathesaurus Representation" jbake/content/"$RSAB"/metarepresentation.html;
done < MRSAB_all.txt