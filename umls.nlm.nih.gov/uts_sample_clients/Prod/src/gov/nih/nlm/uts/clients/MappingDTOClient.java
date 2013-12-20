package gov.nih.nlm.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.content.AtomRelationDTO;
import gov.nih.nlm.uts.webservice.content.MapObjectDTO;
import gov.nih.nlm.uts.webservice.content.MappingDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;

public class MappingDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2011AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    public MappingDTOClient(String username, String password) {
	this.username = username;
	this.password = password;
	
}

	public static String ticketGrantingTicket() throws Exception{
	
    	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);

        //use the Proxy Grant Ticket to get a Single Use Ticket
       // String singleUseTicket = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
        return ticketGrantingTicket;
    	
    }
    
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			MappingDTOClient MappingClient = new MappingDTOClient(args[0],args[1]);
        	String method = args[2];

        	gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
        	java.util.List<MappingDTO> myarrMapClient = new ArrayList<MappingDTO>();
            
            switch (method) {
            case "getMappings": myarrMapClient = utsContentService.getMappings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease,"C3165219",myPsf); 
        	break;
        	case "getMapObjectToMapping": myarrMapClient = utsContentService.getMapObjectToMapping(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease,"466.19", myPsf);
            break;
        	case "getMapObjectFromMapping": myarrMapClient = utsContentService.getMapObjectFromMapping(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "718004", myPsf);
            break;
        	default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrMapClient.size(); i++) {

                MappingDTO myMapDTO = myarrMapClient.get(i);

                String label = myMapDTO.getLabel();
                String addLabel = myMapDTO.getAdditionalLabel();
                String rank = myMapDTO.getRank();
                String restriction = myMapDTO.getRestriction();
                String rootSource = myMapDTO.getRootSource();
                String sourceUi = myMapDTO.getSourceUi();
                String rule = myMapDTO.getRule();
                String subsetId = myMapDTO.getSubsetId();
                int attrCount = myMapDTO.getAttributeCount();
                String ui = myMapDTO.getUi();

                int frmAttrCnt = myMapDTO.getMapFrom().getAttributeCount();
                String frmExpression = myMapDTO.getMapFrom().getExpression();
                String frmRootSrc = myMapDTO.getMapFrom().getRootSource();
                String frmUi = myMapDTO.getMapFrom().getUi();
                String frmType = myMapDTO.getMapFrom().getType();
                String frmSrcUi = myMapDTO.getMapFrom().getRule();
                
                int toAttrCnt = myMapDTO.getMapTo().getAttributeCount();
                String toExpression = myMapDTO.getMapTo().getExpression();
                String toRootSrc = myMapDTO.getMapTo().getRootSource();
                String toUi = myMapDTO.getMapTo().getUi();
                String toType = myMapDTO.getMapTo().getType();
                String toSrcUi = myMapDTO.getMapTo().getSourceUi();


                System.out.println(label+"|"+addLabel+"|"+rank+"|"+restriction+"|"+rootSource+"|"+sourceUi+"|"+rule+"|"+subsetId+"|"+attrCount+"|"+ui);
                System.out.println("MapFrom:\n"+frmAttrCnt+"|"+frmExpression+"|"+frmRootSrc+"|"+frmUi+"|"+frmType+"|"+frmSrcUi);
                System.out.println("MapTo:\n"+toAttrCnt+"|"+toExpression+"|"+toRootSrc+"|"+toUi+"|"+toType+"|"+toSrcUi+"\n");
                
                }
  
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
