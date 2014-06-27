#!/opt/local/bin/perl
use strict;
use warnings;
use diagnostics;
use Getopt::Std;
use File::Find;
use XML::Writer;
use IO::File;
use Env;
use open ":utf8";


my $base;
my $windows=($^O=~/Win/)?1:0;  ##check if we are on Windows
if(! $windows){$base = "$ENV{'HOME'}/sourcereleasedocs";} else{$base = "$ENV{'USERPROFILE'}/sourcereleasedocs";}

getopts("v:");
our($opt_v);
my $version = $opt_v || die "please enter a UMLS version, e.g. 2013AA";
my $path = join ("/", $base,$version);
chomp($path);
#chdir ($path) || die "could not change directory $!\n";

find(\&open_rsab_dir, $path);

sub open_rsab_dir{
	
	if (-d $_ && $_ =~ /^[A-Z]/) {find(\&read_metadata_file,$_)};
	#if (-d $_ && $_ eq "MEDLINEPLUS") {find(\&read_stats_file,$_)};
	
}

sub read_metadata_file{
	
	if (-f $_ && $_ eq "metadata.txt") {
	my $file = $_;
	&parse_file($file);	
		
	};
}

sub parse_file{
	
	##open the stats.txt file and create xml.
	
	my $file = shift;
	#my $rsab = shift;
	open METADATA,$file || die "could not open stats.txt file$!\n";  
	my $output = IO::File->new(">metadata.xml");
	binmode($output);
	my $writer = XML::Writer->new(OUTPUT => $output, DATA_MODE => 'true', DATA_INDENT => 4, ENCODING => 'utf-8');
	$writer->xmlDecl('utf-8');
	$writer->startTag('document'); #<document>
	$writer->startTag('section','name'=>'Source Metadata');
	
	my $section;
	my @headers;
	
	
	while(<METADATA>){
		chomp($_);
		$writer->startTag('row','header'=>'y');
		$writer->startTag('field');
		$writer->characters('Field');
		$writer->endTag();
		$writer->startTag('field');
		$writer->characters('Value');
		$writer->endTag();
		$writer->endTag();
		my @fields = split(/\|/,$_);
		$writer->startTag('row');
		foreach my $field (@fields){
			
			my @NamesValues = split(/\^/,$field);
			my $name = $NamesValues[0];
			my $value = $NamesValues[1];
			$writer->startTag('field','name'=>$name);
			
			#if ($value =~ /http\:\/\//)  {$value = "somethingelse";}
			
			$writer->characters($value);
			$writer->endTag();
		}
		$writer->endTag();
		$writer->endTag();
		
	}$writer->endTag();
} #end parse_file


