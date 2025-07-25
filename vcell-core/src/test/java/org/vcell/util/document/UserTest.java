package org.vcell.util.document;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("Fast")
public class UserTest {

	@Test
	public void publisherTest( ) {
		SpecialUser u = new SpecialUser("schaff", testKey( ), new SpecialUser.SPECIAL_CLAIM[] {SpecialUser.SPECIAL_CLAIM.publicationEditors});
        Assertions.assertTrue(u.isPublisher());
		u = new SpecialUser("fido", testKey( ), new SpecialUser.SPECIAL_CLAIM[]{});
		assertFalse(u.isPublisher());
	}
	@Test
	public void testAcctTest( ) {
		User u = new User("vcelltestaccount", testKey( ));
        Assertions.assertTrue(u.isTestAccount());
		u = new User("fido", testKey( ));
		assertFalse(u.isTestAccount());
	}

	private KeyValue testKey ( ) {
		return new KeyValue(new BigDecimal(400));
	}
}
