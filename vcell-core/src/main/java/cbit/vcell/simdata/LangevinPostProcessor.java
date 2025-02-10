package cbit.vcell.simdata;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.vcell.util.DataAccessException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.SimulationVersion;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class LangevinPostProcessor {

    public static final String FAILURE_KEY = "FAILURE_KEY";
	public static final String SIMULATION_KEY = "SIMULATION_KEY";
    public static final String SIMULATION_OWNER_KEY = "SIMULATION_OWNER_KEY";
    public static final String ODE_SIM_DATA_MAP_KEY = "ODE_SIM_DATA_MAP_KEY";
    public static final String LANGEVIN_MULTI_TRIAL_KEY = "LANGEVIN_MULTI_TRIAL_KEY";
    Hashtable<String, Object> hashTable;

    boolean isMultiTrial = false;   // springsalad / langevin definition of multi-trial: numTasks > 1
    boolean failure;
    Simulation sim;
    SimulationOwner simOwner;
    Map<Integer, ODEDataManager> odeDataManagerMap;

    // the results
    RowColumnResultSet averagesResultSet;
    RowColumnResultSet stdResultSet;
    RowColumnResultSet minResultSet;
    RowColumnResultSet maxResultSet;

    public void postProcessLangevinResults(Hashtable<String, Object> aHashTable) throws DataAccessException {

        this.hashTable = aHashTable;
        failure = (boolean) hashTable.get(FAILURE_KEY);
        sim = (Simulation)hashTable.get(SIMULATION_KEY);
        simOwner = (SimulationOwner)hashTable.get(SIMULATION_OWNER_KEY);

        // key = trial index, value = simulation results (ODESimData object) for that trial
        odeDataManagerMap = (Map<Integer, ODEDataManager>)hashTable.get(ODE_SIM_DATA_MAP_KEY);

        SolverTaskDescription std = sim.getSolverTaskDescription();
        int numTrials = std.getNumTrials();
        isMultiTrial = numTrials > 1 ? true : false;
        hashTable.put(LANGEVIN_MULTI_TRIAL_KEY, isMultiTrial);

        // probably useless at this point
        SimulationInfo simInfo = sim.getSimulationInfo();
        VCSimulationIdentifier vcSimulationIdentifier = simInfo.getAuthoritativeVCSimulationIdentifier();
        MathOverrides mathOverrides = sim.getMathOverrides();
        SimulationVersion simVersion = simInfo.getSimulationVersion();

        ODEDataManager tempODEDataManager = odeDataManagerMap.get(0);
//        ODEDataManager tempODEDataManager1 = odeDataManagerMap.get(1);
        ODESimData tempODESimData = (ODESimData)tempODEDataManager.getODESolverResultSet();
        String format = tempODESimData.getFormatID();
        String mathName = tempODESimData.getMathName();     // should be different instances?

        // sanity check: shouldn't be, that only works for non-spatial stochastic where things are done differently
        System.out.println("isGibsonMultiTrial: " + tempODEDataManager.getODESolverResultSet().isMultiTrialData());

        averagesResultSet = RowColumnResultSet.deepCopy(tempODEDataManager.getODESolverResultSet(), RowColumnResultSet.DuplicateMode.ZeroInitialize);
        stdResultSet = RowColumnResultSet.deepCopy(tempODEDataManager.getODESolverResultSet(), RowColumnResultSet.DuplicateMode.ZeroInitialize);
        minResultSet = RowColumnResultSet.deepCopy(tempODEDataManager.getODESolverResultSet(), RowColumnResultSet.DuplicateMode.CopyValues);
        maxResultSet = RowColumnResultSet.deepCopy(tempODEDataManager.getODESolverResultSet(), RowColumnResultSet.DuplicateMode.CopyValues);

        // we leave the min and max initialized with whatever the first trial has and adjust as we go through the other trials
        if(failure) {
            return;
        }

        calculateLangevinPrimaryStatistics();   // averages, standard deviation, min, max
        calculateLangevinAdvancedStatistics();
    }

    private void calculateLangevinPrimaryStatistics() throws DataAccessException {

        int numTrials = odeDataManagerMap.size();
        for(int trialIndex = 0; trialIndex < numTrials; trialIndex++) {
            ODEDataManager sourceOdm = odeDataManagerMap.get(trialIndex);
            ODESolverResultSet sourceOsrs = sourceOdm.getODESolverResultSet();
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
            ODEDataManager sourceOdm = odeDataManagerMap.get(trialIndex);
            ODESolverResultSet sourceOsrs = sourceOdm.getODESolverResultSet();
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
        System.out.println(" ------------------------------------");
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
                int xOffset = 50;
                int yOffset = 50;
                int width = getWidth();
                int height = getHeight();
                int xStep = (width - xOffset*2) / (means.length+1);

                Graphics2D g2 = (Graphics2D) g;
                Stroke oldStroke = g2.getStroke();
                Font oldFont = g2.getFont();
                Color oldColor = g2.getColor();

                Font boldFont = oldFont.deriveFont(Font.BOLD);
                g2.setFont(boldFont);

                float newWidth = 2.0f;
                g2.setStroke(new BasicStroke(newWidth));

                g2.drawLine(xOffset, height - yOffset, width - yOffset, height - yOffset); // x-axis
                g2.drawLine(xOffset, height - yOffset, xOffset, yOffset);

                int[] xPoints = new int[mins.length + maxs.length];
                int[] yPoints = new int[mins.length + maxs.length];

                for (int i = 0; i < mins.length; i++) {
                    xPoints[i] = 10 + xOffset + xStep*i;
                    yPoints[i] = height - 50 - (int) mins[i] * 10;
                }
                for (int i = maxs.length - 1; i >= 0; i--) {
                    xPoints[mins.length + (maxs.length - 1 - i)] = 10 + xOffset + xStep*i;
                    yPoints[mins.length + (maxs.length - 1 - i)] = height - 50 - (int) maxs[i] * 10;
                }
                g2.setColor(Color.darkGray);
                Polygon polygon = new Polygon(xPoints, yPoints, xPoints.length);
                g2.setColor(Color.yellow.darker().darker());
                g2.draw(polygon);

                for(int i=0; i<means.length; i++) {
                    g2.setColor(Color.green.darker());                  // ----- value
                    int xMean = 10 + xOffset + xStep*i;
                    int yMean = height - 50 - (int) means[i] * 10;
                    g2.fillOval(xMean - 5, yMean - 5, 10, 10);

                    g2.setColor(Color.red.darker());                    // ----- std bar
                    int stdDevHeight = (int) (stds[i] * 10);
                    g2.drawLine(xMean, yMean - stdDevHeight, xMean, yMean + stdDevHeight);

                    g2.setColor(Color.green.darker());                  // ----- labels
                    g2.drawString("Mean", xMean + 10, yMean-3);
                    g2.setColor(Color.red.darker());
                    g2.drawString("Std Dev", xMean + 10, yMean-15);
                }

                g2.setStroke(oldStroke);
                g2.setFont(oldFont);
                g2.setColor(oldColor);
            }
        }

        double[] meansA = { 30.0, 22.5, 15.8, 12.1, 9.4, 7.1, 4.9, 3.2, 2.3 };
        double[] meansB = { 0.0, 7.5, 14.2, 17.9, 20.6, 22.9, 25.1, 26.8, 27.7 };
        double[] stds = { 0.0, 2.2, 2.1, 1.5, 1.6, 1.9, 1.4, 1.1, 0.9 };
        double[] minA = { 30.0, 20.0, 17.0, 12.0, 8.0, 4.0, 4.0, 2.0, 1.0 };
        double[] maxA = { 30.0, 24.0, 20.0, 14.0, 11.0, 9.0, 9.0, 9.0, 5.0 };

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
