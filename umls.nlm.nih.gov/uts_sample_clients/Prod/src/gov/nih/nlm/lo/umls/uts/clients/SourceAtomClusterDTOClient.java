package gov.nih.nlm.lo.umls.uts.clients;

import java.util.List;
import java.util.ArrayList;
import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.security.*;

public class SourceAtomClusterDTOClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			// Runtime properties
			String username = "";
			String password = "";
			String umlsRelease = "2014AB";
			String serviceName = "http://umlsks.nlm.nih.gov";
			
                        
			UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
            UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();
            
            //get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
            String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
            gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
        
            //use the Proxy Grant Ticket to get a Single Use Ticket
            String singleUseTicket = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
            
           // SourceAtomClusterDTO myCode = utsContentService.getCode(singleUseTicket, umlsRelease, "53746-4", "LNC");
           // SourceAtomClusterDTO myCode = utsContentService.getSourceConcept(singleUseTicket, umlsRelease, "1481000119100", "SNOMEDCT_US");
           SourceAtomClusterDTO myCode = utsContentService.getSourceDescriptor(singleUseTicket, umlsRelease, "D064927", "MSH");

            
            String name = myCode.getDefaultPreferredName();
            int atomCount = myCode.getAtomCount();
            int attributeCount = myCode.getAttributeCount();
            int relCount = myCode.getSourceDescriptorRelationCount();
            
       
            
            System.out.println("Source Concept Name: "+ name);
            System.out.println("# of Atoms in Source Concept "+ atomCount);
            System.out.println("# of Source Concept Attributes "+ attributeCount);
            System.out.println("# of Relations to other Source Concepts "+ relCount);


            	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
