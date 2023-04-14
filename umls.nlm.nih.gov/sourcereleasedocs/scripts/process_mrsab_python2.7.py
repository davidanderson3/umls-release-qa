
#!/usr/bin/python

import csv
import os
import sys

os.system ("awk -F\| '$22==\"Y\"' MRSAB.RRF > MRSAB_all.txt")
os.system ("awk -F\| '$22==\"Y\"' MRSAB.RRF | awk -F\| '$10==\"2017AB\"' | cut -f3 -d\| | sort | uniq > vsab.txt")
os.system ("awk -F\| '$22==\"Y\"' MRSAB.RRF | awk -F\| '$10==\"2017AB\"' > MRSAB_2018AA.txt")

f = file('MRSAB_all.txt', 'rb')
reader = csv.reader(f, delimiter='|')

for row in reader:
	f = open('jbake/content/'+row[3]+'/metadata.html', 'wb')
	f.write(
	"title=" + "\n"
	"date=2017-11-06" + "\n"
	"updated=" + "\n"
	"type=page" + "\n"
	"status=published" + "\n"
	"~~~~~~" + "\n"
	"<!--menu-->" + "\n"
	"<!--endmenu-->" + "\n"
    "<br/>" + "\n"
    "<table>" + "\n"
    "<tr>" + "\n"
    "<th>Field</th>" + "\n"
    "<th>Value</th>" + "\n"
    "</tr>" + "\n"
    "<tr><td>Versioned Source Abbreviation</td><td>" + row[2] + "</td></tr>" + "\n"
    "<tr><td>Source Official Name</td><td>" + row[4] + "</td></tr>" + "\n"
    "<tr><td>Short Name</td><td>" + row[23] + "</td></tr>" + "\n"
    "<tr><td>Family</td><td>" + row[5] + "</td></tr>" + "\n"
    "<tr><td>Metathesaurus Insertion Version</td><td>" + row[9] + "</td></tr>" + "\n"
    "<tr><td>Restriction Level</td><td>" + row[13] + "</td></tr>" + "\n"
    "<tr><td>Language</td><td>" + row[19] + "</td></tr>" + "\n"
    "<tr><td>Context Type</td><td>" + row[16] + "</td></tr>" + "\n"
    "<tr><td>License Contact</td><td>" + row[11] + "</td></tr>" + "\n"
    "<tr><td>Content Contact</td><td>" + row[12] + "</td></tr>" + "\n"
    "<tr><td>Citation</td><td>" + row[24] + "</td></tr>" + "\n"
    "</table>"
	)
	f.close()
	
