package gov.nih.nlm.uts.clients;

import static java.lang.System.out;

import java.lang.annotation.Annotation;

import gov.nih.nlm.uts.webservice.metadata.IdentifierTypeDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class IdentifierTypeDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public IdentifierTypeDTOClient (String username, String password) {
	this.username = username;
	this.password = password;
	
}

	public static String ticketGrantingTicket() throws Exception{
	
    	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
        System.out.println("tgt: "+ticketGrantingTicket);  

        //use the Proxy Grant Ticket to get a Single Use Ticket
       // String singleUseTicket = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
        return ticketGrantingTicket;
    	
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			IdentifierTypeDTO  myIdentifierTypeDTO  = new IdentifierTypeDTO();
			IdentifierTypeDTOClient IdentifierTypeDTOClient = new IdentifierTypeDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getIdentifierType": myIdentifierTypeDTO = utsMetadataService.getIdentifierType(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "SCUI");
            String abbreviation = myIdentifierTypeDTO.getAbbreviation();
            String expandedForm = myIdentifierTypeDTO.getExpandedForm();
            
            System.out.println("Abbreviation:"+abbreviation+"|ExpandedForm:"+expandedForm);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
                      
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
