package org.vcell.client.viz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.solver.fenics.FenicsBundle;

import cbit.vcell.math.Constant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.parser.ASTFuncNode.FunctionType;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.math.VariableType;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;

/**
 * The VCell functions of a FEniCSx run, for the field viewer. A FEniCSx results bundle holds only the solver's
 * state variables; the functions of them (a math {@code Function} such as {@code J = k*A*B}) come from the
 * simulation's MathDescription, which the desktop has for the open document: the same flattened list a
 * finite-volume run writes to its {@code .functions} file ({@link SimulationSymbolTable#createAnnotatedFunctionsList}),
 * with this job's constants (scan values included) substituted.
 * <p>
 * A function is evaluated <b>at the mesh vertices</b> from the stored variables' P1 values there, the vertex's
 * position (x, y, z; on a moving mesh the row's) and the row's time (t); a value between vertices is then the
 * P1 interpolation of those vertex values, exactly as {@code /field}'s vertex values are drawn. (For a nonlinear
 * function this differs from evaluating the function of interpolated variables; this is the order that agrees
 * with the 3D view, and the finite-volume rule of evaluating at the solver's data points.)
 * <p>
 * Supported: a function on one domain of the bundle (volume or membrane) of that domain's stored variables, x,
 * y, z and t. Refused, with a message: functions of another domain's variables (on a membrane: the adjacent
 * volume values, deferred), membrane normals, region sizes, field data and gradients, region variables, and
 * anything else the bundle cannot supply.
 */
final class FenicsFunctions {

	private static final Logger LG = LogManager.getLogger(FenicsFunctions.class);

	/** No functions: a run registered without its simulation. */
	static final FenicsFunctions NONE = new FenicsFunctions(List.of(), Set.of());

	/** One function: its flattened expression (constants substituted), its declared domain (null: any) and kind. */
	record Definition(String name, Expression expression, String domain, boolean membrane, String error) {
	}

	private final Map<String, Definition> definitions = new LinkedHashMap<>();
	private final Set<String> regionVariables;

	FenicsFunctions(List<Definition> definitions, Set<String> regionVariables) {
		for (Definition d : definitions) {
			this.definitions.put(d.name(), d);
		}
		this.regionVariables = Collections.unmodifiableSet(new LinkedHashSet<>(regionVariables));
	}

	/**
	 * The functions of {@code simulation}'s job {@code jobIndex}, as the finite-volume solver would write them,
	 * or {@link #NONE} if they cannot be derived (logged).
	 */
	static FenicsFunctions fromSimulation(Simulation simulation, int jobIndex) {
		if (simulation == null) {
			return NONE;
		}
		try {
			MathDescription math = simulation.getMathDescription();
			SimulationSymbolTable symbols = new SimulationSymbolTable(simulation, new MathOverrides.ScanIndex(jobIndex));
			Set<String> regionVariables = new LinkedHashSet<>();
			for (Variable v : symbols.getVariables()) {
				if (v instanceof VolumeRegionVariable || v instanceof MembraneRegionVariable) {
					regionVariables.add(v.getName());
				}
			}
			List<Definition> list = new ArrayList<>();
			for (AnnotatedFunction f : symbols.createAnnotatedFunctionsList(math)) {
				VariableType type = f.getFunctionType();
				boolean membrane = type != null && (type.equals(VariableType.MEMBRANE) || type.equals(VariableType.MEMBRANE_REGION));
				String error = f.getErrorString() == null || f.getErrorString().isEmpty() ? null : f.getErrorString();
				if (type != null && (type.equals(VariableType.VOLUME_REGION) || type.equals(VariableType.MEMBRANE_REGION))) {
					error = "it has one value per region (a region function)";
				}
				Expression exp = f.getExpression() == null ? null : substituteConstants(new Expression(f.getExpression()), symbols);
				list.add(new Definition(f.getName(), exp, f.getDomain() == null ? null : f.getDomain().getName(), membrane, error));
			}
			return new FenicsFunctions(list, regionVariables);
		} catch (Exception e) {
			LG.warn("could not derive the functions of " + simulation.getName() + " for the field viewer: " + e.getMessage(), e);
			return NONE;
		}
	}

