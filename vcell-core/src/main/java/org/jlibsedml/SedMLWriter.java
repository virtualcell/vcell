package org.jlibsedml;

import java.util.List;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jlibsedml.components.*;
import org.jlibsedml.components.algorithm.Algorithm;
import org.jlibsedml.components.algorithm.AlgorithmParameter;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.listOfConstructs.*;
import org.jlibsedml.components.model.*;
import org.jlibsedml.components.output.*;
import org.jlibsedml.components.simulation.*;
import org.jlibsedml.components.task.*;
import org.jmathml.ASTToXMLElementVisitor;

class SedMLWriter {

    Element getXML(SedMLDataContainer sedmlDoc){
        SedML sedml = sedmlDoc.getSedML();
        Element root = this.getXML(sedml);
        root.setNamespace(sedml.getSedMLNamespace());
        List<Namespace> additionalNSs = sedmlDoc.getExtraNamespaces();
        for (Namespace nSs : additionalNSs) {
            root.addNamespaceDeclaration(nSs);
        }
        return root;
    }

    Element getXML(SedML sedmlObject) {
        Element sedDocElement = new Element(SedMLTags.SED_ML_ROOT);
        sedDocElement.setAttribute(SedMLTags.LEVEL_TAG, String.valueOf(sedmlObject.getLevel()));
        sedDocElement.setAttribute(SedMLTags.VERSION_TAG, String.valueOf(sedmlObject.getVersion()));

        sedDocElement.addContent(this.getXML(sedmlObject.getListOfModels()));
        sedDocElement.addContent(this.getXML(sedmlObject.getListOfSimulations()));
        sedDocElement.addContent(this.getXML(sedmlObject.getListOfTasks()));
        sedDocElement.addContent(this.getXML(sedmlObject.getListOfDataGenerators()));
        sedDocElement.addContent(this.getXML(sedmlObject.getListOfOutputs()));

        // set sedML namespace for sedMLElement (but this shouldn't trigger anything unless SedML changes)
        return this.setDefaultNamespace(sedDocElement, sedmlObject.getSedMLNamespace());
    }

    Element getXML(ListOfModels listOfModels) {
        Element listOfModelsElements = this.createElementFromBase(listOfModels); // create
        for (Model elem: listOfModels.getContents()) {
            listOfModelsElements.addContent(this.getXML(elem));
        }
        return listOfModelsElements;
    }

    Element getXML(ListOfSimulations listOfSimulations) {
        Element listOfSimulationsElements = this.createElementFromBase(listOfSimulations); // create
        for (Simulation elem: listOfSimulations.getContents()) {
            listOfSimulationsElements.addContent(this.getXML(elem));
        }
        return listOfSimulationsElements;
    }

    Element getXML(ListOfTasks listOfTasks) {
        Element listOfTasksElements = this.createElementFromBase(listOfTasks); // create
        for (AbstractTask elem: listOfTasks.getContents()) {
            listOfTasksElements.addContent(this.getXML(elem));
        }
        return listOfTasksElements;
    }

    Element getXML(ListOfDataGenerators listOfDataGenerators) {
        Element listOfDataGeneratorsElements = this.createElementFromBase(listOfDataGenerators); // create
        for (DataGenerator elem: listOfDataGenerators.getContents()) {
            listOfDataGeneratorsElements.addContent(this.getXML(elem));
        }
        return listOfDataGeneratorsElements;
    }

    Element getXML(ListOfOutputs listOfOutputs) {
        Element listOfOutputsElement = this.createElementFromBase(listOfOutputs); // create
        for (Output elem: listOfOutputs.getContents()) {
            listOfOutputsElement.addContent(this.getXML(elem));
        }
        return listOfOutputsElement;
    }

    // ================= Models
    Element getXML(Model sedmlModel) {
        Element node = this.createElementFromBase(sedmlModel);

        String language = sedmlModel.getLanguage();
        if (language != null) node.setAttribute(SedMLTags.MODEL_ATTR_LANGUAGE, language);
        String source = sedmlModel.getSourceAsString();
        if (source != null) node.setAttribute(SedMLTags.MODEL_ATTR_SOURCE, source);

        if (sedmlModel.getChanges() != null && !sedmlModel.getChanges().isEmpty()) {
            node.addContent(this.getXML(sedmlModel.getListOfChanges()));
        }

        return node;
    }

