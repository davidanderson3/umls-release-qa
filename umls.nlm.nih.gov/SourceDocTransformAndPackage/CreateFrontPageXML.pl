#!/opt/local/bin/perl
use strict;
use warnings;
use diagnostics;
use Getopt::Std;
use File::Find;
use XML::Writer;
use IO::File;
use Env;
use Fcntl;
use List::MoreUtils qw(uniq);
use open ":utf8";

#my $base = "$ENV{'USERPROFILE'}/sourcereleasedocs";
my $base = "$ENV{'HOME'}/sourcereleasedocs";
getopts("v:");
our($opt_v);
my $version = $opt_v || die "please enter a UMLS version, e.g. 2013AA";

my %languages = ();
my %srls = ();
my %letters = ();

my $input = $base."/".$version."/allsources.txt";
open FH, $input || die "could not open input $!";

my $output = new IO::File(">".$base."/".$version."/allsources.xml") || die "could not open output file due to $!\n";
my $writer = new XML::Writer(OUTPUT => $output,DATA_MODE => 'true',DATA_INDENT => 4);
$writer->xmlDecl("utf-8");
$writer->startTag("document","release"=>"current");


while(<FH>){
	
	chomp($_);
	my @fields = split(/\|/,$_);
	my($rsab,$ssn,$imeta,$language,$srl,$current) = split(/\|/,$_);
	my $letter = substr($rsab,0,1);

	
	## add rows to each type of hash - language, restriction, letter
	
	if($current == '0'){$languages{$language} .= $_};
	if($current == '0'){$srls{$srl} .= $_};
	if($current == '0'){$letters{$letter} .= $_;}
    	
}


foreach my $letter (sort(keys(%letters))) {
	
	print qq{$letter\n};
	my @records = split(/\n/,$letters{$letter});
	print qq{$letters{$letter}\n};
	
}

&processLetters(%letters);
&processLanguages(%languages);
&processRestrictions(%srls);


sub processLetters {
	
	$writer->startTag('letters');
	
	foreach my $letter(sort(keys(%letters))){
	my @records = split(/\*/,$letters{$letter});	
		
	$writer->startTag('letter','name'=>$letter);
	$writer->startTag('sources');
	foreach my $record(@records) {
	my($rsab,$ssn,$imeta,$srl,$current) = split(/\|/,$record);
	
	
	$writer->startTag('source','ssn'=>$ssn,'imeta'=>$imeta);
	$writer->characters($rsab);
	$writer->endTag();
	
	
	}
	$writer->endTag(); ##</sources>
	$writer->endTag(); ## </letter>
    
	} ##/end foreach
    
	$writer->endTag(); ## </letters>
} ##// end procesLanguages

sub processRestrictions {
	
	$writer->startTag('restrictions');
	
	foreach my $srl(sort(keys(%srls))){
	my @records = split(/\*/,$srls{$srl});	
		
	$writer->startTag('restriction','name'=>$srl);
	$writer->startTag('sources');
	foreach my $record(@records) {
	my($rsab,$ssn,$imeta,$srl) = split(/\|/,$record);
	$writer->startTag('source','ssn'=>$ssn,'imeta'=>$imeta);
	$writer->characters($rsab);
	$writer->endTag();
	}
	$writer->endTag(); ##</sources>
	$writer->endTag(); ## </letter>
    
	} ##/end foreach
    
	$writer->endTag(); ## </restrictions>
} ##// end procesLanguages


sub processLanguages {
	
	$writer->startTag('languages');
	
	foreach my $language(sort(keys(%languages))){
	my @records = split(/\*/,$languages{$language});	
		
	$writer->startTag('language','name'=>$language);
	$writer->startTag('sources');
	foreach my $record(@records) {
	my($rsab,$ssn,$imeta,$srl) = split(/\|/,$record);
	$writer->startTag('source','ssn'=>$ssn,'imeta'=>$imeta);
	$writer->characters($rsab);
	$writer->endTag();
	}
	$writer->endTag(); ##</sources>
	$writer->endTag(); ## </language>
    
	} ##/end foreach
    
	$writer->endTag(); ## </languages>
} ##// end procesLanguages






$writer->endTag(); ##//</document>


