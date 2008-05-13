package org.vcell.optimization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class NativeOptSolverTest {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String optProblemXML = getXMLString("D:\\developer\\eclipse\\workspace\\numerics\\OptStandalone2\\test7.xml");
			OptSolverCallbacks optSolverCallbacks = new SimpleOptSolverCallbacks();
			NativeOptSolver nativeOptSolver = new NativeOptSolver();
			
			String optSolverResultSetXML = nativeOptSolver.nativeSolve_CFSQP(optProblemXML, optSolverCallbacks);
			System.out.println(optSolverResultSetXML);

			OptXmlReader optXmlReader = new OptXmlReader();
			OptimizationResultSet optResultSet = optXmlReader.getOptimizationResultSet(optSolverResultSetXML);		
			
			optResultSet.show();
			
			System.out.println("done");
		}catch (Exception e){
			e.printStackTrace(System.out);
		}

	}
	
	public static String getXMLString(String fileName) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String temp;
		StringBuffer buf = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			buf.append(temp);
			buf.append("\n");
		}
		
		return buf.toString();
	}



}
