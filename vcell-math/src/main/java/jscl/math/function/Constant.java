package jscl.math.function;

import jscl.math.Generic;
import jscl.math.Expression;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.NumericWrapper;
import jscl.math.numeric.Numeric;
import jscl.math.numeric.JSCLDouble;
import jscl.math.Variable;
import jscl.util.ArrayComparator;

public class Constant extends Variable {
    public static final Generic e=new Exp(JSCLInteger.valueOf(1)).expressionValue();
    public static final Generic pi=new Constant("pi").expressionValue();
    public static final Generic i=new Sqrt(JSCLInteger.valueOf(-1)).expressionValue();
    public static final Generic half=new Inv(JSCLInteger.valueOf(2)).expressionValue();
    public static final Generic third=new Inv(JSCLInteger.valueOf(3)).expressionValue();
    public static final Generic j=half.negate().multiply(JSCLInteger.valueOf(1).subtract(i.multiply(new Sqrt(JSCLInteger.valueOf(3)).expressionValue())));
    public static final Generic jbar=half.negate().multiply(JSCLInteger.valueOf(1).add(i.multiply(new Sqrt(JSCLInteger.valueOf(3)).expressionValue())));
    public static final Generic infinity=new Constant("oo").expressionValue();
    static final int PRIMECHARS=3;
    protected int prime;
    protected Generic subscript[];

    public Constant(String name) {
        this(name,0);
    }

    public Constant(String name, int prime) {
        this(name,prime,new Generic[0]);
    }

    public Constant(String name, int prime, Generic subscript[]) {
        super(name);
        this.prime=prime;
        this.subscript=subscript;
    }

    public int prime() {
        return prime;
    }

    public Generic[] subscript() {
        return subscript;
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        return null;
    }

    public Generic derivative(Variable variable) {
        if(isIdentity(variable)) return JSCLInteger.valueOf(1);
        else return JSCLInteger.valueOf(0);
    }

    public Generic substitute(Variable variable, Generic generic) {
        Constant v=(Constant)newinstance();
        for(int i=0;i<subscript.length;i++) {
            v.subscript[i]=subscript[i].substitute(variable,generic);
        }
        if(v.isIdentity(variable)) return generic;
        else return v.expressionValue();
    }

    public Generic expand() {
        Constant v=(Constant)newinstance();
        for(int i=0;i<subscript.length;i++) {
            v.subscript[i]=subscript[i].expand();
        }
        return v.expressionValue();
    }

    public Generic factorize() {
        Constant v=(Constant)newinstance();
        for(int i=0;i<subscript.length;i++) {
            v.subscript[i]=subscript[i].factorize();
        }
        return v.expressionValue();
    }

    public Generic elementary() {
        Constant v=(Constant)newinstance();
        for(int i=0;i<subscript.length;i++) {
            v.subscript[i]=subscript[i].elementary();
        }
        return v.expressionValue();
    }

    public Generic simplify() {
        Constant v=(Constant)newinstance();
        for(int i=0;i<subscript.length;i++) {
            v.subscript[i]=subscript[i].simplify();
        }
        return v.expressionValue();
    }

    public Generic function(Variable variable) {
        if(isIdentity(variable)) return jscl.math.Function.identity;
        else return numeric().function(variable);
    }

    public Generic numeric() {
        return new NumericWrapper(this);
    }

    public Numeric numericValue() {
        Expression expr = expressionValue();
        if(expr.compareTo(pi)==0) return JSCLDouble.valueOf(Math.PI);
        else if(expr.compareTo(infinity)==0) return JSCLDouble.valueOf(Double.POSITIVE_INFINITY);
        else throw new ArithmeticException();
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
            Constant v=(Constant)variable;
            c=name.compareTo(v.name);
            if(c<0) return -1;
            else if(c>0) return 1;
            else {
                c=ArrayComparator.comparator.compare(subscript,v.subscript);
                if(c<0) return -1;
                else if(c>0) return 1;
                else {
                    if(prime<v.prime) return -1;
                    else if(prime>v.prime) return 1;
                    else return 0;
                }
            }
        }
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(name);
        for(int i=0;i<subscript.length;i++) {
            buffer.append("[").append(subscript[i]).append("]");
        }
        if(prime==0);
        else if(prime<=PRIMECHARS) buffer.append(primechars(prime));
        else buffer.append("{").append(prime).append("}");
        return buffer.toString();
    }

    static String primechars(int n) {
        StringBuffer buffer=new StringBuffer();
        for(int i=0;i<n;i++) buffer.append("'");
        return buffer.toString();
    }

    public String toMathML() {
        if("pi".equals(name) && prime==0 && subscript.length==0) return "<pi/>";
        else if("oo".equals(name)) return "<infinity/>";
        else return "<ci>" + bodyToMathML() + "</ci>";
    }

    public String bodyToMathML() {
        StringBuffer b = new StringBuffer();
        if(subscript.length==0) {
            if(prime==0) {
                b.append(nameToMathML());
            } else if(prime<=PRIMECHARS) {
                b.append(nameToMathML() + primecharsToMathML(prime));
            } else {
                b.append("<msup>");
                b.append("<mi>" + nameToMathML() + "</mi>");
                b.append(primeToMathML());
                b.append("</msup>");
            }
        } else {
            if(prime==0) {
                b.append("<msub>");
                b.append("<mi>" + nameToMathML() + "</mi>");
                b.append(subscriptToMathML());
                b.append("</msub>");
            } else if(prime<=PRIMECHARS) {
                b.append("<msub>");
                b.append("<mi>" + nameToMathML() + primecharsToMathML(prime) + "</mi>");
                b.append(subscriptToMathML());
                b.append("</msub>");
            } else {
                b.append("<msubsup>");
                b.append("<mi>" + nameToMathML() + "</mi>");
                b.append(subscriptToMathML());
                b.append(primeToMathML());
                b.append("</msubsup>");
            }
        }
        return b.toString();
    }

    String subscriptToMathML() {
        StringBuffer b = new StringBuffer();
        b.append("<mrow>");
        for(int i=0;i<subscript.length;i++) {
            b.append(subscript[i].toMathML());
        }
        b.append("</mrow>");
        return b.toString();
    }

    String primeToMathML() {
        StringBuffer b = new StringBuffer();
        b.append("<mfenced>");
        b.append("<mn>" + String.valueOf(prime) + "</mn>");
        b.append("</mfenced>");
        return b.toString();
    }

    static String primecharsToMathML(int n) {
        StringBuffer b = new StringBuffer();
        for(int i=0;i<n;i++) b.append("\u2032");
        return b.toString();
    }

    protected Variable newinstance() {
        return new Constant(name,prime,new Generic[subscript.length]);
    }
}
