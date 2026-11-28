package org.vcell.api.client;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.function.Consumer;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vcell.util.document.VCellSoftwareVersion;

/**
 * Identifies this client on every request, so the server can log which client and version made each
 * call (see docs/api-style-guide.md §6.3 and §7.1). Without it, nothing tells us which desktop versions
 * still use an endpoint, which is the evidence needed before retiring one.
 */
public final class ClientIdentification {

	public static final String CLIENT_HEADER = "VCell-Client";
	public static final String VERSION_HEADER = "VCell-Client-Version";

	/** Overrides the client name, e.g. for admin tools that reuse this client. */
	public static final String CLIENT_NAME_PROPERTY = "vcell.client.name";
	static final String DEFAULT_CLIENT_NAME = "vcell-desktop";
	static final String UNKNOWN_VERSION = "unknown";

	private ClientIdentification() { }

	public static String clientName() {
		String name = System.getProperty(CLIENT_NAME_PROPERTY);
		return (name == null || name.isBlank()) ? DEFAULT_CLIENT_NAME : name.trim();
	}

	public static String clientVersion() {
		try {
			String version = VCellSoftwareVersion.fromSystemProperty().getSoftwareVersionString();
			return (version == null || version.isBlank()) ? UNKNOWN_VERSION : version.trim();
		} catch (RuntimeException e) {
			// a development run without the version property still identifies itself
			return UNKNOWN_VERSION;
		}
	}

	/** Default headers for the Apache client used for /api/v0. */
	public static List<Header> apacheHeaders() {
		return List.of(new BasicHeader(CLIENT_HEADER, clientName()), new BasicHeader(VERSION_HEADER, clientVersion()));
	}

	/**
	 * A request interceptor for the generated /api/v1 client that adds the identification headers and
	 * then runs {@code next} (for example the token-renewing interceptor of an authenticated client).
	 */
	public static Consumer<HttpRequest.Builder> withIdentification(Consumer<HttpRequest.Builder> next) {
		String name = clientName();
		String version = clientVersion();
		return builder -> {
			builder.header(CLIENT_HEADER, name);
			builder.header(VERSION_HEADER, version);
			if (next != null) {
				next.accept(builder);
			}
		};
	}
}
