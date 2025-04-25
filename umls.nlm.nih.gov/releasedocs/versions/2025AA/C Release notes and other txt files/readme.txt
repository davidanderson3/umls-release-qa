May 2025

README

UMLS 2025AA Release

The 2025AA release of the Unified Medical Language System(R) (UMLS) Knowledge Sources is available for download as of May 5, 2025. Access it on the UMLS Download page at https://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html.

To access the UMLS Release files, you must have an active UMLS Metathesaurus(R) License and a valid UTS account (https://uts.nlm.nih.gov/uts/).  Upon download, you will be prompted to authenticate with an identity provider with the UTS.

Available downloads:
-	UMLS Metathesaurus Precomputed Subsets: Requires no installation, see below for more information.
-	MRCONSO.RRF file: The most widely used Metathesaurus file.
-	Full Release: UMLS Metathesaurus, Semantic Network, Specialist Lexicon and Lexical Tools, database load scripts, and  MetamorphoSys for customizing your UMLS subset and browsing the data.
-	UMLS Metathesaurus History Files: Contains historical data from the UMLS Metathesaurus from 2004AA to present.


UMLS Metathesaurus Precomputed Subsets
--------------------------------
In past years, we have required MetamorphoSys to install and customize the UMLS Metathesaurus. We are now providing ready-to-use precomputed subsets of the UMLS Metathesaurus that require no installation or customization. As of 2024AB, precomputed subsets include obsolete and suppressible content.
-	UMLS Metathesaurus Full Subset: Complete Metathesaurus data
-	UMLS Metathesaurus Level 0 Subset: Includes only level 0 source vocabularies. Level 0 vocabularies do not have any additional restrictions beyond the standard license terms.


UMLS Metathesaurus History Files
--------------------------------
The following files contain historical data from the UMLS Metathesaurus starting with the 2004AA release:
-	Concept History (MRCONSO_HISTORY.txt) - This file contains all atoms, concepts, and codes dropped from the UMLS Metathesaurus MRCONSO.RRF.
-	Relation History (MRREL_HISTORY.txt) - This file contains all relations dropped from the UMLS Metathesaurus MRREL.RRF file.
-	Source Vocabulary History (MRSAB_HISTORY.txt) - This file includes a row for every version of every UMLS source vocabulary updated in the UMLS Metathesaurus MRSAB.RRF file.
If you find these files useful, please provide feedback to the NLM Help Desk (https://support.nlm.nih.gov/support/create-case/) with the subject line: "UMLS History Files".


Metathesaurus
--------------------------------
The 2025AA Metathesaurus contains approximately 3.45 million concepts and 17.1 million unique concept names from 190 source vocabularies.

One new translation:
-	MDRISL (Icelandic Edition of the Medical Dictionary for Regulatory Activities Terminology (MedDRA).

33 English sources and 48 translation sources were updated.  These include MeSH, LOINC, RxNorm, and SNOMED CT.  For detailed information on changes in this version of the Metathesaurus, see the Updated Sources (Expanded) section.  Additional release statistics may be found in the Statistics section.

SPECIALIST Lexicon and Lexical Tools
-	The release includes the updated SPECIALIST Lexicon (2025 Release).
-	The release includes the updated Lexical Tools (2025 Release) which integrate data from the SPECIALIST Lexicon, 2025 Release. The Lexical Tools include the Full and Lite versions of lvg.2025.
The Metathesaurus index files were processed using the updated lvg files.


MetamorphoSys
--------------------------------
The full release requires about 35 GB of disk space. MetamorphoSys can generate custom load scripts for MySQL, Oracle, or Microsoft Access when creating a Metathesaurus subset or installing the Semantic Network. Instructions are available on the UMLS Load Scripts homepage.


UMLS Learning Resources
--------------------------------
2025AA Source Release Documentation Web pages are available: https://www.nlm.nih.gov/research/umls/sourcereleasedocs/.
Additional information about the UMLS is available on the UMLS homepage (https://www.nlm.nih.gov/research/umls/index.html). New users are encouraged to explore the UMLS FAQ and other training materials.


UMLS Terminology Services (UTS)
--------------------------------
The UMLS Metathesaurus Browser and the UMLS API have been updated with the most recent release.

To authenticate with the UMLS API, append an apiKey parameter to your request, for example:
https://uts-ws.nlm.nih.gov/rest/content/current/CUI/C0155502/atoms?apiKey={{YOUR_API_KEY}}


UMLS User Contributions
--------------------------------
UMLS users have extended the functionality of the UMLS by developing APIs, automation scripts, and natural language processing tools. You can find a list of these on the UMLS Community web page: https://www.nlm.nih.gov/research/umls/implementation_resources/community/index.html.

Want to add your tool? Send a request to the NLM Help Desk (https://support.nlm.nih.gov/support/create-case/) with the subject line: "UMLS Community". Be sure to include a link to your source code so that other UMLS users can adapt your tool. We are especially interested in:
-	Database load scripts
-	Transformation scripts that convert UMLS data into other formats (for example, RDF or JSON)
-	Scripts that automate any aspect of UMLS installation
-	Applications that leverage UMLS in text processing

We value your feedback! For more information about improvements made to the UMLS based on user feedback, as well as information about UMLS usage, see our UMLS User Feedback Page: https://www.nlm.nih.gov/research/umls/implementation_resources/community/user_feedback.html


--------------------------------
For information on the Unified Medical Language System (UMLS), consult the UMLS homepage at: https://www.nlm.nih.gov/research/umls.

The latest Release Notes list all known issues, including bugs and fixes, and are available at: https://www.nlm.nih.gov/research/umls/knowledge_sources/metathesaurus/release/notes.html.

UMLS data files and MetamorphoSys are available by download from the UMLS Web site at: https://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html. Users must have an active UMLS Terminology Services (UTS) account to download the files. All files must be downloaded into the same directory.  MetamorphoSys must be unzipped before it can be used.


HARDWARE AND SOFTWARE REQUIREMENTS
------------------------------------------------------------------
Supported operating systems:
Windows
Linux
macOS

Hardware Requirements
  - A MINIMUM 40 GB of free hard disk space.  
  - A MINIMUM of 2 GB of RAM, preferably more. Smaller memory size will cause virtual memory paging with exponentially increased processing time.
  - A CPU speed of at least 2 GHz for reasonable installation times.
 
