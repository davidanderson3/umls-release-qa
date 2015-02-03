#!C:\strawberry\perl\bin
 
use strict;
use warnings;
use LWP::UserAgent;
use URI;
use Getopt::Std;
use XML::LibXML;

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
my %additional_parameters =  ( effectiveDate=>"",version=>"",tagName=>"",tagValue=>"",profile=>"",includeDraft=>"" );
my $additional_parameters_ref = \%additional_parameters;
my @responses;
my $responses_ref = \@responses;
#my $st = &getSingleUseTicket($tgt);
#print qq{$st};

$mode = chooseMode();
$path = choosePath($mode);
$additional_parameters_ref = buildAdditionalParameters();

if($mode eq "2"){
	
	print "Please enter an OID: \n";
	$oid = <>;
	chomp $oid;
	push(@oids,$oid);
	$responses_ref = executeQuery();
}
elsif ($mode eq "3") {
	
	$responses_ref = executeQuery();
	 
}

for my $response(@responses) {
	print qq{$response}
}

sub getTgt{
	
	$uri->path("/vsac/ws/Ticket");
	$uri->query_form(username=>$username,password=>$password);
	my $query = $ua->post($uri) || die "could not obtain tgt $!\n";
    $tgt = $query->{'_content'};
    return $tgt;
    
   
}

sub getSingleUseTicket{
	my $tgt = shift;
	$uri->path("/vsac/ws/Ticket/".$tgt);
	$uri->query_form(service=>$service);
	#print qq{$uri};
	my $query = $ua->post($uri) || die "could not obtain single-use ticket $!\n";
	my $ticket = $query->{'_content'};
	return $ticket;
}


##choose how to run the client
sub chooseMode{
	
 print "Enter the mode -> 1 for Batch OID Mode, 2 for Single Use OID Mode, 3 for Measure Mode :";
 $mode = <>;
 chomp $mode;
 return $mode;
	
}

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
}


sub buildAdditionalParameters{

     my @keys = keys(%$additional_parameters_ref);
     my $joined = join(", ", @keys);
    
     for my $key (sort @keys) {
     	
     	print "Enter a value for $key, or hit the Enter key to skip to the next parameter:\n";
     	my $parameter_value = <>;
     	chomp $parameter_value;
     	if($parameter_value !~/^$/) {$additional_parameters_ref->{$key} = $parameter_value;}
     	next if $parameter_value =~ /^$/;
     	
     	
     }
     
     $additional_parameters_ref = &cleanParameters();
     return $additional_parameters_ref;

}


sub cleanParameters {
	
	foreach my $key(keys %additional_parameters) {
		if($additional_parameters{$key} eq "" ) {delete $additional_parameters{$key};}
	}
	return %additional_parameters;
}
##end cleanParamters


sub executeQuery {
	
	 my $response;
	if (scalar(@oids) > 0) {
	 
	  foreach my $oid(@oids){
	  #print qq{$oid};
	  
	  $st = getSingleUseTicket($tgt);
      $uri->path("/vsac/svs/".$path);
      $base_parameters{id} .= $oid;
      $base_parameters{ticket} .= $st;
      
      my %final_parameters = (%base_parameters,%additional_parameters);
      $uri->query_form(\%final_parameters);
    
       print qq{$uri\n};
      #my $query = $ua->get($uri);
      #if ($query->is_success){$response = $query->{'_content'};push(@responses,$response);print qq{$response};} else {die "could not execute query on $oid\n";}

	  } ## end foreach
	  
	  #return @responses;
	  
    } ## endif
	
	else {
		
	  $st = getSingleUseTicket($tgt);
      $uri->path("/vsac/svs/".$path);
      $base_parameters{ticket} .= $st;
      my %final_parameters = (%base_parameters,%additional_parameters);
      $uri->query_form(\%final_parameters);
      #print qq{$uri\n};
      my $query = $ua->get($uri);
      if ($query->is_success){$response = $query->{'_content'};} else {print "could not execute query for $oid\n";}
      push(@responses,$response);
		
	}##end else
	return @responses;
	
}
## end executeQuery


	