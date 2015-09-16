package org.vcell.stochtest;

import org.vcell.util.document.KeyValue;

public class StochtestRun {
	public static enum StochtestRunStatus {
		none,
		waiting,
		accepted,
		complete,
		failed
	}
	public KeyValue key;
	public Stochtest stochtest;
	public StochtestMathType parentMathType;
	public StochtestMathType mathType;
	public StochtestRun.StochtestRunStatus status;
	public String errmsg;
	public String conclusion;
	public String exclude;
	public String networkGenProbs;
}