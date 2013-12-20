package gov.nih.nlm.uts.clients;

import static java.lang.System.out;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import gov.nih.nlm.uts.clients.Ws_Test_B;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderController;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderControllerImplService;
import gov.nih.nlm.uts.webservice.history.UtsWsHistoryController;
import gov.nih.nlm.uts.webservice.history.UtsWsHistoryControllerImplService;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;
import gov.nih.nlm.uts.webservice.semnet.UtsWsSemanticNetworkController;
import gov.nih.nlm.uts.webservice.semnet.UtsWsSemanticNetworkControllerImplService;

public class Ws_Test_B {
    boolean prResults = true;
    gov.nih.nlm.uts.webservice.content.Psf psf_cdef;
    gov.nih.nlm.uts.webservice.finder.Psf psf_fdef;
	gov.nih.nlm.uts.webservice.content.Psf psf_pagingSample;
	gov.nih.nlm.uts.webservice.content.Psf psf_sourcesSample;
	gov.nih.nlm.uts.webservice.content.Psf psf_termTypesSample;
	gov.nih.nlm.uts.webservice.content.Psf psf_attributeNamesSample;
	gov.nih.nlm.uts.webservice.content.Psf psf_addlRelationLabelSample;
	gov.nih.nlm.uts.webservice.content.Psf psf_languageSample;

    private String username = "";
    private String password = "";

	private String serviceName = "http://umlsks.nlm.nih.gov";
	private UtsWsSecurityController secSvc;
	private UtsWsContentController conSvc;
	private UtsWsMetadataController metaSvc;
	private UtsWsSemanticNetworkController semSvc;
	private UtsWsHistoryController hstSvc;
	private UtsWsFinderController fndSvc;


	private String tgt;
	private String tkt;

	private static Map<String, String> nativeTypes = new HashMap<String, String>();
	private static Map<String, String> ignoreFields = new HashMap<String, String>();
	private static Map<Integer, String> num2name = new HashMap<Integer, String>();
	static {
		nativeTypes.put("String", "1");
		nativeTypes.put("boolean", "1");
		nativeTypes.put("long", "1");
		nativeTypes.put("int", "1");
		nativeTypes.put("BigInteger", "1");
		nativeTypes.put("XMLGregorianCalendar", "1");  // date

		ignoreFields.put("isPerformanceMode", "1");
		ignoreFields.put("getContentClassName", "1");

		num2name.put(1, "getConcept");
		num2name.put(2, "getConceptAtoms");
		num2name.put(3, "getConceptAttributes");
		num2name.put(4, "getAtom");
		num2name.put(5, "getAtomDefinitions");
		num2name.put(6, "getTerm");
		num2name.put(7, "getTermAtoms");
		num2name.put(8, "getTermStrings");
		num2name.put(9, "getTermString");
		num2name.put(10, "getTermStringAtoms");
		num2name.put(11, "getAtomCooccurrences");
		num2name.put(12, "getCooccurrenceSubheadingFrequencies");
		num2name.put(13, "getDefaultPreferredAtom");
		num2name.put(14, "getCode");
		num2name.put(15, "getCodeDefinitions");
		num2name.put(16, "getSourceConcept");
		num2name.put(17, "getSourceConceptDefinitions");
		num2name.put(18, "getSourceDescriptor");
		num2name.put(19, "getSourceDescriptorDefinitions");
		num2name.put(20, "getCodeAtoms");
		num2name.put(21, "getSourceConceptAtoms");
		num2name.put(22, "getSourceDescriptorAtoms");
		num2name.put(23, "getContentViews");
		num2name.put(24, "getContentView");
		num2name.put(25, "getConceptContentViewMemberships");
		num2name.put(26, "getAtomContentViewMemberships");
		num2name.put(27, "getSourceConceptContentViewMemberships");
		num2name.put(28, "getRelationContentViewMemberships");
		num2name.put(29, "getContentViewConceptMembers");
		num2name.put(30, "getContentViewAtomMembers");
		num2name.put(31, "getContentViewRelationMembers");
		num2name.put(32, "getContentViewSourceConceptMembers");
		num2name.put(33, "getSubsets");
		num2name.put(34, "getSubset");
		num2name.put(35, "getAtomSubsetMemberships");
		num2name.put(36, "getSourceConceptSubsetMemberships");
		num2name.put(37, "getRelationSubsetMemberships");
		num2name.put(38, "getSubsetAtomMembers");
		num2name.put(39, "getSubsetSourceConceptMembers");
		num2name.put(40, "getSubsetRelationMembers");
		num2name.put(41, "getMapsets");
		num2name.put(42, "getMappings");
		num2name.put(43, "getMapObjectToMapping");
		num2name.put(44, "getMapObjectFromMapping");
		num2name.put(45, "getRelationRelations");
		num2name.put(46, "getConceptConceptRelations");
		num2name.put(47, "getConceptAtomRelations");
		num2name.put(48, "getCodeCodeRelations");
		num2name.put(49, "getCodeSourceConceptRelations");
		num2name.put(50, "getCodeSourceDescriptorRelations");
		num2name.put(51, "getCodeAtomRelations");
		num2name.put(52, "getSourceConceptCodeRelations");
		num2name.put(53, "getSourceConceptSourceConceptRelations");
		num2name.put(54, "getSourceConceptSourceDescriptorRelations");
		num2name.put(55, "getSourceConceptAtomRelations");
		num2name.put(56, "getSourceDescriptorCodeRelations");
		num2name.put(57, "getSourceDescriptorSourceConceptRelations");
		num2name.put(58, "getSourceDescriptorSourceDescriptorRelations");
		num2name.put(59, "getSourceDescriptorAtomRelations");
		num2name.put(60, "getAtomAtomRelations");
		num2name.put(61, "getAtomCodeRelations");
		num2name.put(62, "getAtomSourceConceptRelations");
		num2name.put(63, "getAtomSourceDescriptorRelations");
		num2name.put(64, "getAtomConceptRelations");
		num2name.put(65, "getRelationAttributes");
		num2name.put(66, "getCodeAttributes");
		num2name.put(67, "getSourceConceptAttributes");
		num2name.put(68, "getSourceDescriptorAttributes");
		num2name.put(69, "getAtomAttributes");
		num2name.put(70, "getSubsetMemberAttributes");
		num2name.put(71, "getMapSetAttributes");
		num2name.put(72, "getMappingAttributes");
		num2name.put(73, "getMapObjectAttributes");
		num2name.put(74, "getContentViewAttributes");
		num2name.put(75, "getContentViewMemberAttributes");
		num2name.put(76, "getSubsetAttributes");
		num2name.put(77, "getRootAtomTreePositions");
		num2name.put(78, "getRootCodeTreePositions");
		num2name.put(79, "getRootSourceConceptTreePositions");
		num2name.put(80, "getRootSourceDescriptorTreePositions");
		num2name.put(81, "getAtomTreePositions");
		num2name.put(82, "getCodeTreePositions");
		num2name.put(83, "getSourceConceptTreePositions");
		num2name.put(84, "getSourceDescriptorTreePositions");
		num2name.put(85, "getAtomTreePositionPathsToRoot");
		num2name.put(86, "getCodeTreePositionPathsToRoot");
		num2name.put(87, "getSourceConceptTreePositionPathsToRoot");
		num2name.put(88, "getSourceDescriptorTreePositionPathsToRoot");
		num2name.put(89, "getAtomTreePositionChildren");
		num2name.put(90, "getCodeTreePositionChildren");
		num2name.put(91, "getSourceConceptTreePositionChildren");
		num2name.put(92, "getSourceDescriptorTreePositionChildren");
		num2name.put(93, "getAtomTreePositionSiblings");
		num2name.put(94, "getCodeTreePositionSiblings");
		num2name.put(95, "getSourceConceptTreePositionSiblings");
		num2name.put(96, "getSourceDescriptorTreePositionSiblings");
		num2name.put(101, "getAdditionalRelationLabel");
		num2name.put(102, "getAllAdditionalRelationLabels");
		num2name.put(103, "getAttributeName");
		num2name.put(104, "getAllAttributeNames");
		num2name.put(105, "getCharacterSet");
		num2name.put(106, "getAllCharacterSets");
		num2name.put(107, "getCooccurrenceType");
		num2name.put(108, "getAllCooccurrenceTypes");
		num2name.put(109, "getGeneralMetadataEntry");
		num2name.put(110, "getIdentifierType");
		num2name.put(111, "getLanguage");
		num2name.put(112, "getAllLanguages");
		num2name.put(113, "getRelationLabel");
		num2name.put(114, "getAllRelationLabels");
		num2name.put(115, " Nav");
		num2name.put(116, "getAllRootSources");
		num2name.put(117, "getVersionedSources");
		num2name.put(118, "getCurrentVersionSource");
		num2name.put(119, "getRootSourceSynonymousNames");
		num2name.put(120, "getSourceAttributeName");
		num2name.put(121, "getAllSourceAttributeNames");
		num2name.put(122, "getAllEquivalentAttributeNames");
		num2name.put(123, "getAllSuperAttributeNames");
		num2name.put(124, "getSubEquivalentAttributeNames");
		num2name.put(125, "getAllSourceCitations");
		num2name.put(126, "getSourceCitation");
		num2name.put(127, "getSource");
		num2name.put(128, "getAllSources");
		num2name.put(129, "getSourceRelationLabel");
		num2name.put(130, "getAllSourceRelationLabels");
		num2name.put(131, "getSourceTermType");
		num2name.put(132, "getAllSourceTermTypes");
		num2name.put(133, "getSubheading");
		num2name.put(134, "getAllSubheadings");
		num2name.put(135, "getTermType");
		num2name.put(136, "getAllTermTypes");
		num2name.put(201, "getSemanticType");
		num2name.put(202, "getAllSemanticTypes");
		num2name.put(203, "getSemanticTypeRelations");
		num2name.put(204, "getSemanticTypeRelationsForPair");
		num2name.put(205, "getInverseSemanticTypeRelations");
		num2name.put(206, "getInheritedSemanticTypeRelations");
		num2name.put(207, "getInverseInheritedSemanticTypeRelations");
		num2name.put(208, "getSemanticTypeGroup");
		num2name.put(209, "getSemanticTypes");
		num2name.put(210, "getAllSemanticTypeGroups");
		num2name.put(211, "getSemanticTypeRelation");
		num2name.put(212, "getAllSemanticTypeRelations");
		num2name.put(213, "getSemanticNetworkRelationLabel");
		num2name.put(214, "getAllSemanticNetworkRelationLabels");
		num2name.put(215, "getSemanticNetworkRelationLabelRelations");
		num2name.put(216, "getSemanticNetworkRelationLabelRelationsForPair");
		num2name.put(217, "getInverseSemanticNetworkRelationLabelRelations");
		num2name.put(218, "getInheritedSemanticNetworkRelationLabelRelations");
		num2name.put(219, "getInverseInheritedSemanticNetworkRelationLabelRelations");
		num2name.put(220, "getSemanticNetworkRelationLabelRelation");
		num2name.put(221, "getAllSemanticNetworkRelationLabelRelations");
		num2name.put(301, "getBequeathedToConceptCuis");
		num2name.put(302, "getConceptBequeathals");
		num2name.put(303, "getConceptDeletions");
		num2name.put(304, "getConceptMerges");
		num2name.put(305, "getMergedToConceptCui");
		num2name.put(306, "getAtomMovements");
		num2name.put(307, "getTermMerges");
		num2name.put(308, "getMergedToTermUi");
		num2name.put(309, "getMovedToConceptCui");
		num2name.put(310, "getSourceAtomChanges");
		num2name.put(311, "getTermDeletions");
		num2name.put(312, "getTermStringDeletions");
		num2name.put(401, "findConcepts");
		num2name.put(402, "findAtoms");
		num2name.put(403, "findCodes");
		num2name.put(404, "findSourceConcepts");
		num2name.put(405, "findSourceDescriptors");
		num2name.put(406, "getCount");

	}


