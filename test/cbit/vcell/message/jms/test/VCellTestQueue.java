package cbit.vcell.message.jms.test;

import cbit.vcell.message.VCellQueue;

public class VCellTestQueue extends VCellQueue {

	public final static VCellQueue JimQueue = new VCellQueue("JimQueue");

	public VCellTestQueue(String queueName) {
		super(queueName);
	}

}
