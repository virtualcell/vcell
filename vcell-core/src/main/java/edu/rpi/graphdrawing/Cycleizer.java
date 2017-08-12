package edu.rpi.graphdrawing;

import java.util.Vector;

public
class Cycleizer implements Embedder {

protected Blackboard _bb;
Vector _visitedNodes;
Vector _theCycle;
Vector _theRest;

	// Implementation of embedder interface, Init and Embed.
	//
protected int _theCycleLength;
protected boolean _updated = false;
public Cycleizer( Blackboard black ) {
	_bb = black;
}
protected final void circularize() {
  if( _theCycle == null ) return;
	double rX = (_bb.ux()-_bb.lx())/2;
	double rY = (_bb.uy()-_bb.ly())/2;
	double theta = 0;
	Vector nodes = _theCycle;
	int nodecnt = nodes.size();
	double delta = 2*Math.PI / nodecnt;
	for (int i = 0; i < nodecnt; ++i ) {
		Node n = (Node) nodes.elementAt(i);
		n.XY( rX*Math.cos(theta), rY*Math.sin(theta) );
		theta += delta;
	}
}
protected synchronized final void cycleize() {
	Vector nodes = _bb.nodes();
	int nodecnt = nodes.size();
	for (int i = 0; i < nodecnt; ++i ) {
		Node n = (Node) nodes.elementAt(i);
    if( !n.fixed() )
      n.XY( 0.0, 0.0 );
	}
  get_fixed();
  if( _theCycle == null ) {
    get_cycle();
    circularize();
  }
  if( _theCycle != null ) {
    get_rest();
    layout_rest();
  }
}
public final void Embed() {
	if( !_updated ) {
		_bb.Update();
		_updated = true;
	}
  int cycleLength = _bb.globals.cycleLength();
  if( cycleLength != _theCycleLength ) {
    double L = _bb.globals.L();
    _bb.setArea( -L/2, -L/2, L/2, L/2 );
    _theCycleLength = cycleLength;
    cycleize();
  }
}
protected final void find_cycle( Node curr, int level ) {
  _visitedNodes.addElement( curr );
  curr.mark();
  curr.pick( true );
  curr.level( level );
  Vector outedges = curr.outedges();
  int outedgecnt = outedges.size();
  for (int i = 0; i < outedgecnt; ++i ) {
    Node n = ((Edge) outedges.elementAt(i)).to();
    if( n.picked() )       save_new_cycle( n, curr );
    else if( !n.marked() ) find_cycle( n, level+1 );
  }
  Vector inedges = curr.inedges();
  int inedgecnt = inedges.size();
  for (int i = 0; i < inedgecnt; ++i ) {
    Edge e = (Edge) inedges.elementAt(i);
    if( !e.reversed() ) continue;
    Node n = e.from();
    if( n.picked() )       save_new_cycle( n, curr );
    else if( !n.marked() ) find_cycle( n, level+1 );
  }
  curr.pick( false );
  _visitedNodes.removeElement( curr );
}
protected final void get_cycle() {
  _theCycle = null;
  _bb.unmarkNodes();
  Vector nodes = _bb.nodes();
  int nodecnt = nodes.size();
  _visitedNodes = new Vector();
  for (int i = 0; i < nodecnt; ++i ) {
    Node n = (Node) nodes.elementAt(i);
    if( !n.marked() ) find_cycle( n, 0 );      
    if( _theCycle != null ) break;
  }
}
protected final void get_fixed() {
  _theCycle = _bb.get_fixed();
  if( _theCycle.size() == 0 )
    _theCycle = null;
}
protected final void get_rest() {
  if( _theCycle == null )
    _theRest = (Vector) _bb.nodes().clone();
  else {
    _theRest = new Vector();
    Vector nodes = _bb.nodes();
    int nodecnt = nodes.size();
    int pos = 0;
    for (int i = 0; i < nodecnt; ++i ) {
      Node n = (Node) nodes.elementAt(i);
      if( !_theCycle.contains( n ) ) {
        n.level( pos );                      // We use the level for pos. info
        _theRest.addElement( n );
        ++pos;
      }
      else
        n.level( -1 );                     // And it's -1 if node is in cycle
    }
  }
}
public final void Init() {
  _bb.removeDummies();
	double L = _bb.globals.L();
	_bb.setArea( -L/2, -L/2, L/2, L/2 );
  _theCycleLength = _bb.globals.cycleLength();
	cycleize();
}
// See the GraphPack paper for a discussion of this
protected final void layout_rest() {
  int restcnt = _theRest.size();
  if( restcnt == 0 ) return;
  double M[][] = new double[restcnt][];
  for( int i = 0; i < restcnt; ++i )
    M[i] = new double[restcnt+2];      // The last 2 cols are the x and y's.

  // Fill in the matrix with -1.0 if i,j are adjacent, 0.0 otherwise (default)
  for( int i = 0; i < restcnt; ++i ) {
    Node n = (Node) _theRest.elementAt( i );
    int degree = 0;
    Vector outedges = n.outedges();
    int outedgecnt = outedges.size();
    for( int j = 0; j < outedgecnt; ++j ) {
      Node to = ((Edge) outedges.elementAt( j )).to();
      int col = to.level();
      if( col == -1 ) continue;
      M[i][col] = -1.0;
      ++degree;
    }
    Vector inedges = n.inedges();
    int inedgecnt = inedges.size();
    for( int j = 0; j < inedgecnt; ++j ) {
      Node from = ((Edge) inedges.elementAt( j )).from();
      int col = from.level();
      if( col == -1 ) continue;
      M[i][col] = -1.0;
      ++degree;
    }
    M[i][i] = degree+1;  // This was lacking in the GraphPack paper?!?!?!
  }
  
  // Fill in the x and y columns with the sum of adjacent nodes' x and y's.
  for( int i = 0; i < restcnt; ++i ) {
    Node u = (Node) _theRest.elementAt( i );
    double x = 0.0, y = 0.0;
    Vector inedges = u.inedges();
    int inedgecnt = inedges.size();
    for( int j = 0; j < inedgecnt; ++j ) {
      Node v = ((Edge) inedges.elementAt(j)).from();
      x += v.X();
      y += v.Y();
    }
    Vector outedges = u.outedges();
    int outedgecnt = outedges.size();
    for( int j = 0; j < outedgecnt; ++j ) {
      Node v = ((Edge) outedges.elementAt(j)).to();
      x += v.X();
      y += v.Y();
    }
    M[i][ restcnt ] = x;
    M[i][restcnt+1] = y;
  }

  // We do a Gauss-Jordan Upper Triangularization of the matrix
  int n = restcnt;   // Number of rows
  int m = restcnt+2; // Number of columns

  for( int i = 0; i < n-1; ++i ) {
		double fac = M[i][i];
		for( int j = 0; j < m; ++j )
      M[i][j] /= fac;
		for( int j = i+1; j < n; ++j ) {
			fac = M[j][i];
			for( int k = i; k < m; ++k ) 
        M[j][k] = M[j][k] - fac*M[i][k];
		}
	}

	double factor = M[n-1][n-1];
	for( int j = 0; j < m; ++j )
    M[n-1][j] /= factor;

  // We solve for the position of the vertices from the upper triangular matrix
	for( int i = n-1; i > 0; --i ) {
    for( int j = 0; j < i; ++j ) {
      double fac = M[j][i];
      for( int k = i-1; k < m; ++k )
        M[j][k] = M[j][k] - fac*M[i][k];
    }
  }
  for( int i = 0; i < n; ++i ) {
    Node u = (Node) _theRest.elementAt( i );
    u.XY( M[i][n]/M[i][i], M[i][n+1]/M[i][i] );
  }
}
protected final void save_new_cycle( Node begin, Node end ) {
  if( end.level()-begin.level()+1 != _theCycleLength
      || _theCycle != null ) 
    return;

  _theCycle = new Vector();
  int b = _visitedNodes.indexOf(begin); 
  int e = _visitedNodes.indexOf(end); 
  for( int i = b; i <= e; ++i )
    _theCycle.addElement( _visitedNodes.elementAt(i) );
}
} // class Circularizer
