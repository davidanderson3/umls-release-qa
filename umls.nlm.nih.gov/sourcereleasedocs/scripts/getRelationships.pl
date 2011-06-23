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


getopts("d:v:r:e:z:");

$db = $opt_d || die "Need option -d to specify database, i.e. femur_midtest";
$vsab = $opt_v || die "Need option -v specify the vsab, i.e. HUGO2008_03";
$release = $opt_r || die "Need option -r for the Metathesaurus release version, i.e. 2008AA";
$enc = $opt_e || die "Need option -e for encoding , i.e. iso-8859-1";
$rsab = $opt_z || die "Need to specify directory";
$release_dir = "/umls_s/dist_root/".$release."/RRF_usr/META/";
$index_dir = "/umls_s/dist_root/sourcereleasedocs/";
$indexes = 4;
#
# Open Database Connection
#
# set variables
$userpass = `$ENV{MIDSVCS_HOME}/bin/get-oracle-pwd.pl -d $db`;
( $user, $password ) = split /\//, $userpass;
chop($password);
#open (MRCONSO, $release_dir."/MRREL.RRF") || die "Could not open MRRREL";
# open connection
$dbh = DBI->connect("dbi:Oracle:$db", "$user", "$password") or die "Can't connect to Oracle database: $DBI::errstr\n";

chdir $rsab;
my $output = new IO::File("relationships.xml");
my $writer = new XML::Writer(OUTPUT => $output,DATA_MODE => 'true',DATA_INDENT => 4,ENCODING=>$enc);
#$writer->xmlDecl($enc);

###############################################TERM TYPE SAMPLES AND COUNTS##########################################################################
### requires that an indexed aui file has already been created through auiindex.sh script.  grabs all the samples and performs a 'look' to determine 
### which line of MRCONSO a given aui came from  
###################################################################################################################################################


##Begin writing xml

$writer->startTag('document','vsab'=>$vsab,'samples'=>'Relationships');

print "Retrieving relationship sample counts for $vsab\n"; 
              	
	
	
	$writer->startTag('samples');
	$writer->startTag('labels');
	$writer->startTag('label','type'=>'metalabel');
	$writer->characters('Relationship');
	$writer->endTag();
	$writer->startTag('label','type'=>'metalabel');
	$writer->characters('Relationship Attribute');
	$writer->endTag();
	$writer->startTag('label','type'=>'count');
	$writer->characters('Count (MRREL.RRF)');
	$writer->endTag();
	$writer->endTag();
	&getValues();
	$writer->endTag();

$writer->endTag();




	
########get the value of the term type for which we are interested and place it into a hash with it's respective count####################################

sub getValues {
	
	my $query = qq{
	select distinct a.value,b.qa_count from src_qa_samples a, src_qa_results b where a.name = 'sab_rel_rela_stype1_stype2_tally' and SUBSTR(a.value,1,INSTR(a.value||',',',') -1) = '$vsab'  and a.value = b.value order by 2 desc
	};
	my $sh = $dbh->prepare($query) ||  ( die "Can't prepare statement: $DBI::errstr");
        $sh->execute || die "Cannot execute query\n";
	
	
	while (my ($value,$qa_count) = $sh->fetchrow_array) {
	
	my @fields = split /,/,$value;
	$midrel = $fields[1];
	$rela = $fields[2];
	##account for condition of context relationships
	my $table = &getTable($midrel);
	
	if ($rela eq "") {
	$rela = "*";	
		
	}
	
	
	
	$linkid = $value;
	$linkid =~ s/,/_/g;
	$linkid =~ s/SFO\/LFO/SFO_LFO/g;
	print "found $qa_count  for $value relationship\n";
	$release_name = &getReleaseName($midrel);
	my $def = `awk -F\\| \'\$1 == "REL" \&\& \$2=="$release_name" \&\& \$3 == "expanded_form" \{print \$4\}\' \/umls_s\/dist_root\/$release\/RRF_usr\/META\/MRDOC.RRF`;
	$writer->startTag("sample","id"=>$linkid,"samplename"=>$release_name,"attribute"=>$rela,"definition"=>$def,"count"=>$qa_count);
	&getSamples($value,$table);
	
	
	$writer->endTag();
	
	
	}

	
	
	
}







######retrieve the aui samples from src_qa_samples table ###########################################################################################

