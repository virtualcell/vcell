package jscl.math.function;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.util.ArrayComparator;

public class ImplicitFunction extends Function {
    protected int derivation[];
    protected Generic subscript[];

    public static Curried apply(String name, int derivation[]) {
        return new Curried(name, derivation, new Generic[0]);
    }

    public static Curried[] apply(String name, int derivation[], int n) {
        Curried element[]=new Curried[n];
        for(int i=0;i<n;i++) element[i]=new Curried(name, derivation, new Generic[] {JSCLInteger.valueOf(i)});
        return element;
    }

    public static class Curried {
        final String name;
        final int derivation[];
        final Generic subscript[];

        public Curried(String name, int derivation[], Generic subscript[]) {
            this.name=name;
            this.derivation=derivation;
            this.subscript=subscript;
        }

        public Generic apply(Generic parameter[]) {
            return new ImplicitFunction(name, parameter, derivation, subscript).expressionValue();
        }
    }

    public ImplicitFunction(String name, Generic parameter[], int derivation[], Generic subscript[]) {
        super(name,parameter);
        this.derivation=derivation;
        this.subscript=subscript;
    }

    public ImplicitFunction(String name, Generic parameter[]) {
        this(name, parameter, new int[parameter.length], new Generic[0]);
    }

    public int[] derivation() {
        return derivation;
    }

    public Generic[] subscript() {
        return subscript;
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        int c[]=new int[derivation.length];
        for(int i=0;i<c.length;i++) {
            if(i==n) {
                if(derivation[i]>0) c[i]=derivation[i]-1;
                else throw new NotIntegrableException();
            } else c[i]=derivation[i];
        }
        return new ImplicitFunction(name,parameter,c,subscript).evaluate();
    }

    public Generic derivative(int n) {
        int c[]=new int[derivation.length];
        for(int i=0;i<c.length;i++) {
            if(i==n) c[i]=derivation[i]+1;
            else c[i]=derivation[i];
        }
        return new ImplicitFunction(name,parameter,c,subscript).evaluate();
    }

    public Generic evaluate() {
        return expressionValue();
    }

    public Generic evalelem() {
        return expressionValue();
    }

    public Generic evalsimp() {
        return expressionValue();
    }

    public Generic evalfunc() {
        throw new ArithmeticException();
    }

    public Generic evalnum() {
        throw new ArithmeticException();
    }

    public int compareTo(Variable variable) {
        if(this==variable) return 0;
        int c=comparator.compare(this,variable);
        if(c<0) return -1;
        else if(c>0) return 1;
        else {
            ImplicitFunction v=(ImplicitFunction)variable;
            c=name.compareTo(v.name);
            if(c<0) return -1;
            else if(c>0) return 1;
            else {
                c=ArrayComparator.comparator.compare(subscript,v.subscript);
                if(c<0) return -1;
                else if(c>0) return 1;
                else {
                    c=compareDerivation(derivation,v.derivation);
                    if(c<0) return -1;
                    else if(c>0) return 1;
                    else return ArrayComparator.comparator.compare(parameter,v.parameter);
                }
            }
        }
    }

    static int compareDerivation(int c1[], int c2[]) {
        int n=c1.length;
        for(int i=n-1;i>=0;i--) {
            if(c1[i]<c2[i]) return -1;
            else if(c1[i]>c2[i]) return 1;
        }
        return 0;
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        int n=0;
        for(int i=0;i<derivation.length;i++) n+=derivation[i];
        buffer.append(name);
        for(int i=0;i<subscript.length;i++) {
            buffer.append("[").append(subscript[i]).append("]");
        }
        if(n==0);
        else if(parameter.length==1?n<=Constant.PRIMECHARS:false) buffer.append(Constant.primechars(n));
        else buffer.append(derivationToString());
        buffer.append("(");
        for(int i=0;i<parameter.length;i++) {
            buffer.append(parameter[i]).append(i<parameter.length-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    String derivationToString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append("{");
        for(int i=0;i<derivation.length;i++) {
            buffer.append(derivation[i]).append(i<derivation.length-1?", ":"");
        }
        buffer.append("}");
        return buffer.toString();
    }

    public String toMathML() {
        StringBuffer b = new StringBuffer();
        b.append("<apply>");
        b.append("<ci>" + bodyToMathML() + "</ci>");
        for(int i=0;i<parameter.length;i++) {
            b.append(parameter[i].toMathML());
        }
        b.append("</apply>");
        return b.toString();
    }

    String bodyToMathML() {
        StringBuffer b = new StringBuffer();
        int n=0;
        for(int i=0;i<derivation.length;i++) n+=derivation[i];
        if(subscript.length==0) {
            if(n==0) {
                b.append(nameToMathML());
            } else if(parameter.length==1?n<=Constant.PRIMECHARS:false) {
                b.append(nameToMathML() + Constant.primecharsToMathML(n));
            } else {
                b.append("<msup>");
                b.append("<mi>" + nameToMathML() + "</mi>");
                b.append(derivationToMathML(n));
                b.append("</msup>");
            }
        } else {
            if(n==0) {
                b.append("<msub>");
                b.append("<mi>" + nameToMathML() + "</mi>");
                b.append(subscriptToMathML());
                b.append("</msub>");
            } else if(parameter.length==1?n<=Constant.PRIMECHARS:false) {
                b.append("<msub>");
                b.append("<mi>" + nameToMathML() + Constant.primecharsToMathML(n) + "</mi>");
                b.append(subscriptToMathML());
                b.append("</msub>");
            } else {
                b.append("<msubsup>");
                b.append("<mi>" + nameToMathML() + "</mi>");
                b.append(subscriptToMathML());
                b.append(derivationToMathML(n));
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

    String derivationToMathML(int n) {
        StringBuffer b = new StringBuffer();
        b.append("<mfenced>");
        for(int i=0;i<derivation.length;i++) {
            b.append("<mn>" + String.valueOf(derivation[i]) + "</mn>");
        }
        b.append("</mfenced>");
        return b.toString();
    }

    protected Variable newinstance() {
        return new ImplicitFunction(name,new Generic[parameter.length],derivation,subscript);
    }
}
