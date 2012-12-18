package cbit.vcell.message.server.jmx;

import java.util.Date;

public interface VCellServiceMXBean {

	public static final String jmxObjectName = "org.vcell:type=VCellService";

	String getHostName();

	int getServiceOrdinal();

	String getServiceName();

	Date getStartupDate();
	
	void setTrace(boolean bTrace);

	boolean getTrace();

}
