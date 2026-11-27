package cbit.vcell.message;

import cbit.vcell.message.VCRpcRequest.RpcServiceType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SpecialUser;
import org.vcell.util.document.User;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * GHSA-9hpw-2h4g-fjhj: an RPC must not act as a user other than the one it was issued for.
 */
@Tag("Fast")
public class VCRpcRequestUserArgumentTest {

	private static final User CALLER = new User("caller", new KeyValue("100"));
	private static final User VICTIM = new User("victim", new KeyValue("200"));

	@Test
	public void testOwnUserArgumentAccepted() {
		VCRpcRequest request = new VCRpcRequest(CALLER, RpcServiceType.DB, "deleteBioModel", new Object[]{CALLER, new KeyValue("5")});
		assertDoesNotThrow(request::verifyUserArguments);
	}

	@Test
	public void testOtherUserArgumentRejected() {
		VCRpcRequest request = new VCRpcRequest(CALLER, RpcServiceType.DB, "deleteBioModel", new Object[]{VICTIM, new KeyValue("5")});
		assertThrows(PermissionException.class, request::verifyUserArguments);
	}

	@Test
	public void testOtherUserInLaterPositionRejected() {
		VCRpcRequest request = new VCRpcRequest(CALLER, RpcServiceType.DB, "publishDirectly", new Object[]{new KeyValue[0], new KeyValue[0], VICTIM});
		assertThrows(PermissionException.class, request::verifyUserArguments);
	}

	@Test
	public void testSpecialUserWithCallerKeyAccepted() {
		User callerWithClaims = new SpecialUser("caller", new KeyValue("100"), new SpecialUser.SPECIAL_CLAIM[0]);
		VCRpcRequest request = new VCRpcRequest(CALLER, RpcServiceType.DB, "curate", new Object[]{callerWithClaims, null});
		assertDoesNotThrow(request::verifyUserArguments);
	}

	@Test
	public void testUserArgumentWithoutRequestUserRejected() {
		VCRpcRequest request = new VCRpcRequest(null, RpcServiceType.DB, "deleteBioModel", new Object[]{VICTIM});
		assertThrows(PermissionException.class, request::verifyUserArguments);
	}

	@Test
	public void testNoUserArgumentsAccepted() {
		VCRpcRequest request = new VCRpcRequest(CALLER, RpcServiceType.DB, "getSimulationStatus", new Object[]{new KeyValue("5")});
		assertDoesNotThrow(request::verifyUserArguments);
	}
}
