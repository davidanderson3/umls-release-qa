#!/usr/bin/perl

use HTML::Entities;
use HTML::Entities qw(encode_entities);
use Getopt::Std;
use File::Find;
use POSIX qw( strftime );
use open ":utf8";
#use strict;
use warnings;
getopts("r:i:");
$release = $opt_r || die "please enter the release, ie 2009AA";
$input_dir = $opt_i || die "need input directory";
$current_date = strftime("%Y-%m-%d", localtime);
my ($year,$month,$day) = split /-/,$current_date;
$exp_year = $year + 1;
print "$exp_year";


#construct a hash to create source page titles
%titles = ("AIR" => "AI/RHEUM",
	   "AOD" => "Alcohol and Other Drug Thesaurus",
	   "AOT" => "Authorized Osteopathic Thesaurus",
	   "ALT" => "Alternative Billing Concepts",
	   "BI" => "Beth Israel OMR Clinical Problem List Vocabulary",
	   "CCPSS" => "Canonical Classifications Software",
	   "COSTAR" => "Computer-Stored Ambulatory Records",
	   "CPM" => "Columbia Presbyterian Medical Center Medical Entities Dictionary",
	   "CPT" => "Current Procedural Terminology",
	   "CPTSP" => "Current Procedural Terminology, Spanish Translation",
	   "CSP" => "Computer Retrieval of Information on Scientific Projects",
	   "CST" => "Coding Symbols for Thesaurus of Adverse Reaction Terms (COSTART)",
	   "DDB" => "Diseases Database",
	   "DMDICD10" => "Internationale Klassifikation der Krankheiten",
	   "DMDUMD" => "Die Nomenklatur fuer Medizinprodukte UMDNS",
	   "DSM4" => "Diagnostic and Statistical Manual of Mental Disorders",
	   "DXP" => "DXplain",
	   "FMA" => "Foundational Model of Anatomy",
	   "HHC" => "Home Health Care Classification of Nursing Diagnoses and Interventions",
	   "HL7V30" => "Health Level Seven Vocabulary",
	   "HLREL" => "ICPC2E-ICD10 relationships from Dr. Henk Lamberts",
	   "GO" => "Gene Ontology",
	   "HUGO" => "HUGO",
	   "ICD9CM" => "ICD-9-CM",
	   "ICD10" => "International Statistical Classification of Diseases and Related Health Problems",
	   "ICD10AE" => "International Statistical Classification of Diseases and Related Health Problems (ICD-10): Americanized Version",
	   "ICD10AM" => "International Statistical Classification of Diseases and Related Health Problems, 10th Revision, Australian Modification",
	   "ICD10AMAE" => "International Statistical Classification of Diseases and Related Health Problems, Australian Modification (ICD-10-AM), Americanized English Equivalents",
	   "ICD10DUT" => "ICD-10, Dutch Translation",
	   "ICPC" => "The International Classification of Primary Care", 
	   "ICPC2EDUT" => "International Classification of Primary Care 2E: 2nd ed. electronic. Dutch Translation",
	   "ICPC2EENG" => "International Classification of Primary Care 2E",
	   "ICPC2ICD10DUT" => "ICPC2-ICD10 Thesaurus, Dutch translation",
	   "ICPC2ICD10ENG" => "ICPC2-ICD10 Thesaurus",
	   "ICPC2P" => "International Classification of Primary Care, Version 2-Plus",
	   "ICPCBAQ" => "The International Classification of Primary Care (ICPC), Basque Translation",
	   "ICPCDAN" => "The International Classification of Primary Care (ICPC), Danish Translation",
	   "ICPCDUT" => "The International Classification of Primary Care (ICPC), Dutch Translation",
	   "ICPCFIN" => "The International Classification of Primary Care (ICPC), Finnish Translation",
	   "ICPCFRE" => "The International Classification of Primary Care (ICPC), French Translation",
	   "ICPCGER" => "The International Classification of Primary Care (ICPC), German Translation",
	   "ICPCHEB" => "The International Classification of Primary Care (ICPC), Hebrew Translation",
	   "ICPCHUN" => "The International Classification of Primary Care (ICPC), Hungarian Translation",
	   "ICPCITA" => "The International Classification of Primary Care (ICPC), Italian Translation",
	   "ICPCNOR" => "The International Classification of Primary Care (ICPC), Norwegian Translation",
	   "ICPCPOR" => "The International Classification of Primary Care (ICPC), Portuguese Translation",
	   "ICPCSPA" => "The International Classification of Primary Care (ICPC), Spanish Translation",
	   "ICPCSWE" => "The International Classification of Primary Care (ICPC), SwedishTranslation",
	   "JABL" => " Online Congenital Multiple Anomaly/Mental Retardation Syndromes",
 	   "GS" => "Gold Standard Alchemy",
	   "HCDT" => "HCPCS Version of Current Dental Terminology",
	   "HCPCS" => "Healthcare Common Procedure Coding System",
	   "ICD10PCS" => "ICD-10-PCS",
	   "ICD10CM"=> "International Classification of Diseases, 10th Edition, Clinical Modification",
	   "ICF-CY" => "International Classification of Functioning, Disability and Health for Children and Youth (ICF-CY)",
	   "ICF" => "International Classification of Functioning, Disability and Health (ICF)",
	   "ICNP" => "International Classification for Nursing Practice (ICNP)",
	   "KCD5" => "Korean Standard Classification of Disease Version 5",
	   "LCH" => "Library of Congress Subject Headings",
	   "LNC" => "LOINC",
	   "LNC_CAM" => "Confusion Assessment Method (CAM)",
	   "LNC_BRADEN" => "Braden Scale",
	   "LNC_FLACC" => "FLACC Scale",
	   "LNC_MDS20" => "Minimum Data Set, 2.0",
	   "LNC_MDS30" => "Minimum Data Set, 3.0",
	   "LNC_OASIS" => "Outcome and Assessment Information Set",
	   "LNC_PHQ_9" => "Patient Health Questionnaire",
	   "LNC_WHO" => "Patient Monitoring Guidelines",
	   "LNC_RHO" => "Routine Health Outcomes",
	   "MBD" => "MEDLINE Backfiles",
	   "MCM" => "Glossary of Methodologic Terms for Clinical Epidemiologic Studies of Human Disorders",
	   "MDDB" => "Master Drug Data Base",
	   "MDR" => "MedDRA",
	   "MED"=>"MEDLINE Current Files",
	   "MDRCZE" => "MedDRA Czech",
	   "MDRDUT" => "MedDRA Dutch",
	   "MDRFRE" => "MedDRA French",
	   "MDRGER" => "MedDRA German",
	   "MDRITA" => "MedDRA Italian",
	   "MDRJPN" => "MedDRA Japanese",
	   "MDRPOR" => "MedDRA Portuguese",
	   "MDRSPA" => "MedDRA Spanish",
	   "MEDCIN" => "MEDCIN",
	   "MMSL" => "Multum",
	   "MMX" => "Micromedex",
	   "MSH" => "MeSH",
	   "MSHCZE" => "MeSH Czech",
	   "MSHDUT" => "MeSH Dutch",
	   "MSHFIN" => "MeSH Finnish",
	   "MSHFRE" => "MeSH French",
	   "MSHGER" => "MeSH German",
	   "MSHITA" => "MeSH Italian",
	   "MSHJPN" => "MeSH Japanese",
	   "MSHLAV" => "MeSH Latvian",
	   "MSHPOR" => "MeSH Portuguese",
	   "MSHRUS" => "MeSH Russian",
	   "MSHSCR" => "MeSH Croatian",
	   "MSHSPA" => "MeSH Spanish",
	   "MSHRUS" => "MeSH Russian",
	   "MSHSWE" => "MeSH Swedish",
	   "MTH" => "UMLS Metathesaurus",
	   "MTHHL7V25" => "HL7 Vocabulary Version 2.5, 7-bit equivalents created by the National Library of Medicine",
	   "MTHICPC2EAE" => "International Classification of Primary Care 2nd Edition, Electronic, 2E, American English Equivalents",
	   "MTHICPC2ICD10AE" => "International Classification of Primary Care 2nd Edition, American English Equivalents",
	   "MTHICPC2ICD107B" => "International Classification of Primary Care 2nd Edition, 7-bit Equivalents",
	   "MTHCH" => "Metathesaurus CPT Hierarchical Terms",
	   "MTHFDA" => "FDA National Drug Code Directory",
	   "MTHHH" => "Metathesaurus HCPCS Hierarchical Terms",
	   "MTHICD9"=> "ICD-9-CM Entry Terms",
	   "MTHMST" => "Metathesaurus Version of Minimal Standard Terminology Digestive Endoscopy",
	   "MTHMSTFRE" => "Metathesaurus Version of Minimal Standard Terminology Digestive Endoscopy, French Translation",
	   "MTHMSTITA" => "Metathesaurus Version of Minimal Standard Terminology Digestive Endoscopy, Italian Translation",
	   "MTHSPL" => "FDA Structured Product Labels",
	   "NAN" => "NANDA nursing diagnoses",
	   "NCBI" => "NCBI Taxonomy",
	   "NCI" => "NCI Thesaurus",
	   "NCISEER" => "NCI Surveillance, Epidemiology, and End Results (SEER) conversions between ICD-9-CM and ICD-10 neoplasm codes",
	   "NDDF" => "National Drug Data File",
	   "NEU" => "Neuronames Brain Hierarchy",
	   "NIC" => "Nursing Interventions Classification (NIC)",
	   "NLM-MED" => "National Library of Medicine (NLM) Medline Data",
	   "NOC" => "Nursing Outcomes Classification",
	   "OMIM" => "Online Mendelian Inheritance in Man",
	   "OMS" => "The Omaha System",
	   "PCDS" => "Patient Data Care Set",
	   "PDQ" => "PDQ",
	   "PNDS" => "Perioperative Nursing Data Set",
	   "PPAC" => "Pharmacy Practice Activity Classification",
	   "PSY" => "Thesaurus of psychological index terms",
	   "QMR" => "Quick Medical Reference",
	   "RAM" => "QMR clinically related terms from Randolf A. Miller",
	   "RCD" => "Clinical Terms Version 3 (CTV3) (Read Codes)",
	   "RCDAE" => "American English Equivalent of the Clinical Terms Version 3",
	   "RCDSA" => "American English Equivalent of Synthesized Terms from the Clinical Terms Version 3",
	   "RCDSY" => "Synthesized Read Terms (without initial bracketed letters) of the Clinical Terms Version 3",
	   "RXNORM" => "RxNorm",
	   "SCTSPA" => "SNOMED CT Spanish",
	   "SNOMEDCT" => "SNOMED CT",
	   "SNM" => "Systematized Nomenclature of Medicine",
	   "SNMI" => "SNOMED International",
	   "SPN" => "Standard Product Nomenclature",
	   "SRC" => "UMLS Metathesaurus Source Terminologies",
	   "TKMT"=> "Traditional Korean Medical Terms",
	   "ULT" => "Ultrasound Structured Attribute Reporting",
	   "UMD" => "UMD",
	   "USPMG" => "United States Pharmacopeia (USP). Medicare Prescription Drug Benefit Model Guidelines",
	   "UWDA" => "University of Washington Digital Anatomist",
	   "VANDF" => "National Drug File",
	   "WHO" => "WHO Adverse Drug Reaction Terminology",
	   "WHOFRE" => "WHO Adverse Drug Reaction Terminology, French Translation",
	   "WHOGER" => "WHO Adverse Drug Reaction Terminology, German Translation",
	   "WHOPOR" => "WHO Adverse Drug Reaction Terminology, Portuguese Translation",
	   "WHOSPA" => "WHO Adverse Drug Reaction Terminology, Spanish Translation",



	   );

