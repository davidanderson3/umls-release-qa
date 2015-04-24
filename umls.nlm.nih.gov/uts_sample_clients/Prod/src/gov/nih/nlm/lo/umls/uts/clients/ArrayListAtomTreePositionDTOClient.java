package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.content.AtomDTO;
import gov.nih.nlm.uts.webservice.content.AtomTreePositionDTO;
import gov.nih.nlm.uts.webservice.content.MapObjectDTO;
import gov.nih.nlm.uts.webservice.content.MappingDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;

public class ArrayListAtomTreePositionDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2013AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    public ArrayListAtomTreePositionDTOClient(String username, String password) {
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

			ArrayListAtomTreePositionDTOClient AtomTrPosClient = new ArrayListAtomTreePositionDTOClient(args[0],args[1]);
        	String method = args[2];

        	gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
        	java.util.List<AtomTreePositionDTO> myarrAtmTreePosClient = new ArrayList<AtomTreePositionDTO>();
            
            switch (method) {
            case "getRootAtomTreePositions": myarrAtmTreePosClient = utsContentService.getRootAtomTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease,myPsf); 
        	break;
        	case "getAtomTreePositions": myarrAtmTreePosClient = utsContentService.getAtomTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease,"A18397223", myPsf);
            break;
        	case "getAtomTreePositionSiblings": myarrAtmTreePosClient = utsContentService.getAtomTreePositionSiblings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "f5d3b10604dffa2c6623e0b5061cee82", myPsf);
            break;
        	case "getAtomTreePositionChildren": myarrAtmTreePosClient = utsContentService.getAtomTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "d911cd41343040aaa9888c72ab26cc41", myPsf);
            break;
        	default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrAtmTreePosClient.size(); i++) {

            	AtomTreePositionDTO myAtmTreePosDTO = myarrAtmTreePosClient.get(i);

                String defPrefName = myAtmTreePosDTO.getDefaultPreferredName();
                String ui = myAtmTreePosDTO.getUi();
                //String addReLabel = myAtmTreePosDTO.getAdditionalRelationLabel();
                //AtomDTO atom = myAtmTreePosDTO.getAtom();
                int pathsToRootSrc = myAtmTreePosDTO.getPathsToRootCount();
                int siblingCount = myAtmTreePosDTO.getSiblingCount();
                int childCnt = myAtmTreePosDTO.getChildCount();
                String rootSource = myAtmTreePosDTO.getRootSource();
                String sourceUi = myAtmTreePosDTO.getSourceUi();
                String atomUi = myAtmTreePosDTO.getAtom().getUi();
                //System.out.println(defPrefName+"|"+ui+"|"+pathsToRootSrc+"|"+siblingCount+"|"+rootSource+"|"+sourceUi);
                System.out.println("&lt;tr&gt;&lt;td&gt;"+defPrefName+"&lt;/td&gt;\n&lt;td&gt;"+ui+"&lt;/td&gt;\n&lt;td&gt;"+pathsToRootSrc+"&lt;/td&gt;\n&lt;td&gt;"+siblingCount+"&lt;/td&gt;\n&lt;td&gt;"+childCnt+"&lt;/td&gt;\n&lt;td&gt;"+rootSource+"&lt;/td&gt;\n&lt;td&gt;"+atomUi+"&lt;/td&gt;&lt;/tr&gt;");

                
                }
  
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
