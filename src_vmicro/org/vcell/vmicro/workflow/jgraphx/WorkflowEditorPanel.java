/**
 * $Id: GraphEditor.java,v 1.2 2012/11/20 09:08:09 gaudenz Exp $
 * Copyright (c) 2006-2012, JGraph Ltd */
package org.vcell.vmicro.workflow.jgraphx;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.CellRendererPane;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.vcell.util.gui.DialogUtils;
import org.vcell.vmicro.workflow.ComputeMeasurementError;
import org.vcell.vmicro.workflow.DisplayDependentROIs;
import org.vcell.vmicro.workflow.DisplayImage;
import org.vcell.vmicro.workflow.DisplayPlot;
import org.vcell.vmicro.workflow.DisplayProfileLikelihoodPlots;
import org.vcell.vmicro.workflow.DisplayTimeSeries;
import org.vcell.vmicro.workflow.Generate2DOptContext;
import org.vcell.vmicro.workflow.Generate2DSimBioModel;
import org.vcell.vmicro.workflow.GenerateDependentImageROIs;
import org.vcell.vmicro.workflow.GenerateNormalizedFrapData;
import org.vcell.vmicro.workflow.GenerateReducedRefData;
import org.vcell.vmicro.workflow.ImportImageROIsFrom2DVCell;
import org.vcell.vmicro.workflow.ImportRawTimeSeriesFrom2DVCellConcentrations;
import org.vcell.vmicro.workflow.ImportRawTimeSeriesFromExperimentImage;
import org.vcell.vmicro.workflow.ImportRawTimeSeriesFromExperimentImages;
import org.vcell.vmicro.workflow.ImportRawTimeSeriesFromHdf5Fluor;
import org.vcell.vmicro.workflow.ImportTimeSeriesFromCSV;
import org.vcell.vmicro.workflow.NormalizeRawBleachData;
import org.vcell.vmicro.workflow.RunRefSimulation;
import org.vcell.vmicro.workflow.jgraphx.WorkflowJGraphProxy.WorkflowGraph;
import org.vcell.vmicro.workflow.jgraphx.WorkflowJGraphProxy.WorkflowObjectCell;
import org.vcell.workflow.Task;
import org.vcell.workflow.WorkflowObject.Status;
import org.w3c.dom.Document;

import com.mxgraph.io.mxCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

public class WorkflowEditorPanel extends BasicGraphEditor
{
	private WorkflowJGraphProxy workflowProxy = null;

	/**
	 * Holds the URL for the icon to be used as a handle for creating new
	 * connections. This is currently unused.
	 */
	public static URL url = null;

	//GraphEditor.class.getResource("/com/mxgraph/examples/swing/images/connector.gif");

	public WorkflowEditorPanel()
	{
		this("Workflow Editor", new CustomGraphComponent(new mxGraph()));
	}
	
	private Class[] ioClasses = new Class[] {
			ImportImageROIsFrom2DVCell.class,
			ImportRawTimeSeriesFrom2DVCellConcentrations.class,
			ImportRawTimeSeriesFromExperimentImage.class,
			ImportRawTimeSeriesFromExperimentImages.class,
			ImportRawTimeSeriesFromHdf5Fluor.class,
			ImportTimeSeriesFromCSV.class
	};
	
	private Class[] displayClasses = new Class[] {
			DisplayDependentROIs.class,
			DisplayImage.class,
			DisplayPlot.class,
			DisplayProfileLikelihoodPlots.class,
			DisplayTimeSeries.class,
	};
	
	private Class[] processingClasses = new Class[] {
			ComputeMeasurementError.class,
			Generate2DSimBioModel.class,
			GenerateDependentImageROIs.class,
			GenerateNormalizedFrapData.class,
			GenerateReducedRefData.class,
			NormalizeRawBleachData.class,
			Generate2DOptContext.class,
			RunRefSimulation.class,
	};
	
	/**
	 * 
	 */
	public WorkflowEditorPanel(String appTitle, mxGraphComponent component)
	{
		super(appTitle, component);
		final mxGraph graph = graphComponent.getGraph();

		// Creates the io palette
		EditorPalette inputPalette = insertPalette("Input");
		for (Class<? extends Task> cls : ioClasses){
			try {
				Constructor<? extends Task> constructor = cls.getConstructor(String.class);
				Task task = constructor.newInstance(cls.getSimpleName());
				inputPalette.addTemplate(task.getName(), new ImageIcon(WorkflowEditorPanel.class.getResource("/images/rectangle.png")), WorkflowJGraphProxy.createGenericCell(task));
			}catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
				e.printStackTrace();
			}
		}

