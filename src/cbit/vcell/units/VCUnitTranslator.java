package cbit.vcell.units;
import ucar.units.RationalNumber;
import cbit.vcell.units.VCUnitDefinition;

import ucar.units.BaseUnit;
import ucar.units.DerivedUnitImpl;
import ucar.units.Factor;
import ucar.units.OffsetUnit;
import ucar.units.ScaledUnit;
import ucar.units.SI;
import ucar.units.StandardUnitDB;
import ucar.units.Unit;
import ucar.units.UnitException;
import ucar.units.UnitImpl;
import ucar.units.UnitName;

import org.jdom.Element;
import org.jdom.Namespace;
import org.vcell.cellml.CELLMLTags;

import java.util.ArrayList;
import java.util.TreeMap;

/**
This class provides unit translation support for the  VCML translations with SBML and CellML. Its placed here to preserve the 
protected access to the wrapped ucar units in VCUnitDefinition.

 * Creation date: (4/21/2004 11:32:44 AM)
 * @author: Rashad Badrawi
 */
public class VCUnitTranslator {

	//a utility class that holds cellML unit defs, coupled with their owner (model/compartment).
	public static class TransCellMLUnits {

		private String owner;                   				//model or compartment.
		private TreeMap unitDefs = new TreeMap();            	//cellml_unit_name mapped to a VCUnitDefinition.

		public TransCellMLUnits(String owner) {

			this.owner = owner;
		}
		
		public void addUnitDef(String cellUnitName, VCUnitDefinition unitDef) {

			if (unitDefs.containsKey(cellUnitName)) {
				System.err.println("Unit already defined: " + cellUnitName);             //a mild warning
			}
			unitDefs.put(cellUnitName, unitDef);
		}
		
		public String getOwner() { return owner; }
		
		public VCUnitDefinition getUnitDef(String cellUnitName) {

			return (VCUnitDefinition)unitDefs.get(cellUnitName);
		}
	}
	
	private static TreeMap cellmlUnits = new TreeMap();        //cellml_compartment/model_name mapped to inner_class
	private static Namespace tAttNamespace = Namespace.getNamespace("");           //temp

//no instances allowed
private VCUnitTranslator() { super(); }


	//takes a CellML units jdom element.Similar to its SBML counterpart.
	//part of the recursive retrieval of CellML units.
	public static VCUnitDefinition CellMLToVCUnit(Element source, Namespace sNamespace, Namespace sAttNamespace) {
 
		ArrayList list = new ArrayList(source.getChildren(CELLMLTags.UNIT, sNamespace));
		if (list.size() == 0) {
			throw new RuntimeException("No units found for CellML unit definition: " + 
										source.getAttributeValue(CELLMLTags.name, sAttNamespace) + " >>> 1");	
		}
		VCUnitDefinition totalUnit = null;
		Unit unit = null;
		for (int i = 0; i < list.size(); i++) {
			Element temp = (Element)list.get(i);
			unit = processCellMLUnit(temp, sNamespace, sAttNamespace);
			if (unit == null) {        //for dimensionless and unresolved?
				continue;
			}
			if (totalUnit == null) {
				totalUnit = VCUnitDefinition.getInstance(unit);
			} else {
				totalUnit = totalUnit.multiplyBy(VCUnitDefinition.getInstance(unit));        //?
			}
		}
		if (totalUnit != null) {
			String unitName = source.getAttributeValue(CELLMLTags.name, sAttNamespace);
			String ownerName = source.getParent().getAttributeValue(CELLMLTags.name, sAttNamespace);
			storeCellMLUnit(ownerName, unitName, totalUnit);
		}
		
		return totalUnit;
	}


