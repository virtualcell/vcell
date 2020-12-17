package org.jlibsedml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jlibsedml.UniformRange.UniformType;
import org.jlibsedml.mathsymbols.SedMLSymbolFactory;
import org.jmathml.ASTNode;
import org.jmathml.ASTRootNode;
import org.jmathml.MathMLReader;
import org.jmathml.SymbolRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@SuppressWarnings("unchecked")
class SEDMLReader {

	Namespace sedNS = null;
	Logger   log = LoggerFactory.getLogger(SEDMLReader.class);

	Model getModel(Element modelElement) throws DataConversionException {
		Model m = new Model(modelElement.getAttributeValue(SEDMLTags.MODEL_ATTR_ID),
		        modelElement.getAttributeValue(SEDMLTags.MODEL_ATTR_NAME),
		        modelElement.getAttributeValue(SEDMLTags.MODEL_ATTR_LANGUAGE),
		        modelElement.getAttributeValue(SEDMLTags.MODEL_ATTR_SOURCE));
		List lModelChildren = modelElement.getChildren();
		Iterator iModelChildren = lModelChildren.iterator();
		while (iModelChildren.hasNext()) {
			Element eModelChild = (Element) iModelChildren.next();
			if (eModelChild.getName().equals(SEDMLTags.CHANGES)) {
				List lChanges = eModelChild.getChildren();
				Iterator iChanges = lChanges.iterator();
				while (iChanges.hasNext()) {
					Element aChange = (Element) iChanges.next();
					Change c = getChange(aChange);
					m.addChange(c);
				}
			}
		}
		addNotesAndAnnotation(m, modelElement);
		return m;
	}

	Change getChange(Element aChange) throws DataConversionException {
		Change rc = null;
		if (aChange.getName().equals(SEDMLTags.CHANGE_ATTRIBUTE)) {
			rc = new ChangeAttribute(new XPathTarget(aChange
					.getAttributeValue(SEDMLTags.CHANGE_ATTR_TARGET)), aChange
					.getAttributeValue(SEDMLTags.CHANGE_ATTR_NEWVALUE));

		} else if (aChange.getName().equals(SEDMLTags.CHANGE_XML)
				|| aChange.getName().equals(SEDMLTags.ADD_XML)) {
			Iterator<Element> changeChildren = aChange.getChildren().iterator();
			while (changeChildren.hasNext()) {
				Element el = (Element) changeChildren.next();
				if (el.getName().equals(SEDMLTags.NEW_XML)) {
					List<Element> xml = getNewXML(el);
					NewXML newxml = new NewXML(xml);
					if (aChange.getName().equals(SEDMLTags.CHANGE_XML))
						rc = new ChangeXML(new XPathTarget(aChange.getAttributeValue(SEDMLTags.CHANGE_ATTR_TARGET)), newxml);
					else
						rc = new AddXML(new XPathTarget(aChange.getAttributeValue(SEDMLTags.CHANGE_ATTR_TARGET)), newxml);
				}
			}

		} else if (aChange.getName().equals(SEDMLTags.REMOVE_XML)) {
			rc = new RemoveXML(new XPathTarget(aChange.getAttributeValue(SEDMLTags.CHANGE_ATTR_TARGET)));
			
		} else if (aChange.getName().equals(SEDMLTags.COMPUTE_CHANGE)) {
			ASTRootNode math = null;
			Element toAdd = null;
			List<Variable> vars = new ArrayList<Variable>();
			List<Parameter> params = new ArrayList<Parameter>();
			Iterator<Element> changeChildren = aChange.getChildren().iterator();
			while (changeChildren.hasNext()) {
				Element el = (Element) changeChildren.next();

				if (el.getName().equals(SEDMLTags.CHANGE_ATTR_MATH)) {
					math = (ASTRootNode) new MathMLReader().parseMathML(el);
				} else if (el.getName().equals(SEDMLTags.DATAGEN_ATTR_VARS_LIST)) {
					List<Element> lVariables = el.getChildren();
					Iterator<Element> iVariables = lVariables.iterator();
					while (iVariables.hasNext()) {
						Element eVariable = (Element) iVariables.next();
						if (eVariable.getName().equals(SEDMLTags.DATAGEN_ATTR_VARIABLE)) {
							vars.add(createVariable(eVariable, true));
						}
					}
				} else if (el.getName().equals(SEDMLTags.DATAGEN_ATTR_PARAMS_LIST)) {
					List<Element> lParameters = el.getChildren();
					Iterator<Element> iParameters = lParameters.iterator();
					while (iParameters.hasNext()) {
						Element eParameter = (Element) iParameters.next();
						if (eParameter.getName().equals(SEDMLTags.DATAGEN_ATTR_PARAMETER)) {
							params.add(createParameter(eParameter));
						}
					}
				}

				// TODO: variable and parameter need to be also
				// loaded here
			}
			ComputeChange cc = new ComputeChange(new XPathTarget(aChange
					.getAttributeValue(SEDMLTags.CHANGE_ATTR_TARGET)), math);
			cc.setListOfParameters(params);
			cc.setListOfVariables(vars);
			rc = cc;

		}
		addNotesAndAnnotation(rc, aChange);
		return rc;
	}

