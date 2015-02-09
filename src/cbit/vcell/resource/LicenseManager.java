package cbit.vcell.resource;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.vcell.util.PropertyLoader;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.SimpleUserMessage;

/**
 * manage licensed libraries
 * @author gweatherby
 * note some information is cached; class will fail to execute correctly if files manually deleted after program start
 */
public class LicenseManager {

	/**
	 * has the user accepted special license? 
	 */
	private static Boolean specialLicenseAccepted[] = new Boolean[LibraryLicense.size];
	/**
	 * preferences key prefix 
	 */
	private static final String LICENSE_ACCEPTED = "licenseAccepted";
	
	/**
	 * libraries which have already been downloaded
	 */
	private static List<File>  librariesDownloaded = new ArrayList<File>();

	/**
	 * open graphic prompt for user to accept license 
	 * @param parent of dialog; may not be null
	 * @param ll licensed library; may not be null
	 * @param throwExceptionOnCancel if true, throw UserCancelException if user declines library 
	 * @return true if user accepts license
	 * @throws {@link UserCancelException}, {@link IllegalArgumentException}, {@link NullPointerException}
	 */
	public static boolean promptForLicense(Component parent, LicensedLibrary ll, boolean throwExceptionOnCancel) throws UserCancelException, IllegalArgumentException {
		if (parent != null) {
		LibraryLicense license = ll.licensedRequired();
		String licenseText = getLicenseText(license);
		String r = DialogUtils.showOKCancelWarningDialog(parent,"License acceptance required",
				licenseText);
		if (SimpleUserMessage.OPTION_CANCEL.equals(r)) {
			if (throwExceptionOnCancel) {
				throw new UserCancelException("User declined license for " + ll.libraryName());
			}
			return false;
		}
		LicenseManager.acceptLicense(license);
		return true;
		}
		throw new IllegalArgumentException("parent may not be null");
	}
	
	/**
	 * determine if licensed is required and user has accepted 
	 * @return true if has
	 */
	static boolean isLicensed(LibraryLicense license) {
		if (license == null) {
			return true;
		}
			
		int index = license.ordinal();
		if (specialLicenseAccepted[index] == null) {
			Preferences uprefs = Preferences.userNodeForPackage(LicenseManager.class);
			specialLicenseAccepted[index] = uprefs.getBoolean(LICENSE_ACCEPTED + license.name(),false);
		}
		return specialLicenseAccepted[index];
	}

	/**
	 * 
	 * @param license
	 * @throws IllegalArgumentException if license null
	 */
	public static void acceptLicense(LibraryLicense license) {
		if (license == null) {
			throw new IllegalArgumentException("null license object");
		}
		int index = license.ordinal();
		specialLicenseAccepted[index] = true; 
		Preferences uprefs = Preferences.userNodeForPackage(LicenseManager.class);
		uprefs.putBoolean(LICENSE_ACCEPTED + license.name(),true);
	}

	/**
	 * download license file from Internet
	 * @param license
	 * @return license text from internet or default text if that fails 
	 */
	public static String getLicenseText(LibraryLicense license) {
		try {
			OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
			String urlS = PropertyLoader.getProperty(PropertyLoader.vcellDownloadDir,"http://vcell.org/webstart/");
			//urlS += license.category.name() + '/' + ResourceUtil.NATIVE_LIB_DIR + '/' + license.filename;
			urlS += license.category.name() + '/' + osi.getNativeLibDirectory() + license.filename;
			URL url = new URL(urlS);
			try (InputStreamReader is = new InputStreamReader(url.openStream())) {
				try (java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A")) {
					return s.next(); 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return license.licenseText; 
	
	}
	/**
	 * download files required for license from Internet
	 * @param library
	 * @throws Exception
	 */
	static void install(LicensedLibrary library) throws IOException {
		File libraryFile = localStoragePath(library);
		URL url = library.downloadUrl(); 
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		try (FileOutputStream fos = new FileOutputStream(libraryFile)) {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}
		librariesDownloaded.add(libraryFile);
	}
	
	/**
	 * @param library
	 * @return standard location to put library when downloaded from Internet
	 */
	private static File localStoragePath(LicensedLibrary library) {
		return new java.io.File(ResourceUtil.getDownloadDirectory(),library.version());
	}
	
	/**
	 * is library present on local file system? 
	 * @param library
	 * @return true if is
	 */
	static boolean isPresent(LicensedLibrary library) {
		File libraryFile = localStoragePath(library);
		if (librariesDownloaded.contains(libraryFile)) {
			return true;
		}
		if (libraryFile.exists()) {
			librariesDownloaded.add(libraryFile);
			return true;
		}
		return false;
	}
	

	/**
	 * copy licensed libraries to specified directories
	 * name is changed from {@link LicensedLibrary#version()} to {@link LicensedLibrary#libraryName()}
	 * @param license
	 * @param directory
	 * @throws IOException
	 */
	static void copyLicensedLibraries(LicensedLibrary ll, File directory) throws IOException {
		File dest = new File(directory,ll.libraryName());
		Files.copy(localStoragePath(ll).toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	//for junit testing
	static void clearLicense(LibraryLicense license) {
		assert license != null;
		int index = license.ordinal();
		specialLicenseAccepted[index] = null; 
		Preferences uprefs = Preferences.userNodeForPackage(LicenseManager.class);
		uprefs.remove(LICENSE_ACCEPTED + license.name());
	}

}
