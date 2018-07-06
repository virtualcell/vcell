#@VCellHelper vh
#@OpService op //This is a net.imagej.ops.OpEnvironment
#@ImageJ ij

import org.vcell.imagej.helper.VCellHelper;
import org.vcell.imagej.helper.VCellHelper.IJGeom;
import org.vcell.imagej.helper.VCellHelper.IJSolverStatus;
import org.vcell.imagej.helper.VCellHelper.BasicStackDimensions;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imagej.ops.OpInfo
import net.imagej.ops.OpMatchingService
import net.imagej.ops.OpRef
import net.imagej.ops.OpCandidate
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.logic.BitType;
import net.imglib2.FinalInterval;
import net.imglib2.util.Intervals;


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


//println(test2_Tmany_Zone.getClass().getName())
//String id = "/path/to/myFile.ext";
//ImagePlus[] imps = BF.openImagePlus(id);
//if(true){return}


//Load image
test2_Tmany_Zone = ij.io().open("C:/temp/ImportImageExamples/test2_Tmany_Zone.lsm")

//Define slice location of pre-bleach image (--------------------User has to determine location)
finterval = FinalInterval.createMinSize(132,146, 0,4, 244,165, 1,1);//xyzt:origin and xyzt:size
int xsize = finterval.dimension(0);
int ysize = finterval.dimension(1);
int zsize = finterval.dimension(2);
println(xsize+" "+ysize+" "+zsize);
//Extract pre-bleach image
slice = ij.op().transform().crop(test2_Tmany_Zone, finterval)

//Smooth slice to aid in creating threshold geometry
median = ij.op().run("create.img", slice)
neighborhood = new HyperSphereShape(4)
ij.op().run("filter.median", median, slice, neighborhood)

//Create threshold image as a BitType image array (--------------------User has to determine threshold)
bits = op.threshold().apply(/*bits,*/median,new UnsignedByteType(15))

//Create bitmap to send to VCell as geometry subvolume definition
byte[] geomBits = new byte[Intervals.numElements(finterval)];
mycursor = bits.localizingCursor();
int currIndex = 0;
while(mycursor.hasNext()){
	BitType nextbit = mycursor.next();
	geomBits[currIndex++] = nextbit.getInteger();
//	sb.append(nextbit.toString());
//	if(mycursor.getIntPosition(0)+finterval.min(0) == finterval.max(0)){
//		println()
//	}
}

//Define relevent simulation parameters
Double diffOverride = null;//new Double(".00001");
Double forwardBinding = null;
Double reverseBinding = null;

//Define new Geometry
subvolumeNames = ["cyt","Nuc"] as String[]
subvolumePixelValues = [0,1] as int[]
origin = [0,0,0] as double[];
extent = [xsize,ysize,zsize] as double[];
//public IJGeom(String[] subvolumeNames, int[] subvolumePixelValue, int xsize, int ysize, int zsize,double[] originXYZ, double[] extentXYZ, byte[] geom) throws Exception{
IJGeom ijGeom = new IJGeom(subvolumeNames /*["cyt","Nuc"]*/, subvolumePixelValues/*[0,1]*/, xsize, ysize, zsize, origin /*new double[] {0,0,0}*/, extent /*new double[] {22,22,10}*/, geomBits);

//Define Laser area of exposure
laserArea = "((x >=  180) && (x <= 190) && (y >=  85) && (y <= 90))";
//laserArea = "((x >=  - 7.4) && (x <= -3.96) && (y >=  - 4.4) && (y <= -3.96))";

//Define new Simulation endtime
newEndTime = new Double(10.0);

//Start Frap simulation
//public IJSolverStatus startFrap(Double rDiffusionOverride,Double kForwardBindingOverride,Double kReversBindingOverride,IJGeom ijGeom,String laserCoverageAnalyticExpression) throws Exception{
IJSolverStatus ijSolverStatus = vh.startFrap(diffOverride,forwardBinding,reverseBinding,ijGeom,laserArea,newEndTime);


//		ijSolverStatus = new IJSolverStatus();
//		ijSolverStatus.simJobId = "SimID_0123456789_0";    	
    	
//    	IJSolverStatus ijSolverStatus = vh.startFrap(diffOverride,null,null);
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
    	String cachekey = null;
    	for(int i=0;i<nodes.getLength();i++){
    		Node simIdNode = nodes.item(i).getAttributes().getNamedItem("simId");
    		if(simIdNode != null && simIdNode.getNodeValue() != null && simulationJobId.startsWith(simIdNode.getNodeValue())){
    			cachekey = nodes.item(i).getAttributes().getNamedItem("cacheKey").getNodeValue();
    			break;
    		}
    	}
    	if(cachekey == null){
    		IJ.showMessage("Couldn't find cacheKey for simulationJobId="+simulationJobId);
    		return;
    	}
    	println("cacheKey for simData="+cachekey)
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





 

//#@OpService ops
//#@ImageJ ij
//
////import ij.IJ;
//import net.imglib2.img.display.imagej.ImageJFunctions;
//import net.imglib2.FinalInterval;
//import net.imglib2.algorithm.neighborhood.HyperSphereShape;
//import net.imglib2.img.array.ArrayImgs;
//import net.imglib2.IterableInterval;
//import net.imglib2.type.numeric.integer.UnsignedByteType;
//import net.imglib2.FinalDimensions;
//import net.imglib2.type.logic.BitType;
//
//test2_Tmany_Zone = ij.io().open("C:/temp/ImportImageExamples/test2_Tmany_Zone.lsm")
////ops.run("transform.crop", test2_Tmany_Zone, Interval, boolean)
//finterval = FinalInterval.createMinSize(132,146, 0,4, 244,165, 1,1);//xyzt:origin and xyzt:size
//println(finterval.min(0)+" "+finterval.max(0))
//println(finterval.numDimensions());
//slice = ij.op().transform().crop(test2_Tmany_Zone, finterval)
//
//median = ij.op().run("create.img", slice)
//neighborhood = new HyperSphereShape(4)
//ij.op().run("filter.median", median, slice, neighborhood)
//
////bits = ArrayImgs.bits(244,165)
////boolean b1 = (bits instanceof Iterable);
////boolean b2 = (median instanceof Iterable);
////
////print(Iterable.class.getName()+" "+b1+" "+b2)
//bits = ops.threshold().apply(/*bits,*/median,new UnsignedByteType(15))
//println(bits.getClass().getName())
////if(true){return}
//
//geomBits = new STringBuffer();
//mycursor = bits.localizingCursor();
//while(mycursor.hasNext()){
//	BitType nextbit = mycursor.next();
//	sb.append(nextbit.toString());
////	if(mycursor.getIntPosition(0)+finterval.min(0) == finterval.max(0)){
////		println()
////	}
//}
//
//
//
////ops.run("create.imgPlus", Img, ImgPlusMetadata)
////imp = IJ.openImage("C:/temp/ImportImageExamples/test2_Tmany_Zone.lsm");
////ops.run("transform.crop", ImgPlus, Interval, boolean)
////ops.run("transform.crop", imp, Interval, boolean)
//
////imp = IJ.openImage("C:/temp/ImportImageExamples/test2_Tmany_Zone.lsm");
////imp.setRoi(168,124,193,176);
////IJ.run(imp, "Crop", "");
//
//ImageJFunctions.show( bits );








