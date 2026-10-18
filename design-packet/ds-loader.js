// Resolves window.__dsReady with the design-system namespace.
// Uses the compiled _ds_bundle.js when present; otherwise evaluates the component sources directly (preview fallback).
(function () {
  var NS = 'PssstDesignSystem_7dc1e9';
  var base = document.currentScript.src.replace(/ds-loader\.js.*$/, '');
  var FILES = ['surfaces/Panel', 'surfaces/Slot', 'icons/AppIcon', 'docs/DocIcon', 'docs/StepNumber',
    'controls/Button', 'controls/TextField', 'controls/Checkbox', 'controls/Switch', 'controls/SegmentedTabs', 'controls/FilterChip',
    'data/StatusChip', 'data/ProgressBar', 'data/ItemSlot', 'navigation/TopBar', 'navigation/BottomNav', 'navigation/DrawerItem',
    'docs/Step', 'docs/SettingChip', 'docs/CodeBlock', 'docs/Callout', 'docs/DocTable', 'docs/ConnectionFlow', 'docs/FanFooter'];
  window.__dsReady = (async function () {
    if (window[NS]) return window[NS];
    var ns = {};
    for (var i = 0; i < FILES.length; i++) {
      var src = await (await fetch(base + 'components/' + FILES[i] + '.jsx')).text();
      var names = [], m, re = /export\s+(?:function|const)\s+(\w+)/g;
      while ((m = re.exec(src))) names.push(m[1]);
      var code = src.replace(/^import .*$/gm, '').replace(/export\s+(function|const)/g, '$1') + '\nreturn {' + names.join(',') + '};';
      var keys = Object.keys(ns);
      var out = Function.apply(null, ['React'].concat(keys, [code])).apply(null, [window.React].concat(keys.map(function (k) { return ns[k]; })));
      Object.assign(ns, out);
    }
    window[NS] = ns;
    return ns;
  })();
})();
