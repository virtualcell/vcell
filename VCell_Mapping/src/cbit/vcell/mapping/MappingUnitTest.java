package cbit.vcell.mapping;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;
import org.vcell.expression.SimpleSymbolTable;
import org.vcell.expression.VCUnitEvaluator;

import cbit.vcell.units.VCUnitDefinition;
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
	try {
		IExpression exps[] = {
			ExpressionFactory.createExpression("currentDensity/_F_*_N_pmol_"),		// from current density to surface density rate
			ExpressionFactory.createExpression("surfaceRate*_F_/_N_pmol_"),			// from surface density rate to current density
			ExpressionFactory.createExpression("surfDens*volFract*svratio*KMOLE"),	// from surface density to concentration
			ExpressionFactory.createExpression("KMOLE/volFract*surfaceRate"),		// inside flux correction
ExpressionFactory.createExpression("_F_*fluxRate"),	// from uM.um.s-1 to pA.um-2
			ExpressionFactory.createExpression("_F_nmol_*fluxRate"),	// from uM.um.s-1 to pA.um-2
			ExpressionFactory.createExpression(" - (_K_GHK_*V * pow(_F_,2.0) * (K_cyt - (K_extr * exp( - (_F_ * V / (_R_ * _T_))))) * Permeability / (_R_ * _T_) / (1.0 - exp( - (_F_ * V / (_R_ * _T_)))))"),
			ExpressionFactory.createExpression(" - (_K_GHK_*V * pow(_F_,2.0) * K_cyt * Permeability / (_R_ * _T_))"),
			ExpressionFactory.createExpression("_F_ * V / (_R_ * _T_)"),
		};
		String varSymbols[] = { "currentDensity",
								cbit.vcell.model.ReservedSymbol.FARADAY_CONSTANT.getName(),
								cbit.vcell.model.ReservedSymbol.N_PMOLE.getName(),
								"surfaceRate",
								"svratio",
								"volFract",
								"surfDens",
								cbit.vcell.model.ReservedSymbol.KMOLE.getName(),
								cbit.vcell.model.ReservedSymbol.FARADAY_CONSTANT_NMOLE.getName(),
								"fluxRate",
								cbit.vcell.model.ReservedSymbol.TEMPERATURE.getName(),
								cbit.vcell.model.ReservedSymbol.GAS_CONSTANT.getName(),
								"K_cyt",
								"K_extr",
								"V",
								"Permeability",
								cbit.vcell.model.ReservedSymbol.K_GHK.getName(),
		};
		String unitSymbols[] = { "pA.um-2",
								cbit.vcell.model.ReservedSymbol.FARADAY_CONSTANT.getUnitDefinition().getSymbol(),
								cbit.vcell.model.ReservedSymbol.N_PMOLE.getUnitDefinition().getSymbol(),
								"molecules.um-2.s-1",
								"um-1",
								"1",
								"molecules.um-2",
								cbit.vcell.model.ReservedSymbol.KMOLE.getUnitDefinition().getSymbol(),
								cbit.vcell.model.ReservedSymbol.FARADAY_CONSTANT_NMOLE.getUnitDefinition().getSymbol(),
								"uM.um.s-1",
								cbit.vcell.model.ReservedSymbol.TEMPERATURE.getUnitDefinition().getSymbol(),
								cbit.vcell.model.ReservedSymbol.GAS_CONSTANT.getUnitDefinition().getSymbol(),
								"uM",
								"uM",
								"mV",
								"um.s-1",
								cbit.vcell.model.ReservedSymbol.K_GHK.getUnitDefinition().getSymbol(),
		};
		VCUnitDefinition vcUnits[] = new VCUnitDefinition[unitSymbols.length];
		for (int i = 0; i < vcUnits.length; i++){
			vcUnits[i] = VCUnitDefinition.getInstance(unitSymbols[i]);
		}
		SimpleSymbolTable symbolTable = new SimpleSymbolTable(varSymbols,null,vcUnits);
		for (int i = 0; i < exps.length; i++){
			exps[i].bindExpression(symbolTable);
			System.out.println("exp = '"+exps[i].infix()+"', unit = ["+VCUnitEvaluator.getUnitDefinition(exps[i]).getSymbol()+"]");
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}