package gov.nih.nlm.uts.clients;

import static java.lang.System.out;

import java.util.ArrayList;

import gov.nih.nlm.uts.webservice.content.AtomDTO;
import gov.nih.nlm.uts.webservice.content.AtomSubsetMemberDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class AtomSubsetMemberDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2011AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public AtomSubsetMemberDTOClient(String username, String password) {
    	AtomSubsetMemberDTOClient.username = username;
    	AtomSubsetMemberDTOClient.password = password;
	
    }


	public static String ticketGrantingTicket() throws Exception{
	   	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
        return ticketGrantingTicket;
       } 
	
	public static void main(String[] args) {
		try {
			// Runtime properties
			AtomSubsetMemberDTOClient AtomSubsetMemDTOClnt = new AtomSubsetMemberDTOClient(args[0],args[1]);
            
        	String method = args[2];

            gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
            java.util.List<AtomSubsetMemberDTO> AtomSubsetMem = new ArrayList<AtomSubsetMemberDTO>();
                        
            switch (method) {
            case "getAtomSubsetMemberships": AtomSubsetMem = utsContentService.getAtomSubsetMemberships(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "A6943203", myPsf); 
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < AtomSubsetMem.size(); i++) {

            	AtomSubsetMemberDTO myAtomSubsetMem = AtomSubsetMem.get(i);
                String ui = myAtomSubsetMem.getAtom().getConcept().getUi();
                String defprefname = myAtomSubsetMem.getAtom().getConcept().getDefaultPreferredName();
                String contviewhandle = myAtomSubsetMem.getSubsetHandle();
                String termtype = myAtomSubsetMem.getAtom().getTermType();
               
                System.out.println(ui+"|"+defprefname+"|"+contviewhandle+"|"+termtype);
                }
            
  
		} catch (Exception ex) {
			ex.printStackTrace();
		}


	}

}
