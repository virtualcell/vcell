/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GraphModelShapeHierarchyPrinter {
	
	public static void showShapeHierarchyBottomUp(GraphModel graphModel) {
		System.out.println("<<<<<<<<<Shape Hierarchy Bottom Up>>>>>>>>>");
		List<Shape> shapes = new ArrayList<Shape>(graphModel.getShapes());
		// gather top(s) ... should only have one
		List<Shape> topList = new ArrayList<Shape>();
		for (int i = 0; i < shapes.size(); i++) {
			if (shapes.get(i).getParent() == null) {
				topList.add(shapes.get(i));
			}
		}
		// for each top, print tree
		Stack<Shape> stack = new Stack<Shape>();
		for (Shape top : topList) {
			System.out.println(top.toString());
			stack.push(top);
			shapes.remove(top);
			while (true) {
				// find first remaining children of current parent and print
				boolean bChildFound = false;
				for (int i = 0; i < shapes.size() && stack.size() > 0; i++) {
					Shape shape = shapes.get(i);
					if (shape.getParent() == stack.peek()) {
						char padding[] = new char[4 * stack.size()];
						for (int k = 0; k < padding.length; k++)
							padding[k] = ' ';
						String pad = new String(padding);
						System.out.println(pad + shape.toString());
						stack.push(shape);
						shapes.remove(shape);
						bChildFound = true;
						break;
					}
				}
				if (stack.size() == 0) {
					break;
				}
				if (bChildFound == false) {
					stack.pop();
				}
			}
		}
		if (shapes.size() > 0) {
			System.out.println(".......shapes left over:");
			for (int i = 0; i < shapes.size(); i++) {
				System.out.println((shapes.get(i)).toString());
			}
		}

	}

	public static void showShapeHierarchyTopDown(GraphModel graphModel) {
		System.out.println("<<<<<<<<<Shape Hierarchy Top Down>>>>>>>>>");
		List<Shape> shapes = new ArrayList<Shape>(graphModel.getShapes());
		// gather top(s) ... should only have one
		List<Shape> topList = new ArrayList<Shape>();
		for (int i = 0; i < shapes.size(); i++) {
			if (shapes.get(i).getParent() == null) {
				topList.add(shapes.get(i));
			}
		}
		// for each top, print tree
		Stack<Shape> stack = new Stack<Shape>();
		for (int j = 0; j < topList.size(); j++) {
			Shape top = topList.get(j);
			System.out.println(top.toString());
			stack.push(top);
			shapes.remove(top);
			while (stack.size() > 0) {
				// find first remaining children of current parent and print
				boolean bChildFound = false;
				Shape currShape = stack.peek();
				for (Shape shape : currShape.getChildren()) {
					if (!shapes.contains(shape))
						continue;
					char padding[] = new char[4 * stack.size()];
					for (int k = 0; k < padding.length; k++)
						padding[k] = ' ';
					String pad = new String(padding);
					System.out.println(pad + shape.toString());
					stack.push(shape);
					shapes.remove(shape);
					bChildFound = true;
					break;
				}
				if (bChildFound == false) {
					stack.pop();
				}
			}
		}
		if (shapes.size() > 0) {
			System.out.println(".......shapes left over:");
			for (int i = 0; i < shapes.size(); i++) {
				System.out.println((shapes.get(i)).toString());
			}
		}
	}
}
