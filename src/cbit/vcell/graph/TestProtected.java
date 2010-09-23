package cbit.vcell.graph;

public class TestProtected {
	
	public static class D {
		protected int x;		
	}
	
	public static class A extends D {
	}
	
	public static class B extends A {
		public void f() {
			x = 5;
			A a = new A();
			a.x = 7;
		}
	}
	
	public static class C extends A {
		public void g() {
			B b = new B();
			b.x = 3;
		}
	}

}
