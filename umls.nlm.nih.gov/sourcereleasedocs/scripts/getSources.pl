#!/share_nfs/perl/5.8.6/bin/perl
use XML::Writer;
use Getopt::Std;
use IO::File;
use warnings;
##arguments are -i, must be a MRSAB.RRF file

getopts("i:");
$input = $opt_i || die "Please enter a list of sources to be processed $!\n";
open FH, "<$input" || die "could not open input file because of $!\n";
my $output = new IO::File(">sources.xml") || die "could not open output file due to $!\n";
$writer = new XML::Writer(OUTPUT => $output,DATA_MODE => 'true',DATA_INDENT => 4);

$writer->xmlDecl("utf-8");
$writer->startTag("document");

$i = 0;
@letters = (A .. Z);
%languages = ("BAQ"=>"Basque","CZE"=>"Czech","DAN"=>"Danish","ENG"=>"English","FRE"=>"French","GER"=>"German","HEB"=>"Hebrew","HUN"=>"Hungarian","ITA"=>"Italian","JPN"=>"Japanese",
              "KOR"=>"Korean","LAV"=>"Latvian","NOR"=>"Norwegian","POL"=>"Polish","POR"=>"Portuguese","RUS"=>"Russian","SCR"=>"Serbo-Croatian","SPA"=>"Spanish","SWE"=>"Swedish");
@restrictions = ("0","1","2","3","4","9");

###hash of arrays for mu Categories
$muCategories{"Medications"} =  ["GS" , "MDDB" , "MMSL" , "MMX" , "MSH" , "MTHFDA" , "MTHSPL" , "NDDF" , "NDFRT" , "RXNORM" , "SNOMEDCT" , "VANDF"];
$muCategories{"Laboratory Tests and Procedures"} = ["LNC"];
$muCategories{"Problems"} = ["ICD9CM","ICD10CM","SNOMEDCT","MEDCIN"];
$muCategories{"Procedures"} = ["CPT","HCPT","HCDT","HCPCS","ICD9CM","ICD10PCS"];


###hash of arrays for content Categories
$contentCategories{"Adverse Drug Reaction Reporting Systems"} = ["MDR"];
$contentCategories{"Anatomy"} = ["FMA"];
$contentCategories{"Consumer Health Information"} = ["CHV" , "MEDLINEPLUS"];
$contentCategories{"Dentistry"} = ["HCDT"];
$contentCategories{"Diagnosis"} = ["ICD9CM" , "ICD10CM" , "MEDCIN", "SNOMEDCT", "CPT"];
$contentCategories{"Disabled Persons"} = ["ICF" , "ICF-CY" ];
$contentCategories{"Disease"} = ["ICD9CM","ICD10CM","MEDLINEPLUS" , "MSH" , "OMIM" , "NCI" , "SNOMEDCT", "MDR"];
$contentCategories{"Drugs [Pharmaceutical Preparations]"} = ["GS" , "MDDB" , "MMSL" , "MMX" , "MSH" , "MTHFDA" , "MTHSPL" , "NDDF" , "NDFRT" , "RXNORM" , "SNOMEDCT" , "VANDF"];
$contentCategories{"Insurance Claim Reporting"} = ["CPT" , "HCDT" , "HCPCS" , "ICD10PCS", "ICD9CM", "ICD10CM"];
$contentCategories{"Genetics"} = ["GO","HUGO","OMIM", "NCI"];
$contentCategories{"Laboratory Techniques and Procedures"} = ["LNC","CPT","SNOMEDCT"];
$contentCategories{"Medical Devices"} = ["HCPCS","UMD"];
$contentCategories{"Nursing"} = ["ICNP", "PNDS"];
$contentCategories{"Phylogeny"} = ["NCBI"];
#$contentCategories{"Problem-Oriented Medical Records"} = ["ICD9CM" , "ICD10CM" , "MEDCIN", "SNOMEDCT"];
$contentCategories{"Procedures"} = ["CPT" , "HCDT" , "HCPCS", "ICD10PCS" , "MEDCIN", "SNOMEDCT", "ICD9CM", "NCI"];
$contentCategories{"Complementary Therapies"} = ["TKMT"];
#$contentCategories{"Subject Headings"} = ["MSH"];



while(<FH>) {
    
    
    my @fields = split/\|/, $_;
    my $rsab = $fields[3];
    my $srl = $fields[13];
    my $lat = $fields[19];
    my $ssn = $fields[23];
    my $imeta = $fields[9];
    my $curver = $fields[21];
    my $firstletter = substr($rsab,0,1);
    next if $curver eq "N";
    ##Create an array of hashes we can re-use later
    push @allsources, {"rsab"=>$rsab,"srl"=>$srl,"lat"=>$lat,"ssn"=>$ssn,"imeta"=>$imeta,"firstletter"=>$firstletter};
    if ($lat eq "ENG") {    
       push @englishSources,{"rsab"=>$rsab,"srl"=>$srl,"lat"=>$lat,"ssn"=>$ssn,"imeta"=>$imeta,"firstletter"=>$firstletter};  
    }
    #print qq{$rsab \n};
    ##only process current sources
    
    if($srl eq "0"){
        
        push @levelZeroSources,{"rsab"=>$rsab,"srl"=>$srl,"lat"=>$lat,"ssn"=>$ssn,"imeta"=>$imeta,"firstletter"=>$firstletter}; 
    }
    
    if($srl eq "3"){
        
        push @levelThreeSources,{"rsab"=>$rsab,"srl"=>$srl,"lat"=>$lat,"ssn"=>$ssn,"imeta"=>$imeta,"firstletter"=>$firstletter}; 
    }
    $i++;


    
}


