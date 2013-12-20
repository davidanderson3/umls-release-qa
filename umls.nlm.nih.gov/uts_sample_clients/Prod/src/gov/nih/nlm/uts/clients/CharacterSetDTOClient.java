package gov.nih.nlm.uts.clients;

import static java.lang.System.out;

import java.util.ArrayList;

import gov.nih.nlm.uts.webservice.metadata.CharacterSetDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class CharacterSetDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public CharacterSetDTOClient (String username, String password) {
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

			java.util.List<CharacterSetDTO> myarrCharSet = new ArrayList<CharacterSetDTO>();
            CharacterSetDTO myCharSet = new CharacterSetDTO();
            CharacterSetDTOClient CharSetDTOClient = new CharacterSetDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAllCharacterSets": myarrCharSet = utsMetadataService.getAllCharacterSets(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getCharacterSet": myCharSet = utsMetadataService.getCharacterSet(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "UTF-8");
            String Name = myCharSet.getName();
            String abbreviation = myCharSet.getAbbreviation();
            String expandedForm = myCharSet.getExpandedForm();
            String classType = myCharSet.getClassType();
            
            System.out.println("Name:"+Name+"|abbreviation:"+abbreviation+"|expandedForm:"+expandedForm+"|classType:"+classType);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrCharSet.size(); i++) {

            CharacterSetDTO myCharSetDTO = myarrCharSet.get(i);
            String Name = myCharSetDTO.getName();
            String abbreviation = myCharSetDTO.getAbbreviation();
            String expandedForm = myCharSetDTO.getExpandedForm();
            String classType = myCharSetDTO.getClassType();
            
            System.out.println("Name:"+Name+"|abbreviation:"+abbreviation+"|expandedForm:"+expandedForm+"|classType:"+classType);
            }
            
            
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
