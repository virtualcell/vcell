package org.vcell.data.io;

import org.vcell.data.Dataset;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Strategy for formats that bundle several datasets into one file, such as the BioSimulators HDF5 report file that
 * holds every report of every SED-ML document in an archive. Like {@link DatasetWriter}, it only formats.
 */
public interface DatasetCollectionWriter {

	/** @return the file extension this writer produces, without the dot */
	String extension();

	void write(List<Dataset> datasets, Path destination) throws IOException;
}
