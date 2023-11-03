package org.vcell.util;

import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

@Category(Fast.class)
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

	@Test
	public void lookupTest( ) {
		java.util.concurrent.TimeUnit tu = BeanUtils.lookupEnum(TimeUnit.class, 3);
		Assert.assertEquals(tu.toString(),"SECONDS");
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
	
	@Ignore
	@Test
	public void stackTrace( ) {
		eenie( );
	}
	
	public void downloadString(ClientTaskStatusSupport taskSupport ) throws MalformedURLException {
		URL url = new URL("http://code3.cam.uchc.edu/slowdownload.php");
		System.out.println("starting download ...");
		String out = BeanUtils.downloadBytes(url, taskSupport);
		System.out.println(out);
	}
	
	@Ignore
	@Test
	public void downloadWatch( ) throws MalformedURLException {
		downloadString(new TaskSupport( ));
	}
	
	@Ignore
	@Test
	public void downloadSilent( ) throws MalformedURLException {
		downloadString(new SilentTaskSupport( ));
	}
	
	@Ignore
	@Test
	public void downloadNull( ) throws MalformedURLException {
		downloadString(null);
	}
	
	private static class SilentTaskSupport implements ClientTaskStatusSupport {

		@Override
		public void setMessage(String message) { }

		@Override
		public void setProgress(int progress) { }

		@Override
		public int getProgress() {
			return 0;
		}

		@Override
		public boolean isInterrupted() {
			return false;
		}

		@Override public void addProgressDialogListener(ProgressDialogListener progressDialogListener) { }
		
	}
	private static class TaskSupport implements ClientTaskStatusSupport {

		private int progress;

		@Override
		public void setMessage(String message) {
			System.out.println("message " + message);
			
			
		}

		@Override
		public void setProgress(int progress) {
			this.progress = progress;
			System.out.println("progress " + progress);
			
		}

		@Override
		public int getProgress() {
			return progress;
		}

		@Override
		public boolean isInterrupted() {
			return false;
		}

		@Override
		public void addProgressDialogListener(ProgressDialogListener progressDialogListener) {
			System.out.println("adpdl");
		}
	}

}
