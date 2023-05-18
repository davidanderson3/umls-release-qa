import os
from bs4 import BeautifulSoup

# Function to replace the specified div in an HTML file
def replace_div_in_html(file_path):
    with open(file_path, 'r') as file:
        content = file.read()
        soup = BeautifulSoup(content, 'html.parser')
        divs = soup.find_all('div', class_='archived')

        if len(divs) > 0:
            for div in divs:
                new_div = soup.new_tag('div')
                new_div['class'] = 'new-div'

                bold_tag = soup.new_tag('b')
                bold_tag.string = 'This source is no longer available in the current UMLS release files. To get content from this source, download the '

                link1_tag = soup.new_tag('a')
                link1_tag['href'] = 'https://www.nlm.nih.gov/research/umls/licensedcontent/umlsarchives04.html'
                link1_tag.string = '2022AA UMLS release'

                link2_tag = soup.new_tag('a')
                link2_tag['href'] = 'https://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html'
                link2_tag.string = 'UMLS History Files'

                bold_tag.append(link1_tag)
                bold_tag.append('. The content is also available in the ')
                bold_tag.append(link2_tag)
                bold_tag.append('.')

                new_div.append(bold_tag)

                # Replace the old div with the new div
                div.replace_with(new_div)

            # Save the modified HTML back to the file
            with open(file_path, 'w') as file:
                file.write(str(soup))

# Directory where the HTML files are located
root_directory = '/path/to/your/directory'

# Iterate through all directories and files
for root, dirs, files in os.walk(root_directory):
    for file_name in files:
        file_path = os.path.join(root, file_name)

        if root.startswith('/path/to/your/directory/NCI_') and file_name.endswith('.html'):
            replace_div_in_html(file_path)
