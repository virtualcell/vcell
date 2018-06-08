package org.vcell.imagej.helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.imagej.ImageJService;
import net.imglib2.Dimensions;

@Plugin(type = Service.class)
public class VCellHelper extends AbstractService implements ImageJService
{	
	private static int lastVCellApiPort = -1;
	
//	public VCellHelper() {
//		
//	}
//    public static void main( String[] args )
//    {
//    	ImageJ ij = new ImageJ();
//    }
    
	public static int findVCellApiServerPort() throws Exception{
		final int start = 8000;
		final int end = 8100;
		int tryCount = end-start+1;
		for(int i = lastVCellApiPort;i<=end;i++) {
			if(i == -1) {
				i= start;
			}
	        try (Socket socket = new Socket();){
				socket.connect(new InetSocketAddress("localhost", i), 100);
				socket.close();
				String response = getRawContent(new URL("http://localhost:"+i+"/hello"));
				if(response.contains("VCellApi")) {
					lastVCellApiPort = i;
					break;
				}
			} catch (IOException e) {
				//ignore, continue scanning
			}
			if(i == end) {
				i = start-1;
			}
			if(--tryCount == 0) {
				throw new Exception("Couldn't find VCellApi Server between ports "+start+" and "+end);
			}
		}
		return lastVCellApiPort;
		
	}
	public static String getApiInfo() throws Exception{
		findVCellApiServerPort();
		URL url = new URL("http://localhost:"+lastVCellApiPort+"/");
	//	URL url = new URL("http://localhost:8080/list");
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		int responseCode = con.getResponseCode();
		if(responseCode == HttpURLConnection.HTTP_OK) {
			BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
	        int currChar;
	        StringBuffer sb = new StringBuffer();
	        while ((currChar = bis.read()) != -1) {
	            sb.append((char)currChar);
	        }
	        return sb.toString();
		}else {
			throw new Exception("Expecting OK but got "+responseCode+" "+con.getResponseMessage());
		}

//		InputStream in = con.getInputStream();
//		String encoding = con.getContentEncoding();
//		encoding = encoding == null ? "UTF-8" : encoding;
//		String body = IOUtils.toString(in, encoding);
//		System.out.println(body);

	}
	
	public static Document getDocument(URL url) throws Exception{
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		int responseCode = con.getResponseCode();
		if(responseCode == HttpURLConnection.HTTP_OK) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document doc = docBuilder.parse(url.toString());
			return doc;
		}else {
//			throw new Exception("Expecting OK but got "+responseCode+" "+con.getResponseMessage());
			throw new Exception("Expecting OK but got "+responseCode+" "+streamToString(con.getErrorStream()));
		}
	}
	public static class BasicStackDimensions implements Dimensions{
		public int xsize;
		public int ysize;
		public int zsize;
		public int csize;
		public int tsize;
		public BasicStackDimensions(int xsize, int ysize, int zsize, int csize, int tsize) {
			this.xsize = xsize;
			this.ysize = ysize;
			this.zsize = zsize;
			this.csize = csize;
			this.tsize = tsize;
		}
		public int getTotalSize(){
			return xsize*ysize*zsize*csize*tsize;
		}
		@Override
		public int numDimensions() {
			int numdims = (xsize>1?1:0)+(ysize>1?1:0)+(zsize>1?1:0)+(csize>1?1:0)+(tsize>1?1:0);
			return (numdims==0?1:numdims);
		}
		@Override
		public void dimensions(long[] dimensions) {
			for(int i=0;i<dimensions.length;i++) {
				dimensions[i] = dimension(i);
			}
		}
		@Override
		public long dimension(int d) {
			switch(d) {
			case 0:
				return xsize;
			case 1:
				return ysize;
			case 2:
				return zsize;
			case 3:
				return csize;
			case 4:
				return tsize;
			default:
				throw new RuntimeException("dim index must be les than "+numDimensions());
			}
		}
	}
	public static BasicStackDimensions getVCStackDims(Document doc) throws Exception{
		NodeList si = (NodeList)doc.getElementsByTagName("stackInfo");
		Node node = (Node)si.item(0);
		NamedNodeMap nnm = node.getAttributes();
		return new BasicStackDimensions(
				Integer.parseInt(nnm.getNamedItem("xsize").getNodeValue()),
				Integer.parseInt(nnm.getNamedItem("ysize").getNodeValue()),
				Integer.parseInt(nnm.getNamedItem("zsize").getNodeValue()),
				Integer.parseInt(nnm.getNamedItem("csize").getNodeValue()),
				Integer.parseInt(nnm.getNamedItem("tsize").getNodeValue()));
	}
	public static double[] getData(Document doc) throws Exception{
			double[] doubleVals = new double[getVCStackDims(doc).getTotalSize()];
			DoubleBuffer db = ByteBuffer.wrap(Base64.getDecoder().decode(doc.getElementsByTagName("data").item(0).getFirstChild().getNodeValue())).asDoubleBuffer();
			db.get(doubleVals);
			return doubleVals;
	}
	
	public static String getRawContent(URL url) throws Exception{
		URLConnection con = url.openConnection();
		return streamToString(con.getInputStream());
//		try(InputStream instrm = con.getInputStream()){				
//		    StringBuilder textBuilder = new StringBuilder();
//			Reader reader = new BufferedReader(new InputStreamReader(instrm, Charset.forName(StandardCharsets.UTF_8.name())));
//			int c = 0;
//			while ((c = reader.read()) != -1) {
//			    textBuilder.append((char) c);
//			}
//			return textBuilder.toString();
//		}
	}
	
	public static String streamToString(InputStream stream) throws Exception{
		try(InputStream instrm = stream){				
		    StringBuilder textBuilder = new StringBuilder();
			Reader reader = new BufferedReader(new InputStreamReader(instrm, Charset.forName(StandardCharsets.UTF_8.name())));
			int c = 0;
			while ((c = reader.read()) != -1) {
			    textBuilder.append((char) c);
			}
			return textBuilder.toString();
		}
		
	}
