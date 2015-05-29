package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;
import java.util.ArrayList;
import gov.nih.nlm.uts.webservice.content.AtomCooccurrenceDTO;
import gov.nih.nlm.uts.webservice.content.AtomDTO;
import gov.nih.nlm.uts.webservice.content.ConceptDTO;
import gov.nih.nlm.uts.webservice.content.SourceAtomClusterDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;


public class AtomCooccurrenceDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utscontentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public AtomCooccurrenceDTOClient(String username, String password) {
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
            gov.nih.nlm.uts.webservice.content.Psf mypsf = new gov.nih.nlm.uts.webservice.content.Psf();
            java.util.List<AtomCooccurrenceDTO> myAtomCooccDTO = new ArrayList<AtomCooccurrenceDTO>();
            AtomCooccurrenceDTOClient SrcDTOClient = new AtomCooccurrenceDTOClient(args[0],args[1]);
            
        	String method = args[2];

        	
            switch (method) {
            case "getAtomCooccurrences": myAtomCooccDTO = utscontentService.getAtomCooccurrences(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "A0076226", mypsf);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
           
            for (int i = 0; i < myAtomCooccDTO.size(); i++) {

            AtomCooccurrenceDTO myAtomCoDTO = myAtomCooccDTO.get(i);
            long freq = myAtomCoDTO.getFrequency();
            AtomDTO relAtom = myAtomCoDTO.getRelatedAtom();
            int relAtomCnt = myAtomCoDTO.getRelatedAtom().getAtomRelationCount();
            int AttrCnt = myAtomCoDTO.getRelatedAtom().getAttributeCount();
            ConceptDTO concept = myAtomCoDTO.getRelatedAtom().getConcept();
            SourceAtomClusterDTO code = myAtomCoDTO.getRelatedAtom().getCode();
            int coocCnt = myAtomCoDTO.getRelatedAtom().getCooccurrenceCount();
            SourceAtomClusterDTO srcConc = myAtomCoDTO.getRelatedAtom().getSourceConcept();
            int cvMemCnt = myAtomCoDTO.getRelatedAtom().getCvMemberCount();
            String rtSrc = myAtomCoDTO.getRelatedAtom().getRootSource();
            String ui = myAtomCoDTO.getRelatedAtom().getUi();
            SourceAtomClusterDTO srcDesc = myAtomCoDTO.getRelatedAtom().getSourceDescriptor();
            String termType = myAtomCoDTO.getRelatedAtom().getTermType();
            int subHeadCnt = myAtomCoDTO.getSubheadingCount();
            String canName = myAtomCoDTO.getClass().getCanonicalName();
            
            System.out.println(freq+"|"+relAtom+"|"+relAtomCnt+"|"+AttrCnt+"|"+concept+"|"
            +code+"|"+coocCnt+"|"+srcConc+"|"+cvMemCnt+"|"+rtSrc+"|"+ui+"|"+srcDesc+"|"+termType+"|"+subHeadCnt+"|"+canName);
            }
            
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}


}
