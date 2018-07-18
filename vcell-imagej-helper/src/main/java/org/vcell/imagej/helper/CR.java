package org.vcell.imagej.helper;

import java.awt.Dimension;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
//import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.scijava.display.Display;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.data.xy.DefaultXYDataset;
import org.vcell.imagej.helper.VCellHelper.BasicStackDimensions;
import org.vcell.imagej.helper.VCellHelper.IJGeom;
import org.vcell.imagej.helper.VCellHelper.IJSolverStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ij.WindowManager;
import io.scif.img.SCIFIOImgPlus;
import net.imagej.DefaultDataset;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imagej.axis.IdentityAxis;
import net.imagej.display.DefaultImageDisplay;
import net.imagej.display.ImageDisplay;
import net.imagej.interval.DefaultCalibratedRealInterval;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Intervals;
import net.imglib2.view.IterableRandomAccessibleInterval;

public class CR {

	public static void main(String[] args) {
		try {
	    	ImageJ ij = new ImageJ();//make sure this is the 'net.imagej' version and not the 'ij' version
			ij.ui().showUI();
			VCellHelper vh = new VCellHelper();//Communicates with VCellApi
			
//	    	double[] diffRates = new double[] { // For test of simulated experimental data
//			1.1,
//			1.15,
//			1.2,
//			1.25,
//			1.3
//			};

	    	double[] diffRates = new double[] {	    			
			1.25,
			1.3,
			1.4,
			1.45,
			1.5,
			1.55,
			1.6
			};
			double[] mse = testSolver2(diffRates,new File(System.getProperty("user.dir", ".")),vh,ij);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static double[] testSolver2(double[] diffRates,File exampleDataDir,VCellHelper vh,ImageJ ij) throws Exception{

    	ImgPlus<? extends RealType<?>> experimentalData = null;// 2D
    	ImgPlus<UnsignedByteType> segmentedGeom = null;// 2D or 3D (if 3D will be z-project for analysis withg 2D experimental data)
    	ImgPlus<? extends RealType<?>> preBleachImage = null;// 2D
    	HashMap<Integer, String> segmentedGeomMapSubvolumeName = new HashMap<>();
    	final UnsignedByteType domainOfInterestFromSegmentedGeom = new UnsignedByteType(255);
    	final int ANALYZE_BEGIN_TIMEINDEX = 5;
    	final int ANALYZE_END_TIMEINDEX = 10;
    	DefaultCalibratedRealInterval laserBounds = new DefaultCalibratedRealInterval(new double[] {43,22},new double[] {47,26}, new IdentityAxis(Axes.X),new IdentityAxis(Axes.Y));
    	ImgPlus<UnsignedByteType> analysisROI = null;// 2D

    	segmentedGeomMapSubvolumeName.put(0, "cyt");
    	segmentedGeomMapSubvolumeName.put(255, "Nuc");
    	
//    	String[] imageTitles = WindowManager.getImageTitles();
//    	for (int i = 0; i < imageTitles.length; i++) {
//    		System.out.println("----------title "+imageTitles[i]+" "+WindowManager.getWindow(imageTitles[i]));
//		}
//    	
    	final String[] exampleFiles = new String[] {"Experimental.zip","Seg Geom_3d.zip","PreBleach.zip","Analysis ROI.zip"};
//    	final String[] exampleFiles = new String[] {"testDiff_1_2.zip","Seg Geom_3d.zip","testPreBleach.zip","Analysis ROI.zip"};// for test
    	
    	
//    	List<Display<?>> displays0 = ij.display().getDisplays();
//    	for (Display<?> display : displays0) {
//			System.out.println("----------disp "+display.getName()+" "+display.getIdentifier());
//		}
    	List<ImageDisplay> knownImageDisplays = ij.imageDisplay().getImageDisplays();
//    	for (ImageDisplay imageDisplay : imageDisplays0) {
//    		System.out.println("----------imgdisp "+imageDisplay.getName()+" "+imageDisplay.getIdentifier());
//		}

    	// Open example images if they are not already open
    	for (int exampleFilesIndex = 0; exampleFilesIndex < exampleFiles.length; exampleFilesIndex++) {
    		DefaultDataset exampleFileDataset = null;
    		
    		for (ImageDisplay imageDisplay : knownImageDisplays) {
    			if(imageDisplay.getName().equals(exampleFiles[exampleFilesIndex])) {
    				exampleFileDataset = (DefaultDataset) imageDisplay.getActiveView().getData();
    				break;
    			}
    		}
			if(exampleFileDataset == null) {
				exampleFileDataset = (DefaultDataset)ij.io().open(new File(exampleDataDir,exampleFiles[exampleFilesIndex]).getAbsolutePath());
//				ij.ui().show(exampleFileDataset);
				showAndZoom(ij, exampleFiles[exampleFilesIndex], exampleFileDataset, 4);
			}
			// Print names of axes (x,y,z,time,channel,unknown,...)
			System.out.print(exampleFileDataset.getName()+": ");
	    	for (int dimensionIndex = 0; dimensionIndex < exampleFileDataset.numDimensions(); dimensionIndex++) {
	    		System.out.print((exampleFileDataset.axis(dimensionIndex)!=null?exampleFileDataset.axis(dimensionIndex).type()+" ":"null "));
	    	}
	    	System.out.println();

	    	// Assign variables (defaultDataset corresponds to open 'exampleFiles' element
			switch (exampleFilesIndex) {
			case 0:
				experimentalData = exampleFileDataset.getImgPlus();
				break;
			case 1:
				segmentedGeom = (ImgPlus<UnsignedByteType>) exampleFileDataset.getImgPlus();
				break;
			case 2:
				preBleachImage = exampleFileDataset.getImgPlus();
				break;
			case 3:
				analysisROI = (ImgPlus<UnsignedByteType>) exampleFileDataset.getImgPlus();
				break;
			}
		}


//    	List<ImageDisplay> imageDisplays = ij.imageDisplay().getImageDisplays();
//    	for (ImageDisplay imageDisplay : imageDisplays) {
//			System.out.println("-----"+imageDisplay.getName()+" ");
//			if(imageDisplay.getName().startsWith("Experimental")) {
//				experimentalData = ij.imageDisplay().getActiveDatasetView(imageDisplay).getData().getImgPlus();
//			}else if(imageDisplay.getName().startsWith("Seg Geom")) {
//				segmentedGeom = (ImgPlus<UnsignedByteType>)ij.imageDisplay().getActiveDatasetView(imageDisplay).getData().getImgPlus();
//			}else if(imageDisplay.getName().startsWith("PreBleach")) {
//				preBleachRAI0 = (ImgPlus<UnsignedByteType>)ij.imageDisplay().getActiveDatasetView(imageDisplay).getData().getImgPlus();
//			}else if(imageDisplay.getName().startsWith("Analysis ROI")) {
//				analysisROI= (ImgPlus<UnsignedByteType>)ij.imageDisplay().getActiveDatasetView(imageDisplay).getData().getImgPlus();
//			}
//	    	CalibratedAxis[] calibratedAxes = new CalibratedAxis[imageDisplay.numDimensions()];
//	    	imageDisplay.axes(calibratedAxes);
//	    	for (int i = 0; i < calibratedAxes.length; i++) {
//	    		System.out.print(calibratedAxes[i].type());
//	    	}
//	    	System.out.println();
//		}
//    	if(experimentalData == null) {
//    		DefaultDataset defaultDataset = (DefaultDataset)ij.io().open(new File(exampleDataDir,"Experimental.zip").getAbsolutePath());
//    		ij.ui().show(defaultDataset);
//    		experimentalData = defaultDataset.getImgPlus();
//    	}
//    	if(segmentedGeom == null) {
//    		DefaultDataset defaultDataset = (DefaultDataset)ij.io().open(new File(exampleDataDir,"Seg Geom_3d.zip").getAbsolutePath());
//    		ij.ui().show(defaultDataset);
//    		segmentedGeom = (ImgPlus<UnsignedByteType>) defaultDataset.getImgPlus();
//    	}
//    	if(preBleachRAI0 == null) {
//    		DefaultDataset defaultDataset = (DefaultDataset)ij.io().open(new File(exampleDataDir,"PreBleach.zip").getAbsolutePath());
//    		ij.ui().show(defaultDataset);
//    		preBleachRAI0 = defaultDataset.getImgPlus();
//    	}
//    	if(analysisROI == null) {
//    		DefaultDataset defaultDataset = (DefaultDataset)ij.io().open(new File(exampleDataDir,"Analysis ROI.zip").getAbsolutePath());
//    		ij.ui().show(defaultDataset);
//    		analysisROI= (ImgPlus<UnsignedByteType>) defaultDataset.getImgPlus();
//    	}
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
//    	ArrayList<Double> results = new ArrayList();
//    	double minResult = Double.POSITIVE_INFINITY;
//    	double maxResult = Double.NEGATIVE_INFINITY;
    	double[] mse = new double[diffRates.length];
    	for(int i=0;i<diffRates.length;i++){
    		double result = runSim(vh, ij, diffRates[i],null,null,"rf",
    			experimentalData, segmentedGeom, preBleachImage, segmentedGeomMapSubvolumeName, domainOfInterestFromSegmentedGeom,
    			ANALYZE_BEGIN_TIMEINDEX, ANALYZE_END_TIMEINDEX, laserBounds, analysisROI,new Double(10.0));
//    		results.add(result);
    		mse[i] = result;
//    		minResult = Math.min(minResult, result);
//    		maxResult = Math.max(maxResult, result);
    	}
    	for(int i=0;i<mse.length;i++){
    		System.out.println("diffRate="+diffRates[i]+" MSE="+(mse[i]));
    	}

//    	DefaultXYDataset dataset = new DefaultXYDataset();
//    	double[][] data =new double[][] { diffRates,mse};
//    	dataset.addSeries("Exp vs. Sim", data);
//    	JFreeChart xyLineChart = ChartFactory.createXYLineChart("Experimental vs. Simulated FRAP", "Diffusion", "MSE", dataset);
//    	xyLineChart.getXYPlot().getRangeAxis().setRange(minResult, maxResult);
//    	ChartPanel chartPanel = new ChartPanel(xyLineChart);
//    	JFrame frame = new JFrame("Chart");
//    	//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    	frame.getContentPane().add(chartPanel);
//    	//Display the window.
//    	frame.pack();
//    	frame.setVisible(true);
    	return mse;

    }
    
    public static void showAndZoom(ImageJ ij,String displayName,Object thingToDisplay,double zoomFactor) throws Exception{
//    	if(!displayName.startsWith("Sim Data")) {
//    		return;
//    	}
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ij.ui().show(displayName,thingToDisplay);
		    	//Find display and set zoom, resize window
		    	List<ImageDisplay> knownImageDisplays = ij.imageDisplay().getImageDisplays();
		    	boolean bvisible = false;
				while (!bvisible) {
					for (ImageDisplay imageDisplay : knownImageDisplays) {
						if (imageDisplay.getName().equals(displayName)) {
							if (imageDisplay.isVisible(imageDisplay.getActiveView())) {
								bvisible = true;
								break;
							}
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
//		    	int vw = ij.imageDisplay().getActiveImageDisplay().getCanvas().getViewportWidth();
//		    	int vh = ij.imageDisplay().getActiveImageDisplay().getCanvas().getViewportHeight();
//		    	System.out.println(" -----byname="+ij.display().getDisplay(displayName));
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
				    	try {
							ij.imageDisplay().getActiveImageDisplay().getCanvas().setZoom(zoomFactor);
							if(ij.display().getDisplay(displayName) != null && ij.ui().getDisplayViewer(ij.display().getDisplay(displayName)) instanceof JFrame) {
								double vw = ij.imageDisplay().getActiveImageDisplay().dimension(ij.imageDisplay().getActiveImageDisplay().dimensionIndex(Axes.X))*zoomFactor;
								double vh = ij.imageDisplay().getActiveImageDisplay().dimension(ij.imageDisplay().getActiveImageDisplay().dimensionIndex(Axes.Y))*zoomFactor;
								((JFrame)ij.ui().getDisplayViewer(ij.display().getDisplay(displayName)).getWindow()).setSize(new Dimension((int)vw+50, (int)vh+150));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}				
					}
				});

//		    	ij.ui().getDisplayViewer(ij.display().getDisplay(displayName)).getPanel().redoLayout();
//		    	List<Display<?>> displays = ij.display().getDisplays();
//		    	for (Display<?> display : displays) {
//					System.out.println(display+" -----byname="+ij.display().getDisplay(displayName));
//				}
				
			}
		}).start();

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
				if(!zProjectedExperimentalData.axis(i).type().equals(nonZProjectedSegmentedGeom.axis(i).type()) || !zProjectedExperimentalData.axis(i).type().equals(zProjectedPreBleach.axis(i).type()) || !zProjectedExperimentalData.axis(i).type().equals(zProjectedAnalysisROI.axis(i).type())) {
					throw new Exception("Axis "+zProjectedExperimentalData.axis(i).type().getLabel()+" type for dimension index="+i+" of experimentalData, segmentedGeom, preBleach and analysisROI must match");
				}
				if(zProjectedExperimentalData.dimension(i) != nonZProjectedSegmentedGeom.dimension(i) || zProjectedExperimentalData.dimension(i) != zProjectedPreBleach.dimension(i) || zProjectedExperimentalData.dimension(i) != zProjectedAnalysisROI.dimension(i)) {
					throw new Exception("Axis "+zProjectedExperimentalData.axis(i).type().getLabel()+" length of experimentalData, segmentedGeom, preBleach and analysisROI must match");
				}
			}
		}
    	
    	//Assume any nonXY axis is the Z axis
    	if(nonZProjectedSegmentedGeom.numDimensions() > 3) {
    		throw new Exception("Segmented Geometry geometry allowed number of dimension must be <= 3");
    	}
    	int assumedZIndex = -1;
    	for (int i = 0; i < nonZProjectedSegmentedGeom.numDimensions(); i++) {
			if(nonZProjectedSegmentedGeom.dimensionIndex(Axes.X)!=i && nonZProjectedSegmentedGeom.dimensionIndex(Axes.Y)!=i) {
				assumedZIndex = i;
				break;
			}
		}
    	int zsize = (assumedZIndex == -1?1:(int)nonZProjectedSegmentedGeom.dimension(assumedZIndex));
    	
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
    		int currentZ = (assumedZIndex==-1?0:nonZProjectedSegmentedGeomCursor.getIntPosition(assumedZIndex));
    		int currXYPixelIndex = nonZProjectedSegmentedGeomCursor.getIntPosition(yIndex)*xsize + nonZProjectedSegmentedGeomCursor.getIntPosition(xIndex);
    		int currXYZPixelIndex = currXYPixelIndex + (assumedZIndex==-1?0:currentZ*xysize);
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

    	// Get the data cachekey that corresponds to the simulation job that just ran (ijSolverStatus.simJobId)
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
    	
    	// Display all the simulation data for a particular variable in a time stack
