package org.vcell.restq.exports;

import com.nimbusds.oauth2.sdk.ParseException;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.ExportResourceApi;
import org.vcell.restclient.auth.InteractiveLogin;
import org.vcell.restclient.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;

public class ManualTestExport {
    public static void main(String[] args) throws URISyntaxException, IOException, ParseException, ApiException, InterruptedException {
        int numOfExports = 12;

        ApiClient apiClient = InteractiveLogin.login(new URI(InteractiveLogin.authDomain),
                new URI("https://minikube.remote"), true);
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
        ExportResourceApi exportResourceApi = new ExportResourceApi(apiClient);
        StandardExportInfo standardExportInformation = new StandardExportInfo();
        standardExportInformation.simulationJob(0);
        standardExportInformation.simulationKey("264891976");
        standardExportInformation.simulationName("Frap");
        standardExportInformation.timeSpecs(new TimeSpecs()
                .mode(TimeMode.RANGE)
                .beginTimeIndex(0)
                .endTimeIndex(1)
                .allTimes(new ArrayList<>(){{add(0.0); add(0.05); add(0.1);}} ));
        standardExportInformation.variableSpecs(new VariableSpecs()
                .variableNames(new ArrayList<>(){{add("Dex");}})
                .mode(VariableMode.ONE)
        );
        standardExportInformation.geometrySpecs(new GeometrySpecDTO().geometryMode(GeometryMode.FULL));

        N5ExportRequest n5ExportRequest = new N5ExportRequest();
        n5ExportRequest.standardExportInformation(standardExportInformation);
        n5ExportRequest.exportableDataType(ExportableDataType.PDE_VARIABLE_DATA);
        n5ExportRequest.subVolume(new java.util.HashMap<>());


        for (int i = 0; i < numOfExports; i++) {
            n5ExportRequest.datasetName("testExport_" + i);
            System.out.println("Starting export " + (i + 1));
            exportResourceApi.exportN5(n5ExportRequest);
        }

        while (true){
            int numOfExportsCompleted = 0;
            try {
                Thread.sleep(1000);
                System.out.println("---------Checking export status...---------");
                Set<ExportEvent> exportStatus = exportResourceApi.exportStatus();
                for (ExportEvent exportEvent : exportStatus) {
                    if (exportEvent.getEventType() == ExportProgressType.COMPLETE) {
                        numOfExportsCompleted++;
                        System.out.println("Export completed: " + exportEvent.getDataIdString());
                    } else if (exportEvent.getEventType() == ExportProgressType.START || exportEvent.getEventType() == ExportProgressType.PROGRESS || exportEvent.getEventType() == ExportProgressType.ASSEMBLING) {
                        System.out.println("Export in progress: " + exportEvent.getProgress());
                    }
                    else if (exportEvent.getEventType() == ExportProgressType.FAILURE) {
                        System.err.println("Export failed: " + exportEvent.getDataKey());
                    }
                    if (numOfExportsCompleted == numOfExports) {
                        System.out.println("All exports completed successfully.");
                        return;
                    }
                }
            } catch (ApiException e) {
                System.err.println("API Exception: " + e.getMessage());
            }
        }

    }
}