&processLetters(@allsources);
&processRestrictions;
&processLanguages;
&processCategories(%muCategories);
&processCategories(%contentCategories);

$writer->endTag();

sub processLetters {
    
 my @selectedsources = @_;
 $writer->startTag("letters");
 foreach my $letter(@letters) {
    
    $writer->startTag("letter","group"=>$letter);
    $writer->startTag("sources");
    my $numberOfSources = scalar(@selectedsources);
    for ($i = 0;$i < $numberOfSources;$i++) {
           my $ssn = $selectedsources[$i]{"ssn"};
           my $imeta= $selectedsources[$i]{"imeta"};
        if ($selectedsources[$i]{"firstletter"} eq $letter){
            $writer->startTag("source","ssn"=>$ssn,"imeta"=>$imeta);
            $writer->characters($selectedsources[$i]{"rsab"});
            $writer->endTag();
        }
        
    }
    
    
    $writer->endTag();
    $writer->endTag();
    
    
 }

$writer->endTag();
}
  
    
sub processRestrictions     {
    
    $writer->startTag("restrictions");
    
        foreach $restriction(@restrictions)  {
        
            
            if($restriction eq "0") {
            $writer->startTag("restriction","level"=>"Category 0");
            &processLetters(@levelZeroSources);
            $writer->endTag();
            } ##end if
        
            elsif($restriction eq "3"){
            $writer->startTag("restriction","level"=>"Category 3");
            &processLetters(@levelThreeSources);
            $writer->endTag();
            } ## end elsif
        
            else {
            $writer->startTag("restriction", "level"=>"Category ".$restriction);
            $writer->startTag("sources");
            my $numberOfSources = scalar(@allsources);
            for ($i = 0;$i < $numberOfSources;$i++) {
            
              if ($allsources[$i]{"srl"} eq $restriction){
              my $ssn = $allsources[$i]{"ssn"};
              my $imeta= $allsources[$i]{"imeta"};
              $writer->startTag("source","ssn"=>$ssn,"imeta"=>$imeta);
              $writer->characters($allsources[$i]{"rsab"});
              $writer->endTag();
              
            
              }  ## end if
               
            }  ##end for
            $writer->endTag();
            $writer->endTag();
        }##end else
       
       
        
        
        
    } ##end foreach
    $writer->endTag();
} ##end function


sub processLanguages {
$writer->startTag("languages");
       foreach my $lats  (keys %languages){
            
          if ($lats eq "ENG") {
            $writer->startTag("language","type"=>"English");
            &processLetters(@englishSources);
            $writer->endTag();
          }## endif
          
          
          else {
            my $language = $languages{$lats};
            $writer->startTag("language","type"=>$language);
            $writer->startTag("sources");
            my $numberOfSources = scalar(@allsources);
            for ($i = 0;$i < $numberOfSources;$i++) {
              my $ssn = $allsources[$i]{"ssn"};
              my $imeta= $allsources[$i]{"imeta"};
              if ($allsources[$i]{"lat"} eq $lats){
             
              $writer->startTag("source","ssn"=>$ssn,"imeta"=>$imeta);
              $writer->characters($allsources[$i]{"rsab"});
              $writer->endTag();
              
            
              }  ## end if
               
            }  ##end for
            $writer->endTag();
            $writer->endTag();
        }##end else
          
          
       }##end foreach
    
    
$writer->endTag();   
}##end processLanguages

sub processCategories{
    
    my %categories = @_;

    if (exists $categories{"Adverse Drug Reaction Reporting Systems"}) {
        
        $writer->startTag("contentCategories");       
        
    }
    
    else {
    $writer->startTag("muCategories");
        
    }
    
    
    $writer->startTag("categories");
    foreach $category (sort keys %categories) {
       print qq{$category\n};
       @sources = @{ $categories{$category} };
       #print qq{$muCategory:\t @muSources\n};
       $writer->startTag("category","name"=>$category);
       $writer->startTag("sources");
       my $numberOfSources = scalar(@allsources);
       foreach $source(@sources){
        for ($i = 0; $i < $numberOfSources;$i++){
            if ($source eq $allsources[$i]{"rsab"}){
                #print qq{$allsources[$i]{"ssn"}};
                my $rsab = $allsources[$i]{"rsab"};
                my $ssn = $allsources[$i]{"ssn"};
                my $imeta = $allsources[$i]{"imeta"};
                $writer->startTag("source","imeta"=>$imeta,"ssn"=>$ssn);
                $writer->characters($rsab);
                $writer->endTag();
            }
            
        }
        
       }
       $writer->endTag();
       $writer->endTag();
        
    }
    
    
    $writer->endTag();
    $writer->endTag();
    
    
}


