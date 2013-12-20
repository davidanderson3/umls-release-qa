package gov.nih.nlm.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.metadata.AttributeNameDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;

public class AttributeNameDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public AttributeNameDTOClient (String username, String password) {
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

			java.util.List<AttributeNameDTO> myarrAttributeName = new ArrayList<AttributeNameDTO>();
			AttributeNameDTO myAttributeName = new AttributeNameDTO();
			AttributeNameDTOClient AttributeNameDTOClient = new AttributeNameDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAllAttributeNames": myarrAttributeName = utsMetadataService.getAllAttributeNames(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getAttributeName": myAttributeName = utsMetadataService.getAttributeName(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "TH");
            String abbreviation = myAttributeName.getAbbreviation();
            String expandedForm = myAttributeName.getExpandedForm();

            System.out.println("Abbreviation:"+abbreviation+"|ExpandedForm:"+expandedForm);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrAttributeName.size(); i++) {

            AttributeNameDTO myarrAttributeNameDTO = myarrAttributeName.get(i);
            String abbreviation = myarrAttributeNameDTO.getAbbreviation();
            String expandedForm = myarrAttributeNameDTO.getExpandedForm();
            
            System.out.println("Abbreviation:"+abbreviation+"|ExpandedForm:"+expandedForm);
            }
            
            
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
