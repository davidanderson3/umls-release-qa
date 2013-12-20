package gov.nih.nlm.uts.qa.clients;

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
            
            //SourceAtomClusterDTO myCode = utsContentService.getCode(singleUseTicket, umlsRelease, "CDR0000039759", "PDQ");
           SourceAtomClusterDTO myCode = utsContentService.getSourceConcept(singleUseTicket, umlsRelease, "M0014340", "MSH");
           // SourceAtomClusterDTO myCode = utsContentService.getSourceDescriptor(singleUseTicket, umlsRelease, "D015060", "MSH");

            
            String name = myCode.getDefaultPreferredName();
            int atomCount = myCode.getAtomCount();
            int codeRelationCount = myCode.getCodeRelationCount();
            int definitionCount = myCode.getDefinitionCount();
            System.out.println(name+"|"+atomCount+"|"+codeRelationCount+"|"+definitionCount);

            	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
