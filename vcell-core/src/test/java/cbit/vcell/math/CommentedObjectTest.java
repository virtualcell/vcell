package cbit.vcell.math;

import java.util.Objects;

import org.junit.Test;

import org.junit.Assert;

public class CommentedObjectTest {

	@Test
	public void equiv( ) {
		Mock one = new Mock("Virtual", "Cell");
		Mock two = new Mock("Virtual", "Cell");
		Assert.assertFalse(one.equals(two));
		Assert.assertTrue(one.compareEqual(two));
		
		two.setAfterComment("Reality");
		Assert.assertFalse(one.equals(two));
		Assert.assertFalse(one.compareEqual(two));
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
