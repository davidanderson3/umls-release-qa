package gov.nih.nlm.uts.clients;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;
import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.security.*;

public class SubsetDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2014AB";
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
            int pageNum = 1;
            
            
            java.util.List<SubsetDTO> mySubsetsDTO = new ArrayList<SubsetDTO>();
            SubsetDTO mySubsetDTO = new SubsetDTO();
            List<SourceConceptSubsetMemberDTO> mySubsetMembersDTO = new ArrayList<SourceConceptSubsetMemberDTO>();
            List<SourceConceptSubsetMemberDTO> mySubsetMemberships = new ArrayList<SourceConceptSubsetMemberDTO>();

            
            switch (method) {
            
            //show me all the available subsets
            case "getSubsets": mySubsetsDTO = utsContentService.getSubsets(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, myPsf); 
            for (int i = 0; i < mySubsetsDTO.size(); i++) {

            	SubsetDTO mySubsets = mySubsetsDTO.get(i);
                String ui = mySubsets.getUi();
                String name = mySubsets.getName();
                System.out.println(ui+"|"+name);  
                }
            
            break;
            
            //what is the information about a given subset?
            case "getSubset": mySubsetDTO = utsContentService.getSubset(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C1368722");
            String ui = mySubsetDTO.getUi();
            String name = mySubsetDTO.getName();
            System.out.println(ui+"|"+name);
            
            break;
            
            //to which subsets does a given source concept belong? *** not working ***?
            case "getSourceConceptSubsetMemberships": mySubsetMemberships = utsContentService.getSourceConceptSubsetMemberships(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "SNOMEDCT_US", "141731000119108", myPsf);
            for(SourceConceptSubsetMemberDTO mySubsetMemberDTO: mySubsetMemberships) {
            	
            	String scui = mySubsetMemberDTO.getSourceConcept().getUi();
            	String term = mySubsetMemberDTO.getSourceConcept().getDefaultPreferredName();
            	String subsetUi = mySubsetMemberDTO.getSubsetHandle();
            	
            	System.out.println (scui+"|"+term+"|"+subsetUi);
            	
            }
            
            break;
            
            //what are the members of a given subset?
            case "getSubsetSourceConceptMembers": mySubsetMembersDTO = utsContentService.getSubsetSourceConceptMembers(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C3714473", myPsf);
            
            System.out.println("id|term");
            for(SourceConceptSubsetMemberDTO subsetMember:mySubsetMembersDTO) {
            	
            	String id = subsetMember.getSourceConcept().getUi();
            	String term = subsetMember.getSourceConcept().getDefaultPreferredName();
            	System.out.println(id+"|"+term);
            	
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
