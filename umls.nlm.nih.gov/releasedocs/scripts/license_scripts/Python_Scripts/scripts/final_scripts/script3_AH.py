import requests
from bs4 import BeautifulSoup, Doctype
from bs4 import Comment
import re

#this script is to remove the yellow box from LicenseAgreement.html and save the resulting file as licenseText.html

#load the local html file modify the input location path as needed
with open(r'L:\SHARE\Rewolinski\Automating release files\Scripts\Begin_with_this_file\script3\LicenseAgreement.html', encoding="utf8") as f:
    soup = BeautifulSoup(f, "html.parser")

#removing the yellow box
p_tag = soup.find('p', {'style':'border: thin solid; border-color: #fccd4e; padding: 5px'})
soup.p.decompose()
soup.hr.decompose()

#removing yellow box comments 
comments = soup.find_all(string=lambda t: isinstance(t, Comment))
#print(comments)
for c in  range(0, len(comments)):
    if comments[c] == "yellow box" or comments[c] == "end yellow box":
        comments[c].extract()

#print(soup.prettify(formatter="html"))
with open('L:\SHARE\Rewolinski\Automating release files\Scripts\Begin_with_this_file\script4\licenseText.html', 'w') as f2:
    pretty_soup = str(soup.prettify(formatter="html"))
    f2.write(pretty_soup)
