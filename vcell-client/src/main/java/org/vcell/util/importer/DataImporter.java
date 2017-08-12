package org.vcell.util.importer;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.vcell.sybil.util.JavaUtil;
import org.vcell.util.ClientTaskStatusSupport;


public class DataImporter {

	protected DataImportSource source = null;
	protected String data = null;
	
	public DataImportSource getSource() { return source; }
	
	public void selectSource(DataImportSource source) {
		if(!JavaUtil.equals(source, this.source)) {
			reset();
			this.source = source;
		}
	}
	
	public void reset() { 
		source = null;
		data = null;
	}
	
	public void selectFile(File file) { selectSource(new DataImportSource.FileImportSource(file)); }
	public void selectURL(URL url) { selectSource(new DataImportSource.URLImportSource(url)); }
	public void selectResource(String path, String description) { 
		selectSource(new DataImportSource.ResourceImportSource(description, path, this.getClass())); 
	}
	
	public String readData(ClientTaskStatusSupport clientTaskStatusSupport) throws IOException {
		data = source.getBytes(clientTaskStatusSupport);
		return data;
	}
	
	public String getPreviouslyReadData() { return data; }
	public String getLabel() { return source != null ? source.getLabel() : null; }
	
}
