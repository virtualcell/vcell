package cbit.vcell.message.server.jmx;

public interface BootstrapMXBean {

	public static final String jmxObjectName = "org.vcell:type=Bootstrap";

	int getConnectedUserCount();

	String getConnectedUserNames();

}
