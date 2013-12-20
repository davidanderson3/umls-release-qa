package gov.nih.nlm.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.history.ConceptBequeathalDTO;
import gov.nih.nlm.uts.webservice.history.UtsWsHistoryController;
import gov.nih.nlm.uts.webservice.history.UtsWsHistoryControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;

public class ConceptBequeathalDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2013AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsHistoryController  utsHistoryService = (new UtsWsHistoryControllerImplService()).getUtsWsHistoryControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public ConceptBequeathalDTOClient (String username, String password) {
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

			java.util.List<ConceptBequeathalDTO> myconceptBeqDTO = new ArrayList<ConceptBequeathalDTO>();
			ConceptBequeathalDTOClient ConceptBeqDTOClient = new ConceptBequeathalDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getConceptBequeathals": myconceptBeqDTO = utsHistoryService.getConceptBequeathals(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C0074722", "2010AA");
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myconceptBeqDTO.size(); i++) {

            ConceptBequeathalDTO myConceptBeq = myconceptBeqDTO.get(i);
            String defPrefName = myConceptBeq.getDefaultPreferredName();
            String addLabel = myConceptBeq.getAdditionalLabel();
            String label = myConceptBeq.getLabel();
            String ui = myConceptBeq.getUi();
            String relConceptUi = myConceptBeq.getRelatedConceptUi();
            String version = myConceptBeq.getVersion();
            String reason = myConceptBeq.getReason();

            System.out.println(defPrefName+"|"+addLabel+"|"+label+"|"+ui+"|"+relConceptUi+"|"+version+"|"+reason);
            }
            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
