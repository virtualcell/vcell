import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';

// vtk.js — Phase 2 feasibility spike: render a VCell-style VTK ImageData scalar field in the browser
// (colormapped slice + isosurface). See docs/salad-3d-renderer-design.md §6.
import vtkGenericRenderWindow from '@kitware/vtk.js/Rendering/Misc/GenericRenderWindow';
import vtkImageData from '@kitware/vtk.js/Common/DataModel/ImageData';
import vtkDataArray from '@kitware/vtk.js/Common/Core/DataArray';
import vtkImageMapper from '@kitware/vtk.js/Rendering/Core/ImageMapper';
import vtkImageSlice from '@kitware/vtk.js/Rendering/Core/ImageSlice';
import vtkImageMarchingCubes from '@kitware/vtk.js/Filters/General/ImageMarchingCubes';
import vtkMapper from '@kitware/vtk.js/Rendering/Core/Mapper';
import vtkActor from '@kitware/vtk.js/Rendering/Core/Actor';
import vtkColorTransferFunction from '@kitware/vtk.js/Rendering/Core/ColorTransferFunction';
import vtkColorMaps from '@kitware/vtk.js/Rendering/Core/ColorTransferFunction/ColorMaps';

/**
 * Phase 2 spike (docs/salad-3d-renderer-design.md §6): prove that vtk.js renders a VCell-style
 * structured-grid scalar field (a PDE concentration field) in the browser — a colormapped slice
 * plane + an isosurface. The data is generated in-code here (identical shape to a VCell PDE field
 * on a CartesianMesh); loading a real VCell `.vti`/`.vtu` file is the documented next step (the
 * vtk.js XML reader transitively needs a Node `url` polyfill under Angular's default builder).
 */
@Component({
  selector: 'app-vtk-spike',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="vtk-wrap">
      <div #vtkContainer class="vtk-container"></div>
      <div class="caption">{{ status }}</div>
    </div>
  `,
  styles: [`
    .vtk-wrap { position: relative; width: 100%; height: calc(100vh - 120px); }
    .vtk-container { width: 100%; height: 100%; }
    .caption {
      position: absolute; top: 10px; left: 12px; color: #eee;
      font: 13px/1.4 monospace; background: rgba(0,0,0,0.45);
      padding: 5px 9px; border-radius: 4px; pointer-events: none;
    }
  `],
})
export class VtkSpikeComponent implements AfterViewInit, OnDestroy {
  @ViewChild('vtkContainer', { static: true }) vtkContainer!: ElementRef<HTMLDivElement>;
  status = 'Initializing vtk.js …';
  private grw: any;

  ngAfterViewInit(): void {
    try {
      const grw = vtkGenericRenderWindow.newInstance({ background: [0.09, 0.09, 0.13] });
      grw.setContainer(this.vtkContainer.nativeElement);
      grw.resize();
      this.grw = grw;
      const renderer: any = grw.getRenderer();
      const renderWindow: any = grw.getRenderWindow();

      // 1) build a VTK ImageData scalar field (stand-in for a VCell PDE concentration field)
      const N = 32;
      const values = new Float32Array(N * N * N);
      const blob = (x: number, y: number, z: number, cx: number, cy: number, cz: number, s: number) =>
        Math.exp(-(((x - cx) ** 2 + (y - cy) ** 2 + (z - cz) ** 2) / (2 * s * s)));
      let i = 0;
      for (let z = 0; z < N; z++)
        for (let y = 0; y < N; y++)
          for (let x = 0; x < N; x++)
            values[i++] = blob(x, y, z, 10, 12, 16, 5) + 0.8 * blob(x, y, z, 22, 20, 14, 4);

      const imageData: any = vtkImageData.newInstance();
      imageData.setDimensions(N, N, N);
      imageData.setSpacing(1, 1, 1);
      imageData.setOrigin(0, 0, 0);
      imageData.getPointData().setScalars(
        vtkDataArray.newInstance({ name: 'concentration', numberOfComponents: 1, values }),
      );
      const [min, max]: number[] = imageData.getPointData().getScalars().getRange();

      // 2) colormap
      const ctf: any = vtkColorTransferFunction.newInstance();
      ctf.applyColorMap(vtkColorMaps.getPresetByName('Cool to Warm'));
      ctf.setMappingRange(min, max);
      ctf.updateRange();

      // 3) colormapped slice through the middle (K/z)
      const imageMapper: any = vtkImageMapper.newInstance();
      imageMapper.setInputData(imageData);
      imageMapper.setKSlice(Math.floor(N / 2));
      const slice: any = vtkImageSlice.newInstance();
      slice.setMapper(imageMapper);
      slice.getProperty().setRGBTransferFunction(0, ctf);
      slice.getProperty().setColorWindow(max - min);
      slice.getProperty().setColorLevel((max + min) / 2);
      renderer.addViewProp(slice);

      // 4) translucent isosurface via marching cubes
      const mc: any = vtkImageMarchingCubes.newInstance({ contourValue: min + 0.45 * (max - min) });
      mc.setInputData(imageData);
      const mapper: any = vtkMapper.newInstance();
      mapper.setInputConnection(mc.getOutputPort());
      mapper.setScalarVisibility(false);
      const actor: any = vtkActor.newInstance();
      actor.setMapper(mapper);
      actor.getProperty().setColor(0.95, 0.6, 0.25);
      actor.getProperty().setOpacity(0.55);
      renderer.addActor(actor);

      renderer.resetCamera();
      renderWindow.render();

      this.status =
        `ImageData ${N}×${N}×${N} · scalar [${min.toFixed(2)}, ${max.toFixed(2)}] · ` +
        `colormapped slice + isosurface — rendered by vtk.js in the browser`;
    } catch (e: any) {
      this.status = 'vtk.js spike error: ' + (e?.message ?? String(e));
      // eslint-disable-next-line no-console
      console.error('vtk-spike', e);
    }
  }

  ngOnDestroy(): void {
    this.grw?.delete?.();
  }
}
