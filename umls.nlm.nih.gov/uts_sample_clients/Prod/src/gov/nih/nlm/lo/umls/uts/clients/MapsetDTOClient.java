package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;
import java.util.ArrayList;
import gov.nih.nlm.uts.webservice.content.MapsetDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class MapsetDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2014AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public MapsetDTOClient(String username, String password) {
    	MapsetDTOClient.username = username;
    	MapsetDTOClient.password = password;
	
    }


	public static String ticketGrantingTicket() throws Exception{
	   	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
        return ticketGrantingTicket;
       } 
	
	public static void main(String[] args) {
		try {
			// Runtime properties
			MapsetDTOClient AtomSubsetMemDTOClnt = new MapsetDTOClient(args[0],args[1]);
            
        	String method = args[2];

            gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
            java.util.List<MapsetDTO> Mapsetdto = new ArrayList<MapsetDTO>();
                        
            switch (method) {
            case "getMapsets": Mapsetdto = utsContentService.getMapsets(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, myPsf); 
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < Mapsetdto.size(); i++) {

            	MapsetDTO myMapset = Mapsetdto.get(i);
                String ui = myMapset.getUi();
                String name  = myMapset.getName();
                String version = myMapset.getVersion();
                String frmrootsrc = myMapset.getFromRootSource();
                String frmcomplexity = myMapset.getFromComplexity();
                String torootsrc = myMapset.getToRootSource();

                System.out.println(ui+"|"+name+"|"+version+"|"+frmrootsrc+"|"+frmcomplexity+"|"+torootsrc);
                }
            
  
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