	/** 
	 * a compromise for not allowing instances, used in the case of cellML...
	 * @deprecated shouldn't have static fields if we can help it.
	**/
	public static void clearCellMLUnits() {
		
		cellmlUnits.clear();
	}


//time mult not included.
//first call: (1.0, 0, unit, new ArrayList(), transType)
//Problem: units override each other's mult/multiplier before getting to the level of derived unit. 
//To avoid that, add a dimensionless unit for cases where 
	protected static ArrayList expandUcarCellML(double mult, double offset, Unit unit, ArrayList transUnits) {
	
		if (unit instanceof UnitImpl) {
			UnitImpl unitImpl = (UnitImpl)unit;
			if (unitImpl instanceof DerivedUnitImpl) {
				DerivedUnitImpl baseUnit = (DerivedUnitImpl)unitImpl;
				//String symbol = baseUnit.getID();
				//String name = baseUnit.getName();
				Factor factors [] = baseUnit.getDimension().getFactors();
				for (int i = 0; i < factors.length; i++) {
					RationalNumber exponent = factors[i].getExponent();
					//String baseSymbol  = factors[i].getBase().getID();
					String baseName  = ((BaseUnit)factors[i].getBase()).getName();
					//System.out.println(baseName + " " + exponent);
					if (factors.length > 1) {
						if (i == 0) {
		 					transUnits.add(getCellMLTransUnit(new RationalNumber(1), mult, offset, DerivedUnitImpl.DIMENSIONLESS.getName()));
						}
						transUnits.add(getCellMLTransUnit(exponent, 1.0, 0.0, baseName));
					} else {
						transUnits.add(getCellMLTransUnit(exponent, mult, offset, baseName));
					}
				}
				return transUnits;
			} else if (unitImpl instanceof OffsetUnit) {
				OffsetUnit offsetUnit = (OffsetUnit)unitImpl;
				offset += offsetUnit.getOffset();
				//offset = offsetUnit.getOffset();
				if (offsetUnit.getUnit() != offsetUnit.getDerivedUnit()){
					return expandUcarCellML(mult, offset, offsetUnit.getUnit(), transUnits);
				}
			} else if (unitImpl instanceof ScaledUnit) {
				ScaledUnit multdUnit = (ScaledUnit)unitImpl;
				mult *= multdUnit.getScale();
				System.out.println("mult: " + mult);
				if (multdUnit.getUnit() != multdUnit.getDerivedUnit()){
					return expandUcarCellML(mult, offset, multdUnit.getUnit(), transUnits);
				}
			}
			if (unitImpl.getDerivedUnit() != unit) {                           //i.e. we have not reached the base unit, yet
				return expandUcarCellML(mult, offset, unitImpl.getDerivedUnit(), transUnits);
			} 
		} else {
			System.err.println("Unable to process unit translation for CellML: " + " " + unit.getSymbol());
		}

		return null;
	}


//works for both SBML and CellML
	public static VCUnitDefinition getBaseUnit(String kind) {

		Unit unit;
		
		if (kind == null || kind.length() == 0) {
			throw new IllegalArgumentException("Invalid base unit kind: " + kind + " >>> 1");
		}
		try {
			StandardUnitDB unitDB = StandardUnitDB.instance();
			if (kind.equals(CELLMLTags.noDimUnit)) {             
				unit = DerivedUnitImpl.DIMENSIONLESS;
			} else if (kind.equals(CELLMLTags.ITEM)) {
				System.out.println("CellML 'item' unit found, interpreted as 'molecule'");
				unit = unitDB.get("molecules");
			} else {
				unit = unitDB.get(kind);
			}
		} catch(UnitException e) {
			System.err.println("Unable to retrieve the standard unit database. >>> 3"); 
			e.printStackTrace();
			return null;
		}

		if (unit == null) {
			return null;
		} else {
			return VCUnitDefinition.getInstance(unit);
		}
	}


//ucar defines 'dimensionless' names as an empty string, which can be problematic if there is a 'real' empty string.
//for now, no scales for neither SBML or CELLML. There are not offsets in SBML level 1.
//assumes that the calling model has already defined 'molecules' (if an SBML model, it redefines 'item')
	private static Element getCellMLTransUnit(RationalNumber exponent, double mult, double offset, String baseName) {

		//System.out.println(exponent + " " + mult + " " + offset + " " + baseName + " " + transType);
		Element baseUnit = new Element(CELLMLTags.UNIT, CELLMLTags.CELLML_NS);
		
		if (baseName.equals(DerivedUnitImpl.DIMENSIONLESS.getName())) {
 	    		baseUnit.setAttribute(CELLMLTags.units, CELLMLTags.noDimUnit, tAttNamespace);
		} else {
			baseUnit.setAttribute(CELLMLTags.units, baseName, tAttNamespace);
		}
		//baseUnit.setAttribute(CELLMLTags.prefix, String.valueOf(scale), tAttNamespace); 
		baseUnit.setAttribute(CELLMLTags.mult, String.valueOf(mult), tAttNamespace);   
		baseUnit.setAttribute(CELLMLTags.exponent, exponent.toString(), tAttNamespace);
		baseUnit.setAttribute(CELLMLTags.offset, String.valueOf(offset), tAttNamespace); 
		
        return baseUnit;
	}


//check first if its a base unit. If not, search if its defined 'locally', then 'globally'.
	public static VCUnitDefinition getMatchingCellMLUnitDef(Element owner, Namespace sAttNamespace, String unitSymbol) {

		VCUnitDefinition unitDef = null;
		
		if (owner == null || sAttNamespace == null) {
			throw new IllegalArgumentException("CellML unit owner/namespace not probably defined: " + owner + " " + sAttNamespace);
		}
		unitDef = getBaseUnit(unitSymbol);
		if (unitDef != null) {
			return unitDef;
		}
		String ownerName = owner.getAttributeValue(CELLMLTags.name, sAttNamespace);
		TransCellMLUnits tunits = (TransCellMLUnits)cellmlUnits.get(ownerName);
		//if not found at component level, see if its defined at model level.
		if (tunits == null && owner.getName().equals(CELLMLTags.COMPONENT)) {
			tunits = (TransCellMLUnits)cellmlUnits.get(owner.getParent().getAttributeValue(CELLMLTags.name, sAttNamespace));
		}
		if (tunits == null) {
			return null;
		}
		unitDef = (VCUnitDefinition)tunits.getUnitDef(unitSymbol);

		return unitDef;                                  //either a valid unit (if found) or null
	}


