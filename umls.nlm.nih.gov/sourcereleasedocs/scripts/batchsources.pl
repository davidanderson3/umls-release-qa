#!/site/bin/perl5
use IO::File;
use Getopt::Std;

getopts("i:r:");
$input = $opt_i || die "Please enter a list of sources to be processeed\n";
$release = $opt_r || die "Please enter Meta release\n";
@vsabs;


%scripts = (
	"getTermTypes.pl"=>{
		"d"=>"cheek_midp",
		"r"=>$release,
	        "e"=>"utf-8",
		
				
	},
	"getAttributes.pl"=>{
		"d"=>"cheek_midp",
		"r"=>$release,
		"e"=>"utf-8"
				
	},
	"getRelationships.pl"=>{
		"d"=>"cheek_midp",
		"r"=>$release,
		"e"=>"utf-8"
		
	},
	
	##must use release db for source overlap and semantic type! ##	
	"getSourceOverlap.pl"=>{
		"d"=>"chin_mrdp",
			
	},
	"getSemanticTypes.pl"=>{
		"d"=>"chin_mrdp",
		
		
	
	},
       "getmrsab.pl"=>{
	  "d"=>"cheek_midp",
	   
	  
	},


);
#end %scripts hash

#create array of sources for this release

open (FH, $input) || die "could not locate input file\n";


while (<FH>) {
chomp($_);

my ($vsab,$rsab) = split(/\|/);
next if $vsab =~ /^#/;
mkdir $rsab;

&produceReports($vsab,$rsab);

}	


close FH;




#foreach $vsab(@vsabs) {
#	
#&produceReports($vsab);
#print qq{processing $vsab , @command_line ...\n};	
#	
#}

##iterate through each source and produce documentation.

sub produceReports {
	
	
my $vsab = shift;
my $rsab = shift;
chomp($vsab);
chomp($rsab);
my %args;
#print qq{print report for $rsab, $vsab};

foreach $report (sort(keys %scripts)) {
	
	my @commands;
	%args = %{ $scripts{$report} };
	while (($arg,$arg_value) = each %args) {
		
						
	#$arg = "-".$arg;
	$arg = qq{-$arg};
	my $command = $arg." ".$arg_value;
	
	unshift(@commands,$command);
	
	
		
	}
	`$report @commands -v $vsab -z $rsab`;
	print qq{$report @commands -v $vsab -z $rsab\n};
}
	
	
	
}

