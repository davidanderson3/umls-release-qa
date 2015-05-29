package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.metadata.RelationLabelDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;

public class RelationLabelDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2013AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public RelationLabelDTOClient (String username, String password) {
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

			java.util.List<RelationLabelDTO> myarrRelationLabel = new ArrayList<RelationLabelDTO>();
			RelationLabelDTO myRelationLabel = new RelationLabelDTO();
			RelationLabelDTOClient RelLabelClient = new RelationLabelDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAllRelationLabels": myarrRelationLabel = utsMetadataService.getAllRelationLabels(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getRelationLabel": myRelationLabel = utsMetadataService.getRelationLabel(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "RN");
            String expandedForm = myRelationLabel.getExpandedForm();
            String abbreviation = myRelationLabel.getAbbreviation();
            RelationLabelDTO inverse = myRelationLabel.getInverse();

            System.out.println("ExpandedForm:"+expandedForm+"\nAbbreviation:"+abbreviation+"\nInverse:"+inverse);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrRelationLabel.size(); i++) {

            RelationLabelDTO myRelationLabelDTO = myarrRelationLabel.get(i);
            String expandedForm = myRelationLabelDTO.getExpandedForm();
            String abbreviation = myRelationLabelDTO.getAbbreviation();
            RelationLabelDTO inverse = myRelationLabelDTO.getInverse();

            System.out.println("ExpandedForm:"+expandedForm+"\nAbbreviation:"+abbreviation+"\nInverse:"+inverse);
            }
            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
