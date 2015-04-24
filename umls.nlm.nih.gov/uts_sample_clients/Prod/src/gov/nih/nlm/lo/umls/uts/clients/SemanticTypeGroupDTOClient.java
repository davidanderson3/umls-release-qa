package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;
import gov.nih.nlm.uts.webservice.semnet.SemanticTypeGroupDTO;
import gov.nih.nlm.uts.webservice.semnet.SemanticTypeRelationDTO;
import gov.nih.nlm.uts.webservice.semnet.UtsWsSemanticNetworkController;
import gov.nih.nlm.uts.webservice.semnet.UtsWsSemanticNetworkControllerImplService;

import java.util.ArrayList;

public class SemanticTypeGroupDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsSemanticNetworkController  utsSemanticNetworkService = (new UtsWsSemanticNetworkControllerImplService()).getUtsWsSemanticNetworkControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SemanticTypeGroupDTOClient (String username, String password) {
	this.username = username;
	this.password = password;
	
}

	public static String ticketGrantingTicket() throws Exception{
	
    	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
        //System.out.println("tgt: "+ticketGrantingTicket);  

        //use the Proxy Grant Ticket to get a Single Use Ticket
       // String singleUseTicket = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
        return ticketGrantingTicket;
    	
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			java.util.List<SemanticTypeGroupDTO> myarrSemTyRelDTO = new ArrayList<SemanticTypeGroupDTO>();
			SemanticTypeGroupDTO mySemTyRelDTO = new SemanticTypeGroupDTO();
			SemanticTypeGroupDTOClient AdditionalRelLabelClient = new SemanticTypeGroupDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAllSemanticTypeGroups": myarrSemTyRelDTO = utsSemanticNetworkService.getAllSemanticTypeGroups(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getSemanticTypeGroup": mySemTyRelDTO = utsSemanticNetworkService.getSemanticTypeGroup(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "GEOG");
            String abbreviation = mySemTyRelDTO.getAbbreviation();
            String expandedForm = mySemTyRelDTO.getExpandedForm();
            int semTypeCnt = mySemTyRelDTO.getSemanticTypeCount();
            
            System.out.println(abbreviation+"|"+expandedForm+"|"+semTypeCnt);
        	break; 
            default: out.println("Unrecognized input ");
            }
            
            for (int i = 0; i < myarrSemTyRelDTO.size(); i++) {

            SemanticTypeGroupDTO mySemTyRel = myarrSemTyRelDTO.get(i);
            String abbreviation = mySemTyRel.getAbbreviation();
            String expandedForm = mySemTyRel.getExpandedForm();
            int semTypeCnt = mySemTyRel.getSemanticTypeCount();
            
            System.out.println(abbreviation+"|"+expandedForm+"|"+semTypeCnt);
             }
            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


}
