package gov.nih.nlm.uts.clients;

import static java.lang.System.out;
import java.util.ArrayList;
import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.security.*;

public class AtomRelationDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public AtomRelationDTOClient(String username, String password) {
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
			// Runtime properties
//			String username = "debjaniani";
//			String password = "Cartoon123!";
			AtomRelationDTOClient AtomRelClient = new AtomRelationDTOClient(args[0],args[1]);
            
        	String method = args[2];

            gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
            java.util.List<AtomRelationDTO> myAtomsRel = new ArrayList<AtomRelationDTO>();
            
            switch (method) {
            case "getSourceConceptAtomRelations": myAtomsRel = utsContentService.getSourceConceptAtomRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease,"441806004","SNOMEDCT",myPsf); 
        	break;
        	case "getSourceDescriptorAtomRelations": myAtomsRel = utsContentService.getSourceDescriptorAtomRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease,"D014028","MSH",myPsf);
            break;
        	case "getAtomAtomRelations": myAtomsRel = utsContentService.getAtomAtomRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease,"A1317707",myPsf);
            break;
        	case "getCodeAtomRelations": myAtomsRel = utsContentService.getCodeAtomRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease,"CDR0000039759","PDQ",myPsf);
            break;
        	default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myAtomsRel.size(); i++) {

                AtomRelationDTO myAtomDTO = myAtomsRel.get(i);

                String ui = myAtomDTO.getRelatedAtom().getUi();
                String name = myAtomDTO.getRelatedAtom().getTermString().getName();
                String rellabel = myAtomDTO.getRelationLabel();
                String arellabel = myAtomDTO.getAdditionalRelationLabel();
                System.out.println(ui+"|"+name+"|"+rellabel+"|"+arellabel);
                
                }
  
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
