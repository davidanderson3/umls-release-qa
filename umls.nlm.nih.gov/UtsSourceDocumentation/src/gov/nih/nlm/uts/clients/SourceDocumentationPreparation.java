package gov.nih.nlm.uts.clients;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;
import gov.nih.nlm.uts.webservice.metadata.*;

public class SourceDocumentationPreparation {
	
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "";
	static String serviceName = "http://umlsks.nlm.nih.gov";
	static StringUtils StringTool = new StringUtils();
	//static String path;
	

static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsMetadataController utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public SourceDocumentationPreparation (String username, String password, String umlsRelease) {
	this.username = username;
	this.password = password;
	this.umlsRelease = umlsRelease;
	
    	}

    
	public static String ticketGrantingTicket() throws Exception{
	
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);

        return ticketGrantingTicket;
    	
    }
	
	public void runAllSources() throws Exception {
		
		String dir = umlsRelease;
		File verDir = new File(dir);
		
		
		//Directory existence check
		if(!verDir.exists()){verDir.mkdirs();}
			
		File allSourcesFile = new File(verDir+"/allsources.txt");
		PrintWriter bw = new PrintWriter(new File(allSourcesFile.toString()), "UTF-8");
		
		List<SourceDTO> allSources = utsMetadataService.getAllSources(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);
		
		for(SourceDTO source:allSources){
			
			String rsab = source.getRootSource().getAbbreviation();
			String ssn = source.getRootSource().getShortName();
			String imeta = source.getInsertMetaVersion();
			String language = source.getRootSource().getLanguage().getExpandedForm();
			int srl = source.getRootSource().getRestrictionLevel();
			int versionsAgo = source.getVersionsAgo();

			bw.println(rsab+"|"+ssn+"|"+imeta+"|"+language+"|"+srl+"|"+versionsAgo+"|*");
			
		}
		bw.close();
	
		
	}
	
	public void runUpdatedSources() throws Exception {
		List<SourceDTO> updatedSources = utsMetadataService.getUpdatedSourcesByVersion(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease);

		for(SourceDTO source:updatedSources) {
			
			
            String rsab = source.getRootSource().getAbbreviation();
            String vsab = source.getAbbreviation();
            String son = source.getRootSource().getExpandedForm();
            String ssn = source.getRootSource().getShortName();
            String family = source.getRootSource().getFamily();
            String imeta = source.getInsertMetaVersion();
            //String license =  source.getRootSource().getLicenseContact().getValue();
            String[] license = StringUtils.splitByWholeSeparator(source.getRootSource().getLicenseContact().getValue(),";");
            String[] content = StringUtils.splitByWholeSeparator(source.getRootSource().getContentContact().getValue(),";");
            String[] citation = StringUtils.splitByWholeSeparator(source.getCitation().getValue(),";");
            

            String slc_fields = StringUtils.join(license,"<br/>");
            String scc_fields = StringUtils.join(content,"<br/>");
            String citation_fields = StringUtils.join(citation,"<br/>");
            
            int srl = source.getRootSource().getRestrictionLevel();
            String cxty = source.getRootSource().getContextType();
            String lat = source.getRootSource().getLanguage().getExpandedForm();
            
            
            
            System.out.println(rsab);
            String rsabDir = umlsRelease+"/"+rsab;
            File sourceVerDir = new File(rsabDir);
			
            
		
          
            
            
			//Directory existence check
			if(!sourceVerDir.exists())
				sourceVerDir.mkdirs();
			    File metadatafile = new File(rsabDir+"/metadata.txt");
			    if(!metadatafile.exists()){metadatafile.createNewFile();}
			    PrintWriter bw = new PrintWriter(new File(metadatafile.toString()), "UTF-8");
			    bw.println("Versioned Source Abbreviation^"+vsab+"|Source Offical Name^"+son+"|Short Name^"+ssn+"|Family^"+family+"|Metathesaurus Insertion Version^"+imeta+"|License Contact^"+slc_fields+"|Content Contact^"+scc_fields+"|Citation^"+citation_fields+"|Restriction Level^"+srl+"|Context Type^"+cxty+"|Language^"+lat);
			    
			    bw.close();
			
			
		}
		
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		
		
		try {

			
			SourceDocumentationPreparation srcDocClient = new SourceDocumentationPreparation(args[0],args[1],args[2]);
			
			srcDocClient.runAllSources();
			srcDocClient.runUpdatedSources();	


	        
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	        
		}
}