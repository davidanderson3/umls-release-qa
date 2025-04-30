from pathlib import Path  # Provides object-oriented filesystem paths
from bs4 import BeautifulSoup, Comment, Doctype  # HTML parsing, comment and doctype handling
import re  # Regular expressions for special character handling

# ----------------------------------------------------------------------------
# script3.py
# Purpose: Remove the yellow-highlighted info box and related markers
# from LicenseAgreement.html, escape special symbols, remove non-ASCII chars,
# and save the cleaned output.
# Steps:
#   1. Load and parse LicenseAgreement.html
#   2. Remove the styled yellow info box and its <hr>
#   3. Strip corresponding comments
#   4. Escape common special characters and strip any remaining non-ASCII
#   5. Write the cleaned and escaped HTML
# ----------------------------------------------------------------------------

# Configuration: encoding and project path
encoding = "utf-8"
project_root = Path(
    r"C:\Users\rewolinskija\Documents\umls-source-release-1\umls.nlm.nih.gov"
    r"\releasedocs\versions\2025AA"
)

# Define input and output file paths
input_file = (
    project_root
    / "license_scripts"
    / "Begin_with_this_file"
    / "script3"
    / "LicenseAgreement.html"
)
output_file = (
    project_root
    / "license_scripts"
    / "Begin_with_this_file"
    / "script4"
    / "licenseText.html"
)
# Ensure output directory exists
output_file.parent.mkdir(parents=True, exist_ok=True)

# ----------------------------------------------------------------------------
# Step 1: Load HTML into BeautifulSoup
# ----------------------------------------------------------------------------
with input_file.open(encoding=encoding) as infile:
    soup = BeautifulSoup(infile, "html.parser")  # parse into a tree

# ----------------------------------------------------------------------------
# Step 2: Remove the styled yellow info box and following <hr>
# ----------------------------------------------------------------------------
yellow_box = soup.find(
    "p", style="border: thin solid; border-color: #fccd4e; padding: 5px"
)
if yellow_box:
    yellow_box.decompose()
hr = soup.find("hr")
if hr:
    hr.decompose()

# ----------------------------------------------------------------------------
# Step 3: Strip lingering comments related to the yellow box
# ----------------------------------------------------------------------------
for c in soup.find_all(string=lambda t: isinstance(t, Comment)):
    if str(c).strip() in ("yellow box", "end yellow box"):
        c.extract()

# ----------------------------------------------------------------------------
# Step 4: Escape special characters and remove non-ASCII
#   - Convert ©, ®, ™, —, –, … to HTML entities
#   - Remove any remaining non-ASCII characters using regex
# ----------------------------------------------------------------------------
html_content = soup.prettify()  # formatted HTML as string
# Map of characters to HTML entities
entities = {
    '©': '&copy;',
    '®': '&reg;',
    '™': '&trade;',
    '—': '&mdash;',
    '–': '&ndash;',
    '…': '&hellip;'
}
for char, ent in entities.items():
    html_content = html_content.replace(char, ent)
# Remove any remaining non-ASCII characters
html_content = re.sub(r'[^\x00-\x7F]+', '', html_content)

# ----------------------------------------------------------------------------
# Step 5: Write the cleaned and escaped HTML
# ----------------------------------------------------------------------------
with output_file.open("w", encoding=encoding) as outfile:
    outfile.write(html_content)  # final HTML output