//    public static String getApiInfo() throws Exception{
//        String responseBody = null;
//        try (CloseableHttpClient httpclient = HttpClients.createDefault()){
//            HttpGet httpget = new HttpGet("http://localhost:8080/");
//
//            System.out.println("Executing request " + httpget.getRequestLine());
//
//            // Create a custom response handler
//            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
//
//                @Override
//                public String handleResponse(
//                        final HttpResponse response) throws ClientProtocolException, IOException {
//                    int status = response.getStatusLine().getStatusCode();
//                    if (status >= 200 && status < 300) {
//                        HttpEntity entity = response.getEntity();
//                        return entity != null ? EntityUtils.toString(entity) : null;
//                    } else {
//                        throw new ClientProtocolException("Unexpected response status: " + status);
//                    }
//                }
//
//            };
//            responseBody = httpclient.execute(httpget, responseHandler);
////            System.out.println("----------------------------------------");
////            System.out.println(responseBody);
//        	return responseBody;
//        }
//    }

//	import java.io.StringReader;
//	import java.io.StringWriter;
//
//	import javax.xml.parsers.DocumentBuilder;
//	import javax.xml.parsers.DocumentBuilderFactory;
//	import javax.xml.transform.OutputKeys;
//	import javax.xml.transform.Transformer;
//	import javax.xml.transform.TransformerException;
//	import javax.xml.transform.TransformerFactory;
//	import javax.xml.transform.dom.DOMSource;
//	import javax.xml.transform.stream.StreamResult;
//
//	import org.w3c.dom.Document;
//	import org.xml.sax.InputSource;
//
//	public class StringToDocumentToString {
//
//	    public static void main(String[] args) {
//	        final String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"+
//	                                "<Emp id=\"1\"><name>Pankaj</name><age>25</age>\n"+
//	                                "<role>Developer</role><gen>Male</gen></Emp>";
//	        Document doc = convertStringToDocument(xmlStr);
//	        
//	        String str = convertDocumentToString(doc);
//	        System.out.println(str);
//	    }
//
//	    private static String convertDocumentToString(Document doc) {
//	        TransformerFactory tf = TransformerFactory.newInstance();
//	        Transformer transformer;
//	        try {
//	            transformer = tf.newTransformer();
//	            // below code to remove XML declaration
//	            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//	            StringWriter writer = new StringWriter();
//	            transformer.transform(new DOMSource(doc), new StreamResult(writer));
//	            String output = writer.getBuffer().toString();
//	            return output;
//	        } catch (TransformerException e) {
//	            e.printStackTrace();
//	        }
//	        
//	        return null;
//	    }
//
//	    private static Document convertStringToDocument(String xmlStr) {
//	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
//	        DocumentBuilder builder;  
//	        try  
//	        {  
//	            builder = factory.newDocumentBuilder();  
//	            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
//	            return doc;
//	        } catch (Exception e) {  
//	            e.printStackTrace();  
//	        } 
//	        return null;
//	    }
//
//	}
	
