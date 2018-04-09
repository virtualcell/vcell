package org.vcell.movingboundary;

import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;

public class TestMovingBoundarySolverSpec {

	@Test
	public void test() throws DataAccessException {
		testState(true);
		testState(false);
	}

	private void testState(boolean tr) throws DataAccessException {
		MovingBoundarySolverSpec mbss = new MovingBoundarySolverSpec();
		mbss.setTextReport(tr);
		String vc = mbss.getVCML();

		MovingBoundarySolverSpec back = fromVCML(vc);
		assertTrue(mbss.compareEqual(back));
		
		MovingBoundarySolverSpec copy = new MovingBoundarySolverSpec(mbss);
		assertTrue(mbss.compareEqual(copy));
	}

	private MovingBoundarySolverSpec fromVCML(String vcml) throws DataAccessException {
		CommentStringTokenizer cst = new CommentStringTokenizer(vcml);
		return new MovingBoundarySolverSpec(cst);
	}

}
