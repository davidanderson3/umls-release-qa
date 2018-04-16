#!/bin/sh
# run from the base directory (.../sourcereleasedocs/)

bash scripts/syncLatestVersion.sh

python scripts/process_mrsab.py

bash scripts/semicolon.sh

bash scripts/replaceSourceTitle.sh

bash scripts/replaceMenu.sh

bash scripts/replaceUpdatedDate.sh

bash scripts/create_qa_page.sh

bash scripts/create_rsab_page.sh
