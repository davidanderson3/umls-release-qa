package gov.nih.nlm.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;
import gov.nih.nlm.uts.webservice.semnet.SemanticNetworkRelationLabelDTO;
import gov.nih.nlm.uts.webservice.semnet.SemanticTypeGroupDTO;
import gov.nih.nlm.uts.webservice.semnet.UtsWsSemanticNetworkController;
import gov.nih.nlm.uts.webservice.semnet.UtsWsSemanticNetworkControllerImplService;

import java.util.ArrayList;

public class SemanticNetworkRelationLabelDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsSemanticNetworkController  utsSemanticNetworkService = (new UtsWsSemanticNetworkControllerImplService()).getUtsWsSemanticNetworkControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SemanticNetworkRelationLabelDTOClient (String username, String password) {
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

			java.util.List<SemanticNetworkRelationLabelDTO> myarrSemTyRelDTO = new ArrayList<SemanticNetworkRelationLabelDTO>();
			SemanticNetworkRelationLabelDTO mySemTyRelDTO = new SemanticNetworkRelationLabelDTO();
			SemanticNetworkRelationLabelDTOClient AdditionalRelLabelClient = new SemanticNetworkRelationLabelDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAllSemanticNetworkRelationLabels": myarrSemTyRelDTO = utsSemanticNetworkService.getAllSemanticNetworkRelationLabels(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
        	break; 
            case "getSemanticNetworkRelationLabel": mySemTyRelDTO = utsSemanticNetworkService.getSemanticNetworkRelationLabel(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "T172");
            String abbreviation = mySemTyRelDTO.getAbbreviation();
            String definition = mySemTyRelDTO.getDefinition();
            int childCnt = mySemTyRelDTO.getChildCount();
            int relCnt = mySemTyRelDTO.getRelationCount();
            String example = mySemTyRelDTO.getExample();
            String nonHuman = mySemTyRelDTO.getInverseLabel();
            String treeNum = mySemTyRelDTO.getTreeNumber();
            String ui = mySemTyRelDTO.getUi();
            String value = mySemTyRelDTO.getLabel();
            System.out.println(abbreviation+"|"+definition+"|"
            +childCnt+"|"+relCnt+"|"+example+"|"+nonHuman+"|"+treeNum+"|"+ui+"|"+value);
            break;
            }
            
            for (int i = 0; i < myarrSemTyRelDTO.size(); i++) {

            SemanticNetworkRelationLabelDTO mySemTyRel = myarrSemTyRelDTO.get(i);
            String abbreviation = mySemTyRel.getAbbreviation();
            String definition = mySemTyRel.getDefinition();
            int childCnt = mySemTyRel.getChildCount();
            int relCnt = mySemTyRel.getRelationCount();
            String example = mySemTyRel.getExample();
            String nonHuman = mySemTyRel.getInverseLabel();
            String treeNum = mySemTyRel.getTreeNumber();
            String ui = mySemTyRel.getUi();
            String value = mySemTyRel.getLabel();
            
            System.out.println(abbreviation+"|"+definition+"|"
            +childCnt+"|"+relCnt+"|"+example+"|"+nonHuman+"|"+treeNum+"|"+ui+"|"+value);             }
            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


}
