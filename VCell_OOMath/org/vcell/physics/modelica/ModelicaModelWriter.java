package org.vcell.physics.modelica;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Vector;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;
import jscl.plugin.Variable;

import org.vcell.physics.component.ModelComponent;
import org.vcell.physics.component.ModelReader;
import org.vcell.physics.component.OOModel;
import org.vcell.physics.component.OOModelTest;
import org.vcell.physics.component.Parameter;
import org.vcell.physics.component.PhysicalSymbol;

import cbit.util.xml.XmlUtil;


public class ModelicaModelWriter {
	
	public static void main(String[] args){
		try {
			OOModel model = OOModelTest.getPlanarPendulumExample();
			System.out.println(XmlUtil.xmlToString(ModelReader.print(model)));
			System.out.println("----------");
			ModelicaModelWriter modelicaWriter = new ModelicaModelWriter();
			System.out.println(modelicaWriter.write(model));
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	
	public String write(OOModel ooModel) throws ParseException{
		//
		// write component descriptions
		//
		StringBuffer buffer = new StringBuffer();
		ModelComponent[] modelComponent = ooModel.getModelComponents();
		for (int i = 0; i < modelComponent.length; i++) {
			Vector<Expression> equationList = new Vector<Expression>();
			equationList.addAll(Arrays.asList(modelComponent[i].getEquations()));
			buffer.append("model "+modelComponent[i].getName()+"\n");
			
			PhysicalSymbol[] physicalSymbols = modelComponent[i].getSymbols();
			for (int j = 0; j < physicalSymbols.length; j++) {
				if (physicalSymbols[j] instanceof Parameter){
					Expression solution = null;
					for (int k = 0; k < equationList.size(); k++) {
						Variable[] variables = Expression.getVariables(equationList.elementAt(k));
						if (variables!=null && variables.length==1 && variables[0].getName().equals(physicalSymbols[j].getName())){
							solution = equationList.elementAt(k).solve(Expression.valueOf(physicalSymbols[j].getName()));
							equationList.remove(k);
							break;
						}
					}
					buffer.append("\tparameter Real "+physicalSymbols[j].getName()+"="+solution.infix()+";\n");
				}else if (physicalSymbols[j] instanceof org.vcell.physics.component.Variable && physicalSymbols[j].getName().endsWith("(t)")){
					String initName = physicalSymbols[j].getName().replace("(t)","(0)");
					Expression initial = null;
					for (int k = 0; k < equationList.size(); k++) {
						Variable[] variables = Expression.getVariables(equationList.elementAt(k));
						if (variables!=null && variables.length==1 && variables[0].infix().equals(initName)){
							initial = equationList.elementAt(k).solve(Expression.valueOf(initName));
							equationList.remove(k);
							break;
						}
					}
					if (initial==null){
						buffer.append("\t Real "+physicalSymbols[j].getName().replace("(t)","")+";\n");
					}else{
						buffer.append("\t Real "+physicalSymbols[j].getName().replace("(t)","")+"(start="+initial.infix()+");\n");
					}
				}
			}
			System.out.println("equation");
			for (int j = 0; j < equationList.size(); j++) {
				buffer.append("\t"+equationList.elementAt(j).infixModelica()+"=0;\n");
			}
			buffer.append("end "+modelComponent[i].getName()+";\n");
		}
		return buffer.toString();
	}

}
