package gov.nih.nlm.uts.clients;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;
import gov.nih.nlm.uts.webservice.metadata.*;

public class UTS_Source_documentation_prepClient {
	
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "";
	static String serviceName = "http://umlsks.nlm.nih.gov";
	static StringUtils StringTool = new StringUtils();
	//static String path;
	

static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public UTS_Source_documentation_prepClient (String username, String password, String umlsRelease) {
	this.username = username;
	this.password = password;
	this.umlsRelease = umlsRelease;
	
    	}

    
	public static String ticketGrantingTicket() throws Exception{
	
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);

        return ticketGrantingTicket;
    	
    }
	
	


	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			
			UTS_Source_documentation_prepClient srcDocClient = new UTS_Source_documentation_prepClient(args[0],args[1],args[2]);
			
			String  myStringMetadata  = new String();
			List<SourceDTO> mySourceDTOs = new ArrayList<SourceDTO>();
			//List<SourceCitationDTO> myCitations = utsMetadataService.getAllSourceCitations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
			
			
//			myStringMetadata = utsMetadataService.getAllUMLSVersions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName));
//            System.out.println(myStringMetadata);
//			String[] myarrStringMetadata = myStringMetadata.split("\\|");
//			int i = 0;
//
//			
//			for(i=0; i < myarrStringMetadata.length; i++){

	            	String dir = umlsRelease;
	            			File verDir = new File(dir);
	            			
	            			//Directory existence check
	            			if(!verDir.exists())
	            				verDir.mkdirs();
	            			
	            			 mySourceDTOs = utsMetadataService.getUpdatedSourcesByVersion(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
	            			 
	            			 for (SourceDTO mySource:mySourceDTOs) {

	            		          
	            		            RootSourceDTO rootSource = mySource.getRootSource();
	            		            String rsab = mySource.getRootSource().getAbbreviation();
	            		            String vsab = mySource.getAbbreviation();
	            		            String son = mySource.getRootSource().getExpandedForm();
	            		            String ssn = mySource.getRootSource().getShortName();
	            		            String family = mySource.getRootSource().getFamily();
	            		            String imeta = mySource.getInsertMetaVersion();
	            		            //String license =  mySource.getRootSource().getLicenseContact().getValue();
	            		            String[] license = StringUtils.splitByWholeSeparator(mySource.getRootSource().getLicenseContact().getValue(),";");
	            		            String[] content = StringUtils.splitByWholeSeparator(mySource.getRootSource().getContentContact().getValue(),";");
	            		            String[] citation = StringUtils.splitByWholeSeparator(mySource.getCitation().getValue(),";");
	            		            

	            		            String slc_fields = StringUtils.join(license,"<br/>");
	            		            String scc_fields = StringUtils.join(content,"<br/>");
	            		            String citation_fields = StringUtils.join(citation,"<br/>");
	            		            
	            		            int srl = mySource.getRootSource().getRestrictionLevel();
	            		            String cxty = mySource.getRootSource().getContextType();
	            		            String lat = mySource.getRootSource().getLanguage().getExpandedForm();
	            		            
	            		            
	            		            
	            		            System.out.println(rsab);
	            		            String rsabDir = umlsRelease+"/"+rsab;
	            		            File sourceVerDir = new File(rsabDir);
	    	            			
	            		            
	    	            		
	            		          
	            		            
	            		            
	    	            			//Directory existence check
	    	            			if(!sourceVerDir.exists())
	    	            				sourceVerDir.mkdirs();
	    	            			    File metadatafile = new File(rsabDir+"/metadata.txt");
	    	            			    if(!metadatafile.exists()){metadatafile.createNewFile();}
	    	            			    PrintWriter bw = new PrintWriter(new File(metadatafile.toString()), "UTF-8");
	    	            			    bw.println("*Source Metadata|Versioned Source Abbreviation|Source Offical Name|Short Name|Family|Insertion Version|License Contact|Content Contact|Source Citation|Restriction Level|Context Type|Language");	    	            			 
	    	            			    bw.println(vsab+"|"+son+"|"+ssn+"|"+family+"|"+imeta+"|"+slc_fields+"|"+scc_fields+"|"+citation_fields+"|"+srl+"|"+cxty+"|"+lat);
	    	            			    bw.println("!");
	    	            			    bw.close();
	    	            			    
	    	            			 
	    	            			    
	            		            
	            			 }
	            			 
			//}
	            				            			


	        
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	        
		}
}