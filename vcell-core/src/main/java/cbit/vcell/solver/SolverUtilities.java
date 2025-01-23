package cbit.vcell.solver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.util.kisao.KisaoOntology;
import cbit.util.kisao.KisaoTerm;
import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.parser.ASTFuncNode.FunctionType;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.Expression.FunctionFilter;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.resource.ResourceUtil;

public class SolverUtilities {
	private static final Logger logger = LogManager.getLogger(SolverUtilities.class);

	public static Expression substituteSizeAndNormalFunctions(Expression origExp, VariableDomain variableDomain) throws ExpressionException {
		Expression exp = new Expression(origExp);
		Set<FunctionInvocation> fiSet = SolverUtilities.getSizeFunctionInvocations(exp);
		for(FunctionInvocation fi : fiSet) {
			String functionName = fi.getFunctionName();
			// replace vcRegionArea('domain') and vcRegionVolume('domain') with vcRegionArea or vcRegionVolume or vcRegionVolume_domain
			// the decision is based on variable domain
			if (variableDomain.equals(VariableDomain.VARIABLEDOMAIN_VOLUME)) {
				exp.substituteInPlace(fi.getFunctionExpression(), new Expression(functionName));
			} else if (variableDomain.equals(VariableDomain.VARIABLEDOMAIN_MEMBRANE)) {
				if (functionName.equals(MathFunctionDefinitions.Function_regionArea_current.getFunctionName())) {
					exp.substituteInPlace(fi.getFunctionExpression(), new Expression(functionName));
				} else {
					String domainName = fi.getArguments()[0].infix();
					// remove single quote
					domainName = domainName.substring(1, domainName.length() - 1);
					exp.substituteInPlace(fi.getFunctionExpression(), new Expression(functionName + "_" + domainName));
				}
			}
		}
		FunctionInvocation[] allFunctions = exp.getFunctionInvocations(null);
		for (FunctionInvocation fi : allFunctions){
			if (fi.getFunctionName().equals(MathFunctionDefinitions.Function_normalX.getFunctionName())){
				exp.substituteInPlace(fi.getFunctionExpression(), new Expression("normalX"));
			}
			if (fi.getFunctionName().equals(MathFunctionDefinitions.Function_normalY.getFunctionName())){
				exp.substituteInPlace(fi.getFunctionExpression(), new Expression("normalY"));
			}
		}
		return exp;
	}

	public static Set<FunctionInvocation> getSizeFunctionInvocations(Expression expression) {
		if(expression == null){
			return null;
		}
		FunctionInvocation[] functionInvocations = expression.getFunctionInvocations(new FunctionFilter() {
			
			@Override
			public boolean accept(String functionName, FunctionType functionType) {
				if (functionName.equals(MathFunctionDefinitions.Function_regionArea_current.getFunctionName())
						|| functionName.equals(MathFunctionDefinitions.Function_regionVolume_current.getFunctionName())) {
					return true;
				}
				return false;
			}
		});
		Set<FunctionInvocation> fiSet = new HashSet<FunctionInvocation>();
		for (FunctionInvocation fi : functionInvocations){
			fiSet.add(fi);			
		}
		return fiSet;
	}
	
	public static boolean isPowerOf2(int n) {
		return n != 0 && ((n & (n-1)) == 0);
	}

	/**
	 * Ensure solvers extracted from resources and registered as property
	 * @param sd the solver description
	 * @return array of exes used by provided solver
	 * @throws IOException, {@link UnsupportedOperationException} if no exe for this solver
	 */
	public static File[] getExes(SolverDescription sd) throws IOException {
		SolverExecutable se = sd.getSolverExecutable(); 
		if (se != null) {
			SolverExecutable.NameInfo[] nameInfos = se.getNameInfo();
			File files[] = new File[nameInfos.length];
			for (int i = 0; i < nameInfos.length; ++i) {
				SolverExecutable.NameInfo ni = nameInfos[i];
				File exe = ResourceUtil.findSolverExecutable(ni.exeName);
				files[i] = exe; 
			}
			return files;
		}
		throw new UnsupportedOperationException("SolverDescription " + sd + " has no executable");
	}

	/**
	 * calls {@link #getExes(SolverDescription)} if solver requires executables,
	 * no-op otherwise
	 */
	public static void prepareSolverExecutable(SolverDescription solverDescription) throws IOException {
		if (solverDescription.getSolverExecutable() != null) {
			getExes(solverDescription);
		}
	}
	
