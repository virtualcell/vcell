/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.inversepde.gui;


import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import org.jdom.Element;
import org.vcell.inversepde.InverseProblem;
import org.vcell.inversepde.InverseProblemUtilities;
import org.vcell.inversepde.LinearResponseModel;
import org.vcell.inversepde.microscopy.AnnotatedImageDataset_inv;
import org.vcell.inversepde.microscopy.FRAPStudy;
import org.vcell.inversepde.microscopy.InverseProblemXmlReader;
import org.vcell.inversepde.microscopy.InverseProblemXmlproducer;
import org.vcell.inversepde.microscopy.ROIImage;
import org.vcell.inversepde.services.InversePDERequestManager;
import org.vcell.util.ISize;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;
import org.vcell.util.gui.DialogUtils;
import org.vcell.vmicro.op.ExportRawTimeSeriesToVFrapOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.ExportEvent;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.client.DataViewerManager;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.server.DataSetControllerProvider;
import cbit.vcell.client.server.SimStatusEvent;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.simdata.DataListener;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.NewClientPDEDataContext;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.xml.XmlHelper;

public class InverseProblemPanel extends JPanel {
	private JTextArea textArea;
	private InverseProblem inverseProblem = null;
//	private File workingDirectory = null;
//	private ClientServerManager clientServerManagerLocal = null;
//	private ClientServerManager clientServerManagerRemote = null;
	private OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
	private InversePDERequestManager inversePDERequestManager = null;

	
	public InverseProblemPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,0,7};
		setLayout(gridBagLayout);

		final JPanel loadButtonPanel = new JPanel();
		loadButtonPanel.setLayout(new GridLayout(5, 0));
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		add(loadButtonPanel, gridBagConstraints);

		final JButton loadInverseproblemButton = new JButton();
		loadInverseproblemButton.setHorizontalAlignment(SwingConstants.LEFT);
		loadButtonPanel.add(loadInverseproblemButton);
		loadInverseproblemButton.setMargin(new Insets(2, 14, 2, 14));
		loadInverseproblemButton.setText("Load InverseProblem");
		loadInverseproblemButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					FileFilter fileFilter = new FileFilter() {
						public boolean accept(File f) {
							return f.isDirectory() || f.getName().endsWith(".inverse");
						}
						public String getDescription() {
							return "inverse problem files";
						}
				    };
				    File selectedFile = inversePDERequestManager.getSelectedOpenFile(InverseProblemPanel.this, fileFilter);
			    	InverseProblemXmlReader reader = new InverseProblemXmlReader(true);
			    	String xmlString = XmlUtil.getXMLString(selectedFile.getAbsolutePath());
			    	Element element = XmlUtil.stringToXML(xmlString,null).getRootElement();
			    	inverseProblem = reader.getInverseProblem(element,null);
			    	printReport();
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});

		final JButton button = new JButton();
		button.setHorizontalAlignment(SwingConstants.LEFT);
		loadButtonPanel.add(button);
		button.setText("load Artificial model");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					FileFilter fileFilter = new FileFilter() {
						public boolean accept(File f) {
							return f.isDirectory() || f.getName().endsWith(".vcml");
						}
						public String getDescription() {
							return "VCell Model";
						}
				    };
					File selectedFile = inversePDERequestManager.getSelectedOpenFile(InverseProblemPanel.this, fileFilter);
			    	String xmlString = XmlUtil.getXMLString(selectedFile.getAbsolutePath());
			    	VCDocument vcDocument = XmlHelper.XMLToDocument(null, xmlString);
			    	inverseProblem = InverseProblemUtilities.loadArtificialModel(inversePDERequestManager,vcDocument);
			    	printReport();
				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});

		final JButton loadVfrapToTimeseriesButton = new JButton();
		loadVfrapToTimeseriesButton.setHorizontalAlignment(SwingConstants.LEFT);
		loadButtonPanel.add(loadVfrapToTimeseriesButton);
		loadVfrapToTimeseriesButton.setText("Load VFRAP to Timeseries");
		loadVfrapToTimeseriesButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					FileFilter fileFilter = new FileFilter() {
						public boolean accept(File f) {
							return f.isDirectory() || f.getName().endsWith(".vfrap");
						}
						public String getDescription() {
							return "VirtualFRAP files";
						}
				    };
				    File selectedFile = inversePDERequestManager.getSelectedOpenFile(InverseProblemPanel.this, fileFilter);
			    	InverseProblemXmlReader reader = new InverseProblemXmlReader(true);
			    	String xmlString = XmlUtil.getXMLString(selectedFile.getAbsolutePath());
			    	Element element = XmlUtil.stringToXML(xmlString,null).getRootElement();
			    	FRAPStudy frapStudy = reader.getFrapStudy(element,null);
			    	AnnotatedImageDataset_inv timeSeriesData = InverseProblemUtilities.getTimeSeriesFromFrapStudy(frapStudy);
			    	if (inverseProblem==null){
			    		inverseProblem = new InverseProblem();
			    	}
			    	inverseProblem.getMicroscopyData().setTimeSeriesImageData(timeSeriesData);
			    	printReport();
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});

		final JButton loadVfrapToZStackButton = new JButton();
		loadVfrapToZStackButton.setHorizontalAlignment(SwingConstants.LEFT);
		loadButtonPanel.add(loadVfrapToZStackButton);
		loadVfrapToZStackButton.setText("Load VFRAP to ZStack");
		loadVfrapToZStackButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					FileFilter fileFilter = new FileFilter() {
						public boolean accept(File f) {
							return f.isDirectory() || f.getName().endsWith(".vfrap");
						}
						public String getDescription() {
							return "VirtualFRAP files";
						}
				    };
				    File selectedFile = inversePDERequestManager.getSelectedOpenFile(InverseProblemPanel.this, fileFilter);
			    	InverseProblemXmlReader reader = new InverseProblemXmlReader(true);
			    	String xmlString = XmlUtil.getXMLString(selectedFile.getAbsolutePath());
			    	Element element = XmlUtil.stringToXML(xmlString,null).getRootElement();
			    	FRAPStudy frapStudy = reader.getFrapStudy(element,null);
			    	AnnotatedImageDataset_inv zSeriesData = InverseProblemUtilities.getZStackFromFrapStudy(frapStudy);
			    	if (inverseProblem==null){
			    		inverseProblem = new InverseProblem();
			    	}
			    	inverseProblem.getMicroscopyData().setZStackImageData(zSeriesData);
			    	printReport();
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});

		final JButton loadVfrapToButton = new JButton();
		loadVfrapToButton.setHorizontalAlignment(SwingConstants.LEFT);
		loadButtonPanel.add(loadVfrapToButton);
		loadVfrapToButton.setText("load VFRAP to PSF");
		loadVfrapToButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					FileFilter fileFilter = new FileFilter() {
						public boolean accept(File f) {
							return f.isDirectory() || f.getName().endsWith(".vfrap");
						}
						public String getDescription() {
							return "VirtualFRAP files";
						}
				    };
				    File selectedFile = inversePDERequestManager.getSelectedOpenFile(InverseProblemPanel.this, fileFilter);
			    	InverseProblemXmlReader reader = new InverseProblemXmlReader(true);
			    	String xmlString = XmlUtil.getXMLString(selectedFile.getAbsolutePath());
			    	Element element = XmlUtil.stringToXML(xmlString,null).getRootElement();
			    	FRAPStudy frapStudy = reader.getFrapStudy(element,null);
			    	InverseProblemUtilities.loadPSFFromFrapStudy(frapStudy,inverseProblem);
			    	printReport();
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});

		final JPanel computeButtonPanel = new JPanel();
		computeButtonPanel.setLayout(new GridLayout(10, 0));
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.insets = new Insets(5, 5, 5, 5);
		add(computeButtonPanel, gridBagConstraints_1);

		final JButton computeBasisROIsButton = new JButton();
		computeBasisROIsButton.setHorizontalAlignment(SwingConstants.LEFT);
		computeButtonPanel.add(computeBasisROIsButton);
		computeBasisROIsButton.setText("compute Bases");
		computeBasisROIsButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					inverseProblem.getLinearResponseModel().getBasisFunctions().calculateBasis(inversePDERequestManager, inverseProblem);
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});

		final JButton storeBasisRoisButton = new JButton();
		storeBasisRoisButton.setHorizontalAlignment(SwingConstants.LEFT);
		computeButtonPanel.add(storeBasisRoisButton);
		storeBasisRoisButton.setText("store Bases fieldData");
		storeBasisRoisButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					//
					// using local clientServerManager to store Bases ROI ... Message Queue complains about the message size.
					//
					InverseProblemUtilities.saveBasisROI(inversePDERequestManager, inverseProblem);
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});

		final JButton storePsfToButton = new JButton();
		storePsfToButton.setHorizontalAlignment(SwingConstants.LEFT);
		computeButtonPanel.add(storePsfToButton);
		storePsfToButton.setText("store PSF fieldData");
		storePsfToButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					//
					// using local clientServerManager to store Bases ROI ... Message Queue complains about the message size.
					//
					InverseProblemUtilities.savePSF(inversePDERequestManager, inverseProblem);
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});

		final JButton compute2dImageButton = new JButton();
		compute2dImageButton.setHorizontalAlignment(SwingConstants.LEFT);
		computeButtonPanel.add(compute2dImageButton);
		compute2dImageButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					InverseProblemUtilities.refreshDependentROIs(inverseProblem.getMicroscopyData().getTimeSeriesImageData());
