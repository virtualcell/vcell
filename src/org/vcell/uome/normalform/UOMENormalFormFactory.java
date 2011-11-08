/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.uome.normalform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.sybil.util.JavaUtil;
import org.vcell.sybil.util.lists.ListOfNone;
import org.vcell.sybil.util.lists.ListOfTwo;
import org.vcell.sybil.util.maps.MapOfNone;
import org.vcell.sybil.util.maps.MapOfTwo;
import org.vcell.uome.core.UOMEBinaryExpression;
import org.vcell.uome.core.UOMEEquivalenzExpression;
import org.vcell.uome.core.UOMEExponentialExpression;
import org.vcell.uome.core.UOMEExpression;
import org.vcell.uome.core.UOMEOffsetExpression;
import org.vcell.uome.core.UOMEProductExpression;
import org.vcell.uome.core.UOMEQuotientExpression;
import org.vcell.uome.core.UOMEScalingExpression;
import org.vcell.uome.core.UOMESingleUnitExpression;
import org.vcell.uome.core.UOMEUnit;

public class UOMENormalFormFactory {
	
	protected final Collection<? extends UOMEUnit> unitsEqualOne;
	protected final Comparator<UOMEUnit> unitComparator;
	
	public UOMENormalFormFactory(Collection<? extends UOMEUnit> unitsEqualOne, Comparator<UOMEUnit> unitComparator) {
		this.unitsEqualOne = unitsEqualOne;
		this.unitComparator = unitComparator;
	}
	
	public UOMENormalForm createNormalEmpty() {
		List<UOMEUnit> unitList = Collections.<UOMEUnit>emptyList();
		Map<UOMEUnit, Double> exponentMap = Collections.<UOMEUnit, Double>emptyMap();
		return new UOMENormalForm(unitList, exponentMap);		
	}
	
	public UOMENormalForm createNormalUnit(UOMEUnit unit) {
		List<UOMEUnit> unitList = Collections.singletonList(unit);
		Map<UOMEUnit, Double> exponentMap = Collections.singletonMap(unit, new Double(1.0));
		return new UOMENormalForm(unitList, exponentMap);					
	}
	
	public UOMENormalForm createNormalEquivalenz(UOMEUnit unit) {
		if(!unitsEqualOne.contains(unit)) {
			return createNormalUnit(unit);			
		} else {
			return createNormalEmpty();
		}
	}

	public UOMENormalForm createNormalScaling(UOMEUnit unit, double factor) {
		if(!unitsEqualOne.contains(unit)) {
			List<UOMEUnit> unitList = Collections.singletonList(unit);
			Map<UOMEUnit, Double> exponentMap = Collections.singletonMap(unit, new Double(1.0));
			return new UOMENormalForm(unitList, exponentMap, factor);					
		} else {
			List<UOMEUnit> unitList = Collections.<UOMEUnit>emptyList();
			Map<UOMEUnit, Double> exponentMap = Collections.<UOMEUnit, Double>emptyMap();
			return new UOMENormalForm(unitList, exponentMap, factor);		
		}
	}

	public UOMENormalForm createNormalOffset(UOMEUnit unit, double offset) {
		if(!unitsEqualOne.contains(unit)) {
			List<UOMEUnit> unitList = Collections.singletonList(unit);
			Map<UOMEUnit, Double> exponentMap = Collections.singletonMap(unit, new Double(1.0));
			return new UOMENormalForm(unitList, exponentMap, 1.0, offset);					
		} else {
			List<UOMEUnit> unitList = Collections.<UOMEUnit>emptyList();
			Map<UOMEUnit, Double> exponentMap = Collections.<UOMEUnit, Double>emptyMap();
			return new UOMENormalForm(unitList, exponentMap, 1.0, offset);		
		}
	}
	
	public UOMENormalForm createNormalExponential(UOMEUnit unit, double exponent) {
		if(!unitsEqualOne.contains(unit) && !UOMENumberUtil.almostEqual(exponent, 0.0)) {
			List<UOMEUnit> unitList = Collections.singletonList(unit);
			Map<UOMEUnit, Double> exponentMap = Collections.singletonMap(unit, new Double(exponent));
			return new UOMENormalForm(unitList, exponentMap);					
		} else {
			return createNormalEmpty();					
		}
	}
	
