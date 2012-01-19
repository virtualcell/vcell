package cbit.vcell.VirtualMicroscopy;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.jar.Manifest;

public class PluginClassLoader extends URLClassLoader {
	

    public PluginClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}

	public PluginClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public PluginClassLoader(URL[] urls) {
		super(urls);
	}

//	@Override
//	protected void addURL(URL url) {
//		System.out.println("PluginClassLoader.addURL("+url+")");
//		super.addURL(url);
//	}
//
//	@Override
//	protected Package definePackage(String name, Manifest man, URL url)	throws IllegalArgumentException {
//		System.out.println("PluginClassLoader.definePackage("+name+","+man+","+url+")");
//		return super.definePackage(name, man, url);
//	}
//
//	@Override
//	protected Class<?> findClass(String name) throws ClassNotFoundException {
//		System.out.println("PluginClassLoader.findClass("+name+")");
//		return super.findClass(name);
//	}
//
//	@Override
//	public URL findResource(String name) {
//		System.out.println("PluginClassLoader.findResource("+name+")");
//		return super.findResource(name);
//	}
//
//	@Override
//	public Enumeration<URL> findResources(String name) throws IOException {
//		System.out.println("PluginClassLoader.findResources("+name+")");
//		return super.findResources(name);
//	}
//
//	@Override
//	protected PermissionCollection getPermissions(CodeSource codesource) {
//		System.out.println("PluginClassLoader.getPermissions("+codesource+")");
//		return super.getPermissions(codesource);
//	}
//
//	@Override
//	public URL[] getURLs() {
//		System.out.println("PluginClassLoader.getURLs()");
//		return super.getURLs();
//	}
//
//	@Override
//	public synchronized void clearAssertionStatus() {
//		System.out.println("PluginClassLoader.clearAssertionStatus()");
//		super.clearAssertionStatus();
//	}
//
//	@Override
//	protected Package definePackage(String name, String specTitle,
//			String specVersion, String specVendor, String implTitle,
//			String implVersion, String implVendor, URL sealBase)
//			throws IllegalArgumentException {
//		System.out.println("PluginClassLoader.definePackage("+name+","+specTitle+","+specVersion+","+specVendor+","+implTitle+","+implVersion+","+implVendor+","+sealBase+")");
//		return super.definePackage(name, specTitle, specVersion, specVendor, implTitle,
//				implVersion, implVendor, sealBase);
//	}
//
//	@Override
//	protected String findLibrary(String libname) {
//		System.out.println("PluginClassLoader.findLibrary("+libname+")");
//		return super.findLibrary(libname);
//	}
//
//	@Override
//	protected Package getPackage(String name) {
//		System.out.println("PluginClassLoader.getPackage("+name+")");
//		return super.getPackage(name);
//	}
//
//	@Override
//	protected Package[] getPackages() {
//		System.out.println("PluginClassLoader.getPackages()");
//		return super.getPackages();
//	}
//
//	@Override
//	public URL getResource(String name) {
//		System.out.println("PluginClassLoader.getResource("+name+")");
//		return super.getResource(name);
//	}
//
//	@Override
//	public InputStream getResourceAsStream(String name) {
//		System.out.println("PluginClassLoader.getResourceAsStream("+name+")");
//		return super.getResourceAsStream(name);
//	}
//
//	@Override
//	public Enumeration<URL> getResources(String name) throws IOException {
//		System.out.println("PluginClassLoader.getResources("+name+")");
//		return super.getResources(name);
//	}
//
//	@Override
//	protected synchronized Class<?> loadClass(String name, boolean resolve)
//			throws ClassNotFoundException {
//		System.out.println("PluginClassLoader.loadClass("+name+","+resolve+")");
//		return super.loadClass(name, resolve);
//	}
//
//	@Override
//	public Class<?> loadClass(String name) throws ClassNotFoundException {
//		System.out.println("PluginClassLoader.loadClass("+name+")");
//		return super.loadClass(name);
//	}
//
//	@Override
//	public synchronized void setClassAssertionStatus(String className,
//			boolean enabled) {
//		System.out.println("PluginClassLoader.setClassAssertionStatus("+className+","+enabled+")");
//		super.setClassAssertionStatus(className, enabled);
//	}
//
//	@Override
//	public synchronized void setDefaultAssertionStatus(boolean enabled) {
//		System.out.println("PluginClassLoader.setDefaultAssertionStatus("+enabled+")");
//		super.setDefaultAssertionStatus(enabled);
//	}
//
//	@Override
//	public synchronized void setPackageAssertionStatus(String packageName,
//			boolean enabled) {
//		System.out.println("PluginClassLoader.setPackageAssertionStatus("+packageName+","+enabled+")");
//		super.setPackageAssertionStatus(packageName, enabled);
//	}
	
	
//    public Class findClass(String name)throws ClassNotFoundException{
//
//        byte[] classBytes = findClassBytes(name);
//        if (classBytes==null){
//            throw new ClassNotFoundException();
//        }
//        else{
//            return defineClass(name, classBytes, 0, classBytes.length);
//        }
//    }
//
//    public Class findClass(String name, byte[] classBytes)throws ClassNotFoundException{
//
//        if (classBytes==null){
//            throw new ClassNotFoundException("(classBytes==null)");
//        }
//        else{
//            return defineClass(name, classBytes, 0, classBytes.length);
//        }
//    }
}