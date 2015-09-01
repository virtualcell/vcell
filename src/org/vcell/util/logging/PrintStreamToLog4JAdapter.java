package org.vcell.util.logging;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class PrintStreamToLog4JAdapter extends OutputStream {
	private final Logger logger;
	private final Level logLevel;
	private final StringBuilder sb;
	

	public PrintStreamToLog4JAdapter(Logger logger, Level logLevel) {
		super();
		this.logger = logger;
		this.logLevel = logLevel;
		sb = new StringBuilder();
	}


	@Override
	public void write(int b) throws IOException {
		sb.append(b);
		// TODO Auto-generated method stub

	}

}