//  public static void exerciseService() {
//	try {
////	HttpClient httpClient = new HttpClient();
////	HostConfiguration hostConfiguration = new HostConfiguration();
////	hostConfiguration.setHost("localhost",8080);
////	HttpMethod method = new GetMethod("/list?type=biom");
////		int var = httpClient.executeMethod(hostConfiguration, method);
////		System.out.println("result="+var);
////		
////		method = new GetMethod("/");
////		var = httpClient.executeMethod(hostConfiguration, method);
////		System.out.println("result="+var);
//
//		int lastVCellApiPort = VCellHelper.findVCellApiServerPort();//search for port that vcell is providing IJ related services on
//		int cachekey = 1;
//		System.out.println(VCellHelper.getApiInfo());//get rest api
////		System.out.println(VCellHelper.getRawContent(new URL("http://localhost:"+lastVCellApiPort+"/"+"getinfo"+"?"/*+"open=true"+"&"*/+"type"+"="+"quick")));//generate cachekeys user can reference to get data
//		System.out.println(VCellHelper.getRawContent(new URL("http://localhost:"+lastVCellApiPort+"/"+"getdata"+"?"/*+"open=true"+"&"*/+"cachekey"+"="+cachekey)));//get variable names
//		Document doc = VCellHelper.getDocument(new URL("http://localhost:"+lastVCellApiPort+"/"+"getdata"+"?"+"cachekey"+"="+cachekey+"&"+"varname"+"="+"C_cyt"+"&"+"timepoint"+"=0.5"));//get data
//		BasicStackDimensions basicStackDimensions = VCellHelper.getVCStackDims(doc);
// 		double[] data = VCellHelper.getData(doc);
//   		System.out.println(basicStackDimensions.getTotalSize());
//   		System.out.println(data.length);
//
//   		long[] dims = new long[basicStackDimensions.numDimensions()];
//   		basicStackDimensions.dimensions(dims);
//   		ArrayImg<DoubleType, DoubleArray> img = ArrayImgs.doubles(data, dims);
////        ArrayImg<DoubleType, DoubleArray> img = (ArrayImg<DoubleType, DoubleArray>)new ArrayImgFactory< DoubleType >().create( basicStackDimensions, new DoubleType() );
////        ArrayCursor<DoubleType> cursor = img.cursor();
////        while(cursor.hasNext()) {
////        	cursor.next().set
////        }
//
//        
////   		Img< UnsignedByteType > img = new ArrayImgFactory< UnsignedByteType >().create( new long[] { 400, 320 }, new UnsignedByteType() );
//        ImageJFunctions.show( img );
//        DialogUtils.showInfoDialog(JOptionPane.getRootFrame(), "blah");
//
////		JAXBContext jaxbContext = JAXBContext.newInstance(IJData.class);
////		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
////		IJData ijData = (IJData) jaxbUnmarshaller.unmarshal(new URL("http://localhost:8080/"+ApiEnum.getdata.name()+"?"/*+"open=true"+"&"*/+IJGetDataParams.cachekey.name()+"=0"));
////		System.out.println(ijData);
//		
////		URLConnection con = url.openConnection();
////		InputStream in = con.getInputStream();
////		String encoding = con.getContentEncoding();
////		encoding = encoding == null ? "UTF-8" : encoding;
////		String body = IOUtils.toString(in, encoding);
////		System.out.println(body);
//	} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//}

}
