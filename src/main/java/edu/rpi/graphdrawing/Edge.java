package edu.rpi.graphdrawing;

import java.util.*;
import java.awt.*;

public 
class Edge {

protected Node _from;
protected Node _to;
protected Blackboard _bb;
protected double _length;
protected final Color edgeColor = Color.black;
protected final int arrowSize = 4;
protected int _dir = 1;
public Edge( Blackboard b, Node f, Node t ) {
  _bb = b;
	_from = f;
	_to = t;
}
public final double currLength() {
	return _bb.Norm( _from, _to );
}
public final Node from() { return _from; }
protected Polygon getArrow() {
	int dX = _to.X()-_from.X();
	int dY = _to.Y()-_from.Y();
	double len = Math.sqrt( dX*dX + dY*dY );
	double ndx = _dir*arrowSize*dX/len;
	double ndy = _dir*arrowSize*dY/len;
	double cx = (_to.X()+_from.X())/2;
	double cy = (_to.Y()+_from.Y())/2;
	Polygon tmp = new Polygon();
	tmp.addPoint( (int)(cx - ndy), (int)(cy + ndx) );
	tmp.addPoint( (int)(cx + ndx), (int)(cy + ndy) );
	tmp.addPoint( (int)(cx + ndy), (int)(cy - ndx) );
	tmp.addPoint( (int)(cx - ndy), (int)(cy + ndx) );
	return tmp;
}
public final double length() {
	return _length;
}
public void paint(Graphics g) {
	g.setColor( edgeColor );
	g.drawLine( (int)_from.X(), (int)_from.Y(), (int)_to.X(), (int)_to.Y() );
	g.fillPolygon( getArrow() );
}
public final boolean reversed() {
	return _dir == -1;
}
public final void reverseDir() {
	if( _dir == 1 ) 
		_dir = -1;
}
public final void swapDir() {
	Node n = _from;
	_from = _to;
	_to = n;
}
public final Node to() { return _to; }
public final String toString() {
	return "{" + _from.label() + ";" + _to.label() + "-" + _length + "}";
}
public final void updateLength() {
	_length = currLength();
}
} // class Edge
