package cbit.vcell.mapping.spatial;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

import cbit.vcell.mapping.ApplicationQuantity;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityCategory;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.parser.NameScope;
import cbit.vcell.units.VCUnitDefinition;

public abstract class SpatialObject implements Serializable, IssueSource, Matchable {
	public static final String PROPERTY_NAME_NAME = "name";
	public static final String PROPERTY_NAME_SPATIALQUANTITIES = "spatialQuantities";
	public static final String PROPERTY_NAME_QUANTITYCATEGORIESENABLED = "quantityCategoriesEnabled";
	protected SpatialQuantity[] spatialQuantities;
	protected SimulationContext simulationContext;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoPropertyChange;
	
	public enum Dimension {
		Nondimensional,
		Length,
		Area,
		Volume,
		Velocity
	}
		
	private final QuantityCategory[] quantityCategories;
	private final Boolean[] quantityCategoryEnabled;
	private String name;
	
	public enum QuantityCategory {
		SurfaceSize("Surface Size","size","SurfaceSize",Dimension.Area),
		PointPosition("Point Position","pos","PointPosition",Dimension.Length),
		SurfaceVelocity("Surface Velocity","vel","SurfaceVelocity",Dimension.Velocity),
		PointVelocity("Point Velocity","vel","PointVelocity",Dimension.Velocity),
		Centroid("Volume Centroid","centroid","VolumeCentroid",Dimension.Length),
		Normal("Surface Normal","normal","SurfaceNormal",Dimension.Length), 
		VolumeSize("Volume Region Size","size","VolumeRegionSize",Dimension.Volume), 
		DistanceToSurface("Distance to Surface","distance","DistanceToSurface",Dimension.Length), 
		DirectionToSurface("Direction to Surface","direction","DirectionToSurface",Dimension.Length), 
		DistanceToPoint("Distance to Point","distance","DistanceToPoint",Dimension.Length), 
		DirectionToPoint("Direction to Point","direction","DirectionToPoint",Dimension.Length);
		
		public final String description;
		public final String varSuffix;
		public final String xmlName;
		public final Dimension dimension;
		
		QuantityCategory(String description, String varSuffix, String xmlName, Dimension dimension){
			this.description = description;
			this.varSuffix = varSuffix;
			this.xmlName = xmlName;
			this.dimension = dimension;
		}

		public static QuantityCategory fromXMLName(String quantityCategoryName) {
			for (QuantityCategory cat : values()){
				if (cat.xmlName.equals(quantityCategoryName)){
					return cat;
				}
			}
			return null;
		}
	}
	
	public enum QuantityComponent {
		Scalar(null,""),
		X("x component","X"),
		Y("y component","Y"),
		Z("z component","Z");
		
		public final String description;
		public final String componentSuffix;
		
		QuantityComponent(String description, String componentSuffix){
			this.description = description;
			this.componentSuffix = componentSuffix;
		}
	}
	
	public final class SpatialQuantity extends ApplicationQuantity {
		private final QuantityCategory quantityCategory;
		private final QuantityComponent quantityComponent;
		
		public SpatialQuantity(QuantityCategory category, QuantityComponent component) {
			super();
			this.quantityCategory = category;
			this.quantityComponent = component;
		}

		@Override
		public VCUnitDefinition getUnitDefinition() {
			if (SpatialObject.this.simulationContext==null){
				return null;
			}
			ModelUnitSystem unitSystem = simulationContext.getModel().getUnitSystem();
			switch (quantityCategory.dimension){
				case Area:{
					return unitSystem.getAreaUnit();
				}
				case Length:{
					return unitSystem.getLengthUnit();
				}
				case Nondimensional:{
					return unitSystem.getInstance_DIMENSIONLESS();
				}
				case Volume:{
					return unitSystem.getVolumeUnit();
				}
				case Velocity:{
					return unitSystem.getLengthUnit().divideBy(unitSystem.getTimeUnit());
				}
				default:{
					return unitSystem.getInstance_TBD();
				}
			}
		}

		@Override
		public final String getDescription() {
			if (quantityComponent.description!=null){
				return quantityCategory.description + " (" + quantityComponent.description + ")";
			}else{
				return quantityCategory.description;
			}
		};
		
		@Override
		public final String getName(){
			return SpatialObject.this.getName()+"_"+quantityCategory.varSuffix+quantityComponent.componentSuffix;
		}
		
		public final SpatialObject getSpatialObject(){
			return SpatialObject.this;
		}

		@Override
		public NameScope getNameScope() {
			if (simulationContext!=null){
				return simulationContext.getNameScope();
			}else{
				return null;
			}
		}

		public QuantityCategory getQuantityCategory() {
			return quantityCategory;
		}
		
