//@VCellHelper vh
//@ImageJ ij


import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.vcell.imagej.helper.VCellHelper.BasicStackDimensions;
import org.vcell.imagej.helper.VCellHelper.IJSolverStatus;
import org.vcell.imagej.helper.VCellHelper.IJVarInfo;
import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults;
import org.vcell.imagej.helper.VCellHelper;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;
import javax.swing.JFrame;
import ij.WindowManager;
import java.awt.Window;
import javax.swing.JRootPane;
import org.jfree.data.xy.XYDataset;
import javax.swing.SwingUtilities;

import io.scif.img.SCIFIOImgPlus;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imagej.ImgPlusMetadata;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;

	//Global variable
	EASY_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	System.out.println("----------ODE data from stochastic variable----------");
	odeDataTest();
	System.out.println("----------PDE data from 3D volume variable----------");
	pdeDataTest();
	System.out.println("----------Run simulation with overriden parameter----------");
	localSimRunVCellTest();

	def localSimRunVCellTest(){
		VCellHelper.VCellModelVersionTimeRange vcellModelVersionTimeRange = new VCellHelper.VCellModelVersionTimeRange(EASY_DATE.parse("2015-01-01 00:00:00"), EASY_DATE.parse("2018-08-01 00:00:00"));
    	VCellHelper.VCellModelSearch search = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.bm, "tutorial", "Tutorial_FRAPbinding", "Spatial", "FRAP binding",null,null);
    	// Find the simulation we want to run
    	ArrayList<VCellModelSearchResults> vcellModelSearchResults = vh.getSearchedModelSimCacheKey(false, search,vcellModelVersionTimeRange);
		if(vcellModelSearchResults.size() != 1) {
			throw new Exception("Expecting only 1 model from search results");
		}
		// Get cacheKey to reference the found simulation
    	String cacheKey = vcellModelSearchResults.get(0).getCacheKey();

    	// As simple example define new simulation endTime and 'rf_diffusionRate' parameter override (refer to FRAP script for more advanced example)
		final double newDiffusionRate = 1.5;
    	HashMap<String,String> simulationParameterOverrides = new HashMap<>();
    	simulationParameterOverrides.put("rf_diffusionRate", ""+newDiffusionRate);
    	double newEndTime = 5.0;
    	// Start running the solver LOCALLY (solver will run on users local machine, equivalent to 'quickrun')
    	System.out.println("Starting solver...");
    	IJSolverStatus ijSolverStatus = vh.startVCellSolver(Long.parseLong(cacheKey), null/*keep default geom*/, simulationParameterOverrides, null/*keep default initConditions*/,newEndTime);
    	System.out.println(ijSolverStatus.toString());
    	String simulationJobId = ijSolverStatus.simJobId;
    	// Wait for simulation to finish
    	while(true) {
    		Thread.sleep(5000);
        	ijSolverStatus =  vh.getSolverStatus(simulationJobId);
        	System.out.println(ijSolverStatus.toString());
        	String statusName = ijSolverStatus.statusName.toLowerCase();
        	if(statusName.equals("finished") || statusName.equals("stopped") || statusName.equals("aborted")) {
        		break;
        	}
    	}
    	//Get reference to simulation data
    	String simulationDataCacheKey = vh.getSimulationDataCacheKey(ijSolverStatus.simJobId);
    	// Get data for variable 'rf' at timeIndex 5
    	VCellHelper.TimePointData timePointData = vh.getTimePointData(simulationDataCacheKey, "rf", [5] as int[], ijSolverStatus.getJobIndex());
    	BasicStackDimensions bsd = timePointData.getBasicStackDimensions();
		System.out.print("xsize="+bsd.xsize+" ysize="+bsd.ysize+" zsize="+bsd.zsize+" csize="+bsd.csize+" tsize="+bsd.tsize);

	}
	def pdeDataTest(){
		// Set the ModelName, ApplicationName and SimulationName for models we are interested in
		VCellHelper.VCellModelSearch search = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.mm, null/*Any user*/, "Calcium sparks_simple", null/*MathModels have no Applications*/, "seed=18",3,null);
		// Use VCellHelper to contact VCell client and return preliminary info
		ArrayList<VCellModelSearchResults> vcellModelSearchResults = vh.getSearchedModelSimCacheKey(false, search,null/*Any date*/);
		System.out.println("Model name='"+vcellModelSearchResults.get(0).getModelName()+"'");
		System.out.println("Application name='"+vcellModelSearchResults.get(0).getApplicationName()+"'");
		System.out.println("Simulation name='"+vcellModelSearchResults.get(0).getSimulationName()+"'"+" geomDims="+vcellModelSearchResults.get(0).getGeometryDimension()+" mathType="+vcellModelSearchResults.get(0).getMathType());
		// Get the cacheKey that identifies the particular simulation result we are interested in
		String simulationInfoCacheKey = vcellModelSearchResults.get(0).getCacheKey();
		// Get more specific information about our simulation using the cacheKey (times, parameter scan count, variable names,...)
		// (the cacheKey was stored in the running VCell client during the previous search and is used to quickly lookup information)
		VCellHelper.IJVarInfos ijVarInfos = vh.getSimulationInfo(simulationInfoCacheKey);
		final int THE_TIME_INDEX = 2;
		for (IJVarInfo ijVarInfo : ijVarInfos.getIjVarInfo()) {
			if(ijVarInfo.getName().equals("Ca")) {
				// Get the actual data (our simulation is non-spatial ODE)
				System.out.println("getting data for variable '"+ijVarInfo.getName()+"' at timepoint="+ijVarInfos.getTimes()[THE_TIME_INDEX]);
				VCellHelper.TimePointData timePointData = vh.getTimePointData(simulationInfoCacheKey, ijVarInfo.getName(), [THE_TIME_INDEX] as int[],0);
				BasicStackDimensions bsd = timePointData.getBasicStackDimensions();
				if(bsd.csize != 1 || bsd.tsize != 1) {
					throw new Exception("Expecting csize and tsize to be 1");
				}
				ArrayImg<DoubleType, DoubleArray> data3dAtSingleTimePoint = ArrayImgs.doubles(timePointData.getTimePointData(), bsd.xsize,bsd.ysize,bsd.zsize,bsd.csize,bsd.tsize);
				ImgPlus<DoubleType> scifoImg = SCIFIOImgPlus.wrap(data3dAtSingleTimePoint);
				DoubleType max= ij.op().stats().max(scifoImg);
				DoubleType min= ij.op().stats().min(scifoImg);
				System.out.println("name='"+ijVarInfo.getName()+"' type="+ijVarInfo.getVariableType()+" min="+min.get()+" max="+max.get());
			}
		}
	}

	def odeDataTest() {
		//
		// SEARCH for a known public simulation (in a BioModel), get information about the simulation and download a portion of the data
		//
		// Set the date range for models we are interested in
		VCellHelper.VCellModelVersionTimeRange vcellModelVersionTimeRange = new VCellHelper.VCellModelVersionTimeRange(EASY_DATE.parse("2016-01-01 00:00:00"), EASY_DATE.parse("2017-01-01 00:00:00"));
		// Set the ModelName, ApplicationName and SimulationName for models we are interested in
		VCellHelper.VCellModelSearch search = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.bm, "tutorial60", "Rule-based_egfr_tutorial", "*stoch", "Simulation1",null,null);
		// Use VCellHelper to contact VCell client and return preliminary info
		ArrayList<VCellModelSearchResults> vcellModelSearchResults = vh.getSearchedModelSimCacheKey(false, search,vcellModelVersionTimeRange);
		//  Our search was constructed such that only 1 simulation should be in the search results
		if(vcellModelSearchResults.size() != 1) {
			throw new Exception("Expecting exactly 1 model from search results");
		}
		System.out.println("Model name='"+vcellModelSearchResults.get(0).getModelName()+"'");
		System.out.println("Application name='"+vcellModelSearchResults.get(0).getApplicationName()+"'");
		System.out.println("Simulation name='"+vcellModelSearchResults.get(0).getSimulationName()+"'"+" geomDims="+vcellModelSearchResults.get(0).getGeometryDimension()+" mathType="+vcellModelSearchResults.get(0).getMathType());
		// Get the cacheKey that identifies the particular simulation result we are interested in
		String simulationInfoCacheKey = vcellModelSearchResults.get(0).getCacheKey();
		
		// Get more specific information about our simulation using the cacheKey (times, parameter scan count, variable names,...)
		// (the cacheKey was stored in the running VCell client during the previous search and is used to quickly lookup information)
		VCellHelper.IJVarInfos ijVarInfos = vh.getSimulationInfo(simulationInfoCacheKey);
			for (IJVarInfo ijVarInfo : ijVarInfos.getIjVarInfo()) {
				if(ijVarInfo.getName().startsWith("Dimer")) {
					// Get the actual data (our simulation is non-spatial ODE)
					VCellHelper.TimePointData timePointData = vh.getTimePointData(simulationInfoCacheKey, ijVarInfo.getName(), null,0);
					// Check the length of the data == length of timepoints (should be true for non-spatial data)
					if(timePointData.getTimePointData().length != ijVarInfos.getTimes().length) {
						throw new Exception("Expecting datalength for variable "+timePointData.getTimePointData().length+" to match number of timepoints "+ijVarInfos.getTimes().length);
					}
//					System.out.println("name='"+ijVarInfo.getName()+"' type="+ijVarInfo.getVariableType());
					makeChart(ijVarInfos.getTimes(),timePointData.getTimePointData(),"Rule Based Stochastic Data","Time vs. Counts","Time","Counts",ijVarInfo.getName());
				}
			}
	}

