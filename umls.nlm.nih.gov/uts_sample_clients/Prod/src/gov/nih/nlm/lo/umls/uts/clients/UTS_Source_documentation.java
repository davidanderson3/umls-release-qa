package gov.nih.nlm.lo.umls.uts.clients;

import gov.nih.nlm.uts.webservice.content.AtomClusterRelationDTO;
import gov.nih.nlm.uts.webservice.content.AtomDTO;
import gov.nih.nlm.uts.webservice.content.AtomTreePositionDTO;
import gov.nih.nlm.uts.webservice.content.AtomTreePositionPathDTO;
import gov.nih.nlm.uts.webservice.content.AttributeDTO;
import gov.nih.nlm.uts.webservice.content.SourceAtomClusterTreePositionDTO;
import gov.nih.nlm.uts.webservice.content.SourceAtomClusterTreePositionPathDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.finder.UiLabel;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderController;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.*;

public class UTS_Source_documentation {
	
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "";
	static String serviceName = "http://umlsks.nlm.nih.gov";
	static String path;
	

static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsFinderController  utsFinderService = (new UtsWsFinderControllerImplService()).getUtsWsFinderControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public UTS_Source_documentation (String username, String password, String umlsRelease) {
	this.username = username;
	this.password = password;
	this.umlsRelease = umlsRelease;
	
    	}

	public static String ticketGrantingTicket() throws Exception{
	
    	//get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
        String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);

