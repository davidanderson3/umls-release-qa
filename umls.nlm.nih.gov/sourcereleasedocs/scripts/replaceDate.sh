#!/bin/bash
# It is probably not necessary to use this script for releases. It replaces the create date for files, and this is probably better done manually. Most files already have a create date, which was set to 2017-11-06 when we switched to JBake. 
# set a value for the CREATEDATE variable before running. 
# run from the base directory (.../sourcereleasedocs/)
CREATEDATE='2022-05-02'
while IFS='|' read -r VCUI RCUI VSAB RSAB SON SF SVER VSTART VEND IMETA RMETA SLC SCC SRL TFR CFR CXTY TTYL ATNL LAT CENC CURVER SABIN SSN SCIT; do
sed -i "/date=/c\date=$CREATEDATE" jbake/content/"$RSAB"/index.html;
sed -i "/date=/c\date=$CREATEDATE" jbake/content/"$RSAB"/metadata.html;
sed -i "/date=/c\date=$CREATEDATE" jbake/content/"$RSAB"/stats.html;
sed -i "/date=/c\date=$CREATEDATE" jbake/content/"$RSAB"/sourcerepresentation.html;
sed -i "/date=/c\date=$CREATEDATE" jbake/content/"$RSAB"/metarepresentation.html;
done < MRSAB_all.txt
