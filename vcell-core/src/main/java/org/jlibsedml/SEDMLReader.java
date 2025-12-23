package org.jlibsedml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jlibsedml.components.*;
import org.jlibsedml.components.dataGenerator.DataGenerator;
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

class SEDMLReader {
    static Logger lg = LoggerFactory.getLogger(SEDMLReader.class);

    /*
     * returns a SedML model given an Element of xml for a complete model non -
     * api method
     */
    public SedMLDataClass getSedDocument(Element sedRoot) throws XMLException {
        SedMLDataClass sedDoc;
        SymbolRegistry.getInstance().addSymbolFactory(new SedMLSymbolFactory());
        try {
            Namespace sedNS = sedRoot.getNamespace();
            String verStr = sedRoot.getAttributeValue(SEDMLTags.VERSION_TAG);
            String lvlStr = sedRoot.getAttributeValue(SEDMLTags.LEVEL_TAG);
            if (verStr != null && lvlStr != null) {
                int sedVersion = Integer.parseInt(verStr);
                int sedLevel = Integer.parseInt(lvlStr);
                sedDoc = new SedMLDataClass(sedLevel, sedVersion, sedNS);
            } else {
                sedDoc = new SedMLDataClass(sedNS);
            }

            // Get additional namespaces if mentioned : mathml, sbml, etc.
            sedDoc.setAdditionalNamespaces(sedRoot.getAdditionalNamespaces());

            // notes and annotations
            this.addNotesAndAnnotation(sedDoc, sedRoot);

            // models
            Element modelElementsParent;
            if (null != (modelElementsParent = sedRoot.getChild(SEDMLTags.MODELS, sedNS))) {
                for (Element modelElement : modelElementsParent.getChildren()) {
                    if (!SEDMLTags.MODEL_TAG.equals(modelElement.getName())) continue;
                    sedDoc.addModel(this.getModel(modelElement));
                }
            }

            // simulations
            Element simulationElementsParent;
            if (null != (simulationElementsParent = sedRoot.getChild(SEDMLTags.SIMS, sedNS))) {
                for (Element simElement : simulationElementsParent.getChildren()){
                    sedDoc.addSimulation(this.getSimulation(simElement));
                }
            }

            // Tasks
            Element taskElementsParent;
            if (null != (taskElementsParent = sedRoot.getChild(SEDMLTags.TASKS, sedNS))) {
                for (Element taskElement : taskElementsParent.getChildren()){
                    if (taskElement.getName().equals(SEDMLTags.TASK_TAG)) {
                        sedDoc.addTask(this.getTask(taskElement));
                    } else if (taskElement.getName().equals(SEDMLTags.REPEATED_TASK_TAG)) {
                        sedDoc.addTask(this.getRepeatedTask(taskElement));
                    }
                    //TODO: Add Parameter Estimation Task parsing here!
                }
            }

            // Data Generators
            Element dataGeneratorElementsParent;
            if (null != (dataGeneratorElementsParent = sedRoot.getChild(SEDMLTags.DATA_GENERATORS, sedNS))) {
                for (Element dataGeneratorElement : dataGeneratorElementsParent.getChildren()){
                    if (!SEDMLTags.DATA_GENERATOR_TAG.equals(dataGeneratorElement.getName())) continue;
                    sedDoc.addDataGenerator(this.getDataGenerator(dataGeneratorElement));
                }
            }

            // Outputs
            Element outputElementsParent;
            if (null != (outputElementsParent = sedRoot.getChild(SEDMLTags.OUTPUTS, sedNS))) {
                for (Element outputElement : outputElementsParent.getChildren()){
                    switch (outputElement.getName()) {
                        case SEDMLTags.OUTPUT_P2D -> sedDoc.addOutput(this.getPlot2D(outputElement));
                        case SEDMLTags.OUTPUT_P3D -> sedDoc.addOutput(this.getPlot3D(outputElement));
                        case SEDMLTags.OUTPUT_REPORT -> sedDoc.addOutput(this.getReport(outputElement));
                        default -> lg.warn("Unknown output element name: {}", outputElement.getName());
                    }
                }
            }

        } catch (Exception e) {
            throw new XMLException("Error loading sed-ml document : " + e.getMessage(), e);
        }
        return sedDoc;
    }

