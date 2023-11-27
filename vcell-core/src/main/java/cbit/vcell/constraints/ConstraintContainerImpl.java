/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.constraints;

import cbit.vcell.parser.SymbolUtils;
import net.sourceforge.interval.ia_math.RealInterval;
import org.vcell.util.ArrayUtils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeSupport;
import java.beans.VetoableChangeListener;
import java.util.HashMap;
import java.util.Map;

public class ConstraintContainerImpl {
    protected transient VetoableChangeSupport vetoableChangeSupport;
    protected transient PropertyChangeSupport propertyChangeSupport;
    private GeneralConstraint[] generalConstraints = new GeneralConstraint[0];
    private SimpleBounds[] simpleBounds = new SimpleBounds[0];
    private final Map<AbstractConstraint, ConstraintStatus> statusMap = new HashMap<>();

    private static class ConstraintStatus {
        private boolean bActive;
        private boolean bConsistent;

        private ConstraintStatus(boolean argActive, boolean argConsistent){
            bActive = argActive;
            bConsistent = argConsistent;
        }
    }


    /**
     * ConstraintContainer constructor comment.
     */
    public ConstraintContainerImpl(){
        super();
    }


    /**
     * Insert the method's description here.
     * Creation date: (6/25/01 4:41:49 PM)
     *
     * @param constraint cbit.vcell.constraints.AbstractConstraint
     */
    public void addGeneralConstraint(GeneralConstraint constraint) throws PropertyVetoException{
        if(ArrayUtils.arrayContains(this.generalConstraints, constraint)){
            throw new RuntimeException(constraint + " already exists");
        }
        GeneralConstraint[] newGeneralConstraint = ArrayUtils.addElement(this.generalConstraints, constraint);
        setGeneralConstraints(newGeneralConstraint);
    }


    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener){
        getPropertyChange().addPropertyChangeListener(listener);
    }


    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener){
        getPropertyChange().addPropertyChangeListener(propertyName, listener);
    }


    public void addSimpleBound(SimpleBounds bounds) throws PropertyVetoException{
        if(ArrayUtils.arrayContains(this.simpleBounds, bounds)){
            throw new RuntimeException(bounds + " already exists");
        }
        SimpleBounds[] newSimpleBounds = ArrayUtils.addElement(this.simpleBounds, bounds);
        setSimpleBounds(newSimpleBounds);
    }


    /**
     * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
     */
    public synchronized void addVetoableChangeListener(VetoableChangeListener listener){
        getVetoPropertyChange().addVetoableChangeListener(listener);
    }


    /**
     * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
     */
    public synchronized void addVetoableChangeListener(java.lang.String propertyName, VetoableChangeListener listener){
        getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
    }


    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(PropertyChangeEvent evt){
        getPropertyChange().firePropertyChange(evt);
    }


    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue){
        getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }


    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue){
        getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }


    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue){
        getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }


    /**
     * The fireVetoableChange method was generated to support the vetoPropertyChange field.
     */
    public void fireVetoableChange(PropertyChangeEvent evt) throws PropertyVetoException{
        getVetoPropertyChange().fireVetoableChange(evt);
    }


    /**
     * The fireVetoableChange method was generated to support the vetoPropertyChange field.
     */
    public void fireVetoableChange(java.lang.String propertyName, int oldValue, int newValue) throws PropertyVetoException{
        getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
    }


    /**
     * The fireVetoableChange method was generated to support the vetoPropertyChange field.
     */
    public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException{
        getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
    }


    /**
     * The fireVetoableChange method was generated to support the vetoPropertyChange field.
     */
    public void fireVetoableChange(String propertyName, boolean oldValue, boolean newValue) throws PropertyVetoException{
        getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
    }


    /**
     * Insert the method's description here.
     * Creation date: (1/4/2005 12:18:54 PM)
     *
     * @param constraint cbit.vcell.constraints.AbstractConstraint
     * @return boolean
     */
    public boolean getActive(AbstractConstraint constraint){
        ConstraintStatus status = this.statusMap.get(constraint);
        if(status != null){
            return status.bActive;
        } else {
            this.statusMap.put(constraint, new ConstraintStatus(true, true));
            return true;
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (6/25/01 5:22:43 PM)
     *
     * @param identifierName java.lang.String
     * @return cbit.vcell.constraints.SimpleBounds
     */
    public RealInterval getBounds(String identifierName){

        //
        // if only one, then just return it, else return intersection of it
        //
        RealInterval bounds = new RealInterval();
        for(SimpleBounds fieldSimpleBound : this.simpleBounds){
            if(fieldSimpleBound.getIdentifier().equals(identifierName) && getActive(fieldSimpleBound)){
                bounds.intersect(fieldSimpleBound.getBounds());
            }
        }
        return bounds;
    }


    /**
     * Insert the method's description here.
     * Creation date: (1/4/2005 12:18:54 PM)
     *
     * @param constraint cbit.vcell.constraints.AbstractConstraint
     * @return boolean
     */
    public boolean getConsistent(AbstractConstraint constraint){
        ConstraintStatus status = this.statusMap.get(constraint);
        if(status != null){
            return status.bConsistent;
        } else {
            this.statusMap.put(constraint, new ConstraintStatus(true, true));
            return true;
        }
    }


    /**
     * Gets the generalConstraints property (cbit.vcell.constraints.GeneralConstraint[]) value.
     *
     * @return The generalConstraints property value.
     * @see #setGeneralConstraints
     */
    public cbit.vcell.constraints.GeneralConstraint[] getGeneralConstraints(){
        return this.generalConstraints;
    }


    /**
     * Gets the generalConstraints index property (cbit.vcell.constraints.GeneralConstraint) value.
     *
     * @param index The index value into the property array.
     * @return The generalConstraints property value.
     * @see #setGeneralConstraints
     */
    public GeneralConstraint getGeneralConstraints(int index){
        return getGeneralConstraints()[index];
    }


    /**
     * Accessor for the propertyChange field.
     */
    protected PropertyChangeSupport getPropertyChange(){
        if(this.propertyChangeSupport == null){
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        }
        ;
        return this.propertyChangeSupport;
    }


    /**
     * Gets the simpleBounds property (cbit.vcell.constraints.SimpleBounds[]) value.
     *
     * @return The simpleBounds property value.
     * @see #setSimpleBounds
     */
    public cbit.vcell.constraints.SimpleBounds[] getSimpleBounds(){
        return this.simpleBounds;
    }


    /**
     * Gets the simpleBounds index property (cbit.vcell.constraints.SimpleBounds) value.
     *
     * @param index The index value into the property array.
     * @return The simpleBounds property value.
     * @see #setSimpleBounds
     */
    public SimpleBounds getSimpleBounds(int index){
        return getSimpleBounds()[index];
    }


    /**
     * Accessor for the vetoPropertyChange field.
     */
    protected VetoableChangeSupport getVetoPropertyChange(){
        if(this.vetoableChangeSupport == null){
            this.vetoableChangeSupport = new VetoableChangeSupport(this);
        }
        ;
        return this.vetoableChangeSupport;
    }


    /**
     * The hasListeners method was generated to support the propertyChange field.
     */
    public synchronized boolean hasListeners(java.lang.String propertyName){
        return getPropertyChange().hasListeners(propertyName);
    }


    /**
     * Insert the method's description here.
     * Creation date: (6/25/01 4:41:49 PM)
     *
     * @param constraint cbit.vcell.constraints.AbstractConstraint
     */
    public void removeGeneralConstraint(GeneralConstraint constraint) throws PropertyVetoException{
        if(!ArrayUtils.arrayContains(this.generalConstraints, constraint)){
            throw new RuntimeException(constraint + " not found");
        }
        GeneralConstraint[] newGeneralConstraint = ArrayUtils.removeFirstInstanceOfElement(this.generalConstraints, constraint);
        setGeneralConstraints(newGeneralConstraint);
    }


    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener){
        getPropertyChange().removePropertyChangeListener(listener);
    }


    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(java.lang.String propertyName, PropertyChangeListener listener){
        getPropertyChange().removePropertyChangeListener(propertyName, listener);
    }


    public void removeSimpleBound(SimpleBounds bounds) throws PropertyVetoException{
        if(!ArrayUtils.arrayContains(this.simpleBounds, bounds)){
            throw new RuntimeException(bounds + " not found");
        }
        SimpleBounds[] newSimpleBounds = ArrayUtils.removeFirstInstanceOfElement(this.simpleBounds, bounds);
        setSimpleBounds(newSimpleBounds);
    }


    /**
     * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
     */
    public synchronized void removeVetoableChangeListener(VetoableChangeListener listener){
        getVetoPropertyChange().removeVetoableChangeListener(listener);
    }


    /**
     * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
     */
    public synchronized void removeVetoableChangeListener(java.lang.String propertyName, VetoableChangeListener listener){
        getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
    }


    /**
     * Insert the method's description here.
     * Creation date: (1/4/2005 12:18:54 PM)
     *
     * @param constraint cbit.vcell.constraints.AbstractConstraint
     * @return boolean
     */
    public void setActive(AbstractConstraint constraint, boolean bActive){
        ConstraintStatus status = (ConstraintStatus) statusMap.get(constraint);
        if(status != null){
            status.bActive = bActive;
        } else {
            statusMap.put(constraint, new ConstraintStatus(bActive, true));
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (1/4/2005 12:18:54 PM)
     *
     * @param constraint cbit.vcell.constraints.AbstractConstraint
     * @return boolean
     */
    public void setConsistent(AbstractConstraint constraint, boolean bConsistent){
        ConstraintStatus status = (ConstraintStatus) statusMap.get(constraint);
        if(status != null){
            status.bConsistent = bConsistent;
        } else {
            statusMap.put(constraint, new ConstraintStatus(true, bConsistent));
        }
    }


    /**
     * Sets the generalConstraints property (cbit.vcell.constraints.GeneralConstraint[]) value.
     *
     * @param generalConstraints The new value for the property.
     * @throws PropertyVetoException The exception description.
     * @see #getGeneralConstraints
     */
    public void setGeneralConstraints(GeneralConstraint[] generalConstraints) throws PropertyVetoException{
        cbit.vcell.constraints.GeneralConstraint[] oldValue = this.generalConstraints;
        fireVetoableChange("generalConstraints", oldValue, generalConstraints);
        this.generalConstraints = generalConstraints;
        firePropertyChange("generalConstraints", oldValue, generalConstraints);
    }


    /**
     * Sets the simpleBounds property (cbit.vcell.constraints.SimpleBounds[]) value.
     *
     * @param simpleBounds The new value for the property.
     * @throws PropertyVetoException The exception description.
     * @see #getSimpleBounds
     */
    public void setSimpleBounds(cbit.vcell.constraints.SimpleBounds[] simpleBounds) throws PropertyVetoException{
        SimpleBounds[] oldValue = this.simpleBounds;
        fireVetoableChange("simpleBounds", oldValue, simpleBounds);
        this.simpleBounds = simpleBounds;
        firePropertyChange("simpleBounds", oldValue, simpleBounds);
    }


    /**
     * Insert the method's description here.
     * Creation date: (12/28/2004 5:47:43 PM)
     *
     * @return java.lang.String
     */
    public String toECLiPSe(){
        StringBuilder buffer = new StringBuilder();
        for(SimpleBounds fieldSimpleBound : this.simpleBounds){
            if(!getActive(fieldSimpleBound)) continue;
            String symbol = fieldSimpleBound.getIdentifier();
            RealInterval bounds = fieldSimpleBound.getBounds();
            String lowBoundsString = Double.toString(bounds.lo());
            if(bounds.lo() == Double.POSITIVE_INFINITY){
                lowBoundsString = "1.0Inf";
            } else if(bounds.lo() == Double.NEGATIVE_INFINITY){
                lowBoundsString = "-1.0Inf";
            }
            String hiBoundsString = Double.toString(bounds.hi());
            if(bounds.hi() == Double.POSITIVE_INFINITY){
                hiBoundsString = "1.0Inf";
            } else if(bounds.hi() == Double.NEGATIVE_INFINITY){
                hiBoundsString = "-1.0Inf";
            }
            buffer.append(SymbolUtils.getEscapedTokenECLiPSe(symbol)).append(" $>= ").append(lowBoundsString).append(",");
            buffer.append(SymbolUtils.getEscapedTokenECLiPSe(symbol)).append(" $=< ").append(hiBoundsString).append(",");
        }

        for(GeneralConstraint fieldGeneralConstraint : this.generalConstraints){
            if(!getActive(fieldGeneralConstraint)){
                continue;
            }
            buffer.append(fieldGeneralConstraint.getExpression().infix_ECLiPSe()).append(", ");
        }
        return buffer.toString();
    }

    public void show(){
        for(GeneralConstraint gc : this.generalConstraints){
            System.out.println(gc.toString());
        }
        for(SimpleBounds sb : this.simpleBounds){
            System.out.println(sb.toString());
        }
    }
}
