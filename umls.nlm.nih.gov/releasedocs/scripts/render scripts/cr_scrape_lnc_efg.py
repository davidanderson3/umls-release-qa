
import requests
from bs4 import BeautifulSoup
import random
import time
from pathlib import Path

# ========================================

def scrape_lnc_efg():
    url = "https://loinc.org/kb/license/"

    try:
        # Step 1: Send a GET request to the website and retrieve the HTML content
        response = requests.get(url)
        if response.status_code == 200:
            html_content = response.text
        else:
            print("Error occurred while fetching HTML content")
            return "",[]

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
        h3_element = div_element.find("h3", id="notice-of-third-party-content-and-copyright-terms")


        # Find and print or return the first paragraph after the h3 element
        notice_para = []
        h3_text = h3_element.get_text(strip=True)

        first_paragraph = h3_element.find_next_sibling("p")
        if first_paragraph:
            first_paragraph_text = first_paragraph.get_text(strip=True)
            notice_para = [h3_text, first_paragraph_text]


        # Step 4: Find the paragraphs starting with "E.", "F.", or "G." and store them in a list
        para_list = []
        next_element = h3_element.find_next_sibling()
        while next_element:
            if next_element.name == "p" and next_element.text.strip().startswith(("E.", "F.", "G.")):
                para_list.append("- " + next_element.text.strip())
            next_element = next_element.find_next_sibling()

        # Step 5: Format the paragraphs as an HTML unordered list
        formatted_html = "<ul>\n" + "\n".join([f"<li>{para}</li>" for para in para_list]) + "\n</ul>"

        # --- Test result prints to html file ---
        # Step 6: Save the formatted HTML to a file
        #Project_file_path = r"C:/Users/rewolinskija/Desktop/Learning python/Scripts templates and output"
<<<<<<<< HEAD:umls.nlm.nih.gov/releasedocs/scripts/render scripts/cr_scrape_lnc_efg.py
        project_path = r"C:\Users\rewolinskija\Documents\umls-source-release-1\umls.nlm.nih.gov\releasedocs\output\testing"
========
        project_path = r"/testing"
>>>>>>>> origin/2025AA:umls.nlm.nih.gov/releasedocs/scripts/misc_scripts/cr_scrape_lnc_efg.py
        html_file_path = Path(project_path) / "loinc_efg.html"
        with open(html_file_path, "w") as file:
            file.write(formatted_html)


        # --- Test result prints to text file ---
        # Step 8: Save the formatted notice_of text, first paragraph, and para_list to a file
        text_file_path = Path(project_path) /  "loinc_efg.txt"
        with open(text_file_path, "w") as file:
            file.write(f"{h3_text}:\n")
            if first_paragraph_text:
                file.write(f"{first_paragraph_text}\n\n")
            file.write("\n".join(para_list))

        # Step 9: Return the modified para_list
        return notice_para, para_list

    except Exception as e:
        print(f"Error occurred during parsing or file saving: {e}")
        return [],[]

# Test the scrape_lnc_efg function
if __name__ == "__main__":
    notice_para, para_list = scrape_lnc_efg()

    print("notice_para:")
    for paragraph in notice_para:
        print(paragraph)
    print("\npara_list")
    for item in para_list:
        print(item)
