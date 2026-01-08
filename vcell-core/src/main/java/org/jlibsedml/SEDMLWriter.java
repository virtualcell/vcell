package org.jlibsedml;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jlibsedml.components.*;
import org.jlibsedml.components.algorithm.Algorithm;
import org.jlibsedml.components.algorithm.AlgorithmParameter;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.model.*;
import org.jlibsedml.components.output.*;
import org.jlibsedml.components.simulation.OneStep;
import org.jlibsedml.components.simulation.Simulation;
import org.jlibsedml.components.simulation.UniformTimeCourse;
import org.jlibsedml.components.task.*;
import org.jmathml.ASTToXMLElementVisitor;

class SEDMLWriter {

    enum VariableType {
        COMPUTE_CHANGE, DATA_GENERATOR
    };

    Element getXML(SedMLDataContainer sedmlObject) {
        Element sedDocElement = new Element(SedMLTags.SED_ML_ROOT);
        sedDocElement.setAttribute(SedMLTags.LEVEL_TAG,
                "" + sedmlObject.getLevel());
        sedDocElement.setAttribute(SedMLTags.VERSION_TAG,
                "" + sedmlObject.getVersion());

        // sedDocElement.setNamespace(sedmlDoc.getNamespace());
        List additionalNSs = sedmlObject.getAdditionalNamespaces();
        for (int i = 0; i < additionalNSs.size(); i++) {
            sedDocElement.addNamespaceDeclaration((Namespace) additionalNSs
                    .get(i));
        }

        List tempArrayList;

        addNotesAndAnnotation(sedmlObject, sedDocElement);

        // add 'simulation' elements from sedDocument
        tempArrayList = sedmlObject.getSimulations();
        Element listOfSimsElement = new Element(SedMLTags.SIMS); // create list
                                                                 // of
                                                                 // simulations
                                                                 // element
        for (int i = 0; i < tempArrayList.size(); i++) {
            listOfSimsElement.addContent(getXML((Simulation) tempArrayList
                    .get(i)));
        }
        sedDocElement.addContent(listOfSimsElement);

        // add 'model' elements from sedDocument
        tempArrayList = sedmlObject.getModels();
        Element listOfModelsElement = new Element(SedMLTags.MODELS); // create
                                                                     // list of
                                                                     // models
                                                                     // element
        for (int i = 0; i < tempArrayList.size(); i++) {
            listOfModelsElement
                    .addContent(getXML((Model) tempArrayList.get(i)));
        }
        sedDocElement.addContent(listOfModelsElement);

        // add 'tasks' elements from sedDocument
        tempArrayList = sedmlObject.getTasks();
        Element listOfTasksElement = new Element(SedMLTags.TASKS); // create
                                                                   // list of
                                                                   // tasks
                                                                   // element
        for (int i = 0; i < tempArrayList.size(); i++) {
            listOfTasksElement.addContent(getXML((AbstractTask) tempArrayList
                    .get(i)));
        }
        sedDocElement.addContent(listOfTasksElement);

        // add 'dataGenerators' elements from sedDocument
        tempArrayList = sedmlObject.getDataGenerators();
        Element listOfDataGeneratorElement = new Element(
                SedMLTags.DATA_GENERATORS); // create list of data generator
                                           // element
        for (int i = 0; i < tempArrayList.size(); i++) {
            listOfDataGeneratorElement
                    .addContent(getXML((DataGenerator) tempArrayList.get(i)));
        }
        sedDocElement.addContent(listOfDataGeneratorElement);

        // add 'outputs' elements from sedDocument
        tempArrayList = sedmlObject.getOutputs();
        Element listOfOutputsElement = new Element(SedMLTags.OUTPUTS); // create
                                                                       // list
                                                                       // of
                                                                       // outputs
                                                                       // element
        for (int i = 0; i < tempArrayList.size(); i++) {
            listOfOutputsElement.addContent(getXML((Output) tempArrayList
                    .get(i)));
        }
        sedDocElement.addContent(listOfOutputsElement);

        // set sedML namespace for sedMLElement
        sedDocElement = setDefaultNamespace(sedDocElement,
                sedmlObject.getNamespace());

        return sedDocElement;
    }

