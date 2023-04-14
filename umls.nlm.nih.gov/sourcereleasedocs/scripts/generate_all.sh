#!/bin/sh
# run from the base directory (.../sourcereleasedocs/)
# dependencies - bash, python(3?), perl, saxon

# before running this, update MRSAB.RRF and make sure variables within scripts are up to date. 

# syncs the contents of a versioned folder (IE 2018AA) with the production folder (jbake/content/)

bash scripts/syncLatestVersion.sh

# makes a derivative of MRSAB.RRF and then makes metadata.html files for each vocabulary

python3 scripts/process_mrsab.py

# removes semicolons from metadata.html files

bash scripts/semicolon.sh

# replaces the source titles using current RSAB and SSN from MRSAB

bash scripts/replaceSourceTitle.sh

# replaces the menus based on the current set of files within each folder

bash scripts/replaceMenu.sh

# replaces the updated date to reflect the next release date

bash scripts/replaceUpdatedDate.sh

# replaces the date for stats.html pages to reflect the next release date 

bash scripts/replaceDate.sh

# creates a qa page for QAing the website before deploying

bash scripts/create_qa_page.sh

# creates a list of RSABs that is published in the root directory for users' reference

bash scripts/generateHome.sh

#makes the table for the homepage (https://www.nlm.nih.gov/research/umls/sourcereleasedocs/index.html) which can be pasted into Teamsite.

