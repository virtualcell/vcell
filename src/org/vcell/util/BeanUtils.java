/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */


package org.vcell.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.KeyStroke;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCellSoftwareVersion;

import cbit.util.xml.XmlUtil;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.util.AmplistorUtils;

import com.ibm.icu.math.BigDecimal;

import edu.uchc.connjur.wb.ExecutionTrace;
/**
 * Insert the type's description here.
 * Creation date: (8/18/2000 2:29:31 AM)
 * @author:
 */
public final class BeanUtils {
	private static boolean bDebugMode = false;
	private static UserLoginInfo loginInfo = null;
	public static void setDebug(boolean isDebug) {
		bDebugMode = isDebug;
	}

	public static void setLoginInfo(UserLoginInfo loginInfo) {
		BeanUtils.loginInfo = loginInfo;
	}


	/**
	 * newline used in email of Content-Type: text/plain
	 */
	public static final String PLAINTEXT_EMAIL_NEWLINE = "\r\n";

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

	public static <T> T[] addElement(T[] array, T element) {
		@SuppressWarnings("unchecked")
		T[] arrayNew =
			(T[]) Array.newInstance(array.getClass().getComponentType(), array.length + 1);
		System.arraycopy(array, 0, arrayNew, 0, array.length);
		arrayNew[array.length] = element;
		return arrayNew;
	}

	public static <T> T[] addElements(T[] array1, T[] array2) {
		@SuppressWarnings("unchecked")
		T[] array =
			(T[]) Array.newInstance(array1.getClass().getComponentType(), array1.length + array2.length);
		System.arraycopy(array1, 0, array, 0, array1.length);
		System.arraycopy(array2, 0, array, array1.length, array2.length);
		return array;
	}

	public static <T> boolean arrayContains(T[] objects, T object) {
		if (object == null || objects == null) {
			return false;
		}
		return Arrays.asList(objects).contains(object);
	}

	public static <T> boolean arrayEquals(T[] a1, T[] a2) {
		if (a1 == a2) {
			return true;
		}
		if (a1 != null && a2 != null) {
			return Arrays.equals(a1, a2);
		}
		return false;
	}

