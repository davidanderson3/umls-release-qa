package gov.nih.nlm.uts.clients;

import static java.lang.System.out;

import java.util.ArrayList;
import gov.nih.nlm.uts.webservice.content.SubsetDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class SubsetDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2011AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
	
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SubsetDTOClient(String username, String password) {
    	SubsetDTOClient.username = username;
    	SubsetDTOClient.password = password;
	
    }


	public static String ticketGrantingTicket() throws Exception{
	   	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
        return ticketGrantingTicket;
       } 
	
	
	public static void main(String[] args) {
		try {
			// Runtime properties
			SubsetDTOClient SubsetDTOClnt = new SubsetDTOClient(args[0],args[1]);
            
        	String method = args[2];

            gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
            
            java.util.List<SubsetDTO> mySubsetsDTO = new ArrayList<SubsetDTO>();
            SubsetDTO mySubsetDTO = new SubsetDTO();

            
            switch (method) {
            case "getSubsets": mySubsetsDTO = utsContentService.getSubsets(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, myPsf); 
            for (int i = 0; i < mySubsetsDTO.size(); i++) {

            	SubsetDTO mySubsets = mySubsetsDTO.get(i);
                String ui = mySubsets.getUi();
                String name = mySubsets.getName();
                String srcui = mySubsets.getSourceUi();
                int atommemcnt = mySubsets.getAtomMemberCount();
               
                System.out.println(ui+"|"+name+"|"+srcui+"|"+atommemcnt);                }
            break;
            case "getSubset": mySubsetDTO = utsContentService.getSubset(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C1368722");
            String ui = mySubsetDTO.getUi();
            String name = mySubsetDTO.getName();
            String srcui = mySubsetDTO.getSourceUi();
            int atommemcnt = mySubsetDTO.getAtomMemberCount();
           
            System.out.println(ui+"|"+name+"|"+srcui+"|"+atommemcnt);
            break;
        	default: out.println("Unrecognized input ");
        	break; 
            }
            

            
  
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
