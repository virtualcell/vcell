svn://code3.cam.uchc.edu/VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units
last changed svn 1730


r1730 | schaff | 2007-09-28 16:07:58 CDT
Changed paths:
	M /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/RationalNumber.java

Apply bug fix for RationalNumber sign error
Start Process of separately handling electrical and reaction mechanisms.
----------------------------------------------------------------------------
r1287 | schaff | 2007-03-15 12:05:58 CDT
Changed paths:
	M /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/SI.java

fixes SI.baseUnitDB() METER to METRE (was cut&paste error from previous change) now ucar.SI is consistent with ThirdParty 1.51
----------------------------------------------------------------------------
r1045 | schaff | 2007-01-12 20:09:03 CST
Changed paths:
	M /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/SI.java

baseline after VAJ reconciliation
----------------------------------------------------------------------------
r995 | schaff | 2006-10-24 08:48:36 CDT
Changed paths:
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/SupplementaryBaseQuantity.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitDBManager.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/BadUnitException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitDB.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/PrefixName.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/META-INF
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/MultiplyException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitSystemManager.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/DivideException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnknownBaseQuantity.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitSystem.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitDBException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/PrefixDBManager.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/StandardUnitFormatConstants.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/PrefixDBImpl.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/Token.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/ConversionException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/PrefixDBException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/.classpath
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitDBAccessException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitName.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/META-INF/MANIFEST.MF
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/Prefix.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitClassException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnknownUnit.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitImpl.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitSystemException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/SpecificationException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitSystemImpl.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/StandardUnitFormatTokenManager.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/RationalNumber.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitDBImpl.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/NoSuchUnitException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/ASCII_CharStream.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/QuantityExistsException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/RegularBaseQuantity.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitFormatException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/test/TimeTest.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/BaseQuantityException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/StandardPrefixDB.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/Unit.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitExistsException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/.settings
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/TokenMgrError.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/ScaledUnit.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/TimeScaleUnit.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitFormatImpl.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/Base.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/Test.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/DerivedUnit.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/PrefixSymbol.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitDimension.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitSymbol.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/Factor.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/SI.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/BaseUnit.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/bin
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/test
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/build.properties
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitFormat.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/Converter.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/.settings/org.eclipse.pde.core.prefs
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/Dimension.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/ParseException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/StandardUnitDB.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/NameException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/PrefixDBAccessException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitFormatManager.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/OffsetUnit.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/OperationException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/BaseQuantity.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/DerivedUnitImpl.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/DerivableUnit.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/PrefixExistsException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitParseException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/.project
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/ConverterImpl.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/PrefixDB.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/QuantityDimension.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/StandardUnitFormat.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/RaiseException.java
	A /VCell5/trunk/ThirdParty_UCAR_UNITS/src/ucar/units/UnitID.java

break apart thirdParty
----------------------------------------------------------------------------