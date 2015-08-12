package org.vcell.util;

import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.vcell.util.Notepad;

public class TestLogNotepad {
	CountDownLatch latch;
	Notepad notepad = new Notepad();
	
	@Before
	public void setup( ) {
		Integer five = 5;
		String dog = "Fido";
		notepad.remember(this, Integer.class,five); 
		notepad.remember(this, String.class,dog); 
		Long[] zip = { 0L, 6L, 0L, 3L, 0L};
		notepad.remember(this, Long[].class,zip); 
	}
	
	@Test
	public void back( ) {
		int f = notepad.recall(this, Integer.class); 
		String d = notepad.recall(this, String.class); 
		Assert.assertTrue(f == 5);
		Assert.assertTrue(d.equals("Fido"));
		Float n = notepad.recall(this, Float.class); 
		Assert.assertTrue(n == null);
		Long[] zip = notepad.recall(this,Long[].class); 
		Assert.assertTrue(zip[1] == 6);
	}
	
	@Test
	public void clean( ) {
		latch = new CountDownLatch(1);
		Whines w = new Whines();
		notepad.remember(w, Integer.class,7); 
		int seven = notepad.recall(w, Integer.class); 
		Assert.assertTrue(seven == 7);
		w = null;
		System.gc();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("done");
		notepad.remember(this, Integer.class,9); 
		Assert.assertTrue( notepad.recall(this, Integer.class) == 9 );
	}
	
	private class Whines extends Object {

		@Override
		protected void finalize() throws Throwable {
			System.out.println("going away");
			latch.countDown();
		}
		
		
	}

}
