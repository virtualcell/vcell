package org.vcell.util.executable;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;

public class ClosedInput extends PipedProcessHandler implements IProcessInput {

	@Override
	public void close() throws IOException {

	}

	@Override
	public Redirect source() {
		 return Redirect.PIPE;
	}

	@Override
	public void set(OutputStream outputStream, String label) {
		name = label;
		try {
			outputStream.close();
		} catch (IOException e) {
			throw new RuntimeException("problem closing standard in",e);
		}
	}

}
