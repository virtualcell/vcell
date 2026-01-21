package cbit.vcell.simdata;

import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.SimulationVersion;

import javax.swing.*;
import java.awt.*;
import java.util.*;

// post-processing for langevin batch runs is now being handled server-side in the langevin solver
@Deprecated
public class LangevinPostProcessor {

    public static final String FAILURE_KEY = "FAILURE_KEY";
	public static final String SIMULATION_KEY = "SIMULATION_KEY";
    public static final String SIMULATION_OWNER_KEY = "SIMULATION_OWNER_KEY";
    public static final String LANGEVIN_MULTI_TRIAL_KEY = "LANGEVIN_MULTI_TRIAL_KEY";
    public static final String LPP_OUTPUT_KEY = "LANGEVIN_POST_PROCESSOR_OUTPUT_KEY";

    boolean isMultiTrial = false;   // springsalad / langevin definition of multi-trial: numTasks > 1
    boolean failure;
    Simulation sim;
    SimulationOwner simOwner;
    Map<Integer, ODESolverResultSet> odeSolverResultSetMap;

    // the results
    RowColumnResultSet averagesResultSet;
    RowColumnResultSet stdResultSet;
    RowColumnResultSet minResultSet;
    RowColumnResultSet maxResultSet;


    public LangevinPostProcessorOutput postProcessLangevinResults(LangevinPostProcessorInput lppInput) {

        sim = lppInput.getSimulation();
        simOwner = lppInput.getSimulationOwner();
        odeSolverResultSetMap = lppInput.getOdeSolverResultSetMap();    // key = trial index, value = simulation results (ODESolverResultSet object) for that trial
        failure = lppInput.isFailed();
        isMultiTrial = odeSolverResultSetMap.size() > 1 ? true : false;

        LangevinPostProcessorOutput pllOut = new LangevinPostProcessorOutput(sim, simOwner);
        if(failure) {
            pllOut.setFailed(failure);
            pllOut.setMultiTrial(isMultiTrial);
            return pllOut;
        }

        // probably useless at this point
        if(sim != null) {
            SimulationInfo simInfo = sim.getSimulationInfo();
            VCSimulationIdentifier vcSimulationIdentifier = simInfo.getAuthoritativeVCSimulationIdentifier();
            MathOverrides mathOverrides = sim.getMathOverrides();
            SimulationVersion simVersion = simInfo.getSimulationVersion();
        }

        try {
            ODESolverResultSet tempODESolverResultSet = odeSolverResultSetMap.get(0);

            // sanity check: shouldn't be, that only works for non-spatial stochastic where things are done differently

            averagesResultSet = RowColumnResultSet.deepCopy(tempODESolverResultSet, RowColumnResultSet.DuplicateMode.ZeroInitialize);
            stdResultSet = RowColumnResultSet.deepCopy(tempODESolverResultSet, RowColumnResultSet.DuplicateMode.ZeroInitialize);
            minResultSet = RowColumnResultSet.deepCopy(tempODESolverResultSet, RowColumnResultSet.DuplicateMode.CopyValues);
            maxResultSet = RowColumnResultSet.deepCopy(tempODESolverResultSet, RowColumnResultSet.DuplicateMode.CopyValues);

            calculateLangevinPrimaryStatistics();   // averages, standard deviation, min, max
            calculateLangevinAdvancedStatistics();

        } catch(DataAccessException dae) {
            pllOut.setFailed(true);
            pllOut.setMultiTrial(isMultiTrial);
            return pllOut;
        }
        pllOut.setFailed(failure);
        pllOut.setMultiTrial(isMultiTrial);
        pllOut.setAveragesResultSet(averagesResultSet);
        pllOut.setStdResultSet(stdResultSet);
        pllOut.setMinResultSet(minResultSet);
        pllOut.setMaxResultSet((maxResultSet));
        return pllOut;
    }