    // ================= Models
    Element getXML(Model sedmlModel) {
        Element node = new Element(SedMLTags.MODEL_TAG);
        addNotesAndAnnotation(sedmlModel, node);
        String s = null;
        // Add Attributes to <model>
        s = sedmlModel.getId();
        if (s != null)
            node.setAttribute(SedMLTags.MODEL_ATTR_ID, sedmlModel.getId()); // insert
                                                                            // 'id'
                                                                            // attribute
        s = sedmlModel.getName();
        if (s != null)
            node.setAttribute(SedMLTags.MODEL_ATTR_NAME, s); // insert 'name'
                                                             // attribute
        s = sedmlModel.getLanguage();
        if (s != null)
            node.setAttribute(SedMLTags.MODEL_ATTR_LANGUAGE, s); // insert
                                                                 // 'type'
                                                                 // attribute
        s = sedmlModel.getSourcePathOrURIString();
        if (s != null)
            node.setAttribute(SedMLTags.MODEL_ATTR_SOURCE, s); // insert
                                                               // 'source'
                                                               // attribute

        if (sedmlModel.getListOfChanges() != null
                && sedmlModel.getListOfChanges().size() > 0) {
            node.addContent(getXML(sedmlModel.getListOfChanges()));
        }

        return node;
    }

    org.jdom2.Element getXML(List<Change> sedModelChanges) {
        Element list = new Element(SedMLTags.CHANGES);
        for (int i = 0; i < sedModelChanges.size(); i++) {
            list.addContent(getXML((Change) sedModelChanges.get(i)));
        }
        return list;
    }

    org.jdom2.Element getXML(Change sedmlChange) {
        Element node = null;
        String s = null;
        // Add Changes to list of changes

        if (sedmlChange.getChangeKind().equals(SedMLTags.CHANGE_ATTRIBUTE_KIND)) {
            node = new Element(SedMLTags.CHANGE_ATTRIBUTE);// various attributes
                                                           // depending on kind
            addNotesAndAnnotation(sedmlChange, node);
            s = ((ChangeAttribute) sedmlChange).getNewValue();
            if (s != null)
                node.setAttribute(SedMLTags.CHANGE_ATTR_NEWVALUE, s);

        } else if (sedmlChange.getChangeKind()
                .equals(SedMLTags.CHANGE_XML_KIND)) {
            node = new Element(SedMLTags.CHANGE_XML);
            addNotesAndAnnotation(sedmlChange, node);
            Element newxml = new Element(SedMLTags.NEW_XML);
            node.addContent(newxml);
            for (Element el : ((ChangeXML) sedmlChange).getNewXML().xml()) {
                newxml.addContent(el.detach());
            }

        } else if (sedmlChange.getChangeKind().equals(SedMLTags.ADD_XML_KIND)) {
            node = new Element(SedMLTags.ADD_XML);
            addNotesAndAnnotation(sedmlChange, node);
            Element newxml = new Element(SedMLTags.NEW_XML);
            node.addContent(newxml);
            for (Element el : ((AddXML) sedmlChange).getNewXML().xml()) {
                newxml.addContent(el.detach());
            }

        } else if (sedmlChange.getChangeKind()
                .equals(SedMLTags.REMOVE_XML_KIND)) {
            node = new Element(SedMLTags.REMOVE_XML);
            addNotesAndAnnotation(sedmlChange, node);

        } else if (sedmlChange.getChangeKind().equals(SedMLTags.SET_VALUE_KIND)) { // SetValue
            node = new Element(SedMLTags.SET_VALUE);
            SetValue c = (SetValue) sedmlChange;
            addNotesAndAnnotation(c, node);
            s = c.getRangeReference();
            if (s != null)
                node.setAttribute(SedMLTags.SET_VALUE_ATTR_RANGE_REF, s);
            s = c.getModelReference();
            if (s != null)
                node.setAttribute(SedMLTags.SET_VALUE_ATTR_MODEL_REF, s);
            List<Variable> vars = c.getListOfVariables();
            if(vars.size() > 0) {
                Element varList = new Element(SedMLTags.COMPUTE_CHANGE_VARS);
                node.addContent(varList);
            	for (Variable var : vars) {
            		varList.addContent(getXML(var, VariableType.COMPUTE_CHANGE));
            	}
            }
            List<Parameter> params = c.getListOfParameters();
            if(params.size() > 0) {
                Element paramList = new Element(SedMLTags.COMPUTE_CHANGE_PARAMS);
                node.addContent(paramList);
            	for (Parameter param : params) {
            		paramList.addContent(getXML(param));
            	}
            }
            ASTToXMLElementVisitor astElementVisitor = new ASTToXMLElementVisitor();
            c.getMath().accept(astElementVisitor);
            node.addContent(astElementVisitor.getElement());

        } else if (sedmlChange.getChangeKind().equals(
                SedMLTags.COMPUTE_CHANGE_KIND)) { // ComputeChange
            node = new Element(SedMLTags.COMPUTE_CHANGE);
            addNotesAndAnnotation(sedmlChange, node);
            ComputeChange computeChange = (ComputeChange) sedmlChange;
            if(!computeChange.getListOfVariables().isEmpty()) {
	            Element varList = new Element(SedMLTags.COMPUTE_CHANGE_VARS);
	            node.addContent(varList);
	            List<Variable> vars = computeChange.getListOfVariables();
	            for (Variable var : vars) {
	                varList.addContent(getXML(var, VariableType.COMPUTE_CHANGE));
	            }
            }
            if(!computeChange.getListOfParameters().isEmpty()) {
	            Element paramList = new Element(SedMLTags.COMPUTE_CHANGE_PARAMS);
	            node.addContent(paramList);
	            List<Parameter> params = computeChange.getListOfParameters();
	            for (Parameter param : params) {
	                paramList.addContent(getXML(param));
	            }
            }
            ASTToXMLElementVisitor astElementVisitor = new ASTToXMLElementVisitor();
            computeChange.getMath().accept(astElementVisitor);
            node.addContent(astElementVisitor.getElement());

        }
        node.setAttribute(SedMLTags.CHANGE_ATTR_TARGET, sedmlChange
                .getTargetXPath().getTargetAsString()); // insert 'target'
                                                        // attribute
        return node;
    }

