package org.vcell.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

import static org.junit.Assert.*;

@Category(Fast.class)
public class BeanUtilsTest {
	
	@Test
	public void downcast( ) {
		Integer i = 3;
		Number n = i;
		Integer back = CastingUtils.downcast(Integer.class,n);
		Double fail =  CastingUtils.downcast(Double.class,n);
        assertSame(back, i);
        assertNull(fail);
		Double notThere =  CastingUtils.downcast(Double.class,null);
        assertNull(notThere);
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
