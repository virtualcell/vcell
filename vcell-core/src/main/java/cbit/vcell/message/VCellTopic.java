package cbit.vcell.message;

import java.util.Objects;

import cbit.vcell.resource.PropertyLoader;

public class VCellTopic implements VCDestination {
	public final static VCellTopic ClientStatusTopic = new VCellTopic(PropertyLoader.jmsClientStatusTopic, null);
	public final static VCellTopic DaemonControlTopic = new VCellTopic(PropertyLoader.jmsDaemonControlTopic, null);
	public final static VCellTopic ServiceControlTopic = new VCellTopic(PropertyLoader.jmsServiceControlTopic, null);

	private final String topicName;
	private VCellTopic(String propertyName, String topicName){
		if (topicName == null) {
			topicName = PropertyLoader.getRequiredProperty(propertyName);
			Objects.requireNonNull(topicName);
		}
		this.topicName = topicName;
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