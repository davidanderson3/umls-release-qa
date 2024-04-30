import requests
from bs4 import BeautifulSoup, Doctype
from bs4 import Comment
import re

#this script is to create LicenseAgreement.html with the divs from the 3 license files and changes mentioned in the requirements document
#-----------------------------------------------------------------------------



#-----------------------------------------------------------------------------
##initial_License_Agreement.html
#-----------------------------------------------------------------------------



#load the local html file modify the input location path as needed
with open(r'C:\Users\rewolinskija\Documents\umls-source-release\umls.nlm.nih.gov\releasedocs\2024AA\Scripts\License scripts\begin_with_this_file\script2\initial_license_agreement.html', encoding="utf8") as f:
    soup = BeautifulSoup(f, "html.parser")

#removing the <HTML>,<head>,<body> tags using beautifulSoup's unwrap() method.
#unwrap() method replaces a tag with the contents inside that tag, returning the tag that was replaced.
soup.html.unwrap()
soup.head.unwrap()
soup.body.unwrap()

#making changes to the <style> tag
style_tag = soup.style
style_tag.string = '''
    table,
	th,
	td {
		border: 1px solid black;
		border-collapse: collapse;
	}
	th,
	td {
		padding: 5px;
    }'''

#removing all existing comments
comments = soup.find_all(string=lambda t: isinstance(t, Comment))
#print(comments)
for c in  range(0, len(comments)):
    comments[c].extract()

#adding comment
comment1 = Comment("START LICENSE AGREEMENT")
soup.div.insert_before(comment1)

#add id = license_agreement to <div> tag
div_tag = soup.find("div")
div_tag['id'] = "license_agreement"

#adding yellow box paragraph
new_p = soup.new_tag("p", **{"style":"border: thin solid; border-color: #fccd4e; padding: 5px"})
new_p.string = '''This is a copy of the License Agreement for Use of the UMLS®
		Metathesaurus® for the 2024AA Release from 05/06/2024. To view the
		current license agreement, go to'''
new_a1 = soup.new_tag("a", **{"href":"https://uts.nlm.nih.gov/uts/assets/LicenseAgreement.pdf", "target":"_blank"})
new_a1.string = "https://uts.nlm.nih.gov/uts/assets/LicenseAgreement.pdf"
soup.div.insert(0, new_p)
soup.p.append(new_a1)
str_p = ". To sign up for a UMLS license, go to"
soup.p.append(str_p)
new_a2 = soup.new_tag("a", **{"href":"https://uts.nlm.nih.gov/uts/signup-login", "target":"_blank"})
new_a2.string = "https://uts.nlm.nih.gov/uts/signup-login"
soup.p.append(new_a2)
str2_p = "."
soup.p.append(str2_p)
hr_tag = soup.new_tag("hr")
soup.p.insert_after(hr_tag)

#adding comments
comment2 = Comment("yellow box")
soup.div.insert(0, comment2)

#adding comments
comment3 = Comment("end yellow box")
soup.hr.insert_after(comment3)

#adding comments
comment4 = Comment("Do not change the id of <h2> tag")
soup.h2.insert_before(comment4)



#adjusting comments location in the file
comment5 = Comment("end div licenseagreement")
soup.div.append(comment5)

#removing the empty <p> tag from inner <div>
#creating a list with empty tags which should not be removed like <hr/>
#empty_whitelist = ['style','hr']
# Iterate each line
#for x in soup.find_all():
#
    # fetching text from tag and remove whitespaces
    #if len(x.get_text(strip=True)) == 0 and (x.name not in empty_whitelist):

        # Remove empty tag
        #x.extract()

#remove top button <button> tag
button_tag = soup.find('button', id = "topBtn")
button_tag.decompose()

#adding target="_blank"  attributes to all the links
a_tags = soup.find_all('a')
for i in range(0, len(a_tags)):
    a_tags[i]['target'] = "_blank"

#adding comment
comment6 = Comment("END LICENSE AGREEMENT")
soup.div.insert_after(comment6)



#-----------------------------------------------------------------------------
#-----------------------------------------------------------------------------
##license_Agreement_appendix.html
#-----------------------------------------------------------------------------
#-----------------------------------------------------------------------------


