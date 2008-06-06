package org.vcell.modelapp.physics;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Vector;

import jscl.plugin.ParseException;

import org.jdom.Element;
import org.vcell.expression.ExpressionException;
import org.vcell.modelapp.analysis.IAnalysisTask;
import org.vcell.modelapp.analysis.IAnalysisTaskFactory;
import org.vcell.physics.component.OOModel;
import org.vcell.physics.math.MappingUtilities;
import org.vcell.physics.math.MathSystem;
import org.vcell.physics.math.ModelAnalysisResults;
import org.vcell.physics.modelica.ModelicaModelWriter;
import org.vcell.util.Matchable;

import cbit.vcell.model.ModelInfo;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.physics.PhysicsMapping;

public class OOModelingTask implements IAnalysisTask {
	
	private SimulationContext simContext = null;
	private ModelAnalysisResults modelAnalysisResults = null;
	private String name = null;
	private String annotation = null;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub
	}

	public IAnalysisTask createClone(String newName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void gatherIssues(Vector issueList) {
		// TODO Auto-generated method stub
	}

	public ModelAnalysisResults getModelAnalysisResults(){
		return modelAnalysisResults;
	}
	
	public IAnalysisTaskFactory getAnalysisTaskFactory() {
		return new OOModelingTaskFactory();
	}

	public String getAnnotation() {
		return annotation;
	}

	public String getName() {
		return name;
	}

	public Element getXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasListeners(String propertyName) {
		// TODO Auto-generated method stub
		return false;
	}

	public void refreshDependencies() {
		// TODO Auto-generated method stub
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub
	}

	public void setAnnotation(String annotation) throws PropertyVetoException {
		this.annotation = annotation;
	}

	public void setName(String name) throws PropertyVetoException {
		this.name = name;
	}

	public boolean compareEqual(Matchable obj) {
		// TODO Auto-generated method stub
		return false;
	}

	public SimulationContext getSimContext() {
		return simContext;
	}

	public OOModelingTask(SimulationContext simContext, String name) throws ExpressionException, PropertyVetoException {
		super();
		this.simContext = simContext;
		this.name = name;
		this.modelAnalysisResults = new ModelAnalysisResults();
		OOModel ooModel = PhysicsMapping.createFromSimulationContext(simContext);
		System.out.println(new org.vcell.physics.component.ModelWriter().print(ooModel));
		try {
			MathSystem mathSystem = MappingUtilities.getMathSystem(ooModel);
			this.modelAnalysisResults = MappingUtilities.analyzeMathSystem(mathSystem);
			this.modelAnalysisResults.oOModel = ooModel;
			try {
				this.modelAnalysisResults.modelicaModelText = new ModelicaModelWriter().write(ooModel);
			}catch (Exception e){
				System.out.println("OOModelingTask.OOModelingTask() ... modelica generation failed");
				e.printStackTrace(System.out);
			}
		}catch (ParseException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
		
	}

}
