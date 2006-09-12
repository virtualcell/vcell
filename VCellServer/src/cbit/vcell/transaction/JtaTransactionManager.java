package cbit.vcell.transaction;

import javax.transaction.*;
import javax.transaction.xa.*;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (7/21/2003 12:53:42 PM)
 * @author: Fei Gao
 */
public class JtaTransactionManager implements TransactionManager, UserTransaction {
   
    
    
    public static final int DEFAULT_TRANSACTION_TIMEOUT = 30;
    
    
    private Map bindings = Collections.synchronizedMap(new HashMap());
    
    
    private Map timeouts = Collections.synchronizedMap(new HashMap());   
    
/**
 * Insert the method's description here.
 * Creation date: (7/25/2003 12:27:18 PM)
 */
public JtaTransactionManager() {}
/**
 * Create a new transaction and associate it with the current thread.
 *
 * @exception NotSupportedException Thrown if the thread is already
 * associated with a transaction and the Transaction Manager
 * implementation does not support nested transactions.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public void begin() throws NotSupportedException, SystemException {

	Transaction currentTransaction = getTransaction();
	if (currentTransaction != null) {
		throw new NotSupportedException();
	}

	currentTransaction = new JtaTransactionImpl(this);
	bindings.put(Thread.currentThread(), currentTransaction);	
}
/**
 * Complete the transaction associated with the current thread. When this
 * method completes, the thread becomes associated with no transaction.
 * If the commit is terminated with an exception, the rollback should be
 * called, to do a proper clean-up.
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
public void commit() throws	RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {

	Thread currentThread = Thread.currentThread();
	Transaction currentTransaction = (Transaction) bindings.get(currentThread);
	if (currentTransaction == null) {
		throw new IllegalStateException();
	}

	timeouts.remove(currentThread);

	try {
		currentTransaction.commit();
	} finally {
		bindings.remove(currentThread);
		currentTransaction = null;
	}

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

	Transaction currentTransaction = getTransaction();
	if (currentTransaction == null) {
		return Status.STATUS_NO_TRANSACTION;
	}

	return currentTransaction.getStatus();

}
/**
 * Get the transaction object that represents the transaction context of
 * the calling thread.
 *
 * @return the Transaction object representing the transaction associated
 * with the calling thread.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public Transaction getTransaction() throws SystemException {
	return (Transaction) bindings.get(Thread.currentThread());
}
/**
 * Resume the transaction context association of the calling thread with
 * the transaction represented by the supplied Transaction object. When
 * this method returns, the calling thread is associated with the
 * transaction context specified.
 *
 * @param tobj The Transaction object that represents the transaction to
 * be resumed.
 * @exception InvalidTransactionException Thrown if the parameter
 * transaction object contains an invalid transaction.
 * @exception IllegalStateException Thrown if the thread is already
 * associated with another transaction.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public void resume(Transaction tobj) throws InvalidTransactionException, IllegalStateException, SystemException {

	if (getTransaction() != null) {
		throw new IllegalStateException();
	}

	if (tobj == null) {
		throw new InvalidTransactionException();
	}

	bindings.put(Thread.currentThread(), tobj);

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
public void rollback() throws SecurityException, IllegalStateException, SystemException {

	Thread currentThread = Thread.currentThread();
	Transaction currentTransaction = (Transaction) bindings.remove(currentThread);
	if (currentTransaction == null) {
		throw new IllegalStateException();
	}

	timeouts.remove(currentThread);

	currentTransaction.rollback();
	currentTransaction = null;
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

	Transaction currentTransaction = getTransaction();
	if (currentTransaction == null) {
		throw new IllegalStateException();
	}

	currentTransaction.setRollbackOnly();

}
/**
 * Modify the value of the timeout value that is associated with the
 * transactions started by the current thread with the begin method.
 * <p>
 * If an application has not called this method, the transaction service
 * uses some default value for the transaction timeout.
 *
 * @param seconds The value of the timeout in seconds. If the value is
 * zero, the transaction service restores the default value.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public void setTransactionTimeout(int seconds) throws SystemException {
	timeouts.put(Thread.currentThread(), new Integer(seconds));
}
/**
 * Suspend the transaction currently associated with the calling thread
 * and return a Transaction object that represents the transaction
 * context being suspended. If the calling thread is not associated with
 * a transaction, the method returns a null object reference. When this
 * method returns, the calling thread is associated with no transaction.
 *
 * @return Transaction object representing the suspended transaction.
 * @exception SystemException Thrown if the transaction manager encounters
 * an unexpected error condition.
 */
public Transaction suspend() throws SystemException {

	Transaction currentTransaction = getTransaction();

	if (currentTransaction != null) {
		Thread currentThread = Thread.currentThread();
		bindings.remove(currentThread);
		timeouts.remove(currentThread);
	}

	return currentTransaction;

}
}