	private static Integer multiplierToScale(double mult) {

		final double ERROR_MARGIN = 1e-8;
		double scale = Math.log(Math.abs(mult))/Math.log(10);
		double reconsituted = Math.pow(10,Math.round(scale));
		double error = Math.abs((mult - reconsituted)/mult);
		System.out.println(mult + " " + scale + " " + error); 
		if (error < ERROR_MARGIN) {
		   return new Integer((int)Math.round(scale));        //cast from long to int
		} else {
			return null;
		}
	}


//derives the ucar unit equivalent to a single cellml 'unit'
//part of the recursive retrieval of CellML units.
	private static Unit processCellMLUnit(Element cellUnit, Namespace sNamespace, Namespace sAttNamespace) {

		Unit unit = null;
		UnitName uName;
		String kind = cellUnit.getAttributeValue(CELLMLTags.units, sAttNamespace);
		if (kind == null || kind.length() == 0) {
			throw new RuntimeException("No kind specified for the CellML unit >>> 2");
		}
		String exp = cellUnit.getAttributeValue(CELLMLTags.exponent, sAttNamespace);
		String scaleStr = cellUnit.getAttributeValue(CELLMLTags.prefix, sAttNamespace);
		int scale;
		try {
			if (scaleStr == null || scaleStr.length() == 0) {
				scale = 0;
			} else {
				scale = Integer.parseInt(scaleStr);
			}
		} catch (NumberFormatException e) {                   //bad practice but no better way.
			scale = CELLMLTags.prefixToScale(scaleStr);
		}
		String multiplier = cellUnit.getAttributeValue(CELLMLTags.mult, sAttNamespace);
		String offset = cellUnit.getAttributeValue(CELLMLTags.offset, sAttNamespace);
		try { 
			StandardUnitDB unitDB = StandardUnitDB.instance();
			if (kind.equals(CELLMLTags.noDimUnit)) {             
				//special treatment. ignore all the other attributes. Not used in computing total unit.
				unit = DerivedUnitImpl.DIMENSIONLESS;
				uName = unit.getUnitName();
			} else if (CELLMLTags.isCellBaseUnit(kind)) {
				unit = unitDB.get(kind);
				uName = UnitName.newUnitName(kind); 
			} else {
				Element owner = cellUnit.getParent().getParent();
				String ownerName = owner.getAttributeValue(CELLMLTags.name, sAttNamespace);
				//check if already added.
				VCUnitDefinition unitDef = getMatchingCellMLUnitDef(owner, sAttNamespace, kind);
				if (unitDef != null) { 
					unit = unitDef.getUcarUnit();
				} else {                              
					//recursive block. assumes model level units are added before component level ones,
					//so no need to recurse through those as well.
					ArrayList siblings = new ArrayList(owner.getChildren(CELLMLTags.UNITS, sNamespace));
					Element cellUnitDef = null;
					for (int i = 0; i < siblings.size(); i++) {
						cellUnitDef = (Element)siblings.get(i);
						if (cellUnitDef.getAttributeValue(CELLMLTags.name, sAttNamespace).equals(kind)) {
							break;
						} else {
							cellUnitDef = null;
						}
					}
					if (cellUnitDef != null) {
						unitDef = CellMLToVCUnit(cellUnitDef, sNamespace, sAttNamespace);
					} else {
						System.err.println("Unit definition: " + kind + " is missing. >>> 3");
						return null;
					}
				}
				uName = UnitName.newUnitName(kind); 
			}
			if (exp != null && Integer.parseInt(exp) != 1) {
				unit = unit.raiseTo(new RationalNumber(Integer.parseInt(exp)));
			}
			if (scale != 0) {                        
				unit = new ScaledUnit(Double.parseDouble("1.0e" + scale), unit, uName);
			}
			if (multiplier != null && Double.parseDouble(multiplier) != 1) {
				unit = new ScaledUnit(Double.parseDouble(multiplier), unit, uName);
			}
			if (offset != null && Double.parseDouble(offset) != 0) {
				unit = new OffsetUnit(unit, Double.parseDouble(offset) , uName);
			}
		} catch (UnitException e) {
			e.printStackTrace();
			throw new cbit.vcell.units.VCUnitException("Unable to set unit value: "+e.getMessage());
		}

		return unit;
	}


