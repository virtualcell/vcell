package cbit.util;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.awt.geom.*;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.*;
import java.util.Vector;
import javax.swing.*;
import java.io.Serializable;
/**
 * Insert the type's description here.
 * Creation date: (8/18/2000 2:29:31 AM)
 * @author: 
 */
public final class BeanUtils {

	public static class XYZ {
		public double x;
		public double y;
		public double z;
		public XYZ(){
			x = 0;
			y = 0;
			z = 0;
		}
		public XYZ(double argX, double argY, double argZ){
			x = argX;
			y = argY;
			z = argZ;
		}
	};

/**
 * Insert the method's description here.
 * Creation date: (8/3/00 2:53:56 PM)
 * @return java.lang.Object[]
 * @param oArray java.lang.Object[]
 * @param o java.lang.Object
 */
public static Object[] addElement(Object[] oArray, Object o) {
	Vector v = new Vector();
	for (int c = 0; c < oArray.length; c += 1) {
		v.addElement(oArray[c]);
	}
	v.addElement(o);
	return v.toArray(oArray);
}


/**
 * Insert the method's description here.
 * Creation date: (8/3/00 2:53:56 PM)
 * @return java.lang.Object[]
 * @param oArray java.lang.Object[]
 * @param o java.lang.Object
 */
public static Object[] addElements(Object[] oArray1, Object[] oArray2) {
	Vector v = new Vector();
	for (int c = 0; c < oArray1.length; c += 1) {
		v.addElement(oArray1[c]);
	}
	int arrLength = oArray2.length;
	for(int i=0;i < arrLength;i+= 1){
		v.addElement(oArray2[i]);
	}
	return v.toArray(oArray1);
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:57:46 PM)
 * @return boolean
 * @param objects java.lang.Object[]
 * @param object java.lang.Object
 */
public static boolean arrayContains(Object[] objects, Object object) {
	if (object == null || objects == null) {
		return false;
	}
	for (int i = 0; i < objects.length; i++){
		if (object.equals(objects[i])) {
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2001 4:49:40 PM)
 * @return boolean
 * @param a1 java.lang.Object[]
 * @param a2 java.lang.Object[]
 */
public static boolean arrayEquals(Object[] a1, Object[] a2) {
	if (a1 == a2) {
		return true;
	}
	if (a1 != null && a2 != null) {
		if (a1.length == a2.length) {
			for (int i = 0; i < a1.length; i++){
				if (a1[i] != null) {
					if (! a1[i].equals(a2[i])) {
						return false;
					}
				} else if (a2[i] != null) {
					return false;
				}
			}
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 2:21:25 AM)
 * @param frame java.awt.Frame
 */
public static void attemptMaximize(Frame frame) {
	// not supported on all platforms
	// not supported by JRE's 1.3 and earlier
	// when not supported, frame will stay unchanged
	Class c = frame.getClass();
	java.lang.reflect.Method[] ms = c.getMethods();
	java.lang.reflect.Method m = null;
	for (int i = 0; i < ms.length; i++){
		if (ms[i].getName().equals("setExtendedState")) {
			m = ms[i];
			break;
		}
	}
	java.lang.reflect.Field[] fs = c.getFields();
	java.lang.reflect.Field f = null;
	for (int i = 0; i < fs.length; i++){
		if (fs[i].getName().equals("MAXIMIZED_BOTH")) {
			f = fs[i];
		}
	}
	if (m != null && f != null) {
		try {
			m.invoke(frame, new Object[] {f.get(frame)});
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace(System.out);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 2:21:25 AM)
 * @param frame java.awt.Frame
 */
public static void attemptResizeWeight(JSplitPane pane, double weight) {
	// not supported on all platforms
	// not supported by JRE's 1.2.x and earlier
	// when not supported, pane will stay unchanged
	java.lang.reflect.Method[] ms = pane.getClass().getMethods();
	java.lang.reflect.Method m = null;
	for (int i = 0; i < ms.length; i++){
		if (ms[i].getName().equals("setResizeWeight")) {
			m = ms[i];
			break;
		}
	}
	if (m != null) {
		try {
			m.invoke(pane, new Object[] {new Double(weight)});
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace(System.out);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/18/00 5:50:51 PM)
 */
public static void centerOnComponent(Component c, Component reference) {
	if (c != null && reference != null) {
		// if reference is orphan, center on screen
		if (reference.getParent() == null) {
			centerOnScreen(c);
			return;
		}
		Point p = null;
		try {
			p = reference.getLocationOnScreen();
		} catch (Exception exc) {
			// reference probably not visible, center on screen
			centerOnScreen(c);
			return;
		}
		// translate reference component's coordinates to the coordinate system
		// of the parent of the component to be centered (if it has a valid one)
		try {
			Component cParent = c.getParent();
			if(cParent != null){
				Point p1 = cParent.getLocationOnScreen();
				if(p1 != null){
					p.x -= p1.x;
					p.y -= p1.y;
				}
			}
		} catch (Exception exc) {
			// probably no valid parent (null, or hidden, etc.) - ignore
		}
		// now we try to center, but we do respect top/left bounds of reference
		// (protection useful if one centers onto parents, esp. for inner windows)
		p.x += Math.max((reference.getWidth() - c.getWidth()) / 2, 0);
		p.y += Math.max((reference.getHeight() - c.getHeight()) / 2, 0);
		c.setLocation(p);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/18/00 5:50:51 PM)
 */
public static void centerOnScreen(Component c) {
	if (c != null) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = c.getSize();
		if (size.height > screenSize.height)
				size.height = screenSize.height;
		if (size.width > screenSize.width)
				size.width = screenSize.width;
		c.setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2002 10:57:56 AM)
 * @return java.awt.geom.Line2D
 * @param line java.awt.geom.Line2D
 * @param rect java.awt.geom.Rectangle2D
 */
public static Line2D clipLine(Line2D line, Rectangle2D rect) {
	if (line != null && rect != null && line.intersects(rect)) {
		Point2D p1 = line.getP1();
		Point2D p2 = line.getP2();
		Line2D[] borders = new Line2D[4]; // we'll do them clockwise
		borders[0] = new Line2D.Double(rect.getMinX(), rect.getMinY(), rect.getMaxX(), rect.getMinY());
		borders[1] = new Line2D.Double(rect.getMaxX(), rect.getMinY(), rect.getMaxX(), rect.getMaxY());
		borders[2] = new Line2D.Double(rect.getMaxX(), rect.getMaxY(), rect.getMinX(), rect.getMaxY());
		borders[3] = new Line2D.Double(rect.getMinX(), rect.getMaxY(), rect.getMinX(), rect.getMinY());
		if (p1.getX() >= rect.getX() && 
			p1.getY() >= rect.getY() && 
			p1.getX() <= rect.getX() + rect.getWidth() && 
			p1.getY() <= rect.getY() + rect.getHeight() &&
			p2.getX() >= rect.getX() && 
			p2.getY() >= rect.getY() && 
			p2.getX() <= rect.getX() + rect.getWidth() && 
			p2.getY() <= rect.getY() + rect.getHeight()) {
			return line;
		} else {
			if (p1.getX() != p2.getX() && p1.getY() != p2.getY()) {
				// lines parallel to the rectangle will get separate treatment
				if (p1.getX() >= rect.getX() && 
					p1.getY() >= rect.getY() && 
					p1.getX() <= rect.getX() + rect.getWidth() && 
					p1.getY() <= rect.getY() + rect.getHeight()) {
					// clip from p1 to intersection with one border
					for (int i=0;i<4;i++) {
						if (line.intersectsLine(borders[i])) {
							line.setLine(p1, BeanUtils.lineIntersection(line, borders[i]));
						}
					}
					return line;
				} else if (p2.getX() >= rect.getX() && 
					p2.getY() >= rect.getY() && 
					p2.getX() <= rect.getX() + rect.getWidth() && 
					p2.getY() <= rect.getY() + rect.getHeight()) {
					// do it again in reverse
					line.setLine(p2, p1);
					return BeanUtils.clipLine(line, rect);
				} else {
					// we must intersect at least 2 borders (3 or 4 if we go through corner(s))
					Point2D p01 = null;
					Point2D p02 = null;
					for (int i=0;i<4;i++) {
						if (line.intersectsLine(borders[i])) {
							if (p01 == null) {
								p01 = BeanUtils.lineIntersection(line, borders[i]);
							} else if (p02 == null || p02.equals(p01)) {
								// second one if not found yet (if non-null and non-equal, it's the one we want,
								// so don't risk overwriting it again with p01 if we're just finding the first one again as a corner
								p02 = BeanUtils.lineIntersection(line, borders[i]);
							}
						}
					}
					line.setLine(p01, p02);
					return line;
				}
			} else if (p1.getX() == p2.getX() && p1.getY() == p2.getY()) {
				// single point within or on border of rectangle
				return line;
			} else if (p1.getX() == p2.getX()) {
				// vertical line with one point within rectangle
				if (p1.getX() >= rect.getX() && 
					p1.getY() >= rect.getY() && 
					p1.getX() <= rect.getX() + rect.getWidth() && 
					p1.getY() <= rect.getY() + rect.getHeight()) {
					if (p2.getY() < rect.getMinY()) {
						// p2 is above rectangle
						p2.setLocation(p2.getX(), rect.getMinY());
					} else {
						// p2 is below rectangle
						p2.setLocation(p2.getX(), rect.getMaxY());
					}
					line.setLine(p1, p2);
					return line;
				} else {
					// must contain p2
					// do it again in reverse
					line.setLine(p2, p1);
					return BeanUtils.clipLine(line, rect);
				}
			} else {
				// horizontal line with one point within rectangle
				if (p1.getX() >= rect.getX() && 
					p1.getY() >= rect.getY() && 
					p1.getX() <= rect.getX() + rect.getWidth() && 
					p1.getY() <= rect.getY() + rect.getHeight()) {
					if (p2.getX() < rect.getMinX()) {
						// p2 is to the left of rectangle
						p2.setLocation(rect.getMinX(), p2.getY());
					} else {
						// p2 is to the right of rectangle
						p2.setLocation(rect.getMaxX(), p2.getY());
					}
					line.setLine(p1, p2);
					return line;
				} else {
					// must contain p2
					// do it again in reverse
					line.setLine(p2, p1);
					return BeanUtils.clipLine(line, rect);
				}
			}
		}
	} else {
		return null;
	}
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param obj java.io.Serializable
 */
public static Serializable cloneSerializable(Serializable obj) throws ClassNotFoundException, java.io.IOException {

//	System.out.println("BeanUtils.cloneSerializable, ("+obj+")");

	Serializable clone = fromSerialized(toSerialized(obj));
	return clone;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 2:21:25 AM)
 * @param frame java.awt.Frame
 */
public static void closeAllWindows(JDesktopPane pane) {
	JInternalFrame[] frames = pane.getAllFrames();
	if (frames != null) {
		for (int i = 0; i < frames.length; i++){
			pane.getDesktopManager().closeFrame(frames[i]);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:33:11 AM)
 * @param out java.io.ObjectOutputStream
 * @exception java.io.IOException The exception description.
 */
public static byte[] compress(byte[] bytes) throws java.io.IOException {	
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	DeflaterOutputStream dos = new DeflaterOutputStream(bos);
	dos.write(bytes,0,bytes.length);
	dos.close();
	byte[] compressed = bos.toByteArray();
	bos.close();
	return compressed;
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2005 1:19:25 PM)
 * @return int
 * @param coordinates int[]
 * @param bounds int[]
 */
public final static int coordinateToIndex(int[] coordinates, int[] bounds) {
	// computes linearized index of a position in an n-dimensional matrix
	// see also related method indexToCoordinate()
	if (coordinates.length != bounds.length) throw new RuntimeException("coordinates and bounds arrays have different lengths");
	int index = 0;
	for (int i = 0; i < bounds.length; i++){
		if (coordinates[i] < 0 || bounds[i] < coordinates[i]) throw new RuntimeException("invalid coordinate");
		int offset = 1;
		for (int j = i + 1; j < bounds.length; j++){
			offset *= bounds[j] + 1;
		}
		index += coordinates[i] * offset;
	}
	return index;
}


/**
 * Comment
 */
public static void enableComponents(Container container, boolean b) {
	Component[] components;
	if (container instanceof JMenu) {
		components = ((JMenu)container).getMenuComponents();
	} else {
		components = container.getComponents();
	}
	for (int i=0;i<components.length;i++) {
		components[i].setEnabled(b);
		if (components[i] instanceof Container) {
			BeanUtils.enableComponents((Container)components[i], b);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/13/2003 6:55:05 PM)
 */
public static Container findTypeParentOfComponent(Component component,Class parentType) {
	for (Container p = component.getParent(); p != null; p = p.getParent()) {
		if(parentType.isAssignableFrom(p.getClass())) {
			return p;
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 5:06:31 PM)
 * @return int
 * @param dd double[]
 * @param d double
 */
public static int firstIndexOf(double[] dd, double d) {
	if (dd != null) {
		for (int i = 0; i < dd.length; i++){
			if (d == dd[i]) {
				return i;
			}
		}
	}
	return -1;
}


/**
 * Insert the method's description here.
 * Creation date: (4/24/2001 11:17:52 AM)
 * @return java.lang.String
 * @param s java.lang.String
 * @param size int
 */
public static String forceStringSize(String s, int size,String padChar,boolean bPrependPad) {
	//
	StringBuffer pad = new StringBuffer();
	for(int c = 0;c < size;c+= 1){
		pad.append(padChar);
	}
	if(size == 0){
		return "";
	}
	if(s == null || s.length() == 0){
		return pad.toString().substring(0,size);
	}
	if(size <= s.length()){
		return s.substring(0,size);
	}
	if(bPrependPad){
		return pad.toString().substring(0,size-s.length()) + s;
	}
	return s + pad.toString().substring(0,size-s.length());
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Cacheable
 * @param objData byte[]
 */
public static Serializable fromCompressedSerialized(byte[] objData) throws ClassNotFoundException, java.io.IOException {
	long before = System.currentTimeMillis();

	java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(objData);
	InflaterInputStream iis = new InflaterInputStream(bis);
	java.io.ObjectInputStream ois = new java.io.ObjectInputStream(iis);
	Serializable cacheClone = (Serializable) ois.readObject();
	ois.close();
	bis.close();
	
	long after = System.currentTimeMillis();
	System.out.println("BeanUtils.fromSerialized, t="+(after-before)+" ms, ("+cacheClone+")");
	
	return cacheClone;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Cacheable
 * @param objData byte[]
 */
public static Serializable fromSerialized(byte[] objData) throws ClassNotFoundException, java.io.IOException {
//	long before = System.currentTimeMillis();

	java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(objData);
	java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bis);
	Serializable cacheClone = (Serializable) ois.readObject();
	ois.close();
	bis.close();
	
//	long after = System.currentTimeMillis();
//	System.out.println("BeanUtils.fromSerialized, t="+(after-before)+" ms, ("+cacheClone+")");
	
	return cacheClone;
}


/**
 * Insert the method's description here.
 * Creation date: (8/3/00 2:53:56 PM)
 * @return java.lang.Object[]
 * @param oArray java.lang.Object[]
 * @param o java.lang.Object
 */
public static Object[] getArray(java.util.Enumeration enumeration, Class elementType) {
	Vector v = new Vector();
	while (enumeration.hasMoreElements()){
		v.addElement(enumeration.nextElement());
	}
	Object[] a = (Object[]) java.lang.reflect.Array.newInstance(elementType, v.size());
	v.copyInto(a);
	return a;
}


/**
 * Insert the method's description here.
 * Creation date: (8/3/00 2:53:56 PM)
 * @return java.lang.Object[]
 * @param oArray java.lang.Object[]
 * @param o java.lang.Object
 */
public static Object[] getArray(java.util.Vector vector, Class elementType) {
	Object[] a = (Object[]) java.lang.reflect.Array.newInstance(elementType, vector.size());
	vector.copyInto(a);
	return a;
}


/**
 * Comment
 */
public static Rectangle[] getTiledBounds(int numberOfTiles, int areaWidth, int areaHeight, boolean horizontal) {
	int n = numberOfTiles; int w = areaWidth; int h = areaHeight;
	if (n <= 0)
		return null;
	Rectangle[] rectangles = new Rectangle[n];
	int a = (int) Math.sqrt(n);
	int r1 = 0; int r2 = 0;
	int x = 0; int x1 = 0;
	int y = 0; int y1 = 0;
	if (horizontal) {
		if (n - a * a > a) {
			r2 = n - a * a - a;
			r1 = a + 1 - r2;
			y = h / (a + 1);
		} else {
			r2 = n - a * a;
			r1 = a - r2;
			y = h / a;
		}
		x = w / a;
		x1 = w / (a + 1);
		for (int i=0;i<r2+r1;i++) {
			if (i == r1 + r2)
				y = h - y * (i - 1);
			if (i < r2) {
				for (int j=0;j<a+1;j++) {
					if (j == a + 1) {
	                    rectangles[i * (a + 1) + j] = new Rectangle(j * x, i * y, w - x * a, y); 
					} else {
						rectangles[i * (a + 1) + j] = new Rectangle(j * x1, i * y, x1, y);
					}
				}
			} else {
				for (int j = 0;j<a;j++) {
					if (j == a) {
						rectangles[r2 * (a + 1) + (i - r2) * a + j] = new Rectangle(j * x, i * y, w - x * (a - 1), y); 
					} else {
						rectangles[r2 * (a + 1) + (i - r2) * a + j] = new Rectangle(j * x, i * y, x, y);
					}
				}
			}
		}
	} else {
		if (n - a * a > a) {
			r2 = n - a * a - a;
			r1 = a + 1 - r2;
			x = w / (a + 1);
		} else {
			r2 = n - a * a;
			r1 = a - r2;
			x = w / a;
		}
		y = h / a;
		y1 = h / (a + 1);
		for (int i=0;i<r1+r2;i++) {
			if (i == r1 + r2)
				x = w - x * (i - 1);
			if (i < r2) {
				for (int j=0;j<a+1;j++) {
					if (j == a + 1) {
	                    rectangles[i * (a + 1) + j] = new Rectangle(i * x, j * y, x, h - y * a); 
					} else {
						rectangles[i * (a + 1) + j] = new Rectangle(i * x, j * y1, x, y1);
					}
				}
			} else {
				for (int j = 0;j<a;j++) {
					if (j == a) {
						rectangles[r2 * (a + 1) + (i - r2) * a + j] = new Rectangle(i * x, j * y, x, h - y * (a - 1)); 
					} else {
						rectangles[r2 * (a + 1) + (i - r2) * a + j] = new Rectangle(i * x, j * y, x, y);
					}
				}
			}
		}
	}
	return rectangles;
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2005 1:19:25 PM)
 * @return int[]
 * @param index int
 * @param bounds int[]
 */
public final static int[] indexToCoordinate(int index, int[] bounds) {
	// computes coordinates of a position in an n-dimensional matrix from linearized index
	// see also related method coordinateToIndex()
	if (index < 0) throw new RuntimeException("invalid index, negative number");
	int[] coordinates = new int[bounds.length];
	for (int i = 0; i < bounds.length; i++){
		int offset = 1;
		for (int j = i + 1; j < bounds.length; j++){
			offset *= bounds[j] + 1;
		}
		coordinates[i] = index / offset;
		if (coordinates[i] > bounds[i]) throw new RuntimeException("invalid index, number too large");
		index -= offset * coordinates[i];
	}
	return coordinates;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/2004 4:53:59 PM)
 * @return cbit.vcell.geometry.XYZ
 */
public static XYZ[] lineIntersect3D(XYZ p1,XYZ p2,XYZ p3,XYZ p4) {
	
	/*
	http://astronomy.swin.edu.au/~pbourke/geometry/lineline3d/

	Calculate the line segment PaPb that is the shortest route between
	   two lines P1P2 and P3P4. Calculate also the values of mua and mub where
	      Pa = P1 + mua (P2 - P1)
	      Pb = P3 + mub (P4 - P3)
	   Return null if no solution exists.
	*/

	final double EPS = 1.0E-12; //epsilon
	
   XYZ p13 = new XYZ();
   XYZ p43 = new XYZ();
   XYZ p21 = new XYZ();
   double d1343,d4321,d1321,d4343,d2121;
   double numer,denom;
   double mua,mub;
   XYZ pa,pb;

   p13.x = p1.x - p3.x;
   p13.y = p1.y - p3.y;
   p13.z = p1.z - p3.z;
   p43.x = p4.x - p3.x;
   p43.y = p4.y - p3.y;
   p43.z = p4.z - p3.z;
   if (Math.abs(p43.x)  < EPS && Math.abs(p43.y)  < EPS && Math.abs(p43.z)  < EPS)
      return(null);
   p21.x = p2.x - p1.x;
   p21.y = p2.y - p1.y;
   p21.z = p2.z - p1.z;
   if (Math.abs(p21.x)  < EPS && Math.abs(p21.y)  < EPS && Math.abs(p21.z)  < EPS)
      return(null);

   d1343 = p13.x * p43.x + p13.y * p43.y + p13.z * p43.z;
   d4321 = p43.x * p21.x + p43.y * p21.y + p43.z * p21.z;
   d1321 = p13.x * p21.x + p13.y * p21.y + p13.z * p21.z;
   d4343 = p43.x * p43.x + p43.y * p43.y + p43.z * p43.z;
   d2121 = p21.x * p21.x + p21.y * p21.y + p21.z * p21.z;

   denom = d2121 * d4343 - d4321 * d4321;
   if (Math.abs(denom) < EPS)
      return(null);
   numer = d1343 * d4321 - d1321 * d4343;

   mua = numer / denom;
   mub = (d1343 + d4321 * (mua)) / d4343;

   pa = new XYZ(p1.x + mua * p21.x,p1.y + mua * p21.y,p1.z + mua * p21.z);
   pb = new XYZ(p3.x + mub * p43.x,p3.y + mub * p43.y,p3.z + mub * p43.z);

   return new XYZ[] {pa,pb};

}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2002 12:05:50 PM)
 * @return java.awt.geom.Point2D
 * @param l1 java.awt.geom.Line2D
 * @param l2 java.awt.geom.Line2D
 */
public static Point2D lineIntersection(Line2D l1, Line2D l2) {
	if (l1 != null && l2 != null && l1.intersectsLine(l2)) {
		double a1 = (l1.getY2() - l1.getY1()) / (l1.getX2() - l1.getX1());
		double b1 = (l1.getX2() * l1.getY1() - l1.getX1() * l1.getY2()) / (l1.getX2() - l1.getX1());
		double a2 = (l2.getY2() - l2.getY1()) / (l2.getX2() - l2.getX1());
		double b2 = (l2.getX2() * l2.getY1() - l2.getX1() * l2.getY2()) / (l2.getX2() - l2.getX1());
		if (l1.getX1() == l1.getX2()) {
			if (l2.getX1() == l2.getX2()) {
				return new Point2D.Double(Double.NaN, Double.NaN);
			} else {
				return new Point2D.Double(l1.getX1(), a2 * l1.getX1() + b2);
			}
		} else if (l2.getX1() == l2.getX2()) {
			return new Point2D.Double(l2.getX2(), a1 * l2.getX2() + b1);
		} else {
			return new Point2D.Double((b1 - b2) / (a2 - a1), a1 * (b1 - b2) / (a2 - a1) + b1);
		}
	} else {
		return null;
	}
}


/**
 * Comment
 */
public static void printComponentInfo(Container container) {
	Component[] components;
	if (container instanceof JMenu) {
		components = ((JMenu)container).getMenuComponents();
	} else {
		components = container.getComponents();
	}
	for (int i=0;i<components.length;i++) {
		System.out.println(components[i]);
		System.out.println("     Preferred Size: " + components[i].getPreferredSize());
		System.out.println("     Actual Size: " + components[i].getSize());
		if (components[i] instanceof Container) {
			BeanUtils.printComponentInfo((Container)components[i]);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/3/00 2:53:56 PM)
 * @return java.lang.Object[]
 * @param oArray java.lang.Object[]
 * @param o java.lang.Object
 */
public static Object[] removeElement(Object[] oArray, Object o) {
	Vector v = new Vector();
	for (int c = 0; c < oArray.length; c += 1) {
		v.addElement(oArray[c]);
	}
	if (v.contains(o)) {
		v.remove(o);
		Object[] a = (Object[]) java.lang.reflect.Array.newInstance(oArray.getClass().getComponentType(), v.size());
		v.copyInto(a);
		return a;
	}
	else{
		throw new RuntimeException("Error removing "+o.toString()+", not in object array");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/6/2005 2:44:51 AM)
 * @param viewport java.awt.Dimension
 * @param toScale java.awt.Dimension
 */
public static Dimension scaleToFitProportional(Dimension viewport, Dimension toScale) {
	double newWidth = viewport.getWidth();
	double newHeight = viewport.getWidth() / toScale.getWidth() * toScale.getHeight();
	if (newHeight > viewport.getHeight()) {
		newHeight = viewport.getHeight();
		newWidth = viewport.getHeight() / toScale.getHeight() * toScale.getWidth();
	}
	return new Dimension((int)newWidth, (int)newHeight);
}


/**
 * Comment
 */
public static void setCursorThroughout(Container container, Cursor cursor) {
	if (container==null){
		return;
	}
	Component[] components = container.getComponents();
	for (int i=0;i<components.length;i++) {
		components[i].setCursor(cursor);
		if (components[i] instanceof Container) {
			if (((Container)components[i]).getComponentCount() > 0) {
				BeanUtils.setCursorThroughout((Container)components[i], cursor);
			}
		}
	}
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String[]
 * @param array1 java.lang.String[]
 * @param array2 java.lang.String[]
 */
public static String[] stringArrayMerge(String[] array1, String[] array2) {
	if (array1 == null && array2 == null){
		return null;
	}
	if (array1 == null){
		return array2;
	}
	if (array2 == null){
		return array1;
	}
	java.util.Vector newVector = new java.util.Vector();
	for (int i=0;i<array1.length;i++){
		newVector.addElement(array1[i]);
	}
	for (int i=0;i<array2.length;i++){
		boolean found = false;
		for (int j=0;j<array1.length;j++){
			if (array1[j].equals(array2[i])){
				found = true;
			}	
		}
		if (!found){
			newVector.addElement(array2[i]);
		}
	}			
	String newArray[] = new String[newVector.size()];
	for (int i=0;i<newVector.size();i++){
		newArray[i] = (String)newVector.elementAt(i);
	}
	return newArray;		
}


/**
 * This method was created in VisualAge.
 * @return byte[]
 * @param obj cbit.sql.Cacheable
 */
public static byte[] toCompressedSerialized(Serializable cacheObj) throws java.io.IOException {
	byte[] bytes = toSerialized(cacheObj);
	
	long before = System.currentTimeMillis();
	
	byte[] objData = null;
	java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
	DeflaterOutputStream dos = new DeflaterOutputStream(bos);
	java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(dos);
	oos.writeObject(cacheObj);
	oos.flush();
	dos.close();
	bos.flush();
	objData = bos.toByteArray();
	oos.close();
	bos.close();

	long after = System.currentTimeMillis();
	System.out.println("BeanUtils.toSerialized, t="+(after-before)+" ms, ("+cacheObj+") ratio=" + objData.length + "/" + bytes.length);

	return objData;
}


/**
 * This method was created in VisualAge.
 * @return byte[]
 * @param obj cbit.sql.Cacheable
 */
public static byte[] toSerialized(Serializable cacheObj) throws java.io.IOException {
//	long before = System.currentTimeMillis();

	byte[] objData = null;
	java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
	java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos);
	oos.writeObject(cacheObj);
	oos.flush();
	objData = bos.toByteArray();
	oos.close();
	bos.close();

//	long after = System.currentTimeMillis();
//	System.out.println("BeanUtils.toSerialized, t="+(after-before)+" ms, ("+cacheObj+")");

	return objData;
}


/**
 * Insert the method's description here.
 * Creation date: (8/21/2001 12:27:24 AM)
 * @return boolean
 * @param a java.lang.Object
 * @param b java.lang.Object
 */
public static boolean triggersPropertyChangeEvent(Object a, Object b) {
	return (a == null || b == null || !a.equals(b));
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:33:11 AM)
 * @param out java.io.ObjectOutputStream
 * @exception java.io.IOException The exception description.
 */
public static byte[] uncompress(byte[] compressedBytes) throws java.io.IOException {	
	ByteArrayInputStream bis = new ByteArrayInputStream(compressedBytes);
	InflaterInputStream iis = new InflaterInputStream(bis);
	int temp;
	byte buf[] = new byte[65536];
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	while((temp = iis.read(buf,0,buf.length)) != -1){
		bos.write(buf,0,temp);
	}
	iis.close();
	bis.close();
	return bos.toByteArray();
}
}