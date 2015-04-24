package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;

import java.lang.annotation.Annotation;
import java.security.ProtectionDomain;
import java.util.ArrayList;

import gov.nih.nlm.uts.webservice.metadata.CharacterSetDTO;
import gov.nih.nlm.uts.webservice.metadata.CooccurrenceTypeDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class CooccurrenceTypeDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2011AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public CooccurrenceTypeDTOClient (String username, String password) {
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

			java.util.List<CooccurrenceTypeDTO> myarrCoocTyp = new ArrayList<CooccurrenceTypeDTO>();
			CooccurrenceTypeDTO myCoocTyp = new CooccurrenceTypeDTO();
            CooccurrenceTypeDTOClient CoocTypDTOClient = new CooccurrenceTypeDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAllCooccurrenceTypes": myarrCoocTyp = utsMetadataService.getAllCooccurrenceTypes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getCooccurrenceType": myCoocTyp = utsMetadataService.getCooccurrenceType(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "LQ");
            String abbreviation = myCoocTyp.getAbbreviation();
            String expandedForm = myCoocTyp.getExpandedForm();
            boolean desiredAssertionSt = myCoocTyp.getClass().desiredAssertionStatus();
            Annotation[] annotation = myCoocTyp.getClass().getAnnotations();
            String canonicalName = myCoocTyp.getClass().getCanonicalName();
            Class<?> componentType = myCoocTyp.getClass().getComponentType();
            String simpleName = myCoocTyp.getClass().getSimpleName();
            ProtectionDomain protDomain = myCoocTyp.getClass().getProtectionDomain();
            
            System.out.println("Abbreviation:"+abbreviation+"|expandedForm:"+expandedForm+"|desiredAssertionSt:"
                    +desiredAssertionSt+"|annotation:"+annotation+"\n|canonicalName:"+canonicalName+
                    "|componentType:"+componentType+"|simpleName:"+simpleName+"|protDomain:"+protDomain);            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrCoocTyp.size(); i++) {

            CooccurrenceTypeDTO myCoocTypDTO = myarrCoocTyp.get(i);
            String abbreviation = myCoocTypDTO.getAbbreviation();
            String expandedForm = myCoocTypDTO.getExpandedForm();
            boolean desiredAssertionSt = myCoocTypDTO.getClass().desiredAssertionStatus();
            Annotation[] annotation = myCoocTypDTO.getClass().getAnnotations();
            String canonicalName = myCoocTypDTO.getClass().getCanonicalName();
            Class<?> componentType = myCoocTypDTO.getClass().getComponentType();
            String simpleName = myCoocTypDTO.getClass().getSimpleName();
            ProtectionDomain protDomain = myCoocTypDTO.getClass().getProtectionDomain();

            
            System.out.println("Abbreviation:"+abbreviation+"|expandedForm:"+expandedForm+"|desiredAssertionSt:"
            +desiredAssertionSt+"|annotation:"+annotation+"\n|canonicalName:"+canonicalName+
            "|componentType:"+componentType+"|simpleName:"+simpleName+"|protDomain:"+protDomain);
            }
            
            
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
