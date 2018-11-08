package cbit.vcell.VirtualMicroscopy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ISize;
import org.vcell.vcellij.ImageDatasetReader;

import cbit.image.ImageSizeInfo;
import cbit.vcell.resource.ResourceUtil;

@Plugin(type = ImageDatasetReader.class)
public class BioformatsImageDatasetReader extends AbstractService implements ImageDatasetReader {
	
	public static final String IMAGEDATAMULTI = "imagedatamulti";
	public static final String IMAGEDATACHAN = "imagedatachan";
	public static final String IMAGEDATA = "imagedata";
	public static final String IMAGEINFO = "imageinfo";
	
	public static final String BIOF_XML_START_DELIM = "bioformats xml start:";
	public static final String BIOF_XML_END_DELIM = "bioformats xml end:";
	public static final String BIOF_XML_STATUS_START_DELIM = "bioformats status start:";
	public static final String BIOF_XML_STATUS_END_DELIM = "bioformats status end:";
	
	public static final String BIOF_XML_ERROR_START_DELIM = "bioformats error start:";
	public static final String BIOF_XML_ERROR_END_DELIM = "bioformats error end:";
	private static File bioformatsExecutableJarFile = null;
	
	public BioformatsImageDatasetReader() throws Exception{
		if (ResourceUtil.getBioFormatsExecutableJarFile() == null){
			ResourceUtil.downloadBioformatsJar();
		}
		bioformatsExecutableJarFile = ResourceUtil.getBioFormatsExecutableJarFile();
		if(bioformatsExecutableJarFile == null) {
			throw new Exception("Couldn't get BioFormats plugin");
		}
	}

//  mkdir bioformatsJarTempDir
//  cd bioformatsJarTempDir
//	ls -1 /home/vcell/workspace_biofthrift/vcell-bioformats/target/dependency/* | xargs -n 1 jar xf
//	ls -1 /home/vcell/workspace_biofthrift/vcell-bioformats/target/vcell-bioformats-0.0.4.jar | xargs -n 1 jar xf
//  rm -rf META-INF
//  mkdir META-INF
//  echo -e "Manifest-Version: 1.0\nMain-Class: org.vcell.bioformats.BioFormatsImageDatasetReaderFrm\n"
//	jar cf mynewbiof.jar .
	private String runpb(ArrayList<String> cmdList, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception{
		//DEBUGGING CODE
//		final Exception[] excHolder = new Exception[1];
//		System.out.println("\n--cmd begin--");
//		Arrays.stream(cmdList.toArray()).map(s->s+" ").forEach(System.out::print);
//		System.out.println("\n--cmd end--");
//		final PrintStream origstdout = System.out;
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		PrintStream newps = new PrintStream(baos) {
//			@Override
//			public void println(String x) {
//				try {
//					baos.write(x.getBytes());
////					origstdout.println("-----"+x);
//					detectException(x);
//					detectMessage(clientTaskStatusSupport, x);
//				}catch (Exception e) {
//					excHolder[0] = e;
//				}
//			}
//		};
//		try {
//			System.setOut(newps);
//			BioFormatsImageDatasetReaderFrm.main(cmdList.toArray(new String[0]));
//			String output = baos.toString();
//			if(excHolder[0] != null) {
//				throw excHolder[0];
//			}
//			output = output.substring(output.indexOf(BIOF_XML_START_DELIM)+BIOF_XML_START_DELIM.length()+1, output.indexOf(BIOF_XML_END_DELIM));
//			if(true) {return output;}
//		}finally {
//			System.setOut(origstdout);
//		}
		
		cmdList.add(0, "java");
		cmdList.add(1,"-jar");
		cmdList.add(2,
//			new File("/home/vcell/workspace_biofthrift/vcell-bioformats/target/vcell-bioformats-0.0.4.jar").getAbsolutePath()+":"+
			ResourceUtil.getBioFormatsExecutableJarFile().getAbsolutePath()
//			+":"+new File("/home/vcell/workspace_biofthrift/vcell/vcell-core/target/vcell-core-0.0.1-SNAPSHOT.jar").getAbsolutePath()+":"+
//			new File("/home/vcell/workspace_biofthrift/vcell/vcell-util/target/vcell-util-0.0.1-SNAPSHOT.jar").getAbsolutePath()+":"+
//			new File("/home/vcell/.m2/repository/javax/media/jai/com.springsource.javax.media.jai.core/1.1.3/com.springsource.javax.media.jai.core-1.1.3.jar").getAbsolutePath()
		);
//		cmdList.add(3,"org.vcell.bioformats.BioFormatsImageDatasetReaderFrm");//Main class name in vcell-bioformats project
		ProcessBuilder pb = new ProcessBuilder(cmdList);
		pb.redirectErrorStream(true);// out and err in one stream
		
		System.out.println("\n--cmd begin--");
		Arrays.stream(pb.command().toArray()).map(s->s+" ").forEach(System.out::print);
		System.out.println("\n--cmd end--");

		final Process p = pb.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		StringBuffer sb = new StringBuffer();
		while((line=br.readLine())!=null){
		   sb.append(line);
		   detectMessage(clientTaskStatusSupport, line);
		   detectException(line);
		}
		return sb.substring(sb.indexOf(BIOF_XML_START_DELIM)+BIOF_XML_START_DELIM.length(), sb.indexOf(BIOF_XML_END_DELIM));
	}

	private void detectException(String line) throws Exception {
		int statusStartIndex;
		int statusEndIndex;
		statusStartIndex = line.indexOf(BIOF_XML_ERROR_START_DELIM);
		   if(statusStartIndex != -1) {
			   statusEndIndex = line.indexOf(BIOF_XML_ERROR_END_DELIM);
			   if(statusEndIndex != -1) {
				   throw new Exception(line.substring(statusStartIndex+BIOF_XML_ERROR_START_DELIM.length(), statusEndIndex));
			   }
			   
		   }
	}

	private void detectMessage(ClientTaskStatusSupport clientTaskStatusSupport, String line) {
		int statusStartIndex;
		int statusEndIndex;
		if(clientTaskStatusSupport != null) {
			   statusStartIndex = line.indexOf(BIOF_XML_STATUS_START_DELIM);
			   if(statusStartIndex != -1) {
				   statusEndIndex = line.indexOf(BIOF_XML_STATUS_END_DELIM);
				   if(statusEndIndex != -1) {
					   clientTaskStatusSupport.setMessage(line.substring(statusStartIndex+BIOF_XML_STATUS_START_DELIM.length(), statusEndIndex));
				   }
			   }
		   }
	}
	
	public static Object createInstanceFromXml(String xml,Class instanceClass) throws JAXBException {
		JAXBContext jaxbContext1 = JAXBContext.newInstance(new Class[] {instanceClass});
		InputStream targetStream = new ByteArrayInputStream(xml.getBytes());
		StreamSource ss = new StreamSource(targetStream);
		Unmarshaller createUnmarshaller = jaxbContext1.createUnmarshaller();
		Object obj = createUnmarshaller.unmarshal(ss,instanceClass).getValue();
		return obj;
	}

	@Override
	public ImageSizeInfo getImageSizeInfoForceZ(String fileName, Integer forceZSize) throws Exception {
		ArrayList<String> argsStr = new ArrayList<>(Arrays.asList(new String[] { 
			IMAGEINFO,
			fileName
		}));
		if(forceZSize != null) {
			argsStr.add(forceZSize+"");
		}
		String xml = runpb(argsStr, null);
		return (ImageSizeInfo)createInstanceFromXml(xml,ImageSizeInfo.class);
	}

	@Override
	public ImageDataset readImageDataset(String imageID, ClientTaskStatusSupport status) throws Exception {
		ArrayList<String> argsStr = new ArrayList<>(Arrays.asList(new String[] { 
				IMAGEDATA,
				imageID
			}));
			String xml = runpb(argsStr, status);
			return (ImageDataset)createInstanceFromXml(xml, ImageDataset.class);
	}

	@Override
	public ImageDataset[] readImageDatasetChannels(String imageID, ClientTaskStatusSupport status,
			boolean bMergeChannels, Integer timeIndex, ISize resize) throws Exception {
		ArrayList<String> argsStr = new ArrayList<>(Arrays.asList(new String[] { 
				IMAGEDATACHAN,
				imageID
			}));
		argsStr.add(Boolean.toString(bMergeChannels));
		argsStr.add((timeIndex==null?"null":timeIndex+""));
		argsStr.add((resize==null?"null":resize.getX()+","+resize.getY()+","+resize.getZ()));
			String xml = runpb(argsStr, status);
			return ((ImageDatasets)createInstanceFromXml(xml, ImageDatasets.class)).getImageDatasets();
	}

	@Override
	public ImageDataset readImageDatasetFromMultiFiles(File[] files, ClientTaskStatusSupport status,
			boolean isTimeSeries, double timeInterval) throws Exception {
		ArrayList<String> argsStr = new ArrayList<>(Arrays.asList(new String[] { 
				IMAGEDATAMULTI
			}));
		for (int i = 0; i < files.length; i++) {
			argsStr.add(files[i].getAbsolutePath());
		}
		argsStr.add(Boolean.toString(isTimeSeries));
		argsStr.add(timeInterval+"");
		String xml = runpb(argsStr, status);
		return (ImageDataset)createInstanceFromXml(xml, ImageDataset.class);
	}

}
