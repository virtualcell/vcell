package cbit.vcell.client;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.AsynchClientTaskFunction;
import cbit.vcell.client.task.ClientTaskDispatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Hashtable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static cbit.vcell.client.task.AsynchClientTask.*;

@Disabled
@Tag("Fast")
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
	
	@Test
	public void sbNotLast( ) {
		Assertions.assertThrows(RuntimeException.class, () -> {
			test(TASKTYPE_NONSWING_BLOCKING,TASKTYPE_NONSWING_BLOCKING, TASKTYPE_SWING_NONBLOCKING, TASKTYPE_NONSWING_BLOCKING);
		});
	}

	@Test void blockingCheck(){
		AsynchClientTask collatzConjecture = new AsynchClientTask(
				"Collatz Conjecture", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				for (long startingNum = 1; startingNum < Long.MAX_VALUE; startingNum++) {
					long currentNum = startingNum;
					long counter = 0;
					while(true){
						if (currentNum == 1 || currentNum == 2 || currentNum == 4) {
							// We won't waste storage space
							System.out.print(counter + ", ");
							break;
						}
						counter++;
						if (currentNum % 2 == 0) currentNum /= 2; // Even case
						if (currentNum %2 == 1) currentNum = (3 * currentNum) + 1;
					}
				}
			}
		} ;
		Assertions.assertThrows(TimeoutException.class, () -> {
			Hashtable<String, Object> hashTable = new Hashtable<>();
			Future<?> terribleFuture = ClientTaskDispatcher.dispatchWithFuture(null, hashTable,
					new AsynchClientTask[]{collatzConjecture});
			terribleFuture.get(2, TimeUnit.SECONDS); // Not expensive but significant timeout
		});

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
