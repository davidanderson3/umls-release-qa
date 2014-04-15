#!C:\strawberry\perl\bin

use strict;
use warnings;
use REST::Client;
use LWP::UserAgent;
use Data::Dumper;
use File::Stat;
use Time::localtime;
use Time::gmtime;
use Time::tm;
use DateTime::Format::Strptime;

my $ua = LWP::UserAgent->new;
my $rclient = REST::Client->new();

my $cas = 'https://vsac.nlm.nih.gov/vsac/ws/Ticket';
my $dp = DateTime::Format::Strptime->new(pattern => '%a %b %d %H:%M:%S %Y');
my $service = 'http://umlsks.nlm.nih.gov'; 
my $response;
my $oid;
my $rvso;
my $choice;
my $method;
my $rvsoption;
my $version;
my $ver;
my $effDate;
my $eff;
my $cms;
my $nqfNum;
my $measureId;
my $MU;
# my $username;
# my $password;

my $username = 'debjaniani';
my $password = 'Cartoon123!';
  #User enters username/password
  
  # while (!my $uname){
  # print "Enter the username:";
  # $username = <STDIN>;
  # if (($username eq "\n") || ($username eq "")){ #checking for blank inputs.
	# print "Invalid input!!"."\n";
	
	# } else {
		# last;
		# }
		# }
	
	 # while (!my $pw){
	  # print "Enter the password:";
	  # $password = <STDIN>;
	  # if (($password eq "\n") || ($password eq "")){ #checking for blank inputs.
	  # print "Invalid input!!"."\n";
	  
	  # } else {
	  # last;
	  # }
	  # }
	 
  # print $username."\n";
  # print $password."\n";

  # open(OUT, ">tgt.txt");

# print OUT %{$lwp->request( $req )};

# close OUT;


my $filename = 'tgt.txt';

if (-e $filename) {

print "TGT Exists!". "\n";
my $mtime = (stat $filename)[9];
my $current_time = time;
my $diff = $current_time - $mtime;

print "\nTime difference: " . $diff . "\n";
#print $date_string."\n";


if ($diff > 28){

print "TGT > 8 hrs. Get new ticket.\n";
$response = $ua->post( $cas,{ username => $username, password => $password });
   # return { 'error' => $response->status_line } unless
   # $response->is_success;
   open(OUT, ">tgt.txt");
   print OUT $response->{'_content'};
   close OUT;

}

} else {

print "TGT does not Exist!\n";
$response = $ua->post( $cas,{ username => $username, password => $password });
   # return { 'error' => $response->status_line } unless
   # $response->is_success;
   open(OUT, ">tgt.txt");
   print OUT $response->{'_content'};
   close OUT;

}


  # Get the TGT.
   # my $response = $ua->post( $cas,{ username => $username, password => $password });
   # return { 'error' => $response->status_line } unless
   # $response->is_success;
   # print $response->{'_content'};
# while ( my ($key, $value) = each(%$response) ) {
        # print "$key => $value\n";
    # }
   

  open(READ, "<tgt.txt");
  my $ticket = <READ>;  
  $response = $ua->post( $cas . '/' . $ticket,{ service => $service });
  # return { 'error' => $response->status_line } unless
  # $response->is_success;
  my $serviceTicket = $response->{'_content'};
    print $serviceTicket."\n";
	close READ;
	
	
my $uoid;

# while (!$uoid){
# print "Enter the OID:";
# $oid = <>;
# if (($oid eq "\n") || ($oid eq "")){ #checking for blank inputs.
# print "Invalid input!!"."\n";

# } else {
	# last;
	# }
	# }
	
	#$oid = '2.16.840.1.113883.3.666.5.1738';
	#Grouping OID containing 5 member value sets:  id=2.16.840.1.113883.3.600.1.1525
	#Extensional (non-grouping OID) value set: id=2.16.840.1.113883.3.600.1.1523
	#measureid:  CMS69v2
	#NQFNumber:  0421
	


