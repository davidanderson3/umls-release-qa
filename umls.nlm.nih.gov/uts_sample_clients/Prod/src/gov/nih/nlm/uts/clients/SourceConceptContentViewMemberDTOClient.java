package gov.nih.nlm.uts.clients;

import java.util.ArrayList;
import java.util.List;
import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.security.*;

public class SourceConceptContentViewMemberDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2013AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SourceConceptContentViewMemberDTOClient(String username, String password) {
    	SourceConceptContentViewMemberDTOClient.username = username;
    	SourceConceptContentViewMemberDTOClient.password = password;
	
    }


	public static String ticketGrantingTicket() throws Exception{
	   	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
        return ticketGrantingTicket;
       } 
	
	
	public static void main(String[] args) {
		try {
			// Runtime properties
			SourceConceptContentViewMemberDTOClient SourceConceptContClient = new SourceConceptContentViewMemberDTOClient(args[0],args[1]);
            
        	String method = args[2];

            gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
            
            java.util.List<SourceConceptContentViewMemberDTO> mySourceConceptContentViewMember = new ArrayList<SourceConceptContentViewMemberDTO>();
                        
            switch (method) {
            case "getSourceConceptContentViewMemberships": mySourceConceptContentViewMember = utsContentService.getSourceConceptContentViewMemberships(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "427419006", "SNOMEDCT", myPsf); 
            break;
            case "getContentViewSourceConceptMembers": mySourceConceptContentViewMember = utsContentService.getContentViewSourceConceptMembers(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C2711988", myPsf);
        	break;
        	default: System.out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < mySourceConceptContentViewMember.size(); i++) {

            	SourceConceptContentViewMemberDTO mySourceConceptCont = mySourceConceptContentViewMember.get(i);
                String ui = mySourceConceptCont.getSourceConcept().getUi();
            	String name = mySourceConceptCont.getSourceConcept().getDefaultPreferredName();
            	String cvId = mySourceConceptCont.getContentViewHandle();
            	ContentViewDTO myCv = utsContentService.getContentView(securityService.getProxyTicket(ticketGrantingTicket(), serviceName),umlsRelease, cvId);
                String cvName = myCv.getName();
            	
                               
                System.out.println(ui+"|"+name+"|"+cvId+"|"+cvName);
                }
            
  
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
