package org.jlibsedml.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jlibsedml.AbstractTask;
import org.jlibsedml.DataGenerator;
import org.jlibsedml.Output;
import org.jlibsedml.Parameter;
import org.jlibsedml.SedML;
import org.jlibsedml.Variable;
import org.jlibsedml.VariableSymbol;
import org.jlibsedml.execution.ExecutionStatusElement.ExecutionStatusType;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.jmathml.ASTCi;
import org.jmathml.ASTNode;
import org.jmathml.ASTNumber;
import org.jmathml.EvaluationContext;

/**
 * Processes raw simulation results according to instructions specified in the
 * {@link DataGenerator} elements specified in the output.<br/>
 * Typical usage would be:
 * 
 * <pre>
 * // First of all run a simulation
 * SedML sedml;
 * Output output; // an output from the the above SedML
 * 
 * // your class which extends AbstractSedmlExecutor
 * SedmlExecutionImpl impl = new SedmlExecutionImpl(sedml, output);
 * 
 * // we get a java.util.Map that maps a task to a time-series result using that
 * // task.
 * Map&lt;AbstractTask, IRawSedmlSimulationResults&gt; rawResults = impl
 *         .runSimulations();
 * if (!impl.isExecuted()) {
 *     // show failure &amp; exit
 * }
 * 
 * // now we can process the results:
 * SedMLResultsProcesser2 pcsr = new SedMLResultsProcesser2(sedml, output);
 * pcsr.process(rawResults);
 * if (pcsr.isProcessed()) {
 *     IProcessedSedMLSimulationResults processedRes = pcsr.getProcessedResult();
 *     // display or save results
 * } else {
 *     ProcessReport pr = processedRes.getProcessReport();
 *     // show processing errors to users..
 * }
 * </pre>
 * 
 * @author radams
 *
 */
public class SedMLResultsProcesser2 {

    static final String MISSING_DG_MESSAGE = "Could not identify a data generator for dataset - missing out this data set: ";
    static final String NO_DATACOLuMN_FORID_MSG = "Could not identify a data column for variable ";
    static final String COULD_NOT_RESOLVE_MATHML_MSG = "Could not resolve the variables in the Mathml required for generating ";
    static final String NO_DG_INOUTPUT_MSG = "No data generators listed in output";
    static final String COULD_NOT_EXECUTE_MATHML_FOR = "Math could not be executed for data generator ";
    private SedML sedml;
    private Output output;
    private IProcessedSedMLSimulationResults toReturn;
    private ProcessReport report = new ProcessReport();
    private IXPathToVariableIDResolver variable2IDResolver = new SBMLSupport();

    /**
     * Container for messages generated during processing of the raw results.
     * 
     * @author radams
     *
     */
    public class ProcessReport {
        List<ExecutionStatusElement> messages = new ArrayList<ExecutionStatusElement>();

        /**
         * Returns an unmodifiable <code>List</code> of messages. This list may
         * be empty but will not be null.
         * 
         * @return A <code>List</code>String of messages.
         */
        public List<ExecutionStatusElement> getMessages() {
            return Collections.unmodifiableList(messages);
        }
    }

