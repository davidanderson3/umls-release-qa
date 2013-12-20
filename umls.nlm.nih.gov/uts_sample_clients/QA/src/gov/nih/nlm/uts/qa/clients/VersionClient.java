package gov.nih.nlm.uts.qa.clients;

import java.util.ArrayList;
import java.util.List;
import gov.nih.nlm.uts.webservice.content.AtomClusterRelationDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.finder.UiLabel;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderController;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderControllerImplService;
import gov.nih.nlm.uts.webservice.metadata.RootSourceDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class VersionClient {
	UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
	UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();
	UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
	UtsWsFinderController utsFinderService = (new UtsWsFinderControllerImplService()).getUtsWsFinderControllerImplPort();

	static String username = "";
	static String password = "";
	String umlsRelease = "2012AA";
	String serviceName = "http://umlsks.nlm.nih.gov";

	public VersionClient(String username, String password) {
		VersionClient.username = username;
		VersionClient.password = password;

	}

	public String ticketGrantingTicket() throws Exception{

		//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
	    String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
	    return ticketGrantingTicket;
		
	}
	
	//Do you want this to produce a particular 
	public  List<String> getAllRootSourcesMethod() throws Exception{     
		        
		List<RootSourceDTO> myRootSources = new ArrayList<RootSourceDTO>();
		//System.out.println("singleUseTicket: "+ securityService.getProxyTicket(singleUseTicket(), serviceName));
         myRootSources = utsMetadataService.getAllRootSources(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);    
                
         List<String> Abbreviation = new ArrayList<String>();
                         
         for (int i = 0; i < myRootSources.size(); i++) {
         RootSourceDTO myRootSourcesDTO = myRootSources.get(i);
         String abr = myRootSourcesDTO.getAbbreviation();
         Abbreviation.add(abr);
         //String code = "V-"+abr;
         //System.out.println("Abbreviation: "+ abr);
         //System.out.println("code: "+ code);
         //System.out.println("singleUseTicket: "+ this.singleUseTicket());
         }

         return Abbreviation; 
         
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
        System.out.println("RCUI"+"|"+ "R-DefaultPrefferdName"+ "|"+"VCUI"+  "|"+ "V-DefaultPrefferdName");

        //String oacu = null;
        for (int i = 0; i < RtSrcAbbr.size(); i++) {
         String abr = RtSrcAbbr.get(i);
         String code = "V-"+ abr; 
     
         myAtomClusterRelations = utsContentService.getCodeCodeRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, code, "SRC", myPsf);

         for (int j = 0; j < myAtomClusterRelations.size(); j++) {
           AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(j);
           String VCODE =  myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
           //String RprefferedName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
           myRCUILabel = utsFinderService.findConcepts(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "code", code, "exact", fmyPsf);
           myVCUILabels = utsFinderService.findConcepts(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "code", VCODE, "exact", fmyPsf);
           
	           for (int k = 0; k < myRCUILabel.size(); k++) {
	               UiLabel myUiLabel = myRCUILabel.get(k);
	               String RCUI = myUiLabel.getUi();
	               String RprefferedName = myUiLabel.getLabel();
	               System.out.print(RCUI+ "|"+ RprefferedName+"|");
	
	           }
	           for (int k = 0; k < myVCUILabels.size(); k++) {
	               UiLabel myUiLabel = myVCUILabels.get(k);
	               String VCUI = myUiLabel.getUi();
	               String VprefferedName = myUiLabel.getLabel();
	               System.out.println( VCUI+ "|"+ VprefferedName);
	
	           }
         	}
        
        }
	}



	public static void main(String[] args) {
		try {
			
			VersionClient versionObj = new VersionClient(args[0], args[1]);
			versionObj.ticketGrantingTicket();
			versionObj.getAllRootSourcesMethod();
			versionObj.getCodeCodeRelationsMethod();
			
		}catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
