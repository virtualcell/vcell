package org.vcell.smoldyn.test;


import junit.framework.TestCase;


public class UnitTesting extends TestCase {
	
	public UnitTesting (String name) {
		super(name);
	}
	
	public void testSomething() {
		assertTrue(4 == (2 * 2 + 1));
	}
}
