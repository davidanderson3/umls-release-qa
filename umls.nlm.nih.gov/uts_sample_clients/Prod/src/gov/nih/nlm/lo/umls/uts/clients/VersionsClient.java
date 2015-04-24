package gov.nih.nlm.lo.umls.uts.clients;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;
import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.security.*;
import gov.nih.nlm.uts.webservice.metadata.*;
import gov.nih.nlm.uts.webservice.finder.*;

public class VersionsClient {
	    
UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();
UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
UtsWsFinderController utsFinderService = (new UtsWsFinderControllerImplService()).getUtsWsFinderControllerImplPort();

static String username = null;
static String password = null;
static String umlsRelease = null;
static String ticketGrantingTicket = null;
String serviceName = "http://umlsks.nlm.nih.gov";

public VersionsClient(String username, String password, String umlsRelease) {
	VersionsClient.username = username;
	VersionsClient.password = password;
	VersionsClient.umlsRelease = umlsRelease;

}

public String getTicketGrantingTicket() throws Exception{

	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
    String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
    return ticketGrantingTicket;
	
}
	
	//Do you want this to produce a particular 
	public  List<String> getAllRootSourcesMethod() throws Exception{     
		        
		List<RootSourceDTO> myRootSources = new ArrayList<RootSourceDTO>();
        myRootSources = utsMetadataService.getAllRootSources(securityService.getProxyTicket(ticketGrantingTicket, serviceName), umlsRelease);    
                
         List<String> RootSourceAbbreviation = new ArrayList<String>();
                         
         for (int i = 0; i < myRootSources.size(); i++) {
         RootSourceDTO myRootSourcesDTO = myRootSources.get(i);
         String abr = myRootSourcesDTO.getAbbreviation();
         RootSourceAbbreviation.add(abr);
         }
         return RootSourceAbbreviation;  
	}
	
	
	public void getCodeCodeRelationsMethod() throws Exception{
				
		List<String> RtSrcAbbr = new ArrayList<String>();
		RtSrcAbbr = getAllRootSourcesMethod();
    
		gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
        myPsf.getIncludedAdditionalRelationLabels().add("has_version");
        List<AtomClusterRelationDTO> myAtomClusterRelations = new ArrayList<AtomClusterRelationDTO>();
        
        gov.nih.nlm.uts.webservice.finder.Psf fmyPsf = new gov.nih.nlm.uts.webservice.finder.Psf();
	    List<UiLabel> myVCUILabels = new ArrayList<UiLabel>(); 
	    List<UiLabel> myRCUILabel = new ArrayList<UiLabel>(); 
        PrintWriter out = new PrintWriter(new FileWriter("Q:\\My documents\\outputfile2012AA.txt")); 
	    out.println("RCUI"+"|"+ "R-DefaultPrefferdName"+ "|"+"VCUI"+  "|"+ "V-DefaultPrefferdName");


        for (int i = 0; i < RtSrcAbbr.size(); i++) {
         String abr = RtSrcAbbr.get(i);
         String code = "V-"+ abr; 
     
         myAtomClusterRelations = utsContentService.getCodeCodeRelations(securityService.getProxyTicket(ticketGrantingTicket, serviceName), umlsRelease, code, "SRC", myPsf);

         for (int j = 0; j < myAtomClusterRelations.size(); j++) {
           AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(j);
           String VCODE =  myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
           myRCUILabel = utsFinderService.findConcepts(securityService.getProxyTicket(ticketGrantingTicket, serviceName), umlsRelease, "code", code, "exact", fmyPsf);
           myVCUILabels = utsFinderService.findConcepts(securityService.getProxyTicket(ticketGrantingTicket, serviceName), umlsRelease, "code", VCODE, "exact", fmyPsf);
           
	           for (int k = 0; k < myRCUILabel.size(); k++) {
	               UiLabel myUiLabel = myRCUILabel.get(k);
	               String RCUI = myUiLabel.getUi();
	               String RprefferedName = myUiLabel.getLabel();
	               out.print(RCUI+ "|"+ RprefferedName+"|");	
	           }
	           
	           for (int k = 0; k < myVCUILabels.size(); k++) {
	               UiLabel myUiLabel = myVCUILabels.get(k);
	               String VCUI = myUiLabel.getUi();
	               String VprefferedName = myUiLabel.getLabel();
	               out.println( VCUI+ "|"+ VprefferedName);
	
	           }
         	}
        
        }
        out.close();
	}
	


	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			
			VersionsClient versionObj = new VersionsClient(args[0], args[1], args[2]);
			ticketGrantingTicket = versionObj.getTicketGrantingTicket();
			versionObj.getAllRootSourcesMethod();
			versionObj.getCodeCodeRelationsMethod();

	       
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		

	}
	
}