//    	if(zsize == 1) {
//	    	URL varInfoUrl = new URL("http://localhost:"+vh.findVCellApiServerPort()+"/"+"getdata"+"?"+"cachekey"+"="+cachekey);
//	    	doc = VCellHelper.getDocument(varInfoUrl);
//	    	System.out.println(VCellHelper.documentToString(doc));
//	    	double[] times = VCellHelper.getTimesFromVarInfos(doc);
//	    	double[] wholeSim = new double[xsize*ysize*times.length];
//	    	for (int i = 0; i < times.length; i++) {
//	        	URL dataUrl = new URL("http://localhost:"+vh.findVCellApiServerPort()+"/"+"getdata"+"?"+"cachekey"+"="+cachekey+"&"+"varname"+"="+varToAnalyze+"&"+"timeindex"+"="+(int)(i)+"&"+"jobid="+ijSolverStatus.getJobIndex());
//	    	    double[] simNormalizer = getNormalizedZProjectedSimTimePointData(dataUrl,xsize,ysize,zsize,null);
//				System.arraycopy(simNormalizer, 0, wholeSim, i*xysize, simNormalizer.length);
//			}
//	    	ArrayImg<DoubleType, DoubleArray> testImg = ArrayImgs.doubles(wholeSim, xsize,ysize,times.length);
//	    	ij.ui().show(testImg);
//    	}
    	
    	
//	    String[] varNames = VCellHelper.getVarNamesFromVarInfos(doc);
	    double[] bleachedTimeStack = new double[ANALYZE_COUNT*xsize*ysize];
	    
	    // Get the SIMULATION pre-bleach timepoint data for normalizing
    	URL dataUrl = new URL("http://localhost:"+vh.findVCellApiServerPort()+"/"+"getdata"+"?"+"cachekey"+"="+cachekey+"&"+"varname"+"="+varToAnalyze+"&"+"timeindex"+"="+(int)(0)+"&"+"jobid="+ijSolverStatus.getJobIndex());
	    double[] simNormalizer = getNormalizedZProjectedSimTimePointData(dataUrl,xsize,ysize,zsize,null);
