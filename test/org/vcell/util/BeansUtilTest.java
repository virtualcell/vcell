package org.vcell.util;

import org.junit.Test;

public class BeansUtilTest {
	
	@SuppressWarnings("null")
	@Test
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

}
