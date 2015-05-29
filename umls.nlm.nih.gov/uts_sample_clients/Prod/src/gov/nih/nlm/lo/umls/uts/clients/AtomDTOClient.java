package gov.nih.nlm.lo.umls.uts.clients;

import java.util.ArrayList;
import java.util.List;

import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.security.*;

public class AtomDTOClient {

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
            
            gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
            List<AtomDTO> myAtoms = new ArrayList<AtomDTO>();
            AtomDTO myAtom = new AtomDTO();

         //myAtom = utsContentService.getAtom(singleUseTicket, umlsRelease, "A1836683");

            myAtom = utsContentService.getDefaultPreferredAtom(singleUseTicket, umlsRelease, "C2916", "NCI");
            
//            String atomName = null;
//            if (myAtom.getTermString() != null){
//            atomName = myAtom.getTermString().getName();
//            }
           // String ui = myAtom.getUi();
            //String tty = myAtom.getTermType();

//            String aui = null;
//            if (myAtom.getSourceConcept() != null){
//                aui = myAtom.getSourceConcept().getUi();
//            	
//            }
//            String termType = myAtom.getTermType();
//            String conceptUi = myAtom.getConcept().getUi();
//            String conceptName = myAtom.getConcept().getDefaultPreferredName();
//            System.out.println(atomName+"|"+ui+"|"+aui+"|"+termType+"|"+conceptUi+"|"+conceptName);
           // System.out.println(ui+"|"+tty);
            
            
           // myAtoms = utsContentService.getConceptAtoms(singleUseTicket, umlsRelease, "C0007097", myPsf);

            for (int i = 0; i < myAtoms.size(); i++) {

            AtomDTO myAtomDTO = myAtoms.get(i);
            String atomName = myAtomDTO.getTermString().getName();
           // String aui = myAtomDTO.getSourceConcept().getUi();
            String termType = myAtomDTO.getTermType();
            String conceptUi = myAtomDTO.getConcept().getUi();
            String conceptName = myAtomDTO.getConcept().getDefaultPreferredName();
            System.out.println(atomName+"|"+termType+"|"+conceptUi+"|"+conceptName);

            }
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
