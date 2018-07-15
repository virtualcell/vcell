package org.vcell.imagej.helper;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingUtilities;

import org.vcell.imagej.helper.VCellHelper.BasicStackDimensions;
import org.vcell.imagej.helper.VCellHelper.IJGeom;
import org.vcell.imagej.helper.VCellHelper.IJSolverStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.scif.img.SCIFIOImgPlus;
import net.imagej.DefaultDataset;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imagej.axis.CalibratedAxis;
import net.imagej.axis.IdentityAxis;
import net.imagej.display.ImageDisplay;
import net.imagej.interval.DefaultCalibratedRealInterval;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Intervals;
import net.imglib2.view.IterableRandomAccessibleInterval;

public class CR {

	public static void main(String[] args) {
		try {
			testSolver2(new File(System.getProperty("user.dir", ".")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static void testSolver2(File exampleDataDir) throws Exception{
    	ImageJ ij = new ImageJ();//make sure this is the 'net.imagej' version and not the 'ij' version
ij.ui().showUI();
    	VCellHelper vh = new VCellHelper();//Communicates with VCellApi
    	
    	ImgPlus<? extends RealType<?>> experimentalData = null;// 2D
    	ImgPlus<UnsignedByteType> segmentedGeom = null;// 2D or 3D (if 3D will be z-project for analysis withg 2D experimental data)
    	ImgPlus<? extends RealType<?>> preBleachRAI0 = null;// 2D
    	HashMap<Integer, String> segmentedGeomMapSubvolumeName = new HashMap<>();
    	final UnsignedByteType domainOfInterestFromSegmentedGeom = new UnsignedByteType(255);
    	final int ANALYZE_BEGIN_TIMEINDEX = 5;
    	final int ANALYZE_END_TIMEINDEX = 10;
    	DefaultCalibratedRealInterval laserBounds = new DefaultCalibratedRealInterval(new double[] {43,22},new double[] {47,26}, new IdentityAxis(Axes.X),new IdentityAxis(Axes.Y));
    	ImgPlus<UnsignedByteType> analysisROI = null;// 2D

    	segmentedGeomMapSubvolumeName.put(0, "cyt");
    	segmentedGeomMapSubvolumeName.put(255, "Nuc");
    	
    	DefaultDataset defaultDataset = (DefaultDataset)ij.io().open(new File(exampleDataDir,"Experimental.zip").getAbsolutePath());
    	ij.ui().show(defaultDataset);
    	defaultDataset = (DefaultDataset)ij.io().open(new File(exampleDataDir,"Seg Geom_3d.zip").getAbsolutePath());
    	ij.ui().show(defaultDataset);
    	defaultDataset = (DefaultDataset)ij.io().open(new File(exampleDataDir,"PreBleach.zip").getAbsolutePath());
    	ij.ui().show(defaultDataset);
    	defaultDataset = (DefaultDataset)ij.io().open(new File(exampleDataDir,"Analysis ROI.zip").getAbsolutePath());
    	ij.ui().show(defaultDataset);

    	List<ImageDisplay> imageDisplays = ij.imageDisplay().getImageDisplays();
    	for (ImageDisplay imageDisplay : imageDisplays) {
			System.out.println("-----"+imageDisplay.getName()+" ");
			if(imageDisplay.getName().startsWith("Experimental")) {
				experimentalData = ij.imageDisplay().getActiveDatasetView(imageDisplay).getData().getImgPlus();
			}else if(imageDisplay.getName().startsWith("Seg Geom")) {
				segmentedGeom = (ImgPlus<UnsignedByteType>)ij.imageDisplay().getActiveDatasetView(imageDisplay).getData().getImgPlus();
			}else if(imageDisplay.getName().startsWith("PreBleach")) {
				preBleachRAI0 = (ImgPlus<UnsignedByteType>)ij.imageDisplay().getActiveDatasetView(imageDisplay).getData().getImgPlus();
			}else if(imageDisplay.getName().startsWith("Analysis ROI")) {
				analysisROI= (ImgPlus<UnsignedByteType>)ij.imageDisplay().getActiveDatasetView(imageDisplay).getData().getImgPlus();
			}
	    	CalibratedAxis[] calibratedAxes = new CalibratedAxis[imageDisplay.numDimensions()];
	    	imageDisplay.axes(calibratedAxes);
	    	for (int i = 0; i < calibratedAxes.length; i++) {
	    		System.out.print(calibratedAxes[i].type());
	    	}
	    	System.out.println();
		}
//    	if(true) {return;}
    	
//    //Load image
////	List<SCIFIOImgPlus<UnsignedByteType>> openImgs = new ImgOpener().openImgs( "C:/temp/ImportImageExamples/test2_Tmany_Zone.lsm",new UnsignedByteType());
//	Img< UnsignedByteType > test2_Tmany_Zone = new ImgOpener().openImg( "C:/temp/ImportImageExamples/test2_Tmany_Zone.lsm",new UnsignedByteType());
////	ImgPlus<? extends RealType<?>> imgPlus = ((Dataset) ij.io().open("C:/temp/ImportImageExamples/test2_Tmany_Zone.lsm")).getImgPlus();
////	ImagePlus[] bfImgPlus = BF.openImagePlus("C:/temp/ImportImageExamples/test2_Tmany_Zone.lsm");
//showAndZoom(ij, "Exp Data", test2_Tmany_Zone, .5);
//	//Crop and scale all slices
//	double[] scaleFactors = new double[] {.25,.25,1.0};
//	long origSliceCount = test2_Tmany_Zone.dimension(3);
//	long[] roiCropAll =  new long[] {132,144, 0,0, 237,167, 1,origSliceCount};//xyzt:origin and xyzt:size
//	FinalInterval cropAllLocation = FinalInterval.createMinSize(roiCropAll);
//	RandomAccessibleInterval<UnsignedByteType> cropAllSlices = ij.op().transform().crop(test2_Tmany_Zone, cropAllLocation);
//	RandomAccessibleInterval<UnsignedByteType> cropAllScaled = ij.op().transform().scale(cropAllSlices, scaleFactors, interpolator);
//showAndZoom(ij, "Exp Data Crop", cropAllSlices, 2);
//showAndZoom(ij, "Exp Data Scaled", cropAllScaled, 4);

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
    			2.8
    			};
    	for(int i=0;i<diffRates.length;i++){
    		double result = runSim(vh, ij, diffRates[i],null,null,"rf",
    			experimentalData, segmentedGeom, preBleachRAI0, segmentedGeomMapSubvolumeName, domainOfInterestFromSegmentedGeom, ANALYZE_BEGIN_TIMEINDEX, ANALYZE_END_TIMEINDEX, laserBounds, analysisROI,new Double(10.0));
    		results.add(result);
    	}

    	for(int i=0;i<diffRates.length;i++){
    		System.out.println("diffRate="+diffRates[i]+" MSE="+(results.get(i)));
    	}

    }
    
    public static void showAndZoom(ImageJ ij,String displayName,Object thingToDisplay,double zoomFactor) throws Exception{
    	if(!displayName.startsWith("Sim Data")) {
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
//	public static NearestNeighborInterpolatorFactory<UnsignedByteType> interpolator = new NearestNeighborInterpolatorFactory<>();
    public static double  runSim(VCellHelper vh,ImageJ ij,
    	Double diffusionRate,Double forwardBinding, Double reverseBinding,
    	String varToAnalyze,
    	ImgPlus<? extends RealType<?>> zProjectedExperimentalData,// 2D
    	ImgPlus<UnsignedByteType> nonZProjectedSegmentedGeom,// 2D or 3D (if 3D will be z-project for analysis withg 2D experimental data)
    	ImgPlus<? extends RealType<?>> zProjectedPreBleach,// 2D
    	HashMap<Integer, String> segmentedGeomValuesMapSubvolumeName,
    	final UnsignedByteType domainOfInterestFromSegmentedGeom,
    	final int ANALYZE_BEGIN_TIMEINDEX,final int ANALYZE_END_TIMEINDEX,
    	DefaultCalibratedRealInterval laserBounds,
    	ImgPlus<UnsignedByteType> zProjectedAnalysisROI,// 2D
    	Double newEndTime
    	) throws Exception{
    	
    	int ANALYZE_COUNT = ANALYZE_END_TIMEINDEX - ANALYZE_BEGIN_TIMEINDEX +1;
    	int xIndex = zProjectedExperimentalData.dimensionIndex(Axes.X);
    	int xsize = (int)zProjectedExperimentalData.dimension(xIndex);
    	int yIndex = zProjectedExperimentalData.dimensionIndex(Axes.Y);
    	int ysize = (int)zProjectedExperimentalData.dimension(yIndex);
    	int xysize = xsize*ysize;
    	FinalInterval analyzeOrigInterval = FinalInterval.createMinSize(new long[] {0,0,ANALYZE_BEGIN_TIMEINDEX, xsize,ysize,ANALYZE_COUNT});//xyzt:origin and xyzt:size

    	int[] dims = new int[zProjectedExperimentalData.numDimensions()];
    	for (int i = 0; i < dims.length; i++) {
			if(zProjectedExperimentalData.axis(i).type().equals(Axes.X) || zProjectedExperimentalData.axis(i).type().equals(Axes.Y)){
				if(!zProjectedExperimentalData.axis(i).equals(nonZProjectedSegmentedGeom.axis(i)) || !zProjectedExperimentalData.axis(i).equals(zProjectedPreBleach.axis(i)) || !zProjectedExperimentalData.axis(i).equals(zProjectedAnalysisROI.axis(i))) {
					throw new Exception("Axis "+zProjectedExperimentalData.axis(i).type().getLabel()+" type for dimension index="+i+" of experimentalData, segmentedGeom, preBleach and analysisROI must match");
				}
				if(zProjectedExperimentalData.dimension(i) != nonZProjectedSegmentedGeom.dimension(i) || zProjectedExperimentalData.dimension(i) != zProjectedPreBleach.dimension(i) || zProjectedExperimentalData.dimension(i) != zProjectedAnalysisROI.dimension(i)) {
					throw new Exception("Axis "+zProjectedExperimentalData.axis(i).type().getLabel()+" length of experimentalData, segmentedGeom, preBleach and analysisROI must match");
				}
			}
		}
    	
    	int zIndex = nonZProjectedSegmentedGeom.dimensionIndex(Axes.Z);
    	int zsize = (zIndex == -1?1:(int)nonZProjectedSegmentedGeom.dimension(zIndex));
    	
    	if((xsize*ysize*zsize) != Intervals.numElements(nonZProjectedSegmentedGeom)) {
    		throw new Exception("x*y*z = "+(xsize*ysize*zsize)+" does not equal segmentedGeom size = "+Intervals.numElements(nonZProjectedSegmentedGeom));
    	}
    	
//    	Img<BitType> zProjectMask = ij.op().create().img(FinalInterval.createMinSize(new long[] {0,0,xsize,ysize}), new BitType());
//    	Img<UnsignedByteType> zProjectMaskImg = (Img<BitType>) ij.op().run("create.img", preBleachRAI0);
//    	IterableRandomAccessibleInterval<UnsignedByteType> segmentedGeom = IterableRandomAccessibleInterval.create(segmentedGeom0);
    	
    	//Create byte array that defines vcell subvolumes ids (handles)
    	byte[] vcellSubvolumeHandles = new byte[(int)Intervals.numElements(nonZProjectedSegmentedGeom)];
    	Cursor<UnsignedByteType> nonZProjectedSegmentedGeomCursor = nonZProjectedSegmentedGeom.localizingCursor();
    	while(nonZProjectedSegmentedGeomCursor.hasNext()){
    		UnsignedByteType segmentedGeomValue = (UnsignedByteType) nonZProjectedSegmentedGeomCursor.next();
    		int currentZ = (zIndex==-1?0:nonZProjectedSegmentedGeomCursor.getIntPosition(zIndex));
    		int currXYPixelIndex = nonZProjectedSegmentedGeomCursor.getIntPosition(yIndex)*xsize + nonZProjectedSegmentedGeomCursor.getIntPosition(xIndex);
    		int currXYZPixelIndex = currXYPixelIndex + (zIndex==-1?0:currentZ*xysize);
    		vcellSubvolumeHandles[currXYZPixelIndex] = (byte)segmentedGeomValue.get();
    	}

//    	System.out.println(xsize+" "+ysize+" "+zsize);
//    	long cropScaleXSize = cropAllScaled.dimension(Axes.X.ordinal());
//    	long cropScaleYSize = cropAllScaled.dimension(Axes.Y.ordinal());

//		//
//    	//User defined parameters
//    	//
//    	final long THRESHOLD_SLICE = 4;
//    	final long ANALYZE_ORIG_BEGIN_TIMEINDEX = 5;
//    	final long ANALYZE_ORIG_END_TIMEINDEX = 10;
//    	final long ANALYZE_ORIG_COUNT = (ANALYZE_ORIG_END_TIMEINDEX-ANALYZE_ORIG_BEGIN_TIMEINDEX+1);
//    	FinalInterval thresholdSliceLocation = FinalInterval.createMinSize(new long[] {0,0, THRESHOLD_SLICE, cropScaleXSize,cropScaleYSize,1});//xyzt:origin and xyzt:size
//    	FinalInterval analyzeOrigInterval = FinalInterval.createMinSize(new long[] {0,0,ANALYZE_ORIG_BEGIN_TIMEINDEX, cropScaleXSize,cropScaleYSize,ANALYZE_ORIG_COUNT});//xyzt:origin and xyzt:size
//		UnsignedByteType userThreshold = new UnsignedByteType(10);
//    	RectangleShape medianSmoothSize = new RectangleShape(1, false);
//    	final int ANALYZE_SIM_BEGIN_TIMEINDEX = 5;
//    	final int ANALYZE_SIM_END_TIMEINDEX = 10;
//    	final int ANALYZE_SIM_COUNT = (ANALYZE_SIM_END_TIMEINDEX-ANALYZE_SIM_BEGIN_TIMEINDEX+1);
//
//
//    	
//    	//Extract pre-bleach image from multi-timepoint data
//    	RandomAccessibleInterval<UnsignedByteType> thresholdSlice = ij.op().transform().crop(cropAllScaled, thresholdSliceLocation);
////    	Img<DoubleType> float64 = ij.op().convert().float64(IterableRandomAccessibleInterval.create(thresholdSlice));
////    	IterableInterval<DoubleType> add1 = ij.op().math().add(float64,new DoubleType(.5));
////showAndZoom(ij, "Added", add1, 4);
//
//    	//Smooth preBleachSlice to aid in creating threshold geometry
//		Img<UnsignedByteType> smoothed = (Img<UnsignedByteType>) ij.op().run("create.img", thresholdSlice);
//		ij.op().filter().median(smoothed, thresholdSlice, medianSmoothSize);
//    	//Create threshold image as a BitType image array
//		IterableInterval<BitType> bits = ij.op().threshold().apply(smoothed,userThreshold);
//    	int xsize = (int)bits.dimension(Axes.X.ordinal());
//    	int ysize = (int)bits.dimension(Axes.Y.ordinal());
//    	int zsize = (int)bits.dimension(Axes.Z.ordinal());
//    	System.out.println(xsize+" "+ysize+" "+zsize);
//showAndZoom(ij, "Exp Data Smooth", smoothed, 4);
//showAndZoom(ij, "Exp Data Threshold", bits, 4);
//    	//Create bitmap to send to VCell as geometry subvolume definition
//    	byte[] geomBits = new byte[(int)Intervals.numElements(bits)];
//    	Cursor bitsCursor = bits.localizingCursor();
//    	int currIndex = 0;
//    	int xIndex = 0;
//    	while(bitsCursor.hasNext()){
//    		BitType nextbit = (BitType) bitsCursor.next();
//    		geomBits[currIndex++] = (byte) nextbit.getInteger();
//    		System.out.print(nextbit.getInteger());
//    		xIndex++;
//    		if(xIndex % xsize == 0) {
//    			System.out.println();
//    		}
//    	}

    	//Define new Geometry with domain equal to xyz pixel size for ease of use
    	String[] subvolumeNames = segmentedGeomValuesMapSubvolumeName.values().toArray(new String[0]);
    	Integer[] subvolumePixelValues = segmentedGeomValuesMapSubvolumeName.keySet().toArray(new Integer[0]);
    	double[] origin = new double[] {0,0,0};
    	double[] extent = new double[] {xsize,ysize,zsize};
    	IJGeom ijGeom = new IJGeom(subvolumeNames, subvolumePixelValues, xsize, ysize, zsize, origin, extent, vcellSubvolumeHandles);
    	
    	//Define analytic laser area of exposure in geometry domain coordinates (will be pixels if origin is 0 and extent is xyz size)
    	double laserXL = laserBounds.realMin(laserBounds.dimensionIndex(Axes.X));
    	double laserYL = laserBounds.realMin(laserBounds.dimensionIndex(Axes.Y));
    	double laserXH = laserBounds.realMax(laserBounds.dimensionIndex(Axes.X));
    	double laserYH = laserBounds.realMax(laserBounds.dimensionIndex(Axes.Y));
    	String laserArea = "((x >=  "+laserXL+") && (x <= "+laserXH+") && (y >=  "+laserYL+") && (y <= "+laserYH+"))";    	

    	//Start Frap simulation
    	IJSolverStatus ijSolverStatus = vh.startFrap(diffusionRate,forwardBinding,reverseBinding,ijGeom,laserArea,newEndTime);
    	
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
	    double[] timeStack = new double[ANALYZE_COUNT*xsize*ysize];
//	    long[] xyztDims = null;
//	    AxisType[] simDataAxes = null;
	    for(int timeIndex = ANALYZE_BEGIN_TIMEINDEX;timeIndex<=ANALYZE_END_TIMEINDEX;timeIndex++){
	    	URL dataUrl = new URL("http://localhost:"+vh.findVCellApiServerPort()+"/"+"getdata"+"?"+"cachekey"+"="+cachekey+"&"+"varname"+"="+varToAnalyze+"&"+"timeindex"+"="+(int)timeIndex+"&"+"jobid="+ijSolverStatus.getJobIndex());
	    	doc = VCellHelper.getDocument(dataUrl);
			System.out.println(VCellHelper.documentToString(doc));
	    	nodes = doc.getElementsByTagName("ijData");
			BasicStackDimensions basicStackDimensions = VCellHelper.getDimensions(nodes.item(0));
			if(basicStackDimensions.xsize != xsize || basicStackDimensions.ysize != ysize || basicStackDimensions.zsize != zsize) {
				throw new Exception("One or more sim data xyz dimensions="+basicStackDimensions.xsize+","+basicStackDimensions.ysize+","+basicStackDimensions.zsize+" does not match expected xyz sizes="+xsize+","+ysize+","+zsize);
			}
	 		double[] data = VCellHelper.getData(nodes.item(0));
//	   		if(xyztDims == null){
//	   			timeStack = new double[ANALYZE_COUNT*data.length];
//	   			xyztDims = new long [] {basicStackDimensions.xsize,basicStackDimensions.ysize,basicStackDimensions.zsize,ANALYZE_COUNT};
//	   			simDataAxes = new AxisType[] {Axes.X,Axes.Y,Axes.Z,Axes.TIME};
//	   		}
	 		
	 		//Sum pixel values in Z direction to match experimental data (open pinhole confocal, essentially brightfield)
			for (int j = 0; j < data.length; j++) {
				timeStack[(timeIndex-ANALYZE_BEGIN_TIMEINDEX)*xysize+(j%xysize)]+= data[j];
			}
		    
//	   		System.arraycopy(data,  0,timeStack, (timeIndex-ANALYZE_BEGIN_TIMEINDEX)*data.length,data.length);
//	   		System.out.println("timeindex="+timeIndex+" copied to index="+((timeIndex-timeIndexToAnalyze)*data.length)+" data length="+data.length);
    	}

    	//Turn VCell data into iterableinterval
//    	FinalInterval zProjectedSimDataSize = FinalInterval.createMinSize(new long[] {0,0,0, xsize,ysize,ANALYZE_COUNT});//xyzt:origin and xyzt:size
		ArrayImg<DoubleType, DoubleArray> simImgs = ArrayImgs.doubles(timeStack, new long[] {xsize,ysize,ANALYZE_COUNT});
		SCIFIOImgPlus<DoubleType> annotatedZProjectedSimData = new SCIFIOImgPlus<>(simImgs, "Sim Data "+diffusionRate,new AxisType[] {Axes.X,Axes.Y,Axes.TIME});
//    	RandomAccessibleInterval<DoubleType> simExtracted = ij.op().transform().crop(annotatedSimData, simExtractInterval);   	
showAndZoom(ij, "Sim Data "+diffusionRate, annotatedZProjectedSimData, 4);




//showAndZoom(ij, "Exp Data to compare", postBleachScaled, 4);
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

		//Calculate mean-squared-error as example, normalize experimental data by dividing by a prebleach image
		RandomAccessibleInterval<? extends RealType<?>> postBleachScaled = ij.op().transform().crop(zProjectedExperimentalData, analyzeOrigInterval);
//		FinalInterval calcRoi = FinalInterval.createMinSize(new long[] {laserDim[Axes.X.ordinal()-2],laserDim[Axes.Y.ordinal()-2],laserDim[XLEN_INDEX+4],laserDim[YLEN_INDEX+4]});//xyzt:origin and xyzt:size
//		IterableRandomAccessibleInterval<UnsignedByteType> thresholdSliceIRAI = IterableRandomAccessibleInterval.create(thresholdSlice);
    	Cursor<DoubleType> simCursor = IterableRandomAccessibleInterval.create(annotatedZProjectedSimData).localizingCursor();
    	Cursor<? extends RealType<?>> postCursor = IterableRandomAccessibleInterval.create(postBleachScaled).localizingCursor();
//    	List<Cursor> cursorList = Arrays.asList(new Cursor[] {preBleachCursor,simCursor,postCursor,maskCursor});
    	IterableRandomAccessibleInterval<? extends RealType<?>> preBleachRAI = IterableRandomAccessibleInterval.create(zProjectedPreBleach);
    	BigDecimal sumSquaredDiff = new BigDecimal(0);
    	int[] analysisPosition = new int[zProjectedAnalysisROI.numDimensions()];
    	int[] preBleachPosition = new int[preBleachRAI.numDimensions()];
    	int[] experimentalPosition = new int[postBleachScaled.numDimensions()];
    	int[] simulationPosition = new int[annotatedZProjectedSimData.numDimensions()];
    	int numValsInMask = 0;
    	while(simCursor.hasNext()){
    		Cursor<UnsignedByteType> analysisCursor = zProjectedAnalysisROI.localizingCursor();
			Cursor<? extends RealType<?>> preBleachCursor = preBleachRAI.localizingCursor();
        	while(analysisCursor.hasNext()){
        		boolean maskBit = !(analysisCursor.next().get() == 0);
        		DoubleType simval = simCursor.next();
        		RealType<?> postBleachVal = postCursor.next();
        		RealType<?> preBleachVal = preBleachCursor.next();
        		if(!maskBit) {//skip areas where analysis mask is 0
        			continue;
        		}
//        		maskCursor.localize(positionSlice);
//        		boolean xmoob = positionSlice[xIndex] < ;
//        		boolean xpoob = positionSlice[yIndex] > laserDim[Axes.X.ordinal()]+laserDim[XLEN_INDEX]+2;
//        		boolean ymoob = positionSlice[yIndex] < laserDim[Axes.Y.ordinal()]-2;
//				boolean ypoob = positionSlice[yIndex] > laserDim[Axes.Y.ordinal()]+laserDim[YLEN_INDEX]+2;
////				System.out.println(positionSlice[Axes.X.ordinal()]+" "+positionSlice[Axes.Y.ordinal()]+" "+xmoob+" "+ymoob+" "+xpoob+" "+ypoob);
//				if(xmoob || ymoob || xpoob || ypoob) {
//        			continue;
//        		}
        		//laserDim[Axes.X.ordinal()-2],laserDim[Axes.Y.ordinal()-2],laserDim[XLEN_INDEX+4],laserDim[YLEN_INDEX+4]
        		analysisCursor.localize(analysisPosition);
        		preBleachCursor.localize(preBleachPosition);
        		postCursor.localize(experimentalPosition);
        		simCursor.localize(simulationPosition);
//        		System.out.println("analysis="+Arrays.toString(analysisPosition)+" prebleach="+Arrays.toString(preBleachPosition)+" exp="+Arrays.toString(experimentalPosition)+" sim="+Arrays.toString(simulationPosition));
        		if(!Arrays.equals(analysisPosition, preBleachPosition) || !Arrays.equals(experimentalPosition, simulationPosition)){
        			throw new Exception("Cursor positions not equal");
        		}
        		if(analysisPosition[0] != experimentalPosition[0] || analysisPosition[1] != experimentalPosition[1]) {//check xy are same
        			throw new Exception("XY Cursor position not equal for analysisROI mask and experimental data");
        		}
    			double normalize = (postBleachVal.getRealDouble()+.0000001)/(preBleachVal.getRealDouble()+.0000001);
    			double diff = simval.get()-(normalize);
    			sumSquaredDiff = sumSquaredDiff.add(new BigDecimal(Math.pow(diff,2.0)));
    			numValsInMask++;
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
