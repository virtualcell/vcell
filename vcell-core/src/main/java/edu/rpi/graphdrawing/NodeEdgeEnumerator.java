package edu.rpi.graphdrawing;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

final
class NodeEdgeEnumerator implements Enumeration {
private Vector _in;
private Vector _out;
private int _incount;
private int _outcount;

public NodeEdgeEnumerator(Vector i, Vector o) {
	_in = i;
	_out = o;
	_incount = 0;
	_outcount = 0;
}
public final boolean hasMoreElements() {
	synchronized (_in) {
		synchronized (_out) {
			if( _incount == _in.size() ) 
				return _outcount < _out.size();
			return _incount < _in.size();
		}
	}
}
public final Object nextElement() {
	synchronized (_in) {
		synchronized (_out) {
			if (_incount < _in.size())
				return _in.elementAt(_incount++);
			if (_outcount < _out.size())
				return _out.elementAt(_outcount++);
		}
	}
	throw new NoSuchElementException("NodeEdgeEnumerator");
}
} // class NodeEdgeEnumerator
