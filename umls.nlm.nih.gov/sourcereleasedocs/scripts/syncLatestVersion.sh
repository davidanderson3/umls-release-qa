#!/bin/bash
# This script merges files from the version specified into the /jbake/content/ folder. 
# Before running this, make sure the files in the versioned folder are finalized.
# run from the base directory (.../sourcereleasedocs/)
VERSION='2020AB'
rsync -avh --progress "$VERSION"/ jbake/content/