	// The Change within a repeated task (SetChange)
    private void addChanges(RepeatedTask t, Element element) throws DataConversionException {
        SetValue sv = null;
        String changeChildName = null;
        List<Element> children = element.getChildren();
        Iterator<Element> iChildren = children.iterator();
        while (iChildren.hasNext()) {
            Element eChild = (Element) iChildren.next();
            changeChildName = eChild.getName();
            if (eChild.getName().equals(SEDMLTags.SET_VALUE)) {
                String xPathString = eChild.getAttributeValue(SEDMLTags.SET_VALUE_ATTR_TARGET);
                XPathTarget target = new XPathTarget(xPathString);
                String rangeReference = eChild.getAttributeValue(SEDMLTags.SET_VALUE_ATTR_RANGE_REF);
                String modelReference = eChild.getAttributeValue(SEDMLTags.SET_VALUE_ATTR_MODEL_REF);
                sv = new SetValue(target, rangeReference, modelReference);
                getSetValueContent(sv, eChild);
                t.addChange(sv);
            } else {
                log.warn("Unexpected " + eChild);
            }
            log.debug("sv   " + sv.toString());
        }
    }
    private SetValue getSetValueContent(SetValue c, Element element) throws DataConversionException {
        List<Element> children = element.getChildren();
        Iterator<Element> iChildren = children.iterator();
        while (iChildren.hasNext()) {
            Element eChild = (Element) iChildren.next();
            if (eChild.getName().equals(SEDMLTags.CHANGE_ATTR_MATH)) {
                ASTNode math = (ASTRootNode) new MathMLReader().parseMathML(eChild);
                c.setMath(math);
            } else if (eChild.getName().equals(SEDMLTags.DATAGEN_ATTR_VARS_LIST)) {
                List<Element> lVariables = eChild.getChildren();
                Iterator<Element> iVariables = lVariables.iterator();
                while (iVariables.hasNext()) {
                    Element eVariable = (Element) iVariables.next();
                    if (eVariable.getName().equals(SEDMLTags.DATAGEN_ATTR_VARIABLE)) {
                        c.addVariable(createVariable(eVariable, true));
                    }
                }
            } else if (eChild.getName().equals(SEDMLTags.DATAGEN_ATTR_PARAMS_LIST)) {
                List<Element> lParameters = eChild.getChildren();
                Iterator<Element> iParameters = lParameters.iterator();
                while (iParameters.hasNext()) {
                    Element eParameter = (Element) iParameters.next();
                    if (eParameter.getName().equals(SEDMLTags.DATAGEN_ATTR_PARAMETER)) {
                        c.addParameter(createParameter(eParameter));
                    }
                }
            } else {
                log.warn("Unexpected " + eChild);
            }
        }
        return c;
    }


	private void addNotesAndAnnotation(SEDBase sedbase, Element xmlElement) {
		List children = xmlElement.getChildren();
		Iterator ichildren = children.iterator();
		List<Element>anns = new ArrayList<Element>();
		while (ichildren.hasNext()) {
			Element element = (Element) ichildren.next();
			if (element.getName().equals(SEDMLTags.NOTES)) {
				Notes n = getNotes(element);
				if (n != null)
					sedbase.addNote(n);
			} else if (element.getName().equals(SEDMLTags.ANNOTATION)) {
			    anns.add(element);
				
			}
		}
		for (Element ann: anns){
		   sedbase.addAnnotation(getAnnotation(ann));
		}
		sedbase.setMetaId(xmlElement.getAttributeValue(SEDMLTags.META_ID_ATTR_NAME));
	}