#define workarea

$base_dir = "/Users/steveemrick/sourcereleasedocs/";
$work_dir = $base_dir.$input_dir;
$output_dir = $base_dir."dcr";



#look recursively through each dir
find(\&get_source_dirs, $work_dir);


sub get_source_dirs {
chomp($_);
my $file = $_;
if (-d $file) {
$rsab = $file;
mkdir $output_dir."/".$rsab, 0755 || die "$!";
$rsaboutput_dir = $output_dir."/".$rsab;
#this is actually the rsab which is also the directory name, ie MSH
#obtain page title from hash
$source_info = $titles{$rsab};


}

#only deal with the html files, as opposed to xml
if ($file =~ /\.html/) {
	
	my ($filename,$extension) = split /\./,$file;	
	
	open FH, "<$file" || die "Could not open $file for reading $!";
	#create array to hold encoded dcr content
	print "outputting $filename for $rsab\n";
	my @encoded_contents;
	while (<FH>) {
	my $encoded_content = encode_entities($_);
	##add each line to the overall content
	push @encoded_contents,$encoded_content;
	}
      
	close FH;
	
	open OUT, ">$rsaboutput_dir/$filename\.dcr" || die "could not open output file $!\n";	
        print OUT qq{<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE record SYSTEM "dcr4.5.dtd">
<record name="termtypes.dcr" type="content">
<item name="title"><value>$release $source_info Source Information</value></item>
<item name="heading"><value>$release $source_info Source Information</value></item>
<item name="title2heading"><value>T</value></item>
<item name="permanence">
  <value>
   <item name="date_issued"><value>$current_date</value></item>
   <item name="button_date_issued"><value>$current_date</value></item>
   <item name="no_button_date_issued"><value>$current_date</value></item>
   <item name="date_modified"><value>$current_date</value></item>
   <item name="date_expires"><value>$exp_year-$month-$day</value></item>
   <item name="date_reviewed"><value>$current_date</value></item>
   <item name="contact_email"><value>nlmumlscustserv\@mail.nlm.nih.gov</value></item>
   <item name="publisher"><value>U.S. National Library of Medicine</value></item>
   <item name="rights"><value>Public Domain</value></item>
   <item name="type"><value>Statistics and Reports</value></item>
   <item name="level"><value>Permanence Not Guaranteed</value></item>
   <item name="guarantor"><value>U.S. National Library of Medicine</value></item>
   <item name="subjectNtype"><value><item name="subject"><value/></item>
   <item name="subjectType"><value>Keyword</value></item></value></item>
   <item name="languages"><value><item name="language"><value>eng</value></item>
  </value>
</item>
<item name="previousversion"><value/></item>
</value></item>
<item name="header">
<value>
  <item name="basetag"/>
  <item name="metadata"/>
<item name="head"><value><item name="content"><value>&lt;link rel="stylesheet" href="../../tabs.css" type="text/css" /&gt;
&lt;link rel="stylesheet" href="../../sourcereleasedocs.css" type="text/css" /&gt;
&lt;script type = "text/javascript" language = "JavaScript" src="../../js/prototype.js"&gt;&lt;/script&gt;
&lt;script type = "text/javascript" language = "JavaScript" src="../../js/scriptaculous.js"&gt;&lt;/script&gt;
&lt;script type = "text/javascript" language = "JavaScript" src="../../js/boxes.js"&gt;&lt;/script&gt;
</value></item>
</value></item>
</value></item>

<item name="pageheader"><value><item name="divsubpage"><value><item name="dcrLocation"><value>/htdocs/research/umls/header.html</value></item>
<item name="images"/>
<item name="breadcrumbs"><value><item name="crumb"><value><item name="name"><value>UMLS Source Release Documentation</value></item>
<item name="url"><value>../../index.html</value></item>
<item name="title"><value>UMLS Source Release Documentation index page</value></item>
</value></item>
</value></item>
</value></item>
</value></item>
<item name="fixedwidth"/>
<item name="printversion"/>
<item name="pagecontent">
<value><item name="HTMLOnly">
<value><item name="content">
<value>
@encoded_contents
</value></item>
</value></item>
</value></item><item name="secondaryContent"/></record>};
	
	
	
}

close OUT;	
	
}



