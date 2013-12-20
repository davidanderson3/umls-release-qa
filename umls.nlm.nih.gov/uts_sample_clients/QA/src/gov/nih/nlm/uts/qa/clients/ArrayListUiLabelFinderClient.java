package gov.nih.nlm.uts.qa.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.finder.UiLabel;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderController;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;

public class ArrayListUiLabelFinderClient {

		private static String username = "";
	    private static String password = ""; 
	    static String umlsRelease = "2012AB";
		static String serviceName = "http://umlsks.nlm.nih.gov";
	    
	static UtsWsFinderController  utsFinderService = (new UtsWsFinderControllerImplService()).getUtsWsFinderControllerImplPort();
	static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

	    
	    public ArrayListUiLabelFinderClient (String username, String password) {
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

				java.util.List<UiLabel> findCon = new ArrayList<UiLabel>();
				gov.nih.nlm.uts.webservice.finder.Psf myPsf = new gov.nih.nlm.uts.webservice.finder.Psf();
				//myPsf.getIncludedSources().add("SNOMEDCT");
				ArrayListUiLabelFinderClient ConceptBeqDTOClient = new ArrayListUiLabelFinderClient(args[0],args[1]);
	            
	        	String method = args[2];
	        	
	            switch (method) {
	            case "findConcepts": findCon = utsFinderService.findConcepts(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "atom", "dog bite", "words", myPsf);
	            break;
	            default: out.println("Unrecognized input ");
	        	break; 
	            }
	            
	            for (int i = 0; i < findCon.size(); i++) {
		            System.out.println(i+":");

	            UiLabel myConceptBeq = findCon.get(i);
	            String label = myConceptBeq.getLabel();
	            String ui = myConceptBeq.getUi();

	            System.out.println(label+"|"+ui);
	            }
	            
	        	
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}
