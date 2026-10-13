package cbit.vcell.math;

import cbit.vcell.parser.Expression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cbit.vcell.mapping.AbstractMathMapping;
import cbit.vcell.math.ParticleProperties.ParticleInitialCondition;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionCount;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionConcentration;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;


/**
 * This class serves to function as a mean of navigating relationships between variables within a
 * {@link MathDescription}.
 * <br/>
 * It's primary and original purpose of this class is to identify the constant used to initialize a given variable.
 * <br/>
 * Note that as Math Description is mutable, this class is by proxy susceptible to side effects
 */
public class MathRelationMappings {
	private static final Logger lg = LogManager.getLogger(MathRelationMappings.class);
	private static final String UNIT_PREFIX = AbstractMathMapping.PARAMETER_K_UNITFACTOR_PREFIX;

	private final MathDescription referenceMath;

	public MathRelationMappings(MathDescription descriptionToMap) {
		this.referenceMath = descriptionToMap;
	}

	/**
	 * Attempts to find the Constant that is responsible for serving as the initial condition to the provided variable.
	 * <br/><br/>
	 * Note that only certain types of variables are accepted, and that {@link Function} specifically automatically returns an empty Optional (it is currently intentionally unsupported)
	 * @param variable the variable to attempt to find the initializing factor for
	 * @throws UnsupportedOperationException if an unsupported variable is provided as an argument.
	 * @return An Optional, which is either empty if not match could be found
	 */
	public Optional<Constant> findInitializingFactor(Variable variable) {
		if (variable instanceof Constant constant) return Optional.of(constant); // A constant is "initialized by itself"
		if (variable instanceof Function) return Optional.empty(); // this will be complicated
		if (variable instanceof StochVolVariable stochVolVariable) return this.getInitForStochVolVariable(stochVolVariable);
		if (variable instanceof VolumeParticleVariable volumeParticleVariable) return this.getInitForVolumeParticleVariable(volumeParticleVariable);
		if (variable instanceof VolVariable volVariable) return this.getInitForVolVariable(volVariable);
		if (variable instanceof MemVariable memVariable) return this.getInitForVolVariable(memVariable);
		throw new UnsupportedOperationException("Type of variable `" + variable.getClass().getSimpleName() +"` not currently supported yet!");
	}

	///
	/// Main Methods
	///

	private Optional<Constant> getInitForStochVolVariable(StochVolVariable stochVolVariable) {
		if (null == this.referenceMath.getVariable(stochVolVariable.getName())) throw new IllegalArgumentException("StochVolVariable " + stochVolVariable.getName() + " not found in math description.");

		Optional<SubDomain> targetSubdomain = this.acquireMathSubDomain(stochVolVariable);
		Optional<VarIniCount> variableInitialCount = targetSubdomain.flatMap(
				(subDomain) -> MathRelationMappings.acquireMathVarIniCondition(subDomain, stochVolVariable)
		);
		return variableInitialCount.flatMap(this::acquireInitializingConstant);
	}

	private Optional<Constant> getInitForVolumeParticleVariable(VolumeParticleVariable volParticleVariable) {
		if (null == this.referenceMath.getVariable(volParticleVariable.getName())) throw new IllegalArgumentException("VolumeParticleVariable " + volParticleVariable.getName() + " not found in math description.");

		Optional<SubDomain> targetSubdomain = this.acquireMathSubDomain(volParticleVariable);
		Optional<ParticleProperties> particleProperties = targetSubdomain.flatMap(
				(subDomain) -> MathRelationMappings.acquireParticleProperties(subDomain, volParticleVariable)
		);
		return particleProperties.flatMap(this::deriveConstantFromParticleProperties);
	}

	private Optional<Constant> getInitForVolVariable(VolVariable volVariable) {
		if (null == this.referenceMath.getVariable(volVariable.getName())) throw new IllegalArgumentException("VolVariable " + volVariable.getName() + " not found in math description.");
		return this._getInitForVolVariable(volVariable, subDomain -> MathRelationMappings.acquireEquation(subDomain, volVariable));
	}

	private Optional<Constant> getInitForVolVariable(MemVariable memVariable) {
		if (null == this.referenceMath.getVariable(memVariable.getName())) throw new IllegalArgumentException("MemVariable " + memVariable.getName() + " not found in math description.");
		return this._getInitForVolVariable(memVariable, subDomain -> MathRelationMappings.acquireEquation(subDomain, memVariable));
	}

	///
	/// Auxiliary Methods
	///

