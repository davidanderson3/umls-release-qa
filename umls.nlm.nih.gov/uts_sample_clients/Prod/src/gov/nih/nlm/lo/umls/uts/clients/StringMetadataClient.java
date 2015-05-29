package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;

import java.util.ArrayList;

import gov.nih.nlm.uts.webservice.metadata.CooccurrenceTypeDTO;
import gov.nih.nlm.uts.webservice.metadata.IdentifierTypeDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class StringMetadataClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2013AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public StringMetadataClient (String username, String password) {
	this.username = username;
	this.password = password;
	
}

	public static String ticketGrantingTicket() throws Exception{
	
    	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
        //System.out.println("tgt: "+ticketGrantingTicket);  

        //use the Proxy Grant Ticket to get a Single Use Ticket
       // String singleUseTicket = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
        return ticketGrantingTicket;
    	
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			java.util.List<String>  myarrListStringMetadata  = new ArrayList<String>();
			String  myarrStringMetadata  = new String();

			StringMetadataClient StringarrMetadataClient = new StringMetadataClient(args[0],args[1]);
            
        	String method = args[2];
        	
        	switch (method) {
            case "getAllUMLSVersions": myarrStringMetadata = utsMetadataService.getAllUMLSVersions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName));
            System.out.println(myarrStringMetadata);
            break;
            case "getCurrentUMLSVersion": myarrStringMetadata = utsMetadataService.getCurrentUMLSVersion(securityService.getProxyTicket(ticketGrantingTicket(), serviceName));
            System.out.println(myarrStringMetadata);
            break;
            case "getRootSourceSynonymousNames": myarrListStringMetadata = utsMetadataService.getRootSourceSynonymousNames(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "CCS");
            for (int i = 0; i < myarrListStringMetadata.size(); i++) {

                String myStringMetadata = myarrListStringMetadata.get(i);
                int hashCode = myStringMetadata.hashCode();
                System.out.println("Hashcode:"+hashCode);
                }
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            

                int hashCode = myarrStringMetadata.hashCode();
                System.out.println("Hashcode:"+hashCode);
                      
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
