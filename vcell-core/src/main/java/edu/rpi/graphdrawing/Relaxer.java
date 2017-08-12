package edu.rpi.graphdrawing;

import java.util.Vector;


public
class Relaxer implements Embedder {

protected Blackboard _bb;
public Relaxer( Blackboard black ) {
	_bb = black;
}
public final void Embed() {
	relaxer();
}
	// Implementation of embedder interface, Init and Embed.
	//
public final void Init() {
  _bb.removeDummies();
	double L = _bb.globals.L();
	_bb.setArea( -L/2, -L/2, L/2, L/2 );
}
protected synchronized void relaxer() {
	double k = _bb.globals.k();
	Vector edges = _bb.edges();
	int edgecnt = edges.size();
	for (int i = 0; i < edgecnt; ++i ) {
		Edge e = (Edge) edges.elementAt(i);
		Node to = e.to();
		Node from = e.from();
		if( to == from ) 
			continue;
		double distance = e.currLength();
		if( distance == 0.0 ) distance = 0.0001;
		double force = (e.length() - distance)/(3*distance);
		to.forced( from, force );
		from.forced( to, force );
	}
	Vector nodes = _bb.nodes();
	int nodecnt = nodes.size();
	for (int i = 0; i < nodecnt; ++i ) {
		Node u = (Node) nodes.elementAt(i);
		PointDelta olddelta = u.getDelta();
		u.stabilize();
		for (int j = 0; j < nodecnt; ++j ) {
			Node v = (Node) nodes.elementAt(j);
			double distance = _bb.Norm( u, v );
			if( distance > 3*k ) 
				continue;
			if( distance == 0.0 ) distance = 0.0001;
			u.forced( v, 1.0/distance );
		}
		double force = u.deltaForce();
		if( force > 0.0 )
			u.scaleDelta( 2.0/force );
		u.addDelta( olddelta );
	}
	for (int i = 0; i < nodecnt; ++i ) {
		Node n = (Node) nodes.elementAt(i);
		if( !n.fixed() && !n.picked() ) {
			n.boundedMoveDelta( 5.0 );
		}
		n.scaleDelta( 0.5 );
	}
}
} // class Relaxer
