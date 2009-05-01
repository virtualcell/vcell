package cbit.vcell.solvers;

/**
 * Insert the type's description here.
 * Creation date: (10/14/2002 5:32:15 PM)
 * @author: Jim Schaff
 */
public class MathExecutableTest {
/**
 * MathExecutableTest constructor comment.
 */
private MathExecutableTest() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (10/14/2002 5:32:29 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		if (args.length==0){
			System.out.println("usage: MathExecutableTest cmd [arg1] [arg2] ...");
			System.exit(1);
		}
		MathExecutable mathExecutable = new MathExecutable(args);
		mathExecutable.start();
		while (mathExecutable.getStatus().equals(org.vcell.util.ExecutableStatus.RUNNING)){
		}
		System.out.println("finished, return code "+mathExecutable.getExitValue());
		System.out.println("STDOUT");
		System.out.println(mathExecutable.getStdoutString());
		System.out.println("STDERR");
		System.out.println(mathExecutable.getStderrString());
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}
