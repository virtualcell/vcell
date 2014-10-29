/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;

import cbit.vcell.data.DataSymbol;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;

@SuppressWarnings("serial")
public class MicroscopeMeasurement implements Serializable, Matchable, IssueSource  {
	
	public static final String CONVOLUTION_KERNEL_PROPERTYNAME = "convolutionKernel";
	public static final String FLUORESCENT_SPECIES_PROPERTYNAME = "fluorescentSpecies";
	
	private String name = "simFluor";
	private ArrayList<SpeciesContext> fluorescentSpecies = new ArrayList<SpeciesContext>();
	private ConvolutionKernel convolutionKernel = null;
	private transient PropertyChangeSupport propertyChangeSupport = null;
	private SimulationContext simulationContext;

	public static abstract class ConvolutionKernel implements Serializable, Matchable {
	}

	public static class GaussianConvolutionKernel extends ConvolutionKernel {
		private Expression sigmaXY_um = new Expression(0.3);
		private Expression sigmaZ_um = new Expression(1.5); 
 
		public GaussianConvolutionKernel() {
		}
		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof GaussianConvolutionKernel)) {
				return false;
			}
			GaussianConvolutionKernel gck = (GaussianConvolutionKernel)obj;
			if (!Compare.isEqualOrNull(sigmaXY_um, gck.sigmaXY_um)) {
				return false;
			}
			if (!Compare.isEqualOrNull(sigmaZ_um, gck.sigmaZ_um)) {
				return false;
			}
			return false;
		}

		public GaussianConvolutionKernel(Expression sigmaXY_um, Expression sigmaZ_um) {
			super();
			this.sigmaXY_um = sigmaXY_um;
			this.sigmaZ_um = sigmaZ_um;
		}

		public final Expression getSigmaXY_um() {
			return sigmaXY_um;
		}

		public final Expression getSigmaZ_um() {
			return sigmaZ_um;
		}
	}
	
	public static class ExperimentalPSF extends ConvolutionKernel {
		private DataSymbol psfDataSymbol = null;
		public ExperimentalPSF(DataSymbol arg_psfDataSymbol){
			this.psfDataSymbol = arg_psfDataSymbol;
		}
		public void setPSFDataSymbol(DataSymbol argDataSymbol) {
			this.psfDataSymbol = argDataSymbol;
		}
		public DataSymbol getPSFDataSymbol(){
			return this.psfDataSymbol;
		}
		public boolean compareEqual(Matchable obj) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	public static class ProjectionZKernel extends ConvolutionKernel {

		public boolean compareEqual(Matchable obj) {
			// TODO Auto-generated method stub
			return true;
		}
	}
		
		
	public MicroscopeMeasurement(String argName, ConvolutionKernel argConvolutionKernel, SimulationContext simContext) {
		this.name = argName;
		this.convolutionKernel = argConvolutionKernel;
		simulationContext = simContext;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean compareEqual(Matchable object) {
		MicroscopeMeasurement microscopeMeasurement = null;
		if (!(object instanceof MicroscopeMeasurement)){
			return false;
		}
		
		microscopeMeasurement = (MicroscopeMeasurement)object;
			 
		if (!Compare.isEqual(getName(),microscopeMeasurement.getName())){
			return false;
		}
		if (!Compare.isEqual(getFluorescentSpecies(), microscopeMeasurement.getFluorescentSpecies())){
			return false;
		}
		
		if (!Compare.isEqualOrNull(convolutionKernel, microscopeMeasurement.convolutionKernel)){
			return false;
		}
		
		return true;
	}
	
	public ArrayList<SpeciesContext> getFluorescentSpecies(){
		return fluorescentSpecies;
	}

	public ConvolutionKernel getConvolutionKernel() {
		return convolutionKernel;
	}
	public void setConvolutionKernel(ConvolutionKernel argConvolutionKernel) {
		if(convolutionKernel == argConvolutionKernel) {
			return;
		}
		ConvolutionKernel oldValue = this.convolutionKernel;
		this.convolutionKernel = argConvolutionKernel;
		getPropertyChangeSupport().firePropertyChange(CONVOLUTION_KERNEL_PROPERTYNAME, oldValue, this.convolutionKernel);
	}

	private PropertyChangeSupport getPropertyChangeSupport(){
		if (propertyChangeSupport==null){
			propertyChangeSupport = new PropertyChangeSupport(this);
		}
		return propertyChangeSupport;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}
	
	public void addFluorescentSpecies(SpeciesContext speciesContext){
		ArrayList<SpeciesContext> oldValue = new ArrayList<SpeciesContext>(fluorescentSpecies);
		fluorescentSpecies.add(speciesContext);
		getPropertyChangeSupport().firePropertyChange(FLUORESCENT_SPECIES_PROPERTYNAME,oldValue,fluorescentSpecies);
	}

	public void removeFluorescentSpecies(SpeciesContext speciesContext){
		ArrayList<SpeciesContext> oldValue = new ArrayList<SpeciesContext>(fluorescentSpecies);
		fluorescentSpecies.remove(speciesContext);
		getPropertyChangeSupport().firePropertyChange(FLUORESCENT_SPECIES_PROPERTYNAME,oldValue,fluorescentSpecies);
	}

	public boolean contains(SpeciesContext sc) {
		return fluorescentSpecies.contains(sc);
	}

	public void gatherIssues(IssueContext issueContext, List<Issue> issueVector) {
		issueContext = issueContext.newChildContext(ContextType.MicroscopyMeasurement,this);
		if (fluorescentSpecies.size() > 0) {
			if(getConvolutionKernel() instanceof ProjectionZKernel && simulationContext.getGeometry().getDimension() != 3) {
				Issue issue = new Issue(this, issueContext, IssueCategory.Microscope_Measurement_ProjectionZKernel_Geometry_3DWarning, "Z Projection is only supported in 3D spatial applications.", Issue.SEVERITY_ERROR);
				issueVector.add(issue);
			}
		}
	}

	public final SimulationContext getSimulationContext() {
		return simulationContext;
	}

}