    // ============= SetValue

    // =============== Simulations
    org.jdom2.Element getXML(Simulation sedmlSim) {
        Element node = null;
        String s = null;
        // Add simulations to list of simulations
        if (sedmlSim.getSimulationKind().equals(SedMLTags.SIMUL_UTC_KIND)) { // various
                                                                             // attributes
                                                                             // depending
                                                                             // on
                                                                             // kind
            node = new Element(SedMLTags.SIM_UTC);
            addNotesAndAnnotation(sedmlSim, node);
            s = sedmlSim.getId();
            if (s != null)
                node.setAttribute(SedMLTags.SIM_ATTR_ID, s);
            s = sedmlSim.getName();
            if (s != null)
                node.setAttribute(SedMLTags.SIM_ATTR_NAME, s);
            node.setAttribute(SedMLTags.UTCA_INIT_T, Double
                    .toString(((UniformTimeCourse) sedmlSim).getInitialTime()));
            node.setAttribute(SedMLTags.UTCA_OUT_START_T, Double
                    .toString(((UniformTimeCourse) sedmlSim)
                            .getOutputStartTime()));
            node.setAttribute(SedMLTags.UTCA_OUT_END_T,
                    Double.toString(((UniformTimeCourse) sedmlSim)
                            .getOutputEndTime()));
            node.setAttribute(SedMLTags.UTCA_POINTS_NUM, Integer
                    .toString(((UniformTimeCourse) sedmlSim)
                            .getNumberOfSteps()));
        } else if (sedmlSim.getSimulationKind().equals(SedMLTags.SIMUL_OS_KIND)) {
            node = new Element(SedMLTags.SIM_ONE_STEP);
            addNotesAndAnnotation(sedmlSim, node);
            s = sedmlSim.getId();
            if (s != null)
                node.setAttribute(SedMLTags.SIM_ATTR_ID, s);
            s = sedmlSim.getName();
            if (s != null)
                node.setAttribute(SedMLTags.SIM_ATTR_NAME, s);
            node.setAttribute(SedMLTags.ONE_STEP_STEP,
                    Double.toString(((OneStep) sedmlSim).getStep()));
        } else if (sedmlSim.getSimulationKind().equals(SedMLTags.SIMUL_SS_KIND)) {
            node = new Element(SedMLTags.SIM_STEADY_STATE);
            addNotesAndAnnotation(sedmlSim, node);
            s = sedmlSim.getId();
            if (s != null)
                node.setAttribute(SedMLTags.SIM_ATTR_ID, s);
            s = sedmlSim.getName();
            if (s != null)
                node.setAttribute(SedMLTags.SIM_ATTR_NAME, s);
        } else if (sedmlSim.getSimulationKind()
                .equals(SedMLTags.SIMUL_ANY_KIND)) {
            node = new Element(SedMLTags.SIM_ANALYSIS);
            addNotesAndAnnotation(sedmlSim, node);
            s = sedmlSim.getId();
            if (s != null)
                node.setAttribute(SedMLTags.SIM_ATTR_ID, s);
            s = sedmlSim.getName();
            if (s != null)
                node.setAttribute(SedMLTags.SIM_ATTR_NAME, s);
        } else {
            throw new RuntimeException(
                    "Simulation must be uniformTimeCourse, oneStep or steadyState or any '"
                            + sedmlSim.getId());
        }
        if (sedmlSim.getAlgorithm() != null) {
            node.addContent(getXML(sedmlSim.getAlgorithm()));
        }
        return node;
    }

