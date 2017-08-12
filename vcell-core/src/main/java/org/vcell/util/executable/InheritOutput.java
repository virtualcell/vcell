package org.vcell.util.executable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;

/**
 * set standard out / error to same as parent process
 */
public class InheritOutput implements IProcessOut {

	@Override
	public void close() throws IOException {

	}

	@Override
	public Redirect destination() {
		return Redirect.INHERIT;
	}

	@Override
	public void set(InputStream inputStream, String label) {

	}
}
