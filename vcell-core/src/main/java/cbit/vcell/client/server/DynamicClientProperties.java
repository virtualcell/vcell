package cbit.vcell.client.server;

import cbit.vcell.resource.PropertyLoader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.http.HttpClient;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Map;
import java.util.TreeMap;

public class DynamicClientProperties {
    private static final Logger lg = LogManager.getLogger(DynamicClientProperties.class);

    private static VCellDynamicProps vcellDynamicProps = new VCellDynamicProps(null);


    public static synchronized void updateDynamicClientProperties(URL vcell_dynamic_properties_url, Duration timeout) {
        try {
            boolean bIgnoreHostMismatch = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreHostMismatch, false);
            boolean bIgnoreCertProblems = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreCertProblems, false);
            boolean ignoreCertProblems = bIgnoreCertProblems || bIgnoreHostMismatch;
            HttpClient client;
            if (ignoreCertProblems) {
                // Create a trust manager that does not validate certificate chains
                TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                            public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                        }
                };

                // Install the all-trusting trust manager
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                // Create an HttpClient that uses the custom SSL context
                client = HttpClient.newBuilder()
                        .sslContext(sslContext)
                        .build();
            } else {
                // Use default SSL verification
                client = HttpClient.newHttpClient();
            }

            // Send a GET request
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(vcell_dynamic_properties_url.toURI())
                    .timeout(timeout)
                    .GET()
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            // Parse the CSV response
            Reader in = new StringReader(response.body());
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withIgnoreSurroundingSpaces()
                    .withQuote('"')
                    .parse(in);
            Map<String,String> temp = new TreeMap<>();
            for (CSVRecord record : records) {
                temp.put(record.get(0), record.get(1));
            }
            DynamicClientProperties.vcellDynamicProps = new VCellDynamicProps(temp);
        } catch (Exception e) {
            lg.error("Unexpected error occurred while getting client properties: " + e.getMessage(), e);
        }
    }

    public static synchronized VCellDynamicProps getDynamicClientProperties(){
        return DynamicClientProperties.vcellDynamicProps;
    }

    public static class VCellDynamicProps {
        private final Map<String, String> dynamicClientProperties;

        private VCellDynamicProps(Map<String, String> dynamicClientProperties){
            super();
            this.dynamicClientProperties = dynamicClientProperties;
        }

        public String getProperty(String propertyName){
            return (this.dynamicClientProperties == null ? null : this.dynamicClientProperties.get(propertyName));
        }
    }
}