//					InverseProblemUtilities.calculateImageROIs(inverseProblem);
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});
		compute2dImageButton.setText("Compute image ROIs");

		final JButton storeImageToButton = new JButton();
		storeImageToButton.setHorizontalAlignment(SwingConstants.LEFT);
		computeButtonPanel.add(storeImageToButton);
		storeImageToButton.setText("store Image ROI fieldData");
		storeImageToButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					//
					// using local clientServerManager to store Bases ROI ... Message Queue complains about the message size.
					//
					PropertyLoader.loadProperties();
					InverseProblemUtilities.saveImageROI(inversePDERequestManager, inverseProblem);
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});

		final JButton computeGeometryButton = new JButton();
		computeGeometryButton.setHorizontalAlignment(SwingConstants.LEFT);
		computeButtonPanel.add(computeGeometryButton);
		computeGeometryButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					inverseProblem.setGeometry(InverseProblemUtilities.createGeometry(inverseProblem));
				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});
		computeGeometryButton.setText("compute geometry");
		
		final JButton createmathmodelButton = new JButton();
		createmathmodelButton.setHorizontalAlignment(SwingConstants.LEFT);
		computeButtonPanel.add(createmathmodelButton);
		createmathmodelButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					String mathModelPrefix = "tempMathVolume";
					Geometry geometry = inverseProblem.getGeometry();
					if (geometry==null){
						throw new RuntimeException("geometry is missing");
					}
					LinearResponseModel linearResponseModel = inverseProblem.getLinearResponseModel();
					if (linearResponseModel.getBasisFunctions().getBasisFieldDataEDI()==null){
						throw new RuntimeException("basis FieldData hasn't been saved");
					}
					if (linearResponseModel.getPsfFieldDataEDI()==null){
						throw new RuntimeException("PSF FieldData hasn't been saved");
					}
					ROIImage basisROIImage = inverseProblem.getMicroscopyData().getZStackImageData().getROIImage(ROIImage.ROIIMAGE_BASIS);
					linearResponseModel.createRefVolumeSimMathModel(geometry, basisROIImage, inverseProblem);
					linearResponseModel.createRefMembraneOrFluxSimMathModel(geometry, basisROIImage, true);
					linearResponseModel.createRefMembraneOrFluxSimMathModel(geometry, basisROIImage, false);
					printReport();
				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});
		createmathmodelButton.setText("create reference models");

		final JButton runmathmodelButton = new JButton();
		runmathmodelButton.setHorizontalAlignment(SwingConstants.LEFT);
		computeButtonPanel.add(runmathmodelButton);
		runmathmodelButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					InverseProblemUtilities.runRefSimMathModel(inversePDERequestManager, inverseProblem.getLinearResponseModel().getRefVolumeSimMathModel());
					InverseProblemUtilities.runRefSimMathModel(inversePDERequestManager, inverseProblem.getLinearResponseModel().getRefMembraneSimMathModel());
					InverseProblemUtilities.runRefSimMathModel(inversePDERequestManager, inverseProblem.getLinearResponseModel().getRefFluxSimMathModel());
				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});
		runmathmodelButton.setText("run reference simulations");

		final JButton checkmathmodelButton = new JButton();
		checkmathmodelButton.setHorizontalAlignment(SwingConstants.LEFT);
		computeButtonPanel.add(checkmathmodelButton);
		checkmathmodelButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					InverseProblemUtilities.checkRefSimStatus(inversePDERequestManager, inverseProblem.getLinearResponseModel().getRefVolumeSimMathModel());
					InverseProblemUtilities.checkRefSimStatus(inversePDERequestManager, inverseProblem.getLinearResponseModel().getRefMembraneSimMathModel());
					InverseProblemUtilities.checkRefSimStatus(inversePDERequestManager, inverseProblem.getLinearResponseModel().getRefFluxSimMathModel());
				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});
		checkmathmodelButton.setText("check simulation status");

		final JButton readBasisResponseButton = new JButton();
		readBasisResponseButton.setHorizontalAlignment(SwingConstants.LEFT);
		computeButtonPanel.add(readBasisResponseButton);
		readBasisResponseButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					FileFilter fileFilter = new FileFilter() {
						public boolean accept(File f) {
							return f.isDirectory() || f.getName().endsWith(".mat");
						}
						public String getDescription() {
							return "matlab data files";
						}
				    };
				    File selectedFile = inversePDERequestManager.getSelectedSaveFile(InverseProblemPanel.this, fileFilter, "save referenceData as a Matlab file");
					InverseProblemUtilities.readRefData(inversePDERequestManager, inverseProblem, selectedFile.getAbsolutePath());
					System.out.println("done");
				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});
		readBasisResponseButton.setText("read simulation results");

		final JPanel displayPanel = new JPanel();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.rowHeights = new int[] {0,7,0,0,7,7,7,7,7};
		displayPanel.setLayout(gridBagLayout_1);
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.gridy = 2;
		gridBagConstraints_3.gridx = 0;
		add(displayPanel, gridBagConstraints_3);

		final JButton displayArtificialSimButton = new JButton();
		displayArtificialSimButton.setHorizontalAlignment(SwingConstants.LEFT);
		displayArtificialSimButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					if (inverseProblem==null){
						throw new RuntimeException("inverse problem is null");
					}
					if (inverseProblem.getExactSolutionEDI()==null){
						throw new RuntimeException("exact solution is null");
					}
					inverseProblem.getExactSolutionEDI();
					if (inverseProblem.getMicroscopyData().getTimeSeriesImageData()==null){
						throw new RuntimeException("time series image data is null");
					}
					PDEDataViewer pdeDataViewer = new PDEDataViewer();
					DataViewerManager dataViewerManager = new DataViewerManager(){
						public void addDataListener(DataListener newListener) {
						}
						public User getUser() {
							return null;
						}
						public UserPreferences getUserPreferences() {
							return null;
						}
						public void removeDataListener(DataListener newListener) {
						}
						public void showDataViewerPlotsFrames(JInternalFrame[] plotFrames) {
						}
						public void startExport(OutputContext outputContext, ExportSpecs exportSpecs) {
						}
						public void exportMessage(ExportEvent event) {
						}
						public void simStatusChanged(SimStatusEvent simStatusEvent) {
						}
						public void dataJobMessage(DataJobEvent event) {
						}
						public RequestManager getRequestManager() {
							return null;
						}
					};
					pdeDataViewer.setDataViewerManager(dataViewerManager);
					DataSetControllerProvider dataSetControllerProvider = inversePDERequestManager.getDataSetControllerProvider();
					VCDataManager vcDataManager = new VCDataManager(dataSetControllerProvider);
					PDEDataManager pdeDataManager = new PDEDataManager(outputContext,vcDataManager,inverseProblem.getExactSolutionEDI());
					NewClientPDEDataContext pdeDataContext = new NewClientPDEDataContext(pdeDataManager);
					pdeDataViewer.setPdeDataContext(pdeDataContext);
					DialogUtils.showComponentCloseDialog(InverseProblemPanel.this, pdeDataViewer, "simulation data");
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this,ex.getMessage());
				}
			}
		});
		displayArtificialSimButton.setText("display artificial sim results");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.gridx = 0;
		gridBagConstraints_5.gridy = 0;
		displayPanel.add(displayArtificialSimButton, gridBagConstraints_5);

		final JButton displayPsfButton = new JButton();
		displayPsfButton.setHorizontalAlignment(SwingConstants.LEFT);
		displayPsfButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					if (inverseProblem==null){
						throw new RuntimeException("inverse problem is null");
					}
					if (inverseProblem.getMicroscopyData()==null){
						throw new RuntimeException("microscopy data is null");
					}
					if (inverseProblem.getMicroscopyData().getPsfImageData()==null){
						throw new RuntimeException("psf image data is null");
					}
					ImageDataset imageDataset = inverseProblem.getMicroscopyData().getPsfImageData().getImageDataset();
					short[] imageData = imageDataset.getPixelsZ(0, 0);
					ISize size = imageDataset.getISize();
					InverseProblemUtilities.displayImage(InverseProblemPanel.this,imageData, size.getX(), size.getY(), size.getZ(), "PSF image data");
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this,ex.getMessage());
				}
			}
		});
		displayPsfButton.setText("display psf");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_10.gridx = 0;
		gridBagConstraints_10.gridy = 1;
		displayPanel.add(displayPsfButton, gridBagConstraints_10);

		final JButton displayTimeSeriesButton = new JButton();
		displayTimeSeriesButton.setHorizontalAlignment(SwingConstants.LEFT);
		displayTimeSeriesButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					if (inverseProblem==null){
						throw new RuntimeException("inverse problem is null");
					}
					if (inverseProblem.getMicroscopyData()==null){
						throw new RuntimeException("microscopy data is null");
					}
					if (inverseProblem.getMicroscopyData().getTimeSeriesImageData()==null){
						throw new RuntimeException("time series image data is null");
					}
					ImageDataset imageDataset = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getImageDataset();
					short[] imageData = imageDataset.getPixelsZ(0, 0);
					ISize size = imageDataset.getISize();
					InverseProblemUtilities.displayImage(InverseProblemPanel.this, imageData, size.getX(), size.getY(), size.getZ(), "Z-stack image data");
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this,ex.getMessage());
				}
			}
		});
		displayTimeSeriesButton.setText("display 2D time series");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.gridx = 0;
		gridBagConstraints_6.gridy = 2;
		displayPanel.add(displayTimeSeriesButton, gridBagConstraints_6);

		final JButton display2dProtocolButton = new JButton();
		display2dProtocolButton.setHorizontalTextPosition(SwingConstants.LEFT);
		display2dProtocolButton.setHorizontalAlignment(SwingConstants.LEADING);
		display2dProtocolButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					if (inverseProblem==null){
						throw new RuntimeException("inverse problem is null");
					}
					if (inverseProblem.getMicroscopyData()==null){
						throw new RuntimeException("microscopy data is null");
					}
					if (inverseProblem.getMicroscopyData().getTimeSeriesImageData()==null){
						throw new RuntimeException("time series image data is null");
					}
					ROIImage roiImage = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getROIImage(ROIImage.ROIIMAGE_PROTOCOL);
					if (roiImage==null){
						throw new RuntimeException("time series image protocol ROIImage is null");
					}
					InverseProblemUtilities.displayROIImage(InverseProblemPanel.this, roiImage);
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this,ex.getMessage());
				}
			}
		});
		display2dProtocolButton.setText("protocol roi");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.ipadx = 35;
		gridBagConstraints_9.anchor = GridBagConstraints.EAST;
		gridBagConstraints_9.gridx = 0;
		gridBagConstraints_9.gridy = 3;
		displayPanel.add(display2dProtocolButton, gridBagConstraints_9);

		final JButton display2dSegmentationButton_1 = new JButton();
		display2dSegmentationButton_1.setHorizontalTextPosition(SwingConstants.LEFT);
		display2dSegmentationButton_1.setHorizontalAlignment(SwingConstants.LEADING);
		display2dSegmentationButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					if (inverseProblem==null){
						throw new RuntimeException("inverse problem is null");
					}
					if (inverseProblem.getMicroscopyData()==null){
						throw new RuntimeException("microscopy data is null");
					}
					if (inverseProblem.getMicroscopyData().getTimeSeriesImageData()==null){
						throw new RuntimeException("time series image data is null");
					}
					ROIImage roiImage = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getROIImage(ROIImage.ROIIMAGE_SEGMENTATION);
					if (roiImage==null){
						throw new RuntimeException("time series image segmentation ROIImage is null");
					}
					InverseProblemUtilities.displayROIImage(InverseProblemPanel.this, roiImage);
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this,ex.getMessage());
				}
			}
		});
		display2dSegmentationButton_1.setText("seg roi");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.ipadx = 60;
		gridBagConstraints_7.anchor = GridBagConstraints.EAST;
		gridBagConstraints_7.gridx = 0;
		gridBagConstraints_7.gridy = 4;
		displayPanel.add(display2dSegmentationButton_1, gridBagConstraints_7);

		final JButton display2dImageRoisButton = new JButton();
		display2dImageRoisButton.setHorizontalTextPosition(SwingConstants.LEFT);
		display2dImageRoisButton.setHorizontalAlignment(SwingConstants.LEADING);
		display2dImageRoisButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					if (inverseProblem==null){
						throw new RuntimeException("inverse problem is null");
					}
					if (inverseProblem.getMicroscopyData()==null){
						throw new RuntimeException("microscopy data is null");
					}
					if (inverseProblem.getMicroscopyData().getTimeSeriesImageData()==null){
						throw new RuntimeException("time series image data is null");
					}
					ROIImage roiImage = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getROIImage(ROIImage.ROIIMAGE_IMAGEROIS);
					if (roiImage==null){
						throw new RuntimeException("time series image 'image rois' ROIImage is null");
					}
					InverseProblemUtilities.displayROIImage(InverseProblemPanel.this, roiImage);
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this,ex.getMessage());
				}
			}
		});
		display2dImageRoisButton.setText("image rois");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.ipadx = 40;
		gridBagConstraints_8.anchor = GridBagConstraints.EAST;
		gridBagConstraints_8.gridx = 0;
		gridBagConstraints_8.gridy = 5;
		gridBagConstraints_8.insets = new Insets(0, 0, 0, 0);
		displayPanel.add(display2dImageRoisButton, gridBagConstraints_8);

		final JButton displayZStackButton = new JButton();
		displayZStackButton.setHorizontalAlignment(SwingConstants.LEFT);
		displayZStackButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					if (inverseProblem==null){
						throw new RuntimeException("inverse problem is null");
					}
					if (inverseProblem.getMicroscopyData()==null){
						throw new RuntimeException("microscopy data is null");
					}
					if (inverseProblem.getMicroscopyData().getZStackImageData()==null){
						throw new RuntimeException("z stack image data is null");
					}
					ImageDataset imageDataset = inverseProblem.getMicroscopyData().getZStackImageData().getImageDataset();
					short[] imageData = imageDataset.getPixelsZ(0, 0);
					ISize size = imageDataset.getISize();
					InverseProblemUtilities.displayImage(InverseProblemPanel.this, imageData, size.getX(), size.getY(), size.getZ(), "Z-stack image data");
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this,ex.getMessage());
				}
			}
		});
		displayZStackButton.setText("display z stack");
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_11.gridx = 0;
		gridBagConstraints_11.gridy = 6;
		displayPanel.add(displayZStackButton, gridBagConstraints_11);

		final JButton stackSegRoiButton = new JButton();
		stackSegRoiButton.setHorizontalTextPosition(SwingConstants.LEFT);
		stackSegRoiButton.setHorizontalAlignment(SwingConstants.LEADING);
		stackSegRoiButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					if (inverseProblem==null){
						throw new RuntimeException("inverse problem is null");
					}
					if (inverseProblem.getMicroscopyData()==null){
						throw new RuntimeException("microscopy data is null");
					}
					if (inverseProblem.getMicroscopyData().getZStackImageData()==null){
						throw new RuntimeException("z-stack image data is null");
					}
					ROIImage roiImage = inverseProblem.getMicroscopyData().getZStackImageData().getROIImage(ROIImage.ROIIMAGE_SEGMENTATION);
					if (roiImage==null){
						throw new RuntimeException("z-stack segmentation ROIImage is null");
					}
					InverseProblemUtilities.displayROIImage(InverseProblemPanel.this, roiImage);
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this,ex.getMessage());
				}
			}
		});
		stackSegRoiButton.setText("stack seg roi");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.ipadx = 25;
		gridBagConstraints_12.anchor = GridBagConstraints.EAST;
		gridBagConstraints_12.gridy = 7;
		gridBagConstraints_12.gridx = 0;
		displayPanel.add(stackSegRoiButton, gridBagConstraints_12);

		final JButton stackProtocolRoiButton = new JButton();
		stackProtocolRoiButton.setHorizontalTextPosition(SwingConstants.LEFT);
		stackProtocolRoiButton.setHorizontalAlignment(SwingConstants.LEADING);
		stackProtocolRoiButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					if (inverseProblem==null){
						throw new RuntimeException("inverse problem is null");
					}
					if (inverseProblem.getMicroscopyData()==null){
						throw new RuntimeException("microscopy data is null");
					}
					if (inverseProblem.getMicroscopyData().getZStackImageData()==null){
						throw new RuntimeException("z-stack image data is null");
					}
					ROIImage roiImage = inverseProblem.getMicroscopyData().getZStackImageData().getROIImage(ROIImage.ROIIMAGE_PROTOCOL);
					if (roiImage==null){
						throw new RuntimeException("z-stack protocol ROIImage is null");
					}
					InverseProblemUtilities.displayROIImage(InverseProblemPanel.this, roiImage);
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this,ex.getMessage());
				}
			}
		});
		stackProtocolRoiButton.setText("stack protocol roi");
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.anchor = GridBagConstraints.EAST;
		gridBagConstraints_13.gridy = 8;
		gridBagConstraints_13.gridx = 0;
		displayPanel.add(stackProtocolRoiButton, gridBagConstraints_13);

		final JButton basisRoisButton = new JButton();
		basisRoisButton.setHorizontalTextPosition(SwingConstants.LEFT);
		basisRoisButton.setHorizontalAlignment(SwingConstants.LEADING);
		basisRoisButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					if (inverseProblem==null){
						throw new RuntimeException("inverse problem is null");
					}
					if (inverseProblem.getMicroscopyData()==null){
						throw new RuntimeException("microscopy data is null");
					}
					if (inverseProblem.getMicroscopyData().getZStackImageData()==null){
						throw new RuntimeException("z-stack image data is null");
					}
					ROIImage roiImage = inverseProblem.getMicroscopyData().getZStackImageData().getROIImage(ROIImage.ROIIMAGE_BASIS);
					if (roiImage==null){
						throw new RuntimeException("z-stack basis ROIImage is null");
					}
					InverseProblemUtilities.displayROIImage(InverseProblemPanel.this, roiImage);
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this,ex.getMessage());
				}
			}
		});
		basisRoisButton.setText("basis rois");
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.ipadx = 45;
		gridBagConstraints_14.anchor = GridBagConstraints.EAST;
		gridBagConstraints_14.gridy = 9;
		gridBagConstraints_14.gridx = 0;
		displayPanel.add(basisRoisButton, gridBagConstraints_14);

		final JPanel saveButtonPanel = new JPanel();
		saveButtonPanel.setLayout(new GridLayout(3, 0));
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_2.gridx = 0;
		gridBagConstraints_2.gridy = 3;
		gridBagConstraints_2.insets = new Insets(5, 5, 5, 5);
		add(saveButtonPanel, gridBagConstraints_2);
		
		final JButton exportTimeSeriesButton = new JButton();
		saveButtonPanel.add(exportTimeSeriesButton);
		exportTimeSeriesButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					FileFilter fileFilter = new FileFilter() {
						public boolean accept(File f) {
							return f.isDirectory() || f.getName().endsWith(".vfrap");
						}
						public String getDescription() {
							return "vfrap files";
						}
				    };
				    File selectedFile = inversePDERequestManager.getSelectedSaveFile(InverseProblemPanel.this, fileFilter, "save time series as VFRAP file");
				    UShortImage[] ushortImages = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getImageDataset().getAllImages();
				    double[] timestamps = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getImageDataset().getImageTimeStamps();
				    int numZ = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getImageDataset().getSizeZ();
				    ImageTimeSeries<UShortImage> imageTimeSeries = new ImageTimeSeries<UShortImage>(UShortImage.class,ushortImages,timestamps,numZ);
					new ExportRawTimeSeriesToVFrapOp().exportToVFRAP(selectedFile, imageTimeSeries, null);
					System.out.println("done");
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this,ex.getMessage());
				}
			}
		});
		exportTimeSeriesButton.setText("Export time series as tiff");
		
		final JButton exportVFRAPButton = new JButton();
		saveButtonPanel.add(exportVFRAPButton);
		exportVFRAPButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					FileFilter fileFilter = new FileFilter() {
						public boolean accept(File f) {
							return f.isDirectory() || f.getName().endsWith(".vfrap");
						}
						public String getDescription() {
							return "vfrap files";
						}
				    };
				    File selectedFile = inversePDERequestManager.getSelectedSaveFile(InverseProblemPanel.this, fileFilter, "save time series as VFRAP file");
					InverseProblemUtilities.exportVFRAPData(inverseProblem, selectedFile);
					System.out.println("done");
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this,ex.getMessage());
				}
			}
		});
		exportVFRAPButton.setText("Export time series as VFRAP");

		final JButton saveInverseproblemButton = new JButton();
		saveButtonPanel.add(saveInverseproblemButton);
		saveInverseproblemButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					FileFilter fileFilter = new FileFilter() {
						public boolean accept(File f) {
							return f.isDirectory() || f.getName().endsWith(".inverse");
						}
						public String getDescription() {
							return "inverse problem files";
						}
				    };
				    File selectedFile = inversePDERequestManager.getSelectedSaveFile(InverseProblemPanel.this, fileFilter, "save inverse problem document");
			    	DataSetControllerImpl.ProgressListener progressListener = null;
			    	boolean bSaveCompressed = true;
			    	InverseProblemXmlproducer.writeXMLFile(inverseProblem, selectedFile, true, progressListener, bSaveCompressed);
				}catch (Exception ex){
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(InverseProblemPanel.this, ex.getMessage());
				}
			}
		});
		saveInverseproblemButton.setText("Save InverseProblem");

		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.fill = GridBagConstraints.BOTH;
		gridBagConstraints_4.gridheight = 4;
		gridBagConstraints_4.weighty = 1;
		gridBagConstraints_4.weightx = 1;
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.gridx = 1;
		add(panel, gridBagConstraints_4);

		final JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		final JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.SOUTH);

		final JButton button_1 = new JButton();
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				printReport();
			}
		});
		button_1.setText("Report");
		panel_1.add(button_1);

		final JButton button_2 = new JButton();
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				getTextArea().setText("");
			}
		});
		button_2.setText("Clear");
		panel_1.add(button_2);
	}

	public InverseProblem getInverseProblem() {
		return inverseProblem;
	}

	public void setInverseProblem(InverseProblem inverseProblem) {
		this.inverseProblem = inverseProblem;
		refreshPanel();
	}
	
	private void printReport(){
		if (inverseProblem!=null){
			System.out.println(InverseProblemReport.getReport(inverseProblem));
		}else{
			System.out.println("inverse problem is null");
		}
	}
	
	private void refreshPanel(){
	}

//	public File getWorkingDirectory() {
//		return workingDirectory;
//	}

//	public void setClientServerManager(ClientServerManager clientServerManager){
//		this.clientServerManagerRemote = clientServerManager;
//	}
	
//	public void setWorkingDirectory(File workingDirectory) {
//		this.workingDirectory = workingDirectory;
//	}

//	private ClientServerManager getRemoteClientServerManager() throws Exception {
//		return clientServerManagerRemote;
//		//return getLocalClientServerManager();
//	}
//
//	private ClientServerManager getLocalClientServerManager() throws Exception {
//		if (clientServerManagerLocal==null && clientServerManagerRemote!=null){
//			String username = clientServerManagerLocal.getClientServerInfo().getUsername();
//			String password = clientServerManagerLocal.getClientServerInfo().getUserLoginInfo().getPassword();
//			ClientServerInfo localCSInfo = ClientServerInfo.createLocalServerInfo(username, password);
//			clientServerManagerLocal = new ClientServerManager();
//			clientServerManagerLocal.connect(null, localCSInfo);
//		}
//		return clientServerManagerLocal;
//	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setInversePDERequestManager(InversePDERequestManager inversePdeRequestManager) {
		this.inversePDERequestManager = inversePdeRequestManager;
	}

	


}
