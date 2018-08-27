package org.vcell.util.gui;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.prefs.BackingStoreException;

import javax.swing.JFrame;

import org.junit.Ignore;

import cbit.vcell.resource.ResourceUtil;

@Ignore
public class GraphicExecutableFinderTest {
	public static String TEST_EXE  = "MovingBoundary";
	//@Test
	public void testDialog( ) throws FileNotFoundException, BackingStoreException, InterruptedException {
		ExecutableFinderDialog gef = new ExecutableFinderDialog(new JFrame( ), "find the moving boundary executable, okay?");
		 File f = ResourceUtil.getExecutable(TEST_EXE, false);
		 System.out.println(f.getAbsolutePath());
	}
	
	//@Test
	public void qt( ) {
		Component parentComponent = new JFrame( );
		String rcode = DialogUtils.showOKCancelWarningDialog(parentComponent, "title" ," Eureka!");
		System.out.println(rcode);
		
		
	}
}
