package gov.nih.nlm.uts.clients;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

public class UTS_Source_documentation {
	
	private static String username = "debjaniani";
    private static String password = "Cartoon123!"; 
    static String umlsRelease = "2013AA";
	static String serviceName = "http://umlsks.nlm.nih.gov";

static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsFinderController  utsFinderService = (new UtsWsFinderControllerImplService()).getUtsWsFinderControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();



    
    public UTS_Source_documentation (String username, String password) {
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
	
	
	
	public static void findConcepts(String val1, String val2, String path) throws Exception{
		PrintWriter out = new PrintWriter(new File(path), "UTF-8"); 
		out.println("Results for "+val2+":");
		gov.nih.nlm.uts.webservice.finder.Psf myPsf = new gov.nih.nlm.uts.webservice.finder.Psf();
		myPsf.getIncludedSources().add(val1);

		java.util.List<UiLabel> myFindConcepts = new ArrayList<UiLabel>();

		myFindConcepts = utsFinderService.findConcepts(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "sourceConcept", val2, "exact", myPsf);
		out.println("*Concepts|Label|Ui");
		
        if (myFindConcepts.size() == 0){
        	
        	out.println("None");	
        } 
        
        else {
		
        for (int i = 0; i < myFindConcepts.size(); i++) {

        	UiLabel myFinCon = myFindConcepts.get(i);
        	String ui = myFinCon.getUi();
        	String label = myFinCon.getLabel();
        	
    	    out.println(label+"|"+ui);
        	//System.out.println(label+"|"+ui+"\n");
        }
        }
		 out.println("!");
			
	}
	
	
	
	//Method for Concept calls
	public static void findSrcConcepts(String val1, String val2, String path) throws Exception{
		
		PrintWriter out = new PrintWriter(new FileOutputStream(new File(path), "UTF-8")); 
		

	        AtomDTO myAtom = new AtomDTO();
		    myAtom = utsContentService.getDefaultPreferredAtom(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1);
	        out.println("*Default Preferred Atom|Atom Name|Term Type");

		    String defPrefUi = myAtom.getUi();
		    String atomName = myAtom.getTermString().getName();
		    String termType = myAtom.getTermType();

		    out.println(defPrefUi+"|"+atomName+"|"+termType);
		    out.println("!");
        
        
		    
        gov.nih.nlm.uts.webservice.content.Psf myconPsf = new gov.nih.nlm.uts.webservice.content.Psf();
        myconPsf.setIncludeSuppressible(false);
		myconPsf.getIncludedSources().add(val1);
		
        java.util.List<AtomDTO> myAtoms = new ArrayList<AtomDTO>();
        myAtoms = utsContentService.getSourceConceptAtoms(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, myconPsf);

        out.println("*Source Concept Atoms|Atom ID|SUI|Term|Term Type|Source Atom ID");
        
        if (myAtoms.size() == 0){
        	
        	out.println("None");	
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
        String srcDescriptor = null;

		if (myAtomDTO.getSourceConcept() != (null) ){

	     srcConcept = myAtomDTO.getSourceConcept().getUi();
	     }
	     
	    
	    if (myAtomDTO.getSourceDescriptor() != null){

	     srcDescriptor = myAtomDTO.getSourceDescriptor().getUi();
	    }
	    
	    //out.println(aui+"|"+sui+"|"+name+"|"+TermType+"|"+atomRelCount+"|"+attributeCount+"|"+srcConcept+"|"+srcDescriptor+"\n");
	    out.println(ui+"|"+sui+"|"+sourceUi+"|"+name+"|"+TermType+"|"+srcConcept+"|"+srcDescriptor);

	    }
                }

		 out.println("!");
		 
		 
  
			gov.nih.nlm.uts.webservice.content.Psf myAtomTreePsf = new gov.nih.nlm.uts.webservice.content.Psf();

		    List<AtomTreePositionDTO> myarrAtomTrPos = new ArrayList<AtomTreePositionDTO>();
		    myarrAtomTrPos = utsContentService.getAtomTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, defPrefUi, myAtomTreePsf);
		      
