package jscl.math.polynomial;

public class JSCLBoolean extends ModularInteger {
    public static final JSCLBoolean factory=new JSCLBoolean(0);

    public JSCLBoolean(long content) {
        super(content,2);
    }

    public static JSCLBoolean valueOf(boolean value) {
        return new JSCLBoolean(value?1:0);
    }

    @Override
    public String toMathML() {
	return content != 0?"<true/>":"<false/>";
    }

    @Override
    protected JSCLBoolean newinstance(long content) {
        return new JSCLBoolean(content);
    }
}