	Simulation getSimulation(Element simElement) {
	    Simulation s = null;
		List<Element> children = simElement.getChildren();
		Algorithm alg = null;
		for (Element el : children) {
			if (el.getName().equals(SEDMLTags.ALGORITHM_TAG)) {
				alg = getAlgorithm(el);
			}
		}
		if (simElement.getName().equals(SEDMLTags.SIM_UTC)) {
            s = new UniformTimeCourse(simElement.getAttributeValue(SEDMLTags.SIM_ATTR_ID), 
                    simElement.getAttributeValue(SEDMLTags.SIM_ATTR_NAME), 
                    Double.parseDouble(simElement.getAttributeValue(SEDMLTags.UTCA_INIT_T)), 
                    Double.parseDouble(simElement.getAttributeValue(SEDMLTags.UTCA_OUT_START_T)),
                    Double.parseDouble(simElement.getAttributeValue(SEDMLTags.UTCA_OUT_END_T)),
                    Integer.parseInt(simElement.getAttributeValue(SEDMLTags.UTCA_POINTS_NUM)), alg);
		} else if(simElement.getName().equals(SEDMLTags.SIM_OS)) {
            s = new OneStep(simElement.getAttributeValue(SEDMLTags.SIM_ATTR_ID), 
                    simElement.getAttributeValue(SEDMLTags.SIM_ATTR_NAME), alg,
                    Double.parseDouble(simElement.getAttributeValue(SEDMLTags.OS_STEP)));
		} else if(simElement.getName().equals(SEDMLTags.SIM_SS)) {
            s = new SteadyState(simElement.getAttributeValue(SEDMLTags.SIM_ATTR_ID), 
                    simElement.getAttributeValue(SEDMLTags.SIM_ATTR_NAME), alg);
		} else if(simElement.getName().equals(SEDMLTags.SIM_ANY)) {   // we don't know what "any" means, we do SteadyState as it's the simplest
            s = new SteadyState(simElement.getAttributeValue(SEDMLTags.SIM_ATTR_ID), 
                    simElement.getAttributeValue(SEDMLTags.SIM_ATTR_NAME), alg);
		}
		addNotesAndAnnotation(s, simElement);

		return s;
	}

	Algorithm getAlgorithm(Element algorithmElement) {
		Algorithm alg = new Algorithm(algorithmElement.getAttributeValue(SEDMLTags.ALGORITHM_ATTR_KISAOID));
		addNotesAndAnnotation(alg, algorithmElement);
		
        List<Element> children = algorithmElement.getChildren();
        Iterator<Element> iChildren = children.iterator();
        while (iChildren.hasNext()) {
            Element eChild = (Element) iChildren.next();
            if (eChild.getName().equals(SEDMLTags.ALGORITHM_PARAMETER_LIST)) {
                addAlgorithmParameters(alg, eChild);
            } else {
                log.warn("Unexpected " + eChild);
            }
        }
		return alg;
	}
	private void addAlgorithmParameters(Algorithm a, Element element) {
	    String childName = null;
	    List<Element> children = element.getChildren();
	    Iterator<Element> iChildren = children.iterator();
	    while (iChildren.hasNext()) {
	        Element eChild = (Element) iChildren.next();
	        childName = eChild.getName();
	        if (eChild.getName().equals(SEDMLTags.ALGORITHM_PARAMETER_TAG)) {
	            AlgorithmParameter ap = new AlgorithmParameter(
	                    eChild.getAttributeValue(SEDMLTags.ALGORITHM_PARAMETER_KISAOID),
	                    eChild.getAttributeValue(SEDMLTags.ALGORITHM_PARAMETER_VALUE));
	            a.addAlgorithmParameter(ap);
	        } else {
	            log.warn("Unexpected " + eChild);
	        }
	    }
	}

    Task getTask(Element taskElement) {
        Task t = null;
        t = new Task(
                taskElement.getAttributeValue(SEDMLTags.TASK_ATTR_ID), // Task
                // Attribute
                // "id"
                taskElement.getAttributeValue(SEDMLTags.TASK_ATTR_NAME),
                taskElement.getAttributeValue(SEDMLTags.TASK_ATTR_MODELREF),
                taskElement.getAttributeValue(SEDMLTags.TASK_ATTR_SIMREF));
        // notes and annotations
        addNotesAndAnnotation(t, taskElement);

        return t;
    }
    RepeatedTask getRepeatedTask(Element taskElement) 
    throws DataConversionException {
        RepeatedTask t = null;
        String resetModel = taskElement.getAttributeValue(SEDMLTags.REPEATED_TASK_RESET_MODEL);
        boolean bResetModel = resetModel.equals("true") ? true : false;
        t = new RepeatedTask(
                taskElement.getAttributeValue(SEDMLTags.TASK_ATTR_ID),
                taskElement.getAttributeValue(SEDMLTags.TASK_ATTR_NAME),
                bResetModel,
                taskElement.getAttributeValue(SEDMLTags.REPEATED_TASK_ATTR_RANGE));
        
        addNotesAndAnnotation(t, taskElement);  // notes and annotations
        
        List<Element> children = taskElement.getChildren();
        Iterator<Element> iChildren = children.iterator();
        while (iChildren.hasNext()) {
            Element eChild = (Element) iChildren.next();
            String repeatedTaskChildName = eChild.getName();
            if (eChild.getName().equals(SEDMLTags.REPEATED_TASK_RANGES_LIST)) {
                addRanges(t, eChild);
            } else if(eChild.getName().equals(SEDMLTags.REPEATED_TASK_CHANGES_LIST)) {
              addChanges(t, eChild);
            } else if(eChild.getName().equals(SEDMLTags.REPEATED_TASK_SUBTASKS_LIST)) {
              addSubTasks(t, eChild);
            } else {
                log.warn("Unexpected " + eChild);
            }
        }
        return t;
    }

