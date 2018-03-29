#!/bin/bash
# This script merges files from the version specified into the /jbake/content/ folder. 
# Before running this, make sure the files in the versioned folder are finalized.
BASEDIR='/Users/andersondm2/umlsdoc/umls.nlm.nih.gov/sourcereleasedocs/'
VERSION='2018AA'
rsync -avh --progress "$BASEDIR""$VERSION"/ "$BASEDIR"/jbake/content/
