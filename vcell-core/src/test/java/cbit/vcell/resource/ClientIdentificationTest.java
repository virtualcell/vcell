package cbit.vcell.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Fast")
public class ClientIdentificationTest {

	@AfterEach
	public void clearOverride() {
		System.clearProperty(ClientIdentification.CLIENT_NAME_PROPERTY);
	}

	@Test
	public void v0RequestsCarryTheClientNameAndAVersion() {
		Map<String, String> h = ClientIdentification.apacheHeaders().stream()
				.collect(Collectors.toMap(Header::getName, Header::getValue));
		assertEquals("vcell-desktop", h.get(ClientIdentification.CLIENT_HEADER));
		String version = h.get(ClientIdentification.VERSION_HEADER);
		assertTrue(version != null && !version.isBlank(), "a version, or 'unknown' without the version property");
	}

	@Test
	public void theClientNameCanBeOverriddenForOtherTools() {
		System.setProperty(ClientIdentification.CLIENT_NAME_PROPERTY, " vcell-admin ");
		assertEquals("vcell-admin", ClientIdentification.clientName());
	}

	/** The v1 client already has an interceptor (token renewal); identification must not replace it. */
	@Test
	public void v1RequestsCarryTheHeadersAndStillRunTheExistingInterceptor() {
		AtomicBoolean existingRan = new AtomicBoolean(false);
		Consumer<HttpRequest.Builder> existing = b -> { existingRan.set(true); b.header("Authorization", "Bearer t"); };
		HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("https://example.org/api/v1/x"));
		ClientIdentification.withIdentification(existing).accept(builder);
		HttpRequest request = builder.build();
		assertTrue(existingRan.get(), "the existing interceptor still runs");
		assertEquals("vcell-desktop", request.headers().firstValue(ClientIdentification.CLIENT_HEADER).orElse(null));
		assertTrue(request.headers().firstValue(ClientIdentification.VERSION_HEADER).isPresent());
		assertEquals("Bearer t", request.headers().firstValue("Authorization").orElse(null));
	}

	@Test
	public void v1IdentificationWorksWithoutAnExistingInterceptor() {
		HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("https://example.org/api/v1/x"));
		ClientIdentification.withIdentification(null).accept(builder);
		assertTrue(builder.build().headers().firstValue(ClientIdentification.CLIENT_HEADER).isPresent());
	}

	/** A bad -Dvcell.client.name must not make every request fail (java.net.http rejects control characters). */
	@Test
	public void aNameWithControlCharactersIsCleaned() {
		System.setProperty(ClientIdentification.CLIENT_NAME_PROPERTY, "evil\r\nInjected: yes");
		assertEquals("evil??Injected: yes", ClientIdentification.clientName());
		HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("https://example.org/api/v1/x"));
		ClientIdentification.withIdentification(null).accept(builder);
		assertEquals("evil??Injected: yes", builder.build().headers().firstValue(ClientIdentification.CLIENT_HEADER).orElse(null));
	}

	@Test
	public void cleanKeepsPrintableAsciiAndCapsTheLength() {
		assertEquals("vcell-desktop", ClientIdentification.clean("  vcell-desktop ", 64));
		assertEquals("a?b", ClientIdentification.clean("a\u00e9b", 64));
		assertEquals("abc", ClientIdentification.clean("abcdef", 3));
		assertNull(ClientIdentification.clean("   ", 64));
		assertNull(ClientIdentification.clean(null, 64));
		assertEquals(64, ClientIdentification.clean("x".repeat(10_000), 64).length());
	}
}