    private void addSubTasks(RepeatedTask t, Element element) {
        SubTask s = null;
        String subTaskChildName = null;
        List<Element> children = element.getChildren();
        Iterator<Element> iChildren = children.iterator();
        while(iChildren.hasNext()) {
            Element eChild = (Element) iChildren.next();
            subTaskChildName = eChild.getName();
            if (eChild.getName().equals(SEDMLTags.SUBTASK_TAG)) {
                String order = eChild.getAttributeValue(SEDMLTags.SUBTASK_ATTR_ORDER);
                String taskId = eChild.getAttributeValue(SEDMLTags.SUBTASK_ATTR_TASK);
                s = new SubTask(order, taskId);
                addDependTasks(s, eChild);
                t.addSubtask(s);
            } else {
                log.warn("Unexpected " + eChild);
            }
            log.debug("s   " + s.toString());
        }
    }

    private void addDependTasks(SubTask t, Element element) {
       
        String subTaskChildName = null;
        List<Element> children = element.getChildren();
        Iterator<Element> iChildren = children.iterator();
        while(iChildren.hasNext()) {
            Element eChild = (Element) iChildren.next();
            subTaskChildName = eChild.getName();
            if (eChild.getName().equals(SEDMLTags.DEPENDENT_TASK_SUBTASKS_LIST)) {
                addDependTask(t, eChild);
            } else {
                log.warn("Unexpected " + eChild);
            }
        }
    }

    private void addDependTask(SubTask t, Element element) {
        SubTask s = null;
        String subTaskChildName = null;
        List<Element> children = element.getChildren();
        Iterator<Element> iChildren = children.iterator();
        while(iChildren.hasNext()) {
            Element eChild = (Element) iChildren.next();
            subTaskChildName = eChild.getName();
            if (eChild.getName().equals(SEDMLTags.DEPENDENTTASK_TAG)) {
                String taskId = eChild.getAttributeValue(SEDMLTags.SUBTASK_ATTR_TASK);
                s = new SubTask(taskId);
                t.addDependentTask(s);
            } else {
                log.warn("Unexpected " + eChild);
            }
            log.debug("s      " + s.toString());
        }
    }

    private void addRanges(RepeatedTask task, Element element) 
    throws DataConversionException {
        Range range = null;
        String childName = null;
        List<Element> children = element.getChildren();
        Iterator<Element> iChildren = children.iterator();
        while (iChildren.hasNext()) {
            Element eChild = (Element) iChildren.next();
            childName = eChild.getName();
            if (eChild.getName().equals(SEDMLTags.VECTOR_RANGE_TAG)) {
                String id = eChild.getAttributeValue(SEDMLTags.RANGE_ATTR_ID);
                range = new VectorRange(id);
                addVectorRangeValues(range, eChild);
                task.addRange(range);
            } else if(eChild.getName().equals(SEDMLTags.UNIFORM_RANGE_TAG)) {
                String id = eChild.getAttributeValue(SEDMLTags.RANGE_ATTR_ID);
                Double start = Double.parseDouble(eChild.getAttributeValue(SEDMLTags.UNIFORM_RANGE_ATTR_START));
                Double end = Double.parseDouble(eChild.getAttributeValue(SEDMLTags.UNIFORM_RANGE_ATTR_END));
                int numberOfPoints = Integer.parseInt(eChild.getAttributeValue(SEDMLTags.UNIFORM_RANGE_ATTR_NUMP));
                String type = eChild.getAttributeValue(SEDMLTags.UNIFORM_RANGE_ATTR_TYPE);
                range = new UniformRange(id, start, end, numberOfPoints, UniformType.fromString(type));
                task.addRange(range);
            } else if(eChild.getName().equals(SEDMLTags.FUNCTIONAL_RANGE_TAG)) {
                String id = eChild.getAttributeValue(SEDMLTags.RANGE_ATTR_ID);
                String index = eChild.getAttributeValue(SEDMLTags.FUNCTIONAL_RANGE_INDEX);
                range = new FunctionalRange(id, index);
                addFunctionalRangeLists(range, eChild);
                task.addRange(range);
            } else {
                log.warn("Unexpected range type {}",  eChild);
            }
            log.debug("range is {}", range);
        }
    }

