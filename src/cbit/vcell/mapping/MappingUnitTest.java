package cbit.vcell.mapping;

import cbit.vcell.model.Model;
import cbit.vcell.model.ModelTest;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.VCUnitEvaluator;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;
/**
 * Insert the type's description here.
 * Creation date: (11/5/2004 5:49:02 PM)
 * @author: Jim Schaff
 */
public class MappingUnitTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	// create a model, since reserved symbols are now a part of model
	Model model = null;
	try {
		model = ModelTest.getExample();
	} catch (Exception e1) {
		e1.printStackTrace(System.out);
		throw new RuntimeException("Unable to create example model : " + e1.getMessage());
	}
	try {
		Expression exps[] = {
			new Expression("currentDensity/_F_*_N_pmol_"),		// from current density to surface density rate
			new Expression("surfaceRate*_F_/_N_pmol_"),			// from surface density rate to current density
			new Expression("surfDens*volFract*svratio*KMOLE"),	// from surface density to concentration
			new Expression("KMOLE/volFract*surfaceRate"),		// inside flux correction
//			new Expression("fluxCorrection*KMOLE*surfaceRate"), //
			new Expression("_F_*fluxRate"),	// from uM.um.s-1 to pA.um-2
			new Expression("_F_nmol_*fluxRate"),	// from uM.um.s-1 to pA.um-2
			new Expression(" - (_K_GHK_*V * pow(_F_,2.0) * (K_cyt - (K_extr * exp( - (_F_ * V / (_R_ * _T_))))) * Permeability / (_R_ * _T_) / (1.0 - exp( - (_F_ * V / (_R_ * _T_)))))"),
			new Expression(" - (_K_GHK_*V * pow(_F_,2.0) * K_cyt * Permeability / (_R_ * _T_))"),
			new Expression("_F_ * V / (_R_ * _T_)"),
		};
		String varSymbols[] = { "currentDensity",
								model.getFARADAY_CONSTANT().getName(),
								model.getN_PMOLE().getName(),
								"surfaceRate",
								"svratio",
								"volFract",
								"surfDens",
								model.getKMOLE().getName(),
								model.getFARADAY_CONSTANT_NMOLE().getName(),
								"fluxRate",
								model.getTEMPERATURE().getName(),
								model.getGAS_CONSTANT().getName(),
								"K_cyt",
								"K_extr",
								"V",
								"Permeability",
								model.getK_GHK().getName(),
		};
		String unitSymbols[] = { "pA.um-2",
								model.getFARADAY_CONSTANT().getUnitDefinition().getSymbol(),
								model.getN_PMOLE().getUnitDefinition().getSymbol(),
								"molecules.um-2.s-1",
								"um-1",
								"1",
								"molecules.um-2",
								model.getKMOLE().getUnitDefinition().getSymbol(),
								model.getFARADAY_CONSTANT_NMOLE().getUnitDefinition().getSymbol(),
								"uM.um.s-1",
								model.getTEMPERATURE().getUnitDefinition().getSymbol(),
								model.getGAS_CONSTANT().getUnitDefinition().getSymbol(),
								"uM",
								"uM",
								"mV",
								"um.s-1",
								model.getK_GHK().getUnitDefinition().getSymbol(),
		};
		VCUnitDefinition vcUnits[] = new VCUnitDefinition[unitSymbols.length];
		VCUnitSystem modelUnitSystem = model.getUnitSystem();
		for (int i = 0; i < vcUnits.length; i++){
			vcUnits[i] = modelUnitSystem.getInstance(unitSymbols[i]);
		}
		SimpleSymbolTable symbolTable = new SimpleSymbolTable(varSymbols,null,vcUnits);
		VCUnitEvaluator unitEvaluator = new VCUnitEvaluator(model.getUnitSystem());

		for (int i = 0; i < exps.length; i++){
			exps[i].bindExpression(symbolTable);
			System.out.println("exp = '"+exps[i].infix()+"', unit = ["+unitEvaluator.getUnitDefinition(exps[i]).getSymbol()+"]");
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}