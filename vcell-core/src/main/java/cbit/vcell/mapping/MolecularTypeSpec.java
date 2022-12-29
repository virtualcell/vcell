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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cbit.vcell.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.RbmElementAbstract;
import org.vcell.util.*;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.document.Identifiable;

import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.mapping.SimulationContext.Kind;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityCategory;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityComponent;
import cbit.vcell.mapping.spatial.SpatialObject.SpatialQuantity;
import cbit.vcell.mapping.spatial.VolumeRegionObject;
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.parser.AbstractNameScope;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;
import cbit.vcell.units.VCUnitDefinition;
import net.sourceforge.interval.ia_math.RealInterval;

@SuppressWarnings("serial")
public class MolecularTypeSpec implements Matchable, ScopedSymbolTable, Serializable, SimulationContextEntity, IssueSource,
	Identifiable, Displayable {

	private final static Logger logger = LogManager.getLogger(MolecularTypeSpec.class);

	public static final String PARAMETER_NAME_PROXY_PARAMETERS = "proxyParameters";

	public class MolecularTypeSpecNameScope extends BioNameScope {
		private final NameScope children[] = new NameScope[0]; // always empty
		public MolecularTypeSpecNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			return children;
		}
		public String getName() {
			return MolecularTypeSpec.this.getMolecularType().getName()+"_mts";
		}
		public NameScope getParent() {
			return null;
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return MolecularTypeSpec.this;
		}
		@Override
		public String getPathDescription() {
			if (simulationContext != null){
				return "App("+simulationContext.getName()+") / MolecularType("+getMolecularType().getName()+")";
			}
			return null;
		}
		@Override
		public NamescopeType getNamescopeType() {
			return null;
		}
	}
	
	public class MolecularTypeSpecParameter extends Parameter implements Identifiable, ExpressionContainer, IssueSource {
		private Expression fieldParameterExpression = null;
		private String fieldParameterName = null;

		public MolecularTypeSpecParameter(String parmName, Expression argExpression, int argRole, VCUnitDefinition argUnitDefinition, String argDescription) {
			super();
			fieldParameterName = parmName;
			fieldParameterExpression = argExpression;
			setDescription(argDescription);
		}

		public MolecularType getMolecularType() {
			return MolecularTypeSpec.this.molecularType;
		}

		public MolecularTypeSpec getMolecularTypeSpec() {
			return MolecularTypeSpec.this;
		}


		@Override
		public boolean compareEqual(Matchable obj) {
			// TODO: implement this
			return true;
		}


		@Override
		public boolean relate(Relatable obj, RelationVisitor rv) {
			// TODO: implement this
			return true;
		}

		public NameScope getNameScope(){
			return MolecularTypeSpec.this.getNameScope();
		}

		public boolean isExpressionEditable(){
			return true;
		}

		@Override
		public String getDescription() {
			return super.getDescription() + " for "+getMolecularType().getName();
		}
		
		public boolean isUnitEditable(){
			return false;
		}

		public boolean isNameEditable(){
			return false;
		}

		public void setExpression(Expression expression) throws ExpressionBindingException {
			if (expression!=null){
				expression = new Expression(expression);
				// Need to bind this expression, but changing the binding from MolecularTypeSpec.this to a symbolTable created on the fly.
				// This new symbolTable is this MolecularTypeSpec, but omits its parameters, so that the MolecularTypeSpecParameter
				// expression that is being bound cannot contain other molecularTypeSpecParameters in its expression.
				expression.bindExpression(new SymbolTable() {
					public SymbolTableEntry getEntry(String identifierString) {
						SymbolTableEntry ste = MolecularTypeSpec.this.getEntry(identifierString);
						if (ste instanceof MolecularTypeSpecParameter) {
							throw new RuntimeException("\nCannot use a molecularTypeSpec parameter (e.g., diff, initConc, Vel_X, etc.) in another molecularTypeSpec parameter expression.");
						}
						return ste;
					}

					public void getEntries(Map<String, SymbolTableEntry> entryMap) {
						MolecularTypeSpec.this.getEntries(entryMap);
					}
				});
			}
			Expression oldValue = fieldParameterExpression;
			fieldParameterExpression = expression;

			super.firePropertyChange("expression", oldValue, expression);
		}
		
		/**
		 * return 0 if {@link #getRole()} initial concentration, diffusion rate, or initial count
		 */
		@Override
		public Expression getDefaultExpression() {
			return new Expression(0);
		}

		public double getConstantValue() throws ExpressionException {
			return fieldParameterExpression.evaluateConstant();
		}
		
		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = fieldParameterName;
			super.fireVetoableChange("name", oldValue, name);
			fieldParameterName = name;
			super.firePropertyChange("name", oldValue, name);
		}
		
		public String getName(){
			return fieldParameterName;
		}
		
		public Expression getExpression(){
			return fieldParameterExpression;
		}
		
		public int getIndex() {
			return -1;
		}

		@Override
		public void setUnitDefinition(VCUnitDefinition unit) throws PropertyVetoException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public VCUnitDefinition getUnitDefinition() {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
	public class MolecularTypeSpecProxyParameter extends ProxyParameter {

		public MolecularTypeSpecProxyParameter(SymbolTableEntry target){
			super(target);
		}
		
		public NameScope getNameScope(){
			return MolecularTypeSpec.this.getNameScope();
		}

		@Override
		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof MolecularTypeSpecProxyParameter)){
				return false;
			}
			MolecularTypeSpecProxyParameter other = (MolecularTypeSpecProxyParameter)obj;
			if (getTarget() instanceof Matchable && other.getTarget() instanceof Matchable &&
					Compare.isEqual((Matchable)getTarget(), (Matchable)other.getTarget())){
				return true;
			}else{
				return false;
			}
		}


		@Override
		public boolean relate(Relatable obj, RelationVisitor rv) {
			if (!(obj instanceof MolecularTypeSpecProxyParameter)){
				return false;
			}
			MolecularTypeSpecProxyParameter other = (MolecularTypeSpecProxyParameter)obj;
			if (getTarget() instanceof Relatable && other.getTarget() instanceof Relatable &&
					((Relatable)getTarget()).relate((Relatable)other.getTarget(), rv)){
				return true;
			}else{
				return false;
			}
		}


		@Override
		public String getDescription() {
			if (getTarget() instanceof MolecularType) {
				return "Species Concentration";
			} else {
				return super.getDescription();
			}
		}

		@Override
		public void targetPropertyChange(PropertyChangeEvent evt) {
			super.targetPropertyChange(evt);
		}
	}

	
	private MolecularType molecularType = null;

	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private MolecularTypeSpecParameter[] fieldParameters = null;
	private MolecularTypeSpecProxyParameter[] fieldProxyParameters = new MolecularTypeSpecProxyParameter[0];
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient SimulationContext simulationContext = null;
//	private MoleculartypeSpecNameScope nameScope = new MolecularTypeSpecNameScope();
	

	private static RealInterval[] parameterBounds = {
		new RealInterval(0.0, Double.POSITIVE_INFINITY), // init concentration
		new RealInterval(0.0, Double.POSITIVE_INFINITY), // diff rate
		null,	// BC X-
		null,	// BC X+
		null,	// BC Y-
		null,	// BC Y+
		null,	// BC Z-
		null,	// BC Z+
		new RealInterval(0.0, Double.POSITIVE_INFINITY), // init amount
		null, // Velocity X
		null, // Velocity Y
		null  // Velocity Z
	};

