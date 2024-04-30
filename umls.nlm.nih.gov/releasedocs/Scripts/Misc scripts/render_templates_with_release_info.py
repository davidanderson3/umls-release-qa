

from datetime import datetime
from pathlib import Path
from jinja2 import Environment, FileSystemLoader
from calc_release_date import calculate_release_date



# Example release version
release_version = '2024AA'

# ============================================

# Set the paths for the template directory and output directory
template_dir = Path("L:\SHARE\Rewolinski\Automating release files\Learning python\Scripts templates and output\Templates")   # Path to the directory containing the templates
output_dir = Path("L:\SHARE\Rewolinski\Automating release files\Learning python\Scripts templates and output\Output")  # Path to the directory where the output files will be saved


# Create the template environment
template_env = Environment(loader=FileSystemLoader(str(template_dir)))


# ============================================
# Data preparation

# Call the calculate_release_date function to get the release info dictionary and JSON data
release_info, json_data = calculate_release_date(release_version)


# ============================================
# Template rendering

# Define the templates and their corresponding output file names
templates = {
    'template_rss.txt': 'rss_output1.txt',
    'template_copyright_notice.txt': 'copyright_notice_output1.txt',
}

# Render the templates with the release information and additional data
for template_name, output_file_name in templates.items():
    template = template_env.get_template(template_name)
    rendered_template = template.render(release_info=release_info)

    # Save the rendered template to the desired output file
    output_file = output_dir / output_file_name
    with open(output_file, 'w', encoding='utf-8') as file:
        file.write(rendered_template)