package cbit.vcell.resource;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

public class ResourceUtilTest {
	
	public static String TEST_DIR  = "d:/test";
	public static String TEST_EXE  = "MovingBoundary";
	
	@Test
	public void findTest() throws FileNotFoundException {
		 File f = ResourceUtil.getExecutable(TEST_EXE, false,null);
		 System.out.println(f.getAbsolutePath());
	}
	

}