    private void addFunctionalRangeLists(Range r, Element element) 
    throws DataConversionException {
        String childName = null;
        List<Element> children = element.getChildren();
        Iterator<Element> iChildren = children.iterator();
        while (iChildren.hasNext()) {
            Element eChild = (Element) iChildren.next();
            childName = eChild.getName();
            if (eChild.getName().equals(SEDMLTags.FUNCTIONAL_RANGE_VAR_LIST)) {
                addFunctionalRangeVariable(r, eChild);
            } else if(eChild.getName().equals(SEDMLTags.FUNCTIONAL_RANGE_PAR_LIST)) {
                addFunctionalRangeParameter(r, eChild);
            } else if(eChild.getName().equals(SEDMLTags.FUNCTION_MATH_TAG)) {
                ASTNode math = (ASTRootNode) new MathMLReader().parseMathML(eChild);
                log.debug("r      " + math.toString());
                ((FunctionalRange) r).setMath(math);
           } else {
                log.warn("Unexpected " + eChild);
            }
        }
    }

    private void addFunctionalRangeVariable(Range r, Element element) 
    throws DataConversionException {
        String childName = null;
        List<Element> children = element.getChildren();
        Iterator<Element> iChildren = children.iterator();
        while (iChildren.hasNext()) {
            Element eChild = (Element) iChildren.next();
            childName = eChild.getName();
            if (eChild.getName().equals(SEDMLTags.DATAGEN_ATTR_VARIABLE)) {
                Variable v = createVariable(eChild, true);
                log.debug("r      Functional Range " + v.toString());
                ((FunctionalRange) r).addVariable(v);
            } else {
                log.warn("Unexpected " + eChild);
            }
        }
    }
    private void addFunctionalRangeParameter(Range r, Element element) 
    throws DataConversionException {
        String childName = null;
        List<Element> children = element.getChildren();
        Iterator<Element> iChildren = children.iterator();
        while (iChildren.hasNext()) {
            Element eChild = (Element) iChildren.next();
            childName = eChild.getName();
            if(eChild.getName().equals(SEDMLTags.DATAGEN_ATTR_PARAMETER)) {
                Parameter p = createParameter(eChild);
                log.debug("r      Functional Range " + p.toString());
                ((FunctionalRange) r).addParameter(p);
            } else {
                log.warn("Unexpected " + eChild);
            }
        }
    }

    private void addVectorRangeValues(Range r, Element element) {
        String childName = null;
        List<Element> children = element.getChildren();
        Iterator<Element> iChildren = children.iterator();
        while (iChildren.hasNext()) {
            Element eChild = (Element) iChildren.next();
            childName = eChild.getName();
            if (eChild.getName().equals(SEDMLTags.VECTOR_RANGE_VALUE_TAG)) {
                Double value = Double.parseDouble(eChild.getText());
                ((VectorRange) r).addValue(value);
                log.debug("r      Vector Range: addValue(" + value + ")");
            } else {
                log.warn("Unexpected " + eChild);
            }
        }
    }

    DataGenerator getDataGenerator(Element dataGenElement)
			throws DataConversionException {
		DataGenerator d = null;
		ASTNode math = null;
		d = new DataGenerator(dataGenElement
				.getAttributeValue(SEDMLTags.DATAGEN_ATTR_ID), dataGenElement
				.getAttributeValue(SEDMLTags.DATAGEN_ATTR_NAME));
		// eDataGenerator.getAttributeValue(MiaseMLTags.DGA_MATH));
		List<Element> lDataGeneratorChildren = dataGenElement.getChildren();
		Iterator<Element> iDataGeneratorChildren = lDataGeneratorChildren
				.iterator();
		while (iDataGeneratorChildren.hasNext()) {
			Element eDataGeneratorChild = (Element) iDataGeneratorChildren.next();

			if (eDataGeneratorChild.getName().equals(
					SEDMLTags.DATAGEN_ATTR_VARS_LIST)) {
				List<Element> lVariables = eDataGeneratorChild.getChildren();
				Iterator<Element> iVariables = lVariables.iterator();
				while (iVariables.hasNext()) {
					Element eVariable = (Element) iVariables.next();

					if (eVariable.getName().equals(
							SEDMLTags.DATAGEN_ATTR_VARIABLE)) {
						// task
						d.addVariable(createVariable(eVariable, false));
					}
				}
			} else if (eDataGeneratorChild.getName().equals(
					SEDMLTags.DATAGEN_ATTR_PARAMS_LIST)) {
				List<Element> lParameters = eDataGeneratorChild.getChildren();
				Iterator<Element> iParameters = lParameters.iterator();
				while (iParameters.hasNext()) {
					Element eParameter = (Element) iParameters.next();
					if (eParameter.getName().equals(
							SEDMLTags.DATAGEN_ATTR_PARAMETER)) {

						d.addParameter(createParameter(eParameter));
					}
				}
			} else if (eDataGeneratorChild.getName().equals(
					SEDMLTags.DATAGEN_ATTR_MATH)) {
				math = (ASTRootNode) new MathMLReader()
						.parseMathML(eDataGeneratorChild);

			}
		}
		d.setMathML(math);
		// notes and annotations
		addNotesAndAnnotation(d, dataGenElement);

		return d;
	}

