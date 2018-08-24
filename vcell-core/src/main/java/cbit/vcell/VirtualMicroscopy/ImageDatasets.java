package cbit.vcell.VirtualMicroscopy;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ImageDatasets {
	@XmlElement
	private ImageDataset[] imageDatasets;
	public ImageDatasets() {}//For jaxb
	public ImageDatasets(ImageDataset[] imageDatasets) {
		super();
		this.imageDatasets = imageDatasets;
	}
	public ImageDataset[] getImageDatasets() {
		return imageDatasets;
	}
}
