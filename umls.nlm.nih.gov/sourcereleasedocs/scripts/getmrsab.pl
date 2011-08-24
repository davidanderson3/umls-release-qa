#!/site/bin/perl5
use IO::File;
use XML::Writer;
use Getopt::Std;
unshift @INC, "$ENV{ENV_HOME}/bin";
require "env.pl";
use open ":utf8";
require DBI;
require DBD::Oracle;
use warnings;
##arguments are -v for vsab, -d for database

getopts("v:d:z:");


$vsab = $opt_v || die "Need option -v specify the vsab, i.e. HUGO2008_03";
$db = $opt_d || die "Please specify a database (cheek_midp)\n";
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

my $output = new IO::File(">mrsab.xml");
$writer = new XML::Writer(OUTPUT => $output,DATA_MODE => 'true',DATA_INDENT => 4);
$writer->xmlDecl("utf-8");
$writer->startTag("document","vsab"=>$vsab);



$query = qq{

	select a.versioned_cui as VCUI,a.root_cui as RCUI,a.source as VSAB,a.source_short_name as RSAB,a.source_official_name as SON,b.source_family as SF,a.meta_ver as IMETA, a.license_contact as SLC,a.content_contact as SCC,b.restriction_level as SRL,a.term_frequency as TFR,a.cui_frequency as CFR,
        a.context_type as CXTY,a.term_type_list as TTYL, a.attribute_name_list as ATNL,a.language as LAT,a.character_set as CENC,a.source_short_name as SSN,a.citation as SCIT from sims_info a, source_rank b where a.source = '$vsab' and a.source = b.source
	
	
};


$sh = $dbh->prepare($query) ||  ( die "Can't prepare statement: $DBI::errstr");
$sh->execute || die "Cannot execute query\n";


while (my($vcui,$rcui,$vsab,$rsab,$son,$sf,$imeta,$slc,$scc,$srl,$tfr,$cfr,$cxty,$ttyl,$atnl,$lat,$cenc,$ssn,$scit) = $sh->fetchrow_array) {
	
       
	
	%hash1 = ("1VCUI"=>$vcui,"2RCUI"=>$rcui,"3VSAB"=>$vsab,"4SON"=>$son,"5SF"=>$sf,"6IMETA"=>$imeta);
	#print "$vcui,$rcui,$vsab,$rsab\n";
	&printfields(\%hash1);
	
	%slchash = &processSlcScc($slc);
	%scchash = &processSlcScc($scc);
	%scithash = &processScit($scit);
	$slcdef = &getDef("SLC");
	$writer->startTag("field","type"=>"SLC","definition"=>$slcdef);
	&printsubfields(\%slchash);
	$writer->endTag();
	$sccdef = &getDef("SCC");
	$writer->startTag("field","type"=>"SCC","definition"=>$sccdef);
	&printsubfields(\%scchash);
	$writer->endTag();
	%hash2 = ("1SRL"=>$srl,"2TFR"=>$tfr,"3CFR"=>$cfr,"4CXTY"=>$cxty,"5TTYL"=>$ttyl,"6ATNL"=>$atnl,"7LAT"=>$lat,
		  "8CENC"=>$cenc,"9SSN"=>$ssn);
	&printfields(\%hash2);
	
	
	$scitdef = &getDef("SCIT");
	$writer->startTag("field","type"=>"SCIT","definition"=>$scitdef);
	&printsubfields(\%scithash);
        $writer->endTag();
	
}

$writer->endTag();

sub printfields(\%) {
	
	my (%metadata) = %{(shift)};	
	print "Sorting through hash\n";
        foreach  $key (sort(keys(%metadata))) {
			
	#don't print out blank elements!
	if  ($metadata{$key} ne "") {
	#assign value of hash to variable value
	
	my $value = $metadata{$key};
	#get rid of number used to sort
	$key =~ s/\d+//;
	$keydef = &getDef($key);
			
	$writer->startTag("field","type"=>$key,"definition"=>$keydef);
	$writer->characters($value);
	$writer->endTag();
			
        }
       
	}
	
	
	
	
}


