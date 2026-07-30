import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';

// vtk.js — Phase 2 feasibility spike: render a VCell-style VTK ImageData scalar field in the browser
// (colormapped slice + isosurface). See docs/salad-3d-renderer-design.md §6.
import vtkGenericRenderWindow from '@kitware/vtk.js/Rendering/Misc/GenericRenderWindow';
import vtkXMLImageDataReader from '@kitware/vtk.js/IO/XML/XMLImageDataReader';
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
 * plane + an isosurface. The field is loaded from a real VTK XML ImageData file
 * (`assets/sample.vti`) via vtk.js's `XMLImageDataReader`, proving the file→render path
 * end-to-end (the same `.vti` format VCell's export pipeline already produces, §2.3).
 *
 * The reader transitively `require`s Node's `url` module (xmlbuilder2 → @oozcitak/url); the app
 * uses `@angular-builders/custom-webpack` with a `resolve.fallback` to the browser `url` polyfill
 * (see webpack.config.js) so the browser build resolves it.
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

  async ngAfterViewInit(): Promise<void> {
    try {
      const grw = vtkGenericRenderWindow.newInstance({ background: [0.09, 0.09, 0.13] });
      grw.setContainer(this.vtkContainer.nativeElement);
      grw.resize();
      this.grw = grw;
      const renderer: any = grw.getRenderer();
      const renderWindow: any = grw.getRenderWindow();

      // 1) load a real VTK XML ImageData file (the .vti format VCell's export pipeline emits)
      this.status = 'Loading assets/sample.vti …';
      const resp = await fetch('assets/sample.vti');
      if (!resp.ok) throw new Error(`fetch sample.vti -> HTTP ${resp.status}`);
      const buffer = await resp.arrayBuffer();
      const reader: any = vtkXMLImageDataReader.newInstance();
      reader.parseAsArrayBuffer(buffer);
      const imageData: any = reader.getOutputData(0);
      const dims: number[] = imageData.getDimensions();
      const N = dims[2];
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
        `sample.vti loaded · ImageData ${dims[0]}×${dims[1]}×${dims[2]} · ` +
        `scalar [${min.toFixed(2)}, ${max.toFixed(2)}] · colormapped slice + isosurface ` +
        `— parsed & rendered by vtk.js in the browser`;
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