    public Ws_Test_B() {
		this.prResults = true;
		init();
	}

    public Ws_Test_B(String username, String password) {
		this.prResults = true;
		this.username = username;
		this.password = password;
		init();
	}

    public Ws_Test_B(boolean prResults, String username, String password) {
		this.prResults = prResults;
		this.username = username;
		this.password = password;
		init();
	}

	public boolean getPrResults() { return prResults; }
	public void setPrResults(boolean prResults) { this.prResults = prResults; }



    private void init() {
		try {
			secSvc = (new UtsWsSecurityControllerImplService())
				.getUtsWsSecurityControllerImplPort();

			conSvc = (new UtsWsContentControllerImplService())
				.getUtsWsContentControllerImplPort();

			metaSvc = (new UtsWsMetadataControllerImplService())
				.getUtsWsMetadataControllerImplPort();

			semSvc = (new UtsWsSemanticNetworkControllerImplService())
				.getUtsWsSemanticNetworkControllerImplPort();

			hstSvc = (new UtsWsHistoryControllerImplService())
				.getUtsWsHistoryControllerImplPort();

			fndSvc = (new UtsWsFinderControllerImplService())
				.getUtsWsFinderControllerImplPort();

			tgt = secSvc.getProxyGrantTicket(username, password);
			if (tgt == null || tgt.length() == 1) {
				out.println("Could not get valid TGT. Exiting...");
			}

			psf_cdef = new gov.nih.nlm.uts.webservice.content.Psf();
			//psf_cdef.getIncludedSources().add("SNOMEDCT");
			//psf_sourcesSample.getIncludedSources().add("SNOMEDCT");
			//psf_sourcesSample.getIncludedTermTypes().add("PT");
			//psf_cdef.setPaging(1);
			//psf_cdef.setPageLn(25);
			//psf_cdef.setIncludedLanguage("SPA");
			//psf_cdef.getIncludedRelationLabels().add("CHD");
			//psf_cdef.addIncludedSource("SNOMEDCT");
			//psf_cdef.addIncludedTermType("FSN");
			psf_cdef.setIncludeObsolete(false);
			psf_cdef.setIncludeSuppressible(false);
			//psf_fdef.setPaging(1);
			//psf_fdef.setPageLn(25);
			//psf_fdef.getIncludedSources().add("SNOMEDCT");
			//psf_fdef.setCaseSensitive(false);
		} catch (Exception e) {
			out.println("Error!!!" + e.getMessage());
		}
	}


	private void getNoData(String methodName) throws Exception {
		out.println("Currently no data is available\n");
	}
    private void recResult (String recNum, Calendar bCal, String str, boolean ans) {

		int hr = bCal.get(Calendar.HOUR_OF_DAY);
		int mn = bCal.get(Calendar.MINUTE);
		int sc = bCal.get(Calendar.SECOND);

		Calendar eCal = Calendar.getInstance();
		int hr2 = eCal.get(Calendar.HOUR_OF_DAY);
		int mn2 = eCal.get(Calendar.MINUTE);
		int sc2 = eCal.get(Calendar.SECOND);
		String tokens[] = str.split("\\|",2);
		String method = num2name.get(Integer.parseInt(tokens[0]));

		long time = (eCal.getTimeInMillis() - bCal.getTimeInMillis()) / 1000;

		out.println("Test_" + hr + ":" + mn + ":" + sc + "->"
					+ mn2 + ":" + sc2 + " [" + time + "] - <" + method + "> " + str);
    }
	


    private String getProxyTicket() {
		try {
			return secSvc.getProxyTicket(tgt, serviceName);
		} catch (Exception e) { 
			return "";
		}
	}