        //use the Proxy Grant Ticket to get a Single Use Ticket
        return ticketGrantingTicket;
    	
    }
	
	
	
	
	
	//Method for Concept calls
	public static void findSrcConcepts(String val1, String val2, String path) throws Exception{

	       
		
        PrintWriter bw = new PrintWriter(new File(path), "UTF-8"); 
        bw.println("Results for "+val2+":");
        gov.nih.nlm.uts.webservice.finder.Psf myPsf = new gov.nih.nlm.uts.webservice.finder.Psf();
        myPsf.getIncludedSources().add(val1);
        myPsf.setPageLn(500);

        java.util.List<UiLabel> myFindConcepts = new ArrayList<UiLabel>();

        myFindConcepts = utsFinderService.findConcepts(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "sourceConcept", val2, "exact", myPsf);
        bw.println("*Concept Information|CUI|Preferred Name");
        
		if (myFindConcepts.size() == 0){
		
		bw.println("None");      
		} 
		
		else {
		        
		for (int i = 0; i < myFindConcepts.size(); i++) {
		
		UiLabel myFinCon = myFindConcepts.get(i);
		String ui = myFinCon.getUi();
		String label = myFinCon.getLabel();
		
		bw.println(ui+"|"+label);
		//System.out.println(label+"|"+ui+"\n");
		}
		}
		        bw.println("!");

	       
	       

	        AtomDTO myAtom = new AtomDTO();
		    myAtom = utsContentService.getDefaultPreferredAtom(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1);
	        bw.println("*Atom Information|Default Preferred Atom|Atom Name|Term Type");
	        //bw.newLine();

		    String defPrefUi = myAtom.getUi();
		    String atomName = myAtom.getTermString().getName();
		    String termType = myAtom.getTermType();

		    bw.println(defPrefUi+"|"+atomName+"|"+termType);
		    //bw.newLine();
		    bw.println("!");
		    //bw.newLine();

        
        
		    
        gov.nih.nlm.uts.webservice.content.Psf myconPsf = new gov.nih.nlm.uts.webservice.content.Psf();
		myconPsf.getIncludedSources().add(val1);
		myconPsf.setPageLn(500);
		
        java.util.List<AtomDTO> myAtoms = new ArrayList<AtomDTO>();
        myAtoms = utsContentService.getSourceConceptAtoms(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, myconPsf);

        bw.println("*Source Concept Atoms|Atom ID|SUI|Term|Term Type|Source Atom ID|Source Concept|Obsolescence|Suppressibility");
	    //bw.newLine();

        
        if (myAtoms.size() == 0){
        	
        	bw.println("None");	
 	       //bw.newLine();

        } 
        
        else {

        for (int i = 0; i < myAtoms.size(); i++) {


        AtomDTO myAtomDTO = myAtoms.get(i);

        String ui = myAtomDTO.getUi();
	    String sui = myAtomDTO.getTermString().getUi();
        String name = myAtomDTO.getTermString().getName();        
        String TermType = myAtomDTO.getTermType();
        String sourceUi = myAtomDTO.getSourceUi();
        String srcConcept = null;


		if (myAtomDTO.getSourceConcept() != (null) ){

	     srcConcept = myAtomDTO.getSourceConcept().getUi();
	     }
		
		boolean obsolete = myAtomDTO.isObsolete();
		boolean suppressible = myAtomDTO.isSuppressible();


	    
	    bw.println(ui+"|"+sui+"|"+name+"|"+TermType+"|"+sourceUi+"|"+srcConcept+"|"+obsolete+"|"+suppressible);
	    //bw.newLine();


	    }
                }

		 bw.println("!");
	     //bw.newLine();

		 
		 
  
			gov.nih.nlm.uts.webservice.content.Psf myAtomTreePsf = new gov.nih.nlm.uts.webservice.content.Psf();
			myAtomTreePsf.setPageLn(500);

		    List<AtomTreePositionDTO> myarrAtomTrPos = new ArrayList<AtomTreePositionDTO>();
		    myarrAtomTrPos = utsContentService.getAtomTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, defPrefUi, myAtomTreePsf);
		      
		    for (int i = 0; i < myarrAtomTrPos.size(); i++) {

		      AtomTreePositionDTO myAtmTreePosDTO = myarrAtomTrPos.get(i);
		      String ui = myAtmTreePosDTO.getUi();
		      
		      java.util.List<AtomTreePositionPathDTO> myAtomTreePosPathDTOClient = new ArrayList<AtomTreePositionPathDTO>();
		      myAtomTreePosPathDTOClient = utsContentService.getAtomTreePositionPathsToRoot(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myAtomTreePsf);
		      //bw.println("*Tree Position "+(i+1)+":");
		      
			     if (myAtomTreePosPathDTOClient.size() == 0){
			        	
			        	bw.println("None");	
			   	        //bw.newLine();

			        } 
			        
			     else {

			     for (int j = 0; j < myAtomTreePosPathDTOClient.size(); j++) {
			
			      AtomTreePositionPathDTO myAtmTrPosDTO = myAtomTreePosPathDTOClient.get(j);
			
			      List<AtomTreePositionDTO> treepos = myAtmTrPosDTO.getTreePositions();
			      bw.println("*Tree Position Paths To Root "+(j+1)+"|Default Preferred ID|Default Preferred Name");
   			     //bw.newLine();

			
				     for (int k = 0; k < treepos.size(); k++) {
				       AtomTreePositionDTO getj = treepos.get(k);
				       String defPrefName = getj.getDefaultPreferredName();
				       String Ui = getj.getUi();
				        
				       bw.println(Ui+"|"+defPrefName);
				       //bw.newLine();
				       
				       }
			     		}
					
			        }
					 bw.println("!");
					 //bw.newLine();
	        
			        
			        
			    List<AtomTreePositionDTO> myAtomTreePosChildrenDTO = new ArrayList<AtomTreePositionDTO>();
			    myAtomTreePosChildrenDTO = utsContentService.getAtomTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myAtomTreePsf);
			    bw.println("*Tree Position Children|Default Preferred ID|Default Preferred Name");
			    //bw.newLine();
			    
			     if (myAtomTreePosChildrenDTO.size() == 0){
			        	
			        	bw.println("None");	
			        	//bw.newLine();
			        } 
			        
			     else {

				     for (int j = 0; j < myAtomTreePosChildrenDTO.size(); j++) {
				
				        AtomTreePositionDTO myAtmTrPosChilDTO = myAtomTreePosChildrenDTO.get(j);
				
				        String defaultPrefName = myAtmTrPosChilDTO.getDefaultPreferredName();
				        String cUi = myAtmTrPosChilDTO.getUi();
					        
					    bw.println(cUi+"|"+defaultPrefName);
					    //bw.newLine();
					        }
			     		}
					 bw.println("!");
					 //bw.newLine();
				     
					 
				     
				     
				List<AtomTreePositionDTO> myarrTreePosSiblingDTOClient = new ArrayList<AtomTreePositionDTO>();
				myarrTreePosSiblingDTOClient = utsContentService.getAtomTreePositionSiblings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myAtomTreePsf);
				bw.println("*Tree Position Sibling|Default Preferred ID|Default Preferred Name");
				//bw.newLine();

			     if (myarrTreePosSiblingDTOClient.size() == 0){
			        	
			        	bw.println("None");	
			        	//bw.newLine();
			        } 
			        
			     else {
					for (int j = 0; j < myarrTreePosSiblingDTOClient.size(); j++) {
						
						 AtomTreePositionDTO myAtmClustTrPosDTO = myarrTreePosSiblingDTOClient.get(j);
						
						 String defaultPrefName = myAtmClustTrPosDTO.getDefaultPreferredName();
						 String cUi = myAtmClustTrPosDTO.getUi();
							        
						 bw.println(cUi+"|"+defaultPrefName);
						 //bw.newLine();
						    }
			     		}       
					 bw.println("!");
					 //bw.newLine();
		    }
		
					 
        
        
        gov.nih.nlm.uts.webservice.content.Psf mySrcConPsf = new gov.nih.nlm.uts.webservice.content.Psf();
        mySrcConPsf.getIncludedRelationLabels().add("RO");
        mySrcConPsf.getIncludedRelationLabels().add("RB");
        mySrcConPsf.getIncludedRelationLabels().add("SY");
        mySrcConPsf.getIncludedSources().add(val1);
        mySrcConPsf.setPageLn(500);

        List<AtomClusterRelationDTO> myAtomClusterRelations = new ArrayList<AtomClusterRelationDTO>();

        myAtomClusterRelations = utsContentService.getSourceConceptSourceConceptRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, mySrcConPsf);
        bw.println("*Source Concept Source Concept Relations|Relation Label|Additional Relation Label|Related Atom Cluster ID|Related Atom Cluster Default Preferred Name|Suppressibility");
        //bw.newLine();
        
        if (myAtomClusterRelations.size() == 0){
        	
        	bw.println("None");	
        	//bw.newLine();
        } 
        
        else {

        for (int i = 0; i < myAtomClusterRelations.size(); i++) {

        AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
        String relationLabel = myAtomClusterRelationDTO.getRelationLabel();
        String addRelationLabel = myAtomClusterRelationDTO.getAdditionalRelationLabel();
        String relAtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
        String relAtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
        boolean supp = myAtomClusterRelationDTO.isSuppressible();
        
        bw.println(relationLabel+"|"+addRelationLabel+"|"+relAtomClusterUi+"|"+relAtomClusterName+"|"+supp);
        //bw.newLine();
	    
        }
        }
        
		 bw.println("!");
		 //bw.newLine();
        
        
		 
		 gov.nih.nlm.uts.webservice.content.Psf myPsf1 = new gov.nih.nlm.uts.webservice.content.Psf();
		 myPsf1.setPageLn(500);
	     List<AttributeDTO> myAttributes = new ArrayList<AttributeDTO>();
	     myAttributes = utsContentService.getSourceConceptAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1,myPsf1);
	     bw.println("*Source Concept Attributes|Attribute Name|Attribute Value");
	     //bw.newLine();
	     
	     if (myAttributes.size() == 0){
	        	
	        	bw.println("None");	
	        	//bw.newLine();
	        } 
	        
	     else {
	     
	      for (int i = 0; i < myAttributes.size(); i++) {

	      AttributeDTO myAttributeDTO = myAttributes.get(i);
	      String attributeName = myAttributeDTO.getName();
	      String attributeValue = myAttributeDTO.getValue();

	      bw.println(attributeName+"|"+attributeValue);
	     // bw.newLine();

	      } 
	     }
		 bw.println("!");
		 
		 
        
		gov.nih.nlm.uts.webservice.content.Psf myTreePsf = new gov.nih.nlm.uts.webservice.content.Psf();
		myTreePsf.setPageLn(500);


        List<SourceAtomClusterTreePositionDTO> myarrAtomClustTrPosClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
        myarrAtomClustTrPosClient = utsContentService.getSourceConceptTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, myTreePsf);
        

        for (int i = 0; i < myarrAtomClustTrPosClient.size(); i++) {

        SourceAtomClusterTreePositionDTO myAtmClustTreePosDTO = myarrAtomClustTrPosClient.get(i);
        String ui = myAtmClustTreePosDTO.getUi();
        
    	java.util.List<SourceAtomClusterTreePositionPathDTO> myarrSrcDescTreePosPathDTOClient = new ArrayList<SourceAtomClusterTreePositionPathDTO>();
    	myarrSrcDescTreePosPathDTOClient = utsContentService.getSourceConceptTreePositionPathsToRoot(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
    	bw.println("*Tree Position "+(i+1)+":");
    	//bw.newLine();
    	
        if (myarrSrcDescTreePosPathDTOClient.size() == 0){
        	
        	bw.println("None");	
        	//bw.newLine();
        } 
        
        else {

	        for (int j = 0; j < myarrSrcDescTreePosPathDTOClient.size(); j++) {
	
	        SourceAtomClusterTreePositionPathDTO myAtmClustTrPosDTO = myarrSrcDescTreePosPathDTOClient.get(j);

	        List<SourceAtomClusterTreePositionDTO> treepos = myAtmClustTrPosDTO.getTreePositions();
		    bw.println("*Tree Position Paths To Root"+(j+1)+"|Cluster ID|Default Preferred Name");
		    //bw.newLine();

	
		        for (int k = 0; k < treepos.size(); k++) {
		        SourceAtomClusterTreePositionDTO getj = treepos.get(k);
		        String clusterUi = getj.getCluster().getUi();
		        String defPrefName = getj.getDefaultPreferredName();
		        
		        bw.println(clusterUi+"|"+defPrefName);
		        //bw.newLine();
		        }
	        }
				 bw.println("!");
				 //bw.newLine();
	
	        }
	        
	        
	        
	    List<SourceAtomClusterTreePositionDTO> myarrSrcDescTreePosChildrenDTOClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
	    myarrSrcDescTreePosChildrenDTOClient = utsContentService.getSourceConceptTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
	    bw.println("*Tree Position Children|Cluster ID|Default Preferred Name");
	    //bw.newLine();
	    
        if (myarrSrcDescTreePosChildrenDTOClient.size() == 0){
        	
        	bw.println("None");	
        	//bw.newLine();
        } 
        
        else {

		     for (int j = 0; j < myarrSrcDescTreePosChildrenDTOClient.size(); j++) {
		
		        SourceAtomClusterTreePositionDTO myAtmClustTrPosDTO = myarrSrcDescTreePosChildrenDTOClient.get(j);
		        String clusterUi = myAtmClustTrPosDTO.getCluster().getUi();
		        String defaultPrefName = myAtmClustTrPosDTO.getCluster().getDefaultPreferredName();
			        
			    bw.println(clusterUi+"|"+defaultPrefName);
			    //bw.newLine();
			        }
        		}
			        
			 bw.println("!");
			 //bw.newLine();
		     
			 
		     
		     
		List<SourceAtomClusterTreePositionDTO> myarrSrcDescTreePosSiblingDTOClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
		myarrSrcDescTreePosSiblingDTOClient = utsContentService.getSourceConceptTreePositionSiblings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
		bw.println("*Tree Position Sibling|Cluster ID|Default Preferred Name");
		//bw.newLine();
		
        if (myarrSrcDescTreePosSiblingDTOClient.size() == 0){
        	
        	bw.println("None");	
        	//bw.newLine();
        } 
        
        else {

			for (int j = 0; j < myarrSrcDescTreePosSiblingDTOClient.size(); j++) {
				
				 SourceAtomClusterTreePositionDTO myAtmClustTrPosDTO = myarrSrcDescTreePosSiblingDTOClient.get(j);
				 String clusterUi = myAtmClustTrPosDTO.getCluster().getUi();
				 String defaultPrefName = myAtmClustTrPosDTO.getCluster().getDefaultPreferredName();
					        
				 bw.println(clusterUi+"|"+defaultPrefName);
				 //bw.newLine();
				    }
        	}
					        
			 bw.println("!");
			 //bw.newLine();
	     
        }    
		bw.println("!");
		//bw.newLine();
		bw.close(); 

	}
	
	
	
	
	
	//Method for Descriptor calls
	public static void findDescriptors(String val1, String val2, String path) throws Exception{
		

        PrintWriter bw = new PrintWriter(new File(path), "UTF-8"); 
        bw.println("Results for "+val2+":");
        gov.nih.nlm.uts.webservice.finder.Psf myPsf = new gov.nih.nlm.uts.webservice.finder.Psf();
        myPsf.getIncludedSources().add(val1);
		myPsf.setPageLn(500);


        java.util.List<UiLabel> myFindConcepts = new ArrayList<UiLabel>();

        myFindConcepts = utsFinderService.findConcepts(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "sourceDescriptor", val2, "exact", myPsf);
        bw.println("*Concept Information|CUI|Preferred Name");
        
		if (myFindConcepts.size() == 0){
		
		bw.println("None");      
		} 
		
		else {
		        
		for (int i = 0; i < myFindConcepts.size(); i++) {
		
		UiLabel myFinCon = myFindConcepts.get(i);
		String ui = myFinCon.getUi();
		String label = myFinCon.getLabel();
		
		bw.println(ui+"|"+label);
		//System.out.println(label+"|"+ui+"\n");
		}
		}
		        bw.println("!");	       
	       
	     AtomDTO myAtoms = new AtomDTO();
		    myAtoms = utsContentService.getDefaultPreferredAtom(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1);
	        bw.println("*Atom Information|Default Preferred Atom|Atom Name|Term Type");
	        //bw.newLine();

		    String defPrefUi = myAtoms.getUi();
		    String atomName = myAtoms.getTermString().getName();
		    String termType = myAtoms.getTermType();

		    bw.println(defPrefUi+"|"+atomName+"|"+termType);
		   // bw.newLine();
		    bw.println("!");
		    //bw.newLine();
		    
		    
		 gov.nih.nlm.uts.webservice.content.Psf myconPsf = new gov.nih.nlm.uts.webservice.content.Psf();
		 myconPsf.getIncludedSources().add(val1);
		 myconPsf.setPageLn(500);

		 java.util.List<AtomDTO> myAtom = new ArrayList<AtomDTO>();
		 myAtom = utsContentService.getSourceDescriptorAtoms(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, myconPsf);
	     bw.println("*Source Descriptor Atoms|Atom ID|Source Atom ID|Term|Term Type|Source Concept|Source Descriptor|Obsolescence|Suppressibility");
	     //bw.newLine();
	        if (myAtom.size() == 0){
	        	
	        	bw.println("None");	
	        	//bw.newLine();
	        } 
	        
	        else {
	        	
	
		      for (int i = 0; i < myAtom.size(); i++) {
		
		      AtomDTO myAtomDTO = myAtom.get(i);
		
		      String ui = myAtomDTO.getUi();
		      String sui = myAtomDTO.getSourceUi();
		      String name = myAtomDTO.getTermString().getName();
		      String TermType = myAtomDTO.getTermType();
		      //int atomRelCount = myAtomDTO.getAtomRelationCount();
		      //int attributeCount = myAtomDTO.getAttributeCount();
		      String srcConcept = null;
		      String srcDescriptor = null;
		      
			     if (myAtomDTO.getSourceConcept() != (null) ){

			     srcConcept = myAtomDTO.getSourceConcept().getUi();
			     }
			     
			     
			     if (myAtomDTO.getSourceDescriptor() != (null) ){

			     srcDescriptor = myAtomDTO.getSourceDescriptor().getUi();
			     }
			     
			   boolean obsolete = myAtomDTO.isObsolete();
			   boolean suppressible = myAtomDTO.isSuppressible();
			      
		     bw.println(ui+"|"+sui+"|"+name+"|"+TermType+"|"+srcConcept+"|"+srcDescriptor+"|"+obsolete+"|"+suppressible);
		     //bw.newLine();
		      }
	        }
	      
	    bw.println("!");
	    //bw.newLine();
	    


	      
		gov.nih.nlm.uts.webservice.content.Psf myAtomTreePsf = new gov.nih.nlm.uts.webservice.content.Psf();
		myAtomTreePsf.setPageLn(500);


	    List<AtomTreePositionDTO> myarrAtomTrPos = new ArrayList<AtomTreePositionDTO>();
	    myarrAtomTrPos = utsContentService.getAtomTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, defPrefUi, myAtomTreePsf);
	      
	    for (int i = 0; i < myarrAtomTrPos.size(); i++) {

	      AtomTreePositionDTO myAtmTreePosDTO = myarrAtomTrPos.get(i);
	      String ui = myAtmTreePosDTO.getUi();
	      //bw.println("*Atom Tree Position: "+ui);
	      java.util.List<AtomTreePositionPathDTO> myAtomTreePosPathDTOClient = new ArrayList<AtomTreePositionPathDTO>();
	      myAtomTreePosPathDTOClient = utsContentService.getAtomTreePositionPathsToRoot(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myAtomTreePsf);
	      
	      //bw.println("*Tree Position "+(i+1)+":");
	      
		     if (myAtomTreePosPathDTOClient.size() == 0){
		        	
		        	bw.println("None");	
		        	//bw.newLine();
		        } 
		        
		     else {

		     for (int j = 0; j < myAtomTreePosPathDTOClient.size(); j++) {
		
		      AtomTreePositionPathDTO myAtmTrPosDTO = myAtomTreePosPathDTOClient.get(j);
		
		      List<AtomTreePositionDTO> treepos = myAtmTrPosDTO.getTreePositions();
		      bw.println("*Tree Position Paths To Root"+(j+1)+"|Default Preferred ID|Default Preferred Name");
		      //bw.newLine();
		
			     for (int k = 0; k < treepos.size(); k++) {
			       AtomTreePositionDTO getj = treepos.get(k);
			       String defPrefName = getj.getDefaultPreferredName();
			       String Ui = getj.getUi();
			        
			       bw.println(Ui+"|"+defPrefName);
			      // bw.newLine();
			       }
		     		}
				
		        }
				 bw.println("!");
				 //bw.newLine();
        
		        
		        
		    List<AtomTreePositionDTO> myAtomTreePosChildrenDTO = new ArrayList<AtomTreePositionDTO>();
		    myAtomTreePosChildrenDTO = utsContentService.getAtomTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myAtomTreePsf);
		    bw.println("*Tree Position Children|Default Preferred ID|Default Preferred Name");
		    //bw.newLine();
		    
		     if (myAtomTreePosChildrenDTO.size() == 0){
		        	
		        	bw.println("None");	
		        	//bw.newLine();
		        } 
		        
		     else {

			     for (int j = 0; j < myAtomTreePosChildrenDTO.size(); j++) {
			
			        AtomTreePositionDTO myAtmTrPosChilDTO = myAtomTreePosChildrenDTO.get(j);
			
			        String defaultPrefName = myAtmTrPosChilDTO.getDefaultPreferredName();
			        String Ui = myAtmTrPosChilDTO.getUi();
				        
				    bw.println(Ui+"|"+defaultPrefName);
				   // bw.newLine();
				        }
		     		}
				 bw.println("!");
				 //bw.newLine();
			     
				 
			     
			     
			List<AtomTreePositionDTO> myarrTreePosSiblingDTOClient = new ArrayList<AtomTreePositionDTO>();
			myarrTreePosSiblingDTOClient = utsContentService.getAtomTreePositionSiblings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myAtomTreePsf);
			bw.println("*Tree Position Sibling|Default Preferred ID|Default Preferred Name");
			//bw.newLine();

		     if (myarrTreePosSiblingDTOClient.size() == 0){
		        	
		        	bw.println("None");	
		        	//bw.newLine();
		        } 
		        
		     else {
				for (int j = 0; j < myarrTreePosSiblingDTOClient.size(); j++) {
					
					 AtomTreePositionDTO myAtmClustTrPosDTO = myarrTreePosSiblingDTOClient.get(j);
					
					 String defaultPrefName = myAtmClustTrPosDTO.getDefaultPreferredName();
					 String Ui = myAtmClustTrPosDTO.getUi();
						        
					 bw.println(Ui+"|"+defaultPrefName);
					 //bw.newLine();
					    }
		     		}       
				 bw.println("!");
				// bw.newLine();
	    	}
	    
	    
	    
	    
	     gov.nih.nlm.uts.webservice.content.Psf mySrcConPsf = new gov.nih.nlm.uts.webservice.content.Psf();
	     mySrcConPsf.getIncludedRelationLabels().add("RB");
	     mySrcConPsf.getIncludedRelationLabels().add("RO");
         mySrcConPsf.getIncludedSources().add(val1);
	     mySrcConPsf.setPageLn(500);


	     List<AtomClusterRelationDTO> myAtomClusterRelations = new ArrayList<AtomClusterRelationDTO>();

	     myAtomClusterRelations = utsContentService.getSourceDescriptorSourceDescriptorRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, mySrcConPsf);
	     bw.println("*Source Descriptor Source Descriptor Relations|Relation Label|Additional Relation Label|Related Atom Cluster ID|Related Atom Cluster Default Preferred Name|Suppressibility");
	     //bw.newLine();

	     if (myAtomClusterRelations.size() == 0){
	        	
	        	bw.println("None");	
	        	//bw.newLine();
	        } 
	        
	     else {
	        	
	        
	     for (int i = 0; i < myAtomClusterRelations.size(); i++) {

	        AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
	        String relationLabel = myAtomClusterRelationDTO.getRelationLabel();
	        String addRelationLabel = myAtomClusterRelationDTO.getAdditionalRelationLabel();
	        String relAtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
            String relAtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
            boolean supp = myAtomClusterRelationDTO.isSuppressible();
            
            bw.println(relationLabel+"|"+addRelationLabel+"|"+relAtomClusterUi+"|"+relAtomClusterName+"|"+supp);
            //bw.newLine();
            
            }
	       }
	        
		 bw.println("!");
		 //bw.newLine();
	        
	        
	        
		 gov.nih.nlm.uts.webservice.content.Psf myPsf1 = new gov.nih.nlm.uts.webservice.content.Psf();
		 myPsf1.setPageLn(500);
	     List<AttributeDTO> myAttributes = new ArrayList<AttributeDTO>();
	     myAttributes = utsContentService.getSourceDescriptorAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1,myPsf1);
	     bw.println("*Source Descriptor Attributes|Attribute Name|Attribute Value");
	     //bw.newLine();
	     
	     if (myAttributes.size() == 0){
	        	
	        	bw.println("None");	
	        	//bw.newLine();
	        } 
	        
	     else {
	     
	      for (int i = 0; i < myAttributes.size(); i++) {

	      AttributeDTO myAttributeDTO = myAttributes.get(i);
	      String attributeName = myAttributeDTO.getName();
	      String attributeValue = myAttributeDTO.getValue();

	      bw.println(attributeName+"|"+attributeValue);
	     // bw.newLine();

	      } 
	     }
		 bw.println("!");
		 //bw.newLine();

	      
	      
		gov.nih.nlm.uts.webservice.content.Psf myTreePsf = new gov.nih.nlm.uts.webservice.content.Psf();
		myTreePsf.setPageLn(500);

	    List<SourceAtomClusterTreePositionDTO> myarrAtomClustTrPosClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
	    myarrAtomClustTrPosClient = utsContentService.getSourceDescriptorTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, myTreePsf);
	      
	    for (int i = 0; i < myarrAtomClustTrPosClient.size(); i++) {

	      SourceAtomClusterTreePositionDTO myAtmClustTreePosDTO = myarrAtomClustTrPosClient.get(i);
	      String ui = myAtmClustTreePosDTO.getUi();
	      java.util.List<SourceAtomClusterTreePositionPathDTO> myarrSrcDescTreePosPathDTOClient = new ArrayList<SourceAtomClusterTreePositionPathDTO>();
	      myarrSrcDescTreePosPathDTOClient = utsContentService.getSourceDescriptorTreePositionPathsToRoot(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
	      //bw.println("*Tree Position "+(i+1)+":");
	      
		     if (myarrSrcDescTreePosPathDTOClient.size() == 0){
		        	
		        	bw.println("None");	
		        	//bw.newLine();
		        } 
		        
		     else {

		     for (int j = 0; j < myarrSrcDescTreePosPathDTOClient.size(); j++) {
		
		      SourceAtomClusterTreePositionPathDTO myAtmClustTrPosDTO = myarrSrcDescTreePosPathDTOClient.get(j);
		
		      List<SourceAtomClusterTreePositionDTO> treepos = myAtmClustTrPosDTO.getTreePositions();
		      bw.println("*Tree Position Paths To Root"+(j+1)+"|Cluster ID|Default Preferred Name");
		      //bw.newLine();
		
			     for (int k = 0; k < treepos.size(); k++) {
			       SourceAtomClusterTreePositionDTO getj = treepos.get(k);
			       String defPrefName = getj.getDefaultPreferredName();
			       String clusterUi = getj.getCluster().getUi();
			        
			       bw.println(clusterUi+"|"+defPrefName);
			       //bw.newLine();
			       }
		     		}
				
		        }
				 bw.println("!");
				 //bw.newLine();
        
		        
		        
		    List<SourceAtomClusterTreePositionDTO> myarrSrcDescTreePosChildrenDTOClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
		    myarrSrcDescTreePosChildrenDTOClient = utsContentService.getSourceDescriptorTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
		    bw.println("*Tree Position Children|Cluster ID|Default Preferred Name");
		    //bw.newLine();
		    
		     if (myarrSrcDescTreePosChildrenDTOClient.size() == 0){
		        	
		        	bw.println("None");	
		        	//bw.newLine();
		        } 
		        
		     else {

			     for (int j = 0; j < myarrSrcDescTreePosChildrenDTOClient.size(); j++) {
			
			        SourceAtomClusterTreePositionDTO myAtmClustTrPosDTO = myarrSrcDescTreePosChildrenDTOClient.get(j);
			
			        String defaultPrefName = myAtmClustTrPosDTO.getCluster().getDefaultPreferredName();
			        String clusterUi = myAtmClustTrPosDTO.getCluster().getUi();
				        
				    bw.println(clusterUi+"|"+defaultPrefName);
				    //bw.newLine();
				        }
		     		}
				 bw.println("!");
				 //bw.newLine();
			     
				 
			     
			     
			List<SourceAtomClusterTreePositionDTO> myarrSrcDescTreePosSiblingDTOClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
			myarrSrcDescTreePosSiblingDTOClient = utsContentService.getSourceDescriptorTreePositionSiblings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
			bw.println("*Tree Position Sibling|Cluster ID|Default Preferred Name");
			//bw.newLine();

		     if (myarrSrcDescTreePosSiblingDTOClient.size() == 0){
		        	
		        	bw.println("None");	
		        	//bw.newLine();
		        } 
		        
		     else {
				for (int j = 0; j < myarrSrcDescTreePosSiblingDTOClient.size(); j++) {
					
					   SourceAtomClusterTreePositionDTO myAtmClustTrPosDTO = myarrSrcDescTreePosSiblingDTOClient.get(j);
					
					 String defaultPrefName = myAtmClustTrPosDTO.getCluster().getDefaultPreferredName();
					 String clusterUi = myAtmClustTrPosDTO.getCluster().getUi();
						        
					 bw.println(clusterUi+"|"+defaultPrefName);
					 //bw.newLine();
					    }
		     		}       
				 bw.println("!");
				 //bw.newLine();


	        }
		bw.println("!");
	    bw.close();


		}
	
	
	
	
	public static void findCodes(String val1, String val2, String val4, String path) throws Exception{
		

        PrintWriter bw = new PrintWriter(new File(path), "UTF-8"); 
        bw.println("Results for "+val2+":");
        gov.nih.nlm.uts.webservice.finder.Psf myPsf = new gov.nih.nlm.uts.webservice.finder.Psf();
        myPsf.getIncludedSources().add(val1);
        myPsf.setPageLn(500);

        java.util.List<UiLabel> myFindConcepts = new ArrayList<UiLabel>();

        myFindConcepts = utsFinderService.findConcepts(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "code", val2, "exact", myPsf);
        bw.println("*Concept Information|CUI|Preferred Name");
        
		if (myFindConcepts.size() == 0){
		
		bw.println("None");      
		} 
		
		else {
		        
		for (int i = 0; i < myFindConcepts.size(); i++) {
		
		UiLabel myFinCon = myFindConcepts.get(i);
		String ui = myFinCon.getUi();
		String label = myFinCon.getLabel();
		
		bw.println(ui+"|"+label);
		//System.out.println(label+"|"+ui+"\n");
		}
		}
		        bw.println("!");

	       
		 AtomDTO myAtoms = new AtomDTO();
		    myAtoms = utsContentService.getDefaultPreferredAtom(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1);
	        bw.println("*Atom Information|Default Preferred Atom|Atom Name|Term Type");
	        //bw.newLine();

		    String defPrefUi = myAtoms.getUi();
		    String atomName = myAtoms.getTermString().getName();
		    String termType = myAtoms.getTermType();

		    bw.println(defPrefUi+"|"+atomName+"|"+termType);
		    //bw.newLine();
		    bw.println("!");
		    //bw.newLine();
		    
		 
		 gov.nih.nlm.uts.webservice.content.Psf myconPsf = new gov.nih.nlm.uts.webservice.content.Psf();
		 myconPsf.setIncludeSuppressible(false);
		 myconPsf.getIncludedSources().add(val1);
		 myconPsf.setPageLn(500);
		 
		 java.util.List<AtomClusterRelationDTO> myAtom = new ArrayList<AtomClusterRelationDTO>();
		 myAtom = utsContentService.getCodeCodeRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, myconPsf);
	     bw.println("*Code Code Relations|Ui|Default Preferred Name|Relation Label|Additional Relation Label");
	     //bw.newLine();
	        if (myAtom.size() == 0){
	        	
	        	bw.println("None");	
	        	//bw.newLine();
	        } 
	        
	        else {
	        	
	
		      for (int i = 0; i < myAtom.size(); i++) {
		
		      AtomClusterRelationDTO myAtomDTO = myAtom.get(i);
		
		      String AtomClusterUi = myAtomDTO.getRelatedAtomCluster().getUi();
		      String AtomClusterName = myAtomDTO.getRelatedAtomCluster().getDefaultPreferredName();
		      String AtomClusterRel = myAtomDTO.getRelationLabel();
		      String AtomClusterRela = myAtomDTO.getAdditionalRelationLabel();

			      
		     bw.println(AtomClusterUi+"|"+AtomClusterName+"|"+AtomClusterRel+"|"+AtomClusterRela);
		     //bw.newLine();
		      }
	        }
	      
	    bw.println("!");
	    //bw.newLine();
	    
		    
		    
		    gov.nih.nlm.uts.webservice.content.Psf myPsf1 = new gov.nih.nlm.uts.webservice.content.Psf();
		    myPsf1.setPageLn(500);
		    
		     List<AttributeDTO> myAttributes = new ArrayList<AttributeDTO>();
		     myAttributes = utsContentService.getAtomAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val4, myPsf1);
		     bw.println("*Atom Attributes|Attribute Name|Attribute Value");
		     //bw.newLine();
		     
		     if (myAttributes.size() == 0){
		        	
		        	bw.println("None");	
		        	//bw.newLine();
		        } 
		        
		     else {
		     
		      for (int i = 0; i < myAttributes.size(); i++) {

		      AttributeDTO myAttributeDTO = myAttributes.get(i);
		      String attributeName = myAttributeDTO.getName();
		      String attributeValue = myAttributeDTO.getValue();

		      bw.println(attributeName+"|"+attributeValue);
		      //bw.newLine();

		      } 
		     }
			 bw.println("!");
			 //bw.newLine();
   
		    
		    
	      
		gov.nih.nlm.uts.webservice.content.Psf myTreePsf = new gov.nih.nlm.uts.webservice.content.Psf();
		myTreePsf.setPageLn(500);

	    List<AtomTreePositionDTO> myAtomTrPosClient = new ArrayList<AtomTreePositionDTO>();
	    myAtomTrPosClient = utsContentService.getAtomTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val4, myTreePsf);
	      
	    for (int i = 0; i < myAtomTrPosClient.size(); i++) {

	      AtomTreePositionDTO myAtmTreePosDTO = myAtomTrPosClient.get(i);
	      String ui = myAtmTreePosDTO.getUi();
	      java.util.List<AtomTreePositionPathDTO> myarrAtomTreePosPathDTOClient = new ArrayList<AtomTreePositionPathDTO>();
	      myarrAtomTreePosPathDTOClient = utsContentService.getAtomTreePositionPathsToRoot(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
	      //bw.println("*Tree Position "+(i+1)+":");
	      
		     if (myarrAtomTreePosPathDTOClient.size() == 0){
		        	
		        	bw.println("None");	
		        	//bw.newLine();
		        } 
		        
		     else {

		     for (int j = 0; j < myarrAtomTreePosPathDTOClient.size(); j++) {
		
		      AtomTreePositionPathDTO myAtmTrPosDTO = myarrAtomTreePosPathDTOClient.get(j);
		
		      List<AtomTreePositionDTO> treepos = myAtmTrPosDTO.getTreePositions();
		      bw.println("*Tree Position Paths To Root"+(j+1)+"|Atom ID|Default Preferred Name");
		      //bw.newLine();
		
			     for (int k = 0; k < treepos.size(); k++) {
			       AtomTreePositionDTO getj = treepos.get(k);
			       String defPrefName = getj.getDefaultPreferredName();
			       String atomUi = getj.getAtom().getUi();
			        
			       bw.println(atomUi+"|"+defPrefName);
			      // bw.newLine();
			       }
		     		}
				
		        }
				 bw.println("!");
				 //bw.newLine();
       
		        
		        
				 
		    List<AtomTreePositionDTO> myarrAtomTreePosChildrenDTOClient = new ArrayList<AtomTreePositionDTO>();
		    myarrAtomTreePosChildrenDTOClient = utsContentService.getAtomTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
		    bw.println("*Tree Position Children|Atom ID|Default Preferred Name");
		    //bw.newLine();
		    
		     if (myarrAtomTreePosChildrenDTOClient.size() == 0){
		        	
		        	bw.println("None");	
		        	//bw.newLine();
		        } 
		        
		     else {

			     for (int j = 0; j < myarrAtomTreePosChildrenDTOClient.size(); j++) {
			
			        AtomTreePositionDTO myAtmTrPosDTO = myarrAtomTreePosChildrenDTOClient.get(j);
			
			        String defaultPrefName = myAtmTrPosDTO.getDefaultPreferredName();
			        String atomUi = myAtmTrPosDTO.getAtom().getUi();
				        
				    bw.println(atomUi+"|"+defaultPrefName);
				    //bw.newLine();
				        }
		     		}
				 bw.println("!");
				 //bw.newLine();
				 
			     
			     
			List<AtomTreePositionDTO> myarrAtomTreePosSiblingDTOClient = new ArrayList<AtomTreePositionDTO>();
			myarrAtomTreePosSiblingDTOClient = utsContentService.getAtomTreePositionSiblings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
			bw.println("*Tree Position Sibling|Atom ID|Default Preferred Name");
			//bw.newLine();

		     if (myarrAtomTreePosSiblingDTOClient.size() == 0){
		        	
		        	bw.println("None");	
		        	//bw.newLine();
		        } 
		        
		     else {
				for (int j = 0; j < myarrAtomTreePosSiblingDTOClient.size(); j++) {
					
					   AtomTreePositionDTO myAtmTrPosDTO = myarrAtomTreePosSiblingDTOClient.get(j);
					
					 String defaultPrefName = myAtmTrPosDTO.getDefaultPreferredName();
					 String atomUi = myAtmTrPosDTO.getAtom().getUi();
						        
					 bw.println(atomUi+"|"+defaultPrefName);
					 //bw.newLine();
					    }
		     		}       
				 bw.println("!");
				 //bw.newLine();

	        }
	    
		bw.println("!");
	    bw.close();
		
	}
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			
			UTS_Source_documentation srcDocClient = new UTS_Source_documentation(args[0],args[1],args[2]);
			 
			String dataFileName = "S:/SHARE/MMS/UMLS/UMLS_Source_Documentation_VM_Files/"+umlsRelease+"/source_samples.txt";
 
			BufferedReader bReader = new BufferedReader(new FileReader(dataFileName));
			
			String line;
			String value1 = null;
			String value2 = null;
			String value3 = null;
			String value4 = null;

	        while ((line = bReader.readLine()) != null) {
	 
	        	int lineIndex = 0; 
	        	String datavalue[] = line.split("\\|");
	        	
	        	if (datavalue.length > 3){
	            value1 = datavalue[0];
	            value2 = datavalue[1];
	            value3 = datavalue[2];
	            value4 = datavalue[3];
	            
	            } else{
	            	value1 = datavalue[0];
		            value2 = datavalue[1];
		            value3 = datavalue[2];
	            	
	            }

	            	
	            	String dir = "S:/SHARE/MMS/UMLS/UMLS_Source_Documentation_VM_Files/"+umlsRelease+"/"+value1;
	            			File sourceDir = new File(dir);
	            			
	            			//Directory existence check
	            			if(!sourceDir.exists())
	            				sourceDir.mkdirs();
	            				            			
	            			//Results file existence check
	            			File sampleFile = new File(sourceDir+"/samples.txt");
	            			if(!sampleFile.exists()){
	            				sampleFile.createNewFile();
	            				  }

	            			path = sourceDir+"/samples.txt";
	            			
	            			//findConcepts(value1, value2, value3, path);
	            			
	            			if (value3.equals("sourceConcept")){
	            				findSrcConcepts(value1, value2, path);
	            				
	            			} 
	            				
	            			else if (value3.equals("sourceDescriptor")){
	            				findDescriptors(value1, value2, path);
	            			}
	            			
	            			else if (value3.equals("code")){
	            				findCodes(value1, value2, value4, path);
	            			}
	            			lineIndex++;
	            			
	            
	        }
	        bReader.close();
			
	        
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}

