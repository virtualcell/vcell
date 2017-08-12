package edu.rpi.graphdrawing;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Vector;

public 
class Node {

protected Vector _in;
protected Vector _out;

protected String _label;
	// Basic stuff
	//
protected double _x, _y, _z;
protected boolean _fixed = false;
protected boolean _picked = false;
protected boolean _dummy = false;
protected Blackboard _bb;
	// DFS stuff
	//
protected boolean _marked = false;
	// Level stuff
	//
protected int _level;
protected int _useage_level;
protected double _barycenter;
	// Localization stuff
	//
protected boolean _center;
protected boolean _showing = true;
	// Force-directed placement stuff
	//
protected double _dx, _dy, _dz;
	// Paint stuff
	//
protected final Color fixedColor = Color.red;
protected final Color nodeColor = Color.white;
protected final Color selectColor = Color.pink;
private final int xmarginSize = 10;
private final int ymarginSize = 4;
private final int dummySize = 8;
protected int _boundingWidth;
protected int _boundingHeight;
public Node( Blackboard b ) {
	this( b, "DUMMY", false, false );
	_dummy = true;
}
public Node( Blackboard b, String label ) {
	this( b, label, false, false );
}
public Node( Blackboard b, String label, boolean fix, boolean pick ) {
  _bb = b;
	_in = new Vector();
	_out = new Vector();
	_label = label;
	_fixed = fix;
	_picked = pick;
	_x = _y = _z = _dx = _dy = _dz = 0.0; 
}
private final double __max( double a, double b ) { return (a>b)?a:b; }
private final double __min( double a, double b ) { return (a<b)?a:b; }
	// Edge stuff
	//
public final void add_in( Edge e ) {
	_in.addElement(e);
}
public final void add_out( Edge e ) {
	_out.addElement(e);
}
public final void addDelta( PointDelta d ) {
	_dx += d.dx;
	_dy += d.dy;
	_dz += d.dz;
}
public final double barycenter() {
	return _barycenter;
}
public final void barycenter( double b ) {
	_barycenter = b;
}
public final void boundedMoveDelta( double bound ) {
	_x += __max( -bound, __min(bound, _dx) );
	_y += __max( -bound, __min(bound, _dy) );
	_z += __max( -bound, __min(bound, _dz) );
}
public final int boundingHeight() {
	return _boundingHeight;
}
public final int boundingWidth() {
	return _boundingWidth;
}
public final boolean center() { return _center; }
public final void center(boolean b) { _center = b; }
public final double computeBarycenter(boolean doin, boolean doout) {
  double insum = 0.0;
	int insize = _in.size();
	if( doin ) {
    for( int i = 0; i < insize; ++i )
      insum += ((Edge)_in.elementAt( i )).from().x();
		if( insize == 0 ) {
			insize = 1;
			insum = x();
		}
	}
  double outsum = 0.0;
	int outsize = _out.size();
	if( doout ) {
    for( int i = 0; i < outsize; ++i )
      outsum += ((Edge)_out.elementAt( i )).to().x();
		if( outsize == 0 ) {
			outsize = 1;
			outsum = x();
		}
	}
	if( doin && doout )
		return (insum+outsum)/(insize+outsize);
	else if( doin )
		return insum/insize;
	else if( doout )
		return outsum/outsize;
	Node n = null;    // Throw an exception if neither doin or dout are there.
	return n.level();
}
public final int degree() { 
	return _in.size()+_out.size(); 
}
public final double deltaForce() {
	return Math.sqrt( _dx*_dx + _dy*_dy + _dz*_dz );
}
public final boolean dummy() { return _dummy; }
public final Enumeration edges() {
	return new NodeEdgeEnumerator(_in,_out);
}
public final void fix(boolean b) { _fixed = b; }
public final boolean fixed() { return _fixed; }
public final void forced( Node other, double factor ) {
	_dx += (_x - other._x) * factor;
	_dy += (_y - other._y) * factor;
	_dz += (_z - other._z) * factor;
}
public final PointDelta getDelta() {
	return new PointDelta( _dx, _dy, _dz );
}
public final Vector inedges() {
  return _in;
}
public final String label() { return _label; }
public final int level() {
	return _level;
}
public final void level( int l ) {
	_level = l;
}
protected final void mark() {
	_marked = true;
}
protected final boolean marked() {
	return _marked;
}
public final void moveDelta( double factor ) {
	_x += _dx * factor;
	_y += _dy * factor;
	_z += _dz * factor;
}
public final Vector outedges() {
  return _out;
}
public void paint(Graphics g) {
	int x = _bb.projection.projectX( _x, _y, _z );
	int y = _bb.projection.projectY( _x, _y, _z );

	if( dummy() ) {
		//g.setColor(Color.blue);
		//g.fillOval( x-dummySize/2, y-dummySize/2, dummySize, dummySize );
		_boundingHeight = dummySize;
		_boundingWidth = dummySize;
	}
	else {
		g.setColor( ((_picked) ? selectColor : (_fixed ? fixedColor : nodeColor)) );
		int depdelta = (int)( 10*_bb.projection.projectZ( _x, _y, _z )
													/ _bb.globals.L() );
		FontMetrics fm = g.getFontMetrics();
		int w = _boundingWidth = fm.stringWidth(_label) + xmarginSize + depdelta;
		int h = _boundingHeight = fm.getHeight() + ymarginSize + depdelta;
		g.fillRect( x-w/2, y-h/2, w, h );
		g.setColor(Color.black);
		g.drawRect( x-w/2, y-h/2, w-1, h-1 );
		if( _center ) {
			g.drawRect( x-w/2-1, y-h/2-1, w+1, h+1 );
			g.drawRect( x-w/2-2, y-h/2-2, w+3, h+3 );
			g.drawRect( x-w/2-3, y-h/2-3, w+5, h+5 );
		}
		g.drawString( _label,
								  x-(w-xmarginSize)/2,
								  y-(h-ymarginSize)/2 + fm.getAscent());
	}
}
public final void pick(boolean b) { _picked = b; }
public final boolean picked() { return _picked; }
	// Initial placement stuff
	//
public final void randomPlacement( double xf, double yf, double zf ) {
	XY( xf*(Math.random() - 0.5), yf*(Math.random() - 0.5) );
	_z = zf*(Math.random() - 0.5);
	stabilize();
}
public final void remove_in( Edge e ) {
	_in.removeElement(e);
}
public final void remove_out( Edge e ) {
	_out.removeElement(e);
}
public final void replace_in( Edge e, Edge newe ) {
	int pos = _in.indexOf(e);
	_in.setElementAt( newe, pos );
}
public final void replace_out( Edge e, Edge newe ) {
	int pos = _out.indexOf(e);
	_out.setElementAt( newe, pos );
}
public final void scaleDelta( double factor ) {
	_dx = _dx * factor;
	_dy = _dy * factor;
	_dz = _dz * factor;
}
public final boolean showing() { return _showing; }
public final void showing(boolean b) { _showing = b; }
public final void stabilize() {
	_dx = _dy = _dz = 0.0;
}
public final String toString() {
 String r =  "LABEL: " + _label
	      + ", lev "  + _level
	      + ", deg "  + degree()
	      + ", bar "  + barycenter()
	      + ", ("  + _x + "," + _y + "," + _z + ")";
 r = r + "\n\tIN:  "  + _in.toString()
       + "\n\tOUT: "  + _out.toString();
 return r;
}
protected final void unmark() {
	_marked = false;
}
public final int useage_level() {
	return _useage_level;
}
public final void useage_level( int l ) {
	_useage_level = l;
}
public final double x() { return _x; }
	// Projection stuff
	//
public final int X() {
	return _bb.projection.projectX( _x, _y, _z );
}
public final void x(double d) { _x = d; }
public final void XY( double newx, double newy ) {
	XY( (int) newx, (int) newy );
}
public final void XY( int newx, int newy ) {
	_x = _bb.projection.inverseX( newx, newy );
	_y = _bb.projection.inverseY( newx, newy );
}
public final double y() { return _y; }
public final int Y() {
	return _bb.projection.projectY( _x, _y, _z );
}
public final void y(double d) { _y = d; }
public final double z() { return _z; }
public final int Z() {
	return _bb.projection.projectZ( _x, _y, _z );
}
public final void z(double d) { _z = d; }
} // class Node
