package cbit.vcell.message;

import org.vcell.util.PropertyLoader;

public class VCellTopic implements VCDestination {
	public final static VCellTopic ClientStatusTopic = new VCellTopic(PropertyLoader.jmsClientStatusTopic, null);
	public final static VCellTopic DaemonControlTopic = new VCellTopic(PropertyLoader.jmsDaemonControlTopic, null);
	public final static VCellTopic ServiceControlTopic = new VCellTopic(PropertyLoader.jmsServiceControlTopic, null);

	private String vcellPropertyName;
	private String topicName;
	private VCellTopic(String propertyName, String topicName){
		this.vcellPropertyName = propertyName;
		this.topicName = topicName;
	}
	
	public VCellTopic(String topicName){
		this(null,topicName);
	}
	
	public String getName() {
		if (topicName!=null){
			return topicName;
		}
		return PropertyLoader.getRequiredProperty(vcellPropertyName);
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