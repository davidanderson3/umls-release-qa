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
getopts("d:v:r:z:");

$db = $opt_d || die "Need option -d to specify database, i.e. femur_midtest";
$vsab = $opt_v || die "Need option -v specify the vsab, i.e. HUGO2008_03";
$rsab = $opt_z || die "Need to specify directory";
#$release = $opt_r || die "Need option -r for the Metathesaurus release version, i.e. 2008AA";
#$enc = $opt_e || die "Need option -e for encoding , i.e. iso-8859-1";
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


my $output = new IO::File(">overlap.xml");
my $writer = new XML::Writer(OUTPUT => $output,DATA_MODE => 'true',DATA_INDENT => 4);
#$writer->xmlDecl($enc);

###############################################SEMANTIC TYPE COUNTS#################################################
###   simple counts from chin_mrdp db to calculate the percentage overlap of sources in the Metathesuaurus       ###
####################################################################################################################
$sql = qq{
	  select /* +PARALLEL(cr) */ count(distinct concept_id) from classes where source = '$vsab'
         };
$sh = $dbh->prepare($sql) || die "Cannot prepare the query\n";
$sh->execute || die "Cannot execute the query\n";
@results = $sh->fetchrow_array;
$numConcepts = $results[0];
print "Found $numConcepts cuis containing at least one term from $vsab.  Executing Source Overlap query...\n";


$writer->startTag('document','vsab'=>$vsab,'samples'=>'Overlap');

#print "Retrieving term type sample counts for $vsab\n"; 
              	
	
	
	$writer->startTag('samples');
	$writer->startTag('labels');
	$writer->startTag('label','type'=>'metalabel');
	$writer->characters('Source');
	$writer->endTag();
	$writer->startTag('label','type'=>'count');
	$writer->characters('Number of shared concepts<br/> out of ' .$numConcepts.' from '.$vsab);
	$writer->endTag();
	$writer->startTag('label','type'=>'count');
	$writer->characters('Percentage Overlap');
	$writer->endTag();
	$writer->endTag();
	&getCounts();
	$writer->endTag();
$writer->endTag();

########get the counts of cuis and stys from chin_mrdp####################################


sub getCounts {
   
	
	
	
	
	my $query = qq{
		select source,count(concept_id) as c,ROUND(count(distinct concept_id)/$numConcepts*100,1) as percentage from (
		select /* +PARALLEL(cr) */ distinct concept_id,source from classes
		where concept_id in (select concept_id from classes where source = '$vsab')
		and source != '$vsab'
		and tobereleased in ('Y','y')
		and source != 'MTH'
		)
		group by source
		order by count(concept_id) desc
	};
	my $sh = $dbh->prepare($query) ||  ( die "Can't prepare statement: $DBI::errstr");
        $sh->execute || die "Cannot execute query\n";
	
	
	while (my ($source,$c,$percentage) = $sh->fetchrow_array) {
	
	
		
		if ($percentage > .99) {	
	$writer->startTag("sample","id"=>$source,"samplename"=>$source,"count"=>$c,"percentage"=>$percentage);
	#&getSamples($value);
        $writer->endTag();
		}
	
	
	}
	
	
}
