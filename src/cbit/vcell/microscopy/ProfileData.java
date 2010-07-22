package cbit.vcell.microscopy;

import java.util.ArrayList;

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
