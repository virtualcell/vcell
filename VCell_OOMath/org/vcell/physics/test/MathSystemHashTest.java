package org.vcell.physics.test;


import org.vcell.expression.ExpressionFactory;
import org.vcell.physics.math.MathSystemHash;
import org.vcell.physics.math.MathSystemHash.IndependentVariable;
import org.vcell.physics.math.MathSystemHash.Symbol;
import org.vcell.physics.math.MathSystemHash.Variable;
import org.vcell.physics.math.MathSystemHash.VariableDerivative;
import org.vcell.physics.math.MathSystemHash.VariableInitial;

import cbit.util.graph.Graph;
import cbit.util.graph.Node;
/**
 * Insert the type's description here.
 * Creation date: (11/1/2005 8:01:24 PM)
 * @author: Jim Schaff
 */
public class MathSystemHashTest {
/**
 * MathSystemTest constructor comment.
 */
public MathSystemHashTest() {
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

	cbit.vcell.math.Variable vars[] = (cbit.vcell.math.Variable[])cbit.util.BeanUtils.getArray(mathDesc.getVariables(),cbit.vcell.math.Variable.class);
	for (int i = 0; i < vars.length; i++){
		hash.addSymbol(new MathSystemHash.Variable(vars[i].getName(),vars[i].getExpression()));
	}

	cbit.vcell.math.SubDomain subDomains[] = (cbit.vcell.math.SubDomain[])cbit.util.BeanUtils.getArray(mathDesc.getSubDomains(),cbit.vcell.math.SubDomain.class);
	for (int i = 0; i < subDomains.length; i++){

		cbit.vcell.math.Equation[] equations = (cbit.vcell.math.Equation[])cbit.util.BeanUtils.getArray(subDomains[i].getEquations(),cbit.vcell.math.Equation.class);
		for (int j = 0; j < equations.length; j++){
			MathSystemHash.Variable var = (MathSystemHash.Variable)hash.getSymbol(equations[j].getVariable().getName());
			hash.addSymbol(new MathSystemHash.VariableInitial(var,equations[j].getInitialExpression()));
			if (equations[j] instanceof cbit.vcell.math.PdeEquation){
				cbit.vcell.math.PdeEquation pde = (cbit.vcell.math.PdeEquation)equations[j];
				hash.addSymbol(new MathSystemHash.VariableDerivative(var,pde.getRateExpression()));
			}else if (equations[j] instanceof cbit.vcell.math.OdeEquation){
				cbit.vcell.math.OdeEquation ode = (cbit.vcell.math.OdeEquation)equations[j];
				hash.addSymbol(new MathSystemHash.VariableDerivative(var,ode.getRateExpression()));
			}else if (equations[j] instanceof cbit.vcell.math.VolumeRegionEquation){
				cbit.vcell.math.VolumeRegionEquation vre = (cbit.vcell.math.VolumeRegionEquation)equations[j];
				hash.addSymbol(new MathSystemHash.VariableDerivative(var,vre.getRateExpression()));
			}else if (equations[j] instanceof cbit.vcell.math.MembraneRegionEquation){
				cbit.vcell.math.MembraneRegionEquation mre = (cbit.vcell.math.MembraneRegionEquation)equations[j];
				hash.addSymbol(new MathSystemHash.VariableDerivative(var,mre.getRateExpression()));
			}else if (equations[j] instanceof cbit.vcell.math.FilamentRegionEquation){
				cbit.vcell.math.FilamentRegionEquation fre = (cbit.vcell.math.FilamentRegionEquation)equations[j];
				hash.addSymbol(new MathSystemHash.VariableDerivative(var,fre.getRateExpression()));
			}
		}		
		// still have to handle fast systems.

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
		hash.addSymbol(new MathSystemHash.Variable("Kr",ExpressionFactory.createExpression(1)));
		hash.addSymbol(new MathSystemHash.Variable("_T_",ExpressionFactory.createExpression(300)));
		hash.addSymbol(new MathSystemHash.Variable("_F_nmol_",ExpressionFactory.createExpression(9.648E-5)));
		hash.addSymbol(new MathSystemHash.Variable("Kf",ExpressionFactory.createExpression(1)));
		hash.addSymbol(new MathSystemHash.Variable("_F_",ExpressionFactory.createExpression(96480)));
		hash.addSymbol(new MathSystemHash.Variable("species1_cyt_init",ExpressionFactory.createExpression(2.0)));
		hash.addSymbol(new MathSystemHash.Variable("_R_",ExpressionFactory.createExpression(8314)));
		hash.addSymbol(new MathSystemHash.Variable("species0_mito_init",ExpressionFactory.createExpression(0.0)));
		hash.addSymbol(new MathSystemHash.Variable("_N_pmol_",ExpressionFactory.createExpression(6.02e11)));
		hash.addSymbol(new MathSystemHash.Variable("SurfToVol_mitoMem",ExpressionFactory.createExpression(1.0)));
		hash.addSymbol(new MathSystemHash.Variable("k",ExpressionFactory.createExpression(1.0)));
		hash.addSymbol(new MathSystemHash.Variable("KMOLE",ExpressionFactory.createExpression(0.0016611295681063123)));
		hash.addSymbol(new MathSystemHash.Variable("VolFract_mito",ExpressionFactory.createExpression(0.2)));
		hash.addSymbol(new MathSystemHash.Variable("KMOLE",ExpressionFactory.createExpression(0.0016611295681063123)));
		hash.addSymbol(new MathSystemHash.Variable("_K_GHK_",ExpressionFactory.createExpression(1.0e-9)));
		hash.addSymbol(new MathSystemHash.Variable("species0_cyt_init",ExpressionFactory.createExpression(10.0)));
		hash.addSymbol(new MathSystemHash.Variable("K_millivolts_per_volt",ExpressionFactory.createExpression(1000.0)));

		MathSystemHash.Variable species1_cyt = null;
		MathSystemHash.Variable species0_mito = null;
		hash.addSymbol(species1_cyt = new MathSystemHash.Variable("species1_cyt",null));
		hash.addSymbol(species0_mito = new MathSystemHash.Variable("species0_mito",null));

		hash.addSymbol(new MathSystemHash.Variable("KFlux_mitoMem",ExpressionFactory.createExpression("(SurfToVol_mitoMem * VolFract_mito / (1.0 - VolFract_mito))")));
		hash.addSymbol(new MathSystemHash.Variable("K_species0_cyt_total",ExpressionFactory.createExpression("((VolFract_mito * species0_mito_init) + ((1.0 - VolFract_mito) * species0_cyt_init) + ((1.0 - VolFract_mito) * species1_cyt_init))")));
		hash.addSymbol(new MathSystemHash.Variable("species0_cyt",ExpressionFactory.createExpression(" (( - (VolFract_mito * species0_mito) + K_species0_cyt_total - ((1.0 - VolFract_mito) * species1_cyt)) / (1.0 - VolFract_mito))")));
		hash.addSymbol(new MathSystemHash.Variable("J_flux0",ExpressionFactory.createExpression("(k * (species0_cyt - species0_mito))")));
		hash.addSymbol(new MathSystemHash.Variable("J_reaction0",ExpressionFactory.createExpression(" ((Kf * species0_cyt) - (Kr * species1_cyt))")));
		hash.addSymbol(new MathSystemHash.Variable("Voltage_mitoMem",ExpressionFactory.createExpression(0.0)));
		hash.addSymbol(new MathSystemHash.Variable("KFlux_mitoMem_mito",ExpressionFactory.createExpression("SurfToVol_mitoMem")));

		hash.addSymbol(new MathSystemHash.VariableDerivative(species1_cyt,ExpressionFactory.createExpression("J_reaction0")));
		hash.addSymbol(new MathSystemHash.VariableInitial(species1_cyt,ExpressionFactory.createExpression("species1_cyt_init")));

		hash.addSymbol(new MathSystemHash.VariableDerivative(species0_mito,ExpressionFactory.createExpression("(KFlux_mitoMem_mito * J_flux0)")));
		hash.addSymbol(new MathSystemHash.VariableInitial(species0_mito,ExpressionFactory.createExpression("species0_mito_init")));

		return hash;
	}catch (org.vcell.expression.ExpressionException e){
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
		cbit.gui.DialogUtils.showComponentOKCancelDialog(null,graphModelPanel,"graph");
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}