    Element getXML(ListOfChanges sedModelChanges) {
        Element list = this.createElementFromBase(sedModelChanges);
        for (Change change: sedModelChanges.getContents()) {
            list.addContent(this.getXML(change));
        }
        return list;
    }

    Element getXML(Change sedmlChange) {
        Element node = this.createElementFromBase(sedmlChange);

        node.setAttribute(SedMLTags.CHANGE_ATTR_TARGET, sedmlChange.getTargetXPath().getTargetAsString());

        if (sedmlChange instanceof ChangeAttribute changeAttribute) {
            String s = changeAttribute.getNewValue();
            if (s != null) node.setAttribute(SedMLTags.CHANGE_ATTR_NEWVALUE, s);
        } else if (sedmlChange instanceof ChangeXML changeXML) {
            Element newXML = new Element(SedMLTags.NEW_XML);
            node.addContent(newXML);
            for (Element el : changeXML.getNewXML().xml()) {
                newXML.addContent(el.detach());
            }
        } else if (sedmlChange instanceof AddXML addXML) {
            Element newXML = new Element(SedMLTags.NEW_XML);
            node.addContent(newXML);
            for (Element el : addXML.getNewXML().xml()) {
                newXML.addContent(el.detach());
            }
        } else if (sedmlChange instanceof SetValue setValue) { // SetValue
            SId rangeId = setValue.getRangeReference();
            if (rangeId != null) node.setAttribute(SedMLTags.SET_VALUE_ATTR_RANGE_REF, rangeId.string());
            SId modelRef = setValue.getModelReference();
            if (modelRef != null) node.setAttribute(SedMLTags.SET_VALUE_ATTR_MODEL_REF, modelRef.string());
            this.addCalculationContent(node, setValue);
        } else if (sedmlChange instanceof ComputeChange computeChange) {
            this.addCalculationContent(node, computeChange);
        } else if (!(sedmlChange instanceof RemoveXML)) {
            throw new IllegalArgumentException("Unknown change kind: " + sedmlChange.getChangeKind());
        }
        return node;
    }


    void addCalculationContent(Element rootElement, Calculation sedCalc){
        ListOfVariables vars = sedCalc.getListOfVariables();
        if(!vars.isEmpty()) {
            Element varList = this.createElementFromBase(vars);
            for (Variable var : vars.getContents()) {
                varList.addContent(this.getXML(var));
            }
            rootElement.addContent(varList);
        }
        ListOfParameters params = sedCalc.getListOfParameters();
        if(!params.isEmpty()) {
            Element paramList = this.createElementFromBase(params);
            for (Parameter param : params.getContents()) {
                paramList.addContent(this.getXML(param));
            }
            rootElement.addContent(paramList);
        }
        ASTToXMLElementVisitor astElementVisitor = new ASTToXMLElementVisitor();
        sedCalc.getMath().accept(astElementVisitor);
        rootElement.addContent(astElementVisitor.getElement());
    }


    Element getXML(Simulation sedmlSim) {
        final Element node = this.createElementFromBase(sedmlSim);
        // Add simulations to list of simulations
        if (sedmlSim instanceof UniformTimeCourse utcSim) {
            node.setAttribute(SedMLTags.UTCA_INIT_T, Double.toString(utcSim.getInitialTime()));
            node.setAttribute(SedMLTags.UTCA_OUT_START_T, Double.toString(utcSim.getOutputStartTime()));
            node.setAttribute(SedMLTags.UTCA_OUT_END_T, Double.toString(utcSim.getOutputEndTime()));
            node.setAttribute(SedMLTags.UTCA_POINTS_NUM, Integer.toString(utcSim.getNumberOfSteps()));
        } else if (sedmlSim instanceof OneStep oneStepSim) {
            node.setAttribute(SedMLTags.ONE_STEP_STEP, Double.toString(oneStepSim.getStep()));
        } else if (!(sedmlSim instanceof SteadyState || sedmlSim instanceof Analysis)){
            throw new RuntimeException("Simulation must be uniformTimeCourse, oneStep or steadyState or analysis'" + sedmlSim.getId().string());
        }
        Algorithm algorithm = sedmlSim.getAlgorithm();
        if (algorithm != null) node.addContent(this.getXML(algorithm));
        return node;
    }

