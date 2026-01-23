package org.jlibsedml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jlibsedml.components.*;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.simulation.OneStep;
import org.jlibsedml.components.task.UniformRange.UniformType;
import org.jlibsedml.components.algorithm.Algorithm;
import org.jlibsedml.components.algorithm.AlgorithmParameter;
import org.jlibsedml.components.model.*;
import org.jlibsedml.components.output.*;
import org.jlibsedml.components.simulation.Simulation;
import org.jlibsedml.components.simulation.SteadyState;
import org.jlibsedml.components.simulation.UniformTimeCourse;
import org.jlibsedml.components.task.*;
import org.jlibsedml.mathsymbols.SedMLSymbolFactory;
import org.jmathml.ASTNode;
import org.jmathml.ASTRootNode;
import org.jmathml.MathMLReader;
import org.jmathml.SymbolRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SedMLReader {
    static final Logger lg = LoggerFactory.getLogger(SedMLReader.class);

    public static SedMLDataContainer readFile(File file) throws JDOMException, IOException, XMLException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(file);
        Element sedRoot = doc.getRootElement();
        try {
            SedMLElementFactory.getInstance().setStrictCreation(false);
            SedMLReader reader = new SedMLReader();
            return reader.getSedDocument(sedRoot, file.getName());
        } finally {
            SedMLElementFactory.getInstance().setStrictCreation(true);
        }
    }

    /*
     * returns a SedML model given an Element of xml for a complete model non -
     * api method
     */
    public SedMLDataContainer getSedDocument(Element sedRoot) throws XMLException{
        return this.getSedDocument(sedRoot, null);
    }

    /*
     * returns a SedML model given an Element of xml for a complete model non -
     * api method
     */
    public SedMLDataContainer getSedDocument(Element sedRoot, String filename) throws XMLException {
        Namespace sedNS;
        SedMLDataContainer sedDoc;
        SedML sedML;
        SymbolRegistry.getInstance().addSymbolFactory(new SedMLSymbolFactory());
        try {
            sedNS = sedRoot.getNamespace();
            Attribute idAttr = sedRoot.getAttribute(SedMLTags.ATTRIBUTE_ID);
            SId idStr = SedMLReader.parseId(idAttr == null ? null : idAttr.getValue(), filename);
            String nameStr = sedRoot.getAttributeValue(SedMLTags.ATTRIBUTE_NAME);
            String lvlStr = sedRoot.getAttributeValue(SedMLTags.LEVEL_TAG);
            String verStr = sedRoot.getAttributeValue(SedMLTags.VERSION_TAG);
            int level = Integer.parseInt(lvlStr);
            int version = Integer.parseInt(verStr);
            sedML = new SedML(idStr, nameStr, level, version);
            if (!sedML.getSedMLNamespaceURI().equals(sedNS.getURI()))
                throw new XMLException(String.format("SedML namespace mismatch;'%s' != '%s' (expected)", sedNS.getURI(), sedML.getSedMLNamespaceURI()));
        } catch (Exception e) {
            throw new XMLException("Error loading start of SedML document : ", e);
        }
        sedDoc = new SedMLDataContainer(sedML);

        try {
            // Get additional namespaces if mentioned : mathml, sbml, etc.
            sedDoc.addAllAdditionalNamespaces(sedRoot.getAdditionalNamespaces());
            // notes and annotations
            this.addNotesAndAnnotation(sedML, sedRoot);
        } catch (Exception e) {
            throw new XMLException("Error loading basics of SedML document : ", e);
        }

        try {
            // models
            Element modelElementsParent;
            if (null != (modelElementsParent = sedRoot.getChild(SedMLTags.MODELS, sedNS))) {
                for (Element modelElement : modelElementsParent.getChildren()) {
                    if (!SedMLTags.MODEL_TAG.equals(modelElement.getName())) continue;
                    sedML.addModel(this.getModel(modelElement));
                }
            }
        } catch (Exception e) {
            throw new XMLException("Error loading models of SedML document : ", e);
        }

        try {
            // simulations
            Element simulationElementsParent;
            if (null != (simulationElementsParent = sedRoot.getChild(SedMLTags.SIMS, sedNS))) {
                for (Element simElement : simulationElementsParent.getChildren()){
                    sedML.addSimulation(this.getSimulation(simElement));
                }
            }
        } catch (Exception e) {
            throw new XMLException("Error loading simulations of SedML document : ", e);
        }

        try {
            // Tasks
            Element taskElementsParent;
            if (null != (taskElementsParent = sedRoot.getChild(SedMLTags.TASKS, sedNS))) {
                for (Element taskElement : taskElementsParent.getChildren()){
                    if (taskElement.getName().equals(SedMLTags.TASK_TAG)) {
                        sedML.addTask(this.getTask(taskElement));
                    } else if (taskElement.getName().equals(SedMLTags.REPEATED_TASK_TAG)) {
                        sedML.addTask(this.getRepeatedTask(taskElement));
                    }
                    //TODO: Add Parameter Estimation Task parsing here!
                }
            }
        } catch (Exception e) {
            throw new XMLException("Error loading tasks of SedML document : ", e);
        }

        try {
            // Data Generators
            Element dataGeneratorElementsParent;
            if (null != (dataGeneratorElementsParent = sedRoot.getChild(SedMLTags.DATA_GENERATORS, sedNS))) {
                for (Element dataGeneratorElement : dataGeneratorElementsParent.getChildren()){
                    if (!SedMLTags.DATA_GENERATOR_TAG.equals(dataGeneratorElement.getName())) continue;
                    sedML.addDataGenerator(this.getDataGenerator(dataGeneratorElement));
                }
            }
        } catch (Exception e) {
            throw new XMLException("Error loading data generators of SedML document : ", e);
        }

        try {
            // Outputs
            Element outputElementsParent;
            if (null != (outputElementsParent = sedRoot.getChild(SedMLTags.OUTPUTS, sedNS))) {
                for (Element outputElement : outputElementsParent.getChildren()){
                    switch (outputElement.getName()) {
                        case SedMLTags.OUTPUT_P2D -> sedML.addOutput(this.getPlot2D(outputElement));
                        case SedMLTags.OUTPUT_P3D -> sedML.addOutput(this.getPlot3D(outputElement));
                        case SedMLTags.OUTPUT_REPORT -> sedML.addOutput(this.getReport(outputElement));
                        default -> lg.warn("Unknown output element name: {}", outputElement.getName());
                    }
                }
            }

        } catch (Exception e) {
            throw new XMLException("Error loading outputs of sed-ml document : " + e.getMessage(), e);
        }
        return sedDoc;
    }

    Model getModel(Element xmlEncodedModel) throws DataConversionException {
        SId modelId = SedMLReader.parseId(xmlEncodedModel.getAttributeValue(SedMLTags.MODEL_ATTR_ID));
        String modelNameStr = xmlEncodedModel.getAttributeValue(SedMLTags.MODEL_ATTR_NAME);
        String languageStr = xmlEncodedModel.getAttributeValue(SedMLTags.MODEL_ATTR_LANGUAGE);
        String sourceStr = xmlEncodedModel.getAttributeValue(SedMLTags.MODEL_ATTR_SOURCE);

        Model sedmlModel = new Model(modelId, modelNameStr, languageStr, sourceStr);
        for (Element eModelChild : xmlEncodedModel.getChildren()) {
            if (!eModelChild.getName().equals(SedMLTags.CHANGES)) continue;
            for (Element aChange : eModelChild.getChildren()) {
                sedmlModel.addChange(this.getChange(aChange));
            }
        }
        this.addNotesAndAnnotation(sedmlModel, xmlEncodedModel);
        return sedmlModel;
    }

    Change getChange(Element xmlEncodedChange) throws DataConversionException {
        Change rc = null;
        switch (xmlEncodedChange.getName()) {
            case SedMLTags.CHANGE_ATTRIBUTE -> {
                XPathTarget changeTarget = new XPathTarget(xmlEncodedChange.getAttributeValue(SedMLTags.CHANGE_ATTR_TARGET));
                String valueToChangeTo = xmlEncodedChange.getAttributeValue(SedMLTags.CHANGE_ATTR_NEWVALUE);
                rc = new ChangeAttribute(changeTarget, valueToChangeTo);
            }
            case SedMLTags.CHANGE_XML, SedMLTags.ADD_XML -> {
                for (Element el : xmlEncodedChange.getChildren()) {
                    if (!el.getName().equals(SedMLTags.NEW_XML)) continue;
                    List<Element> xml = this.getNewXML(el);
                    NewXML newxml = new NewXML(xml);
                    XPathTarget newTarget = new XPathTarget(xmlEncodedChange.getAttributeValue(SedMLTags.CHANGE_ATTR_TARGET));
                    boolean isChangeXML = SedMLTags.CHANGE_XML.equals(xmlEncodedChange.getName());
                    rc = isChangeXML ? (new ChangeXML(newTarget, newxml)) : (new AddXML(newTarget, newxml));
                }
            }
            case SedMLTags.REMOVE_XML -> {
                String changeAttributeTarget = xmlEncodedChange.getAttributeValue(SedMLTags.CHANGE_ATTR_TARGET);
                rc = new RemoveXML(new XPathTarget(changeAttributeTarget));
            }
            case SedMLTags.COMPUTE_CHANGE -> {
                ASTRootNode math = null;
                List<Variable> vars = new ArrayList<>();
                List<Parameter> params = new ArrayList<>();
                for (Element el : xmlEncodedChange.getChildren()) {
                    switch (el.getName()) {
                        case SedMLTags.CHANGE_ATTR_MATH ->
                                math = (ASTRootNode) new MathMLReader().parseMathML(el);
                        case SedMLTags.DATAGEN_ATTR_VARS_LIST -> {
                            for (Element eVariable : el.getChildren()) {
                                if (!SedMLTags.DATAGEN_ATTR_VARIABLE.equals(eVariable.getName())) continue;
                                vars.add(this.createVariableWithinComputeChange(eVariable));
                            }
                        }
                        case SedMLTags.DATAGEN_ATTR_PARAMS_LIST -> {
                            for (Element eParameter : el.getChildren()) {
                                if (!SedMLTags.DATAGEN_ATTR_PARAMETER.equals(eParameter.getName())) continue;
                                params.add(this.createParameter(eParameter));
                            }
                        }
                    }
                    // TODO: variable and parameter need to be also loaded here
                }
                XPathTarget newChangeTarget = new XPathTarget(xmlEncodedChange.getAttributeValue(SedMLTags.CHANGE_ATTR_TARGET));
                ComputeChange cc = new ComputeChange(newChangeTarget, math);
                cc.setListOfParameters(params);
                cc.setListOfVariables(vars);
                rc = cc;
            }
        }
        this.addNotesAndAnnotation(rc, xmlEncodedChange);
        return rc;
    }

    // The Change within a repeated task (SetChange)
    private void addChanges(RepeatedTask t, Element element) throws DataConversionException {
        for (Element eChild : element.getChildren()) {
            if (!SedMLTags.SET_VALUE.equals(eChild.getName())) { lg.warn("Unexpected non-SetValue encountered:" + eChild); continue; }
            XPathTarget target = new XPathTarget(eChild.getAttributeValue(SedMLTags.SET_VALUE_ATTR_TARGET));
            SId rangeId = SedMLReader.parseId(eChild.getAttributeValue(SedMLTags.SET_VALUE_ATTR_RANGE_REF));
            SId modelId = SedMLReader.parseId(eChild.getAttributeValue(SedMLTags.SET_VALUE_ATTR_MODEL_REF));
            SetValue sv = new SetValue(target, rangeId, modelId);
            this.setValueContent(sv, eChild);
            t.addChange(sv);
        }
    }

    private void setValueContent(SetValue c, Element element) throws DataConversionException {
        for (Element eChild : element.getChildren()) {
            switch (eChild.getName()) {
                case SedMLTags.CHANGE_ATTR_MATH -> {
                    ASTNode math = new MathMLReader().parseMathML(eChild);
                    c.setMath(math);
                }
                case SedMLTags.DATAGEN_ATTR_VARS_LIST -> {
                    List<Element> lVariables = eChild.getChildren();
                    for (Element eVariable : lVariables) {
                        if (!SedMLTags.DATAGEN_ATTR_VARIABLE.equals(eVariable.getName())) continue;
                        c.addVariable(this.createVariableWithinSetValue(eVariable));
                    }
                }
                case SedMLTags.DATAGEN_ATTR_PARAMS_LIST -> {
                    List<Element> lParameters = eChild.getChildren();
                    for (Element eParameter : lParameters) {
                        if (!SedMLTags.DATAGEN_ATTR_PARAMETER.equals(eParameter.getName())) continue;
                        c.addParameter(this.createParameter(eParameter));
                    }
                }
                default -> lg.warn("Unexpected " + eChild);
            }
        }
    }


    private void addNotesAndAnnotation(SedBase sedbase, Element xmlElement) {
        List<Element> children = xmlElement.getChildren();

        for (Element eChild : children) {
            if (SedMLTags.NOTES.equals(eChild.getName())) sedbase.setNotes(new Notes(eChild));
            if (SedMLTags.ANNOTATION.equals(eChild.getName())) sedbase.setAnnotation(this.getAnnotation(eChild));
        }

        sedbase.setMetaId(xmlElement.getAttributeValue(SedMLTags.META_ID_ATTR_NAME));
    }

    Simulation getSimulation(Element simElement) {
        Simulation s;
        List<Element> children = simElement.getChildren();
        Algorithm requestedAlgorithm = null;
        for (Element el : children) {
            if (!SedMLTags.ALGORITHM_TAG.equals(el.getName())) continue;
            requestedAlgorithm = this.getAlgorithm(el);
            break;
        }
        String simElementName = simElement.getName();
        if (null == simElementName){
            throw new IllegalArgumentException("Sim 'name' element is null");
        }
        switch (simElementName) {
            case SedMLTags.SIM_UTC -> {
                String attributeValue = simElement.getAttributeValue(SedMLTags.UTCA_STEPS_NUM);
                if (null == attributeValue) attributeValue = simElement.getAttributeValue(SedMLTags.UTCA_POINTS_NUM); // UTCA_POINTS_NUM deprecated in version 4
                if (null == attributeValue) throw new IllegalArgumentException("Number of UTC Time points cannot be determined.");

                s = new UniformTimeCourse(
                        SedMLReader.parseId(simElement.getAttributeValue(SedMLTags.SIM_ATTR_ID)),
                        simElement.getAttributeValue(SedMLTags.SIM_ATTR_NAME),
                        Double.parseDouble(simElement.getAttributeValue(SedMLTags.UTCA_INIT_T)),
                        Double.parseDouble(simElement.getAttributeValue(SedMLTags.UTCA_OUT_START_T)),
                        Double.parseDouble(simElement.getAttributeValue(SedMLTags.UTCA_OUT_END_T)),
                        Integer.parseInt(attributeValue),
                        requestedAlgorithm
                );
            }
            case SedMLTags.SIM_ONE_STEP -> s = new OneStep(
                    SedMLReader.parseId(simElement.getAttributeValue(SedMLTags.SIM_ATTR_ID)),
                    simElement.getAttributeValue(SedMLTags.SIM_ATTR_NAME),
                    requestedAlgorithm,
                    Double.parseDouble(simElement.getAttributeValue(SedMLTags.ONE_STEP_STEP))
            );
            case SedMLTags.SIM_STEADY_STATE -> s = new SteadyState(
                    SedMLReader.parseId(simElement.getAttributeValue(SedMLTags.SIM_ATTR_ID)),
                    simElement.getAttributeValue(SedMLTags.SIM_ATTR_NAME),
                    requestedAlgorithm
            );
            case SedMLTags.SIM_ANALYSIS ->
//            s = new SteadyState(simElement.getAttributeValue(SEDMLTags.SIM_ATTR_ID),
//                    simElement.getAttributeValue(SEDMLTags.SIM_ATTR_NAME), alg);
                    throw new UnsupportedOperationException("Analysis simulations not yet supported");
            default -> throw new UnsupportedOperationException("Unknown simulation: " + simElementName);
        }
        this.addNotesAndAnnotation(s, simElement);

        return s;
    }

    Algorithm getAlgorithm(Element algorithmElement) {
        Algorithm alg = new Algorithm(algorithmElement.getAttributeValue(SedMLTags.ALGORITHM_ATTR_KISAOID));
        this.addNotesAndAnnotation(alg, algorithmElement);
        for (Element eChild : algorithmElement.getChildren()) {
            if (!SedMLTags.ALGORITHM_PARAMETER_LIST.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild);
                continue;
            }
            this.addAlgorithmParameters(alg, eChild);
        }
        return alg;
    }

    private void addAlgorithmParameters(Algorithm a, Element element) {
        for (Element eChild : element.getChildren()) {
            if (!SedMLTags.ALGORITHM_PARAMETER_TAG.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild);
                continue;
            }
            AlgorithmParameter ap = new AlgorithmParameter(
                    eChild.getAttributeValue(SedMLTags.ALGORITHM_PARAMETER_KISAOID),
                    eChild.getAttributeValue(SedMLTags.ALGORITHM_PARAMETER_VALUE)
            );
            a.addAlgorithmParameter(ap);
        }
    }

    Task getTask(Element taskElement) {
        Task t;
        t = new Task(
                SedMLReader.parseId(taskElement.getAttributeValue(SedMLTags.TASK_ATTR_ID)),
                taskElement.getAttributeValue(SedMLTags.TASK_ATTR_NAME),
                SedMLReader.parseId(taskElement.getAttributeValue(SedMLTags.TASK_ATTR_MODELREF)),
                SedMLReader.parseId(taskElement.getAttributeValue(SedMLTags.TASK_ATTR_SIMREF))
        );
        // notes and annotations
        this.addNotesAndAnnotation(t, taskElement);

        return t;
    }

    RepeatedTask getRepeatedTask(Element taskElement) throws DataConversionException, XMLException {
        RepeatedTask t;
        String resetModel = taskElement.getAttributeValue(SedMLTags.REPEATED_TASK_RESET_MODEL);
        boolean bResetModel = resetModel == null || resetModel.equals("true");
        t = new RepeatedTask(
                SedMLReader.parseId(taskElement.getAttributeValue(SedMLTags.TASK_ATTR_ID)),
                taskElement.getAttributeValue(SedMLTags.TASK_ATTR_NAME),
                bResetModel,
                SedMLReader.parseId(taskElement.getAttributeValue(SedMLTags.REPEATED_TASK_ATTR_RANGE))
        );

        this.addNotesAndAnnotation(t, taskElement);  // notes and annotations

        for (Element eChild : taskElement.getChildren()) {
            String repeatedTaskChildName = eChild.getName();
            if (SedMLTags.REPEATED_TASK_RANGES_LIST.equals(repeatedTaskChildName)) {
                this.addRanges(t, eChild);
            } else if (SedMLTags.REPEATED_TASK_CHANGES_LIST.equals(repeatedTaskChildName)) {
                this.addChanges(t, eChild);
            } else if (SedMLTags.REPEATED_TASK_SUBTASKS_LIST.equals(repeatedTaskChildName)) {
                this.addSubTasks(t, eChild);
            } else {
                lg.warn("Unexpected " + eChild);
            }
        }
        return t;
    }

    private void addSubTasks(RepeatedTask t, Element element) throws XMLException {
        for (Element eChild : element.getChildren()) {
            if (!SedMLTags.SUBTASK_TAG.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild); continue;
            }
            Attribute orderAttr = eChild.getAttribute(SedMLTags.SUBTASK_ATTR_ORDER);
            SubTask s;
            try {
                s = new SubTask(
                        orderAttr == null ? null : Integer.parseInt(orderAttr.getValue()),
                        SedMLReader.parseId(eChild.getAttributeValue(SedMLTags.SUBTASK_ATTR_TASK))
                );
            } catch (NumberFormatException e) {
                throw new XMLException("Invalid type for SubTask: order cannot be parsed into an integer");
            }
            t.addSubtask(s);
        }
    }

    private void addRanges(RepeatedTask task, Element element) throws DataConversionException {
        for (Element eChild : element.getChildren()) {
            String childName = eChild.getName();
            if (null == childName) throw new IllegalArgumentException("Child 'name' element is null");
            Range range;
            switch (childName) {
                case SedMLTags.VECTOR_RANGE_TAG -> {
                    range = new VectorRange(SedMLReader.parseId(eChild.getAttributeValue(SedMLTags.RANGE_ATTR_ID)));
                    this.addVectorRangeValues((VectorRange)range, eChild);
                }
                case SedMLTags.UNIFORM_RANGE_TAG -> {
                    String numRangePointsAsString = eChild.getAttributeValue(SedMLTags.UNIFORM_RANGE_ATTR_NUMP);
                    if (numRangePointsAsString == null) numRangePointsAsString = eChild.getAttributeValue(SedMLTags.UNIFORM_RANGE_ATTR_NUMS);
                    if (numRangePointsAsString == null) throw new IllegalArgumentException("Number of Range points cannot be determined.");

                    range = new UniformRange(
                            SedMLReader.parseId(eChild.getAttributeValue(SedMLTags.RANGE_ATTR_ID)),
                            Double.parseDouble(eChild.getAttributeValue(SedMLTags.UNIFORM_RANGE_ATTR_START)),
                            Double.parseDouble(eChild.getAttributeValue(SedMLTags.UNIFORM_RANGE_ATTR_END)),
                            Integer.parseInt(numRangePointsAsString),
                            UniformType.fromString(eChild.getAttributeValue(SedMLTags.UNIFORM_RANGE_ATTR_TYPE))
                    );

                }
                case SedMLTags.FUNCTIONAL_RANGE_TAG -> {
                    SId id = SedMLReader.parseId(eChild.getAttributeValue(SedMLTags.RANGE_ATTR_ID));
                    SId index = SedMLReader.parseId(eChild.getAttributeValue(SedMLTags.FUNCTIONAL_RANGE_INDEX));
                    range = new FunctionalRange(id, index);
                    this.addFunctionalRangeLists((FunctionalRange)range, eChild);
                }
                default -> {
                    lg.warn("Unexpected range type {}", eChild);
                    continue;
                }
            }
            task.addRange(range);
        }
    }

    private void addFunctionalRangeLists(FunctionalRange fr, Element element)
            throws DataConversionException {
        String childName;
        List<Element> children = element.getChildren();
        for (Element eChild : children) {
            if (null == (childName = eChild.getName())) throw new IllegalArgumentException("Child 'name' element is null");
            switch (childName) {
                case SedMLTags.FUNCTIONAL_RANGE_VAR_LIST -> this.addFunctionalRangeVariable(fr, eChild);
                case SedMLTags.FUNCTIONAL_RANGE_PAR_LIST -> this.addFunctionalRangeParameter(fr, eChild);
                case SedMLTags.FUNCTION_MATH_TAG -> {
                    ASTNode math = new MathMLReader().parseMathML(eChild);
                    fr.setMath(math);
                }
                default -> lg.warn("Unexpected " + eChild);
            }
        }
    }

    private void addFunctionalRangeVariable(FunctionalRange fr, Element element) {
        for (Element eChild : element.getChildren()) {
            if (!SedMLTags.DATAGEN_ATTR_VARIABLE.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild); continue;
            }
            fr.addVariable(this.createVariableWithinFunctionalRange(eChild));
        }
    }

    private void addFunctionalRangeParameter(FunctionalRange fr, Element element) throws DataConversionException {
        for (Element eChild : element.getChildren()) {
            if (!SedMLTags.DATAGEN_ATTR_PARAMETER.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild); continue;
            }
            fr.addParameter(this.createParameter(eChild));
        }
    }

    private void addVectorRangeValues(VectorRange vr, Element element) {
        for (Element eChild : element.getChildren()) {
            if (!SedMLTags.VECTOR_RANGE_VALUE_TAG.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild); continue;
            }
            vr.addValue(Double.parseDouble(eChild.getText()));
        }
    }

    DataGenerator getDataGenerator(Element dataGenElement) throws DataConversionException {
        ASTNode math = null;
        DataGenerator d = new DataGenerator(
                SedMLReader.parseId(dataGenElement.getAttributeValue(SedMLTags.DATAGEN_ATTR_ID)),
                dataGenElement.getAttributeValue(SedMLTags.DATAGEN_ATTR_NAME)
        );
        // eDataGenerator.getAttributeValue(MiaseMLTags.DGA_MATH));
        for (Element eDataGeneratorChild : dataGenElement.getChildren()) {
            switch (eDataGeneratorChild.getName()) {
                case SedMLTags.DATAGEN_ATTR_VARS_LIST -> {
                    for (Element eVariable : eDataGeneratorChild.getChildren()) {
                        if (!SedMLTags.DATAGEN_ATTR_VARIABLE.equals(eVariable.getName())) continue;
                        d.addVariable(this.createVariableWithinDataGenerator(eVariable));
                    }
                }
                case SedMLTags.DATAGEN_ATTR_PARAMS_LIST -> {
                    for (Element eParameter : eDataGeneratorChild.getChildren()) {
                        if (!SedMLTags.DATAGEN_ATTR_PARAMETER.equals(eParameter.getName())) continue;
                        d.addParameter(this.createParameter(eParameter));
                    }
                }
                case SedMLTags.DATAGEN_ATTR_MATH -> math = new MathMLReader().parseMathML(eDataGeneratorChild);
            }
        }
        d.setMath(math);
        // notes and annotations
        this.addNotesAndAnnotation(d, dataGenElement);

        return d;
    }

    Parameter createParameter(Element eParameter) throws DataConversionException {
        Parameter p = new Parameter(
                SedMLReader.parseId(eParameter.getAttributeValue(SedMLTags.PARAMETER_ID)),
                eParameter.getAttributeValue(SedMLTags.PARAMETER_NAME),
                eParameter.getAttribute(SedMLTags.PARAMETER_VALUE).getDoubleValue()
        );
        this.addNotesAndAnnotation(p, eParameter);
        return p;
    }

    Variable createVariableWithinDataGenerator(Element eVariable) {
        return this.createVariable(eVariable, false, true);
    }

    Variable createVariableWithinComputeChange(Element eVariable) {
        return this.createVariable(eVariable, true, false);
    }

    Variable createVariableWithinFunctionalRange(Element eVariable) {
        return this.createVariable(eVariable, false, false);
    }

    Variable createVariableWithinSetValue(Element eVariable) {
        return this.createVariable(eVariable, false, false);
    }


    private Variable createVariable(Element eVariable, boolean requireModelRef, boolean requireTaskRef) {
        SId varId = SedMLReader.parseId(eVariable.getAttributeValue(SedMLTags.VARIABLE_ID));
        String varName = eVariable.getAttributeValue(SedMLTags.VARIABLE_NAME);
        Attribute symbolAttr = eVariable.getAttribute(SedMLTags.VARIABLE_SYMBOL);
        boolean hasSymbol = symbolAttr != null;
        Attribute targetAttr =  eVariable.getAttribute(SedMLTags.VARIABLE_TARGET);
        SId modelRef = SedMLReader.parseId(eVariable.getAttributeValue(SedMLTags.VARIABLE_MODEL));
        if (requireModelRef && modelRef == null) throw new IllegalArgumentException("maskRef attribute is null; either the document is invalid, or this is the incorrect method to call.");
        SId taskRef = SedMLReader.parseId(eVariable.getAttributeValue(SedMLTags.VARIABLE_TASK));
        if (requireTaskRef && taskRef == null) throw new IllegalArgumentException("taskRef attribute is null; either the document is invalid, or this is the incorrect method to call.");

        Variable v; // Can't condense; these use two different signatures
        if (hasSymbol) v = new Variable(varId, varName, modelRef, taskRef, VariableSymbol.getVariableSymbolFor(symbolAttr.getValue()));
        else v = new Variable(varId, varName, modelRef, taskRef, targetAttr.getValue());

        this.addNotesAndAnnotation(v, eVariable);
        return v;
    }

    private Plot2D getPlot2D(Element ePlot2D) {
        Plot2D p2d = new Plot2D(
                SedMLReader.parseId(ePlot2D.getAttributeValue(SedMLTags.OUTPUT_ID)),
                ePlot2D.getAttributeValue(SedMLTags.OUTPUT_NAME)
        );
        Attribute showLegendAttr = ePlot2D.getAttribute(SedMLTags.OUTPUT_LEGEND);
        if (showLegendAttr != null) p2d.setUseLegend(Boolean.parseBoolean(showLegendAttr.getValue()));
        Attribute plotHeightAttr = ePlot2D.getAttribute(SedMLTags.OUTPUT_HEIGHT);
        if (plotHeightAttr != null) p2d.setPlotHeight(Double.parseDouble(plotHeightAttr.getValue()));
        Attribute plotWidthAttr = ePlot2D.getAttribute(SedMLTags.OUTPUT_WIDTH);
        if (plotWidthAttr != null) p2d.setPlotWidth(Double.parseDouble(plotWidthAttr.getValue()));

        for (Element ePlot2DChild : ePlot2D.getChildren()) {
            if (SedMLTags.OUTPUT_CURVES_LIST.equals(ePlot2DChild.getName())){
                for (Element aCurve : ePlot2DChild.getChildren()) {
                    if (!SedMLTags.OUTPUT_CURVE.equals(aCurve.getName())) continue;
                    p2d.addCurve(this.getCurve(aCurve));
                }
            } else if (SedMLTags.AXIS_X.equals(ePlot2DChild.getName())){
                p2d.setXAxis(this.getXAxis(ePlot2DChild));
            } else if (SedMLTags.AXIS_Y.equals(ePlot2DChild.getName())){
                p2d.setYAxis(this.getYAxis(ePlot2DChild));
            } else if (SedMLTags.AXIS_RIGHT_Y.equals(ePlot2DChild.getName())){
                p2d.setRightYAxis(this.getRightYAxis(ePlot2DChild));
            }
        }
        this.addNotesAndAnnotation(p2d, ePlot2D);
        return p2d;
    }

    private Plot3D getPlot3D(Element ePlot3D) {
        Plot3D p3d = new Plot3D(
                SedMLReader.parseId(ePlot3D.getAttributeValue(SedMLTags.OUTPUT_ID)),
                ePlot3D.getAttributeValue(SedMLTags.OUTPUT_NAME)
        );

        Attribute showLegendAttr = ePlot3D.getAttribute(SedMLTags.OUTPUT_LEGEND);
        if (showLegendAttr != null) p3d.setUseLegend(Boolean.parseBoolean(showLegendAttr.getValue()));
        Attribute plotHeightAttr = ePlot3D.getAttribute(SedMLTags.OUTPUT_HEIGHT);
        if (plotHeightAttr != null) p3d.setPlotHeight(Double.parseDouble(plotHeightAttr.getValue()));
        Attribute plotWidthAttr = ePlot3D.getAttribute(SedMLTags.OUTPUT_WIDTH);
        if (plotWidthAttr != null) p3d.setPlotWidth(Double.parseDouble(plotWidthAttr.getValue()));

        for (Element ePlot3DChild : ePlot3D.getChildren()) {
            if (SedMLTags.OUTPUT_SURFACES_LIST.equals(ePlot3DChild.getName())) {
                for (Element aSurface : ePlot3DChild.getChildren()) {
                    if (!SedMLTags.OUTPUT_SURFACE.equals(aSurface.getName())) continue;
                    p3d.addSurface(this.getSurface(aSurface));
                }
            } else if (SedMLTags.AXIS_X.equals(ePlot3DChild.getName())){
                p3d.setXAxis(this.getXAxis(ePlot3DChild));
            } else if (SedMLTags.AXIS_Y.equals(ePlot3DChild.getName())){
                p3d.setYAxis(this.getYAxis(ePlot3DChild));
            } else if (SedMLTags.AXIS_RIGHT_Y.equals(ePlot3DChild.getName())){
                p3d.setZAxis(this.getZAxis(ePlot3DChild));
            }

        }
        this.addNotesAndAnnotation(p3d, ePlot3D);
        return p3d;
    }

    private XAxis getXAxis(Element eXAxis) {
        XAxis xAxis = new XAxis(
                SedMLReader.parseId(eXAxis.getAttributeValue(SedMLTags.AXIS_ID)),
                eXAxis.getAttributeValue(SedMLTags.AXIS_NAME),
                Axis.Type.fromTag(eXAxis.getAttributeValue(SedMLTags.AXIS_TYPE))
        );
        this.updateGeneralAxis(xAxis, eXAxis);
        return xAxis;
    }

    private YAxis getYAxis(Element eYAxis) {
        YAxis yAxis = new YAxis(
                SedMLReader.parseId(eYAxis.getAttributeValue(SedMLTags.AXIS_ID)),
                eYAxis.getAttributeValue(SedMLTags.AXIS_NAME),
                Axis.Type.fromTag(eYAxis.getAttributeValue(SedMLTags.AXIS_TYPE))
        );
        this.updateGeneralAxis(yAxis, eYAxis);
        return yAxis;
    }

    private ZAxis getZAxis(Element eZAxis) {
        ZAxis zAxis = new ZAxis(
                SedMLReader.parseId(eZAxis.getAttributeValue(SedMLTags.AXIS_ID)),
                eZAxis.getAttributeValue(SedMLTags.AXIS_NAME),
                Axis.Type.fromTag(eZAxis.getAttributeValue(SedMLTags.AXIS_TYPE))
        );
        this.updateGeneralAxis(zAxis, eZAxis);
        return zAxis;
    }

    private RightYAxis getRightYAxis(Element eRightYAxis) {
        RightYAxis rightYAxis = new RightYAxis(
                SedMLReader.parseId(eRightYAxis.getAttributeValue(SedMLTags.AXIS_ID)),
                eRightYAxis.getAttributeValue(SedMLTags.AXIS_NAME),
                Axis.Type.fromTag(eRightYAxis.getAttributeValue(SedMLTags.AXIS_TYPE))
        );
        this.updateGeneralAxis(rightYAxis, eRightYAxis);
        return rightYAxis;
    }

    private void updateGeneralAxis(Axis axis, Element eAxis){
        Attribute minAttr = eAxis.getAttribute(SedMLTags.AXIS_MIN);
        if (minAttr != null) axis.setMin(Double.parseDouble(minAttr.getValue()));
        Attribute maxAttr = eAxis.getAttribute(SedMLTags.AXIS_MAX);
        if (maxAttr != null) axis.setMax(Double.parseDouble(maxAttr.getValue()));
        Attribute gridAttr = eAxis.getAttribute(SedMLTags.AXIS_GRID);
        if (gridAttr != null) axis.setGrid(Boolean.parseBoolean(gridAttr.getValue()));
        //Attribute styleAttr = eAxis.getAttribute(SedMLTags.AXIS_STYLE);
        // Not implemented yet...
        Attribute reverseAttr = eAxis.getAttribute(SedMLTags.AXIS_REVERSE);
        if (reverseAttr != null) axis.setReverse(Boolean.parseBoolean(reverseAttr.getValue()));
    }

    private Report getReport(Element eReport) {
        Report r = new Report(
                SedMLReader.parseId(eReport.getAttributeValue(SedMLTags.OUTPUT_ID)),
                eReport.getAttributeValue(SedMLTags.OUTPUT_NAME)
        );
        for (Element eReportDChild : eReport.getChildren()) {
            if (!SedMLTags.OUTPUT_DATASETS_LIST.equals(eReportDChild.getName())) continue;
            for (Element aDataSet : eReportDChild.getChildren()) {
                if (!SedMLTags.OUTPUT_DATASET.equals(aDataSet.getName())) continue;
                r.addDataSet(this.getDataset(aDataSet));
            }
        }

        this.addNotesAndAnnotation(r, eReport);
        return r;
    }

    DataSet getDataset(Element aDataSet) {
        DataSet ds = new DataSet(
                SedMLReader.parseId(aDataSet.getAttributeValue(SedMLTags.OUTPUT_ID)),
                aDataSet.getAttributeValue(SedMLTags.OUTPUT_NAME),
                aDataSet.getAttributeValue(SedMLTags.OUTPUT_DATASET_LABEL),
                SedMLReader.parseId(aDataSet.getAttributeValue(SedMLTags.OUTPUT_DATA_REFERENCE))
        );
        this.addNotesAndAnnotation(ds, aDataSet);
        return ds;
    }

    Curve getCurve(Element aCurve) {
        SId id = SedMLReader.parseId(aCurve.getAttributeValue(SedMLTags.OUTPUT_ID));
        String name = aCurve.getAttributeValue(SedMLTags.OUTPUT_NAME);
        SId xDataReference = SedMLReader.parseId(aCurve.getAttributeValue(SedMLTags.OUTPUT_DATA_REFERENCE_X));
        SId yDataReference = SedMLReader.parseId(aCurve.getAttributeValue(SedMLTags.OUTPUT_DATA_REFERENCE_Y));

        Attribute logXAttr = aCurve.getAttribute(SedMLTags.OUTPUT_LOG_X);
        Attribute logYAttr = aCurve.getAttribute(SedMLTags.OUTPUT_LOG_Y);
        Attribute typeAttr = aCurve.getAttribute(SedMLTags.OUTPUT_TYPE);
        Attribute orderAttr = aCurve.getAttribute(SedMLTags.OUTPUT_ORDER);
        //Attribute styleAttr = ...; //Not Yet Implemented
        Attribute yAxisAttr = aCurve.getAttribute(SedMLTags.OUTPUT_RIGHT_Y_AXIS);
        Attribute xErrUpAttr = aCurve.getAttribute(SedMLTags.OUTPUT_ERROR_X_UPPER);
        Attribute xErrLowAttr = aCurve.getAttribute(SedMLTags.OUTPUT_ERROR_X_LOWER);
        Attribute yErrUpAttr = aCurve.getAttribute(SedMLTags.OUTPUT_ERROR_Y_UPPER);
        Attribute yErrLowAttr = aCurve.getAttribute(SedMLTags.OUTPUT_ERROR_Y_LOWER);

        Boolean logScaleXAxis = null == logXAttr ? null: Boolean.valueOf(logXAttr.getValue());
        Boolean logScaleYAxis = null == logYAttr ? null: Boolean.valueOf(logYAttr.getValue());
        Curve.Type type = null == typeAttr ? Curve.Type.POINTS: Curve.Type.fromTag(typeAttr.getValue());
        Integer order = null == orderAttr ? null: Integer.valueOf(orderAttr.getValue());
        // SId style; Not Yet Implemented
        AbstractCurve.YAxisAlignment yAxis = null == yAxisAttr ? AbstractCurve.YAxisAlignment.NOT_APPLICABLE : AbstractCurve.YAxisAlignment.fromTag(yAxisAttr.getValue()) ;
        SId xErrorUpper = null == xErrUpAttr ? null: SedMLReader.parseId(xErrUpAttr.getValue());
        SId xErrorLower = null == xErrLowAttr ? null: SedMLReader.parseId(xErrLowAttr.getValue());
        SId yErrorUpper = null == yErrUpAttr ? null: SedMLReader.parseId(yErrUpAttr.getValue());
        SId yErrorLower = null == yErrLowAttr ? null: SedMLReader.parseId(yErrLowAttr.getValue());

        Curve c = new Curve(id, name, xDataReference, yDataReference, logScaleXAxis, logScaleYAxis, type, order,
                null, yAxis, xErrorUpper, xErrorLower, yErrorUpper, yErrorLower);
        this.addNotesAndAnnotation(c, aCurve);
        return c;

    }

    Surface getSurface(Element aSurface) {
        SId id = SedMLReader.parseId(aSurface.getAttributeValue(SedMLTags.OUTPUT_ID));
        String name = aSurface.getAttributeValue(SedMLTags.OUTPUT_NAME);
        SId xDataReference = SedMLReader.parseId(aSurface.getAttributeValue(SedMLTags.OUTPUT_DATA_REFERENCE_X));
        SId yDataReference = SedMLReader.parseId(aSurface.getAttributeValue(SedMLTags.OUTPUT_DATA_REFERENCE_Y));
        SId zDataReference = SedMLReader.parseId(aSurface.getAttributeValue(SedMLTags.OUTPUT_DATA_REFERENCE_Z));

        Attribute logXAttr = aSurface.getAttribute(SedMLTags.OUTPUT_LOG_X);
        Attribute logYAttr = aSurface.getAttribute(SedMLTags.OUTPUT_LOG_Y);
        Attribute logZAttr = aSurface.getAttribute(SedMLTags.OUTPUT_LOG_Z);
        Attribute typeAttr = aSurface.getAttribute(SedMLTags.OUTPUT_TYPE);
        Attribute orderAttr = aSurface.getAttribute(SedMLTags.OUTPUT_ORDER);
        //Attribute styleAttr = ...; //Not Yet Implemented

        Boolean logScaleXAxis = null == logXAttr ? null: Boolean.valueOf(logXAttr.getValue());
        Boolean logScaleYAxis = null == logYAttr ? null: Boolean.valueOf(logYAttr.getValue());
        Boolean logScaleZAxis = null == logZAttr ? null: Boolean.valueOf(logZAttr.getValue());
        Surface.Type type = null == typeAttr ? null: Surface.Type.fromTag(typeAttr.getValue());
        Integer order = null == orderAttr ? null: Integer.valueOf(orderAttr.getValue());
        // SId style; Not Yet Implemented
        Surface s = new Surface(id, name, xDataReference, yDataReference, zDataReference, logScaleXAxis, logScaleYAxis, logScaleZAxis, null, type, order);
        this.addNotesAndAnnotation(s, aSurface);
        return s;
    }

    List<Element> getNewXML(Element newXMLElement) {
        List<Element> rc = new ArrayList<>();
        for (int i = 0; i < newXMLElement.getChildren().size(); i++) {
            rc.add(newXMLElement.getChildren().get(0).detach());
        }
        return rc;
    }

    Annotation getAnnotation(Element annotationElement) {
        return new Annotation(annotationElement.getChildren().get(0).detach());
    }

    private static SId parseId(String id) {
        return null == id ? null : new SId(id);
    }

    // This one attempts to create an id if one doesn't exist, but a file name is provided!
    private static SId parseId(String id, String filename){
        if (null != id) return new SId(id);
        if (filename == null) return null;
        String alternativeFilename = filename.substring(0, filename.lastIndexOf("."));
        if (!alternativeFilename.matches("[a-zA-z0-9_]+")) return null;
        return new SId(alternativeFilename);
    }
}