//Make a new chart if a window with frame title 'chartFrameTitle' doesn't exist, otherwise re-use chart and add data as new series
def makeChart(double[] domain,double[] range, String chartFrameTitle,String chartHeading,String domainLabel,String rangeLabel,String seriesLabel){
	double[][] newdata =[domain,range] as double[][];
	XYDataset dataset = null;
	if(WindowManager.getWindow(chartFrameTitle) == null){
		dataset = new DefaultXYDataset();
		JFreeChart xyLineChart = ChartFactory.createXYLineChart(chartHeading, domainLabel, rangeLabel, dataset);
		xyLineChart.getXYPlot().getRangeAxis().setAutoRange(true);
		xyLineChart.getXYPlot().getDomainAxis().setAutoRange(true);
		ChartPanel chartPanel = new ChartPanel(xyLineChart);
		JFrame frame = new JFrame(chartFrameTitle);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(chartPanel);
		frame.pack();
		WindowManager.addWindow(frame);
		frame.setVisible(true);
	}else{
		Window chartWin = WindowManager.getWindow(chartFrameTitle);
		chartWin.setVisible(true);
		JFreeChart dataChart = ((ChartPanel)(((JRootPane)chartWin.getComponents()[0]).getContentPane().getComponents()[0])).getChart();
		dataset = dataChart.getXYPlot().getDataset();
	}
	SwingUtilities.invokeLater(new Runnable() {
		@Override
		public void run() {
			dataset.addSeries(seriesLabel,newdata);
		}
	});
}