    org.jdom2.Element getXML(Algorithm algorithm) {
        String s = null;
        Element node = new Element(SedMLTags.ALGORITHM_TAG);
        addNotesAndAnnotation(algorithm, node);
        // Add Attributes to tasks
        s = algorithm.getKisaoID();
        if (s != null)
            node.setAttribute(SedMLTags.ALGORITHM_ATTR_KISAOID, s);

        // list of algorithm parameters
        List<AlgorithmParameter> aps = algorithm.getListOfAlgorithmParameters();
        if (aps != null && aps.size() > 0) {
            Element apList = new Element(SedMLTags.ALGORITHM_PARAMETER_LIST);
            for (int i = 0; i < aps.size(); i++) {
                apList.addContent(getXML(aps.get(i)));
            }
            node.addContent(apList);
        }
        return node;
    }

    org.jdom2.Element getXML(AlgorithmParameter ap) {
        String s = null;
        Element node = new Element(SedMLTags.ALGORITHM_PARAMETER_TAG);
        s = ap.getKisaoID();
        if (s != null)
            node.setAttribute(SedMLTags.ALGORITHM_PARAMETER_KISAOID, s);
        s = ap.getValue();
        if (s != null)
            node.setAttribute(SedMLTags.ALGORITHM_PARAMETER_VALUE, s);
        return node;
    }

