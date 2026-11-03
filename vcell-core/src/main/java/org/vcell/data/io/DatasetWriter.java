package org.vcell.data.io;

import org.vcell.data.Dataset;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Strategy for turning a {@link Dataset} into a file. A writer only formats: what to write is decided beforehand by
 * {@link Dataset#select}, so no writer fetches data, evaluates functions or interprets export requests.
 */
public interface DatasetWriter {

	/** @return the file extension this writer produces, without the dot */
	String extension();

	void write(Dataset dataset, Path destination) throws IOException;
}
