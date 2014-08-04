package cbit.vcell.tools;

import cbit.vcell.tools.PortableCommand;

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
		System.out.println("sum is " + ( a+ b) );
		return 0;
	}

	@Override
	public Exception exception() {
		return null;
	}
	
}