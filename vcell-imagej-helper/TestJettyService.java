package scrapbook;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.vcell.imagej.ImageJHelper;
import org.vcell.imagej.helper.VCellHelper;
import org.vcell.imagej.helper.VCellHelper.BasicStackDimensions;
import org.vcell.util.gui.DialogUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.imglib2.Dimensions;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;

public class TestJettyService {

	public static void main(String[] args) {
		try {
//			ImageJHelper.startService(8010);
			exerciseService();
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void printDocument(Document doc, OutputStream out) throws Exception {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	    transformer.transform(new DOMSource(doc),new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}
    public static void exerciseService() {
    	try {
//    	HttpClient httpClient = new HttpClient();
//    	HostConfiguration hostConfiguration = new HostConfiguration();
//    	hostConfiguration.setHost("localhost",8080);
//    	HttpMethod method = new GetMethod("/list?type=biom");
//			int var = httpClient.executeMethod(hostConfiguration, method);
//			System.out.println("result="+var);
//			
//			method = new GetMethod("/");
//			var = httpClient.executeMethod(hostConfiguration, method);
//			System.out.println("result="+var);
    		int lastVCellApiPort = VCellHelper.findVCellApiServerPort();//search for port that vcell is providing IJ related services on
    		System.out.println(VCellHelper.getApiInfo()+"\n");//get rest api

    		URL[] testUrls = new URL[] {
    				new URL("http://localhost:"+lastVCellApiPort+"/"+"getinfo"+"?"+"open=true"+"&"+"type"+"="+"quick"),
//    				new URL("http://localhost:"+lastVCellApiPort+"/"+"getinfo"+"?"+"open=true"+"&"+"type"+"="+"bm"),
//    				new URL("http://localhost:"+lastVCellApiPort+"/"+"getinfo"+"?"+"open=true"+"&"+"type"+"="+"mm"),
//    				new URL("http://localhost:"+lastVCellApiPort+"/"+"getinfo"+"?"+"open=false"+"&"+"type"+"="+"quick"),
//    				new URL("http://localhost:"+lastVCellApiPort+"/"+"getinfo"+"?"+"open=false"+"&"+"type"+"="+"bm"),
//    				new URL("http://localhost:"+lastVCellApiPort+"/"+"getinfo"+"?"+"open=false"+"&"+"type"+"="+"mm")
    		};
    		for (int i = 0; i < testUrls.length; i++) {
    			System.out.println("----------Test "+i+": "+testUrls[i].toString());
//    			System.out.println(VCellHelper.getRawContent(testUrls[i]));
				Document doc = VCellHelper.getDocument(testUrls[i]);
				printDocument(doc, System.out);
				Node simNode = doc.getElementsByTagName("simInfo").item(0);
				String cachekey = simNode.getAttributes().getNamedItem("cacheKey").getNodeValue();
				
				URL varInfoUrl = new URL("http://localhost:"+lastVCellApiPort+"/"+"getdata"+"?"+"cachekey"+"="+cachekey);
				System.out.println("  --varInfo="+varInfoUrl.toString());
				System.out.println(VCellHelper.getRawContent(varInfoUrl));
				doc = VCellHelper.getDocument(varInfoUrl);
				ArrayList<Double> times = new ArrayList<>();
				Node timeNode = doc.getElementsByTagName("times").item(0);
				StringTokenizer st = new StringTokenizer(timeNode.getTextContent(), ",");
				while(st.hasMoreTokens()) {
					StringTokenizer timepointT = new StringTokenizer(st.nextToken(), "='");
					timepointT.nextToken();
					times.add(Double.parseDouble(timepointT.nextToken()));
				}
				String varName = null;
				NodeList varNodes = doc.getElementsByTagName("ijVarInfo");
				for(int j=0;j<varNodes.getLength();j++) {
					if(varNodes.item(j).getAttributes().getNamedItem("variableType").getNodeValue().equals("Volume")) {
						varName = varNodes.item(j).getAttributes().getNamedItem("name").getNodeValue();
						break;
					}
				}
				
				URL dataUrl = new URL("http://localhost:"+lastVCellApiPort+"/"+"getdata"+"?"+"cachekey"+"="+cachekey+"&"+"varname"+"="+varName+"&"+"timepoint"+"="+times.get(times.size()/2)+"&"+"jobid=0");
				System.out.println("  --data="+dataUrl.toString());
				doc = VCellHelper.getDocument(dataUrl);
	    		BasicStackDimensions basicStackDimensions = VCellHelper.getVCStackDims(doc);
	     		double[] data = VCellHelper.getData(doc);
	       		System.out.println(basicStackDimensions.getTotalSize());
	       		System.out.println(data.length);
			}
    		if(true) {return;}
    		
    		int cachekey = 4;
    		System.out.println(VCellHelper.getApiInfo());//get rest api
//    		System.out.println(VCellHelper.getRawContent(new URL("http://localhost:"+lastVCellApiPort+"/"+"getinfo"+"?"+"open=true"+"&"+"type"+"="+"bm")));//generate cachekeys user can reference to get data
    		System.out.println(VCellHelper.getRawContent(new URL("http://localhost:"+lastVCellApiPort+"/"+"getdata"+"?"/*+"open=true"+"&"*/+"cachekey"+"="+cachekey)));//get variable names
    		Document doc = VCellHelper.getDocument(new URL("http://localhost:"+lastVCellApiPort+"/"+"getdata"+"?"+"cachekey"+"="+cachekey+"&"+"varname"+"="+"C_cyt"+"&"+"timepoint"+"=5.55394648006857"+"&"+"jobid=0"));//get data
    		BasicStackDimensions basicStackDimensions = VCellHelper.getVCStackDims(doc);
     		double[] data = VCellHelper.getData(doc);
       		System.out.println(basicStackDimensions.getTotalSize());
       		System.out.println(data.length);

       		long[] dims = new long[basicStackDimensions.numDimensions()];
       		basicStackDimensions.dimensions(dims);
       		ArrayImg<DoubleType, DoubleArray> img = ArrayImgs.doubles(data, dims);
//            ArrayImg<DoubleType, DoubleArray> img = (ArrayImg<DoubleType, DoubleArray>)new ArrayImgFactory< DoubleType >().create( basicStackDimensions, new DoubleType() );
//            ArrayCursor<DoubleType> cursor = img.cursor();
//            while(cursor.hasNext()) {
//            	cursor.next().set
//            }

            
//       		Img< UnsignedByteType > img = new ArrayImgFactory< UnsignedByteType >().create( new long[] { 400, 320 }, new UnsignedByteType() );
            ImageJFunctions.show( img );
            DialogUtils.showInfoDialog(JOptionPane.getRootFrame(), "blah");

//    		JAXBContext jaxbContext = JAXBContext.newInstance(IJData.class);
//    		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//    		IJData ijData = (IJData) jaxbUnmarshaller.unmarshal(new URL("http://localhost:8080/"+ApiEnum.getdata.name()+"?"/*+"open=true"+"&"*/+IJGetDataParams.cachekey.name()+"=0"));
//    		System.out.println(ijData);
    		
//			URLConnection con = url.openConnection();
//			InputStream in = con.getInputStream();
//			String encoding = con.getContentEncoding();
//			encoding = encoding == null ? "UTF-8" : encoding;
//			String body = IOUtils.toString(in, encoding);
//			System.out.println(body);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
