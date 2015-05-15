package org.vcell;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import edu.uchc.connjur.wb.ExecutionTrace;

/**
 * attempt to load all .class files in jars in specific directories
 * @author GWeatherby
 */
public class LoadAll {

	/**
	 * attempt to load and log to java{VERSION}.txt
	 * example command line: d:/workspace/VCellTrunk/lib d:/workspace/VCellTrunk/testlib
	 * VM switches:  -Xmx10G -XX:+CMSClassUnloadingEnabled  -XX:MaxPermSize=2G
	 * @param args
	 */
	public static void main(String[] args) {
		String v = System.getProperty("java.version");
		try (PrintWriter pw = new PrintWriter(new FileWriter("java" + v + ".txt") ) ) {
			for (String source : args) {
				File dir = new File(source);
				if (dir.isDirectory()) {
					for (File j : dir.listFiles()) {
						if (j.getName().endsWith(".jar")) {
							try {
								load(j.getAbsolutePath(),pw);
							} catch (Throwable tbl) {
								System.err.println("Error reading " + j + ' ' + tbl.getMessage());
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * load jar, log Errors
	 * @param pathToJar not null
	 * @param log not null; Errors loading specific classes logged here
	 * @throws Throwable if can't open or read jar 
	 */
	private static void load(String pathToJar, PrintWriter log) throws Throwable {
		Objects.requireNonNull(pathToJar);
		Objects.requireNonNull(log);
		//from: http://stackoverflow.com/questions/11016092/how-to-load-classes-at-runtime-from-a-folder-or-jar
		try (JarFile jarFile = new JarFile(pathToJar)) {
			Enumeration<JarEntry> e = jarFile.entries();

			URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
			URLClassLoader cl = URLClassLoader.newInstance(urls);

			while (e.hasMoreElements()) {
				JarEntry je = (JarEntry) e.nextElement();
				if(je.isDirectory() || !je.getName().endsWith(".class")){
					continue;
				}
				// -6 because of .class
				String className = je.getName().substring(0,je.getName().length()-6);
				className = className.replace('/', '.');
				try {
					@SuppressWarnings("unused")
					Class<?> c = cl.loadClass(className);
				} catch (Throwable tbl) {
					String tname = ExecutionTrace.justClassName(tbl);
					log.println(pathToJar + ' ' + className + ' ' + tname + ' ' + tbl.getMessage());
				}
			}
		}
	}

}
