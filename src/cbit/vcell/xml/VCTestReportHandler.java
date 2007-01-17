package cbit.vcell.xml;
import java.util.ArrayList;
import cbit.vcell.vcml.JDOMTreeWalker;

import org.jdom.Element;
import org.jdom.filter.ElementFilter;

import java.util.Date;

/**
A utility class for the VCRoundTripTest class. Its an XML handler (reader and writer) for the simple report format for the test
results. The structure of the report is:
<VCMLTest Name=test_name Date=xxx>
   <UserReport ID=userID TotalCount=xxx DBFail=xxx XMLWriteFail=xxx XMLReadFail=xxx RTFail=xxx>
      <FailedModel ID=modelID Name=modelName Type=Bio_or_Math FType=DBFail_XMLFail_RTFail ExceptionText=xxx>
         <LostE ID=xxx Xpath=xxx/>
         <LostT ID=xxx Xpath=xxx Value=xxx/>
         <LostA ID=xxx Xpath=xxx Name=xxx Value=xxx />
      </FailedModel>
   </UserReport>
</VCMLTest>
 * Creation date: (3/15/2004 1:33:38 PM)
 * @author: Rashad Badrawi
 */
public class VCTestReportHandler {

	public static final String DEF_REPORT_NAME = "VC_RT";
	public static final String MODE_WRITE = "mw";
	public static final String MODE_READ = "mr";
	//xml elements
	private static final String VCML_TEST = "VCMLTest";
	private static final String USER_REPORT = "UserReport";
	private static final String FAILED_MODEL = "FailedModel";
	private static final String LOST_E = "LostE";
	private static final String LOST_A = "LostA";
	private static final String LOST_T = "LostT";
	//xml attributes
	private static final String name = XMLTags.NameAttrTag;
	private static final String id = "ID";
	private static final String type = "Type";
	private static final String fType = "FailType";
	private static final String date = "Date";
	private static final String total_cnt = "TotalCount";
	private static final String db_fail = "DBFail";
	private static final String xml_write_fail = "XMLWriteFail";
	private static final String xml_read_fail = "XMLReadFail";
	private static final String roundtrip_fail = "RTFail";
	private static final String exc_text = "ExceptionText";
	private static final String xpath = "Xpath";
	private static final String value = "Value";

	private Element root;


public VCTestReportHandler(String mode) {

	this(mode, DEF_REPORT_NAME);
}


	public VCTestReportHandler(String mode, String testName) {

		if (mode.equals(MODE_WRITE)) {
			root = new Element(VCML_TEST);
			root.setAttribute(name, testName + "_" + new Date().toString());
			root.setAttribute(date, new Date().toString());
		} else if (mode.equals(MODE_READ)) {
			System.out.println("Option: " + MODE_READ + " not supported yet");
		}
	}


	public String displayReport() {

		StringBuffer buf = new StringBuffer();

		
		return buf.toString();
	}


	private String [] getAllFailedModels(String attName) {

		ArrayList list = new ArrayList();
		
		ArrayList fmList = new ArrayList(root.getChildren(FAILED_MODEL));
		for (int i = 0; i < fmList.size(); i++) {
			Element fm = (Element)fmList.get(i);
			list.add(fm.getAttributeValue(attName));
		}

		return (String [])list.toArray(new String [list.size()]);
	}


	protected String [] getAllFailedModelsByID() {

		return getAllFailedModels(id);		
	}


	protected String [] getAllFailedModelsByName() {

		return getAllFailedModels(name);
	}


	protected String getException(String modelID) {

		Element fm = getFailedModel(modelID);
		return fm.getAttributeValue(exc_text);
	}


	protected Element getFailedModel(String mID) {

		Element fm = null;

		ArrayList fmList = new ArrayList(root.getChildren(FAILED_MODEL));
		for (int i = 0; i < fmList.size(); i++) {
			fm = (Element)fmList.get(i);
			if (mID.equals(fm.getAttributeValue(id))) {
				break;
			} else {
				fm = null;
			}
		}
		if (fm == null) {
			System.err.println("No Model with the ID: " + mID + " was found");
			return null;
		} else {
			return fm;
		}
	}


	protected Element getReport() { return root; }


	protected Element setFailedModel(String modelID, String modelName, String modelType, String failType, String exceptString) {

		if (modelID == null || modelID.length() == 0 || modelName == null || modelName.length() == 0 || modelType == null ||
			modelType.length() == 0)
			throw new IllegalArgumentException("Invalid argument list for the failed model: " + modelID + " " + modelName + " " + 
												modelType);
		JDOMTreeWalker walker = new JDOMTreeWalker(root, new ElementFilter(FAILED_MODEL));
		Element fmodel = walker.getMatchingElement(id, modelID);
		if (fmodel == null) {
			fmodel = new Element(FAILED_MODEL);
			fmodel.setAttribute(id, modelID);
			fmodel.setAttribute(name, modelName);
			fmodel.setAttribute(type, modelType);
			root.addContent(fmodel);
		}
		if (failType != null && failType.length() > 0) {
			fmodel.setAttribute(fType, failType);
		}
		if (exceptString != null && exceptString.length() > 0) {
			fmodel.setAttribute(exc_text, exceptString);
		}

		return fmodel;
	}


	protected void setLostAttribute(Element fModel, String pID, String path, String attName, String attValue) {

		Element lostA = new Element(LOST_A);
		lostA.setAttribute(id, pID);
		lostA.setAttribute(xpath, path);
		lostA.setAttribute(name, attName);
		lostA.setAttribute(value, attValue);
		fModel.addContent(lostA);
	}


	protected void setLostElement(Element fModel, String parentID, String parentPath) {

		Element lostE = new Element(LOST_E);
		lostE.setAttribute(id, parentID);
		lostE.setAttribute(xpath, parentPath);
		fModel.addContent(lostE);
	}


	protected void setLostText(Element fModel, String pID, String path, String text) {

		Element lostT = new Element(LOST_T);
		lostT.setAttribute(id, pID);
		lostT.setAttribute(xpath, path);
		lostT.setAttribute(value, text);

		fModel.addContent(lostT);
	}


	protected void setUserReport(String userID, int total, int DBFail, int XMLWriteFail, int XMLReadFail, int roundTripFail) {

		JDOMTreeWalker walker = new JDOMTreeWalker(root, new ElementFilter(USER_REPORT));
		Element user = walker.getMatchingElement(id, userID);
		if (user == null) {
			user = new Element(USER_REPORT);
			root.addContent(user);
		}
		user.setAttribute(id, userID);
		user.setAttribute(total_cnt, String.valueOf(total));
		user.setAttribute(db_fail, String.valueOf(DBFail));
		user.setAttribute(xml_write_fail, String.valueOf(XMLWriteFail));
		user.setAttribute(xml_read_fail, String.valueOf(XMLReadFail));
		user.setAttribute(roundtrip_fail, String.valueOf(roundTripFail));
	}
}