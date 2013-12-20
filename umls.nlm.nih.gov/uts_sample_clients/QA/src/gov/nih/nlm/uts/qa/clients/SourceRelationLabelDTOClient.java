package gov.nih.nlm.uts.qa.clients;

import static java.lang.System.out;
import gov.nih.nlm.uts.webservice.metadata.ContactInformationDTO;
import gov.nih.nlm.uts.webservice.metadata.LanguageDTO;
import gov.nih.nlm.uts.webservice.metadata.SourceRelationLabelDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;
import java.util.List;

public class SourceRelationLabelDTOClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SourceRelationLabelDTOClient (String username, String password) {
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

			java.util.List<SourceRelationLabelDTO> myarrSourceRelationLabel = new ArrayList<SourceRelationLabelDTO>();
			SourceRelationLabelDTO mySourceRelationLabel = new SourceRelationLabelDTO();
			SourceRelationLabelDTOClient AdditionalRelLabelClient = new SourceRelationLabelDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getAllSourceRelationLabels": myarrSourceRelationLabel = utsMetadataService.getAllSourceRelationLabels(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
            break;
            case "getSourceRelationLabel": mySourceRelationLabel = utsMetadataService.getSourceRelationLabel(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "MEDCIN", "RN", "isa");
            String origValue = mySourceRelationLabel.getOrigValue();
            String rangeId = mySourceRelationLabel.getRangeId();
            
            String rtSrcAbbr = mySourceRelationLabel.getRootSource().getAbbreviation();
            String rtSrcExpandedForm = mySourceRelationLabel.getRootSource().getExpandedForm();
            String rtSrcHeirarchicalName = mySourceRelationLabel.getRootSource().getHierarchicalName();
            String rtSrcFamily = mySourceRelationLabel.getRootSource().getFamily();
            String rtSrcPrefName = mySourceRelationLabel.getRootSource().getPreferredName();
            List<String> synonymousNames = mySourceRelationLabel.getRootSource().getSynonymousNames();
            String shortName = mySourceRelationLabel.getRootSource().getShortName();
            ContactInformationDTO acquisitionContact = mySourceRelationLabel.getRootSource().getAcquisitionContact();
            ContactInformationDTO contentContact = mySourceRelationLabel.getRootSource().getContentContact();
            ContactInformationDTO licenseContact = mySourceRelationLabel.getRootSource().getLicenseContact();
            LanguageDTO language = mySourceRelationLabel.getRootSource().getLanguage();
            int restrictionLevel = mySourceRelationLabel.getRootSource().getRestrictionLevel();

            String relAbbreviation = mySourceRelationLabel.getRelationLabel().getAbbreviation();
            String relExpandedForm = mySourceRelationLabel.getRelationLabel().getExpandedForm();

            String addRelAbbreviation = mySourceRelationLabel.getAdditionalRelationLabel().getAbbreviation();
            String addRelExpandedForm = mySourceRelationLabel.getAdditionalRelationLabel().getExpandedForm();


            System.out.println("Orig Value:"+origValue+"|rangeId:"+rangeId+"|rtSrcAbbr:"+rtSrcAbbr+"|rtSrcExpandedForm:"+rtSrcExpandedForm
            		+"\n|rtSrcHeirarchicalName:"+rtSrcHeirarchicalName+"|rtSrcFamily:"+rtSrcFamily+"rtSrcPrefName:"+rtSrcPrefName
            		+"\n|synonymousNames:"+synonymousNames+"|shortName:"+shortName+"|acquisitionContact:"+acquisitionContact+"|contentContact:"+contentContact
            		+"\n|licenseContact:"+licenseContact+"|language:"+language+"|restrictionLevel:"+restrictionLevel
            		+"\n|relAbbreviation:"+relAbbreviation+"|relExpandedForm:"+relExpandedForm+"|addRelAbbreviation:"+addRelAbbreviation+"|addRelExpandedForm:"+addRelExpandedForm);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            
            for (int i = 0; i < myarrSourceRelationLabel.size(); i++) {

            SourceRelationLabelDTO mySourceRelationLabelDTO = myarrSourceRelationLabel.get(i);
            String origValue = mySourceRelationLabelDTO.getOrigValue();
            String rangeId = mySourceRelationLabelDTO.getRangeId();
            
            String rtSrcAbbr = mySourceRelationLabelDTO.getRootSource().getAbbreviation();
            String rtSrcExpandedForm = mySourceRelationLabelDTO.getRootSource().getExpandedForm();
            String rtSrcHeirarchicalName = mySourceRelationLabelDTO.getRootSource().getHierarchicalName();
            String rtSrcFamily = mySourceRelationLabelDTO.getRootSource().getFamily();
            String rtSrcPrefName = mySourceRelationLabelDTO.getRootSource().getPreferredName();
            List<String> synonymousNames = mySourceRelationLabelDTO.getRootSource().getSynonymousNames();
            String shortName = mySourceRelationLabelDTO.getRootSource().getShortName();
            //String acquisitionContact = mySourceRelationLabelDTO.getRootSource().getAcquisitionContact().getCountry();
            String contentContact = mySourceRelationLabelDTO.getRootSource().getContentContact().getName();
            String licenseContact = mySourceRelationLabelDTO.getRootSource().getLicenseContact().getOrganization();
            String language = mySourceRelationLabelDTO.getRootSource().getLanguage().getExpandedForm();
            int restrictionLevel = mySourceRelationLabelDTO.getRootSource().getRestrictionLevel();

            String relAbbreviation = mySourceRelationLabelDTO.getRelationLabel().getAbbreviation();
            String relExpandedForm = mySourceRelationLabelDTO.getRelationLabel().getExpandedForm();

            String addRelAbbreviation = mySourceRelationLabelDTO.getAdditionalRelationLabel().getAbbreviation();
            String addRelExpandedForm = mySourceRelationLabelDTO.getAdditionalRelationLabel().getExpandedForm();
            System.out.println("Orig Value:"+origValue+"|rangeId:"+rangeId+"|rtSrcAbbr:"+rtSrcAbbr+"|rtSrcExpandedForm:"+rtSrcExpandedForm
            		+"\n|rtSrcHeirarchicalName:"+rtSrcHeirarchicalName+"|rtSrcFamily:"+rtSrcFamily+"rtSrcPrefName:"+rtSrcPrefName
            		+"\n|synonymousNames:"+synonymousNames+"|shortName:"+shortName+"|contentContact:"+contentContact
            		+"\n|licenseContact:"+licenseContact+"|language:"+language+"|restrictionLevel:"+restrictionLevel
            		+"\n|relAbbreviation:"+relAbbreviation+"|relExpandedForm:"+relExpandedForm+"|addRelAbbreviation:"+addRelAbbreviation+"|addRelExpandedForm:"+addRelExpandedForm);
                      
            }
            
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
