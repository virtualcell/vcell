package edu.rpi.graphdrawing;

import java.util.Vector;

public
class Leveller implements Embedder {

protected Blackboard _bb;
protected int _maxlevel;
protected Vector _levels[];

private final int xmarginSize = 10;
protected int _operation = 0;
protected final int _Order = 100;
public Leveller( Blackboard black ) {
	_bb = black;
}
public final void Embed() {
	double L = _bb.globals.L();
	_bb.setArea( 0, 0, L, L );
  if( _operation < _Order ) {
    orderNodes( L, _operation );
  }
  else {
		straightenLayout( L );
	}
  _bb.Update();
  ++_operation;
  _bb.globals.Temp( (double)_operation );
}
	// Implementation of embedder interface, Init and Embed.
	//
public final void Init() {
  // NB: The order here matters, L depends on no. of nodes
  _bb.addDummies();
	double L = _bb.globals.L();
	_bb.setArea( 0, 0, L, L );
	makelevels();
  placeNodes();
	_operation = 0;
}
public final void initialOrderNodes( Node curr ) {
	curr.mark();
	Vector inedges = curr.inedges();
	int edgecnt = inedges.size();
	for (int i = 0; i < edgecnt; ++i ) {
		Node n = ((Edge) inedges.elementAt(i)).from();
		if( n.marked() == false )
			initialOrderNodes( n );
	}
	_levels[ curr.level() ].addElement( curr );
}
protected synchronized final void makelevels() {
	_maxlevel = -1;
	Node maxlevelnode = null;
	Vector nodes = _bb.nodes();
	int nodecnt = nodes.size();
	for (int i = 0; i < nodecnt; ++i ) {             // Find maximum level
		Node n = (Node) nodes.elementAt(i);
    if( _maxlevel < n.level() ) {
      _maxlevel = n.level();
			maxlevelnode = n;
		}
	}
  _levels = new Vector[_maxlevel+1];              // Make and initialize levels
	for( int j = 0; j <= _maxlevel; ++j ) {
		_levels[j] = new Vector();
	}
	_bb.unmarkNodes();
	initialOrderNodes( maxlevelnode );              // DFS order most the nodes
	for (int k = 0; k < nodecnt; ++k ) {             // Find maximum level
		Node n = (Node) nodes.elementAt(k);
		if( !n.marked() )
			initialOrderNodes( n );
	}
}
protected final void orderLevel( Vector nodes,
																 double L, double y, 
																 boolean doin, boolean doout ) {
	int levelcnt = nodes.size();
	for( int j = 0; j < levelcnt; ++j ) {
		Node curr = (Node) nodes.elementAt( j );
		curr.barycenter( curr.computeBarycenter(doin, doout) );
	}
	sortLevel( nodes );
	placeLevel( L, y, levelcnt, nodes );
}
	// Do downwards barycentering on first pass, upwards on second, then average
protected synchronized final void orderNodes( double L, int op ) {
	boolean doup = ((op & 0x1) == 1);
	boolean doin = (op > 5 || !doup);
	boolean doout = (op > 5 || doup);
  double ystep = (_maxlevel>0) ? (L/_maxlevel) : 0.0;
	if( doup ) {
		double y = 0.0;
		for( int i = 0; i <= _maxlevel; ++i ) {         // Going upwards
			Vector nodes = _levels[i];
			orderLevel( nodes, L, y, doin, doout );
			y += ystep;
		}
	}
	else {
		double y = L;
		for( int i = _maxlevel; i >= 0; --i ) {         // Going downwards
			Vector nodes = _levels[i];
			orderLevel( nodes, L, y, doin, doout );
			y -= ystep;
		}
	}
}
protected final void placeLevel( double L, double y, 
																 int levelcnt, Vector nodes ) {
	double xstep = L/(levelcnt+1);
	for( int i = 0; i < levelcnt; ++i ) {
		Node n = (Node) nodes.elementAt( i );
    n.x( xstep*(i+1) );
    n.y( y );
	}  
}
protected final void placeNodes() {
	double L = _bb.globals.L();
  double ystep = L/(_maxlevel+1);
	double y = 0.0;
	for( int i = 0; i <= _maxlevel; ++i ) {
		Vector nodes = _levels[i];
		placeLevel( L, y, nodes.size(), nodes );
		y += ystep;
	}
}
protected final void sortLevel( Vector nodes ) {
  // Do insertionsort on the level based on the barycenters, then reorder
	int len = nodes.size();
  for( int P = 1; P < len; ++P ) {
    Node tmp = (Node) nodes.elementAt( P );
		double barycent = tmp.barycenter();
    int j;
    for( j = P; j > 0; --j ) {
      Node tmp2 = (Node) nodes.elementAt( j-1 );
      if( barycent >= tmp2.barycenter() ) break;
      nodes.setElementAt( tmp2, j );
		}
		nodes.setElementAt( tmp, j );
	}
}
protected final void straightenDummy( Node n ) {
	Node from = ((Edge) n.inedges().firstElement()).from();
	Node to = ((Edge) n.inedges().firstElement()).to();
	double avg = (n.x() + from.x() + to.x()) / 3;
	n.x( avg );
}
protected synchronized final void straightenLayout( double L ) {
  double ystep = L/(_maxlevel+1);
	double y = 0.0;
	for( int i = 0; i <= _maxlevel; ++i ) {
		Vector nodes = _levels[i];
		int levelcnt = nodes.size();
		for( int j = 0; j < levelcnt; ++j ) {
			Node n = (Node) nodes.elementAt( j );
			if( n.dummy() )
				straightenDummy( n );
		}
		for( int j = 1; j < levelcnt; ++j ) {
			Node n = (Node) nodes.elementAt( j );
			Node prev = (Node) nodes.elementAt( j-1 );
			double prevright = prev.x() + prev.boundingWidth()/2 + xmarginSize;
			double thisleft =  n.x()    - n.boundingWidth()/2    - xmarginSize;
			double overlap = prevright - thisleft;
			if( overlap > 0 ) {
				prev.x( prev.x() - overlap/2 );
				n.x( n.x() + overlap/2 );
			}
			n.y( y ); 
		}
		y += ystep;
	}
}
} // class Leveller
