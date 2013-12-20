package gov.nih.nlm.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.history.ConceptMergeDTO;
import gov.nih.nlm.uts.webservice.history.UtsWsHistoryController;
import gov.nih.nlm.uts.webservice.history.UtsWsHistoryControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;

public class ConceptMergeDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsHistoryController  utsHistoryService = (new UtsWsHistoryControllerImplService()).getUtsWsHistoryControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public ConceptMergeDTOClient (String username, String password) {
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

			java.util.List<ConceptMergeDTO> myConceptMergeDTO = new ArrayList<ConceptMergeDTO>();
			ConceptMergeDTOClient ConceptMergeDTOClient = new ConceptMergeDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getConceptMerges": myConceptMergeDTO = utsHistoryService.getConceptMerges(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "", "1993AA");
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myConceptMergeDTO.size(); i++) {

            ConceptMergeDTO myConceptMer = myConceptMergeDTO.get(i);
            String newUi = myConceptMer.getNewConceptUi();
            String ui = myConceptMer.getUi();
            String version = myConceptMer.getVersion();
            String reason = myConceptMer.getReason();

            System.out.println(newUi+"|"+ui+"|"+version+"|"+reason);
            }
            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
