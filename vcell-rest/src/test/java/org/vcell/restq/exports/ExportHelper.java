package org.vcell.restq.exports;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.export.server.ExportEnums;
import cbit.vcell.exports.ExportHistory;
import cbit.vcell.exports.ExportHistoryDBRep;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.*;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.ExportResourceApi;
import org.vcell.restclient.model.*;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.util.document.KeyValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ExportHelper {
    private static final Logger lg = LogManager.getLogger(ExportRequestTest.class);

    public static ExportEvent exportAndWait(ExportResourceApi exportResourceApi, N5ExportRequest n5ExportRequest) throws ApiException {
        final long time = Instant.now().getEpochSecond(); // Before export even starts, so that all events are grabbed from the queue
        long jobId = exportResourceApi.exportN5(n5ExportRequest);
        CompletableFuture<ExportEvent> future = CompletableFuture.supplyAsync(() -> {
            try{
                List<ExportEvent> allEvents = new ArrayList<>();
                while (allEvents.isEmpty()){
                    Thread.sleep(100);
                    allEvents = exportResourceApi.exportStatus(time);
                    lg.debug("The number of events found for time {} is 0. Retrying...", time);
                }
                ExportEvent eventUnderInspection = getLastEventForJob(allEvents, jobId);
//                Assertions.assertEquals(ExportEnums.ExportProgressType.EXPORT_ASSEMBLING, ExportEnums.ExportProgressType.valueOf(eventUnderInspection.getEventType().getValue()));
                while (ExportEnums.ExportProgressType.valueOf(eventUnderInspection.getEventType().getValue()) != ExportEnums.ExportProgressType.EXPORT_COMPLETE){
                    allEvents = exportResourceApi.exportStatus(time);
                    lg.debug("The events retrieved are: {}", allEvents);
                    eventUnderInspection = getLastEventForJob(allEvents, jobId);
                    lg.debug("The event under inspection is: {}", eventUnderInspection);
                    Thread.sleep(500);
                }
                Assertions.assertEquals(ExportProgressType.COMPLETE, eventUnderInspection.getEventType());
                return eventUnderInspection;
            } catch (Exception e){
                Assertions.fail();
            }
            throw new RuntimeException("This should never be reached");
        }).orTimeout(20, TimeUnit.SECONDS);
        return future.join();
    }

    private static ExportEvent getLastEventForJob(List<ExportEvent> events, long jobId){
        for (int i = events.size() - 1; i >= 0; i--){
            ExportEvent event = events.get(i);
            if(event.getJobID() == jobId){
                return event;
            }
        }
        throw new RuntimeException("Cannot find event for job id " + jobId);
    }

    public static N5ExportRequest getValidExportRequestDTO(int startTimeIndex, int endTimeIndex,
                                                           DataServerImpl dataServer, Simulation simulationID, BioModel bioModel) throws Exception {
        VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(simulationID.getKey(), TestEndpointUtils.administratorUser);
        VCSimulationDataIdentifier simulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
        DataIdentifier[] dataIdentifier = dataServer.getDataIdentifiers(new OutputContext(new AnnotatedFunction[0]), TestEndpointUtils.administratorUser, simulationDataIdentifier);
        DataIdentifier volumetricDataID = getOneDIWithSpecificType(VariableType.VOLUME, dataIdentifier);
        double[] allTimes = dataServer.getDataSetTimes(TestEndpointUtils.administratorUser, simulationDataIdentifier);
        StandardExportInfo exportRequest = new StandardExportInfo()
                .simulationKey(simulationID.getVersion().getVersionKey().toString())
                .simulationJob(0)
                .geometrySpecs(new GeometrySpecDTO().geometryMode(GeometryMode.FULL))
                .timeSpecs(
                        new org.vcell.restclient.model.TimeSpecs().beginTimeIndex(startTimeIndex).endTimeIndex(endTimeIndex).allTimes(Arrays.stream(allTimes).boxed().toList()).mode(TimeMode.RANGE)
                )
                .variableSpecs(
                        new org.vcell.restclient.model.VariableSpecs().variableNames(new ArrayList<>(){{add(volumetricDataID.getName());}}).mode(VariableMode.ONE)
                )
                .contextName("")
                .simulationName(vcSimulationIdentifier.getID())
                .bioModelKey(bioModel.getVersion().getVersionKey().toString())
                .mathModelKey(null)
                .mathDescriptionKey(bioModel.getSimulationContext(0).getMathDescription().getVersion().getVersionKey().toString())
                .outputContext(new ArrayList<>());

        return new N5ExportRequest().standardExportInformation(exportRequest)
                .exportableDataType(ExportableDataType.PDE_VARIABLE_DATA)
                .datasetName("testExport")
                .subVolume(new java.util.HashMap<>());
    }

    private static DataIdentifier getOneDIWithSpecificType(VariableType variableType, DataIdentifier[] dataIdentifiers){
        for(DataIdentifier dataIdentifier: dataIdentifiers){
            if(dataIdentifier.getVariableType().equals(variableType)){
                return dataIdentifier;
            }
        }
        throw new RuntimeException("Cannot find variable type " + variableType);
    }
}
