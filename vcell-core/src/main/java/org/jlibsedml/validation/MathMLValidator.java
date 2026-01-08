package org.jlibsedml.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jlibsedml.IIdentifiable;
import org.jlibsedml.components.Calculation;
import org.jlibsedml.components.Parameter;
import org.jlibsedml.SedMLError;
import org.jlibsedml.SedMLError.ERROR_SEVERITY;
import org.jlibsedml.components.Variable;
import org.jmathml.ASTCi;
import org.jmathml.ASTNode;
import org.jmathml.EvaluationContext;

/**
 * Provides some validation that :<ul>
 * <li> An MathML element has its variables defined in ListOfVariables and ListOfParameters
 * <li> A node can be evaluated to a numerical value.
 * </ul>
 *
 * @author radams
 *
 */
public class MathMLValidator extends AbstractDocumentValidator implements ISedMLValidator {

    private final Calculation sedCalculation;

    /**
     *
     * @param document
     * @param mathElement A non-null {@link Calculation}
     */
    MathMLValidator(Document document, Calculation mathElement) {
        super(document);
        this.sedCalculation = mathElement;
    }

    /**
     * Alternative constructor taking a single {@link Calculation} object
     *
     * @param mathElement An {@link Calculation} object.
     */
    public MathMLValidator(Calculation mathElement) {
        super(null);
        this.sedCalculation = mathElement;
    }

    /**
     * Checks that values of &lt;ci&gt; elements in a MathML block are defined in
     * {@link Variable} or {@link Parameter} declarations in this object.
     *
     * @see ISedMLValidator
     */
    public List<SedMLError> validate() {
        List<SedMLError> errors = this.checkMathIds();
        errors.addAll(this.checkFunctions());
        return errors;
    }

    private List<SedMLError> checkFunctions() {
        List<SedMLError> errors = new ArrayList<SedMLError>();
        ASTNode node = this.sedCalculation.getMath();
        EvaluationContext cont = new EvaluationContext();
        for (IIdentifiable id : this.sedCalculation.getListOfParameters().getContents()) {
            cont.setValueFor(id.getId(), 0.0);
        }
        for (IIdentifiable id : this.sedCalculation.getListOfVariables().getContents()) {
            cont.setValueFor(id.getId(), 0.0);
        }

        if (!node.canEvaluate(cont)) {
            errors.add(new SedMLError(0, "This node [" + node.getName() + "] cannot be evaluated", ERROR_SEVERITY.WARNING));
        }
        return errors;

    }

    private List<SedMLError> checkMathIds() {
        List<SedMLError> errors = new ArrayList<SedMLError>();

        List<? extends IIdentifiable> vars = this.sedCalculation.getListOfVariables().getContents();
        List<? extends IIdentifiable> params = this.sedCalculation.getListOfParameters().getContents();
        Set<ASTCi> identifiers = this.sedCalculation.getMath().getIdentifiers();
        for (ASTCi var : identifiers) {
            if (!(this.check(var, vars) || this.check(
                    var, params))) {
                errors
                        .add(new SedMLError(
                                0,
                                "Math ml in  [" + this.sedCalculation.getId() + "] refers to  a variable not defined in the model",
                                ERROR_SEVERITY.WARNING));
            }
        }


        return errors;

    }

    private boolean check(ASTCi var2, List<? extends IIdentifiable> vars) {
        for (IIdentifiable var : vars) {
            if (var.getId().equals(var2.getName())) {
                return true;
            }
        }
        return false;
    }


}

