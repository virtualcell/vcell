package org.vcell.cli.run.results;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.*;
import org.jlibsedml.components.Variable;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.output.*;
import org.jlibsedml.components.task.AbstractTask;
import org.jlibsedml.components.task.RepeatedTask;
import org.jlibsedml.components.task.SubTask;
import org.jlibsedml.execution.IXPathToVariableIDResolver;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.vcell.sbml.vcell.lazy.LazySBMLDataAccessor;

import java.util.*;

public abstract class ResultsConverter {
    private final static Logger logger = LogManager.getLogger(ResultsConverter.class);

    protected static List<Output> getValidOutputs(SedMLDataContainer sedml){
        List<Output> nonPlot3DOutputs = new ArrayList<>();
        List<Plot3D> plot3DOutputs = new ArrayList<>();
        for (Output output : sedml.getOutputs()){
            if (output instanceof Plot3D plot3D) plot3DOutputs.add(plot3D);
            else nonPlot3DOutputs.add(output);
        }

        if (!plot3DOutputs.isEmpty()) logger.warn("VCell currently does not support creation of 3D plots, {} plot{} will be skipped.",
                plot3DOutputs.size(), plot3DOutputs.size() == 1 ? "" : "s");
        return nonPlot3DOutputs;
    }

    /**
     * We need the sedmlId to help remove prefixes, but the sedmlId itself may need to be fixed.
     *
     * If a sedmlId is being checked, just provide itself twice
     *
     * The reason for this, is having an overload with just "(String s)" as a requirment is misleading.
     */
    protected static String removeVCellPrefixes(String s, String sedmlId){
        String plotPrefix = "__plot__";
        String reservedPrefix = "__vcell_reserved_data_set_prefix__";

        String checkedId = sedmlId.startsWith(plotPrefix) ? sedmlId.replace(plotPrefix, "") : sedmlId;
        if (sedmlId.equals(s)) return checkedId;

        if (s.startsWith(plotPrefix)){
            s = s.replace(plotPrefix, "");
        }

        if (s.startsWith(reservedPrefix)){
            s = s.replace(reservedPrefix, "");
        }

        if (s.startsWith(checkedId + "_")){
            s = s.replace(checkedId + "_", "");
        }

        if (s.startsWith(checkedId)){
            s = s.replace(checkedId, "");
        }

        return s;
    }

    protected static void add2DPlotsAsReports(SedMLDataContainer sedml, Map<DataGenerator, ValueHolder<LazySBMLDataAccessor>> organizedNonSpatialResults, List<Report> listToModify){
        for (Plot2D plot2D: sedml.getOutputs().stream().filter(Plot2D.class::isInstance).map(Plot2D.class::cast).toList()){
            Set<String> addedDataGenIDs = new LinkedHashSet<>();
            Report fakeReport = new Report(plot2D.getId(), plot2D.getName());
            for (Curve curve: plot2D.getListOfCurves()){
                DataGenerator dataGenX = sedml.getDataGeneratorWithId(curve.getXDataReference());
                if (dataGenX == null || !organizedNonSpatialResults.containsKey(dataGenX))
                    throw new RuntimeException("No data for Data Generator `" + curve.getXDataReference() + "` can be found!");
                if (!addedDataGenIDs.contains(dataGenX.getId())) {
                    addedDataGenIDs.add(dataGenX.getId());
                    String fakeLabel = String.format("%s.%s::X", plot2D.getId(), curve.getId());
                    fakeReport.addDataSet(new DataSet(dataGenX.getId(), dataGenX.getName(), fakeLabel, dataGenX.getId()));
                }
                DataGenerator dataGenY = sedml.getDataGeneratorWithId(curve.getYDataReference());
                if (dataGenY == null || !organizedNonSpatialResults.containsKey(dataGenY))
                    throw new RuntimeException("No data for Data Generator `" + curve.getYDataReference() + "` can be found!");
                if (!addedDataGenIDs.contains(dataGenY.getId())) {
                    addedDataGenIDs.add(dataGenY.getId());
                    String fakeLabel = String.format("%s.%s::X", plot2D.getId(), curve.getId());
                    fakeReport.addDataSet(new DataSet(dataGenY.getId(), dataGenY.getName(), fakeLabel, dataGenY.getId()));
                }
            }
            listToModify.add(fakeReport);
        }
    }

    protected static AbstractTask getBaseTask(AbstractTask task, SedMLDataContainer sedml){
        if (task == null) throw new IllegalArgumentException("task arguement is `null`!");
        while (task instanceof RepeatedTask repeatedTask) { // We need to find the original task burried beneath.
            // We assume that we can never have a sequential repeated task at this point, we check for that in SEDMLImporter
            SubTask st = repeatedTask.getSubTasks().entrySet().iterator().next().getValue(); // single subtask
            task = sedml.getTaskWithId(st.getTask());
            if (task == null) throw new IllegalArgumentException("Bad SedML formatting; task with id " + st.getTask() +" not found");
        }
        return task;
    }

    protected static String convertToVCellSymbol(Variable var){
        // must get variable ID from SBML model
        String sbmlVarId = "";
        if (var.getSymbol() != null) { // it is a predefined symbol
            // search the sbml model to find the vcell variable name associated with the run
            switch(var.getSymbol().name()){
                case "TIME": { // TIME is t, etc
                    sbmlVarId = "t"; // this is VCell reserved symbold for time
                    break;
                }
                default:{
                    sbmlVarId = var.getSymbol().name();
                }
                // etc, TODO: check spec for other symbols (CSymbols?)
                // Delay? Avogadro? rateOf?
            }
        } else { // it is an XPATH target in model
            String target = var.getTarget();
            IXPathToVariableIDResolver resolver = new SBMLSupport();
            sbmlVarId = resolver.getIdFromXPathIdentifer(target);
        }
        return sbmlVarId;
    }

    protected static String getKind(String prefixedSedmlId){
        String plotPrefix = "__plot__";
        if (prefixedSedmlId.startsWith(plotPrefix))
            return "SedPlot2D";
        return "SedReport";
    }
}
