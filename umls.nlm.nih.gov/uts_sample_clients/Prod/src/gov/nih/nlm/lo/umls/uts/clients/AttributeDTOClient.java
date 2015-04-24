package gov.nih.nlm.lo.umls.uts.clients;

import static java.lang.System.out;

import java.awt.List;
import java.util.ArrayList;

import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.security.*;

public class AttributeDTOClient {
	private static String username = "";
    private static String password = ""; 
    static String umlsRelease = "2012AB";
	static String serviceName = "http://umlsks.nlm.nih.gov";
    
static UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
static UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

    
    public AttributeDTOClient(String username, String password) {
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

            gov.nih.nlm.uts.webservice.content.Psf myPsf = new gov.nih.nlm.uts.webservice.content.Psf();
            java.util.List<AttributeDTO> myAttributes = new ArrayList<AttributeDTO>();
            AttributeDTOClient AttDTOClient = new AttributeDTOClient(args[0],args[1]);
            
        	String method = args[2];
        	
            switch (method) {
            case "getConceptAttributes": myAttributes = utsContentService.getConceptAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C2711988",myPsf);
            break;
            case "getRelationAttributes": myAttributes = utsContentService.getRelationAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "R74224153",myPsf);
            break;
            case "getCodeAttributes": myAttributes = utsContentService.getCodeAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "10042784","MDR",myPsf);
            break;
            case "getSourceConceptAttributes": myAttributes = utsContentService.getSourceConceptAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "102735002","SNOMEDCT",myPsf);
            break;
            case "getSourceDescriptorAttributes": myAttributes = utsContentService.getSourceDescriptorAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "D015060","MSH",myPsf);
            break;
            case "getAtomAttributes": myAttributes = utsContentService.getAtomAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "A7755565",myPsf);
            break;
            case "getMapSetAttributes": myAttributes = utsContentService.getMapSetAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "C2963202",myPsf);
            break;
            case "getSubsetMemberAttributes": myAttributes = utsContentService.getSubsetMemberAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "AT139571931",myPsf);
            break;
            case "getSubsetAttributes": myAttributes = utsContentService.getSubsetAttributes(securityService.getProxyTicket(ticketGrantingTicket(), serviceName), umlsRelease, "IC1321498",myPsf);
            break;
            default: out.println("Unrecognized input ");
        	break; 
            }
            
            for (int i = 0; i < myAttributes.size(); i++) {

            AttributeDTO myAttributeDTO = myAttributes.get(i);
            String attributeName = myAttributeDTO.getName();
            String attributeValue = myAttributeDTO.getValue();
            
            System.out.println("attributeName:"+attributeName+"|attributeValue:"+attributeValue);
            }
            
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}

/*            myAttributes = utsContentService.getConceptAttributes(singleUseTicket, umlsRelease, "C2711988",myPsf);
for (int i = 0; i < myAttributes.size(); i++) {

AttributeDTO myAttributeDTO = myAttributes.get(i);
String attributeName = myAttributeDTO.getName();
String attributeValue = myAttributeDTO.getValue();

System.out.println("attributeName:"+attributeName+"|attributeValue:"+attributeValue);
}
//IS RETurnign these methods enogh????????????
myAttributes = utsContentService.getRelationAttributes(singleUseTicket, umlsRelease, "R74224153",myPsf);
for (int i = 0; i < myAttributes.size(); i++) {

AttributeDTO myAttributeDTO = myAttributes.get(i);
String attributeName = myAttributeDTO.getName();
String attributeValue = myAttributeDTO.getValue();

System.out.println("attributeName:"+attributeName+"|attributeValue:"+attributeValue);
}

myAttributes = utsContentService.getCodeAttributes(singleUseTicket, umlsRelease, "10042784","MDR",myPsf);
for (int i = 0; i < myAttributes.size(); i++) {

AttributeDTO myAttributeDTO = myAttributes.get(i);
String attributeName = myAttributeDTO.getName();
String attributeValue = myAttributeDTO.getValue();

System.out.println("attributeName:"+attributeName+"|attributeValue:"+attributeValue);
}


myAttributes = utsContentService.getSourceConceptAttributes(singleUseTicket, umlsRelease, "102735002","SNOMEDCT",myPsf);
for (int i = 0; i < myAttributes.size(); i++) {

AttributeDTO myAttributeDTO = myAttributes.get(i);
String attributeName = myAttributeDTO.getName();
String attributeValue = myAttributeDTO.getValue();

System.out.println("attributeName:"+attributeName+"|attributeValue:"+attributeValue);
}


myAttributes = utsContentService.getSourceDescriptorAttributes(singleUseTicket, umlsRelease, "D015060","MSH",myPsf);
for (int i = 0; i < myAttributes.size(); i++) {

AttributeDTO myAttributeDTO = myAttributes.get(i);
String attributeName = myAttributeDTO.getName();
String attributeValue = myAttributeDTO.getValue();

System.out.println("attributeName:"+attributeName+"|attributeValue:"+attributeValue);
}

myAttributes = utsContentService.getAtomAttributes(singleUseTicket, umlsRelease, "A7755565",myPsf);
for (int i = 0; i < myAttributes.size(); i++) {

AttributeDTO myAttributeDTO = myAttributes.get(i);
String attributeName = myAttributeDTO.getName();
String attributeValue = myAttributeDTO.getValue();

System.out.println("attributeName:"+attributeName+"|attributeValue:"+attributeValue);
}

myAttributes = utsContentService.getSubsetMemberAttributes(singleUseTicket, umlsRelease, "AT139571931",myPsf);
for (int i = 0; i < myAttributes.size(); i++) {

AttributeDTO myAttributeDTO = myAttributes.get(i);
String attributeName = myAttributeDTO.getName();
String attributeValue = myAttributeDTO.getValue();

System.out.println("attributeName:"+attributeName+"|attributeValue:"+attributeValue);
}


myAttributes = utsContentService.getMapSetAttributes(singleUseTicket, umlsRelease, "C2963202",myPsf);
for (int i = 0; i < myAttributes.size(); i++) {

AttributeDTO myAttributeDTO = myAttributes.get(i);
String attributeName = myAttributeDTO.getName();
String attributeValue = myAttributeDTO.getValue();

System.out.println("attributeName:"+attributeName+"|attributeValue:"+attributeValue);
}*/
