package jscl.math;

import jscl.math.function.*;
import jscl.math.function.trigonometric.*;
import jscl.math.function.hyperbolic.*;
import jscl.math.function.bool.*;
import jscl.math.operator.*;
import jscl.math.operator.vector.*;
import jscl.math.operator.matrix.*;
import jscl.math.operator.product.*;
import jscl.math.operator.number.*;
import jscl.math.numeric.*;

public class Predef {
	public static final Generic pi=Constant.pi;
	public static final Generic oo=Constant.infinity;

	public static final Generic lex=Groebner.lex;
	public static final Generic tdl=Groebner.tdl;
	public static final Generic drl=Groebner.drl;

	public static Generic elim(int k) {
		return Groebner.elim.apply(new Generic[] {JSCLInteger.valueOf(k)});
	}

	public static Generic cl(int p, int q) {
		return GeometricProduct.cl.apply(new Generic[] {JSCLInteger.valueOf(p), JSCLInteger.valueOf(q)});
	}

	public static ImplicitFunction.Curried function(String name, int derivation) {
		return ImplicitFunction.apply(name, new int[] {derivation});
	}

	public static ImplicitFunction.Curried[] function(String name, int derivation, int n) {
		return ImplicitFunction.apply(name, new int[] {derivation}, n);
	}

	public static ImplicitFunction.Curried function(String name, int derivation[]) {
		return ImplicitFunction.apply(name, derivation);
	}

	public static ImplicitFunction.Curried[] function(String name, int derivation[], int n) {
		return ImplicitFunction.apply(name, derivation, n);
	}

	public static Generic[] root(Generic parameter[]) {
		return Root.apply(parameter);
	}

	public static Generic cubic(Generic a) {
		return new Cubic(a).expressionValue();
	}

	public static Generic sqrt(Generic a) {
		return new Sqrt(a).expressionValue();
	}

	public static Generic sin(Generic a) {
		return new Sin(a).expressionValue();
	}

	public static Generic cos(Generic a) {
		return new Cos(a).expressionValue();
	}

	public static Generic tan(Generic a) {
		return new Tan(a).expressionValue();
	}

	public static Generic cot(Generic a) {
		return new Cot(a).expressionValue();
	}

	public static Generic arcsin(Generic a) {
		return new Asin(a).expressionValue();
	}

	public static Generic arccos(Generic a) {
		return new Acos(a).expressionValue();
	}

	public static Generic arctan(Generic a) {
		return new Atan(a).expressionValue();
	}

	public static Generic arccot(Generic a) {
		return new Acot(a).expressionValue();
	}

	public static Generic sinh(Generic a) {
		return new Sinh(a).expressionValue();
	}

	public static Generic cosh(Generic a) {
		return new Cosh(a).expressionValue();
	}

	public static Generic tanh(Generic a) {
		return new Tanh(a).expressionValue();
	}

	public static Generic coth(Generic a) {
		return new Coth(a).expressionValue();
	}

	public static Generic arcsinh(Generic a) {
		return new Asinh(a).expressionValue();
	}

	public static Generic arccosh(Generic a) {
		return new Acosh(a).expressionValue();
	}

	public static Generic arctanh(Generic a) {
		return new Atanh(a).expressionValue();
	}

	public static Generic arccoth(Generic a) {
		return new Acoth(a).expressionValue();
	}

	public static Generic exp(Generic a) {
		return new Exp(a).expressionValue();
	}

	public static Generic log(Generic a) {
		return new Log(a).expressionValue();
	}

	public static Generic power(Generic a, Generic b) {
		return new Pow(a, b).expressionValue();
	}

	public static Generic[] vector(String name, int prime, int n) {
		return new JSCLVector(name, prime, n).elements();
	}

	public static Generic[] vector(String name, int n) {
		return new JSCLVector(name, 0, n).elements();
	}

	public static Generic[][] matrix(String name, int n, int p) {
		return new Matrix(name, 0, n, p).elements();
	}

	public static Generic[][] matrix(String name, int n) {
		return new Matrix(name, 0, n, n).elements();
	}

	public static Generic variable(String name) {
		return new Constant(name).expressionValue();
	}

	public static Generic variable(String name, int prime) {
		return new Constant(name, prime).expressionValue();
	}

	public static Generic integral(Generic expression, Generic variable) {
		return new IndefiniteIntegral(expression, variable).expressionValue();
	}

	public static Generic integral(Generic expression, Generic variable, Generic n1, Generic n2) {
		return new Integral(expression, variable, n1, n2).expressionValue();
	}

	public static Generic d(Generic expression, Generic variable) {
		return new Derivative(expression, variable, variable, JSCLInteger.valueOf(1)).expressionValue();
	}

	public static Generic d(Generic expression, Generic variable, Generic value) {
		return new Derivative(expression, variable, value, JSCLInteger.valueOf(1)).expressionValue();
	}

