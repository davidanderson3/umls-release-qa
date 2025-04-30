from pathlib import Path  # Provides object-oriented filesystem path handling
from bs4 import BeautifulSoup, Comment, Doctype  # HTML parsing, comment and doctype handling

# ----------------------------------------------------------------------------
# script2.py
# Purpose: Merge and annotate three license HTML sections into a single document.
# Steps:
#   1. Load three separate HTML files from 'Begin_with_this_file/script2'
#   2. Clean, style, and annotate each section
#   3. Combine and output the final LicenseAgreement.html to 'Begin_with_this_file/script3'
# ----------------------------------------------------------------------------

# Configuration
encoding = "utf-8"  # Consistent text encoding
script_dir = Path(__file__).resolve().parent  # Path to 'license_scripts' directory
source_dir = script_dir / "Begin_with_this_file" / "script2"  # Source HTMLs
output_dir = script_dir / "Begin_with_this_file" / "script3"  # Destination for merged output
output_dir.mkdir(parents=True, exist_ok=True)  # Ensure the directory exists

# File paths for inputs and final output
initial_file = source_dir / "initial_license_agreement.html"
appendix_file = source_dir / "license_agreement_appendix.html"
snomed_file = source_dir / "license_agreement_snomed.html"
final_file = output_dir / "LicenseAgreement.html"


def load_soup(path: Path, parser: str = "html.parser") -> BeautifulSoup:
    """
    Load an HTML file and return a BeautifulSoup object.
    Raises FileNotFoundError if the file does not exist.
    """
    with path.open(encoding=encoding) as f:
        return BeautifulSoup(f, parser)

# ----------------------------------------------------------------------------

def process_initial(soup: BeautifulSoup) -> None:
    """
    Clean and annotate the initial license section:
      - Unwrap <html>, <head>, <body> for direct manipulation
      - Apply consistent table styling
      - Remove existing comments
      - Insert start marker and stable div ID
      - Create a yellow info box with links
      - Add an <hr> and end marker
      - Strip empty <p> tags and optional top-button
      - Enforce target=_blank on all links
    """
    soup.html.unwrap()
    soup.head.unwrap()
    soup.body.unwrap()

    style_el = soup.style
    style_el.string = (
        "table, th, td { border:1px solid black; border-collapse:collapse; }"
        "th, td { padding:5px; }"
    )

    for comment in soup.find_all(string=lambda t: isinstance(t, Comment)):
        comment.extract()

    soup.div.insert_before(Comment("START LICENSE AGREEMENT"))
    main_div = soup.find("div")
    main_div["id"] = "license_agreement"

    info = soup.new_tag(
        "p",
        style="border:thin solid; border-color:#fccd4e; padding:5px"
    )
    info.string = (
        "Copy of the UMLS Metathesaurus license (2025AA Release). View current license at: "
    )
    link1 = soup.new_tag(
        "a", href="https://uts.nlm.nih.gov/uts/assets/LicenseAgreement.pdf", target="_blank"
    )
    link1.string = link1["href"]
    info.append(link1)
    info.append(" | Sign up at: ")
    link2 = soup.new_tag(
        "a", href="https://uts.nlm.nih.gov/uts/signup-login", target="_blank"
    )
    link2.string = link2["href"]
    info.append(link2)
    main_div.insert(0, info)

    hr = soup.new_tag("hr")
    info.insert_after(hr)
    hr.insert_after(Comment("end yellow box"))
    main_div.insert_after(Comment("END LICENSE AGREEMENT"))

    for p in soup.find_all("p"):
        if not p.get_text(strip=True):
            p.decompose()

    btn = soup.find("button", id="topBtn")
    if btn:
        btn.decompose()

    for a in soup.find_all("a"):
        a["target"] = "_blank"

# ----------------------------------------------------------------------------

def process_appendix(soup: BeautifulSoup) -> None:
    """
    Clean and annotate the appendix license section:
      - Unwrap wrappers and remove top-button
      - Strip only back-to-top comments
      - Insert end marker for appendix
    """
    soup.html.unwrap()
    soup.head.decompose()
    soup.body.unwrap()

    btn = soup.find("button", id="topBtn")
    if btn:
        btn.decompose()

    for comment in soup.find_all(string=lambda t: isinstance(t, Comment)):
        if str(comment) in ("back to top button", "end back to top button"):
            comment.extract()

    soup.div.insert_after(Comment("END APPENDIX 1"))

# ----------------------------------------------------------------------------

def process_snomed(soup: BeautifulSoup) -> None:
    """
    Clean and annotate the SNOMED license section:
      - Remove DOCTYPE and unwrap wrappers
      - Strip all comments
      - Tag main div and annotate section
      - Mark table boundaries
      - Remove empty tags except <br> and <hr>
    """
    for node in list(soup.contents):
        if isinstance(node, Doctype):
            node.extract()

    soup.html.unwrap()
    soup.head.decompose()
    soup.body.unwrap()

    for comment in soup.find_all(string=lambda t: isinstance(t, Comment)):
        comment.extract()

    div3 = soup.find("div")
    div3["id"] = "license_appendix2"
    br = soup.new_tag("br")
    div3.insert_before(br)
    br.insert_after(Comment("START APPENDIX 2"))
    div3.insert(-1, Comment("end div Appendix 2"))
    div3.insert_after(Comment("END APPENDIX 2"))

    btn = soup.find("button", id="topBtn")
    if btn:
        btn.decompose()

    table = soup.find("table", class_="table table-bordered")
    if table:
        table.insert_before(Comment("Start table for Appendix A"))
        table.insert_after(Comment("End table Appendix A"))

    whitelist = {"br", "hr"}
    for tag in soup.find_all():
        if not tag.get_text(strip=True) and tag.name not in whitelist:
            tag.decompose()

# ----------------------------------------------------------------------------
# Main execution: load, process, merge, write output
# ----------------------------------------------------------------------------
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
