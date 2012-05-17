package edu.northwestern.at.utils.math;

import java.math.BigDecimal;
import java.math.BigInteger;

/*	Please see the license information at the end of this file. */

/**	Perform arithmetic on java.lang.Number objects.
 */

public class NumberOps
{
	/**	Integer zero as a Number.  Used for comparisons.
	 */

	protected final static Number intZero	= new Integer( 0 );

	/**	Compare two numbers.
	 *
	 *	@param	number1		First number.
	 *	@param	number2		Second number.
	 *
	 *	@return				-1 if number1 < number2
	 *						0 if number1 == number2
	 *						1 if number1 > number2
	 */

	public static int compareNumbers( Number number1 , Number number2 )
	{
		if ( ( number1 instanceof Integer ) && ( number2 instanceof Integer ) )
		{
			int i1	= number1.intValue();
			int i2	= number2.intValue();

			if ( i1 < i2 )
			{
				return -1;
			}
			else if ( i1 > i2 )
			{
				return +1;
			}
			else
			{
				return 0;
			}
		}
		else
		{
			double d1	= number1.doubleValue();
			double d2	= number2.doubleValue();

			if ( d1 < d2 )
			{
				return -1;
			}
			else if ( d1 > d2 )
			{
				return +1;
			}
			else
			{
				return 0;
			}
		}
	}

	/**	Compare a number to zero.
	 *
	 *	@param	number		Number to compare to zero.
	 *
	 *	@return				-1 if number < 0
	 *						0 if number1 == 0
	 *						1 if number1 > 0
	 */

	public static int compareToZero( Number number )
	{
		return compareNumbers( number , intZero );
	}

	/**	Add two numbers.
	 *
	 *	@param	number1		First number.
	 *	@param	number2		Second number.
	 *
	 *	@return				Sum of number1 and number2.
	 *
	 *	<p>
	 *	If both number1 and number2 are Integers, the result is an
	 *	Integer.  Any other combination of Number types results
	 *	in a Double.
	 *	</p>
	 */

	public static Number add( Number number1 , Number number2 )
	{
		if	(	( number1 instanceof Integer ) &&
				( number2 instanceof Integer ) )
		{
			return new Integer
			(
				((Integer)number1).intValue() +
				((Integer)number2).intValue()
			);
		}
		else
		{
			return new Double
			(
				number1.doubleValue() + number2.doubleValue()
			);
		}
	}

	/**	Substract two numbers.
	 *
	 *	@param	number1		First number.
	 *	@param	number2		Second number.
	 *
	 *	@return				number1 - number2 .
	 *
	 *	<p>
	 *	If both number1 and number2 are Integers, the result is an
	 *	Integer.  Any other combination of Number types results
	 *	in a Double.
	 *	</p>
	 */

	public static Number subtract( Number number1 , Number number2 )
	{
		if	(	( number1 instanceof Integer ) &&
				( number2 instanceof Integer ) )
		{
			return new Integer
			(
				((Integer)number1).intValue() -
				((Integer)number2).intValue()
			);
		}
		else
		{
			return new Double
			(
				number1.doubleValue() - number2.doubleValue()
			);
		}
	}

	/**	Multiply two numbers.
	 *
	 *	@param	number1		First number.
	 *	@param	number2		Second number.
	 *
	 *	@return				number1 * number2 .
	 *
	 *	<p>
	 *	If both number1 and number2 are Integers, the result is an
	 *	Integer.  Any other combination of Number types results
	 *	in a Double.
	 *	</p>
	 */

	public static Number multiply( Number number1 , Number number2 )
	{
		if	(	( number1 instanceof Integer ) &&
				( number2 instanceof Integer ) )
		{
			return new Integer
			(
				((Integer)number1).intValue() *
				((Integer)number2).intValue()
			);
		}
		else
		{
			return new Double
			(
				number1.doubleValue() * number2.doubleValue()
			);
		}
	}

	/**	Divide two numbers.
	 *
	 *	@param	number1		First number.
	 *	@param	number2		Second number.
	 *
	 *	@return				number1 / number2 .
	 *
	 *	<p>
	 *	If both number1 and number2 are Integers, the result is an
	 *	Integer.  Any other combination of Number types results
	 *	in a Double.  Zerodivides are passed through as NAN.
	 *	</p>
	 */

