package edu.rpi.graphdrawing;

import java.util.*;

public
class Stabilizer implements Embedder {

protected Blackboard _bb;
public Stabilizer( Blackboard black ) {
	_bb = black;
}
public final void Embed() {
}
	// Implementation of embedder interface, Init and Embed.
	//
public final void Init() {
	Vector nodes = _bb.nodes();
	int nodecnt = nodes.size();
	for (int i = 0; i < nodecnt; ++i ) {
		Node n = (Node) nodes.elementAt(i);
		n.stabilize();
	}
}
} // class Randomizer