	public static SolverDescription matchSolverWithKisaoId(String originalId, boolean exactMatchOnly) {
		
		// if originating from SED-ML it will likely come in a wrong format using colon
		
		String fixedId = originalId.replace(":","_");
		
		 KisaoTerm originalKisaoTerm = KisaoOntology.getInstance().getTermById(fixedId);
		 if(originalKisaoTerm == null) {
			 return null;		// kisao id not found in ontology
		 }
		 
		 // ----- cross fingers and hope that the original kisao is a match
		 List<SolverDescription> matchingSolverDescriptions = new ArrayList<>();
		 matchingSolverDescriptions = matchByKisaoId(originalKisaoTerm);
		 if(!matchingSolverDescriptions.isEmpty()) {
			 return matchingSolverDescriptions.get(0);		// exact match
		 }
		 
		 // ----- We don't have a match; see if the user doesn't want us substituting, and if so return execution.
		 if(SolverUtilities.needsExactMatch(exactMatchOnly)) return null;
		 
		 
		 // ----- make descendant list and check them all until a match is found
		 List<KisaoTerm> descendantList = KisaoOntology.makeDescendantList(originalKisaoTerm);
		 if(descendantList.isEmpty()) {
			 if(!originalKisaoTerm.getId().equals("KISAO_0000000")) {
				 return attemptLastResortMatch(originalKisaoTerm);
			 } else {
				 return null;		// for KISAO_0000000
			 }
		 }

		 for(KisaoTerm descendant : descendantList) {
			 matchingSolverDescriptions = matchByKisaoId(descendant);
			 if(!matchingSolverDescriptions.isEmpty()) {
				 return matchingSolverDescriptions.get(0);
			 }
		 }

		 KisaoTerm last = descendantList.get(descendantList.size()-1);
		 logger.info("No direct match with any descendant, trying last resort match for descendant " + last.getId());
		 return attemptLastResortMatch(last);
	}

	private static List<SolverDescription> matchByKisaoId(KisaoTerm candidate) {
        List<SolverDescription> solverDescriptions = new ArrayList<>();
		for (SolverDescription sd : SolverDescription.values()) {
			if (sd.getKisao().contains(":") || sd.getKisao().contains("_")) {
				logger.trace(sd.getKisao());
			} else if ("KISAO".equals(sd.getKisao())){
				logger.info("Skipping not-yet-created KiSAO term");
			} else {
				logger.warn("`{}` is bad KiSAO formating, skipping", sd.getKisao());
				continue;
			}
			String s1 = candidate.getId();
			String s2 = sd.getKisao();
			s2 = s2.replace(":", "_");
			if(s1.equalsIgnoreCase(s2)) {
				solverDescriptions.add(sd);
			}
		}
		return solverDescriptions;
	}

	private static SolverDescription attemptLastResortMatch(KisaoTerm last) {
        return switch (last.getId()) {
            case "KISAO_0000056" -> SolverDescription.Smoldyn; // Livermore Solver
            case "KISAO_0000094" /* Livermore Solver */, "KISAO_0000284" /* ? missing ? */,
				 "KISAO_0000433" /* CVODE-like method */, "KISAO_0000694" /* ODE-solver */ ->
                    SolverDescription.CVODE;
            case "KISAO_0000281" /*multi-step method*/ ->
                    SolverDescription.AdamsMoulton;
            case "KISAO_0000319" /*Monte-carlo*/ ->
                    SolverDescription.StochGibson;
            case "KISAO_0000352" /* Hybrid-method */ ->
                    SolverDescription.HybridMilstein;
            case "KISAO_0000369" /* partial differential equation discretization method */,
				 "KISAO_0000398" /* iterative method for solving a system of equations */ ->
                    SolverDescription.SundialsPDE;
            case "KISAO_0000377" /* one-step method */ ->
                    SolverDescription.RungeKuttaFehlberg;
            case "KISAO_0000408" /* Newton type method */ ->
                    SolverDescription.IDA;
            default -> {
                logger.error("Failed last resort match for descendant {}", last.getId());
                yield null;
            }
        };
	}

	private static boolean needsExactMatch(boolean cmdRequestedMatch){
		final String ENV_VAR_NAME = "ALGORITHM_SUBSTITUTION_POLICY";
		boolean result;
		// Get the BSTS env-var
		String envVar = Optional.ofNullable(System.getenv(ENV_VAR_NAME)).orElse("ANY"), message = null;
		switch (envVar){
			case "SAME_METHOD":
			case "SAME_MATH":
			case "SIMILAR_APPROXIMATIONS":
			case "DISTINCT_APPROXIMATIONS":
			case "DISTICT_SCALES":
			case "SAME_VARIABLES":
				result = true;
				break;
			case "SIMILAR_VARIABLES":
			case "SAME_FRAMEWORK":
			case "ANY":
				result = false;
				break;
			case "NONE": // Since we try anyway with the sundials solvers, we need to throw an exception here.
				if (message == null) 
					message = String.format("User specifically requested no substitutions using envrionment variable \"%s\".", ENV_VAR_NAME);
			default:
				if (message == null) message = String.format("Environment varaible (\"%s\") value encountered <%s>.", ENV_VAR_NAME, envVar);
				logger.error(message);
				throw new IllegalArgumentException(message);
		}
		return result || cmdRequestedMatch; 
	}
}
