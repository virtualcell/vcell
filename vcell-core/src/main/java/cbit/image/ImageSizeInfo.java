package cbit.image;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.vcell.util.ISize;

@XmlRootElement
public class ImageSizeInfo{
	@XmlAttribute
	private String imagePath;
	@XmlAttribute
	private int x,y,z;
	@XmlAttribute
	private int numChannels;
	@XmlElement
	private double[] timePoints;
	@XmlAttribute
	private Integer selectedTimeIndex;
	public ImageSizeInfo() {
		
	}
	public ImageSizeInfo(String imagePath, ISize iSize, int numChannels,double[] timePoints,Integer selectedTimeIndex) {
		super();
		this.imagePath = imagePath;
		this.x = iSize.getX();
		this.y = iSize.getY();
		this.z = iSize.getZ();
		this.numChannels = numChannels;
		this.timePoints = timePoints;
		this.selectedTimeIndex = selectedTimeIndex;
	}
	public String getImagePath() {
		return imagePath;
	}
	public ISize getiSize() {
		return new ISize(x, y, z);
	}
	public int getNumChannels() {
		return numChannels;
	}
	public double[] getTimePoints(){
		return timePoints;
	}
	public Integer getSelectedTimeIndex(){
		return selectedTimeIndex;
	}
	
}