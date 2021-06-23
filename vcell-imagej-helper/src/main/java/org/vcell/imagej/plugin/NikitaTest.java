package org.vcell.imagej.plugin;

import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYDataset;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt.MessageType;
import org.scijava.ui.UIService;
import org.vcell.imagej.helper.VCellHelper;
import org.vcell.imagej.helper.VCellHelper.IJDataList;
import org.vcell.imagej.helper.VCellHelper.IJTimeSeriesJobResults;
import org.vcell.imagej.helper.VCellHelper.IJVarInfos;
import org.vcell.imagej.helper.VCellHelper.VARTYPE_POSTPROC;
import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults;

import net.imagej.ImageJ;

@Plugin(type = ContextCommand.class, menuPath = "Plugins>NikitaTest")
public class NikitaTest extends ContextCommand{

	@Parameter
	private UIService uiService;

  	@Parameter
	private VCellHelper vcellHelper;
  	
  	@Parameter (choices={"bm", "mm"}, style="listBox")
  	private String modelType;
  	
	@Parameter
	private String vCellUser = "colreeze";
	
	@Parameter
	private String  vCellModel = "Monkeyflower_pigmentation_v2";
	
	@Parameter
	private String application = "Pattern_formation";
	
	@Parameter
	private String simulation = "WT";
	
	//String[] require more complicated programming
	//For now use just 1 string
	@Parameter
	private String variable = "A";
	
	@Parameter
	private int  timePoint = 500;

	@Parameter
	private int  startIndex = 1279;

	@Parameter
	private int  endIndex = 1321;

	@Parameter
	private String  imageName = "test";

	public static void main(String[] args) {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
	}

	@Override
	public void run() {
		
		try {
			//Find the port that a separately running VCell client is listening on
			System.out.println("vcell service port="+vcellHelper.findVCellApiServerPort());
		} catch (Exception e) {
				uiService.showDialog("Activate VCell client ImageJ service\nTools->'Start Fiji (ImageJ) service'\n"+e.getMessage(), "Couldn't contact VCell client", MessageType.ERROR_MESSAGE);
				return;
		}
		String theCacheKey = null;
		VCellHelper.VCellModelSearch vcms = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.valueOf(modelType),vCellUser,vCellModel,application,simulation,null,null);
	      try {
			ArrayList<VCellModelSearchResults> vcmsr = vcellHelper.getSearchedModelSimCacheKey(false,vcms,null);
			if(vcmsr.size() == 0) {
				throw new Exception("No Results for search found");
			}
			theCacheKey = vcmsr.get(0).getCacheKey();
			System.out.println("theCacheKey="+theCacheKey);
		} catch (Exception e) {
			uiService.showDialog(modelType+", "+vCellUser+", "+vCellModel+", "+application+", "+simulation+", null, null\n"+e.getMessage(), "Search failed", MessageType.ERROR_MESSAGE);
			return;
		}

    	   IJDataList timePointData = null;
    	  int[] timePointIndexes = new int[1]; // there is 1 timePoint
    	  int theTimePointIndex = 0;
    	  timePointIndexes[theTimePointIndex] = timePoint;
    	  try {
			timePointData = vcellHelper.getTimePointData(theCacheKey, variable, VARTYPE_POSTPROC.NotPostProcess, timePointIndexes, 0);
		} catch (Exception e) {
			uiService.showDialog(modelType+", "+vCellUser+", "+vCellModel+", "+application+", "+simulation+", "+variable+", "+timePoint+"\n"+e.getMessage(), "Get Data failed", MessageType.ERROR_MESSAGE);
			return;
		}
    	 double[] theTimePointData = timePointData.ijData[theTimePointIndex].getDoubleData();
    	Range xAxisRange = null;
   	  int[] dataIndexes = new int[endIndex-startIndex+1];
		double[] dataIndexesDouble = new double[dataIndexes.length];// This is just for JFreeChart
   	  for(int i=startIndex;i<=endIndex;i++) {
   		  dataIndexes[i-startIndex] = i;
   		dataIndexesDouble[i-startIndex] = i;
   		xAxisRange = Range.expandToInclude(xAxisRange, dataIndexesDouble[i-startIndex]);
   	  }

   	  Range yAxisRange = null;
    	double[] chartTheseDataPoints = new double[dataIndexes.length];
		for(int i=0;i<dataIndexes.length;i++){
			chartTheseDataPoints[i] = theTimePointData[dataIndexes[i]];
			yAxisRange = Range.expandToInclude(yAxisRange,chartTheseDataPoints[i]);
				//ijTimeSeriesJobResults.data[0/*index of "r"*/][i+1/*data, skip 0 because it holds copy of times*/][0/*0 always because we had only 1 timePoint=22.0*/]
		}
	      
	   	try {
			//LINE plot of data at 1 timePoint along defined indexes
			//Create JFreechart x,y axis arrays for plotting x=data indexes, y=dataPoint values
			double[][] data = new double[][] {dataIndexesDouble,chartTheseDataPoints};



			String title = "LINE Plot at time="+timePoint;
			String xAxisLabel = "distance";
			String yAxisLabel = "val";

			DefaultXYDataset xyDataset = new DefaultXYDataset();
			xyDataset.addSeries( "data1", data);


			JFreeChart chart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, xyDataset, PlotOrientation.VERTICAL, false, false, false);
			chart.getXYPlot().getDomainAxis().setRange(xAxisRange);//Y
			chart.getXYPlot().getRangeAxis().setRange(yAxisRange);//X
			ChartPanel chartPanel = new ChartPanel(chart);

			JFrame frame = new JFrame("Chart");
			        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(chartPanel);
			        //Display the window.
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			uiService.showDialog("LINE PlotGet Data failed\n"+e.getMessage(), MessageType.ERROR_MESSAGE);
			return;
		}

	   	  
	   	  
	   	IJTimeSeriesJobResults timeSeries = null;
	   	try {
			IJVarInfos simulationInfo = vcellHelper.getSimulationInfo(theCacheKey);
			double[] times = simulationInfo.getTimes();
			
			timeSeries = vcellHelper.getTimeSeries(new String[] {variable}, new int[] {dataIndexes[0]}, times[0], 1, times[times.length-1], false, false, 0, Integer.parseInt(theCacheKey));
		} catch (Exception e) {
			uiService.showDialog("TIME Plot Get Data failed\n"+e.getMessage(), MessageType.ERROR_MESSAGE);
			return;
		}
	   	
		try {
			//TIME plot of data at 1 timePoint along defined indexes
			//Create JFreechart x,y axis arrays for plotting x=data indexes, y=dataPoint values
			double[][] data = new double[][] {timeSeries.data[0][0],timeSeries.data[0][1]};



			String title = "TIME Plot at dataIndex="+dataIndexes[0];
			String xAxisLabel = "time";
			String yAxisLabel = "val";

			DefaultXYDataset xyDataset = new DefaultXYDataset();
			xyDataset.addSeries( "data1", data);


			JFreeChart chart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, xyDataset, PlotOrientation.VERTICAL, false, false, false);
//		chart.getXYPlot().getDomainAxis().setRange(yAxisRange);//Y
//		chart.getXYPlot().getRangeAxis().setRange(xAxisRange);//X
			ChartPanel chartPanel = new ChartPanel(chart);

			JFrame frame = new JFrame("Chart");
			        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(chartPanel);
			        //Display the window.
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			uiService.showDialog("TIME Plot show chart failed\n"+e.getMessage(), MessageType.ERROR_MESSAGE);
			return;
		}		
	}
}
