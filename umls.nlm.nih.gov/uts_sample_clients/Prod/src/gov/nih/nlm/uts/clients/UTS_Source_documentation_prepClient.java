package gov.nih.nlm.uts.clients;

import java.io.File;
import java.util.ArrayList;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;
import gov.nih.nlm.uts.webservice.metadata.SourceDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;

public class UTS_Source_documentation_prepClient {
	
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "";
	static String serviceName = "http://umlsks.nlm.nih.gov";
	//static String path;
	

static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public UTS_Source_documentation_prepClient (String username, String password, String umlsRelease) {
	this.username = username;
	this.password = password;
	this.umlsRelease = umlsRelease;
	
    	}

    
	public static String ticketGrantingTicket() throws Exception{
	
    	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);

        //use the Proxy Grant Ticket to get a Single Use Ticket
        return ticketGrantingTicket;
    	
    }


	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			
			UTS_Source_documentation_prepClient srcDocClient = new UTS_Source_documentation_prepClient(args[0],args[1],args[2]);
			
			String  myStringMetadata  = new String();
			java.util.List<SourceDTO> mySrcVersion = new ArrayList<SourceDTO>();
			
//			myStringMetadata = utsMetadataService.getAllUMLSVersions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName));
//            System.out.println(myStringMetadata);
//			String[] myarrStringMetadata = myStringMetadata.split("\\|");
//			int i = 0;
//
//			
//			for(i=0; i < myarrStringMetadata.length; i++){

	            	String dir = "S:/SHARE/MMS/UMLS/UMLS_Source_Documentation_VM_Files/"+umlsRelease;
	            			File verDir = new File(dir);
	            			
	            			//Directory existence check
	            			if(!verDir.exists())
	            				verDir.mkdirs();
	            			
	            			 mySrcVersion = utsMetadataService.getUpdatedSourcesByVersion(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
	            			 
	            			 for (int j = 0; j < mySrcVersion.size(); j++) {

	            		            SourceDTO myArrSrcDTO = mySrcVersion.get(j);
	            		            String rtSrcAbbr = myArrSrcDTO.getRootSource().getAbbreviation();
	            		            
	            		            String verSrcDir = "S:/SHARE/MMS/UMLS/UMLS_Source_Documentation_VM_Files/"+umlsRelease+"/"+rtSrcAbbr;
	            		            File sourceVerDir = new File(verSrcDir);
	    	            			
	    	            			//Directory existence check
	    	            			if(!sourceVerDir.exists())
	    	            				sourceVerDir.mkdirs();
	            		            
	            		            
	            			 }
	            			 
			//}
	            				            			


	        
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	        
		}
}