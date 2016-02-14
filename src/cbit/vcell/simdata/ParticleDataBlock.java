/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.*;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.VCAssert;
import org.vcell.util.document.User;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class ParticleDataBlock implements java.io.Serializable {

	private ParticleDataInfo particleDataInfo = null;
	/**
	 * species -> coordinates map
	 */
	private Map<String, List<Coordinate> > cMap;

/**
 * SimDataBlock constructor comment.
 */
public ParticleDataBlock(User user, String simIdentifier, double time, File file, File zipFile) throws DataAccessException, IOException {
	cMap =  new HashMap<>();
	long timeStamp = read(file, zipFile);
	this.particleDataInfo = new ParticleDataInfo(user,simIdentifier,time,timeStamp);
}

/**
 * set of species in data block
 * @return unmodifiable Set
 */
public Set<String> getSpecies( ) {
	return Collections.unmodifiableSet( cMap.keySet() );
}

/**
 * coordinates of particles of specified species
 * @param speciesName
 * @return unmodifiable List
 * @throws IllegalArgumentException if speciesName not in {@link #getSpecies()}
 */
public List<Coordinate> getCoordinates(String speciesName) {
	List<Coordinate> lst = cMap.get(speciesName);
	if (lst != null) {
		return Collections.unmodifiableList( lst );
	}
	throw new IllegalArgumentException("Invalid speciesName " + speciesName);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.SimDataInfo
 */
public ParticleDataInfo getParticleDataInfo() {
	return particleDataInfo;
}
/**
 * not really implemented
 * @return long
 */
public long getSizeInBytes() {
	return 1;
}
/**
 * This method was created in VisualAge.
 * @return long
 */
public long getTimeStamp() {
	return particleDataInfo.getTimeStamp();
}

/**
 * lazy get ( create) list for species name
 * @param speciesName not null
 * @return new or existing list
 */
private List<Coordinate> fetch(String speciesName) {
	VCAssert.assertValid(speciesName);
	List<Coordinate> lst = cMap.get(speciesName);
	if (lst != null) {
		return lst;
	}
	lst = new ArrayList<>();
	cMap.put(speciesName, lst);
	return lst;
}

/**
 * read data from either existing file or zipped entry in zipfile
 * @param file
 * @param zipFile
 * @return timeStamp of data (from either file or zipFile entry)
 * @throws DataAccessException
 * @throws IOException
 */
private long read(File file, File zipFile) throws DataAccessException, IOException {
	VCAssert.assertValid(file);

	List<String> lines;
	long timeStamp;
	if (file.exists( )) {
		lines = FileUtils.readLines(file);
		timeStamp = file.lastModified();
	}
	else {
		VCAssert.assertValid(zipFile);
		try (ZipFile zf = new ZipFile(zipFile)) {
			ZipEntry entry = zf.getEntry(file.getName( ));
			if (entry != null) {
				InputStream is = zf.getInputStream(entry);
				lines = IOUtils.readLines(is);
				timeStamp= entry.getTime();
			}
			else {
				throw new DataAccessException("Unable to find " + file + " in zip file " + zipFile);
			}

		}
	}
	readParticleData(lines);
	return timeStamp;
}

private void readParticleData(List<String> lines) throws DataAccessException {
	String lastSpecies = null;
	List<Coordinate> working = null;

	for (String line: lines) {
		try {
			CommentStringTokenizer st = new CommentStringTokenizer(line);
			String sp = st.nextToken();
			if (!sp.equals(lastSpecies)) {
				lastSpecies = sp;
				working = fetch(sp);
			}
			double x = Double.parseDouble(st.nextToken());
			double y = Double.parseDouble(st.nextToken());
			double z = Double.parseDouble(st.nextToken());
			Coordinate c = new Coordinate(x,y,z);
			working.add(c);
		} catch (Exception exc) {
			throw new DataAccessException("Particle data file invalid. " + exc.getMessage());
		}
	}
}
}
