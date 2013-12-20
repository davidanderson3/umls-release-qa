package gov.nih.nlm.uts.clients;

import static java.lang.System.out;

import java.util.ArrayList;

import gov.nih.nlm.uts.webservice.history.*;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class ArrayListConceptDeathDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsHistoryController utsHistoryService = (new UtsWsHistoryControllerImplService()).getUtsWsHistoryControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public ArrayListConceptDeathDTOClient (String username, String password) {
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

            java.util.List<ConceptDeathDTO> myConceptDeath = new ArrayList<ConceptDeathDTO>();
            ArrayListConceptDeathDTOClient ConceptDeathDTOClient = new ArrayListConceptDeathDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getConceptDeletions": myConceptDeath = utsHistoryService.getConceptDeletions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C0066997", "1993AA");
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myConceptDeath.size(); i++) {

            ConceptDeathDTO myConceptDeathDTO = myConceptDeath.get(i);
            String ui = myConceptDeathDTO.getUi();
            String version = myConceptDeathDTO.getVersion();
            String DefaultPreferredName = myConceptDeathDTO.getDefaultPreferredName();
            String classType = myConceptDeathDTO.getClassType();
            
            System.out.println("ui:"+ui+"|version:"+version+"|DefaultPreferredName:"+DefaultPreferredName+"|classType:"+classType);
            }
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
