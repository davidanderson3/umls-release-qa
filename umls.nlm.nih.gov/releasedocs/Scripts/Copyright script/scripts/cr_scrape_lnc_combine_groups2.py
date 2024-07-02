
import requests
from bs4 import BeautifulSoup
import random
import time
import re
from pathlib import Path


# ========================================

# Function to capture HTML content from the website
def capture_html_content(url):
    try:
        # Send a GET request to the website and retrieve the HTML content
        response = requests.get(url)
        if response.status_code == 200:
            html_content = response.text
            return html_content
        else:
            print("Error occurred while fetching HTML content")
            return None
    except Exception as e:
        print(f"Error occurred during HTML capture: {e}")
        return None


def scrape_lnc_groupinfo():
    url = "https://loinc.org/kb/license/"

    try:
        # Capture the HTML content from the website
        html_content = capture_html_content(url)
        if html_content is None:
            return [], []

        # Step 2: Check the crawl-delay specified in the robots.txt file and add a delay between requests
        robots_url = "https://loinc.org/robots.txt"
        crawl_delay = 1
        try:
            robots_response = requests.get(robots_url).text
            crawl_delay_line = [line for line in robots_response.split("\n") if line.startswith("Crawl-delay")]
            if crawl_delay_line:
                crawl_delay = int(crawl_delay_line[0].split(":")[1].strip())
        except requests.exceptions.RequestException:
            pass

        time.sleep(random.uniform(1, crawl_delay))

        # Step 3: Parse the HTML content using BeautifulSoup
        soup = BeautifulSoup(html_content, "html.parser")
        div_element = soup.find("div", class_="kb-doc")

        group_li = []
        groupinfo_p = []

        h2_element = div_element.find('h2', id='copyright-notice-and-license')
        next_element = h2_element.find_next_sibling()
        '''
        This works to make each element a new line with a dash in front of it
        while next_element:
            if next_element.name == 'ul':
                for li in next_element.find_all('li'):
                    l_text = li.get_text().strip()
                    if l_text.startswith(('"Group 1 Artifacts"', '"Group 3 Artifacts"')):   # If the text for the group items is already enclosed in quotes on the webpage, and you want to include the quotes in the extracted group information, you can modify the code with a single quote then a double quote like this '"text"'
                        group_li.append("- " + l_text)
            elif next_element.name == 'p':
                p_text = next_element.get_text().strip()
                if p_text.startswith(('The Group 1', 'The Group 2')):
                    groupinfo_p.append("- " + p_text)

            next_element = next_element.find_next_sibling()

        group_li = [group.replace('\n', '') for group in group_li]
        '''

        while next_element:
            if next_element.name == 'ul':
                for li in next_element.find_all('li'):
                    l_text = li.get_text().strip()
                    if l_text.startswith(('"Group 1 Artifacts"', '"Group 3 Artifacts"')):
                        group_li.append(l_text)
            elif next_element.name == 'p':
                p_text = next_element.get_text().strip()
                if p_text.startswith(('The Group 1', 'The Group 2')):
                    groupinfo_p.append(p_text)

            next_element = next_element.find_next_sibling()

        group_li = [group.replace('\n', '') for group in group_li]

        group_li_text = "".join(group_li)
        groupinfo_p_text = "".join(groupinfo_p)

        combined_text = group_li_text + groupinfo_p_text

        # Add spaces between sentences
        combined_text = re.sub(r'(?<=[.!?])', ' ', combined_text)

        # Test result prints to html file
        # Step 5: Format the paragraphs as an HTML unordered list
        formatted_html = "<ul>\n" + "\n".join([f"<li>{group}</li>" for group in group_li + groupinfo_p]) + "\n</ul>"

        # Step 6: Save the formatted HTML to a file
        project_path = r"L:\SHARE\Rewolinski\Automating release files\Learning python\Scripts templates and output\Output"
        html_file_path = Path(project_path) / "loinc_group.html"
        with open(html_file_path, "w") as file:
            file.write(formatted_html)

        # Step 7: Save the combined text to a file
        combined_text = group_li_text + groupinfo_p_text
        text_file_path = Path(project_path) / "loinc_group.txt"
        with open(text_file_path, "w") as file:
            file.write(combined_text)

        # Step 8: Return the modified group_li and groupinfo_p
        return group_li, groupinfo_p

    except Exception as e:
        print(f"Error occurred during parsing or file saving: {e}")
        return [],[]

# Main function to perform the scraping for the c and r template files
def main():
    group_li, groupinfo_p = scrape_lnc_groupinfo()

    print("Group List:")
    for paragraph in group_li:
        print(paragraph)
    print("\nGroupinfo_p:")
    for item in groupinfo_p:
        print(item)


if __name__ == "__main__":
    main()