/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

import org.jetbrains.annotations.NotNull;

/**
 * This is an immutable class for storing points in 3-space.
 */
public record Coordinate (double x, double y, double z) implements java.io.Serializable, org.vcell.util.Matchable {
	public enum Axis {
		X,
		Y,
		Z;

		public String getNormalPlaneName(){
			if (this == X) return "YZ";
			if (this == Y) return "XZ";
			if (this == Z) return "XY";
			throw new RuntimeException("this should be unreachable!");
		}

		public static Axis fromNormalPlaneName(String normalPlaneName){
			return switch (normalPlaneName){
				case "YZ" -> X;
				case "XZ" -> Y;
				case "XY" -> Z;
				default -> throw new IllegalArgumentException("invalid normalPlaneName: " + normalPlaneName);
			};
		}

		public static Axis fromAxisChar(String axisChar){
			if (null == axisChar) throw new IllegalArgumentException("invalid axisChar; cannot be null!");
			if (axisChar.length() != 1) throw new IllegalArgumentException("invalid axisChar: " + axisChar + "; must have one of: X,Y,Z");
			return Coordinate.Axis.fromAxisChar(axisChar.charAt(0));
		}

		public static Axis fromAxisChar(Character axisChar){
			if (null == axisChar) throw new IllegalArgumentException("invalid axisChar; cannot be null!");
			char lowerCase = axisChar.toString().toLowerCase().charAt(0);
			return switch (lowerCase){
				case 'x' -> X;
				case 'y' -> Y;
				case 'z' -> Z;
				default -> throw new IllegalArgumentException("invalid axisChar: " + axisChar + "; must have one of: X,Y,Z");
			};
		}
	}
	//Plane name represents which is horizontal(1st letter) and vertical(2nd letter) axis when looking down the normal axis

	public Coordinate(Coordinate toCopy){
		this(toCopy.x, toCopy.y, toCopy.z);
	}

	private static double axisConversion(double origX, double origY, double origZ, Axis axisElement, Axis normalAxis, boolean bToStandardXYZ) {
		return switch (normalAxis) {
			case X -> switch (axisElement) {
				case X -> bToStandardXYZ ? origZ : origY;
				case Y -> bToStandardXYZ ? origX : origZ;
				case Z -> bToStandardXYZ ? origY : origX;
			};
			case Y -> switch (axisElement) {
				case X -> origX;
				case Y -> origZ;
				case Z -> origY;
			};
			case Z -> switch (axisElement) {
				case X -> origX;
				case Y -> origY;
				case Z -> origZ;
			};
		};
	}

	public boolean compareEqual(Matchable obj) {
		return this.equals(obj);
	}

	public static Coordinate convertAxisFromStandardXYZToNormal(double origX, double origY, double origZ, Axis normalAxis) {
		return new Coordinate(
				Coordinate.axisConversion(origX, origY, origZ, Axis.X, normalAxis, false),
				Coordinate.axisConversion(origX, origY, origZ, Axis.Y, normalAxis, false),
				Coordinate.axisConversion(origX, origY, origZ, Axis.Z, normalAxis, false)
		);
	}

	public static ISize convertAxisFromStandardXYZToNormal(ISize isize, Axis normalAxis) {
		return new ISize(
				(int) Coordinate.axisConversion(isize.getX(), isize.getY(), isize.getZ(), Axis.X, normalAxis, false),
				(int) Coordinate.axisConversion(isize.getX(), isize.getY(), isize.getZ(), Axis.Y, normalAxis, false),
				(int) Coordinate.axisConversion(isize.getX(), isize.getY(), isize.getZ(), Axis.Z, normalAxis, false));
	}

	public static Extent convertAxisFromStandardXYZToNormal(Extent extent, Axis normalAxis) {
		return new Extent(
				(int) Coordinate.axisConversion(extent.getX(), extent.getY(), extent.getZ(), Axis.X, normalAxis, false),
				(int) Coordinate.axisConversion(extent.getX(), extent.getY(), extent.getZ(), Axis.Y, normalAxis, false),
				(int) Coordinate.axisConversion(extent.getX(), extent.getY(), extent.getZ(), Axis.Z, normalAxis, false));
	}

