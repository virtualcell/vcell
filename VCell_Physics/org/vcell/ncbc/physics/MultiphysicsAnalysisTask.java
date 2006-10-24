package org.vcell.ncbc.physics;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Vector;

import org.jdom.Element;
import org.vcell.expression.ExpressionException;
import org.vcell.modelapp.analysis.IAnalysisTask;
import org.vcell.modelapp.analysis.IAnalysisTaskFactory;
import org.vcell.ncbc.physics.component.PhysicalModel;

import cbit.image.ImageException;
import cbit.util.Matchable;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.modelapp.SimulationContext;

public class MultiphysicsAnalysisTask implements IAnalysisTask {
	
	private SimulationContext simContext = null;
	private PhysicalModel physicalModel = null;
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

	public PhysicalModel getPhysicalModel(){
		return physicalModel;
	}
	
	public IAnalysisTaskFactory getAnalysisTaskFactory() {
		return new MultiphysicsAnalysisTaskFactory();
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

	public MultiphysicsAnalysisTask(SimulationContext simContext, String name) throws ExpressionException, ImageException, GeometryException, PropertyVetoException {
		super();
		this.simContext = simContext;
		this.name = name;
		this.physicalModel = org.vcell.ncbc.physics.engine.MappingUtilities.createFromSimulationContext(simContext);

	}

}