while (!$method){
print "Enter a choice of method you want to run -> 1 for RetrieveValueSet, 2 for RetrieveMultipleValueSets :";
$choice = <>;
chomp $choice;

#For Retrieve Value Set
if ($choice eq "1") {

	while (!$uoid){
	print "Enter the OID:";
	$oid = <>;
	if (($oid eq "\n") || ($oid eq "")){ #checking for blank inputs.
	print "Invalid input!!"."\n";

	} else {
		last;
		}
		}
		
	while (!$rvsoption){
	print "Choose an option for the number of parameters: \n 
	1= OID, Service Ticket\n 
	2= OID, Service Ticket, Version\n 
	3= OID, Service Ticket, Effective Date\n 
	4= OID, Service Ticket, Version,  Effective Date\n";
	
	$rvso = <>;
	chomp $rvso;
	
	if ($rvso eq "1"){
		my $RVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveValueSet?id=';
		$rclient->GET($RVS . $oid . '&ticket=' . $serviceTicket);
		my $get_response = $rclient->responseContent();
		
		open(RVS, ">rvs.xml");
		print RVS $get_response;
		close RVS;
		last;
	
	
	} elsif ($rvso eq "2"){
			while (!$ver){
				print "Enter the version:";
				$version = <>;
				if (($version eq "\n") || ($version eq "")){ #checking for blank inputs.
				print "Invalid input!!"."\n";

				} else {
					last;
					}
					}
	
		my $RVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveValueSet?id=';
		$rclient->GET($RVS . $oid . '&version='. $version. '&ticket=' . $serviceTicket);
		my $get_response = $rclient->responseContent();
		
		open(RVS, ">rvs_ver.xml");
		print RVS $get_response;
		close RVS;
		last;
	
	
	} elsif ($rvso eq "3"){
				while (!$eff){
				print "Enter the Effective Date:";
				$effDate = <>;
				if (($effDate eq "\n") || ($effDate eq "")){ #checking for blank inputs.
				print "Invalid input!!"."\n";

				} else {
					last;
					}
					}
		my $RVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveValueSet?id=';
		$rclient->GET($RVS . $oid . '&effectiveDate='. $effDate . '&ticket=' . $serviceTicket);
		my $get_response = $rclient->responseContent();
		
		open(RVS, ">rvs_effDt.xml");
		print RVS $get_response;
		close RVS;
		last;
	
	
	} elsif ($rvso eq "4"){
		while (!$ver){
				print "Enter the version:";
				$version = <>;				
				if (($version eq "\n") || ($version eq "")){ #checking for blank inputs.
				print "Invalid input!!"."\n";

				} else {
					last;
					}
					}
					
				while (!$eff){
				print "Enter the Effective Date:";
				$effDate = <>;
				if (($effDate eq "\n") || ($effDate eq "")){ #checking for blank inputs.
				print "Invalid input!!"."\n";

				} else {
					last;
					}
					}
	
		my $RVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveValueSet?id=';
		$rclient->GET($RVS . $oid . '&version='. $version. '&ticket=' . $serviceTicket);
		my $get_response = $rclient->responseContent();
		
		open(RVS, ">rvs_verED.xml");
		print RVS $get_response;
		close RVS;
		last;
	
		} else {
		
		print "Invalid input!!"."\n";
		last;
		}
	
	}
	last;

		
   
   #For Retrieve Multiple Value Set
   } elsif ($choice eq "2") {
   
	  while (!$uoid){
		print "Enter the OID:";
		$oid = <>;
		if (($oid eq "\n") || ($oid eq "")){ #checking for blank inputs.
		print "Invalid input!!"."\n";

		} else {
			last;
			}
			}
			
	while (!$rvsoption){
	print "Choose an option for the number of parameters: \n 
	1= OID, Service Ticket\n 
	2= OID, Service Ticket, Version\n 
	3= OID, Service Ticket, Effective Date\n 
	4= OID, Service Ticket, Version,  Effective Date\n
	5= OID, Service Ticket, CMSeMeasureId \n 
	6= OID, Service Ticket, NQFNumber \n 
	7= OID, Service Ticket, MeasureId \n ";
	
	
	$rvso = <>;
	chomp $rvso;
	
	if ($rvso eq "1"){
		my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?id=';
		$rclient->GET($RMVS . $oid . '&ticket=' . $serviceTicket);
		my $get_response = $rclient->responseContent();
		
		open(RMVS, ">rmvs.xml");
		print RMVS $get_response;
		close RMVS;
		last;
	
	
	} elsif ($rvso eq "2"){
			while (!$ver){
				print "Enter the version:";
				$version = <>;
				if (($version eq "\n") || ($version eq "")){ #checking for blank inputs.
				print "Invalid input!!"."\n";

				} else {
					last;
					}
					}
	
		my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?id=';
		$rclient->GET($RMVS . $oid . '&version='. $version. '&ticket=' . $serviceTicket);
		my $get_response = $rclient->responseContent();
		
		open(RMVS, ">rmvs_ver.xml");
		print RMVS $get_response;
		close RMVS;
		last;
	
	
	} elsif ($rvso eq "3"){
				while (!$eff){
				print "Enter the Effective Date:";
				$effDate = <>;
				if (($effDate eq "\n") || ($effDate eq "")){ #checking for blank inputs.
				print "Invalid input!!"."\n";

				} else {
					last;
					}
					}
		my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?id=';
		$rclient->GET($RMVS . $oid . '&effectiveDate='. $effDate . '&ticket=' . $serviceTicket);
		my $get_response = $rclient->responseContent();
		
		open(RMVS, ">rmvs_effDt.xml");
		print RMVS $get_response;
		close RMVS;
		last;
	
	
	} elsif ($rvso eq "4"){
		while (!$ver){
				print "Enter the version:";
				$version = <>;				
				if (($version eq "\n") || ($version eq "")){ #checking for blank inputs.
				print "Invalid input!!"."\n";

				} else {
					last;
					}
					}
					
				while (!$eff){
				print "Enter the Effective Date:";
				$effDate = <>;
				if (($effDate eq "\n") || ($effDate eq "")){ #checking for blank inputs.
				print "Invalid input!!"."\n";

				} else {
					last;
					}
					}
	
		my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?id=';
		$rclient->GET($RMVS . $oid . '&version='. $version. '&ticket=' . $serviceTicket);
		my $get_response = $rclient->responseContent();
		
		open(RMVS, ">rmvs_verED.xml");
		print RMVS $get_response;
		close RMVS;
		last;
		
	} elsif ($rvso eq "5"){
		while (!$ver){
				print "Enter the CMSeMeasureId:";
				$cms = <>;				
				if (($cms eq "\n") || ($cms eq "")){ #checking for blank inputs.
				print "Invalid input!!"."\n";

				} else {
					last;
					}
					}
					
	
		my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?';
		$rclient->GET($RMVS . 'cmsemeasureid='. $cms. '&ticket=' . $serviceTicket);
		my $get_response = $rclient->responseContent();
		
		open(RMVS, ">rmvs_cms.xml");
		print RMVS $get_response;
		close RMVS;
		last;
		
		
	} elsif ($rvso eq "6"){
		while (!$ver){
				print "Enter the NQFNumber:";
				$nqfNum = <>;				
				if (($nqfNum eq "\n") || ($nqfNum eq "")){ #checking for blank inputs.
				print "Invalid input!!"."\n";

				} else {
					last;
					}
					}
					
	
		my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?';
		$rclient->GET($RMVS . 'NQFNumber='. $nqfNum. '&ticket=' . $serviceTicket);
		my $get_response = $rclient->responseContent();
		my $fname = ">rmvs_NQF".$nqfNum.".xml";
		
		open(RMVS, $fname);
		print RMVS $get_response;
		close RMVS;
		last;
		
	} elsif ($rvso eq "7"){
		while (!$ver){
				print "Enter the MeasureId:";
				$measureId = <>;				
				if (($measureId eq "\n") || ($measureId eq "")){ #checking for blank inputs.
				print "Invalid input!!"."\n";

				} else {
					last;
					}
					}
					
	
		my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?';
		$rclient->GET($RMVS . 'measureid='. $measureId. '&ticket=' . $serviceTicket);
		my $get_response = $rclient->responseContent();
		
		open(RMVS, ">rmvs_msrId.xml");
		print RMVS $get_response;
		close RMVS;
		last;

   
	} else {

		print "Invalid input!!"."\n";
		last;
		}
	}
	last;
	}}
	
