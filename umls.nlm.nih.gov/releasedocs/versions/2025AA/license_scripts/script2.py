from pathlib import Path
from bs4 import BeautifulSoup, Comment, Doctype

# ----------------------------------------------------------------------------
# script2.py
# Purpose: Merge and annotate three license HTML sections into one document.
# Steps:
#   1. Load three HTML files from 'Begin_with_this_file/script2'
#   2. Clean, annotate, and style each section
#   3. Combine and write final LicenseAgreement.html to 'script3'
# ----------------------------------------------------------------------------

# Configuration
encoding = "utf-8"  # Consistent text encoding
script_dir = Path(__file__).resolve().parent
source_dir = script_dir / "Begin_with_this_file" / "script2"
output_dir = script_dir / "Begin_with_this_file" / "script3"
output_dir.mkdir(parents=True, exist_ok=True)

# Input and output file paths
initial_file = source_dir / "initial_license_agreement.html"
appendix_file = source_dir / "license_agreement_appendix.html"
snomed_file = source_dir / "license_agreement_snomed.html"
final_file = output_dir / "LicenseAgreement.html"


def load_soup(path: Path, parser: str = "html.parser") -> BeautifulSoup:
    """Load HTML file from disk into a BeautifulSoup object."""
    with path.open(encoding=encoding) as f:
        return BeautifulSoup(f, parser)


def process_initial(soup: BeautifulSoup) -> None:
    """
    Prepare the initial license section:
      - Unwrap <html>, <head>, <body>
      - Apply table styling
      - Remove existing comments
      - Insert start/end markers
      - Create and insert the yellow info box
      - Clean up empty tags and buttons
    """
    # Unwrap wrapper tags
    soup.html.unwrap()
    soup.head.unwrap()
    soup.body.unwrap()

    # Table styling for readability
    style_el = soup.style
    style_el.string = (
        "table, th, td { border: 1px solid black; border-collapse: collapse; } "
        "th, td { padding: 5px; }"
    )

    # Remove all pre-existing comments
    for c in soup.find_all(string=lambda t: isinstance(t, Comment)):
        c.extract()

    # Annotate start of license agreement
    soup.div.insert_before(Comment("START LICENSE AGREEMENT"))
    main_div = soup.find("div")
    main_div["id"] = "license_agreement"

    # Build yellow info box
    info = soup.new_tag(
        "p",
        style="border: thin solid; border-color: #fccd4e; padding: 5px"
    )
    info.string = (
        "This is a copy of the License Agreement for Use of the UMLS® Metathesaurus® "
        "for the 2025AA Release from 05/05/2025. To view the current license agreement, go to "
    )
    link1 = soup.new_tag(
        "a",
        href="https://uts.nlm.nih.gov/uts/assets/LicenseAgreement.pdf",
        target="_blank"
    )
    link1.string = link1["href"]
    info.append(link1)
    info.append(". To sign up for a UMLS license, go to ")
    link2 = soup.new_tag(
        "a",
        href="https://uts.nlm.nih.gov/uts/signup-login",
        target="_blank"
    )
    link2.string = link2["href"]
    info.append(link2)
    info.append('.')

    # Insert yellow box comment, info, and separator
    main_div.insert(0, Comment("yellow box"))
    main_div.insert(1, info)
    hr = soup.new_tag("hr")
    info.insert_after(hr)
    hr.insert_after(Comment("end yellow box"))
    main_div.insert_after(Comment("END LICENSE AGREEMENT"))

    # Clean up empty paragraphs
    for p in soup.find_all("p"):
        if not p.get_text(strip=True):
            p.decompose()

    # Remove any existing top button
    btn = soup.find("button", id="topBtn")
    if btn:
        btn.decompose()

    # Ensure all links open in new tabs
    for a in soup.find_all("a"):
        a["target"] = "_blank"


def process_appendix(soup: BeautifulSoup) -> None:
    """
    Clean and annotate the appendix section:
      - Unwrap wrappers and remove back-to-top button
      - Strip only back-to-top comments
      - Insert end marker for appendix
    """
    soup.html.unwrap()
    soup.head.decompose()
    soup.body.unwrap()

    # Remove back-to-top button if present
    btn = soup.find("button", id="topBtn")
    if btn:
        btn.decompose()

    # Remove only the back-to-top comments
    for c in soup.find_all(string=lambda t: isinstance(t, Comment)):
        if str(c) in ("back to top button", "end back to top button"):
            c.extract()

    soup.div.insert_after(Comment("END APPENDIX 1"))


def process_snomed(soup: BeautifulSoup) -> None:
    """
    Clean and annotate the SNOMED appendix:
      - Remove DOCTYPE and wrappers
      - Strip all comments
      - Tag and annotate the div, table boundaries
      - Remove empty non-whitelisted tags
    """
    # Remove DOCTYPE
    for node in list(soup.contents):
        if isinstance(node, Doctype):
            node.extract()

    soup.html.unwrap()
    soup.head.decompose()
    soup.body.unwrap()

    # Strip all comments
    for c in soup.find_all(string=lambda t: isinstance(t, Comment)):
        c.extract()

    # Tag main div
    div3 = soup.find("div")
    div3["id"] = "license_appendix2"

    # Annotate SNOMED section boundaries
    br = soup.new_tag("br")
    div3.insert_before(br)
    br.insert_after(Comment("START APPENDIX 2"))
    div3.insert(-1, Comment("end div Appendix 2"))
    div3.insert_after(Comment("END APPENDIX 2"))

    # Remove top button if present
    btn = soup.find("button", id="topBtn")
    if btn:
        btn.decompose()

    # Mark table boundaries
    table = soup.find("table", class_="table table-bordered")
    if table:
        table.insert_before(Comment("Start table for Appendix A"))
        table.insert_after(Comment("End table Appendix A"))

    # Remove empty tags except <br> and <hr>
    whitelist = {"br", "hr"}
    for tag in soup.find_all():
        if not tag.get_text(strip=True) and tag.name not in whitelist:
            tag.decompose()


if __name__ == "__main__":
    soup1 = load_soup(initial_file)
    process_initial(soup1)
    soup2 = load_soup(appendix_file)
    process_appendix(soup2)
    soup3 = load_soup(snomed_file)
    process_snomed(soup3)

    with final_file.open("w", encoding=encoding) as out:
        out.write(soup1.prettify())
        out.write(soup2.prettify())
        out.write(soup3.prettify())
