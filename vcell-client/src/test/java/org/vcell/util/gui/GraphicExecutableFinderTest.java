package org.vcell.util.gui;

import cbit.vcell.resource.ResourceUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.prefs.BackingStoreException;

@Disabled
@Tag("Fast")
public class GraphicExecutableFinderTest {
	public static String TEST_EXE  = "MovingBoundary";
	@Test
	public void testDialog( ) throws FileNotFoundException, BackingStoreException, InterruptedException {
		ExecutableFinderDialog gef = new ExecutableFinderDialog(new JFrame( ), "find the moving boundary executable, okay?");
		 File f = ResourceUtil.getExecutable(TEST_EXE, false);
		 System.out.println(f.getAbsolutePath());
	}
	
	@Test
	public void qt( ) {
		Component parentComponent = new JFrame( );
		String rcode = DialogUtils.showOKCancelWarningDialog(parentComponent, "title" ," Eureka!");
		System.out.println(rcode);
		
		
	}
}
