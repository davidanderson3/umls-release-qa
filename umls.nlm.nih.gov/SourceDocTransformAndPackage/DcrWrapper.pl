#!/opt/local/bin/perl
use strict;
use warnings;
use diagnostics;
use Getopt::Std;
use File::Find;
use HTML::Entities;
use open ":utf8";
#use strict;
use warnings;
getopts("v:i:");
my $version = our($opt_v) || die "please enter the release, ie 2009AA";
#my $input_dir = our($opt_i) || die "need input directory";
#my $current_date = strftime("%Y-%m-%d", localtime);
#my ($year,$month,$day) = split /-/,$current_date;
my $base;
my %sources = ();
my $windows=($^O=~/Win/)?1:0;

if(! $windows){$base = "$ENV{'HOME'}/sourcereleasedocs";} else{$base = "$ENV{'USERPROFILE'}/sourcereleasedocs";}
#my $exp_year = $year + 1;
my $path = join ("/", $base,$version);
chomp($path);

my $input = $path."/allsources.txt";
my $output = $path."/dcr";
open FH, $input || die "could not open input $!";


while(<FH>) {
   chomp($_);
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
  print qq{processing $rsab\n};
  if($extension eq "html") {open FH, "<$file" || die "Could not open $file for reading $!";
  	  print qq{processing $name\n};
      my @encoded_contents = [];
      
      while(<FH>){
      	
      	my $encoded_content = encode_entities($_);
      	push(@encoded_contents,$encoded_content);
      	
      	
      } ##end while
      close FH;
  	#open OUT,">$output/$version/$rsab/$name\.dcr" || die "could not open output file $!";
    
  	
  	
  	
  } ##endif
  
}


