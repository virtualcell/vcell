package org.vcell.util.logging;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.Logger;

public class PrintStreamToLog4JAdapter extends OutputStream {
	private final Logger logger;
	private final StringBuilder sb;
	

	public PrintStreamToLog4JAdapter(Logger logger) {
		super();
		this.logger = logger;
		sb = new StringBuilder();
	}


	@Override
	public void write(int b) throws IOException {
		sb.append(b);
		// TODO Auto-generated method stub

	}

}
