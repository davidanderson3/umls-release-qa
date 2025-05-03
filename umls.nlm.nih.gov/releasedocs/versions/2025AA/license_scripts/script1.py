from pathlib import Path  # Cross-platform filesystem path handling
from bs4 import BeautifulSoup, Comment, Tag  # HTML parsing, comment, and tag handling

# ----------------------------------------------------------------------------
# script1.py
# Purpose: Transform the appendix HTML file by:
#   1. Removing initial metadata comments
#   2. Cleaning the <head> section and injecting Back-To-Top CSS
#   3. Simplifying <body>: removing anchors/buttons, converting <sup> to text
#   4. Inserting a Back-To-Top button with markers
#   5. Wrapping content: placing the first <h2> in outer and the rest in inner <div>
#   6. Annotating sections with comments and stable IDs
#   7. Deduplicating SNOMED license links and enforcing target="_blank"
# ----------------------------------------------------------------------------

# Set up directories based on this script's location
script_dir = Path(__file__).resolve().parent
source_dir = script_dir / "Begin_with_this_file" / "script1"
output_dir = script_dir / "Begin_with_this_file" / "script2"
output_dir.mkdir(parents=True, exist_ok=True)

# Define file paths
input_file = source_dir / "input_license_agreement_appendix.html"
output_file = output_dir / "license_agreement_appendix.html"

# Step 1: Load HTML document
with input_file.open(encoding="utf-8") as f:
    soup = BeautifulSoup(f, "html.parser")

# Step 2: Remove the first HTML comment (metadata/disclaimer)
first_comment = soup.find(string=lambda t: isinstance(t, Comment))
if first_comment:
    first_comment.extract()

# Step 3: Clean the <head> section
if soup.head:
    # Remove all <meta> and <link> tags to avoid legacy references
    for tag in soup.head.find_all(["meta", "link"]):
        tag.decompose()
    # Inject Back-To-Top button CSS
    style = soup.new_tag("style")
    style.string = (
        "#topBtn { background-color: #e6e6e6; border: 1px solid DimGray;"
        " padding: 6px 7px; font-size: 12px; font-family: 'Roboto', monospace; }"
        "#topBtn:hover { background-color: #87cefa; text-decoration: none; }"
    )
    soup.head.append(style)

# Step 4: Simplify the <body> content
if soup.body:
    body = soup.body
    # Remove any <a name="top"> anchors
    top_anchor = body.find("a", attrs={"name": "top"})
    if top_anchor:
        top_anchor.decompose()
    # Remove existing Back-To-Top buttons by ID
    for btn_id in ("myBtn", "topBtn"):
        btn = body.find("button", id=btn_id)
        if btn:
            btn.decompose()
    # Convert all <sup> tags to their text content
    for sup in body.find_all("sup"):
        sup.replace_with(sup.get_text())
    # Insert new Back-To-Top button with comment markers
    body.append(Comment("back to top button"))
    back_btn = soup.new_tag("button", id="topBtn")
    link = soup.new_tag("a", href="#top")
    link.string = "Back to Top"
    back_btn.append(link)
    body.append(back_btn)
    back_btn.append(Comment("end back to top button"))

# Step 5: Wrap content into outer and inner divs
if soup.body:
    body = soup.body
    # Capture all current nodes (including text and tags)
    all_nodes = list(body.contents)
    # Clear body for restructuring
    body.clear()
    # Add start marker
    body.append(Comment("START APPENDIX 1"))
    # Create outer container
    outer = soup.new_tag("div", id="license_agreement_appendix1")
    body.append(outer)
    # Inside outer, place first <h2> (if any)
    first_h2 = None
    for node in all_nodes:
        if isinstance(node, Tag) and node.name == "h2":
            first_h2 = node
            break
    if first_h2:
        first_h2.extract()
        first_h2["id"] = "appendix1"
        outer.append(first_h2)
    # Create inner container for remaining content
    inner = soup.new_tag("div", **{"class": "gwt-HTML"})
    outer.append(inner)
    # Append all other nodes to inner
    for node in all_nodes:
        if node is first_h2:
            continue
        inner.append(node)
    # Close outer div and mark end of section
    outer.append(Comment("end div Appendix1"))
    body.append(Comment("body"))

# Step 6: Ensure all links open in a new tab
for a in soup.find_all("a"):
    a["target"] = "_blank"

# Step 7: Deduplicate SNOMED license links
snomed_href = (
    "https://www.nlm.nih.gov/research/umls/knowledge_sources/"
    "metathesaurus/release/license_agreement_snomed.html"
)
for dup in soup.find_all("a", href=snomed_href)[1::2]:
    if dup.parent:
        dup.parent.decompose()

# Final Step: Write the transformed HTML to output
with output_file.open("w", encoding="utf-8") as f:
    f.write(soup.prettify())
