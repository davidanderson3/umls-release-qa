#!/usr/bin/perl

##Version 0.2
##Updates
# Add ability to retrieve measure and QDM usage (via value-set-measure-counts.xsl)
# Add ability to ouptut pure xml (use the 'SVS XML' report option)

##Usage Notes
# create a directory in your $HOME called 'svs'
# to start the client: perl vsac-svs-api.pl -u username -p password
# output will write to your $HOME/svs directory to the filename you specifiy
# Only use mode RetrieveMultipleValueSets for now
# Only use Batch OID or Single Use.  Measure Mode is not yet implemented.

use strict;
use warnings;
use LWP::UserAgent;
use URI;
use Getopt::Std;
use XML::LibXML;
use XML::Saxon::XSLT2;
my $windows=($^O=~/Win/)?1:0;
my $outputdir;
my $outputfile;
if(! $windows){$outputdir = "$ENV{'HOME'}/svs";} else{$outputdir = "$ENV{'USERPROFILE'}/svs";}
my $uri = URI->new("https://vsac.nlm.nih.gov");
my $service = URI->new("http://umlsks.nlm.nih.gov");
our ($opt_u,$opt_p);
getopt('up');
my $username = $opt_u || die "please provide username";
my $password = $opt_p || die "please provide password";
my $ua = LWP::UserAgent->new;
my $tgt = &getTgt();
my $st;
my $mode;
my $method;
my $path; #retrieveValueSet or retrieveMultipleValueSets
my $oid;
my @oids;
my %base_parameters;
my %buildAdditionalParameters;
my %additional_parameters;
my $additional_parameters_ref = \%additional_parameters;
my @additional_parameters = ("effectiveDate","version","tagName","tagValue","profile","includeDraft");
my %reports = ("SVS XML"=>"none","Measure Counts" => "value-set-measure-counts.xsl","Value Set Codes" => "value-set-codes.xsl","Value Set Definitions"=>"value-set-definitions.xsl","Code Counts"=>"value-set-code-counts.xsl");
my @report_choices = keys(%reports);
my @responses;
my $stylesheet;
my $responses_ref = \@responses;
my $parser = XML::LibXML->new;


$mode = chooseMode();
$path = choosePath($mode);
$stylesheet = getStyleSheet();
$additional_parameters_ref = buildAdditionalParameters();
open(ERR,">$outputdir/error.txt") || die "could not open error file$!\n";

if ($mode eq "1"){
	
	print "Enter your file containing a list of OIDs, one per line:\n";
	my $file = <>;
	chomp($file);
	print "Enter the name of the output file:\n";
	$outputfile = <>;
	chomp($outputfile);
	open FH, $file || die "could not open input $!";
    while (<FH>) {
    chomp($_);
    my $oid = $_;
    if(&isValid("oid",$oid) ne "true") {print "OID is invalid - exiting"; exit 1;}
    push(@oids, $oid);
  
    }
    close(FH);
	$responses_ref = executeQuery();
}


elsif($mode eq "2"){
	
    
	print "Please enter an OID: \n";
	$oid = <>;
	chomp $oid;
	if(&isValid("oid",$oid) ne "true") {print "OID is invalid - exiting"; exit 1;}
	print "Enter the name of the output file:\n";
	$outputfile = <>;
	chomp($outputfile);
	push(@oids,$oid);
	$responses_ref = executeQuery();
}



elsif ($mode eq "3") {
	
	print "Enter the name of the output file:\n";
	$outputfile = <>;
	chomp($outputfile);
	$responses_ref = executeQuery();
	 
}

open(OUT,">$outputdir/$outputfile") || die "cannot open output file$!";

for my $response(@responses) {
	if ($stylesheet eq "none") {print OUT $response;}
	else {
	my $xslt = $parser->load_xml( location => $stylesheet );
	my $dom = $parser->load_xml( string => $response );
	my $transformation = XML::Saxon::XSLT2->new($xslt);
	my $output = $transformation->transform( $dom, 'text' );
	print OUT $output;
	}
}
close(OUT);
close(ERR);

sub getTgt{
	
	$uri->path("/vsac/ws/Ticket");
	$uri->query_form(username=>$username,password=>$password);
	my $query = $ua->post($uri) || die "could not obtain tgt $!\n";
    $tgt = $query->{'_content'};
    return $tgt;
    
   
} ## end getTgt

