package cbit.vcell.mapping;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;

import cbit.vcell.data.DataSymbol;
import cbit.vcell.mapping.gui.FluorescenceSpeciesContextListModel;
import cbit.vcell.mapping.gui.SpeciesFluorescenceSpec;
import cbit.vcell.model.Model;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;

public class MicroscopeMeasurement implements Serializable  {
	
	public static final String CONVOLUTION_KERNEL_PROPERTYNAME = "convolutionKernel";
	public static final String FLUORESCENT_SPECIES_PROPERTYNAME = "fluorescentSpecies";
	
	private String name = "simFluor";
	private ArrayList<SpeciesContext> fluorescentSpecies = new ArrayList<SpeciesContext>();
	private ConvolutionKernel convolutionKernel = null;
	private transient PropertyChangeSupport propertyChangeSupport = null;
	

	public static abstract class ConvolutionKernel {
	}
	
	public static class ExperimentalPSF extends ConvolutionKernel {
		private DataSymbol psfDataSymbol = null;
		public ExperimentalPSF(DataSymbol arg_psfDataSymbol){
			this.psfDataSymbol = arg_psfDataSymbol;
		}
		public void setDataSymbol(DataSymbol argDataSymbol) {
			this.psfDataSymbol = argDataSymbol;
		}
		public DataSymbol getPSFDataSymbol(){
			return this.psfDataSymbol;
		}
	}
	
	public static class ProjectionZKernel extends ConvolutionKernel {
	}
	
	
	public MicroscopeMeasurement(String argName, ExperimentalPSF argConvolutionKernel, Expression argFluorescentMoleculeExpression) {
		this.name = argName;
		this.convolutionKernel = argConvolutionKernel;
	}
	public MicroscopeMeasurement(String argName, ProjectionZKernel argConvolutionKernel, Expression argFluorescentMoleculeExpression) {
		this.name = argName;
		this.convolutionKernel = argConvolutionKernel;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<SpeciesContext> getFluorescentSpecies(){
		return fluorescentSpecies;
	}
	public void update(Model model){
		
	}
	public ConvolutionKernel getConvolutionKernel() {
		return convolutionKernel;
	}
	public void setConvolutionKernel(ConvolutionKernel convolutionKernel) {
		ConvolutionKernel oldValue = this.convolutionKernel;
		this.convolutionKernel = convolutionKernel;
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

}
