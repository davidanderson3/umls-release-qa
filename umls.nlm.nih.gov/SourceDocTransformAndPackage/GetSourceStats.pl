#!/opt/local/bin/perl
use strict 'vars';
use strict 'subs';
use warnings;
use Getopt::Std;
require DBI;
require DBD::Oracle;
use Env;
getopts("v:");
my $username = 'kss2013AB';
my $pw = 'kss2013AB582!()';
my $db = 'utsrel_prod';
#my $samples = '../etc/source_samples.txt';
#my $samples = 'C:/Eclipse UTS Workspace/SourceDocumentationStatistics/etc/source_samples.txt';
my $base = "$ENV{'HOME'}/sourcereleasedocs";
#my $base = "$ENV{'USERPROFILE'}/sourcereleasedocs";
my $dbh = DBI->connect("dbi:Oracle:$db", "$username", "$pw") or die "Can't connect to Oracle database: $DBI::errstr\n";
our($opt_v);
my $version = $opt_v || die "Please specify a UTS release against which you can query\n";
my $samples = "$base/$version/source_samples.txt";

open DATA, $samples || die "Could not open sample file $!\n";

#chdir ($outputarea) || die "Could not find output area $!\n";
while (defined (my $line=<DATA>)) {


	
	chomp $line;
	my ($rsab,$source,$idType) = split(/\|/,$line);
	print qq{Processing $rsab...\n};
	next if (! -d $base."/".$version."/".$rsab);
	chdir($base."/".$version."/".$rsab) || die "Could not cd to source directory $rsab $!";
	open (my $fh, ">", "stats.txt") || die "Could not open stats file $!\n";
	binmode($fh);
	&getTermTypes($rsab,$fh);
	#&getAttributes($rsab,$fh);
	#&getRelationships($rsab,$fh);
	#&getSemanticTypes($rsab,$fh);
	#&getPrefNameSemanticTypes($rsab,$fh);
	#&getSourceOverlap($rsab,$fh);
	print qq{Done processing $rsab\n};

}

print qq{Stats processing complete!\n};


sub getTermTypes {
	
	my $rsab = shift;
	my $fh = shift;
	my $query = qq{
		select a.term_type,b.expanded_form,count(*) from atoms a, term_types b
		where a.root_source = '$rsab' and a.term_type = b.abbreviation
		group by a.term_type,b.expanded_form
		order by count(*) desc
	};
	
	my $sh = $dbh->prepare($query) ||  die "Can't prepare statement: $DBI::errstr";
    $sh->execute || die "Cannot execute query\n";
    
    print qq{  Processing Term Types for $rsab\n};
    print $fh qq{*Term Type Counts|Term Type|Expanded Form|Count\n};
    while (my (@row)  = $sh->fetchrow_array) {
    	printf $fh "%s\n", join("|",@row);
    	
    }
    print $fh qq{!\n};
   
	
}

sub getAttributes {
	
	my $rsab = shift;
	my $fh = shift;
	my $query = qq{
		select a.name,b.expanded_form,count(*) from attributes a, attribute_names b
        where
        a.root_source = '$rsab'
        and
        a.name=b.abbreviation
        group by a.name,b.expanded_form
        order by 
        count(*) desc
	};
	
	my $sh = $dbh->prepare($query) ||  die "Can't prepare statement: $DBI::errstr";
    $sh->execute || die "Cannot execute query\n";
    
    print qq{  Processing Attributes for $rsab};
    print $fh qq{*Attribute Counts|Attribute Name|Expanded Form|Count\n};
    while (my (@row)  = $sh->fetchrow_array) {
    	printf $fh "%s\n", join("|",@row);
    	
    }
    print $fh qq{!\n};
	
}

sub getRelationships {
	
	my $rsab = shift;
	my $fh = shift;
	my $query = qq{
		select a.label|| '/' || a.additional_label "REL/RELA", b.expanded_form,count(*) from
		relations a, relation_labels b
		where a.root_source = '$rsab'
		and
		a.label=b.abbreviation
		group by a.label|| '/' || a.additional_label,b.expanded_form
		order by count(*) desc
	};
	
	my $sh = $dbh->prepare($query) ||  die "Can't prepare statement: $DBI::errstr";
    $sh->execute || die "Cannot execute query\n";
    
    print qq{  Processing Relationships for $rsab\n};
    print $fh qq{*Relationship Counts|Relation Name/Additional Label(if present)|Expanded Form|Count\n};
    while (my (@row)  = $sh->fetchrow_array) {
    	printf $fh "%s\n", join("|",@row);
    	
    }
    print $fh qq{!\n};
	
}

