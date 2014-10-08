#!C:\strawberry\perl\bin
 
use strict;
use warnings;
use REST::Client;
use Data::Dumper;
use File::Stat;

my $defFilename = 'value-set-definitions.txt';
my @columns;
my @definition;
my $oids;
my $def;
my @oid;
my @defParts;


open(OIDS, '>', "value-set-member-oids.txt");
open(DFILE, "<", $defFilename);
 while (my $lines = <DFILE>) {
	 @columns = split(/\|/, $lines);
		
		 @definition = $columns[3];
		 
		 foreach my $val(@definition){
		 		
		 		my $oidDefVal = $val;
		 		@defParts = split(/,/, $oidDefVal);
		 		
		 		foreach my $defPart(@defParts){
		 		
		 		chomp($defPart);
		 		($oids, $def) = split(/:/, $defPart);
		 		@oid = split(/\(/, $oids);
		 		splice(@oid, 0, 1);
		 		#print @oid;
		 		
		 		foreach (@oid){
		 				print OIDS "$_\n";	
		 			
		 		}
		 	}
		 	
		 
		 }

  }
  
  close DFILE;
  close OIDS;
  
		 

		 