package org.vcell.vmicro.op.display;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.vcell.util.Coordinate;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.DataAccessException;
import org.vcell.util.Range;
import org.vcell.util.gui.DialogUtils;

import cbit.image.DisplayAdapterService;
import cbit.image.ImageException;
import cbit.image.SourceDataInfo;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.image.gui.ImagePlaneManagerPanel;
import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.plot.gui.PlotPane;
import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.client.data.SimulationModelInfo.DataSymbolMetadataResolver;
import cbit.vcell.geometry.Curve;
import cbit.vcell.geometry.CurveSelectionInfo;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.gui.CurveEditorTool;
import cbit.vcell.math.MathException;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VolVariable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.SpatialSelectionVolume;
import cbit.vcell.simdata.gui.CurveValueProvider;
import cbit.vcell.solvers.CartesianMesh;

public class DisplayImageOp {
	
	public void displayImage(final Image image, String title, WindowListener listener) {
		final ImagePlaneManagerPanel imagePanel = new ImagePlaneManagerPanel();
		double[] doublePixels = image.getDoublePixels();
		double minPixel = Double.MAX_VALUE;
		double maxPixel = -Double.MAX_VALUE;
		for (int i=0;i<doublePixels.length;i++){
			double pixel = doublePixels[i];
			doublePixels[i] = pixel;
			minPixel = Math.min(minPixel,pixel);
			maxPixel = Math.max(maxPixel,pixel);
		}
		Range newRange = new Range(minPixel,maxPixel);
		SourceDataInfo source = new SourceDataInfo(
				SourceDataInfo.RAW_VALUE_TYPE, 
				doublePixels, 
				image.getExtent(), 
				image.getOrigin(), 
				newRange, 
				0, 
				image.getNumX(), 
				1, 
				image.getNumY(), 
				image.getNumX(), 
				image.getNumZ(), 
				image.getNumX()*image.getNumY());
		imagePanel.setDisplayAdapterServicePanelVisible(true);
		imagePanel.setCurveValueProvider(new CurveValueProvider(){

			@Override
			public void curveAdded(Curve curve) {
				System.out.println("called curveAdded("+curve+"), do nothing for now");
			}

			@Override
			public void curveRemoved(Curve curve) {
				System.out.println("called curveRemoved("+curve+")");
			}

			@Override
			public String getCurveValue(CurveSelectionInfo csi) {
				System.out.println("called getCurveValue(CurveSelectionInfo "+csi);
				return null;
			}

			@Override
			public CurveSelectionInfo getInitalCurveSelection(int tool,	Coordinate wc) {
				System.out.println("called getInitialCurveSelection(tool="+tool+", coord="+wc+")");
				return null;
			}

			@Override
			public boolean isAddControlPointOK(int tool, Coordinate wc,	Curve addedToThisCurve) {
				System.out.println("called isAddControlPointOK");
				return true;
			}

			@Override
			public boolean providesInitalCurve(int tool, Coordinate wc) {
				System.out.println("called providesInitialCurve(tool="+tool+" (TOOL_LINE="+CurveEditorTool.TOOL_LINE+"), coord="+wc);
				return false;
			}

			@Override
			public void setDescription(Curve curve) {
				System.out.println("called setDescription("+curve+")");
				curve.setDescription(CurveValueProvider.DESCRIPTION_VOLUME);
			}

			@Override
			public CurveSelectionInfo findChomboCurveSelectionInfoForPoint(CoordinateIndex ci) {
				System.out.println("called find ChomboCurveSelectionInfoForPoint(coord="+ci+")");
				return null;
			}
			
		});
		DisplayAdapterService das = imagePanel.getDisplayAdapterServicePanel().getDisplayAdapterService();
		das.setValueDomain(null);
		das.addColorModelForValues(
			DisplayAdapterService.createGrayColorModel(), 
			DisplayAdapterService.createGraySpecialColors(),
			DisplayAdapterService.GRAY);
		das.addColorModelForValues(
			DisplayAdapterService.createBlueRedColorModel(),
			DisplayAdapterService.createBlueRedSpecialColors(),
			DisplayAdapterService.BLUERED);
		das.setActiveColorModelID(DisplayAdapterService.BLUERED);

		final JFrame jframe = new JFrame();
		jframe.setTitle(title);
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints imageConstraints = new GridBagConstraints();
		imageConstraints.gridx = 0;
		imageConstraints.gridy = 0;
		imageConstraints.weightx = 1.0;
		imageConstraints.weighty = 1.0;
		imageConstraints.fill = GridBagConstraints.BOTH;
		panel.add(imagePanel, imageConstraints);
		JButton plotButton = new JButton("plot");
		plotButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Curve curve = imagePanel.getCurveRenderer().getSelection().getCurve();
					VariableType variableType = VariableType.VOLUME;
					Curve samplerCurve = curve.getSampledCurve();
					samplerCurve.setDescription(curve.getDescription());
					
					VCImage vcImage = new VCImageUncompressed(null, new byte[image.getISize().getXYZ()], image.getExtent(), image.getISize().getX(), image.getISize().getY(), image.getISize().getZ());
					int dimension = 1+(image.getISize().getY()>0?1:0)+(image.getISize().getZ()>0?1:0);
					RegionImage regionImage = new RegionImage(vcImage, dimension, image.getExtent(), image.getOrigin(), RegionImage.NO_SMOOTHING);
					CartesianMesh mesh = CartesianMesh.createSimpleCartesianMesh(image.getOrigin(), image.getExtent(), image.getISize(), regionImage);
					SpatialSelectionVolume ssVolume = new SpatialSelectionVolume(new CurveSelectionInfo(samplerCurve),variableType, mesh);
					String varName = "var";
					SymbolTableEntry[] symbolTableEntries = new SymbolTableEntry[] { new VolVariable(varName, null) };
					
					PlotData plotData = getLineScan(ssVolume, image, mesh);
					PlotPane plotPane = new PlotPane();
					DataSymbolMetadataResolver resolver = null;
					Plot2D plot2D = new Plot2D(symbolTableEntries, resolver, new String[] { varName },new PlotData[] { plotData },
								new String[] {"Values along curve", "Distance (\u00b5m)", "[" + varName + "]"});
					plotPane.setPlot2D(	plot2D);
					DialogUtils.showComponentCloseDialog(jframe, plotPane, "plot");
				} catch (ImageException | IOException | DataAccessException | MathException e1) {
					e1.printStackTrace();
				}
			}
		});

		GridBagConstraints plotButtonConstraints = new GridBagConstraints();
		plotButtonConstraints.gridx = 0;
		plotButtonConstraints.gridy = 1;
		panel.add(plotButton,plotButtonConstraints);
		jframe.getContentPane().add(panel);
		jframe.setSize(500,500);
		jframe.addWindowListener(listener);
		jframe.setVisible(true);
		
		imagePanel.setSourceDataInfo(source);
	}
	
	public PlotData getLineScan(SpatialSelectionVolume ssVolume, Image image, CartesianMesh mesh) throws DataAccessException, MathException {
		ssVolume.setMesh(mesh);
		
		double data[] = image.getDoublePixels();
		
		SpatialSelection.SSHelper ssvHelper = ssVolume.getIndexSamples(0.0,1.0);
		ssvHelper.initializeValues_VOLUME(data);
		double[] values = ssvHelper.getSampledValues();
		return new PlotData(ssvHelper.getWorldCoordinateLengths(), values);
	}

}