    /**
     * Constructor for creating a SedMLResultsProcesser2 object. Clients should
     * ensure that the <code>output</code> argument represents an {@link Output}
     * object defined in the <code>sedml argument</code>.
     * 
     * @param sedml
     *            A non-null SED-ML object
     * @param output
     *            A non-null <code>Output</code> belonging to the
     *            <code>sedml</code> argument.
     * @throws IllegalArgumentException
     *             if any argument is <code>null</code> or if the output does
     *             not belong to the {@link SedML} object ( based on the output
     *             id ).
     */
    public SedMLResultsProcesser2(final SedML sedml, final Output output) {
        super();
        if (sedml == null || output == null) {
            throw new IllegalArgumentException();
        }
        this.sedml = sedml;
        this.output = output;
        boolean found = false;
        for (Output o : sedml.getOutputs()) {
            if (o.getId().equals(output.getId())) {
                found = true;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Output [" + output.getId()
                    + "] does not belong the SED-ML object. ");
        }
    }

    /**
     * Processes a set of simulation results and processes them according to the
     * output passed into the constructor. There are several reasons that
     * processing may fail, for example:
     * <ul>
     * <li>If cross-references between outputs, variables and data generators
     * are not valid.
     * <li>If mappings between the data column headers in the raw result, and
     * model element identifiers cannot be resolved.
     * <li>If the MathML for the data post-processing is invalid or outside the
     * scope of SED-ML's MathML usage.
     * </ul>
     * Failures such as these will be logged and can be accessed by a call to
     * getProcessReport() after calling this method.
     * 
     * @param results
     * @throws IllegalArgumentException
     *             if results is <code>null</code>.
     */
    public void process(Map<AbstractTask, IRawSedmlSimulationResults> results) {
        if (results == null) {
            throw new IllegalArgumentException();
        }
        Map<AbstractTask, double[][]> rawTask2Results = new HashMap<AbstractTask, double[][]>();
        int numRows = 0;
        numRows = makeDefensiveCopyOfData(results, rawTask2Results, numRows);

        List<double[]> processed = new ArrayList<double[]>();
        if (output.getAllDataGeneratorReferences().isEmpty()) {
            report.messages.add(new ExecutionStatusElement(null,
                    NO_DG_INOUTPUT_MSG, ExecutionStatusType.ERROR));
            return;
        }
        for (String dgId : output.getAllDataGeneratorReferences()) {
            double[] mutated = new double[numRows];
            processed.add(mutated);
            DataGenerator dg = sedml.getDataGeneratorWithId(dgId);
            if (dg == null) {
                report.messages.add(new ExecutionStatusElement(null,
                        MISSING_DG_MESSAGE + dgId, ExecutionStatusType.ERROR));
                return;
            }
            List<Variable> vars = dg.getListOfVariables();
            List<Parameter> params = dg.getListOfParameters();
            Map<String, String> Var2Model = new HashMap<String, String>();
            Map<String, IRawSedmlSimulationResults> var2Result = new HashMap<String, IRawSedmlSimulationResults>();
            Map<String, double[][]> var2Data = new HashMap<String, double[][]>();
            String timeID = "";
            // map varIds to result, based upon task reference
            for (Variable variable : vars) {
                String modelID;

                if (variable.isVariable()) {
                    // get the task from which this result variable was
                    // generated.
                    modelID = variable2IDResolver.getIdFromXPathIdentifer(variable
                            .getTarget());
                    String taskRef = variable.getReference();
                    AbstractTask t = sedml.getTaskWithId(taskRef);

                    // get results for this task
                    IRawSedmlSimulationResults res = results.get(t);
                    // set up lookups to results, raw data and model ID
                    var2Result.put(variable.getId(), res);
                    var2Data.put(variable.getId(), rawTask2Results.get(t));
                    Var2Model.put(variable.getId(), modelID);
                    // it's a symbol
                } else if (variable.isSymbol()
                        && variable.getSymbol().equals(VariableSymbol.TIME)) {
                    timeID = variable.getId();
                    var2Data.put(variable.getId(), rawTask2Results.values().iterator()
                            .next());
                    Var2Model.put(variable.getId(), variable.getId());

                }

            }
            // get Parameter values
            Map<String, Double> Param2Value = new HashMap<String, Double>();
            for (Parameter p : params) {
                Param2Value.put(p.getId(), p.getValue());
            }
            // now parse maths, and replace raw simulation results with
            // processed results.
            ASTNode node = dg.getMath();
            Set<ASTCi> identifiers = node.getIdentifiers();
            for (ASTCi var : identifiers) {
                if (var.isVector()) {
                    String varName = var.getName();
                    IModel2DataMappings coll = var2Result.get(varName)
                            .getMappings();
                    int otherVarInx = coll.getColumnIndexFor(Var2Model
                            .get(varName));
                    if (otherVarInx < 0
                            || otherVarInx >= var2Result.get(varName)
                                    .getNumColumns()) {
                        report.messages.add(new ExecutionStatusElement(null,
                                NO_DATACOLuMN_FORID_MSG + var,
                                ExecutionStatusType.ERROR));
                        return;
                    }
                    EvaluationContext con = new EvaluationContext();
                    Double[] data = var2Result.get(varName)
                            .getDataByColumnIndex(otherVarInx);

                    con.setValueFor(varName, Arrays.asList(data));

                    if (var.getParentNode() == null
                            || var.getParentNode().getParentNode() == null) {
                        report.messages
                                .add(new ExecutionStatusElement(
                                        null,
                                        "Could not evaluate ["
                                                + var
                                                + "] as symbol does not have parent element",
                                        ExecutionStatusType.ERROR));
                        return;
                    }
                    if (!var.getParentNode().canEvaluate(con)) {
                        report.messages.add(new ExecutionStatusElement(null,
                                "Could not evaluate [" + var + "] ",
                                ExecutionStatusType.ERROR));
                        return;
                    }
                    ASTNumber num = var.getParentNode().evaluate(con);
                    // replace vector operation with calculated value.
                    var.getParentNode().getParentNode()
                            .replaceChild(var.getParentNode(), num);
                }
            }
            // identifiers.add(var.getSpId());
            if (identifiersMapToData(identifiers, Var2Model, Param2Value,
                    var2Result, timeID)) {

                for (int i = 0; i < numRows; i++) {
                    EvaluationContext con = new EvaluationContext();

                    for (String id : Param2Value.keySet()) {
                        con.setValueFor(id, Param2Value.get(id));
                    }

                    for (ASTCi var : identifiers) {
                        // we've already resolved parameters
                        if (Param2Value.get(var.getName()) != null) {
                            continue;
                        }
                        int otherVarInx = 0;
                        if (!var.getName().equals(timeID)) {
                            IModel2DataMappings coll = var2Result.get(
                                    var.getName()).getMappings();
                            otherVarInx = coll.getColumnIndexFor(Var2Model
                                    .get(var.getName()));
                            if (otherVarInx < 0
                                    || otherVarInx >= var2Result.get(
                                            var.getName()).getNumColumns()) {
                                report.messages.add(new ExecutionStatusElement(
                                        null, NO_DATACOLuMN_FORID_MSG + var,
                                        ExecutionStatusType.ERROR));
                                return;
                            }
                        }
                        con.setValueFor(var.getName(),
                                var2Data.get(var.getName())[i][otherVarInx]);
                    }

                    if (node.canEvaluate(con)) {
                        mutated[i] = node.evaluate(con).getValue();
                    } else {
                        report.messages.add(new ExecutionStatusElement(null,
                                COULD_NOT_EXECUTE_MATHML_FOR + dgId,
                                ExecutionStatusType.INFO));
                    }
                }
            } else {
                report.messages.add(new ExecutionStatusElement(null,
                        COULD_NOT_RESOLVE_MATHML_MSG + dgId,
                        ExecutionStatusType.ERROR));
                return;
            }

        }
        toReturn = createData(processed, numRows);

    }

    /**
     * Optional method which can set a client implementation of an
     * {@link IXPathToVariableIDResolver} interface.
     * 
     * @param variable2idrEolver
     */
    public void setVariable2IDResolver(
            IXPathToVariableIDResolver variable2idrEolver) {
        variable2IDResolver = variable2idrEolver;
    }

    // makes copy of result data and returns num rows
    private int makeDefensiveCopyOfData(
            Map<AbstractTask, IRawSedmlSimulationResults> results,
            Map<AbstractTask, double[][]> rawTask2Results, int numRows) {
        // makes a defensive copy of all input data
        for (AbstractTask t : results.keySet()) {
            IRawSedmlSimulationResults result = results.get(t);
            numRows = result.getNumDataRows();
            double[][] toCopy = result.getData(); // for look-up of
            double[][] original = new double[toCopy.length][];

            int in = 0;
            for (double[] row : toCopy) {
                double[] copyRow = new double[row.length];
                System.arraycopy(row, 0, copyRow, 0, row.length);

                original[in++] = copyRow;
            }
            rawTask2Results.put(t, original);
        }
        return numRows;
    }

    private IProcessedSedMLSimulationResults createData(
            List<double[]> processed, int NumRows) {

        String[] hdrs = new String[processed.size()];
        int colInd = 0;
        for (Iterator<String> it = output.getAllDataGeneratorReferences()
                .iterator(); it.hasNext();) {
            hdrs[colInd++] = it.next();
        }

        double[][] data = new double[NumRows][hdrs.length];
        for (int j = 0; j < NumRows; j++) {
            for (int i = 0; i < hdrs.length; i++) {
                data[j][i] = processed.get(i)[j];
            }

        }
        return new SedmlData(data, hdrs);

    }

    private boolean identifiersMapToData(Set<ASTCi> identifiers,
            Map<String, String> Var2Model, Map<String, Double> Param2Value,
            Map<String, IRawSedmlSimulationResults> var2Result, String timeID) {

        for (ASTCi var : identifiers) {
            boolean seen = false;
            if (Param2Value.get(var.getName()) != null) {
                seen = true;
            } else if (Var2Model.get(var.getName()) != null) {
                if (var.getName().equals(timeID)) {
                    seen = true;
                } else {
                    IModel2DataMappings coll = var2Result.get(var.getName())
                            .getMappings();
                    if (coll.hasMappingFor(Var2Model.get(var.getName()))
                            && coll.getColumnTitleFor(Var2Model.get(var
                                    .getName())) != null
                            || var.getName().equals(timeID)) {
                        seen = true;
                    }
                }
            }

            if (!seen) {
                return false;
            }

        }
        return true;
    }

    /**
     * Gets a processed result, if isProcessed() == <code>true</code>. The data
     * encapsulated by this object is a copy of the data passed in. Further
     * manipulation of the data backing this object will not alter the data in
     * the original raw result.
     * 
     * @return An {@link IProcessedSedMLSimulationResults} object.
     */
    public IProcessedSedMLSimulationResults getProcessedResult() {
        return toReturn;
    }

    /**
     * Gets the processing report. This report will be empty if:
     * <ul>
     * <li>process() has not yet been called
     * <li>process() has been called and was successful.
     * </ul>
     * But this method will not return <code>null</code> at any stage.
     * 
     * @return A {@link ProcessReport}
     */
    public ProcessReport getProcessingReport() {
        return report;
    }

    /**
     * Boolean XPATH_PREFIX for whether processing was successful or not.
     * 
     * @return <code>true</code> if successful, <code>false</code> otherwise.
     */
    public boolean isProcessed() {
        return toReturn != null;
    }

}
