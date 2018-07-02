#@VCellHelper vh
#@OpService op //This is a net.imagej.ops.OpEnvironment

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
import net.imagej.ops.OpInfo
import net.imagej.ops.OpMatchingService
import net.imagej.ops.OpRef
import net.imagej.ops.OpCandidate




//Collection<OpInfo> opInfos = op.infos();
//for(OpInfo opInfo:opInfos){
//	println(opInfo.getName());
//}
//OpMatchingService matcher = op.matcher();
//ArrayImg<DoubleType, DoubleArray> img0;
//Double d = new Double("100.5");
//List<OpCandidate> opCandidates = matcher.findCandidates(op,OpRef.create("stats.mean"));
//for(OpCandidate opCandidate:opCandidates){
//	println(opCandidate);
//}
//something = op.image();
//if(true){return}


//		ijSolverStatus = new IJSolverStatus();
//		ijSolverStatus.simJobId = "SimID_0123456789_0";

		Double diffOverride = new Double(".00001");
    	IJSolverStatus ijSolverStatus = vh.startFrap(diffOverride,null,null);
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
		println(op.stats().mean(img))// 'op' is a net.imagej.ops.OpEnvironment











