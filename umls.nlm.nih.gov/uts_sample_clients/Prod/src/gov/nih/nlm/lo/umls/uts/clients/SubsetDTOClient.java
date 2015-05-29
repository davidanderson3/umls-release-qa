package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;

import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.security.*;

public class SubsetDTOClient {
	private static String username = "";
    private static String password = ""; 
    private static String umlsRelease = "";
	static String serviceName = "http://umlsks.nlm.nih.gov";
	
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SubsetDTOClient(String username, String password,String umlsRelease) {
    	SubsetDTOClient.username = username;
    	SubsetDTOClient.password = password;
    	SubsetDTOClient.umlsRelease = umlsRelease;
	
    }
 
    

	public static String ticketGrantingTicket() throws Exception{
	   	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
        return ticketGrantingTicket;
       } 
	
	
	public static void main(String[] args) {
		try {
			// Runtime properties
			SubsetDTOClient client = new SubsetDTOClient(args[0],args[1],args[2]);
            
        	String method = args[3];
            String ticket = client.ticketGrantingTicket();
            gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
            int pageNum = 1;
            myPsf.setPageLn(25);
            myPsf.setIncludedLanguage("ENG");
            
            
            List<SubsetDTO> mySubsets = new ArrayList<SubsetDTO>();
            SubsetDTO mySubsetDTO = new SubsetDTO();
            List<SourceConceptSubsetMemberDTO> mySubsetMembersDTO = new ArrayList<SourceConceptSubsetMemberDTO>();
            List<SourceConceptSubsetMemberDTO> mySubsetMemberships = new ArrayList<SourceConceptSubsetMemberDTO>();

            
            switch (method) {
            
            //show me all the available subsets
            case "getSubsets": mySubsets = utsContentService.getSubsets(securityService.getProxyTicket(ticket, serviceName), umlsRelease, myPsf); 
            System.out.println(mySubsets.size());
            for (SubsetDTO subset:mySubsets) {

            
                String ui = subset.getUi();
                String scui = subset.getSourceUi();
                String name = subset.getName();
                System.out.println(ui+"|"+scui+"|"+name);  
                }
            
            break;
            
            //what is the information about a given subset?
            case "getSubset": mySubsetDTO = utsContentService.getSubset(securityService.getProxyTicket(ticket, serviceName), umlsRelease, "C3714473");
            String ui = mySubsetDTO.getUi();
            String name = mySubsetDTO.getName();
            System.out.println(ui+"|"+name);
            
            break;
            
            //to which subsets does a given source concept belong? *** not working ***?
            case "getSourceConceptSubsetMemberships": mySubsetMemberships = utsContentService.getSourceConceptSubsetMemberships(securityService.getProxyTicket(ticket, serviceName), umlsRelease, "SNOMEDCT_US", "141731000119108", myPsf);
            for(SourceConceptSubsetMemberDTO mySubsetMemberDTO: mySubsetMemberships) {
            	
            	String scui = mySubsetMemberDTO.getSourceConcept().getUi();
            	String term = mySubsetMemberDTO.getSourceConcept().getDefaultPreferredName();
            	String subsetUi = mySubsetMemberDTO.getSubsetHandle();
            	
            	System.out.println (scui+"|"+term+"|"+subsetUi);
            	
            }
            
            break;
            
            //what are the members of a given subset?
            case "getSubsetSourceConceptMembers": 
            do {	
            	myPsf.setPageNum(pageNum);
            	
            	gov.nih.nlm.uts.webservice.content.Psf mySubsetPsf = new gov.nih.nlm.uts.webservice.content.Psf(); 
            	//mySubsetPsf.setSortBy("NAME");
            	mySubsetMembersDTO = utsContentService.getSubsetSourceConceptMembers(securityService.getProxyTicket(ticket, serviceName), umlsRelease, "C3853365", myPsf);
                for(SourceConceptSubsetMemberDTO subsetMember:mySubsetMembersDTO) {
            	String id = subsetMember.getSourceConcept().getUi();
            	String term = subsetMember.getSourceConcept().getDefaultPreferredName();
            	String atui = subsetMember.getUi();
            
            	List<AttributeDTO> subsetMemberAttributes = utsContentService.getSubsetMemberAttributes(securityService.getProxyTicket(ticket, serviceName), umlsRelease, atui, mySubsetPsf);
            	    
            		for(AttributeDTO subsetMemberAttribute:subsetMemberAttributes) {
            			String atn = subsetMemberAttribute.getName();
            			String atv = subsetMemberAttribute.getValue();
            			String sourceAtui = subsetMemberAttribute.getSourceUi();
            			System.out.println(sourceAtui+"|"+id+"|"+term+"|"+atn+"|"+atv);
            		}

                }
                pageNum++;
                
               }
            while (mySubsetMembersDTO.size() > 0);
            break;
            
        	default: out.println("Unrecognized input ");
        	break; 
            }

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
