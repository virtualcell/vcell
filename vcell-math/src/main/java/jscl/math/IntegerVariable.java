package jscl.math;

class IntegerVariable extends GenericVariable {
    IntegerVariable(Generic generic) {
        super(generic);
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
        return new IntegerVariable(null);
    }
}
