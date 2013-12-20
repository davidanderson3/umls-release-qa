package gov.nih.nlm.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.finder.UiLabelRootSource;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderController;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;
import java.util.List;

public class ArrayListUiLabelRootSourceFinderClient {

		private static String username = "";
	    private static String password = ""; 
	    static String umlsRelease = "2013AA";
		static String serviceName = "http://umlsks.nlm.nih.gov";
	    
	static UtsWsFinderController  utsFinderService = (new UtsWsFinderControllerImplService()).getUtsWsFinderControllerImplPort();
	static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

	    
	    public ArrayListUiLabelRootSourceFinderClient (String username, String password) {
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

				List<UiLabelRootSource> findCon = new ArrayList<UiLabelRootSource>();
				gov.nih.nlm.uts.webservice.finder.Psf myPsf = new gov.nih.nlm.uts.webservice.finder.Psf();
				//myPsf.setPageLn(100);
				myPsf.getIncludedSources().add("NCI");
				ArrayListUiLabelRootSourceFinderClient UiLabelRtSrcClient = new ArrayListUiLabelRootSourceFinderClient(args[0],args[1]);
	            
	        	String method = args[2];
	        	
	            switch (method) {
	            case "findSourceConcepts": findCon = utsFinderService.findSourceConcepts(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "sourceConcept", "C0007097", "exact", myPsf);
	            break;
	            default: out.println("Unrecognized input ");
	        	break; 
	            }
	            
	            for (int i = 0; i < findCon.size(); i++) {
		            System.out.println(i+":");

	            UiLabelRootSource myUiLabelRtSrc = findCon.get(i);
	            String label = myUiLabelRtSrc.getLabel();
	            String ui = myUiLabelRtSrc.getUi();

	            System.out.println(label+"|"+ui);
	            }
	            
	        	
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}
