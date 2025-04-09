from datetime import datetime, timedelta, date
import json
from pathlib import Path
from jinja2 import Environment, FileSystemLoader
from calc_release_date import calculate_release_date

import sys
sys.path.append("C:\Users\rewolinskija\Documents\umls-source-release-1\umls.nlm.nih.gov\releasedocs\scripts\Misc scripts")
from get_loinc_efg import scrape_website


# Example release version
release_version = '2025AA'

# Set the paths for the template directory and output directory

template_dir = Path("C:\Users\rewolinskija\Documents\umls-source-release-1\umls.nlm.nih.gov\releasedocs\templates")
output_dir = Path("C:\Users\rewolinskija\Documents\umls-source-release-1\umls.nlm.nih.gov\releasedocs\output")

# Create the template environment
template_env = Environment(loader=FileSystemLoader(template_dir))


# ============================================
# Data preparation

# Call the calculate_release_date function to get the release info dictionary and JSON data
release_info, json_data = calculate_release_date(release_version)

# Call the scrape_website function to get additional data
loinc_efg_data = scrape_website()

# ============================================
# Template rendering

# Define the templates and their contexts
templates = {
    "template_rss.txt": {
        "release_info": release_info,
        "output_file": output_dir / "rss_output.txt"
    },
    "template_copyright_notice.txt": {
        "release_info": release_info,
        "loinc_efg_data": loinc_efg_data,
        "output_file": output_dir / "copyright_notice_output.txt"
    },
}

# ============================================

# Render the templates with the release information and additional data
for template_name, context in templates.items():
    template = template_env.get_template(template_name)
    rendered_template = template.render(**context)

    # Save the rendered template to the desired output file using a context manager
    output_file = output_dir / f"{template_name}_output.txt"
    with open(output_file, "w", encoding='utf-8') as file:
        file.write(rendered_template)
