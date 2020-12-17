package org.jlibsedml.extensions;

import org.jlibsedml.AbstractIdentifiableElement;
import org.jlibsedml.AddXML;
import org.jlibsedml.Algorithm;
import org.jlibsedml.ChangeAttribute;
import org.jlibsedml.ChangeXML;
import org.jlibsedml.ComputeChange;
import org.jlibsedml.Curve;
import org.jlibsedml.DataGenerator;
import org.jlibsedml.DataSet;
import org.jlibsedml.FunctionalRange;
import org.jlibsedml.Model;
import org.jlibsedml.Output;
import org.jlibsedml.Parameter;
import org.jlibsedml.RemoveXML;
import org.jlibsedml.RepeatedTask;
import org.jlibsedml.SEDBase;
import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.SedML;
import org.jlibsedml.SetValue;
import org.jlibsedml.Simulation;
import org.jlibsedml.Surface;
import org.jlibsedml.Task;
import org.jlibsedml.UniformRange;
import org.jlibsedml.Variable;
import org.jlibsedml.VectorRange;
/**
 * This class searches the object structure for an element with its value of an id attribute
 *  equal to that passed into this object's constructor.
 * @author radams
 *
 */

public class ElementSearchVisitor extends SEDMLVisitor {

    /**
     * @param searchTerm A non-null <code>String</code> of an element identifier with which  to search the object structure
     */
    public ElementSearchVisitor(String searchTerm) {
        super();
        this.searchTerm = searchTerm;
    }

    String searchTerm;
    SEDBase foundElement;
    @Override
    public boolean visit(SedML sedml) {
       return true;
    }

    @Override
    public boolean visit(Simulation sim) {
        return checkID(sim);
    }

    @Override
    public boolean visit(Model model) {
        return checkID(model);
    }

    @Override
    public boolean visit(Task task) {
        return checkID(task);
    }


    @Override
    public boolean visit(DataGenerator dg) {
        return checkID(dg);
        
        
    }

    @Override
    public boolean visit(Variable var) {
        return checkID(var);
    }

    @Override
    public boolean visit(Parameter param) {
        return checkID(param);
    }

    @Override
    public boolean visit(Output output) {
        return checkID(output);
    }
    
    // returns boolean to  visit() methods - 'true' means 'keep searching'
    // false means ' found element with that id, stop search'
    boolean checkID(AbstractIdentifiableElement aie) {
       if(searchTerm.equals(aie.getId())){
           foundElement=aie;
           return false;
       }
       return true;
    }

    @Override
    public boolean visit(Algorithm algorithm) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean visit(Curve curve) {
        return checkID(curve);
    }

    @Override
    public boolean visit(DataSet dataSet) {
        return checkID(dataSet);
    }

    @Override
    public boolean visit(Surface surface) {
        return checkID(surface);
    }
    
    /**
     * Gets either the found element, or <code>null</code> if no element was found
     *  with the ID specified in the constructor.<br/>
     *  Before calling <code>accept()</code>, this method will always return <code>null</code>.
     * @return A {@link SEDBase} or <code>null</code>.
     */
    public SEDBase getFoundElement(){
        return foundElement;
    }
    
    /**
     * Resets the search. After calling this method, 
     * <pre>
     * getFoundElement()
     * </pre>
     * will return <code>null</code>.
     */
    public void clear(){
        foundElement=null;
    }

    @Override
    public boolean visit(AddXML change) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean visit(RemoveXML change) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean visit(ChangeXML change) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean visit(ChangeAttribute change) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean visit(ComputeChange change) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean visit(SetValue setValue) {
        // TODO Auto-generated method stub
        return true;
    }
    @Override
    public boolean visit(RepeatedTask repeatedTask) {
        return checkID(repeatedTask);
    }

    @Override
    public boolean visit(UniformRange uniformRange) {
        return checkID(uniformRange);
    }

    @Override
    public boolean visit(VectorRange vectorRange) {
        return checkID(vectorRange);
    }

    @Override
    public boolean visit(FunctionalRange functionalRange) {
        return checkID(functionalRange);
    }

}
