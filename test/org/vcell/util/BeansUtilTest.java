package org.vcell.util;

import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Test;

public class BeansUtilTest {
	
	@SuppressWarnings("null")
	//@Test
	public void tryIt( ) {
		try {
			String n = null;
			System.out.print(n.length());
		
		} catch(NullPointerException npe) {
			npe.printStackTrace();
		}
		
		try {
			String n = null;
			System.out.print(BeanUtils.notNull(String.class, n)); 
		
		} catch(NullPointerException npe) {
			npe.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * expected behavior of {@link BeanUtils#findTypeParentOfComponent(Component, Class)} 
	 * @param method not null
	 */
	private void aTest(BiFunction<Class<?>, Component, Container> method) {
		JFrame jf = new JFrame( );
		JPanel jp = new JPanel( );
		jf.add(jp);
		JButton btn = new JButton();
		jf.add(btn);
		assertTrue(method.apply(Frame.class, jf) == jf);
		assertTrue(method.apply(Frame.class, jp) == jf);
		assertTrue(method.apply(Window.class, jp) == jf);
		assertTrue(method.apply(JDialog.class, jp) == null);
		assertTrue(method.apply(Frame.class, null) == null);
		assertTrue(method.apply(Frame.class, btn) == jf);
		assertTrue(method.apply(Window.class, btn) == jf);
		assertTrue(method.apply(JDialog.class, btn) == null);
	}
	
	
	/**
	 * ensure  {@link BeanUtils#findTypeParentOfComponent(Component, Class)} has expected behavior
	 */
	@Test
	public void ancestorTest( ) {
		BiFunction<Class<?>, Component, Container> buMethod =  
				(clzz, cmpt) -> { return BeanUtils.findTypeParentOfComponent(cmpt, clzz); };
				aTest(buMethod);
	}

	@Test
	public void lookupTest( ) {
		java.util.concurrent.TimeUnit tu = BeanUtils.lookupEnum(TimeUnit.class, 3);
		System.out.println(tu);
	}
	
	@Test
	public void downcast( ) {
		Integer i = 3;
		Number n = i;
		Integer back = BeanUtils.downcast(Integer.class,n);
		Double fail =  BeanUtils.downcast(Double.class,n);
		assertTrue(back == i);
		assertTrue(fail == null);
		Double notThere =  BeanUtils.downcast(Double.class,null);
		assertTrue(notThere == null);
	}
	
	private void eenie( ) {
		meenie( );
	}
	
	private void meenie( ) {
		mo( );
		
	}
	
	private void mo( ) {
		System.out.println(BeanUtils.getStackTrace());
	}
	
	@Test
	public void stackTrace( ) {
		eenie( );
	}

}
