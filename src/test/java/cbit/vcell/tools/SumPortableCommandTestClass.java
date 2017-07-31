package cbit.vcell.tools;

import cbit.vcell.simdata.PortableCommand;

/**
 * test class
 * @author gweatherby
 */
public class SumPortableCommandTestClass implements PortableCommand {
	private final int a;
	private final int b;
	
	public SumPortableCommandTestClass(int a, int b) {
		super();
		this.a = a;
		this.b = b;
	}

	@Override
	public int execute() {
		String message = "sum is " + ( a+ b);
		//System.out.println(message );
		return 0;
	}

	@Override
	public Exception exception() {
		return null;
	}
	
}