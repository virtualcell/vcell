/**
 * Copyright 2009 Jee Vang
 * Modified: Dan Vasilescu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package cbit.vcell.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * License writer. Prepends a license statement to a file.
 */
public class LicenseWriter {

	public static final String TEMP_FILE_PREFIX = "TEMP_";	// File name prefix for temporary file.
	private File licenseFile;					// File with license statement.
	private String newLine = "\r\n";			// String to add after every line. i.e. \r\n or just \n.
	private String _licenseStatement;			// License statement in license file.
	
	private static final String spaces = "                                                                                                                                                             ";
	private static final String oldUConn = "University of Connecticut Health Center 2001";
	
	// various statistics
	private static int summaryFilesWithForeignLicense = 0;
	private static int summaryFilesWithOldUConnLicense = 0;
	private static int summaryFilesWithNoLicense = 0;
	private static int summaryOldLicenseSuccesfulDeletion = 0;
	private static int summaryOldLicenseUnexpectedLocation = 0;
	private static int summaryTotalFilesChecked = 0;
	private static int summaryExceptionsEncountered = 0;
	private static int summarySkippedDirectories = 0;
	private static int summaryAddedLicenses = 0;
	
	// directories and files which we ignore
	private static ArrayList <String> fileExceptions = new ArrayList <String>();
	private static ArrayList <String> directoryExceptions = new ArrayList <String>();

	
	public LicenseWriter() {
	}

	public LicenseWriter(File licenseFile) {	// licenseFile File with license statement.
		this.licenseFile = licenseFile;
	}

