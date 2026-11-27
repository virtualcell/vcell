package cbit.vcell.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import cbit.vcell.message.server.bootstrap.client.RpcDataServerProxy;
import cbit.vcell.message.server.bootstrap.client.RpcDbServerProxy;
import cbit.vcell.message.server.bootstrap.client.RpcSimServerProxy;
import cbit.vcell.simdata.DataServerImpl;

@Tag("Fast")
public class RpcMethodAllowlistTest {

	@Test
	public void eachServiceQueueHasItsOwnList() {
		assertSame(RpcMethodAllowlist.DATA, RpcMethodAllowlist.forQueue(VCellQueue.DataRequestQueue));
		assertSame(RpcMethodAllowlist.DB, RpcMethodAllowlist.forQueue(VCellQueue.DbRequestQueue));
		assertSame(RpcMethodAllowlist.DISPATCH, RpcMethodAllowlist.forQueue(VCellQueue.SimReqQueue));
	}

	@Test
	public void anyOtherQueueMustBeGivenAListExplicitly() {
		assertThrows(IllegalArgumentException.class, () -> RpcMethodAllowlist.forQueue(VCellQueue.SimJobQueue));
		assertThrows(IllegalArgumentException.class, () -> RpcMethodAllowlist.forQueue(new VCellQueue("someTestQueue")));
		assertThrows(IllegalArgumentException.class, () -> new VCRpcMessageHandler(new Object(), new VCellQueue("someTestQueue")));
	}

	@Test
	public void theListsAreReadOnly() {
		assertThrows(UnsupportedOperationException.class, () -> RpcMethodAllowlist.DB.add("anything"));
	}

	/**
	 * Every name the current client proxies send must be on its queue's list, or the desktop breaks. Each
	 * proxy method is named after the remote method it calls, so the proxies' public methods stand in for
	 * the rpc(...) names they send.
	 */
	@Test
	public void theCurrentProxiesMethodsAreAllowed() {
		assertAllowed(RpcDataServerProxy.class, RpcMethodAllowlist.DATA);
		assertAllowed(RpcDbServerProxy.class, RpcMethodAllowlist.DB);
		assertAllowed(RpcSimServerProxy.class, RpcMethodAllowlist.DISPATCH);
	}

	/** The data service's public methods that are not remote stay unreachable. */
	@Test
	public void theDataServiceKeepsItsInProcessMethodsPrivate() {
		Set<String> publicNotRemote = publicMethods(DataServerImpl.class).stream()
				.filter(n -> !RpcMethodAllowlist.DATA.contains(n)).collect(Collectors.toCollection(TreeSet::new));
		assertFalse(publicNotRemote.isEmpty(), "expected the data service to have public in-process methods");
		for (String n : new String[] { "saveFieldData", "deleteFieldData", "analyzeAndCreateFieldData" }) {
			if (publicMethods(DataServerImpl.class).contains(n)) {
				assertTrue(publicNotRemote.contains(n), n + " must not be remotely callable");
			}
		}
		assertFalse(RpcMethodAllowlist.DATA.contains("wait"));
		assertFalse(RpcMethodAllowlist.DATA.contains("getClass"));
	}

	private static void assertAllowed(Class<?> proxy, Set<String> allowed) {
		Set<String> missing = new TreeSet<>();
		for (Method m : proxy.getDeclaredMethods()) {
			if (Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers()) && !allowed.contains(m.getName())) {
				missing.add(m.getName());
			}
		}
		assertEquals(Set.of(), missing, proxy.getSimpleName() + " has public methods whose RPC name is not allowlisted");
	}

	private static Set<String> publicMethods(Class<?> c) {
		return Arrays.stream(c.getMethods()).map(Method::getName).collect(Collectors.toCollection(TreeSet::new));
	}
}