	private Optional<Constant> _getInitForVolVariable(Variable variable, java.util.function.Function<? super SubDomain, ? extends Optional<Equation>> getEquationFunction) {
		Optional<SubDomain> targetSubdomain = this.acquireMathSubDomain(variable);
		Optional<Equation> equation = targetSubdomain.flatMap(getEquationFunction);
		return equation.flatMap(this::getInitialFromEquation);
	}

	private Optional<SubDomain> acquireMathSubDomain(Variable variable) {
		Variable.Domain domain = variable.getDomain();
		if (null == domain && !this.referenceMath.isNonSpatialStoch()) return Optional.empty();
		String domainName = (null == domain) ? "Compartment" : domain.getName(); // for some reason, Nonspatial stoch doesn't prefix the domain!
		if (null == domainName) return Optional.empty();
		SubDomain targetSubDomain = this.referenceMath.getSubDomain(domainName);
		if (null == targetSubDomain) return Optional.empty();
		return Optional.of(targetSubDomain);
	}

	private static Optional<VarIniCondition> acquireMathVarIniCondition(SubDomain subDomain, Variable variable) {
		VarIniCondition varIniCount = subDomain.getVarIniCondition(variable);
		return null == varIniCount? Optional.empty() : Optional.of(varIniCount);
	}

	private static Optional<VarIniCount> acquireMathVarIniCondition(SubDomain subDomain, StochVolVariable variable){
		Optional<VarIniCondition> iniCondition = MathRelationMappings.acquireMathVarIniCondition(subDomain, (Variable) variable);
		return iniCondition.filter(VarIniCount.class::isInstance).map(VarIniCount.class::cast);
	}

	private Optional<Constant> acquireInitializingConstant(VarIniCount varIniCount) {
		return this.deriveConstantFromExpression(varIniCount.getIniVal());
	}

	private static Optional<ParticleProperties> acquireParticleProperties(SubDomain subDomain, ParticleVariable variable) {
		ParticleProperties particleProperties = subDomain.getParticleProperties(variable);
		return null == particleProperties ? Optional.empty() : Optional.of(particleProperties);
	}

	private Optional<Constant> deriveConstantFromParticleProperties(ParticleProperties particleProperties) {
		java.util.function.Function<? super ParticleInitialCondition, ? extends Stream<Constant>> mapToConstantBasedOnPICType = (val)-> {
			if (val instanceof ParticleInitialConditionConcentration picConcentration) return MathRelationMappings.this.acquireInitializingConstant(picConcentration).stream();
			if (val instanceof ParticleInitialConditionCount picCount) return MathRelationMappings.this.acquireInitializingConstant(picCount).stream();
			throw new UnsupportedOperationException("Unknown subtype of ParticleInitialConditions detected: " + val.getClass().getSimpleName());
		};
		return particleProperties.getParticleInitialConditions().stream().flatMap(mapToConstantBasedOnPICType).findAny(); //NOTE: This may not be stable, if we actually have more than 1 potential target for paste!
	}

	private Optional<Constant> acquireInitializingConstant(ParticleInitialConditionConcentration particleInitialConditionConcentration) {
		Expression expr = particleInitialConditionConcentration.getDistribution();
		return (null == expr) ? Optional.empty() : this.deriveConstantFromExpression(expr);
	}

	private Optional<Constant> acquireInitializingConstant(ParticleInitialConditionCount particleInitialConditionCount) {
		Expression expr = particleInitialConditionCount.getCount();
		return (null == expr) ? Optional.empty() : this.deriveConstantFromExpression(expr);
	}

	private static Optional<Equation> acquireEquation(SubDomain subDomain, VolVariable variable) {
		return MathRelationMappings._acquireEquation(subDomain, variable);
	}

	private static Optional<Equation> acquireEquation(SubDomain subDomain, MemVariable variable) {
		return MathRelationMappings._acquireEquation(subDomain, variable);
	}

	private static Optional<Equation> _acquireEquation(SubDomain subDomain, Variable variable) {
		Equation equation = subDomain.getEquation(variable);
		return null == equation ? Optional.empty() : Optional.of(equation);
	}

	private Optional<Constant> getInitialFromEquation(Equation equation) {
		return this.deriveConstantFromExpression(equation.getInitialExpression());
	}

	private Optional<Constant> deriveConstantFromExpression(Expression expression) {
		if (null == expression) return Optional.empty();
		Predicate<String> NON_UNIT_PREFIXED_SYMBOLS = (str) -> !str.startsWith(UNIT_PREFIX);
		return Arrays.stream(expression.getSymbols()).filter(NON_UNIT_PREFIXED_SYMBOLS)
				.map(this.referenceMath::getVariable).filter(Objects::nonNull)
				.filter(Constant.class::isInstance).map(Constant.class::cast).findAny();
	}
}
