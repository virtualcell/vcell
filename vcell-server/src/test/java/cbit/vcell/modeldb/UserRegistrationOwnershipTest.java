package cbit.vcell.modeldb;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * GHSA-9hpw-2h4g-fjhj: userRegistrationOP GETINFO/UPDATE must be limited to the caller's own account.
 */
@Tag("Fast")
public class UserRegistrationOwnershipTest {

	private static final User CALLER = new User("caller", new KeyValue("100"));

	@Test
	public void testOwnAccountAllowed() {
		assertDoesNotThrow(() -> DBTopLevel.requireOwnAccount(CALLER, new KeyValue("100")));
	}

	@Test
	public void testOtherAccountRejected() {
		assertThrows(PermissionException.class, () -> DBTopLevel.requireOwnAccount(CALLER, new KeyValue("200")));
	}

	@Test
	public void testMissingAccountKeyRejected() {
		assertThrows(PermissionException.class, () -> DBTopLevel.requireOwnAccount(CALLER, null));
	}

	@Test
	public void testMissingCallerRejected() {
		assertThrows(PermissionException.class, () -> DBTopLevel.requireOwnAccount(null, new KeyValue("100")));
	}
}
