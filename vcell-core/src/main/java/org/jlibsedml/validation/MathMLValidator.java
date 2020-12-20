package org.jlibsedml.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jlibsedml.IIdentifiable;
import org.jlibsedml.IMathContainer;
import org.jlibsedml.Parameter;
import org.jlibsedml.SedMLError;
import org.jlibsedml.SedMLError.ERROR_SEVERITY;
import org.jlibsedml.Variable;
import org.jmathml.ASTCi;
import org.jmathml.ASTNode;
import org.jmathml.EvaluationContext;

/**
 * Provides some validation that :<ul>
 * <li> An MathML element has its variables defined in ListOfVariables and ListOfParameters
 * <li> A node can be evaluated to a numerical value.
 * </ul>
 * @author radams
 *
 */
 public class MathMLValidator   extends AbstractDocumentValidator implements ISedMLValidator{
 
     private IMathContainer mathContainer;
     
     /**
      * 
      * @param document 
     * @param mathElement A non-null {@link IMathContainer}
      */
      MathMLValidator(Document document, IMathContainer mathElement) {
        super(document);
        this.mathContainer=mathElement;
    }
      /**
       * Alternative constructor taking a single {@link IMathContainer} object
       * @param mathElement An {@link IMathContainer} object.
       */
      public MathMLValidator( IMathContainer mathElement) {
            super(null);
            this.mathContainer=mathElement;
        }

    /**
     * Checks that values of &lt;ci&gt; elements in a MathML block are defined in
     *  {@link Variable} or {@link Parameter} declarations in this object.
     * @see ISedMLValidator
     */
    public List<SedMLError> validate()  {
        List<SedMLError> errors= checkMathIds();
        errors.addAll(checkFunctions());
        return errors;
    }
    
    private List<SedMLError>  checkFunctions() {
        List<SedMLError> errors = new ArrayList<SedMLError>();
        ASTNode node = mathContainer.getMath();
        EvaluationContext cont = new EvaluationContext();
        for (IIdentifiable id: mathContainer.getListOfParameters()){
            cont.setValueFor(id.getId(), 0.0);
        }
        for (IIdentifiable id: mathContainer.getListOfVariables()){
            cont.setValueFor(id.getId(), 0.0);
        }
        
        if(!node.canEvaluate(cont)){
            errors.add(new SedMLError(0, "This node ["+ node.getName() + "] cannot be evaluated",ERROR_SEVERITY.WARNING));
        }
        return errors;
        
    }

    private List<SedMLError> checkMathIds() {
        List<SedMLError> errors = new ArrayList<SedMLError>();
        
                    List<?extends IIdentifiable> vars = mathContainer.getListOfVariables();
                    List<?extends IIdentifiable> params = mathContainer.getListOfParameters();
                    Set<ASTCi> identifiers = mathContainer.getMath().getIdentifiers();
                    for (ASTCi var : identifiers) {
                        if (!(check(var, vars) || check(
                                var, params))) {
                            errors
                                    .add(new SedMLError(
                                            0,
                                            "Math ml in  [" + mathContainer.getId()+"] refers to  a variable not defined in the model",
                                            ERROR_SEVERITY.WARNING));
                        }
                    }
                

        

        return errors;

    }

    private boolean check(ASTCi var2, List<?extends IIdentifiable> vars) {
        for (IIdentifiable var : vars) {
            if (var.getId().equals(var2.getName())) {
                return true;
            }
        }
        return false;
    }



}

