package cbit.vcell.resource;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.function.Consumer;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vcell.util.document.VCellSoftwareVersion;

/**
 * Identifies this client on every request, so the server can log which client and version made each
 * call (docs/api-style-guide.md §6.3 and §7.1). Without it, nothing tells us which desktop versions
 * still use an endpoint, which is the evidence needed before retiring one.
 * <p>
 * It lives in vcell-core so that every HTTP client of the desktop can use it, including those in
 * vcell-core itself ({@link ErrorUtils}). The servers use {@link #clean} on the values they log.
 */
public final class ClientIdentification {

	public static final String CLIENT_HEADER = "VCell-Client";
	public static final String VERSION_HEADER = "VCell-Client-Version";

	/** Overrides the client name, e.g. for admin tools that reuse this client. */
	public static final String CLIENT_NAME_PROPERTY = "vcell.client.name";
	static final String DEFAULT_CLIENT_NAME = "vcell-desktop";
	static final String UNKNOWN_VERSION = "unknown";

	/** Longest client name or version kept, on either side. */
	public static final int MAX_NAME_LENGTH = 64;
	/** Longest User-Agent kept in a log line. */
	public static final int MAX_USER_AGENT_LENGTH = 256;

	private ClientIdentification() { }

	public static String clientName() {
		String name = clean(System.getProperty(CLIENT_NAME_PROPERTY), MAX_NAME_LENGTH);
		return name == null ? DEFAULT_CLIENT_NAME : name;
	}

	public static String clientVersion() {
		try {
			String version = clean(VCellSoftwareVersion.fromSystemProperty().getSoftwareVersionString(), MAX_NAME_LENGTH);
			return version == null ? UNKNOWN_VERSION : version;
		} catch (RuntimeException e) {
			// a development run without the version property still identifies itself
			return UNKNOWN_VERSION;
		}
	}

	/** Default headers for an Apache HttpClient (the /api/v0 client, error reports, export downloads). */
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

	/**
	 * A caller-supplied value made safe to send as a header or to write in a log line: every character
	 * outside printable ASCII becomes {@code ?} (so no line breaks or control characters), surrounding
	 * spaces are dropped, and the result is cut to {@code maxLength}. Null for a null or blank value.
	 */
	public static String clean(String value, int maxLength) {
		if (value == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder(Math.min(value.length(), maxLength + 1));
		for (int i = 0; i < value.length() && sb.length() <= maxLength; i++) {
			char c = value.charAt(i);
			sb.append(c >= 0x20 && c <= 0x7e ? c : '?');
		}
		String s = sb.toString().trim();
		if (s.length() > maxLength) {
			s = s.substring(0, maxLength).trim();
		}
		return s.isEmpty() ? null : s;
	}
}
