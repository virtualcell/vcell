package cbit.vcell.message.server.htc;

import cbit.vcell.message.server.htc.HtcProxy.HtcJobInfo;
import cbit.vcell.server.HtcJobID;

public class HtcJobNotFoundException extends HtcException {
	private final HtcJobID id;
	private final String jobName;

	public HtcJobNotFoundException(String message, HtcJobID id) {
		super(message);
		this.id = id;
		this.jobName = null;
	}

	public HtcJobNotFoundException(String message, HtcJobInfo info) {
		super(message);
		this.id = info.getHtcJobID();
		this.jobName = info.getJobName();
	}

	public HtcJobID getId() {
		return id;
	}
	public String getJobName() {
		return jobName;
	}
}
