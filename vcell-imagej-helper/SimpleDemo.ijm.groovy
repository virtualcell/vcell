//@VCellHelper vh
//@ImageJ ij


import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

	SimpleDateFormat EASY_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	try {
//			VCellHelper vh = new VCellHelper();
		
		//
		// SEARCH for a known public simulation (in a BioModel), get information about the simulation and download a portion of the data
		//
		// Set the date range for models we are interested in
		VCellHelper.VCellModelVersionTimeRange vcellModelVersionTimeRange = new VCellHelper.VCellModelVersionTimeRange(EASY_DATE.parse("2016-01-01 00:00:00"), EASY_DATE.parse("2017-01-01 00:00:00"));
		// Set the ModelName, ApplicationName and SimulationName for models we are interested in
		VCellHelper.VCellModelSearch search = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.bm, "tutorial60", "Rule-based_egfr_tutorial", "*stoch", "Simulation1",null,null);
		// Use VCellHelper to contact VCell client and return preliminary info
		ArrayList<VCellModelSearchResults> vcellModelSearchResults = vh.getSearchedModelSimCacheKey(true, search,vcellModelVersionTimeRange);
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
	} catch (Exception e) {
		e.printStackTrace();
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
