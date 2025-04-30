import requests
from bs4 import BeautifulSoup
from bs4 import Comment
import re


#this script is to update the license_agreement_appendix.html with the changes mentioned in the requirements document

#load the local html file modify the input location path as needed
with open(r'L:\SHARE\Rewolinski\Automating release files\Scripts\Begin_with_this_file\script1\input_license_agreement_appendix.html', encoding="utf8") as f:
    soup = BeautifulSoup(f, "html.parser")

#print(soup) 
#remove comment before <html> tag
comment = soup.find(string=lambda t: isinstance(t, Comment))
comment.extract()

#modify the <head> tag
#create a new <style> tag which will later be appended to header tag
style_tag = soup.new_tag("style")
style_tag.string ='''/*Back-To-Top Button*/
			#topBtn {
				background-color: #e6e6e6;
				border: 1px solid DimGray;
				color: black;
				padding: 6px 7px;
				text-align: center;
				text-decoration: none;
				display: inline-block;
				font-size: 12px;
				font-family: "Roboto", "Times New Roman", monospace;
			}

			/*On Hover Color Change*/
			#topBtn:hover {
				background-color: #87cefa;
				text-decoration: none;
			}'''
head_tag = soup.find("head")

#remove meta and link tags from header tag
head_tag.meta.decompose()
head_tag.link.decompose()

#append newly created style tag to header tag
head_tag.append(style_tag)

#remove <a> and button and <sup> tags from <body>
body_tag = soup.find("body")
body_tag.a.decompose()
body_tag.button.decompose()
sup_tags = soup.find_all("sup")
for sup in sup_tags:
    sup.replaceWith(sup.getText())

#adding comment
comment2 = Comment("back to top button")
soup.html.body.insert(-1, comment2)

#add back to top button 
button_tag = soup.new_tag('button', **{"id":"topBtn"})
body_tag.append(button_tag)
button_a_tag = soup.new_tag('a', **{"href":"#top"})
button_a_tag.string = "Back to Top"
button_tag.append(button_a_tag)

#adding comments
comment3 = Comment("end back to top button")
soup.button.append(comment3)

#create inner div tag in body
wrapper = soup.new_tag('div', **{"class":"gwt-HTML"})
body_children = list(soup.body.children)
soup.body.clear()
soup.body.append(body_children[1])
soup.html.body.h2.insert_after(wrapper)
children = []
for i in range(2, len(body_children)):
    children.append(body_children[i])
#print(children)
for child in children:
    wrapper.append(child)

#create outer div tag in body
wrapper = soup.new_tag('div', **{"id":"license_agreement_appendix1"})
body_children = list(soup.body.children)
soup.body.clear()
soup.body.append(wrapper)
for child in body_children:
    wrapper.append(child)

#adding comments
comment4 = Comment("end div Appendix1")
soup.div.append(comment4)
   
#adding comments
comment5 = Comment("body")
soup.body.append(comment5)

#adding comments
comment1 = Comment("START APPENDIX 1")
soup.html.body.insert(0, comment1)

#adding id to h2 tag of appendix
h2_tag = soup.find("h2")
h2_tag['id'] = "appendix1"

#adding target="_blank"  attributes to all the links
a_tags = soup.find_all('a')
for i in range(0, len(a_tags)):
    a_tags[i]['target'] = "_blank"
    

#remove duplicate strings
dup_tags = soup.find_all('a', href = "https://www.nlm.nih.gov/research/umls/knowledge_sources/metathesaurus/release/license_agreement_snomed.html")
del_tags = []
for k in range(1, len(dup_tags), 2):
    del_tags.append(dup_tags[k])
for element in del_tags:
    element.parent.decompose()
    

#print(soup.prettify(formatter="html"))

# write the output to html file with BeautifulSoup
with open('L:\SHARE\Rewolinski\Automating release files\Scripts\Begin_with_this_file\script2\license_agreement_appendix.html', 'w') as f2:
    pretty_soup = str(soup.prettify(formatter="html"))
    f2.write(pretty_soup)

