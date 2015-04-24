package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;

import java.util.ArrayList;

import gov.nih.nlm.uts.webservice.metadata.CooccurrenceTypeDTO;
import gov.nih.nlm.uts.webservice.metadata.IdentifierTypeDTO;
import gov.nih.nlm.uts.webservice.metadata.SourceCitationDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class SourceCitationDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SourceCitationDTOClient (String username, String password) {
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
			java.util.List<SourceCitationDTO> myarrSrcCitationDTO = new ArrayList<SourceCitationDTO>();
			SourceCitationDTO  mySrcCitationDTO  = new SourceCitationDTO();
			SourceCitationDTOClient SrcCitationDTOClient = new SourceCitationDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAllSourceCitations": myarrSrcCitationDTO = utsMetadataService.getAllSourceCitations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getSourceCitation": mySrcCitationDTO = utsMetadataService.getSourceCitation(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "37");
            String address = mySrcCitationDTO.getAddress();
            String author = mySrcCitationDTO.getAuthor();
            String title = mySrcCitationDTO.getTitle();
            String dtPub = mySrcCitationDTO.getDateOfPublication();
            String dtRev = mySrcCitationDTO.getDateOfRevision();
            String edition = mySrcCitationDTO.getEdition();
            String handle = mySrcCitationDTO.getHandle();
            String value = mySrcCitationDTO.getValue();
            String classType = mySrcCitationDTO.getClassType();
            
            System.out.println(address+"|"+author+"|"+title+"|"+dtPub+"|"+dtRev+"|"+edition+"|"+handle+"|"+value+"|"+classType);
            break;
            default: out.println("Unrecognized input ");
        	break;  
            }
            
            for (int i = 0; i < myarrSrcCitationDTO.size(); i++) {

                SourceCitationDTO mySrcCitDTO = myarrSrcCitationDTO.get(i);
                String address = mySrcCitDTO.getAddress();
                String author = mySrcCitDTO.getAuthor();
                String title = mySrcCitDTO.getTitle();
                String dtPub = mySrcCitDTO.getDateOfPublication();
                String dtRev = mySrcCitDTO.getDateOfRevision();
                String edition = mySrcCitDTO.getEdition();
                String handle = mySrcCitDTO.getHandle();
                String value = mySrcCitDTO.getValue();
                String classType = mySrcCitDTO.getClassType();
                
                System.out.println(address+"|"+author+"|"+title+"|"+dtPub+"|"+dtRev+"|"+edition+"|"+handle+"|"+value+"|"+classType);
                }
                     
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
	}

}
