package gov.nih.nlm.uts.qa.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.metadata.TermTypeDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;

public class TermTypeDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2011AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public TermTypeDTOClient (String username, String password) {
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

			java.util.List<TermTypeDTO> myarrTermTyp = new ArrayList<TermTypeDTO>();
			TermTypeDTO myTermTyp = new TermTypeDTO();
			TermTypeDTOClient TermTypClient = new TermTypeDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAllTermTypes": myarrTermTyp = utsMetadataService.getAllTermTypes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getTermType": myTermTyp = utsMetadataService.getTermType(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "MH");
            String expandedForm = myTermTyp.getExpandedForm();
            String srcTrmTyp = myTermTyp.getSourceTermType();
            String abbreviation = myTermTyp.getAbbreviation();
            boolean obsolete = myTermTyp.isObsolete();
            String classType = myTermTyp.getClassType();
            
            System.out.println("ExpandedForm:"+expandedForm+"SrcTermTypr:"+srcTrmTyp+"Abbreviation:"+abbreviation+"|Obsolete:"+obsolete+"|ClassType:"+classType);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrTermTyp.size(); i++) {

            TermTypeDTO myTermTypDTO = myarrTermTyp.get(i);
            String expandedForm = myTermTypDTO.getExpandedForm();
            String srcTrmTyp = myTermTypDTO.getSourceTermType();
            String abbreviation = myTermTypDTO.getAbbreviation();
            boolean obsolete = myTermTypDTO.isObsolete();
            String classType = myTermTypDTO.getClassType();
            
            System.out.println("ExpandedForm:"+expandedForm+"SrcTermTypr:"+srcTrmTyp+"Abbreviation:"+abbreviation+"|Obsolete:"+obsolete+"|ClassType:"+classType);
            }
            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