	Parameter createParameter(Element eParameter)
			throws DataConversionException {
		Parameter p = new Parameter(eParameter
				.getAttributeValue(SEDMLTags.PARAMETER_ID), eParameter
				.getAttributeValue(SEDMLTags.PARAMETER_NAME), eParameter
				.getAttribute(SEDMLTags.PARAMETER_VALUE).getDoubleValue());
		addNotesAndAnnotation(p, eParameter);
		return p;
	}

	Variable createVariable(Element eVariable, boolean isModel) {
		if (eVariable.getAttribute(SEDMLTags.VARIABLE_SYMBOL) == null) {
			Variable v = new Variable(
					eVariable.getAttributeValue(SEDMLTags.VARIABLE_ID),
					eVariable.getAttributeValue(SEDMLTags.VARIABLE_NAME),
					isModel ? eVariable
							.getAttributeValue(SEDMLTags.VARIABLE_MODEL)
							: eVariable
									.getAttributeValue(SEDMLTags.VARIABLE_TASK),
					eVariable.getAttributeValue(SEDMLTags.VARIABLE_TARGET));
			addNotesAndAnnotation(v, eVariable);
			return v;
		} else {
			Variable v = new Variable(
					eVariable.getAttributeValue(SEDMLTags.VARIABLE_ID),
					eVariable.getAttributeValue(SEDMLTags.VARIABLE_NAME),
					isModel ? eVariable
							.getAttributeValue(SEDMLTags.VARIABLE_MODEL)
							: eVariable
									.getAttributeValue(SEDMLTags.VARIABLE_TASK),
					VariableSymbol.getVariableSymbolFor(eVariable
							.getAttributeValue(SEDMLTags.VARIABLE_SYMBOL)));
			addNotesAndAnnotation(v, eVariable);
			return v;
		}
	}

	Output getOutput(Element outputElement) {
		if (outputElement.getName().equals(SEDMLTags.OUTPUT_P2D)) {
			Plot2D p2d = new Plot2D(outputElement
					.getAttributeValue(SEDMLTags.OUTPUT_ID), outputElement
					.getAttributeValue(SEDMLTags.OUTPUT_NAME));
			List<Element> lPlot2DChildren = outputElement.getChildren();
			Iterator<Element> iPlot2DChildren = lPlot2DChildren.iterator();
			while (iPlot2DChildren.hasNext()) {
				Element ePlot2DChild = (Element) iPlot2DChildren.next();
		
				// "listOfCurves"
				if (ePlot2DChild.getName().equals(SEDMLTags.OUTPUT_CURVES_LIST)) {
					List<Element> lCurves = ePlot2DChild.getChildren();
					Iterator<Element> iCurves = lCurves.iterator();
					while (iCurves.hasNext()) {
						Element aCurve = (Element) iCurves.next();
				
						if (aCurve.getName().equals(SEDMLTags.OUTPUT_CURVE)) {
							Curve c = getCurve(aCurve);
							p2d.addCurve(c);
						}
					}
				}
			}
			// notes and annotations
			addNotesAndAnnotation(p2d, outputElement);

			return p2d;
		} else if (outputElement.getName().equals(SEDMLTags.OUTPUT_P3D)) { // ex:
			// "plot3D"
			Plot3D p3d = new Plot3D(outputElement
					.getAttributeValue(SEDMLTags.OUTPUT_ID), outputElement
					.getAttributeValue(SEDMLTags.OUTPUT_NAME));
			List<Element> lPlot3DChildren = outputElement.getChildren();
			Iterator<Element> iPlot3DChildren = lPlot3DChildren.iterator();
			while (iPlot3DChildren.hasNext()) {
				Element ePlot3DChild = (Element) iPlot3DChildren.next();
		
				// "listOfSurfaces"
				if (ePlot3DChild.getName().equals(
						SEDMLTags.OUTPUT_SURFACES_LIST)) {
					List<Element> lSurfaces = ePlot3DChild.getChildren();
					Iterator<Element> iSurfaces = lSurfaces.iterator();
					while (iSurfaces.hasNext()) {
						Element aSurface = (Element) iSurfaces.next();
					
						if (aSurface.getName().equals(SEDMLTags.OUTPUT_SURFACE)) {
							Surface s = getSurface(aSurface);
							p3d.addSurface(s);
						}
					}
				}
			}
			// notes and annotations
			addNotesAndAnnotation(p3d, outputElement);

			return p3d;
		} else if (outputElement.getName().equals(SEDMLTags.OUTPUT_REPORT)) { // ex:
			// "report"
			Report r = new Report(outputElement
					.getAttributeValue(SEDMLTags.OUTPUT_ID), outputElement
					.getAttributeValue(SEDMLTags.OUTPUT_NAME));
			List<Element> lReportChildren = outputElement.getChildren();
			Iterator<Element> iReportChildren = lReportChildren.iterator();
			while (iReportChildren.hasNext()) {
				Element eReportDChild = (Element) iReportChildren.next();
		
				// "listOfDataSets"
				if (eReportDChild.getName().equals(
						SEDMLTags.OUTPUT_DATASETS_LIST)) {
					List<Element> lDataSets = eReportDChild.getChildren();
					Iterator<Element> iDataSets = lDataSets.iterator();
					while (iDataSets.hasNext()) {
						Element aDataSet = (Element) iDataSets.next();
				
						if (aDataSet.getName().equals(SEDMLTags.OUTPUT_DATASET)) {
							DataSet ds = getDataset(aDataSet);
							r.addDataSet(ds);
						}
					}
				}
			}
			// notes and annotations
			addNotesAndAnnotation(r, outputElement);

			return r;
		}
		return null;
	}

