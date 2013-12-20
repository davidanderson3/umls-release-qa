package gov.nih.nlm.uts.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.metadata.AttributeNameDTO;
import gov.nih.nlm.uts.webservice.metadata.ContactInformationDTO;
import gov.nih.nlm.uts.webservice.metadata.LanguageDTO;
import gov.nih.nlm.uts.webservice.metadata.RootSourceDTO;
import gov.nih.nlm.uts.webservice.metadata.SourceAttributeNameDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;
import java.util.List;

public class SourceAttributeNameDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SourceAttributeNameDTOClient (String username, String password) {
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

			java.util.List<SourceAttributeNameDTO> myarrSrcAttributeName = new ArrayList<SourceAttributeNameDTO>();
			SourceAttributeNameDTO mySrcAttributeName = new SourceAttributeNameDTO();
			SourceAttributeNameDTOClient SrcAttributeNameDTOClient = new SourceAttributeNameDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAllSourceAttributeNames": myarrSrcAttributeName = utsMetadataService.getAllSourceAttributeNames(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getSourceAttributeName": mySrcAttributeName = utsMetadataService.getSourceAttributeName(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "PSY", "HN");
            
            String attrNameAbbreviation = mySrcAttributeName.getAttributeName().getAbbreviation();
            String attrNameExpandedForm = mySrcAttributeName.getAttributeName().getExpandedForm();
            String originalVal = mySrcAttributeName.getOrigValue();
            
            String rtSourceAbbreviation = mySrcAttributeName.getRootSource().getAbbreviation();
            String rtSrcExpandedForm = mySrcAttributeName.getRootSource().getExpandedForm();
            String family = mySrcAttributeName.getRootSource().getFamily();
            String heirarchicalName = mySrcAttributeName.getRootSource().getHierarchicalName();
            String prefferedName = mySrcAttributeName.getRootSource().getPreferredName();
            String shortName = mySrcAttributeName.getRootSource().getShortName();
            //String acquisitionContact = mySrcAttributeName.getRootSource().getAcquisitionContact().getName();
            String contentContact = mySrcAttributeName.getRootSource().getContentContact().getName();
            String licenseContact = mySrcAttributeName.getRootSource().getLicenseContact().getValue();
            String language = mySrcAttributeName.getRootSource().getLanguage().getExpandedForm();
            int restrictionLevel = mySrcAttributeName.getRootSource().getRestrictionLevel();
            List<String> synonymousNames = mySrcAttributeName.getRootSource().getSynonymousNames();
            
            System.out.println("AttrNameAbbreviation:"+attrNameAbbreviation+"|attrNameExpandedForm:"+attrNameExpandedForm+"|Original Val:"+originalVal
            		+"|rtSourceAbbreviation:"+rtSourceAbbreviation+"|rtSrcExpandedForm:"+rtSrcExpandedForm+"|family:"+family+"|heirarchicalName:"+heirarchicalName+
            		"|prefferedName:"+prefferedName+"|shortName:"+shortName
            		+"|contentContact:"+contentContact+"|licenseContact:"+licenseContact+"|language:"+language+"|restrictionLevel:"+restrictionLevel+"|synonymousNames:"+synonymousNames);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myarrSrcAttributeName.size(); i++) {

            SourceAttributeNameDTO myarrSrcAttributeNameDTO = myarrSrcAttributeName.get(i);
            String attrNameAbbreviation = myarrSrcAttributeNameDTO.getAttributeName().getAbbreviation();
            String attrNameExpandedForm = myarrSrcAttributeNameDTO.getAttributeName().getExpandedForm();
            String originalVal = myarrSrcAttributeNameDTO.getOrigValue();
            
            String rtSourceAbbreviation = myarrSrcAttributeNameDTO.getRootSource().getAbbreviation();
            String rtSrcExpandedForm = myarrSrcAttributeNameDTO.getRootSource().getExpandedForm();
            String family = myarrSrcAttributeNameDTO.getRootSource().getFamily();
            String heirarchicalName = myarrSrcAttributeNameDTO.getRootSource().getHierarchicalName();
            String prefferedName = myarrSrcAttributeNameDTO.getRootSource().getPreferredName();
            String shortName = myarrSrcAttributeNameDTO.getRootSource().getShortName();
            //String acquisitionContact = myarrSrcAttributeNameDTO.getRootSource().getAcquisitionContact().getName();
            String contentContact = myarrSrcAttributeNameDTO.getRootSource().getContentContact().getName();
            String licenseContact = myarrSrcAttributeNameDTO.getRootSource().getLicenseContact().getName();
            String language = myarrSrcAttributeNameDTO.getRootSource().getLanguage().getExpandedForm();
            int restrictionLevel = myarrSrcAttributeNameDTO.getRootSource().getRestrictionLevel();
            List<String> synonymousNames = myarrSrcAttributeNameDTO.getRootSource().getSynonymousNames();
            
            System.out.println("AttrNameAbbreviation:"+attrNameAbbreviation+"|attrNameExpandedForm:"+attrNameExpandedForm+"|Original Val:"+originalVal
            		+"|rtSourceAbbreviation:"+rtSourceAbbreviation+"|rtSrcExpandedForm:"+rtSrcExpandedForm+"|family:"+family+"|heirarchicalName:"+heirarchicalName+
            		"|prefferedName:"+prefferedName+"|shortName:"+shortName
            		+"|contentContact:"+contentContact+"|licenseContact:"+licenseContact+"|language:"+language+"|restrictionLevel:"+restrictionLevel+"|synonymousNames:"+synonymousNames);
            
            }
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