	public UOMENormalForm createNormalProduct(UOMEUnit unit1, UOMEUnit unit2) {
		boolean unit1EqualsOne = unitsEqualOne.contains(unit1);
		boolean unit2EqualsOne = unitsEqualOne.contains(unit2);
		if(!unit1EqualsOne && !unit2EqualsOne) {
			if(!JavaUtil.equals(unit1, unit2)) {
				List<UOMEUnit> unitList = unitComparator.compare(unit1, unit2) <= 0 ? 
						new ListOfTwo<UOMEUnit>(unit1, unit2) : new ListOfTwo<UOMEUnit>(unit2, unit1);
				Map<UOMEUnit, Double> exponentMap = new MapOfTwo<UOMEUnit, Double>(unit1, new Double(1.0), unit2, new Double(1.0));
				return new UOMENormalForm(unitList, exponentMap);					
			} else {
				List<UOMEUnit> unitList = Collections.singletonList(unit1);
				Map<UOMEUnit, Double> exponentMap = Collections.singletonMap(unit1, new Double(2.0));
				return new UOMENormalForm(unitList, exponentMap);								
			}
		} else if(unit1EqualsOne && !unit2EqualsOne) {
			return createNormalUnit(unit2);								
		} else if(!unit1EqualsOne && unit2EqualsOne) {
			return createNormalUnit(unit1);								
		} else {
			return createNormalEmpty();					
		}
	}
	
	public UOMENormalForm createNormalQuotient(UOMEUnit unit1, UOMEUnit unit2) {
		boolean unit1EqualsOne = unitsEqualOne.contains(unit1);
		boolean unit2EqualsOne = unitsEqualOne.contains(unit2);
		if(!unit1EqualsOne && !unit2EqualsOne) {
			if(!JavaUtil.equals(unit1, unit2)) {
				List<UOMEUnit> unitList = unitComparator.compare(unit1, unit2) <= 0 ? 
						new ListOfTwo<UOMEUnit>(unit1, unit2) : new ListOfTwo<UOMEUnit>(unit2, unit1);
				Map<UOMEUnit, Double> exponentMap = new MapOfTwo<UOMEUnit, Double>(unit1, new Double(1.0), unit2, new Double(-1.0));
				return new UOMENormalForm(unitList, exponentMap);					
			} else {
				return createNormalEmpty();								
			}
		} else if(unit1EqualsOne && !unit2EqualsOne) {
			List<UOMEUnit> unitList = Collections.singletonList(unit2);
			Map<UOMEUnit, Double> exponentMap = Collections.singletonMap(unit2, new Double(-1.0));
			return new UOMENormalForm(unitList, exponentMap);								
		} else if(!unit1EqualsOne && unit2EqualsOne) {
			return createNormalUnit(unit1);								
		} else {
			return createNormalEmpty();					
		}		
	}

	public UOMENormalForm createNormalScaling(UOMENormalForm form, double factor) {
		Set<UOMEUnit> units = new HashSet<UOMEUnit>();
		units.addAll(form.getUnits());
		units.removeAll(unitsEqualOne);
		List<UOMEUnit> unitList = new ArrayList<UOMEUnit>();
		unitList.addAll(units);
		Collections.sort(unitList, unitComparator);
		return new UOMENormalForm(unitList, form.getExponentMap(), factor*form.getFactor(), factor*form.getOffset());		
	}
	
	public UOMENormalForm createNormalOffset(UOMENormalForm form, double offset) {
		Set<UOMEUnit> units = new HashSet<UOMEUnit>();
		units.addAll(form.getUnits());
		units.removeAll(unitsEqualOne);
		List<UOMEUnit> unitList = new ArrayList<UOMEUnit>();
		unitList.addAll(units);
		Collections.sort(unitList, unitComparator);
		return new UOMENormalForm(unitList, form.getExponentMap(), form.getFactor(), form.getOffset() + offset);		
	}
	
	@SuppressWarnings("serial")
	public static class CanNotCreateNormalFormException extends Exception {
		public CanNotCreateNormalFormException(String message) { super(message); }
	}
	
