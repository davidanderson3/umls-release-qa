#!/bin/bash 
# Updates the 'updated' date in the jbake metadata. Should match the latest release date. 
# set a value for the UPDATEDATE variable before running
# run from the base directory (.../sourcereleasedocs/)
UPDATEDATE='2018-05-07'
while IFS='|' read -r VCUI RCUI VSAB RSAB SON SF SVER VSTART VEND IMETA RMETA SLC SCC SRL TFR CFR CXTY TTYL ATNL LAT CENC CURVER SABIN SSN SCIT; do
sed -i "/updated=/c\updated=$UPDATEDATE" jbake/content/"$RSAB"/index.html;
sed -i "/updated=/c\updated=$UPDATEDATE" jbake/content/"$RSAB"/metadata.html;
sed -i "/updated=/c\updated=$UPDATEDATE" jbake/content/"$RSAB"/stats.html;
sed -i "/updated=/c\updated=$UPDATEDATE" jbake/content/"$RSAB"/sourcerepresentation.html;
sed -i "/updated=/c\updated=$UPDATEDATE" jbake/content/"$RSAB"/metarepresentation.html;
done < MRSAB_all.txt 
