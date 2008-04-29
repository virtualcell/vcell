package org.vcell.sbml;

import java.io.File;
import java.io.IOException;

public interface SBMLSolver {
	public File solve(String filePrefix, File outDir, String sbmlText, SimSpec testSpec) throws IOException, SolverException, SbmlException;
	public String getResultsFileColumnDelimiter();
	public void open() throws SolverException;
	public void close() throws SolverException;
}
