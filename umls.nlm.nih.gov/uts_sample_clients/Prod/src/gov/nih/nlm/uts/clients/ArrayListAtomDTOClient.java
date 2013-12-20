package gov.nih.nlm.uts.clients;

import java.awt.List;
import java.util.ArrayList;

import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.security.*;

public class ArrayListAtomDTOClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
            
            //The PSF object in the code is made after psf is used in getconceptatoms. Do we need to fix that??/????
            //gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
            gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
            myPsf.getIncludedSources().add("NCI");
//            myPsf.setPageLn(25);
//            myPsf.setIncludeSuppressible(true);
//            myPsf.setIncludeObsolete(true);
            
            /*psf_cdef.getIncludedWords().add("Black");
            psf_cdef.setPageLn(25);
            psf_cdef.setIncludeSuppressible(false);
            psf_cdef.setIncludeObsolete(false);*/
            
            
            java.util.List<AtomDTO> myAtoms = new ArrayList<AtomDTO>();
            //NO OUTPUT for 2011AB-------------------------------------------------------------
           /* myAtoms = utsContentService.getConceptAtoms(singleUseTicket, "2012AA","C0004775",myPsf);
            
            myPsf.getIncludedSources().add("SNOMEDCT");
            myPsf.getIncludedTermTypes().add("FSN");
            myPsf.getIncludedTermTypes().add("SY");
            myPsf.getIncludedTermTypes().add("PT");

            for (int i = 0; i < myAtoms.size(); i++) {


            AtomDTO myAtomDTO = myAtoms.get(i);

            String aui = myAtomDTO.getUi();
            String source = myAtomDTO.getRootSource();
            String name = myAtomDTO.getTermString().getName();
            String TermType = myAtomDTO.getTermType();
            int cvMemberCount = myAtomDTO.getCvMemberCount();
            //DO we need to put in the print statements??
            System.out.println(aui+"|"+source+"|"+name+"|"+TermType+"|"+cvMemberCount);
            
            }*/
            
            
          //myAtoms = utsContentService.getSourceConceptAtoms(singleUseTicket, "2012AA","150618008","SNOMEDCT",myPsf);
                     /* for (int i = 0; i < myAtoms.size(); i++) {


            AtomDTO myAtomDTO = myAtoms.get(i);

            String aui = myAtomDTO.getUi();
            String source = myAtomDTO.getRootSource();
            String name = myAtomDTO.getTermString().getName();
            String TermType = myAtomDTO.getTermType();
            int cvMemberCount = myAtomDTO.getCvMemberCount();
          
            System.out.println(aui+"|"+source+"|"+name+"|"+TermType+"|"+cvMemberCount);
            } */
 /*   myAtoms = utsContentService.getCodeAtoms(singleUseTicket, "2012AA","D008636","MSHFRE",myPsf);
            
            myPsf.getIncludedSources().add("SNOMEDCT");
            myPsf.getIncludedTermTypes().add("FSN");
            myPsf.getIncludedTermTypes().add("SY");
            myPsf.getIncludedTermTypes().add("PT");

            for (int i = 0; i < myAtoms.size(); i++) {


            AtomDTO myAtomDTO = myAtoms.get(i);

            String aui = myAtomDTO.getUi();
            String source = myAtomDTO.getRootSource();
            String name = myAtomDTO.getTermString().getName();
            String TermType = myAtomDTO.getTermType();
            int cvMemberCount = myAtomDTO.getCvMemberCount();
            //DO we need to put in the print statements??
            System.out.println(aui+"|"+source+"|"+name+"|"+TermType+"|"+cvMemberCount);
            
            }*/
            
           

            
            myAtoms = utsContentService.getConceptAtoms(singleUseTicket, umlsRelease,"C0007097",myPsf);
            //myAtoms = utsContentService.getCodeAtoms(singleUseTicket, umlsRelease,"154895004","SNOMEDCT",myPsf);
            //myAtoms = utsContentService.getSourceConceptAtoms(singleUseTicket, umlsRelease,"262790002","SNOMEDCT",myPsf);
            //myAtoms = utsContentService.getSourceDescriptorAtoms(singleUseTicket, umlsRelease,"D000103","MSH",myPsf);
            
            for (int i = 0; i < myAtoms.size(); i++) {


            AtomDTO myAtomDTO = myAtoms.get(i);

            String aui = myAtomDTO.getUi();
            String source = myAtomDTO.getRootSource();
            String name = myAtomDTO.getTermString().getName();
            String TermType = myAtomDTO.getTermType();
            String srcUi = myAtomDTO.getSourceConcept().getUi();
            //int cvMemberCount = myAtomDTO.getCvMemberCount();
            boolean obsolete = myAtomDTO.isObsolete();
            boolean suppresible = myAtomDTO.isSuppressible();
            
            System.out.println(aui+"\n"+source+"\n"+name+"\n"+TermType+"|"+srcUi+"\n"+obsolete+"\n"+suppresible+"\n");
            }
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
