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


my $base = "$ENV{'USERPROFILE'}/sourcereleasedocs";
#my $base = "$ENV{'HOME'}/sourcereleasedocs";
getopts("v:");
our($opt_v);
my $version = $opt_v || die "please enter a UMLS version, e.g. 2013AA";
my $path = join ("/", $base,$version);
chomp($path);
#chdir ($path) || die "could not change directory $!\n";

find(\&open_rsab_dir, $path);

sub open_rsab_dir{
	
	if (-d $_ && $_ =~ /^[A-Z]/) {find(\&read_stats_file,$_)};
	#if (-d $_ && $_ eq "MEDLINEPLUS") {find(\&read_stats_file,$_)};
	
}

sub read_stats_file{
	
	if (-f $_ && $_ eq "stats.txt") {
	my $file = $_;
	&parse_file($file);	
		
	};
}

sub parse_file{
	
	##open the stats.txt file and create xml.
	
	my $file = shift;
	#my $rsab = shift;
	open STATS,$file || die "could not open stats.txt file$!\n";  
	my $output = IO::File->new(">stats.xml");
	binmode($output);
	my $writer = XML::Writer->new(OUTPUT => $output, DATA_MODE => 'true', DATA_INDENT => 4, ENCODING => 'utf-8');
	$writer->xmlDecl('utf-8');
	$writer->startTag('document'); #<document>
	
	
	my $section;
	my @headers;
	
	
	while(<STATS>){
		chomp;
		my %data;
		chomp($_);
        if (/^\*+/) {
        
        @headers = split(/\|/,$_);
        $section = shift(@headers);
        my $name = substr($section,1);
        $writer->startTag('section','name'=>substr($section,1)); #<section>
        $writer->startTag('row','header'=>'y');#<row>
        
        foreach my $header(@headers){
        	
        	$writer->startTag('field');#<field>
        	$writer->characters($header);
        	$writer->endTag();#</field>
        	
        } #end foreach
        
        $writer->endTag();#</row>
        
        } #endif
        
        elsif (/^\!+/) {
        undef $section;
        $writer->endTag();#</section>
        
        
        } #end elsif
        
        
        elsif (defined $section && $_ ne "None") {
        
        my @fields = split(/\|/,$_);
        $writer->startTag('row'); #<row>
        
        foreach my $field(@fields) {
        $writer->startTag('field'); #<field>
        $writer->characters($field);
        $writer->endTag();	#</field>
        
        }
        
        $writer->endTag();#</row>
        
        } #end elsif
        
	} #end while
	
	$writer->endTag(); #</document>
} #end parse_file


