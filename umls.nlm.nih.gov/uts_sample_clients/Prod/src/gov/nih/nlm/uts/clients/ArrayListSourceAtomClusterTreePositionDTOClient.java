package gov.nih.nlm.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.content.AtomDTO;
import gov.nih.nlm.uts.webservice.content.AtomTreePositionDTO;
import gov.nih.nlm.uts.webservice.content.SourceAtomClusterDTO;
import gov.nih.nlm.uts.webservice.content.SourceAtomClusterTreePositionDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.awt.List;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;

public class ArrayListSourceAtomClusterTreePositionDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2011AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    public ArrayListSourceAtomClusterTreePositionDTOClient(String username, String password) {
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

			ArrayListSourceAtomClusterTreePositionDTOClient AtomClustTrPosClient = new ArrayListSourceAtomClusterTreePositionDTOClient(args[0],args[1]);
        	String method = args[2];

        	gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
        	java.util.List<SourceAtomClusterTreePositionDTO> myarrAtomClustTrPosClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
            
            switch (method) {
            case "getRootSourceConceptTreePositions": myarrAtomClustTrPosClient = utsContentService.getRootSourceConceptTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, myPsf); 
        	break;
        	case "getRootSourceDescriptorTreePositions": myarrAtomClustTrPosClient = utsContentService.getRootSourceDescriptorTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, myPsf);
            break;
        	case "getSourceConceptTreePositions": myarrAtomClustTrPosClient = utsContentService.getSourceConceptTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C2916", "NCI", myPsf);
            break;
        	case "getSourceDescriptorTreePositions": myarrAtomClustTrPosClient = utsContentService.getSourceDescriptorTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "10011078", "MDR", myPsf);
            break;
        	case "getSourceConceptTreePositionChildren": myarrAtomClustTrPosClient = utsContentService.getSourceConceptTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "df26dc6c2ba321499edce7293b47dd74", myPsf);
            break;
        	case "getSourceDescriptorTreePositionChildren": myarrAtomClustTrPosClient = utsContentService.getSourceDescriptorTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "e25d785327624aca92b4f3d419b66c17", myPsf);
            break;
        	case "getSourceConceptTreePositionSiblings": myarrAtomClustTrPosClient = utsContentService.getSourceConceptTreePositionSiblings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "7de51e59b78501cbf441665885ae4559", myPsf);
            break;
        	case "getSourceDescriptorTreePositionSiblings": myarrAtomClustTrPosClient = utsContentService.getSourceDescriptorTreePositionSiblings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "aa0515eaa1fe1e9fd263feeab56fdb4c", myPsf);
            break;
        	default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrAtomClustTrPosClient.size(); i++) {

            	SourceAtomClusterTreePositionDTO myAtmClustTreePosDTO = myarrAtomClustTrPosClient.get(i);

                String defaultPrefName = myAtmClustTreePosDTO.getCluster().getDefaultPreferredName();
                String clusterUi = myAtmClustTreePosDTO.getCluster().getUi();
                String ui = myAtmClustTreePosDTO.getUi();
                int childCnt = myAtmClustTreePosDTO.getChildCount();
                int siblingCnt = myAtmClustTreePosDTO.getSiblingCount();
                String rootSrc = myAtmClustTreePosDTO.getRootSource();
                String srcUi = myAtmClustTreePosDTO.getSourceUi();
//                String handle = myAtmClustTreePosDTO.getHandle();

                int modifiers = myAtmClustTreePosDTO.getClass().getModifiers();
                Annotation[] annotations = myAtmClustTreePosDTO.getClass().getAnnotations();
                String canName = myAtmClustTreePosDTO.getClass().getCanonicalName();
                Class<?> componentType = myAtmClustTreePosDTO.getClass().getComponentType();
                boolean desiredAssertStat = myAtmClustTreePosDTO.getClass().desiredAssertionStatus();
                Constructor<?>[] constructors = myAtmClustTreePosDTO.getClass().getConstructors();
                Method enclosingMethod = myAtmClustTreePosDTO.getClass().getEnclosingMethod();
                int hashCode = myAtmClustTreePosDTO.getClass().hashCode();
                String className = myAtmClustTreePosDTO.getClass().getName();
                ProtectionDomain protDomain = myAtmClustTreePosDTO.getClass().getProtectionDomain();

                System.out.println(childCnt+"|"+siblingCnt+"|"+clusterUi+"|"+defaultPrefName+"|"+rootSrc+"|"+srcUi+"|"+ui);
//                System.out.println("\n"+modifiers+"|"+annotations+"|"+canName+"|"+componentType+"|"+desiredAssertStat
//                		+"|"+constructors+"|"+enclosingMethod+"|"+hashCode+"|"+className+"|"+protDomain);
                //System.out.println("&lt;tr&gt;&lt;td&gt;"+defaultPrefName+"&lt;/td&gt;\n&lt;td&gt;"+clusterUi+"&lt;/td&gt;\n&lt;td&gt;"+ui+"&lt;/td&gt;\n&lt;td&gt;"+childCnt+"&lt;/td&gt;\n&lt;td&gt;"+siblingCnt+"&lt;/td&gt;\n&lt;td&gt;"+rootSrc+"&lt;/td&gt;&lt;/tr&gt;");

                
                }
  
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
