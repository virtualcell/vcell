package org.vcell.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

@Category(Fast.class)
public class BeanUtilsTest {
	
	private void eenie() {meenie();}
	
	private void meenie() {miney();}

	private void miney() {mo();}
	
	private void mo( ) {
		System.out.println(StackTraceUtils.getStackTrace());
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
