package gov.nih.nlm.uts.qa.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.metadata.AdditionalRelationLabelDTO;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;
import gov.nih.nlm.uts.webservice.semnet.SemanticNetworkRelationLabelDTO;
import gov.nih.nlm.uts.webservice.semnet.SemanticTypeDTO;
import gov.nih.nlm.uts.webservice.semnet.SemanticTypeRelationDTO;
import gov.nih.nlm.uts.webservice.semnet.UtsWsSemanticNetworkController;
import gov.nih.nlm.uts.webservice.semnet.UtsWsSemanticNetworkControllerImplService;

import java.util.ArrayList;

public class SemanticTypeRelationDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsSemanticNetworkController  utsSemanticNetworkService = (new UtsWsSemanticNetworkControllerImplService()).getUtsWsSemanticNetworkControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SemanticTypeRelationDTOClient (String username, String password) {
	this.username = username;
	this.password = password;
	
}

	public static String ticketGrantingTicket() throws Exception{
	
    	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);

        //use the Proxy Grant Ticket to get a Single Use Ticket
        return ticketGrantingTicket;
    	
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			java.util.List<SemanticTypeRelationDTO> myarrSemTyRelDTO = new ArrayList<SemanticTypeRelationDTO>();
			SemanticTypeRelationDTO mySemTyRelDTO = new SemanticTypeRelationDTO();
			SemanticTypeRelationDTOClient AdditionalRelLabelClient = new SemanticTypeRelationDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getSemanticTypeRelations": myarrSemTyRelDTO = utsSemanticNetworkService.getSemanticTypeRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "T005");
            break;
            case "getInverseSemanticTypeRelations": myarrSemTyRelDTO = utsSemanticNetworkService.getInverseSemanticTypeRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "T046");
            break;
            case "getInheritedSemanticTypeRelations": myarrSemTyRelDTO = utsSemanticNetworkService.getInheritedSemanticTypeRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "T037");
            break;
            case "getInverseInheritedSemanticTypeRelations": myarrSemTyRelDTO = utsSemanticNetworkService.getInverseInheritedSemanticTypeRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "T037");
            break;
            case "getAllSemanticTypeRelations": myarrSemTyRelDTO = utsSemanticNetworkService.getAllSemanticTypeRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getSemanticTypeRelation": mySemTyRelDTO = utsSemanticNetworkService.getSemanticTypeRelation(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "T046", "T152","T037");
            SemanticNetworkRelationLabelDTO relLabel = mySemTyRelDTO.getRelationLabel();
            SemanticTypeDTO relSemType = mySemTyRelDTO.getRelatedSemanticType();
            String handle = mySemTyRelDTO.getHandle();
            boolean isBlocked = mySemTyRelDTO.isBlocked();
            System.out.println("relLabel:"+relLabel+"relLabel:"+relLabel+"|handle:"+handle);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrSemTyRelDTO.size(); i++) {

            SemanticTypeRelationDTO mySemTyRel = myarrSemTyRelDTO.get(i);
            SemanticNetworkRelationLabelDTO relLabel = mySemTyRel.getRelationLabel();
            SemanticTypeDTO relSemLabel = mySemTyRel.getRelatedSemanticType();
            String handle = mySemTyRel.getHandle();
            boolean isBlocked = mySemTyRel.isBlocked();

            System.out.println("relLabel:"+relLabel+"relSemLabel:"+relSemLabel+"|handle:"+handle+"|isBlocked:"+isBlocked);
            }
            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
