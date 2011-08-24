#!/site/bin/perl5
use IO::File;
use XML::Writer;
use Getopt::Std;
unshift @INC, "$ENV{ENV_HOME}/bin";
require "env.pl";
use open ":utf8";
require DBI;
require DBD::Oracle;
##arguments are -d for database, -v for vsab, -r for release (e.g. 2008AA)
# set variables
my %options;
getopts("d:v:r:e:z:");


$db = $opt_d || die "Need option -d to specify database, i.e. femur_midtest";
$vsab = $opt_v || die "Need option -v specify the vsab, i.e. HUGO2008_03";
$release = $opt_r || die "Need option -r for the Metathesaurus release version, i.e. 2008AA";
$enc = $opt_e || die "Need option -e for encoding , i.e. iso-8859-1";
$rsab = $opt_z || die "Need to specify directory";

$userpass = `$ENV{MIDSVCS_HOME}/bin/get-oracle-pwd.pl -d $db`;
( $user, $password ) = split /\//, $userpass;
chop($password);

# open connection
$dbh = DBI->connect("dbi:Oracle:$db", "$user", "$password") or die "Can't connect to Oracle database: $DBI::errstr\n";

#check to make sure we are starting in the right place
$home = $ENV{'HOME'};
$workingdir = $home."/sourcereleasedocs";

if (-d $workingdir) {

print qq{ Working directory sourcereleasedocs exists};
chdir $workingdir."/".$rsab;

}
else {

die "Need to create sourcereleasedocs directory in under $home to run this script\n";

}

#instantiate xml

my $output = new IO::File(">termtypes.xml");
my $writer = new XML::Writer(OUTPUT => $output,DATA_MODE => 'true',DATA_INDENT => 4,ENCODING=>$enc);
#$writer->xmlDecl($enc);

###############################################TERM TYPE SAMPLES AND COUNTS########################################################################
###         grab samples from MID and join them to pre-indexed MRCONSO file in /umls_s/dist_root/sourcereleasedocs                              ###
###################################################################################################################################################


$writer->startTag('document','vsab'=>$vsab,'samples'=>'Term Types');

print "Retrieving term type sample counts for $vsab\n"; 
              	
	
	
	$writer->startTag('samples');
	$writer->startTag('labels');
	$writer->startTag('label','type'=>'metalabel');
	$writer->characters('Term Type');
	$writer->endTag();
	$writer->startTag('label','type'=>'definition');
	$writer->characters('Description');
	$writer->endTag();
	$writer->startTag('label','type'=>'count');
	$writer->characters('Count (MRCONSO.RRF)');
	$writer->endTag();
	$writer->endTag();
	&getValues();
	$writer->endTag();
$writer->endTag();

########get the value of the term type for which we are interested, then get the sample_ids from src_qa_samples####################################

sub getValues {
	
	my $query = qq{
	select distinct a.value,b.qa_count from src_qa_samples a, src_qa_results b where a.name = 'sab_lat_tty_tally' and SUBSTR(a.value,1,INSTR(a.value||',',',') -1) = '$vsab'  and a.value = b.value order by 2 desc
	};
	my $sh = $dbh->prepare($query) ||  ( die "Can't prepare statement: $DBI::errstr");
        $sh->execute || die "Cannot execute query\n";
	
	
	while (my ($value,$qa_count) = $sh->fetchrow_array) {
	
	my @sab_lat_tty_tally = split /,/,$value;
	my $tty = $sab_lat_tty_tally[2];
	
        my $def = `awk -F\\| \'\$1 == "TTY" \&\& \$2=="$tty" \&\& \$3 == "expanded_form" \{print \$4\}\' \/umls_s\/dist_root\/$release\/RRF_usr\/META\/MRDOC.RRF`;
	chomp($def);
	print "found $qa_count $tty term type atoms\n";
	
	
	$writer->startTag("sample","id"=>$tty,"samplename"=>$tty,"definition"=>$def,"count"=>$qa_count);
	&getSamples($value);
        $writer->endTag();
	
	
	}

	
	
	
}
######retrieve the aui samples from src_qa_samples table and join them to the MRCONSOindex1 file ###################################################

sub getSamples{
	print "Retrieving sample RRF data and producing xml\n";
	my $value = $_[0];
	my $query = qq{
	select aui from classes where atom_id in (select sample_id from src_qa_samples where value = '$value') and rownum < 10
	};
	my $sh = $dbh->prepare($query) ||  ( die "Can't prepare statement: $DBI::errstr");
	$sh->execute || die "Cannot execute query\n";
	while (my ($aui) = $sh->fetchrow_array) {
	
		print "$aui\n";
		@matches = `look $aui /umls_s/dist_root/sourcereleasedocs/MRCONSOindex1`;
		$num_matches = scalar(@matches);
		##since identifier length has increased, look command can return more than one line###
		if ($num_matches > 1) {
	 	$match = $matches[$#matches];
		print "found $num_matches for $aui\n";
		}
		
		else {
		$match = $matches[0];	
			
		}
		
		
		@matchfields = split /\|/,$match;
		
		$atom = $matchfields[0];
		$cui = $matchfields[1];
		$lui = $matchfields[2];
		$sui = $matchfields[3];
		$saui = $matchfields[4]; 
		$scui = $matchfields[5];
		$sdui = $matchfields[6];
		$code = $matchfields[7];
		$string = $matchfields[8];
		$string =~ s/&/&amp;/g;
		$string =~ s/\</&lt;/g;#NCI string fix
		$string =~ s/\>/&gt;/g;#NCI string fix
		$string =~ s/&lt;sub&gt;/<sub>/g;
		$string =~ s/&lt;\/sub&gt;/<\/sub>/g;
		$string =~ s/&lt;sup&gt;/<sup>/g;
		$string =~ s/&lt;\/sup&gt;/<\/sup>/g;
		#print "matching $aui from mid with $atom from rrf\n";
		##numbers in keys allow us to sort any way we want##
		%metadata = ("1CUI"=>$cui,"2AUI"=>$atom,"3LUI"=>$lui,"4SUI"=>$sui,"5SAUI"=>$saui,"6SCUI"=>$scui,"7SDUI"=>$sdui,"8CODE"=>$code,"9STR"=>$string);
		$writer->startTag("row","type"=>standard);
		
		##short,sweet way to construct xml sample rows##
		foreach $key (sort(keys(%metadata))) {
			
			#don't print out blank elements!
			if  ($metadata{$key} ne "") {
		        #assign value of hash to variable value
			$value = $metadata{$key};
			#get rid of number used to sort
			$key =~ s/^.//;
			
			$writer->startTag("field","type"=>$key);
			$writer->characters($value);
			$writer->endTag();
			
			}
		}
		  					
		$writer->endTag();
		
		
	
	}
	
	
	
	
	
	
}




	    


	   
