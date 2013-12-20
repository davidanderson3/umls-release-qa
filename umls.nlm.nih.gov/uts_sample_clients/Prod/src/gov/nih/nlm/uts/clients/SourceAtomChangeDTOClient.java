package gov.nih.nlm.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.history.AtomMovementDTO;
import gov.nih.nlm.uts.webservice.history.SourceAtomChangeDTO;
import gov.nih.nlm.uts.webservice.history.UtsWsHistoryController;
import gov.nih.nlm.uts.webservice.history.UtsWsHistoryControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;

public class SourceAtomChangeDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsHistoryController  utsHistoryService = (new UtsWsHistoryControllerImplService()).getUtsWsHistoryControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SourceAtomChangeDTOClient (String username, String password) {
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

			java.util.List<SourceAtomChangeDTO> mySrcAtomChange = new ArrayList<SourceAtomChangeDTO>();
			SourceAtomChangeDTOClient SourceAtomChangeDTO = new SourceAtomChangeDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getSourceAtomChanges": mySrcAtomChange = utsHistoryService.getSourceAtomChanges(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C0198939", "SNOMEDCT", "512218015");
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < mySrcAtomChange.size(); i++) {

            SourceAtomChangeDTO myAtomCh = mySrcAtomChange.get(i);
            String key = myAtomCh.getKey();
            String rtSource = myAtomCh.getRootSource();
            String ui = myAtomCh.getUi();
            String srcUi = myAtomCh.getSourceUi();
            String value = myAtomCh.getValue();
            String type = myAtomCh.getType();
            String version = myAtomCh.getVersion();
            String reason = myAtomCh.getReason();

            System.out.println(key+"|"+rtSource+"|"+ui+"|"+srcUi+"|"+value+"|"+type+"|"+version+"|"+reason);
            }
            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
