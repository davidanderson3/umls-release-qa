package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;
import java.util.ArrayList;
import gov.nih.nlm.uts.webservice.content.AtomContentViewMemberDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class AtomContentViewMemberDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public AtomContentViewMemberDTOClient(String username, String password) {
    	AtomContentViewMemberDTOClient.username = username;
    	AtomContentViewMemberDTOClient.password = password;
	
    }


	public static String ticketGrantingTicket() throws Exception{
	   	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
        return ticketGrantingTicket;
       }
    
	public static void main(String[] args) {
		try {
			// Runtime properties
//			String username = "debjaniani";
//			String password = "Cartoon123!";
			AtomContentViewMemberDTOClient AtomContMemClient = new AtomContentViewMemberDTOClient(args[0],args[1]);
            
        	String method = args[2];

            gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
            java.util.List<AtomContentViewMemberDTO> myAtomContentViewMember = new ArrayList<AtomContentViewMemberDTO>();
                        
            switch (method) {
            case "getAtomContentViewMemberships": myAtomContentViewMember = utsContentService.getAtomContentViewMemberships(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "A2878777", myPsf); 
            for (int i = 0; i < myAtomContentViewMember.size(); i++) {

            	AtomContentViewMemberDTO myAtomContentViewMemberDTO = myAtomContentViewMember.get(i);

                String ui = myAtomContentViewMemberDTO.getAtom().getConcept().getUi();
                String defprefname = myAtomContentViewMemberDTO.getAtom().getConcept().getDefaultPreferredName();
                String contviewhandle = myAtomContentViewMemberDTO.getContentViewHandle();
                String termtype = myAtomContentViewMemberDTO.getAtom().getTermType();
               
                System.out.println(ui+"|"+defprefname+"|"+contviewhandle+"|"+termtype);
                }
            break;
            
            case "getContentViewAtomMembers": myAtomContentViewMember = utsContentService.getContentViewAtomMembers(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C1700357", myPsf);
            for (int i = 0; i < myAtomContentViewMember.size(); i++) {

            	AtomContentViewMemberDTO myAtomContentViewMemberDTO = myAtomContentViewMember.get(i);

                String ui = myAtomContentViewMemberDTO.getAtom().getConcept().getUi();
                String defprefname = myAtomContentViewMemberDTO.getAtom().getConcept().getDefaultPreferredName();
                String contviewhandle = myAtomContentViewMemberDTO.getContentViewHandle();
                String termtype = myAtomContentViewMemberDTO.getAtom().getTermType();
               
                System.out.println(ui+"|"+defprefname+"|"+contviewhandle+"|"+termtype);
                }
            break;
        	default: out.println("Unrecognized input ");
        	break; 
            }
            

            
  
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