		    for (int i = 0; i < myarrAtomTrPos.size(); i++) {

		      AtomTreePositionDTO myAtmTreePosDTO = myarrAtomTrPos.get(i);
		      String ui = myAtmTreePosDTO.getUi();
		      
		      java.util.List<AtomTreePositionPathDTO> myAtomTreePosPathDTOClient = new ArrayList<AtomTreePositionPathDTO>();
		      myAtomTreePosPathDTOClient = utsContentService.getAtomTreePositionPathsToRoot(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myAtomTreePsf);
		      //out.println("*Tree Position "+(i+1)+":");
		      
			     if (myAtomTreePosPathDTOClient.size() == 0){
			        	
			        	out.println("None");	
			        } 
			        
			     else {

			     for (int j = 0; j < myAtomTreePosPathDTOClient.size(); j++) {
			
			      AtomTreePositionPathDTO myAtmTrPosDTO = myAtomTreePosPathDTOClient.get(j);
			
			      List<AtomTreePositionDTO> treepos = myAtmTrPosDTO.getTreePositions();
			      out.println("*Tree Position Paths To Root "+(j+1)+"|Default Preferred Name|Ui");
			
				     for (int k = 0; k < treepos.size(); k++) {
				       AtomTreePositionDTO getj = treepos.get(k);
				       String defPrefName = getj.getDefaultPreferredName();
				       String Ui = getj.getUi();
				        
				       out.println(defPrefName+"|"+Ui);
				       }
			     		}
					
			        }
					 out.println("!");
	        
			        
			        
			    List<AtomTreePositionDTO> myAtomTreePosChildrenDTO = new ArrayList<AtomTreePositionDTO>();
			    myAtomTreePosChildrenDTO = utsContentService.getAtomTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myAtomTreePsf);
			    out.println("*Tree Position Children|Default Preferred Name|Ui");
			    
			     if (myAtomTreePosChildrenDTO.size() == 0){
			        	
			        	out.println("None");	
			        } 
			        
