package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;

import gov.nih.nlm.uts.webservice.metadata.ContactInformationDTO;
import gov.nih.nlm.uts.webservice.metadata.LanguageDTO;
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
    static String umlsRelease = "2013AB";
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
        //System.out.println("tgt: "+ticketGrantingTicket);  

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
            boolean suppresible = mySrcTermTypeDTO.isSuppressible();
            String origValue = mySrcTermTypeDTO.getOrigValue();

            String rtSrcAbbreviation = mySrcTermTypeDTO.getRootSource().getAbbreviation();
            //String rtSrcAcquisitionContact = mySrcTermTypeDTO.getRootSource().getAcquisitionContact().getName();
            String rtSrcContentContact = mySrcTermTypeDTO.getRootSource().getContentContact().getName();
            String rtSrcContextType = mySrcTermTypeDTO.getRootSource().getContextType();
            String rtSrcExpandedForm = mySrcTermTypeDTO.getRootSource().getExpandedForm();
            String rtSrcfamily = mySrcTermTypeDTO.getRootSource().getFamily();
            String rtSrcLanguage = mySrcTermTypeDTO.getRootSource().getLanguage().getExpandedForm();
            String rtSrcHierarchicalName = mySrcTermTypeDTO.getRootSource().getHierarchicalName();
            String rtSrcLicenseContact = mySrcTermTypeDTO.getRootSource().getLicenseContact().getName();
            int rtSrcRestriction = mySrcTermTypeDTO.getRootSource().getRestrictionLevel();
            List<String> rtSrcSynonymousNames = mySrcTermTypeDTO.getRootSource().getSynonymousNames();            
            
            System.out.println("Suppresible:"+suppresible+"|origValue:"+origValue+"|rtSrcAbbreviation:"+rtSrcAbbreviation+"|rtSrcContentContact:"+rtSrcContentContact
            		+"|rtSrcContextType:"+rtSrcContextType+"|rtSrcExpandedForm:"+rtSrcExpandedForm+"|rtSrcfamily:"+rtSrcfamily+"|rtSrcLanguage:"+rtSrcLanguage+"|rtSrcHierarchicalName:"+rtSrcHierarchicalName
            		+"|rtSrcLicenseContact:"+rtSrcLicenseContact+"|rtSrcRestriction:"+rtSrcRestriction+"|rtSrcSynonymousNames:"+rtSrcSynonymousNames);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrSrcTermTypeDTO.size(); i++) {

            SourceTermTypeDTO mySrcTrmTyp = myarrSrcTermTypeDTO.get(i);
            boolean suppresible = mySrcTrmTyp.isSuppressible();
            String origValue = mySrcTrmTyp.getOrigValue();

            String rtSrcAbbreviation = mySrcTrmTyp.getRootSource().getAbbreviation();
           // String rtSrcAcquisitionContact = mySrcTrmTyp.getRootSource().getAcquisitionContact().getCity();
            String rtSrcContentContact = mySrcTrmTyp.getRootSource().getContentContact().getAddress1();
            String rtSrcContextType = mySrcTrmTyp.getRootSource().getContextType();
            String rtSrcExpandedForm = mySrcTrmTyp.getRootSource().getExpandedForm();
            String rtSrcfamily = mySrcTrmTyp.getRootSource().getFamily();
            String rtSrcLanguage = mySrcTrmTyp.getRootSource().getLanguage().getExpandedForm();
            String rtSrcHierarchicalName = mySrcTrmTyp.getRootSource().getHierarchicalName();
            String rtSrcLicenseContact = mySrcTrmTyp.getRootSource().getLicenseContact().getEmail();
            int rtSrcRestriction = mySrcTrmTyp.getRootSource().getRestrictionLevel();
            List<String> rtSrcSynonymousNames = mySrcTrmTyp.getRootSource().getSynonymousNames();

            System.out.println("Suppresible:"+suppresible+"|origValue:"+origValue+"|rtSrcAbbreviation:"+rtSrcAbbreviation+"|rtSrcContentContact:"+rtSrcContentContact
            		+"|rtSrcContextType:"+rtSrcContextType+"|rtSrcExpandedForm:"+rtSrcExpandedForm+"|rtSrcfamily:"+rtSrcfamily+"|rtSrcLanguage:"+rtSrcLanguage+"|rtSrcHierarchicalName:"+rtSrcHierarchicalName
            		+"|rtSrcLicenseContact:"+rtSrcLicenseContact+"|rtSrcRestriction:"+rtSrcRestriction+"|rtSrcSynonymousNames:"+rtSrcSynonymousNames);
            }
            
            
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		
	}

}
