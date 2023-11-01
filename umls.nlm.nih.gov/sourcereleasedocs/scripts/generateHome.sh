#!/bin/bash

# Remove the old home_table.html if it exists
rm -f home_table.html

# Generate the initial part of the HTML table
echo -e "<p>Basic information about the source vocabularies represented in the UMLS.</p>\n<form id=\"form1\"></form>\n<table id=\"example\" class=\"display\" style=\"display:none;\" >" > home_table.html

# Generate the table header
echo -e "<thead>\n<tr>\n<th>Abbreviation</th>\n<th>Name</th>\n<th>Last Updated</th>\n<th>Language</th>\n<th>Restriction Level <a href=\"https://uts.nlm.nih.gov/uts/license/license-category-help.html\" target=\"_blank\">(?)</a></th>\n</tr>\n</thead>" >> home_table.html

# Read the MRSAB_all.txt file line by line and populate the table
while IFS='|' read -r VCUI RCUI VSAB RSAB SON SF SVER VSTART VEND IMETA RMETA SLC SCC SRL TFR CFR CXTY TTYL ATNL LAT CENC CURVER SABIN SSN SCIT; do
    # Include only rows where CURVER is "Y"
    if [ "$CURVER" != "Y" ]; then
        continue
    fi

    echo -e "<tr><td><a href=\"//www.nlm.nih.gov/research/umls/sourcereleasedocs/current/$RSAB\">$RSAB</a></td>\n<td>$SSN</td>\n<td>$IMETA</td>\n<td>$LAT</td>\n<td>$SRL</td></tr>" >> home_table.html
done < MRSAB_all.txt

# Close the table tag
echo -e "</table>" >> home_table.html
