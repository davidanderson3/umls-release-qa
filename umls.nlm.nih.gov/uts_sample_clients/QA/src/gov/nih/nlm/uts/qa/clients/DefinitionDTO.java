package gov.nih.nlm.uts.qa.clients;

import static java.lang.System.out;

import java.awt.List;
import java.util.ArrayList;

import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.security.*;

public class DefinitionDTO {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2013AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public DefinitionDTO (String username, String password) {
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

            gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
            myPsf.getIncludedSources().add("NCI");
            java.util.List<gov.nih.nlm.uts.webservice.content.DefinitionDTO> myDefinition = new ArrayList<gov.nih.nlm.uts.webservice.content.DefinitionDTO>();
            DefinitionDTO AttDTOClient = new DefinitionDTO(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getConceptDefinitions": myDefinition = utsContentService.getConceptDefinitions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C0004775", myPsf);
            break;
            case "getAtomDefinitions": myDefinition = utsContentService.getAtomDefinitions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "", myPsf); 
        	break;
            case "getCodeDefinitions": myDefinition = utsContentService.getCodeDefinitions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "", "", myPsf); 
        	break;
            case "getSourceConceptDefinitions": myDefinition = utsContentService.getSourceConceptDefinitions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "M0014340", "MSH", myPsf); 
        	break;
        	case "getSourceDescriptorDefinitions": myDefinition = utsContentService.getSourceDescriptorDefinitions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "10042784", "MDR", myPsf);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myDefinition.size(); i++) {

            gov.nih.nlm.uts.webservice.content.DefinitionDTO myDefDTO = myDefinition.get(i);
            String rootSource = myDefDTO.getRootSource();
            //String definitionName = myDefDTO.getName();
            String value = myDefDTO.getValue();
            String ui = myDefDTO.getUi();
            		
            System.out.println(rootSource+"|"+value+"|"+ui);
            }
            
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}