sub getSourceOverlap {
	
	my $rsab = shift;
	my $fh = shift;
	my $query = qq{
		
    WITH source_concept AS
        (select count(distinct concept_id) as source_concept_count from atoms where root_source = '$rsab'),

        other_source_concept AS 
         (select root_source as other_root_source, count(distinct concept_id) as other_source_concept_count 
         from atoms where root_source not in ('$rsab', 'MTH') and concept_id in
          (
          select distinct concept_id from atoms where root_source = '$rsab'
          )
         group by root_source
         )

    select a.other_root_source,a.other_source_concept_count||'/'||b.source_concept_count, ROUND((a.other_source_concept_count/(select source_concept_count from source_concept))*100, 1) AS "percent overlap" from 
    other_source_concept a, source_concept b
    order by a. other_source_concept_count desc
		
		};
		
	my $sh = $dbh->prepare($query) ||  die "Can't prepare statement: $DBI::errstr";
    $sh->execute || die "Cannot execute query\n";
    
    print qq{  Processing Overlap for $rsab\n};
    print $fh qq{*Source Overlap|Source|# Concepts Sharing Atom/# Total Concepts|Percentage Overlap\n};
    while (my (@row)  = $sh->fetchrow_array) {
    	printf $fh "%s\n", join("|",@row);
    	
    }
	print $fh qq{!\n};
	
}

sub getSemanticTypes {
	
	my $rsab = shift;
	my $fh = shift;
	my $query = qq{
		WITH total_concepts AS
        (select  count(distinct concept_id) as source_concept_count from atoms where root_source = '$rsab')
        select c.ui, c.value, count(distinct a.concept_id), ROUND(count(distinct b.concept_id)/(select * from total_concepts)*100,1)
        from atoms a, concept_stys b, stys c, total_concepts
        where a.root_source = '$rsab' 
        and a.concept_id = b.concept_id
        and b.sty_id = c.id
        group by c.ui, c.value
        order by count(distinct a.concept_id) desc

	};
	
	my $sh = $dbh->prepare($query) ||  die "Can't prepare statement: $DBI::errstr";
    $sh->execute || die "Cannot execute query\n";
    
    print qq{  Processing Semantic Types for $rsab\n};
    print $fh qq{*Semantic Type Distribution|Semantic Type Id|Semantic Type Name|Total Count|Percentage Distribution\n};
    while (my (@row)  = $sh->fetchrow_array) {
    	printf $fh "%s\n", join("|",@row);
    	
    }
    print $fh qq{!\n};
	
}


sub getPrefNameSemanticTypes {
	
	my $rsab = shift;
	my $fh = shift;
	my $query = qq{
		WITH total_concepts AS
        (select  count(distinct concept_id) as source_concept_count from atoms where root_source = '$rsab')
        select c.ui, c.value, count(distinct a.concept_id), ROUND(count(distinct b.concept_id)/(select * from total_concepts)*100,1)
        from atoms a, concept_stys b, stys c, total_concepts
        where a.concept_id in(
        select distinct a.concept_id from atoms a, atom_clusters b, concepts c where a.id=b.default_pref_atom_id and a.root_source = '$rsab' and b.id=c.id
        )
        and a.concept_id = b.concept_id
        and b.sty_id = c.id
        group by c.ui, c.value
        order by count(distinct a.concept_id) desc

	};
	
	my $sh = $dbh->prepare($query) ||  die "Can't prepare statement: $DBI::errstr";
    $sh->execute || die "Cannot execute query\n";
    
    print qq{  Processing Preferred Name Semantic Types for $rsab\n};
    print $fh qq{*Preferred Name Semantic Type Distribution|Semantic Type Id|Semantic Type Name|Total Count|Percentage Distribution\n};
    while (my (@row)  = $sh->fetchrow_array) {
    	printf $fh "%s\n", join("|",@row);
    	
    }
    print $fh qq{!\n};
	
}