	public UOMENormalForm createNormalExponential(UOMENormalForm form, double exponent) throws CanNotCreateNormalFormException {
		if(UOMENumberUtil.almostEqual(exponent, 0.0)) { 
			return new UOMENormalForm(new ListOfNone<UOMEUnit>(), new MapOfNone<UOMEUnit, Double>()); 
		}
		if(UOMENumberUtil.almostEqual(exponent, 1.0)) { return form; }
		if(!UOMENumberUtil.almostEqual(form.getOffset(), 0.0)) {
			throw new CanNotCreateNormalFormException("Can not exponentiate a unit expression with an offset.");
		}
		Set<UOMEUnit> units = new HashSet<UOMEUnit>();
		units.addAll(form.getUnits());
		units.removeAll(unitsEqualOne);
		List<UOMEUnit> unitList = new ArrayList<UOMEUnit>();
		unitList.addAll(units);
		Collections.sort(unitList, unitComparator);
		Map<UOMEUnit, Double> exponentMap = new HashMap<UOMEUnit, Double>();
		for(Map.Entry<UOMEUnit, Double> entry : form.getExponentMap().entrySet()) {
			UOMEUnit unit = entry.getKey();
			Double unitExponent = entry.getValue();
			exponentMap.put(unit, new Double(unitExponent.doubleValue()*exponent));
		}
		return new UOMENormalForm(unitList, exponentMap, Math.pow(form.getFactor(), exponent));
	}

	public UOMENormalForm createNormalProduct(UOMEUnit unit, UOMENormalForm form) throws CanNotCreateNormalFormException {
		if(!UOMENumberUtil.almostEqual(form.getOffset(), 0.0)) {
			throw new CanNotCreateNormalFormException("Can not multiply two unit expressions if one has an offset.");
		}
		Set<UOMEUnit> units = new HashSet<UOMEUnit>();
		units.add(unit);
		units.addAll(form.getUnits());
		units.removeAll(unitsEqualOne);
		HashMap<UOMEUnit, Double> exponentMap = new HashMap<UOMEUnit, Double>();
		exponentMap.putAll(form.getExponentMap());
		Double exponentOld = exponentMap.get(unit);
		if(exponentOld == null) {
			exponentMap.put(unit, 1.0);
		} else {
			double exponentNew = exponentOld + 1.0;
			if(!UOMENumberUtil.almostEqual(exponentNew, 0.0)) {
				exponentMap.put(unit, new Double(exponentNew));
			} else {
				units.remove(unit);
				exponentMap.remove(unit);
			}
		}
		List<UOMEUnit> unitList = new ArrayList<UOMEUnit>();
		unitList.addAll(units);
		Collections.sort(unitList, unitComparator);
		return new UOMENormalForm(unitList, exponentMap, form.getFactor());
	}

	public UOMENormalForm createNormalProduct(UOMENormalForm form, UOMEUnit unit) throws CanNotCreateNormalFormException {
		return createNormalProduct(unit, form);
	}

	public UOMENormalForm createNormalQuotient(UOMEUnit unit, UOMENormalForm form) throws CanNotCreateNormalFormException {
		if(!UOMENumberUtil.almostEqual(form.getOffset(), 0.0)) {
			throw new CanNotCreateNormalFormException("Can not divide a unit expressions by another if one has an offset.");
		}
		Set<UOMEUnit> units = new HashSet<UOMEUnit>();
		units.add(unit);
		units.addAll(form.getUnits());
		units.removeAll(unitsEqualOne);
		HashMap<UOMEUnit, Double> exponentMap = new HashMap<UOMEUnit, Double>();
		for(Map.Entry<UOMEUnit, Double> entry : form.getExponentMap().entrySet()) {
			UOMEUnit unit2 = entry.getKey();
			Double exponentOld = entry.getValue();
			exponentMap.put(unit2, new Double(-exponentOld.doubleValue()));
		}
		exponentMap.putAll(form.getExponentMap());
		Double exponentOld = exponentMap.get(unit);
		if(exponentOld == null) {
			exponentMap.put(unit, 1.0);
		} else {
			double exponentNew = exponentOld + 1.0;
			if(!UOMENumberUtil.almostEqual(exponentNew, 0.0)) {
				exponentMap.put(unit, new Double(exponentNew));
			} else {
				units.remove(unit);
				exponentMap.remove(unit);
			}
		}
		List<UOMEUnit> unitList = new ArrayList<UOMEUnit>();
		unitList.addAll(units);
		Collections.sort(unitList, unitComparator);
		return new UOMENormalForm(unitList, exponentMap, form.getFactor());
	}
	