    Element getXML(Algorithm algorithm) {
        Element node = this.createElementFromBase(algorithm);
        String kisaoID = algorithm.getKisaoID();
        if (kisaoID != null) node.setAttribute(SedMLTags.ALGORITHM_ATTR_KISAOID, kisaoID);

        // list of algorithm parameters
        ListOfAlgorithmParameters aps = algorithm.getListOfAlgorithmParameters();
        if (!aps.isEmpty()) node.addContent(this.getXML(aps));

        return node;
    }

    Element getXML(ListOfAlgorithmParameters aps) {
        Element apList = new Element(SedMLTags.ALGORITHM_PARAMETER_LIST);
        for (AlgorithmParameter ap : aps.getContents()) apList.addContent(this.getXML(ap));
        return apList;
    }

    Element getXML(AlgorithmParameter ap) {
        Element node = this.createElementFromBase(ap);
        String kisaoID = ap.getKisaoID();
        if (kisaoID != null) node.setAttribute(SedMLTags.ALGORITHM_PARAMETER_KISAOID, kisaoID);
        String value = ap.getValue();
        if (value != null) node.setAttribute(SedMLTags.ALGORITHM_PARAMETER_VALUE, value);
        return node;
    }

    private Element getXML(Range range) {
        final Element node = this.createElementFromBase(range);
        if (range instanceof VectorRange vecRange) {
            for (double val : vecRange.getElements()){
                Element v = new Element(SedMLTags.VECTOR_RANGE_VALUE_TAG);
                v.setText(Double.toString(val));
                node.addContent(v);
            }

        } else if (range instanceof UniformRange ur) {
            node.setAttribute(SedMLTags.UNIFORM_RANGE_ATTR_START, Double.toString(ur.getStart()));
            node.setAttribute(SedMLTags.UNIFORM_RANGE_ATTR_END, Double.toString(ur.getEnd()));
            node.setAttribute(SedMLTags.UNIFORM_RANGE_ATTR_NUMP, Integer.toString(ur.getNumberOfSteps()));
            node.setAttribute(SedMLTags.UNIFORM_RANGE_ATTR_TYPE, ur.getType().getText());
        } else if (range instanceof FunctionalRange fr) {
            SId rangeAttr = fr.getRange();
            if (rangeAttr == null) throw new IllegalArgumentException("range is null");
            node.setAttribute(SedMLTags.FUNCTIONAL_RANGE_INDEX, rangeAttr.string());
            this.addCalculationContent(node, fr);
        } else {
            throw new IllegalArgumentException("Unknown range type requested");
        }
        return node;
    }

    // ============== SubTasks
    private Element getXML(SubTask t) {
        Element node = this.createElementFromBase(t);
        Integer order = t.getOrder();
        if (order != null) node.setAttribute(SedMLTags.SUBTASK_ATTR_ORDER, order.toString());
        SId task = t.getTask();
        if (task == null) throw new IllegalArgumentException("task is null");
        node.setAttribute(SedMLTags.SUBTASK_ATTR_TASK, task.string());
        return node;
    }

    Element getXML(AbstractTask abstractTask) {
        final Element node = this.createElementFromBase(abstractTask);
        if (abstractTask instanceof RepeatedTask repeatedTask) {
            node.setAttribute(SedMLTags.REPEATED_TASK_RESET_MODEL, Boolean.toString(repeatedTask.getResetModel()));
            SId range = repeatedTask.getRange();
            if (range == null) throw new IllegalArgumentException("range is null");
            node.setAttribute(SedMLTags.REPEATED_TASK_ATTR_RANGE, range.string());
            // Add list of ranges
            ListOfRanges ranges = repeatedTask.getListOfRanges();
            if (!ranges.isEmpty()) node.addContent(this.getXML(ranges));

            // Add list of changes
            ListOfRepeatedTaskChanges lcs = repeatedTask.getListOfChanges();
            if (!lcs.isEmpty()) node.addContent(this.getXML(lcs));

            // Add list of subtasks
            ListOfSubTasks subTasks = repeatedTask.getListOfSubTasks();
            if (!subTasks.isEmpty()) node.addContent(this.getXML(subTasks));
        } else if (abstractTask instanceof Task task) {
            // Add Attributes to tasks
            SId modelReference = task.getModelReference();
            if (modelReference == null) throw new IllegalArgumentException("Model reference cannot be null!");
            node.setAttribute(SedMLTags.TASK_ATTR_MODELREF, modelReference.string());

            SId simulationReference = task.getSimulationReference();
            if (simulationReference == null) throw new IllegalArgumentException("Task reference cannot be null!");
            node.setAttribute(SedMLTags.TASK_ATTR_SIMREF, simulationReference.string());

        } else {
            throw new IllegalArgumentException("Unknown task type");
        }
        return node;
    }