public MolecularTypeSpec(MolecularTypeSpec molecularTypeSpec, SimulationContext argSimulationContext) {
	this.molecularType = molecularTypeSpec.molecularType;
	this.simulationContext = argSimulationContext;
	fieldParameters = new MolecularTypeSpecParameter[molecularTypeSpec.fieldParameters.length];
	refreshDependencies();
}            


private void refreshDependencies() {
	// TODO Auto-generated method stub
	
}


public MolecularTypeSpec(MolecularType molecularType, SimulationContext argSimulationContext) {
	this.molecularType = molecularType;
	this.simulationContext = argSimulationContext;
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


private RbmElementAbstract getPropertyChange() {
	// TODO Auto-generated method stub
	return null;
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}

private RbmElementAbstract getVetoPropertyChange() {
	// TODO Auto-generated method stub
	return null;
}


/**
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(Matchable object) {

	MolecularTypeSpec scs = null;
	if (!(object instanceof MolecularTypeSpec)){
		return false;
	}
	scs = (MolecularTypeSpec)object;

	if (!Compare.isEqual(molecularType,scs.molecularType)){
		return false;
	}

// TODO: more

	return true;
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	// TODO: stub
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, int oldValue, int newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, boolean oldValue, boolean newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 10:03:46 AM)
 * @param issueVector java.util.Vector
 */
public void gatherIssues(IssueContext issueContext, List<Issue> issueVector) {

}


public SymbolTableEntry getEntry(String identifierString) {
	
	SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
			
	ste = getNameScope().getExternalEntry(identifierString,this);

	if (ste instanceof SymbolTableFunctionEntry){
		return ste;
	}
	
	if (ste!=null){
		if (ste instanceof SymbolTableFunctionEntry){
			return ste;
		}
	}
	
	return null;
}

public MolecularType getMolecularType() {
	return molecularType;
}

@Override
public Kind getSimulationContextKind() {
	return SimulationContext.Kind.SPECIFICATIONS_KIND;
}


@Override
public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	// TODO Auto-generated method stub
	
}


@Override
public SymbolTableEntry getLocalEntry(String identifier) {
	// TODO Auto-generated method stub
	return null;
}


@Override
public NameScope getNameScope() {
	// TODO Auto-generated method stub
	return null;
}


@Override
public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {
	// TODO Auto-generated method stub
	
}

public static final String typeName = "MolecularTypeSpec";
@Override
public String getDisplayName() {
	if(getMolecularType() != null) {
		return getMolecularType().getDisplayName();
	}
	return("?");
}


@Override
public String getDisplayType() {
	return typeName;
}


}
