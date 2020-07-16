package jscl.math;

import jscl.editor.rendering.MathObject;

public abstract class Generic implements Arithmetic, Comparable, MathObject {
    public abstract Generic add(Generic generic);

    public Generic subtract(Generic generic) {
        return add(generic.negate());
    }

    public abstract Generic multiply(Generic generic);

    public boolean multiple(Generic generic) throws ArithmeticException {
        return true;
    }

    public abstract Generic divide(Generic generic) throws ArithmeticException;

    public Arithmetic add(Arithmetic arithmetic) {
        return add((Generic)arithmetic);
    }

    public Arithmetic subtract(Arithmetic arithmetic) {
        return subtract((Generic)arithmetic);
    }

    public Arithmetic multiply(Arithmetic arithmetic) {
        return multiply((Generic)arithmetic);
    }

    public Arithmetic divide(Arithmetic arithmetic) throws ArithmeticException {
        return divide((Generic)arithmetic);
    }

    public Generic[] divideAndRemainder(Generic generic) throws ArithmeticException {
        return new Generic[] {divide(generic),JSCLInteger.valueOf(0)};
    }

    public Generic remainder(Generic generic) throws ArithmeticException {
        return divideAndRemainder(generic)[1];
    }

    public Generic inverse() {
        return JSCLInteger.valueOf(1).divide(this);
    }

    public abstract Generic gcd(Generic generic);

    public Generic scm(Generic generic) {
        return divide(gcd(generic)).multiply(generic);
    }

    public abstract Generic gcd();

    public Generic[] gcdAndNormalize() {
        Generic gcd=gcd();
        if(gcd.signum()==0) return new Generic[] {gcd,this};
        if(gcd.signum()!=signum()) gcd=gcd.negate();
        return new Generic[] {gcd,divide(gcd)};
    }

    public Generic normalize() {
        return gcdAndNormalize()[1];
    }

    public Generic pow(int exponent) {
        return pow(JSCLInteger.valueOf(exponent));
    }

    public Generic pow(final JSCLInteger exponent) {
        final Generic a;
        if (exponent.signum() < 0) {
            throw new ArithmeticException();
        } else if (exponent.signum() == 0) {
            a = JSCLInteger.valueOf(1);
        } else {
            final JSCLInteger e[] = exponent.divideAndRemainder(JSCLInteger.valueOf(2));
            if (e[1].signum() == 0) {
                final Generic c = pow(e[0]);
                a = c.multiply(c);
            } else {
                a = multiply(pow(exponent.subtract(JSCLInteger.valueOf(1))));
            }
        }
        return a;
    }

    public Generic abs() {
        return signum()<0?negate():this;
    }

    public abstract Generic negate();
    public abstract int signum();
    public abstract int degree();

//    public abstract Generic mod(Generic generic);
//    public abstract Generic modPow(Generic exponent, Generic generic);
//    public abstract Generic modInverse(Generic generic);
//    public abstract boolean isProbablePrime(int certainty);
    public abstract Generic antiderivative(Variable variable) throws NotIntegrableException;
    public abstract Generic derivative(Variable variable);
    public abstract Generic substitute(Variable variable, Generic generic);
    public abstract Generic function(Variable variable);
    public abstract Generic expand();
    public abstract Generic factorize();
    public abstract Generic elementary();
    public abstract Generic simplify();
    public abstract Generic numeric();
    public abstract Generic valueof(Generic generic);
    public abstract Generic[] sumValue();
    public abstract Generic[] productValue() throws NotProductException;
    public abstract Power powerValue() throws NotPowerException;
    public abstract Expression expressionValue() throws NotExpressionException;
    public abstract JSCLInteger integerValue() throws NotIntegerException;
    public JSCLBoolean booleanValue() throws NotBooleanException {
        try {
            return JSCLBoolean.valueOf(integerValue().signum() != 0);
        } catch (final NotIntegerException e) {}
        throw new NotBooleanException();
    }
    public JSCLVector vectorValue() throws NotVectorException {
        final Generic p = GenericVariable.content(this);
        if (p instanceof JSCLVector) {
            return (JSCLVector) p;
        }
        throw new NotVectorException();
    }
    public abstract Variable variableValue() throws NotVariableException;
    public abstract Variable[] variables();
    public abstract boolean isPolynomial(Variable variable);
    public abstract boolean isConstant(Variable variable);

    public boolean isZero() {
        return signum() == 0;
    }

    public boolean isOne() {
        return equals(JSCLInteger.valueOf(1));
    }

    public boolean isIdentity(Variable variable) {
        try {
            return variableValue().isIdentity(variable);
        } catch (NotVariableException e) {
            return false;
        }
    }

    public abstract int compareTo(Generic generic);

    public int compareTo(Object o) {
        return compareTo((Generic)o);
    }

    public boolean equals(Object obj) {
        if(obj instanceof Generic) {
            return compareTo((Generic)obj)==0;
        } else return false;
    }

    public abstract String toMathML();
}
