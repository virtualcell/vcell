package org.vcell.vmicro.workflow.data;

import cbit.vcell.VirtualMicroscopy.Image;

public interface SampleFunction {
	String getName();
	double average(Image image);
	double integrate(Image image);
}