	public static double convertAxisFromStandardXYZToNormal(double origX, double origY, double origZ, Axis axisElement, Axis normalAxis) {
		return Coordinate.axisConversion(origX, origY, origZ, axisElement, normalAxis, false);
	}

	public static double convertAxisFromStandardXYZToNormal(Coordinate coord, Axis axisElement, Axis normalAxis) {
		return Coordinate.axisConversion(coord.x(), coord.y(), coord.z(), axisElement, normalAxis, false);
	}

	public static Coordinate convertCoordinateFromNormalToStandardXYZ(double origX, double origY, double origZ, Axis normalAxis) {
		return new Coordinate(
				Coordinate.axisConversion(origX, origY, origZ, Axis.X, normalAxis, true),
				Coordinate.axisConversion(origX, origY, origZ, Axis.Y, normalAxis, true),
				Coordinate.axisConversion(origX, origY, origZ, Axis.Z, normalAxis, true));
	}

	public static void convertCoordinateIndexFromNormalToStandardXYZ(org.vcell.util.CoordinateIndex coordIndex, Axis normalAxis) {
		//Re-uses CoordinateIndex
		double origX = coordIndex.x;
		double origY = coordIndex.y;
		double origZ = coordIndex.z;
		coordIndex.x = (int) axisConversion(origX, origY, origZ, Axis.X, normalAxis, true);
		coordIndex.y = (int) axisConversion(origX, origY, origZ, Axis.Y, normalAxis, true);
		coordIndex.z = (int) axisConversion(origX, origY, origZ, Axis.Z, normalAxis, true);
	}

	public double distanceTo(double otherX, double otherY, double otherZ) {
		otherX -= this.x;
		otherY -= this.y;
		otherZ -= this.z;
		return Math.sqrt(otherX * otherX + otherY * otherY + otherZ * otherZ);
	}

	public double distanceTo(Coordinate coord) {
		return this.distanceTo(coord.x, coord.y, coord.z);
	}

	public static java.awt.geom.Point2D.Double get2DProjection(Coordinate coord, Axis normalAxis) {
		double newX = Coordinate.convertAxisFromStandardXYZToNormal(coord, Coordinate.Axis.X, normalAxis);
		double newY = Coordinate.convertAxisFromStandardXYZToNormal(coord, Coordinate.Axis.Y, normalAxis);
		return new java.awt.geom.Point2D.Double(newX, newY);
	}

	public double getAxisElement(Axis axis) {
		return switch (axis) {
			case X -> this.x;
			case Y -> this.y;
			case Z -> this.z;
		};
	}

	/// WARNING: This hash-code appears to have order-insensitive collisions; it also doesn't follow the "equals" contract
//	public int hashCode() {
//		long bits = Double.doubleToLongBits(this.x + this.y + this.z);
//		return (int) (bits ^ (bits >> 32));
//	}

	public static boolean isCoordinateInBounds(Coordinate coord, Origin origin, Extent extent, Coordinate deltaCoord) {
		Coordinate originCoordinate = new Coordinate(origin.getX(), origin.getY(), origin.getZ());
		Coordinate extentCoordinate = new Coordinate(origin.getX() + extent.getX(), origin.getY() + extent.getY(), origin.getZ() + extent.getZ());

		double newX = coord.x() + deltaCoord.x();
		if ((originCoordinate.x() - newX) > .000001) return false;
		if ((newX - extentCoordinate.x()) > .000001) return false;

		double newY = coord.x() + deltaCoord.x();
		if ((originCoordinate.x() - newY) > .000001) return false;
		if ((newY - extentCoordinate.x()) > .000001) return false;

		double newZ = coord.x() + deltaCoord.x();
		if ((originCoordinate.x() - newZ) > .000001) return false;
		return !((newZ - extentCoordinate.x()) > .000001);
	}

	@NotNull
	public String toString() {
		return String.format("X=%f Y=%f Z=%f", this.x, this.y, this.z);
	}

	public static double coordComponentFromSinglePlanePolicy(Origin origin, Extent extent, Axis axis) {
		if (Axis.X.equals(axis)) return extent.getX() / 2.0 + origin.getX();
		if (Axis.Y.equals(axis)) return extent.getY() / 2.0 + origin.getY();
		if (Axis.Z.equals(axis)) return extent.getZ() / 2.0 + origin.getZ();
		throw new IllegalArgumentException("Unknown Axis =" + axis);
	}
}
