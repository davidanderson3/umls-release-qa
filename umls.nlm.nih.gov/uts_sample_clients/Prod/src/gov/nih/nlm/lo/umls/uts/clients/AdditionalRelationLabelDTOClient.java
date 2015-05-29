package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.metadata.AdditionalRelationLabelDTO;
import gov.nih.nlm.uts.webservice.metadata.RelationLabelDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;

public class AdditionalRelationLabelDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public AdditionalRelationLabelDTOClient (String username, String password) {
	this.username = username;
	this.password = password;
	
}

	public static String ticketGrantingTicket() throws Exception{
	
    	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
        System.out.println("tgt: "+ticketGrantingTicket);  

        //use the Proxy Grant Ticket to get a Single Use Ticket
       // String singleUseTicket = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
        return ticketGrantingTicket;
    	
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			java.util.List<AdditionalRelationLabelDTO> myarrAdditionalRelationLabel = new ArrayList<AdditionalRelationLabelDTO>();
			AdditionalRelationLabelDTO myAdditionalRelationLabel = new AdditionalRelationLabelDTO();
			AdditionalRelationLabelDTOClient AdditionalRelLabelClient = new AdditionalRelationLabelDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAllAdditionalRelationLabels": myarrAdditionalRelationLabel = utsMetadataService.getAllAdditionalRelationLabels(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getAdditionalRelationLabel": myAdditionalRelationLabel = utsMetadataService.getAdditionalRelationLabel(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "origin_of");
            String expandedForm = myAdditionalRelationLabel.getExpandedForm();
            String abbreviation = myAdditionalRelationLabel.getAbbreviation();
            String inverseExpForm = myAdditionalRelationLabel.getInverse().getExpandedForm();
            String inverseAbbreviation = myAdditionalRelationLabel.getInverse().getAbbreviation();

            System.out.println("ExpandedForm:"+expandedForm+"|Abbreviation:"+abbreviation+"|inverseExpForm:"+inverseExpForm+"|inverseAbbreviation:"+inverseAbbreviation);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrAdditionalRelationLabel.size(); i++) {

            AdditionalRelationLabelDTO myAddRelationLabelDTO = myarrAdditionalRelationLabel.get(i);
            String expandedForm = myAddRelationLabelDTO.getExpandedForm();
            String abbreviation = myAddRelationLabelDTO.getAbbreviation();
            String inverseExpForm = myAddRelationLabelDTO.getInverse().getExpandedForm();
            String inverseAbbreviation = myAddRelationLabelDTO.getInverse().getAbbreviation();

            System.out.println("ExpandedForm:"+expandedForm+"|Abbreviation:"+abbreviation+"|inverseExpForm:"+inverseExpForm+"|inverseAbbreviation:"+inverseAbbreviation);
            }
            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
