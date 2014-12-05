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
my $oidList;
my @oid;
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
my $username;
my $password;
my $draft;
my $serviceTicket;
 
  #User enters username/password
 
  while (!my $uname){
  print "Enter the username:";
  $username = <STDIN>;
  chomp($username);
  if (($username eq "\n") || ($username eq "")){ #checking for blank inputs.
                print "Invalid input!!"."\n";
             
                } else {
                                last;
                                }
                                }
             
                while (!my $pw){
                  print "Enter the password:";
                  $password = <STDIN>;
                  chomp($password);
                  if (($password eq "\n") || ($password eq "")){ #checking for blank inputs.
                  print "Invalid input!!"."\n";
               
                  } else {
                  last;
                  }
                  }
             

 
my $filename = 'TicketGrantingTicket.txt';
 
if (-e $filename) {
 
print "Ticket Granting Ticket Exists!". "\n";
my $mtime = (stat $filename)[9];
my $current_time = time;
my $diff = $current_time - $mtime;
 
#print "\nTime difference: " . $diff . "\n";

 
if ($diff > 28800){  #Check if TGT is older than 8hrs.
 
print "Ticket Granting Ticket is > 8 hrs. Get new ticket.\n";
$response = $ua->post( $cas,{ username => $username, password => $password });
   # return { 'error' => $response->status_line } unless
   # $response->is_success;
   open(OUT, ">TicketGrantingTicket.txt");
   print OUT $response->{'_content'};
   close OUT;
 
}
 
} else {
 
print "Ticket Granting Ticket does not Exist!\n";
$response = $ua->post( $cas,{ username => $username, password => $password });
   # return { 'error' => $response->status_line } unless
   # $response->is_success;
   open(OUT, ">TicketGrantingTicket.txt");
   print OUT $response->{'_content'};
   close OUT;
 
}

          
	
	
				
