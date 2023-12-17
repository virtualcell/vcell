package org.vcell.core;

import cbit.vcell.solver.TimeStep;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.pathway.SequenceLocation;
import org.vcell.util.Matchable;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * test {@link Matchable#areEqual(Matchable, Matchable)]
 */
@Tag("Fast")
public class MatcheableTest {

	@Test
	public void null2() {
		TimeStep t = null;
		SequenceLocation sl = null;
		assertTrue(Matchable.areEqual(t, t));
		assertTrue(Matchable.areEqual(t, sl));
		assertTrue(Matchable.areEqual(t, null));
	}
	
	@Test
	public void nullSafe( ) {
		TimeStep t = new TimeStep();
		TimeStep cp = new TimeStep(t);
		TimeStep nStep = null;
		TimeStep nStep2 = null;
		assertTrue(Matchable.areEqual(t, t));
		assertTrue(Matchable.areEqual(cp, t));
		assertTrue(Matchable.areEqual(t, cp));
		assertTrue(Matchable.areEqual(nStep, nStep2));

		assertFalse(Matchable.areEqual(t, nStep));
		assertFalse(Matchable.areEqual(nStep, t));
	}

}
