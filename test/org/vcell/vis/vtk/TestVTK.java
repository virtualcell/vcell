package org.vcell.vis.vtk;

import org.junit.Before;
import org.junit.Test;
import org.vcell.util.PropertyLoader;

import vtk.vtkActor;
import vtk.vtkGeometryFilter;
import vtk.vtkIdTypeArray;
import vtk.vtkIntersectionPolyDataFilter;
import vtk.vtkNativeLibrary;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderWindow;
import vtk.vtkRenderWindowInteractor;
import vtk.vtkRenderer;
import vtk.vtkSphereSource;
import vtk.vtkUnstructuredGrid;
import vtk.vtkUnstructuredGridGeometryFilter;
import vtk.vtkWindowedSincPolyDataFilter;
import cbit.vcell.resource.NativeLib;

/**
 * Basic VTK test
 * @author jschaff 
 *
 */
public class TestVTK {

	@Before
	public void loadLibs( ) {
		try {
			if (PropertyLoader.getProperty(PropertyLoader.installationRoot,null) == null) {
				System.setProperty(PropertyLoader.installationRoot,System.getProperty("user.dir"));
			}
			NativeLib.VTK.load( );
			vtkNativeLibrary.LoadAllNativeLibraries();
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
		vtkNativeLibrary.DisableOutputWindow(null);
	}

	public static vtkUnstructuredGrid testWindowedSincPolyDataFilter(vtkUnstructuredGrid grid){
		
		vtkUnstructuredGridGeometryFilter ugGeometryFilter = new vtkUnstructuredGridGeometryFilter();
		ugGeometryFilter.PassThroughPointIdsOn();
		ugGeometryFilter.MergingOff();
		ugGeometryFilter.SetInputData(grid);
		ugGeometryFilter.Update();
		vtkUnstructuredGrid surfaceUnstructuredGrid = ugGeometryFilter.GetOutput();
		
		{
		int numCellArrays = surfaceUnstructuredGrid.GetCellData().GetNumberOfArrays();
		for (int i=0;i<numCellArrays;i++){
			String cellArrayName = surfaceUnstructuredGrid.GetCellData().GetArrayName(i);
			System.out.println("CellArray("+i+") \""+cellArrayName+"\"");
		}
		int numPointArrays = surfaceUnstructuredGrid.GetPointData().GetNumberOfArrays();
		for (int i=0;i<numPointArrays;i++){
			String pointArrayName = surfaceUnstructuredGrid.GetPointData().GetArrayName(i);
			System.out.println("PointArray("+i+") \""+pointArrayName+"\"");
		}
		}
		
		vtkGeometryFilter geometryFilter = new vtkGeometryFilter();
		geometryFilter.SetInputData(surfaceUnstructuredGrid);
		geometryFilter.Update();
		vtkPolyData polyData = geometryFilter.GetOutput();
		
		vtkWindowedSincPolyDataFilter filter = new vtkWindowedSincPolyDataFilter();
		filter.SetInputData(polyData);
		filter.SetNumberOfIterations(15);
		filter.BoundarySmoothingOff();
		filter.FeatureEdgeSmoothingOff();
		filter.SetFeatureAngle(120.0);
		filter.SetPassBand(0.001);
		filter.NonManifoldSmoothingOff();
		filter.NormalizeCoordinatesOn();
		filter.Update();
		
		vtkPolyData smoothedPolydata = filter.GetOutput();
		
		vtkPoints smoothedPoints = smoothedPolydata.GetPoints();
		String originalPointsIdsName = ugGeometryFilter.GetOriginalPointIdsName();
		vtkIdTypeArray pointIdsArray = (vtkIdTypeArray)smoothedPolydata.GetPointData().GetArray(originalPointsIdsName);
		int pointsIdsArraySize = pointIdsArray.GetSize();
		for (int i=0;i<pointsIdsArraySize;i++){
			int pointId = pointIdsArray.GetValue(i);
			double[] smoothedPoint = smoothedPoints.GetPoint(i);
			grid.GetPoints().SetPoint(pointId, smoothedPoint);
		}

		return grid;
	}
	
	
	@Test
	public void testIntersection(){
		  vtkSphereSource sphereSource1 = new vtkSphereSource();
		  sphereSource1.SetCenter(0.0, 0.0, 0.0);
		  sphereSource1.SetRadius(2.0f);
		  sphereSource1.Update();
		  vtkPolyDataMapper sphere1Mapper = new vtkPolyDataMapper();
		  sphere1Mapper.SetInputConnection( sphereSource1.GetOutputPort() );
		  sphere1Mapper.ScalarVisibilityOff();
		  vtkActor sphere1Actor = new vtkActor();
		  sphere1Actor.SetMapper( sphere1Mapper );
		  sphere1Actor.GetProperty().SetOpacity(.3);
		  sphere1Actor.GetProperty().SetColor(1,0,0);
		 
		  vtkSphereSource sphereSource2 = new vtkSphereSource();
		  sphereSource2.SetCenter(1.0, 0.0, 0.0);
		  sphereSource2.SetRadius(2.0f);
		  vtkPolyDataMapper sphere2Mapper = new vtkPolyDataMapper();
		  sphere2Mapper.SetInputConnection( sphereSource2.GetOutputPort() );
		  sphere2Mapper.ScalarVisibilityOff();
		  vtkActor sphere2Actor = new vtkActor();
		  sphere2Actor.SetMapper( sphere2Mapper );
		  sphere2Actor.GetProperty().SetOpacity(.3);
		  sphere2Actor.GetProperty().SetColor(0,1,0);
		 
		  vtkIntersectionPolyDataFilter intersectionPolyDataFilter = new vtkIntersectionPolyDataFilter();
		  intersectionPolyDataFilter.SetInputConnection( 0, sphereSource1.GetOutputPort() );
		  intersectionPolyDataFilter.SetInputConnection( 1, sphereSource2.GetOutputPort() );
		  intersectionPolyDataFilter.Update();
		 
		  vtkPolyDataMapper intersectionMapper = new vtkPolyDataMapper();
		  intersectionMapper.SetInputConnection( intersectionPolyDataFilter.GetOutputPort() );
		  intersectionMapper.ScalarVisibilityOff();
		 
		  vtkActor intersectionActor = new vtkActor();
		  intersectionActor.SetMapper( intersectionMapper );
		 
		  vtkRenderer renderer = new vtkRenderer();
		  renderer.AddViewProp(sphere1Actor);
		  renderer.AddViewProp(sphere2Actor);
		  renderer.AddViewProp(intersectionActor);
		 
		  vtkRenderWindow renderWindow = new vtkRenderWindow();
		  renderWindow.AddRenderer( renderer );
		 
		  vtkRenderWindowInteractor renWinInteractor = new vtkRenderWindowInteractor();
		  renWinInteractor.SetRenderWindow( renderWindow );
		 
		  renderWindow.Render();
		  renWinInteractor.Start();
	}


}
