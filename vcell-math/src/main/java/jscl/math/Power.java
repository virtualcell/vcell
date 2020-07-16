package jscl.math;

public class Power {
    final Generic value;
    final int exponent;

    public Power(Generic value, int exponent) {
        this.value=value;
        this.exponent=exponent;
    }

    public Generic value() {
        return value;
    }

    public int exponent() {
        return exponent;
    }

    public String toString() {
        return "("+value+", "+exponent+")";
    }
}
