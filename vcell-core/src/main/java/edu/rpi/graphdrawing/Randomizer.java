package edu.rpi.graphdrawing;

import java.util.Vector;

public
class Randomizer implements Embedder {

protected Blackboard _bb;
protected boolean _updated = false;
public Randomizer( Blackboard black ) {
	_bb = black;
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
	double L = _bb.globals.L();
	_bb.setArea( -L/2, -L/2, L/2, L/2 );
  _bb.removeDummies();
	randomize();
}
protected final void randomize() {
	double X = _bb.ux()-_bb.lx();
	double Y = _bb.uy()-_bb.ly();
	Vector nodes = _bb.nodes();
	int nodecnt = nodes.size();
	for (int i = 0; i < nodecnt; ++i ) {
		Node n = (Node) nodes.elementAt(i);
		n.randomPlacement( X, Y, _bb.globals.depth3D() );
	}
}
} // class Randomizer
