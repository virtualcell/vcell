package org.vcell.imagej.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import org.vcell.imagej.helper.VCellHelper.BasicStackDimensions;
import org.vcell.imagej.helper.VCellHelper.IJGeom;
import org.vcell.imagej.helper.VCellHelper.IJSolverStatus;
//import org.vcell.imagej.helper.VCellHelper.BasicStackDimensions;
//import org.vcell.imagej.helper.VCellHelper.IJGeom;
//import org.vcell.imagej.helper.VCellHelper.IJSolverStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.scif.img.ImgOpener;
import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Intervals;
import net.imglib2.view.IterableRandomAccessibleInterval;

public class CR {

	public static void main(String[] args) {
		try {
			testSolver2();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static void testSolver2() throws Exception{
    	ImageJ ij = new ImageJ();//make sure this is the 'net.imagej' version and not the 'ij' version
ij.ui().showUI();
    	VCellHelper vh = new VCellHelper();//Communicates with VCell
    	
    	//Load image
//	List<SCIFIOImgPlus<UnsignedByteType>> openImgs = new ImgOpener().openImgs( "C:/temp/ImportImageExamples/test2_Tmany_Zone.lsm",new UnsignedByteType());
	Img< UnsignedByteType > test2_Tmany_Zone = new ImgOpener().openImg( "C:/temp/ImportImageExamples/test2_Tmany_Zone.lsm",new UnsignedByteType());
//	ImgPlus<? extends RealType<?>> imgPlus = ((Dataset) ij.io().open("C:/temp/ImportImageExamples/test2_Tmany_Zone.lsm")).getImgPlus();
//	ImagePlus[] bfImgPlus = BF.openImagePlus("C:/temp/ImportImageExamples/test2_Tmany_Zone.lsm");
showAndZoom(ij, "Exp Data", test2_Tmany_Zone, .5);
	//Crop and scale all slices
	double[] scaleFactors = new double[] {.25,.25,1.0};
	long origSliceCount = test2_Tmany_Zone.dimension(3);
	long[] roiCropAll =  new long[] {132,144, 0,0, 237,167, 1,origSliceCount};//xyzt:origin and xyzt:size
	FinalInterval cropAllLocation = FinalInterval.createMinSize(roiCropAll);
	RandomAccessibleInterval<UnsignedByteType> cropAllSlices = ij.op().transform().crop(test2_Tmany_Zone, cropAllLocation);
	RandomAccessibleInterval<UnsignedByteType> cropAllScaled = ij.op().transform().scale(cropAllSlices, scaleFactors, interpolator);
showAndZoom(ij, "Exp Data Crop", cropAllSlices, 2);
showAndZoom(ij, "Exp Data Scaled", cropAllScaled, 4);

    	//Run multiple VCell sims of the 'frap' model using different diffusion rates
    	ArrayList<Double> results = new ArrayList();
    	double[] diffRates = new double[] {
    			.00001,
    			.4,
    			.8,
    			1.2,
    			1.6,
    			2.0,
    			2.4,
    			2.8,
    			3.2,
    			3.6,
    			4.0
    			};
    	for(int i=0;i<diffRates.length;i++){
    		double result = runSim(vh,ij,diffRates[i],"rf",cropAllScaled);
    		results.add(result);
    	}

    	for(int i=0;i<diffRates.length;i++){
    		System.out.println("diffRate="+diffRates[i]+" liicq="+(results.get(i)));
    	}

    }
    
    public static void showAndZoom(ImageJ ij,String displayName,Object thingToDisplay,double zoomFactor) throws Exception{
    	if(!displayName.equals("Exp Data Scaled") && !displayName.startsWith("Sim Data")) {
    		return;
    	}
    	SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
		    	String fileName = displayName.replace(" ", "_");
//		    	ij.io().save(thingToDisplay, new File("C:/temp/ImportImageExamples/vcij_images","a_"+fileName).getAbsolutePath());
		    	//Display<?> bitDisplay = ij.imageDisplay().getDisplayService().createDisplay("subvolumes",bits);
		    	ij.ui().show(displayName,thingToDisplay);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	//((DefaultImageDisplay)ij.imageDisplay().getDisplayService().getDisplay("subvolumes")).getCanvas().setZoom(2.0);
		    	ij.imageDisplay().getActiveImageDisplay().getCanvas().setZoom(zoomFactor);
		    	//ij.command().run(ZoomSet.class, true, new Object[] {"zoomPercent",200.0,"centerU",(cropScaleXSize/2),"centerV",(cropScaleYSize/2)});
			}
		});

    }
	public static NearestNeighborInterpolatorFactory<UnsignedByteType> interpolator = new NearestNeighborInterpolatorFactory<>();
	private static enum Axes {X,Y,Z};
    public static double  runSim(VCellHelper vh,ImageJ ij,double diffusionRate,String varToAnalyze,RandomAccessibleInterval<UnsignedByteType> cropAllScaled) throws Exception{

    	long cropScaleXSize = cropAllScaled.dimension(Axes.X.ordinal());
    	long cropScaleYSize = cropAllScaled.dimension(Axes.Y.ordinal());

		//
    	//User defined parameters
    	//
    	final long THRESHOLD_SLICE = 4;
    	final long ANALYZE_ORIG_BEGIN_TIMEINDEX = 5;
    	final long ANALYZE_ORIG_END_TIMEINDEX = 10;
    	final long ANALYZE_ORIG_COUNT = (ANALYZE_ORIG_END_TIMEINDEX-ANALYZE_ORIG_BEGIN_TIMEINDEX+1);
    	FinalInterval thresholdSliceLocation = FinalInterval.createMinSize(new long[] {0,0, THRESHOLD_SLICE, cropScaleXSize,cropScaleYSize,1});//xyzt:origin and xyzt:size
    	FinalInterval analyzeOrigInterval = FinalInterval.createMinSize(new long[] {0,0,ANALYZE_ORIG_BEGIN_TIMEINDEX, cropScaleXSize,cropScaleYSize,ANALYZE_ORIG_COUNT});//xyzt:origin and xyzt:size
		UnsignedByteType userThreshold = new UnsignedByteType(10);
    	RectangleShape medianSmoothSize = new RectangleShape(1, false);
    	final int ANALYZE_SIM_BEGIN_TIMEINDEX = 5;
    	final int ANALYZE_SIM_END_TIMEINDEX = 10;
    	final int ANALYZE_SIM_COUNT = (ANALYZE_SIM_END_TIMEINDEX-ANALYZE_SIM_BEGIN_TIMEINDEX+1);


    	
    	//Extract pre-bleach image from multi-timepoint data
    	RandomAccessibleInterval<UnsignedByteType> thresholdSlice = ij.op().transform().crop(cropAllScaled, thresholdSliceLocation);
//    	Img<DoubleType> float64 = ij.op().convert().float64(IterableRandomAccessibleInterval.create(thresholdSlice));
//    	IterableInterval<DoubleType> add1 = ij.op().math().add(float64,new DoubleType(.5));
//showAndZoom(ij, "Added", add1, 4);

    	//Smooth preBleachSlice to aid in creating threshold geometry
		Img<UnsignedByteType> smoothed = (Img<UnsignedByteType>) ij.op().run("create.img", thresholdSlice);
		ij.op().filter().median(smoothed, thresholdSlice, medianSmoothSize);
    	//Create threshold image as a BitType image array
		IterableInterval<BitType> bits = ij.op().threshold().apply(smoothed,userThreshold);
    	int xsize = (int)bits.dimension(Axes.X.ordinal());
    	int ysize = (int)bits.dimension(Axes.Y.ordinal());
    	int zsize = (int)bits.dimension(Axes.Z.ordinal());
    	System.out.println(xsize+" "+ysize+" "+zsize);
showAndZoom(ij, "Exp Data Smooth", smoothed, 4);
showAndZoom(ij, "Exp Data Threshold", bits, 4);
    	//Create bitmap to send to VCell as geometry subvolume definition
    	byte[] geomBits = new byte[(int)Intervals.numElements(bits)];
    	Cursor bitsCursor = bits.localizingCursor();
    	int currIndex = 0;
    	int xIndex = 0;
    	while(bitsCursor.hasNext()){
    		BitType nextbit = (BitType) bitsCursor.next();
    		geomBits[currIndex++] = (byte) nextbit.getInteger();
    		System.out.print(nextbit.getInteger());
    		xIndex++;
    		if(xIndex % xsize == 0) {
    			System.out.println();
    		}
    	}

    	//Define relevent simulation parameters
    	Double diffOverride = diffusionRate;
    	Double forwardBinding = null;
    	Double reverseBinding = null;

    	//Define new Geometry
    	String[] subvolumeNames = new String[] {"cyt","Nuc"};
    	int[] subvolumePixelValues = new int[] {0,1};
    	double[] origin = new double[] {0,0,0};
    	double[] extent = new double[] {xsize,ysize,zsize};
    	IJGeom ijGeom = new IJGeom(subvolumeNames, subvolumePixelValues, xsize, ysize, zsize, origin, extent, geomBits);
    	
    	long[] laserDim = new long[] {43,22,5,5};//x,y,xlen,ylen
    	final int XLEN_INDEX = 2;
    	final int YLEN_INDEX = 3;
    	//Define Laser area of exposure in image coordinates (will be pixels if origin is 0 and extent is xyz size
    	String laserArea = "((x >=  "+laserDim[Axes.X.ordinal()]+") && (x <= "+(laserDim[Axes.X.ordinal()]+laserDim[XLEN_INDEX]-1)+") && (y >=  "+laserDim[Axes.Y.ordinal()]+") && (y <= "+(laserDim[Axes.Y.ordinal()]+laserDim[YLEN_INDEX]-1)+"))";
    	//String laserArea = "((x >=  43) && (x <= 47) && (y >=  22) && (y <= 26))";

    	//Define new Simulation endtime
    	Double newEndTime = new Double(10.0);

    	//Start Frap simulation
    	IJSolverStatus ijSolverStatus = vh.startFrap(diffOverride,forwardBinding,reverseBinding,ijGeom,laserArea,newEndTime);
    	
    	//Wait for frap sim to end
    	System.out.println(ijSolverStatus.toString());
    	String simulationJobId = ijSolverStatus.simJobId;
    	while(true) {
    		Thread.sleep(5000);
        	ijSolverStatus =  vh.getSolverStatus(simulationJobId);
        	System.out.println(ijSolverStatus.toString());
        	String statusName = ijSolverStatus.statusName.toLowerCase();
        	if(statusName.equals("finished") || statusName.equals("stopped") || statusName.equals("aborted")) {
        		break;
        	}
    	}

    	URL url = new URL("http://localhost:"+vh.findVCellApiServerPort()+"/"+"getinfo"+"?"+"type"+"="+"quick");
    	Document doc = VCellHelper.getDocument(url);
    	System.out.println(VCellHelper.documentToString(doc));
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
    		throw new Exception("Couldn't find cacheKey for simulationJobId="+simulationJobId);
    	}
    	URL varInfoUrl = new URL("http://localhost:"+vh.findVCellApiServerPort()+"/"+"getdata"+"?"+"cachekey"+"="+cachekey);
    	doc = VCellHelper.getDocument(varInfoUrl);
    	System.out.println(VCellHelper.documentToString(doc));
