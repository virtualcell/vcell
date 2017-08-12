/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.schemas;

/*   SBPAX  --- by Oliver Ruebenacker, UCHC --- April 2008 to May 2011
 *   The SBPAX Level 3 schema
 */

import java.util.Arrays;
import java.util.List;

import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.OWL;
import org.sbpax.impl.HashGraph;
import org.sbpax.schemas.util.NameSpace;
import org.sbpax.schemas.util.OntUtil;
import org.sbpax.schemas.util.UOMEUtil;
import org.sbpax.schemas.util.UOMEUtil.ExponentialExpression;
import org.sbpax.schemas.util.UOMEUtil.IdentityExpression;
import org.sbpax.schemas.util.UOMEUtil.OffsetExpression;
import org.sbpax.schemas.util.UOMEUtil.ProductExpression;
import org.sbpax.schemas.util.UOMEUtil.QuotientExpression;
import org.sbpax.schemas.util.UOMEUtil.ScalingExpression;


public class UOMEList {

	private static final String PI = "\u03c0";
	public static final String PERMIL = "\u2030";
	public static final String AO = "\u212b";
	public static final String MU = "\u03bc";
	public static final String DEGREE = "\u00b0";
	public static final String OMEGA = "\u03a9";

	public static final String uri = "http://www.sbpax.org/uome/list.owl";

	public static final NameSpace ns = new NameSpace("uome-list", uri + "#");

	public static final Graph schema = new HashGraph();

	public static final URI ontology = OntUtil.createOntologyNode(schema, uri);

	static {
		schema.add(ontology, OWL.IMPORTS, UOMECore.ontology);
	}

	public static final int NONUMBER = -1;
	
	public static final URI Dimensionless = 
		UOMEUtil.createUnit(schema, ns, "Dimensionless", "Dimensionless", "", 186);
	public static final URI Metre = UOMEUtil.createUnit(schema, ns, "Metre", "metre", "m", 8);
	public static final URI Kilogram = 
		UOMEUtil.createUnit(schema, ns, "Kilogram", "kilogram", "kg", 9);
	public static final URI Second = UOMEUtil.createUnit(schema, ns, "Second", "second", "s", 10);
	public static final URI Ampere = UOMEUtil.createUnit(schema, ns, "Ampere", "ampere", "A", 11);
	public static final URI Kelvin = UOMEUtil.createUnit(schema, ns, "Kelvin", "kelvin", "K", 12);
	public static final URI Candela = 
		UOMEUtil.createUnit(schema, ns, "Candela", "candela", "cd", 14);
	public static final URI Mole = UOMEUtil.createUnit(schema, ns, "Mole", "mole", "mol", 13);
	
	public static final List<URI> siBaseUnits = Arrays.asList(Metre, Kilogram, Second, Ampere, Kelvin, Candela, Mole);

	public static final URI Hertz = UOMEUtil.createUnit(schema, ns, "Hertz", "hertz", "Hz", 106,
			new ExponentialExpression(Second, -1));
	public static final URI Radian = UOMEUtil.createUnit(schema, ns, "Radian", "radian", "rad", 
			123, new IdentityExpression(Dimensionless));
	public static final URI Steradian = UOMEUtil.createUnit(schema, ns, "Steradian", "steradian", 
			"sr", 125, new IdentityExpression(Dimensionless));
	public static final URI KilogramTimesMetre = UOMEUtil.createUnit(schema, ns, 
			"KilogramTimesMetre", "kilogram times metre", "kg*m", NONUMBER, 
			new ProductExpression(Kilogram, Metre));
	public static final URI SecondSquared = UOMEUtil.createUnit(schema, ns, 
			"SecondSquared", "second squared", "s^2", NONUMBER, 
			new ExponentialExpression(Second, 2));
	public static final URI Newton = UOMEUtil.createUnit(schema, ns, "Newton", "newton", 
			"kg*m/s^2", 108, new QuotientExpression(KilogramTimesMetre, SecondSquared));
	public static final URI SquareMetre = UOMEUtil.createUnit(schema, ns, "SquareMetre", 
			"square metre", "m^2", 80, new ExponentialExpression(Metre, 2));
	public static final URI Pascal = UOMEUtil.createUnit(schema, ns, "Pascal", "pascal", "Pa", 
			110, new QuotientExpression(Newton, SquareMetre));
	public static final URI Joule = UOMEUtil.createUnit(schema, ns, "Joule", "joule", "J", 112, 
			new ProductExpression(Newton, Metre));
	public static final URI Watt = UOMEUtil.createUnit(schema, ns, "Watt", "watt", "W", 114, 
			new QuotientExpression(Joule, Second));
	public static final URI Coulomb = UOMEUtil.createUnit(schema, ns, "Coulomb", "coulomb", "C", 
			220, new ProductExpression(Ampere, Second));
	public static final URI Volt = UOMEUtil.createUnit(schema, ns, "Volt", "volt", "V", 218, 
			new QuotientExpression(Joule, Coulomb));
	public static final URI Farad = UOMEUtil.createUnit(schema, ns, "Farad", "farad", "F", NONUMBER, 
			new QuotientExpression(Coulomb, Volt));
	public static final URI Ohm = UOMEUtil.createUnit(schema, ns, "Ohm", "ohm", OMEGA, NONUMBER, 
			new QuotientExpression(Volt, Ampere));
	public static final URI Siemens = UOMEUtil.createUnit(schema, ns, "Siemens", "Siemens", "S", 
			264, new QuotientExpression(Ampere, Volt));
	public static final URI Weber = UOMEUtil.createUnit(schema, ns, "Weber", "weber", "Wb", 226, 
			new QuotientExpression(Joule, Ampere));
	public static final URI VoltSecond = UOMEUtil.createUnit(schema, ns, "VoltSecond", 
			"volt second", "V*s", 228, new ProductExpression(Volt, Second));
	public static final URI Tesla = UOMEUtil.createUnit(schema, ns, "Tesla", "tesla", "T", 228, 
			new QuotientExpression(VoltSecond, SquareMetre), new QuotientExpression(Weber, SquareMetre));
	public static final URI Henry = UOMEUtil.createUnit(schema, ns, "Henry", "henry", "H", NONUMBER, 
			new QuotientExpression(VoltSecond, Ampere), new QuotientExpression(Weber, Ampere));
	public static final URI DegreeCelsius = UOMEUtil.createUnit(schema, ns, "DegreeCelsius", 
			"degree celsius", DEGREE + "C", 27, new OffsetExpression(Kelvin, 273.15));
	public static final URI Lumen = UOMEUtil.createUnit(schema, ns, "Lumen", "lumen", "lm", 118, 
			new ProductExpression(Candela, Steradian));
	public static final URI Lux = UOMEUtil.createUnit(schema, ns, "Lux", "lux", "lx", 116, 
			new ProductExpression(Lumen, SquareMetre));
	public static final URI Becquerel = UOMEUtil.createUnit(schema, ns, "Becquerel", "becquerel", 
			"Bq", 132, new ExponentialExpression(Second, -1));
	public static final URI Gray = UOMEUtil.createUnit(schema, ns, "Gray", "gray", "Gy", 134, 
			new QuotientExpression(Joule, Kilogram));
	public static final URI Sievert = UOMEUtil.createUnit(schema, ns, "Sievert", "sievert", "Sv", 
			137, new QuotientExpression(Joule, Kilogram));
	public static final URI Katal = UOMEUtil.createUnit(schema, ns, "Katal", "katal", "kat", 
			120, new QuotientExpression(Mole, Second));
	
