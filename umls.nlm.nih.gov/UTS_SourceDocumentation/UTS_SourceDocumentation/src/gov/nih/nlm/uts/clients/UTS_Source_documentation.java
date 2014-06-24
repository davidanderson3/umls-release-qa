package gov.nih.nlm.uts.clients;

import gov.nih.nlm.uts.webservice.content.AtomClusterRelationDTO;
import gov.nih.nlm.uts.webservice.content.AtomDTO;
import gov.nih.nlm.uts.webservice.content.ConceptDTO;
import gov.nih.nlm.uts.webservice.content.AtomRelationDTO;
import gov.nih.nlm.uts.webservice.content.AtomTreePositionDTO;
import gov.nih.nlm.uts.webservice.content.AtomTreePositionPathDTO;
import gov.nih.nlm.uts.webservice.content.AttributeDTO;
import gov.nih.nlm.uts.webservice.content.SourceAtomClusterTreePositionDTO;
import gov.nih.nlm.uts.webservice.content.SourceAtomClusterTreePositionPathDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.Psf;
import gov.nih.nlm.uts.webservice.semnet.*;
import gov.nih.nlm.uts.webservice.metadata.*;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.finder.UiLabel;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderController;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;
import org.apache.commons.lang3.StringUtils;

public class UTS_Source_documentation {
	
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "";
	static String serviceName = "http://umlsks.nlm.nih.gov";
	static String path;
	

static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSemanticNetworkController utsSemanticTypeService = (new UtsWsSemanticNetworkControllerImplService().getUtsWsSemanticNetworkControllerImplPort());
static UtsWsFinderController  utsFinderService = (new UtsWsFinderControllerImplService()).getUtsWsFinderControllerImplPort();
static UtsWsMetadataController  utsMetaDataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();
static StringUtils StringTool = new StringUtils();
    
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
	
	
	public static RootSourceDTO getSourceInfo(String val2) throws Exception{
		
		RootSourceDTO myRootSource = utsMetaDataService.getRootSource(securityService.getProxyTicket(ticketGrantingTicket(), serviceName),umlsRelease,val2);
		return myRootSource;
		
	}
	
	public static ConceptDTO getConceptDTO (String ui) throws Exception{
		
		ConceptDTO myConcept = utsContentService.getConcept(securityService.getProxyTicket(ticketGrantingTicket(), serviceName),umlsRelease, ui);
		return myConcept;
		
			
	}
	
	public static List<AtomDTO> getMyConceptAtoms(String ui) throws Exception {
		
		Psf myPsf = new Psf();
		myPsf.setIncludedLanguage("ENG");
		myPsf.getIncludedTermTypes().add("PT");
		myPsf.getIncludedTermTypes().add("MH");
		myPsf.getIncludedTermTypes().add("PN");
		myPsf.getIncludedTermTypes().add("SCD");
		myPsf.getIncludedTermTypes().add("IN");
		myPsf.getIncludedTermTypes().add("LN");
		myPsf.getIncludedTermTypes().add("RXN_PT");
		myPsf.getIncludedTermTypes().add("PIN");
		
		List<AtomDTO> myAtoms = utsContentService.getConceptAtoms(securityService.getProxyTicket(ticketGrantingTicket(), serviceName),umlsRelease,ui, myPsf);

		return myAtoms;
		
	}
	
	public static SemanticTypeDTO getSemanticTypeDTO (String semanticTypeId) throws Exception {
		
		SemanticTypeDTO mySemanticType = utsSemanticTypeService.getSemanticType(securityService.getProxyTicket(ticketGrantingTicket(), serviceName),umlsRelease, semanticTypeId);
		return mySemanticType;
	}
	
