package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.content.AtomDTO;
import gov.nih.nlm.uts.webservice.content.AtomTreePositionDTO;
import gov.nih.nlm.uts.webservice.content.AtomTreePositionPathDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class AtomTreePositionPathDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    public AtomTreePositionPathDTOClient(String username, String password) {
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

			AtomTreePositionPathDTOClient AtomTrPosPathClient = new AtomTreePositionPathDTOClient(args[0],args[1]);
        	String method = args[2];

        	gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
        	java.util.List<AtomTreePositionPathDTO> myarrAtmTreePosPathClient = new ArrayList<AtomTreePositionPathDTO>();
            
            switch (method) {
            case "getAtomTreePositionPathsToRoot": myarrAtmTreePosPathClient = utsContentService.getAtomTreePositionPathsToRoot(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "f5d3b10604dffa2c6623e0b5061cee82", myPsf); 
        	break;
        	default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrAtmTreePosPathClient.size(); i++) {

            	AtomTreePositionPathDTO myAtmTreePosPathDTO = myarrAtmTreePosPathClient.get(i);

                List<AtomTreePositionDTO> treePos = myAtmTreePosPathDTO.getTreePositions();
                for (int j = 0; j < treePos.size(); j++) {
                	AtomTreePositionDTO getj = treePos.get(j);
                	String defPrefName = getj.getDefaultPreferredName();
                	String ui = getj.getUi();
                	String atomUi = getj.getAtom().getUi();
                	String rootSrc = getj.getRootSource();
                	String termType = getj.getAtom().getTermType();

                    //System.out.println("&lt;tr&gt;&lt;td&gt;"+defPrefName+"&lt;/td&gt;\n&lt;td&gt;"+ui+"&lt;/td&gt;\n&lt;td&gt;"+atomUi+"&lt;/td&gt;\n&lt;td&gt;"+rootSrc+"&lt;/td&gt;\n&lt;td&gt;"+termType+"&lt;/td&gt;&lt;/tr&gt;");
                    System.out.println(defPrefName+"|"+ui+"|"+atomUi+"|"+rootSrc+"|"+termType);
                }
                }
  
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


}
