package gov.nih.nlm.uts.clients;


import java.lang.*;
import java.util.*;
import gov.nih.nlm.uts.webservice.content.*;
//import gov.nih.nlm.uts.webservice.finder.*;
//import gov.nih.nlm.uts.webservice.history.*;
//import gov.nih.nlm.uts.webservice.metadata.*;
import gov.nih.nlm.uts.webservice.security.*;


public class AtomClusterRelationDTOClient {

	public static void main (String[] args) {
		try {
			// Runtime properties
			String username = "debjaniani";
			String password = "Cartoon123!";
			String umlsRelease = "2013AA";
			String serviceName = "http://umlsks.nlm.nih.gov";
			
                        
			UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
            UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();
            
            //get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
            String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
        
            //use the Proxy Grant Ticket to get a Single Use Ticket
            String singleUseTicket = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
            
      
            //Constructor for getSourceDescriptorSourceDescriptorRelations() method
           gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
           //myPsf.getIncludedRelationLabels().add("RB");
           //myPsf.getIncludedRelationLabels().add("SIB");
           //myPsf.getIncludedSources().add("MSH");
           //myPsf.setSortBy("REL");
           //myPsf.setPageLn(100);
           List<AtomClusterRelationDTO> myAtomClusterRelations = new ArrayList<AtomClusterRelationDTO>();

       /* myAtomClusterRelations = utsContentService.getSourceDescriptorSourceDescriptorRelations(singleUseTicket, umlsRelease, "D004700", "MSH", myPsf);
        
        if (myAtomClusterRelations.size() == 0){
        	
        	System.out.println("There are no outputs for the given UMLS release and PSF value.");	
        } 
        
        else {
        
        
        for (int i = 0; i < myAtomClusterRelations.size(); i++) {

            AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
            String otherAtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
            String otherAtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
            String otherAtomClusterRel = myAtomClusterRelationDTO.getRelationLabel();
            String otherAtomClusterRela = myAtomClusterRelationDTO.getAdditionalRelationLabel();
            
            System.out.println("getSourceDescriptorSourceDescriptorRelations:"+otherAtomClusterName+"|"+otherAtomClusterUi+"|"+otherAtomClusterRel+"|"+otherAtomClusterRela);

            } */
            
          //Constructor for getSourceDescriptorCodeRelations() method
               gov.nih.nlm.uts.webservice.content.Psf myPsf_sdcr = new gov.nih.nlm.uts.webservice.content.Psf();
            //myPsf_sdcr.getIncludedRelationLabels().add("SIB");
            //myPsf_sdcr.getIncludedRelationLabels().add("PAR");
            List<AtomClusterRelationDTO> myAtomClusterRelations_sdcr = new ArrayList<AtomClusterRelationDTO>();

            myAtomClusterRelations_sdcr = utsContentService.getSourceDescriptorCodeRelations(singleUseTicket, umlsRelease, "D001419", "MSH", myPsf_sdcr);
            for (int j = 0; j < myAtomClusterRelations_sdcr.size(); j++) {

            AtomClusterRelationDTO myAtomClusterRelationDTO_sdcr = myAtomClusterRelations_sdcr.get(j);
            String otherAtomClusterUi_sdcr = myAtomClusterRelationDTO_sdcr.getRelatedAtomCluster().getUi();
            String otherAtomClusterName_sdcr = myAtomClusterRelationDTO_sdcr.getRelatedAtomCluster().getDefaultPreferredName();
            String otherAtomClusterRel_sdcr = myAtomClusterRelationDTO_sdcr.getRelationLabel();
            String otherAtomClusterRela_sdcr = myAtomClusterRelationDTO_sdcr.getAdditionalRelationLabel();
            
            System.out.println(otherAtomClusterUi_sdcr+"|"+otherAtomClusterName_sdcr+"|"+otherAtomClusterRel_sdcr+"|"+otherAtomClusterRela_sdcr);
            
            }
            
            //Constructor for getCodeSourceDescriptorRelations() method
//        gov.nih.nlm.uts.webservice.content.Psf myPsf_sdcr = new gov.nih.nlm.uts.webservice.content.Psf();
//            myPsf_sdcr.getIncludedRelationLabels().add("SIB");
//            myPsf_sdcr.getIncludedRelationLabels().add("PAR");
//           List<AtomClusterRelationDTO> myAtomClusterRelations_csdr = new ArrayList<AtomClusterRelationDTO>();
//
//            myAtomClusterRelations_csdr = utsContentService.getCodeSourceDescriptorRelations(singleUseTicket, umlsRelease, "U000005", "MSH", myPsf_sdcr);
//            for (int j = 0; j < myAtomClusterRelations_csdr.size(); j++) {
//
//            AtomClusterRelationDTO myAtomClusterRelationDTO_csdr = myAtomClusterRelations_csdr.get(j);
//            String otherAtomClusterUi_csdr = myAtomClusterRelationDTO_csdr.getRelatedAtomCluster().getUi();
//            String otherAtomClusterName_csdr = myAtomClusterRelationDTO_csdr.getRelatedAtomCluster().getDefaultPreferredName();
//            String otherAtomClusterRel_csdr = myAtomClusterRelationDTO_csdr.getRelationLabel();
//            String otherAtomClusterRela_csdr = myAtomClusterRelationDTO_csdr.getAdditionalRelationLabel();
//            
//            System.out.println("getCodeSourceDescriptorRelations:"+otherAtomClusterUi_csdr+"|"+otherAtomClusterName_csdr+"|"+otherAtomClusterRel_csdr+"|"+otherAtomClusterRela_csdr);
//            
//            } 
            
            
           /* myAtomClusterRelations = utsContentService.getCodeCodeRelations(singleUseTicket, umlsRelease, "53746-4", "LNC", myPsf);
            for (int i = 0; i < myAtomClusterRelations.size(); i++) {

            AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
            String otherAtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
            String otherAtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
            String otherAtomClusterRel = myAtomClusterRelationDTO.getRelationLabel();
            String otherAtomClusterRela = myAtomClusterRelationDTO.getAdditionalRelationLabel();
            
            System.out.println("getCodeCodeRelations:"+otherAtomClusterName+"|"+otherAtomClusterUi+"|"+otherAtomClusterRel+"|"+otherAtomClusterRela);

            }*/
            
/*            
            myAtomClusterRelations = utsContentService.getCodeSourceConceptRelations(singleUseTicket, umlsRelease, "579.0", "ICD9CM", myPsf);
            for (int i = 0; i < myAtomClusterRelations.size(); i++) {

            AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
            String otherAtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
            String otherAtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
            String otherAtomClusterRel = myAtomClusterRelationDTO.getRelationLabel();
            String otherAtomClusterRela = myAtomClusterRelationDTO.getAdditionalRelationLabel();
            
            System.out.println("getCodeSourceConceptRelations:"+otherAtomClusterName+"|"+otherAtomClusterUi+"|"+otherAtomClusterRel+"|"+otherAtomClusterRela);

            }  */
            
            /* myAtomClusterRelations = utsContentService.getSourceConceptCodeRelations(singleUseTicket, umlsRelease, "32337007", "SNOMEDCT", myPsf);
            for (int i = 0; i < myAtomClusterRelations.size(); i++) {

            AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
            String otherAtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
            String otherAtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
            String otherAtomClusterRel = myAtomClusterRelationDTO.getRelationLabel();
            String otherAtomClusterRela = myAtomClusterRelationDTO.getAdditionalRelationLabel();
            
            System.out.println("getSourceConceptCodeRelations:"+otherAtomClusterName+"|"+otherAtomClusterUi+"|"+otherAtomClusterRel+otherAtomClusterRela);

            }   
            
             myAtomClusterRelations = utsContentService.getAtomCodeRelations(singleUseTicket, umlsRelease, "A4356606", myPsf);
            for (int i = 0; i < myAtomClusterRelations.size(); i++) {

            AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
            String otherAtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
            String otherAtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
            String otherAtomClusterRel = myAtomClusterRelationDTO.getRelationLabel();
            String otherAtomClusterRela = myAtomClusterRelationDTO.getAdditionalRelationLabel();
            
            System.out.println("getAtomCodeRelations:"+otherAtomClusterName+"|"+otherAtomClusterUi+"|"+otherAtomClusterRel+otherAtomClusterRela);

            } */
            
            
//               myAtomClusterRelations = utsContentService.getAtomSourceConceptRelations(singleUseTicket, umlsRelease, "A16344698", myPsf);
//            for (int i = 0; i < myAtomClusterRelations.size(); i++) {
//
//            AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
//            String otherAtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
//            String otherAtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
//            String otherAtomClusterRel = myAtomClusterRelationDTO.getRelationLabel();
//            String otherAtomClusterRela = myAtomClusterRelationDTO.getAdditionalRelationLabel();
//            
//            System.out.println("getAtomSourceConceptRelations:"+otherAtomClusterName+"|"+otherAtomClusterUi+"|"+otherAtomClusterRel+otherAtomClusterRela);
//
//            }  
            
            
/*            myAtomClusterRelations = utsContentService.getAtomSourceDescriptorRelations(singleUseTicket, umlsRelease, "A17775421", myPsf);
            for (int i = 0; i < myAtomClusterRelations.size(); i++) {

            AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
            String otherAtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
            String otherAtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
            String otherAtomClusterRel = myAtomClusterRelationDTO.getRelationLabel();
            String otherAtomClusterRela = myAtomClusterRelationDTO.getAdditionalRelationLabel();
            
            System.out.println("getAtomSourceDescriptorRelations:"+otherAtomClusterName+"|"+otherAtomClusterUi+"|"+otherAtomClusterRel+otherAtomClusterRela);

            } */
            
            
/*             myAtomClusterRelations = utsContentService.getAtomConceptRelations(singleUseTicket, umlsRelease, "A0851653", myPsf);
            for (int i = 0; i < myAtomClusterRelations.size(); i++) {

            AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
            String otherAtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
            String otherAtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
            String otherAtomClusterRel = myAtomClusterRelationDTO.getRelationLabel();
            String otherAtomClusterRela = myAtomClusterRelationDTO.getAdditionalRelationLabel();
            
            System.out.println(otherAtomClusterName+"|"+otherAtomClusterUi+"|"+otherAtomClusterRel+"|"+otherAtomClusterRela);

            }  
            */
            
//           myAtomClusterRelations = utsContentService.getSourceConceptSourceConceptRelations(singleUseTicket, umlsRelease, "C0220650", "NCI", myPsf);
//            for (int i = 0; i < myAtomClusterRelations.size(); i++) {
//
//            AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
//            String otherAtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
//            String otherAtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
//            String otherAtomClusterRel = myAtomClusterRelationDTO.getRelationLabel();
//            String otherAtomClusterRela = myAtomClusterRelationDTO.getAdditionalRelationLabel();
//            
//            System.out.println("getSourceConceptSourceConceptRelations:"+otherAtomClusterName+"|"+otherAtomClusterUi+"|"+otherAtomClusterRel+otherAtomClusterRela);
//
//            } 
            
          
                        
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
