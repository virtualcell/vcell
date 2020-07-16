package jscl.math;

import java.math.BigInteger;

public class ModularInteger extends JSCLInteger implements Field {
    private final BigInteger modulo;

    public ModularInteger(BigInteger content, BigInteger modulo) {
        super(content.mod(modulo));
        this.modulo=modulo;
    }

    public JSCLInteger divide(ModularInteger integer) {
        return multiply(integer.inverse());
    }

    @Override
    public JSCLInteger integerDivide(JSCLInteger integer) {
        return divide(valueof(integer));
    }

    @Override
    public ModularInteger inverse() {
        return newinstance(content.modInverse(modulo));
    }

    public ModularInteger gcd(ModularInteger integer) {
        return integer.signum()==0?this:integer;
    }

    @Override
    public ModularInteger gcd(JSCLInteger integer) {
        return gcd(valueof(integer));
    }

    @Override
    public ModularInteger pow(final JSCLInteger exponent) {
        return newinstance(content.modPow(exponent.content(),modulo));
    }

    @Override
    public ModularInteger negate() {
        return newinstance(modulo.subtract(content));
    }

    @Override
    public ModularInteger valueof(Generic generic) {
        return (ModularInteger)super.valueof(generic);
    }

    public static ModularInteger valueOf(String str, String mod) {
        return new ModularInteger(new BigInteger(str), new BigInteger(mod));
    }

    public String toString() {
        return content.toString();
    }

    @Override
    public String toMathML() {
	return "<cn type=\"integer\" base=\"" + modulo + "\">" + content + "</cn>";
    }

    @Override
    protected ModularInteger newinstance(BigInteger content) {
        return new ModularInteger(content,modulo);
    }
}
