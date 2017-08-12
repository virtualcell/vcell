package edu.rpi.graphdrawing;

import java.util.Vector;

public
class Circularizer implements Embedder {

protected Blackboard _bb;
protected boolean _updated = false;
public Circularizer( Blackboard black ) {
	_bb = black;
}
protected final void circularize() {
	double rX = (_bb.ux()-_bb.lx())/2;
	double rY = (_bb.uy()-_bb.ly())/2;
	double theta = 0;
	double delta = 2*Math.PI / _bb.nodes().size();
	Vector nodes = _bb.nodes();
	int nodecnt = nodes.size();
	for (int i = 0; i < nodecnt; ++i ) {
		Node n = (Node) nodes.elementAt(i);
		n.randomPlacement( 0, 0, _bb.globals.depth3D() );
		n.XY( rX*Math.cos(theta), rY*Math.sin(theta) );
		theta += delta;
	}
}
public final void Embed() {
	if( !_updated ) {
		_bb.Update();
		_updated = true;
	}
}
	// Implementation of embedder interface, Init and Embed.
	//
public final void Init() {
  _bb.removeDummies();
	double L = _bb.globals.L();
	_bb.setArea( -L/2, -L/2, L/2, L/2 );
	circularize();
}
} // class Circularizer
