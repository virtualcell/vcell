package org.vcell.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.pathway.SequenceLocation;
import org.vcell.test.Fast;
import org.vcell.util.Matchable;

import cbit.vcell.solver.TimeStep;

/**
 * test {@link Matchable#areEqual(Matchable, Matchable)]
 */
@Category(Fast.class)
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
