package cbit.vcell.message;

import cbit.vcell.resource.PropertyLoader;

public class VCellTopic implements VCDestination {
	public final static VCellTopic ClientStatusTopic = new VCellTopic(PropertyLoader.jmsClientStatusTopic, "clientStatus");
	public final static VCellTopic ServiceControlTopic = new VCellTopic(PropertyLoader.jmsServiceControlTopic, "serviceControl");

	private final String topicName;
	private VCellTopic(String propertyName, String defaultTopicName){
		String topicNameProp = PropertyLoader.getProperty(propertyName, null);
		if (topicNameProp != null) {
			topicName = topicNameProp;
		}else{
			topicName = defaultTopicName;
		}
	}
	
	public VCellTopic(String topicName){
		this(null,topicName);
	}
	
	public String getName() {
			return topicName;
	}
		
	public boolean equals(Object obj){
		if (obj instanceof VCellTopic){
			VCellTopic other = (VCellTopic)obj;
			return other.getName().equals(getName());
		}
		return false;
	}
	
	public int hashCode(){
		return getName().hashCode();
	}
	
}