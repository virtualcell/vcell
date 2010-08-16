package org.vcell.smoldyn.test;


import java.util.HashSet;


public class HashsetTest {

	public static void main(String [] args) {
		HashSet<Thing> a = new HashSet<Thing>();
		Thing thing = new Thing("bob");
		print(thing.toString());
		Thing thing2 = new Thing("bob");
		Thing thing3 = new Thing("forse");
		Thing thing4 = new Thing(null);
		a.add(thing2);
		a.add(thing3);
		a.add(null);
		a.add(null);
		a.add(thing4);
		a.add(new Thing("whatever"));
		a.add(new Thing("forse"));
		if (thing == thing2) {
			print("they are ==");
		} else {
			print("un ==");
		}
		if (thing.equals(thing2)) {
			print("they are .equals");
		} else {
			print("un .equals");
		}
		a.add(thing);
		
		Thing [] boo = a.toArray(new Thing [a.size()]);
		print("names:  ");
		for(Thing b : boo) {
			if(b == null) {
				print(b);
			} else {
				print(b.getName());
			}
		}
	}
	
	private static class Thing {
		private String name;
		
		private Thing(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		
		public boolean equals(Object o) {
			if (!(o instanceof Thing)) {
				return false;
			}
			Thing t = (Thing) o;
			if(t.getName() == this.getName()) {
				return true;
			}
			return false;
		}
		
		public int hashCode() {
//			int superhashcode = super.hashCode();
//			return this.name.hashCode();
			return super.hashCode();
		}
	}
	
	private static void print(Object o) {
		System.out.println(o);
	}
}
