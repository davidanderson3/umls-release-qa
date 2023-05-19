import os
from bs4 import BeautifulSoup

# Function to replace the specified div in an HTML file
def insert_div_after_endmenu(file_path):
    with open(file_path, 'r') as file:
        content = file.read()
        soup = BeautifulSoup(content, 'html.parser')

        # Remove existing <div class="archived"> elements
        for div in soup.find_all('div', class_='archived'):
            div.decompose()

        new_div = '''
        <br/>
        <div class="archived border border-danger p-3" style="background-color:#faf1f0;">
            <b>This source is no longer available in the current version of the UMLS Metathesaurus. To obtain content from this source, download the <a href="https://www.nlm.nih.gov/research/umls/licensedcontent/umlsarchives04.html">2022AA UMLS release</a>. The content is also available in the <a href="https://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html#umls-history-files">UMLS History Files</a>.</b>
        </div>
        '''

        # Find the position of the <!--endmenu--> comment
        end_menu_index = content.find('<!--endmenu-->')

        if end_menu_index != -1:
            # Insert the new div after the <!--endmenu--> comment
            new_content = content[:end_menu_index + len('<!--endmenu-->')] + new_div + content[end_menu_index + len('<!--endmenu-->'):]

            # Save the modified HTML back to the file
            with open(file_path, 'w') as file:
                file.write(new_content)

# Directory where the HTML files are located
root_directory = '/Users/andersondm2/umlsdoc/umls.nlm.nih.gov/sourcereleasedocs/jbake/content'

# Iterate through all directories and files
for root, dirs, files in os.walk(root_directory):
    if os.path.basename(root).startswith('NCI_'):
        for file_name in files:
            file_path = os.path.join(root, file_name)

            if file_name.endswith('.html'):
                insert_div_after_endmenu(file_path)