    private void calculateLangevinPrimaryStatistics() throws DataAccessException {

        int numTrials = odeSolverResultSetMap.size();
        for(int trialIndex = 0; trialIndex < numTrials; trialIndex++) {
            ODESolverResultSet sourceOsrs = odeSolverResultSetMap.get(trialIndex);
            int rowCount = sourceOsrs.getRowCount();
            for (int row = 0; row < rowCount; row++) {
                double[] sourceRowData = sourceOsrs.getRow(row);
                double[] averageRowData = averagesResultSet.getRow(row);    // destination average
                double[] minRowData = minResultSet.getRow(row);             // destination min
                double[] maxRowData = maxResultSet.getRow(row);             // destination max

                for (int i = 0; i < averageRowData.length; i++) {
                    ColumnDescription cd = averagesResultSet.getColumnDescriptions(i);
                    String name = cd.getName();
                    if (name.equals("t")) {
                        continue;
                    }
                    averageRowData[i] += sourceRowData[i] / numTrials;
                    if (minRowData[i] > sourceRowData[i]) {
                        minRowData[i] = sourceRowData[i];
                    }
                    if (maxRowData[i] < sourceRowData[i]) {
                        maxRowData[i] = sourceRowData[i];
                    }
                }
            }
        }

        for(int trialIndex = 0; trialIndex < numTrials; trialIndex++) {
            ODESolverResultSet sourceOsrs = odeSolverResultSetMap.get(trialIndex);
            int rowCount = sourceOsrs.getRowCount();
            for (int row = 0; row < rowCount; row++) {
                double[] sourceRowData = sourceOsrs.getRow(row);
                double[] averageRowData = averagesResultSet.getRow(row);
                double[] stdRowData = stdResultSet.getRow(row);    // destination std

                for (int i = 0; i < averageRowData.length; i++) {
                    ColumnDescription cd = averagesResultSet.getColumnDescriptions(i);
                    String name = cd.getName();
                    if (name.equals("t")) {
                        continue;
                    }

                    double variance = Math.pow(sourceRowData[i] - averageRowData[i], 2);
                    stdRowData[i] += variance / numTrials;
                }
            }
        }

        int rowCount = stdResultSet.getRowCount();
        for (int row = 0; row < rowCount; row++) {
            double[] stdRowData = stdResultSet.getRow(row);
            for (int i = 0; i < stdRowData.length; i++) {
                ColumnDescription cd = stdResultSet.getColumnDescriptions(i);
                String name = cd.getName();
                if (name.equals("t")) {
                    continue;
                }
                double variance = stdRowData[i];
                stdRowData[i] = Math.sqrt(variance);
            }
        }
    }

    private void calculateLangevinAdvancedStatistics() {

    }


