package cbit.vcell.mapping;

import org.vcell.util.VCellThreadChecker;

import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathException;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableHash;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

public abstract class AbstractStochMathMapping extends AbstractMathMapping {

	static final String PARAMETER_PROBABILITY_RATE_REVERSE_SUFFIX = "_reverse";
	static final String PARAMETER_PROBABILITYRATE_PREFIX = "P_";


	public AbstractStochMathMapping(SimulationContext simContext, MathMappingCallback callback, NetworkGenerationRequirements networkGenerationRequirements) {
		super(simContext, callback, networkGenerationRequirements);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Basically the function clears the error list and calls to get a new mathdescription.
	 */
	@Override
	protected final void refresh(MathMappingCallback callback) throws MappingException, ExpressionException, MatrixException, MathException, ModelException{
		VCellThreadChecker.checkCpuIntensiveInvocation();
		
		localIssueList.clear();
		//refreshKFluxParameters();
		
		refreshSpeciesContextMappings();
		//refreshStructureAnalyzers();
		if(callback != null) {
			callback.setProgressFraction(52.0f/100.0f);
		}
		refreshVariables();
		
		refreshLocalNameCount();
		refreshMathDescription();
		reconcileWithOriginalModel();
	}

	protected abstract void refreshMathDescription()  throws MappingException, MatrixException, MathException, ExpressionException, ModelException;

	protected abstract void refreshVariables() throws MappingException;

	protected abstract void refreshSpeciesContextMappings()  throws ExpressionException, MappingException, MathException;

	/**
	 * getExpressionConcToAmt : converts the concentration expression ('concExpr') to an expression of the number of particles. 
	 * 		If argument 'speciesContext' is on a membrane, particlesExpr = concExpr * size_of_Mem. If 'speciesContext' is in 
	 * 		feature, particlesExpr = (concExpr * size_of_Feature)/KMOLE.
	 * @param concExpr
	 * @param speciesContext
	 * @return
	 * @throws MappingException
	 * @throws ExpressionException
	 */
	protected Expression getExpressionConcToExpectedCount(Expression concExpr, Structure structure)
			throws MappingException, ExpressionException {
				Expression exp = Expression.mult(concExpr, new Expression(structure.getStructureSize(), getNameScope()));
				ModelUnitSystem unitSystem = getSimulationContext().getModel().getUnitSystem();
				VCUnitDefinition substanceUnit = unitSystem.getSubstanceUnit(structure);
				Expression unitFactor = getUnitFactor(unitSystem.getStochasticSubstanceUnit().divideBy(substanceUnit));
				Expression particlesExpr = Expression.mult(unitFactor, exp).flattenFactors("KMOLE");
				return particlesExpr;
			}

	/**
	 * getExpressionAmtToConc : converts the particles expression ('particlesExpr') to an expression for concentration. 
	 * 		If argument 'speciesContext' is on a membrane, concExpr = particlesExpr/size_of_Mem. If 'speciesContext' is in 
	 * 		feature, concExpr = (particlesExpr/size_of_Feature)*KMOLE.
	 * @param particlesExpr
	 * @param speciesContext
	 * @return
	 * @throws MappingException
	 * @throws ExpressionException
	 */
	protected Expression getExpressionAmtToConc(Expression particlesExpr, Structure structure)
			throws MappingException, ExpressionException {
				ModelUnitSystem unitSystem = getSimulationContext().getModel().getUnitSystem();
				VCUnitDefinition substanceUnit = unitSystem.getSubstanceUnit(structure);
				Expression unitFactor = getUnitFactor(substanceUnit.divideBy(unitSystem.getStochasticSubstanceUnit()));
				Expression scStructureSize = new Expression(structure.getStructureSize(), getNameScope());
				Expression concentrationExpr = Expression.mult(unitFactor, particlesExpr, Expression.invert(scStructureSize)).flattenFactors("KMOLE");
				return concentrationExpr;
			}

	protected void addInitialConditions(Domain domain, SpeciesContextSpec[] speciesContextSpecs, VariableHash varHash)
			throws ExpressionException, MathException, MappingException {
				//
				// species initial values (either function or constant)
				//
				for (int i = 0; i < speciesContextSpecs.length; i++){
					SpeciesContextSpec.SpeciesContextSpecParameter initParam = null;//can be concentration or amount
					Expression iniExp = null;
					StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
					if(speciesContextSpecs[i].getInitialConcentrationParameter() != null && speciesContextSpecs[i].getInitialConcentrationParameter().getExpression() != null)
					{//use concentration, need to set up amount functions
						initParam = speciesContextSpecs[i].getInitialConcentrationParameter();
						iniExp = initParam.getExpression();
						iniExp = getSubstitutedExpr(iniExp, true, !speciesContextSpecs[i].isConstant());
						// now create the appropriate function or Constant for the speciesContextSpec.
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(initParam,sm.getGeometryClass()),getIdentifierSubstitutions(iniExp,initParam.getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass()));
			
						//add function for initial amount
						SpeciesContextSpec.SpeciesContextSpecParameter initAmountParam = speciesContextSpecs[i].getInitialCountParameter();
						Expression 	iniAmountExp = getExpressionConcToExpectedCount(new Expression(initParam, getNameScope()),speciesContextSpecs[i].getSpeciesContext().getStructure());
						// this is just going to add a var in math with iniCountSymbol, it is not actually write the expression to IniCountParameter.
						varHash.addVariable(new Function(getMathSymbol(initAmountParam, sm.getGeometryClass()),getIdentifierSubstitutions(iniAmountExp,initAmountParam.getUnitDefinition(),sm.getGeometryClass()),domain));
					}
					else if(speciesContextSpecs[i].getInitialCountParameter() != null && speciesContextSpecs[i].getInitialCountParameter().getExpression() != null)
					{// use amount
						initParam = speciesContextSpecs[i].getInitialCountParameter();
						iniExp = initParam.getExpression();
						iniExp = getSubstitutedExpr(iniExp, false, !speciesContextSpecs[i].isConstant());
						// now create the appropriate function or Constant for the speciesContextSpec.
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(initParam,sm.getGeometryClass()),getIdentifierSubstitutions(iniExp,initParam.getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass()));
					}
			
					//add spConcentration (concentration of species) to varHash as function or constant
					SpeciesConcentrationParameter spConcParam = getSpeciesConcentrationParameter(speciesContextSpecs[i].getSpeciesContext());
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(spConcParam,sm.getGeometryClass()),getIdentifierSubstitutions(spConcParam.getExpression(), spConcParam.getUnitDefinition(), sm.getGeometryClass()),sm.getGeometryClass()));
			
				}
			}

	/**
	 * 
	 * @param expr
	 * @param bConcentration
	 * @return
	 * @throws ExpressionException
	 */
	protected Expression getSubstitutedExpr(Expression expr, boolean bConcentration, boolean bIsInitialCondn)
			throws ExpressionException {
				expr = new Expression(expr);
				String[] symbols = expr.getSymbols();
				// Check if 'expr' has other speciesContexts in its expression, need to replace it with 'spContext_init'
				for (int j = 0; symbols != null && j < symbols.length; j++) {
					// if symbol is a speciesContext, replacing it with a reference to initial condition for that speciesContext.
					SpeciesContext spC = null;
					SymbolTableEntry ste = expr.getSymbolBinding(symbols[j]);
					if (ste instanceof ProxyParameter) {
						// if expression is for speciesContextSpec or Kinetics, ste will be a ProxyParameter instance.
						ProxyParameter spspp = (ProxyParameter)ste;
						if (spspp.getTarget() instanceof SpeciesContext) {
							spC = (SpeciesContext)spspp.getTarget();
						}
					} else if (ste instanceof SpeciesContext) {
						// if expression is for a global parameter, ste will be a SpeciesContext instance. 
						spC = (SpeciesContext)ste;
					}
					if (spC != null) {
						SpeciesContextSpec spcspec = getSimulationContext().getReactionContext().getSpeciesContextSpec(spC);
						Parameter spCParm = null;
						if (bConcentration && bIsInitialCondn) {
							// speciesContext has initConcentration set, so need to replace 'spContext' in 'expr' 'spContext_init'
							spCParm = spcspec.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
						} else if (!bConcentration && bIsInitialCondn) {
							// speciesContext has initCount set, so need to replace 'spContext' in 'expr' 'spContext_initCount'
							spCParm = spcspec.getParameterFromRole(SpeciesContextSpec.ROLE_InitialCount);
						} else if (bConcentration && !bIsInitialCondn) {
							// need to replace 'spContext' in 'expr' 'spContext_Conc'
							spCParm = getSpeciesConcentrationParameter(spC);
						} else if (!bConcentration && !bIsInitialCondn) {
							// need to replace 'spContext' in 'expr' 'spContext_Count'
							spCParm = getSpeciesCountParameter(spC);
						}
						// need to get init condn expression, but can't get it from getMathSymbol() (mapping between bio and math), hence get it as below.
						Expression scsInitExpr = new Expression(spCParm, getNameScope());
			//			scsInitExpr.bindExpression(this);
						expr.substituteInPlace(new Expression(spC.getName()), scsInitExpr);
					}
				}
				return expr;
			}

}