//    	double[] times = VCellHelper.getTimesFromVarInfos(doc);
//	    String[] varNames = VCellHelper.getVarNamesFromVarInfos(doc);
	    double[] timeStack = null;
	    long[] xyztDims = null;
	    for(int timeIndex = ANALYZE_SIM_BEGIN_TIMEINDEX;timeIndex<=ANALYZE_ORIG_END_TIMEINDEX;timeIndex++){
	    	URL dataUrl = new URL("http://localhost:"+vh.findVCellApiServerPort()+"/"+"getdata"+"?"+"cachekey"+"="+cachekey+"&"+"varname"+"="+varToAnalyze+"&"+"timeindex"+"="+(int)timeIndex+"&"+"jobid="+ijSolverStatus.getJobIndex());
	    	doc = VCellHelper.getDocument(dataUrl);
			System.out.println(VCellHelper.documentToString(doc));
	    	nodes = doc.getElementsByTagName("ijData");
			BasicStackDimensions basicStackDimensions = VCellHelper.getDimensions(nodes.item(0));
	 		double[] data = VCellHelper.getData(nodes.item(0));
	   		if(xyztDims == null){
	   			timeStack = new double[ANALYZE_SIM_COUNT*data.length];
	   			xyztDims = new long [] {basicStackDimensions.xsize,basicStackDimensions.ysize,basicStackDimensions.zsize,ANALYZE_SIM_COUNT};
	   		}
	   		System.arraycopy(data,  0,timeStack, (timeIndex-ANALYZE_SIM_BEGIN_TIMEINDEX)*data.length,data.length);
//	   		System.out.println("timeindex="+timeIndex+" copied to index="+((timeIndex-timeIndexToAnalyze)*data.length)+" data length="+data.length);
    	}

    	
    	//Extract post-bleach image for comparison to simulation
    	RandomAccessibleInterval<UnsignedByteType> postBleachScaled = ij.op().transform().crop(cropAllScaled, analyzeOrigInterval);

    	//Turn VCell data into iterableinterval
		ArrayImg<DoubleType, DoubleArray> simImgs = ArrayImgs.doubles(timeStack, xyztDims);
    	FinalInterval simExtractInterval = FinalInterval.createMinSize(new long[] {0,0,0,0, cropScaleXSize,cropScaleYSize,1,ANALYZE_ORIG_COUNT});//xyzt:origin and xyzt:size
    	RandomAccessibleInterval<DoubleType> simExtracted = ij.op().transform().crop(simImgs, simExtractInterval);

