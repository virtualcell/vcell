package org.vcell.util.document;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

@Category(Fast.class)
public class UserTest {

	@Test
	public void publisherTest( ) {
		User u = new User("schaff", testKey( ));
		Assert.assertTrue(u.isPublisher());
		u = new User("fido", testKey( ));
		Assert.assertFalse(u.isPublisher());
	}
	@Test
	public void testAcctTest( ) {
		User u = new User("vcelltestaccount", testKey( ));
		Assert.assertTrue(u.isTestAccount());
		u = new User("fido", testKey( ));
		Assert.assertFalse(u.isTestAccount());
	}

	private KeyValue testKey ( ) {
		return new KeyValue(new BigDecimal(400));
	}
}