	public static Generic d(Generic expression, Generic variable, Generic value, Generic order) {
		return new Derivative(expression, variable, value, order).expressionValue();
	}

	public static Generic taylor(Generic expression, Generic variable, Generic value, Generic order) {
		return new Taylor(expression, variable, value, order).expressionValue();
	}

	public static Generic modint(String str, String mod) {
		return ModularInteger.valueOf(str, mod);
	}

	public static Generic integer(String str) {
		return JSCLInteger.valueOf(str);
	}

	public static Generic rational(String n, String d) {
		return Rational.valueOf(n, d);
	}

	public static Generic real(double val) {
		return new NumericWrapper(JSCLDouble.valueOf(val));
	}

	public static Generic complex(double real, double imag) {
		return new NumericWrapper(Complex.valueOf(real, imag));
	}

	public static Generic bool(boolean value) {
		return JSCLBoolean.valueOf(value);
	}

	public static Generic vector(Generic element[]) {
		return new JSCLVector(element);
	}

	public static Generic matrix(Generic element[][]) {
		return new Matrix(element);
	}

	public static Object graph(Generic a) {
		return jscl.engine.Graph.apply(a.expand());
	}

	public static Object graph(Generic a[]) {
		return jscl.engine.Graph.apply(new JSCLVector(a).expand());
	}

	public static Generic function(Generic expression, Generic variable) {
		return new Graph(expression, variable).expressionValue();
	}

	public static Generic elementary(Generic expression) {
		return new Elementary(expression).expressionValue();
	}

	public static Generic factorize(Generic expression) {
		return new Factorize(expression).expressionValue();
	}

	public static Generic simplify(Generic expression) {
		return new Simplify(expression).expressionValue();
	}

	public static Generic numeric(Generic expression) {
		return new jscl.math.operator.Numeric(expression).expressionValue();
	}

	public static Generic quote(Generic expression) {
		return new Quote(expression).expressionValue();
	}

	public static Generic sum(Generic expression, Generic variable, Generic n1, Generic n2) {
		return new Sum(expression, variable, n1, n2).expressionValue();
	}

	public static Generic product(Generic expression, Generic variable, Generic n1, Generic n2) {
		return new Product(expression, variable, n1, n2).expressionValue();
	}

	public static Generic limit(Generic expression, Generic variable, Generic limit) {
		return new Limit(expression, variable, limit, JSCLInteger.valueOf(0)).expressionValue();
	}

	public static Generic limit(Generic expression, Generic variable, Generic limit, int direction) {
		return new Limit(expression, variable, limit, JSCLInteger.valueOf(direction)).expressionValue();
	}

	public static Generic factorial(Generic expression) {
		return new Factorial(expression).expressionValue();
	}

	public static Generic factorial(Generic expression, Generic order) {
		return new Factorial(expression, order).expressionValue();
	}

	public static Generic quotient(Generic expression1, Generic expression2) {
		return new Division(expression1, expression2).expressionValue();
	}

	public static Generic rem(Generic expression1, Generic expression2) {
		return new Remainder(expression1, expression2).expressionValue();
	}

	public static Generic factorof(Generic expression1, Generic expression2) {
		return new FactorOf(expression1, expression2).expressionValue();
	}

	public static Generic eq(Generic expression1, Generic expression2) {
		return new Comparison("eq", expression1, expression2).expressionValue();
	}

	public static Generic neq(Generic expression1, Generic expression2) {
		return new Comparison("neq", expression1, expression2).expressionValue();
	}

	public static Generic leq(Generic expression1, Generic expression2) {
		return new Comparison("leq", expression1, expression2).expressionValue();
	}

	public static Generic lt(Generic expression1, Generic expression2) {
		return new Comparison("lt", expression1, expression2).expressionValue();
	}

	public static Generic geq(Generic expression1, Generic expression2) {
		return new Comparison("geq", expression1, expression2).expressionValue();
	}

	public static Generic gt(Generic expression1, Generic expression2) {
		return new Comparison("gt", expression1, expression2).expressionValue();
	}

	public static Generic approx(Generic expression1, Generic expression2) {
		return new Comparison("approx", expression1, expression2).expressionValue();
	}

	public static Generic and(Generic expression1, Generic expression2) {
		return new And(expression1, expression2).expressionValue();
	}

	public static Generic or(Generic expression1, Generic expression2) {
		return new Or(expression1, expression2).expressionValue();
	}

	public static Generic xor(Generic expression1, Generic expression2) {
		return new Xor(expression1, expression2).expressionValue();
	}

	public static Generic not(Generic expression) {
		return new Not(expression).expressionValue();
	}

	public static Generic implies(Generic expression1, Generic expression2) {
		return new Implies(expression1, expression2).expressionValue();
	}

	public static Generic transpose(Generic matrix) {
		return new Transpose(matrix).expressionValue();
	}

	public static Generic trace(Generic matrix) {
		return new Trace(matrix).expressionValue();
	}

