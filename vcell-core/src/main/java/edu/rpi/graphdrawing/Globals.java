package edu.rpi.graphdrawing;

public
class Globals {

protected Blackboard _bb;
	// Localization depth constant
	//
protected int _localizationDepth;
  // Cycle length constant
  //
protected int _cycleLength;
	// Sundry placement constants
	//
protected double _depth3D;
protected double _D;
protected double _minTemp;
protected double _temp;
protected double _ac;
protected double _ae;
protected double _rc;
protected double _re;
	// Applet environment stuff
	//
protected String _basedir;
public Globals( Blackboard black ) {
	_bb = black;
}
public final double ac() {
	return _ac;
}
public final void ac( double d ) {
	_ac = d;
}
public final double ae() {
	return _ae;
}
public final void ae( double d ) {
	_ae = d;
}
public final double area() {
	return L()*L();
}
public String basedir() {
	return _basedir;
}
public final int cycleLength() {
	return _cycleLength;
}
public final void cycleLength( int l ) {
	_cycleLength = l;
}
public final double D() {
	return _D;
}
public final void D( double d ) {
	_D = d;
}
public final double depth3D() {
	return _depth3D;
}
public final void depth3D( double d ) {
	_depth3D = d;
}
public final double k() {
	return Math.sqrt( area()/_bb.nodes().size() );
}
public final double L() {
  return _D*_bb.nodes().size();
}
public final int localizationDepth() {
	return _localizationDepth;
}
public final void localizationDepth( int d ) {
	_localizationDepth = d;
}
public final double minTemp() {
	return _minTemp;
}
public final void minTemp( double d ) {
	_minTemp = d;
}
public final double rc() {
	return _rc;
}
public final void rc( double d ) {
	_rc = d;
}
public final double re() {
	return _re;
}
public final void re( double d ) {
	_re = d;
}
public void setBasedir( String d ) {
	_basedir = d;
}
public final double Temp() {
	return _temp;
}
public final void Temp( double d ) {
	_temp = d;
}
} // class Globals
