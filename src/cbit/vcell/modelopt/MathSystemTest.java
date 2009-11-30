package cbit.vcell.modelopt;
import cbit.util.graph.*;
import cbit.vcell.parser.Expression;
/**
 * Insert the type's description here.
 * Creation date: (11/1/2005 8:01:24 PM)
 * @author: Jim Schaff
 */
public class MathSystemTest {
/**
 * MathSystemTest constructor comment.
 */
public MathSystemTest() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 8:11:32 PM)
 * @return cbit.vcell.mapping.MathSystemHash
 * @param mathDesc cbit.vcell.math.MathDescription
 */
public static MathSystemHash fromMath(cbit.vcell.math.MathDescription mathDesc) {
	MathSystemHash hash = new MathSystemHash();

	hash.addSymbol(new MathSystemHash.IndependentVariable("t"));
	hash.addSymbol(new MathSystemHash.IndependentVariable("x"));
	hash.addSymbol(new MathSystemHash.IndependentVariable("y"));
	hash.addSymbol(new MathSystemHash.IndependentVariable("z"));

	cbit.vcell.math.Variable vars[] = (cbit.vcell.math.Variable[])org.vcell.util.BeanUtils.getArray(mathDesc.getVariables(),cbit.vcell.math.Variable.class);
	for (int i = 0; i < vars.length; i++){
		hash.addSymbol(new MathSystemHash.Variable(vars[i].getName(),vars[i].getExpression()));
	}

	cbit.vcell.math.SubDomain subDomains[] = (cbit.vcell.math.SubDomain[])org.vcell.util.BeanUtils.getArray(mathDesc.getSubDomains(),cbit.vcell.math.SubDomain.class);
	for (int i = 0; i < subDomains.length; i++){

		cbit.vcell.math.Equation[] equations = (cbit.vcell.math.Equation[])org.vcell.util.BeanUtils.getArray(subDomains[i].getEquations(),cbit.vcell.math.Equation.class);
		for (int j = 0; j < equations.length; j++){
			MathSystemHash.Variable var = (MathSystemHash.Variable)hash.getSymbol(equations[j].getVariable().getName());
			hash.addSymbol(new MathSystemHash.VariableInitial(var,equations[j].getInitialExpression()));
			if (equations[j] instanceof cbit.vcell.math.PdeEquation){
				//cbit.vcell.math.PdeEquation pde = (cbit.vcell.math.PdeEquation)equations[j];
				//hash.addSymbol(new MathSystemHash.VariableDerivative(var,pde.getRateExpression()));
				throw new RuntimeException("MathSystemHash doesn't yet support spatial models");
			}else if (equations[j] instanceof cbit.vcell.math.VolumeRegionEquation){
				//cbit.vcell.math.VolumeRegionEquation vre = (cbit.vcell.math.VolumeRegionEquation)equations[j];
				//hash.addSymbol(new MathSystemHash.VariableDerivative(var,vre.getRateExpression()));
				throw new RuntimeException("MathSystemHash doesn't yet support spatial models");
			}else if (equations[j] instanceof cbit.vcell.math.MembraneRegionEquation){
				//cbit.vcell.math.MembraneRegionEquation mre = (cbit.vcell.math.MembraneRegionEquation)equations[j];
				//hash.addSymbol(new MathSystemHash.VariableDerivative(var,mre.getRateExpression()));
				throw new RuntimeException("MathSystemHash doesn't yet support spatial models");
			}else if (equations[j] instanceof cbit.vcell.math.FilamentRegionEquation){
				//cbit.vcell.math.FilamentRegionEquation fre = (cbit.vcell.math.FilamentRegionEquation)equations[j];
				//hash.addSymbol(new MathSystemHash.VariableDerivative(var,fre.getRateExpression()));
				throw new RuntimeException("MathSystemHash doesn't yet support spatial models");
				
			}else if (equations[j] instanceof cbit.vcell.math.OdeEquation){
				cbit.vcell.math.OdeEquation ode = (cbit.vcell.math.OdeEquation)equations[j];
				hash.addSymbol(new MathSystemHash.VariableDerivative(var,ode.getRateExpression()));
			}
		}
	}
	return hash;
}


/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 4:39:24 PM)
 * @param args java.lang.String[]
 */