    Element getXML(ListOfRanges ranges){
        Element rangesListElement = this.createElementFromBase(ranges);
        for (Range range : ranges.getContents()) {
            rangesListElement.addContent(this.getXML(range));
        }
        return rangesListElement;
    }

    Element getXML(ListOfRepeatedTaskChanges changes){
        Element changesListElement = this.createElementFromBase(changes);
        for (SetValue sv : changes.getContents()) {
            changesListElement.addContent(this.getXML(sv));
        }
        return changesListElement;
    }

    Element getXML(ListOfSubTasks subTasks){
        Element subTasksListElement = this.createElementFromBase(subTasks);
        for (SubTask st : subTasks.getContents()) {
            subTasksListElement.addContent(this.getXML(st));
        }
        return subTasksListElement;
    }

    private void addNotesAndAnnotation(SedBase sedbase, Element node) {

        // add 'notes' elements from sedml
    	Notes note = sedbase.getNotes();
    	if(note != null) {
            Element newElement = new Element(SedMLTags.NOTES);
            for (Element noteElement : note.getNotesElements()){
                newElement.addContent(noteElement.detach());
            }
            node.addContent(newElement);
    	}

        // add 'annotation' elements from sedml
        Annotation annotation = sedbase.getAnnotations();
        if (annotation != null) {
            Element newElement = new Element(SedMLTags.ANNOTATION);
            for (Element annElement : annotation.getAnnotationElements()) {
                newElement.addContent(annElement.detach());
            }
            node.addContent(newElement);
        }

        String metaID = sedbase.getMetaId();
        if (metaID != null) {
            node.setAttribute(SedMLTags.META_ID_ATTR_NAME, metaID);
        }
    }

    Element getXML(DataGenerator sedmlDataGen) {
        Element node = this.createElementFromBase(sedmlDataGen);
        this.addCalculationContent(node, sedmlDataGen);
        return node;
    }

    Element getXML(Variable variable) {
        Element node = this.createElementFromBase(variable);
        SId modelReference = variable.getModelReference();
        if (modelReference != null) node.setAttribute(SedMLTags.VARIABLE_MODEL, modelReference.string());
        SId taskReference = variable.getTaskReference();
        if (taskReference != null) node.setAttribute(SedMLTags.VARIABLE_TASK, taskReference.string());

        if (variable.referencesXPath()) {
            node.setAttribute(SedMLTags.VARIABLE_TARGET, variable.getTarget());
        } else if (variable.isSymbol()) {
            node.setAttribute(SedMLTags.VARIABLE_SYMBOL, variable.getSymbol().getUrn());
        }
        return node;
    }

    Element getXML(Parameter parameter) {
        Element node = this.createElementFromBase(parameter);
        node.setAttribute(SedMLTags.PARAMETER_VALUE, Double.toString(parameter.getValue()));
        return node;
    }

