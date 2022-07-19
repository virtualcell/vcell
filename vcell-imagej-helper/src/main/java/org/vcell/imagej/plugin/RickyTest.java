package org.vcell.imagej.plugin;

import java.util.ArrayList;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Hashtable;

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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/*import org.eclipse.swt.widgets.Text; */
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt.MessageType;
import org.scijava.ui.UIService;
import org.vcell.imagej.helper.VCellHelper;
import org.vcell.imagej.helper.VCellHelper.BasicStackDimensions;
import org.vcell.imagej.helper.VCellHelper.IJDataList;
import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults;

import net.imagej.ImageJ;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;

import net.imagej.ImageJ;

@Plugin(type = ContextCommand.class, menuPath = "Plugins>RickyTest")
public class RickyTest extends ContextCommand{

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
	
	@Parameter (choices={" ","Line Plot", "Model Load"}, style="listBox")
  	private String Action;

	public static void main(String[] args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
	}

	private JDialog progressDialog = null;
	private final Dimension dim = new Dimension(200,25);
	private final JProgressBar jProgressBar = new JProgressBar(0,100) {
		@Override
		public Dimension getPreferredSize() {
			// TODO Auto-generated method stub
			return dim;
		}
		@Override
		public Dimension getSize(Dimension rv) {
			// TODO Auto-generated method stub
			return dim;
		}
	};
    private void displayProgressBar(boolean bShow,String message,String title) {
    	if(progressDialog == null) {
			JFrame applicationFrame = (JFrame)uiService.getDefaultUI().getApplicationFrame();
			progressDialog = new JDialog(applicationFrame,"Checking for VCell Client",false);
			progressDialog.getContentPane().add(jProgressBar);
			jProgressBar.setStringPainted(true);
			progressDialog.pack();
    	}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(!bShow) {
					progressDialog.setVisible(false);
					return;
				}
				jProgressBar.setValue(0);
				progressDialog.setTitle(title);
				jProgressBar.setString(message);
				progressDialog.setVisible(true);
			}
		});
    }

    private Hashtable<String,Thread> threadHash = new Hashtable<String,Thread>();
    private void startJProgressThread0(String lastName,String newName) {
    	if(lastName != null && threadHash.get(lastName) != null) {
	    	threadHash.get(lastName).interrupt();
	    	while(threadHash.get(lastName) != null) {
	    		try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
	    	}
    	}
    	if(newName == null) {
    		return;
    	}
    	final Thread progressThread = new Thread(new Runnable(){
			@Override
			public void run() {
				final int[] progress = new int[] {1};
				while(progressDialog.isVisible()) {
					if(Thread.currentThread().isInterrupted()) {
						break;
					}
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							jProgressBar.setValue(progress[0]);
						}});
					progress[0]++;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						break;
					}
				}
				threadHash.remove(Thread.currentThread().getName());
			}});
    	threadHash.put(newName, progressThread);
		progressThread.setName(newName);
		progressThread.setDaemon(true);//So not block JVM exit
		progressThread.start();
    }
	
	@Override
	public void run() {
		
	//System.out.println(Action);
	
	 if (Action.equals("Line Plot")) {
		 try {
				//Find the port that a separately running VCell client is listening on
				System.out.println("vcell service port="+vcellHelper.findVCellApiServerPort());
			} catch (Exception e) {
					uiService.showDialog("Click \"Tools\" in the top left corner of the VCell Application, then \"Start Fiji (ImageJ) service\"\n", "Plugin Unable to Contact VCell Client", MessageType.ERROR_MESSAGE);
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
//			chart.getXYPlot().getDomainAxis().setRange(yAxisRange);//Y
//			chart.getXYPlot().getRangeAxis().setRange(xAxisRange);//X
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

    	  } else if (Action.equals("Model Load")) {
    		  displayProgressBar(true, "Checking listening ports...", "Checking for VCell Client");
    			startJProgressThread0(null,"Check");
    			
    	      try {
    	    	  //Find the port that a separately running VCell client is listening on
    	    	  //
    				System.out.println("vcell service port="+vcellHelper.findVCellApiServerPort());
    				//uiService.getDisplayViewer(textDisplay).dispose();
    				displayProgressBar(false, null, null);
    			} catch (Exception e) {
    				//e.printStackTrace();
    				displayProgressBar(false, null, null);
    				//uiService.getDisplayViewer(textDisplay).dispose();
    				uiService.showDialog("Click \"Tools\" in the top left corner of the VCell Application, then \"Start Fiji (ImageJ) service\"\n", "Plugin Unable to Contact VCell Client", MessageType.ERROR_MESSAGE);
    				return;
    			}
    	      
    	      
    			displayProgressBar(true, "Searching...", "Searching VCell Models");
    			startJProgressThread0("Check","Search");

    			String theCacheKey = null;
    	      VCellHelper.VCellModelSearch vcms = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.bm,vCellUser,vCellModel,application,simulation,null,null);
    	      try {
    			ArrayList<VCellModelSearchResults> vcmsr = vcellHelper.getSearchedModelSimCacheKey(false,vcms,null);
    			if(vcmsr.size() == 0) {
    				throw new Exception("No Results for search found");
    			}
    			theCacheKey = vcmsr.get(0).getCacheKey();
    			System.out.println("theCacheKey="+theCacheKey);
    			displayProgressBar(false, null, null);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			//e.printStackTrace();
    			uiService.showDialog("VCellHelper.ModelType.bm,vCellUser,vCellModel,application,simulation,null,null\n"+e.getMessage(), "Search failed", MessageType.ERROR_MESSAGE);
    			displayProgressBar(false, null, null);
    		}
    	      
    	      displayProgressBar(true, "Loading Data...", "Loading Data");
    	      startJProgressThread0("Search","getTimePointData");

    	      try {
    	    	  String var = variable;
    	    	  int[] time = new int[] {timePoint};
    	    	  IJDataList tpd = vcellHelper.getTimePointData(theCacheKey,var,VCellHelper.VARTYPE_POSTPROC.NotPostProcess,time,0);
    	    	  double[] data = tpd.ijData[0].getDoubleData();
    	    	  BasicStackDimensions bsd = tpd.ijData[0].stackInfo;
    	    	  System.out.println(bsd.xsize+" "+bsd.ysize);
    	    	  ArrayImg<DoubleType, DoubleArray> testimg = ArrayImgs.doubles( data, bsd.xsize,bsd.ysize);
    	    	  uiService.show(testimg);
    	    	  
    	    	  displayProgressBar(false, null, null);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
//    			e.printStackTrace();
    			uiService.showDialog("theCacheKey,var,VCellHelper.VARTYPE_POSTPROC.NotPostProcess,time,0\n"+e.getMessage(), "getTimePoint(...) failed", MessageType.ERROR_MESSAGE);
    			displayProgressBar(false, null, null);

    		}
    	      startJProgressThread0("getTimePointData",null);
    	  } else {
    		  uiService.showDialog("Please select an action", "Task failed", MessageType.ERROR_MESSAGE);
    	  }
  }
}