#!/opt/local/bin/perl
use strict;
use warnings;
use diagnostics;
use Getopt::Std;
use File::Find;
use HTML::Entities;
use HTML::Entities qw(encode_entities);
use POSIX qw( strftime );
use open ":utf8";
use warnings;
getopts("v:");
my $version = our($opt_v) || die "please enter the release, ie 2009AA";
my $current_date = strftime("%Y-%m-%d", localtime);
my ($year,$month,$day) = split /-/,$current_date;
my $exp_year = $year + 1;
my $base;
my %sources = ();
my $windows=($^O=~/Win/)?1:0;

if(! $windows){$base = "$ENV{'HOME'}/sourcereleasedocs";} else{$base = "$ENV{'USERPROFILE'}/sourcereleasedocs";}

my $path = join ("/", $base,$version);
chomp($path);

my $input = $path."/allsources.txt";
my $output = join("/",$base,"dcr",$version);
open FH, $input || die "could not open input $!";


while(<FH>) {
   
   my ($rsab,$ssn,$imeta,$language,$srl,$current,$delim) = split(/\|/,$_);
   if($imeta eq $version){$sources{$rsab} .= $ssn;}
      
}close FH;

find(\&get_sources,$path);


sub get_sources{
	
	#if (exists($sources{$_}) && -d) {print qq{$_\n};}
	if (exists($sources{$_}) && -d) {
		
		my $rsab = $_;
		
		my $ssn = $sources{$rsab};
		#print qq{$rsab -> $ssn\n};
		find(\&wrap_with_dcr,$rsab);
		
	}
}

sub wrap_with_dcr {
  
  my ($rsab,$file) = split(/\//,$File::Find::name);
  my $ssn = $sources{$rsab};
  #if (-f && $_ =~ /\.html/) {print qq{$rsab -> $name\n};}
  my ($name,$extension) = split(/\./,$file);
 
  if($extension eq "html") {open FH, "<$file" || die "Could not open $file for reading $!";
  	  print qq{processing $rsab, $file \n};
 
      my @encoded_contents;
      
      while(<FH>){
        
      	   my $encoded_content = encode_entities($_);
      	   $encoded_content =~ s/\n//g; ##get rid of newlines
           $encoded_content =~ s/\r//g; ##get rid of carriage returns
           if (/\S/) {push @encoded_contents,$encoded_content;}  ##don't add any blank lines to final output
      	   
      } ##end while
      close FH;
  	  open OUT,">$name\.dcr" || die "could not open output file $!";
      print OUT qq{<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE record SYSTEM "dcr4.5.dtd">
<record name="$name\.dcr" type="content">
<item name="title"><value>$version UMLS $ssn Source Information</value></item>
<item name="heading"><value>$version UMLS $ssn Source Information</value></item>
<item name="title2heading"><value></value></item>
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
<item name="metadata"><value><item name="ncbitoggler" content="indicator: 'plus-minus-big'"></item></value></item>
<item name="head"><value><item name="content"><value>
&lt;link rel="stylesheet" href="../../sourcereleasedocs-v2.css" type="text/css"/&gt;
&lt;script type="text/javascript" src="http://www.ncbi.nlm.nih.gov/core/jig/1.5.2/js/jig.min.js" language="javascript"&gt;&lt;/script&gt;
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
<value>};

if($name eq "metarepresentation"){unshift @encoded_contents,encode_entities("<p>This page lists UMLS Metathesaurus data elements and traces them back to the specific source data that populates them.</p>");}
if($name eq "sourcerepresentation"){unshift @encoded_contents,encode_entities("<p>This page lists specific source data elements and provides information on their representation in the UMLS Metathesaurus.</p>");}

print OUT join("\n",@encoded_contents); ##add content separated by newlines
print OUT qq{
</value></item>
</value></item>
</value></item><item name="secondaryContent"/></record>};
      	
      	
      close OUT;
    
  	
  	
  	
  } ##endif
  
}


