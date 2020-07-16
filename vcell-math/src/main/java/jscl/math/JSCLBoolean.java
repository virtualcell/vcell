package jscl.math;

import java.math.BigInteger;

public class JSCLBoolean extends ModularInteger {
    public JSCLBoolean(BigInteger content) {
        super(content,BigInteger.valueOf(2));
    }

    public static JSCLBoolean valueOf(boolean value) {
        return new JSCLBoolean(BigInteger.valueOf(value?1:0));
    }

    public JSCLBoolean and(JSCLBoolean bool) {
        return valueOf(content.signum() != 0 && bool.content.signum() != 0);
    }

    public JSCLBoolean or(JSCLBoolean bool) {
        return valueOf(content.signum() != 0 || bool.content.signum() != 0);
    }

    public JSCLBoolean xor(JSCLBoolean bool) {
        return valueOf(content.signum() != 0 ^ bool.content.signum() != 0);
    }

    public JSCLBoolean not() {
        return valueOf(content.signum() == 0);
    }

    public JSCLBoolean implies(JSCLBoolean bool) {
        return valueOf(content.signum() == 0 || bool.content.signum() != 0);
    }

    @Override
    public String toMathML() {
	return content.signum() != 0?"<true/>":"<false/>";
    }

    @Override
    protected JSCLBoolean newinstance(BigInteger content) {
        return new JSCLBoolean(content);
    }
}