	public static final List<URI> siNamedDerivedUnits = Arrays.asList(Hertz, Radian, Steradian, Newton, Pascal, Joule, Watt, Coulomb,
			Volt, Farad, Ohm, Siemens, Weber, Tesla, Henry, DegreeCelsius, Lumen, Lux, Becquerel, Gray, Sievert, Katal);

	// units accepted for use with SI units
	
	public static final URI Minute = UOMEUtil.createUnit(schema, ns, "Minute", "minute", "min", 31, 
			new ScalingExpression(Second, 60));
	public static final URI Hour = UOMEUtil.createUnit(schema, ns, "Hour", "hour", "h", 32, 
			new ScalingExpression(Minute, 60), new ScalingExpression(Second, 3600));
	public static final URI Day = UOMEUtil.createUnit(schema, ns, "Day", "day", "d", 33, 
			new ScalingExpression(Hour, 24));
	public static final URI DegreeOfArc = UOMEUtil.createUnit(schema, ns, "DegreeOfArc", 
			"degree of arc", DEGREE, 185, new ScalingExpression(Radian, (Math.PI/180)));
	public static final URI MinuteOfArc = UOMEUtil.createUnit(schema, ns, "MinuteOfArc", 
			"minute of arc", "'", NONUMBER, new ScalingExpression(DegreeOfArc, (1.0/60.0)));
	public static final URI SecondOfArc = UOMEUtil.createUnit(schema, ns, "SecondOfArc", 
			"second of arc", "\"", NONUMBER, new ScalingExpression(MinuteOfArc, (1.0/60.0)));
	public static final URI Hectare = UOMEUtil.createUnit(schema, ns, "Hectare", "hectare", "ha", 
			NONUMBER, new ScalingExpression(SquareMetre, 10000));
	public static final URI CubicMetre = UOMEUtil.createUnit(schema, ns, "CubicMetre", 
			"cubic metre", "m^3", 96, new ExponentialExpression(Metre, 3));
	public static final URI Litre = UOMEUtil.createUnit(schema, ns, "Litre", "litre", "l", 99, 
			new ScalingExpression(CubicMetre, 0.001));
	public static final URI Tonne = UOMEUtil.createUnit(schema, ns, "Tonne", "tonne", "t", NONUMBER, 
			new ScalingExpression(Kilogram, 1000));
	
	// other UO units
	