		// Creates the shapes palette
		EditorPalette displayPalette = insertPalette("Display");
		for (Class<? extends Task> cls : displayClasses){
			try {
				Constructor<? extends Task> constructor = cls.getConstructor(String.class);
				Task task = constructor.newInstance(cls.getSimpleName());
				displayPalette.addTemplate(task.getName(), new ImageIcon(WorkflowEditorPanel.class.getResource("/images/rectangle.png")), WorkflowJGraphProxy.createGenericCell(task));
			}catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
				e.printStackTrace();
			}
		}

		// Creates the shapes palette
		EditorPalette processingPalette = insertPalette("Processing");
		for (Class<? extends Task> cls : processingClasses){
			try {
				Constructor<? extends Task> constructor = cls.getConstructor(String.class);
				Task task = constructor.newInstance(cls.getSimpleName());
				processingPalette.addTemplate(task.getName(), new ImageIcon(WorkflowEditorPanel.class.getResource("/images/rectangle.png")), WorkflowJGraphProxy.createGenericCell(task));
			}catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
				e.printStackTrace();
			}
		}
	}
	
	public static class SwingCanvas extends mxInteractiveCanvas implements Serializable
	{
		protected CellRendererPane rendererPane = new CellRendererPane();

		protected JLabel vertexRenderer = new JLabel();

		protected mxGraphComponent graphComponent;

		public SwingCanvas(mxGraphComponent graphComponent)
		{
			this.graphComponent = graphComponent;

			vertexRenderer.setHorizontalAlignment(JLabel.CENTER);
			vertexRenderer.setOpaque(false);
		}
		

		@Override
		public Object drawCell(mxCellState state)
		{
			Object object = super.drawCell(state);
			if (state.getCell() instanceof WorkflowObjectCell && ((WorkflowObjectCell)state.getCell()).workflowObject instanceof Task){
				WorkflowObjectCell cell = (WorkflowObjectCell)state.getCell();
				Status status = cell.workflowObject.getStatus();
				StringBuffer buffer = new StringBuffer("<html>");
				if (status.bOutputsDirty && !status.bRunning){
					buffer.append("<font color='red'><b>OUTPUTS_DIRTY</b></font><br>");
				}
				if (status.bRunning){
					buffer.append("<font color='green'><b>RUNNING</b></font><br>");
				}
				if (!status.bRunning && !status.bOutputsDirty){
					buffer.append("<font color='green'><b>DONE</b></font><br>");
				}
//				if (status.issues.length>0){
//					buffer.append("<ul>");
//					for (Issue issue : status.issues){
//						buffer.append("<li>"+issue.getMessage()+"</li>");
//					}
//					buffer.append("</ul>");
//				}
				buffer.append("</html>");
				vertexRenderer.setText(buffer.toString());
				
				rendererPane.paintComponent(g, vertexRenderer, graphComponent,
						(int) state.getX() + translate.x, (int) state.getY()
								+ translate.y, (int) state.getWidth(),
						(int) state.getHeight(), true);

				return cell;
			}
			return object;
		}

	}


	/**
	 * 
	 */
	public static class CustomGraphComponent extends mxGraphComponent
	{
		public CustomGraphComponent(mxGraph graph)
		{
			super(graph);

			// Sets switches typically used in an editor
//			setPageVisible(true);
//			setGridVisible(true);
			setToolTips(true);
//			getConnectionHandler().setCreateTarget(true);

			// Loads the defalt stylesheet from an external file
//			mxCodec codec = new mxCodec();
//			Document doc = mxUtils.loadDocument(WorkflowEditorPanel.class.getResource(
//					"/com/mxgraph/examples/swing/resources/default-style.xml")
//					.toString());
//			codec.decode(doc.getDocumentElement(), graph.getStylesheet());

			// Sets the background to white
			getViewport().setOpaque(true);
			getViewport().setBackground(Color.WHITE);
		}

		@Override
		public mxInteractiveCanvas createCanvas()
		{
			return new SwingCanvas(this);
		}
	}

	/**
	 * 
	 */
	protected void showGraphPopupMenu(MouseEvent e)
	{
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
				graphComponent);
		EditorPopupMenu menu = new EditorPopupMenu(this);
		menu.addSeparator();
		menu.add(bind("Run",  new AbstractAction("Run"){			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (workflowProxy!=null){
					try {
						workflowProxy.workflowRun();
					} catch (Exception e1) {
						e1.printStackTrace();
						DialogUtils.showErrorDialog(WorkflowEditorPanel.this, e1.getMessage());
					}
				}
			}}
		));

		menu.show(graphComponent, pt.x, pt.y);

		e.consume();
	}

	public void setWorkflowGraphProxy(WorkflowJGraphProxy workflowJGraphProxy) {
		this.workflowProxy = workflowJGraphProxy;
		WorkflowGraph graph = workflowJGraphProxy.getGraph();
		getGraphComponent().setGraph(graph);
		graph.setGraphComponent(getGraphComponent());
	}

}
