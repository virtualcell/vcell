#@VCellHelper vh

import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.ChartPanel
import org.jfree.data.category.CategoryDataset
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.DefaultXYDataset
import javax.swing.JFrame
import org.vcell.imagej.helper.VCellHelper.IJTimeSeriesJobResults


vh.getApiInfo()
start = 0
end = 40000
int[] indices = new int[end-start]
for(int i=0;i<indices.length;i++){
	indices[i] = start+i
}
String[] vars = ["C_cyt"]
ijTimeSeriesJobResults = vh.getTimeSeries(vars, indices, 0.05, 1, 0.05, false, false, 0, Integer.valueOf("7"))
double[] vals = new double[indices.length]
for(int i=0;i<indices.length;i++){
	vals[i] = ijTimeSeriesJobResults.data[0][i+1][0]
}
double[][] data = [indices,vals];


String title = "test"
String xAxisLabel = "distance"
String yAxisLabel = "val"


xyDataset = new DefaultXYDataset()
xyDataset.addSeries((Comparable) "data1", data)

chart = ChartFactory.createXYLineChart( title,  xAxisLabel,  yAxisLabel, xyDataset)
chartPanel = new ChartPanel(chart)

frame = new JFrame("Chart");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.getContentPane().add(chartPanel)
        //Display the window.
frame.pack();
frame.setVisible(true);