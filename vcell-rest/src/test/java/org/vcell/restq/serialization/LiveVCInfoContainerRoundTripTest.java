package org.vcell.restq.serialization;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.api.VcInfoContainerResourceApi;
import org.vcell.restclient.model.VCInfoContainerSummary;
import org.vcell.restclient.utils.DtoModelTransforms;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * END-TO-END round-trip against a LIVE, already-running vcInfoContainer service (e.g. a local
 * vcell-rest started in prod mode against Oracle). Unlike the unit round-trip test, this hits the
 * real HTTP endpoint, so it exercises the actual server serialization AND the generated client's
 * deserialization, then reconstructs every model via {@link DtoModelTransforms} — the exact path
 * the desktop client (VCellClientMain) follows.
 *
 * <p>Each model that fails reconstruction is collected (not fatal mid-run) and reported with its
 * name/key, so one run surfaces every data-dependent problem at once (a null child summary, a shared
 * GroupAccessSome, an undecodable preview, ...). This is the check that reproduces the client-side
 * NPE against real production data.
 *
 * <p><b>Disabled unless {@code VCELL_API_LIVE} is set</b> (and tagged {@code live-service}), so it
 * never runs in the normal CI groups. Run it against a locally-running prod-mode service:
 * <pre>
 *   # anonymous (public records only)
 *   VCELL_API_LIVE=1 mvn test -pl vcell-rest -am -Dtest=LiveVCInfoContainerRoundTripTest \
 *        -Dgroups=live-service -Dmdep.analyze.skip=true
 *
 *   # authenticated (the user's own + shared records; measures the slow subquery path)
 *   VCELL_API_LIVE=1 VCELL_API_TOKEN='<auth0 access token>' mvn test -pl vcell-rest -am ...
 * </pre>
 * {@code VCELL_API_BASE_URL} defaults to {@code http://localhost:9000}.
 */
@Tag("live-service")
public class LiveVCInfoContainerRoundTripTest {

    private record Failure(String kind, String label, Throwable error) {
        @Override public String toString() {
            Throwable root = error;
            while (root.getCause() != null && root.getCause() != root) root = root.getCause();
            return String.format("%-9s %s: %s: %s", kind, label, root.getClass().getSimpleName(), root.getMessage());
        }
    }

    private static String env(String name, String dflt) {
        String v = System.getenv(name);
        return (v == null || v.isBlank()) ? dflt : v.trim();
    }

    @Test
    public void everyModelFromLiveEndpointRoundTrips() throws Exception {
        Assumptions.assumeTrue(System.getenv("VCELL_API_LIVE") != null,
                "live-service round-trip skipped: set VCELL_API_LIVE=1 (service must be running)");
        String baseUri = env("VCELL_API_BASE_URL", "http://localhost:9000");
        String token = env("VCELL_API_TOKEN", null);

        ApiClient client = new ApiClient();
        client.updateBaseUri(baseUri);
        if (token != null) {
            client.setRequestInterceptor(request -> request.header("Authorization", "Bearer " + token));
        }

        long t0 = System.nanoTime();
        VCInfoContainerSummary vcic = new VcInfoContainerResourceApi(client).getVCInfoContainer();
        long fetchMs = (System.nanoTime() - t0) / 1_000_000;

        List<Failure> failures = new ArrayList<>();
        int total = 0;

        total += roundTrip("BioModel", vcic.getBioModelSummaries(),
                s -> s.getVersion() == null ? "?" : s.getVersion().getName() + " (key=" + s.getVersion().getVersionKey() + ")",
                DtoModelTransforms::bioModelContextToBioModelInfo, failures);
        total += roundTrip("MathModel", vcic.getMathModelSummaries(),
                s -> s.getVersion() == null ? "?" : s.getVersion().getName() + " (key=" + s.getVersion().getVersionKey() + ")",
                DtoModelTransforms::mathModelContextToMathModel, failures);
        total += roundTrip("Geometry", vcic.getGeometrySummaries(),
                s -> s.getVersion() == null ? "?" : s.getVersion().getName() + " (key=" + s.getVersion().getVersionKey() + ")",
                DtoModelTransforms::geometrySummaryToGeometryInfo, failures);
        total += roundTrip("VCImage", vcic.getVcImageSummaries(),
                s -> s.getVersion() == null ? "?" : s.getVersion().getName() + " (key=" + s.getVersion().getVersionKey() + ")",
                DtoModelTransforms::imageSummaryToVCImageInfo, failures);

        System.out.printf("live round-trip @ %s (%s): %d models in %d ms fetch, %d failures%n",
                baseUri, token == null ? "anonymous" : "authenticated", total, fetchMs, failures.size());
        if (!failures.isEmpty()) {
            StringBuilder sb = new StringBuilder(failures.size() + " of " + total
                    + " models failed the vcInfoContainer DTO round-trip:\n");
            for (Failure f : failures) sb.append("  - ").append(f).append('\n');
            fail(sb.toString());
        }
    }

    private static <T> int roundTrip(String kind, List<T> summaries, Function<T, String> label,
                                     Consumer<T> reconstruct, List<Failure> failures) {
        if (summaries == null) return 0;
        for (T s : summaries) {
            try {
                reconstruct.accept(s);
            } catch (Throwable t) {
                failures.add(new Failure(kind, safe(label, s), t));
            }
        }
        return summaries.size();
    }

    private static <T> String safe(Function<T, String> label, T s) {
        try { return label.apply(s); } catch (Throwable t) { return "?"; }
    }
}
