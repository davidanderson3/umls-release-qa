package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.lo.umls.uts.helpers.TicketClient;
import gov.nih.nlm.uts.webservice.finder.UiLabel;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderController;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderControllerImplService;

import java.util.List;
import java.util.ArrayList;

public class ArrayListUiLabelFinderClient {

	    private static String umlsRelease = "";
		private static String serviceName = "http://umlsks.nlm.nih.gov";
		private static TicketClient ticketClient = new TicketClient();
		private static String tgt = ticketClient.getTicketGrantingTicket();
	    
	static UtsWsFinderController  utsFinderService = (new UtsWsFinderControllerImplService()).getUtsWsFinderControllerImplPort();
	    
	    public ArrayListUiLabelFinderClient (String umlsRelease) {
		this.umlsRelease = umlsRelease;
		
	}
	
		public static void main(String[] args) {
			// TODO Auto-generated method stub
			try {

				List<UiLabel> labels = new ArrayList<UiLabel>();
				gov.nih.nlm.uts.webservice.finder.Psf myPsf = new gov.nih.nlm.uts.webservice.finder.Psf();
				myPsf.setPageLn(100);
	        	String method = args[1];
	        	
	            switch (method) {
	            case "findConcepts": 
	            labels = utsFinderService.findConcepts(ticketClient.getSingleUseTicket(tgt), umlsRelease, "atom", "diabetic foot", "exact", myPsf);
	            break;
	            default: out.println("Unrecognized input ");
	        	break; 
	            }
	            
	            for (UiLabel label:labels) {
                 String ui = label.getUi();
                 String name = label.getLabel();
                 Class myClass = label.getClass();

	            System.out.println(ui+"|"+label+"|"+myClass);
	            }
	            
	        	
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}
