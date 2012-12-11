package cbit.vcell.message.server.jmx;

import java.util.Date;

import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.mongodb.VCMongoMessage;

public class VCellServiceMXBeanImpl implements VCellServiceMXBean {
	
	@Override
	public String getHostName(){
		return ManageUtils.getHostName();
	}
	
	@Override
	public int getServiceOrdinal(){
		return VCMongoMessage.getServiceOrdinal();
	}
	
	@Override
	public String getServiceName(){
		return VCMongoMessage.getServiceName().name();
	}
	
	@Override
	public Date getStartupDate(){
		return new Date(VCMongoMessage.getServiceStartupTime());
	}
	
	@Override
	public void setTrace(boolean bTrace){
		VCMongoMessage.bTrace = bTrace;
	}
	
	@Override
	public boolean getTrace(){
		return VCMongoMessage.bTrace;
	}

}
