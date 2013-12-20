package gov.nih.nlm.uts.qa.clients;

import static java.lang.System.out;
import java.util.ArrayList;

import gov.nih.nlm.uts.webservice.metadata.RootSourceDTO;
import gov.nih.nlm.uts.webservice.metadata.SourceTermTypeDTO;
import gov.nih.nlm.uts.webservice.metadata.TermTypeDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class SourceTermTypeDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2011AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SourceTermTypeDTOClient (String username, String password) {
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

			java.util.List<SourceTermTypeDTO > myarrSrcTermTypeDTO = new ArrayList<SourceTermTypeDTO >();
			SourceTermTypeDTO  mySrcTermTypeDTO = new SourceTermTypeDTO ();
			SourceTermTypeDTOClient mySrcTermTypeDTOClient = new SourceTermTypeDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAllSourceTermTypes": myarrSrcTermTypeDTO = utsMetadataService.getAllSourceTermTypes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getSourceTermType": mySrcTermTypeDTO = utsMetadataService.getSourceTermType(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "MSH", "MH");
            String handle = mySrcTermTypeDTO.getHandle();
            TermTypeDTO termTyp = mySrcTermTypeDTO.getTermType();
            boolean suppresible = mySrcTermTypeDTO.isSuppressible();
            RootSourceDTO rtSrc = mySrcTermTypeDTO.getRootSource();
            int versionsAgo = mySrcTermTypeDTO.getVersionsAgo();
            String classType = mySrcTermTypeDTO.getClassType();
            
            System.out.println("Handle:"+handle+"|TermType:"+termTyp+"Suppresible:"+suppresible+"RootSrc:"+rtSrc+"versionsAgo:"+versionsAgo+"|ClassType:"+classType);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrSrcTermTypeDTO.size(); i++) {

            SourceTermTypeDTO mySrcTrmTyp = myarrSrcTermTypeDTO.get(i);
            String handle = mySrcTrmTyp.getHandle();
            TermTypeDTO termTyp = mySrcTrmTyp.getTermType();
            boolean suppresible = mySrcTrmTyp.isSuppressible();
            RootSourceDTO rtSrc = mySrcTrmTyp.getRootSource();
            int versionsAgo = mySrcTrmTyp.getVersionsAgo();
            String classType = mySrcTrmTyp.getClassType();
            
            System.out.println("Handle:"+handle+"|TermType:"+termTyp+"Suppresible:"+suppresible+"RootSrc:"+rtSrc+"versionsAgo:"+versionsAgo+"|ClassType:"+classType);
            }
            
            
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		
	}

}
