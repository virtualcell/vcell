package jscl.math;

public class ExpressionVariable extends GenericVariable {
    public ExpressionVariable(Generic generic) {
        super(generic);
    }

    public static Generic content(Generic generic) {
        try {
            Variable v=generic.variableValue();
            if(v instanceof ExpressionVariable) generic=((ExpressionVariable)v).content;
        } catch (NotVariableException e) {}
        return generic;
    }

    public Generic substitute(Variable variable, Generic generic) {
        if(isIdentity(variable)) return generic;
        else return content.substitute(variable,generic);
    }

    public Generic elementary() {
        return content.elementary();
    }

    public Generic simplify() {
        return content.simplify();
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append("(").append(content).append(")");
        return buffer.toString();
    }

    protected Variable newinstance() {
        return new ExpressionVariable(null);
    }
}
