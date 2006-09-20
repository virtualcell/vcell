package cbit.vcell.xml.merge;

public class DefaultXmlComparePolicy implements XmlComparePolicy {

	public String getKeyAttributeName(String elementName) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean ignoreAttribute(String elementName, String attributeName) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean ignoreElement(String elementName) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getDefaultKeyAttributeName() {
		// TODO Auto-generated method stub
		return "id";
	}

	public boolean skipOrdering(String elementName) {
		// TODO Auto-generated method stub
		return false;
	}

}
