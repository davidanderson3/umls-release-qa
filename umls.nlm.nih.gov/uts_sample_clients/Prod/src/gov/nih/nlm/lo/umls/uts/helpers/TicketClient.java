package gov.nih.nlm.lo.umls.uts.helpers;

import gov.nih.nlm.uts.webservice.security.UtsFault_Exception;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TicketClient{
    
	private UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();
	private String ticketGrantingTicket;
	private String singleUseTicket;
	private String serviceName = "http://umlsks.nlm.nih.gov";
	private String username = null;
	private String password = null;

	
public TicketClient(String username, String password) {
		
        this.username = username;
        this.password = password;
		
}

public TicketClient () {
	
		Properties prop = new Properties();
		InputStream input = null;
		try {
			 
			input = new FileInputStream("resources/config.properties");
	 
			// load a properties file
			prop.load(input);
	 
			// set your umls username and password in resources/config.properties and
			//add do not commit to the repository
			this.username = prop.getProperty("username");
			this.password = prop.getProperty("password");

	 
		   } catch (IOException ex) {
			ex.printStackTrace();
		
		
	      }
}
	public String getUsername() {
		
		return this.username;
	}
    
	public String getPassword() {
		
		return this.password;
	}

	public String getTicketGrantingTicket () {
    try {
		return securityService.getProxyGrantTicket(username, password);
		//return ticketGrantingTicket;
		
	} catch (UtsFault_Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return "cannot return ticket";
	}

	
		
    
	}
	
	
	public String getSingleUseTicket(String ticket) {

		
		try {
			return securityService.getProxyTicket(ticket, serviceName);
			}
			catch (Exception e) {
			return "";
			}
		
	}
	
}
