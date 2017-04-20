package cbit.vcell.messaging.db;

public class WaitingStateInfo extends StateInfo {
	public final int myQueueOrdinal;
	public final int globalQueueOrdinal;
	public WaitingStateInfo(int myQueueOrdinal, int globalQueueOrdinal){
		this.myQueueOrdinal = myQueueOrdinal;
		this.globalQueueOrdinal = globalQueueOrdinal;
	}
}