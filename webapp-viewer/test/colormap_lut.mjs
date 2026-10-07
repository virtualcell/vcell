/**
 * LUT endpoints and CIELAB L* for the shared field-viewer tables.
 * Prints one JSON object. The pytest wrapper asserts it.
 */
import { CIVIDIS_RGB, RAINBOW_RGB } from '../colormap.js';

function linearize(channel) {
  const c = channel / 255;
  return c <= 0.04045 ? c / 12.92 : ((c + 0.055) / 1.055) ** 2.4;
}

function cielabL(rgb, i) {
  const r = linearize(rgb[3 * i]);
  const g = linearize(rgb[3 * i + 1]);
  const b = linearize(rgb[3 * i + 2]);
  const y = 0.2126729 * r + 0.7151522 * g + 0.0721750 * b;
  const delta = 6 / 29;
  const f = y > delta ** 3 ? Math.cbrt(y) : y / (3 * delta * delta) + 4 / 29;
  return 116 * f - 16;
}

function monotonic(rgb) {
  let previous = cielabL(rgb, 0);
  for (let i = 1; i < 256; i++) {
    const lightness = cielabL(rgb, i);
    if (lightness <= previous) return false;
    previous = lightness;
  }
  return true;
}

const report = {
  cividisLength: CIVIDIS_RGB.length,
  cividisLow: [CIVIDIS_RGB[0], CIVIDIS_RGB[1], CIVIDIS_RGB[2]],
  cividisHigh: [CIVIDIS_RGB[765], CIVIDIS_RGB[766], CIVIDIS_RGB[767]],
  cividisMonotonic: monotonic(CIVIDIS_RGB),
  rainbowLength: RAINBOW_RGB.length,
  rainbowLow: [RAINBOW_RGB[0], RAINBOW_RGB[1], RAINBOW_RGB[2]],
  rainbowHigh: [RAINBOW_RGB[765], RAINBOW_RGB[766], RAINBOW_RGB[767]],
};
process.stdout.write(JSON.stringify(report));
