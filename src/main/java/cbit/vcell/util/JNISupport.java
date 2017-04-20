package cbit.vcell.util;

public class JNISupport {
	private JNISupport( ){}
	
	public static void verifyPackage(Class<?> clzz, String packageName) {
		Package p = clzz.getPackage();
		if (!p.getName().equals(packageName)) {
			throw new RuntimeException(
					"JNI configuration error, class " + clzz.getName() + 
					" not in package expected by JNI library " + packageName);
		}
	}
}
