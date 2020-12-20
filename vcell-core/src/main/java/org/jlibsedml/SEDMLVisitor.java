package org.jlibsedml;

/**
 * Abstract class for any visitor to extend.
 * @author radams
 *
 */
public abstract class SEDMLVisitor {
    
    
    public abstract boolean visit (SedML sedml);
    
    public abstract boolean visit (Simulation sim);
    
    public abstract boolean visit (Model model);
    
    public abstract boolean visit (Task task);
    public abstract boolean visit (RepeatedTask repeatedTask);
    
    public abstract boolean visit (AddXML change);
    
    public abstract boolean visit (RemoveXML change);
    
    public abstract boolean visit (ChangeXML change);
    
    public abstract boolean visit (ChangeAttribute change);
    
    public abstract boolean visit (ComputeChange change);
    
    public abstract boolean visit(SetValue setValue) ;

    public abstract boolean visit (DataGenerator dg);
    
    public abstract boolean visit (Variable var);
    
    public abstract boolean visit (Parameter model);
    
    public abstract boolean visit (Output output);

    public abstract boolean visit(Algorithm algorithm);

    public abstract boolean visit(Curve curve) ;

    public abstract boolean visit(DataSet dataSet) ;

    public abstract boolean visit(Surface surface) ;

    public abstract boolean visit(UniformRange uniformRange) ;
    public abstract boolean visit(VectorRange vectorRange) ;
    public abstract boolean visit(FunctionalRange functionalRange) ;

}
