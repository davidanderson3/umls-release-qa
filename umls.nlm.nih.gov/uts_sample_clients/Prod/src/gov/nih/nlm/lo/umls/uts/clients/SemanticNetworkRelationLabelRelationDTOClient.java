package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;
import gov.nih.nlm.uts.webservice.semnet.SemanticNetworkRelationLabelRelationDTO;
import gov.nih.nlm.uts.webservice.semnet.UtsWsSemanticNetworkController;
import gov.nih.nlm.uts.webservice.semnet.UtsWsSemanticNetworkControllerImplService;

import java.util.ArrayList;
import java.util.List;

public class SemanticNetworkRelationLabelRelationDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2011AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsSemanticNetworkController  utsSemanticNetworkService = (new UtsWsSemanticNetworkControllerImplService()).getUtsWsSemanticNetworkControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SemanticNetworkRelationLabelRelationDTOClient (String username, String password) {
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
			
			java.util.List<SemanticNetworkRelationLabelRelationDTO> myarrSemNetRelLabel = new ArrayList<SemanticNetworkRelationLabelRelationDTO>();
			SemanticNetworkRelationLabelRelationDTO mySemNetRelLabel = new SemanticNetworkRelationLabelRelationDTO();
			SemanticNetworkRelationLabelRelationDTOClient SemNetRelLabelClient = new SemanticNetworkRelationLabelRelationDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAllSemanticNetworkRelationLabelRelations": myarrSemNetRelLabel = utsSemanticNetworkService.getAllSemanticNetworkRelationLabelRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getInverseInheritedSemanticNetworkRelationLabelRelations": myarrSemNetRelLabel = utsSemanticNetworkService.getInverseInheritedSemanticNetworkRelationLabelRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "T166");
            break;
            case "getInheritedSemanticNetworkRelationLabelRelations": myarrSemNetRelLabel = utsSemanticNetworkService.getInheritedSemanticNetworkRelationLabelRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "T143");
            break;
            case "getInverseSemanticNetworkRelationLabelRelations": myarrSemNetRelLabel = utsSemanticNetworkService.getInverseSemanticNetworkRelationLabelRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "T151");
            break;
            case "getSemanticNetworkRelationLabelRelationsForPair": myarrSemNetRelLabel = utsSemanticNetworkService.getSemanticNetworkRelationLabelRelationsForPair(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "T151", "T139");
            break;
            case "getSemanticNetworkRelationLabelRelations": myarrSemNetRelLabel = utsSemanticNetworkService.getSemanticNetworkRelationLabelRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "T151");
            break;
            case "getSemanticNetworkRelationLabelRelation": mySemNetRelLabel = utsSemanticNetworkService.getSemanticNetworkRelationLabelRelation(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "T151", "T186", "T139");
            String handle = mySemNetRelLabel.getHandle();
            String SnRelLabAbbr = mySemNetRelLabel.getSnRelationLabel().getAbbreviation();
            String SNRelLabAbbr = mySemNetRelLabel.getRelatedSNRelationLabel().getAbbreviation();

            String abbreviation = mySemNetRelLabel.getRelationLabel().getAbbreviation();
            String definition = mySemNetRelLabel.getRelationLabel().getDefinition();
            String Example = mySemNetRelLabel.getRelationLabel().getExample();
            String InvLabel = mySemNetRelLabel.getRelationLabel().getInverseLabel();
            String TreeNum = mySemNetRelLabel.getRelationLabel().getTreeNumber();
            String Ui = mySemNetRelLabel.getRelationLabel().getUi();
            String UsageNote = mySemNetRelLabel.getRelationLabel().getUsageNote();
            int ChildCnt = mySemNetRelLabel.getRelationLabel().getChildCount();
            int RelCnt = mySemNetRelLabel.getRelationLabel().getRelationCount();
            System.out.println(handle+"|"+SnRelLabAbbr+"|"+SNRelLabAbbr+"|"+abbreviation+"|"+definition+"|"+Example+"|"+InvLabel+
            		"|"+InvLabel+"|"+TreeNum+"|"+Ui+"|"+UsageNote+"|"+ChildCnt+"|"+RelCnt+"|"+"\n");
                     break;
            
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrSemNetRelLabel.size(); i++) {

                SemanticNetworkRelationLabelRelationDTO mySemTyRel = myarrSemNetRelLabel.get(i);
                String handle = mySemTyRel.getHandle();
                String SnRelLabAbbr = mySemTyRel.getSnRelationLabel().getAbbreviation();
                String SNRelLabAbbr = mySemTyRel.getRelatedSNRelationLabel().getAbbreviation();

                String abbreviation = mySemTyRel.getRelationLabel().getAbbreviation();
                String definition = mySemTyRel.getRelationLabel().getDefinition();
                String Example = mySemTyRel.getRelationLabel().getExample();
                String InvLabel = mySemTyRel.getRelationLabel().getInverseLabel();
                String TreeNum = mySemTyRel.getRelationLabel().getTreeNumber();
                String Ui = mySemTyRel.getRelationLabel().getUi();
                String UsageNote = mySemTyRel.getRelationLabel().getUsageNote();
                int ChildCnt = mySemTyRel.getRelationLabel().getChildCount();
                int RelCnt = mySemTyRel.getRelationLabel().getRelationCount();
                
                System.out.println(handle+"|"+SnRelLabAbbr+"|"+SNRelLabAbbr+"|"+abbreviation+"|"+definition+"|"+Example+"|"+InvLabel+
                		"|"+InvLabel+"|"+TreeNum+"|"+Ui+"|"+UsageNote+"|"+ChildCnt+"|"+RelCnt+"|"+"\n");
                       }

                    	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


}
