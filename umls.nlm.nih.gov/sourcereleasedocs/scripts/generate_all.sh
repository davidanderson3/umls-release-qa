#!/bin/sh
# run from the base directory (.../sourcereleasedocs/)

python scripts/process_mrsab.py

bash scripts/semicolon.sh

bash scripts/replaceSourceTitle.sh

bash scripts/replaceMenu.sh

bash scripts/replaceUpdatedDate.sh