    public static void main(String[] args) {

        class MyClass extends JPanel {
            private double[] means;
            private double[] stds;
            private double[] mins;
            private double[] maxs;

            public MyClass(double[] means, double[] stds, double[] mins, double[] maxs) {
                this.means = means;
                this.stds = stds;
                this.mins = mins;
                this.maxs = maxs;
            }

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int xOffset = 50;           // margins
                int yOffset = 50;
                int width = getWidth();
                int height = getHeight();

                Graphics2D g2 = (Graphics2D) g;
                Stroke oldStroke = g2.getStroke();
                Font oldFont = g2.getFont();
                Color oldColor = g2.getColor();
                Font boldFont = oldFont.deriveFont(Font.BOLD);
                g2.setFont(boldFont);

                g2.setStroke(new BasicStroke(2.0f));
                g2.setColor(Color.darkGray);
                g2.drawLine(xOffset, height - yOffset, width - xOffset +15, height - yOffset);  // x-axis
                g2.drawLine(xOffset, yOffset - 20, xOffset, height - yOffset);                      // y-axis
                g2.setStroke(oldStroke);

                // calculate scale factors
                double scaleX = (width - 2 * xOffset) / (double) (means.length - 1);
                double scaleY = (height - 2 * yOffset) / getMaxValue();

                // arrays to hold the polygon points
                int[] xPoints = new int[mins.length + maxs.length];
                int[] yPoints = new int[mins.length + maxs.length];
                for (int i = 0; i < mins.length; i++) {             // mins array
                    xPoints[i] = xOffset + (int) (i * scaleX);
                    yPoints[i] = height - yOffset - (int) (mins[i] * scaleY);
                }
                for (int i = maxs.length - 1; i >= 0; i--) {        // maxs array
                    xPoints[mins.length + (maxs.length - 1 - i)] = xOffset + (int) (i * scaleX);
                    yPoints[mins.length + (maxs.length - 1 - i)] = height - yOffset - (int) (maxs[i] * scaleY);
                }
                Polygon polygon = new Polygon(xPoints, yPoints, xPoints.length);    // close the polygon by uniting mins[0] with maxs[0]
                g2.setColor(Color.YELLOW);
                g2.fill(polygon);               // fill the polygon with yellow
                g2.setColor(Color.BLACK);
                g2.draw(polygon);               // draw the outline of the polygon

                for (int i = 0; i < means.length; i++) {
                    int x = xOffset + (int) (i * scaleX);
                    int y = height - yOffset - (int) (means[i] * scaleY);

                    g2.setColor(Color.green.darker());
                    g2.fillOval(x - 5, y - 5, 10, 10);  // draw mean point
                    g2.setStroke(new BasicStroke(2.0f));
                    if(i>0) {                                             // unite points (draw line to previous point)
                        g2.drawLine(x, y, xOffset + (int) ((i-1) * scaleX), height - yOffset - (int) (means[i-1] * scaleY));
                    }
                    g2.setStroke(oldStroke);

                    g2.setColor(Color.red.darker());
                    g2.setStroke(new BasicStroke(3.0f));
                    int stdDevHeight = (int) (stds[i] * scaleY);
                    g2.drawLine(x, y - stdDevHeight, x, y + stdDevHeight);     // draw standard deviation bar
                    g2.setStroke(oldStroke);

//                    g2.setColor(Color.green.darker());            // labels for each
//                    g2.drawString("Mean", x + 10, y-3);
//                    g2.setColor(Color.red.darker());
//                    g2.drawString("Std Dev", x + 10, y-15);
                }
                g2.setColor(Color.green.darker());                  // labels just once
                g2.drawString("Mean", xOffset + 10, yOffset - 3);
                g2.setColor(Color.red.darker());
                g2.drawString("Std Dev", xOffset + 10, yOffset-15);

                g2.setStroke(oldStroke);
                g2.setFont(oldFont);
                g2.setColor(oldColor);
            }

            private double getMaxValue() {          // needed for scaling properly - based on the largest number
                double maxVal = Double.MIN_VALUE;
                for (double val : maxs) {           // we only need to look into maxs, no mins or means may be higher
                    if (val > maxVal) {
                        maxVal = val;
                    }
                }
                return maxVal;
            }
        }

        double[] meansA = { 30.0, 22.5, 15.8, 12.1, 9.4, 7.1, 4.9, 3.2, 2.3 };
        double[] meansB = { 0.0, 7.5, 14.2, 17.9, 20.6, 22.9, 25.1, 26.8, 27.7 };
        double[] stds = { 0.0, 2.2, 2.1, 1.5, 1.6, 1.9, 1.4, 1.1, 0.9 };
        double[] minA = { 30.0, 18.0, 14.0, 11.0, 8.0, 4.0, 4.0, 2.0, 1.0 };
        double[] maxA = { 30.0, 24.0, 20.0, 15.0, 12.0, 10.0, 9.0, 8.0, 5.0 };

        JFrame frame = new JFrame("Mean and Standard Deviation Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new MyClass(meansA, stds, minA, maxA));
        frame.setSize(600, 400);
        frame.setVisible(true);
    }



//    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
//    private static File createDirFile(SimulationContext simulationContext){
//        String userid = simulationContext.getBioModel().getVersion().getOwner().getName();
//        String simContextDirName =
//                TokenMangler.fixTokenStrict(userid)+"-"+
//                        TokenMangler.fixTokenStrict(simulationContext.getBioModel().getName())+"-"+
//                        TokenMangler.fixTokenStrict(simulationContext.getName())+"-"+
//                        TokenMangler.fixTokenStrict(simpleDateFormat.format(simulationContext.getBioModel().getVersion().getDate()));
////		simContextDirName = TokenMangler.fixTokenStrict(simContextDirName);
//        File dirFile = new File("C:\\temp\\ruleBasedTestDir\\"+simContextDirName);
//        if(!dirFile.exists()){
//            dirFile.mkdirs();
//        }
//        return dirFile;
//    }

}
