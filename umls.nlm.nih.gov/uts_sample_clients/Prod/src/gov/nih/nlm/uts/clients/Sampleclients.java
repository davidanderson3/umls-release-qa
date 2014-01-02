package gov.nih.nlm.uts.clients;

import java.lang.*;
import java.util.*;
import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.security.*;

public class Sampleclients {

	public static void main (String[] args) {
		try {
			// Runtime properties
			String username = "umlsguest";
			String password = "umlsguest1!";
			String umlsRelease = "2013AB";
			String serviceName = "http://umlsks.nlm.nih.gov";
			
                        
			UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
            UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();
            
            //get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
            String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);

           
            //use the Proxy Grant Ticket to get a Single Use Ticket
            String singleUseTicket1 = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
            ConceptDTO result1 =  utsContentService.getConcept(singleUseTicket1, umlsRelease, "C0018787");
            
            System.out.println(result1.getUi() );
            System.out.println(result1.getDefaultPreferredName() );
            
            //use the Proxy Grant Ticket to get another Single Use Ticket
            String singleUseTicket2 = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
            ConceptDTO result2 =  utsContentService.getConcept(singleUseTicket2, umlsRelease, "C0004057");
            System.out.println(result2.getUi() );
            System.out.println(result2.getDefaultPreferredName() );
                        
                        
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
