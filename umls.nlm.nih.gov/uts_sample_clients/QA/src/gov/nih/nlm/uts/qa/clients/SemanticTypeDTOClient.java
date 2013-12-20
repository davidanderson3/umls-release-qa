package gov.nih.nlm.uts.qa.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;
import gov.nih.nlm.uts.webservice.semnet.SemanticTypeDTO;
import gov.nih.nlm.uts.webservice.semnet.UtsWsSemanticNetworkController;
import gov.nih.nlm.uts.webservice.semnet.UtsWsSemanticNetworkControllerImplService;
import java.util.ArrayList;

public class SemanticTypeDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsSemanticNetworkController  utsSemanticNetworkService = (new UtsWsSemanticNetworkControllerImplService()).getUtsWsSemanticNetworkControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SemanticTypeDTOClient (String username, String password) {
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

			java.util.List<SemanticTypeDTO> myarrSemTyRelDTO = new ArrayList<SemanticTypeDTO>();
			SemanticTypeDTOClient SemTypeDTOClient = new SemanticTypeDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getSemanticTypesByGroup": myarrSemTyRelDTO = utsSemanticNetworkService.getSemanticTypesByGroup(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "ACTI");
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrSemTyRelDTO.size(); i++) {

            SemanticTypeDTO mySemTyRel = myarrSemTyRelDTO.get(i);
            String abbreviation = mySemTyRel.getAbbreviation();
            String definition = mySemTyRel.getDefinition();
            int childCnt = mySemTyRel.getChildCount();
            int relCnt = mySemTyRel.getRelationCount();
            String example = mySemTyRel.getExample();
            String nonHuman = mySemTyRel.getNonHuman();
            String treeNum = mySemTyRel.getTreeNumber();
            String ui = mySemTyRel.getUi();
            String value = mySemTyRel.getValue();
            
            System.out.println(abbreviation+"|"+definition+"|"
            +childCnt+"|"+relCnt+"|"+example+"|"+nonHuman+"|"+treeNum+"|"+ui+"|"+value);
             }
            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
