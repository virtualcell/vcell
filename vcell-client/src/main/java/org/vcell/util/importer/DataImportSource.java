package org.vcell.util.importer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.sbpax.util.StringUtil;
import org.vcell.util.BeanUtils;
import org.vcell.util.ClientTaskStatusSupport;


public abstract class DataImportSource {

	protected final String label;
	
	public DataImportSource(String label) { this.label = label; }
	
	public final String getLabel() { return label; }
	public int hashCode() { return label.hashCode(); }
	
	public boolean equals(Object o) {
		if(o instanceof DataImportSource) {
			return label.equals(((DataImportSource) o).getLabel());
		}
		return false;
	}
	
	public abstract String getBytes(ClientTaskStatusSupport clientTaskStatusSupport) throws IOException;
	
	public static class StringImportSource extends DataImportSource {

		protected final String data;

		public StringImportSource(String label, String data) {
			super(label);
			this.data = data;
		}

		public String getBytes(ClientTaskStatusSupport clientTaskStatusSupport) { return data; }
				
	}
	
	public static class FileImportSource extends DataImportSource {

		protected final File file;
		
		public FileImportSource(File file) { super(file.getAbsolutePath()); this.file = file; }
		
		public File getFile() { return file; }
		
		public String getBytes(ClientTaskStatusSupport clientTaskStatusSupport) throws IOException {
			return BeanUtils.readBytesFromFile(file, clientTaskStatusSupport);
		}
		
	}
	
	public static class URLImportSource extends DataImportSource {

		protected final URL url;
		
		public URLImportSource(URL url) { super(url.toString()); this.url = url; }
		
		public URL getURL() { return url; }
		
		public String getBytes(ClientTaskStatusSupport clientTaskStatusSupport) throws IOException {
			return BeanUtils.downloadBytes(url, clientTaskStatusSupport);
		}
		
	}
	
	public static class ResourceImportSource extends DataImportSource {
		
		protected final String resourceName;
		protected final Class<?> loaderClass;
		protected final String encoding;
		
		public ResourceImportSource(String label, String resourceName, Class<?> loaderClass) {
			this(label, resourceName, loaderClass, null);
		}
			
		public ResourceImportSource(String label, String resourceName, Class<?> loaderClass, String encoding) {
			super(label);
			this.resourceName = resourceName;
			this.loaderClass = loaderClass;
			this.encoding = encoding;
		}
		
		public String getBytes(ClientTaskStatusSupport clientTaskStatusSupport) throws IOException {
			String data = null;
			InputStream resourceStream = null;
			try {
				resourceStream = loaderClass.getResourceAsStream(resourceName);
				if(encoding != null) { 
					data = StringUtil.textFromInputStream(resourceStream, encoding); 
				} else { 
					data = StringUtil.textFromInputStream(resourceStream);
				}
			}finally{
				if (resourceStream != null){
					resourceStream.close();
				}
			}
			return data;
		}
		
	}
	
}
