#!/bin/bash 
# Updates the 'updated' date in the jbake metadata. Should match the latest release date. 
# set a value for the UPDATEDATE variable before running
# run from the base directory (.../sourcereleasedocs/)
UPDATEDATE='2023-11-06'
while IFS='|' read -r VCUI RCUI VSAB RSAB SON SF SVER VSTART VEND IMETA RMETA SLC SCC SRL TFR CFR CXTY TTYL ATNL LAT CENC CURVER SABIN SSN SCIT; do
[ -f jbake/content/"$RSAB"/index.html ] && sed -i '' -e "/updated=/c\ 
updated=$UPDATEDATE" jbake/content/"$RSAB"/index.html;
[ -f jbake/content/"$RSAB"/metadata.html ] && sed -i '' -e "/updated=/c\ 
updated=$UPDATEDATE" jbake/content/"$RSAB"/metadata.html;
[ -f jbake/content/"$RSAB"/stats.html ] && sed -i '' -e "/updated=/c\ 
updated=$UPDATEDATE" jbake/content/"$RSAB"/stats.html;
[ -f jbake/content/"$RSAB"/sourcerepresentation.html ] && sed -i '' -e "/updated=/c\ 
updated=$UPDATEDATE" jbake/content/"$RSAB"/sourcerepresentation.html;
[ -f jbake/content/"$RSAB"/metarepresentation.html ] && sed -i '' -e "/updated=/c\ 
updated=$UPDATEDATE" jbake/content/"$RSAB"/metarepresentation.html;
done < MRSAB_all.txt 
