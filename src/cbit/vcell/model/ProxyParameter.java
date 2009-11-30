package cbit.vcell.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolProxy;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

public abstract class ProxyParameter extends Parameter implements SymbolProxy {
	
	private SymbolTableEntry target = null;
	
	public class ProxyPropertyChangeListener implements PropertyChangeListener {
		//
		// refire property change events locally.
		//
		public void propertyChange(PropertyChangeEvent evt) {
			targetPropertyChange(evt);
		}
	};

	public ProxyParameter(SymbolTableEntry targetSymbolTableEntry){
		this.target = targetSymbolTableEntry;
		try {
			Method addPropertyChangeListenerMethod = target.getClass().getMethod("addPropertyChangeListener", PropertyChangeListener.class);
			if (addPropertyChangeListenerMethod!=null){
				addPropertyChangeListenerMethod.invoke(target, new ProxyPropertyChangeListener());
			}
		}catch (NoSuchMethodException e1){
			//System.err.println(e1.getMessage());
		}catch (IllegalAccessException e2){
		}catch (InvocationTargetException e3){
		}
	}

	public final double getConstantValue() throws ExpressionException {
		return target.getConstantValue();
	}

/**
 * targetPropertyChange : recasting and throwing PropertyChangeEvent - can be overridden for specific behavior in implementor/extender,
 * e.g., in KineticsProxyParameter.
 * @param evt
 */
	public void targetPropertyChange(PropertyChangeEvent evt) {
		PropertyChangeEvent pce = new PropertyChangeEvent(this,evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		pce.setPropagationId(evt.getPropagationId());
		firePropertyChange(pce);
	}
	
	public final Expression getExpression() {
		try {
			return target.getExpression();
		}catch (ExpressionException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}

	public final String getName() {
		return target.getName();
	}

	public final VCUnitDefinition getUnitDefinition() {
		return target.getUnitDefinition();
	}

	public String getDescription() {
		if (target instanceof EditableSymbolTableEntry){
			return ((EditableSymbolTableEntry)target).getDescription();
		}else{
			return null;
		}
	}

	public final boolean isExpressionEditable() {
		if (target instanceof EditableSymbolTableEntry){
			return ((EditableSymbolTableEntry)target).isExpressionEditable();
		}else{
			return false;
		}
	}

	public final boolean isDescriptionEditable() {
		if (target instanceof EditableSymbolTableEntry){
			return ((EditableSymbolTableEntry)target).isDescriptionEditable();
		}else{
			return false;
		}
	}

	public final boolean isNameEditable() {
		if (target instanceof EditableSymbolTableEntry){
			return ((EditableSymbolTableEntry)target).isNameEditable();
		}else{
			return false;
		}
	}

	public final boolean isUnitEditable() {
		if (target instanceof EditableSymbolTableEntry){
			return ((EditableSymbolTableEntry)target).isUnitEditable();
		}else{
			return false;
		}
	}

	public final int getIndex() {
		return -1;
	}
	
	public final SymbolTableEntry getTarget(){
		return target;
	}

	public final void setDescription(String description){
		if (target instanceof Parameter){
			((Parameter)target).setDescription(description);
		}else{
			throw new RuntimeException("ProxyParameter.setDescription(): cannot set description on target symbol '"+getName()+"'");
		}
	}
	
	public final void setExpression(Expression expression) throws PropertyVetoException, ExpressionBindingException {
		if (target instanceof EditableSymbolTableEntry){
			((EditableSymbolTableEntry)target).setExpression(expression);
		}else{
			throw new RuntimeException("ProxyParameter.setExpression(): cannot set expression on target symbol '"+getName()+"'");
		}
	}

	public final void setUnitDefinition(VCUnitDefinition unitDefinition) throws PropertyVetoException {
		if (target instanceof EditableSymbolTableEntry){
			((EditableSymbolTableEntry)target).setUnitDefinition(unitDefinition);
		}else{
			throw new RuntimeException("ProxyParameter.setUnitDefinition(): cannot set unit on target symbol '"+getName()+"'");
		}
	}

	public final void setName(String name) throws PropertyVetoException{
		if (target instanceof EditableSymbolTableEntry){
			((EditableSymbolTableEntry)target).setName(name);
		}else{
			throw new RuntimeException("ProxyParameter.setName(): cannot set name on target symbol '"+getName()+"'");
		}
	}

}
