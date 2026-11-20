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
import cbit.vcell.math.InsideVariable;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.OutsideVariable;
import cbit.vcell.math.SubDomain;
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
 * y, z and t; and on a membrane, the <b>adjacent volume values</b> -- a volume variable of either compartment the
 * membrane separates, as VCell's finite-volume evaluation resolves it: {@code c_INSIDE} / {@code c_OUTSIDE} are
 * {@code c} in the membrane's inside / outside compartment (the math's {@link MembraneSubDomain}), and a plain
 * {@code c} is {@code c} in whichever adjacent compartment holds it. At a membrane vertex such a value is the
 * compartment's own P1 value at that same vertex (the bundle's membrane-to-volume point map, vcell-fenics ADR 010
 * §3): exact on a body-fitted mesh, where the finite-volume solver extrapolates from the neighbouring elements.
 * <p>
 * Refused, with a message: a volume function of another domain's variables, adjacent values from a bundle
 * without the point map (written by vcell-fenics {@value #ADJACENT_MAPS_AFTER} or older), membrane normals,
 * region sizes, field data and gradients, region variables, and anything else the bundle cannot supply.
 */
final class FenicsFunctions {

	private static final Logger LG = LogManager.getLogger(FenicsFunctions.class);

	/** No functions: a run registered without its simulation. */
	static final FenicsFunctions NONE = new FenicsFunctions(List.of(), Set.of());

	/** the last vcell-fenics release whose bundles have no membrane-to-volume point maps */
	static final String ADJACENT_MAPS_AFTER = "0.1.1";

	/** One function: its flattened expression (constants substituted), its declared domain (null: any) and kind. */
	record Definition(String name, Expression expression, String domain, boolean membrane, String error) {
	}

	/** A membrane's compartments in the VCell math: {@code X_INSIDE} is X in {@code inside}, {@code X_OUTSIDE} in {@code outside}. */
	record Sides(String inside, String outside) {
	}

	private final Map<String, Definition> definitions = new LinkedHashMap<>();
	private final Set<String> regionVariables;
	private final Map<String, Sides> membranes;

	FenicsFunctions(List<Definition> definitions, Set<String> regionVariables) {
		this(definitions, regionVariables, Map.of());
	}

	/** @param membranes each membrane's inside and outside compartments, from the math */
	FenicsFunctions(List<Definition> definitions, Set<String> regionVariables, Map<String, Sides> membranes) {
		for (Definition d : definitions) {
			this.definitions.put(d.name(), d);
		}
		this.regionVariables = Collections.unmodifiableSet(new LinkedHashSet<>(regionVariables));
		this.membranes = Collections.unmodifiableMap(new LinkedHashMap<>(membranes));
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
			Map<String, Sides> membranes = new LinkedHashMap<>();
			for (SubDomain sd : math.getSubDomainCollection()) {
				if (sd instanceof MembraneSubDomain m && m.getInsideCompartment() != null && m.getOutsideCompartment() != null) {
					membranes.put(m.getName(), new Sides(m.getInsideCompartment().getName(), m.getOutsideCompartment().getName()));
				}
			}
			return new FenicsFunctions(list, regionVariables, membranes);
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
		if (d.domain() != null && d.membrane() && d.error() == null) {
			throw new IllegalArgumentException("function '" + name + "' is defined on the membrane '" + d.domain()
					+ "', which this run's results do not include (a membrane without species is written by vcell-fenics newer than "
					+ ADJACENT_MAPS_AFTER + "; re-run the simulation to draw it)");
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
		Map<String, List<String>> elsewhere = new LinkedHashMap<>(); // variable -> the domains holding it
		for (FenicsBundle.Variable v : bundle.getVariables()) {
			if (v.domain().equals(domain)) {
				onDomain.add(v.name());
			} else {
				elsewhere.computeIfAbsent(v.name(), k -> new ArrayList<>()).add(v.domain());
			}
		}
		List<String> symbolsUsed = new ArrayList<>(); // as the expression names them (c, c_INSIDE)
		List<String> variables = new ArrayList<>(); // the stored variable each is read from
		List<String> domains = new ArrayList<>(); // and the domain holding it
		String[] symbols = exp.getSymbols();
		if (symbols != null) {
			for (String s : symbols) {
				if (s.equals("t") || s.equals("x") || s.equals("y") || s.equals("z")) {
					continue;
				}
				if (onDomain.contains(s)) {
					symbolsUsed.add(s);
					variables.add(s);
					domains.add(domain);
					continue;
				}
				if (regionVariables.contains(s) || regionVariables.contains(base(s))) {
					throw new IllegalArgumentException(what + " uses the region variable '" + s + "', which FEniCSx results do not hold");
				}
				boolean sided = !s.equals(base(s));
				if (elsewhere.containsKey(s) || (sided && elsewhere.containsKey(base(s)))) {
					if (!target.isMembrane()) {
						throw new IllegalArgumentException(what + " uses '" + s + "' of another domain");
					}
					String compartment = adjacentCompartment(bundle, domain, what, s, elsewhere);
					symbolsUsed.add(s);
					variables.add(base(s));
					domains.add(compartment);
					continue;
				}
				if (definitions.containsKey(s)) {
					throw new IllegalArgumentException(what + " uses the function '" + s + "' unflattened");
				}
				throw new IllegalArgumentException(what + " uses '" + s + "', which is not a variable of '" + domain + "'"
						+ (target.isMembrane() ? " or of a compartment beside it" : ""));
			}
		}
		return new Compiled(name, exp, symbolsUsed.toArray(new String[0]), variables.toArray(new String[0]), domains.toArray(new String[0]));
	}

	/** {@code c} for {@code c_INSIDE} / {@code c_OUTSIDE}; else the name itself */
	private static String base(String symbol) {
		for (String suffix : new String[] { InsideVariable.INSIDE_VARIABLE_SUFFIX, OutsideVariable.OUTSIDE_VARIABLE_SUFFIX }) {
			if (symbol.endsWith(suffix) && symbol.length() > suffix.length()) {
				return symbol.substring(0, symbol.length() - suffix.length());
			}
		}
		return symbol;
	}

	/**
	 * The compartment beside {@code membrane} whose value of {@code symbol} a membrane function reads, as VCell's
	 * finite-volume evaluation resolves it: {@code c_INSIDE} / {@code c_OUTSIDE} name a side of the math's
	 * MembraneSubDomain; a plain {@code c} is the adjacent compartment holding it. Refused when the bundle has no
	 * point map onto that compartment (an older vcell-fenics), the variable is in no adjacent compartment, or a
	 * plain name is in both.
	 */
	private String adjacentCompartment(FenicsBundle bundle, String membrane, String what, String symbol,
			Map<String, List<String>> elsewhere) {
		FenicsBundle.Adjacency adjacency = bundle.adjacency(membrane);
		if (adjacency == null) {
			throw new IllegalArgumentException(what + " uses '" + symbol + "', a value of the compartment beside the membrane '"
					+ membrane + "'; these results carry no membrane-to-volume point map: re-run the simulation with vcell-fenics newer than "
					+ ADJACENT_MAPS_AFTER);
		}
		Sides sides = membranes.get(membrane);
		String variable = base(symbol);
		String compartment;
		if (!variable.equals(symbol)) {
			if (sides == null) {
				throw new IllegalArgumentException(what + " uses '" + symbol + "', but the inside and outside of '" + membrane
						+ "' are not known (the simulation's math was not given)");
			}
			compartment = symbol.endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX) ? sides.inside() : sides.outside();
			if (!elsewhere.getOrDefault(variable, List.of()).contains(compartment)) {
				throw new IllegalArgumentException(what + " uses '" + symbol + "', but '" + variable + "' is not a variable of '"
						+ compartment + "', the " + (compartment.equals(sides.inside()) ? "inside" : "outside") + " of '" + membrane + "'");
			}
		} else {
			List<String> beside = new ArrayList<>(sides != null ? List.of(sides.inside(), sides.outside()) : adjacency.compartments());
			beside.retainAll(elsewhere.getOrDefault(variable, List.of()));
			if (beside.isEmpty()) {
				throw new IllegalArgumentException(what + " uses '" + symbol + "' of " + elsewhere.get(variable)
						+ ", which is not beside the membrane '" + membrane + "' " + adjacency.compartments());
			}
			if (beside.size() > 1) {
				throw new IllegalArgumentException(what + " uses '" + symbol + "', which is a variable of both sides of '" + membrane
						+ "' " + beside + "; name a side with '" + symbol + InsideVariable.INSIDE_VARIABLE_SUFFIX + "' or '" + symbol
						+ OutsideVariable.OUTSIDE_VARIABLE_SUFFIX + "'");
			}
			compartment = beside.get(0);
		}
		if (!adjacency.maps().containsKey(compartment)) {
			throw new IllegalArgumentException(what + " uses '" + symbol + "' of '" + compartment + "', onto which these results have no point map from '"
					+ membrane + "'");
		}
		return compartment;
	}

	/**
	 * A function bound to its arguments: {@code t, x, y, z} and the stored variables {@link #variables}, in that
	 * order, evaluated one vertex at a time. Argument {@code k} is {@link #variables}{@code [k]} read on
	 * {@link #domains}{@code [k]}: the function's own domain, or on a membrane an adjacent compartment, whose values
	 * are carried to the membrane's vertices through the bundle's point map before evaluation.
	 */
	static final class Compiled {
		final String name;
		final String[] variables;
		final String[] domains;
		private final Expression bound;
		private final double[] args;

		private Compiled(String name, Expression exp, String[] symbols, String[] variables, String[] domains) {
			this.name = name;
			this.variables = variables;
			this.domains = domains;
			String[] names = new String[4 + symbols.length];
			names[0] = "t";
			names[1] = "x";
			names[2] = "y";
			names[3] = "z";
			System.arraycopy(symbols, 0, names, 4, symbols.length);
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