    Element getXML(Output sedmlOutput) {
        final Element node = this.createElementFromBase(sedmlOutput);

        if (sedmlOutput instanceof Plot plot){
            Boolean useLegend = plot.getUseLegend();
            if (useLegend != null) node.setAttribute(SedMLTags.OUTPUT_LEGEND, useLegend.toString());
            Double plotHeight = plot.getPlotHeight();
            if (plotHeight != null) node.setAttribute(SedMLTags.OUTPUT_HEIGHT, plotHeight.toString());
            Double plotWidth = plot.getPlotWidth();
            if (plotWidth != null) node.setAttribute(SedMLTags.OUTPUT_WIDTH, plotWidth.toString());

            XAxis xAxis = plot.getXAxis();
            boolean hasXAxis = xAxis != null;
            if (hasXAxis) node.addContent(this.getXML(xAxis));
            YAxis yAxis = plot.getYAxis();
            boolean hasYAxis = yAxis != null;
            if (hasYAxis) node.addContent(this.getXML(yAxis));

            if (plot instanceof Plot2D plot2D){
                RightYAxis rightYAxis = plot2D.getRightYAxis();
                if (rightYAxis != null) node.addContent(this.getXML(rightYAxis));
                ListOfCurves listOfCurves = plot2D.getListOfCurves();
                if (!listOfCurves.isEmpty()) node.addContent(this.getXML(listOfCurves, hasXAxis, hasYAxis));
            } else if (plot instanceof Plot3D plot3D){
                ZAxis zAxis = plot3D.getZAxis();
                boolean hasZAxis = zAxis != null;
                if (hasZAxis) node.addContent(this.getXML(zAxis));
                ListOfSurfaces listOfSurfaces = plot3D.getListOfSurfaces();
                if (!listOfSurfaces.isEmpty()) node.addContent(this.getXML(listOfSurfaces, hasXAxis, hasYAxis, hasZAxis));
            }
        } else if (sedmlOutput instanceof Report report){
            ListOfDataSets listOfDataSets = report.getListOfDataSets();
            if (!listOfDataSets.isEmpty())  node.addContent(this.getXML(listOfDataSets));
        }

        return node;
    }

    Element getXML(ListOfCurves listOfCurves, boolean xAxisIncluded, boolean yAxisIncluded) {
        Element node = this.createElementFromBase(listOfCurves);
        for (AbstractCurve abstractCurve : listOfCurves.getContents()) {
            if (abstractCurve instanceof Curve curve) node.addContent(this.getXML(curve, xAxisIncluded, yAxisIncluded));
            else throw new IllegalArgumentException("Unsupported curve encountered: " + abstractCurve);
        }
        return node;
    }

    Element getXML(ListOfSurfaces listOfSurfaces, boolean xAxisIncluded, boolean yAxisIncluded, boolean zAxisIncluded) {
        Element node = this.createElementFromBase(listOfSurfaces);
        for (Surface surface : listOfSurfaces.getContents()) {
            node.addContent(this.getXML(surface, xAxisIncluded, yAxisIncluded, zAxisIncluded));
        }
        return node;
    }

    Element getXML(ListOfDataSets listOfDataSets) {
        Element node = this.createElementFromBase(listOfDataSets);
        for (DataSet dataSet : listOfDataSets.getContents()) {
            node.addContent(this.getXML(dataSet));
        }
        return node;
    }

    Element getXML(Axis axis) {
        Element node = this.createElementFromBase(axis);
        node.setAttribute(SedMLTags.AXIS_TYPE, axis.getType().getTag());
        Double min = axis.getMin();
        if (min != null) node.setAttribute(SedMLTags.AXIS_MIN, min.toString());
        Double max = axis.getMax();
        if (max != null) node.setAttribute(SedMLTags.AXIS_MAX, max.toString());
        Boolean grid = axis.getGrid();
        if (grid != null) node.setAttribute(SedMLTags.AXIS_GRID, grid.toString());
        Boolean reverse = axis.getReverse();
        if (reverse != null) node.setAttribute(SedMLTags.AXIS_REVERSE, reverse.toString());
        return node;
    }

