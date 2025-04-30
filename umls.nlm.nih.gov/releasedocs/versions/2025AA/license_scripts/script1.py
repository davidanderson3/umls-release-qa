from pathlib import Path  # Cross-platform filesystem paths
from bs4 import BeautifulSoup, Comment  # HTML parsing and comment handling

# ----------------------------------------------------------------------------
# script1.py
# Purpose: Update the appendix HTML file per requirements:
#   1. Remove the initial metadata comment
#   2. Clean up <head> by removing legacy tags and injecting Back-To-Top styles
#   3. Simplify <body> by removing old links/buttons and converting superscripts
#   4. Insert a Back-To-Top button with annotation comments
#   5. Wrap content in inner and outer <div> containers for structure
#   6. Annotate sections with comments and stable IDs
#   7. Deduplicate specific SNOMED license links and enforce new-tab behavior
# ----------------------------------------------------------------------------

# Configuration
encoding = "utf-8"  # Use UTF-8 for consistent file encoding
project_root = Path(
    r"C:\Users\rewolinskija\Documents\umls-source-release-1"
    r"\umls.nlm.nih.gov\releasedocs\versions\2025AA"
)
input_file = (
    project_root
    / "license_scripts"
    / "Begin_with_this_file"
    / "script1"
    / "input_license_agreement_appendix.html"
)
output_file = (
    project_root
    / "license_scripts"
    / "Begin_with_this_file"
    / "script2"
    / "license_agreement_appendix.html"
)

# Ensure the output directory exists
output_file.parent.mkdir(parents=True, exist_ok=True)

# ----------------------------------------------------------------------------
# Step 1: Load and parse the HTML document
# ----------------------------------------------------------------------------
with input_file.open(encoding=encoding) as infile:
    soup = BeautifulSoup(infile, "html.parser")

# ----------------------------------------------------------------------------
# Step 2: Remove the first HTML comment (metadata/disclaimer)
# ----------------------------------------------------------------------------
first_comment = soup.find(string=lambda t: isinstance(t, Comment))
if first_comment:
    first_comment.extract()

# ----------------------------------------------------------------------------
# Step 3: Clean <head> section
#   - Remove all <meta> and <link> tags
#   - Add custom <style> for Back-To-Top button
# ----------------------------------------------------------------------------
style_tag = soup.new_tag("style")
style_tag.string = (
    "/* Back-To-Top Button Styles */\n"
    "#topBtn { background-color: #e6e6e6; border: 1px solid DimGray;"
    " padding: 6px 7px; font-size: 12px; font-family: 'Roboto', monospace; }\n"
    "#topBtn:hover { background-color: #87cefa; text-decoration: none; }"
)
head = soup.find("head")
if head:
    for tag in head.find_all(["meta", "link"]):
        tag.decompose()
    head.append(style_tag)

# ----------------------------------------------------------------------------
# Step 4: Simplify <body> content
#   - Remove legacy <a> and <button> tags
#   - Convert <sup> tags to plain text
#   - Insert Back-To-Top button with markers
# ----------------------------------------------------------------------------
body = soup.find("body")
if body:
    first_a = body.find("a")
    if first_a:
        first_a.decompose()

    existing_btn = body.find("button", id="topBtn")
    if existing_btn:
        existing_btn.decompose()

    for sup in body.find_all("sup"):
        sup.replace_with(sup.get_text())

    body.insert(-1, Comment("back to top button"))
    back_btn = soup.new_tag("button", id="topBtn")
    link = soup.new_tag("a", href="#top")
    link.string = "Back to Top"
    back_btn.append(link)
    body.append(back_btn)
    back_btn.append(Comment("end back to top button"))

    # Wrap remaining content in an inner div for styling
    inner = soup.new_tag("div", **{"class": "gwt-HTML"})
    children = list(body.children)
    first_elem = children[1] if len(children) > 1 else None
    body.clear()
    if first_elem:
        body.append(first_elem)
    if body.h2:
        body.h2.insert_after(inner)
    for child in children[2:]:
        inner.append(child)

    # Wrap the inner div in an outer container with a unique ID
    outer = soup.new_tag("div", id="license_agreement_appendix1")
    contents = list(body.children)
    body.clear()
    body.append(outer)
    for item in contents:
        outer.append(item)

    # Annotate sections and assign stable IDs
    outer.append(Comment("end div Appendix1"))
    body.append(Comment("body"))
    body.insert(0, Comment("START APPENDIX 1"))
    h2 = soup.find("h2")
    if h2:
        h2["id"] = "appendix1"

    # Ensure all external links open in a new tab
    for a in soup.find_all("a"):
        a["target"] = "_blank"

    # Remove duplicate SNOMED license links (every second occurrence)
    dup_links = soup.find_all(
        "a",
        href=(
            "https://www.nlm.nih.gov/research/umls/knowledge_sources/"
            "metathesaurus/release/license_agreement_snomed.html"
        ),
    )
    for dup in dup_links[1::2]:
        if dup.parent:
            dup.parent.decompose()

# ----------------------------------------------------------------------------
# Step 5: Write the transformed HTML back to disk
# ----------------------------------------------------------------------------
with output_file.open("w", encoding=encoding) as outfile:
    outfile.write(soup.prettify())
