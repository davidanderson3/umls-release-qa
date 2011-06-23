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
$indexes = 4;


#instantiate xml
chdir $rsab;
my $output = new IO::File("attributes.xml");
my $writer = new XML::Writer(OUTPUT => $output,DATA_MODE => 'true',DATA_INDENT => 4,ENCODING=>$enc);
#$writer->xmlDecl($enc);

###############################################TERM TYPE SAMPLES AND COUNTS########################################################################
###         grab samples from MID and join them to pre-indexed MRCONSO file in /umls_s/dist_root/sourcereleasedocs                              ###
###################################################################################################################################################


$writer->startTag('document','vsab'=>$vsab,'samples'=>'Attributes');

print "Retrieving term type sample counts for $vsab\n"; 
              	
	
	
	$writer->startTag('samples');
	$writer->startTag('labels');
	$writer->startTag('label','type'=>'metalabel');
	$writer->characters('Attribute Name');
	$writer->endTag();
	$writer->startTag('label','type'=>'definition');
	$writer->characters('Description');
	$writer->endTag();
	$writer->startTag('label','type'=>'count');
	$writer->characters('Count (MRSAT.RRF)');
	$writer->endTag();
	$writer->endTag();
	&getValues();
	$writer->endTag();
$writer->endTag();

########get the value of the term type for which we are interested, then get the sample_ids from src_qa_samples####################################

sub getValues {
	
	#my $query = qq{
	#select distinct a.value,b.qa_count from src_qa_samples a, src_qa_results b where a.name = 'sab_atn_stype_tally' and a.value like '$vsab%'  and a.value = b.value order by 2 desc
	#};
	my $query = qq{
	select distinct a.value,b.qa_count from src_qa_samples a, src_qa_results b where a.name = 'sab_atn_stype_tally' and SUBSTR(a.value,1,INSTR(a.value||',',',') -1) = '$vsab' and a.value = b.value order by 2 desc
	};
	my $sh = $dbh->prepare($query) ||  ( die "Can't prepare statement: $DBI::errstr");
        $sh->execute || die "Cannot execute query\n";
	
	
	while (my ($value,$qa_count) = $sh->fetchrow_array) {
	
	my @sab_atn_stype_tally = split /,/,$value;
	my $atn = $sab_atn_stype_tally[1];
	my $linkid = $value;
	$linkid =~ s/,/_/g;
	my $def = `awk -F\\| \'\$1 == "ATN" \&\& \$2=="$atn" \&\& \$3 == "expanded_form" \{print \$4\}\' \/umls_s\/dist_root\/$release\/RRF_usr\/META\/MRDOC.RRF`;
	chomp($def);
	print "found $qa_count $atn attribute names\n";
	$writer->startTag("sample","id"=>$linkid,"samplename"=>($atn),"definition"=>$def,"count"=>$qa_count);
	&getSamples($value);
        $writer->endTag();
	
	
	}

	
	
	
}
######retrieve the aui samples from src_qa_samples table and join them to the MRCONSOindex1 file ###################################################

sub getSamples{
	print "Retrieving sample RRF data and producing xml\n";
	my $value = $_[0];
	my $query = qq{
		select a.atui,a.attribute_value,b.atom_name from attributes a, atoms b where a.attribute_id in (select sample_id from src_qa_samples where value = '$value') and a.atom_id = b.atom_id and rownum < 20
	};
	my $sh = $dbh->prepare($query) ||  ( die "Can't prepare statement: $DBI::errstr");
	$sh->execute || die "Cannot execute query\n";
	while (my ($atui,$atv,$term) = $sh->fetchrow_array) {
	
		
		if ($atv =~ /Long_Attribute/) {
		#$atv = "long_attribute\n";	
		@longatv = split/:/,$atv;
		$atv = getStringTab($longatv[1]);
		
		
		}
		
	        for ($i = 1; $i < $indexes +1; $i++)   {
		
			
			
		$match = `look $atui /umls_s/dist_root/sourcereleasedocs/MRSATindex$i`;
		#chomp($match);
		if ($match =~ /[A]+/) {
		@matchfields = split /\|/,$match;
		$umlsatui = $matchfields[0];
		#print "found $atui from MID with $umlsatui from MRSAT\n";
		$cui = $matchfields[1];
		$lui = $matchfields[2];
		$sui = $matchfields[3];
		$metaui = $matchfields[4]; 
		$stype = $matchfields[5];
		$code = $matchfields[6];
		$atn = $matchfields[7];
		#$atv = $matchfields[8];
		$stringlengths = length($atv);
		chomp($atv);
		#print "$atui \t $stringlengths \t $atv\n";
		print "matching $atui from mid with $umlsatui from rrf\n";
		
		##numbers in keys allow us to sort any way we want##
		%metadata = ("1CUI"=>$cui,"2METAUI"=>$metaui,"3LUI"=>$lui,"4SUI"=>$sui,"5STYPE"=>$stype,"6CODE"=>$code,"7ATN"=>$atn,"8ATV"=>$atv,"9STR"=>$term);
		$writer->startTag("row","type"=>"standard");
		
		##short,sweet way to construct xml sample rows##
		foreach $key (sort(keys(%metadata))) {
			
			#don't print out blank elements!
			if  ($metadata{$key} ne "") {
		        #assign value of hash to variable value
			$value = $metadata{$key};
			#get rid of number used to sort
			$key =~ s/^.//;
			#added for atns  that have links
			#if ($value =~ /http:\/\//) {
			#	
			#$newvalue = 
			#	
			#}
			$value =~ s/&/&amp;/g;
			$value =~ s/\</&lt;/g;
			$value =~ s/\>/&gt;/g;
			$value =~ s/&lt;sup&gt;/<sup>/g;
			$value =~ s/&lt;\/sup&gt;/<\/sup>/g;
			$value =~ s/&lt;sub&gt;/<sub>/g;
			$value =~ s/&lt;\/sub&gt;/<\/sub>/g;
			
			#//added for atns  that have links
			$writer->startTag("field","type"=>$key);
			$writer->characters($value);
			$writer->endTag();
			
			}
		       #end if
		
		
		}
		#end foreach  					
		$writer->endTag();
				        
		
		
		
		
		
	        }
	        #end for
	
		}#end if
	
	}
        #end while	
}


sub getStringTab {
	
my $string_id = $_[0];
my $query = qq{
		select text_value from stringtab where string_id = '$string_id'
};
my $sh = $dbh->prepare($query) ||  ( die "Can't prepare statement: $DBI::errstr");
$sh->execute || die "Cannot execute query\n";

while (my ($atv) = $sh->fetchrow_array) {
return $atv;
	
	
}

} 


	    


	   