	public static List<AttributeDTO> getAttributeDTOs (String id, String source, String mode) throws Exception {
		Psf myPsf =  new Psf();
		myPsf.getIncludedSources().add(source);
		myPsf.setPageLn(500);
		List<AttributeDTO> myAttributes = new ArrayList<AttributeDTO>();
		
		switch(mode) {
		
		case "sourceDescriptor": {myAttributes = utsContentService.getSourceDescriptorAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName),umlsRelease, id, source, myPsf); break;}
		case "sourceConcept": {myAttributes = utsContentService.getSourceConceptAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName),umlsRelease, id, source, myPsf); break;}
		case "code":{myAttributes = utsContentService.getCodeAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName),umlsRelease, id, source, myPsf); break;}
		case "atom": {myAttributes = utsContentService.getAtomAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName),umlsRelease, id, myPsf);}
		
	
		
		}
		return myAttributes;
		
	}
	
	public static List<AtomRelationDTO> getAtomAtomRelations (String id) throws Exception {
		Psf myPsf =  new Psf();
		List<AtomRelationDTO> atomRelations = utsContentService.getAtomAtomRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, id, myPsf);
	    return atomRelations;
	}
	
	//Method for Concept calls
	public static void findSrcConcepts(String val1, String val2, String path) throws Exception{

	       
		
        PrintWriter bw = new PrintWriter(new File(path), "UTF-8"); 
        bw.println(val2);
        gov.nih.nlm.uts.webservice.finder.Psf myPsf = new gov.nih.nlm.uts.webservice.finder.Psf();
        myPsf.getIncludedSources().add(val1);
        
        RootSourceDTO myRootSource = getSourceInfo(val1);
        String son = myRootSource.getPreferredName();
        
        
        myPsf.setPageLn(500);

        java.util.List<UiLabel> myFindConcepts = new ArrayList<UiLabel>();

        myFindConcepts = utsFinderService.findConcepts(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "sourceConcept", val2, "exact", myPsf);
        bw.println("*Sample Information|Source Concept "+val2+ " from " +son+" in the "+umlsRelease+ " version of UMLS");
        bw.println("!");
        
        
        bw.println("*UMLS Concept Information|CUI|Preferred Name|Number of Atoms|Semantic Type(s)|Date Added To Metathesaurus|Preferred English Language Synonyms");
        
		if (myFindConcepts.size() == 0){
		
		bw.println("None");      
		} 
		
		else {
		        
		for (int i = 0; i < myFindConcepts.size(); i++) {
		
		UiLabel myFinCon = myFindConcepts.get(i);
		String ui = myFinCon.getUi();
		ConceptDTO myConcept = getConceptDTO(ui);
		List<AtomDTO> mySynonyms = getMyConceptAtoms(ui);
		
		String dateAdded = StringUtils.left(myConcept.getDateAdded().toString(), 10);
		int noAtoms = myConcept.getAtomCount();
		java.util.List<String> semanticTypes = myConcept.getSemanticTypes();
		List<String> semanticTypeNames = new ArrayList<>();
		List<String> namesAndRsabs = new ArrayList<>();
		
		for (AtomDTO atom: mySynonyms) {
			String name = atom.getTermString().getName();
			String rsab = atom.getRootSource();
			String nameAndRsab = name+" ("+rsab+")";
			if (!rsab.equals(val1)) {namesAndRsabs.add(nameAndRsab);}
			
		}
		
		String synonyms = namesAndRsabs.isEmpty() ? "None" : StringUtils.join(namesAndRsabs,"<br/>");
		for (String styId: semanticTypes) {
			
			SemanticTypeDTO mySty = getSemanticTypeDTO(styId);
			String styName = mySty.getValue();
			semanticTypeNames.add(styName);
		}
		String label = myFinCon.getLabel();
		
		
		bw.println(ui+"|"+label+"|"+noAtoms+"|"+semanticTypeNames+"|"+dateAdded+"|"+synonyms);
		
		//System.out.println(label+"|"+ui+"\n");
		}
		}
	    bw.println("!");
	    
      
	       
	       

	        AtomDTO myAtom = new AtomDTO();
		    myAtom = utsContentService.getDefaultPreferredAtom(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1);
	        
		    bw.println("*Highest Ranking Atom of "+val2+"|AUI|Term Type|Atom Name|Lexically Normalized Name");
	        
		    String defPrefUi = myAtom.getUi();
		    String atomName = myAtom.getTermString().getName();
		    String normForm = myAtom.getTermString().getTerm().getLuinormForm();
		    String termType = myAtom.getTermType();
		    
		    bw.println(defPrefUi+"|"+termType+"|"+atomName+"|"+normForm);
		    //bw.newLine();
		    bw.println("!");
		    
		    
		    
		    List<AttributeDTO> myAtomAttributes = getAttributeDTOs(defPrefUi,val1,"atom");
		    
		    if (myAtomAttributes.size() > 0) {
			    bw.println("*Highest Ranking Atom of "+val2+" - Attributes |Attribute Name|Attribute Value");	
			    for(int i = 0;i < myAtomAttributes.size();i++) {
			    AttributeDTO myAttributeDTO = myAtomAttributes.get(i);
			    String atn = myAttributeDTO.getName();
			    String atv = myAttributeDTO.getValue();
			    bw.println(atn+"|"+atv);	
			    }
			    
			    bw.println("!");
			}
		    
		    //begin Atom-Atom Relations
		    
		    List<AtomRelationDTO> myAtomRelations = getAtomAtomRelations(defPrefUi);
		    
		    if (myAtomRelations.size() > 0) {
		    bw.println("*Highest Ranking Atom of "+val2+" - Atom Relations|Relation Label|Additional Relation Label|Related Atom ID|Related Atom Name|Suppressible");
		    
		    for (int i = 0;i < myAtomRelations.size();i++){
		    	
		    	AtomRelationDTO myAtomRelation = myAtomRelations.get(i);
		    	String rel = myAtomRelation.getRelationLabel();
		    	String rela = myAtomRelation.getAdditionalRelationLabel();
		    	String aui2id = myAtomRelation.getRelatedAtom().getUi();
		    	String aui2name = myAtomRelation.getRelatedAtom().getTermString().getName();
		    	String aui2tty = myAtomRelation.getRelatedAtom().getTermType();
		    	Boolean aui2suppress = myAtomRelation.isSuppressible();
		    	bw.println(rel+"|"+rela+"|"+aui2id+"|"+aui2name+"|"+aui2tty+"|"+aui2suppress);
		    }
		    bw.println("!");
		    }
		    //end Atom-Atom Relations
		    //bw.newLine();

        
        
		    
        gov.nih.nlm.uts.webservice.content.Psf myconPsf = new gov.nih.nlm.uts.webservice.content.Psf();
		myconPsf.getIncludedSources().add(val1);
		myconPsf.setPageLn(500);
		
        java.util.List<AtomDTO> myAtoms = new ArrayList<AtomDTO>();
        myAtoms = utsContentService.getSourceConceptAtoms(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, myconPsf);

        bw.println("*Source Concept Atoms|AUI|SUI|Term|Term Type|Source Atom ID|Source Concept|Obsolete|Suppressible");
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
			      bw.println("*Path to Root|Id|Name");
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
			    bw.println("*Children|Id|Name");
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
				bw.println("*Siblings|Id|Name");
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
        mySrcConPsf.getIncludedRelationLabels().add("RN");
        mySrcConPsf.getIncludedRelationLabels().add("RQ");
        mySrcConPsf.getIncludedRelationLabels().add("QB");
        mySrcConPsf.getIncludedSources().add(val1);
        mySrcConPsf.setPageLn(500);

        List<AtomClusterRelationDTO> myAtomClusterRelations = new ArrayList<AtomClusterRelationDTO>();

        myAtomClusterRelations = utsContentService.getSourceConceptSourceConceptRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, mySrcConPsf);
        bw.println("*Source Concept-Source Concept Relations|Relation Label|Additional Relation Label|Source Concept Id|Name");
        //bw.newLine();
        
        if (myAtomClusterRelations.size() == 0){
        	
        	bw.println("None");	
        	//bw.newLine();
        } 
        
        else {

        for (int i = 0; i < myAtomClusterRelations.size(); i++) {

        	  AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
	          String AtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
		      String AtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
		      String AtomClusterRel = myAtomClusterRelationDTO.getRelationLabel();
		      String AtomClusterRela = myAtomClusterRelationDTO.getAdditionalRelationLabel();
          
		      bw.println(AtomClusterRel+"|"+AtomClusterRela+"|"+AtomClusterUi+"|"+AtomClusterName);
        
          
        //bw.newLine();
	    
        }
        }
        
		 bw.println("!");
		 //bw.newLine();
        
        
		
	     List<AttributeDTO> myAttributes = getAttributeDTOs(val2,val1,"sourceConcept");
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
    	//bw.println("*Tree Position "+(i+1)+":");
    	//bw.newLine();
    	
        if (myarrSrcDescTreePosPathDTOClient.size() == 0){
        	
        	bw.println("None");	
        	//bw.newLine();
        } 
        
        else {

	        for (int j = 0; j < myarrSrcDescTreePosPathDTOClient.size(); j++) {
	
	        SourceAtomClusterTreePositionPathDTO myAtmClustTrPosDTO = myarrSrcDescTreePosPathDTOClient.get(j);

	        List<SourceAtomClusterTreePositionDTO> treepos = myAtmClustTrPosDTO.getTreePositions();
		    bw.println("*Path to Root|Id|Name");
		   
		    
                //reverse the treepos array list so that the rsab root node is on top
		        Collections.reverse(treepos);
		       
		        
		        for (int k = 0; k < treepos.size(); k++) {
		        SourceAtomClusterTreePositionDTO getj = treepos.get(k);
		        String clusterUi = getj.getCluster().getUi();
		        String defPrefName = getj.getDefaultPreferredName();
		        
		        bw.println(clusterUi+"|"+StringTool.repeat("&#160;",k)+defPrefName);
		        //bw.newLine();
		        }
		        bw.println("!");
		        
	        }
				 
				 //bw.newLine();
	
	        }
	        
	        
	        
	    List<SourceAtomClusterTreePositionDTO> myarrSrcDescTreePosChildrenDTOClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
	    myarrSrcDescTreePosChildrenDTOClient = utsContentService.getSourceConceptTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
	    bw.println("*Children|Id|Name");
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
		bw.println("*Siblings|Id|Name");
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
		//bw.println("!");
		//bw.newLine();
		bw.close(); 

	}
	
	
	
	
	
	//Method for Descriptor calls
	public static void findDescriptors(String val1, String val2, String path) throws Exception{
		

        PrintWriter bw = new PrintWriter(new File(path), "UTF-8"); 
        bw.println(val2);
        gov.nih.nlm.uts.webservice.finder.Psf myPsf = new gov.nih.nlm.uts.webservice.finder.Psf();
        myPsf.getIncludedSources().add(val1);
		myPsf.setPageLn(500);
		
		
		RootSourceDTO myRootSource = getSourceInfo(val1);
        String son = myRootSource.getPreferredName();


        java.util.List<UiLabel> myFindConcepts = new ArrayList<UiLabel>();

        myFindConcepts = utsFinderService.findConcepts(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "sourceDescriptor", val2, "exact", myPsf);
        bw.println("*Sample Information|Source Descriptor "+val2+ " from " +son+" in the "+umlsRelease+ " version of UMLS");
        bw.println("!");
        
bw.println("*UMLS Concept Information|CUI|Preferred Name|Number of Atoms|Semantic Type(s)|Date Added To Metathesaurus|Preferred English Language Synonyms");
        
		if (myFindConcepts.size() == 0){
		
		bw.println("None");      
		} 
		
		else {
		        
		for (int i = 0; i < myFindConcepts.size(); i++) {
		
		UiLabel myFinCon = myFindConcepts.get(i);
		String ui = myFinCon.getUi();
		ConceptDTO myConcept = getConceptDTO(ui);
		List<AtomDTO> mySynonyms = getMyConceptAtoms(ui);
		
		String dateAdded = StringUtils.left(myConcept.getDateAdded().toString(), 10);
		int noAtoms = myConcept.getAtomCount();
		java.util.List<String> semanticTypes = myConcept.getSemanticTypes();
		List<String> semanticTypeNames = new ArrayList<>();
		List<String> namesAndRsabs = new ArrayList<>();
		
		for (AtomDTO atom: mySynonyms) {
			String name = atom.getTermString().getName();
			String rsab = atom.getRootSource();
			String nameAndRsab = name+" ("+rsab+")";
			if (!rsab.equals(val1)) {namesAndRsabs.add(nameAndRsab);}
			
		}
		
		String synonyms = namesAndRsabs.isEmpty() ? "None" : StringUtils.join(namesAndRsabs,"<br/>");
		for (String styId: semanticTypes) {
			
			SemanticTypeDTO mySty = getSemanticTypeDTO(styId);
			String styName = mySty.getValue();
			semanticTypeNames.add(styName);
		}
		String label = myFinCon.getLabel();
		
		
		bw.println(ui+"|"+label+"|"+noAtoms+"|"+semanticTypeNames+"|"+dateAdded+"|"+synonyms);
		
		//System.out.println(label+"|"+ui+"\n");
		}
		}
	    bw.println("!");  
	       
	        AtomDTO myAtom = new AtomDTO();
		    myAtom = utsContentService.getDefaultPreferredAtom(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1);
	        bw.println("*Highest Ranking Atom of "+val2+"|AUI|Term Type|Atom Name|Lexically Normalized Name");
	        
		    String defPrefUi = myAtom.getUi();
		    String atomName = myAtom.getTermString().getName();
		    String normForm = myAtom.getTermString().getTerm().getLuinormForm();
		    String termType = myAtom.getTermType();
		    
		    bw.println(defPrefUi+"|"+termType+"|"+atomName+"|"+normForm);
		    bw.println("!");
		
		    
		    List<AttributeDTO> myAtomAttributes = getAttributeDTOs(defPrefUi,val1,"atom");
		    
		    if (myAtomAttributes.size() > 0) {
			    bw.println("*Highest Ranking Atom of "+val2+" - Atom Attributes|Attribute Name|Attribute Value");	
			    for(int i = 0;i < myAtomAttributes.size();i++) {
			    AttributeDTO myAttributeDTO = myAtomAttributes.get(i);
			    String atn = myAttributeDTO.getName();
			    String atv = myAttributeDTO.getValue();
			    bw.println(atn+"|"+atv);	
			    }
			    
			    bw.println("!");
		   }
		    
           //begin Atom-Atom Relations
		    
		    List<AtomRelationDTO> myAtomRelations = getAtomAtomRelations(defPrefUi);
		    
		    if (myAtomRelations.size() > 0) {
		    bw.println("*Highest Ranking Atom of "+val2+" - Atom Relations|Relation Label|Additional Relation Label|Related Atom ID|Related Atom Name|Term Type|Suppressible");
		    
		    for (int i = 0;i < myAtomRelations.size();i++){
		    	
		    	AtomRelationDTO myAtomRelation = myAtomRelations.get(i);
		    	String rel = myAtomRelation.getRelationLabel();
		    	String rela = myAtomRelation.getAdditionalRelationLabel();
		    	String aui2id = myAtomRelation.getRelatedAtom().getUi();
		    	String aui2name = myAtomRelation.getRelatedAtom().getTermString().getName();
		    	String aui2tty = myAtomRelation.getRelatedAtom().getTermType();
		    	Boolean aui2suppress = myAtomRelation.isSuppressible();
		    	bw.println(rel+"|"+rela+"|"+aui2id+"|"+aui2name+"|"+aui2tty+"|"+aui2suppress);
		    }
		    bw.println("!");
		    }
		    //end Atom-Atom Relations   
		    
		 gov.nih.nlm.uts.webservice.content.Psf myconPsf = new gov.nih.nlm.uts.webservice.content.Psf();
		 myconPsf.getIncludedSources().add(val1);
		 myconPsf.setPageLn(500);

		 java.util.List<AtomDTO> myAtoms = new ArrayList<AtomDTO>();
		 myAtoms = utsContentService.getSourceDescriptorAtoms(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, myconPsf);
	     bw.println("*Source Descriptor Atoms|AUI|Source Atom ID|Term|Term Type|Source Concept|Source Descriptor|Obsolete|Suppressible");
	     //bw.newLine();
	        if (myAtoms.size() == 0){
	        	
	        	bw.println("None");	
	        	//bw.newLine();
	        } 
	        
	        else {
	        	
	
		      for (int i = 0; i < myAtoms.size(); i++) {
		
		      AtomDTO myAtomDTO = myAtoms.get(i);
		
		      String ui = myAtomDTO.getUi();
		      String sui = myAtomDTO.getSourceUi();
		      String name = myAtomDTO.getTermString().getName();
		      String TermType = myAtomDTO.getTermType();
		      String srcConcept = null;
		      String srcDescriptor = null;
		      
			     if (myAtomDTO.getSourceConcept() != (null) ){

			     srcConcept = myAtomDTO.getSourceConcept().getUi();
			     }
			     
			     
			     if (myAtomDTO.getSourceDescriptor() != (null) ){

			     srcDescriptor = myAtomDTO.getSourceDescriptor().getUi();
			     }
			     
			   boolean suppressible = myAtomDTO.isSuppressible();
			   boolean obsolete = myAtomDTO.isObsolete();
			      
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
		      //System.out.println("Printing path" + i);
		      bw.println("*Path to Root|Id|Name");
		      
		      Collections.reverse(treepos);
		
			     for (int k = 0; k < treepos.size(); k++) {
			       AtomTreePositionDTO getj = treepos.get(k);
			       String defPrefName = getj.getDefaultPreferredName();
			       String Ui = getj.getUi();
			        
			       bw.println(Ui+"|"+StringTool.repeat("&#160;",k)+defPrefName);
			      // bw.newLine();
			       }
		     		}
				
		        }
				 bw.println("!");
				 //bw.newLine();
        
		        
		        
		    List<AtomTreePositionDTO> myAtomTreePosChildrenDTO = new ArrayList<AtomTreePositionDTO>();
		    myAtomTreePosChildrenDTO = utsContentService.getAtomTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myAtomTreePsf);
		    bw.println("*Children|Id|Name");
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
			bw.println("*Siblings|Id|Name");
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
	     mySrcConPsf.getIncludedRelationLabels().add("RN");
	     mySrcConPsf.getIncludedRelationLabels().add("SY");
	     mySrcConPsf.getIncludedRelationLabels().add("RQ");
	     mySrcConPsf.getIncludedRelationLabels().add("QB");
         mySrcConPsf.getIncludedSources().add(val1);
	     mySrcConPsf.setPageLn(500);


	     List<AtomClusterRelationDTO> myAtomClusterRelations = new ArrayList<AtomClusterRelationDTO>();

	     myAtomClusterRelations = utsContentService.getSourceDescriptorSourceDescriptorRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, mySrcConPsf);
	     bw.println("*Source Descriptor-Source Descriptor Relations|Relation Label|Additional Relation Label|Source Descriptor Id|Name");
	     //bw.newLine();

	     if (myAtomClusterRelations.size() == 0){
	        	
	        	bw.println("None");	
	        	//bw.newLine();
	        } 
	        
	     else {
	        	
	        
	     for (int i = 0; i < myAtomClusterRelations.size(); i++) {

	          AtomClusterRelationDTO myAtomClusterRelationDTO = myAtomClusterRelations.get(i);
	          String AtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
		      String AtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
		      String AtomClusterRel = myAtomClusterRelationDTO.getRelationLabel();
		      String AtomClusterRela = myAtomClusterRelationDTO.getAdditionalRelationLabel();
            
		      bw.println(AtomClusterRel+"|"+AtomClusterRela+"|"+AtomClusterUi+"|"+AtomClusterName);
            //bw.newLine();
            
            }
	       }
	        
		 bw.println("!");
		 //bw.newLine();
	        
	        
		 List<AttributeDTO> myAttributes = getAttributeDTOs(val2,val1,"sourceDescriptor");	    
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
		      bw.println("*Path to Root|Id|Name");
		      
		      Collections.reverse(treepos);
		         
			     for (int k = 0; k < treepos.size(); k++) {
			       SourceAtomClusterTreePositionDTO getj = treepos.get(k);
			       String defPrefName = getj.getDefaultPreferredName();
			       String clusterUi = getj.getCluster().getUi();
			        
			       bw.println(clusterUi+"|"+StringTool.repeat("&#160;",k)+defPrefName);
			       //bw.newLine();
			       }
			     bw.println("!");
		     	}
		         
		        }
				 //bw.println("!");
				 //bw.newLine();
        
		        
		        
		    List<SourceAtomClusterTreePositionDTO> myarrSrcDescTreePosChildrenDTOClient = new ArrayList<SourceAtomClusterTreePositionDTO>();
		    myarrSrcDescTreePosChildrenDTOClient = utsContentService.getSourceDescriptorTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
		    bw.println("*Children|Id|Name");
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
			bw.println("*Siblings|Id|Name");
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
		//bw.println("!");
	    bw.close();


		}
	
	
	
	
	
	public static void findCodes(String val1, String val2, String val4, String path) throws Exception{
		

        PrintWriter bw = new PrintWriter(new File(path), "UTF-8"); 
        bw.println(val2);
        gov.nih.nlm.uts.webservice.finder.Psf myPsf = new gov.nih.nlm.uts.webservice.finder.Psf();
        myPsf.getIncludedSources().add(val1);
        myPsf.setPageLn(500);
        
        RootSourceDTO myRootSource = getSourceInfo(val1);
        String son = myRootSource.getPreferredName();

        java.util.List<UiLabel> myFindConcepts = new ArrayList<UiLabel>();

        myFindConcepts = utsFinderService.findConcepts(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "code", val2, "exact", myPsf);
        bw.println("*Sample Information|Code "+val2+ " from " +son+" in the "+umlsRelease+ " version of UMLS");
        bw.println("!");
        
        
bw.println("*UMLS Concept Information|CUI|Preferred Name|Number of Atoms|Semantic Type(s)|Date Added To Metathesaurus|Preferred English Language Synonyms");
        
		if (myFindConcepts.size() == 0){
		
		bw.println("None");      
		} 
		
		else {
		        
		for (int i = 0; i < myFindConcepts.size(); i++) {
		
		UiLabel myFinCon = myFindConcepts.get(i);
		String ui = myFinCon.getUi();
		ConceptDTO myConcept = getConceptDTO(ui);
		List<AtomDTO> mySynonyms = getMyConceptAtoms(ui);
		
		String dateAdded = StringUtils.left(myConcept.getDateAdded().toString(), 10);
		int noAtoms = myConcept.getAtomCount();
		java.util.List<String> semanticTypes = myConcept.getSemanticTypes();
		List<String> semanticTypeNames = new ArrayList<>();
		List<String> namesAndRsabs = new ArrayList<>();
		
		for (AtomDTO atom: mySynonyms) {
			String name = atom.getTermString().getName();
			String rsab = atom.getRootSource();
			String nameAndRsab = name+" ("+rsab+")";
			if (!rsab.equals(val1)) {namesAndRsabs.add(nameAndRsab);}
			
		}
		
		String synonyms = namesAndRsabs.isEmpty() ? "None" : StringUtils.join(namesAndRsabs,"<br/>");
		for (String styId: semanticTypes) {
			
			SemanticTypeDTO mySty = getSemanticTypeDTO(styId);
			String styName = mySty.getValue();
			semanticTypeNames.add(styName);
		}
		String label = myFinCon.getLabel();
		
		
		bw.println(ui+"|"+label+"|"+noAtoms+"|"+semanticTypeNames+"|"+dateAdded+"|"+synonyms);
		
		//System.out.println(label+"|"+ui+"\n");
		}
		}
	    bw.println("!");
	       
		    AtomDTO myAtom = new AtomDTO();
		    myAtom = utsContentService.getDefaultPreferredAtom(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1);
	        
		    bw.println("*Highest Ranking Atom of "+val2+"|AUI|Term Type|Atom Name|Lexically Normalized Name");
	        
		    String defPrefUi = myAtom.getUi();
		    String atomName = myAtom.getTermString().getName();
		    String normForm = myAtom.getTermString().getTerm().getLuinormForm();
		    String termType = myAtom.getTermType();
		    
		    bw.println(defPrefUi+"|"+termType+"|"+atomName+"|"+normForm);
		    bw.println("!");
		    
            
		    
		    List<AttributeDTO> myAtomAttributes = getAttributeDTOs(defPrefUi,val1,"atom");
		    
		    if (myAtomAttributes.size() > 0) {
		    bw.println("*Highest Ranking Atom of "+val2+" - Atom Attributes|Attribute Name|Attribute Value");	
		    for(int i = 0;i < myAtomAttributes.size();i++) {
		    AttributeDTO myAttributeDTO = myAtomAttributes.get(i);
		    String atn = myAttributeDTO.getName();
		    String atv = myAttributeDTO.getValue();
		    bw.println(atn+"|"+atv);	
		    }
		    
		    bw.println("!");
		    }
		    
		    
//begin Atom-Atom Relations
		    
		    List<AtomRelationDTO> myAtomRelations = getAtomAtomRelations(defPrefUi);
		    
		    if (myAtomRelations.size() > 0) {
		    bw.println("*Highest Ranking Atom of "+val2+" - Atom Relations|Relation Label|Additional Relation Label|Related Atom ID|Related Atom Name|Term Type");
		    
		    for (int i = 0;i < myAtomRelations.size();i++){
		    	
		    	AtomRelationDTO myAtomRelation = myAtomRelations.get(i);
		    	String rel = myAtomRelation.getRelationLabel();
		    	String rela = myAtomRelation.getAdditionalRelationLabel();
		    	String aui2id = myAtomRelation.getRelatedAtom().getUi();
		    	String aui2name = myAtomRelation.getRelatedAtom().getTermString().getName();
		    	String aui2tty = myAtomRelation.getRelatedAtom().getTermType();
		    	bw.println(rel+"|"+rela+"|"+aui2id+"|"+aui2name+"|"+aui2tty);
		    }
		    bw.println("!");
		    }
		    //end Atom-Atom Relations   
		    
		    gov.nih.nlm.uts.webservice.content.Psf mycodePsf = new gov.nih.nlm.uts.webservice.content.Psf();
			mycodePsf.getIncludedSources().add(val1);
			mycodePsf.setPageLn(500);
			
	        java.util.List<AtomDTO> myAtoms = new ArrayList<AtomDTO>();
	        myAtoms = utsContentService.getCodeAtoms(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, mycodePsf);

	        bw.println("*Code Atoms|AUI|SUI|Term|Term Type|Source Atom ID|Source Concept|Obsolete|Suppressible");
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
		 
		 gov.nih.nlm.uts.webservice.content.Psf myconPsf = new gov.nih.nlm.uts.webservice.content.Psf();
		 myconPsf.setIncludeSuppressible(false);
		 myconPsf.getIncludedSources().add(val1);
		 myconPsf.setPageLn(500);
		 
		 java.util.List<AtomClusterRelationDTO> myCodeRelations = new ArrayList<AtomClusterRelationDTO>();
		 myCodeRelations = utsContentService.getCodeCodeRelations(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, val2, val1, myconPsf);
	     bw.println("*Code-Code Relations|Relation Label|Additional Relation Label|Id|Name");
	     //bw.newLine();
	        if (myCodeRelations.size() == 0){
	        	
	        	bw.println("None");	
	        	//bw.newLine();
	        } 
	        
	        else {
	        	
	
		      for (int i = 0; i < myCodeRelations.size(); i++) {
		
		      AtomClusterRelationDTO myAtomClusterRelationDTO = myCodeRelations.get(i);
		
		      String AtomClusterUi = myAtomClusterRelationDTO.getRelatedAtomCluster().getUi();
		      String AtomClusterName = myAtomClusterRelationDTO.getRelatedAtomCluster().getDefaultPreferredName();
		      String AtomClusterRel = myAtomClusterRelationDTO.getRelationLabel();
		      String AtomClusterRela = myAtomClusterRelationDTO.getAdditionalRelationLabel();

			      
		     bw.println(AtomClusterRel+"|"+AtomClusterRela+"|"+AtomClusterUi+"|"+AtomClusterName);
		     //bw.newLine();
		      }
	        }
	      
	    bw.println("!");
	    //bw.newLine();
	    
		    
	    
		 
		 
	    gov.nih.nlm.uts.webservice.content.Psf myPsf2 = new gov.nih.nlm.uts.webservice.content.Psf();
	    myPsf2.setPageLn(500);
	    
	    List<AttributeDTO> myAttributes = getAttributeDTOs(val2,val1,"code");
	     bw.println("*Code Attributes|Attribute Name|Attribute Value");
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
	      //bw.println("*Paths To Root "+ (i+1)+"|Id|Name");
		     if (myarrAtomTreePosPathDTOClient.size() == 0){
		        	
		        	bw.println("None");	
		        	//bw.newLine();
		        } 
		        
		     else {

		     for (int j = 0; j < myarrAtomTreePosPathDTOClient.size(); j++) {
		
		      AtomTreePositionPathDTO myAtmTrPosDTO = myarrAtomTreePosPathDTOClient.get(j);
		
		      List<AtomTreePositionDTO> treepos = myAtmTrPosDTO.getTreePositions();
		      bw.println("*Path to Root|Id|Name");
		      
		      Collections.reverse(treepos);
		
			     for (int k = 0; k < treepos.size(); k++) {
			       AtomTreePositionDTO getj = treepos.get(k);
			       String defPrefName = getj.getDefaultPreferredName();
			       String clusterUi = getj.getAtom().getCode().getUi();
			        
			       bw.println(clusterUi+"|"+StringTool.repeat("&#160;",k)+defPrefName);
			      // bw.newLine();
			       }
			     bw.println("!");
		     	}
				
		        }

       		        
				 
		    List<AtomTreePositionDTO> myarrAtomTreePosChildrenDTOClient = new ArrayList<AtomTreePositionDTO>();
		    myarrAtomTreePosChildrenDTOClient = utsContentService.getAtomTreePositionChildren(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
		    bw.println("*Children|Id|Name");
		    //bw.newLine();
		    
		     if (myarrAtomTreePosChildrenDTOClient.size() == 0){
		        	
		        	bw.println("None");	
		        	//bw.newLine();
		        } 
		        
		     else {

			     for (int j = 0; j < myarrAtomTreePosChildrenDTOClient.size(); j++) {
			
			        AtomTreePositionDTO myAtmTrPosDTO = myarrAtomTreePosChildrenDTOClient.get(j);
			
			        String defaultPrefName = myAtmTrPosDTO.getDefaultPreferredName();
			        //String atomUi = myAtmTrPosDTO.getAtom().getUi();
			        String atomUi = myAtmTrPosDTO.getAtom().getCode().getUi();   
				    bw.println(atomUi+"|"+defaultPrefName);
				    //bw.newLine();
				        }
		     		}
				 bw.println("!");
				 //bw.newLine();
				 
			     
			     
			List<AtomTreePositionDTO> myarrAtomTreePosSiblingDTOClient = new ArrayList<AtomTreePositionDTO>();
			myarrAtomTreePosSiblingDTOClient = utsContentService.getAtomTreePositionSiblings(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, ui, myTreePsf);
			bw.println("*Siblings|Id|Name");
			//bw.newLine();

		     if (myarrAtomTreePosSiblingDTOClient.size() == 0){
		        	
		        	bw.println("None");	
		        	//bw.newLine();
		        } 
		        
		     else {
				for (int j = 0; j < myarrAtomTreePosSiblingDTOClient.size(); j++) {
					
					   AtomTreePositionDTO myAtmTrPosDTO = myarrAtomTreePosSiblingDTOClient.get(j);
					
					 String defaultPrefName = myAtmTrPosDTO.getDefaultPreferredName();
					 //String atomUi = myAtmTrPosDTO.getAtom().getUi();
				     String atomUi = myAtmTrPosDTO.getAtom().getCode().getUi();
						        
					 bw.println(atomUi+"|"+defaultPrefName);
					 //bw.newLine();
					    }
		     		}       
				 bw.println("!");
				 //bw.newLine();

	        }
	    
		//bw.println("!");
	    bw.close();
		
	}
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			
			UTS_Source_documentation srcDocClient = new UTS_Source_documentation(args[0],args[1],args[2]);
			 
			String dataFileName = umlsRelease+"/source_samples.txt";
 
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

	            	
	            	String dir = umlsRelease+"/"+value1;
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
	            			System.out.println("processing samples for "+ value1);
	            			lineIndex++;
	            			
	            
	        }
	        bReader.close();
	        System.out.println("completed sample generation");
	        
        	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}

