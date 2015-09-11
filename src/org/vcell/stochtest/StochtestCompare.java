package org.vcell.stochtest;

import org.vcell.util.document.KeyValue;


public class StochtestCompare {
	public static enum StochtestCompareStatus {
		none,
		waiting,
		accepted,
		not_verydifferent,
		verydifferent,
		failed
	}
	public KeyValue key;
	public KeyValue stochtestRun1ref;
	public KeyValue stochtestRun2ref;
	public StochtestCompareStatus status;
	public String errmsg;
	public String results;
	public String conclusion;
}