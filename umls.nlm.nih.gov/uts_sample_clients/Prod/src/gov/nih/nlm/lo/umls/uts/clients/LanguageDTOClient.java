package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;

import gov.nih.nlm.uts.webservice.metadata.LanguageDTO;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

public class LanguageDTOClient {


			private static String username = "";
		    private static String password = ""; 
		    static String umlsRelease = "2012AB";
			static String serviceName = "http://umlsks.nlm.nih.gov";
		    
		static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
		static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

		    
		    public LanguageDTOClient(String username, String password) {
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

					List<LanguageDTO> myArrSrc = new ArrayList<LanguageDTO>();
					LanguageDTO languageDTO = new LanguageDTO();

					LanguageDTOClient languageDTOClient = new LanguageDTOClient(args[0],args[1]);
		            
		        	String method = args[2];
		        	
		            switch (method) {
		            case "getAllLanguages": myArrSrc = utsMetadataService.getAllLanguages(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
		            break;
		            case "getLanguage": languageDTO = utsMetadataService.getLanguage(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "ENG");
		            String abbreviation = languageDTO.getAbbreviation();
		            String expForm = languageDTO.getExpandedForm();
 
		            System.out.println(""+abbreviation+"|"+expForm);
		            
		            break;
		            default: out.println("Unrecognized input ");
		        	break; 
		            }
		            
		            for (int i = 0; i < myArrSrc.size(); i++) {

		            LanguageDTO myArrSrcDTO = myArrSrc.get(i);
		           
		            String abbreviation = myArrSrcDTO.getAbbreviation();
		            String expForm = myArrSrcDTO.getExpandedForm();

		            
		            
		            System.out.println(""+abbreviation+"|"+expForm);

		            }
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}

		}