	public static Generic determinant(Generic matrix) {
		return new Determinant(matrix).expressionValue();
	}

	public static Generic grad(Generic expression, Generic variable[]) {
		return new Grad(expression, new JSCLVector(variable)).expressionValue();
	}

	public static Generic divergence(Generic vector, Generic variable[]) {
		return new Divergence(vector, new JSCLVector(variable)).expressionValue();
	}

	public static Generic curl(Generic vector, Generic variable[]) {
		return new Curl(vector, new JSCLVector(variable)).expressionValue();
	}

	public static Generic laplacian(Generic expression, Generic variable[]) {
		return new Laplacian(expression, new JSCLVector(variable)).expressionValue();
	}

	public static Generic dalembertian(Generic expression, Generic variable[]) {
		return new Dalembertian(expression, new JSCLVector(variable)).expressionValue();
	}

	public static Generic jacobian(Generic vector, Generic variable[]) {
		return new Jacobian(vector, new JSCLVector(variable)).expressionValue();
	}

	public static Generic del(Generic vector, Generic variable[]) {
		return new Del(vector, new JSCLVector(variable), JSCLInteger.valueOf(0)).expressionValue();
	}

	public static Generic del(Generic vector, Generic variable[], Generic algebra) {
		return new Del(vector, new JSCLVector(variable), algebra).expressionValue();
	}

	public static Generic groebner(Generic generic, Generic variable[]) {
		return new Groebner(generic, new JSCLVector(variable), lex, JSCLInteger.valueOf(0)).expressionValue();
	}

	public static Generic groebner(Generic generic, Generic variable[], Generic ordering) {
		return new Groebner(generic, new JSCLVector(variable), ordering, JSCLInteger.valueOf(0)).expressionValue();
	}

	public static Generic groebner(Generic generic, Generic variable[], Generic ordering, int modulo) {
		return new Groebner(generic, new JSCLVector(variable), ordering, JSCLInteger.valueOf(modulo)).expressionValue();
	}

	public static Generic coef(Generic expression, Generic variable) {
		return new Coefficient(expression, variable).expressionValue();
	}

	public static Generic subst(Generic expression, Generic variable, Generic value) {
		return new Substitute(expression, variable, value).expressionValue();
	}

	public static Generic subst(Generic expression, Generic variable[], Generic value[]) {
		return new Substitute(expression, new JSCLVector(variable), new JSCLVector(value)).expressionValue();
	}

	public static Generic solve(Generic expression, Generic variable) {
		return new Solve(expression, variable, JSCLInteger.valueOf(0)).expressionValue();
	}

	public static Generic solve(Generic expression, Generic variable, int subscript) {
		return new Solve(expression, variable, JSCLInteger.valueOf(subscript)).expressionValue();
	}

	public static Generic complex(Generic vector1[], Generic vector2[]) {
		return new ComplexProduct(new JSCLVector(vector1), new JSCLVector(vector2)).expressionValue();
	}

	public static Generic quaternion(Generic vector1[], Generic vector2[]) {
		return new QuaternionProduct(new JSCLVector(vector1), new JSCLVector(vector2)).expressionValue();
	}

	public static Generic vector(Generic vector1[], Generic vector2[]) {
		return new VectorProduct(new JSCLVector(vector1), new JSCLVector(vector2)).expressionValue();
	}

	public static Generic matrix(Generic matrix1[][], Generic matrix2[][]) {
		return new MatrixProduct(new Matrix(matrix1), new Matrix(matrix2)).expressionValue();
	}

	public static Generic tensor(Generic matrix1[][], Generic matrix2[][]) {
		return new TensorProduct(new Matrix(matrix1), new Matrix(matrix2)).expressionValue();
	}

	public static Generic geometric(Generic vector1[], Generic vector2[]) {
		return new GeometricProduct(new JSCLVector(vector1), new JSCLVector(vector2), JSCLInteger.valueOf(0)).expressionValue();
	}

	public static Generic geometric(Generic vector1[], Generic vector2[], Generic algebra) {
		return new GeometricProduct(new JSCLVector(vector1), new JSCLVector(vector2), algebra).expressionValue();
	}

	public static Generic mod(Generic integer, Generic modulo) {
		return new Mod(integer, modulo).expressionValue();
	}

	public static Generic modpow(Generic integer, Generic exponent, Generic modulo) {
		return new ModPow(integer, exponent, modulo).expressionValue();
	}

	public static Generic modinv(Generic integer, Generic modulo) {
		return new ModInverse(integer, modulo).expressionValue();
	}

	public static Generic phi(Generic integer) {
		return new EulerPhi(integer).expressionValue();
	}

	public static Generic primitiveroots(Generic integer) {
		return new PrimitiveRoots(integer).expressionValue();
	}

	public static Generic C(Generic n, Generic p) {
		return new Binomial(n, p).expressionValue();
	}
}