	public static void attemptMaximize(Frame frame) {
		// not supported on all platforms
		// not supported by JRE's 1.3 and earlier
		// when not supported, frame will stay unchanged
		Class<? extends Frame> c = frame.getClass();
		Method[] ms = c.getMethods();
		Method m = null;
		for (int i = 0; i < ms.length; i++){
			if (ms[i].getName().equals("setExtendedState")) {
				m = ms[i];
				break;
			}
		}
		Field[] fs = c.getFields();
		Field f = null;
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

	public static void centerOnComponent(Window window, Component reference) {
		if (window == null) {
			return;
		}
		if (reference == null) {
			System.out.println("BeanUtils.centerOnComponent, reference=null");
			Thread.dumpStack();
			window.setLocation(0, 0);
			return;
		}
		try{
			Point pR = reference.getLocationOnScreen();
			pR.x += Math.max((reference.getWidth() - window.getWidth()) / 2, 0);
			pR.y += Math.max((reference.getHeight() - window.getHeight()) / 2, 0);
			window.setLocation(pR);
		}catch(Exception e){
			centerOnScreen(window);
		}
	}

	public static void centerOnScreen(Window window) {
		if (window != null) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final Rectangle maxBounds = ge.getMaximumWindowBounds();
			Dimension screenSize = maxBounds.getSize();
			Dimension size = window.getSize();
			if (size.height > screenSize.height)
				size.height = screenSize.height;
			if (size.width > screenSize.width)
				size.width = screenSize.width;
			window.setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);
		}
	}

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

	public static Serializable cloneSerializable(Serializable obj) throws ClassNotFoundException, java.io.IOException {

		//	System.out.println("BeanUtils.cloneSerializable, ("+obj+")");

		Serializable clone = fromSerialized(toSerialized(obj));
		return clone;
	}

	public static byte[] compress(byte[] bytes) throws java.io.IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DeflaterOutputStream dos = new DeflaterOutputStream(bos);
		dos.write(bytes,0,bytes.length);
		dos.close();
		byte[] compressed = bos.toByteArray();
		bos.close();
		return compressed;
	}

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
	 * @deprecated should not have to do this
	 * @param disposableChild
	 */
	public static void disposeParentWindow(Component disposableChild){
		if(BeanUtils.findTypeParentOfComponent(disposableChild, JDialog.class) != null){
			((JDialog)BeanUtils.findTypeParentOfComponent(disposableChild, JDialog.class)).dispose();
		}else if(BeanUtils.findTypeParentOfComponent(disposableChild, JFrame.class) != null){
			((JFrame)BeanUtils.findTypeParentOfComponent(disposableChild, JFrame.class)).dispose();
		}else{
			throw new IllegalArgumentException(BeanUtils.class.getName()+".dispose(...) only handles JFrame parents");
		}
	}

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
	 * return Container of specified type that is either the input component, or a AWT parent
	 * of the input component
	 * @param component, may be null
	 * @param parentType not null
	 * @return component, ancestor of component, or null
	 */
	public static Container findTypeParentOfComponent(Component component,Class<?> parentType) {
		Container p = component == null || component instanceof Container ? (Container) component : component.getParent();
		for (; p != null; p = p.getParent()) {
			if(parentType.isAssignableFrom(p.getClass())) {
				return p;
			}
		}
		return null;
	}

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

	public static String forceStringSize(String s, int size,String padChar,boolean bPrependPad) {
		//
		if(padChar == null || padChar.length()==0){
			padChar = " ";
		}
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

	public static Serializable fromCompressedSerialized(byte[] objData) throws ClassNotFoundException, java.io.IOException {
		//	long before = System.currentTimeMillis();

		java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(objData);
		InflaterInputStream iis = new InflaterInputStream(bis);
		java.io.ObjectInputStream ois = new java.io.ObjectInputStream(iis);
		Serializable cacheClone = (Serializable) ois.readObject();
		ois.close();
		bis.close();

		//	long after = System.currentTimeMillis();
		//	System.out.println("BeanUtils.fromCompressedSerialized, t="+(after-before)+" ms, ("+cacheClone+")");

		return cacheClone;
	}

	public static Serializable fromSerialized(byte[] objData) throws ClassNotFoundException, IOException {
		//	long before = System.currentTimeMillis();

		ByteArrayInputStream bis = new ByteArrayInputStream(objData);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Serializable cacheClone = (Serializable) ois.readObject();
		ois.close();
		bis.close();

		//	long after = System.currentTimeMillis();
		//	System.out.println("BeanUtils.fromSerialized, t="+(after-before)+" ms, ("+cacheClone+")");

		return cacheClone;
	}

	public static <T> T[] getArray(Enumeration<? extends T> enumeration, Class<T> elementType) {
		List<T> list = new ArrayList<T>();
		while (enumeration.hasMoreElements()){
			list.add(enumeration.nextElement());
		}
		@SuppressWarnings("unchecked")
		T[] array = list.toArray((T[])Array.newInstance(elementType, list.size()));
		return array;
	}

	/**
	 * @return list.toArray()
	 */
	public static <T> T[] getArray(List<?> list, Class<T> elementType) {
		@SuppressWarnings("unchecked")
		T[] array = list.toArray((T[])Array.newInstance(elementType, list.size()));
		return array;
	}

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


	public static <T> T[] removeElement(T[] array, T o) {
		int index = Arrays.asList(array).indexOf(o);
		if(index >= 0) {
			int lengthNew = array.length - 1;
			@SuppressWarnings("unchecked")
			T[] arrayNew = (T[]) Array.newInstance(array.getClass().getComponentType(), lengthNew);
			if(index >= 0) {
				if(index > 0) {
					System.arraycopy(array, 0, arrayNew, 0, index);
				}
				if(index < array.length - 1) {
					System.arraycopy(array, index + 1, arrayNew, index, lengthNew - index);
				}
			}
			return arrayNew;
		}
		else{
			throw new RuntimeException("Error removing "+o.toString()+", not in object array");
		}
	}

	public static Dimension scaleToFitProportional(Dimension viewport, Dimension toScale) {
		double newWidth = viewport.getWidth();
		double newHeight = viewport.getWidth() / toScale.getWidth() * toScale.getHeight();
		if (newHeight > viewport.getHeight()) {
			newHeight = viewport.getHeight();
			newWidth = viewport.getHeight() / toScale.getHeight() * toScale.getWidth();
		}
		return new Dimension((int)newWidth, (int)newHeight);
	}

	public static void setCursorThroughout(final Container container, final Cursor cursor) {
		if (container==null){
			return;
		}
		container.setCursor(cursor);
//		Component[] components = container.getComponents();
//		for (int i=0;i<components.length;i++) {
//			components[i].setCursor(cursor);
//			if(components[i] instanceof JRootPane){
//				BeanUtils.setCursorThroughout(((JRootPane)components[i]).getContentPane(), cursor);
//			}else if (components[i] instanceof Container) {
//				if (((Container)components[i]).getComponentCount() > 0) {
//					BeanUtils.setCursorThroughout((Container)components[i], cursor);
//				}
//			}
//		}
	}

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
		Vector<String> newVector = new Vector<String>();
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
			newArray[i] = newVector.elementAt(i);
		}
		return newArray;
	}

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

	public static boolean triggersPropertyChangeEvent(Object a, Object b) {
		return (a == null || b == null || !a.equals(b));
	}

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

	public static String getStackTrace(Throwable throwable) {
		StringWriter out = new StringWriter();
		PrintWriter pw = new PrintWriter(out);
		Throwable t = throwable;
		while (t != null) {
			t.printStackTrace(pw);
			t = t.getCause( );
		}
		pw.close();
		return out.getBuffer().toString();
	}

	/**
	 * @return String for current stack trace
	 */
	public static String getStackTrace() {
		StackTraceElement[] stackTraceArray = Thread.currentThread().getStackTrace();
		StringBuilder sb = new StringBuilder();
		//0 and 1 are "getStackTrace" -- start with callers invocation
		for (int i = 2; i < stackTraceArray.length; i++) {
			sb.append(stackTraceArray[i]);
			sb.append('\n');
		}
		return sb.toString();
	}
	/**
	 * recursive assemble exception message
	 * @param throwable
	 * @return {@link Throwable#getMessage()}, recursively
	 */
	public static String getMessageRecursive(Throwable throwable) {
		String rval = throwable.getMessage();
		Throwable cause = throwable.getCause();
		while (cause != null) {
			rval += " caused by " + cause.getMessage();
			cause = cause.getCause();
		}

		return rval;
	}


	public static void sendSMTP(String smtpHost, int smtpPort,String from, String to,String subject, String content)
	throws AddressException, MessagingException {

		// Create a mail session
		java.util.Properties props = new java.util.Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", ""+smtpPort);
		Session session = Session.getDefaultInstance(props, null);

		// Construct the message
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(from));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		msg.setSubject(subject);
		msg.setText(content);

		// Send the message
		Transport.send(msg);
	}

	public static void sendErrorReport(Throwable exception) throws RuntimeException {
		sendErrorReport(exception,null);
	}
	/**
	 * send error report
	 * @param exception
	 * @param supplement extra information to add, may be null
	 * @throws RuntimeException
	 */
	public static void sendErrorReport(Throwable exception, String supplement) throws RuntimeException {
		if (exception == null) {
			throw new RuntimeException("Send Error Report, exception is null");
		}
		String smtpHost = PropertyLoader.getProperty(PropertyLoader.vcellSMTPHostName, null);
		if (smtpHost == null) {
			return;
		}
		String smtpPort = PropertyLoader.getProperty(PropertyLoader.vcellSMTPPort, null);
		if (smtpPort == null) {
			return;
		}
		String from = "VCell";
		String to = PropertyLoader.getProperty(PropertyLoader.vcellSMTPEmailAddress, null);
		if (to == null) {
			return;
		}
		String subject = "VCell Error Report from " + PropertyLoader.getRequiredProperty(PropertyLoader.vcellSoftwareVersion);
		String content = BeanUtils.getStackTrace(exception)+"\n";
		String platform = "Running under Java major version: ONE point "+ ResourceUtil.getJavaVersion().toString()+".  Specifically: Java "+(System.getProperty("java.version"))+
			", published by "+(System.getProperty("java.vendor"))+", on the "+ (System.getProperty("os.arch"))+" architecture running version "+(System.getProperty("os.version"))+
			" of the "+(System.getProperty("os.name"))+" operating system";
		content = content + platform;
		if (supplement != null) {
			content += PLAINTEXT_EMAIL_NEWLINE + supplement;
		}

		try {
			BeanUtils.sendSMTP(smtpHost, Integer.parseInt(smtpPort), from, to, subject, content);
		} catch (AddressException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	public static final KeyStroke CLOSE_WINDOW_KEY_STROKE;
	static { //allow initialization in headless environment
		KeyStroke ks;
		try {
			ks  = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		} catch (HeadlessException he) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.CTRL_DOWN_MASK);
		}
		CLOSE_WINDOW_KEY_STROKE = ks;
	}
	public static void addCloseWindowKeyboardAction(JComponent jComponent){
		@SuppressWarnings("serial")
		Action winCloseAction = new AbstractAction() {
			@Override
			public synchronized void actionPerformed(ActionEvent e) {
				Window window = null;
				if(e.getSource() != null){
					if(e.getSource() instanceof Window){
						window = (Window)e.getSource();
					}else{
						window = (Window)BeanUtils.findTypeParentOfComponent((Component)e.getSource(), Window.class);
					}
				}
				if(window != null){
					window.dispatchEvent(new WindowEvent(window,java.awt.event.WindowEvent.WINDOW_CLOSING));
				}
			}
		};

		final String winCloseInputMapAction = "winCloseAction";
		InputMap inputMap = jComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		if(inputMap.get(CLOSE_WINDOW_KEY_STROKE) == null){
			inputMap.put(CLOSE_WINDOW_KEY_STROKE, winCloseInputMapAction);
			jComponent.getActionMap().put(winCloseInputMapAction, winCloseAction);
		}
	}

	public static void setMessage(ClientTaskStatusSupport clientTaskStatusSupport,String message){
		//convenience method in case clientTaskStatusSupport is null
		if(clientTaskStatusSupport != null){
			clientTaskStatusSupport.setMessage(message);
		}
	}
	private static enum FLAG_STATE {START,FINISHED,INTERRUPTED,FAILED}

	public static String readBytesFromFile(File file, ClientTaskStatusSupport clientTaskStatusSupport) throws IOException {
		StringBuffer stringBuffer = new StringBuffer();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line = new String();
			while((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line + "\n");
				if (clientTaskStatusSupport!=null && clientTaskStatusSupport.isInterrupted()){
					break;
				}
			}
		}finally{
			if (bufferedReader != null){
				bufferedReader.close();
			}
		}
		return stringBuffer.toString();
	}

	public static String downloadBytes(final URL url,final ClientTaskStatusSupport clientTaskStatusSupport){
		final ChannelFuture[] connectFuture = new ChannelFuture[1];
		final ChannelFuture[] closeFuture = new ChannelFuture[1];
		final Exception[] exception = new Exception[1];
		final FLAG_STATE[] bFlag = new FLAG_STATE[] {FLAG_STATE.START};
		final HttpResponseHandler responseHandler = new HttpResponseHandler(clientTaskStatusSupport,url.getHost());
		final Thread readBytesThread = new Thread(new Runnable() {
			ClientBootstrap bootstrap = null;

			public void run() {
				try {
					bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool()));

					// Set up the event pipeline factory.
					bootstrap.setPipelineFactory(new HttpClientPipelineFactory(responseHandler));
//					int connectionTimeout = 1000 * 60 * 5;
//					bootstrap.setOption("connectTimeoutMillis", connectionTimeout);

					// Start the connection attempt.
					int port = url.getPort();
					if (port <= 0) {
						port = 80;
					}

					String host = url.getHost();
					connectFuture[0] = bootstrap.connect(new InetSocketAddress(host, port));

					// Wait until the connection attempt succeeds or fails.
					connectFuture[0].awaitUninterruptibly();

					// Now we are sure the future is completed.
					// assert future.isDone();

					if (connectFuture[0].isCancelled()) {
						bFlag[0] = FLAG_STATE.INTERRUPTED;
						return;
					} else if (!connectFuture[0].isSuccess()) {
						connectFuture[0].getCause().printStackTrace();
						if (connectFuture[0].getCause() instanceof Exception){
							throw (Exception)connectFuture[0].getCause();
						}else{
							throw new RuntimeException(exceptionMessage(connectFuture[0].getCause()));
						}
					} else {
						// Connection established successfully
					}

					// Prepare the HTTP request.
					HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, url.toURI().toASCIIString());
					request.setHeader(HttpHeaders.Names.HOST, host);
					request.setHeader(HttpHeaders.Names.CONNECTION,	HttpHeaders.Values.CLOSE);
					request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);

					Channel channel = connectFuture[0].getChannel();

					// Send the HTTP request.
					channel.write(request);

					// Wait for the server to close the connection.
					closeFuture[0] = channel.getCloseFuture();

					closeFuture[0].awaitUninterruptibly();

					if (closeFuture[0].isCancelled()) {
						bFlag[0] = FLAG_STATE.INTERRUPTED;
						return;
					} else if (!closeFuture[0].isSuccess()) {
						closeFuture[0].getCause().printStackTrace();
						if (closeFuture[0].getCause() instanceof Exception){
							throw (Exception)closeFuture[0].getCause();
						}else{
							throw new RuntimeException(exceptionMessage(closeFuture[0].getCause()));
						}
					} else {
						// Connection established successfully
					}

					bFlag[0] = FLAG_STATE.FINISHED;

				} catch (Exception e) {
					e.printStackTrace();
					bFlag[0] = FLAG_STATE.FAILED;
					exception[0] = new RuntimeException("contacting outside server " + url.toString() + " failed.\n" + exceptionMessage(e));
				} finally {
					if (bootstrap != null) {
						// Shut down executor threads to exit.
						bootstrap.releaseExternalResources();
					}
				}
			}
		});
		readBytesThread.start();

		//Monitor content
		long maximumElapsedTime_ms = 1000*60*2;
		long startTime_ms = System.currentTimeMillis();
		while(true){
			try {
				Thread.sleep(100);
			} catch (Exception e) {
//				throw UserCancelException.CANCEL_GENERIC;
			}
			long elapsedTime_ms = System.currentTimeMillis()-startTime_ms;
			if(clientTaskStatusSupport != null && clientTaskStatusSupport.isInterrupted()){
				bFlag[0] = FLAG_STATE.INTERRUPTED;
				if (connectFuture[0]!=null){
					connectFuture[0].cancel();
				}
				if (closeFuture[0]!=null){
					closeFuture[0].cancel();
				}
				throw UserCancelException.CANCEL_GENERIC;
			}else if(elapsedTime_ms > maximumElapsedTime_ms){
				bFlag[0] = FLAG_STATE.INTERRUPTED;
				if (connectFuture[0]!=null){
					connectFuture[0].cancel();
				}
				if (closeFuture[0]!=null){
					closeFuture[0].cancel();
				}
				readBytesThread.stop();
				throw new RuntimeException("Download timed out after "+(maximumElapsedTime_ms/1000)+" seconds");
			}
			if(bFlag[0] == FLAG_STATE.FINISHED){//finished normally
				break;
			}
			if(bFlag[0] == FLAG_STATE.FAILED){//finished error
				if (connectFuture[0]!=null){
					connectFuture[0].cancel();
				}
				if (closeFuture[0]!=null){
					closeFuture[0].cancel();
				}
				if(exception[0] instanceof RuntimeException){
					throw (RuntimeException)exception[0];
				}
				throw new RuntimeException(exception[0]);
			}
		}
		return responseHandler.getResponseContent().toString();
	}

	public static String exceptionMessage(Throwable e){
		if (e.getMessage()!=null){
			return e.getMessage();
		}

		if (e.getCause()!=null){
			if (e.getCause().getMessage()!=null){
				return e.getCause().getMessage();
			}else{
				return e.getCause().getClass().getName();
			}
		}else{
			return e.getClass().getName();
		}
	}

	public static org.jdom.Document getJDOMDocument(URL url,final ClientTaskStatusSupport clientTaskStatusSupport){
		//parse content
		final String contentString = downloadBytes(url, clientTaskStatusSupport);
		org.jdom.Document jdomDocument = XmlUtil.stringToXML(contentString, null);
		return jdomDocument;
	}

	public static final String SQL_ESCAPE_CHARACTER = "/";
	public static String convertToSQLSearchString(String searchString){
		if(searchString == null || searchString.length() == 0){return searchString;}
		String convertedLikeString = "";
		StringBuffer sb = new StringBuffer(searchString);
		for(int i=0;i<sb.length();i+= 1){
			if(sb.charAt(i) == '*'){
				convertedLikeString+= "%";//sql wildcard
			}else if(sb.charAt(i) == '%'){
				convertedLikeString+= SQL_ESCAPE_CHARACTER+"%";// "%" special in sql 'like'
			}else if(sb.charAt(i) == '_'){
				convertedLikeString+= SQL_ESCAPE_CHARACTER+"_";// "_" special in sql 'like'
			}else {
				convertedLikeString+= sb.charAt(i);
			}
		}
		if(convertedLikeString.indexOf("%") == -1 && convertedLikeString.indexOf("_") == -1){
			convertedLikeString = "%"+convertedLikeString+"%";
		}
		//The character "%" matches any string of zero or more characters except null.
		//The character "_" matches any single character.
		//A wildcard character is treated as a literal if preceded by the character designated as the escape character.
		//Default ESCAPE character for VCell = '/' defined in DictionaryDbDriver.getDatabaseSpecies
		return convertedLikeString;
	}

	/**
	 * send message to Virtual Cell server, if not in debug mode
	 * @param userLoginInfo; if null, user previously set info if available
	 * @param message
	 */
	public static void sendRemoteLogMessage(UserLoginInfo argUserLoginInfo,final String message){
		final UserLoginInfo userLoginInfo = argUserLoginInfo != null ? argUserLoginInfo : BeanUtils.loginInfo;
		if (!bDebugMode && userLoginInfo != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						final String formattedMessage = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+"\n"+
							"vers='"+VCellSoftwareVersion.fromSystemProperty().getSoftwareVersionString()+"' java='"+userLoginInfo.getJava_version()+"' os='"+userLoginInfo.getOs_name()+"' osvers='"+userLoginInfo.getOs_version()+"' arch='"+userLoginInfo.getOs_arch()+"'\n"+
							message;
						AmplistorUtils.uploadString(AmplistorUtils.DEFAULT_PROXY_AMPLI_VCELL_LOGS_URL+userLoginInfo.getUserName()+"_"+System.currentTimeMillis(), null, formattedMessage);
					}catch(Exception e){
						e.printStackTrace();
						System.err.println("Failed to upload message to Amplistor "+AmplistorUtils.DEFAULT_PROXY_AMPLI_VCELL_LOGS_URL+" : "+message);
						//ignore
					}
				}
			}).start();
		}
		else {
			System.err.println("Remote log message: " + message);
		}
	}

	/**
	 * @param clzz
	 * @return class name without package info
	 * @throws NullPointerException if clzz null
	 */
	public static String leafName(Class<?> clzz) {
		String cname = clzz.getName();
		final int ldot = cname.lastIndexOf('.');
		if (ldot != - 1) {
			cname = cname.substring(ldot + 1);
		}
		return cname;
	}

	/**
	 * customize error message for null pointers
	 * @param clzz type of obj
	 * @param obj to check for null
	 * @return obj (not null)
	 * @throws NullPointerException if obj is null
	 */
	public static <T> T notNull(Class<T> clzz, T obj) {
		if (obj != null) {
			return obj;
		}
		throw new NullPointerException("unexpected " + clzz.getName() + " null pointer");
	}

	/**
	 * see if numerator / divisor is integer using decimal (base 10) arithmetic instead of
	 * floating point (base 2)
	 * @param numerator
	 * @param divisor
	 * @return true if decimal numerator / decimal divisor is integer
	 */
	public static boolean isIntegerMultiple(double numerator, double divisor) {
		BigDecimal n = BigDecimal.valueOf(numerator);
		BigDecimal d = BigDecimal.valueOf(divisor);
		BigDecimal remainder = n.remainder(d);
		return remainder.compareTo(BigDecimal.ZERO) == 0;
	}

	/**
	 * convert ordinal to Enum
	 * @param clzz may not be null
	 * @param ordinal
	 * @return e with e.ordinal( ) == ordinal
	 * @throws IllegalArgumentException if ordinal out of range
	 */
	public static <E extends Enum<E> > E lookupEnum(Class<E> clzz, int ordinal) {
		EnumSet<E> set = EnumSet.allOf(clzz);
		if (ordinal < set.size()) {
			Iterator<E> iter = set.iterator();
			for (int i = 0; i < ordinal; i++) {
				iter.next();
			}
			E rval = iter.next();
			assert(rval.ordinal() == ordinal);
			return rval;
		}
		throw new IllegalArgumentException("Invalid value " + ordinal + " for " + ExecutionTrace.justClassName(clzz) + ", must be < " + set.size());
	}

	/**
	 * downcast to object or return null
	 * @param clzz return type, not null
	 * @param obj may be null
	 * @return obj as T or null if obj is null or not of type T
	 */
	public static <T> T downcast(Class<T> clzz, Object obj) {
		if (obj != null && clzz.isAssignableFrom(obj.getClass())) {
			@SuppressWarnings("unchecked")
			T rval = (T) obj;
			return rval;
		}
		return null;
	}

	public interface CastInfo <T> {
		/**
		 * was cast successful?
		 */
		boolean isGood( );
		/**
		 * return type converted object
		 * @return non null pointer
		 * @throws ProgrammingException if {@link #isGood()} returns false
		 */
		T get( );
		/**
		 * @return name of desired class if {@link #isGood()} returns false
		 */
		String requiredName( );
		/**
		 * @return name of provided object if {@link #isGood()} returns false
		 */
		String actualName( );

		/**
		 * @return message explaining cast
		 */
		String castMessage( );
	}

	/**
	 * attempt to class object to specified type
	 * @param clzz desired type, not null
	 * @param obj object to cast; if null returns !isGood( ) CastInfo<T>
	 * @return CastInfo<T> object describing results
	 */
	public static <T> CastInfo<T> attemptCast(Class<T> clzz, Object obj) {
		final String rname = clzz.getName();
		if (obj != null) {
			final String aname = obj.getClass().getName();
			T result = downcast(clzz, obj);
			if (result == null) {
				return new FailInfo<>(rname, aname);
			}
			return new SucceedInfo<>(rname,aname,result);
		}
		return new FailInfo<>(rname, "null");
	}

	private abstract static class CiBase<T> implements CastInfo<T> {
		final String rname;
		final String aname;
		protected CiBase(String rname, String aname) {
			this.rname = rname;
			this.aname = aname;
		}
		public String requiredName() { return rname; }
		public String actualName() { return aname; }
		public String castMessage( ) {
			return "cast from " + aname + " to " + rname;
		}
	}
	private static class FailInfo<T> extends CiBase<T> {

		FailInfo(String rname, String aname) {
			super(rname,aname);
		}
		public boolean isGood() {
			return false;
		}
		public T get() {
			String msg = "Programming exception, " + castMessage() + " failed";
			throw new ProgrammingException(msg);
		}
	}

	private static class SucceedInfo<T> extends CiBase<T> {
		final T obj;

		SucceedInfo(String rname,String aname, T obj) {
			super(rname,aname);
			this.obj = obj;
		}
		public boolean isGood() {
			return true;
		}
		public T get() {
			return obj;
		}
	}

	/**
	 * filter subtype out of a collection
	 * @param clzz non null
	 * @param coll non null
	 * @return list containing elements from coll of type clzz
	 */
	public static <T>  List<T> filterCollection(Class<T> clzz,Collection<?> coll) {
		Objects.requireNonNull(clzz);
		Objects.requireNonNull(coll);
		return coll.stream( )
		.filter(clzz::isInstance)
		.map(clzz::cast)
		.collect(Collectors.toList());
	}


}