	DataSet getDataset(Element aDataSet) {
		DataSet ds = new DataSet(aDataSet
				.getAttributeValue(SEDMLTags.OUTPUT_ID), aDataSet
				.getAttributeValue(SEDMLTags.OUTPUT_NAME), aDataSet
				.getAttributeValue(SEDMLTags.OUTPUT_DATASET_LABEL), aDataSet
				.getAttributeValue(SEDMLTags.OUTPUT_DATA_REFERENCE));
		addNotesAndAnnotation(ds, aDataSet);
		return ds;
	}

	Surface getSurface(Element aSurface) {
		Surface s = new Surface(
				aSurface.getAttributeValue(SEDMLTags.OUTPUT_ID), aSurface
						.getAttributeValue(SEDMLTags.OUTPUT_NAME), Boolean
						.parseBoolean(aSurface
								.getAttributeValue(SEDMLTags.OUTPUT_LOG_X)),
				Boolean.parseBoolean(aSurface
						.getAttributeValue(SEDMLTags.OUTPUT_LOG_Y)), Boolean
						.parseBoolean(aSurface
								.getAttributeValue(SEDMLTags.OUTPUT_LOG_Z)),
				aSurface.getAttributeValue(SEDMLTags.OUTPUT_DATA_REFERENCE_X),
				aSurface.getAttributeValue(SEDMLTags.OUTPUT_DATA_REFERENCE_Y),
				aSurface.getAttributeValue(SEDMLTags.OUTPUT_DATA_REFERENCE_Z));
		addNotesAndAnnotation(s, aSurface);
		return s;
	}

	Curve getCurve(Element aCurve) {

		Curve c = new Curve(aCurve.getAttributeValue(SEDMLTags.OUTPUT_ID),
				aCurve.getAttributeValue(SEDMLTags.OUTPUT_NAME), Boolean
						.parseBoolean(aCurve
								.getAttributeValue(SEDMLTags.OUTPUT_LOG_X)),
				Boolean.parseBoolean(aCurve
						.getAttributeValue(SEDMLTags.OUTPUT_LOG_Y)), aCurve
						.getAttributeValue(SEDMLTags.OUTPUT_DATA_REFERENCE_X),
				aCurve.getAttributeValue(SEDMLTags.OUTPUT_DATA_REFERENCE_Y));
		addNotesAndAnnotation(c, aCurve);
		return c;

	}

	List<Element> getNewXML(Element newXMLElement) {
		List<Element> rc = new ArrayList<Element>();
		int numEl = newXMLElement.getChildren().size();
		for (int i = 0; i < numEl; i++) {
			rc.add((Element) ((Element) newXMLElement.getChildren().get(0))
					.detach());
		}
		return rc;
	}

	Annotation getAnnotation(Element annotationElement) {
		return new Annotation((Element) ((Element)annotationElement.getChildren().get(0)).detach());
	}

