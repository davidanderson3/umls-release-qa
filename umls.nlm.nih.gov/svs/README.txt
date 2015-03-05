Documentation for vsac-svs-api.pl

Before running the client, in your $HOME directory, create a directory called 'svs'.
This is the directory where your output will write.

The client is available by checking out the umlsdoc project from git, or by logging into mlbapp01.nlm.nih.gov and going to /umls_dev/mms/git/umlsdoc/umls.nlm.nih.gov/svs
Be sure you are in the 'develop' branch in git.  mlbapp01 is already configured like this, so please don't switch branches.

To run from mlbapp01:
  1) cd /umls_dev/mms/git/umlsdoc/umls.nlm.nih.gov/svs
  2) perl vsac-svs-api.pl -u username -p password (use your UMLS credentials for username/password)
  3) select your options accordingly.   While the client does provide parameter validation, it doesn't force you to choose the 'right' parameters, so it
  assumes some familiarity with the API.
  
Update history:

##Version 0.1
# Initial Commit

##Version 0.2
##Updates
# Add ability to retrieve measure and QDM usage (via value-set-measure-counts.xsl)
# Add ability to ouptut pure xml (use the 'SVS XML' report option)

##Usage Notes
# create a directory in your $HOME called 'svs'
# to start the client: perl vsac-svs-api.pl -u username -p password
# output will write to your $HOME/svs directory to the filename you specifiy
# Only use mode RetrieveMultipleValueSets for now
# Only use Batch OID or Single Use.  Measure Mode is not yet implemented.



