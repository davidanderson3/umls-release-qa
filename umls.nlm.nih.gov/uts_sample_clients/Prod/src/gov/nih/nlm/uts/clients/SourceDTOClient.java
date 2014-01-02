package gov.nih.nlm.uts.clients;

import static java.lang.System.out;

import java.util.ArrayList;

import javax.xml.datatype.XMLGregorianCalendar;

import gov.nih.nlm.uts.webservice.metadata.*;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class SourceDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2013AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SourceDTOClient(String username, String password) {
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

            SourceDTO mySrcDTO = new SourceDTO();
            SourceDTOClient SrcDTOClient = new SourceDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getCurrentVersionSource": mySrcDTO = utsMetadataService.getCurrentVersionSource(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "LNC");
            break;
            case "getSource": mySrcDTO = utsMetadataService.getSource(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "ICD9CM_1998");
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
           
            String abbreviation = mySrcDTO.getAbbreviation();
            String prefferedName = mySrcDTO.getPreferredName();
            String version = mySrcDTO.getVersion();
            int atomCount = mySrcDTO.getAtomCount();
            
            String rtSrcAbbr = mySrcDTO.getRootSource().getAbbreviation();
            String rtSrcExpForm = mySrcDTO.getRootSource().getExpandedForm();
            String rtSrcHeirarchicalName = mySrcDTO.getRootSource().getHierarchicalName();
            String rtSrcPrefName = mySrcDTO.getRootSource().getPreferredName();
            String rtSrcFamily = mySrcDTO.getRootSource().getFamily();
            String rtSrcShortName = mySrcDTO.getRootSource().getShortName();
            String rtSrcContextType = mySrcDTO.getRootSource().getContextType();

            
            System.out.println("abbreviation:"+abbreviation+"|prefferedName:"+prefferedName+"|version:"+version
            		+"|atomCount:"+atomCount+"\n|rtSrcAbbr:"+rtSrcAbbr+"|rtSrcExpForm:"+rtSrcExpForm
            		+"|rtSrcHeirarchicalName:"+rtSrcHeirarchicalName+"\n|rtSrcPrefName:"+rtSrcPrefName
            		+"|rtSrcFamily:"+rtSrcFamily+"|rtSrcShortName:"+rtSrcShortName+"|rtSrcContextType:"+rtSrcContextType);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