	Notes getNotes(Element noteElement) {
		if (noteElement.getChildren().size() > 0) {
			return new Notes((Element) ((Element) noteElement.getChildren()
					.get(0)).detach());
		}
		return null;
	}

	/*
	 * returns a SedML model given an Element of xml for a complete model non -
	 * api method
	 */
	public SedML getSedDocument(Element sedRoot) throws XMLException {
		SedML sedDoc = null;
		SymbolRegistry.getInstance().addSymbolFactory(new SedMLSymbolFactory());
		try {
			Namespace sedNS = sedRoot.getNamespace();
			String verStr = sedRoot.getAttributeValue(SEDMLTags.VERSION_TAG);
			String lvlStr = sedRoot.getAttributeValue(SEDMLTags.LEVEL_TAG);
			if (verStr != null && lvlStr != null) {
				int sedVersion = Integer.parseInt(verStr);
				int sedLevel = Integer.parseInt(lvlStr);
				sedDoc = new SedML(sedLevel, sedVersion, sedNS);
			} else {
				sedDoc = new SedML(sedNS);
			}

			// Get additional namespaces if mentioned : mathml, sbml, etc.
			List additionalNamespaces = sedRoot.getAdditionalNamespaces();
			sedDoc.setAdditionalNamespaces(additionalNamespaces);

			// notes and annotations
			addNotesAndAnnotation(sedDoc, sedRoot);
			Iterator<Element> elementsIter = null;
			// models
			Element el = sedRoot.getChild(SEDMLTags.MODELS, sedNS);
			if (el != null) {
				List elementsList = el.getChildren();

				elementsIter = elementsList.iterator();

				while (elementsIter.hasNext()) {
					Element modelElement = elementsIter.next();
					if (modelElement.getName().equals(SEDMLTags.MODEL_TAG)) {
						sedDoc.addModel(getModel(modelElement));
					}
				}
			}

			// simulations
			el = sedRoot.getChild(SEDMLTags.SIMS, sedNS);
			if (el != null) {
				List elementsList = el.getChildren();
				elementsIter = elementsList.iterator();
				while (elementsIter.hasNext()) {
					Element simElement = elementsIter.next();
					sedDoc.addSimulation(getSimulation(simElement));
				}
			}

			el = sedRoot.getChild(SEDMLTags.TASKS, sedNS);
			if (el != null) {
				List elementsList = el.getChildren();
				elementsIter = elementsList.iterator();
				while (elementsIter.hasNext()) {
					Element taskElement = elementsIter.next();
					if (taskElement.getName().equals(SEDMLTags.TASK_TAG)) {
						sedDoc.addTask(getTask(taskElement));
					} else if(taskElement.getName().equals(SEDMLTags.REPEATED_TASK_TAG)) {
					    sedDoc.addTask(getRepeatedTask(taskElement));					
					}
				}
			}

			el = sedRoot.getChild(SEDMLTags.DATAGENERATORS, sedNS);
			if (el != null) {
				List elementsList = el.getChildren();
				elementsIter = elementsList.iterator();
				while (elementsIter.hasNext()) {
					Element dataGenElement = elementsIter.next();
					if (dataGenElement.getName().equals(SEDMLTags.DATAGENERATOR_TAG)) {
						sedDoc.addDataGenerator(getDataGenerator(dataGenElement));
					}
				}
			}

			el = sedRoot.getChild(SEDMLTags.OUTPUTS, sedNS);
			if (el != null) {
				List elementsList = el.getChildren();
				elementsIter = elementsList.iterator();
				while (elementsIter.hasNext()) {
					Element outputElement = elementsIter.next();
					if (outputElement.getName().equals(SEDMLTags.OUTPUT_P2D)
						|| outputElement.getName().equals(SEDMLTags.OUTPUT_P3D)
						|| outputElement.getName().equals(SEDMLTags.OUTPUT_REPORT)) {
						    sedDoc.addOutput(getOutput(outputElement));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new XMLException("Error loading sed-ml document : "
					+ e.getMessage());
			// return sedDoc;
		}
		return sedDoc;
	}
	
	public static SedML readFile (File file) throws JDOMException, IOException, XMLException{
	    SAXBuilder builder = new SAXBuilder();
	    Document doc = builder.build(file);
	    Element sedRoot = doc.getRootElement();
	    try {
	        SEDMLElementFactory.getInstance().setStrictCreation(false);
	        SEDMLReader reader = new SEDMLReader();
	        SedML sedML = reader.getSedDocument(sedRoot);
	        return sedML;
	    } finally {
	        SEDMLElementFactory.getInstance().setStrictCreation(true);
	    }
	}

}