showAndZoom(ij, "Sim Data "+diffusionRate, simImgs, 4);
showAndZoom(ij, "Exp Data to compare", postBleachScaled, 4);
//		//Print simulation data
//    	bitsCursor = bits.localizingCursor();
//    	Cursor<DoubleType> simCursor = simImgslice.localizingCursor();
//    	xIndex = 0;
//    	while(bitsCursor.hasNext()){
//    		BitType nextbit = (BitType) bitsCursor.next();
//    		Object simval = simCursor.next();
//    		if(nextbit.get()) {
//        		System.out.print(simval+" ");	
//    		}else {
//    			System.out.print("|");
//    		}
//    		xIndex++;
//    		if(xIndex % xsize == 0) {
//    			System.out.println();
//    		}
//    	}
//
//    	//Print original post-bleach image data
//    	bitsCursor = bits.localizingCursor();
//    	Cursor<UnsignedByteType> postCursor = IterableRandomAccessibleInterval.create(postBleachScaled).localizingCursor();
//    	xIndex = 0;
//    	while(bitsCursor.hasNext()){
//    		BitType nextbit = (BitType) bitsCursor.next();
//    		Object postval = postCursor.next();
//    		if(nextbit.get()) {
//        		System.out.print(postval+" ");	
//    		}else {
//    			System.out.print("|");
//    		}
//    		xIndex++;
//    		if(xIndex % xsize == 0) {
//    			System.out.println();
//    		}
//    	}