	public void evaluate (File file) throws Exception {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			// read in the contents of the file and check for certain keywords
			String line = null;
			while(null != (line = reader.readLine())) {
				if(line.contains("Copyright") && line.contains(oldUConn)) {
					System.out.println("          Old UConn license in " + file.getPath());
					summaryFilesWithOldUConnLicense++;
					reader.close();
					return;
				} else if(line.contains("Copyright") && !line.contains(oldUConn)) {
// TODO: uncomment this!
//					System.out.println(file.getPath()
//							+ "   possible foreign license."
//							);
					summaryFilesWithForeignLicense++;
					reader.close();
					return;
				}
			}
//			System.out.print("no license.");
			System.out.println(file.getPath() + "   no license." );
			summaryFilesWithNoLicense++;
			reader.close();
		} catch(Exception ex) {
			throw ex;
		} finally {
			if(null != reader) {
				try {
					reader.close();
					reader = null;
				} catch(Exception ex) { }
			}
		}
	}
	
	public void cleanup(File file) throws Exception {
		BufferedWriter writer = null;
		BufferedReader reader = null;
//		if(file.getPath().contains("src\\org\\vcell\\util\\gui\\sorttable\\JSortTable.java")) {
//			System.out.println("Found " + file.getPath());
//		}
		try {
			File tempFile = getTemporaryFile(file);					// get the temporary file that we will write to
			writer = new BufferedWriter(new FileWriter(tempFile));	// create the writer that will write to the temporary file
			reader = new BufferedReader(new FileReader(file));		// create the reader that will read from the file

			// read in the contents of the file, and write it to the temp file
			String line = null;
			int lineIndex = 0;		// copyright comment must be near the top, we don't look at comments starting below line 10
			boolean oldLicenseFound = false;		// true if we find an obsolete license
			while(null != (line = reader.readLine())) {
				
				if(line.startsWith("/*") && !line.contains("*/") && !oldLicenseFound) {
					// found a multiline comment
					ArrayList <String> comments = new ArrayList <String>();
					comments.add(line);
					if(line.contains("Copyright")){
						// probable external license
//						System.out.print(file.getPath());
//						System.out.println("   possible foreign license. File left unchanged.");
						summaryFilesWithForeignLicense++;
						writer.close();
						reader.close();
						tempFile.delete();
						return;	// no cleanup for this file
					}
					line = reader.readLine();	// 2nd line - here should be the copyright notice
					if(line == null) {
						System.out.print(file.getPath());
						System.out.println("   Error reading file. File left unchanged.");
						writer.close();
						reader.close();
						tempFile.delete();
						return;	// no cleanup for this file
					}
					if(line.contains("*/")) {
						comments.add(line);
						for(String s : comments) {
							writer.write(s);
							writer.write(newLine);
							lineIndex++;
							continue;		// next iteration of exterior while loop
						}
					}
					if(line.contains("Copyright") && line.contains(oldUConn)) {
						// found obsolete UConn copyright
						summaryFilesWithOldUConnLicense++;
						comments.add(line);
						while(true) {
							line = reader.readLine();
							if(line == null) {
								System.out.print(file.getPath());
								System.out.println("   Error reading file. File left unchanged.");
								writer.close();
								reader.close();
								tempFile.delete();
								return;	// no cleanup for this file
							}
							comments.add(line);
							if(line.contains("*/")) {
								summaryOldLicenseSuccesfulDeletion++;
								break;		// break out of internal while loop
							}
						}
//						if(comments.size() != 4) {
//							for(String s : comments) {
//								System.out.println(s);
//							}
//						}
						// we "swallow" (consume) this comment - it's the obsolete license
						oldLicenseFound = true;
//						System.out.print(file.getPath());
//						System.out.println("   old license deleted.");
						continue;		// next iteration of exterior while loop
					} else if(line.contains("Copyright") && !line.contains(oldUConn)){
						// external license
//						System.out.print(file.getPath());
//						System.out.println("   possible foreign license. File left unchanged.");
						summaryFilesWithForeignLicense++;
						writer.close();
						reader.close();
						tempFile.delete();
						return;	// no cleanup for this file
					} else {	// line 2 doesn't contain any copyright notice, we assume it's just a plain comment
						comments.add(line);
						for(String s : comments) {
							writer.write(s);
							writer.write(newLine);
							lineIndex++;
						}
						continue;		// next iteration of exterior while loop
					}
				}
				writer.write(line);
				writer.write(newLine);
				lineIndex++;
				if(!oldLicenseFound && line.contains("Copyright") && line.contains(oldUConn)) {
					summaryOldLicenseUnexpectedLocation++;
					writer.close();
					reader.close();
					tempFile.delete();
					System.out.print(file.getPath());
					System.out.println("   old license found outside comment!!!");
					return;
				}
			}
			writer.close();
			reader.close();
			if(!oldLicenseFound) {		// normal file, nothing to clean up - we leave unchanged
				tempFile.delete();
				return;
			}
			
			//now read from the temp file and copy the contents to the original file
			writer = new BufferedWriter(new FileWriter(file));
			reader = new BufferedReader(new FileReader(tempFile));

			while(null != (line = reader.readLine())) {
				writer.write(line);
				writer.write(newLine);
			}
			writer.close();
			reader.close();
			tempFile.delete();
			
		} catch(Exception ex) {
			throw ex;
		} finally {
			if(null != writer) {
				try {
					writer.close();
					writer = null;
				} catch(Exception ex) { }
			}

			if(null != reader) {
				try {
					reader.close();
					reader = null;
				} catch(Exception ex) { }
			}
		}
	}

	public void write(File file) throws Exception {
		BufferedWriter writer = null;
		BufferedReader reader = null;

		String filePath = file.getPath();
		if(filePath.contains("src\\") && fileExceptions.contains(filePath.substring(filePath.lastIndexOf("src\\")))) {
			System.out.println(filePath + "   ignoring file on exception list.");
			summaryExceptionsEncountered++;
			return;
		}
		try {
			String newLine = getNewLine();							// get the newline string
			File tempFile = getTemporaryFile(file);					// get the temporary file that we will write to
			writer = new BufferedWriter(new FileWriter(tempFile));	// create the writer that will write to the temporary file
			reader = new BufferedReader(new FileReader(file));		// create the reader that will read from the file
			
			// pass 1 - we skip all the files still containing a Copyright statement
			String line1 = null;
			while(null != (line1 = reader.readLine())) {
				if(line1.contains("Copyright")) {
					System.out.println(filePath + "   ignoring file containing Copyright statement.");
					summaryExceptionsEncountered++;
					writer.close();
					reader.close();
					tempFile.delete();
					return;
				}
			}
			reader.close();
			
			// pass 2 - on clean file, with no Copyright statement
			reader = new BufferedReader(new FileReader(file));
			
			// write the license statement to the temporary file
			String licenseStatement = getLicenseStatement();
			writer.write(licenseStatement);
			writer.write(newLine);

			// read in the contents of the file, and write it to the temp file
			String line = null;
			while(null != (line = reader.readLine())) {
				writer.write(line);
				writer.write(newLine);
			}
			writer.close();
			reader.close();

			// now read from the temp file and copy the contents to the original file
			writer = new BufferedWriter(new FileWriter(file));
			reader = new BufferedReader(new FileReader(tempFile));
			while(null != (line = reader.readLine())) {
				writer.write(line);
				writer.write(newLine);
			}
			writer.close();
			reader.close();
			tempFile.delete();
			
			summaryAddedLicenses++;
		} catch(Exception ex) {
			throw ex;
		} finally {
			if(null != writer) {
				try {
					writer.close();
					writer = null;
				} catch(Exception ex) { }
			}

			if(null != reader) {
				try {
					reader.close();
					reader = null;
				} catch(Exception ex) { }
			}
		}
	}

	public File getTemporaryFile(File file) throws IOException {
		String temporaryFileName = getTemporaryFileName(file);
		File tempFile = new File(file.getParent(), temporaryFileName);
		return tempFile;
	}
	public String getTemporaryFileName(File file) {
		StringBuffer sb = new StringBuffer();
		sb.append(TEMP_FILE_PREFIX);
		sb.append(file.getName());
		return sb.toString();
	}
	public String getLicenseStatement() throws Exception {
		StringBuffer sb = new StringBuffer();
		if(null == _licenseStatement) {
			BufferedReader reader = null;
			try {
				File licenseFile = getLicenseFile();
				reader = new BufferedReader(new FileReader(licenseFile));
				String line = null;
				while(null != (line = reader.readLine())) {
					sb.append(line);
					sb.append(getNewLine());
				}
			} catch(Exception ex) {
				throw ex;
			} finally {
				if(null != reader) {
					try {
						reader.close();
						reader = null;
					} catch(Exception ex) { }
				}
			}
		}
		return sb.toString();
	}
	public File getLicenseFile() {
		return licenseFile;
	}
	public void setLicenseFile(File licenseFile) {
		this.licenseFile = licenseFile;
	}

	public String getNewLine() {
		return newLine;
	}
	public void setNewLine(String newLine) {
		this.newLine = newLine;
	}

	/**
	 *  Optional arguments:
	 *  - command
	 *  - directory where java source code is located
	 *  - file with license statement
	 */
	public static void main(String[] args) {
		// Exceptions:
		fileExceptions.add("src\\cbit\\vcell\\tools\\LicenseWriter.java");				// this file
		fileExceptions.add("src\\org\\vcell\\sybil\\rdf\\reason\\MakeSkolem.java");		// external license
		fileExceptions.add("src\\org\\vcell\\util\\gui\\sorttable\\JSortTable.java");	// external license
//		fileExceptions.add("src\\cbit\\vcell\\client\\desktop\\DocumentWindowAboutBox.java");	// license string inside code
//		fileExceptions.add("src\\cbit\\rmi\\event\\RemoteMessageHandler.java");			// code & comment on same line
//		fileExceptions.add("src\\cbit\\rmi\\event\\RemoteMessageListener.java");
		
        fileExceptions.add("src/org/vcell/wizard/Wizard.java");
        fileExceptions.add("src/org/vcell/wizard/FinishDescriptor.java");
        fileExceptions.add("src/org/vcell/wizard/WizardPanelNotFoundException.java");
        fileExceptions.add("src/org/vcell/wizard/WizardController.java");
        fileExceptions.add("src/org/vcell/wizard/WizardPanelDescriptor.java");
        fileExceptions.add("src/org/vcell/wizard/WizardModel.java");
        
        // from numerics - these files have their own copyright statement
        fileExceptions.add("VCell/include/VCELL/ilTIFF.h");
        fileExceptions.add("VCell_CodeGen/include/VCELL/ilTIFF.h");

        // numerics - list of foreign directories (which won't get our license)
        String numericsPrefix = "hyhyhyhy";
		directoryExceptions.add(numericsPrefix + "blas");
//		directoryExceptions.add(numericsPrefix + "ExpressionParser");
//		directoryExceptions.add(numericsPrefix + "ExpressionParserTest");
//		directoryExceptions.add(numericsPrefix + "FiniteVolume");
		directoryExceptions.add(numericsPrefix + "fsqp");				// csfqp?
		directoryExceptions.add(numericsPrefix + "glut-3.7.6");
		directoryExceptions.add(numericsPrefix + "Hy3S");
//		directoryExceptions.add(numericsPrefix + "IDAWin");
		directoryExceptions.add(numericsPrefix + "IFortran");
//		directoryExceptions.add(numericsPrefix + "JavaBinding");
		directoryExceptions.add(numericsPrefix + "JNI");
		directoryExceptions.add(numericsPrefix + "libSBML-3.3.2");
		directoryExceptions.add(numericsPrefix + "libSBML-4.0.0-b2");
		directoryExceptions.add(numericsPrefix + "netcdf-3.6.2");
//		directoryExceptions.add(numericsPrefix + "Optimization2");
//		directoryExceptions.add(numericsPrefix + "OptStandalone2");
		directoryExceptions.add(numericsPrefix + "PCGPack");
		directoryExceptions.add(numericsPrefix + "qhull");
		directoryExceptions.add(numericsPrefix + "smoldyn");
		directoryExceptions.add(numericsPrefix + "smoldyn-2.15");
//		directoryExceptions.add(numericsPrefix + "Stochastic");
		directoryExceptions.add(numericsPrefix + "sundials");
		directoryExceptions.add(numericsPrefix + "SundialsSolverStandalone");
		directoryExceptions.add(numericsPrefix + "tinyxml");
		directoryExceptions.add(numericsPrefix + "unzip");
//		directoryExceptions.add(numericsPrefix + "VCell");
//		directoryExceptions.add(numericsPrefix + "VCell_CodeGen");
//		directoryExceptions.add(numericsPrefix + "VCellMTMD");
		directoryExceptions.add(numericsPrefix + "zip");
		directoryExceptions.add(numericsPrefix + "zlib");

		if(args.length != 0 && args.length != 3) {
			throw new IllegalArgumentException("Arguments:  [[command] [java source dir] [license file name]]");
		}
		String command = null;
		File dir = null;
		String rootDirectory = null;
		File licenseFile = null;
		String licenseFileName = null;
		if(args.length == 0) {
			{	// ************************************ setup area ***********************************
			command = "evaluate";
//			command = "cleanup";
//			command = "write";
			licenseFileName = "C:/dan/projects/VCell_trunk/src/cbit/vcell/tools/license.txt";
			String vCellSourceDirectory = "C:/dan/projects/VCell_5.0_branch";
//			String vCellSourceDirectory = "C:/dan/projects/VCell_trunk";
//			licenseFileName = "/home/CAM/eboyce/Desktop/license.txt";
//    		String vCellSourceDirectory = "/eboyce-local/workspace/VCell";
//			String numericsSourceDirectory = "/h";
			rootDirectory = vCellSourceDirectory;
			}
			dir = new File(rootDirectory);
			licenseFile = new File(licenseFileName);
		} else if (args.length == 3) {
			command = args[0];
			String directory = args[1];	// java source directory is first parameter
			dir = new File(directory);
			licenseFileName = args[2];	// name of file with license statement is second parameter
			licenseFile = new File(licenseFileName);
		}
		
		System.out.println("Command: " + command);
		System.out.println("Root directory: " + rootDirectory);
		
		//construct the license writer
		LicenseWriter licenseWriter = new LicenseWriter(licenseFile);

		try {
			//prepend license statement to files in directory
			int depth = 0;
			manageFiles(command, dir, licenseWriter, depth);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		if(command.equals("evaluate")) {
			System.out.println("Summary :");
			System.out.println("  Files With Foreign License: " + summaryFilesWithForeignLicense);
			System.out.println("  Files With Old UConn License: " + summaryFilesWithOldUConnLicense);
			System.out.println("  Files With No License: " + summaryFilesWithNoLicense);
			System.out.println("  Total Files Checked: " + summaryTotalFilesChecked);
		}
		if(command.equals("cleanup")) {
			System.out.println("Summary :");
			System.out.println("  Files With Old UConn License: " + summaryFilesWithOldUConnLicense);
			System.out.println("  Successful Deletions: " + summaryOldLicenseSuccesfulDeletion);
			System.out.println("  Unexpected Location: " + summaryOldLicenseUnexpectedLocation);
			System.out.println("  Total Files Checked: " + summaryTotalFilesChecked);
		}
		if(command.equals("write")) {
			System.out.println("Summary :");
			System.out.println("  Files Receiving License Statement: " + summaryAddedLicenses);
			System.out.println("  Skipped Directories: " + summarySkippedDirectories);
			System.out.println("  Skipped Files: " + summaryExceptionsEncountered);
			System.out.println("  Total Files Checked: " + summaryTotalFilesChecked);
		}

	}

	/**
	 * Prepend license statement to file. If the file is a directory,
	 * then recurses into the directory.
	 * @param file File or directory which to prepend license statement.
	 * @param licenseWriter License writer.
	 * @throws Exception
	 */
	public static void manageFiles(String command, File file, LicenseWriter licenseWriter, int depthParam) throws Exception {
		int depth = depthParam;
		if(file.isDirectory()) {	// if file is a directory
			if(file.getName().startsWith(".svn")) {
//				System.out.println("----------- ignoring directory " + file.getName());
				return;
			}
			String directoryName = file.getPath();
			for(String directoryException : directoryExceptions) {
				if(directoryName.contains(directoryException)) {
					System.out.println(directoryName + "   ignoring directory on exception list.");
					summarySkippedDirectories++;
					return;
				}
			}

//			System.out.println("--------------------- dir: " + file.getPath());
			// get the children of this directory
			String[] fileNames = file.list();
			for(int i=0; i < fileNames.length; i++) {
				String fileName = fileNames[i];
				File f = new File(file, fileName);
				manageFiles(command, f, licenseWriter, depth+1);	// prepend license statement to child
			}
		} else {					// base case, if file represents a file
			if(
					!file.getName().endsWith(".java") &&			// file must have .java extension
					!file.getName().endsWith(".cpp") &&
					!file.getName().endsWith(".c") &&
					!file.getName().endsWith(".hpp") &&
					!file.getName().endsWith(".h")
//					!file.getName().endsWith(".f") &&				// Fortran files
//					!file.getName().endsWith(".f90")
					) {					
				return;
			}
			if(file.getName().contains(TEMP_FILE_PREFIX)) {			// we ignore the temp files we create
//				file.delete();		// delete the temp files we left behind by mistake
				return;
			}
			if(file.getPath().contains("src\\cbit\\vcell\\tools\\LicenseWriter.java")) {	// ignore this file
				return;
			}
			summaryTotalFilesChecked++;
			if(command.equals("evaluate")) {			// report on files with foreign license, old UConn license or no license
//				System.out.print(spaces.substring(0, 2*depth) + file.getName() + spaces.substring(0, 80-(2*depth+file.getName().length())));
				licenseWriter.evaluate(file);
//				System.out.println("");
			} else if(command.equals("write")) {		// writes the new copyright notice
				licenseWriter.write(file);
			} else if(command.equals("cleanup")) {		// deletes old UConn copyright notices
				licenseWriter.cleanup(file);
			} else {
				throw new IllegalArgumentException("Command must be: evaluate, write or cleanup");
			}
		}
	}
}