//showAndZoom(ij, "Sim Data PreBleach", ArrayImgs.doubles(simNormalizer, xsize,ysize), 4);
	    
	    // Get the SIMULATION post-bleach data to analyze
	    for(int timeIndex = ANALYZE_BEGIN_TIMEINDEX;timeIndex<=ANALYZE_END_TIMEINDEX;timeIndex++){
	    	dataUrl = new URL("http://localhost:"+vh.findVCellApiServerPort()+"/"+"getdata"+"?"+"cachekey"+"="+cachekey+"&"+"varname"+"="+varToAnalyze+"&"+"timeindex"+"="+(int)timeIndex+"&"+"jobid="+ijSolverStatus.getJobIndex());
	 		double[] data = getNormalizedZProjectedSimTimePointData(dataUrl,xsize,ysize,zsize,simNormalizer);	 		
		    System.arraycopy(data, 0, bleachedTimeStack, (timeIndex-ANALYZE_BEGIN_TIMEINDEX)*xysize, data.length);
    	}

    	//Turn VCell data into iterableinterval
//    	FinalInterval zProjectedSimDataSize = FinalInterval.createMinSize(new long[] {0,0,0, xsize,ysize,ANALYZE_COUNT});//xyzt:origin and xyzt:size
		ArrayImg<DoubleType, DoubleArray> simImgs = ArrayImgs.doubles(bleachedTimeStack, new long[] {xsize,ysize,ANALYZE_COUNT});
		SCIFIOImgPlus<DoubleType> annotatedZProjectedSimPostBleachData = new SCIFIOImgPlus<>(simImgs, "Sim Data "+diffusionRate,new AxisType[] {Axes.X,Axes.Y,Axes.TIME});
