package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JSCLInteger;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.math.function.ImplicitFunction;
import jscl.math.polynomial.Basis;
import jscl.math.polynomial.Monomial;
import jscl.math.polynomial.Ordering;
import jscl.math.polynomial.Polynomial;

public class Groebner extends Operator {
    public static final Generic lex = new Constant("lex").expressionValue();
    public static final Generic tdl = new Constant("tdl").expressionValue();
    public static final Generic drl = new Constant("drl").expressionValue();
    public static final ImplicitFunction.Curried elim = ImplicitFunction.apply("elim", new int[1]);

    public Groebner(Generic generic, Generic variable, Generic ordering, Generic modulo) {
        super("groebner",new Generic[] {generic,variable,ordering,modulo});
    }

    public Generic compute() {
        Generic generic[]=parameter[0].vectorValue().elements();
        Variable variable[]=variables(parameter[1].vectorValue());
        Ordering ord=ordering(parameter[2]);
        int m=parameter[3].integerValue().intValue();
        return new PolynomialVector(Basis.compute(generic,variable,ord,m));
    }

    static Ordering ordering(Generic generic) {
        Variable v=generic.variableValue();
        if(v.compareTo(lex.variableValue())==0) return Monomial.lexicographic;
        else if(v.compareTo(tdl.variableValue())==0) return Monomial.totalDegreeLexicographic;
        else if(v.compareTo(drl.variableValue())==0) return Monomial.degreeReverseLexicographic;
        else if(v instanceof ImplicitFunction) {
            Generic g[]=((ImplicitFunction)v).parameters();
            int k=g[0].integerValue().intValue();
            if(v.compareTo(elim.apply(new Generic[] {JSCLInteger.valueOf(k)}).variableValue())==0) return Monomial.kthElimination(k);
        }
        throw new ArithmeticException();
    }

    @Override
    public String toString() {
        StringBuffer buffer=new StringBuffer();
        int n=4;
        if(parameter[3].signum()==0) {
            n=3;
            if(ordering(parameter[2])==Monomial.lexicographic) n=2;
        }
        buffer.append(name);
        buffer.append("(");
        for(int i=0;i<n;i++) {
            buffer.append(parameter[i]).append(i<n-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toMathML() {
	StringBuffer b = new StringBuffer();
        int n=4;
        if(parameter[3].signum()==0) {
            n=3;
            if(ordering(parameter[2])==Monomial.lexicographic) n=2;
        }
	b.append("<apply>");
	b.append("<ci>" + nameToMathML() + "</ci>");
        for(int i=0;i<n;i++) {
            b.append(parameter[i].toMathML());
        }
	b.append("</apply>");
	return b.toString();
    }

    protected Variable newinstance() {
        return new Groebner(null,null,null,null);
    }
}

class PolynomialVector extends JSCLVector {
    final Basis basis;

    PolynomialVector(Basis basis) {
        this(basis,basis.elements());
    }

    PolynomialVector(Basis basis, Generic generic[]) {
        super(generic.length>0?generic:new Generic[] {JSCLInteger.valueOf(0)});
        this.basis=basis;
    }

    @Override
    public String toString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append("{");
        for(int i=0;i<n;i++) {
            buffer.append(basis.polynomial(element[i])).append(i<n-1?", ":"");
        }
        buffer.append("}");
        return buffer.toString();
    }

    @Override
    public String toMathML() {
	StringBuffer b = new StringBuffer();
	b.append("<vector>");
        for(int i=0;i<n;i++) {
            b.append(basis.polynomial(element[i]).toMathML());
        }
	b.append("</vector>");
	return b.toString();
    }

    protected Generic newinstance(Generic element[]) {
        return new PolynomialVector(basis,element);
    }
}
