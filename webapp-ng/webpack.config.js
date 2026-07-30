// Partial webpack config merged by @angular-builders/custom-webpack.
//
// vtk.js's XML readers (IO/XML/XMLReader -> xmlbuilder2 -> @oozcitak/dom -> @oozcitak/url)
// transitively `require('url')`, a Node core module. Angular's browser build target does not
// polyfill Node core modules, so without this fallback the build fails with
// "Module not found: Can't resolve 'url'". Point it at the browser `url` polyfill.
// See docs/salad-3d-renderer-design.md §6 (Phase 2 web-viz).
module.exports = {
  resolve: {
    fallback: {
      url: require.resolve('url/'),
    },
  },
};
