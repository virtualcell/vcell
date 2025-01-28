package org.vcell.cli.messaging;

import org.vcell.trace.Tracer;

public class CliTracer implements CLIRecordable {

    @Override
    public void writeDetailedErrorList(Exception e, String message) {
        Tracer.failure(e, "writeDetailedErrorList(): "+message);
    }
    @Override
    public void writeFullSuccessList(String message) {
        Tracer.success("writeFullSuccessList(): " + message);
    }
    @Override
    public void writeErrorList(Exception e, String message) {
        Tracer.failure(e, "writeErrorList(): " + message);
    }
    @Override
    public void writeDetailedResultList(String message) {
        Tracer.log("writeDetailedResultList(): "+message);
    }
    @Override
    public void writeSpatialList(String message) {
        Tracer.log("writeSpatialList(): "+message);
    }
    @Override
    public void writeImportErrorList(Exception e, String message) {
        Tracer.failure(e, "writeImportErrorList(): " + message);
    }
}
