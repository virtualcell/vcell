package cbit.vcell.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("Fast")
public class CommentedObjectTest {

	@Test
	public void equiv( ) {
		Mock one = new Mock("Virtual", "Cell");
		Mock two = new Mock("Virtual", "Cell");
		assertFalse(one.equals(two));
		Assertions.assertTrue(one.compareEqual(two));
		
		two.setAfterComment("Reality");
		assertFalse(one.equals(two));
		assertFalse(one.compareEqual(two));
	}
	
	@SuppressWarnings("serial")
	static class Mock extends CommentedObject {

		Mock() {
			super();
		}
		
		Mock(String before, String after) {
			setBeforeComment(before);
			setAfterComment(after);
		}
		
	}

}
