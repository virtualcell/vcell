package cbit.vcell.mapping.spatial.processes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

import cbit.vcell.mapping.ParameterContext;
import cbit.vcell.mapping.ParameterContext.GlobalParameterContext;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.ParameterContext.ParameterPolicy;
import cbit.vcell.mapping.ParameterContext.ParameterRoleEnum;
import cbit.vcell.mapping.SimulationContext.Kind;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContextEntity;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SpatialObject.SpatialQuantity;
import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.Model;
import cbit.vcell.model.Parameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.units.UnitSystemProvider;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;
import net.sourceforge.interval.ia_math.RealInterval;

public abstract class SpatialProcess implements Serializable, IssueSource, PropertyChangeListener, Matchable, SimulationContextEntity {
	private static final String PROPERTY_NAME_NAME = "name";
	public static final String PROPERTY_NAME_PARAMETERS = "parameters";
	
		
	public class SpatialProcessNameScope extends BioNameScope {
		private final NameScope children[] = new NameScope[0]; // always empty
		public SpatialProcessNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return SpatialProcess.this.getName();
		}
		public NameScope getParent() {
			if (SpatialProcess.this.simulationContext != null){
				return SpatialProcess.this.simulationContext.getNameScope();
			}else{
				return null;
			}
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return SpatialProcess.this.parameterContext;
		}
		public String getPathDescription() {
			if (simulationContext != null){
				return "/App(\""+simulationContext.getName()+"\")/SpatialProcess(\""+SpatialProcess.this.getName()+"\")";
			}
			return null;
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.spatialProcessType;
		}
	}
	

	public static enum SpatialProcessParameterType implements ParameterRoleEnum {

		PointInitialPositionX(0,"PointInitialPositionX","initPosX","initial point position (x coord)"),
		PointInitialPositionY(1,"PointInitialPositionY","initPosY","initial point position (y coord)"),
		PointInitialPositionZ(2,"PointInitialPositionZ","initPosZ","initial point position (z coord)"),
		
		PointPositionX(3,"PointPositionX","posX","point position (x coord)"),
		PointPositionY(4,"PointPositionY","posY","point position (y coord)"),
		PointPositionZ(5,"PointPositionZ","posZ","point position (z coord)"),
		
		PointVelocityX(6,"PointVelocityX","velX","point velocity (x coord)"),
		PointVelocityY(7,"PointVelocityY","velY","point velocity (y coord)"),
		PointVelocityZ(8,"PointVelocityZ","velZ","point velocity (z coord)"),
		
		SurfaceVelocityX(9,"SurfaceVelocityX","velX","surface velocity (x coord)"),
		SurfaceVelocityY(10,"SurfaceVelocityY","velY","surface velocity (y coord)"),
		SurfaceVelocityZ(11,"SurfaceVelocityZ","velZ","surface velocity (z coord)"),
				
		Viscosity(12,"Viscosity","viscosity","viscosity"),
		ExternalDrag(13,"ExternalDrag","viscosity","viscosity"),
		ActiveStressX(14,"ActiveStressX","activeStressX","externally applied active stress (x coord)"),
		ActiveStressY(15,"ActiveStressY","activeStressY","externally applied active stress (y coord)"),
		ActiveStressZ(16,"ActiveStressZ","activeStressZ","externally applied active stress (z coord)"),
		
		InternalVelocityX(17,"InternalVelocityX","velX","internal velocity (x coord)"),
		InternalVelocityY(18,"InternalVelocityY","velY","internal velocity (y coord)"),
		InternalVelocityZ(19,"InternalVelocityZ","velZ","internal velocity (z coord)"),
		
		UserDefined(20, "UserDefined", null,"user defined");
		
		private final int role;
		private final String roleXmlName;
		private final String defaultName;
		private final String description;
		
		private SpatialProcessParameterType(int role,String roleXmlName,String defaultName,String description){
			this.role = role;
			this.roleXmlName = roleXmlName;
			this.defaultName = defaultName;
			this.description = description;
		}
		
		public int getRole(){
			return role;
		}
	
		public String getRoleXmlName(){
			return roleXmlName;
		}
	
		public String getDefaultName() {
			return defaultName;
		}
	
		public String getDescription() {
			return description;
		}
		
		public static SpatialProcessParameterType fromRole(int role){
			for (SpatialProcessParameterType type : values()){
				if (type.getRole()==role){
					return type;
				}
			}
			return null;
		}

		public static SpatialProcessParameterType fromRoleXmlName(String roleStr) {
			for (SpatialProcessParameterType type : values()){
				if (type.getRoleXmlName().equals(roleStr)){
					return type;
				}
			}
			return null;
		}
	}

	private class ParameterContextSettings implements Serializable, ParameterPolicy, UnitSystemProvider, GlobalParameterContext {

		@Override /* ParameterPolicy */
		public boolean isUserDefined(LocalParameter localParameter) {
			return (localParameter.getRole() == SpatialProcessParameterType.UserDefined);
		}

		@Override /* ParameterPolicy */
		public boolean isExpressionEditable(LocalParameter localParameter) {
			return (localParameter.getExpression()!=null);
		}

		@Override /* ParameterPolicy */
		public boolean isNameEditable(LocalParameter localParameter) {
			return true;
		}

		@Override /* ParameterPolicy */
		public boolean isUnitEditable(LocalParameter localParameter) {
			return isUserDefined(localParameter);
		}
		
		@Override /* UnitSystemProvider */
		public VCUnitSystem getUnitSystem() {
			return getSimulationContext().getModel().getUnitSystem();
		}
		
		@Override /* GlobalParameterContext */
		public ScopedSymbolTable getSymbolTable() {
			return getSimulationContext().getModel();
		}
		
		@Override /* GlobalParameterContext */
		public Parameter getParameter(String name) {
			return getSimulationContext().getModel().getModelParameter(name);
		}
		
		@Override /* GlobalParameterContext */
		public Parameter addParameter(String name, Expression exp, VCUnitDefinition unit) throws PropertyVetoException {
			Model model = getSimulationContext().getModel();
			return model.addModelParameter(model.new ModelParameter(name, exp, Model.ROLE_UserDefined, unit));
		}

		@Override
		public ParameterRoleEnum getUserDefinedRole() {
			return SpatialProcessParameterType.UserDefined;
		}

		@Override
		public IssueSource getIssueSource() {
			return SpatialProcess.this;
		}

		@Override
		public RealInterval getConstraintBounds(ParameterRoleEnum role) {
			// TODO Auto-generated method stub
			return null;
		}

	};
	
	private final ParameterContextSettings parameterContextSettings = new ParameterContextSettings();
	private SpatialProcessNameScope nameScope = new SpatialProcessNameScope();
	private final ParameterContext parameterContext;
	private String name;

	protected SimulationContext simulationContext = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoPropertyChange;
	
	
	protected SpatialProcess(String name, SimulationContext simContext){
		super();
		this.name = name; 
		this.simulationContext = simContext;
		this.parameterContext = new ParameterContext(getNameScope(),parameterContextSettings, parameterContextSettings);
		parameterContext.addPropertyChangeListener(this);
	}
	
	protected SpatialProcess(SpatialProcess argSpatialProcess, SimulationContext argSimContext) {
		this.simulationContext = argSimContext;
		this.name = argSpatialProcess.getName();
		this.parameterContext = new ParameterContext(getNameScope(),parameterContextSettings, parameterContextSettings);
		for (LocalParameter p : argSpatialProcess.parameterContext.getLocalParameters()){
			try {
				parameterContext.addLocalParameter(p.getName(), new Expression(p.getExpression()), p.getRole(), p.getUnitDefinition(), p.getDescription());
			} catch (PropertyVetoException | ExpressionBindingException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to copy parameters for SpatialProcess "+getName(),e);
			}
		}
	}

	public SimulationContext getSimulationContext() {
		return simulationContext;
	}

	public ScopedSymbolTable getScopedSymbolTable(){
		return parameterContext;
	}

	public void setName(String newValue) throws PropertyVetoException {
		String oldValue = name;
		fireVetoableChange(PROPERTY_NAME_NAME, oldValue, newValue);
		this.name = newValue;
		firePropertyChange(PROPERTY_NAME_NAME, oldValue, newValue);
	}

	public Expression getParameterValue(SpatialProcessParameterType parameterType) {
		if(parameterContext.getLocalParameterFromRole(parameterType) == null) {
			return null;
		}
		return parameterContext.getLocalParameterFromRole(parameterType).getExpression();
	}

	public LocalParameter getParameter(SpatialProcessParameterType parameterType) {
		return parameterContext.getLocalParameterFromRole(parameterType);
	}

	public SpatialProcessParameterType getParameterType(LocalParameter parameter) {
		if (!parameterContext.contains(parameter)){
			throw new RuntimeException("parameter "+parameter.getName()+" not found in bioEvent "+getName());
		}
		return (SpatialProcessParameterType)parameter.getRole();
	}

	public void setParameterValue(SpatialProcessParameterType parameterType, Expression expression) throws ExpressionBindingException, PropertyVetoException {
		parameterContext.getLocalParameterFromRole(parameterType).setExpression(expression);
	}
	
	public abstract void gatherIssues(IssueContext issueContext, List<Issue> issueList);
	
	protected final boolean compareEqual0(Matchable obj) {
		if (obj instanceof SpatialProcess){
			SpatialProcess other = (SpatialProcess)obj;
			if (!Compare.isEqual(getName(),other.getName())){
				return false;
			}
			if (!Compare.isEqual(parameterContext,  other.parameterContext)){
				return false;
			}
			return true;
		}
		return false;
	}

	public LocalParameter[] getParameters() {
		return parameterContext.getLocalParameters();
	}

	public Parameter[] getProxyParameters() {
		return parameterContext.getProxyParameters();
	}

	public Parameter[] getUnresolvedParameters() {
		return parameterContext.getUnresolvedParameters();
	}

	public void renameParameter(String name, String newName) throws ExpressionException, PropertyVetoException {
		parameterContext.renameLocalParameter(name, newName);
	}

	public void convertParameterType(Parameter param, boolean bConvertToGlobal) throws PropertyVetoException, ExpressionBindingException {
		if ((param instanceof LocalParameter) && ((LocalParameter)param).getRole() != SpatialProcessParameterType.UserDefined) {
			throw new RuntimeException("Cannot convert pre-defined local parameter : \'" + param.getName() + "\' to global parameter.");
		}

		parameterContext.convertParameterType(param, bConvertToGlobal, parameterContextSettings);
	}

	public void setParameterValue(LocalParameter parm, Expression exp, boolean autocreateLocalParameter) throws PropertyVetoException, ExpressionException {
		parameterContext.setParameterValue(parm, exp, autocreateLocalParameter);
	}

	public void resolveUndefinedUnits() {
		parameterContext.resolveUndefinedUnits();
	}

	public void refreshDependencies() {
		removePropertyChangeListener(this);
		addPropertyChangeListener(this);

		parameterContext.removePropertyChangeListener(this);
		parameterContext.addPropertyChangeListener(this);
		
		parameterContext.refreshDependencies();
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

	public void fireVetoableChange(String propertyName, Object oldValue, Object newValue)
			throws PropertyVetoException {
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

	public void bind() throws ExpressionBindingException {
		parameterContext.refreshDependencies();
	}
	
	public String getName() {
		return name;
	}

	public BioNameScope getNameScope() {
		return nameScope;
	}
	
	public void vetoableChange(PropertyChangeEvent evt)	throws PropertyVetoException {
		if (evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_NAME)) {
			String newName = (String) evt.getNewValue();
			if (simulationContext.getSpatialProcess(newName) != null) {
				throw new PropertyVetoException("A spatial process with name '" + newName + "' already exists!",evt);
			}
			if (simulationContext.getEntry(newName)!=null){
				throw new PropertyVetoException("Cannot use existing symbol '" + newName + "' as a spatial process name",evt);
			}
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
	}


	public LocalParameter createNewParameter(String paramName, SpatialProcessParameterType parameterType, Expression exp, VCUnitDefinition unit) {
		return parameterContext.new LocalParameter(paramName, exp, parameterType, unit, parameterType.description);
	}

	public void setParameters(LocalParameter[] parameters) throws PropertyVetoException, ExpressionBindingException {
		LocalParameter[] oldValue = parameterContext.getLocalParameters();
		parameterContext.setLocalParameters(parameters);
		firePropertyChange(PROPERTY_NAME_PARAMETERS, oldValue, parameterContext.getLocalParameters());
	}
	
	@Override
	public Kind getSimulationContextKind() {
		return SimulationContext.Kind.GEOMETRY_KIND;
	}

	public abstract String getDescription();

	public abstract List<SpatialObject> getSpatialObjects();
	
	public abstract List<SpatialQuantity> getReferencedSpatialQuantities();
}
