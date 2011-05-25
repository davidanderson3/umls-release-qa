#!/usr/bin/perl
#use Getopt::Std;
#getopts("i:");
use warnings;
$base_dir = `echo \$SOURCEDOC_HOME` || die "please set your SOURCEDOC_HOME variable in your environment\n";

chdir "$base_dir"."xml" || die "cannot change directory $!\n";


%output_hash = (
	"ttysamples.xml"=>"termtypes.html",
	"atnsamples.xml"=>"attributes.html",
	"relsamples.xml"=>"relationships.html",
	"stysamples.xml"=>"semtypes.html",
	"overlapsamples.xml"=>"overlap.html",
	"mrsab.xml"=>"mrsab.html"	
	);



@rsabs =  `ls`;
if (scalar(@rsabs) < 1){
	
	die "no directories to read";
}


foreach $dir(@rsabs) {
	print qq{ outputting $dir };	
&getHTML($dir);	

	
	
}


sub getHTML($dir) {
	
my $dir = shift;
chomp($dir);

chdir "$dir" or die "$!\n";

my @all_files = <*.xml>;

foreach $file(@all_files) {
	
	
	if ($file =~ /tty/ || $file =~ /atn/ || $file =~ /rel/ || $file =~ /sty/ || $file =~ /overlap/ ) {
	
	$xsl = $base_dir."scripts/sourcestats.xsl";
	
	$output = &chooseOutput($file);
	
	open (FH, ">$output") or die "could not open $output for writing\n";
	$stdout = `\$JAVA_HOME\/bin\/java -cp \$SOURCEDOC_HOME\/lib\/saxon\/saxon9he.jar net.sf.saxon.Transform -s:$file -xsl:$xsl` || die "could not create html $!\n";
	print FH "$stdout";
	close FH;
		
	}
		
	else {
		
	$xsl = $base_dir."scripts/mrsab.xsl";	
	$output = &chooseOutput($file);
	
	open (FH, ">mrsab.html") or die "could not open $output for writing\n";
	$stdout = `\$JAVA_HOME\/bin\/java -cp \$SOURCEDOC_HOME\/lib\/saxon\/saxon9he.jar -s:$file -xsl:$xsl` || die "could not create html $!\n";
	print FH "$stdout";
	close FH;
	
	}
	
	
	#print "$file, $xsl\n";
	
	
		
	
}




chdir "../";
	
}

sub chooseOutput($input) {
	
	
my $input = shift;

 #print "$input\n";
 foreach $xmldocs (sort keys %output_hash) {
   
	 if ($input =~ /$xmldocs/) {
	 $output = $output_hash{$xmldocs};
	 }
  }

return $output;
	
	
}
