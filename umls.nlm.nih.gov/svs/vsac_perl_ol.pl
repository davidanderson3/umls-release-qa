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
my $oidFile;
my $rvso;
my $choice;
my $modechoice;
my $chooseoid;
my $mode;
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
my $rvs_url = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveValueSet?id='; 
my $rmvs_url = 'https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?id='; 
my $servTic;
my $retrievingValueSet;
 
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

          
    
    
                

#print $oidList;

 #Option for user to choose to run batch or single use mode
 while (!$mode){
 print "Enter the mode -> 1 for Batch Mode, 2 for Single Use Mode :";
$modechoice = <>;
chomp $modechoice;

if ($modechoice eq "1"){
#User input for filename with list of OIDs              
print "Enter the filename with the list of OIDs:"; 
$oidFile = <>;
chomp($oidFile);
my @oid_arr;
open(OID, "<", $oidFile);
 while (my $lines = <OID>) {
  chomp($lines);
  push(@oid_arr, $lines);
  
 }

$retrievingValueSet = retrievingValueSet($rvs_url, $rmvs_url, $serviceTicket, \@oid_arr);
close OID;
last;

} elsif ($modechoice eq "2") {
print "Enter an OID:"; 
$chooseoid = <>;
chomp($chooseoid);
push(my @oid_arr, $chooseoid);
$retrievingValueSet = retrievingValueSet($rvs_url, $rmvs_url, $serviceTicket, \@oid_arr);
close OID;
last;

} else {
          print "Invalid input!!"."\n";
          last;
		  }
		  
}


 
#Sub for choosing from RetrieveValueSet or RetrieveMultipleValueSets.
sub retrievingValueSet{
  my $RVS = $_[0];
  my $RMVS = $_[1];
  $servTic = $_[2];
  my @oid = @{$_[3]};
  

while (!$method){
print "Enter a choice of method you want to run -> 1 for RetrieveValueSet, 2 for RetrieveMultipleValueSets :";
$choice = <>;
chomp $choice;
 
#For Retrieving Value Set
if ($choice eq "1") {
                                         
                while (!$rvsoption){
                print "Choose from the options below the call you would like to make: \n
                1= OID, Service Ticket\n
                2= OID, Service Ticket, Version\n
                3= OID, Service Ticket, Effective Date\n
                4= OID, Service Ticket, Version,  Effective Date\n
                5= OID, Service Ticket, Include Draft\n";
             
                $rvso = <>;
                chomp $rvso;
                                                               
                if ($rvso eq "1"){
				
				my $fname = "RetrieveValueSet_Default.xml";
                                
                  if (-e $fname) {
					unlink $fname;
						}
					 
				open(RVS, '>', $fname);	 
                 foreach $oid(@oid){
                 chomp($oid);
				 #print $oid."\n";
					$servTic = serviceTicket($service, $cas);
                                $rclient->GET($RVS . $oid . '&ticket=' . $servTic);
                                my $get_response = $rclient->responseContent();
								
                                print RVS $get_response;
                                } 
				close RVS;  
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
																				
							my $fname = "RetrieveValueSet_version.xml";
                                
							  if (-e $fname) {
								unlink $fname;
									}
                                open(RVS, '>', $fname);          

                                 foreach $oid(@oid){
                                 chomp($oid);              
									$servTic = serviceTicket($service, $cas);
								             
                                $rclient->GET($RVS . $oid . '&version='. $version. '&ticket=' . $servTic);
                                my $get_response = $rclient->responseContent();
                             
                                print RVS $get_response;
								}
                                close RVS;
								
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
                                                                                
																				
							my $fname = "RetrieveValueSet_effDate.xml";
                                
							  if (-e $fname) {
								unlink $fname;
									}
                                open(RVS, '>', $fname);          

                                 foreach $oid(@oid){
                                 chomp($oid);              
									$servTic = serviceTicket($service, $cas);
                                 
                                $rclient->GET($RVS . $oid . '&effectiveDate='. $effDate . '&ticket=' . $servTic);
                                my $get_response = $rclient->responseContent();
								
                                print RVS $get_response;
								}
                                close RVS;
                                        
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
                                                                                
							my $fname = "RetrieveValueSet_version.xml";
                                
							  if (-e $fname) {
								unlink $fname;
									}
                                open(RVS, '>', $fname);          

                                 foreach $oid(@oid){
                                 chomp($oid);              
									$servTic = serviceTicket($service, $cas);                           
             
                                $rclient->GET($RVS . $oid . '&version='. $version. '&ticket=' . $servTic);
                                my $get_response = $rclient->responseContent();
                              
                                print RVS $get_response;
								}
                                close RVS;
                                            
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
                               
     						my $fname = "RetrieveValueSet_draft.xml";
                                
							  if (-e $fname) {
								unlink $fname;
									}
                                open(RVS, '>', $fname);          

                                 foreach $oid(@oid){
                                 chomp($oid);              
									$servTic = serviceTicket($service, $cas);                             
              
                                $rclient->GET('https://vsac.nlm.nih.gov/vsac/ws/RetrieveValueSet?releaseType=VSAC' . '&includeDraft=' . $draft. '&id='. $oid . '&ticket=' . $servTic);
                                my $get_response = $rclient->responseContent();
                             
                                print RVS $get_response;
								}
                                close RVS;
                                            
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
                print "Choose from the options below, the call you would like to make: \n
                1= OID, Service Ticket\n
                2= OID, Service Ticket, Version\n
                3= OID, Service Ticket, Effective Date\n
                4= OID, Service Ticket, Version,  Effective Date\n
                5= OID, Service Ticket, CMSeMeasureId \n
                6= OID, Service Ticket, NQFNumber \n
                7= OID, Service Ticket, MeasureId \n
				8= OID, Service Ticket, Include Draft \n
                9= OID, Service Ticket, MU \n";
                  
                $rvso = <>;
                chomp $rvso;
                
             
                if ($rvso eq "1"){
                		
						my $fname = "RetrieveMultipleValueSets_Default.xml";
                                
							  if (-e $fname) {
								unlink $fname;
									}
                                open(RMVS, '>', $fname);          

                                 foreach $oid(@oid){
                                 chomp($oid);              
									$servTic = serviceTicket($service, $cas);     
									#print $servTic;
                                 
                                $rclient->GET($RMVS . $oid . '&ticket=' . $servTic);
                                my $get_response = $rclient->responseContent();
                             
                                print RMVS $get_response;
								}
                                close RMVS;
                                
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
                                                                                
						my $fname = "RetrieveMultipleValueSets_version.xml";
                                
							  if (-e $fname) {
								unlink $fname;
									}
                                open(RMVS, '>', $fname);          

                                 foreach $oid(@oid){
                                 chomp($oid);              
									$servTic = serviceTicket($service, $cas);    
              
                                $rclient->GET($RMVS . $oid . '&version='. $version. '&ticket=' . $servTic);
                                my $get_response = $rclient->responseContent();
                             
                                print RMVS $get_response;
								}
                                close RMVS;
                                    
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
                                                                                
						my $fname = "RetrieveMultipleValueSets_effDate.xml";
                                
							  if (-e $fname) {
								unlink $fname;
									}
                                open(RMVS, '>', $fname);          

                                 foreach $oid(@oid){
                                 chomp($oid);              
									$servTic = serviceTicket($service, $cas);    
                                 
                                $rclient->GET($RMVS . $oid . '&effectiveDate='. $effDate . '&ticket=' . $servTic);
                                my $get_response = $rclient->responseContent();
                             
                                print RMVS $get_response;
								}
                                close RMVS;
                                            
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
                                
						my $fname = "RetrieveMultipleValueSets_version.xml";
                                
							  if (-e $fname) {
								unlink $fname;
									}
                                open(RMVS, '>', $fname);          

                                 foreach $oid(@oid){
                                 chomp($oid);              
									$servTic = serviceTicket($service, $cas);    
             
                                $rclient->GET($RMVS . $oid . '&version='. $version. '&ticket=' . $servTic);
                                my $get_response = $rclient->responseContent();
                             
                                print RMVS $get_response;
								}
                                close RMVS;
                                            
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
                                                                             
              
						my $fname = "RetrieveMultipleValueSets_cms.xml";
                                
							  if (-e $fname) {
								unlink $fname;
									}
                                open(RMVS, '>', $fname);          

                                 foreach $oid(@oid){
                                 chomp($oid);              
									$servTic = serviceTicket($service, $cas);    
              
                                $rclient->GET('https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?' . 'cmsemeasureid='. $cms. '&ticket=' . $servTic);
                                my $get_response = $rclient->responseContent();
                             
                                print RMVS $get_response;
								}
                                close RMVS;
                                            
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
                             
						my $fname = "RetrieveMultipleValueSets_NQF.xml";
                                
							  if (-e $fname) {
								unlink $fname;
									}
                                open(RMVS, '>', $fname);          

                                 foreach $oid(@oid){
                                 chomp($oid);              
									$servTic = serviceTicket($service, $cas);    
              
                                $rclient->GET('https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?' . 'NQFNumber='. $nqfNum. '&ticket=' . $servTic);
                                my $get_response = $rclient->responseContent();
                             
                                print RMVS $get_response;
								}
                                close RMVS;
                                            
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
                                                                              
              
						my $fname = "RetrieveMultipleValueSets_MeasureId.xml";
                                
							  if (-e $fname) {
								unlink $fname;
									}
                                open(RMVS, '>', $fname);          

                                 foreach $oid(@oid){
                                 chomp($oid);              
									$servTic = serviceTicket($service, $cas);    
                                 
                                $rclient->GET('https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?' . 'measureid='. $measureId. '&ticket=' . $servTic);
                                my $get_response = $rclient->responseContent();
                             
                                print RMVS $get_response;
								}
                                close RMVS;
                                            
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
																	 }
																}

                                
                       
						my $fname = "RetrieveMultipleValueSets_draft.xml";
                                
							  if (-e $fname) {
								unlink $fname;
									}
                                open(RMVS, '>', $fname);          

                                 foreach $oid(@oid){
                                 chomp($oid);              
									$servTic = serviceTicket($service, $cas);    
              
                                $rclient->GET('https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?releaseType=VSAC' . '&includeDraft=' . $draft. '&id='. $oid . '&ticket=' . $servTic);
                                my $get_response = $rclient->responseContent();
                             
                                print RMVS $get_response;
								}
                                close RMVS;
                                            
                                last;       
								
			 
                                } elsif ($rvso eq "9"){
											
											while (!$ver){
                                                                print "Enter the MU:";
                                                                $MU = <>;
                                                                chomp($MU);                                                              
                                                                if (($MU eq "\n") || ($MU eq "")){ #checking for blank inputs.
                                                                print "Invalid input!!"."\n";
 
                                                                } else {
                                                                                last;
                                                                                }
                                                                                }
                                                                              
              
						my $fname = "RetrieveMultipleValueSets_MU.xml";
                                
							  if (-e $fname) {
								unlink $fname;
									}
                                open(RMVS, '>', $fname);          

                                 foreach $oid(@oid){
                                 chomp($oid);              
									$servTic = serviceTicket($service, $cas);    
                                 
                                $rclient->GET('https://vsac.nlm.nih.gov/vsac/ws/RetrieveMultipleValueSets?' . 'MU='. $MU. '&ticket=' . $servTic);
                                my $get_response = $rclient->responseContent();
                             
                                print RMVS $get_response;
								}
                                close RMVS;
                                            
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

