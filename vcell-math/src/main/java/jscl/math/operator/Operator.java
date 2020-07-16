package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.JSCLVector;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.Variable;
import jscl.util.ArrayComparator;

public abstract class Operator extends Variable {
    protected Generic parameter[];

    public Operator(String name, Generic parameter[]) {
        super(name);
        this.parameter=parameter;
    }

    public Generic[] parameters() {
        return parameter;
    }

    public abstract Generic compute();

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        return null;
    }

    public Generic derivative(Variable variable) {
        if(isIdentity(variable)) return JSCLInteger.valueOf(1);
        else return JSCLInteger.valueOf(0);
    }

    public Generic substitute(Variable variable, Generic generic) {
        Operator v=(Operator)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].substitute(variable,generic);
        }
        if(v.isIdentity(variable)) return generic;
        else return v.compute();
    }

    public Generic expand() {
        Operator v=(Operator)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].expand();
        }
        return v.compute();
    }

    public Generic factorize() {
        Operator v=(Operator)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].factorize();
        }
        return v.expressionValue();
    }

    public Generic elementary() {
        Operator v=(Operator)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].elementary();
        }
        return v.expressionValue();
    }

    public Generic simplify() {
        Operator v=(Operator)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].simplify();
        }
        return v.expressionValue();
    }

    public Generic function(Variable variable) {
        throw new ArithmeticException();
    }

    public Generic numeric() {
        throw new ArithmeticException();
    }

    public boolean isConstant(Variable variable) {
        return !isIdentity(variable);
    }

    public int compareTo(Variable variable) {
        if(this==variable) return 0;
        int c=comparator.compare(this,variable);
        if(c<0) return -1;
        else if(c>0) return 1;
        else {
            Operator v=(Operator)variable;
            c=name.compareTo(v.name);
            if(c<0) return -1;
            else if(c>0) return 1;
            else return ArrayComparator.comparator.compare(parameter,v.parameter);
        }
    }

    protected static Variable[] variables(JSCLVector vector) throws NotVariableException {
        Generic element[]=vector.elements();
        Variable variable[]=new Variable[element.length];
        for(int i=0;i<element.length;i++) {
            variable[i]=element[i].variableValue();
        }
        return variable;
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(name);
        buffer.append("(");
        for(int i=0;i<parameter.length;i++) {
            buffer.append(parameter[i]).append(i<parameter.length-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    public String toMathML() {
        StringBuffer b = new StringBuffer();
        b.append("<apply>");
        b.append("<ci>" + nameToMathML() + "</ci>");
        for(int i=0;i<parameter.length;i++) {
            b.append(parameter[i].toMathML());
        }
        b.append("</apply>");
        return b.toString();
    }
}
