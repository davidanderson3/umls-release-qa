from pathlib import Path  # Filesystem path handling
from bs4 import BeautifulSoup, Comment, Doctype  # HTML parsing and comment/doctype handling
import re  # Regular expressions for entity conversion and non-ASCII stripping

# ----------------------------------------------------------------------------
# script3.py
# Purpose:
#   - Remove the yellow info box and its separator
#   - Strip related comments
#   - Escape special characters (©, ®, ™, —, –, …)
#   - Strip any remaining non-ASCII characters
#   - Preserve all other content (head, style, body structure)
# ----------------------------------------------------------------------------

# Configuration
encoding = "utf-8"
script_dir = Path(__file__).resolve().parent
source_dir = script_dir / "Begin_with_this_file" / "script3"
output_dir = script_dir / "Begin_with_this_file" / "script4"
output_dir.mkdir(parents=True, exist_ok=True)

# File paths
input_file = source_dir / "LicenseAgreement.html"
output_file = output_dir / "licenseText.html"

# Step 1: Load HTML into BeautifulSoup
with input_file.open(encoding=encoding) as infile:
    soup = BeautifulSoup(infile, "html.parser")  # parse into a tree

# Step 2: Remove the styled yellow info box
#   - Identified by its inline style rule
#   - Also remove the horizontal rule that follows
yellow_box = soup.find(
    "p", style="border: thin solid; border-color: #fccd4e; padding: 5px"
)
if yellow_box:
    yellow_box.decompose()  # drop the info box entirely

hr = soup.find("hr")  # visual separator after the box
if hr:
    hr.decompose()

# Step 3: Strip any lingering comments related to the yellow box
for c in soup.find_all(string=lambda t: isinstance(t, Comment)):
    if str(c).strip() in ("yellow box", "end yellow box"):
        c.extract()

# Step 4: Escape special characters and remove non-ASCII
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

# Step 5: Wrap with html, head, and body tags, then write output
import re as _re  # internal regex
# Extract style tag for head
style_match = _re.search(r'<style>.*?</style>', html_content, flags=_re.DOTALL)
if style_match:
    style_str = style_match.group(0)
    body_str = html_content.replace(style_str, '').strip()
    final_output = f"<html>
<head>
{style_str}
</head>

<body>
{body_str}
</body>
</html>"
else:
    final_output = f"<html>
<head></head>

<body>
{html_content}
</body>
</html>"

with output_file.open("w", encoding=encoding) as outfile:
    outfile.write(final_output)
