package org.jlibsedml;

/**
 * Encapsulates the versioning information of a SEDML description.
 * @author radams
 *
 */
public final class Version {
 @Override
	public String toString() {
		return "Version [level=" + level + ", version=" + version + "]";
	}

private int level, version;
 
 /**
  * @param level A positive integer, or zero
  * @param version A positive integer, or zero.
  */
 public Version(int level, int version) {
		super();
		this.level = level;
		this.version = version;
	}

/**
 * Gets the level of the document; this the major version.
 * @return An int
 */
public int getLevel() {
	return level;
}

/**
 * Gets the version for a particular level.
 * @return the level's version.
 */
public int getVersion() {
	return version;
}

/**
 * Boolean test to compare two versions. This method returns <code>true</code> if
 *  this version is earlier than <code>otherVersion</code>.
 * @param otherVersion
 * @return <code>true</code> if this version is earlier than <code>otherVersion</code>, false otherwise.
 */
public boolean isEarlierThan(Version otherVersion){
	return this.level < otherVersion.getLevel()  ||
	       this.level == otherVersion.level && this.version < otherVersion.version;
}

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + level;
	result = prime * result + version;
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Version other = (Version) obj;
	if (level != other.level)
		return false;
	if (version != other.version)
		return false;
	return true;
}


}
