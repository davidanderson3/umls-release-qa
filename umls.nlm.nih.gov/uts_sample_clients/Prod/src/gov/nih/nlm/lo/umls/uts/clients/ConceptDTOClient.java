package gov.nih.nlm.lo.umls.uts.clients;

import java.awt.List;

import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.security.*;


public class ConceptDTOClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			// Runtime properties
			String username = "debjaniani";
			String password = "Cartoon123!";
			String umlsRelease = "2012AB";
			String serviceName = "http://umlsks.nlm.nih.gov";
			
                        
			UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
            UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();
            
            //get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
            String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
        
            //use the Proxy Grant Ticket to get a Single Use Ticket
            String singleUseTicket = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
            
            
            ConceptDTO myConcept = utsContentService.getConcept(singleUseTicket, "2012AB", "C0220650");
            String preferredName = myConcept.getDefaultPreferredName();
            java.util.List<java.lang.String> semanticTypes = myConcept.getSemanticTypes();
            int numberofAtoms = myConcept.getAtomCount();
            
            System.out.println("numberofAtoms:"+numberofAtoms+"|semanticTypes:"+semanticTypes+"|preferredName:"+preferredName);

            
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		

	}
}