		public QuantityComponent getQuantityComponent() {
			return quantityComponent;
		}

		public Boolean isEnabled() {
			return isQuantityCategoryEnabled(quantityCategory);
		}
		
	}
		
	
	protected SpatialObject(String name, SimulationContext simContext, QuantityCategory[] quantityCategories, Boolean[] quantityCategoriesEnabled){
		super();
		this.quantityCategories = quantityCategories;
		this.quantityCategoryEnabled = quantityCategoriesEnabled;
		this.simulationContext = simContext;
		this.name = name; 
	}
	
	protected SpatialObject(SpatialObject argSpatialObject, SimulationContext argSimContext) {
		super();
		this.simulationContext = argSimContext;
		this.name = argSpatialObject.getName();
		this.spatialQuantities = new SpatialQuantity[argSpatialObject.spatialQuantities.length];
		for (int i=0;i<argSpatialObject.spatialQuantities.length;i++){
			this.spatialQuantities[i] = new SpatialQuantity(argSpatialObject.spatialQuantities[i].quantityCategory,argSpatialObject.spatialQuantities[i].quantityComponent);
		}
		this.quantityCategories = argSpatialObject.quantityCategories.clone();
		this.quantityCategoryEnabled = argSpatialObject.quantityCategoryEnabled.clone();
	}

	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
		getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
	}

	protected java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}

	protected VetoableChangeSupport getVetoPropertyChange() {
		if (vetoPropertyChange == null) {
			vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
		};
		return vetoPropertyChange;
	}

	public void setName(String newValue) throws PropertyVetoException {
		String oldValue = name;
		fireVetoableChange(PROPERTY_NAME_NAME, oldValue, newValue);
		this.name = newValue;
		firePropertyChange(PROPERTY_NAME_NAME, oldValue, newValue);
	}

	public void gatherIssues(IssueContext issueContext, List<Issue> issueList){
		// look for negative times ... etc.
	}
	
	protected final boolean compareEqual0(Matchable obj) {
		if (obj instanceof SpatialObject){
			SpatialObject other = (SpatialObject)obj;
			if (!Compare.isEqual(getName(),other.getName())){
				return false;
			}
			return true;
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void vetoableChange(PropertyChangeEvent evt)	throws PropertyVetoException {
		if (evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_NAME)) {
			String newName = (String) evt.getNewValue();
			if (simulationContext.getSpatialObject(newName) != null) {
				throw new PropertyVetoException("An event with name '" + newName + "' already exists!",evt);
			}
			if (simulationContext.getEntry(newName)!=null){
				throw new PropertyVetoException("Cannot use existing symbol '" + newName + "' as an event name",evt);
			}
		}
	}
	
	public final SpatialQuantity getSpatialQuantity(String identifier) {
		for (SpatialQuantity spatialQuantity : getSpatialQuantities()){
			if (identifier.equals(spatialQuantity.getName())){
				return spatialQuantity;
			}
		}
		return null;
	}
	
	public final SpatialQuantity getSpatialQuantity(QuantityCategory quantityCategory, QuantityComponent quantityComponent) {
		for (SpatialQuantity spatialQuantity : getSpatialQuantities()){
			if (spatialQuantity.getQuantityCategory() == quantityCategory && spatialQuantity.getQuantityComponent() == quantityComponent){
				return spatialQuantity;
			}
		}
		return null;
	}
		
	public List<QuantityCategory> getQuantityCategories(){
		return Arrays.asList(quantityCategories);
	}
	
	public void setQuantityCategoryEnabled(QuantityCategory quantityCategory, boolean enabled){
		Boolean[] oldValue = this.quantityCategoryEnabled.clone();
		for (int i=0;i<this.quantityCategories.length;i++){
			if (this.quantityCategories[i] == quantityCategory){
				this.quantityCategoryEnabled[i] = enabled;
			}
		}
		firePropertyChange(PROPERTY_NAME_QUANTITYCATEGORIESENABLED, oldValue, this.quantityCategoryEnabled);
	}
	
	public boolean isQuantityCategoryEnabled(QuantityCategory quantityCategory){
		for (int i=0;i<this.quantityCategories.length;i++){
			if (this.quantityCategories[i] == quantityCategory){
				return this.quantityCategoryEnabled[i];
			}
		}
		return false;
	}

	public abstract String getDescription();

	/**
	 * property name PROPERTY_NAME_SPATIALQUANTITIES = "spatialQuantities"
	 * @return
	 */
	public abstract SpatialQuantity[] getSpatialQuantities();

	public void refreshDependencies() {
		// TODO Auto-generated method stub
	}

	public SimulationContext getSimulationContext() {
		return simulationContext;
	}
}