	public UOMENormalForm createNormalQuotient(UOMENormalForm form, UOMEUnit unit) throws CanNotCreateNormalFormException {
		if(!UOMENumberUtil.almostEqual(form.getOffset(), 0.0)) {
			throw new CanNotCreateNormalFormException("Can not divide a unit expressions by another if one has an offset.");
		}
		Set<UOMEUnit> units = new HashSet<UOMEUnit>();
		units.add(unit);
		units.addAll(form.getUnits());
		units.removeAll(unitsEqualOne);
		HashMap<UOMEUnit, Double> exponentMap = new HashMap<UOMEUnit, Double>();
		exponentMap.putAll(form.getExponentMap());
		Double exponentOld = exponentMap.get(unit);
		if(exponentOld == null) {
			exponentMap.put(unit, -1.0);
		} else {
			double exponentNew = exponentOld - 1.0;
			if(!UOMENumberUtil.almostEqual(exponentNew, 0.0)) {
				exponentMap.put(unit, new Double(exponentNew));
			} else {
				units.remove(unit);
				exponentMap.remove(unit);
			}
		}
		List<UOMEUnit> unitList = new ArrayList<UOMEUnit>();
		unitList.addAll(units);
		Collections.sort(unitList, unitComparator);
		return new UOMENormalForm(unitList, exponentMap, form.getFactor());
	}
	
	public UOMENormalForm createNormalProduct(UOMENormalForm form1, UOMENormalForm form2) throws CanNotCreateNormalFormException {
		if(!UOMENumberUtil.almostEqual(form1.getOffset(), 0.0) || !UOMENumberUtil.almostEqual(form2.getOffset(), 0.0)) {
			throw new CanNotCreateNormalFormException("Can not multiply two unit expressions if one has an offset.");
		}
		Set<UOMEUnit> units = new HashSet<UOMEUnit>();
		units.addAll(form1.getUnits());
		units.addAll(form2.getUnits());
		units.removeAll(unitsEqualOne);
		HashMap<UOMEUnit, Double> exponentMap = new HashMap<UOMEUnit, Double>();
		Set<UOMEUnit> cancelledUnits = new HashSet<UOMEUnit>();
		for(UOMEUnit unit : units) {
			double exponent = 0.0;
			Double exponent1 = form1.getExponentMap().get(unit);
			if(exponent1 != null) { exponent += exponent1.doubleValue(); }
			Double exponent2 = form2.getExponentMap().get(unit);
			if(exponent2 != null) { exponent += exponent2.doubleValue(); }
			if(exponent != 0.0 && !UOMENumberUtil.almostEqual(exponent, 0.0)) {
				exponentMap.put(unit, new Double(exponent));
			} else {
				cancelledUnits.add(unit);
			}
		}
		units.removeAll(cancelledUnits);
		List<UOMEUnit> unitList = new ArrayList<UOMEUnit>();
		unitList.addAll(units);
		Collections.sort(unitList, unitComparator);
		return new UOMENormalForm(unitList, exponentMap, form1.getFactor()*form2.getFactor());
	}
	
	public UOMENormalForm createNormalQuotient(UOMENormalForm form1, UOMENormalForm form2) throws CanNotCreateNormalFormException {
		if(!UOMENumberUtil.almostEqual(form1.getOffset(), 0.0) || !UOMENumberUtil.almostEqual(form2.getOffset(), 0.0)) {
			throw new CanNotCreateNormalFormException("Can not divide a unit expressions by another if one has an offset.");
		}
		Set<UOMEUnit> units = new HashSet<UOMEUnit>();
		units.addAll(form1.getUnits());
		units.addAll(form2.getUnits());
		units.removeAll(unitsEqualOne);
		HashMap<UOMEUnit, Double> exponentMap = new HashMap<UOMEUnit, Double>();
		Set<UOMEUnit> cancelledUnits = new HashSet<UOMEUnit>();
		for(UOMEUnit unit : units) {
			double exponent = 0.0;
			Double exponent1 = form1.getExponentMap().get(unit);
			if(exponent1 != null) { exponent += exponent1.doubleValue(); }
			Double exponent2 = form2.getExponentMap().get(unit);
			if(exponent2 != null) { exponent -= exponent2.doubleValue(); }
			if(exponent != 0.0 && !UOMENumberUtil.almostEqual(exponent, 0.0)) {
				exponentMap.put(unit, new Double(exponent));
			} else {
				cancelledUnits.add(unit);
			}
		}
		units.removeAll(cancelledUnits);
		List<UOMEUnit> unitList = new ArrayList<UOMEUnit>();
		unitList.addAll(units);
		Collections.sort(unitList, unitComparator);
		return new UOMENormalForm(unitList, exponentMap, form1.getFactor()/form2.getFactor());
	}
	
