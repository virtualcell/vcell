/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization;

import java.util.ArrayList;

/*
 * The class contains a list of ProfileDataElement, which is the profile distribution
 * for a specific parameter.
 * author: Tracy Li
 * version: 1.1
 */
public class ProfileData 
{
	private ArrayList<ProfileDataElement> profileDataElements = null;
	
	public ProfileData()
	{
		profileDataElements = new ArrayList<ProfileDataElement>();
	}
	
	public void addElement(ProfileDataElement profileDataElement)
	{
		profileDataElements.add(profileDataElement);
	}
	public void addElement(int insertIdx, ProfileDataElement profileDataElement)
	{
		profileDataElements.add(insertIdx, profileDataElement);
	}
	public ArrayList<ProfileDataElement> getProfileDataElements()
	{
		return profileDataElements;
	}
}
