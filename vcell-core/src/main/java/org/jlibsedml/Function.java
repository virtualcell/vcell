package org.jlibsedml;

import org.jmathml.ASTNode;

@Deprecated
public class Function{

    private ASTNode math = null;

    public Function(ASTNode mathAsNode) {
        super();
        this.math = mathAsNode;
    }
    
    public void setMath(ASTNode math) {
        this.math = math;
    }

    public ASTNode getMath() {
        return math;
    }

    public String toString() {
        return "Function ["
        + "getMath()=" + getMath()
        + "]";
    }

    public String getElementName() {
        return SEDMLTags.FUNCTIONAL_RANGE_TAG;
    }

}