	public static final URI Centimetre = UOMEUtil.createUnit(schema, ns, "Centimetre", "centimetre", 
			"cm", 15, new ScalingExpression(Metre, 0.01));
	public static final URI Millimetre = UOMEUtil.createUnit(schema, ns, "Millimetre", "millimetre", 
			"mm", 16, new ScalingExpression(Metre, 0.001));
	public static final URI Micrometre = UOMEUtil.createUnit(schema, ns, "Micrometre", "micrometre", 
			MU + "m", 17, new ScalingExpression(Metre, 1e-6));
	public static final URI Nanometre = UOMEUtil.createUnit(schema, ns, "Nanometre", "nanometre", 
			"nm", 18, new ScalingExpression(Metre, 1e-9));
	public static final URI Angstrom = UOMEUtil.createUnit(schema, ns, "Angstrom", "angstrom", 
			AO, 19, new ScalingExpression(Metre, 1e-10));
	public static final URI Picometre = UOMEUtil.createUnit(schema, ns, "Picometre", "picometre", 
			"pm", 20, new ScalingExpression(Metre, 1e-12));
	public static final URI Gram = UOMEUtil.createUnit(schema, ns, "Gram", "gram", 
			"g", 21, new ScalingExpression(Kilogram, 1e-3));
	public static final URI Milligram = UOMEUtil.createUnit(schema, ns, "Milligram", "milligram", 
			"mg", 22, new ScalingExpression(Gram, 1e-3));
	public static final URI Microgram = UOMEUtil.createUnit(schema, ns, "Microgram", "microgram", 
			MU + "g", 23, new ScalingExpression(Gram, 1e-6));
	public static final URI Nanogram = UOMEUtil.createUnit(schema, ns, "Nanogram", "nanogram", 
			"ng", 24, new ScalingExpression(Gram, 1e-9));
	public static final URI Picogram = UOMEUtil.createUnit(schema, ns, "Picogram", "picogram", 
			"pg", 25, new ScalingExpression(Gram, 1e-12));
	public static final URI Femtogram = UOMEUtil.createUnit(schema, ns, "Femtogram", "femtogram", 
			"fg", 26, new ScalingExpression(Gram, 1e-15));
	public static final URI Millisecond = UOMEUtil.createUnit(schema, ns, "Millisecond", "millisecond", 
			"ms", 28, new ScalingExpression(Second, 1e-3));
	public static final URI Microsecond = UOMEUtil.createUnit(schema, ns, "Microsecond", "microsecond", 
			MU + "s", 29, new ScalingExpression(Second, 1e-6));
	public static final URI Picosecond = UOMEUtil.createUnit(schema, ns, "Picosecond", "picosecond", 
			"ps", 30, new ScalingExpression(Second, 1e-12));
	public static final URI Week = UOMEUtil.createUnit(schema, ns, "Week", "week", 
			"week", 34, new ScalingExpression(Day, 7));
	public static final URI Month = UOMEUtil.createUnit(schema, ns, "Month", "month", 
			"mon", 35, new ScalingExpression(Day, 30));
	public static final URI Year = UOMEUtil.createUnit(schema, ns, "Year", "year", 
			"a", 36, new ScalingExpression(Day, 365.25));
	public static final URI Milliampere = UOMEUtil.createUnit(schema, ns, "Milliampere", "milliampere", 
			"mA", 37, new ScalingExpression(Ampere, 1e-3));
	public static final URI Microampere = UOMEUtil.createUnit(schema, ns, "Microampere", "microampere", 
			MU + "A", 38, new ScalingExpression(Ampere, 1e-6));
	public static final URI Micromole = UOMEUtil.createUnit(schema, ns, "Micromole", "micromole", 
			MU + "mol", 39, new ScalingExpression(Mole, 1e-6));
	public static final URI Millimole = UOMEUtil.createUnit(schema, ns, "Millimole", "millimole", 
			"mmol", 40, new ScalingExpression(Mole, 1e-3));
	public static final URI Nanomole = UOMEUtil.createUnit(schema, ns, "Nanomole", "nanomole", 
			"nmol", 41, new ScalingExpression(Mole, 1e-9));
	public static final URI Picomole = UOMEUtil.createUnit(schema, ns, "Picomole", "picomole", 
			"pmol", 42, new ScalingExpression(Mole, 1e-12));
	public static final URI Femtomole = UOMEUtil.createUnit(schema, ns, "Femtomole", "femtomole", 
			"pmol", 43, new ScalingExpression(Mole, 1e-15));
	public static final URI Attomole = UOMEUtil.createUnit(schema, ns, "Attomole", "attomole", 
			"amol", 44, new ScalingExpression(Mole, 1e-18));
	public static final URI Molar = UOMEUtil.createUnit(schema, ns, "Molar", "molar", 
			"M", 62, new QuotientExpression(Mole, Litre));
	public static final URI Millimolar = UOMEUtil.createUnit(schema, ns, "Millimolar", "millimolar", 
			"mM", 63, new ScalingExpression(Molar, 1e-3));
	public static final URI Micromolar = UOMEUtil.createUnit(schema, ns, "Micromolar", "micromolar", 
			MU + "M", 64, new ScalingExpression(Molar, 1e-6));
	public static final URI Nanomolar = UOMEUtil.createUnit(schema, ns, "Nanomolar", "nanomolar", 
			"nM", 65, new ScalingExpression(Molar, 1e-9));
	public static final URI Picomolar = UOMEUtil.createUnit(schema, ns, "Picomolar", "Picomolar", 
			"pM", 66, new ScalingExpression(Molar, 1e-12));
	public static final URI Molal = UOMEUtil.createUnit(schema, ns, "Molal", "molal", 
			"mol/kg", 68, new QuotientExpression(Mole, Kilogram));
	public static final URI Millimolal = UOMEUtil.createUnit(schema, ns, "Millimolal", "millimolal", 
			"mmol/kg", 69, new ScalingExpression(Molal, 1e-3));
	public static final URI Micromolal = UOMEUtil.createUnit(schema, ns, "Micromolal", "micromolal", 
			MU + "mol/kg", 70, new ScalingExpression(Molal, 1e-6));
	public static final URI Nanomolal = UOMEUtil.createUnit(schema, ns, "Nanomolal", "nanomolal", 
			"nmol/kg", 71, new ScalingExpression(Molal, 1e-9));
	public static final URI Picomolal = UOMEUtil.createUnit(schema, ns, "Picomolal", "picomolal", 
			"pmol/kg", 72, new ScalingExpression(Molar, 1e-12));
	public static final URI Femtomolar = UOMEUtil.createUnit(schema, ns, "Femtomolar", "femtomolar", 
			"fM", 73, new ScalingExpression(Molar, 1e-15));
	public static final URI Normal = UOMEUtil.createUnit(schema, ns, "Normal", "normal", 
			"N", 75, new QuotientExpression(Gram, Litre));
	public static final URI MoleFraction = UOMEUtil.createUnit(schema, ns, "MoleFraction", 
			"mole fraction", "", 76, new IdentityExpression(Dimensionless));
	public static final URI MetrePerSecondSquared = UOMEUtil.createUnit(schema, ns, 
			"MetrePerSecondSquared", "metre per second squared", "m/s^2", 77, 
			new QuotientExpression(Metre, SecondSquared));
	public static final URI RadianPerSecondSquared = UOMEUtil.createUnit(schema, ns, 
			"RadianPerSecondSquared", "radian per second squared", "rad/s^2", 78, 
			new QuotientExpression(Radian, SecondSquared));
	public static final URI RadianPerSecond = UOMEUtil.createUnit(schema, ns, "RadianPerSecond", 
			"radian per second", "rad/s", 79, new QuotientExpression(Radian, SecondSquared));
	public static final URI SquareCentimetre = UOMEUtil.createUnit(schema, ns, "SquareCentimetre", 
			"square centimetre", "cm^2", 81, new ExponentialExpression(Centimetre, 2));
	public static final URI SquareMillimetre = UOMEUtil.createUnit(schema, ns, "SquareMillimetre", 
			"square millimetre", "mm^2", 82, new ExponentialExpression(Millimetre, 2));
	public static final URI KilogramPerCubicMetre = UOMEUtil.createUnit(schema, ns, 
			"KilogramPerCubicMetre", "kilogram per cubic metre", "kg/m^3", 83, 
			new QuotientExpression(Kilogram, CubicMetre));
	public static final URI CubicCentimetre = UOMEUtil.createUnit(schema, ns, 
			"CubicCentimetre", "cubic centimetre", "cm^3", 97, 
			new ExponentialExpression(Centimetre, 3));
	public static final URI GramPerCubicCentimetre = UOMEUtil.createUnit(schema, ns, 
			"GramPerCubicCentimetre", "gram per cubic centimetre", "g/cm^3", 84, 
			new QuotientExpression(Gram, CubicCentimetre));
	public static final URI CandelaPerSquareMetre = UOMEUtil.createUnit(schema, ns, 
			"CandelaPerSquareMetre", "candela per square metre", "C/m^2", 85, 
			new QuotientExpression(Candela, SquareMetre));
	public static final URI KilogramPerSquareMetre = UOMEUtil.createUnit(schema, ns, 
			"KilogramPerSquareMetre", "kilogram per square metre", "kg/m^2", 86, 
			new QuotientExpression(Kilogram, SquareMetre));
	public static final URI KilogramPerMole = UOMEUtil.createUnit(schema, ns, 
			"KilogramPerMole", "kilogram per mole", "kg/mol", 87, 
			new QuotientExpression(Kilogram, Mole));
	public static final URI GramPerMole = UOMEUtil.createUnit(schema, ns, 
			"GramPerMole", "gram per mole", "g/mol", 88, new QuotientExpression(Gram, Mole));
	public static final URI CubicMetrePerMole = UOMEUtil.createUnit(schema, ns, "CubicMetrePerMole", 
			"cubic metre per mole", "m^3/mol", 89, new QuotientExpression(CubicMetre, Mole));
	public static final URI CubicCentimetrePerMole = UOMEUtil.createUnit(schema, ns, 
			"CubicCentimetrePerMole", "cubic centimetre per mole", "cm^3/mol", 90, 
			new QuotientExpression(CubicCentimetre, Mole));
	public static final URI KilogramMetre = UOMEUtil.createUnit(schema, ns, "KilogramMetre", 
			"kilogram metre", "kg*m", NONUMBER, new ProductExpression(Kilogram, Metre));
	public static final URI KilogramMetrePerSecond = UOMEUtil.createUnit(schema, ns, 
			"KilogramMetrePerSecond", "kilogram metre per second", "kg*m/s", 91, 
			new QuotientExpression(KilogramMetre, Second));
	public static final URI TurnsPerSecond = UOMEUtil.createUnit(schema, ns, "TurnsPerSecond", 
			"turns per second", "1/s", 92, new ExponentialExpression(Second, -1));
	public static final URI CubicMetrePerKilogram = UOMEUtil.createUnit(schema, ns, 
			"CubicMetrePerKilogram", "cubic metre per kilogram", "m^3/kg", 93, 
			new QuotientExpression(CubicMetre, Kilogram));
	public static final URI MetrePerSecond = UOMEUtil.createUnit(schema, ns, "MetrePerSecond", 
			"metre per second", "m/s", 94, new QuotientExpression(Metre, Second));
	public static final URI Millilitre = UOMEUtil.createUnit(schema, ns, "Millilitre", "millilitre", 
			"ml", 98, new ScalingExpression(Litre, 1e-3));
	public static final URI Decimetre = UOMEUtil.createUnit(schema, ns, "Decimetre", "decimetre", 
			"dm", NONUMBER, new ScalingExpression(Metre, 0.1));
	public static final URI CubicDecimetre = UOMEUtil.createUnit(schema, ns, "CubicDecimetre", 
			"cubic decimetre", "dm^3", 100, new ExponentialExpression(Decimetre, 3));
	public static final URI Microlitre = UOMEUtil.createUnit(schema, ns, "Microlitre", "microlitre", 
			MU + "l", 101, new ScalingExpression(Litre, 1e-6));
	public static final URI Nanolitre = UOMEUtil.createUnit(schema, ns, "Nanolitre", "nanolitre", 
			"nl", 102, new ScalingExpression(Litre, 1e-9));
	public static final URI Picolitre = UOMEUtil.createUnit(schema, ns, "Picolitre", "picolitre", 
			"pl", 103, new ScalingExpression(Litre, 1e-12));
	public static final URI Femtolitre = UOMEUtil.createUnit(schema, ns, "Femtolitre", "femtolitre", 
			"fl", 104, new ScalingExpression(Litre, 1e-15));
	public static final URI Curie = UOMEUtil.createUnit(schema, ns, "Curie", "curie", 
			"Ci", 133, new ScalingExpression(Becquerel, 3.7e10));
	public static final URI Rad = UOMEUtil.createUnit(schema, ns, "Rad", "rad", 
			"rad", 135, new ScalingExpression(Gray, 0.01));
	public static final URI CoulombPerKilogram = UOMEUtil.createUnit(schema, ns, 
			"CoulombPerKilogram", "coulomb per kilogram", "C/kg", NONUMBER, 
			new QuotientExpression(Coulomb, Kilogram));
	public static final URI Roentgen = UOMEUtil.createUnit(schema, ns, "Roentgen", "roentgen", 
			"R", 136, new ScalingExpression(CoulombPerKilogram, 2.58e-4));
	public static final URI Millisievert = UOMEUtil.createUnit(schema, ns, "Millisievert", 
			"millisievert", "mSv", 138, new ScalingExpression(Sievert, 1e-3));
	public static final URI Microsievert = UOMEUtil.createUnit(schema, ns, "Microsievert", 
			"microsievert", MU + "Sv", 139, new ScalingExpression(Sievert, 1e-6));
	public static final URI RoentgenEquivalentMan = UOMEUtil.createUnit(schema, ns, 
			"RoentgenEquivalentMan", "roentgen equivalent man", "rem", 140, 
			new ScalingExpression(Sievert, 1e-2));
	public static final URI Microgray = UOMEUtil.createUnit(schema, ns, "Microgray", 
			"microgray", MU + "Gy", 141, new ScalingExpression(Gray, 1e-6));
	public static final URI Milligray = UOMEUtil.createUnit(schema, ns, "Milligray", 
			"milligray", "mGy", 142, new ScalingExpression(Gray, 1e-3));
	public static final URI Nanogray = UOMEUtil.createUnit(schema, ns, "Nanogray", 
			"nanogray", "nGy", 143, new ScalingExpression(Gray, 1e-9));
	public static final URI Nanosievert = UOMEUtil.createUnit(schema, ns, "Nanosievert", 
			"nanosievert", "nSv", 144, new ScalingExpression(Sievert, 1e-9));
	public static final URI Millicurie = UOMEUtil.createUnit(schema, ns, "Millicurie", 
			"millicurie", "mCi", 145, new ScalingExpression(Curie, 1e-3));
	public static final URI Microcurie = UOMEUtil.createUnit(schema, ns, "Microcurie", 
			"microcurie", MU + "Ci", 146, new ScalingExpression(Gray, 1e-6));
	public static final URI DisintegrationsPerMinute = UOMEUtil.createUnit(schema, ns, 
			"DisintegrationsPerMinute", "disintegrations per minute", "dpm", 147, 
			new ExponentialExpression(Minute, -1));
	public static final URI CountsPerMinute = UOMEUtil.createUnit(schema, ns, "CountsPerMinute", 
			"counts per minute", "cpm", 148, new ExponentialExpression(Minute, -1));
	public static final URI Nanosecond = UOMEUtil.createUnit(schema, ns, "Nanosecond", 
			"nanosecond", "ns", 150, new ScalingExpression(Second, 1e-9));
	public static final URI Century = UOMEUtil.createUnit(schema, ns, "Century", 
			"century", "century", 151, new ScalingExpression(Year, 100));
	public static final URI Foot = UOMEUtil.createUnit(schema, ns, "Foot", 
			"foot", "ft", NONUMBER, new ScalingExpression(Metre, 0.3048));
	public static final URI SquareFoot = UOMEUtil.createUnit(schema, ns, "SquareFoot", 
			"square foot", "ft^2", NONUMBER, new ExponentialExpression(Foot, 2));
	public static final URI FootCandle = UOMEUtil.createUnit(schema, ns, "FootCandle", 
			"foot-candle", "fc", 153, new QuotientExpression(Lumen, SquareFoot));
	public static final URI WattPerSquareMetre = UOMEUtil.createUnit(schema, ns, 
			"WattPerSquareMetre", "watt per square metre", "W/m^2", 155, 
			new QuotientExpression(Watt, SquareMetre));
	public static final URI Einstein = UOMEUtil.createUnit(schema, ns, "Einstein", "einstein", 
			"einstein", NONUMBER, new IdentityExpression(Mole));
	public static final URI SquareMetreSecond = UOMEUtil.createUnit(schema, ns, 
			"SquareMetreSecond", "square metre second", "m^2s", NONUMBER, 
			new ProductExpression(SquareMetre, Second));
	public static final URI EinsteinPerSquareMetrePerSecond = UOMEUtil.createUnit(schema, ns, 
			"EinsteinPerSquareMetrePerSecond", "einstein per square metre per second", 
			"einstein/(m^2s)", 156, new QuotientExpression(Einstein, SquareMetreSecond));
	public static final URI SteradianSquareMetre = UOMEUtil.createUnit(schema, ns, 
			"SteradianSquareMetre", "steradian square metre", "sr*m^2", NONUMBER, 
			new ProductExpression(Steradian, SquareMetre));
	public static final URI WattPerSteradianPerSquareMetre = UOMEUtil.createUnit(schema, ns, 
			"WattPerSteradianPerSquareMetre", "watt per steradian per square metre", 
			"W/(sr*m^2)", 158, new QuotientExpression(Watt, SteradianSquareMetre));
	public static final URI MicroEinsteinPerSquareMetrePerSecond = UOMEUtil.createUnit(schema, ns, 
			"MicroEinsteinPerSquareMetrePerSecond", "microeinstein per square metre per second", 
			MU + "einstein/(m^2s)", 160, new ScalingExpression(Einstein, 1e-6));
	public static final URI WattPerSteradian = UOMEUtil.createUnit(schema, ns, 
			"WattPerSteradian", "watt per steradian", "W/sr", 162, 
			new QuotientExpression(Watt, Steradian));
	public static final URI PartsPerHundred = UOMEUtil.createUnit(schema, ns, "PartsPerHundred", 
			"parts per hundred", "%", 167, new ScalingExpression(Dimensionless, 1e-2));
	public static final URI PartsPerThousand = UOMEUtil.createUnit(schema, ns, "PartsPerThousand", 
			"parts per thousand", PERMIL, 168, new ScalingExpression(Dimensionless, 1e-3));
	public static final URI PartsPerMillion = UOMEUtil.createUnit(schema, ns, "PartsPerMillion", 
			"parts per million", "ppm", 169, new ScalingExpression(Dimensionless, 1e-6));
	public static final URI PartsPerBillion = UOMEUtil.createUnit(schema, ns, "PartsPerBillion", 
			"parts per billion", "ppb", 170, new ScalingExpression(Dimensionless, 1e-9));
	public static final URI PartsPerTrillion = UOMEUtil.createUnit(schema, ns, "PartsPerTrillion", 
			"parts per trillion", "ppt", 171, new ScalingExpression(Dimensionless, 1e-12));
	public static final URI PartsPerQuadrillion = UOMEUtil.createUnit(schema, ns, 
			"PartsPerQuadrillion", "parts per quadrillion", "ppt", 172, 
			new ScalingExpression(Dimensionless, 1e-15));
	public static final URI GramPerMillilitre = UOMEUtil.createUnit(schema, ns, 
			"GramPerMillilitre", "gram per millilitre", "g/ml", 173, 
			new QuotientExpression(Gram, Millilitre));
	public static final URI KilogramPerLitre = UOMEUtil.createUnit(schema, ns, "KilogramPerLitre", 
			"kilogram per litre", "kg/l", 174, new QuotientExpression(Kilogram, Litre));
	public static final URI GramPerLitre = UOMEUtil.createUnit(schema, ns, "GramPerLitre", 
			"gram per litre", "g/l", 175, new QuotientExpression(Gram, Litre));
	public static final URI MilligramPerMillilitre = UOMEUtil.createUnit(schema, ns, 
			"MilligramPerMillilitre", "milligram per millilitre", "mg/ml", 176, 
			new QuotientExpression(Milligram, Millilitre));
	public static final URI EnzymeUnit = UOMEUtil.createUnit(schema, ns, "EnzymeUnit", 
			"enzyme unit", "U", 181, new ScalingExpression(Katal, 1e-6/60));
	public static final URI KilogramPerMetre = UOMEUtil.createUnit(schema, ns, "KilogramPerMetre", 
			"kilogram per metre", "kg/m", 184, new QuotientExpression(Kilogram, Metre));
	public static final URI Percent = UOMEUtil.createUnit(schema, ns, "Percent", "percent", "%", 
			187, new ScalingExpression(Dimensionless, 1e-2));
	public static final URI Pi = UOMEUtil.createUnit(schema, ns, "Pi", "pi", PI, 188, 
			new ScalingExpression(Dimensionless, Math.PI));
	public static final URI Count = UOMEUtil.createUnit(schema, ns, "Count", "count", "", 189, 
			new IdentityExpression(Dimensionless));
	public static final URI Ratio = UOMEUtil.createUnit(schema, ns, "Ratio", "ratio", "", 190, 
			new IdentityExpression(Dimensionless));
	public static final URI Fraction = UOMEUtil.createUnit(schema, ns, "Fraction", "fraction", "", 
			191, new IdentityExpression(Dimensionless));
	public static final URI MoleculeCount = UOMEUtil.createUnit(schema, ns, "MoleculeCount", 
			"molecule count", "", 192, new IdentityExpression(Dimensionless));
	public static final URI DegreeCelsiusScaledToFahrenheit = UOMEUtil.createUnit(schema, ns, 
			"DegreeCelsiusScaledToFahrenheit", "degree celsius scaled to fahrenheit", 
			"1.8 " + DEGREE + "C", NONUMBER, new ScalingExpression(DegreeCelsius, 1.8));	
	public static final URI DegreeFahrenheit = UOMEUtil.createUnit(schema, ns, 
			"DegreeFahrenheit", "degree fahrenheit", DEGREE + "F", 195, 
			new OffsetExpression(DegreeCelsiusScaledToFahrenheit, -32));	
	public static final URI PH = UOMEUtil.createUnit(schema, ns, "PH", "pH", "", 
			196, new IdentityExpression(Dimensionless));
	public static final URI LitrePerKilogram = UOMEUtil.createUnit(schema, ns, "LitrePerKilogram", 
			"litre per kilogram", "l/kg", 197, new QuotientExpression(Litre, Kilogram));
	public static final URI MillilitrePerKilogram = UOMEUtil.createUnit(schema, ns, 
			"MillilitrePerKilogram", "millilitre per kilogram", "ml/kg", 198, 
			new QuotientExpression(Millilitre, Kilogram));
	public static final URI MicrolitrePerKilogram = UOMEUtil.createUnit(schema, ns, 
			"MicrolitrePerKilogram", "microlitre per kilogram", MU + "l/kg", 199, 
			new QuotientExpression(Millilitre, Kilogram));
	public static final URI CountPerMillilitre = UOMEUtil.createUnit(schema, ns, 
			"CountPerMillilitre", "count per millilitre", "1/ml", 201, 
			new QuotientExpression(Dimensionless, Millilitre));
	public static final URI KatalPerCubicMetre = UOMEUtil.createUnit(schema, ns, 
			"KatalPerCubicMetre", "katal per cubic metre", "kat/m^3", 203, 
			new QuotientExpression(Katal, CubicMetre));
	public static final URI KatalPerLitre = UOMEUtil.createUnit(schema, ns, "KatalPerLitre", 
			"katal per litre", "kat/l", 204, new QuotientExpression(Katal, CubicMetre));
	public static final URI MillilitrePerCubicMetre = UOMEUtil.createUnit(schema, ns, 
			"MillilitrePerCubicMetre", "millilitre per cubic metre", "ml/m^3", 206, 
			new QuotientExpression(Millilitre, CubicMetre), new ScalingExpression(Dimensionless, 1e-6));
	public static final URI MillilitrePerLitre = UOMEUtil.createUnit(schema, ns, 
			"MillilitrePerLitre", "millilitre per litre", "ml/l", 207, 
			new QuotientExpression(Millilitre, Litre), new ScalingExpression(Dimensionless, 1e-3));
	public static final URI Decilitre = UOMEUtil.createUnit(schema, ns, "Decilitre", "decilitre", 
			"dl", 209, new ScalingExpression(Litre, 0.1));
	public static final URI GramPerDecilitre = UOMEUtil.createUnit(schema, ns, 
			"GramPerDecilitre", "gram per decilitre", "g/dl", 208, 
			new QuotientExpression(Gram, Decilitre));
	public static final URI DisintegrationsPerSecond = UOMEUtil.createUnit(schema, ns, 
			"DisintegrationsPerSecond", "disintegrations per second", "disintegrations per second", 216, 
			new ExponentialExpression(Minute, -1));
	public static final URI Dalton = UOMEUtil.createUnit(schema, ns, "Dalton", "dalton", "Da", 221, 
			new ScalingExpression(Kilogram, 1.660538782e-27));
	public static final URI Kilodalton = UOMEUtil.createUnit(schema, ns, "Kilodalton", 
			"kilodalton", "kDa", 222, new ScalingExpression(Dalton, 1000));
	public static final URI WattHour = UOMEUtil.createUnit(schema, ns, "WattHour", 
			"watt-hour", "Wh", 223, new ProductExpression(Watt, Hour));
	public static final URI Kilowatt = UOMEUtil.createUnit(schema, ns, "Kilowatt", 
			"kilowatt", "kW", NONUMBER, new ScalingExpression(Watt, 1000));
	public static final URI KilowattHour = UOMEUtil.createUnit(schema, ns, "KilowattHour", 
			"kilowatt-hour", "kWh", 224, new ProductExpression(Kilowatt, Hour));
	public static final URI VoltHour = UOMEUtil.createUnit(schema, ns, "VoltHour", 
			"volt-hour", "Vh", 229, new ProductExpression(Volt, Hour));
	public static final URI Kilovolt = UOMEUtil.createUnit(schema, ns, "Kilovolt", 
			"kilovolt", "kV", 248, new ScalingExpression(Volt, 1000));
	public static final URI KilovoltHour = UOMEUtil.createUnit(schema, ns, "KilovoltHour", 
			"kilovolt-hour", "kVh", 230, new ProductExpression(Kilovolt, Hour));
	public static final URI Bit = UOMEUtil.createUnit(schema, ns, "Bit", 
			"bit", "bit", 232);
	public static final URI Byte = UOMEUtil.createUnit(schema, ns, "Byte", "byte", "B", 233, 
			new ScalingExpression(Bit, 8));
	public static final URI Kilobyte = UOMEUtil.createUnit(schema, ns, "Kilobyte", "kilobyte", 
			"kB", 234, new ScalingExpression(Byte, 1000));
	public static final URI Megabyte = UOMEUtil.createUnit(schema, ns, "Megabyte", "megabyte", 
			"MB", 235, new ScalingExpression(Byte, 1e6));
	public static final URI Inch = UOMEUtil.createUnit(schema, ns, "Inch", "inch", "in", NONUMBER, 
			new ScalingExpression(Metre, 25.4e-3));
	public static final URI DotsPerInch = UOMEUtil.createUnit(schema, ns, "DotsPerInch", 
			"dots per inch", "DPI", 240, new QuotientExpression(Count, Inch));
	public static final URI MicronPixel = UOMEUtil.createUnit(schema, ns, "MicronPixel", 
			"micron pixel", MU + "m/dot", 241, new IdentityExpression(Micrometre));
	public static final URI PixelsPerInch = UOMEUtil.createUnit(schema, ns, "PixelsPerInch", 
			"pixels per inch", "dots/in", 242, new QuotientExpression(Count, Inch));
	public static final URI PixelsPerMillimetre = UOMEUtil.createUnit(schema, ns, 
			"PixelsPerMillimetre", "pixels per millimetre", "dots/mm", 243, 
			new QuotientExpression(Count, Millimetre));
	public static final URI BasePairs = UOMEUtil.createUnit(schema, ns, 
			"BasePairs", "base pairs", "base pairs", 244, new IdentityExpression(Count));
	public static final URI Kibibyte = UOMEUtil.createUnit(schema, ns, "Kibibyte", "kibibyte", 
			"KiB", 245, new ScalingExpression(Byte, 1024));
	public static final URI Mebibyte = UOMEUtil.createUnit(schema, ns, "Mebibyte", "mebibyte", 
			"MiB", 246, new ScalingExpression(Kibibyte, 1024));
	public static final URI Millivolt = UOMEUtil.createUnit(schema, ns, "Millivolt", "millivolt", 
			"mV", 247, new ScalingExpression(Volt, 1e-3));
	public static final URI Microvolt = UOMEUtil.createUnit(schema, ns, "Microvolt", "microvolt", 
			MU + "V", 249, new ScalingExpression(Volt, 1e-6));
	public static final URI Nanovolt = UOMEUtil.createUnit(schema, ns, "Nanovolt", "nanovolt", 
			"nV", 250, new ScalingExpression(Volt, 1e-9));
	public static final URI Picovolt = UOMEUtil.createUnit(schema, ns, "Picovolt", "picovolt", 
			"pV", 251, new ScalingExpression(Volt, 1e-12));
	public static final URI Megavolt = UOMEUtil.createUnit(schema, ns, "Megavolt", "megavolt", 
			"MV", 252, new ScalingExpression(Volt, 1e+6));
	public static final URI NewtonPerMetre = UOMEUtil.createUnit(schema, ns, "NewtonPerMetre", 
			"newton per metre", "N/m", 254, new QuotientExpression(Newton, Metre));
	public static final URI Dyne = UOMEUtil.createUnit(schema, ns, "Dyne", "dyne", "dyn", NONUMBER, 
			new ScalingExpression(Newton, 1e-5));
	public static final URI DynePerCentimetre = UOMEUtil.createUnit(schema, ns, 
			"DynePerCentimetre", "dyne per centimetre", "dyn/cm", 255, 
			new QuotientExpression(Dyne, Centimetre));
	public static final URI PascalSecond = UOMEUtil.createUnit(schema, ns, "PascalSecond", 
			"pascal second", "Pa*s", 257, new ProductExpression(Pascal, Second));
	public static final URI Poise = UOMEUtil.createUnit(schema, ns, "Poise", "poise", "P", 258, 
			new ScalingExpression(PascalSecond, 0.1));
	public static final URI Bel = UOMEUtil.createUnit(schema, ns, "Bel", "bel", "B", NONUMBER);
	public static final URI Decibel = UOMEUtil.createUnit(schema, ns, "Decibel", "decibel", "dB", 
			259, new ScalingExpression(Bel, 0.1));
	public static final URI MetreKelvin = UOMEUtil.createUnit(schema, ns, "MetreKelvin", 
			"metre kelvin", "m*K", NONUMBER, new ProductExpression(Metre, Kelvin));
	public static final URI WattPerMetreKelvin = UOMEUtil.createUnit(schema, ns, 
			"WattPerMetreKelvin", "watt per metre kelvin", "W/(m*K)", 265, 
			new QuotientExpression(Watt, MetreKelvin));
	public static final URI ElementaryCharge = UOMEUtil.createUnit(schema, ns, "ElementaryCharge", 
			"elementary charge", "e", NONUMBER, new ScalingExpression(Coulomb, 1.60217653e-19));
	public static final URI Electronvolt = UOMEUtil.createUnit(schema, ns, "Electronvolt", 
			"electronvolt", "eV", 266, new ProductExpression(ElementaryCharge, Volt));
	public static final URI VoltPerMetre = UOMEUtil.createUnit(schema, ns, "VoltPerMetre", 
			"volt per metre", "V/m", 268, new QuotientExpression(Volt, Metre));
	public static final URI Absorbance = UOMEUtil.createUnit(schema, ns, "Absorbance", 
			"absorbance", "A", 269);
	public static final URI MicrolitrePerMinute = UOMEUtil.createUnit(schema, ns, 
			"MicrolitrePerMinute", "microlitre per minute", MU + "l/min", 271, 
			new QuotientExpression(Microlitre, Minute));
	public static final URI Atmosphere = UOMEUtil.createUnit(schema, ns, "Atmosphere", 
			"atmosphere", "atm", NONUMBER, new ScalingExpression(Pascal, 101325));
	public static final URI Torr = UOMEUtil.createUnit(schema, ns, "Torr", 
			"torr", "Torr", NONUMBER, new ScalingExpression(Atmosphere, 1.0/760.0));
	public static final URI MillimetresOfMercury = UOMEUtil.createUnit(schema, ns, 
			"MillimetresOfMercury", "millimetres of mercury", "mmHg", 272, 
			new ScalingExpression(Torr, 1.0));
	public static final URI MilligramPerLitre = UOMEUtil.createUnit(schema, ns, 
			"MilligramPerLitre", "milligram per litre", "mg/l", 273, 
			new QuotientExpression(Milligram, Litre));
	public static final URI MicrogramPerMillilitre = UOMEUtil.createUnit(schema, ns, 
			"MicrogramPerMillilitre", "microgram per millilitre", MU + "g/ml", 274, 
			new QuotientExpression(Microgram, Millilitre));
	public static final URI NanogramPerMillilitre = UOMEUtil.createUnit(schema, ns, 
			"NanogramPerMillilitre", "nanogram per millilitre", "ng/ml", 275, 
			new QuotientExpression(Nanogram, Millilitre));
	public static final URI NanomolarSecond = UOMEUtil.createUnit(schema, ns, "NanomolarSecond", 
			"nanomolar second", "nM*s", NONUMBER, new ProductExpression(Nanomolar, Second));
	public static final URI CountPerNanomolarSecond = UOMEUtil.createUnit(schema, ns, 
			"CountPerNanomolarSecond", "count per nanomolar second", "1/(nM*s)", 281, 
			new QuotientExpression(Count, NanomolarSecond));
	public static final URI MolarSecond = UOMEUtil.createUnit(schema, ns, "MolarSecond", 
			"molar second", "M*s", NONUMBER, new ProductExpression(Molar, Second));
	public static final URI CountPerMolarSecond = UOMEUtil.createUnit(schema, ns, 
			"CountPerMolarSecond", "count per molar second", "1/(M*s)", 282, 
			new QuotientExpression(Count, MolarSecond));
	public static final URI MolePerMilligram = UOMEUtil.createUnit(schema, ns, 
			"MolePerMilligram", "mole per milligram", "mol/mg", NONUMBER, 
			new QuotientExpression(Mole, Milligram));
	public static final URI MilligramMinute = UOMEUtil.createUnit(schema, ns, 
			"MilligramMinute", "milligram minute", "mg*min", NONUMBER, 
			new ProductExpression(Milligram, Minute));
	public static final URI MolePerMilligramMinute = UOMEUtil.createUnit(schema, ns, 
			"MolePerMilligramMinute", "mole per milligram minute", "mol/(mg*min)", NONUMBER, 
			new QuotientExpression(Mole, MilligramMinute));
	public static final URI PerSecond = UOMEUtil.createUnit(schema, ns, 
			"PerSecond", "per second", "1/s", NONUMBER, new ExponentialExpression(Second, -1));
	public static final URI MicromolePerMilligramMinute = UOMEUtil.createUnit(schema, ns, 
			"MicromolePerMilligramMinute", "micromole per milligram minute", MU + "mol/(mg*min)", 
			NONUMBER, new QuotientExpression(Micromole, MilligramMinute));
	public static final URI Picosiemens = UOMEUtil.createUnit(schema, ns, 
			"Picosiemens", "picosiemens", "pS", 
			NONUMBER, new ScalingExpression(Siemens, 1e-12));
	public static final URI PerMinute = UOMEUtil.createUnit(schema, ns, 
			"PerMinute", "per minute", "1/min", 
			NONUMBER, new ExponentialExpression(Minute, -1));

	public static List<URI> unitsEqualOne = Arrays.asList(Dimensionless, Count);
	
}
