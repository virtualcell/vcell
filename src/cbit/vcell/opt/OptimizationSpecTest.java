package cbit.vcell.opt;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.*;
import java.util.*;
import cbit.vcell.server.*;
import cbit.vcell.solvers.*;
import java.io.*;
import cbit.vcell.parser.*;
/**
 * Insert the type's description here.
 * Creation date: (3/3/00 12:40:33 AM)
 * @author: 
 */
public class OptimizationSpecTest {
/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:58:27 AM)
 * @return cbit.vcell.opt.OptimizationSpec
 */
public static OptimizationSpec getCFSQP_Sample1() throws ExpressionException {
	OptimizationSpec os = new OptimizationSpec();
	
	os.addParameter(new Parameter("x1",0, 1e10, 1.0, 0.1));
	os.addParameter(new Parameter("x2",0, 1e10, 1.0, 0.7));
	os.addParameter(new Parameter("x3",0, 1e10, 1.0, 0.2));

	os.setObjectiveFunction(new ExplicitObjectiveFunction(new Expression("pow(x1 + 3*x2 + x3,2) + 4*pow(x1 - x2,2)")));

	os.addConstraint(new Constraint(ConstraintType.NonlinearInequality,new Expression("pow(x1,3) - 6*x2 - 4*x3 + 3")));
	os.addConstraint(new Constraint(ConstraintType.LinearEquality,new Expression("1 - x1 - x2 - x3")));
	
	return os;
}
/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:58:27 AM)
 * @return cbit.vcell.opt.OptimizationSpec
 */
public static OptimizationSpec getExample() throws ExpressionException {
	OptimizationSpec os = new OptimizationSpec();
	os.addParameter(new Parameter("calcium",0.4,0.6,1.0,0.5));
	os.addParameter(new Parameter("ip3",0.01,100,1.0,1));
	os.setObjectiveFunction(new ExplicitObjectiveFunction(new Expression("calcium+ip3")));
	os.addConstraint(new Constraint(ConstraintType.LinearEquality,new Expression("calcium - ip3")));
	return os;
}
/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:58:27 AM)
 * @return cbit.vcell.opt.OptimizationSpec
 */
public static OptimizationSpec getExampleBoris() throws ExpressionException, MathException {
	OptimizationSpec os = new OptimizationSpec();
	//os.addConstant(new cbit.vcell.math.Constant("abc",new Expression(1)));
	//os.addConstant(new cbit.vcell.math.Constant("def",new Expression(1)));
	//os.addConstant(new cbit.vcell.math.Constant("K",new Expression("abc/def")));
	os.addParameter(new Parameter("x0",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,1.0,2));
	os.addParameter(new Parameter("x1",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,1.0,.1));
	os.addParameter(new Parameter("x2",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,1.0,.1));
	os.addConstraint(new Constraint(ConstraintType.NonlinearEquality,new Expression("(0.01 + 80/(200+100000)) * 0.5 * (150000-x2)/(0.5+x2) - x0")));
	os.addConstraint(new Constraint(ConstraintType.NonlinearEquality,new Expression("4000 * 0.25 * (x1 - 0.7)/(x1 + 0.25)/(0.07 + 0.25) - x0")));
	os.addConstraint(new Constraint(ConstraintType.NonlinearEquality,new Expression("(300 + 60*250*0.43/(x1 + 0.43)/(x2 + 0.43)) * (x2 - x1)/20 - x0")));

// answers from boris
//
// x0 = 424.543
// x1 = 0.120308
// x2 = 1.36068
//

	return os;
}
/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:58:27 AM)
 * @return cbit.vcell.opt.OptimizationSpec
 */
public static OptimizationSpec getOdeExample() throws ExpressionException, Exception {
	OptimizationSpec os = new OptimizationSpec();
	
	os.addParameter(new Parameter("x1",1,5,1.0,1.0));
	os.addParameter(new Parameter("x2",1,5,1.0,5.0));
	os.addParameter(new Parameter("x3",1,5,1.0,5.0));
	os.addParameter(new Parameter("x4",1,5,1.0,1.0));
	MathDescription mathDesc = MathDescriptionTest.getOdeExample();
	final String[] names = new String[] { "t", "calcium" };
	final Vector rowData = new Vector();
	rowData.add(new double[] {0.0, 0.0});
	rowData.add(new double[] {0.1, 1.0});
	rowData.add(new double[] {0.2, 2.0});
	rowData.add(new double[] {0.3, 3.0});
	rowData.add(new double[] {0.4, 4.0});
	rowData.add(new double[] {0.5, 5.0});
	rowData.add(new double[] {0.6, 6.0});
	rowData.add(new double[] {0.7, 7.0});
	rowData.add(new double[] {0.8, 8.0});
	rowData.add(new double[] {0.9, 9.0});
	rowData.add(new double[] {1.0, 10.0});
	ReferenceData constraintData = new ReferenceData() {
		public int getNumRows() {
			return rowData.size();
		}
		public String[] getColumnNames(){
			return names;
		}
		public double[] getColumnWeights(){
			return new double[] { 1.0, 1.0 };
		}
		public double[] getRowData(int rowIndex){
			return (double[])rowData.elementAt(rowIndex);
		}
		public int findColumn(java.lang.String colName) {
			for (int i = 0; i < names.length; i++){
				if (names[i].equals(colName)){
					return i;
				}
			}
			return -1;
		}	
		public double[] getColumnData(int columnIndex) {
			double[] colData = new double[rowData.size()];
			for (int i = 0; i < rowData.size(); i++){
				colData[i] = ((double[])rowData.elementAt(i))[columnIndex];
			}
			return colData;
		}
		public int getNumColumns() {
			return names.length;
		}
		public boolean compareEqual(cbit.util.Matchable obj){
			return equals(obj);
		}
		public int getDataSize() {			
			return 1;
		}
	};
	os.setObjectiveFunction(new OdeObjectiveFunction(mathDesc,constraintData));
	os.addConstraint(new Constraint(ConstraintType.NonlinearInequality,new Expression("25 - x1*x2*x3*x4")));
	os.addConstraint(new Constraint(ConstraintType.NonlinearEquality,new Expression("x1*x1 + x2*x2 + x3*x3 + x4*x4 - 40")));
	
	return os;
}
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		OptimizationSpec[] osArray = new OptimizationSpec[4];

	
		osArray[0] = OptimizationSpecTest.getCFSQP_Sample1();
		osArray[1] = OptimizationSpecTest.getExample();
		osArray[2] = OptimizationSpecTest.getExampleBoris();
 		osArray[3] = OptimizationSpecTest.getOdeExample();

 		for (int i = 0; i < osArray.length; i++){
			String vcml1 = osArray[i].getVCML();
			// System.out.println("vcml1 = :\n"+vcml1);

			OptimizationSpec os2 = new OptimizationSpec();
			CommentStringTokenizer tokens = new CommentStringTokenizer(vcml1);
			try {
				os2.read(tokens);	
				String vcml2 = os2.getVCML();
				if (vcml1.equals(vcml2)) {
					System.out.println("Successfully read VCML\n");
					// System.out.println("Successfully read from osArray["+i+"] VCML :\n"+vcml2);
				} else {
					System.out.println("Failed to read VCML\n");
					// System.out.println("Failed to read from osArray["+i+"] VCML :\n"+vcml1);
				}
			} catch (Throwable e) {
				System.out.println("Failed to read from osArray["+i+"] VCML :\n"+vcml1);
				e.printStackTrace(System.out);
			}
 		}

	}catch (Throwable e){
		e.printStackTrace(System.out);
		System.exit(0);
	}
}
}
