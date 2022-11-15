#!/bin/bash
# Generate the vocab docs home page for pasting into Teamsite
rm home_table.html
echo "<p>Basic information about the source vocabularies represented in the UMLS.</p>
<form id=\"form1\"></form>
<table id=\"example\" class=\"display\" style=\"display:none;\" >" >> home_table.html
echo "<thead>
    <tr>
      <th>Abbreviation</th>
      <th>Name</th>
      <th>Last Updated</th>
      <th>Language</th>
      <th>Restriction Level <a href="https://uts.nlm.nih.gov/uts/license/license-category-help.html" target="_blank">
               (?)
            </a></th>
      
    </tr>
  </thead>" >> home_table.html

CREATEDATE='2017-11-06'
while IFS='|' read -r VCUI RCUI VSAB RSAB SON SF SVER VSTART VEND IMETA RMETA SLC SCC SRL TFR CFR CXTY TTYL ATNL LAT CENC CURVER SABIN SSN SCIT; do
echo "<tr><td><a href=\"//www.nlm.nih.gov/research/umls/sourcereleasedocs/current/"$RSAB"\">$RSAB</a></td>" >> home_table.html;
echo "<td>"$SSN"</td>" >> home_table.html;
echo "<td>"$IMETA"</td>" >> home_table.html;
echo "<td>"$LAT"</td>" >> home_table.html;
echo "<td>"$SRL"</td></tr>" >> home_table.html;
done < MRSAB_all.txt
echo "</table>" >> home_table.html
