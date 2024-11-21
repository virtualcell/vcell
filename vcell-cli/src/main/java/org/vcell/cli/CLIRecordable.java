package org.vcell.cli;

import java.io.IOException;


public interface CLIRecordable {

    public void writeDetailedErrorList(Exception e, String message) throws IOException;

    public void writeFullSuccessList(String message) throws IOException;

    public void writeErrorList(Exception e, String message) throws IOException;

    public void writeDetailedResultList(String message) throws IOException;

    // we make a list with the omex files that contain (some) spatial simulations (FVSolverStandalone solver)
    public void writeSpatialList(String message) throws IOException;

    public void writeImportErrorList(Exception e, String message) throws IOException;
}
