package gov.nih.nlm.uts.qa.clients;

import java.util.ArrayList;
import static java.lang.System.out;

import gov.nih.nlm.uts.webservice.content.AtomDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.*;

public class PSFObsoleteSupressibleFixClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public PSFObsoleteSupressibleFixClient (String username, String password) {
    	PSFObsoleteSupressibleFixClient.username = username;
    	PSFObsoleteSupressibleFixClient.password = password;
	
}

	public static String ticketGrantingTicket() throws Exception{
	
    	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
        System.out.println("tgt: "+ticketGrantingTicket);  

        //use the Proxy Grant Ticket to get a Single Use Ticket
       // String singleUseTicket = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
        return ticketGrantingTicket;
    	
    }
	
	public static void main (String[] args) {
		// TODO Auto-generated method stub
		try {
            PSFObsoleteSupressibleFixClient PSFClient = new PSFObsoleteSupressibleFixClient(args[0],args[1]);
            String method = args[2];

			gov.nih.nlm.uts.webservice.content.Psf psf_cdef = new gov.nih.nlm.uts.webservice.content.Psf();
            psf_cdef.getIncludedSources().add("SNOMEDCT");
            psf_cdef.setPageLn(25);
            psf_cdef.setIncludeSuppressible(true);
            psf_cdef.setIncludeObsolete(false);
                    
            java.util.List<AtomDTO> myAtoms = new ArrayList<AtomDTO>();
            /*psf_cdef.getIncludedWords().add("Black");
            psf_cdef.setPageLn(25);
            psf_cdef.setIncludeSuppressible(false);
            psf_cdef.setIncludeObsolete(false);*/
            switch (method) {
            case "getConceptAtoms": myAtoms = utsContentService.getConceptAtoms(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease,"C0037162",psf_cdef);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myAtoms.size(); i++) {
            AtomDTO myAtomDTO = myAtoms.get(i);

            String aui = myAtomDTO.getUi();
            String source = myAtomDTO.getRootSource();
            String name = myAtomDTO.getTermString().getName();
            String TermType = myAtomDTO.getTermType();
            int cvMemberCount = myAtomDTO.getCvMemberCount();
          
            System.out.println(aui+"\n"+source+"\n"+name+"\n"+TermType+"\n"+cvMemberCount);
            }
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
