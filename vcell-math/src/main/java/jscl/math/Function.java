package jscl.math;

public abstract class Function extends Generic implements java.io.Serializable {
    public static final Function identity = new Function() {
        public double apply(double value) {
            return value;
        }
    };

    public static Function valueOf(final double cste) {
        return new Function() {
            public double apply(double value) {
                return cste;
            }
        };
    }

    public static Function valueOf(JSCLInteger integer) {
        return valueOf(integer.content().doubleValue());
    }

    public static Function valueOf(Rational rational) {
        return valueOf(rational.numerator().doubleValue()/rational.denominator().doubleValue());
    }

    public abstract double apply(double value);

    public Function add(final Function func) {
        return new Function() {
            public double apply(double value) {
                return Function.this.apply(value)+func.apply(value);
            }
        };
    }

    public Generic add(Generic generic) {
        if(generic instanceof Function) {
            return add((Function)generic);
        } else {
            return add(valueof(generic));
        }
    }

    public Function subtract(final Function func) {
        return new Function() {
            public double apply(double value) {
                return Function.this.apply(value)-func.apply(value);
            }
        };
    }

    public Generic subtract(Generic generic) {
        if(generic instanceof Function) {
            return subtract((Function)generic);
        } else {
            return subtract(valueof(generic));
        }
    }

    public Function multiply(final Function func) {
        return new Function() {
            public double apply(double value) {
                return Function.this.apply(value)*func.apply(value);
            }
        };
    }

    public Generic multiply(Generic generic) {
        if(generic instanceof Function) {
            return multiply((Function)generic);
        } else {
            return multiply(valueof(generic));
        }
    }

    public Function divide(final Function func) {
        return new Function() {
            public double apply(double value) {
                return Function.this.apply(value)/func.apply(value);
            }
        };
    }

    public Generic divide(Generic generic) {
        if(generic instanceof Function) {
            return divide((Function)generic);
        } else {
            return divide(valueof(generic));
        }
    }

    public Generic gcd(Generic generic) {
        return null;
    }

    public Generic gcd() {
        return null;
    }

    @Override
    public Function pow(int exponent) {
        return (Function)super.pow(exponent);
    }

    public Generic abs() {
        return new Function() {
            public double apply(double value) {
                return Math.abs(Function.this.apply(value));
            }
        };
    }

    public Function negate() {
        return new Function() {
            public double apply(double value) {
                return -Function.this.apply(value);
            }
        };
    }

    public int signum() {
        return 0;
    }

    public int degree() {
        return 0;
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        return null;
    }

    public Generic derivative(Variable variable) {
        return null;
    }

    public Generic substitute(Variable variable, Generic generic) {
        return null;
    }

    public Generic expand() {
        return this;
    }

    public Generic factorize() {
        return this;
    }

    public Generic elementary() {
        return this;
    }

    public Generic simplify() {
        return this;
    }

    public Generic function(Variable variable) {
        return this;
    }

    public Generic numeric() {
        return null;
    }

    public Generic valueof(Generic generic) {
        if(generic instanceof Function) {
            return generic;
        } else {
            return generic.function(new TechnicalVariable("t"));
        }
    }

    public Generic[] sumValue() {
        return null;
    }

    public Generic[] productValue() throws NotProductException {
        return null;
    }

    public Power powerValue() throws NotPowerException {
        return null;
    }

    public Expression expressionValue() throws NotExpressionException {
        throw new NotExpressionException();
    }

    public JSCLInteger integerValue() throws NotIntegerException {
        throw new NotIntegerException();
    }

    public Variable variableValue() throws NotVariableException {
        throw new NotVariableException();
    }

    public Variable[] variables() {
        return new Variable[0];
    }

    public boolean isPolynomial(Variable variable) {
        return true;
    }

    public boolean isConstant(Variable variable) {
        return true;
    }

    public Generic log() {
        return new Function() {
            public double apply(double value) {
                return Math.log(Function.this.apply(value));
            }
        };
    }

    public Generic exp() {
        return new Function() {
            public double apply(double value) {
                return Math.exp(Function.this.apply(value));
            }
        };
    }

    public Function pow(final Function func) {
        return new Function() {
            public double apply(double value) {
                return Math.pow(Function.this.apply(value),func.apply(value));
            }
        };
    }

    public Generic pow(Generic generic) {
        if(generic instanceof Function) {
            return pow((Function)generic);
        } else throw new ArithmeticException();
    }

    public Function sqrt() {
        return new Function() {
            public double apply(double value) {
                return Math.sqrt(Function.this.apply(value));
            }
        };
    }

    public Generic nthrt(int n) {
        return pow(valueOf(1./n));
    }

    public static Generic root(int subscript, Generic parameter[]) {
        throw new ArithmeticException();
    }

    public Generic conjugate() {
        return this;
    }

    public Generic acos() {
        return new Function() {
            public double apply(double value) {
                return Math.acos(Function.this.apply(value));
            }
        };
    }

    public Generic asin() {
        return new Function() {
            public double apply(double value) {
                return Math.asin(Function.this.apply(value));
            }
        };
    }

    public Generic atan() {
        return new Function() {
            public double apply(double value) {
                return Math.atan(Function.this.apply(value));
            }
        };
    }

    public Generic acot() {
        return valueOf(Math.PI/2).subtract(atan());
    }

    public Generic cos() {
        return new Function() {
            public double apply(double value) {
                return Math.cos(Function.this.apply(value));
            }
        };
    }

    public Generic sin() {
        return new Function() {
            public double apply(double value) {
                return Math.sin(Function.this.apply(value));
            }
        };
    }

    public Generic tan() {
        return new Function() {
            public double apply(double value) {
                return Math.tan(Function.this.apply(value));
            }
        };
    }

    public Generic cot() {
        return tan().inverse();
    }

    public Generic acosh() {
        return add(valueOf(-1).add(pow(2)).sqrt()).log();
    }

    public Generic asinh() {
        return add(valueOf(1).add(pow(2)).sqrt()).log();
    }

    public Generic atanh() {
        return valueOf(1).add(this).divide(valueOf(1).subtract(this)).log().divide(valueOf(2));
    }

    public Generic acoth() {
        return valueOf(1).add(this).divide(valueOf(1).subtract(this)).negate().log().divide(valueOf(2));
    }

    public Generic cosh() {
        return new Function() {
            public double apply(double value) {
                return Math.cosh(Function.this.apply(value));
            }
        };
    }

    public Generic sinh() {
        return new Function() {
            public double apply(double value) {
                return Math.sinh(Function.this.apply(value));
            }
        };
    }

    public Generic tanh() {
        return new Function() {
            public double apply(double value) {
                return Math.tanh(Function.this.apply(value));
            }
        };
    }

    public Generic coth() {
        return valueOf(1).add(exp().pow(2)).divide(valueOf(1).subtract(exp().pow(2))).negate();
    }

    @Override
    public boolean isZero() {
        return false;
    }

    @Override
    public boolean isOne() {
        return false;
    }

    public int compareTo(Function func) {
        return 0;
    }

    public int compareTo(Generic generic) {
        if(generic instanceof Function) {
            return compareTo((Function)generic);
        } else {
            return compareTo(valueof(generic));
        }
    }

    public String toString() {
        return "<function>";
    }

    public String toMathML() {
	return "<ci>function</ci>";
    }

    protected Generic newinstance() {
        return null;
    }
}
