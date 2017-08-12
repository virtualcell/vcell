package org.vcell.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.vcell.pathway.SequenceLocation;

import cbit.vcell.solver.TimeStep;

/**
 * test {@link Matchable#areEqual(Matchable, Matchable)]
 */
public class MatcheableTest {

	@Test
	public void null2() {
		TimeStep t = null;
		SequenceLocation sl = null;
		assertTrue(Matchable.areEqual(t,t));
		assertTrue(Matchable.areEqual(t,sl));
		assertTrue(Matchable.areEqual(t,null));
	}
	
	@Test
	public void nullSafe( ) {
		TimeStep t = new TimeStep(); 
		TimeStep cp = new TimeStep(t); 
		TimeStep nStep = null;
		TimeStep nStep2 = null;
		assertTrue(Matchable.areEqual(t,t));
		assertTrue(Matchable.areEqual(cp,t));
		assertTrue(Matchable.areEqual(t,cp));
		assertTrue(Matchable.areEqual(nStep,nStep2));
		
		assertFalse(Matchable.areEqual(t,nStep));
		assertFalse(Matchable.areEqual(nStep,t));
	}

}
