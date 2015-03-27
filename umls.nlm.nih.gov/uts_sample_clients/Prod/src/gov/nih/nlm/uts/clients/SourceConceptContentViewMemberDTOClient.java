package gov.nih.nlm.uts.clients;

import java.util.ArrayList;
import java.util.List;

import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.security.*;

public class SourceConceptContentViewMemberDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SourceConceptContentViewMemberDTOClient(String username, String password,String umlsRelease) {
    	SourceConceptContentViewMemberDTOClient.username = username;
    	SourceConceptContentViewMemberDTOClient.password = password;
    	SourceConceptContentViewMemberDTOClient.umlsRelease = umlsRelease;
    	
	
    }


	public static String ticketGrantingTicket() throws Exception{
	   	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String tgt = securityService.getProxyGrantTicket(username, password);
        return tgt;
       } 
	
	
	public static void main(String[] args) {
		try {
			// Runtime properties
			SourceConceptContentViewMemberDTOClient SourceConceptContClient = new SourceConceptContentViewMemberDTOClient(args[0],args[1],args[2]);
            
        	String method = args[3];

            gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
            
            java.util.List<SourceConceptContentViewMemberDTO> mySourceConceptContentViewMember = new ArrayList<SourceConceptContentViewMemberDTO>();
                        
            switch (method) {
            case "getSourceConceptContentViewMemberships": mySourceConceptContentViewMember = utsContentService.getSourceConceptContentViewMemberships(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "427419006", "SNOMEDCT", myPsf); 
            break;
            
            
            case "getContentViewSourceConceptMembers": 
            int pageNum = 1;
            List<SourceConceptContentViewMemberDTO> mySourceConceptContentViewMembers = new ArrayList<SourceConceptContentViewMemberDTO>();
            
            do {	
            	myPsf.setPageNum(pageNum);
            	mySourceConceptContentViewMembers = utsContentService.getContentViewSourceConceptMembers(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C2711988", myPsf);
                for(SourceConceptContentViewMemberDTO contentViewMember:mySourceConceptContentViewMembers) {
            	String scui = contentViewMember.getSourceConcept().getUi();
            	String term = contentViewMember.getSourceConcept().getDefaultPreferredName();
            	String cvMemberId = contentViewMember.getUi();
            	System.out.println(cvMemberId+"|"+scui+"|"+term);
            	
            	//return of attribute info is not working.

            	/*List<AttributeDTO> contentViewMemberAttributes = utsContentService.getContentViewMemberAttributes(securityService.getProxyTicket(ticketGrantingTicket(),serviceName),umlsRelease, cvMemberId, myPsf);
            	    
            		for(AttributeDTO contentViewMemberAttribute:contentViewMemberAttributes) {
            		
            			String atn = contentViewMemberAttribute.getName();
            			String atv = contentViewMemberAttribute.getValue();
            			System.out.println(cvMemberId+"|"+scui+"|"+term+"|"+atn+"|"+atv);
            		}*/

                }pageNum++;
                
                
               }
            while (mySourceConceptContentViewMembers.size() > 0);
            break;
            
            
            
            
        	default: System.out.println("Unrecognized input ");
        	break; 
            }
            
            
        
            
  
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
