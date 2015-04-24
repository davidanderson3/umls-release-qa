package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.history.AtomMovementDTO;
import gov.nih.nlm.uts.webservice.history.ConceptMergeDTO;
import gov.nih.nlm.uts.webservice.history.UtsWsHistoryController;
import gov.nih.nlm.uts.webservice.history.UtsWsHistoryControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;

public class AtomMovementDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsHistoryController  utsHistoryService = (new UtsWsHistoryControllerImplService()).getUtsWsHistoryControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public AtomMovementDTOClient (String username, String password) {
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

			java.util.List<AtomMovementDTO> myAtomMovementDTO = new ArrayList<AtomMovementDTO>();
			AtomMovementDTOClient AtomMovementDTO = new AtomMovementDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAtomMovements": myAtomMovementDTO = utsHistoryService.getAtomMovements(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "A0000230");
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myAtomMovementDTO.size(); i++) {

            AtomMovementDTO myAtomMov = myAtomMovementDTO.get(i);
            String newUi = myAtomMov.getNewConceptUi();
            String oldUi = myAtomMov.getOldConceptUi();
            String ui = myAtomMov.getUi();
            String version = myAtomMov.getVersion();
            String reason = myAtomMov.getReason();

            System.out.println(newUi+"|"+oldUi+"|"+ui+"|"+version);
            }
            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


}