public static MathSystemHash getExample() {
	try {
		MathSystemHash hash = new MathSystemHash();
		hash.addSymbol(new MathSystemHash.Variable("Kr",new Expression(1)));
		hash.addSymbol(new MathSystemHash.Variable("_T_",new Expression(300)));
		hash.addSymbol(new MathSystemHash.Variable("_F_nmol_",new Expression(9.648E-5)));
		hash.addSymbol(new MathSystemHash.Variable("Kf",new Expression(1)));
		hash.addSymbol(new MathSystemHash.Variable("_F_",new Expression(96480)));
		hash.addSymbol(new MathSystemHash.Variable("species1_cyt_init",new Expression(2.0)));
		hash.addSymbol(new MathSystemHash.Variable("_R_",new Expression(8314)));
		hash.addSymbol(new MathSystemHash.Variable("species0_mito_init",new Expression(0.0)));
		hash.addSymbol(new MathSystemHash.Variable("_N_pmol_",new Expression(6.02e11)));
		hash.addSymbol(new MathSystemHash.Variable("SurfToVol_mitoMem",new Expression(1.0)));
		hash.addSymbol(new MathSystemHash.Variable("k",new Expression(1.0)));
		hash.addSymbol(new MathSystemHash.Variable("KMOLE",new Expression(0.0016611295681063123)));
		hash.addSymbol(new MathSystemHash.Variable("VolFract_mito",new Expression(0.2)));
		hash.addSymbol(new MathSystemHash.Variable("KMOLE",new Expression(0.0016611295681063123)));
		hash.addSymbol(new MathSystemHash.Variable("_K_GHK_",new Expression(1.0e-9)));
		hash.addSymbol(new MathSystemHash.Variable("species0_cyt_init",new Expression(10.0)));
		hash.addSymbol(new MathSystemHash.Variable("K_millivolts_per_volt",new Expression(1000.0)));

		MathSystemHash.Variable species1_cyt = null;
		MathSystemHash.Variable species0_mito = null;
		hash.addSymbol(species1_cyt = new MathSystemHash.Variable("species1_cyt",null));
		hash.addSymbol(species0_mito = new MathSystemHash.Variable("species0_mito",null));

		hash.addSymbol(new MathSystemHash.Variable("KFlux_mitoMem",new Expression("(SurfToVol_mitoMem * VolFract_mito / (1.0 - VolFract_mito))")));
		hash.addSymbol(new MathSystemHash.Variable("K_species0_cyt_total",new Expression("((VolFract_mito * species0_mito_init) + ((1.0 - VolFract_mito) * species0_cyt_init) + ((1.0 - VolFract_mito) * species1_cyt_init))")));
		hash.addSymbol(new MathSystemHash.Variable("species0_cyt",new Expression(" (( - (VolFract_mito * species0_mito) + K_species0_cyt_total - ((1.0 - VolFract_mito) * species1_cyt)) / (1.0 - VolFract_mito))")));
		hash.addSymbol(new MathSystemHash.Variable("J_flux0",new Expression("(k * (species0_cyt - species0_mito))")));
		hash.addSymbol(new MathSystemHash.Variable("J_reaction0",new Expression(" ((Kf * species0_cyt) - (Kr * species1_cyt))")));
		hash.addSymbol(new MathSystemHash.Variable("Voltage_mitoMem",new Expression(0.0)));
		hash.addSymbol(new MathSystemHash.Variable("KFlux_mitoMem_mito",new Expression("SurfToVol_mitoMem")));

		hash.addSymbol(new MathSystemHash.VariableDerivative(species1_cyt,new Expression("J_reaction0")));
		hash.addSymbol(new MathSystemHash.VariableInitial(species1_cyt,new Expression("species1_cyt_init")));

		hash.addSymbol(new MathSystemHash.VariableDerivative(species0_mito,new Expression("(KFlux_mitoMem_mito * J_flux0)")));
		hash.addSymbol(new MathSystemHash.VariableInitial(species0_mito,new Expression("species0_mito_init")));

		return hash;
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 4:39:24 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		MathSystemHash hash = getExample();
		//
		// unsorted list
		//
		MathSystemHash.Symbol unsortedSymbols[] = hash.getSymbols();
		for (int i = 0; i < unsortedSymbols.length; i++){
			System.out.println(unsortedSymbols[i].getName());
		}

		System.out.println("\n\n");
		
		//
		// sorted list
		//
		Graph graph = hash.getDependencyGraph(hash.getSymbols());
		Node[] sortedNodes = graph.topologicalSort();
		
		for (int i = 0; i < sortedNodes.length; i++){
			System.out.println(sortedNodes[i].getName());
		}

		cbit.gui.graph.SimpleGraphModelPanel graphModelPanel = new cbit.gui.graph.SimpleGraphModelPanel();
		graphModelPanel.setGraph(graph);
		graphModelPanel.setPreferredSize(new java.awt.Dimension(800,800));
		graphModelPanel.setMinimumSize(new java.awt.Dimension(800,800));
		org.vcell.util.gui.DialogUtils.showComponentOKCancelDialog(null,graphModelPanel,"graph");
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}