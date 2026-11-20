package org.vcell.rest.rpc;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.api.types.rpc.VCellApiRpcBody;
import org.vcell.api.types.rpc.VCellApiRpcRequest;
import org.vcell.util.CompressionUtils;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.InvalidClassException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The RPC body is deserialized from untrusted bytes, so only expected classes may appear in it.
 */
@Tag("Fast")
public class RpcBodyFilterTest {

	private static byte[] rpcBody(Object... args) throws Exception {
		VCellApiRpcRequest request = new VCellApiRpcRequest("caller", VCellApiRpcBody.RpcDestination.DbRequestQueue, "someMethod", args);
		VCellApiRpcBody body = new VCellApiRpcBody(VCellApiRpcBody.RpcDestination.DbRequestQueue, request, true, 1000, null, null);
		return CompressionUtils.toCompressedSerialized(body);
	}

	@Test
	public void testOrdinaryRpcBodyAccepted() throws Exception {
		List<KeyValue> keys = new ArrayList<>(List.of(new KeyValue("1"), new KeyValue("2")));
		byte[] bytes = rpcBody(new User("caller", new KeyValue("100")), keys, new KeyValue[]{new KeyValue("3")}, 7, true, "text", new double[]{1.0});
		Serializable result = CompressionUtils.fromCompressedSerialized(bytes, RpcRestlet.RPC_BODY_FILTER);
		assertInstanceOf(VCellApiRpcBody.class, result);
	}

	@Test
	public void testUnexpectedClassRejected() throws Exception {
		byte[] bytes = rpcBody(new URL("http://example.invalid/"));
		assertThrows(InvalidClassException.class, () -> CompressionUtils.fromCompressedSerialized(bytes, RpcRestlet.RPC_BODY_FILTER));
	}

	@Test
	public void testUnexpectedClassNestedInCollectionRejected() throws Exception {
		List<Object> nested = new ArrayList<>();
		nested.add(new URL("http://example.invalid/"));
		byte[] bytes = rpcBody(nested);
		assertThrows(InvalidClassException.class, () -> CompressionUtils.fromCompressedSerialized(bytes, RpcRestlet.RPC_BODY_FILTER));
	}
}
