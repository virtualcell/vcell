package org.vcell.restq.telemetry;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.SimpleResourceInfo;

/**
 * One structured log line per API request (docs/api-style-guide.md §7.1): method, route template,
 * status, duration, response size, caller, client name and version, and a request id that is also
 * returned to the caller (§7.2). The line is written when the response has ended, so the final status,
 * the bytes actually written and the full duration (including serialization) are known.
 * <p>
 * This runs after a resource method has been matched, so requests that never reach one (unknown paths,
 * requests refused by authentication before routing) are not logged here yet.
 */
public class RequestLogFilter {

	public static final String REQUEST_ID_HEADER = "X-Request-Id";
	public static final String CLIENT_HEADER = "VCell-Client";
	public static final String CLIENT_VERSION_HEADER = "VCell-Client-Version";
	static final String USER_AGENT_HEADER = "User-Agent";

	private static final Pattern REQUEST_ID = Pattern.compile("[A-Za-z0-9._-]{8,128}");
	private static final Pattern SLASHES = Pattern.compile("/+");
	/** Route templates by resource method; computed once each, by reflection. */
	private static final Map<String, String> ROUTES = new ConcurrentHashMap<>();

	@Inject
	SecurityIdentity identity;

	@Inject
	RoutingContext routingContext;

	@ServerRequestFilter(preMatching = false)
	public void beforeResource(ContainerRequestContext request, SimpleResourceInfo resource) {
		try {
			RequestLogLine line = new RequestLogLine(System.nanoTime())
					.method(request.getMethod())
					.route(routeTemplate(resource))
					.caller(identity)
					.client(request.getHeaderString(CLIENT_HEADER), request.getHeaderString(CLIENT_VERSION_HEADER),
							request.getHeaderString(USER_AGENT_HEADER))
					.requestId(requestId(request.getHeaderString(REQUEST_ID_HEADER)));
			// resolved now, not through the request-scoped proxy after the request has ended
			HttpServerResponse response = routingContext.response();
			response.putHeader(REQUEST_ID_HEADER, line.requestId());
			routingContext.addEndHandler(ended -> {
				try {
					line.finish(response.getStatusCode(), response.bytesWritten(), ended.failed()).log();
				} catch (RuntimeException e) {
					RequestLogLine.lg.debug("request telemetry not recorded: " + e.getMessage());
				}
			});
		} catch (RuntimeException e) {
			// telemetry never fails a request
			RequestLogLine.lg.debug("request telemetry not recorded: " + e.getMessage());
		}
	}

	/** The matched resource's path template, from its class and method @Path annotations. */
	static String routeTemplate(SimpleResourceInfo resource) {
		if (resource == null || resource.getResourceClass() == null) {
			return null;
		}
		Class<?> cls = resource.getResourceClass();
		String key = cls.getName() + "#" + resource.getMethodName() + Arrays.toString(resource.parameterTypes());
		return ROUTES.computeIfAbsent(key, k -> computeRouteTemplate(cls, resource));
	}

	private static String computeRouteTemplate(Class<?> cls, SimpleResourceInfo resource) {
		Path classPath = cls.getAnnotation(Path.class);
		String methodPath = null;
		try {
			Method m = cls.getMethod(resource.getMethodName(), resource.parameterTypes());
			Path p = m.getAnnotation(Path.class);
			methodPath = (p == null) ? null : p.value();
		} catch (NoSuchMethodException e) {
			// fall back to the class path
		}
		return join(classPath == null ? "" : classPath.value(), methodPath == null ? "" : methodPath);
	}

	static String join(String a, String b) {
		String s = SLASHES.matcher("/" + a + "/" + b).replaceAll("/");
		return (s.length() > 1 && s.endsWith("/")) ? s.substring(0, s.length() - 1) : s;
	}

	/** The caller's request id if it is a sane token, else a new one. */
	static String requestId(String incoming) {
		if (incoming != null && REQUEST_ID.matcher(incoming).matches()) {
			return incoming;
		}
		return UUID.randomUUID().toString().replace("-", "");
	}
}