sub getSingleUseTicket{
	my $tgt = shift;
	$uri->path("/vsac/ws/Ticket/".$tgt);
	$uri->query_form(service=>$service);
	#print qq{$uri};
	my $query = $ua->post($uri) || die "could not obtain single-use ticket $!\n";
	my $ticket = $query->{'_content'};
	return $ticket;

} ## end getSingleUseTicket


##choose how to run the client
sub chooseMode{
	
 print "Enter the mode -> 1 for Batch OID Mode, 2 for Single Use OID Mode, 3 for Measure Mode :";
 $mode = <>;
 chomp $mode;
 return $mode;
	
} ## end chooseMode

sub choosePath{
	
  $mode = shift;
  if($mode eq "1" || $mode eq "2"){
  	
  print "Please choose: 1 for RetrieveValueSet or 2 for RetrieveMultipleValueSets";
  $method = <>;
  chomp $method;
  
  if ($method eq "1") {$path = "RetrieveValueSet";} elsif($method eq "2"){$path = "RetrieveMultipleValueSets";} else{print "Invalid entry - exiting";exit 1;}

  }
  
  elsif($mode eq "3") {$path = "RetrieveMultipleValueSets";}
  else {print "Invalid entry - exiting"; exit 1;}
  return $path;
} ## end choosePath


sub buildAdditionalParameters{

     for my $key (sort @additional_parameters) {
     	
     	print "Enter a value for $key, or hit the Enter key to skip to the next parameter:\n";
     	my $parameter_value = <>;
     	chomp $parameter_value;
     	next if $parameter_value =~ /^$/;
     	#if($parameter_value !~/^$/) {$additional_parameters_ref->{$key} = $parameter_value;}
     	if (&isValid($key,$parameter_value) ne "false") {$additional_parameters_ref->{$key} = $parameter_value;}
     	else{print qq{invalid entry for $key, exiting}; exit 1;}
     	
     }

     return $additional_parameters_ref;

} ##end buildAdditionalParameters


sub executeQuery {
	
	my $response;
	## are we dealing with OID-based calls or not?  
	if (scalar(@oids) > 0) {
	 
	  foreach my $oid(@oids){
	  
	  $st = getSingleUseTicket($tgt);
      $uri->path("/vsac/svs/".$path);
      $base_parameters{id} = $oid;
      $base_parameters{ticket} = $st;
      
      my %final_parameters = (%base_parameters,%additional_parameters);
      $uri->query_form(\%final_parameters);
      print qq{$uri\n};
      my $query = $ua->get($uri);
      if ($query->is_success){$response = $query->{'_content'};push(@responses,$response);} 
      else {print ERR $oid."|".$query->status_line."\n";}

	  } ## end foreach
	  
	  return @responses;
	  
    } ## endif
	
	else {
		
	  $st = getSingleUseTicket($tgt);
      $uri->path("/vsac/svs/".$path);
      $base_parameters{ticket} .= $st;
      my %final_parameters = (%base_parameters,%additional_parameters);
      $uri->query_form(\%final_parameters);
      print qq{$uri\n};
      my $query = $ua->get($uri);
      if ($query->is_success){$response = $query->{'_content'};} else {print "could not execute query for $oid\n";}
      push(@responses,$response);
	  	
	}##end else
	return @responses;
	
} ## end executeQuery


##validation for entered values such as OIDs and other parameters for security reasons
sub isValid {
	
	my($parameter,$value) = @_;
	
	if($parameter eq "oid" && $value =~ /^[0-9\.]+(\.[0-9]+)$/) {return "true";}
	elsif($parameter eq "effectiveDate" && $value =~ /2{1}0{1}[0-9]{2}[0-1]{1}[1-9]{1}[0-3]{1}[0-9]{1}/) {return "true";}
	elsif($parameter eq "includeDraft" && (lc($value) eq "yes" || lc($value) eq "no")) {return "true";}
	elsif(($parameter eq "version" || $parameter eq "tagName" || $parameter eq "tagValue" || $parameter eq "profile")  && $value =~ /[0-9a-zA-Z]{2,25}/) {return "true";}
	else {return "false";}

} ## end isValid

sub getStyleSheet {
	
	print "Please choose report format: ". join(',',@report_choices),"\n";
	my $report_choice = <>;
	chomp($report_choice);
	if(exists($reports{$report_choice})) {$stylesheet = $reports{$report_choice};} else{print "invalid entry, exiting"; exit 1;}
	return $stylesheet;
}

	