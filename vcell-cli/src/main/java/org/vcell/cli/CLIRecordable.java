package org.vcell.cli;

import java.io.IOException;


public interface CLIRecordable {

    void writeDetailedErrorList(String message) throws IOException;

    void writeFullSuccessList(String message) throws IOException;

    void writeErrorList(String message) throws IOException;

    void writeDetailedResultList(String message) throws IOException;

    void writeDetailedSimBreakdown(String message) throws IOException;

    // we make a list with the omex files that contain (some) spatial simulations (FVSolverStandalone solver)
    void writeSpatialList(String message) throws IOException;

    void writeImportErrorList(String message) throws IOException;
}
