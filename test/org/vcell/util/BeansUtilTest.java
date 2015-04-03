package org.vcell.util;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class BeansUtilTest {
	
	@SuppressWarnings("null")
	//@Test
	public void tryIt( ) {
		try {
			String n = null;
			System.out.print(n.length());
		
		} catch(NullPointerException npe) {
			npe.printStackTrace();
		}
		
		try {
			String n = null;
			System.out.print(BeanUtils.notNull(String.class, n)); 
		
		} catch(NullPointerException npe) {
			npe.printStackTrace();
		}
		
	}
	
	@Test
	public void lookupTest( ) {
		java.util.concurrent.TimeUnit tu = BeanUtils.lookupEnum(TimeUnit.class, 3);
		System.out.println(tu);
	}
	@Test
	public void downcast( ) {
		Integer i = 3;
		Number n = i;
		Integer back = BeanUtils.downcast(Integer.class,n);
		Double fail =  BeanUtils.downcast(Double.class,n);
		assertTrue(back == i);
		assertTrue(fail == null);
		Double notThere =  BeanUtils.downcast(Double.class,null);
		assertTrue(notThere == null);
	}

}
