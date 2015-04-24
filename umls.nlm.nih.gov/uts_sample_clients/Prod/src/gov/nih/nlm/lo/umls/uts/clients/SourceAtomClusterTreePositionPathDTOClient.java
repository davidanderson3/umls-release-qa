package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class SourceAtomClusterTreePositionPathDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2011AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    public SourceAtomClusterTreePositionPathDTOClient(String username, String password) {
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

			SourceAtomClusterTreePositionPathDTOClient SrcDescTreePosPathDTOClient = new SourceAtomClusterTreePositionPathDTOClient(args[0],args[1]);
        	String method = args[2];

        	gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
        	java.util.List<SourceAtomClusterTreePositionPathDTO> myarrSrcDescTreePosPathDTOClient = new ArrayList<SourceAtomClusterTreePositionPathDTO>();
            
            switch (method) {
            case "getSourceDescriptorTreePositionPathsToRoot": myarrSrcDescTreePosPathDTOClient = utsContentService.getSourceDescriptorTreePositionPathsToRoot(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "aa0515eaa1fe1e9fd263feeab56fdb4c", myPsf); 
        	break;
        	default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrSrcDescTreePosPathDTOClient.size(); i++) {

            	SourceAtomClusterTreePositionPathDTO myAtmClustTreePosDTO = myarrSrcDescTreePosPathDTOClient.get(i);

                int modifiers = myAtmClustTreePosDTO.getClass().getModifiers();
                List<SourceAtomClusterTreePositionDTO> treePos = myAtmClustTreePosDTO.getTreePositions();
                Annotation[] annotations = myAtmClustTreePosDTO.getClass().getAnnotations();
                String classType = myAtmClustTreePosDTO.getClassType();
                String canName = myAtmClustTreePosDTO.getClass().getCanonicalName();
                String className = myAtmClustTreePosDTO.getClass().getName();
                Package pack = myAtmClustTreePosDTO.getClass().getPackage();

                System.out.println(modifiers+"|"+treePos+"|"+annotations+"|"+classType+"|"+canName+"|"+className+"|"+pack);
                
                }
  
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}



}
