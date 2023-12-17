package org.vcell.movingboundary;

import org.junit.jupiter.api.Tag;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;

@Category(Fast.class)
@Tag("Fast")
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
