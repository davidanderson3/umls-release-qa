package gov.nih.nlm.lo.umls.uts.clients;

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
        //System.out.println("tgt: "+ticketGrantingTicket);  

        //use the Proxy Grant Ticket to get a Single Use Ticket
       // String singleUseTicket = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
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
            String relAbbr = mySemTyRelDTO.getRelationLabel().getAbbreviation();
            String relDef = mySemTyRelDTO.getRelationLabel().getDefinition();
            String relExample = mySemTyRelDTO.getRelationLabel().getExample();
            String relInvLabel = mySemTyRelDTO.getRelationLabel().getInverseLabel();
            String relTreeNum = mySemTyRelDTO.getRelationLabel().getTreeNumber();
            String relUi = mySemTyRelDTO.getRelationLabel().getUi();
            String relUsageNote = mySemTyRelDTO.getRelationLabel().getUsageNote();
            int relChildCnt = mySemTyRelDTO.getRelationLabel().getChildCount();
            int relRelCnt = mySemTyRelDTO.getRelationLabel().getRelationCount();
            
            String SemTypeAbbr = mySemTyRelDTO.getRelatedSemanticType().getAbbreviation();
            String SemTypeDef = mySemTyRelDTO.getRelatedSemanticType().getDefinition();
            String SemTypeExample = mySemTyRelDTO.getRelatedSemanticType().getExample();
            String SemTypeNonhuman = mySemTyRelDTO.getRelatedSemanticType().getNonHuman();
            String SemTypeTreeNum = mySemTyRelDTO.getRelatedSemanticType().getTreeNumber();
            String SemTypeUi = mySemTyRelDTO.getRelatedSemanticType().getUi();
            String SemTypeVal = mySemTyRelDTO.getRelatedSemanticType().getValue();
            String SemTypeUsage = mySemTyRelDTO.getRelatedSemanticType().getUsageNote();
            int SemTypeChildCnt = mySemTyRelDTO.getRelatedSemanticType().getChildCount();
            int SemTypeRelCnt = mySemTyRelDTO.getRelatedSemanticType().getRelationCount();
            System.out.println(relAbbr+"|"+relDef+"|"+relExample+"|"+relInvLabel+
            		"|"+relInvLabel+"|"+relTreeNum+"|"+relUi+"|"+relUsageNote+"|"+relChildCnt+"|"+relRelCnt+"|"+"\n");
            System.out.println(SemTypeAbbr+"|"+SemTypeDef+"|"+SemTypeExample+"|"+SemTypeNonhuman+
            		"|"+SemTypeTreeNum+"|"+SemTypeUi+"|"+SemTypeVal+"|"+SemTypeUsage+"|"+SemTypeChildCnt+"|"+SemTypeRelCnt+"|"+"\n\n");            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrSemTyRelDTO.size(); i++) {

            SemanticTypeRelationDTO mySemTyRel = myarrSemTyRelDTO.get(i);
            String relAbbr = mySemTyRel.getRelationLabel().getAbbreviation();
            String relDef = mySemTyRel.getRelationLabel().getDefinition();
            String relExample = mySemTyRel.getRelationLabel().getExample();
            String relInvLabel = mySemTyRel.getRelationLabel().getInverseLabel();
            String relTreeNum = mySemTyRel.getRelationLabel().getTreeNumber();
            String relUi = mySemTyRel.getRelationLabel().getUi();
            String relUsageNote = mySemTyRel.getRelationLabel().getUsageNote();
            int relChildCnt = mySemTyRel.getRelationLabel().getChildCount();
            int relRelCnt = mySemTyRel.getRelationLabel().getRelationCount();
            
            String SemTypeAbbr = mySemTyRel.getRelatedSemanticType().getAbbreviation();
            String SemTypeDef = mySemTyRel.getRelatedSemanticType().getDefinition();
            String SemTypeExample = mySemTyRel.getRelatedSemanticType().getExample();
            String SemTypeNonhuman = mySemTyRel.getRelatedSemanticType().getNonHuman();
            String SemTypeTreeNum = mySemTyRel.getRelatedSemanticType().getTreeNumber();
            String SemTypeUi = mySemTyRel.getRelatedSemanticType().getUi();
            String SemTypeVal = mySemTyRel.getRelatedSemanticType().getValue();
            String SemTypeUsage = mySemTyRel.getRelatedSemanticType().getUsageNote();
            int SemTypeChildCnt = mySemTyRel.getRelatedSemanticType().getChildCount();
            int SemTypeRelCnt = mySemTyRel.getRelatedSemanticType().getRelationCount();

            System.out.println(relAbbr+"|"+relDef+"|"+relExample+"|"+relInvLabel+
            		"|"+relInvLabel+"|"+relTreeNum+"|"+relUi+"|"+relUsageNote+"|"+relChildCnt+"|"+relRelCnt+"|"+"\n");
            System.out.println(SemTypeAbbr+"|"+SemTypeDef+"|"+SemTypeExample+"|"+SemTypeNonhuman+
            		"|"+SemTypeTreeNum+"|"+SemTypeUi+"|"+SemTypeVal+"|"+SemTypeUsage+"|"+SemTypeChildCnt+"|"+SemTypeRelCnt+"|"+"\n\n");
            }
            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
