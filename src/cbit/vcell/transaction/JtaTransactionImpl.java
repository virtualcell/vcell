package cbit.vcell.transaction;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.transaction.*;
import javax.transaction.xa.*;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (7/23/2003 4:12:01 PM)
 * @author: Fei Gao
 */
public class JtaTransactionImpl implements Transaction {

	private JtaXidImpl xid = null;

	private Map branches = Collections.synchronizedMap(new HashMap());

	private Map activeBranches = Collections.synchronizedMap(new HashMap());

	private List enlistedResources = Collections.synchronizedList(new ArrayList());
	private List nonXAResources = Collections.synchronizedList(new ArrayList());

	private Map suspendedResources = Collections.synchronizedMap(new HashMap());

	private int status = Status.STATUS_ACTIVE;

	private List synchronizationObjects = Collections.synchronizedList(new ArrayList());
	private int branchCounter = 1;

	private static int globalCreatedTransactions = 0;

	private int currentTransactionNumber = 0;

	private String currentThreadName = null;

	private javax.transaction.TransactionManager transactionManager = null;

/**
 * Constructor.
 */
public JtaTransactionImpl(TransactionManager tm) {
	// Generate the transaction id
	globalCreatedTransactions ++;
	currentTransactionNumber = globalCreatedTransactions;
	currentThreadName = Thread.currentThread().getName();
	xid = new JtaXidImpl((currentThreadName + "-" + System.currentTimeMillis() + "-" + currentTransactionNumber).getBytes(), new byte[0]);
	this.transactionManager = tm;
}

/**
 * Complete the transaction represented by this Transaction object.
 *
 * @exception RollbackException Thrown to indicate that the transaction
 * has been rolled back rather than committed.
 * @exception HeuristicMixedException Thrown to indicate that a heuristic
 * decision was made and that some relevant updates have been committed
 * while others have been rolled back.
 * @exception HeuristicRollbackException Thrown to indicate that a
 * heuristic decision was made and that some relevant updates have been
 * rolled back.
 * @exception SecurityException Thrown to indicate that the thread is not
 * allowed to commit the transaction.
 * @exception IllegalStateException Thrown if the current thread is not
 * associated with a transaction.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public void commit() throws	RollbackException, HeuristicMixedException, HeuristicRollbackException,	SecurityException, IllegalStateException, SystemException {

	System.out.println(this +"  COMMIT ");

	if (status == Status.STATUS_MARKED_ROLLBACK) {
		rollback();
		return;
	}

	// Check status ACTIVE
	if (status != Status.STATUS_ACTIVE)
		throw new IllegalStateException();

	// Call synchronized objects beforeCompletion
	Iterator syncIter = synchronizationObjects.iterator();
	while (syncIter.hasNext()) {
		Synchronization sync = (Synchronization) syncIter.next();
		sync.beforeCompletion();
	}

	List exceptions = Collections.synchronizedList(new ArrayList());
	boolean fail = false;

	switch (enlistedResources.size()) {
		case 0: {
			// only commit nonXA
			if (nonXAResources.size() > 0) {
				try {
					do_commitNonXA();
					status = Status.STATUS_COMMITTED;
				} catch (Throwable e) {				
					fail = false;
					try {
						do_rollbackNonXA();
					} catch (Throwable ex) {
						exceptions.add(ex);
						fail = true;
					}
					status = Status.STATUS_ROLLEDBACK;
				}

			}
			break;
		} // case 0

		case 1:	{
			//System.out.print("1-Phase commit started....");

			// One phase commit		
			try {
				do_delist();
			} catch (SystemException e) {
				exceptions.add(e);
				fail = true;
				status = Status.STATUS_MARKED_ROLLBACK;
			}
				
			if (!fail) {
				try {
					do_commitNonXA();
					do_commit(true);
				} catch (Throwable e) {					
					if (e instanceof XAException) {
						System.out.println("1-Phase commit XAException: " + getXAErrorCode((XAException)e));
					}					
					exceptions.add(e);
					fail = true;				
				}

				status = Status.STATUS_COMMITTED;
			} else {
				try {
					fail = false;
					do_rollbackNonXA();
					do_rollback();
				} catch (Throwable e) {					
					if (e instanceof XAException) {
						System.out.println("1-Phase rollback XAException: " + getXAErrorCode((XAException)e));
					}
					exceptions.add(e);
					fail = true;				
				}
				status = Status.STATUS_ROLLEDBACK;
			}

			//System.out.println("1-phase commit ended");
			break;
		} // case 1

		default: {
			// two phase commit						
			// end each enlisted resource	
			try {
				// Preparing the resource manager using its branch xid
				do_delist();
			} catch (SystemException e) {
				exceptions.add(e);
				fail = true;
				status = Status.STATUS_MARKED_ROLLBACK;
			}

			// Prepare each enlisted resource
			if (!fail) {
				try {
					do_prepare();
				} catch (XAException ex) {
					System.out.println("2-Phase commit XAException: " + getXAErrorCode((XAException)ex));
					fail = true;
					exceptions.add(ex);
					status = Status.STATUS_MARKED_ROLLBACK;
				}
			}		
		
			if (!fail) {
				try {
					do_commitNonXA();
				} catch (Throwable e) {
					exceptions.add(e);
					fail = true;				
				}
				status = Status.STATUS_COMMITTED;
			}

			if (!fail) {
				// Commit each enlisted resource
				status = Status.STATUS_PREPARED;
				status = Status.STATUS_COMMITTING;
				
				//System.out.print("2-Phase commit started....");
				
				try {
					do_commit(false);	
				} catch (XAException e) {
					System.out.println("2-Phase commit XAException: " + getXAErrorCode((XAException)e));
					exceptions.add(e);
					fail = true;
				}
				status = Status.STATUS_COMMITTED;
				//System.out.println("2-Phase commit ended");

			} else {
				System.out.print("2-Phase commit fail, rollback started....");
				//If fail, rollback
				status = Status.STATUS_ROLLING_BACK;
				fail = false;
				// Rolling back all the prepared (and unprepared) branches
				try {
					do_rollbackNonXA();
					do_rollback();
				} catch (Throwable e) {
					if (e instanceof XAException) {
						System.out.println("2-Phase rollback XAException: " + getXAErrorCode((XAException)e));
					}
					exceptions.add(e);
					fail = true;
				}
				status = Status.STATUS_ROLLEDBACK;
				//System.out.println("2-Phase rollback ended");
			}

			break;
		} //default

	}

	// Call synchronized objects afterCompletion
	syncIter = synchronizationObjects.iterator();
	while (syncIter.hasNext()) {
		Synchronization sync = (Synchronization) syncIter.next();
		sync.afterCompletion(status);
	}

	// Parsing exception and throwing an appropriate exception
	Iterator iter = exceptions.iterator();
	if (iter.hasNext()) {
		if (status == Status.STATUS_ROLLEDBACK) {
			if (!fail) {
				throw new RollbackException();
			} else {
				throw new HeuristicRollbackException();
			}
		}
		if (status == Status.STATUS_COMMITTED && fail) {
			throw new HeuristicMixedException();
		}
	}

}
/**
 * Delist the resource specified from the current transaction associated
 * with the calling thread.
 *
 * @param xaRes The XAResource object representing the resource to delist
 * @param flag One of the values of TMSUCCESS, TMSUSPEND, or TMFAIL
 * @exception IllegalStateException Thrown if the transaction in the
 * target object is inactive.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public boolean delistResource(XAResource xaRes, int flag) throws IllegalStateException, SystemException { 

	//System.out.println(this + "  DELIST " + xaRes);

	// Check status ACTIVE
	if (status != Status.STATUS_ACTIVE)
		throw new IllegalStateException();

	Xid xid = (Xid) activeBranches.get(xaRes);
	if (xid == null) {
		throw new IllegalStateException();
	}
	activeBranches.remove(xaRes);

	try {
		xaRes.end(xid, flag);
	} catch (XAException e) {
		e.printStackTrace(System.out);
		return false;
	}

	if (flag == XAResource.TMSUSPEND) {
		suspendedResources.put(xaRes, xid);
	}

	//System.out.println("Delisted ok(" + this + ") = " + xaRes + " xid: " + xid);

	return true;

}

/**
 * Complete the transaction represented by this Transaction object.
 *
 * @exception RollbackException Thrown to indicate that the transaction
 * has been rolled back rather than committed.
 * @exception HeuristicMixedException Thrown to indicate that a heuristic
 * decision was made and that some relevant updates have been committed
 * while others have been rolled back.
 * @exception HeuristicRollbackException Thrown to indicate that a
 * heuristic decision was made and that some relevant updates have been
 * rolled back.
 * @exception SecurityException Thrown to indicate that the thread is not
 * allowed to commit the transaction.
 * @exception IllegalStateException Thrown if the current thread is not
 * associated with a transaction.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public void do_commit(boolean onePhase) throws	XAException {

	Iterator iter = branches.keySet().iterator();
	while (iter.hasNext()) {
		Object key = iter.next();
		XAResource resourceManager = (XAResource) branches.get(key);
		if (onePhase) {
			resourceManager.commit((Xid) key, true);
		} else {
			resourceManager.commit((Xid) key, false);
		}
			
	}	
}

/**
 * Complete the transaction represented by this Transaction object.
 *
 * @exception RollbackException Thrown to indicate that the transaction
 * has been rolled back rather than committed.
 * @exception HeuristicMixedException Thrown to indicate that a heuristic
 * decision was made and that some relevant updates have been committed
 * while others have been rolled back.
 * @exception HeuristicRollbackException Thrown to indicate that a
 * heuristic decision was made and that some relevant updates have been
 * rolled back.
 * @exception SecurityException Thrown to indicate that the thread is not
 * allowed to commit the transaction.
 * @exception IllegalStateException Thrown if the current thread is not
 * associated with a transaction.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public void do_commitNonXA() throws SystemException {
	for (int i = 0; i < nonXAResources.size(); i++){
		NonXAResource res = (NonXAResource)nonXAResources.get(i);	
		res.commit();	
	}
}
/**
 * Delist the resource specified from the current transaction associated
 * with the calling thread.
 *
 * @param xaRes The XAResource object representing the resource to delist
 * @param flag One of the values of TMSUCCESS, TMSUSPEND, or TMFAIL
 * @exception IllegalStateException Thrown if the transaction in the
 * target object is inactive.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public void do_delist() throws SystemException { 
	Iterator iter = branches.keySet().iterator();
	// end each enlisted resource	
	while (iter.hasNext()) {
		Object key = iter.next();
		XAResource resourceManager = (XAResource) branches.get(key);
		// Preparing the resource manager using its branch xid
		delistResource(resourceManager, XAResource.TMSUCCESS);
	}
}
/**
 * Roll back the transaction associated with the current thread. When
 * this method completes, the thread becomes associated with no
 * transaction.
 *
 * @exception SecurityException Thrown to indicate that the thread is not
 * allowed to commit the transaction.
 * @exception IllegalStateException Thrown if the current thread is not
 * associated with a transaction.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public void do_prepare() throws XAException { 	
	Iterator iter = branches.keySet().iterator();
	while (iter.hasNext()) {
		Object key = iter.next();
		XAResource resourceManager = (XAResource) branches.get(key);
		// Preparing the resource manager using its branch xid
		resourceManager.prepare((Xid) key);
	}

}
/**
 * Roll back the transaction associated with the current thread. When
 * this method completes, the thread becomes associated with no
 * transaction.
 *
 * @exception SecurityException Thrown to indicate that the thread is not
 * allowed to commit the transaction.
 * @exception IllegalStateException Thrown if the current thread is not
 * associated with a transaction.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public void do_rollback() throws XAException {

	Iterator iter = branches.keySet().iterator();

	status = Status.STATUS_ROLLING_BACK;
	while (iter.hasNext()) {
		Xid xid = (Xid) iter.next();
		XAResource resourceManager = (XAResource) branches.get(xid);
		resourceManager.rollback(xid);
	}
}
/**
 * Roll back the transaction associated with the current thread. When
 * this method completes, the thread becomes associated with no
 * transaction.
 *
 * @exception SecurityException Thrown to indicate that the thread is not
 * allowed to commit the transaction.
 * @exception IllegalStateException Thrown if the current thread is not
 * associated with a transaction.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public void do_rollbackNonXA() throws SystemException {
	for (int i = 0; i < nonXAResources.size(); i++){
		NonXAResource res = (NonXAResource)nonXAResources.get(i);	
		res.rollback();	
	}
}
/**
 * Enlist the resource specified with the current transaction context of
 * the calling thread.
 *
 * @param xaRes The XAResource object representing the resource to delist
 * @return true if the resource was enlisted successfully; otherwise false.
 * @exception RollbackException Thrown to indicate that the transaction
 * has been marked for rollback only.
 * @exception IllegalStateException Thrown if the transaction in the
 * target object is in prepared state or the transaction is inactive.
 * @exception SystemException Thrown if the transaction manager
 * encounters an unexpected error condition.
 */
public boolean enlistNonXAResource(NonXAResource res) {
	//System.out.println("Enlist a non XA Resource");
	nonXAResources.add(res);		
	return true;
}
/**
 * Enlist the resource specified with the current transaction context of
 * the calling thread.
 *
 * @param xaRes The XAResource object representing the resource to delist
 * @return true if the resource was enlisted successfully; otherwise false.
 * @exception RollbackException Thrown to indicate that the transaction
 * has been marked for rollback only.
 * @exception IllegalStateException Thrown if the transaction in the
 * target object is in prepared state or the transaction is inactive.
 * @exception SystemException Thrown if the transaction manager
 * encounters an unexpected error condition.
 */
public boolean enlistResource(XAResource xaRes)	throws RollbackException, IllegalStateException, SystemException {

	//System.out.println(this + "  ENLIST " + xaRes);
	if (status == Status.STATUS_MARKED_ROLLBACK) {
		throw new RollbackException();
	}

	// Check status ACTIVE
	if (status != Status.STATUS_ACTIVE) {
		throw new IllegalStateException();
	}

	// Preventing two branches from being active at the same time on the
	// same resource manager
	Xid activeXid = (Xid) activeBranches.get(xaRes);
	if (activeXid != null) {
		return false;
	}
		
	boolean alreadyEnlisted = false;
	int flag = XAResource.TMNOFLAGS;

	Xid branchXid = (Xid) suspendedResources.get(xaRes);

	if (branchXid == null) {
		Iterator iter = enlistedResources.iterator();

		while (!alreadyEnlisted && iter.hasNext()) {
			XAResource resourceManager = (XAResource) iter.next();
			try {
				if (resourceManager.isSameRM(xaRes)) {
					System.out.println("Same Resource: " + xaRes + "," + resourceManager);

					// TMJOIN is not support , because TMJOIN is allowed only after XAResource.end is invoked

					//flag = XAResource.TMJOIN;
					// use the same branch Xid to join the existing branch
					//branchXid = (Xid)activeBranches.get(resourceManager);

					alreadyEnlisted = true;
				}
			} catch (XAException e) {				
				throw new SystemException(JtaTransactionImpl.getXAErrorCode(e));
			}
		}
		
		// use different branch Xid
		branchXid = this.xid.newBranch(branchCounter ++);
			
		//System.out.println("Creating new branch for " + xaRes + "with Xid " + branchXid);

	} else {
		alreadyEnlisted = true;
		flag = XAResource.TMRESUME;
		suspendedResources.remove(xaRes);
	}

	try {
		//System.out.println("Starting" + xaRes + " Branch: " + branchXid + ", Flag: " + getXAFlag(flag));
		xaRes.start(branchXid, flag);
		//System.out.println("End Starting" + xaRes + " Branch: " + branchXid + ", Flag: " + getXAFlag(flag));
	} catch (XAException e) {
		System.out.println("XAResource start Error code = " + getXAErrorCode(e));
		return false;
	}

	if (!alreadyEnlisted) {
		enlistedResources.add(xaRes);			
	}
		
	branches.put(branchXid, xaRes);		
	activeBranches.put(xaRes, branchXid);
		
	return true;
}
/**
 * Obtain the status of the transaction associated with the current thread.
 *
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 * @return The transaction status. If no transaction is associated with
 * the current thread, this method returns the Status.NoTransaction value.
 */
public int getStatus() throws SystemException {
	return status;
}
/**
 * Return a String representation of the error code contained in a
 * XAException.
 */
public static String getXAErrorCode(Throwable throww) {
	String result = null;
	if (throww instanceof XAException) {
		result = getXAErrorCode((XAException) throww);
	} else {
		StringWriter sw = new StringWriter();
		throww.printStackTrace(new PrintWriter(sw, true)); //autoFlush=true
		result = sw.toString();
	}
	return result;
}
/**
 * Return a String representation of the error code contained in a
 * XAException.
 */
public static String getXAErrorCode(XAException xae) {
	switch (xae.errorCode) {
		case XAException.XA_HEURCOM :
			{
				return "XA_HEURCOM";
			}
		case XAException.XA_HEURHAZ :
			{
				return "XA_HEURHAZ";
			}
		case XAException.XA_HEURMIX :
			{
				return "XA_HEURMIX";
			}
		case XAException.XA_HEURRB :
			{
				return "XA_HEURRB";
			}
		case XAException.XA_NOMIGRATE :
			{
				return "XA_NOMIGRATE";
			}
		case XAException.XA_RBBASE :
			{
				return "XA_RBBASE";
			}
		case XAException.XA_RBCOMMFAIL :
			{
				return "XA_RBCOMMFAIL";
			}
		case XAException.XA_RBDEADLOCK :
			{
				return "XA_RBBEADLOCK";
			}
		case XAException.XA_RBEND :
			{
				return "XA_RBEND";
			}
		case XAException.XA_RBINTEGRITY :
			{
				return "XA_RBINTEGRITY";
			}
		case XAException.XA_RBOTHER :
			{
				return "XA_RBOTHER";
			}
		case XAException.XA_RBPROTO :
			{
				return "XA_RBPROTO";
			}
		case XAException.XA_RBTIMEOUT :
			{
				return "XA_RBTIMEOUT";
			}
		case XAException.XA_RDONLY :
			{
				return "XA_RDONLY";
			}
		case XAException.XA_RETRY :
			{
				return "XA_RETRY";
			}
		case XAException.XAER_ASYNC :
			{
				return "XAER_ASYNC";
			}
		case XAException.XAER_DUPID :
			{
				return "XAER_DUPID";
			}
		case XAException.XAER_INVAL :
			{
				return "XAER_INVAL";
			}
		case XAException.XAER_NOTA :
			{
				return "XAER_NOTA";
			}
		case XAException.XAER_OUTSIDE :
			{
				return "XAER_OUTSIDE";
			}
		case XAException.XAER_PROTO :
			{
				return "XAER_PROTO";
			}
		case XAException.XAER_RMERR :
			{
				return "XAER_RMERR";
			}
		case XAException.XAER_RMFAIL :
			{
				return "XAER_RMFAIL";
			}
		default :
			{
				return "UNKNOWN";
			}
	}
}
/**
 * Return a String representation of a flag.
 */
public static String getXAFlag(int flag) {
	switch (flag) {
		case XAResource.TMENDRSCAN :
			{
				return "TMENDRSCAN";
			}
		case XAResource.TMFAIL :
			{
				return "TMFAIL";
			}
		case XAResource.TMJOIN :
			{
				return "TMJOIN";
			}
		case XAResource.TMNOFLAGS :
			{
				return "TMNOFLAGS";
			}
		case XAResource.TMONEPHASE :
			{
				return "TMONEPHASE";
			}
		case XAResource.TMRESUME :
			{
				return "TMRESUME";
			}
		case XAResource.TMSTARTRSCAN :
			{
				return "TMSTARTRSCAN";
			}
		case XAResource.TMSUCCESS :
			{
				return "TMSUCCESS";
			}
		case XAResource.TMSUSPEND :
			{
				return "TMSUSPEND";
			}
		default :
			{
				return "UNKNOWN";
			}
	}
}
/**
 * Register a synchronization object for the transaction currently
 * associated with the calling thread. The transction manager invokes the
 * beforeCompletion method prior to starting the transaction commit
 * process. After the transaction is completed, the transaction manager
 * invokes the afterCompletion method.
 *
 * @param sync The Synchronization object for the transaction associated
 * with the target object.
 * @exception RollbackException Thrown to indicate that the transaction
 * has been marked for rollback only.
 * @exception IllegalStateException Thrown if the transaction in the
 * target object is in prepared state or the transaction is inactive.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public void registerSynchronization(Synchronization sync) throws RollbackException, IllegalStateException, SystemException {

	if (status == Status.STATUS_MARKED_ROLLBACK) {
		throw new RollbackException();
	}

	if (status != Status.STATUS_ACTIVE) {
		throw new IllegalStateException();
	}

	synchronizationObjects.add(sync);

}
/**
 * Roll back the transaction associated with the current thread. When
 * this method completes, the thread becomes associated with no
 * transaction.
 *
 * @exception SecurityException Thrown to indicate that the thread is not
 * allowed to commit the transaction.
 * @exception IllegalStateException Thrown if the current thread is not
 * associated with a transaction.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public void rollback() throws IllegalStateException, SystemException {

	//System.out.println(this + "  ROLLBACK ");

	// Check status ACTIVE	
	if (status != Status.STATUS_ACTIVE && status != Status.STATUS_MARKED_ROLLBACK) {
		throw new IllegalStateException();
	}

	status = Status.STATUS_ROLLING_BACK;

	try {
		do_rollbackNonXA();
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}

	try {
		do_rollback();
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
	
	status = Status.STATUS_ROLLEDBACK;
}
/**
 * Modify the transaction associated with the current thread such that
 * the only possible outcome of the transaction is to roll back the
 * transaction.
 *
 * @exception IllegalStateException Thrown if the current thread is not
 * associated with a transaction.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public void setRollbackOnly() throws IllegalStateException, SystemException {
	status = Status.STATUS_MARKED_ROLLBACK;
}
/**
 * Print the Transaction object in a debugger friendly manner
 */
public String toString() {
	return "Transaction " + currentTransactionNumber + " xid " + xid + " in thread " + currentThreadName
		+ ((currentThreadName.equals(Thread.currentThread().getName())) ? "" : (" current= " + Thread.currentThread().getName()));
}
}