    // ============ Ranges
    private Element getXML(Range range) {
        String s = null;
        if (range instanceof VectorRange) {
            VectorRange vecRange = (VectorRange) range;
            Element node = new Element(SedMLTags.VECTOR_RANGE_TAG);
            s = vecRange.getId();
            if (s != null)
                node.setAttribute(SedMLTags.RANGE_ATTR_ID, s);
            for (int i = 0; i < vecRange.getNumElements(); i++) {
                double n = vecRange.getElementAt(i);
                Element v = new Element(SedMLTags.VECTOR_RANGE_VALUE_TAG);
                v.setText(Double.toString(n));
                node.addContent(v);
            }
            return node;
        } else if (range instanceof UniformRange) {
            UniformRange ur = (UniformRange) range;
            Element node = new Element(SedMLTags.UNIFORM_RANGE_TAG);
            s = ur.getId();
            if (s != null)
                node.setAttribute(SedMLTags.RANGE_ATTR_ID, s);
            s = Double.toString(((UniformRange) ur).getStart());
            if (s != null)
                node.setAttribute(SedMLTags.UNIFORM_RANGE_ATTR_START, s);
            s = Double.toString(((UniformRange) ur).getEnd());
            if (s != null)
                node.setAttribute(SedMLTags.UNIFORM_RANGE_ATTR_END, s);
            s = Integer.toString(((UniformRange) ur).getNumberOfPoints());
            if (s != null)
                node.setAttribute(SedMLTags.UNIFORM_RANGE_ATTR_NUMP, s);
            s = ((UniformRange) ur).getType().getText();
            if (s != null)
                node.setAttribute(SedMLTags.UNIFORM_RANGE_ATTR_TYPE, s);
            return node;
        } else { // FunctionalRange
            FunctionalRange fr = (FunctionalRange) range;
            Element node = new Element(SedMLTags.FUNCTIONAL_RANGE_TAG);
            s = fr.getId();
            if (s != null)
                node.setAttribute(SedMLTags.RANGE_ATTR_ID, s);
            s = fr.getRange();
            if (s != null)
                node.setAttribute(SedMLTags.FUNCTIONAL_RANGE_INDEX, s);
            // list of variables
            if(!fr.getVariables().isEmpty()) {
	            Element varList = new Element(SedMLTags.FUNCTIONAL_RANGE_VAR_LIST);
	            node.addContent(varList);
	            Map<String, SedBase> vars = fr.getVariables();
	            for (SedBase var : vars.values()) {
	                if (var instanceof Variable) {
	                    varList.addContent(getXML((Variable) var,
	                            VariableType.COMPUTE_CHANGE));
	                } else {
	                    throw new RuntimeException("Entity must be a Variable "
	                            + var);
	                }
	            }
            }
            if(!fr.getParameters().isEmpty()) {
	            Element parList = new Element(SedMLTags.FUNCTIONAL_RANGE_PAR_LIST);
	            node.addContent(parList);
	            Map<String, SedBase> pars = fr.getParameters();
	            for (SedBase par : pars.values()) {
	                if (par instanceof Parameter) {
	                    parList.addContent(getXML((Parameter) par));
	                } else {
	                    throw new RuntimeException("Entity must be a Parameter "
	                            + par);
	                }
	            }
            }
            if (fr.getMath() != null) {
                try {
                    ASTToXMLElementVisitor astElementVisitor = new ASTToXMLElementVisitor();
                    fr.getMath().accept(astElementVisitor);
                    node.addContent(astElementVisitor.getElement()); // insert
                                                                     // 'math'
                                                                     // attribute
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Unable to process mathML for functional range '"
                                    + fr.getId() + "' : " + e.getMessage(), e);
                }
            }
            return node;
        }
    }

    // ============== SubTasks
    private Element getXML(SubTask t) {
        Element node = new Element(SedMLTags.SUBTASK_TAG);
        String s = null;
        s = t.getOrder();
        if (s != null)
            node.setAttribute(SedMLTags.SUBTASK_ATTR_ORDER, s);
        s = t.getTask();
        if (s != null)
            node.setAttribute(SedMLTags.SUBTASK_ATTR_TASK, s);
        // Add list of dependent tasks
        Map<String, SubTask> dependentTasks = ((SubTask) t).getDependentTasks();
        if (dependentTasks != null && !dependentTasks.isEmpty()) {
            Element subTasksListElement = new Element(
                    SedMLTags.DEPENDENT_TASK_SUBTASKS_LIST);
            for (SubTask st : dependentTasks.values()) {
                // we avoid recursion by NOT calling here
                // subTasksListElement.addContent(getXML(st))
                // otherwise we might show dependent tasks of dependent tasks
                Element dt = new Element(SedMLTags.DEPENDENT_TASK);
                String s1 = null;
                s1 = st.getTask();
                if (s1 != null)
                    dt.setAttribute(SedMLTags.SUBTASK_ATTR_TASK, s1);
                subTasksListElement.addContent(dt);
            }
            node.addContent(subTasksListElement);
        }
        return node;
    }

    // ============== Tasks
    org.jdom2.Element getXML(AbstractTask sedmlTask) {

        if (sedmlTask instanceof RepeatedTask) {
            Element node = new Element(SedMLTags.REPEATED_TASK_TAG);
            addNotesAndAnnotation(sedmlTask, node);
            String s = null;
            // Add Attributes to tasks
            s = sedmlTask.getId();
            if (s != null)
                node.setAttribute(SedMLTags.TASK_ATTR_ID, s); // insert 'id'
                                                              // attribute
            s = sedmlTask.getName();
            if (s != null) {
                node.setAttribute(SedMLTags.TASK_ATTR_NAME, s); // insert 'name'
                                                                // attribute
            }
            // s = sedmlTask.getModelReference();if(s !=
            // null)node.setAttribute(SEDMLTags.TASK_ATTR_MODELREF, s); //
            // insert 'model' reference
            // s = sedmlTask.getSimulationReference();if(s !=
            // null)node.setAttribute(SEDMLTags.TASK_ATTR_SIMREF, s);
            s = Boolean.toString(((RepeatedTask) sedmlTask).getResetModel());
            node.setAttribute(SedMLTags.REPEATED_TASK_RESET_MODEL, s);
            s = ((RepeatedTask) sedmlTask).getRange();
            if (s != null)
                node.setAttribute(SedMLTags.REPEATED_TASK_ATTR_RANGE, s); // "range"
                                                                          // attribute
            // Add list of ranges
            Map<String, Range> mr = ((RepeatedTask) sedmlTask).getRanges();
            if (mr != null && !mr.isEmpty()) {
                Element rangesListElement = new Element(
                        SedMLTags.REPEATED_TASK_RANGES_LIST);
                for (Range r : mr.values()) {
                    rangesListElement.addContent(getXML(r));
                }
                node.addContent(rangesListElement);
            }
            // Add list of changes
            List<SetValue> lcs = ((RepeatedTask) sedmlTask).getChanges();
            if (lcs != null && !lcs.isEmpty()) {
                Element changesListElement = new Element(
                        SedMLTags.REPEATED_TASK_CHANGES_LIST);
                for (SetValue sv : lcs) {
                    changesListElement.addContent(getXML(sv));
                }
                node.addContent(changesListElement);
            }
            // Add list of subtasks
            Map<String, SubTask> mt = ((RepeatedTask) sedmlTask).getSubTasks();
            if (mt != null && !mt.isEmpty()) {
                Element subTasksListElement = new Element(
                        SedMLTags.REPEATED_TASK_SUBTASKS_LIST);
                for (SubTask st : mt.values()) {
                    subTasksListElement.addContent(getXML(st));
                }
                node.addContent(subTasksListElement);
            }

            return node;
        } else {
            Element node = new Element(SedMLTags.TASK_TAG);
            addNotesAndAnnotation(sedmlTask, node);
            String s = null;
            // Add Attributes to tasks
            s = sedmlTask.getId();
            if (s != null)
                node.setAttribute(SedMLTags.TASK_ATTR_ID, s); // insert 'id'
                                                              // attribute
            s = sedmlTask.getName();
            if (s != null) {
                node.setAttribute(SedMLTags.TASK_ATTR_NAME, s); // insert 'name'
                                                                // attribute
            }
            s = sedmlTask.getModelReference();
            if (s != null)
                node.setAttribute(SedMLTags.TASK_ATTR_MODELREF, s); // insert
                                                                    // 'model'
                                                                    // reference
            s = sedmlTask.getSimulationReference();
            if (s != null)
                node.setAttribute(SedMLTags.TASK_ATTR_SIMREF, s);
            return node;
        }
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

    // =============== DataGenerators
    org.jdom2.Element getXML(DataGenerator sedmlDataGen) {
        Element node = new Element(SedMLTags.DATA_GENERATOR_TAG);
        String s = null;
        addNotesAndAnnotation(sedmlDataGen, node);
        // Add Attributes to data generators
        s = sedmlDataGen.getId();
        if (s != null)
            node.setAttribute(SedMLTags.DATAGEN_ATTR_ID, sedmlDataGen.getId()); // insert
                                                                                // 'id'
                                                                                // attribute
        s = sedmlDataGen.getName();
        if (s != null) {
            node.setAttribute(SedMLTags.DATAGEN_ATTR_NAME, s); // insert 'name'
                                                               // attribute
        }
        List<Variable> listOfVariables = sedmlDataGen.getListOfVariables();
        if (listOfVariables != null && listOfVariables.size() > 0) {
            Element list = new Element(SedMLTags.DATAGEN_ATTR_VARS_LIST);
            for (int i = 0; i < listOfVariables.size(); i++) {
                list.addContent(getXML(listOfVariables.get(i),
                        VariableType.DATA_GENERATOR));
            }
            node.addContent(list);
        }
        List<Parameter> listOfParameters = sedmlDataGen.getListOfParameters();
        if (listOfParameters != null && listOfParameters.size() > 0) {
            Element list = new Element(SedMLTags.DATAGEN_ATTR_PARAMS_LIST);
            for (int i = 0; i < listOfParameters.size(); i++) {
                list.addContent(getXML(listOfParameters.get(i)));
            }
            node.addContent(list);
        }
        if (sedmlDataGen.getMath() != null) {
            try {
                ASTToXMLElementVisitor astElementVisitor = new ASTToXMLElementVisitor();
                sedmlDataGen.getMath().accept(astElementVisitor);
                node.addContent(astElementVisitor.getElement()); // insert
                                                                 // 'math'
                                                                 // attribute
            } catch (Exception e) {
                throw new RuntimeException(
                        "Unable to process mathML for datagenerator '"
                                + sedmlDataGen.getId() + "' : "
                                + e.getMessage(), e);
            }
        }

        return node;
    }

    // TODO: need to add another getXML(Variable...) for the "change math"
    // variables
    org.jdom2.Element getXML(Variable variable, VariableType varType) {
        Element node = new Element(SedMLTags.DATAGEN_ATTR_VARIABLE);
        addNotesAndAnnotation(variable, node);// Add Variables to list of
                                              // variables
        String s = null;
        s = variable.getId();
        if (s != null)
            node.setAttribute(SedMLTags.VARIABLE_ID, variable.getId()); // insert
                                                                        // 'id'
                                                                        // attribute
        s = variable.getName();
        if (s != null) {
            node.setAttribute(SedMLTags.VARIABLE_NAME, s);
        }
        s = variable.getReference();
        if (s != null && s.length() > 0
                && varType.equals(VariableType.COMPUTE_CHANGE)) {
            node.setAttribute(SedMLTags.VARIABLE_MODEL, variable.getReference()); // we
                                                                                  // know
                                                                                  // it's
                                                                                  // a
                                                                                  // task
                                                                                  // reference
        } else if (s != null && s.length() > 0
                && varType.equals(VariableType.DATA_GENERATOR)) {
            node.setAttribute(SedMLTags.VARIABLE_TASK, variable.getReference());
        }
        if (variable.isVariable()) {
            s = variable.getTarget();
            if (s != null)
                node.setAttribute(SedMLTags.VARIABLE_TARGET, s);
        } else if (variable.isSymbol()) {
            s = variable.getSymbol().getUrn();
            if (s != null)
                node.setAttribute(SedMLTags.VARIABLE_SYMBOL, s);
        }

        return node;
    }

    org.jdom2.Element getXML(Parameter parameter) {
        Element node = new Element(SedMLTags.DATAGEN_ATTR_PARAMETER);
        String s = null;

        s = parameter.getId();
        if (s != null)
            node.setAttribute(SedMLTags.PARAMETER_ID, parameter.getId()); // insert
                                                                          // 'id'
                                                                          // attribute
        s = parameter.getName();
        if (s != null)
            node.setAttribute(SedMLTags.PARAMETER_NAME, s);
        node.setAttribute(SedMLTags.PARAMETER_VALUE,
                Double.toString(parameter.getValue()));
        addNotesAndAnnotation(parameter, node);
        return node;
    }

    // ============ Outputs
    org.jdom2.Element getXML(Output sedmlOutput) {
        Element node = null; // Add outputs to list of outputs
        String s = null;
        if (sedmlOutput.getKind().equals(SedMLTags.PLOT2D_KIND)) { // various
                                                                   // attributes
                                                                   // depending
                                                                   // on kind
            node = new Element(SedMLTags.OUTPUT_P2D);
            addNotesAndAnnotation(sedmlOutput, node);
            s = sedmlOutput.getId();
            if (s != null)
                node.setAttribute(SedMLTags.OUTPUT_ID, sedmlOutput.getId());
            s = sedmlOutput.getName();
            if (s != null)
                node.setAttribute(SedMLTags.OUTPUT_NAME, s);
            List<Curve> listOfCurves = ((Plot2D) sedmlOutput).getListOfCurves();
            if (listOfCurves != null && listOfCurves.size() > 0) {
                Element list = new Element(SedMLTags.OUTPUT_CURVES_LIST);
                for (int i = 0; i < listOfCurves.size(); i++) {
                    list.addContent(getXML((Curve) listOfCurves.get(i)));
                }
                node.addContent(list);
            }
        } else if (sedmlOutput.getKind().equals(SedMLTags.PLOT3D)) {
            node = new Element(SedMLTags.OUTPUT_P3D);
            addNotesAndAnnotation(sedmlOutput, node);
            s = sedmlOutput.getId();
            if (s != null)
                node.setAttribute(SedMLTags.OUTPUT_ID, sedmlOutput.getId());
            s = sedmlOutput.getName();
            if (s != null)
                node.setAttribute(SedMLTags.OUTPUT_NAME, s);
            List<Surface> listOfSurfaces = ((Plot3D) sedmlOutput)
                    .getListOfSurfaces();
            if (listOfSurfaces != null && listOfSurfaces.size() > 0) {
                Element list = new Element(SedMLTags.OUTPUT_SURFACES_LIST);
                for (int i = 0; i < listOfSurfaces.size(); i++) {
                    list.addContent(getXML(listOfSurfaces.get(i)));
                }
                node.addContent(list);
            }
        } else if (sedmlOutput.getKind().equals(SedMLTags.REPORT_KIND)) {
            node = new Element(SedMLTags.OUTPUT_REPORT);
            addNotesAndAnnotation(sedmlOutput, node);
            s = sedmlOutput.getId();
            if (s != null)
                node.setAttribute(SedMLTags.OUTPUT_ID, sedmlOutput.getId());
            s = sedmlOutput.getName();
            if (s != null)
                node.setAttribute(SedMLTags.OUTPUT_NAME, s);
            List<DataSet> listOfDataSets = ((Report) sedmlOutput)
                    .getListOfDataSets();
            if (listOfDataSets != null && listOfDataSets.size() > 0) {
                Element list = new Element(SedMLTags.OUTPUT_DATASETS_LIST);
                for (int i = 0; i < listOfDataSets.size(); i++) {
                    list.addContent(getXML(listOfDataSets.get(i)));
                }
                node.addContent(list);
            }
        }

        return node;
    }

    org.jdom2.Element getXML(Curve sedCurve) {
        String val = null;// Curves
        Element node = new Element(SedMLTags.OUTPUT_CURVE);
        val = sedCurve.getId();
        if (val != null)
            node.setAttribute(SedMLTags.OUTPUT_ID, val);
        val = sedCurve.getName();
        if (val != null)
            node.setAttribute(SedMLTags.OUTPUT_NAME, val);
        node.setAttribute(SedMLTags.OUTPUT_LOG_X,
                String.valueOf(sedCurve.getLogX()));
        node.setAttribute(SedMLTags.OUTPUT_LOG_Y,
                String.valueOf(sedCurve.getLogY()));
        val = sedCurve.getXDataReference();
        if (val != null)
            node.setAttribute(SedMLTags.OUTPUT_DATA_REFERENCE_X, val); // insert
                                                                       // 'xDataReference'
                                                                       // attribute
        val = sedCurve.getYDataReference();
        if (val != null)
            node.setAttribute(SedMLTags.OUTPUT_DATA_REFERENCE_Y, val); // insert
                                                                       // 'yDataReference'
                                                                       // attribute
        addNotesAndAnnotation(sedCurve, node);
        return node;
    }

    org.jdom2.Element getXML(Surface sedSurface) {
        // Surfaces
        Element node = new Element(SedMLTags.OUTPUT_SURFACE);
        String val = null;// Curves
        node.setAttribute(SedMLTags.OUTPUT_LOG_Z,
                String.valueOf(sedSurface.getLogZ()));

        val = sedSurface.getId();
        if (val != null)
            node.setAttribute(SedMLTags.OUTPUT_ID, val);
        val = sedSurface.getName();
        if (val != null)
            node.setAttribute(SedMLTags.OUTPUT_NAME, val);
        node.setAttribute(SedMLTags.OUTPUT_LOG_X,
                String.valueOf(sedSurface.getLogX()));
        node.setAttribute(SedMLTags.OUTPUT_LOG_Y,
                String.valueOf(sedSurface.getLogY()));
        val = sedSurface.getXDataReference();
        if (val != null)
            node.setAttribute(SedMLTags.OUTPUT_DATA_REFERENCE_X, val); // insert
                                                                       // 'xDataReference'
                                                                       // attribute
        val = sedSurface.getYDataReference();
        if (val != null)
            node.setAttribute(SedMLTags.OUTPUT_DATA_REFERENCE_Y, val); // insert
                                                                       // 'yDataReference'
                                                                       // attribute
        val = sedSurface.getzDataReference();
        if (val != null)
            node.setAttribute(SedMLTags.OUTPUT_DATA_REFERENCE_Z, val);
        addNotesAndAnnotation(sedSurface, node);
        return node;
    }

    org.jdom2.Element getXML(DataSet sedDataSet) { // DataSets
        String val = null;
        Element node = new Element(SedMLTags.OUTPUT_DATASET);
        val = sedDataSet.getDataReference();
        if (val != null)
            node.setAttribute(SedMLTags.OUTPUT_DATA_REFERENCE, val);
        val = sedDataSet.getId();
        if (val != null)
            node.setAttribute(SedMLTags.OUTPUT_ID, val);
        val = sedDataSet.getName();
        if (val != null)
            node.setAttribute(SedMLTags.OUTPUT_NAME, val);
        val = sedDataSet.getLabel();
        if (val != null)
            node.setAttribute(SedMLTags.OUTPUT_DATASET_LABEL, val);
        addNotesAndAnnotation(sedDataSet, node);

        return node;
    }

    private org.jdom2.Element setDefaultNamespace(org.jdom2.Element rootNode,
            org.jdom2.Namespace namespace) {
        // only if there is a node and it has no default namespace!
        if (rootNode != null && rootNode.getNamespaceURI().length() == 0) {
            // set namespace for this node
            rootNode.setNamespace(namespace);
            Iterator<Element> childIterator = rootNode.getChildren().iterator();
            while (childIterator.hasNext()) {
                org.jdom2.Element child = childIterator.next();
                // check children
                setDefaultNamespace(child, namespace);
            }
        }
        return rootNode;
    }

}
