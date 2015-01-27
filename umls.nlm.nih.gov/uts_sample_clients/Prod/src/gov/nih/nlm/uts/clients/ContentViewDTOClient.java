package gov.nih.nlm.uts.clients;

import static java.lang.System.out;

import java.util.ArrayList;
import gov.nih.nlm.uts.webservice.content.ContentViewDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class ContentViewDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2014AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public ContentViewDTOClient(String username, String password) {
	ContentViewDTOClient.username = username;
	ContentViewDTOClient.password = password;
	
    }


	public static String ticketGrantingTicket() throws Exception{
	
    	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);

        //use the Proxy Grant Ticket to get a Single Use Ticket
       // String singleUseTicket = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
        return ticketGrantingTicket;
    	
    }
    
	public static void main(String[] args) {
		try {
			// Runtime properties

			ContentViewDTOClient ConViewClient = new ContentViewDTOClient(args[0],args[1]);
            
        	String method = args[2];

            gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
            java.util.List<ContentViewDTO> myContentViews = new ArrayList<ContentViewDTO>();
            ContentViewDTO myContentView = new ContentViewDTO();
            
            switch (method) {
            case "getContentViews": myContentViews = utsContentService.getContentViews(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, myPsf); 
            for (int i = 0; i < myContentViews.size(); i++) {

            	ContentViewDTO myContentViewDTO = myContentViews.get(i);
            	String cui = myContentViewDTO.getHandle();
                String name = myContentViewDTO.getName();
                int noAuis = myContentViewDTO.getAtomMemberCount();
                int noScuis = myContentViewDTO.getSourceConceptMemberCount();

                System.out.println("&lt;tr&gt;&lt;td&gt;"+cui+"&lt;/td&gt;"+"&lt;td&gt;"+name+"&lt;/td&gt;&lt;/tr&gt;");
                }
            break;
            
        	case "getContentView": myContentView = utsContentService.getContentView(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C3812142");
        	String name = myContentView.getName();
            int scuiCount = myContentView.getSourceConceptMemberCount();
            String contributor = myContentView.getContributor();
            String contributorurl = myContentView.getContributorURL();
            System.out.println(name+"|"+scuiCount+"|"+contributor+"|"+contributorurl);
        	break;
        	
         	default: out.println("Unrecognized input ");
        	break; 
            }
            
  
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
