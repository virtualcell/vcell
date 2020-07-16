package jscl.engine;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.NotFunctionException;
import jscl.math.Function;

public class Graph implements jscl.editor.rendering.Plot {
	private final Function f;

	private Graph(final Function f) {
		this.f = f;
	}

	public double apply(final double value) {
		return f.apply(value);
	}

	public static Object apply(Generic expr) {
		if (expr instanceof Function) {
			return new Graph((Function) expr);
		} else if (expr instanceof JSCLVector) {
			Generic a[] = ((JSCLVector) expr).elements();
			Graph s[] = new Graph[a.length];
			for(int i=0;i<a.length;i++) {
				if (a[i] instanceof Function) {
					s[i] = new Graph((Function) a[i]);
				} else throw new NotFunctionException();
			}
			return s;
		} else throw new NotFunctionException();
	}
}
