package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;


import gov.nih.nlm.uts.webservice.metadata.RootSourceDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class ArrayListRootSourceDTOClient {

		private static String username = "";
	    private static String password = ""; 
	    static String umlsRelease = "2013AA";
		static String serviceName = "http://umlsks.nlm.nih.gov";
	    
	static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
	static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

	    
	    public ArrayListRootSourceDTOClient(String username, String password) {
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

				List<RootSourceDTO> myArrSrc = new ArrayList<RootSourceDTO>();
				ArrayListRootSourceDTOClient rtSrcDTOClient = new ArrayListRootSourceDTOClient(args[0],args[1]);
	            
	        	String method = args[2];
	        	
	            switch (method) {
	            case "getAllRootSources": myArrSrc = utsMetadataService.getAllRootSources(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
	            break;
	            case "getRootSourcesByLanguage": myArrSrc = utsMetadataService.getRootSourcesByLanguage(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "GER");
	            break;
	            case "getAllRootSourceFamilies": myArrSrc = utsMetadataService.getAllRootSourceFamilies(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
	            break;
	            case "getRootSourcesByFamily": myArrSrc = utsMetadataService.getRootSourcesByFamily(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "SNOMEDCT");
	            break;
	            case "getRootSourcesByRestrictionLevel": myArrSrc = utsMetadataService.getRootSourcesByRestrictionLevel(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, 0);
	            break;
	            default: out.println("Unrecognized input ");
	        	break; 
	            }
	            
	            for (int i = 0; i < myArrSrc.size(); i++) {

	            RootSourceDTO myArrSrcDTO = myArrSrc.get(i);
	           
	            String abbreviation = myArrSrcDTO.getAbbreviation();
	            int restrictionLevel = myArrSrcDTO.getRestrictionLevel();
	            String family = myArrSrcDTO.getFamily();
	            String expForm = myArrSrcDTO.getExpandedForm();

	            
	            //System.out.println("&lt;tr&gt;&lt;td&gt;"+abbreviation+"&lt;/td&gt;\n&lt;td&gt;"+restrictionLevel+"&lt;/td&gt;\n&lt;td&gt;"+family+"&lt;/td&gt;\n&lt;td&gt;"+expForm+"&lt;/td&gt;&lt;/tr&gt;");

	            System.out.println(abbreviation+"|"+restrictionLevel+"|"+family+"|"+expForm);

	            }
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}


