package cbit.xml.merge.gui;

public interface XmlComparePolicy {
	public String getKeyAttributeName(String elementName);
	public boolean ignoreElement(String elementName);
	public boolean ignoreAttribute(String elementName, String attributeName);
}
