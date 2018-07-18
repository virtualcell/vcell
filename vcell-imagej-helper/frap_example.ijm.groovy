//@VCellHelper vh
//@ImageJ ij

import org.vcell.imagej.helper.CR;
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
import net.imagej.display.DefaultDatasetView;
import net.imagej.display.DefaultImageDisplay;

final String CHART_TITLE = "FRAP results";


//disps = ij.display().getDisplays();
//for(DefaultImageDisplay ddsv:disps){
//println(ddsv.getActiveView().getData().getDepth());	
//}
//
//owins = ij.window().getOpenWindows();
//println(owins);
//noimg =WindowManager.getNonImageTitles();
//println(noimg);
//Window mywin = WindowManager.getWindow(CHART_TITLE);
////WindowManager.removeWindow(mywin);
////if(true){return}
//mywin.setVisible(true);
//JFreeChart mychart = ((ChartPanel)(((JRootPane)mywin.getComponents()[0]).getContentPane().getComponents()[0])).getChart();
//XYDataset mydataset = mychart.getXYPlot().getDataset();
////double[][] newdata = [[0.95,1.0],[0.194,0.195]] as double[][]
////SwingUtilities.invokeLater(new Runnable() {
////	@Override
////	public void run() {
////		mydataset.addSeries("moredata2",newdata);
//////		mychart.fireChartChanged();
////	}
////});
//println(mydataset.getSeriesCount());
//if(true){return}

File exampleDir = new File("C:/Users/frm/VCellTrunkGitWorkspace2/vcell/vcell-imagej-helper");
println(exampleDir);
//double[] diffRates = [
//			1.25,
//			1.3,
//			1.4,
//			1.45,
//			1.5,
//			1.55,
//			1.6,
//			1.65
//] as double[];

double[] diffRates = [
			0.00001,
			0.5,
			1.0,
			1.5,
			2.0,
			2.5,
			3.0,
			3.5,
			4.0
] as double[];

//double[] diffRates = [
//			1.40,
//			1.41,
//			1.42,
//			1.43,
//			1.44,
//			1.45,
//			1.46,
//			1.47,
//			1.48
//] as double[];

double[] mse = CR.testSolver2(diffRates,exampleDir,vh,ij);
double minResult = Double.POSITIVE_INFINITY;
double maxResult = Double.NEGATIVE_INFINITY;
for(int i =0;i<mse.length;i++){
	minResult = Math.min(minResult, mse[i]);
	maxResult = Math.max(maxResult, mse[i]);
}
//String[] chartWindowTitles = WindowManager.getNonImageTitles();
//Arrays.sort(chartWindowTitles);
//int foundIndex = Arrays.binarySearch(chartWindowTitles,CHART_TITLE);
double[][] newdata =[diffRates,mse] as double[][];

XYDataset dataset = null;
if(WindowManager.getWindow(CHART_TITLE) == null){
	dataset = new DefaultXYDataset();
	JFreeChart xyLineChart = ChartFactory.createXYLineChart("Experimental vs. Simulated FRAP", "Diffusion", "MSE", dataset);
	xyLineChart.getXYPlot().getRangeAxis().setAutoRange(true);
	xyLineChart.getXYPlot().getDomainAxis().setAutoRange(true);
//	if(minResult != maxResult){
//		xyLineChart.getXYPlot().getRangeAxis().setRange(minResult, maxResult);	
//	}
	ChartPanel chartPanel = new ChartPanel(xyLineChart);
	JFrame frame = new JFrame(CHART_TITLE);
	//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().add(chartPanel);
	frame.pack();
	WindowManager.addWindow(frame);
	frame.setVisible(true);
}else{
	Window chartWin = WindowManager.getWindow(CHART_TITLE);
	JFreeChart dataChart = ((ChartPanel)(((JRootPane)chartWin.getComponents()[0]).getContentPane().getComponents()[0])).getChart();
	dataset = dataChart.getXYPlot().getDataset();
//	double[][] newdata = [[0.95,1.0],[0.194,0.195]] as double[][]
}

//dataset.addSeries("Exp vs. Sim", data);
SwingUtilities.invokeLater(new Runnable() {
	@Override
	public void run() {
		dataset.addSeries("trial "+(dataset.getSeriesCount()+1),newdata);
//		mychart.fireChartChanged();
	}
});


