package org.vcell.cli.messaging;

import java.io.IOException;


public interface CLIRecordable {

    void writeDetailedErrorList(Exception e, String message) throws IOException;

    void writeFullSuccessList(String message) throws IOException;

    void writeErrorList(Exception e, String message) throws IOException;

    void writeDetailedResultList(String message) throws IOException;

    // we make a list with the omex files that contain (some) spatial simulations (FVSolverStandalone solver)
//    void writeSpatialList(String message) throws IOException;

    void writeImportErrorList(Exception e, String message) throws IOException;
}
