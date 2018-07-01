#@VCellHelper vh

import org.vcell.imagej.helper.VCellHelper;
import org.vcell.imagej.helper.VCellHelper.IJSolverStatus;
import org.vcell.imagej.helper.VCellHelper.BasicStackDimensions;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;

//long[] dims = [20,20,20,5];
//double[] stack = new double[20*20*20*5];
//ArrayImg<DoubleType, DoubleArray> test = ArrayImgs.doubles(stack, dims);
//ImageJFunctions.show( test );
//if(true){return}

    	IJSolverStatus ijSolverStatus = vh.startFrap();
    	println(ijSolverStatus.toString());
    	String simulationJobId = ijSolverStatus.simJobId;
    	while(true) {
    		Thread.sleep(5000);
        	ijSolverStatus =  vh.getSolverStatus(simulationJobId);
        	println(ijSolverStatus.toString());
        	String statusName = ijSolverStatus.statusName.toLowerCase();
        	if(statusName.equals("finished") || statusName.equals("stopped") || statusName.equals("aborted")) {
        		break;
        	}
    	}

    	URL url = new URL("http://localhost:"+vh.findVCellApiServerPort()+"/"+"getinfo"+"?"+"type"+"="+"quick");
    	Document doc = VCellHelper.getDocument(url);
    	println(VCellHelper.documentToString(doc));
    	NodeList nodes = doc.getElementsByTagName("simInfo");
    	String cachekey = nodes.item(0).getAttributes().getNamedItem("cacheKey").getNodeValue();
    	URL varInfoUrl = new URL("http://localhost:"+vh.findVCellApiServerPort()+"/"+"getdata"+"?"+"cachekey"+"="+cachekey);
    	doc = VCellHelper.getDocument(varInfoUrl);
    	println(VCellHelper.documentToString(doc));
    	double[] times = VCellHelper.getTimesFromVarInfos(doc);
	    String[] varNames = VCellHelper.getVarNamesFromVarInfos(doc);
	    double[] timeStack = null;
	    long[] xyztDims = null;
	    int thisManyTimes = times.length;
    	for(int timeIndex = 0;timeIndex<thisManyTimes;timeIndex++){
	    	URL dataUrl = new URL("http://localhost:"+vh.findVCellApiServerPort()+"/"+"getdata"+"?"+"cachekey"+"="+cachekey+"&"+"varname"+"="+"r"+"&"+"timeindex"+"="+(int)timeIndex+"&"+"jobid="+ijSolverStatus.getJobIndex());
	    	doc = VCellHelper.getDocument(dataUrl);
//	    	println(VCellHelper.documentToString(doc));
	    	nodes = doc.getElementsByTagName("ijData");
			BasicStackDimensions basicStackDimensions = VCellHelper.getDimensions(nodes.item(0));
	 		double[] data = VCellHelper.getData(nodes.item(0));
	   		println(timeIndex+" "+basicStackDimensions.getTotalSize()+" "+data.length);
	   		if(xyztDims == null){
	   			println("int stack and dims");
	   			timeStack = new double[thisManyTimes*data.length];
	   			xyztDims = [basicStackDimensions.xsize,basicStackDimensions.ysize,basicStackDimensions.zsize,thisManyTimes];
	   		}
	   		System.arraycopy(data,  0,timeStack, timeIndex*data.length,data.length);
	   		println("timeindex="+timeIndex+" copied to index="+(timeIndex*data.length)+" data length="+data.length);
    	}

		ArrayImg<DoubleType, DoubleArray> img = ArrayImgs.doubles(timeStack, xyztDims);
		ImageJFunctions.show( img );