	public static Number divide( Number number1 , Number number2 )
	{
		if	(	( number1 instanceof Integer ) &&
				( number2 instanceof Integer ) )
		{
			return new Integer
			(
				((Integer)number1).intValue() /
				((Integer)number2).intValue()
			);
		}
		else
		{
			return new Double
			(
				number1.doubleValue() / number2.doubleValue()
			);
		}
	}

	/**	Find modulus of two numbers.
	 *
	 *	@param	number1		First number.
	 *	@param	number2		Second number.
	 *
	 *	@return				number1 modulo number2 as a long.
	 *
	 *	<p>
	 *	The numbers are converted to Longs before performing
	 *	the operation.  The result is a Long.  This gives the
	 *	expected results if the numbers are both integral.
	 *	</p>
	 */

	public static Number modulus( Number number1 , Number number2 )
	{
		return new Long( number1.longValue() % number2.longValue() );
	}

	/**	Convert string to Number.
	 *
	 *	@param	s	String to convert to Number.
	 *
	 *	@return		String converted to a Number.
	 *
	 *	@throws		NumberFormatException if "s" does not contain
	 *				a valid number.
	 *
	 *	<p>
	 *	If the string contains an integer, the resulting Number is
	 *	an Integer.  Anything other valid numeric string is converted
	 *	to a Double.  An invalid numeric string throws a
	 *	NumberFormatException.
	 *	</p>
	 */

	public static Number toNumber( String s )
		throws NumberFormatException
	{
		Number result;

								//	Try to convert to integer first.
		try
		{
			result	= new Integer( s );
		}
		catch ( NumberFormatException e )
		{
								//	Can't convert to integer -- try double.
								//	If this fails, a NumberFormatException
								//	will be thrown.

			result	= new Double( s );
		}

		return result;
	}

	/**	Get copy of a Number object.
	 *
	 *	@param	number	The number object for which a copy is desired.
	 *
	 *	@return			A copy of the specified Number object.
	 *
	 *	<p>
	 *	This method exists because Java does not implement a proper clone
	 *	method for the Number class.
	 *	</p>
	 */

	 public static Number cloneNumber( Number number )
	 {
	 	Number result	= null;

	 	if ( number != null )
	 	{
		 	if ( number instanceof Integer )
		 	{
		 		result	= new Integer( number.intValue() );
	 		}
		 	else if ( number instanceof Double )
		 	{
		 		result	= new Double( number.doubleValue() );
	 		}
		 	else if ( number instanceof Long )
		 	{
		 		result	= new Long( number.longValue() );
	 		}
		 	else if ( number instanceof Float )
		 	{
		 		result	= new Float( number.floatValue() );
	 		}
		 	else if ( number instanceof Byte )
		 	{
		 		result	= new Byte( number.byteValue() );
	 		}
		 	else if ( number instanceof Short )
		 	{
		 		result	= new Short( number.shortValue() );
	 		}
		 	else if ( number instanceof BigDecimal )
		 	{
		 		BigDecimal bigDec	= (BigDecimal)number;

		 		result				=
		 			new BigDecimal(
		 				bigDec.unscaledValue() ,
		 				bigDec.scale() );
	 		}
		 	else if ( number instanceof BigInteger )
		 	{
		 		BigInteger bigInt	= (BigInteger)number;

		 		byte[] bytes		= bigInt.toByteArray();

		 		result				= new BigInteger( bytes );
	 		}
	 	}

	 	return result;
	 }
}

/*
Copyright (c) 2008, 2009 by Northwestern University.
All rights reserved.

Developed by:
   Academic and Research Technologies
   Northwestern University
   http://www.it.northwestern.edu/about/departments/at/

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal with the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimers.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimers in the documentation and/or other materials provided
      with the distribution.

    * Neither the names of Academic and Research Technologies,
      Northwestern University, nor the names of its contributors may be
      used to endorse or promote products derived from this Software
      without specific prior written permission.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE CONTRIBUTORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
*/


