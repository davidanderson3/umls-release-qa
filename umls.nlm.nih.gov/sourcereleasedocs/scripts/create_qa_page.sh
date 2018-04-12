#!/bin/bash
# This script replaces the title field in the jbake metadata. It uses MRSAB.RRF to insert the RSAB and SSN into the title field. 
# make sure the BASEDIR variable is set for your local environment
BASEDIR='/Users/andersondm2/umlsdoc/umls.nlm.nih.gov/sourcereleasedocs/' 
rm qapage.html
sort -k4 -t'|' MRSAB_all.txt -o MRSAB_all.txt
while IFS='|' read -r VCUI RCUI VSAB RSAB SON SF SVER VSTART VEND IMETA RMETA SLC SCC SRL TFR CFR CXTY TTYL ATNL LAT CENC CURVER SABIN SSN SCIT; do 
echo "<p><a href=\"http://localhost:8080/$RSAB/\">$RSAB</a></p>" >> qapage.html
done < "$BASEDIR"MRSAB_all.txt