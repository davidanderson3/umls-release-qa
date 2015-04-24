package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;

import java.util.ArrayList;

import gov.nih.nlm.uts.webservice.metadata.SubheadingDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class SubheadingDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SubheadingDTOClient (String username, String password) {
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

			java.util.List<SubheadingDTO> myarrSubheading = new ArrayList<SubheadingDTO>();
			SubheadingDTO mySubheading = new SubheadingDTO();
			SubheadingDTOClient SubheadingClient = new SubheadingDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAllSubheadings": myarrSubheading = utsMetadataService.getAllSubheadings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getSubheading": mySubheading = utsMetadataService.getSubheading(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "AE");
            String expandedForm = mySubheading.getExpandedForm();
            String abbreviation = mySubheading.getAbbreviation();
            
            System.out.println("ExpandedForm:"+expandedForm+"Abbreviation:"+abbreviation);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrSubheading.size(); i++) {

            SubheadingDTO mySubheadingDTO = myarrSubheading.get(i);
            String expandedForm = mySubheadingDTO.getExpandedForm();
            String abbreviation = mySubheadingDTO.getAbbreviation();

            System.out.println("ExpandedForm:"+expandedForm+"Abbreviation:"+abbreviation);
            }
            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