//		FinalInterval calcRoi = FinalInterval.createMinSize(new long[] {laserDim[Axes.X.ordinal()-2],laserDim[Axes.Y.ordinal()-2],laserDim[XLEN_INDEX+4],laserDim[YLEN_INDEX+4]});//xyzt:origin and xyzt:size
		IterableRandomAccessibleInterval<UnsignedByteType> thresholdSliceIRAI = IterableRandomAccessibleInterval.create(thresholdSlice);
    	Cursor<DoubleType> simCursor = IterableRandomAccessibleInterval.create(simExtracted).localizingCursor();
    	Cursor<UnsignedByteType> postCursor = IterableRandomAccessibleInterval.create(postBleachScaled).localizingCursor();
//    	List<Cursor> cursorList = Arrays.asList(new Cursor[] {preBleachCursor,simCursor,postCursor,maskCursor});
    	BigDecimal sumSquaredDiff = new BigDecimal(0);
    	int[] positionSlice = new int[bits.numDimensions()];
    	int[] positionData = new int[postBleachScaled.numDimensions()];
    	int numValsInMask = 0;
    	while(simCursor.hasNext()){
    		Cursor<BitType> maskCursor = bits.localizingCursor();
			Cursor<UnsignedByteType> preBleachCursor = thresholdSliceIRAI.localizingCursor();
        	while(maskCursor.hasNext()){
        		BitType nextbit = (BitType) maskCursor.next();
        		DoubleType simval = simCursor.next();
        		UnsignedByteType postBleachVal = postCursor.next();
        		UnsignedByteType preBleachVal = preBleachCursor.next();
        		maskCursor.localize(positionSlice);
        		boolean xmoob = (positionSlice[Axes.X.ordinal()] < laserDim[Axes.X.ordinal()]-2);
        		boolean xpoob =  positionSlice[Axes.X.ordinal()] > laserDim[Axes.X.ordinal()]+laserDim[XLEN_INDEX]+2;
        		boolean ymoob = (positionSlice[Axes.Y.ordinal()] < laserDim[Axes.Y.ordinal()]-2);
				boolean ypoob =  positionSlice[Axes.Y.ordinal()] > laserDim[Axes.Y.ordinal()]+laserDim[YLEN_INDEX]+2;
//				System.out.println(positionSlice[Axes.X.ordinal()]+" "+positionSlice[Axes.Y.ordinal()]+" "+xmoob+" "+ymoob+" "+xpoob+" "+ypoob);
				if(xmoob || ymoob || xpoob || ypoob) {
        			continue;
        		}
        		//laserDim[Axes.X.ordinal()-2],laserDim[Axes.Y.ordinal()-2],laserDim[XLEN_INDEX+4],laserDim[YLEN_INDEX+4]
        		int[] tempPositionSlice = positionSlice.clone();
        		preBleachCursor.localize(positionSlice);
        		postCursor.localize(positionData);
        		int[] tempPositionData = positionData.clone();
        		if(!Arrays.equals(positionSlice, tempPositionSlice) || !Arrays.equals(positionData, tempPositionData)){
        			throw new Exception("Cursor positions not equal");
        		}
        		if(positionSlice[0] != positionData[0] || positionSlice[1] != positionData[1]) {//check xy are same
        			throw new Exception("XY Cursor position not equal");
        		}
        		if(nextbit.get()) {
        			double normalize = (postBleachVal.get()+.0000001)/(preBleachVal.get()+.0000001);
        			double diff = simval.get()-(normalize);
        			sumSquaredDiff = sumSquaredDiff.add(new BigDecimal(Math.pow(diff,2.0)));
        			numValsInMask++;
        		}        		
        	}
    	}


//    	Img<DoubleType> preBleach64 = ij.op().convert().float64(IterableRandomAccessibleInterval.create(thresholdSlice));
//    	IterableInterval<DoubleType> add1 = ij.op().math().add(preBleach64,new DoubleType(.5));

		//Do some comparison between simulation data and experimental data
//		IterableInterval<DoubleType> it1 = simImgslice;
//		IterableInterval<UnsignedByteType> it2 = IterableRandomAccessibleInterval.create(postBleachScaled);
//		Double result = (Double)ij.op().run("coloc.icq", it1, it2);
		return sumSquaredDiff.divide(new BigDecimal(numValsInMask),8, RoundingMode.HALF_UP).doubleValue();
    }

}