sub getDef {
my $sabfield = $_[0];
my $definition;
$defaulttext = "Click link for full explanation";
        if ($sabfield eq "VCUI") {
	
	$definition = "Versioned CUI, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "RCUI"){
		
	$definition = "Root CUI, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "VSAB"){
		
	$definition = "Versioned Source Abbreviation, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "RSAB"){
		
	$definition = "Root Source Abbreviation, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "SON"){
		
	$definition = "Source Official Name, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "SF"){
		
	$definition = "Source Family, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "SVER"){
		
	$definition = "Source Version, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "VSTART"){
		
	$definition = "Meta Start Date, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "VEND"){
		
	$definition = "Meta End Date, ".$defaulttext;
		
	}
	
	
	
	elsif ($sabfield eq "IMETA"){
		
	$definition = "Meta Insert Version, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "RMETA"){
		
	$definition = "Meta Remove Version, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "SLC"){
		
	$definition = "Source License Contact, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "SCC"){
		
	$definition = "Source Content Contact, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "SRL"){
		
	$definition = "Source Restriction Level, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "TFR"){
		
	$definition = "Term Frequency, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "CFR"){
		
	$definition = "CUI Frequency, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "CXTY"){
		
	$definition = "Context Type, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "TTYL"){
		
	$definition = "Term Type List, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "ATNL"){
		
	$definition = "Attribute Name List, ".$defaulttext;
		
	}
	elsif ($sabfield eq "LAT"){
		
	$definition = "Language, ".$defaulttext;
		
	}
	elsif ($sabfield eq "CENC"){
		
	$definition = "Character Encoding, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "CURVER"){
		
	$definition = "Current Version, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "SABIN"){
		
	$definition = "Source in Subset, ".$defaulttext;
		
	}
	
	elsif ($sabfield eq "SSN"){
		
	$definition = "Source Short Name, ".$defaulttext;
		
	}
	elsif ($sabfield eq "SCIT"){
		
	$definition = "Source Citation, ".$defaulttext;
		
	}
	
	
	else {
	$definition = "";
	}
	
	
	


return $definition;	
	
	
}


sub printsubfields(\%) {
	
	my (%metadata) = %{(shift)};	
	print "Sorting through hash\n";
        foreach  $key (sort(keys(%metadata))) {
			
	#don't print out blank elements!
	if  ($metadata{$key} ne "") {
	#assign value of hash to variable value
	
	my $value = $metadata{$key};
	#get rid of number used to sort
	#$key =~ s/\d+//;
	$key =~ s/^.//;
	print "$key\n";
	
	if ($key eq "URL") {
	my $link ="<a href = \'$value\' target = \'blank\'>".$value."</a>";
	$writer->startTag("subfield","type"=>$key);
	$writer->characters($link);
	$writer->endTag();
	
	
	}
	
	
	elsif ($key eq "Email") {
	$value = "<a href = \'mailto:$value\'>".$value."</a>";
	$writer->startTag("subfield","type"=>$key);
	$writer->characters($value);
	$writer->endTag();
	
	
	}
	
	else {
		
        $writer->startTag("subfield","type"=>$key);
	$writer->characters($value);
	$writer->endTag();
		
	}
	
	
	
	
       }
   
	
    }
	
	
	
	
}




sub processSlcScc {
	
   my $content = $_[0];
   print "$content\n";
   my @fields = split /;/,$content;
   my $contact_name = $fields[0];
   my $contact_title = $fields[1];
   my $contact_organization = $fields[2];
   my $contact_address1 = $fields[3];
   my $contact_address2 = $fields[4];
   my $contact_city = $fields[5];
   my $contact_state = $fields[6];
   my $contact_country = $fields[7];
   my $contact_zip = $fields[8];
   my $contact_phone = $fields[9];
   my $contact_fax = $fields[10];
   my $contact_email = $fields[11];
   my $contact_url = $fields[12];
   
   
   
   my %contactinfo = ("1Contact Name"=>$contact_name,"2Contact Title"=>$contact_title,"3Contact Organization"=>$contact_organization,"4Address 1"=>$contact_address1,
	              "5Address 2"=>$contact_address2,"6City"=>$contact_city,"7State or Province"=>$contact_state,"8Country"=>$contact_country,"9Zip/Postal Code"=>$contact_zip,"aPhone"=>$contact_phone,
		      "bFax"=>$contact_fax,"cEmail"=>$contact_email,"dURL"=>$contact_url);
   
   
   return %contactinfo;
   
	
	
}

sub processScit {
	
      my $content = $_[0];
      print "$content\n";
      my @fields = split /;/,$content;
      my $authors = $fields[0];
      my $author_address = $fields[1];
      my $organization = $fields[2];
      my $editors = $fields[3];
      my $title = $fields[4];
      my $content_designator = $fields[5];
      my $medium_designator = $fields[6];
      my $edition = $fields[7];
      my $place_of_publication = $fields[8];
      my $publisher = $fields[9];
      my $date_of_publication = $fields[10];
      my $date_of_revision = $fields[11];
      my $location = $fields[12];
      my $extent = $fields[13];
      my $series = $fields[14];
      my $url = $fields[15];
      my $language = $fields[16];
      my $notes = $fields[17];

      
      
      
      
      my %citationinfo = ("1Author(s)"=>$authors,"2Address"=>$author_address,"3Organization"=>$organization,"4Editor(s)"=>$editors,"5Title"=>$title,
	                   "6Content Designator"=>$content_designator,"7Medium Designator"=>$medium_designator,"8Editon"=>$edition,
			   "9Place of Publication"=>$place_of_publication,"aPublisher"=>$publisher,"bDate of publication/copyright"=>$date_of_publication,
			   "cDate of Revision"=>$date_of_revision,"dLocation"=>$location,"eExtent"=>$extent,"fSeries"=>$series,"gURL"=>$url,
			   "hLanguage"=>$language,"iNotes"=>$notes);
      
      
      
      return %citationinfo;
	
	
}