//    	RandomAccessibleInterval<DoubleType> simExtracted = ij.op().transform().crop(annotatedSimData, simExtractInterval);   	
showAndZoom(ij, "Sim Data "+diffusionRate, annotatedZProjectedSimPostBleachData, 4);




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
		RandomAccessibleInterval<? extends RealType<?>> zProjectedExperimentalPostBleach = ij.op().transform().crop(zProjectedExperimentalData, analyzeOrigInterval);
    	Cursor<DoubleType> zProjectedSimPostBleachDataCursor = IterableRandomAccessibleInterval.create(annotatedZProjectedSimPostBleachData).localizingCursor();
    	Cursor<? extends RealType<?>> zProjectedExpPostBleachDataCursor = IterableRandomAccessibleInterval.create(zProjectedExperimentalPostBleach).localizingCursor();
    	IterableRandomAccessibleInterval<? extends RealType<?>> zProjectedPreBleachInterval = IterableRandomAccessibleInterval.create(zProjectedPreBleach);
    	BigDecimal sumSquaredDiff = new BigDecimal(0);
    	int[] analysisPosition = new int[zProjectedAnalysisROI.numDimensions()];
    	int[] preBleachPosition = new int[zProjectedPreBleachInterval.numDimensions()];
    	int[] experimentalPosition = new int[zProjectedExperimentalPostBleach.numDimensions()];
    	int[] simulationPosition = new int[annotatedZProjectedSimPostBleachData.numDimensions()];
    	int numValsInMask = 0;
    	while(zProjectedSimPostBleachDataCursor.hasNext()){
    		Cursor<UnsignedByteType> analysisCursor = zProjectedAnalysisROI.localizingCursor();
			Cursor<? extends RealType<?>> preBleachNormalizingCursor = zProjectedPreBleachInterval.localizingCursor();
        	while(analysisCursor.hasNext()){
        		boolean maskBit = !(analysisCursor.next().get() == 0);
        		DoubleType simPostBleachVal = zProjectedSimPostBleachDataCursor.next();
        		RealType<?> expPostBleachVal = zProjectedExpPostBleachDataCursor.next();
        		RealType<?> preBleachNormalizingVal = preBleachNormalizingCursor.next();
        		if(!maskBit) {//skip areas where analysis mask is 0
        			continue;
        		}
        		//Get the position of all the cursors and check they are in sync
        		analysisCursor.localize(analysisPosition);
        		preBleachNormalizingCursor.localize(preBleachPosition);
        		zProjectedExpPostBleachDataCursor.localize(experimentalPosition);
        		zProjectedSimPostBleachDataCursor.localize(simulationPosition);
//        		System.out.println("analysis="+Arrays.toString(analysisPosition)+" prebleach="+Arrays.toString(preBleachPosition)+" exp="+Arrays.toString(experimentalPosition)+" sim="+Arrays.toString(simulationPosition));
        		if(!Arrays.equals(analysisPosition, preBleachPosition) || !Arrays.equals(experimentalPosition, simulationPosition)){
        			throw new Exception("Cursor positions not equal");
        		}
        		if(analysisPosition[0] != experimentalPosition[0] || analysisPosition[1] != experimentalPosition[1]) {//check xy are same
        			throw new Exception("XY Cursor position not equal for analysisROI mask and experimental data");
        		}
        		double normalizedExpDataVal = 0;
        		if(expPostBleachVal.getRealDouble() != 0) {
        			normalizedExpDataVal = (expPostBleachVal.getRealDouble())/(preBleachNormalizingVal.getRealDouble()+Double.MIN_VALUE);
        		}
    			double diff = simPostBleachVal.get()-(normalizedExpDataVal);
    			sumSquaredDiff = sumSquaredDiff.add(new BigDecimal(Math.pow(diff,2.0)));
    			numValsInMask++;
        	}
    	}
		return sumSquaredDiff.divide(new BigDecimal(numValsInMask),8, RoundingMode.HALF_UP).doubleValue();
    }

    public static double[] getNormalizedZProjectedSimTimePointData(URL dataUrl,int xsize,int ysize,int zsize,double[] normalizer) throws Exception{
    	Document doc = VCellHelper.getDocument(dataUrl);
//		System.out.println(VCellHelper.documentToString(doc));
		NodeList nodes = doc.getElementsByTagName("ijData");
		BasicStackDimensions basicStackDimensions = VCellHelper.getDimensions(nodes.item(0));
		if(basicStackDimensions.xsize != xsize || basicStackDimensions.ysize != ysize || basicStackDimensions.zsize != zsize) {
			throw new Exception("One or more sim data xyz dimensions="+basicStackDimensions.xsize+","+basicStackDimensions.ysize+","+basicStackDimensions.zsize+" does not match expected xyz sizes="+xsize+","+ysize+","+zsize);
		}
		double[] zData = VCellHelper.getData(nodes.item(0));
 		//Sum pixel values in Z direction to match experimental data (open pinhole confocal, essentially brightfield)
		int xysize = xsize*ysize;
		double[] normalizedData = new double[xysize];
		for (int i = 0; i < zData.length; i++) {
			normalizedData[(i%xysize)]+= zData[i];
		}
		if(normalizer != null) {
			for (int i = 0; i < normalizedData.length; i++) {
				if(normalizedData[i] != 0) {
					normalizedData[i] = (normalizedData[i]+Double.MIN_VALUE)/(normalizer[i]+Double.MIN_VALUE);
				}
			}
		}
		return normalizedData;

    }
}
