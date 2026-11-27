package spike;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.vcell.api.dto.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.BioModelsApi;
import org.vcell.restclient.api.ExportsApi;

/** Runs the generated client (with shared DTO records) against a stub server and checks each behaviour. */
public class SpikeRun {
    static int failures = 0;
    static void check(String what, boolean ok, Object detail) {
        System.out.println((ok ? "PASS " : "FAIL ") + what + (ok ? "" : "   <- " + detail));
        if (!ok) failures++;
    }

    public static void main(String[] args) throws Exception {
        AtomicReference<String> lastQuery = new AtomicReference<>();
        AtomicReference<String> lastBody = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/v2/", ex -> {
            String path = ex.getRequestURI().getPath();
            lastQuery.set(ex.getRequestURI().getRawQuery());
            lastBody.set(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            int status; String type = "application/json"; String body;
            String m = ex.getRequestMethod();
            if (m.equals("GET") && path.equals("/api/v2/biomodels/42")) {
                status = 200;
                body = "{\"bioModelId\":\"42\",\"name\":\"Ran\",\"owner\":{\"userId\":\"7\",\"userName\":\"schaff\"},"
                     + "\"savedAt\":\"2026-11-27T15:04:05Z\",\"curation\":\"PUBLISHED\",\"futureField\":{\"x\":1},"
                     + "\"applications\":[{\"name\":\"ode\",\"geometryDimension\":0},{\"name\":\"3d\",\"geometryDimension\":3}]}";
            } else if (m.equals("GET") && path.equals("/api/v2/biomodels/43")) {
                status = 200;
                body = "{\"bioModelId\":\"43\",\"name\":\"x\",\"owner\":{\"userId\":\"7\",\"userName\":\"s\"},"
                     + "\"savedAt\":\"2026-11-27T15:04:05Z\",\"curation\":\"RETIRED\",\"applications\":[]}";
            } else if (m.equals("GET") && path.equals("/api/v2/datasets/d1/timeseries")) {
                status = 200;
                body = "{\"variable\":\"Ran_cyt\",\"times\":[0.0,0.5],"
                     + "\"values\":[[1.25,null],[\"NaN\",\"Infinity\"],[\"-Infinity\",2.0]]}";
            } else if (m.equals("GET") && path.equals("/api/v2/biomodels")) {
                status = 200;
                body = "[{\"bioModelId\":\"1\",\"name\":\"a\",\"owner\":{\"userId\":\"7\",\"userName\":\"s\"},\"savedAt\":\"2026-01-01T00:00:00Z\",\"curation\":\"CURRENT\"}]";
            } else if (m.equals("GET")) {
                status = 404; type = "application/problem+json";
                body = "{\"type\":\"/api/problems/not-found\",\"title\":\"Not found\",\"status\":404,\"detail\":\"No such BioModel.\",\"requestId\":\"r-1\"}";
            } else if (m.equals("POST") && path.equals("/api/v2/biomodels")) {
                status = 400; type = "application/problem+json";
                body = "{\"type\":\"/api/problems/validation\",\"title\":\"Invalid request\",\"status\":400,\"requestId\":\"r-2\","
                     + "\"errors\":[{\"field\":\"vcml\",\"code\":\"required\",\"message\":\"VCML is required.\"}]}";
            } else {
                status = 202; body = "{\"exportId\":\"e-1\",\"status\":\"QUEUED\"}";
            }
            byte[] b = body.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", type);
            ex.sendResponseHeaders(status, b.length);
            ex.getResponseBody().write(b);
            ex.close();
        });
        server.start();
        try {
            ApiClient client = new ApiClient();
            client.updateBaseUri("http://127.0.0.1:" + server.getAddress().getPort());
            BioModelsApi bioModels = new BioModelsApi(client);
            ExportsApi exports = new ExportsApi(client);
            ObjectMapper mapper = client.getObjectMapper();

            BioModel bm = bioModels.getBioModel("42");
            check("record with nested records, list, enum, timestamp deserializes",
                    bm.owner().userName().equals("schaff") && bm.applications().size() == 2
                            && bm.applications().get(1).geometryDimension() == 3 && bm.curation() == Curation.PUBLISHED
                            && bm.savedAt().getYear() == 2026, bm);
            check("unknown response field is ignored (additive change, guide 6.1)", bm.name().equals("Ran"), bm);
            check("missing optional field is null", bm.description() == null, bm.description());

            List<BioModelSummary> list = bioModels.listBioModels(List.of("1", "2"), true);
            check("list of records deserializes", list.size() == 1 && list.get(0).curation() == Curation.CURRENT, list);
            check("array query parameter is repeated (guide 1.2)",
                    "bioModelId=1&bioModelId=2&includeShared=true".equals(lastQuery.get()), lastQuery.get());

            try {
                bioModels.createBioModel(new BioModelCreate("m", null));
                check("400 raises ApiException", false, "no exception");
            } catch (ApiException e) {
                Problem p = mapper.readValue(e.getResponseBody(), Problem.class);
                check("400 problem+json body parses into the Problem record",
                        e.getCode() == 400 && p.type().equals("/api/problems/validation") && p.errors().get(0).field().equals("vcml")
                                && p.requestId().equals("r-2"), p);
                check("request body serialized from a record (null fields omitted)", lastBody.get().equals("{\"name\":\"m\"}"), lastBody.get());
            }
            try {
                bioModels.getBioModel("missing");
                check("404 raises ApiException", false, "no exception");
            } catch (ApiException e) {
                Problem p = mapper.readValue(e.getResponseBody(), Problem.class);
                check("404 problem+json body parses", e.getCode() == 404 && p.status() == 404, p);
            }

            ExportJob job = exports.createExport(new CsvExport("sim-9", List.of("Ran_cyt", "RanC_nuc")));
            check("discriminated union (csv) serializes with its discriminator",
                    lastBody.get().contains("\"format\":\"csv\"") && lastBody.get().contains("\"variables\":[\"Ran_cyt\",\"RanC_nuc\"]"), lastBody.get());
            exports.createExport(new N5Export("sim-9", 4));
            check("discriminated union (n5) serializes", lastBody.get().contains("\"format\":\"n5\"") && lastBody.get().contains("\"compressionLevel\":4"), lastBody.get());
            check("job record deserializes", job.exportId().equals("e-1"), job);
            ExportRequest back = mapper.readValue("{\"format\":\"n5\",\"simulationId\":\"s\",\"compressionLevel\":2}", ExportRequest.class);
            check("discriminated union deserializes to the right record", back instanceof N5Export n && n.compressionLevel() == 2, back);

            org.vcell.restclient.api.SimulationDataApi data = new org.vcell.restclient.api.SimulationDataApi(client);
            TimeSeries ts = data.getTimeSeries("d1", "Ran_cyt", List.of("1.0,2.5", "3.0,4.0"));
            List<List<Double>> v = ts.values();
            check("Float64: finite number", v.get(0).get(0) == 1.25, v);
            check("Float64: null stays null (missing, guide 2.3)", v.get(0).get(1) == null, v);
            check("Float64: \"NaN\" string -> Double.NaN", v.get(1).get(0) != null && Double.isNaN(v.get(1).get(0)), v);
            check("Float64: \"Infinity\" / \"-Infinity\" strings -> +/-Infinity",
                    v.get(1).get(1) == Double.POSITIVE_INFINITY && v.get(2).get(0) == Double.NEGATIVE_INFINITY, v);
            check("coordinate tuples sent as repeated point=x,y (guide 1.2)",
                    lastQuery.get().equals("variable=Ran_cyt&point=1.0%2C2.5&point=3.0%2C4.0")
                    || lastQuery.get().equals("variable=Ran_cyt&point=1.0,2.5&point=3.0,4.0"), lastQuery.get());
            String out = mapper.writeValueAsString(new TimeSeries("x", List.of(0.0),
                    List.of(java.util.Arrays.asList(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, null))));
            check("Java writes non-finite as strings, null as null (valid JSON)",
                    out.contains("[\"NaN\",\"Infinity\",\"-Infinity\",null]"), out);

            try {
                BioModel r = bioModels.getBioModel("43");
                check("unknown ENUM value tolerated (maps to UNKNOWN)", r.curation() == Curation.UNKNOWN, r.curation());
            } catch (ApiException e) {
                check("unknown ENUM value tolerated (needs a rule, guide 6.1)", false, e.getMessage().split("\n")[0]);
            }
        } finally {
            server.stop(0);
        }
        System.out.println(failures == 0 ? "ALL PASSED" : failures + " FAILED");
    }
}
