/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;

class TIFFDataType 
{
   final static int TIFF_NOTYPE	= 0;	 /* placeholder */
   final static int TIFF_BYTE	= 1;	 /* 8-bit unsigned integer */
   final static int TIFF_ASCII	= 2;	 /* 8-bit bytes w/ last byte null */
   final static int TIFF_SHORT	= 3;	 /* 16-bit unsigned integer */
   final static int TIFF_LONG	= 4;	 /* 32-bit unsigned integer */
   final static int TIFF_RATIONAL = 5;	 /* 64-bit unsigned fraction */
   final static int TIFF_SBYTE	= 6;	 /* !8-bit signed integer */
   final static int TIFF_UNDEFINED = 7;	 /* !8-bit untyped data */
   final static int TIFF_SSHORT	= 8;	 /* !16-bit signed integer */
   final static int TIFF_SLONG	= 9;	 /* !32-bit signed integer */
   final static int TIFF_SRATIONAL = 10; /* !64-bit signed fraction */
   final static int TIFF_FLOAT	= 11;	 /* !32-bit IEEE floating point */
   final static int TIFF_DOUBLE	= 12;	 /* !64-bit IEEE floating point */

   private int type;

   TIFFDataType(int type){
      this.type = type;
   }
   int getType(){
      return type;
   } 
   public String toString()
   {
      switch(type){
	  case TIFF_NOTYPE:
	     return "notype";
	  case TIFF_BYTE:
	     return "byte";
	  case TIFF_ASCII:
	     return "ascii";
	  case TIFF_SHORT:
	     return "unsigned short";
	  case TIFF_LONG:
	     return "unsigned long";
	  case TIFF_RATIONAL:
	     return "rational";
	  case TIFF_SBYTE:
	     return "signed byte";
	  case TIFF_UNDEFINED:
	     return "undefined";
	  case TIFF_SSHORT:
	     return "signed short";
	  case TIFF_SLONG:
	     return "signed long";
	  case TIFF_SRATIONAL:
	     return "signed rational";
	  case TIFF_FLOAT:
	     return "float";
	  case TIFF_DOUBLE:
	     return "double";
	  default:
	     return "unknown data type...";
      }
   }
}
