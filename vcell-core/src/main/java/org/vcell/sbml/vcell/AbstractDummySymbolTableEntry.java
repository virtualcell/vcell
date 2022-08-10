package org.vcell.sbml.vcell;

import cbit.vcell.parser.ExpressionException;
import cbit.vcell.units.VCUnitDefinition;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

public abstract class AbstractDummySymbolTableEntry implements SBMLSymbolMapping.DummySymbolTableEntry {

    @Override
    public boolean isExpressionEditable() {
        return true;
    }

    @Override
    public boolean isUnitEditable() {
        return false;
    }

    @Override
    public boolean isNameEditable() {
        return false;
    }

    @Override
    public void setName(String name) throws PropertyVetoException {
        throw new RuntimeException("name not editable");
    }

    @Override
    public void setUnitDefinition(VCUnitDefinition unit) throws PropertyVetoException {
        throw new RuntimeException("unit not editable");
    }

    @Override
    public String toString(){
        return super.toString() + getDescription();
    }

    @Override
    public void setDescription(String description) throws PropertyVetoException {
        throw new RuntimeException("name not editable");
    }

    @Override
    public boolean isDescriptionEditable() {
        return false;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        throw new RuntimeException("not supported");
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        throw new RuntimeException("not supported");
    }

    @Override
    public int getIndex() {
        return -1;
    }

    @Override
    public boolean isConstant() throws ExpressionException {
        return false;
    }
}

