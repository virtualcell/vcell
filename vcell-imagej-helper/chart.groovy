
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.ChartPanel
import org.jfree.data.category.CategoryDataset
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.DefaultXYDataset
import javax.swing.JFrame

String title = "test"
String xAxisLabel = "distance"
String yAxisLabel = "val"
double[][] data = [[1,2,3,4,5,6,7,8,9,10],[1,2,3,4,5,6,7,8,9,10]];

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