			     else {

				     for (int j = 0; j < myAtomTreePosChildrenDTO.size(); j++) {
				
				        AtomTreePositionDTO myAtmTrPosChilDTO = myAtomTreePosChildrenDTO.get(j);
				
				        String defaultPrefName = myAtmTrPosChilDTO.getDefaultPreferredName();
				        String cUi = myAtmTrPosChilDTO.getUi();
					        
					    out.println(defaultPrefName+"|"+cUi);
					        }
			     		}
					 out.println("!");
				     
					 
				     
				     
				List<AtomTreePositionDTO> myarrTreePosSiblingDTOClient = new ArrayList<AtomTreePositionDTO>();
				myarrTreePosSiblingDTOClient = utsContentService.getAtomTreePositionSiblings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myAtomTreePsf);
				out.println("*Tree Position Sibling|Default Preferred Name|Ui");

			     if (myarrTreePosSiblingDTOClient.size() == 0){
			        	
			        	out.println("None");	
			        } 
			        
			     else {
					for (int j = 0; j < myarrTreePosSiblingDTOClient.size(); j++) {
						
						 AtomTreePositionDTO myAtmClustTrPosDTO = myarrTreePosSiblingDTOClient.get(j);
						
						 String defaultPrefName = myAtmClustTrPosDTO.getDefaultPreferredName();
						 String cUi = myAtmClustTrPosDTO.getUi();
							        
						 out.println(defaultPrefName+"|"+cUi);
						    }
			     		}       
					 out.println("!");
		    }
		
					 
        
        
        gov.nih.nlm.uts.webservice.content.Psf mySrcConPsf = new gov.nih.nlm.uts.webservice.content.Psf();
        mySrcConPsf.getIncludedRelationLabels().add("RO");
        mySrcConPsf.getIncludedRelationLabels().add("RB");
        mySrcConPsf.getIncludedRelationLabels().add("SY");
        mySrcConPsf.getIncludedSources().add(val1);

        List<AtomClusterRelationDTO> myAtomClusterRelations = new ArrayList<AtomClusterRelationDTO>();

        myAtomClusterRelations = utsContentService.getSourceConceptSourceConceptRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, mySrcConPsf);
        out.println("*Source Concept Source Concept Relations|Relation Label|Additional Relation Label|Related Atom Cluster ID|Related Atom Cluster Default Preferred Name|Suppressibility");
        
        if (myAtomClusterRelations.size() == 0){
        	
        	out.println("None");	
        } 
        
        else {

        for (int i = 0; i < myAtomClusterRelations.size(); i++) {

        AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
        String relationLabel = myAtomClusterRelationDTO.getRelationLabel();
        String addRelationLabel = myAtomClusterRelationDTO.getAdditionalRelationLabel();
        String relAtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
        String relAtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
        boolean supp = myAtomClusterRelationDTO.isSuppressible();
        
        out.println(relationLabel+"|"+addRelationLabel+"|"+relAtomClusterUi+"|"+relAtomClusterName+"|"+supp);
	    
        }
        }
        
		 out.println("!");
        
        
        
		gov.nih.nlm.uts.webservice.content.Psf myTreePsf = new gov.nih.nlm.uts.webservice.content.Psf();

        List<SourceAtomClusterTreePositionDTO> myarrAtomClustTrPosClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
        myarrAtomClustTrPosClient = utsContentService.getSourceConceptTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, myTreePsf);
        

        for (int i = 0; i < myarrAtomClustTrPosClient.size(); i++) {

        SourceAtomClusterTreePositionDTO myAtmClustTreePosDTO = myarrAtomClustTrPosClient.get(i);
        String ui = myAtmClustTreePosDTO.getUi();
        
    	java.util.List<SourceAtomClusterTreePositionPathDTO> myarrSrcDescTreePosPathDTOClient = new ArrayList<SourceAtomClusterTreePositionPathDTO>();
    	myarrSrcDescTreePosPathDTOClient = utsContentService.getSourceConceptTreePositionPathsToRoot(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
    	out.println("*Tree Position "+(i+1)+":");
    	
        if (myarrSrcDescTreePosPathDTOClient.size() == 0){
        	
        	out.println("None");	
        } 
        
        else {

	        for (int j = 0; j < myarrSrcDescTreePosPathDTOClient.size(); j++) {
	
	        SourceAtomClusterTreePositionPathDTO myAtmClustTrPosDTO = myarrSrcDescTreePosPathDTOClient.get(j);

	        List<SourceAtomClusterTreePositionDTO> treepos = myAtmClustTrPosDTO.getTreePositions();
		    out.println("*Tree Position Paths To Root"+(j+1)+"|Default Preferred Name|Cluster ID");

	
		        for (int k = 0; k < treepos.size(); k++) {
		        SourceAtomClusterTreePositionDTO getj = treepos.get(k);
		        String clusterUi = getj.getCluster().getUi();
		        String defPrefName = getj.getDefaultPreferredName();
		        
		        out.println(defPrefName+"|"+clusterUi);
		        }
	        }
				 out.println("!");
	
	        }
	        
	        
	        
	    List<SourceAtomClusterTreePositionDTO> myarrSrcDescTreePosChildrenDTOClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
	    myarrSrcDescTreePosChildrenDTOClient = utsContentService.getSourceConceptTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
	    out.println("*Tree Position Children|Default Preferred Name|Cluster ID");
	    
        if (myarrSrcDescTreePosChildrenDTOClient.size() == 0){
        	
        	out.println("None");	
        } 
        
        else {

		     for (int j = 0; j < myarrSrcDescTreePosChildrenDTOClient.size(); j++) {
		
		        SourceAtomClusterTreePositionDTO myAtmClustTrPosDTO = myarrSrcDescTreePosChildrenDTOClient.get(j);
		        String clusterUi = myAtmClustTrPosDTO.getCluster().getUi();
		        String defaultPrefName = myAtmClustTrPosDTO.getCluster().getDefaultPreferredName();
			        
			    out.println(defaultPrefName+"|"+clusterUi);
			        }
        		}
			        
			 out.println("!");
		     
			 
		     
		     
		List<SourceAtomClusterTreePositionDTO> myarrSrcDescTreePosSiblingDTOClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
		myarrSrcDescTreePosSiblingDTOClient = utsContentService.getSourceConceptTreePositionSiblings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
		out.println("*Tree Position Sibling|Default Preferred Name|Cluster ID");
		
        if (myarrSrcDescTreePosSiblingDTOClient.size() == 0){
        	
        	out.println("None");	
        } 
        
        else {

			for (int j = 0; j < myarrSrcDescTreePosSiblingDTOClient.size(); j++) {
				
				 SourceAtomClusterTreePositionDTO myAtmClustTrPosDTO = myarrSrcDescTreePosSiblingDTOClient.get(j);
				 String clusterUi = myAtmClustTrPosDTO.getCluster().getUi();
				 String defaultPrefName = myAtmClustTrPosDTO.getCluster().getDefaultPreferredName();
					        
				 out.println(defaultPrefName+"|"+clusterUi);
				    }
        	}
					        
			 out.println("!");
	     
        }    
		out.println("!");
	    out.close();

	}
	
	
	
	
	//Method for Descriptor calls
	public static void findDescriptors(String val1, String val2, String path) throws Exception{
		
		 PrintWriter out = new PrintWriter(new File(path), "UTF-8"); 		     

	     AtomDTO myAtoms = new AtomDTO();
		    myAtoms = utsContentService.getDefaultPreferredAtom(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1);
	        out.println("*Default Preferred Atom|Atom Name|Term Type");

		    String defPrefUi = myAtoms.getUi();
		    String atomName = myAtoms.getTermString().getName();
		    String termType = myAtoms.getTermType();

		    out.println(defPrefUi+"|"+atomName+"|"+termType);
		    out.println("!");
		    
		    
		 gov.nih.nlm.uts.webservice.content.Psf myconPsf = new gov.nih.nlm.uts.webservice.content.Psf();
		 myconPsf.setIncludeSuppressible(false);
		 myconPsf.getIncludedSources().add(val1);
		 java.util.List<AtomDTO> myAtom = new ArrayList<AtomDTO>();
		 myAtom = utsContentService.getSourceDescriptorAtoms(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, myconPsf);
	     out.println("*Source Descriptor Atoms|Atom ID|Source Atom ID|Term|Term Type|Source Concept|Source Descriptor");
	        if (myAtom.size() == 0){
	        	
	        	out.println("None");	
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
			      
		     out.println(ui+"|"+sui+"|"+name+"|"+TermType+"|"+srcConcept+"|"+srcDescriptor);
		      }
	        }
	      
	    out.println("!");
	    
	    


	      
		gov.nih.nlm.uts.webservice.content.Psf myAtomTreePsf = new gov.nih.nlm.uts.webservice.content.Psf();

	    List<AtomTreePositionDTO> myarrAtomTrPos = new ArrayList<AtomTreePositionDTO>();
	    myarrAtomTrPos = utsContentService.getAtomTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, defPrefUi, myAtomTreePsf);
	      
	    for (int i = 0; i < myarrAtomTrPos.size(); i++) {

	      AtomTreePositionDTO myAtmTreePosDTO = myarrAtomTrPos.get(i);
	      String ui = myAtmTreePosDTO.getUi();
	      //out.println("*Atom Tree Position: "+ui);
	      java.util.List<AtomTreePositionPathDTO> myAtomTreePosPathDTOClient = new ArrayList<AtomTreePositionPathDTO>();
	      myAtomTreePosPathDTOClient = utsContentService.getAtomTreePositionPathsToRoot(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myAtomTreePsf);
	      
	      //out.println("*Tree Position "+(i+1)+":");
	      
		     if (myAtomTreePosPathDTOClient.size() == 0){
		        	
		        	out.println("None");	
		        } 
		        
		     else {

		     for (int j = 0; j < myAtomTreePosPathDTOClient.size(); j++) {
		
		      AtomTreePositionPathDTO myAtmTrPosDTO = myAtomTreePosPathDTOClient.get(j);
		
		      List<AtomTreePositionDTO> treepos = myAtmTrPosDTO.getTreePositions();
		      out.println("*Tree Position Paths To Root"+(j+1)+"|Default Preferred Name|Ui");
		
			     for (int k = 0; k < treepos.size(); k++) {
			       AtomTreePositionDTO getj = treepos.get(k);
			       String defPrefName = getj.getDefaultPreferredName();
			       String Ui = getj.getUi();
			        
			       out.println(defPrefName+"|"+Ui);
			       }
		     		}
				
		        }
				 out.println("!");
        
		        
		        
		    List<AtomTreePositionDTO> myAtomTreePosChildrenDTO = new ArrayList<AtomTreePositionDTO>();
		    myAtomTreePosChildrenDTO = utsContentService.getAtomTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myAtomTreePsf);
		    out.println("*Tree Position Children|Default Preferred Name|Ui");
		    
		     if (myAtomTreePosChildrenDTO.size() == 0){
		        	
		        	out.println("None");	
		        } 
		        
		     else {

			     for (int j = 0; j < myAtomTreePosChildrenDTO.size(); j++) {
			
			        AtomTreePositionDTO myAtmTrPosChilDTO = myAtomTreePosChildrenDTO.get(j);
			
			        String defaultPrefName = myAtmTrPosChilDTO.getDefaultPreferredName();
			        String Ui = myAtmTrPosChilDTO.getUi();
				        
				    out.println(defaultPrefName+"|"+Ui);
				        }
		     		}
				 out.println("!");
			     
				 
			     
			     
			List<AtomTreePositionDTO> myarrTreePosSiblingDTOClient = new ArrayList<AtomTreePositionDTO>();
			myarrTreePosSiblingDTOClient = utsContentService.getAtomTreePositionSiblings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myAtomTreePsf);
			out.println("*Tree Position Sibling|Default Preferred Name|Ui");

		     if (myarrTreePosSiblingDTOClient.size() == 0){
		        	
		        	out.println("None");	
		        } 
		        
		     else {
				for (int j = 0; j < myarrTreePosSiblingDTOClient.size(); j++) {
					
					 AtomTreePositionDTO myAtmClustTrPosDTO = myarrTreePosSiblingDTOClient.get(j);
					
					 String defaultPrefName = myAtmClustTrPosDTO.getDefaultPreferredName();
					 String Ui = myAtmClustTrPosDTO.getUi();
						        
					 out.println(defaultPrefName+"|"+Ui);
					    }
		     		}       
				 out.println("!");
	    	}
	    
	    
	    
	    
	     gov.nih.nlm.uts.webservice.content.Psf mySrcConPsf = new gov.nih.nlm.uts.webservice.content.Psf();

	     mySrcConPsf.getIncludedRelationLabels().add("RB");
	     mySrcConPsf.getIncludedRelationLabels().add("RO");
         mySrcConPsf.getIncludedSources().add(val1);
	     mySrcConPsf.setPageLn(100);


	     List<AtomClusterRelationDTO> myAtomClusterRelations = new ArrayList<AtomClusterRelationDTO>();

	     myAtomClusterRelations = utsContentService.getSourceDescriptorSourceDescriptorRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, mySrcConPsf);
	     out.println("*Source Descriptor Source Descriptor Relations|Relation Label|Additional Relation Label|Related Atom Cluster ID|Related Atom Cluster Default Preferred Name|Suppressibility");

	     
	     if (myAtomClusterRelations.size() == 0){
	        	
	        	out.println("None");	
	        } 
	        
	     else {
	        	
	        
	     for (int i = 0; i < myAtomClusterRelations.size(); i++) {

	        AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
	        String relationLabel = myAtomClusterRelationDTO.getRelationLabel();
	        String addRelationLabel = myAtomClusterRelationDTO.getAdditionalRelationLabel();
	        String relAtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
            String relAtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
            boolean supp = myAtomClusterRelationDTO.isSuppressible();
            
            out.println(relationLabel+"|"+addRelationLabel+"|"+relAtomClusterUi+"|"+relAtomClusterName+"|"+supp);
            
            }
	       }
	        
		 out.println("!");
	        
	        
	        
		 gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
	     List<AttributeDTO> myAttributes = new ArrayList<AttributeDTO>();
	     myAttributes = utsContentService.getSourceDescriptorAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1,myPsf);
	     out.println("*Source Descriptor Attributes|Attribute Name|Attribute Value");
	     
	     if (myAttributes.size() == 0){
	        	
	        	out.println("None");	
	        } 
	        
	     else {
	     
	      for (int i = 0; i < myAttributes.size(); i++) {

	      AttributeDTO myAttributeDTO = myAttributes.get(i);
	      String attributeName = myAttributeDTO.getName();
	      String attributeValue = myAttributeDTO.getValue();

	      out.println(attributeName+"|"+attributeValue);

	      } 
	     }
		 out.println("!");

	      
	      
		gov.nih.nlm.uts.webservice.content.Psf myTreePsf = new gov.nih.nlm.uts.webservice.content.Psf();

	    List<SourceAtomClusterTreePositionDTO> myarrAtomClustTrPosClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
	    myarrAtomClustTrPosClient = utsContentService.getSourceDescriptorTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, myTreePsf);
	      
	    for (int i = 0; i < myarrAtomClustTrPosClient.size(); i++) {

	      SourceAtomClusterTreePositionDTO myAtmClustTreePosDTO = myarrAtomClustTrPosClient.get(i);
	      String ui = myAtmClustTreePosDTO.getUi();
	      java.util.List<SourceAtomClusterTreePositionPathDTO> myarrSrcDescTreePosPathDTOClient = new ArrayList<SourceAtomClusterTreePositionPathDTO>();
	      myarrSrcDescTreePosPathDTOClient = utsContentService.getSourceDescriptorTreePositionPathsToRoot(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
	      //out.println("*Tree Position "+(i+1)+":");
	      
		     if (myarrSrcDescTreePosPathDTOClient.size() == 0){
		        	
		        	out.println("None");	
		        } 
		        
		     else {

		     for (int j = 0; j < myarrSrcDescTreePosPathDTOClient.size(); j++) {
		
		      SourceAtomClusterTreePositionPathDTO myAtmClustTrPosDTO = myarrSrcDescTreePosPathDTOClient.get(j);
		
		      List<SourceAtomClusterTreePositionDTO> treepos = myAtmClustTrPosDTO.getTreePositions();
		      out.println("*Tree Position Paths To Root"+(j+1)+"|Default Preferred Name|Cluster ID");
		
			     for (int k = 0; k < treepos.size(); k++) {
			       SourceAtomClusterTreePositionDTO getj = treepos.get(k);
			       String defPrefName = getj.getDefaultPreferredName();
			       String clusterUi = getj.getCluster().getUi();
			        
			       out.println(defPrefName+"|"+clusterUi);
			       }
		     		}
				
		        }
				 out.println("!");
        
		        
		        
		    List<SourceAtomClusterTreePositionDTO> myarrSrcDescTreePosChildrenDTOClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
		    myarrSrcDescTreePosChildrenDTOClient = utsContentService.getSourceDescriptorTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
		    out.println("*Tree Position Children|Default Preferred Name|Cluster ID");
		    
		     if (myarrSrcDescTreePosChildrenDTOClient.size() == 0){
		        	
		        	out.println("None");	
		        } 
		        
		     else {

			     for (int j = 0; j < myarrSrcDescTreePosChildrenDTOClient.size(); j++) {
			
			        SourceAtomClusterTreePositionDTO myAtmClustTrPosDTO = myarrSrcDescTreePosChildrenDTOClient.get(j);
			
			        String defaultPrefName = myAtmClustTrPosDTO.getCluster().getDefaultPreferredName();
			        String clusterUi = myAtmClustTrPosDTO.getCluster().getUi();
				        
				    out.println(defaultPrefName+"|"+clusterUi);
				        }
		     		}
				 out.println("!");
			     
				 
			     
			     
			List<SourceAtomClusterTreePositionDTO> myarrSrcDescTreePosSiblingDTOClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
			myarrSrcDescTreePosSiblingDTOClient = utsContentService.getSourceDescriptorTreePositionSiblings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
			out.println("*Tree Position Sibling|Default Preferred Name|Cluster ID");

		     if (myarrSrcDescTreePosSiblingDTOClient.size() == 0){
		        	
		        	out.println("None");	
		        } 
		        
		     else {
				for (int j = 0; j < myarrSrcDescTreePosSiblingDTOClient.size(); j++) {
					
					   SourceAtomClusterTreePositionDTO myAtmClustTrPosDTO = myarrSrcDescTreePosSiblingDTOClient.get(j);
					
					 String defaultPrefName = myAtmClustTrPosDTO.getCluster().getDefaultPreferredName();
					 String clusterUi = myAtmClustTrPosDTO.getCluster().getUi();
						        
					 out.println(defaultPrefName+"|"+clusterUi);
					    }
		     		}       
				 out.println("!");


	        }
		out.println("!");
	     out.close();


		}
	
	
	
	
	public static void findCodes(String val1, String val2, String val4, String path) throws Exception{
		
		 PrintWriter out = new PrintWriter(new File(path), "UTF-8"); 
		 

		 AtomDTO myAtoms = new AtomDTO();
		    myAtoms = utsContentService.getDefaultPreferredAtom(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1);
	        out.println("*Default Preferred Atom|Atom Name|Term Type");

		    String defPrefUi = myAtoms.getUi();
		    String atomName = myAtoms.getTermString().getName();
		    String termType = myAtoms.getTermType();

		    out.println(defPrefUi+"|"+atomName+"|"+termType);
		    out.println("!");
		    
		 
		 gov.nih.nlm.uts.webservice.content.Psf myconPsf = new gov.nih.nlm.uts.webservice.content.Psf();
		 myconPsf.setIncludeSuppressible(false);
		 myconPsf.getIncludedSources().add(val1);
		 java.util.List<AtomClusterRelationDTO> myAtom = new ArrayList<AtomClusterRelationDTO>();
		 myAtom = utsContentService.getCodeCodeRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, myconPsf);
	     out.println("*Code Code Relations|Ui|Default Preferred Name|Relation Label|Additional Relation Label");
	        if (myAtom.size() == 0){
	        	
	        	out.println("None");	
	        } 
	        
	        else {
	        	
	
		      for (int i = 0; i < myAtom.size(); i++) {
		
		      AtomClusterRelationDTO myAtomDTO = myAtom.get(i);
		
		      String AtomClusterUi = myAtomDTO.getRelatedAtomCluster().getUi();
		      String AtomClusterName = myAtomDTO.getRelatedAtomCluster().getDefaultPreferredName();
		      String AtomClusterRel = myAtomDTO.getRelationLabel();
		      String AtomClusterRela = myAtomDTO.getAdditionalRelationLabel();

			      
		     out.println(AtomClusterUi+"|"+AtomClusterName+"|"+AtomClusterRel+"|"+AtomClusterRela);
		      }
	        }
	      
	    out.println("!");
	    
		    
		    
		    gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
		     List<AttributeDTO> myAttributes = new ArrayList<AttributeDTO>();
		     myAttributes = utsContentService.getAtomAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val4, myPsf);
		     out.println("*Atom Attributes|Attribute Name|Attribute Value");
		     
		     if (myAttributes.size() == 0){
		        	
		        	out.println("None");	
		        } 
		        
		     else {
		     
		      for (int i = 0; i < myAttributes.size(); i++) {

		      AttributeDTO myAttributeDTO = myAttributes.get(i);
		      String attributeName = myAttributeDTO.getName();
		      String attributeValue = myAttributeDTO.getValue();

		      out.println(attributeName+"|"+attributeValue);

		      } 
		     }
			 out.println("!");
   
		    
		    
	      
		gov.nih.nlm.uts.webservice.content.Psf myTreePsf = new gov.nih.nlm.uts.webservice.content.Psf();

	    List<AtomTreePositionDTO> myAtomTrPosClient = new ArrayList<AtomTreePositionDTO>();
	    myAtomTrPosClient = utsContentService.getAtomTreePositions(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val4, myTreePsf);
	      
	    for (int i = 0; i < myAtomTrPosClient.size(); i++) {

	      AtomTreePositionDTO myAtmTreePosDTO = myAtomTrPosClient.get(i);
	      String ui = myAtmTreePosDTO.getUi();
	      java.util.List<AtomTreePositionPathDTO> myarrAtomTreePosPathDTOClient = new ArrayList<AtomTreePositionPathDTO>();
	      myarrAtomTreePosPathDTOClient = utsContentService.getAtomTreePositionPathsToRoot(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
	      //out.println("*Tree Position "+(i+1)+":");
	      
		     if (myarrAtomTreePosPathDTOClient.size() == 0){
		        	
		        	out.println("None");	
		        } 
		        
		     else {

		     for (int j = 0; j < myarrAtomTreePosPathDTOClient.size(); j++) {
		
		      AtomTreePositionPathDTO myAtmTrPosDTO = myarrAtomTreePosPathDTOClient.get(j);
		
		      List<AtomTreePositionDTO> treepos = myAtmTrPosDTO.getTreePositions();
		      out.println("*Tree Position Paths To Root"+(j+1)+"|Default Preferred Name|Atom ID");
		
			     for (int k = 0; k < treepos.size(); k++) {
			       AtomTreePositionDTO getj = treepos.get(k);
			       String defPrefName = getj.getDefaultPreferredName();
			       String atomUi = getj.getAtom().getUi();
			        
			       out.println(defPrefName+"|"+atomUi);
			       }
		     		}
				
		        }
				 out.println("!");
       
		        
		        
				 
		    List<AtomTreePositionDTO> myarrAtomTreePosChildrenDTOClient = new ArrayList<AtomTreePositionDTO>();
		    myarrAtomTreePosChildrenDTOClient = utsContentService.getAtomTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
		    out.println("*Tree Position Children|Default Preferred Name|Atom ID");
		    
		     if (myarrAtomTreePosChildrenDTOClient.size() == 0){
		        	
		        	out.println("None");	
		        } 
		        
		     else {

			     for (int j = 0; j < myarrAtomTreePosChildrenDTOClient.size(); j++) {
			
			        AtomTreePositionDTO myAtmTrPosDTO = myarrAtomTreePosChildrenDTOClient.get(j);
			
			        String defaultPrefName = myAtmTrPosDTO.getDefaultPreferredName();
			        String atomUi = myAtmTrPosDTO.getAtom().getUi();
				        
				    out.println(defaultPrefName+"|"+atomUi);
				        }
		     		}
				 out.println("!");
			     
				 
			     
			     
			List<AtomTreePositionDTO> myarrAtomTreePosSiblingDTOClient = new ArrayList<AtomTreePositionDTO>();
			myarrAtomTreePosSiblingDTOClient = utsContentService.getAtomTreePositionSiblings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
			out.println("*Tree Position Sibling|Default Preferred Name|Atom ID");

		     if (myarrAtomTreePosSiblingDTOClient.size() == 0){
		        	
		        	out.println("None");	
		        } 
		        
		     else {
				for (int j = 0; j < myarrAtomTreePosSiblingDTOClient.size(); j++) {
					
					   AtomTreePositionDTO myAtmTrPosDTO = myarrAtomTreePosSiblingDTOClient.get(j);
					
					 String defaultPrefName = myAtmTrPosDTO.getDefaultPreferredName();
					 String atomUi = myAtmTrPosDTO.getAtom().getUi();
						        
					 out.println(defaultPrefName+"|"+atomUi);
					    }
		     		}       
				 out.println("!");

	        }
	    
		out.println("!");
	      out.close();
		
	}
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			//Reading from a sample text file
			//BufferedReader br = null;
			//String sCurrentLine;
			 
			String dataFileName = "S:/SHARE/MMS/UMLS/UMLS_Source_Documentation_VM_Files/2013AA/source_samples.txt";
 
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

	            	
	            	String dir = "S:/SHARE/MMS/UMLS/UMLS_Source_Documentation_VM_Files/2013AA/"+value1;
	            			File sourceDir = new File(dir);
	            			//Directory existence check
	            			if(!sourceDir.exists())
	            				sourceDir.mkdirs();
	            				            			
	            			//Results file existence check
	            			File sampleFile = new File(sourceDir+"/samples.txt");
	            			if(!sampleFile.exists()){
	            				sampleFile.createNewFile();
	            				  }

	            			String path = sourceDir+"/samples.txt";
	            			
	            			findConcepts(value1, value2, path);
	            			
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
	            			

	            
	           // System.out.println(value1+","+value2+","+value3);
	        }
	        bReader.close();
			
	        
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}