	public static void storeCellMLUnit(String ownerName, String unitName, VCUnitDefinition unitDef) {

		TransCellMLUnits tunits = (TransCellMLUnits)cellmlUnits.get(ownerName);
		if (tunits == null) {                                 //is it the first unit added to this owner
			tunits = new TransCellMLUnits(ownerName);
		}
		tunits.addUnitDef(unitName, unitDef);
		cellmlUnits.put(ownerName, tunits);                   //overwrite old one, if already there.
	}


//returns a cellml 'Units' element
	public static Element VCUnitToCellML(String unitSymbol, Namespace tNamespace, Namespace tAttNamespace) {

		ArrayList transUnits;
		if (unitSymbol == null || unitSymbol.length() == 0) {
			throw new IllegalArgumentException("Invalid value for the VC unit: " + unitSymbol);
		}
		if (unitSymbol.equals(VCUnitDefinition.TBD_SYMBOL)) {
			System.err.println("No translation possible for unit " + VCUnitDefinition.TBD_SYMBOL);
			return null;
		}
		VCUnitDefinition VC_unitDef = VCUnitDefinition.getInstance(unitSymbol);
		Unit unit = VC_unitDef.getUcarUnit();
		Element unitDef = new Element(CELLMLTags.UNITS, tNamespace);
	    if (VC_unitDef.compareEqual(VCUnitDefinition.UNIT_DIMENSIONLESS)) {
		    unitDef.setAttribute(CELLMLTags.name, CELLMLTags.noDimUnit);
			transUnits = new ArrayList();
			transUnits.add(getCellMLTransUnit(new RationalNumber(1), 1.0, 0, CELLMLTags.noDimUnit));
		} else {
			unitDef.setAttribute(CELLMLTags.name, unitSymbol);
	    	transUnits = expandUcarCellML(1.0, 0, unit, new ArrayList());
		}
		for (int i = 0; i < transUnits.size(); i++) {   
			Element basicUnit = (Element)transUnits.get(i); 
			unitDef.addContent(basicUnit);
		}

		return unitDef;
	}
}