    Element getXML(Curve sedCurve, boolean xAxisIncluded, boolean yAxisIncluded) {
        Element node = this.createElementFromBase(sedCurve);

        node.setAttribute(SedMLTags.OUTPUT_TYPE, sedCurve.getType().getTag());
        Integer order = sedCurve.getOrder();
        if (order != null) node.setAttribute(SedMLTags.OUTPUT_ORDER, order.toString());

        Boolean logXAxis = sedCurve.getLogScaleXAxis();
        Boolean logYAxis = sedCurve.getLogScaleYAxis();
        if (!xAxisIncluded) node.setAttribute(SedMLTags.OUTPUT_LOG_X, Boolean.toString(logXAxis != null && logXAxis));
        if (!yAxisIncluded) node.setAttribute(SedMLTags.OUTPUT_LOG_Y, Boolean.toString(logYAxis != null && logYAxis));

        node.setAttribute(SedMLTags.OUTPUT_DATA_REFERENCE_X, sedCurve.getXDataReference().string());
        node.setAttribute(SedMLTags.OUTPUT_DATA_REFERENCE_Y, sedCurve.getYDataReference().string());

        SId xErrUpper = sedCurve.getXErrorUpper();
        SId xErrLower = sedCurve.getXErrorLower();
        SId yErrUpper = sedCurve.getYErrorUpper();
        SId yErrLower = sedCurve.getYErrorLower();
        if (xErrUpper != null) node.setAttribute(SedMLTags.OUTPUT_ERROR_X_UPPER, xErrUpper.string());
        if (xErrLower != null) node.setAttribute(SedMLTags.OUTPUT_ERROR_X_LOWER, xErrLower.string());
        if (yErrUpper != null) node.setAttribute(SedMLTags.OUTPUT_ERROR_Y_UPPER, yErrUpper.string());
        if (yErrLower != null) node.setAttribute(SedMLTags.OUTPUT_ERROR_Y_LOWER, yErrLower.string());

        return node;
    }

    Element getXML(Surface sedSurface, boolean xAxisIncluded, boolean yAxisIncluded, boolean zAxisIncluded) {
        // Surfaces
        Element node = this.createElementFromBase(sedSurface);

        node.setAttribute(SedMLTags.OUTPUT_TYPE, sedSurface.getType().getTag());
        Integer order = sedSurface.getOrder();
        if (order != null) node.setAttribute(SedMLTags.OUTPUT_ORDER, order.toString());

        Boolean logZAxis = sedSurface.getLogScaleZAxis();
        Boolean logYAxis = sedSurface.getLogScaleYAxis();
        Boolean logXAxis = sedSurface.getLogScaleXAxis();
        if (!zAxisIncluded) node.setAttribute(SedMLTags.OUTPUT_LOG_Z, Boolean.toString(logZAxis != null && logZAxis));
        if (!yAxisIncluded) node.setAttribute(SedMLTags.OUTPUT_LOG_Y, Boolean.toString(logYAxis != null && logYAxis));
        if (!xAxisIncluded) node.setAttribute(SedMLTags.OUTPUT_LOG_X, Boolean.toString(logXAxis != null && logXAxis));

        node.setAttribute(SedMLTags.OUTPUT_DATA_REFERENCE_X, sedSurface.getXDataReference().string());
        node.setAttribute(SedMLTags.OUTPUT_DATA_REFERENCE_Y, sedSurface.getYDataReference().string());
        node.setAttribute(SedMLTags.OUTPUT_DATA_REFERENCE_Z, sedSurface.getZDataReference().string());
        return node;
    }

    Element getXML(DataSet sedDataSet) { // DataSets
        Element node = this.createElementFromBase(sedDataSet);
        SId dataReference = sedDataSet.getDataReference();
        if (dataReference == null) throw new IllegalArgumentException("Null data reference");
        node.setAttribute(SedMLTags.OUTPUT_DATA_REFERENCE, dataReference.string());
        String label = sedDataSet.getLabel();
        if (label == null) throw new IllegalArgumentException("Null label");
        node.setAttribute(SedMLTags.OUTPUT_DATASET_LABEL, label);

        return node;
    }

    private Element setDefaultNamespace(Element rootNode, Namespace namespace) {
        // only if there is a node and it has no default namespace!
        if (rootNode != null && rootNode.getNamespaceURI().isEmpty()) {
            // set namespace for this node
            rootNode.setNamespace(namespace);
            for (Element child : rootNode.getChildren()) {
                // check children
                this.setDefaultNamespace(child, namespace);
            }
        }
        return rootNode;
    }

    private Element createElementFromBase(SedBase baseSedml){
        Element root = new Element(baseSedml.getElementName());
        if (baseSedml.getId() != null) root.setAttribute(SedMLTags.ATTRIBUTE_ID, baseSedml.getIdAsString());
        if (baseSedml.getName() != null) root.setAttribute(SedMLTags.ATTRIBUTE_NAME, baseSedml.getName());
        this.addNotesAndAnnotation(baseSedml, root);
        return root;
    }
}
