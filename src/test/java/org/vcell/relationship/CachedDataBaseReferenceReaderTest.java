package org.vcell.relationship;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.junit.Test;

public class CachedDataBaseReferenceReaderTest {
	
	/**
	 * test soft reference works correctly
	 */
	@Test
	public void fetchAndConsume( ) {
		ReferenceQueue<CachedDataBaseReferenceReader> rq = new ReferenceQueue<CachedDataBaseReferenceReader>();
		WeakReference<CachedDataBaseReferenceReader> weakR = new WeakReference<CachedDataBaseReferenceReader>(
				CachedDataBaseReferenceReader.getCachedReader(),rq);
		
		boolean outOfMem = false;
		ArrayList<int[]> pig = new ArrayList<int[]>( );
		for (int size = 10;!outOfMem;size*=10) {
			try {
				assertFalse(weakR.isEnqueued());
				pig.add(new int[size]);
				CachedDataBaseReferenceReader w = weakR.get( );
				//make sure getting same cache as long as not out of memory
				assertTrue(w == CachedDataBaseReferenceReader.getCachedReader());
			} catch(OutOfMemoryError error) {
				assertTrue(weakR.isEnqueued());
				assertTrue(weakR.get( ) == null);
				outOfMem = true;
			}
		}
		pig.clear();
		
		//make sure we can get another (new) cache now that memory is avaiable
		CachedDataBaseReferenceReader dbReader = CachedDataBaseReferenceReader.getCachedReader();
		assertTrue(dbReader != null);
	}
	
	/**
	 * test GOTerm results is fast after first retrieval
	 * @throws Exception
	 */
	@Test
	public void goTest( ) throws Exception {
		final String key = "0006814";
		CachedDataBaseReferenceReader dbReader = CachedDataBaseReferenceReader.getCachedReader();
		String s = dbReader.getGOTerm(key);
		
		long start = System.currentTimeMillis();
		String s2 = dbReader.getGOTerm(key);
		//we should get exact same String, not equal string
		assertTrue(s == s2);
		
		long end = System.currentTimeMillis();
		long fetchTime = end - start;
		//cached retrieval should take less han millisecond
		assertTrue(fetchTime <= 1);
	}

	/**
	 * test molecular id is fast after first retrieval
	 * @throws Exception
	 */
	@Test
	public void molIdTest( ) throws Exception {
		final String key = "p00533";
		CachedDataBaseReferenceReader dbReader = CachedDataBaseReferenceReader.getCachedReader();
		String s = dbReader.getMoleculeDataBaseReference(key);
		
		long start = System.currentTimeMillis();
		String s2 = dbReader.getMoleculeDataBaseReference(key);
		//we should get exact same String, not equal string
		assertTrue(s == s2);
		
		long end = System.currentTimeMillis();
		long fetchTime = end - start;
		//cached retrieval should take less han millisecond
		assertTrue(fetchTime <= 1);
	}
	
	@Test
	public void threadSafety(  ) throws InterruptedException {
		final int NTHREADS = 10;
		ArrayList<TestThread> threads = new ArrayList<TestThread>(NTHREADS);
		for (int i =0 ; i < NTHREADS;i++) {
			threads.add(new TestThread()); 
		}
		//make threads fight to create object...
		CachedDataBaseReferenceReader.clearExisting( );
		for (TestThread t: threads) {
			t.start();
		}
		CachedDataBaseReferenceReader r = CachedDataBaseReferenceReader.getCachedReader();
		for (TestThread t: threads) {
			t.join();
		}
		for (TestThread t: threads) {
			//make sure all threads got the same reader
			assertTrue(t.reader == r);
		}
	}
	
	static class TestThread extends Thread {
		CachedDataBaseReferenceReader reader = null;
		@Override
		public void run() {
			reader = CachedDataBaseReferenceReader.getCachedReader();
		}
		
	}
}