#load the local html file modify the input location path as needed
with open(r'C:\Users\rewolinskija\Documents\umls-source-release\umls.nlm.nih.gov\releasedocs\2024AA\Scripts\License scripts\begin_with_this_file\script2\license_agreement_appendix.html', encoding="utf8") as f2:
    soup2 = BeautifulSoup(f2, "html.parser")

#removing the <HTML>,<head>,<body> tags using beautifulSoup's unwrap() and decompose().
#unwrap() method replaces a tag with the contents inside that tag, returning the tag that was replaced.
#decompose() method removes the tag and completely  destroys it and its content.
soup2.html.unwrap()
soup2.head.decompose()
soup2.body.unwrap()

#remove top button <button> tag
button_tag2 = soup2.find('button', id = "topBtn")
button_tag2.decompose()

#adding comment
comment_end = Comment("END APPENDIX 1")
soup2.div.insert_after(comment_end)

#removing button tag and body comments
comments2 = soup2.find_all(string=lambda t: isinstance(t, Comment))
#print(comments2)
for ct in  range(0, len(comments2)):
    if comments2[ct] == "back to top button" or comments2[ct] == "end back to top button":
        comments2[ct].extract()



#-----------------------------------------------------------------------------------------
##License_Agreement_snomed.html
#-----------------------------------------------------------------------------------------



#load the local html file modify the input location path as needed
with open(r'C:\Users\rewolinskija\Documents\umls-source-release\umls.nlm.nih.gov\releasedocs\2024AA\Scripts\License scripts\begin_with_this_file\script2\license_agreement_snomed.html', encoding="utf8") as f3:
    soup3 = BeautifulSoup(f3, "html.parser")

#removing <DOCTYPE> tag
for item in soup3.contents:
    if isinstance(item, Doctype):
        item.extract()

#removing the <HTML>,<head>,<body> tags using beautifulSoup's unwrap() and decompose().
#unwrap() method replaces a tag with the contents inside that tag, returning the tag that was replaced.
#decompose() method removes the tag and completely  destroys it and its content.
soup3.html.unwrap()
soup3.head.decompose()
soup3.body.unwrap()

#removing all existing comments
comments_1 = soup3.find_all(string=lambda t: isinstance(t, Comment))
#print(comments)
for d in  range(0, len(comments_1)):
    comments_1[d].extract()

#add id = license_agreement to <div> tag
div_tag3 = soup3.find("div")
div_tag3['id'] = "license_appendix2"

#Adding <br/> tag
br_tag = soup3.new_tag("br")
soup3.div.insert_before(br_tag)

#adjusting comments location in the file
comment_br = Comment("START APPENDIX 2")
soup3.br.insert_after(comment_br)
comment_div = Comment("end div Appendix 2")
soup3.div.insert(-1, comment_div)
comment_end = Comment("END APPENDIX 2")
soup3.div.insert_after(comment_end)

#remove top button <button> tag
button_tag3 = soup3.find('button', id = "topBtn")
button_tag3.decompose()

#adding Appendix A table comment
comment_table_start = Comment("Start table for Appendix A")
comment_table_end = Comment("End table Appendix A")
table_tag = soup.find('table', class_= "table table-bordered")
soup3.table.insert_before(comment_table_start)
soup3.table.insert_after(comment_table_end)

#removing the empty <p> tag from <div>
#creating a list with empty tags which should not be removed like <hr/>
empty_whitelist = ['br', 'hr']
# Iterate each line
for y in soup3.find_all():

    # fetching text from tag and remove whitespaces
    if len(y.get_text(strip=True)) == 0 and (y.name not in empty_whitelist):

        # Remove empty tag
        y.extract()



# ---------------------------------
# write output
# ---------------------------------

#print(soup2.prettify(formatter="html"))
#print(soup.prettify(formatter="html") + soup2.prettify(formatter="html") + soup3.prettify(formatter="html"))

# write the output to html file with BeautifulSoup
with open(r'C:\Users\rewolinskija\Documents\umls-source-release\umls.nlm.nih.gov\releasedocs\2024AA\Scripts\License scripts\begin_with_this_file\script3\LicenseAgreement.html', 'w') as f4:
    pretty_soup = str(soup.prettify(formatter="html") + soup2.prettify(formatter="html") + soup3.prettify(formatter="html"))
    f4.write(pretty_soup)
