package org.vcell.vis.vtk;


import vtk.vtkActor;
import vtk.vtkCellData;
import vtk.vtkDataArray;
import vtk.vtkDataSetMapper;
import vtk.vtkLookupTable;
import vtk.vtkRenderWindow;
import vtk.vtkRenderWindowInteractor;
import vtk.vtkRenderer;
import vtk.vtkUnstructuredGrid;

public class SimpleVTKViewer {

	public void showGrid(vtkUnstructuredGrid uGrid, String field1Name, String field2Name) {
	
		    int numCells = uGrid.GetCells().GetNumberOfCells();
	
		    vtkLookupTable lut = new vtkLookupTable();
		    lut.SetNumberOfTableValues(256);
		    lut.SetHueRange(0.0,0.6);
		    lut.SetValueRange(1,1);
		    lut.SetAlphaRange(1,1);
		    lut.SetRange(0,numCells);
		    lut.Build();
	
		    vtkCellData cellData = uGrid.GetCellData();
		    
		    double[] bounds = uGrid.GetBounds();
		    double gridMinX = bounds[0];
		    double gridMaxX = bounds[1];
		    double gridMinY = bounds[2];
		    double gridMaxY = bounds[3];
		    double gridMinZ = bounds[4];
		    double gridMaxZ = bounds[5];
		 
	    System.out.println("grid bounds are from ("+gridMinX+","+gridMinY+","+gridMinZ+") to ("+gridMaxX+","+gridMaxY+","+gridMaxZ+")");
	    System.out.println("grid has "+cellData.GetNumberOfArrays()+" arrays");
	    	vtkDataArray cellScalars1 = cellData.GetArray(field1Name);
		    double[] dataRange = cellScalars1.GetRange();
		    double dataMin1 = dataRange[0];
		    double dataMax1 = dataRange[1];
	    System.out.println("data1 \""+field1Name+"\" range ["+dataMin1+","+dataMax1+"]");
		    vtkDataSetMapper mapper1 = new vtkDataSetMapper();
	        mapper1.SetInputData(uGrid);  // vtk 6.0
		    
		    mapper1.SetLookupTable(lut);
		    mapper1.SetScalarRange(dataMin1,dataMax1);
		    mapper1.SelectColorArray(field1Name);
		    mapper1.ScalarVisibilityOn();
		    mapper1.SetScalarModeToUseCellFieldData();
		  // mapper1.SetColorModeToMapScalars();
		    mapper1.Update();
	
		    vtkActor actor1 = new vtkActor();
		    actor1.SetMapper(mapper1);
		    // actor1.GetProperty().SetRepresentationToWireframe();
		    //actor1.AddPosition(0,0,0);
		    //actor1.GetProperty().SetColor(1,1,0);
		    actor1.GetProperty().SetOpacity(0.60);
		   // actor1.GetProperty().EdgeVisibilityOn();
		    actor1.GetProperty().SetEdgeColor(0,0,0);
		    //actor1.GetProperty().SetLineWidth(1.5);
		    
		    vtkDataArray cellScalars2 = cellData.GetArray(field2Name);
		   // cellData.SetActiveScalars('field2')
		    double[] dataRange2 = cellScalars2.GetRange();
		    double dataMin2 = dataRange2[0];
		    double dataMax2 = dataRange2[1];
	    System.out.println("data2 \""+field2Name+"\" range ["+dataMin2+","+dataMax2+"]");
		    vtkDataSetMapper mapper2 = new vtkDataSetMapper();
		    mapper2.SetInputData(uGrid);  // vtk 6.0
		    mapper2.SetLookupTable(lut);
		    mapper2.SetScalarRange(dataMin2,dataMax2);
		    mapper2.SelectColorArray(field2Name);
		    mapper2.ScalarVisibilityOn();
		    mapper2.SetScalarModeToUseCellFieldData();
		   // mapper2.SetColorModeToMapScalars();
		    mapper2.Update();
	
	
		    vtkActor actor2 = new vtkActor();
		    actor2.SetMapper(mapper2);
		    //actor2.GetProperty().SetRepresentationToWireframe();
		    actor2.AddPosition(1.1 * (gridMaxX-gridMinX),0,0);
		    //actor2.GetProperty().SetColor(1,1,0);
		    actor2.GetProperty().SetOpacity(0.60);
		   // actor2.GetProperty().EdgeVisibilityOn();
		    actor2.GetProperty().SetEdgeColor(0,0,0);
		    //actor2.GetProperty().SetLineWidth(1.5);
	
		    // Setup a renderer, render window, and interactor
		    vtkRenderer renderer = new vtkRenderer();
		    vtkRenderWindow renderWindow = new vtkRenderWindow();
		    //renderWindow.SetWindowName("Test");
		 
		    renderWindow.AddRenderer(renderer);
		    vtkRenderWindowInteractor renderWindowInteractor = new vtkRenderWindowInteractor();
		    renderWindowInteractor.SetRenderWindow(renderWindow);
		 
		    //Add the actor to the scene
		    renderer.AddActor(actor1);
		    renderer.AddActor(actor2);
		    renderer.SetBackground(0,0,0); //  Background color white
		    
		    //Render and interact
		    renderWindow.SetSize(900,900);
		    renderWindow.Render();
		    renderWindowInteractor.Start();
	}
	

}