	/** replaces each constant (this job's value, scans included) by its number */
	private static Expression substituteConstants(Expression exp, SimulationSymbolTable symbols) throws ExpressionException {
		String[] names = exp.getSymbols();
		if (names != null) {
			for (String name : names) {
				SymbolTableEntry entry = symbols.getEntry(name);
				if (entry instanceof Constant) {
					Expression value = new Expression(((Constant) entry).getExpression());
					value.bindExpression(symbols);
					exp.substituteInPlace(new Expression(name), new Expression(value.evaluateConstant()));
				}
			}
		}
		return exp.flatten();
	}

	boolean isEmpty() {
		return definitions.isEmpty();
	}

	boolean has(String name) {
		return definitions.containsKey(name);
	}

	/** the functions that can be evaluated on {@code domain} of {@code bundle}, in definition order */
	List<String> namesFor(FenicsBundle bundle, String domain) {
		List<String> names = new ArrayList<>();
		for (Definition d : definitions.values()) {
			try {
				compile(bundle, domain, d.name());
				names.add(d.name());
			} catch (IllegalArgumentException notHere) {
				// not a function of this domain (or not supported): listed nowhere it cannot be drawn
			}
		}
		return names;
	}

	/** the domain a function is drawn on when a request names none: its declared one, else the first it compiles on */
	String domainOf(FenicsBundle bundle, String name) {
		Definition d = definitions.get(name);
		if (d == null) {
			throw new IllegalArgumentException("unknown function '" + name + "'");
		}
		if (d.domain() != null && bundle.getDomains().containsKey(d.domain())) {
			return d.domain();
		}
		IllegalArgumentException why = null;
		for (String domain : bundle.getDomains().keySet()) {
			try {
				compile(bundle, domain, name);
				return domain;
			} catch (IllegalArgumentException e) {
				why = why == null ? e : why;
			}
		}
		throw why != null ? why : new IllegalArgumentException("function '" + name + "' fits no domain of this run");
	}

