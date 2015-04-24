package gov.nih.nlm.lo.umls.uts.clients;
import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.content.Psf;
import gov.nih.nlm.uts.webservice.semnet.*;
import gov.nih.nlm.uts.webservice.metadata.*;
import gov.nih.nlm.uts.webservice.finder.*;
import gov.nih.nlm.uts.webservice.security.*;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;


public class MeshNames {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "";
	static String serviceName = "http://umlsks.nlm.nih.gov";
	
	static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
	static UtsWsFinderController  utsFinderService = (new UtsWsFinderControllerImplService()).getUtsWsFinderControllerImplPort();
	static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();
	static StringUtils StringTool = new StringUtils();
	
	
	
    public MeshNames (String username, String password, String umlsRelease) {
	this.username = username;
	this.password = password;
	this.umlsRelease = umlsRelease;
	
    }//end MeshNames contstructor
	
    public static String ticketGrantingTicket() throws Exception{
    	
    	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);

        //use the Proxy Grant Ticket to get a Single Use Ticket
        return ticketGrantingTicket;
    	
    }// end ticketGrantingTicket() method
	
    public String getTermUi(String term, String mode,String tgt) throws Exception{
    	String row = null;
    	gov.nih.nlm.uts.webservice.finder.Psf myPsf = new gov.nih.nlm.uts.webservice.finder.Psf();
    	myPsf.getIncludedSources().add("MSH");
    	ArrayList<String> uis = new ArrayList<>();
    	ArrayList<String> mhs = new ArrayList<>();
    	boolean result;
    	boolean multiple;
         
    	switch (mode) {
    	
    	
    	case "mui":{
    	
    	List<UiLabelRootSource> labels = utsFinderService.findSourceConcepts(securityService.getProxyTicket(tgt,serviceName), umlsRelease, "atom", term, "exact", myPsf);
    	result = labels.isEmpty() ? false:true;
    	multiple = labels.size() > 1? true:false;
    	for (UiLabelRootSource label:labels){uis.add(label.getUi()); mhs.add(label.getLabel());}
    	if(result) {row = StringUtils.join(StringUtils.join(uis," "),"|",StringUtils.join(mhs," "));} 
		else{row = StringUtils.join(term,"|NA|NA");}
    	break;
    	}
    	
    	
        case "cui":{
        List<UiLabel> labels = utsFinderService.findConcepts(securityService.getProxyTicket(tgt, serviceName), umlsRelease, "atom", term, "exact", myPsf);
        result = labels.isEmpty() ? false:true;
    	multiple = labels.size() > 1? true:false;
    	for (UiLabel label:labels){uis.add(label.getUi());}
    	if(result) {row = StringUtils.join(term,"|",multiple,"|",StringUtils.join(uis," "));} 
		else{row = StringUtils.join(term,"|NA|NA");}
        break;
        }
        
    	
    	
    	}//end switch

        return row;

    	
    } //end getMeshMui
    
    

    
	public static void main(String[] args) {
		
		try{
			final MeshNames client = new MeshNames(args[0],args[1],args[2]);
			String inputFile = "etc/mesh-names.txt";
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			String tgt = securityService.getProxyGrantTicket(username, password);
			//System.out.println(tgt);
			String line = null;
			String output = "/Users/steveemrick/Desktop/mesh_cuis-2.txt";
			
			PrintWriter bw = new PrintWriter(new File(output), "UTF-8");
			bw.println("Term|MultipleCUIs|CUI(s)|");
			int index = 1;
			
			while ((line = reader.readLine()) != null) {
				
				String data[] = line.split("\n");
				String term = data[0];
				
			 
				String umlsinfo = client.getTermUi(term,"cui",tgt);
				String meshinfo = client.getTermUi(term,"mui",tgt);
				
				System.out.println(index+":  "+StringUtils.join(term,"|",umlsinfo,"|",meshinfo));
				bw.println(StringUtils.join(term,"|",umlsinfo,"|",meshinfo));
				
				index++;
				//Thread.sleep(500);
				
			}//end while
			reader.close();
			bw.close();
			
		}//end try

		catch (Exception ex) {
			ex.printStackTrace();
		}//end catch

	}//end main class

}//end MeshNames class
