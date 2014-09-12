package gov.nih.nlm.uts.clients;
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


public class ConceptsClient {

	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "";
	static String serviceName = "http://umlsks.nlm.nih.gov";
	
	static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
	static UtsWsFinderController  utsFinderService = (new UtsWsFinderControllerImplService()).getUtsWsFinderControllerImplPort();
	static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();
	static StringUtils StringTool = new StringUtils();
	
	
	
    public ConceptsClient (String username, String password, String umlsRelease) {
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
	
    
    public String getConcept(String tgt, String release, String cui) throws Exception
    {
    	String row = null;
    	ConceptDTO concept = utsContentService.getConcept(securityService.getProxyTicket(tgt, serviceName), release, cui);
    	
    	return StringUtils.join(concept.getHandle()+"|"+concept.getUi()+"|"+concept.getDefaultPreferredName());
    }
    
    public String getConceptAtoms(String tgt, String release, String cui) throws Exception
    {
    	String row = "";
    	gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
    	List<AtomDTO> conceptAtoms = utsContentService.getConceptAtoms(securityService.getProxyTicket(tgt, serviceName), release, cui, myPsf);
    	
    	for(AtomDTO atom: conceptAtoms)
    	{
    		row += StringUtils.join(atom.getHandle()+"|"+atom.getUi()+"|"+atom.getTermString().getName()+"\n");
    	}
    	return row;
    }
    
    public String getConceptAttributes(String tgt, String release, String cui) throws Exception
    {
    	String row = "";
    	gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
    	List<AttributeDTO> conceptAttributes = utsContentService.getConceptAttributes(securityService.getProxyTicket(tgt, serviceName), release, cui, myPsf);
    	
    	for(AttributeDTO attribute: conceptAttributes)
    	{
    		row += StringUtils.join(attribute.getHandle()+"|"+attribute.getUi()+"|"+attribute.getName()+"\n");
    	}
    	return row;
    }    
    
	public static void main(String[] args) {
		
		try{
			final ConceptsClient client = new ConceptsClient(args[0],args[1],args[2]);
			String inputFile = "etc/cuis.txt";
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			String tgt = securityService.getProxyGrantTicket(username, password);
			//System.out.println(tgt);
			String line = null;
			String output = "/Users/viswanadhamsv/Desktop/cui_output.txt";
			
			PrintWriter bw = new PrintWriter(new File(output), "UTF-8");
			int index = 1;
			double getConceptTime = 0.0;
			double getConceptAtomsTime = 0.0;
			double getConceptAttributesTime = 0.0;
			
			while (index <=50 && (line = reader.readLine()) != null) {
				
				String data[] = line.split("\n");
				String cui = data[0];
				
				double startTime = System.currentTimeMillis();
				String result = client.getConcept(tgt, args[2], cui);
				double t1 = System.currentTimeMillis() - startTime;		
				getConceptTime += t1;
				System.out.println(index+":  concept ----" + t1);
				bw.println(result);
				startTime = System.currentTimeMillis();
				result = client.getConceptAtoms(tgt, args[2], cui);
				double t2 = System.currentTimeMillis() - startTime;
				getConceptAtomsTime += t2;
				System.out.println(index+":  Atom ----" + t2);
				bw.println(result);
				startTime = System.currentTimeMillis();
				result = client.getConceptAttributes(tgt, args[2], cui);
				double t3 = System.currentTimeMillis() - startTime;
				getConceptAttributesTime += t3;
				System.out.println(index+": Attribute ----" + t3);
				
				
				index++;
				//Thread.sleep(500);
				
			}//end while
			
			System.out.println("Totals: " + getConceptTime + " ::: " + getConceptAtomsTime + " ::: " + getConceptAttributesTime);
			System.out.println("Averages: " + getConceptTime/index + " ::: " + getConceptAtomsTime/index + " ::: " + getConceptAttributesTime/index);
			reader.close();
			bw.close();
			
		}//end try

		catch (Exception ex) {
			ex.printStackTrace();
		}//end catch

	}//end main class

}//end MeshNames class