	/**
	 * {@code name} ready to evaluate on {@code domain}; an {@link IllegalArgumentException} (a 400) saying why
	 * it cannot be.
	 */
	Compiled compile(FenicsBundle bundle, String domain, String name) {
		Definition d = definitions.get(name);
		if (d == null) {
			throw new IllegalArgumentException("unknown function '" + name + "'");
		}
		String what = "function '" + name + "'";
		if (d.error() != null) {
			throw new IllegalArgumentException(what + " cannot be shown: " + d.error());
		}
		if (d.expression() == null) {
			throw new IllegalArgumentException(what + " has no expression");
		}
		FenicsBundle.Domain target = bundle.domain(domain);
		if (d.domain() != null && !d.domain().equals(domain)) {
			throw new IllegalArgumentException(what + " is defined on '" + d.domain() + "', not '" + domain + "'");
		}
		if (d.membrane() != target.isMembrane()) {
			throw new IllegalArgumentException(what + " is a " + (d.membrane() ? "membrane" : "volume") + " function; '"
					+ domain + "' is a " + (target.isMembrane() ? "membrane" : "volume") + " domain");
		}
		Expression exp = d.expression();
		FunctionInvocation[] calls = exp.getFunctionInvocations(null);
		if (calls != null) {
			for (FunctionInvocation call : calls) {
				if (call.getFunctionId() != FunctionType.USERDEFINED) {
					continue; // exp, pow, …: plain arithmetic
				}
				String f = call.getFunctionName();
				if (f.startsWith("normal")) {
					throw new IllegalArgumentException(what + " uses the membrane normal (" + f + "), which FEniCSx results do not record yet");
				}
				if (f.startsWith("vcRegion")) {
					throw new IllegalArgumentException(what + " uses a region size (" + f + "), which the field viewer does not evaluate for FEniCSx runs yet");
				}
				if (f.equals("vcField") || f.equals("field")) {
					throw new IllegalArgumentException(what + " uses field data (" + f + "), which the field viewer does not evaluate for FEniCSx runs");
				}
				if (f.equals("vcGrad") || f.equals("grad")) {
					throw new IllegalArgumentException(what + " uses a gradient (" + f + "), which the field viewer does not evaluate for FEniCSx runs");
				}
				throw new IllegalArgumentException(what + " uses " + f + "(), which the field viewer does not evaluate for FEniCSx runs");
			}
		}
		Set<String> onDomain = new LinkedHashSet<>();
		Set<String> elsewhere = new LinkedHashSet<>();
		for (FenicsBundle.Variable v : bundle.getVariables()) {
			(v.domain().equals(domain) ? onDomain : elsewhere).add(v.name());
		}
		List<String> variables = new ArrayList<>();
		String[] symbols = exp.getSymbols();
		if (symbols != null) {
			for (String s : symbols) {
				if (s.equals("t") || s.equals("x") || s.equals("y") || s.equals("z")) {
					continue;
				}
				if (onDomain.contains(s)) {
					variables.add(s);
					continue;
				}
				if (regionVariables.contains(s)) {
					throw new IllegalArgumentException(what + " uses the region variable '" + s + "', which FEniCSx results do not hold");
				}
				if (elsewhere.contains(s) || s.endsWith("_INSIDE") || s.endsWith("_OUTSIDE")) {
					throw new IllegalArgumentException(what + " uses '" + s + "' of another domain"
							+ (target.isMembrane() ? " (the adjacent volume's values on a membrane are not supported for FEniCSx runs yet)" : ""));
				}
				if (definitions.containsKey(s)) {
					throw new IllegalArgumentException(what + " uses the function '" + s + "' unflattened");
				}
				throw new IllegalArgumentException(what + " uses '" + s + "', which is not a variable of '" + domain + "'");
			}
		}
		return new Compiled(name, exp, variables.toArray(new String[0]));
	}

	/**
	 * A function bound to its arguments: {@code t, x, y, z} and the stored variables {@link #variables}, in that
	 * order, evaluated one vertex at a time.
	 */
	static final class Compiled {
		final String name;
		final String[] variables;
		private final Expression bound;
		private final double[] args;

		private Compiled(String name, Expression exp, String[] variables) {
			this.name = name;
			this.variables = variables;
			String[] names = new String[4 + variables.length];
			names[0] = "t";
			names[1] = "x";
			names[2] = "y";
			names[3] = "z";
			System.arraycopy(variables, 0, names, 4, variables.length);
			try {
				this.bound = new Expression(exp);
				this.bound.bindExpression(new SimpleSymbolTable(names));
			} catch (ExpressionException e) {
				throw new IllegalArgumentException("function '" + name + "' cannot be bound: " + e.getMessage(), e);
			}
			this.args = new double[names.length];
		}

		/**
		 * The function at vertex {@code v} of a row: {@code points} is the row's mesh (x,y,z per vertex),
		 * {@code values[k][v]} the {@code k}-th variable's value there. NaN where it cannot be evaluated (an
		 * unwritten row's NaN, a division by zero, a domain error).
		 */
		double at(double t, double[] points, int v, double[][] values) {
			args[0] = t;
			args[1] = points[3 * v];
			args[2] = points[3 * v + 1];
			args[3] = points[3 * v + 2];
			for (int k = 0; k < variables.length; k++) {
				args[4 + k] = values[k][v];
			}
			try {
				return bound.evaluateVector(args);
			} catch (ExpressionException e) {
				return Double.NaN;
			}
		}
	}
}
