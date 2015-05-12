package org.vcell.sbml;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.vcell.sbml.vcell.UnitRepresentation;

public abstract class ConversionTest {

	protected Map<String,String> dictionary = new TreeMap<>();

	public ConversionTest() {
		dictionary.put("A","ampere");
		dictionary.put("mol","mole");
		dictionary.put("s","second");
		dictionary.put("M","1000 mole.m3");
		dictionary.put("m","metre");
	}

	@Test
	public void load() throws FileNotFoundException, IOException {
		try (FileReader fr = new FileReader("unit.txt"); 
			BufferedReader bf = new BufferedReader(fr)) {
			while (bf.ready()) {
				String unitString = bf.readLine();
				System.out.println("processing " + unitString);
				process(unitString);
			}
		};
	}
	protected void process(String unitString) {
		UnitRepresentation unitRep = UnitRepresentation.parseUcar(unitString,dictionary);
		process(unitString, unitRep);
	}

	abstract protected void process(String unitString, UnitRepresentation unitRep);
}
