package org.vcell.sbml;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.vcell.sbml.vcell.UnitRepresentation;
import org.vcell.sbml.vcell.UnitRepresentation.Fundamental;

import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;

/**
 * test suite for {@link UnitRepresentation}
 */
public class UnitRepresentationTest {
	private VCUnitSystem unitSystem = ModelUnitSystem.createDefaultVCModelUnitSystem();
	private Map<String,String> dictionary = new TreeMap<>();
	
	
	
	public UnitRepresentationTest() {
		super();
		dictionary.put("m","metre");
		dictionary.put("A","ampere");
		dictionary.put("mol","mole");
		dictionary.put("s","second");
		dictionary.put("M","1000 mole.m3");
	}

	//@Test
	public void load( ) throws FileNotFoundException, IOException  {
		try (FileReader fr = new FileReader("unit.txt"); 
			BufferedReader bf = new BufferedReader(fr)) {
			while (bf.ready()) {
				String unitString = bf.readLine();
				parse(unitString);
			}
		};
	}
	
	@Test(expected = NullPointerException.class)
	public void nTest( ) {
		parse(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void constantTest( ) {
		parse("7");
	}
	@Test
	public void itemTest( ) {
		parse("item");
	}
	@Test
	public void molarTest( ) {
		parse("M");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void emptyTest( ) {
		parse("");
		
	}
	
	private void parse(String s) {
		UnitRepresentation unitRep = UnitRepresentation.parseUcar(s,dictionary);
		System.out.println(s + " = " + unitRep.toString( )); 
		for (Fundamental f : unitRep.getFundamentals()) {
			VCUnitDefinition vcUd = unitSystem.getInstance(f.unit);
			System.out.println("VC: " + vcUd.getSymbol());
			
		}
	}
}