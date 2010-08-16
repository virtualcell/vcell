package org.vcell.smoldyn.test;

import java.util.ArrayList;
import java.util.Vector;


public class VectorArrayListTest {

	
	public static void main(String [] args) {
		int repeat = 100;
		int size = 100000;
		for(int j = 0; j < 100; j++) {
			for(int i = 0; i < 4; i++) {
				doTime(i, repeat, size);
			}
			System.out.println();
		}
	}
	
	
	private static void doTime(final int choice, int repeatcount, int size) {
		long timestart = System.currentTimeMillis();
		if(choice == 0) {
			for(int i = 0; i < repeatcount; i++) {
				doVector(size);
			}
		} else if (choice == 1){
			for(int i = 0; i < repeatcount; i++) {
				doVector2(size);
			}
		} else if (choice == 2) {
			for(int i = 0; i < repeatcount; i++) {
				doArrayList(size);
			}
		} else if (choice == 3) {
			for(int i = 0; i < repeatcount; i++) {
				doArrayList2(size);
			}
		} else {
			throw new RuntimeException();
		}
		long timestop = System.currentTimeMillis();
		System.out.print((timestop - timestart) + "    ");
	}
	
	private static void doVector(int size) {
		Vector<Integer> a = new Vector<Integer>(0);
		for(int i = 0; i < size; i++) {
			a.addElement(new Integer(3));
		}
	}
	
	private static void doVector2(int size) {
		Vector<Integer> a = new Vector<Integer>(size + 10);
		for(int i = 0; i < size; i++) {
			a.addElement(new Integer(3));
		}
	}
	
	private static void doArrayList(int size) {
		ArrayList<Integer> a = new ArrayList<Integer>(0);
		for(int i = 0; i < size; i++) {
			a.add(new Integer(3));
		}
	}
	
	private static void doArrayList2(int size) {
		ArrayList<Integer> a = new ArrayList<Integer>(size + 10);
		for(int i = 0; i < size; i++) {
			a.add(new Integer(3));
		}
	}
}