	public UOMENormalForm createNormalForm(UOMEExpression expression) throws CanNotCreateNormalFormException {
		if(expression instanceof UOMENormalForm) {
			return (UOMENormalForm) expression;
		} else if(expression instanceof UOMEEquivalenzExpression) {
			UOMEEquivalenzExpression equivalenzExpression = (UOMEEquivalenzExpression) expression;
			return createNormalEquivalenz(equivalenzExpression.getUnit());
		} else if(expression instanceof UOMEScalingExpression) {
			UOMEScalingExpression scalingExpression = (UOMEScalingExpression) expression;
			return createNormalScaling(scalingExpression.getUnit(), scalingExpression.getFactor());
		} else if(expression instanceof UOMEOffsetExpression) {
			UOMEOffsetExpression offsetExpression = (UOMEOffsetExpression) expression;
			return createNormalOffset(offsetExpression.getUnit(), offsetExpression.getOffset());
		} else if(expression instanceof UOMEExponentialExpression) {
			UOMEExponentialExpression exponentialExpression = (UOMEExponentialExpression) expression;
			return createNormalExponential(exponentialExpression.getUnit(), exponentialExpression.getExponent());
		} else if(expression instanceof UOMEProductExpression) {
			UOMEProductExpression productExpression = (UOMEProductExpression) expression;
			return createNormalProduct(productExpression.getUnit1(), productExpression.getUnit2());
		} else if(expression instanceof UOMEQuotientExpression) {
			UOMEQuotientExpression quotientExpression = (UOMEQuotientExpression) expression;
			return createNormalProduct(quotientExpression.getUnit1(), quotientExpression.getUnit2());
		}
		throw new CanNotCreateNormalFormException("Do not know how to create normal form of expression of class " 
				+ expression.getClass());
	}

	public UOMENormalForm createNormalSubstitution(UOMESingleUnitExpression singleUnitExpression, UOMENormalForm form) 
	throws CanNotCreateNormalFormException {
		if(singleUnitExpression instanceof UOMEEquivalenzExpression) {
			return form;
		} else if(singleUnitExpression instanceof UOMEScalingExpression) {
			UOMEScalingExpression scalingExpression = (UOMEScalingExpression) singleUnitExpression;
			return createNormalScaling(form, scalingExpression.getFactor());
		} else if(singleUnitExpression instanceof UOMEOffsetExpression) {
			UOMEOffsetExpression offsetExpression = (UOMEOffsetExpression) singleUnitExpression;
			return createNormalOffset(form, offsetExpression.getOffset());
		} else if(singleUnitExpression instanceof UOMEExponentialExpression) {
			UOMEExponentialExpression exponentialExpression = (UOMEExponentialExpression) singleUnitExpression;
			return createNormalExponential(form, exponentialExpression.getExponent());
		}
		throw new CanNotCreateNormalFormException("Do not know how to create normal form of expression of class " 
				+ singleUnitExpression.getClass());		
	}
	
	public UOMENormalForm createNormalSubstitution(UOMEBinaryExpression binaryExpression, UOMENormalForm form1, UOMENormalForm form2) 
	throws CanNotCreateNormalFormException {
		if(binaryExpression instanceof UOMEProductExpression) {
			return createNormalProduct(form1, form2);
		} else if(binaryExpression instanceof UOMEQuotientExpression) {
			return createNormalQuotient(form1, form2);
		}
		throw new CanNotCreateNormalFormException("Do not know how to create normal form of expression of class " 
				+ binaryExpression.getClass());		
	}
	
	
	
}