	private String fldName(String fullName) {
		if (fullName.startsWith("get")) { return fullName.substring(3); }
		if (fullName.startsWith("is")) { return fullName.substring(2); }
		return fullName;
	}

	private void printObj(Object obj) {
		if (!prResults) return;
		if (obj == null) return;
		String stype = "";

		int ln = 0;
		if (obj.getClass().getSimpleName().equals("String")) {
			out.println(obj);
		} else if (obj.getClass().isArray()) {
			ln = Array.getLength(obj);
			if (ln > 0) {
				stype = Array.get(obj, 0).getClass().getSimpleName();
			}
			out.format("%s:%s<%s>%n", obj.getClass().getSimpleName(), stype, ln);
			printObj(obj, 1);
		} else if (obj instanceof ArrayList) {
			Object ooal = ((ArrayList)obj).toArray();
			ln = Array.getLength(ooal);
			if (ln > 0) {
				stype = Array.get(ooal, 0).getClass().getSimpleName();
			}
			out.format("%s:%s<%s>%n", obj.getClass().getSimpleName(), stype, ln);
			printObj(ooal, 1);
		} else {
			out.format("%s%n", obj.getClass().getSimpleName());
			printObj(obj, 1);
		}
		out.println("");
	}

	private void printObj(Object obj, int n) {
		try {
			Class aClass = obj.getClass();
			String simpleName = aClass.getSimpleName();
			if (aClass.isArray()) {
				for (int i = 0; i < Array.getLength(obj); i++) {
					String bClassNm = Array.get(obj, i).getClass().getSimpleName();
					if (nativeTypes.containsKey(bClassNm)) {
						out.format("%"+(n+2)+"c%d. %s%n", ' ', i+1, Array.get(obj, i));
					} else {
						out.format("%"+(n+2)+"c%d%n", ' ', i+1);
						printObj(Array.get(obj, i), n+2);
						out.println("");
					}
				}
			} else {
				String stype = "";
				int ln = 0;
				Method[] methods = aClass.getMethods();
				if (methods != null) {
					String mName;
					String retTypeName;
					for (Method m: aClass.getMethods()) {
						mName = m.getName();
						// ignore certain fields.
						if ((!ignoreFields.containsKey(mName))
							&& (mName.startsWith("get") || mName.startsWith("is"))) {
							if (m.getParameterTypes().length == 0) {
								Object oo = m.invoke(obj);
								retTypeName = m.getReturnType().getSimpleName();
								if (oo != null) {
									if (retTypeName.equals("TypeDesc")
										|| retTypeName.equals("Class")) {
									} else {
										if (nativeTypes.containsKey(retTypeName)) {
											out.format("%"+(n+2)+"c%s: %s%n", ' ',
													   fldName(mName), oo);
										} else {
											if (oo.getClass().isArray()) {
												ln = Array.getLength(oo);
												if (ln > 0) {
													stype = Array.get(oo, 0).getClass().getSimpleName();
												}
												out.format("%"+(n+2)+"c%s: %s:%s<%s>%n", ' ',
														   fldName(mName), retTypeName,
														   stype, ln);
												printObj(oo, n+2);
											} else if (oo instanceof ArrayList) {
												Object ooal = ((ArrayList) oo).toArray();
												ln = Array.getLength(ooal);
												if (ln > 0) {
													stype = Array.get(ooal, 0).getClass().getSimpleName();
												}
												out.format("%"+(n+2)+"c%s: %s:%s<%s>%n", ' ',
														   fldName(mName), retTypeName,
														   stype, ln);
												printObj(ooal, n+2);
											} else {
												out.format("%"+(n+2)+"c%s: %s%n", ' ',
														   fldName(mName), retTypeName);
												printObj(oo, n+2);
											}
											out.println("");
										}
									}
								}
							}
						}
					}
				} else {
					out.println("No methods!!!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	////////////////////
	private String printObj2Str(Object obj) {
		if (!prResults) return "";
		if (obj == null) return "";
		String stype = "";

		int ln = 0;
		StringBuilder sb = new StringBuilder();
		Formatter frmt = new Formatter(sb);


		if (obj.getClass().getSimpleName().equals("String")) {
			//out.println(obj);
			frmt.format("%s\n", obj);
		} else if (obj.getClass().isArray()) {
			ln = Array.getLength(obj);
			if (ln > 0) {
				stype = Array.get(obj, 0).getClass().getSimpleName();
			}
			//out.format("%s:%s<%s>%n", obj.getClass().getSimpleName(), stype, ln);
			//printObj(obj, 1);
			frmt.format("%s:%s<%s>%n", obj.getClass().getSimpleName(), stype, ln);
			printObj2Str(obj, 1, frmt);
		} else if (obj instanceof ArrayList) {
			Object ooal = ((ArrayList)obj).toArray();
			ln = Array.getLength(ooal);
			if (ln > 0) {
				stype = Array.get(ooal, 0).getClass().getSimpleName();
			}
			//out.format("%s:%s<%s>%n", obj.getClass().getSimpleName(), stype, ln);
			//printObj(ooal, 1);
			frmt.format("%s:%s<%s>%n", obj.getClass().getSimpleName(), stype, ln);
			printObj2Str(ooal, 1, frmt);
		} else {
			//out.format("%s%n", obj.getClass().getSimpleName());
			//printObj(obj, 1);
			frmt.format("%s%n", obj.getClass().getSimpleName());
			printObj2Str(obj, 1, frmt);
		}
		//out.println("");
		frmt.format("\n");
		return frmt.toString();
	}

	private void printObj2Str(Object obj, int n, Formatter frmt) {
		try {
			Class aClass = obj.getClass();
			String simpleName = aClass.getSimpleName();
			if (aClass.isArray()) {
				for (int i = 0; i < Array.getLength(obj); i++) {
					String bClassNm = Array.get(obj, i).getClass().getSimpleName();
					if (nativeTypes.containsKey(bClassNm)) {
						//out.format("%"+(n+2)+"c%d. %s%n", ' ', i+1, Array.get(obj, i));
						frmt.format("%"+(n+2)+"c%d. %s%n", ' ', i+1, Array.get(obj, i));
					} else {
						//out.format("%"+(n+2)+"c%d%n", ' ', i+1);
						//printObj(Array.get(obj, i), n+2);
						//out.println("");
						frmt.format("%"+(n+2)+"c%d%n", ' ', i+1);
						printObj2Str(Array.get(obj, i), n+2, frmt);
						frmt.format("\n");
					}
				}
			} else {
				String stype = "";
				int ln = 0;
				Method[] methods = aClass.getMethods();
				if (methods != null) {
					String mName;
					String retTypeName;
					for (Method m: aClass.getMethods()) {
						mName = m.getName();
						// ignore certain fields.
						if ((!ignoreFields.containsKey(mName))
							&& (mName.startsWith("get") || mName.startsWith("is"))) {
							if (m.getParameterTypes().length == 0) {
								Object oo = m.invoke(obj);
								retTypeName = m.getReturnType().getSimpleName();
								if (oo != null) {
									if (retTypeName.equals("TypeDesc")
										|| retTypeName.equals("Class")) {
									} else {
										if (nativeTypes.containsKey(retTypeName)) {
											//out.format("%"+(n+2)+"c%s: %s%n", ' ',
											frmt.format("%"+(n+2)+"c%s: %s%n", ' ',
													   fldName(mName), oo);
										} else {
											if (oo.getClass().isArray()) {
												ln = Array.getLength(oo);
												if (ln > 0) {
													stype = Array.get(oo, 0).getClass().getSimpleName();
												}
												//out.format("%"+(n+2)+"c%s: %s:%s<%s>%n", ' ',
												frmt.format("%"+(n+2)+"c%s: %s:%s<%s>%n", ' ',
														   fldName(mName), retTypeName,
														   stype, ln);
												//printOb(oo, n+2);
												printObj2Str(oo, n+2, frmt);
											} else if (oo instanceof ArrayList) {
												Object ooal = ((ArrayList) oo).toArray();
												ln = Array.getLength(ooal);
												if (ln > 0) {
													stype = Array.get(ooal, 0).getClass().getSimpleName();
												}
												//out.format("%"+(n+2)+"c%s: %s:%s<%s>%n", ' ',
												frmt.format("%"+(n+2)+"c%s: %s:%s<%s>%n", ' ',
														   fldName(mName), retTypeName,
														   stype, ln);
												//printObj(ooal, n+2);
												printObj2Str(ooal, n+2, frmt);
											} else {
												//out.format("%"+(n+2)+"c%s: %s%n", ' ',
												frmt.format("%"+(n+2)+"c%s: %s%n", ' ',
														   fldName(mName), retTypeName);
												//printObj(oo, n+2);
												printObj2Str(oo, n+2, frmt);
											}
											//out.println("");
											frmt.format("\n");
										}
									}
								}
							}
						}
					}
				} else {
					//out.println("No methods!!!");
					frmt.format("No methods!!!\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	////////////////////
    public boolean doRec(String recNum, String str) {
		try {
			String[] tokens = str.split("\\|",5);
			int testNum = Integer.parseInt(tokens[0]);

			boolean ans = false;
			Calendar bCal = Calendar.getInstance();
			Object obj = null;

			switch (testNum) {
			case 1: obj = conSvc.getConcept(getProxyTicket(), tokens[1], tokens[2]); break;
				//case 1: out.println(printObj2Str(conSvc.getConcept(getProxyTicket(), tokens[1], tokens[2]))); break;
			case 2: obj = conSvc.getConceptAtoms(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 3: obj = conSvc.getConceptAttributes(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 4: obj = conSvc.getAtom(getProxyTicket(), tokens[1], tokens[2]); break;
			case 5: obj = conSvc.getAtomDefinitions(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 6: obj = conSvc.getTerm(getProxyTicket(), tokens[1], tokens[2]); break;
			case 7: obj = conSvc.getTermAtoms(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 8: obj = conSvc.getTermStrings(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 9: obj = conSvc.getTermString(getProxyTicket(), tokens[1], tokens[2]); break;
			case 10: obj = conSvc.getTermStringAtoms(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 11: obj = conSvc.getAtomCooccurrences(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;

			case 12: obj = conSvc.getCooccurrenceSubheadingFrequencies(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 13: obj = conSvc.getDefaultPreferredAtom(getProxyTicket(), tokens[1], tokens[2], tokens[3]); break;
			case 14: obj = conSvc.getCode(getProxyTicket(), tokens[1], tokens[2], tokens[3]); break;
			case 15: obj = conSvc.getCodeDefinitions(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;

			case 16: obj = conSvc.getSourceConcept(getProxyTicket(), tokens[1], tokens[2], tokens[3]); break;
			case 17: obj = conSvc.getSourceConceptDefinitions(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 18: obj = conSvc.getSourceDescriptor(getProxyTicket(), tokens[1], tokens[2], tokens[3]); break;
			case 19: obj = conSvc.getSourceDescriptorDefinitions(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 20: obj = conSvc.getCodeAtoms(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 21: obj = conSvc.getSourceConceptAtoms(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 22: obj = conSvc.getSourceDescriptorAtoms(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 23: obj = conSvc.getContentViews(getProxyTicket(), tokens[1], psf_cdef); break;
			case 24: obj = conSvc.getContentView(getProxyTicket(), tokens[1], tokens[2]); break;

			case 25: //obj = conSvc.getConceptContentViewMemberships(tokens[1], tokens[2]);
				getNoData("getConceptContentViewMemberships"); break;
			case 26: obj = conSvc.getAtomContentViewMemberships(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 27: obj = conSvc.getSourceConceptContentViewMemberships
							  (getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 28: //obj = conSvc.getRelationContentViewMemberships(tokens[1], tokens[2]);
				getNoData("getRelationContentViewMemberships"); break;
			case 29: //obj = conSvc.getContentViewConceptMembers(tokens[1], tokens[2]);
				getNoData("getContentViewConceptMembers"); break;
			case 30: obj = conSvc.getContentViewAtomMembers(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 31: //obj = conSvc.getContentViewRelationMembers(tokens[1], tokens[2]);
				getNoData("getContentViewRelationMembers"); break;
			case 32: obj = conSvc.getContentViewSourceConceptMembers(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 33: obj = conSvc.getSubsets(getProxyTicket(), tokens[1], psf_cdef); break;
			case 34: obj = conSvc.getSubset(getProxyTicket(), tokens[1], tokens[2]); break;

			case 35: obj = conSvc.getAtomSubsetMemberships(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 36: //obj = conSvc.getSourceConceptSubsetMemberships(tokens[1], tokens[2], tokens[3]);
				getNoData("getSourceConceptSubsetMemberships"); break;
			case 37: //obj = conSvc.getRelationSubsetMemberships(tokens[1], tokens[2]);
				getNoData("getRelationSubsetMemberships"); break;
			case 38: obj = conSvc.getSubsetAtomMembers(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 39: //obj = conSvc.getSubsetSourceConceptMembers(tokens[1], tokens[2]);
				getNoData("getSubsetSourceConceptMembers"); break;
			case 40: //obj = conSvc.getSubsetRelationMembers(tokens[1], tokens[2]);
				getNoData("getSubsetRelationMembers"); break;
			case 41: obj = conSvc.getMapsets(getProxyTicket(), tokens[1], psf_cdef); break;
			case 42: obj = conSvc.getMappings(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 43: obj = conSvc.getMapObjectToMapping(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 44: obj = conSvc.getMapObjectFromMapping(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;

			case 45: //obj = conSvc.getRelationRelations(tokens[1], tokens[2]);
				getNoData("getRelationRelations"); break;
			case 46: obj = conSvc.getConceptConceptRelations(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 47: obj = conSvc.getConceptAtomRelations(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 48: obj = conSvc.getCodeCodeRelations(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 49: obj = conSvc.getCodeSourceConceptRelations(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 50: obj = conSvc.getCodeSourceDescriptorRelations(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 51: obj = conSvc.getCodeAtomRelations(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 52: obj = conSvc.getSourceConceptCodeRelations(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 53: obj = conSvc.getSourceConceptSourceConceptRelations(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 54: //obj = conSvc.getSourceConceptSourceDescriptorRelations
				//(tokens[1], tokens[2], tokens[3]);
				getNoData("getSourceConceptSourceDescriptorRelations"); break;

			case 55: obj = conSvc.getSourceConceptAtomRelations(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 56: obj = conSvc.getSourceDescriptorCodeRelations(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 57: //obj = conSvc.getSourceDescriptorSourceConceptRelations(tokens[1], tokens[2], tokens[3]);
				getNoData("getSourceDescriptorSourceConceptRelations"); break;
			case 58: obj = conSvc.getSourceDescriptorSourceDescriptorRelations(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 59: obj = conSvc.getSourceDescriptorAtomRelations(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 60: obj = conSvc.getAtomAtomRelations(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 61: obj = conSvc.getAtomCodeRelations(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 62: obj = conSvc.getAtomSourceConceptRelations(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 63: obj = conSvc.getAtomSourceDescriptorRelations(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 64: obj = conSvc.getAtomConceptRelations(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;

			case 65: obj = conSvc.getRelationAttributes(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 66: obj = conSvc.getCodeAttributes(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 67: obj = conSvc.getSourceConceptAttributes(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 68: obj = conSvc.getSourceDescriptorAttributes(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 69: obj = conSvc.getAtomAttributes(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 70: obj = conSvc.getSubsetMemberAttributes(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 71: obj = conSvc.getMapSetAttributes(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 72: //obj = conSvc.getMappingAttributes(tokens[1], tokens[2]); break;
				getNoData("getMapObjectAttributes"); break;
			case 73: //obj = conSvc.getMapObjectAttributes(tokens[1], tokens[2]);
				getNoData("getMapObjectAttributes"); break;
			case 74: //obj = conSvc.getContentViewAttributes(tokens[1], tokens[2]);
				getNoData("getContentViewAttributes"); break;

			case 75: //obj = conSvc.getContentViewMemberAttributes(tokens[1], tokens[2]);
				getNoData("getContentViewMemberAttributes"); break;
			case 76: obj = conSvc.getSubsetAttributes(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 77: obj = conSvc.getRootAtomTreePositions(getProxyTicket(), tokens[1], psf_cdef); break;
			case 78: //obj = conSvc.getRootCodeTreePositions(tokens[1]);
				getNoData("getRootCodeTreePositions"); break;
			case 79: obj = conSvc.getRootSourceConceptTreePositions(getProxyTicket(), tokens[1], psf_cdef); break;
			case 80: obj = conSvc.getRootSourceDescriptorTreePositions(getProxyTicket(), tokens[1], psf_cdef); break;
			case 81: obj = conSvc.getAtomTreePositions(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 82: //obj = conSvc.getCodeTreePositions(tokens[1], tokens[2], tokens[3]);
				getNoData("getCodeTreePositions"); break;
			case 83: obj = conSvc.getSourceConceptTreePositions(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;
			case 84: obj = conSvc.getSourceDescriptorTreePositions(getProxyTicket(), tokens[1], tokens[2], tokens[3], psf_cdef); break;

			case 85: obj = conSvc.getAtomTreePositionPathsToRoot(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 86: //obj = conSvc.getCodeTreePositionPathsToRoot(tokens[1], tokens[2]);
				getNoData("getCodeTreePositionPathsToRoot"); break;
			case 87: obj = conSvc.getSourceConceptTreePositionPathsToRoot(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 88: obj = conSvc.getSourceDescriptorTreePositionPathsToRoot(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 89: obj = conSvc.getAtomTreePositionChildren(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 90: //obj = conSvc.getCodeTreePositionChildren(tokens[1], tokens[2]);
				getNoData("getCodeTreePositionChildren"); break;
			case 91: obj = conSvc.getSourceConceptTreePositionChildren(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 92: obj = conSvc.getSourceDescriptorTreePositionChildren(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 93: obj = conSvc.getAtomTreePositionSiblings(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 94: //obj = conSvc.getCodeTreePositionSiblings(tokens[1], tokens[2]);
				getNoData("getCodeTreePositionSiblings"); break;

			case 95: obj = conSvc.getSourceConceptTreePositionSiblings(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
			case 96: obj = conSvc.getSourceDescriptorTreePositionSiblings(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;
				//case 97: obj = conSvc.getSourceDefinitionsAttributes(getProxyTicket(), tokens[1], tokens[2], psf_cdef); break;

				// meta data
			case 101: obj = metaSvc.getAdditionalRelationLabel(getProxyTicket(), tokens[1], tokens[2]); break;
			case 102: obj = metaSvc.getAllAdditionalRelationLabels(getProxyTicket(), tokens[1]); break;
			case 103: obj = metaSvc.getAttributeName(getProxyTicket(), tokens[1], tokens[2]); break;
			case 104: obj = metaSvc.getAllAttributeNames(getProxyTicket(), tokens[1]); break;
			case 105: obj = metaSvc.getCharacterSet(getProxyTicket(), tokens[1], tokens[2]); break;
			case 106: obj = metaSvc.getAllCharacterSets(getProxyTicket(), tokens[1]); break;
			case 107: obj = metaSvc.getCooccurrenceType(getProxyTicket(), tokens[1], tokens[2]); break;
			case 108: obj = metaSvc.getAllCooccurrenceTypes(getProxyTicket(), tokens[1]); break;
			case 109: //obj = metaSvc.getGeneralMetadataEntry(tokens[1], tokens[2]);
				getNoData("getGeneralMetadataEntry"); break;
			case 110: obj = metaSvc.getIdentifierType(getProxyTicket(), tokens[1], tokens[2]); break;

			case 111: obj = metaSvc.getLanguage(getProxyTicket(), tokens[1], tokens[2]); break;
			case 112: obj = metaSvc.getAllLanguages(getProxyTicket(), tokens[1]); break;
			case 113: obj = metaSvc.getRelationLabel(getProxyTicket(), tokens[1], tokens[2]); break;
			case 114: obj = metaSvc.getAllRelationLabels(getProxyTicket(), tokens[1]); break;
			case 115: obj = metaSvc.getRootSource(getProxyTicket(), tokens[1], tokens[2]); break;
			case 116: obj = metaSvc.getAllRootSources(getProxyTicket(), tokens[1]); break;
			case 117: obj = metaSvc.getVersionedSources(getProxyTicket(), tokens[1], tokens[2]); break;
			case 118: obj = metaSvc.getCurrentVersionSource(getProxyTicket(), tokens[1], tokens[2]); break;
			case 119: obj = metaSvc.getRootSourceSynonymousNames(getProxyTicket(), tokens[1], tokens[2]); break;
			case 120: obj = metaSvc.getSourceAttributeName(getProxyTicket(), tokens[1], tokens[2], tokens[3]); break;
			case 121: obj = metaSvc.getAllSourceAttributeNames(getProxyTicket(), tokens[1]); break;
			case 122: //obj = metaSvc.getAllEquivalentAttributeNames(tokens[1], tokens[2], tokens[3]);
				getNoData("getAllEquivalentAttributeNames"); break;
			case 123: //obj = metaSvc.getAllSuperAttributeNames(tokens[1], tokens[2], tokens[3]);
				getNoData("getAllSuperAttributeNames"); break;
			case 124: //obj = metaSvc.getSubEquivalentAttributeNames(tokens[1], tokens[2], tokens[3]);
				getNoData("getSubEquivalentAttributeNames"); break;
			case 125: obj = metaSvc.getAllSourceCitations(getProxyTicket(), tokens[1]); break;
			case 126: obj = metaSvc.getSourceCitation(getProxyTicket(), tokens[1], tokens[2]); break;
			case 127: obj = metaSvc.getSource(getProxyTicket(), tokens[1], tokens[2]); break;
			case 128: obj = metaSvc.getAllSources(getProxyTicket(), tokens[1]); break;
			case 129: obj = metaSvc.getSourceRelationLabel(getProxyTicket(), tokens[1], tokens[2], tokens[3], tokens[4]); break;
			case 130: obj = metaSvc.getAllSourceRelationLabels(getProxyTicket(), tokens[1]); break;

			case 131: obj = metaSvc.getSourceTermType(getProxyTicket(), tokens[1], tokens[2], tokens[3]); break;
			case 132: obj = metaSvc.getAllSourceTermTypes(getProxyTicket(), tokens[1]); break;
			case 133: obj = metaSvc.getSubheading(getProxyTicket(), tokens[1], tokens[2]); break;
			case 134: obj = metaSvc.getAllSubheadings(getProxyTicket(), tokens[1]); break;
			case 135: obj = metaSvc.getTermType(getProxyTicket(), tokens[1], tokens[2]); break;
			case 136: obj = metaSvc.getAllTermTypes(getProxyTicket(), tokens[1]); break;

				// semantic network
			case 201: obj = semSvc.getSemanticType(getProxyTicket(), tokens[1], tokens[2]); break;
			case 202: obj = semSvc.getAllSemanticTypes(getProxyTicket(), tokens[1]); break;
			case 203: obj = semSvc.getSemanticTypeRelations(getProxyTicket(), tokens[1], tokens[2]); break;
			case 204: obj = semSvc.getSemanticTypeRelationsForPair(getProxyTicket(), tokens[1], tokens[2], tokens[3]); break;
			case 205: obj = semSvc.getInverseSemanticTypeRelations(getProxyTicket(), tokens[1], tokens[2]); break;
			case 206: obj = semSvc.getInheritedSemanticTypeRelations(getProxyTicket(), tokens[1], tokens[2]); break;
			case 207: obj = semSvc.getInverseInheritedSemanticTypeRelations(getProxyTicket(), tokens[1], tokens[2]); break;
			case 208: obj = semSvc.getSemanticTypeGroup(getProxyTicket(), tokens[1], tokens[2]); break;
			case 209: obj = semSvc.getSemanticTypes(getProxyTicket(), tokens[1], tokens[2]); break;
			case 210: obj = semSvc.getAllSemanticTypeGroups(getProxyTicket(), tokens[1]); break;

			case 211: obj = semSvc.getSemanticTypeRelation(getProxyTicket(), tokens[1], tokens[2], tokens[3], tokens[4]); break;
			case 212: obj = semSvc.getAllSemanticTypeRelations(getProxyTicket(), tokens[1]); break;
			case 213: obj = semSvc.getSemanticNetworkRelationLabel(getProxyTicket(), tokens[1], tokens[2]); break;
			case 214: obj = semSvc.getAllSemanticNetworkRelationLabels(getProxyTicket(), tokens[1]); break;
			case 215: obj = semSvc.getSemanticNetworkRelationLabelRelations(getProxyTicket(), tokens[1], tokens[2]); break;
			case 216: obj = semSvc.getSemanticNetworkRelationLabelRelationsForPair
					(getProxyTicket(), tokens[1], tokens[2], tokens[3]); break;
			case 217: obj = semSvc.getInverseSemanticNetworkRelationLabelRelations
					(getProxyTicket(), tokens[1], tokens[2]); break;
			case 218: obj = semSvc.getInheritedSemanticNetworkRelationLabelRelations
					(getProxyTicket(), tokens[1], tokens[2]); break;
			case 219: obj = semSvc.getInverseInheritedSemanticNetworkRelationLabelRelations
					(getProxyTicket(), tokens[1], tokens[2]); break;
			case 220: obj = semSvc.getSemanticNetworkRelationLabelRelation
					(getProxyTicket(), tokens[1], tokens[2], tokens[3], tokens[4]); break;
			case 221: obj = semSvc.getAllSemanticNetworkRelationLabelRelations(getProxyTicket(), tokens[1]); break;

			case 301: obj = hstSvc.getBequeathedToConceptCuis(getProxyTicket(), tokens[1], tokens[2]); break;
			case 302: obj = hstSvc.getConceptBequeathals(getProxyTicket(), tokens[1], tokens[2], tokens[3]); break;
			case 303: obj = hstSvc.getConceptDeletions(getProxyTicket(), tokens[1], tokens[2], tokens[3]); break;
			case 304: obj = hstSvc.getConceptMerges(getProxyTicket(), tokens[1], tokens[2], tokens[3]); break;
			case 305: obj = hstSvc.getMergedToConceptCui(getProxyTicket(), tokens[1], tokens[2]); break;
			case 306: obj = hstSvc.getAtomMovements(getProxyTicket(), tokens[1], tokens[2]); break;
			case 307: obj = hstSvc.getTermMerges(getProxyTicket(), tokens[1], tokens[2]); break;
			case 308: obj = hstSvc.getMergedToTermUi(getProxyTicket(), tokens[1], tokens[2]); break;
			case 309: obj = hstSvc.getMovedToConceptCui(getProxyTicket(), tokens[1], tokens[2]); break;
			case 310: obj = hstSvc.getSourceAtomChanges(getProxyTicket(), tokens[1], tokens[2], tokens[3], tokens[4]); break;
			case 311: obj = hstSvc.getTermDeletions(getProxyTicket(), tokens[1], tokens[2], tokens[3]); break;
			case 312: obj = hstSvc.getTermStringDeletions(getProxyTicket(), tokens[1], tokens[2], tokens[3]); break;


			case 401: obj = fndSvc.findConcepts(getProxyTicket(), tokens[1], tokens[2], tokens[3].replace('^', '|'), tokens[4], psf_fdef); break;
			case 402: obj = fndSvc.findAtoms(getProxyTicket(), tokens[1], tokens[2], tokens[3].replace('^', '|'), tokens[4], psf_fdef); break;
			case 403: obj = fndSvc.findCodes(getProxyTicket(), tokens[1], tokens[2], tokens[3].replace('^', '|'), tokens[4], psf_fdef); break;
			case 404: obj = fndSvc.findSourceConcepts(getProxyTicket(), tokens[1], tokens[2], tokens[3].replace('^', '|'), tokens[4], psf_fdef); break;
			case 405: obj = fndSvc.findSourceDescriptors(getProxyTicket(), tokens[1], tokens[2], tokens[3].replace('^', '|'), tokens[4], psf_fdef); break;
			//case 406: obj = fndSvc.getCount(getProxyTicket(), tokens[1], tokens[2], tokens[3].replace('^', '|'), tokens[4], psf_fdef); break;



			default: out.println("Unrecognized input " + str);
				return false; 
			}
			if (obj != null) {
				recResult(recNum, bCal, str, true);
				if (prResults) {
					out.println(printObj2Str(obj));
				}
				return true;
			} else {
				recResult(recNum, bCal, str, false);
				return false;
			}
		} catch (Throwable t) {
			out.println("Error invoking req " + str);
			t.printStackTrace();
			return false;
		}
    }

	private static Map<String, String> tests = new HashMap<String, String>();
	static {

		tests.put("1", "1|CURREL|C0014591");
		tests.put("2", "2|CURREL|C1265527");
		tests.put("3", "3|CURREL|C1700357");
		tests.put("4", "4|CURREL|A8377608");
		tests.put("5", "5|CURREL|A15660675");

		tests.put("6", "6|CURREL|L1086573");
		tests.put("7", "7|CURREL|L1086573");
		tests.put("8", "8|CURREL|L6055888");
		tests.put("9", "9|CURREL|S5418236");
		tests.put("10", "10|CURREL|S3492332");
		tests.put("11", "11|CURREL|A0076226");
		tests.put("12", "12|CURREL|7752212");
		//tests.put("13", "13|CURREL|352ccbe48393975e52cdfc6aa327e4dd|MSH");
		tests.put("13", "13|CURREL|D000532|MSH");

		tests.put("14", "14|CURREL|102735002|SNOMEDCT");
		tests.put("15", "15|CURREL|DNS|SPN");
		tests.put("16", "16|CURREL|150618008|SNOMEDCT");
		tests.put("16a", "16|CURREL|249366005|SNOMEDCT");
		tests.put("16b", "16|CURREL|363662004|SNOMEDCT");
		tests.put("17", "17|CURREL|M0526999|MSH");
		tests.put("18", "18|CURREL|D015060|MSH");
		tests.put("19", "19|CURREL|GO:0006555|GO");

		tests.put("20", "20|CURREL|D008636|MSHFRE");
		tests.put("21", "21|CURREL|150618008|SNOMEDCT");
		tests.put("22", "22|CURREL|D000103|MSH");
		tests.put("23", "23|CURREL|");
		tests.put("24", "24|CURREL|C2711988");
		tests.put("25", "25|CURREL|????");
		tests.put("26", "26|CURREL|A9459570");
		tests.put("27", "27|CURREL|82261001|SNOMEDCT");
		tests.put("28", "28|CURREL|????");
		tests.put("29", "29|CURREL|????");
		tests.put("30", "30|CURREL|C1964028");
		tests.put("31", "31|CURREL|???");
		tests.put("32", "32|CURREL|IC2711988");
		tests.put("33", "33|CURREL|");
		tests.put("34", "34|CURREL|C1368722");
		tests.put("35", "35|CURREL|A6943203");
		tests.put("36", "36|CURREL|????");
		tests.put("37", "37|CURREL|????");
		tests.put("38", "38|CURREL|C1368722");
		tests.put("39", "39|CURREL|????");
		tests.put("40", "40|CURREL|????");
		tests.put("41", "41|CURREL|");
		tests.put("42", "42|CURREL|C3165219");
		tests.put("43", "43|CURREL|82261001");
		tests.put("43a", "43|CURREL|718004");
		tests.put("43b", "43|CURREL|466.19");
		tests.put("44", "44|CURREL|820.8");
		tests.put("44a", "44|CURREL|718004");
		tests.put("44b", "44|CURREL|466.19");
		tests.put("45", "45|CURREL|????");
		tests.put("46", "46|CURREL|C0014591");
		tests.put("47", "47|CURREL|C0007286");
		tests.put("48", "48|CURREL|20000022|MDRGER");
		tests.put("49", "49|CURREL|579.0|ICD9CM");
		tests.put("50", "50|CURREL|U000005|MSH");
		tests.put("51", "51|CURREL|CDR0000039759|PDQ");
		tests.put("52", "52|CURREL|32337007|SNOMEDCT");
		tests.put("53", "53|CURREL|179159005|SNOMEDCT");
		tests.put("54", "54|CURREL|???|SNOMEDCT");
		tests.put("55", "55|CURREL|441806004|SNOMEDCT");
		tests.put("56", "56|CURREL|D001419|MSH");
		tests.put("57", "57|CURREL|????|MSH");
		tests.put("58", "58|CURREL|D000652|MSH");
		tests.put("58a", "58|CURREL|D004844|MSH");
		tests.put("59", "59|CURREL|D014028|MSH");
		tests.put("60", "60|CURREL|A1317707");
		tests.put("61", "61|CURREL|A4356606");
		tests.put("62", "62|CURREL|A16344698");
		tests.put("63", "63|CURREL|A17775421");
		tests.put("64", "64|CURREL|A0851653");
		tests.put("65", "65|CURREL|R74224153");
		tests.put("66", "66|CURREL|10042784|MDR");
		tests.put("67", "67|CURREL|102735002|SNOMEDCT");
		tests.put("68", "68|CURREL|D015060|MSH");
		tests.put("69", "69|CURREL|A7755565");
		tests.put("70", "70|CURREL|AT139571931");
		tests.put("71", "71|CURREL|C2963202");
		tests.put("72", "72|CURREL|???");
		tests.put("73", "73|CURREL|????");
		tests.put("74", "74|CURREL|????");
		tests.put("75", "75|CURREL|????");
		tests.put("76", "76|CURREL|IC1321498");
		tests.put("77", "77|CURREL|");
		tests.put("78", "78|CURREL|????");
		tests.put("79", "79|CURREL|");
		tests.put("80", "80|CURREL|");
		tests.put("81", "81|CURREL|A3870755");
		tests.put("82", "82|CURREL|???|???");
		tests.put("83", "83|CURREL|149194007|SCTSPA");
		tests.put("84", "84|CURREL|D015060|MSH");
		tests.put("85", "85|CURREL|a4c9892742da11ddb432fa7025451a3b");
		tests.put("86", "86|CURREL|????");
		tests.put("87", "87|CURREL|7de51e59b78501cbf441665885ae4559");
		tests.put("88", "88|CURREL|aa0515eaa1fe1e9fd263feeab56fdb4c");
		tests.put("89", "89|CURREL|A0055007");
		tests.put("89a", "89|CURREL|a4c9892742da11ddb432fa7025451a3b");
		tests.put("90", "90|CURREL|????");
		tests.put("91", "91|CURREL|df26dc6c2ba321499edce7293b47dd74");
		tests.put("92", "92|CURREL|e25d785327624aca92b4f3d419b66c17");
		tests.put("93", "93|CURREL|a4c9892742da11ddb432fa7025451a3b");
		tests.put("94", "94|CURREL|????");
		tests.put("95", "95|CURREL|7de51e59b78501cbf441665885ae4559");
		tests.put("96", "96|CURREL|aa0515eaa1fe1e9fd263feeab56fdb4c");
		tests.put("97", "97|CURREL|e00804648ee8c2dd732b98874fdbbec4");

		tests.put("101", "101|CURREL|origin_of");
		tests.put("102", "102|CURREL|");
		tests.put("103", "103|CURREL|TH");
		tests.put("104", "104|CURREL|");
		tests.put("105", "105|CURREL|UTF-8");
		tests.put("106", "106|CURREL|");
		tests.put("107", "107|CURREL|LQ");
		tests.put("108", "108|CURREL|");
		tests.put("109", "109|CURREL|?gmd?");
		tests.put("110", "110|CURREL|SCUI");
		tests.put("111", "111|CURREL|DAN");
		tests.put("112", "112|CURREL|");
		tests.put("113", "113|CURREL|RN");
		tests.put("114", "114|CURREL|");
		tests.put("115", "115|CURREL|SNOMEDCT");
		tests.put("116", "116|CURREL|");
		tests.put("117", "117|CURREL|LNC");
		tests.put("118", "118|CURREL|LNC");
		tests.put("119", "119|CURREL|CCS");
		tests.put("120", "120|CURREL|PSY|HN");
		tests.put("121", "121|CURREL|");
		tests.put("121a", "121|CURREL|SNOMEDCT");
		tests.put("122", "122|CURREL|???DX|MSH");
		tests.put("123", "123|CURREL|???DRT|MSH");
		tests.put("124", "124|CURREL|???DRT|MSH");
		tests.put("125", "125|CURREL|");
		tests.put("126", "126|CURREL|37");
		tests.put("127", "127|CURREL|ICD9CM_1998");
		tests.put("128", "128|CURREL|");
		tests.put("129", "129|CURREL|MEDCIN|RN|isa");
		tests.put("130", "130|CURREL|MEDCIN");
		tests.put("131", "131|CURREL|MSH|MH");
		tests.put("132", "132|CURREL|SNOMEDCT");
		tests.put("133", "133|CURREL|AE");
		tests.put("134", "134|CURREL|");
		tests.put("135", "135|CURREL|MH");
		tests.put("136", "136|CURREL|");

		tests.put("201", "201|CURREL|T074");
		tests.put("202", "202|CURREL|");
		tests.put("203", "203|CURREL|T005");
		tests.put("204", "204|CURREL|T059|T031");
		tests.put("205", "205|CURREL|T046");
		tests.put("206", "206|CURREL|T037");
		tests.put("207", "207|CURREL|T037");
		tests.put("208", "208|CURREL|GEOG");
		tests.put("209", "209|CURREL|DISO");
		tests.put("210", "210|CURREL|");
		tests.put("211", "211|CURREL|T046|T152|T037");
		tests.put("212", "212|CURREL|");
		tests.put("213", "213|CURREL|T172");
		tests.put("214", "214|CURREL");
		tests.put("215", "215|CURREL|T151");
		tests.put("216", "216|CURREL|T151|T139");
		tests.put("217", "217|CURREL|T151");
		tests.put("218", "218|CURREL|T143");
		tests.put("219", "219|CURREL|T166");
		tests.put("220", "220|CURREL|T151|T186|T139");
		tests.put("221", "221|CURREL|");
		tests.put("301", "301|CURREL|C0000603");
		tests.put("302", "302|CURREL|C0074722|2010AA");
		tests.put("303", "303|CURREL|C0066997|1993AA|");
		tests.put("304", "304|CURREL|C0000258||");
		tests.put("305", "305|CURREL|C0000258");
		tests.put("306", "306|CURREL|A0000230");
		tests.put("307", "307|CURREL|?lui?");  // ND
		tests.put("308", "308|CURREL|?lui?");  // ND
		tests.put("309", "309|CURREL|A0005183");
		tests.put("310", "310|CURREL|C0000039|SNOMEDCT||");
		tests.put("311", "311|CURREL|L1970625|");
		tests.put("312", "312|CURREL|S0129521|");
		tests.put("313", "313|CURREL|DeletedLUI|????");  //ND
		tests.put("314", "314|CURREL|DeletedSUI|????");  // ND

		//////////////////////////////////////////////
		// get cocnept
		//////////////////////////////////////////////
		tests.put("401", "401|CURREL|code|V-SNOMEDCT_2012_01_31|exact");
		tests.put("401a", "401|CURREL|sourceConcept|385093006|exact");            // Y
		tests.put("401b", "401|CURREL|sourceDescriptor|D006332|exact");           // Y
		tests.put("401c", "401|CURREL|sourceAui|2630733018|exact");               // Y
		tests.put("401d", "401|CURREL|aui|A0479264|exact");                       // Y
		tests.put("401e", "401|CURREL|aui|e14210f2143d9a5e701862cbdc37c516|exact");  // Y
		tests.put("401f", "401|CURREL|semanticType|Devices|exact");   // NOT WRK

		tests.put("401g", "401|CURREL|atom|broken hip|exact");                       // Y
		tests.put("401h", "401|CURREL|atom|fibro|approximate");                    // Y
		tests.put("401i", "401|CURREL|atom|brosis|leftTruncation");                // Y
		tests.put("401j", "401|CURREL|atom|fibro|rightTruncation");                // Y
		tests.put("401k", "401|CURREL|atom|fibroblast^growth|words");              // Y
		tests.put("401l", "401|CURREL|atom|fibrosarcoma^adult|normalizedWords");   // Y
		tests.put("401m", "401|CURREL|atom|adult fibrosarcoma|normalizedString");  // Y



		//////////////////////////////////////////////
		// get atom
		//////////////////////////////////////////////
		tests.put("402", "402|CURREL|concept|C0004903|exact");                      // Y
		tests.put("402a", "402|CURREL|sourceAui|2630733018|exact");                  // Y
		tests.put("402b", "402|CURREL|aui|A0479264|exact");                          // Y
		tests.put("402c", "402|CURREL|aui|e14210f2143d9a5e701862cbdc37c516|exact");  // Y
		tests.put("402d", "402|CURREL|code|24126|exact");		                      // Y
		tests.put("402e", "402|CURREL|sourceConcept|385093006|exact");               // Y
		tests.put("402f", "402|CURREL|sourceDescriptor|D008636|exact");              // Y

		tests.put("402g", "402|CURREL|atom|pancreatitis|exact");                         // Y
		tests.put("402h", "402|CURREL|atom|fibroses|approximate");                   // Y
		tests.put("402i", "402|CURREL|atom|brosis|leftTruncation");                  // Y
		tests.put("402j", "402|CURREL|atom|fibro|rightTruncation");                  // Y
		tests.put("402k", "402|CURREL|atom|fibroblast^growth|words");                // Y
		tests.put("402l", "402|CURREL|atom|fibrosarcoma^adult|normalizedWords");     // Y
		tests.put("402m", "402|CURREL|atom|adult fibrosarcoma|normalizedString");    // Y



		//////////////////////////////////////////////
		// get code
		//////////////////////////////////////////////
		tests.put("403", "403|CURREL|concept|C0004903|exact");                      // Y
		tests.put("403a", "403|CURREL|sourceAui|2630733018|exact");                  // Y
		tests.put("403b", "403|CURREL|aui|A0479264|exact");                          // Y
		tests.put("403c", "403|CURREL|aui|e14210f2143d9a5e701862cbdc37c516|exact");  // Y
		tests.put("403d", "403|CURREL|sourceConcept|88919005|exact");                // Y
		tests.put("403e", "403|CURREL|sourceDescriptor|D005346|exact");              // Y

		tests.put("403f", "403|CURREL|atom|fibrosis|exact");                         // Y
		tests.put("403g", "403|CURREL|atom|fibroses|approximate");                   // Y
		tests.put("403h", "403|CURREL|atom|brosis|leftTruncation");                  // Y
		tests.put("403i", "403|CURREL|atom|fibro|rightTruncation");                  // Y
		tests.put("403j", "403|CURREL|atom|fibroblast^growth|words");                // Y
		tests.put("403k", "403|CURREL|atom|fibrosarcoma^adult|normalizedWords");     // Y
		tests.put("403l", "403|CURREL|atom|adult fibrosarcoma|normalizedString");    // Y



		//////////////////////////////////////////////
		// get scui
		//////////////////////////////////////////////
		tests.put("404", "404|CURREL|concept|C0004057|exact");                      // Y
		tests.put("404a", "404|CURREL|sourceAui|2630733018|exact");                  // Y
		tests.put("404b", "404|CURREL|aui|A0399181|exact");                          // Y
		tests.put("404c", "404|CURREL|aui|efe2e4a96515b99f2bf9d686ae105fff|exact");  // Y
		tests.put("404d", "404|CURREL|code|24126|exact");                            // Y
		tests.put("404e", "404|CURREL|sourceDescriptor|D005346|exact");              // Y

		tests.put("404f", "404|CURREL|atom|closed fracture of hip|exact");                         // Y
		tests.put("404g", "404|CURREL|atom|broken hip|approximate");                   // Y
		tests.put("404h", "404|CURREL|atom|nzyme|leftTruncation");                   // Y
		tests.put("404i", "404|CURREL|atom|fibro|rightTruncation");                  // Y
		tests.put("404j", "404|CURREL|atom|closed fracture of hip|words");                // Y
		tests.put("404k", "404|CURREL|atom|closed fracture of hip|normalizedWords");     // Y
		tests.put("404l", "404|CURREL|atom|broken hip|normalizedString");    
		tests.put("404m", "404|CURREL|code|363662004|exact");                            // Y




		//////////////////////////////////////////////
		// get sdui
		//////////////////////////////////////////////
		tests.put("405", "405|CURREL|concept|C0004903|exact");                      // Y
		tests.put("405a", "405|CURREL|sourceAui|2630733018|exact");            // NO DATA
		tests.put("405b", "405|CURREL|aui|A0399181|exact");                          // Y
		tests.put("405c", "405|CURREL|aui|9d0b27296e024fa2336cdb1d53ddfa0d|exact");  // Y
		tests.put("405d", "405|CURREL|code|D000103|exact");                          // Y
		tests.put("405e", "405|CURREL|sourceConcept|M0000156|exact");                // Y

		tests.put("405f", "405|CURREL|atom|aspirin|exact");                         // Y
		tests.put("405g", "405|CURREL|atom|fibroses|approximate");                   // Y
		tests.put("405h", "405|CURREL|atom|brosis|leftTruncation");                  // Y
		tests.put("405i", "405|CURREL|atom|fibro|rightTruncation");                  // Y
		tests.put("405j", "405|CURREL|atom|fibroblast^growth|words");                // Y
		tests.put("405k", "405|CURREL|atom|fibrosarcoma^adult|normalizedWords");     // Y
		tests.put("405l", "405|CURREL|atom|adult fibrosarcoma|normalizedString");    // Y

	}

	public static void main(String[] args) {
		try {
			// args:
			//   1: username
			//   2: password
			//   3: testcase
			//   4: umls release
			//
			// if arg3 (testcase) is 0, take the next arg as input.
			// ex: prog un pw 0 "1|2010AA|C0221192" would get the supplied concept.
			
			// process arguments
			if (args.length < 3) {
				out.println("Usage prog username password testcase [umlsRelease]");
				return;
			}

			Ws_Test_B wst = new Ws_Test_B(args[0],args[1]);
			if (args[0].equals("0")) {
				wst.doRec("1", args[3]);
			} else {
				String nrel = "2011AB";
				if (args.length > 3) { nrel = args[3]; }
				wst.doRec("1", tests.get(args[2]).replace("CURREL", nrel)); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
