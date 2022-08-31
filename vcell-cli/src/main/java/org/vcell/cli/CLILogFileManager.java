package org.vcell.cli;

import java.io.IOException;


public interface CLILogFileManager {

    public void writeDetailedErrorList(String message) throws IOException;

    public void writeFullSuccessList(String message) throws IOException;

    public void writeErrorList(String message) throws IOException;

    public void writeDetailedResultList(String message) throws IOException;

    // we make a list with the omex files that contain (some) spatial simulations (FVSolverStandalone solver)
    public void writeSpatialList(String message) throws IOException;

}
