package cbit.vcell.export.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.User;
import cbit.vcell.server.*;
import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (4/3/2001 3:01:34 PM)
 * @author: Ion Moraru
 */
public class JobRequest {
	public static final int EXPORT_JOB = 1000;
	private static Random random = new Random();
	private User user = null;
	private long jobID;
	private int jobType;
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:09:30 PM)
 * @param user cbit.vcell.server.User
 * @param jobType int
 */
private JobRequest(User user, int jobType) {
	this.user = user;
	this.jobType = jobType;
	this.jobID = random.nextInt() + 5000000000L;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:48:06 PM)
 * @return cbit.vcell.export.server.JobRequest
 * @param user cbit.vcell.server.User
 */
public static JobRequest createExportJobRequest(User user) {
	if (user == null) {
		throw new NullPointerException("User cannot be null");
	} else {
		return new JobRequest(user, EXPORT_JOB);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 4:21:34 PM)
 * @return long
 */
public long getJobID() {
	return jobID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 4:21:59 PM)
 * @return int
 */
public int getJobType() {
	return jobType;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 4:22:26 PM)
 * @return cbit.vcell.server.User
 */
public User getUser() {
	return user;
}
}
