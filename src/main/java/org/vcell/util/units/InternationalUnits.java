package org.vcell.util.units;

import java.util.HashMap;
import java.util.Map;
import org.vcell.util.units.InternationalUnits.Unit.Status;

public class InternationalUnits {
	
	public static class Token {
		
		protected final String name;
		protected final String symbol;

		public Token(String name, String symbol) { 
			this.name = name; 
			this.symbol = symbol;
		}
	
		public String getName() { return name; }
		public String getSymbol() { return symbol; }

	}
	
	public static class Unit extends Token {
		
		public static enum Status { BASE, DERIVED, ACCEPTED, OTHER }
		
		protected final Status status;
		
		public Unit(String name, String symbol, Status status) {
			super(name, symbol);
			this.status = status;
		}
		
		public Status getStatus() { return status; }
		
	}

	protected static final Map<String, Token> dict = new HashMap<String, Token>();
	
	protected static Unit createUnit(String name, String symbol, Status status) {
		Unit unit = new Unit(name, symbol, status);
		dict.put(name, unit);
		dict.put(symbol, unit);
		return unit;
	}

	public static final Unit AMPERE = createUnit("ampere", "A", Status.BASE);
	public static final Unit BECQUEREL = createUnit("becquerel", "Bq", Status.DERIVED);
	public static final Unit CANDELA = createUnit("candela", "cd", Status.BASE);
	public static final Unit CELSIUS = createUnit("celsius", "Cel", Status.DERIVED);
	public static final Unit CENTIMETER = createUnit("centimeter", "cm", Status.DERIVED);
	public static final Unit COULOMB = createUnit("coulomb", "C", Status.DERIVED);
	public static final Unit DAY = createUnit("day", "d", Status.ACCEPTED);
	public static final Unit FARAD = createUnit("farad", "F", Status.DERIVED);
	public static final Unit GRAM = createUnit("gram", "g", Status.DERIVED);
	public static final Unit GRAY = createUnit("gray", "Gy", Status.DERIVED);
	public static final Unit HENRY = createUnit("henry", "H", Status.DERIVED);
	public static final Unit HERTZ = createUnit("hertz", "Hz", Status.DERIVED);
	public static final Unit HOUR = createUnit("hour", "h", Status.ACCEPTED);
	public static final Unit JOULE = createUnit("joule", "J", Status.DERIVED);
	public static final Unit KATAL = createUnit("katal", "kat", Status.DERIVED);
	public static final Unit KELVIN = createUnit("kelvin", "K", Status.BASE);
	public static final Unit KILOGRAM = createUnit("kilogram", "kg", Status.BASE);
	public static final Unit LITRE = createUnit("litre", "l", Status.ACCEPTED);
	public static final Unit LUMEN = createUnit("lumen", "lm", Status.DERIVED);
	public static final Unit LUX = createUnit("lux", "lx", Status.DERIVED);
	public static final Unit METRE = createUnit("metre", "m", Status.BASE);
	public static final Unit MINUTE = createUnit("minute", "min", Status.ACCEPTED);
	public static final Unit MOLE = createUnit("mole", "mol", Status.BASE);
	public static final Unit NEWTON = createUnit("newton", "N", Status.DERIVED);
	public static final Unit OHM = createUnit("ohm", "Ohm", Status.DERIVED);
	public static final Unit PASCAL = createUnit("pascal", "Pa", Status.DERIVED);
	public static final Unit RADIAN = createUnit("radian", "rad", Status.DERIVED);
	public static final Unit SECOND = createUnit("second", "s", Status.BASE);
	public static final Unit SIEMENS = createUnit("siemens", "S", Status.DERIVED);
	public static final Unit SIEVERT = createUnit("sievert", "Sv", Status.DERIVED);
	public static final Unit STERADIAN = createUnit("steradian", "sr", Status.DERIVED);
	public static final Unit TESLA = createUnit("tesla", "T", Status.DERIVED);
	public static final Unit VOLT = createUnit("volt", "V", Status.DERIVED);
	public static final Unit WATT = createUnit("watt", "W", Status.DERIVED);
	public static final Unit WEBER = createUnit("weber", "Wb", Status.DERIVED);
	
}
