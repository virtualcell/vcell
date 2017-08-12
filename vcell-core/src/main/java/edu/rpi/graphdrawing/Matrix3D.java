package edu.rpi.graphdrawing;

// This is adapted from some alpha code by a Sun programmer
public
class Matrix3D {
protected double xx, xy, xz;
protected double yx, yy, yz;
protected double zx, zy, zz;
protected static final double pi = 3.14159265;
protected double _den; 
	// Create a new unit matrix
public Matrix3D () {
	xx = 1.0;
	yy = 1.0;
	zz = 1.0;
}
public final double inverseX( int X, int Y ) {
	double f1 = yy*zz - yz*zy;
	double f2 = xz*zy - xy*zz;
	_den = xx*f1 + yx*f2 + zx*(xy*yz - xz*yy);
	return (X*f1 +  Y*f2) / _den;
}
public final double inverseY( int X, int Y ) {
	return (X*(yz*zx - yx*zz) +  Y*(xx*zz - xz*zx)) / _den;
}
public final double inverseZ( int X, int Y ) {
	return -(X*(yy*zx - yx*zy) +  Y*(xx*zy - xy*zx)) / _den;
}
public final int projectX( double x, double y, double z ) {
	return (int) (x * xx + y * xy + z * xz);
}
public final int projectY( double x, double y, double z ) {
	return (int) (x * yx + y * yy + z * yz);
}
public final int projectZ( double x, double y, double z ) {
	return (int) (x * zx + y * zy + z * zz);
}
	// rotate theta degrees about the x axis
public final void rotateX(double theta) {
	theta *= (pi / 180);
	double ct = Math.cos(theta);
	double st = Math.sin(theta);

	double Nyx = (double) (yx * ct + zx * st);
	double Nyy = (double) (yy * ct + zy * st);
	double Nyz = (double) (yz * ct + zz * st);

	double Nzx = (double) (zx * ct - yx * st);
	double Nzy = (double) (zy * ct - yy * st);
	double Nzz = (double) (zz * ct - yz * st);

	yx = Nyx;
	yy = Nyy;
	yz = Nyz;
	zx = Nzx;
	zy = Nzy;
	zz = Nzz;
}
	// rotate theta degrees about the y axis 
public final void rotateY(double theta) {
	theta *= (pi / 180);
	double ct = Math.cos(theta);
	double st = Math.sin(theta);

	double Nxx = (double) (xx * ct + zx * st);
	double Nxy = (double) (xy * ct + zy * st);
	double Nxz = (double) (xz * ct + zz * st);

	double Nzx = (double) (zx * ct - xx * st);
	double Nzy = (double) (zy * ct - xy * st);
	double Nzz = (double) (zz * ct - xz * st);

	xx = Nxx;
	xy = Nxy;
	xz = Nxz;
	zx = Nzx;
	zy = Nzy;
	zz = Nzz;
}
	// rotate theta degrees about the z axis
public final void rotateZ(double theta) {
	theta *= (pi / 180);
	double ct = Math.cos(theta);
	double st = Math.sin(theta);

	double Nyx = (double) (yx * ct + xx * st);
	double Nyy = (double) (yy * ct + xy * st);
	double Nyz = (double) (yz * ct + xz * st);

	double Nxx = (double) (xx * ct - yx * st);
	double Nxy = (double) (xy * ct - yy * st);
	double Nxz = (double) (xz * ct - yz * st);

	yx = Nyx;
	yy = Nyy;
	yz = Nyz;
	xx = Nxx;
	xy = Nxy;
	xz = Nxz;
}
}
