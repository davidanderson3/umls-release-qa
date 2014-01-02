package gov.nih.nlm.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.history.ConceptDeathDTO;
import gov.nih.nlm.uts.webservice.history.UtsWsHistoryController;
import gov.nih.nlm.uts.webservice.history.UtsWsHistoryControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;

public class StringConceptHistoryClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2013AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsHistoryController  utsHistoryService = (new UtsWsHistoryControllerImplService()).getUtsWsHistoryControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public StringConceptHistoryClient (String username, String password) {
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

			java.util.List<String> myarrString= new ArrayList<String>();
			String myString= new String();

			StringConceptHistoryClient arrStringClient = new StringConceptHistoryClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getBequeathedToConceptCuis": myarrString = utsHistoryService.getBequeathedToConceptCuis(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C0000603");
            for (int i = 0; i < myarrString.size(); i++) {

                String myarrstr = myarrString.get(i);
                String intern = myarrstr.intern();
                int hashcode = myarrstr.intern().hashCode();

                System.out.println(intern+"|"+hashcode);
                }
            break;
            case "getMergedToConceptCui": myString = utsHistoryService.getMergedToConceptCui(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C0000258");
            if(myString != null){
            String intern = myString.intern();
            int hashcode = myString.intern().hashCode();

            System.out.println(intern+"|"+hashcode);
            }
            break;
            case "getMovedToConceptCui": myString = utsHistoryService.getMovedToConceptCui(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "A0005183");
            String intern1 = myString.intern();
            int hashcode1 = myString.intern().hashCode();

            System.out.println(intern1+"|"+hashcode1);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            

            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	
}
