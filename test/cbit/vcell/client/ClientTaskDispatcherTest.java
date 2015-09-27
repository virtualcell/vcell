package cbit.vcell.client;

import cbit.vcell.client.task.AsynchClientTaskFunction;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;

import java.awt.Component;
import java.awt.Label;
import java.util.Hashtable; 
import static cbit.vcell.client.task.AsynchClientTask.TASKTYPE_NONSWING_BLOCKING;
import static cbit.vcell.client.task.AsynchClientTask.TASKTYPE_SWING_BLOCKING;
import static cbit.vcell.client.task.AsynchClientTask.TASKTYPE_SWING_NONBLOCKING;

import org.junit.Test;

public class ClientTaskDispatcherTest {
	private Hashtable<String,Object> ht  = new Hashtable<>(); 
	private  Component cmpt = new Label() ;

	public ClientTaskDispatcherTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void simple( ) {
		test(TASKTYPE_NONSWING_BLOCKING,TASKTYPE_NONSWING_BLOCKING);
	}
	
	@Test
	public void sbLast( ) {
		test(TASKTYPE_NONSWING_BLOCKING,TASKTYPE_NONSWING_BLOCKING, TASKTYPE_SWING_NONBLOCKING);
		test(TASKTYPE_NONSWING_BLOCKING,TASKTYPE_SWING_BLOCKING,TASKTYPE_NONSWING_BLOCKING, TASKTYPE_SWING_NONBLOCKING);
	}
	
	@Test( expected = RuntimeException.class)
	public void sbNotLast( ) {
		test(TASKTYPE_NONSWING_BLOCKING,TASKTYPE_NONSWING_BLOCKING, TASKTYPE_SWING_NONBLOCKING, TASKTYPE_NONSWING_BLOCKING);
	}
	
	private AsynchClientTask[] create(int ... types)  {
		AsynchClientTask[] rval = new AsynchClientTask[types.length];
		int idx = 0;
		for (int t :types) {
			rval[idx] = makeTask(idx++,t);
		}
		return rval;
	}
	
	private void test(int ...types) {
		try {
			ht.clear();
			AsynchClientTask[] act = create(types);
			ClientTaskDispatcher.dispatch(cmpt,ht,act,false,false,false,null,true); 
			Thread.sleep(1000);
		} catch(InterruptedException ie) {}
	}

	private AsynchClientTask makeTask(int serial, int taskType) {
		return new AsynchClientTaskFunction( h -> mock(serial), "task " + serial, taskType);
	}
	
	private void mock(int value) {
		System.out.println("mock task " + value);
	}
	
	

}
