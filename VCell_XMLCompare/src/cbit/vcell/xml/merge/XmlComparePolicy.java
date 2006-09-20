package cbit.vcell.xml.merge;

public interface XmlComparePolicy {
	public String getKeyAttributeName(String elementName);
	public boolean ignoreElement(String elementName);
	public boolean ignoreAttribute(String elementName, String attributeName);
	public String getDefaultKeyAttributeName();
	public boolean skipOrdering(String elementName);
}
