package gov.nih.nlm.uts.qa.clients;

import static java.lang.System.out;

import java.util.ArrayList;

import javax.xml.datatype.XMLGregorianCalendar;

import gov.nih.nlm.uts.webservice.metadata.RootSourceDTO;
import gov.nih.nlm.uts.webservice.metadata.SourceDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class ArrayListSourceDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2013AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public ArrayListSourceDTOClient(String username, String password) {
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

			java.util.List<SourceDTO> myArrSrc = new ArrayList<SourceDTO>();
			ArrayListSourceDTOClient SrcDTOClient = new ArrayListSourceDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getVersionedSources": myArrSrc = utsMetadataService.getVersionedSources(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "LNC");
            break;
            case "getUpdatedSourcesByVersion": myArrSrc = utsMetadataService.getUpdatedSourcesByVersion(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getAllSources": myArrSrc = utsMetadataService.getAllSources(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myArrSrc.size(); i++) {

            SourceDTO myArrSrcDTO = myArrSrc.get(i);
            String abbreviation = myArrSrcDTO.getAbbreviation();
            String rtSrcAbbr = myArrSrcDTO.getRootSource().getAbbreviation();
            String rtSrcExpForm = myArrSrcDTO.getRootSource().getExpandedForm();
//            String prefferedName = myArrSrcDTO.getPreferredName();
            String version = myArrSrcDTO.getVersion();
//            int atomCount = myArrSrcDTO.getAtomCount();
//            String rtSrcHeirarchicalName = myArrSrcDTO.getRootSource().getHierarchicalName();
//            String rtSrcPrefName = myArrSrcDTO.getRootSource().getPreferredName();
//            String rtSrcFamily = myArrSrcDTO.getRootSource().getFamily();
//            String rtSrcShortName = myArrSrcDTO.getRootSource().getShortName();
//            String rtSrcContextType = myArrSrcDTO.getRootSource().getContextType();
            
            //System.out.println(&lt;tr&gt;&lt;td&gt;NCI&lt;/td&gt;abbreviation+"|"+rtSrcAbbr+"|"+rtSrcExpForm+"|"+version);
            System.out.println("&lt;tr&gt;&lt;td&gt;"+abbreviation+"&lt;/td&gt;\n&lt;td&gt;"+rtSrcAbbr+"&lt;/td&gt;\n&lt;td&gt;"+rtSrcExpForm+"&lt;/td&gt;\n&lt;td&gt;"+version+"&lt;/td&gt;&lt;/tr&gt;");


            }
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