#User input for filename with list of OIDs              
print "Enter the filename with the list of OIDs:"; 
$oidList = <>;
chomp($oidList);
#print $oidList;

 
#Main program for getting RetrieveValueSet or RetrieveMultipleValueSets.
while (!$method){
print "Enter a choice of method you want to run -> 1 for RetrieveValueSet, 2 for RetrieveMultipleValueSets :";
$choice = <>;
chomp $choice;
 
#For Retrieving Value Set
if ($choice eq "1") {
			                             
                while (!$rvsoption){
                print "Choose an option for the number of parameters: \n
                1= OID, Service Ticket\n
                2= OID, Service Ticket, Version\n
                3= OID, Service Ticket, Effective Date\n
                4= OID, Service Ticket, Version,  Effective Date\n
                5= OID, Service Ticket, Include Draft\n";
             
                $rvso = <>;
                chomp $rvso;
				

             
                if ($rvso eq "1"){

				open(OID, "<", $oidList);
				 while (my $lines = <OID>) {
				 @oid = split(/\n/, $lines);
				 foreach $oid(@oid){
				 chomp($oid);
				 $serviceTicket = serviceTicket($service, $cas);												

                                my $RVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveValueSet?id=';
                                $rclient->GET($RVS . $oid . '&ticket=' . $serviceTicket);
                                my $get_response = $rclient->responseContent();
								my $fname = "RetrieveValueSet_".$oid."_Default.xml";
                              
                                open(RVS, '>', $fname);
                                print RVS $get_response;
                                close RVS;
								}
							}
				close OID;
				last;
              
                } elsif ($rvso eq "2"){
                                                while (!$ver){
                                                                print "Enter the version:";
                                                                $version = <>;
                                                                chomp($version);
                                                                if (($version eq "\n") || ($version eq "")){ #checking for blank inputs.
                                                                print "Invalid input!!"."\n";
 
                                                                } else {
                                                                                last;
                                                                                }
                                                                                }
								open(OID, "<", $oidList);
								 while (my $lines = <OID>) {
								 @oid = split(/\n/, $lines);
								 foreach $oid(@oid){
								 chomp($oid);
								$serviceTicket = serviceTicket($service, $cas);												
								 
             
                                my $RVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveValueSet?id=';
                                $rclient->GET($RVS . $oid . '&version='. $version. '&ticket=' . $serviceTicket);
                                my $get_response = $rclient->responseContent();
                                my $fname = "RetrieveValueSet_".$oid."_version".$version.".xml";
                             
                                open(RVS, '>', $fname);          
                                print RVS $get_response;
                                close RVS;
									}
								}
					close OID;
					last;              
              
                } elsif ($rvso eq "3"){
                                                                while (!$eff){
                                                                print "Enter the Effective Date:";
                                                                $effDate = <>;
                                                                chomp($effDate);                                                              
                                                                if (($effDate eq "\n") || ($effDate eq "")){ #checking for blank inputs.
                                                                print "Invalid input!!"."\n";
 
                                                                } else {
                                                                                last;
                                                                                }
                                                                                }
																				
								open(OID, "<", $oidList);
								 while (my $lines = <OID>) {
								 @oid = split(/\n/, $lines);
								 foreach $oid(@oid){
								 chomp($oid);
								 $serviceTicket = serviceTicket($service, $cas);												

								 
                                my $RVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveValueSet?id=';
                                $rclient->GET($RVS . $oid . '&effectiveDate='. $effDate . '&ticket=' . $serviceTicket);
                                my $get_response = $rclient->responseContent();
                                my $fname = "RetrieveValueSet_effDate".$effDate.".xml";
                             
                                open(RVS, '>', $fname);                           
                                print RVS $get_response;
                                close RVS;
											}
										}
							close OID;
							last;              
             
              
                } elsif ($rvso eq "4"){
                                while (!$ver){
                                                               print "Enter the version:";
                                                                $version = <>;
                                                                chomp($version);                                                               
                                                                if (($version eq "\n") || ($version eq "")){ #checking for blank inputs.
                                                                print "Invalid input!!"."\n";
 
                                                                } else {
                                                                                last;
                                                                                }
                                                                                }
                                                                             
                                                                while (!$eff){
                                                                print "Enter the Effective Date:";
                                                                $effDate = <>;
                                                                chomp($effDate);
                                                                if (($effDate eq "\n") || ($effDate eq "")){ #checking for blank inputs.
                                                                print "Invalid input!!"."\n";
 
                                                                } else {
                                                                                last;
                                                                                }
                                                                                }
																				
								open(OID, "<", $oidList);
								 while (my $lines = <OID>) {
								 @oid = split(/\n/, $lines);
								 foreach $oid(@oid){
								 chomp($oid);
								$serviceTicket = serviceTicket($service, $cas);												
								 
             
                                my $RVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveValueSet?id=';
                                $rclient->GET($RVS . $oid . '&version='. $version. '&ticket=' . $serviceTicket);
                                my $get_response = $rclient->responseContent();
                                my $fname = "RetrieveValueSet_version".$version.".xml";
                              
                                open(RVS, '>', $fname);
                                print RVS $get_response;
                                close RVS;
												}
											}
								close OID;
								last;              
             
                                                  
                                } elsif ($rvso eq "5"){
                               
                                                                                                                                                                                                                                                                while (!$draft){
                                                                print "Include Draft? Enter Y/N:";
                                                                $draft = <>;
                                                                chomp($draft);                                                              
                                                                if (($draft eq "y") || ($draft eq "Y")){ #checking for blank inputs.
                                                                $draft = "yes";
																chomp($draft);
																
																} elsif (($draft eq "n") || ($draft eq "N")) {
																$draft = "no";
																chomp($draft);
																
                                                                } else {
																		print "Invalid input!!"."\n";
                                                                        last;
                                                                       }
                                                                    }
                               
							   open(OID, "<", $oidList);
								 while (my $lines = <OID>) {
								 @oid = split(/\n/, $lines);
								 foreach $oid(@oid){
								 chomp($oid);
								$serviceTicket = serviceTicket($service, $cas);												
								 
              
                                my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveValueSet?releaseType=VSAC';
                                $rclient->GET($RMVS . '&includeDraft=' . $draft. '&id='. $oid . '&ticket=' . $serviceTicket);
                                my $get_response = $rclient->responseContent();
                                my $fname = "RetrieveSet_draft_".$draft.".xml";
                             
                                open(RVS, '>', $fname);
                                print RVS $get_response;
                                close RVS;
												}
											}
								close OID;
								last;              
             
                                } else {
                             
                                print "Invalid input!!"."\n";
                                last;
                                }
                }
                last;
                             
 
   #For Retrieving Multiple Value Set
   } elsif ($choice eq "2") {
                                             
                while (!$rvsoption){
                print "Choose an option for the number of parameters: \n
                1= OID, Service Ticket\n
                2= OID, Service Ticket, Version\n
                3= OID, Service Ticket, Effective Date\n
                4= OID, Service Ticket, Version,  Effective Date\n
                5= OID, Service Ticket, CMSeMeasureId \n
                6= OID, Service Ticket, NQFNumber \n
                7= OID, Service Ticket, MeasureId \n
                8= OID, Service Ticket, MeasureId \n";
                  
                $rvso = <>;
                chomp $rvso;
				
             
                if ($rvso eq "1"){
				
								open(OID, "<", $oidList);
								 while (my $lines = <OID>) {
								 @oid = split(/\n/, $lines);
								 foreach $oid(@oid){
								 chomp($oid);
								$serviceTicket = serviceTicket($service, $cas);												
								 
                                my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?id=';
                                $rclient->GET($RMVS . $oid . '&ticket=' . $serviceTicket);
                                my $get_response = $rclient->responseContent();
                             
                                open(RMVS, ">RetrieveMultipleValueSetsDefault.xml");
                                print RMVS $get_response;
                                close RMVS;
									}
								}
					close OID;
					last;                           
              
                } elsif ($rvso eq "2"){
                                                while (!$ver){
                                                                print "Enter the version:";
                                                                $version = <>;
                                                                chomp($version);
                                                                if (($version eq "\n") || ($version eq "")){ #checking for blank inputs.
                                                                print "Invalid input!!"."\n";
 
                                                                } else {
                                                                                last;
                                                                                }
                                                                                }
																				
							open(OID, "<", $oidList);
								 while (my $lines = <OID>) {
								 @oid = split(/\n/, $lines);
								 foreach $oid(@oid){
								 chomp($oid);
								$serviceTicket = serviceTicket($service, $cas);												
              
                                my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?id=';
                                $rclient->GET($RMVS . $oid . '&version='. $version. '&ticket=' . $serviceTicket);
                                my $get_response = $rclient->responseContent();
                                my $fname = "RetrieveMultipleValueSets_version".$version.".xml";
                             
                                open(RMVS, '>', $fname);
                                print RMVS $get_response;
                                close RMVS;
										}
									}
						close OID;
						last;              
             
              
                } elsif ($rvso eq "3"){
                                                                while (!$eff){
                                                                print "Enter the Effective Date:";
                                                                $effDate = <>;
                                                                chomp($effDate);
                                                                if (($effDate eq "\n") || ($effDate eq "")){ #checking for blank inputs.
                                                                print "Invalid input!!"."\n";
 
                                                                } else {
                                                                                last;
                                                                                }
                                                                                }
																				
								open(OID, "<", $oidList);
								 while (my $lines = <OID>) {
								 @oid = split(/\n/, $lines);
								 foreach $oid(@oid){
								 chomp($oid);
								$serviceTicket = serviceTicket($service, $cas);												
								 
                                my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?id=';
                                $rclient->GET($RMVS . $oid . '&effectiveDate='. $effDate . '&ticket=' . $serviceTicket);
                                my $get_response = $rclient->responseContent();
                                my $fname = "RetrieveMultipleValueSets_effDate".$effDate.".xml";
                             
                                open(RMVS, '>', $fname);
                                print RMVS $get_response;
                                close RMVS;
												}
											}
								close OID;
								last;                           
              
                } elsif ($rvso eq "4"){
                                while (!$ver){
                                                                print "Enter the version:";
                                                                $version = <>;    
                                                                chomp($version);
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
								
								open(OID, "<", $oidList);
								 while (my $lines = <OID>) {
								 @oid = split(/\n/, $lines);
								 foreach $oid(@oid){
								 chomp($oid);
								$serviceTicket = serviceTicket($service, $cas);												
             
                                my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?id=';
                                $rclient->GET($RMVS . $oid . '&version='. $version. '&ticket=' . $serviceTicket);
                                my $get_response = $rclient->responseContent();
                                my $fname = "RetrieveMultipleValueSets_version".$version.".xml";
                             
                                open(RMVS, '>', $fname);
                                print RMVS $get_response;
                                close RMVS;
												}
											}
								close OID;
								last;              
                             
                } elsif ($rvso eq "5"){
                                while (!$ver){
                                                                print "Enter the CMSeMeasureId:";
                                                                $cms = <>;
                                                                chomp($cms);                                                              
                                                                if (($cms eq "\n") || ($cms eq "")){ #checking for blank inputs.
                                                                print "Invalid input!!"."\n";
 
                                                                } else {
                                                                                last;
                                                                                }
                                                                                }
                                                                             
              
								open(OID, "<", $oidList);
								 while (my $lines = <OID>) {
								 @oid = split(/\n/, $lines);
								 foreach $oid(@oid){
								 chomp($oid);
								$serviceTicket = serviceTicket($service, $cas);												
			  
                                my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?';
                                $rclient->GET($RMVS . 'cmsemeasureid='. $cms. '&ticket=' . $serviceTicket);
                                my $get_response = $rclient->responseContent();
                                my $fname = "RetrieveMultipleValueSets_cms".$cms.".xml";
                             
                                open(RMVS, '>', $fname);
                                print RMVS $get_response;
                                close RMVS;
												}
											}
								close OID;
								last;                                           
                              
                } elsif ($rvso eq "6"){
                                while (!$ver){
                                                                print "Enter the NQFNumber:";
                                                                $nqfNum = <>;
                                                                chomp($nqfNum);
                                                                if (($nqfNum eq "\n") || ($nqfNum eq "")){ #checking for blank inputs.
                                                                print "Invalid input!!"."\n";
 
                                                                } else {
                                                                                last;
                                                                                }
                                                                                }
                                
								open(OID, "<", $oidList);
								 while (my $lines = <OID>) {
								 @oid = split(/\n/, $lines);
								 foreach $oid(@oid){
								 chomp($oid);
								$serviceTicket = serviceTicket($service, $cas);												
              
                                my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?';
                                $rclient->GET($RMVS . 'NQFNumber='. $nqfNum. '&ticket=' . $serviceTicket);
                                my $get_response = $rclient->responseContent();
                                my $fname = "RetrieveMultipleValueSets_NQF".$nqfNum.".xml";
                             
                                open(RMVS, '>', $fname);
                                print RMVS $get_response;
                                close RMVS;
												}
											}
								close OID;
								last;              
									  
                              
                } elsif ($rvso eq "7"){
                                while (!$ver){
                                                                print "Enter the MeasureId:";
                                                                $measureId = <>;
                                                                chomp($measureId);                                                              
                                                                if (($measureId eq "\n") || ($measureId eq "")){ #checking for blank inputs.
                                                                print "Invalid input!!"."\n";
 
                                                                } else {
                                                                                last;
                                                                                }
                                                                                }
                                                                              
              
                                
								open(OID, "<", $oidList);
								 while (my $lines = <OID>) {
								 @oid = split(/\n/, $lines);
								 foreach $oid(@oid){
								 chomp($oid);
								$serviceTicket = serviceTicket($service, $cas);												
								 
								my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?';
                                $rclient->GET($RMVS . 'measureid='. $measureId. '&ticket=' . $serviceTicket);
                                my $get_response = $rclient->responseContent();
                                my $fname = "RetrieveMultipleValueSets_MeasureId".$measureId.".xml";
                             
                                open(RMVS, '>', $fname);
                                print RMVS $get_response;
                                close RMVS;
											}
											}
								close OID;
								last;              
 
                } elsif ($rvso eq "8"){
                               
                                                                while (!$ver){                                                                                                                                                                                               while (!$draft){
                                                                print "Include Draft? Enter Y/N:";
                                                                $draft = <>;
                                                                chomp($draft);                                                              
                                                                if (($draft eq "y") || ($draft eq "Y")){ #checking for blank inputs.
                                                                $draft = "yes";
																chomp($draft);
																
																} elsif (($draft eq "n") || ($draft eq "N")) {
																$draft = "no";
																chomp($draft);
																
                                                                } else {
																		print "Invalid input!!"."\n";
                                                                        last;
                                                                       }
                                                                    } }
                                
								open(OID, "<", $oidList);
								 while (my $lines = <OID>) {
								 @oid = split(/\n/, $lines);
								 foreach $oid(@oid){
								 chomp($oid);
								$serviceTicket = serviceTicket($service, $cas);												
              
                                my $RMVS = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?releaseType=VSAC';
                                $rclient->GET($RMVS . '&includeDraft=' . $draft. '&id='. $oid . '&ticket=' . $serviceTicket);
                                my $get_response = $rclient->responseContent();
                                my $fname = "RetrieveMultipleValueSets_draft_".$draft.".xml";
                             
                                open(RMVS, '>', $fname);
                                print RMVS $get_response;
                                close RMVS;
												}
											}
								close OID;
								last;              
             
                                } else {
 
                                print "Invalid input!!"."\n";
                                last;
                                }
								
			}
			last;
			} else {
 
                    print "Invalid input!!"."\n";
                    last;
                    }
			}
			
#Function for generating Service Ticket			
sub serviceTicket{
 #Reading TGT from txt file to produce ST
  open(READ, "<TicketGrantingTicket.txt");
  my $ticket = <READ>;
  my $serv = $_[0];
  my $cast = $_[1];
  my $resp = $ua->post( $cast . '/' . $ticket,{ service => $serv });
  # return { 'error' => $response->status_line } unless
  # $response->is_success;
  my $servTicket = $resp->{'_content'};
  #print $servTicket."\n";
  close READ;
  return $servTicket;
   }