sub getSamples{
	my $value = $_[0];
	my $table = $_[1];

	my $query = qq{
	select a.rui, b.atom_name as term1, c.atom_name as term2, d.inverse_rui from $table a,atoms b, atoms c, inverse_relationships_ui d where relationship_id in (
        select relationship_id from $table where relationship_id in (
        select sample_id from src_qa_samples where value = '$value')) and
        a.atom_id_1 = b.atom_id and a.atom_id_2 = c.atom_id  and a.rui=d.rui and rownum < 20 order by a.rui
	};
	my $sh = $dbh->prepare($query) ||  ( die "Can't prepare statement: $DBI::errstr");
	$sh->execute || die "Cannot execute query\n";
	while (my ($rui,$term1,$term2,$inverserui) = $sh->fetchrow_array) {
	
		print "$rui, $inverserui\n";
		$term1 =~ s/\</&lt;/g;#NCI string fix
		$term1 =~ s/\>/&gt;/g;#NCI string fix
		$term2 =~ s/\</&lt;/g;#NCI string fix
		$term2 =~ s/\>/&gt;/g;#NCI string fix
		$term1 =~ s/&lt;sup&gt;/<sup>/g;
		$term1 =~ s/&lt;\/sup&gt;/<\/sup>/g;
		$term1 =~ s/&lt;sub&gt;/<sub>/g;
		$term1 =~ s/&lt;\/sub&gt;/<\/sub>/g;
		$term2 =~ s/&lt;sup&gt;/<sup>/g;
		$term2 =~ s/&lt;\/sup&gt;/<\/sup>/g;
		$term2 =~ s/&lt;sub&gt;/<sub>/g;
		$term2 =~ s/&lt;\/sub&gt;/<\/sub>/g;

                for ($i = 1; $i < $indexes +1; $i++) {

                   
		   $match = `look $rui /umls_s/dist_root/sourcereleasedocs/MRRELindex$i`;
		   $inversematch = `look $inverserui /umls_s/dist_root/sourcereleasedocs/MRRELindex$i`;
		   if ($match =~ /[R]+/) {
		   
	           chomp($match);
		   #print "$match\n";
		   
		    
		   @matchfields = split /\|/,$match;
		   $metarui = $matchfields[0];
		   $cui1 = $matchfields[1];
		   $aui1 = $matchfields[2];
		   $stype1 = $matchfields[3];
		   $rel = $matchfields[4];
		   $rela = $matchfields[5];
		   $cui2 = $matchfields[6];
		   $aui2 = $matchfields[7];
		   $stype2 = $matchfields[8];
		   #print "found $rui in mid and $metarui in rrf in $table\n";
		   %metadata = ("1CUI1"=>$cui1,"2AUI1"=>$aui1,"3STYPE1"=>$stype1,"4STR"=>$term1,"5REL"=>$rel,"6RELA"=>$rela,"aCUI2"=>$cui2,"9AUI2"=>$aui2,"8STYPE2"=>$stype2,"7STR"=>$term2);
		   
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
		       #end if
		
		
		       }
		        #end foreach  					
		        $writer->endTag();
		   
		   
		   
		   
		   #print "Record for $rui: $aui1|$term1|$rel|$rela|$aui2|$term2\n";
		   
		   
		   
		   
		   
		   }
		   if ($inversematch =~ /[R]+/) {
		
	           chomp($inversematch);
		   #print "$match\n";
		
		   @inversematchfields = split /\|/,$inversematch;
		   $inversecui1 = $inversematchfields[1];
		   $inverseaui1 = $inversematchfields[2];
		   $inversestype1 = $inversematchfields[3];
		   $inverserel = $inversematchfields[4];
		   $inverserela = $inversematchfields[5];
		   $inversecui2 = $inversematchfields[6];
		   $inverseaui2 = $inversematchfields[7];
		   $inversestype2 = $inversematchfields[8];
		   %inversemetadata = ("1cui1"=>$inversecui1,"2aui1"=>$inverseaui1,"3stype1"=>$inversestype1,"4str"=>$term2,"5rel"=>$inverserel,"6rela"=>$inverserela,"acui2"=>$inversecui2,"9aui2"=>$inverseaui2,"8stype2"=>$inversestype2,"7str"=>$term1);
		   $writer->startTag("row", "type"=>inverse);
		   foreach $key (sort(keys(%inversemetadata))) {
			
			#don't print out blank elements!
			if  ($inversemetadata{$key} ne "") {
		        #assign value of hash to variable value
			$value = $inversemetadata{$key};
			#get rid of number/letter used to sort
			$key =~ s/^.//;
			
			$writer->startTag("field","type"=>$key);
			$writer->characters($value);
			$writer->endTag();
			
			}
		       #end if
		
		
		        }
		        #end foreach  					
		        $writer->endTag();
		   
		   
		   
		   
		   
		   
		   
		   }
		   
		   
		   
		   
		   
		   
                }		
					
		
				        
	}
	
	
}

sub getReleaseName {
	
$midname = $_[0];
        my $query = qq{
	select release_name from inverse_relationships where relationship_name = '$midname'
	};
	my $sh = $dbh->prepare($query) ||  ( die "Can't prepare statement: $DBI::errstr");
        $sh->execute || die "Cannot execute query\n";
	while (my ($rel) = $sh->fetchrow_array) {
		
	return $rel;	
		
		
	}
	
	
}

sub getTable{
	
my $rel = $_[0];

if ($rel eq "PAR" or $rel eq "CHD" or $rel eq "SIB") {
	
    $table = 'context_relationships';
    
}

else {
	
$table = 'relationships';

}
print "using $table\n";
return $table;

	
}

    


	    


	   
