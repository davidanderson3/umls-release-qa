#!/bin/bash
# This script replaces the title field in the jbake metadata. It uses MRSAB.RRF to insert the RSAB and SSN into the title field. 
# Run this script from /umlsdoc/umls.nlm.nih.gov/sourcereleasedocs/ - bash scripts/create_rsab_page.sh' 
rm jbake/content/source-abbreviations.html
sort -k4 -t'|' MRSAB_all.txt -o MRSAB_all.txt
echo "title=UMLS Source Abbreviations
date=2018-04-16
updated=2018-04-16
type=page
status=published
~~~~~~

<br/>
<p>UMLS source vocabulary abbreviations are used to identify UMLS source vocabularies in the <a href="https://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html">UMLS release files</a> and the <a href="https://documentation.uts.nlm.nih.gov/rest/home.html">UMLS API</a>. 
<table>
<tr><th>Abbreviation</th><th>Vocabulary Name</th></tr>
" >> jbake/content/source-abbreviations.html
while IFS='|' read -r VCUI RCUI VSAB RSAB SON SF SVER VSTART VEND IMETA RMETA SLC SCC SRL TFR CFR CXTY TTYL ATNL LAT CENC CURVER SABIN SSN SCIT; do 
echo "<tr><td>$RSAB</td><td><a href=\"$SSN/\">$SSN</a></td></tr>" >> jbake/content/source-abbreviations.html
done < MRSAB_all.txt

echo "</table>" >> jbake/content/source-abbreviations.html