    public static SedMLDataClass readFile(File file) throws JDOMException, IOException, XMLException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(file);
        Element sedRoot = doc.getRootElement();
        try {
            SedMLElementFactory.getInstance().setStrictCreation(false);
            SEDMLReader reader = new SEDMLReader();
            return reader.getSedDocument(sedRoot);
        } finally {
            SedMLElementFactory.getInstance().setStrictCreation(true);
        }
    }

    Model getModel(Element xmlEncodedModel) throws DataConversionException {
        Model sedmlModel = new Model(
                xmlEncodedModel.getAttributeValue(SEDMLTags.MODEL_ATTR_ID),
                xmlEncodedModel.getAttributeValue(SEDMLTags.MODEL_ATTR_NAME),
                xmlEncodedModel.getAttributeValue(SEDMLTags.MODEL_ATTR_LANGUAGE),
                xmlEncodedModel.getAttributeValue(SEDMLTags.MODEL_ATTR_SOURCE)
        );
        List<Element> lModelChildren = xmlEncodedModel.getChildren();
        for (Element eModelChild : lModelChildren) {
            if (!eModelChild.getName().equals(SEDMLTags.CHANGES)) continue;
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
            case SEDMLTags.CHANGE_ATTRIBUTE -> {
                XPathTarget changeTarget = new XPathTarget(xmlEncodedChange.getAttributeValue(SEDMLTags.CHANGE_ATTR_TARGET));
                String valueToChangeTo = xmlEncodedChange.getAttributeValue(SEDMLTags.CHANGE_ATTR_NEWVALUE);
                rc = new ChangeAttribute(changeTarget, valueToChangeTo);
            }
            case SEDMLTags.CHANGE_XML, SEDMLTags.ADD_XML -> {
                for (Element el : xmlEncodedChange.getChildren()) {
                    if (!el.getName().equals(SEDMLTags.NEW_XML)) continue;
                    List<Element> xml = this.getNewXML(el);
                    NewXML newxml = new NewXML(xml);
                    XPathTarget newTarget = new XPathTarget(xmlEncodedChange.getAttributeValue(SEDMLTags.CHANGE_ATTR_TARGET));
                    boolean isChangeXML = SEDMLTags.CHANGE_XML.equals(xmlEncodedChange.getName());
                    rc = isChangeXML ? (new ChangeXML(newTarget, newxml)) : (new AddXML(newTarget, newxml));
                }
            }
            case SEDMLTags.REMOVE_XML -> {
                String changeAttributeTarget = xmlEncodedChange.getAttributeValue(SEDMLTags.CHANGE_ATTR_TARGET);
                rc = new RemoveXML(new XPathTarget(changeAttributeTarget));
            }
            case SEDMLTags.COMPUTE_CHANGE -> {
                ASTRootNode math = null;
                List<Variable> vars = new ArrayList<>();
                List<Parameter> params = new ArrayList<>();
                for (Element el : xmlEncodedChange.getChildren()) {
                    switch (el.getName()) {
                        case SEDMLTags.CHANGE_ATTR_MATH ->
                                math = (ASTRootNode) new MathMLReader().parseMathML(el);
                        case SEDMLTags.DATAGEN_ATTR_VARS_LIST -> {
                            for (Element eVariable : el.getChildren()) {
                                if (!SEDMLTags.DATAGEN_ATTR_VARIABLE.equals(eVariable.getName())) continue;
                                vars.add(this.createVariable(eVariable, true));
                            }
                        }
                        case SEDMLTags.DATAGEN_ATTR_PARAMS_LIST -> {
                            for (Element eParameter : el.getChildren()) {
                                if (!SEDMLTags.DATAGEN_ATTR_PARAMETER.equals(eParameter.getName())) continue;
                                params.add(this.createParameter(eParameter));
                            }
                        }
                    }
                    // TODO: variable and parameter need to be also loaded here
                }
                XPathTarget newChangeTarget = new XPathTarget(xmlEncodedChange.getAttributeValue(SEDMLTags.CHANGE_ATTR_TARGET));
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
        SetValue sv;
        for (Element eChild : element.getChildren()) {
            if (!SEDMLTags.SET_VALUE.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild); continue;
            }
            sv = new SetValue(
                    new XPathTarget(eChild.getAttributeValue(SEDMLTags.SET_VALUE_ATTR_TARGET)),
                    eChild.getAttributeValue(SEDMLTags.SET_VALUE_ATTR_RANGE_REF),
                    eChild.getAttributeValue(SEDMLTags.SET_VALUE_ATTR_MODEL_REF));
            this.setValueContent(sv, eChild);
            t.addChange(sv);
        }
    }

    private void setValueContent(SetValue c, Element element) throws DataConversionException {
        for (Element eChild : element.getChildren()) {
            switch (eChild.getName()) {
                case SEDMLTags.CHANGE_ATTR_MATH -> {
                    ASTNode math = new MathMLReader().parseMathML(eChild);
                    c.setMath(math);
                }
                case SEDMLTags.DATAGEN_ATTR_VARS_LIST -> {
                    List<Element> lVariables = eChild.getChildren();
                    for (Element eVariable : lVariables) {
                        if (!SEDMLTags.DATAGEN_ATTR_VARIABLE.equals(eVariable.getName())) continue;
                        c.addVariable(this.createVariable(eVariable, true));
                    }
                }
                case SEDMLTags.DATAGEN_ATTR_PARAMS_LIST -> {
                    List<Element> lParameters = eChild.getChildren();
                    for (Element eParameter : lParameters) {
                        if (!SEDMLTags.DATAGEN_ATTR_PARAMETER.equals(eParameter.getName())) continue;
                        c.addParameter(this.createParameter(eParameter));
                    }
                }
                default -> lg.warn("Unexpected " + eChild);
            }
        }
    }


    private void addNotesAndAnnotation(SedBase sedbase, Element xmlElement) {
        // TODO: Do notes and annotations need their independent passes?
        List<Element> children = xmlElement.getChildren();

        // Pass 1: Add all notes
        for (Element eChild : children) {
            if (!SEDMLTags.NOTES.equals(eChild.getName())) continue;
            Notes n = this.getNotes(eChild);
            if (n != null) sedbase.addNote(n);
        }

        // Pass 2: Add all annotations
        for (Element eChild : children){
            if (!SEDMLTags.ANNOTATION.equals(eChild.getName())) continue;
            sedbase.addAnnotation(this.getAnnotation(eChild));
        }

        sedbase.setMetaId(xmlElement.getAttributeValue(SEDMLTags.META_ID_ATTR_NAME));
    }

    Simulation getSimulation(Element simElement) {
        Simulation s;
        List<Element> children = simElement.getChildren();
        Algorithm requestedAlgorithm = null;
        for (Element el : children) {
            if (!SEDMLTags.ALGORITHM_TAG.equals(el.getName())) continue;
            requestedAlgorithm = this.getAlgorithm(el);
            break;
        }
        String simElementName = simElement.getName();
        if (null == simElementName){
            throw new IllegalArgumentException("Sim 'name' element is null");
        }
        switch (simElementName) {
            case SEDMLTags.SIM_UTC -> {
                String attributeValue = simElement.getAttributeValue(SEDMLTags.UTCA_STEPS_NUM);
                if (null == attributeValue) attributeValue = simElement.getAttributeValue(SEDMLTags.UTCA_POINTS_NUM); // UTCA_POINTS_NUM deprecated in version 4
                if (null == attributeValue) throw new IllegalArgumentException("Number of UTC Time points cannot be determined.");

                s = new UniformTimeCourse(
                        simElement.getAttributeValue(SEDMLTags.SIM_ATTR_ID),
                        simElement.getAttributeValue(SEDMLTags.SIM_ATTR_NAME),
                        Double.parseDouble(simElement.getAttributeValue(SEDMLTags.UTCA_INIT_T)),
                        Double.parseDouble(simElement.getAttributeValue(SEDMLTags.UTCA_OUT_START_T)),
                        Double.parseDouble(simElement.getAttributeValue(SEDMLTags.UTCA_OUT_END_T)),
                        Integer.parseInt(attributeValue),
                        requestedAlgorithm
                );
            }
            case SEDMLTags.SIM_ONE_STEP -> s = new OneStep(
                    simElement.getAttributeValue(SEDMLTags.SIM_ATTR_ID),
                    simElement.getAttributeValue(SEDMLTags.SIM_ATTR_NAME),
                    requestedAlgorithm,
                    Double.parseDouble(simElement.getAttributeValue(SEDMLTags.ONE_STEP_STEP))
            );
            case SEDMLTags.SIM_STEADY_STATE -> s = new SteadyState(
                    simElement.getAttributeValue(SEDMLTags.SIM_ATTR_ID),
                    simElement.getAttributeValue(SEDMLTags.SIM_ATTR_NAME),
                    requestedAlgorithm
            );
            case SEDMLTags.SIM_ANALYSIS ->
//            s = new SteadyState(simElement.getAttributeValue(SEDMLTags.SIM_ATTR_ID),
//                    simElement.getAttributeValue(SEDMLTags.SIM_ATTR_NAME), alg);
                    throw new UnsupportedOperationException("Analysis simulations not yet supported");
            default -> throw new UnsupportedOperationException("Unknown simulation: " + simElementName);
        }
        this.addNotesAndAnnotation(s, simElement);

        return s;
    }

    Algorithm getAlgorithm(Element algorithmElement) {
        Algorithm alg = new Algorithm(algorithmElement.getAttributeValue(SEDMLTags.ALGORITHM_ATTR_KISAOID));
        this.addNotesAndAnnotation(alg, algorithmElement);
        for (Element eChild : algorithmElement.getChildren()) {
            if (!SEDMLTags.ALGORITHM_PARAMETER_LIST.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild);
                continue;
            }
            this.addAlgorithmParameters(alg, eChild);
        }
        return alg;
    }

    private void addAlgorithmParameters(Algorithm a, Element element) {
        for (Element eChild : element.getChildren()) {
            if (!SEDMLTags.ALGORITHM_PARAMETER_TAG.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild);
                continue;
            }
            AlgorithmParameter ap = new AlgorithmParameter(
                    eChild.getAttributeValue(SEDMLTags.ALGORITHM_PARAMETER_KISAOID),
                    eChild.getAttributeValue(SEDMLTags.ALGORITHM_PARAMETER_VALUE)
            );
            a.addAlgorithmParameter(ap);
        }
    }

    Task getTask(Element taskElement) {
        Task t;
        t = new Task(
                taskElement.getAttributeValue(SEDMLTags.TASK_ATTR_ID),
                taskElement.getAttributeValue(SEDMLTags.TASK_ATTR_NAME),
                taskElement.getAttributeValue(SEDMLTags.TASK_ATTR_MODELREF),
                taskElement.getAttributeValue(SEDMLTags.TASK_ATTR_SIMREF)
        );
        // notes and annotations
        this.addNotesAndAnnotation(t, taskElement);

        return t;
    }

    RepeatedTask getRepeatedTask(Element taskElement)
            throws DataConversionException {
        RepeatedTask t;
        String resetModel = taskElement.getAttributeValue(SEDMLTags.REPEATED_TASK_RESET_MODEL);
        boolean bResetModel = resetModel == null || resetModel.equals("true");
        t = new RepeatedTask(
                taskElement.getAttributeValue(SEDMLTags.TASK_ATTR_ID),
                taskElement.getAttributeValue(SEDMLTags.TASK_ATTR_NAME),
                bResetModel,
                taskElement.getAttributeValue(SEDMLTags.REPEATED_TASK_ATTR_RANGE));

        this.addNotesAndAnnotation(t, taskElement);  // notes and annotations

        for (Element eChild : taskElement.getChildren()) {
            String repeatedTaskChildName = eChild.getName();
            if (SEDMLTags.REPEATED_TASK_RANGES_LIST.equals(repeatedTaskChildName)) {
                this.addRanges(t, eChild);
            } else if (SEDMLTags.REPEATED_TASK_CHANGES_LIST.equals(repeatedTaskChildName)) {
                this.addChanges(t, eChild);
            } else if (SEDMLTags.REPEATED_TASK_SUBTASKS_LIST.equals(repeatedTaskChildName)) {
                this.addSubTasks(t, eChild);
            } else {
                lg.warn("Unexpected " + eChild);
            }
        }
        return t;
    }

    private void addSubTasks(RepeatedTask t, Element element) {
        SubTask s = null;

        List<Element> children = element.getChildren();
        for (Element eChild : children) {
            if (!SEDMLTags.SUBTASK_TAG.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild); continue;
            }
            s = new SubTask(
                    eChild.getAttributeValue(SEDMLTags.SUBTASK_ATTR_ORDER),
                    eChild.getAttributeValue(SEDMLTags.SUBTASK_ATTR_TASK)
            );
            this.addDependTasks(s, eChild);
            t.addSubtask(s);
        }
    }

    private void addDependTasks(SubTask t, Element element) {
        List<Element> children = element.getChildren();
        for (Element eChild : children) {
            if (!SEDMLTags.DEPENDENT_TASK_SUBTASKS_LIST.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild); continue;
            }
            this.addDependTask(t, eChild);
        }
    }

    private void addDependTask(SubTask t, Element element) {
        for (Element eChild : element.getChildren()) {
            if (!SEDMLTags.DEPENDENT_TASK.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild); continue;
            }
            String taskId = eChild.getAttributeValue(SEDMLTags.SUBTASK_ATTR_TASK);
            t.addDependentTask(new SubTask(taskId));
        }
    }

    private void addRanges(RepeatedTask task, Element element) throws DataConversionException {
        for (Element eChild : element.getChildren()) {
            String childName = eChild.getName();
            if (null == childName) throw new IllegalArgumentException("Child 'name' element is null");
            Range range;
            switch (childName) {
                case SEDMLTags.VECTOR_RANGE_TAG -> {
                    String id = eChild.getAttributeValue(SEDMLTags.RANGE_ATTR_ID);
                    range = new VectorRange(id);
                    this.addVectorRangeValues((VectorRange)range, eChild);
                }
                case SEDMLTags.UNIFORM_RANGE_TAG -> {
                    String numRangePointsAsString = eChild.getAttributeValue(SEDMLTags.UNIFORM_RANGE_ATTR_NUMP);
                    if (numRangePointsAsString == null) eChild.getAttributeValue(SEDMLTags.UNIFORM_RANGE_ATTR_NUMS);
                    if (numRangePointsAsString == null) throw new IllegalArgumentException("Number of Range points cannot be determined.");

                    range = new UniformRange(
                            eChild.getAttributeValue(SEDMLTags.RANGE_ATTR_ID),
                            Double.parseDouble(eChild.getAttributeValue(SEDMLTags.UNIFORM_RANGE_ATTR_START)),
                            Double.parseDouble(eChild.getAttributeValue(SEDMLTags.UNIFORM_RANGE_ATTR_END)),
                            Integer.parseInt(numRangePointsAsString),
                            UniformType.fromString(eChild.getAttributeValue(SEDMLTags.UNIFORM_RANGE_ATTR_TYPE))
                    );

                }
                case SEDMLTags.FUNCTIONAL_RANGE_TAG -> {
                    String id = eChild.getAttributeValue(SEDMLTags.RANGE_ATTR_ID);
                    String index = eChild.getAttributeValue(SEDMLTags.FUNCTIONAL_RANGE_INDEX);
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
                case SEDMLTags.FUNCTIONAL_RANGE_VAR_LIST -> this.addFunctionalRangeVariable(fr, eChild);
                case SEDMLTags.FUNCTIONAL_RANGE_PAR_LIST -> this.addFunctionalRangeParameter(fr, eChild);
                case SEDMLTags.FUNCTION_MATH_TAG -> {
                    ASTNode math = new MathMLReader().parseMathML(eChild);
                    fr.setMath(math);
                }
                default -> lg.warn("Unexpected " + eChild);
            }
        }
    }

    private void addFunctionalRangeVariable(FunctionalRange fr, Element element) {
        for (Element eChild : element.getChildren()) {
            if (!SEDMLTags.DATAGEN_ATTR_VARIABLE.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild); continue;
            }
            fr.addVariable(this.createVariable(eChild, true));
        }
    }

    private void addFunctionalRangeParameter(FunctionalRange fr, Element element) throws DataConversionException {
        for (Element eChild : element.getChildren()) {
            if (!SEDMLTags.DATAGEN_ATTR_PARAMETER.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild); continue;
            }
            fr.addParameter(this.createParameter(eChild));
        }
    }

    private void addVectorRangeValues(VectorRange vr, Element element) {
        for (Element eChild : element.getChildren()) {
            if (!SEDMLTags.VECTOR_RANGE_VALUE_TAG.equals(eChild.getName())) {
                lg.warn("Unexpected " + eChild); continue;
            }
            vr.addValue(Double.parseDouble(eChild.getText()));
        }
    }

    DataGenerator getDataGenerator(Element dataGenElement)
            throws DataConversionException {
        ASTNode math = null;
        DataGenerator d = new DataGenerator(
                dataGenElement.getAttributeValue(SEDMLTags.DATAGEN_ATTR_ID),
                dataGenElement.getAttributeValue(SEDMLTags.DATAGEN_ATTR_NAME)
        );
        // eDataGenerator.getAttributeValue(MiaseMLTags.DGA_MATH));
        for (Element eDataGeneratorChild : dataGenElement.getChildren()) {
            switch (eDataGeneratorChild.getName()) {
                case SEDMLTags.DATAGEN_ATTR_VARS_LIST -> {
                    for (Element eVariable : eDataGeneratorChild.getChildren()) {
                        if (!SEDMLTags.DATAGEN_ATTR_VARIABLE.equals(eVariable.getName())) continue;
                        d.addVariable(this.createVariable(eVariable, false));
                    }
                }
                case SEDMLTags.DATAGEN_ATTR_PARAMS_LIST -> {
                    for (Element eParameter : eDataGeneratorChild.getChildren()) {
                        if (!SEDMLTags.DATAGEN_ATTR_PARAMETER.equals(eParameter.getName())) continue;
                        d.addParameter(this.createParameter(eParameter));
                    }
                }
                case SEDMLTags.DATAGEN_ATTR_MATH -> math = new MathMLReader().parseMathML(eDataGeneratorChild);
            }
        }
        d.setMathML(math);
        // notes and annotations
        this.addNotesAndAnnotation(d, dataGenElement);

        return d;
    }

    Parameter createParameter(Element eParameter) throws DataConversionException {
        Parameter p = new Parameter(
                eParameter.getAttributeValue(SEDMLTags.PARAMETER_ID),
                eParameter.getAttributeValue(SEDMLTags.PARAMETER_NAME),
                eParameter.getAttribute(SEDMLTags.PARAMETER_VALUE).getDoubleValue()
        );
        this.addNotesAndAnnotation(p, eParameter);
        return p;
    }

    Variable createVariable(Element eVariable, boolean isModel) {
        Variable v;
        // Can't condense; these use two different signatures
        if (null != eVariable.getAttribute(SEDMLTags.VARIABLE_SYMBOL)) {
            v = new Variable(
                    eVariable.getAttributeValue(SEDMLTags.VARIABLE_ID),
                    eVariable.getAttributeValue(SEDMLTags.VARIABLE_NAME),
                    eVariable.getAttributeValue(isModel ? SEDMLTags.VARIABLE_MODEL : SEDMLTags.VARIABLE_TASK),
                    VariableSymbol.getVariableSymbolFor(eVariable.getAttributeValue(SEDMLTags.VARIABLE_SYMBOL))
            );
        } else {
            v = new Variable(
                    eVariable.getAttributeValue(SEDMLTags.VARIABLE_ID),
                    eVariable.getAttributeValue(SEDMLTags.VARIABLE_NAME),
                    eVariable.getAttributeValue(isModel ? SEDMLTags.VARIABLE_MODEL : SEDMLTags.VARIABLE_TASK),
                    eVariable.getAttributeValue(SEDMLTags.VARIABLE_TARGET)
            );
        }

        this.addNotesAndAnnotation(v, eVariable);
        return v;
    }


    private Plot2D getPlot2D(Element ePlot2D) {
        Plot2D p2d = new Plot2D(
                ePlot2D.getAttributeValue(SEDMLTags.OUTPUT_ID),
                ePlot2D.getAttributeValue(SEDMLTags.OUTPUT_NAME)
        );
        for (Element ePlot2DChild : ePlot2D.getChildren()) {
            if (!SEDMLTags.OUTPUT_CURVES_LIST.equals(ePlot2DChild.getName())) continue;
            for (Element aCurve : ePlot2DChild.getChildren()) {
                if (!SEDMLTags.OUTPUT_CURVE.equals(aCurve.getName())) continue;
                p2d.addCurve(this.getCurve(aCurve));
            }
        }
        this.addNotesAndAnnotation(p2d, ePlot2D);
        return p2d;
    }

    private Plot3D getPlot3D(Element ePlot3D) {
        Plot3D p3d = new Plot3D(
                ePlot3D.getAttributeValue(SEDMLTags.OUTPUT_ID),
                ePlot3D.getAttributeValue(SEDMLTags.OUTPUT_NAME)
        );
        for (Element ePlot3DChild : ePlot3D.getChildren()) {
            if (!SEDMLTags.OUTPUT_SURFACES_LIST.equals(ePlot3DChild.getName())) continue;
            for (Element aSurface : ePlot3DChild.getChildren()) {
                if (!SEDMLTags.OUTPUT_SURFACE.equals(aSurface.getName())) continue;
                p3d.addSurface(this.getSurface(aSurface));
            }
        }
        this.addNotesAndAnnotation(p3d, ePlot3D);
        return p3d;
    }

    private Report getReport(Element eReport) {
        Report r = new Report(
                eReport.getAttributeValue(SEDMLTags.OUTPUT_ID),
                eReport.getAttributeValue(SEDMLTags.OUTPUT_NAME)
        );
        for (Element eReportDChild : eReport.getChildren()) {
            if (!SEDMLTags.OUTPUT_DATASETS_LIST.equals(eReportDChild.getName())) continue;
            for (Element aDataSet : eReportDChild.getChildren()) {
                if (!SEDMLTags.OUTPUT_DATASET.equals(aDataSet.getName())) continue;
                r.addDataSet(this.getDataset(aDataSet));
            }
        }

        this.addNotesAndAnnotation(r, eReport);
        return r;
    }

    DataSet getDataset(Element aDataSet) {
        DataSet ds = new DataSet(
                aDataSet.getAttributeValue(SEDMLTags.OUTPUT_ID),
                aDataSet.getAttributeValue(SEDMLTags.OUTPUT_NAME),
                aDataSet.getAttributeValue(SEDMLTags.OUTPUT_DATASET_LABEL),
                aDataSet.getAttributeValue(SEDMLTags.OUTPUT_DATA_REFERENCE)
        );
        this.addNotesAndAnnotation(ds, aDataSet);
        return ds;
    }

    Curve getCurve(Element aCurve) {
        Curve c = new Curve(
                aCurve.getAttributeValue(SEDMLTags.OUTPUT_ID),
                aCurve.getAttributeValue(SEDMLTags.OUTPUT_NAME),
                Boolean.parseBoolean(aCurve.getAttributeValue(SEDMLTags.OUTPUT_LOG_X)),
                Boolean.parseBoolean(aCurve.getAttributeValue(SEDMLTags.OUTPUT_LOG_Y)),
                aCurve.getAttributeValue(SEDMLTags.OUTPUT_DATA_REFERENCE_X),
                aCurve.getAttributeValue(SEDMLTags.OUTPUT_DATA_REFERENCE_Y)
        );
        this.addNotesAndAnnotation(c, aCurve);
        return c;

    }

    Surface getSurface(Element aSurface) {
        Surface s = new Surface(
                aSurface.getAttributeValue(SEDMLTags.OUTPUT_ID),
                aSurface.getAttributeValue(SEDMLTags.OUTPUT_NAME),
                Boolean.parseBoolean(aSurface.getAttributeValue(SEDMLTags.OUTPUT_LOG_X)),
                Boolean.parseBoolean(aSurface.getAttributeValue(SEDMLTags.OUTPUT_LOG_Y)),
                Boolean.parseBoolean(aSurface.getAttributeValue(SEDMLTags.OUTPUT_LOG_Z)),
                aSurface.getAttributeValue(SEDMLTags.OUTPUT_DATA_REFERENCE_X),
                aSurface.getAttributeValue(SEDMLTags.OUTPUT_DATA_REFERENCE_Y),
                aSurface.getAttributeValue(SEDMLTags.OUTPUT_DATA_REFERENCE_Z)
        );
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

    Notes getNotes(Element noteElement) {
        if (noteElement.getChildren().isEmpty()) return null;
        return new Notes(noteElement.getChildren().get(0).detach());
    }
}
