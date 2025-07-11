const fs = require('fs');
const fsp = fs.promises;
const path = require('path');
const readline = require('readline');
const crypto = require('crypto');

process.on('unhandledRejection', err => {
  console.error('Unhandled rejection:', err);
  process.exit(1);
});

process.on('uncaughtException', err => {
  console.error('Uncaught exception:', err);
  process.exit(1);
});

const releasesDir = path.join(__dirname, 'releases');
const baseReportsDir = path.join(__dirname, 'reports');
let reportsDir = baseReportsDir;
let diffsDir = path.join(reportsDir, 'diffs');
let styBreakdownDir = path.join(reportsDir, 'sty_breakdowns');
let stySourceDiffDir = path.join(reportsDir, 'sty_source_diffs');
let configFile = path.join(reportsDir, 'config.json');
const userConfigPath = path.join(__dirname, 'report-config.json');
// If --data-only is passed, skip generating HTML output
const generateHtml = !process.argv.includes('--data-only');

function hashOf(str) {
  return crypto.createHash('sha256').update(str).digest('hex');
}

async function loadReportConfig() {
  try {
    const raw = await fsp.readFile(userConfigPath, 'utf-8');
    return JSON.parse(raw);
  } catch {
    return {};
  }
}

function wrapHtml(title, body) {
  const style = '<style>table{width:100%;border-collapse:collapse;border:1px solid #ccc;margin-top:10px;font-size:0.9em}table th,table td{border:1px solid #ccc;padding:6px 10px;text-align:left}thead{background-color:#f2f2f2}</style>';
  const crumbs = '<nav class="breadcrumbs"><a href="../../index.html">Home</a></nav>';
  return `<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>${title}</title><link rel="stylesheet" href="../../css/styles.css">${style}</head><body>${crumbs}<h1>${title}</h1>${body}<script src="../../js/sortable.js"></script></body></html>`;
}

function wrapDiffHtml(title, body, parentTitle = '', parentLink = '') {
  const style = '<style>table{width:100%;border-collapse:collapse;border:1px solid #ccc;margin-top:10px;font-size:0.9em}table th,table td{border:1px solid #ccc;padding:6px 10px;text-align:left}thead{background-color:#f2f2f2}</style>';
  let crumbs = '<nav class="breadcrumbs"><a href="../../../index.html">Home</a>';
  if (parentTitle && parentLink) {
    crumbs += ` &gt; <a href="${parentLink}">${parentTitle}</a>`;
  }
  crumbs += '</nav>';
  return `<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>${title}</title><link rel="stylesheet" href="../../../css/styles.css">${style}</head><body>${crumbs}<h1>${title}</h1>${body}<script src="../../../js/sortable.js"></script></body></html>`;
}

async function detectReleases() {
  let releaseList = [];
  try {
    await fsp.access(releasesDir);
    const entries = await fsp.readdir(releasesDir);
    for (const entry of entries) {
      const full = path.join(releasesDir, entry);
      try {
        const stat = await fsp.stat(full);
        if (stat.isDirectory()) {
          const subEntries = await fsp.readdir(full);
          if (subEntries.some(d => d.toLowerCase() === 'meta')) {
            releaseList.push(entry);
          }
        }
      } catch {}
    }
  } catch {}
  releaseList.sort().reverse();
  return { current: releaseList[0] || null, previous: releaseList[1] || null };
}

async function safeLineCount(file) {
  try {
    let count = 0;
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const _ of rl) {
      count++;
    }
    return count;
  } catch {
    return null;
  }
}

async function readAllLines(file) {
  const lines = [];
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const line of rl) {
      lines.push(line);
    }
  } catch {
    return [];
  }
  return lines;
}

async function listFiles(dir, base = dir) {
  let result = [];
  const entries = await fsp.readdir(dir, { withFileTypes: true });
  for (const entry of entries) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      const sub = await listFiles(full, base);
      result = result.concat(sub.map(s => path.join(entry.name, s)));
    } else {
      result.push(path.relative(base, full));
    }
  }
  return result;
}

async function generateLineCountDiff(current, previous) {
  const currentMeta = path.join(releasesDir, current, 'META');
  const previousMeta = path.join(releasesDir, previous, 'META');
  const result = [];
  const currFiles = await listFiles(currentMeta).catch(() => []);
  const prevFiles = await listFiles(previousMeta).catch(() => []);
  const allFiles = Array.from(new Set([...currFiles, ...prevFiles]));

  for (const name of allFiles) {
    const cur = await safeLineCount(path.join(currentMeta, name));
    const prev = await safeLineCount(path.join(previousMeta, name));
    if (cur === null && prev === null) continue;
    const diff = (cur ?? 0) - (prev ?? 0);
    const percent = prev === 0 || prev === null ? Infinity : (diff / prev * 100);
    let link = '';
    const base = path.basename(name);
    if (/^MRCONSO\.RRF$/i.test(base)) link = 'MRCONSO_report.html';
    else if (/^MRSTY\.RRF$/i.test(base)) link = 'MRSTY_report.html';
    else if (/^MRSAB\.RRF$/i.test(base)) link = 'MRSAB_report.html';
    else if (/^MRDEF\.RRF$/i.test(base)) link = 'MRDEF_report.html';
    else if (/^MRREL\.RRF$/i.test(base)) link = 'MRREL_report.html';
    else if (/^MRSAT\.RRF$/i.test(base)) link = 'MRSAT_report.html';
    else if (/^MRHIER\.RRF$/i.test(base)) link = 'MRHIER_report.html';
    else if (/^MRDOC\.RRF$/i.test(base)) link = 'MRDOC_report.html';
    else if (/^MRCOLS\.RRF$/i.test(base)) link = 'MRCOLS_report.html';
    else if (/^MRFILES\.RRF$/i.test(base)) link = 'MRFILES_report.html';
    else if (/^MRRANK\.RRF$/i.test(base)) link = 'MRRANK_report.html';
    result.push({ name, current: cur, previous: prev, diff, percent, link });
  }

  await fsp.mkdir(reportsDir, { recursive: true });
  const jsonPath = path.join(reportsDir, 'line-count-diff.json');
  await fsp.writeFile(jsonPath, JSON.stringify({ current, previous, files: result }, null, 2));

  let html = `<h3>Line Count Comparison (${current} vs ${previous})</h3>`;
  html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>File</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th><th>Report</th><th>Notes</th></tr></thead><tbody>';
  const unchanged = [];
  for (const f of result) {
    if (f.diff === 0) { unchanged.push(f.name); continue; }
    const diffClass = f.diff < 0 ? 'negative' : 'positive';
    const pct = isFinite(f.percent) ? f.percent.toFixed(2) : 'inf';
    const linkCell = f.link ? `<a href="${f.link}">view</a>` : '';
    html += `<tr><td>${f.name}</td><td>${f.previous ?? 0}</td><td>${f.current ?? 0}</td><td class="${diffClass}">${f.diff}</td><td>${pct}</td><td>${linkCell}</td><td class="editable line-note" data-file="${f.name}"></td></tr>`;
  }
  html += '</tbody></table>';
  if (unchanged.length) {
    html += `<p>Unchanged files: ${unchanged.join(', ')}</p>`;
  }
  html += `<script type="module">
    async function load() {
      try {
        const resp = await fetch('/api/texts');
        const data = resp.ok ? await resp.json() : {};
        const notes = data.lineCountNotes || {};
        document.querySelectorAll('td[data-file]').forEach(td => {
          td.textContent = notes[td.dataset.file] || td.textContent;
          td.contentEditable = true;
          td.addEventListener('blur', save);
        });
      } catch {}
    }
    async function save() {
      const notes = {};
      document.querySelectorAll('td[data-file]').forEach(td => {
        notes[td.dataset.file] = td.textContent;
      });
      try {
        await fetch('/api/texts', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ lineCountNotes: notes }) });
      } catch {}
    }
    load();
  </script>`;
  const wrapped = wrapHtml('Line Count Comparison', html);
  if (generateHtml) {
    await fsp.writeFile(path.join(reportsDir, 'line-count-diff.html'), wrapped);
  }
}

function escapeHTML(str) {
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

function linesToHtmlTable(lines) {
  if (!lines.length) return '';
  const firstParts = lines[0].split('|');
  if (firstParts[firstParts.length - 1] === '') firstParts.pop();
  let html = '<table><thead><tr>';
  for (let i = 0; i < firstParts.length; i++) {
    html += `<th>${i + 1}</th>`;
  }
  html += '</tr></thead><tbody>';
  for (const line of lines) {
    const parts = line.split('|');
    if (parts[parts.length - 1] === '') parts.pop();
    html += '<tr>' + parts.map(p => `<td>${escapeHTML(p)}</td>`).join('') + '</tr>';
  }
  html += '</tbody></table>';
  return html;
}

async function readCountsMRCONSO(file) {
  const counts = new Map();
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const line of rl) {
      const parts = line.split('|');
      if (parts.length < 13) continue;
      const SAB = parts[11] || 'MISSING';
      const TTY = parts[12] || 'MISSING';
      const key = `${SAB}|${TTY}`;
      counts.set(key, (counts.get(key) || 0) + 1);
    }
  } catch {
    return new Map();
  }
  return counts;
}

async function readCountsByIndices(file, indices) {
  const counts = new Map();
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const line of rl) {
      const parts = line.split('|');
      const keyParts = [];
      for (const idx of indices) {
        keyParts.push(parts[idx] || 'MISSING');
      }
      const key = keyParts.join('|');
      counts.set(key, (counts.get(key) || 0) + 1);
    }
  } catch {
    return new Map();
  }
  return counts;
}

// Read all lines from a file but return only specific fields joined by "|"
async function readKeysByIndices(file, indices) {
  const keys = [];
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const line of rl) {
      const parts = line.split('|');
      const keyParts = [];
      for (const idx of indices) {
        keyParts.push(parts[idx] || 'MISSING');
      }
      keys.push(keyParts.join('|'));
    }
  } catch {
    return [];
  }
  return keys;
}

// Read lines from a file and group them by a key built from the given columns
// Returns a Map of key -> array of raw line strings
async function readLineMapByIndices(file, indices) {
  const map = new Map();
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const line of rl) {
      const parts = line.split('|');
      const keyParts = [];
      for (const idx of indices) {
        keyParts.push(parts[idx] || 'MISSING');
      }
      const key = keyParts.join('|');
      if (!map.has(key)) map.set(key, []);
      map.get(key).push(line);
    }
  } catch {
    return new Map();
  }
  return map;
}

async function gatherRows(file, keys) {
  const rows = new Map();
  for (const key of keys) rows.set(key, []);
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const line of rl) {
      const parts = line.split('|');
      if (parts.length < 18) continue;
      const CUI = parts[0];
      const AUI = parts[7];
      const SAB = parts[11] || 'MISSING';
      const TTY = parts[12] || 'MISSING';
      const STR = parts[14];
      if (!AUI) continue;
      const key = `${SAB}|${TTY}`;
      if (rows.has(key)) {
        rows.get(key).push({ SAB, TTY, CUI, AUI, STR });
      }
    }
  } catch {
    return new Map();
  }
  return rows;
}


function buildDiffData(sab, tty, baseRows, prevRows) {
  const base = baseRows;
  const prev = prevRows;
  if (!base.length && !prev.length) return null;
  const baseMap = new Map(base.map(r => [r.AUI, r]));
  const prevMap = new Map(prev.map(r => [r.AUI, r]));
  const added = [];
  const dropped = [];
  const moved = [];
  for (const [aui, row] of baseMap) {
    if (!prevMap.has(aui)) {
      added.push(row);
    }
  }
  for (const [aui, row] of prevMap) {
    if (!baseMap.has(aui)) {
      dropped.push(row);
    } else {
      const cur = baseMap.get(aui);
      if (cur.CUI !== row.CUI && cur.STR === row.STR) {
        moved.push({ AUI: aui, currentCUI: cur.CUI, previousCUI: row.CUI, STR: cur.STR });
      }
    }
  }
  if (!added.length && !dropped.length && !moved.length) return null;
  return { sab, tty, added, dropped, moved };
}

function diffDataToHtml(data) {
  const links = [];
  if (data.added && data.added.length) links.push('<a href="#added">Added</a>');
  if (data.dropped && data.dropped.length) links.push('<a href="#dropped">Dropped</a>');
  if (data.moved && data.moved.length) links.push('<a href="#moved">Moved</a>');

  let html = `<h3>${data.sab} ${data.tty} Differences</h3>`;
  if (links.length) {
    html += `<div class="sticky-links">${links.join(' | ')}</div>`;
  }

  if (data.added && data.added.length) {
    html += `<h4 id="added">Added (${data.added.length})</h4>`;
    html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>CUI</th><th>AUI</th><th>STR</th></tr></thead><tbody>';
    for (const row of data.added) {
      html += `<tr><td>${row.CUI}</td><td>${row.AUI}</td><td>${escapeHTML(row.STR)}</td></tr>`;
    }
    html += '</tbody></table>';
  }

  if (data.dropped && data.dropped.length) {
    html += `<h4 id="dropped">Dropped (${data.dropped.length})</h4>`;
    html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>CUI</th><th>AUI</th><th>STR</th></tr></thead><tbody>';
    for (const row of data.dropped) {
      html += `<tr><td>${row.CUI}</td><td>${row.AUI}</td><td>${escapeHTML(row.STR)}</td></tr>`;
    }
    html += '</tbody></table>';
  }

  if (data.moved && data.moved.length) {
    html += `<h4 id="moved">Moved (${data.moved.length})</h4>`;
    html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>AUI</th><th>Previous CUI</th><th>Current CUI</th><th>STR</th></tr></thead><tbody>';
    for (const row of data.moved) {
      html += `<tr><td>${row.AUI}</td><td>${row.previousCUI}</td><td>${row.currentCUI}</td><td>${escapeHTML(row.STR)}</td></tr>`;
    }
    html += '</tbody></table>';
  }

  return wrapDiffHtml(
    `${data.sab} ${data.tty} Differences`,
    html,
    'MRCONSO Report',
    '../MRCONSO_report.html'
  );
}

function stySabDiffToHtml(data) {
  let html = `<h3>${escapeHTML(data.sty)} - ${escapeHTML(data.sab)} Changes</h3>`;
  if (data.added && data.added.length) {
    html += `<h4>Added (${data.added.length})</h4>`;
    html += '<table><thead><tr><th>CUI</th><th>Name</th></tr></thead><tbody>';
    for (const r of data.added) {
      html += `<tr><td>${r.CUI}</td><td>${escapeHTML(r.Name || '')}</td></tr>`;
    }
    html += '</tbody></table>';
  }
  if (data.removed && data.removed.length) {
    html += `<h4>Removed (${data.removed.length})</h4>`;
    html += '<table><thead><tr><th>CUI</th><th>Name</th></tr></thead><tbody>';
    for (const r of data.removed) {
      html += `<tr><td>${r.CUI}</td><td>${escapeHTML(r.Name || '')}</td></tr>`;
    }
    html += '</tbody></table>';
  }
  if (!data.added.length && !data.removed.length) {
    html += '<p>No changes.</p>';
  }
  return wrapDiffHtml(
    `${data.sty} ${data.sab} Changes`,
    html,
    'MRSTY Report',
    '../MRSTY_report.html'
  );
}

async function generateSABDiff(current, previous) {
  const currentFile = path.join(releasesDir, current, 'META', 'MRCONSO.RRF');
  const previousFile = path.join(releasesDir, previous, 'META', 'MRCONSO.RRF');
  const baseCounts = await readCountsMRCONSO(currentFile);
  const prevCounts = await readCountsMRCONSO(previousFile);

  await fsp.mkdir(diffsDir, { recursive: true });
  const summary = [];
  const allKeys = new Set([...baseCounts.keys(), ...prevCounts.keys()]);
  const detailKeys = new Set();
  for (const key of allKeys) {
    const [sab, tty] = key.split('|');
    const currentCount = baseCounts.get(key) || 0;
    const previousCount = prevCounts.get(key) || 0;
    const difference = currentCount - previousCount;
    const percent = previousCount === 0 ? Infinity : (difference / previousCount * 100);
    const include = percent < 0 || percent > 5 || sab === 'SRC';
    const entry = { SAB: sab, TTY: tty, Previous: previousCount, Current: currentCount, Difference: difference, Percent: percent, link: '', include };
    if (include) detailKeys.add(key);
    summary.push(entry);
  }

  // Gather rows for all selected SAB/TTY pairs in a single pass to avoid
  // repeatedly scanning the large MRCONSO files.
  if (detailKeys.size) {
    const baseRowsMap = await gatherRows(currentFile, detailKeys);
    const prevRowsMap = await gatherRows(previousFile, detailKeys);

    for (const entry of summary) {
      const key = `${entry.SAB}|${entry.TTY}`;
      if (!detailKeys.has(key)) continue;
      const baseRows = baseRowsMap.get(key) || [];
      const prevRows = prevRowsMap.get(key) || [];
      const diffData = buildDiffData(entry.SAB, entry.TTY, baseRows, prevRows);
      if (diffData) {
        const fileName = `${entry.SAB}_${entry.TTY}_differences.json`;
        const filePath = path.join(diffsDir, fileName);
        await fsp.writeFile(filePath, JSON.stringify(diffData, null, 2));
        const htmlName = fileName.replace(/\.json$/, '.html');
        if (generateHtml) {
          await fsp.writeFile(path.join(diffsDir, htmlName), diffDataToHtml(diffData));
        }
        entry.link = `diffs/${fileName}`;
      }
    }
  }

  const summaryPath = path.join(reportsDir, 'MRCONSO_report.json');
  await fsp.writeFile(summaryPath, JSON.stringify({ current, previous, summary }, null, 2));

  const notable = summary.filter(r => r.include);
  let html = `<h3>MRCONSO SAB/TTY Differences (${current} vs ${previous})</h3>`;
  if (notable.length) {
    html += '<h4>Notable Changes</h4>';
    html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>SAB</th><th>TTY</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th><th>Diff</th></tr></thead><tbody>';
    for (const row of notable) {
      const diffClass = row.Difference < 0 ? 'negative' : 'positive';
      const pct = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
      const linkCell = row.link ? `<a href="${row.link.replace(/\.json$/, '.html')}">view</a>` : '';
      html += `<tr><td>${row.SAB}</td><td>${row.TTY}</td><td>${row.Previous}</td><td>${row.Current}</td><td class="${diffClass}">${row.Difference}</td><td>${pct}</td><td>${linkCell}</td></tr>`;
    }
    html += '</tbody></table>';
  }

  html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>SAB</th><th>TTY</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th><th>Diff</th></tr></thead><tbody>';
  for (const row of summary) {
    const diffClass = row.Difference < 0 ? 'negative' : 'positive';
    const pct = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
    const linkCell = row.link ? `<a href="${row.link.replace(/\.json$/, '.html')}">view</a>` : '';
    html += `<tr><td>${row.SAB}</td><td>${row.TTY}</td><td>${row.Previous}</td><td>${row.Current}</td><td class="${diffClass}">${row.Difference}</td><td>${pct}</td><td>${linkCell}</td></tr>`;
  }
  html += '</tbody></table>';
  const wrapped = wrapHtml('MRCONSO Report', html);
  if (generateHtml) {
    await fsp.writeFile(path.join(reportsDir, 'MRCONSO_report.html'), wrapped);
  }
}

// Read mapping of CUI to set of Semantic Types (STYs)
async function readSTYMap(file) {
  const map = new Map();
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const line of rl) {
      const parts = line.split('|');
      if (parts.length < 4) continue;
      const CUI = parts[0];
      const STY = parts[3];
      if (!map.has(CUI)) map.set(CUI, new Set());
      map.get(CUI).add(STY);
    }
  } catch {
    return new Map();
  }
  return map;
}

// Read mapping of CUI to set of SABs
async function readCUISABMap(file) {
  const map = new Map();
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const line of rl) {
      const parts = line.split('|');
      if (parts.length < 12) continue;
      const CUI = parts[0];
      const SAB = parts[11] || 'MISSING';
      if (!map.has(CUI)) map.set(CUI, new Set());
      map.get(CUI).add(SAB);
    }
  } catch {
    return new Map();
  }
  return map;
}

// Collect preferred names for a set of CUIs from MRCONSO
// Only rows with TS=P, STT=PF, and ISPREF=Y are considered
async function collectPreferredNames(file, cuis) {
  const names = new Map();
  if (!cuis.size) return names;
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const line of rl) {
      const parts = line.split('|');
      if (parts.length < 15) continue;
      const cui = parts[0];
      if (!cuis.has(cui) || names.has(cui)) continue;
      const ts = parts[2];
      const stt = parts[4];
      const ispref = parts[6];
      if (ts === 'P' && stt === 'PF' && ispref === 'Y') {
        names.set(cui, parts[14]);
        if (names.size === cuis.size) break;
      }
    }
  } catch {}
  return names;
}

function computeSTYCounts(styMap) {
  const counts = new Map();
  for (const set of styMap.values()) {
    for (const sty of set) {
      counts.set(sty, (counts.get(sty) || 0) + 1);
    }
  }
  return counts;
}

function computeSTYSABCounts(styMap, cuiSabMap) {
  const counts = new Map();
  for (const [cui, stySet] of styMap) {
    const sabs = cuiSabMap.get(cui);
    if (!sabs) continue;
    for (const sty of stySet) {
      for (const sab of sabs) {
        const key = `${sty}|${sab}`;
        counts.set(key, (counts.get(key) || 0) + 1);
      }
    }
  }
  return counts;
}

function computeSTYSABCUIMap(styMap, cuiSabMap) {
  const map = new Map();
  for (const [cui, stySet] of styMap) {
    const sabs = cuiSabMap.get(cui);
    if (!sabs) continue;
    for (const sty of stySet) {
      for (const sab of sabs) {
        const key = `${sty}|${sab}`;
        if (!map.has(key)) map.set(key, new Set());
        map.get(key).add(cui);
      }
    }
  }
  return map;
}

function sanitizeComponent(str) {
  return str.replace(/\s+/g, '_').replace(/[^A-Za-z0-9_-]/g, '');
}

async function generateSTYReports(current, previous, reportConfig = {}) {
  console.log('  Reading MRSTY/MRCONSO files...');
  const styCurFile = path.join(releasesDir, current, 'META', 'MRSTY.RRF');
  const styPrevFile = path.join(releasesDir, previous, 'META', 'MRSTY.RRF');
  const consoCurFile = path.join(releasesDir, current, 'META', 'MRCONSO.RRF');
  const consoPrevFile = path.join(releasesDir, previous, 'META', 'MRCONSO.RRF');

  const styCurMap = await readSTYMap(styCurFile);
  const styPrevMap = await readSTYMap(styPrevFile);
  const sabCurMap = await readCUISABMap(consoCurFile);
  const sabPrevMap = await readCUISABMap(consoPrevFile);

  const curCounts = computeSTYCounts(styCurMap);
  const prevCounts = computeSTYCounts(styPrevMap);
  const includeBreakdowns = reportConfig.includeStyBreakdowns !== false;
  const curSabCounts = includeBreakdowns ? computeSTYSABCounts(styCurMap, sabCurMap) : new Map();
  const prevSabCounts = includeBreakdowns ? computeSTYSABCounts(styPrevMap, sabPrevMap) : new Map();
  const curSabCUIs = includeBreakdowns ? computeSTYSABCUIMap(styCurMap, sabCurMap) : new Map();
  const prevSabCUIs = includeBreakdowns ? computeSTYSABCUIMap(styPrevMap, sabPrevMap) : new Map();

  const diffEntries = [];
  const addedCUIs = new Set();
  const removedCUIs = new Set();

  await fsp.mkdir(styBreakdownDir, { recursive: true });
  await fsp.mkdir(stySourceDiffDir, { recursive: true });

  const summary = [];
  const allSTYs = new Set([...curCounts.keys(), ...prevCounts.keys()]);
  console.log(`  Processing ${allSTYs.size} semantic types...`);
  for (const sty of allSTYs) {
    const currentCount = curCounts.get(sty) || 0;
    const previousCount = prevCounts.get(sty) || 0;
    const diff = currentCount - previousCount;
    const pct = previousCount === 0 ? Infinity : (diff / previousCount * 100);
    let link = '';
    if (includeBreakdowns && diff !== 0) {
      const detail = [];
      const sabKeys = new Set();
      for (const k of curSabCounts.keys()) if (k.startsWith(sty + '|')) sabKeys.add(k.split('|')[1]);
      for (const k of prevSabCounts.keys()) if (k.startsWith(sty + '|')) sabKeys.add(k.split('|')[1]);
      for (const sab of sabKeys) {
        const key = `${sty}|${sab}`;
        const c = curSabCounts.get(key) || 0;
        const p = prevSabCounts.get(key) || 0;
        const d = c - p;
        let link2 = '';
        if (d !== 0) {
          const pp = p === 0 ? Infinity : (d / p * 100);
          const curSet = curSabCUIs.get(key) || new Set();
          const prevSet = prevSabCUIs.get(key) || new Set();
          const added = [...curSet].filter(x => !prevSet.has(x));
          const removed = [...prevSet].filter(x => !curSet.has(x));
          if (added.length || removed.length) {
            const safeSty = sanitizeComponent(sty);
            const safeSab = sanitizeComponent(sab);
            const jsonDiff = `${safeSty}_${safeSab}_changes.json`;
            const htmlDiff = jsonDiff.replace(/\.json$/, '.html');
            const diffData = { current, previous, sty, sab, added, removed };
            diffEntries.push({ jsonDiff, htmlDiff, diffData });
            for (const c of added) addedCUIs.add(c);
            for (const c of removed) removedCUIs.add(c);
            link2 = `sty_source_diffs/${jsonDiff}`;
          }
          detail.push({ SAB: sab, Previous: p, Current: c, Difference: d, Percent: pp, link: link2 });
        }
      }
      if (detail.length) {
        const safe = sanitizeComponent(sty);
        const jsonName = `${safe}_SAB_breakdown.json`;
        const htmlName = jsonName.replace(/\.json$/, '.html');
        await fsp.writeFile(path.join(styBreakdownDir, jsonName), JSON.stringify({ current, previous, sty, detail }, null, 2));
        let html = `<h3>${escapeHTML(sty)} by SAB (${current} vs ${previous})</h3>`;
        html += '<table><thead><tr><th>SAB</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th><th>Diff</th></tr></thead><tbody>';
        for (const row of detail) {
          const diffClass = row.Difference < 0 ? 'negative' : 'positive';
          const pctTxt = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
          const diffLink = row.link ? `<a href="../${row.link.replace(/\.json$/, '.html')}">view</a>` : '';
          html += `<tr><td>${row.SAB}</td><td>${row.Previous}</td><td>${row.Current}</td><td class="${diffClass}">${row.Difference}</td><td>${pctTxt}</td><td>${diffLink}</td></tr>`;
        }
        html += '</tbody></table>';
        if (generateHtml) {
          await fsp.writeFile(
            path.join(styBreakdownDir, htmlName),
            wrapDiffHtml(
              `${sty} by SAB`,
              html,
              'MRSTY Report',
              '../MRSTY_report.html'
            )
          );
        }
        link = `sty_breakdowns/${jsonName}`;
      }
    }
    summary.push({ Key: sty, Previous: previousCount, Current: currentCount, Difference: diff, Percent: pct, link });
  }

  if (diffEntries.length) {
    const curNames = await collectPreferredNames(consoCurFile, addedCUIs);
    const prevNames = await collectPreferredNames(consoPrevFile, removedCUIs);
    for (const { jsonDiff, htmlDiff, diffData } of diffEntries) {
      diffData.added = diffData.added.map(cui => ({ CUI: cui, Name: curNames.get(cui) || '' }));
      diffData.removed = diffData.removed.map(cui => ({ CUI: cui, Name: prevNames.get(cui) || '' }));
      await fsp.writeFile(path.join(stySourceDiffDir, jsonDiff), JSON.stringify(diffData, null, 2));
      if (generateHtml) {
        await fsp.writeFile(path.join(stySourceDiffDir, htmlDiff), stySabDiffToHtml(diffData));
      }
    }
  }

  const summaryPath = path.join(reportsDir, 'MRSTY_report.json');
  await fsp.writeFile(summaryPath, JSON.stringify({ current, previous, summary }, null, 2));

  let html = '';
  const header = includeBreakdowns ?
    '<table><thead><tr><th>STY</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th><th>Details</th></tr></thead><tbody>' :
    '<table><thead><tr><th>STY</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th></tr></thead><tbody>';
  html += header;
  for (const row of summary) {
    const diffClass = row.Difference < 0 ? 'negative' : 'positive';
    const pctTxt = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
    const cells = [
      escapeHTML(row.Key),
      row.Previous,
      row.Current,
      `<span class="${diffClass}">${row.Difference}</span>`,
      pctTxt
    ];
    if (includeBreakdowns) {
      const linkCell = row.link ? `<a href="${row.link.replace(/\.json$/, '.html')}">view</a>` : '';
      cells.push(linkCell);
    }
    html += `<tr><td>${cells.join('</td><td>')}</td></tr>`;
  }
  html += '</tbody></table>';
  if (generateHtml) {
      const title = `MRSTY Report (${current} vs ${previous})`;
      await fsp.writeFile(path.join(reportsDir, 'MRSTY_report.html'), wrapHtml(title, html));
  }
  console.log('  STY reports complete.');
}

async function generateCountReport(current, previous, fileName, indices, tableName) {
  const currentFile = path.join(releasesDir, current, 'META', fileName);
  const previousFile = path.join(releasesDir, previous, 'META', fileName);
  const baseCounts = await readCountsByIndices(currentFile, indices);
  const prevCounts = await readCountsByIndices(previousFile, indices);
  const summary = [];
  const keys = new Set([...baseCounts.keys(), ...prevCounts.keys()]);
  for (const key of keys) {
    const currentCount = baseCounts.get(key) || 0;
    const previousCount = prevCounts.get(key) || 0;
    const diff = currentCount - previousCount;
    const pct = previousCount === 0 ? Infinity : (diff / previousCount * 100);
    summary.push({ Key: key, Previous: previousCount, Current: currentCount, Difference: diff, Percent: pct });
  }
  const jsonName = `${tableName}_report.json`;
  await fsp.writeFile(path.join(reportsDir, jsonName), JSON.stringify({ current, previous, summary }, null, 2));

  let html = '';
  html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>Key</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th></tr></thead><tbody>';
  for (const row of summary) {
    const diffClass = row.Difference < 0 ? 'negative' : 'positive';
    const pctTxt = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
    html += `<tr><td>${escapeHTML(row.Key)}</td><td>${row.Previous}</td><td>${row.Current}</td><td class="${diffClass}">${row.Difference}</td><td>${pctTxt}</td></tr>`;
  }
  html += '</tbody></table>';
  const wrapped = wrapHtml(`${tableName} Report (${current} vs ${previous})`, html);
  if (generateHtml) {
    await fsp.writeFile(path.join(reportsDir, `${tableName}_report.html`), wrapped);
  }
}

async function readSABs(file) {
  const sabs = new Set();
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const line of rl) {
      const parts = line.split('|');
      if (parts.length > 3) {
        const sab = parts[3];
        if (sab) sabs.add(sab);
      }
    }
  } catch {
    return new Set();
  }
  return sabs;
}

async function generateMRSABChangeReport(current, previous) {
  const currentFile = path.join(releasesDir, current, 'META', 'MRSAB.RRF');
  const previousFile = path.join(releasesDir, previous, 'META', 'MRSAB.RRF');

  // Build maps keyed by the 2nd column so we can detect
  // added/dropped rows while still retaining the full line content
  const curMap = await readLineMapByIndices(currentFile, [1]);
  const prevMap = await readLineMapByIndices(previousFile, [1]);

  const curKeys = Array.from(curMap.keys());
  const prevKeys = Array.from(prevMap.keys());
  const addedRows = [];
  const removedRows = [];
  for (const k of curKeys) {
    if (!prevMap.has(k)) {
      addedRows.push(...curMap.get(k));
    }
  }
  for (const k of prevKeys) {
    if (!curMap.has(k)) {
      removedRows.push(...prevMap.get(k));
    }
  }

  const currentSABs = await readSABs(currentFile);
  const previousSABs = await readSABs(previousFile);

  const added = [...currentSABs].filter(s => !previousSABs.has(s)).sort();
  const dropped = [...previousSABs].filter(s => !currentSABs.has(s)).sort();

  const jsonData = { current, previous, added, dropped, addedRows, removedRows };
  await fsp.writeFile(path.join(reportsDir, 'MRSAB_report.json'), JSON.stringify(jsonData, null, 2));

  let html = `<h3>MRSAB Added/Dropped (${current} vs ${previous})</h3>`;
  if (added.length) {
    html += `<h4>Added SABs (${added.length})</h4><ul>`;
    for (const sab of added) html += `<li>${escapeHTML(sab)}</li>`;
    html += '</ul>';
  }
  if (dropped.length) {
    html += `<h4>Dropped SABs (${dropped.length})</h4><ul>`;
    for (const sab of dropped) html += `<li>${escapeHTML(sab)}</li>`;
    html += '</ul>';
  }
  if (addedRows.length) {
    html += `<h4>Added Rows (${addedRows.length})</h4>`;
    html += linesToHtmlTable(addedRows);
  }
  if (removedRows.length) {
    html += `<h4>Removed Rows (${removedRows.length})</h4>`;
    html += linesToHtmlTable(removedRows);
  }
  if (!added.length && !dropped.length && !addedRows.length && !removedRows.length) {
    html += '<p>No MRSAB changes.</p>';
  }
  const wrapped = wrapHtml('MRSAB Report', html);
  if (generateHtml) {
    await fsp.writeFile(path.join(reportsDir, 'MRSAB_report.html'), wrapped);
  }
}

// Gather rows for a set of SAB|REL|RELA keys in MRREL
// Returns a Map from key -> array of row objects
async function gatherMRRELRows(file, keys) {
  const rowsMap = new Map();
  for (const k of keys) rowsMap.set(k, []);
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    let i = 0;
    for await (const line of rl) {
      i++;
      const parts = line.split('|');
      if (parts.length < 11) continue;
      const sab = parts[10] || 'MISSING';
      const rel = parts[3] || 'MISSING';
      const rela = parts[7] || 'MISSING';
      const key = `${sab}|${rel}|${rela}`;
      if (rowsMap.has(key)) {
        rowsMap.get(key).push({
          RUI: parts[8],
          CUI1: parts[0],
          AUI1: parts[1],
          REL: rel,
          CUI2: parts[4],
          AUI2: parts[5],
          SAB: sab,
          RELA: rela
        });
      }
      if (i % 100000 === 0) {
        console.log(`  Processed ${i} lines of ${path.basename(file)}...`);
      }
    }
  } catch {
    return new Map();
  }
  return rowsMap;
}

async function collectConsoNames(file, auis, cuis) {
  const byAUI = new Map();
  const byCUI = new Map();
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const line of rl) {
      const parts = line.split('|');
      const cui = parts[0];
      const aui = parts[7];
      const str = parts[14];
      if (aui && auis.has(aui) && !byAUI.has(aui)) byAUI.set(aui, str);
      if (cui && cuis.has(cui) && !byCUI.has(cui)) byCUI.set(cui, str);
      if (byAUI.size === auis.size && byCUI.size === cuis.size) break;
    }
  } catch {}
  return { byAUI, byCUI };
}

function buildMRRELDiffData(key, baseRows, prevRows) {
  const [sab, rel, rela] = key.split('|');
  const baseMap = new Map(baseRows.map(r => [r.RUI, r]));
  const prevMap = new Map(prevRows.map(r => [r.RUI, r]));
  const added = [];
  const dropped = [];
  for (const [id, row] of baseMap) {
    if (!prevMap.has(id)) added.push(row);
  }
  for (const [id, row] of prevMap) {
    if (!baseMap.has(id)) dropped.push(row);
  }
  if (!added.length && !dropped.length) return null;
  return { sab, rel, rela, added, dropped };
}

function mrrelDiffToHtml(data) {
  const links = [];
  if (data.added && data.added.length) links.push('<a href="#added">Added</a>');
  if (data.dropped && data.dropped.length) links.push('<a href="#dropped">Dropped</a>');

  let html = `<h3>${data.sab} ${data.rel} ${data.rela} Differences</h3>`;
  if (links.length) {
    html += `<div class="sticky-links">${links.join(' | ')}</div>`;
  }

  if (data.added && data.added.length) {
    html += `<h4 id="added">Added (${data.added.length})</h4>`;
    html += '<table><thead><tr><th>RUI</th><th>CUI1</th><th>Name1</th><th>REL</th><th>CUI2</th><th>Name2</th></tr></thead><tbody>';
    for (const r of data.added) {
      html += `<tr><td>${r.RUI}</td><td>${r.CUI1}</td><td>${escapeHTML(r.STR1 || '')}</td><td>${r.REL}</td><td>${r.CUI2}</td><td>${escapeHTML(r.STR2 || '')}</td></tr>`;
    }
    html += '</tbody></table>';
  }
  if (data.dropped && data.dropped.length) {
    html += `<h4 id="dropped">Dropped (${data.dropped.length})</h4>`;
    html += '<table><thead><tr><th>RUI</th><th>CUI1</th><th>Name1</th><th>REL</th><th>CUI2</th><th>Name2</th></tr></thead><tbody>';
    for (const r of data.dropped) {
      html += `<tr><td>${r.RUI}</td><td>${r.CUI1}</td><td>${escapeHTML(r.STR1 || '')}</td><td>${r.REL}</td><td>${r.CUI2}</td><td>${escapeHTML(r.STR2 || '')}</td></tr>`;
    }
    html += '</tbody></table>';
  }
  return wrapDiffHtml(
    `${data.sab} ${data.rel} ${data.rela} Differences`,
    html,
    'MRREL Report',
    '../MRREL_report.html'
  );
}

async function generateMRRELReport(current, previous) {
  const currentFile = path.join(releasesDir, current, 'META', 'MRREL.RRF');
  const previousFile = path.join(releasesDir, previous, 'META', 'MRREL.RRF');
  const baseCounts = await readCountsByIndices(currentFile, [10, 3, 7]);
  const prevCounts = await readCountsByIndices(previousFile, [10, 3, 7]);
  await fsp.mkdir(diffsDir, { recursive: true });
  const summary = [];
  const diffKeys = new Set();
  const diffEntries = [];
  const curAUIs = new Set();
  const curCUIs = new Set();
  const prevAUIs = new Set();
  const prevCUIs = new Set();
  const keys = new Set([...baseCounts.keys(), ...prevCounts.keys()]);
  for (const key of keys) {
    const currentCount = baseCounts.get(key) || 0;
    const previousCount = prevCounts.get(key) || 0;
    const diff = currentCount - previousCount;
    const pct = previousCount === 0 ? Infinity : (diff / previousCount * 100);
    const [sab, rel, rela] = key.split('|');
    const entry = { SAB: sab, REL: rel, RELA: rela, Previous: previousCount, Current: currentCount, Difference: diff, Percent: pct, link: '' };
    if (Math.abs(pct) >= 5) diffKeys.add(key);
    summary.push(entry);
  }

  summary.sort((a, b) => {
    if (a.SAB !== b.SAB) return a.SAB.localeCompare(b.SAB);
    if (a.REL !== b.REL) return a.REL.localeCompare(b.REL);
    return a.RELA.localeCompare(b.RELA);
  });

  if (diffKeys.size) {
    const curRowsMap = await gatherMRRELRows(currentFile, diffKeys);
    const prevRowsMap = await gatherMRRELRows(previousFile, diffKeys);

    for (const entry of summary) {
      const key = `${entry.SAB}|${entry.REL}|${entry.RELA}`;
      if (!diffKeys.has(key)) continue;
      const baseRows = curRowsMap.get(key) || [];
      const prevRows = prevRowsMap.get(key) || [];
      const diffData = buildMRRELDiffData(key, baseRows, prevRows);
      if (diffData) {
        for (const r of diffData.added) {
          if (r.AUI1) curAUIs.add(r.AUI1); else curCUIs.add(r.CUI1);
          if (r.AUI2) curAUIs.add(r.AUI2); else curCUIs.add(r.CUI2);
        }
        for (const r of diffData.dropped) {
          if (r.AUI1) prevAUIs.add(r.AUI1); else prevCUIs.add(r.CUI1);
          if (r.AUI2) prevAUIs.add(r.AUI2); else prevCUIs.add(r.CUI2);
        }
        const safe = key.replace(/[^A-Za-z0-9_-]/g, '_');
        diffEntries.push({ entry, key, diffData, safe });
      }
    }

    const curNames = await collectConsoNames(path.join(releasesDir, current, 'META', 'MRCONSO.RRF'), curAUIs, curCUIs);
    const prevNames = await collectConsoNames(path.join(releasesDir, previous, 'META', 'MRCONSO.RRF'), prevAUIs, prevCUIs);

    for (const { entry, diffData, safe } of diffEntries) {
      for (const r of diffData.added) {
        r.STR1 = curNames.byAUI.get(r.AUI1) || curNames.byCUI.get(r.CUI1) || '';
        r.STR2 = curNames.byAUI.get(r.AUI2) || curNames.byCUI.get(r.CUI2) || '';
      }
      for (const r of diffData.dropped) {
        r.STR1 = prevNames.byAUI.get(r.AUI1) || prevNames.byCUI.get(r.CUI1) || '';
        r.STR2 = prevNames.byAUI.get(r.AUI2) || prevNames.byCUI.get(r.CUI2) || '';
      }
      const fileName = `MRREL_${safe}_diff.json`;
      await fsp.writeFile(path.join(diffsDir, fileName), JSON.stringify(diffData, null, 2));
      if (generateHtml) {
        const htmlName = fileName.replace(/\.json$/, '.html');
        await fsp.writeFile(path.join(diffsDir, htmlName), mrrelDiffToHtml(diffData));
      }
      entry.link = `diffs/${fileName}`;
    }
  }

  const jsonPath = path.join(reportsDir, 'MRREL_report.json');
  await fsp.writeFile(jsonPath, JSON.stringify({ current, previous, summary }, null, 2));

  let html = '';
  const changed = summary.filter(r => Math.abs(r.Percent) >= 5);
  if (changed.length) {
    html += '<h4>Changes \u22655%</h4>';
    html += '<table><thead><tr><th>SAB</th><th>REL</th><th>RELA</th><th>Prev</th><th>Curr</th><th>%</th><th>Diff</th></tr></thead><tbody>';
    for (const row of changed) {
      const pctTxt = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
      const diffClass = row.Difference < 0 ? 'negative' : 'positive';
      const linkCell = row.link ? `<a href="${row.link.replace(/\.json$/, '.html')}">view</a>` : '';
      html += `<tr><td>${escapeHTML(row.SAB)}</td><td>${escapeHTML(row.REL)}</td><td>${escapeHTML(row.RELA)}</td><td>${row.Previous}</td><td>${row.Current}</td><td>${pctTxt}</td><td class="${diffClass}">${row.Difference}</td><td>${linkCell}</td></tr>`;
    }
    html += '</tbody></table>';
  }

  html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>SAB</th><th>REL</th><th>RELA</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th><th>Diff</th></tr></thead><tbody>';
  for (const row of summary) {
    const diffClass = row.Difference < 0 ? 'negative' : 'positive';
    const pctTxt = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
    const linkCell = row.link ? `<a href="${row.link.replace(/\.json$/, '.html')}">view</a>` : '';
    html += `<tr><td>${escapeHTML(row.SAB)}</td><td>${escapeHTML(row.REL)}</td><td>${escapeHTML(row.RELA)}</td><td>${row.Previous}</td><td>${row.Current}</td><td class="${diffClass}">${row.Difference}</td><td>${pctTxt}</td><td>${linkCell}</td></tr>`;
  }
  html += '</tbody></table>';
  const title = `MRREL Report (${current} vs ${previous})`;
  const wrapped = wrapHtml(title, html);
  if (generateHtml) {
    await fsp.writeFile(path.join(reportsDir, 'MRREL_report.html'), wrapped);
  }
}

async function generateMRDOCReport(current, previous) {
  const currentFile = path.join(releasesDir, current, 'META', 'MRDOC.RRF');
  const previousFile = path.join(releasesDir, previous, 'META', 'MRDOC.RRF');
  const curLines = await readAllLines(currentFile);
  const prevLines = await readAllLines(previousFile);
  const curSet = new Set(curLines);
  const prevSet = new Set(prevLines);
  const added = curLines.filter(l => !prevSet.has(l));
  const removed = prevLines.filter(l => !curSet.has(l));
  const jsonPath = path.join(reportsDir, 'MRDOC_report.json');
  await fsp.writeFile(jsonPath, JSON.stringify({ current, previous, added, removed }, null, 2));

  let html = `<h3>MRDOC Differences (${current} vs ${previous})</h3>`;
  if (!added.length && !removed.length) {
    html += '<p>No differences found.</p>';
  } else {
    if (added.length) {
      html += `<h4>Added (${added.length})</h4><pre>${added.map(escapeHTML).join('\n')}</pre>`;
    }
    if (removed.length) {
      html += `<h4>Removed (${removed.length})</h4><pre>${removed.map(escapeHTML).join('\n')}</pre>`;
    }
  }
  if (generateHtml) {
    await fsp.writeFile(path.join(reportsDir, 'MRDOC_report.html'), wrapHtml('MRDOC Report', html));
  }
}

async function generateMRCOLSReport(current, previous) {
  const currentFile = path.join(releasesDir, current, 'META', 'MRCOLS.RRF');
  const previousFile = path.join(releasesDir, previous, 'META', 'MRCOLS.RRF');
  const curKeys = await readKeysByIndices(currentFile, [0, 1, 6]);
  const prevKeys = await readKeysByIndices(previousFile, [0, 1, 6]);
  const curSet = new Set(curKeys);
  const prevSet = new Set(prevKeys);
  const added = curKeys.filter(k => !prevSet.has(k));
  const removed = prevKeys.filter(k => !curSet.has(k));
  const jsonPath = path.join(reportsDir, 'MRCOLS_report.json');
  await fsp.writeFile(jsonPath, JSON.stringify({ current, previous, added, removed }, null, 2));

  let html = `<h3>MRCOLS Differences (${current} vs ${previous})</h3>`;
  if (!added.length && !removed.length) {
    html += '<p>No differences found.</p>';
  } else {
    if (added.length) {
      html += `<h4>Added (${added.length})</h4><pre>${added.map(escapeHTML).join('\n')}</pre>`;
    }
    if (removed.length) {
      html += `<h4>Removed (${removed.length})</h4><pre>${removed.map(escapeHTML).join('\n')}</pre>`;
    }
  }
  if (generateHtml) {
    await fsp.writeFile(path.join(reportsDir, 'MRCOLS_report.html'), wrapHtml('MRCOLS Report', html));
  }
}

async function collectMRFILESizes(release) {
  const file = path.join(releasesDir, release, 'META', 'MRFILES.RRF');
  const lines = await readAllLines(file);
  const result = new Map();
  for (const line of lines) {
    const parts = line.split('|');
    const fname = parts[0];
    if (!fname) continue;
    const possible = [
      path.join(releasesDir, release, fname),
      path.join(releasesDir, release, 'META', fname)
    ];
    for (const p of possible) {
      try {
        const stat = await fsp.stat(p);
        result.set(fname, stat.size);
        break;
      } catch {}
    }
  }
  return result;
}

async function generateMRFILESReport(current, previous) {
  const curMap = await collectMRFILESizes(current);
  const prevMap = await collectMRFILESizes(previous);

  const added = [];
  const removed = [];
  const changed = [];

  for (const [file, size] of curMap) {
    if (!prevMap.has(file)) {
      added.push({ file, size });
    } else {
      const prevSize = prevMap.get(file);
      if (prevSize !== size) {
        const diff = size - prevSize;
        const percent = prevSize === 0 ? Infinity : diff / prevSize * 100;
        changed.push({ file, previous: prevSize, current: size, diff, percent });
      }
    }
  }
  for (const [file, size] of prevMap) {
    if (!curMap.has(file)) {
      removed.push({ file, size });
    }
  }

  const jsonPath = path.join(reportsDir, 'MRFILES_report.json');
  await fsp.writeFile(jsonPath, JSON.stringify({ current, previous, added, removed, changed }, null, 2));

  let html = `<h3>MRFILES Differences (${current} vs ${previous})</h3>`;
  if (!added.length && !removed.length && !changed.length) {
    html += '<p>No differences found.</p>';
  } else {
    if (added.length) {
      html += `<h4>Added Files (${added.length})</h4>`;
      html += '<table><thead><tr><th>File</th><th>Size</th></tr></thead><tbody>';
      for (const a of added) {
        html += `<tr><td>${escapeHTML(a.file)}</td><td>${a.size}</td></tr>`;
      }
      html += '</tbody></table>';
    }
    if (removed.length) {
      html += `<h4>Dropped Files (${removed.length})</h4>`;
      html += '<table><thead><tr><th>File</th><th>Size</th></tr></thead><tbody>';
      for (const r of removed) {
        html += `<tr><td>${escapeHTML(r.file)}</td><td>${r.size}</td></tr>`;
      }
      html += '</tbody></table>';
    }
    if (changed.length) {
      html += `<h4>Size Changes (${changed.length})</h4>`;
      html += '<table><thead><tr><th>File</th><th>Previous</th><th>Current</th><th>Diff</th><th>%</th></tr></thead><tbody>';
      for (const c of changed) {
        const pct = isFinite(c.percent) ? c.percent.toFixed(2) : 'inf';
        html += `<tr><td>${escapeHTML(c.file)}</td><td>${c.previous}</td><td>${c.current}</td><td>${c.diff}</td><td>${pct}</td></tr>`;
      }
      html += '</tbody></table>';
    }
  }
  if (generateHtml) {
    await fsp.writeFile(path.join(reportsDir, 'MRFILES_report.html'), wrapHtml('MRFILES Report', html));
  }
}

async function readMRRANKOrders(file) {
  const map = new Map();
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const line of rl) {
      const parts = line.split('|');
      if (parts.length < 3) continue;
      const rank = parseInt(parts[0], 10);
      const sab = parts[1];
      const tty = parts[2];
      if (!sab || !tty || isNaN(rank)) continue;
      if (!map.has(sab)) map.set(sab, []);
      map.get(sab).push({ rank, tty });
    }
    for (const [sab, arr] of map) {
      arr.sort((a, b) => a.rank - b.rank);
      map.set(sab, arr.map(r => r.tty));
    }
  } catch {
    return new Map();
  }
  return map;
}

async function generateMRRANKReport(current, previous) {
  const curFile = path.join(releasesDir, current, 'META', 'MRRANK.RRF');
  const prevFile = path.join(releasesDir, previous, 'META', 'MRRANK.RRF');
  const curMap = await readMRRANKOrders(curFile);
  const prevMap = await readMRRANKOrders(prevFile);

  const sabs = new Set([...curMap.keys(), ...prevMap.keys()]);
  const summary = [];
  for (const sab of sabs) {
    const curOrder = curMap.get(sab) || [];
    const prevOrder = prevMap.get(sab) || [];
    const added = curOrder.filter(t => !prevOrder.includes(t));
    const removed = prevOrder.filter(t => !curOrder.includes(t));
    if (added.length || removed.length || curOrder.join('|') !== prevOrder.join('|')) {
      summary.push({ SAB: sab, previousOrder: prevOrder, currentOrder: curOrder, added, removed });
    }
  }

  const jsonPath = path.join(reportsDir, 'MRRANK_report.json');
  await fsp.writeFile(jsonPath, JSON.stringify({ current, previous, summary }, null, 2));

  let html = `<h3>MRRANK Order Changes (${current} vs ${previous})</h3>`;
  if (!summary.length) {
    html += '<p>No differences found.</p>';
  } else {
    html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>SAB</th><th>Previous Order</th><th>Current Order</th><th>Added</th><th>Removed</th></tr></thead><tbody>';
    for (const row of summary) {
      const addedTxt = row.added.join(', ');
      const remTxt = row.removed.join(', ');
      html += `<tr><td>${escapeHTML(row.SAB)}</td><td>${escapeHTML(row.previousOrder.join(', '))}</td><td>${escapeHTML(row.currentOrder.join(', '))}</td><td>${escapeHTML(addedTxt)}</td><td>${escapeHTML(remTxt)}</td></tr>`;
    }
    html += '</tbody></table>';
  }
  if (generateHtml) {
    await fsp.writeFile(path.join(reportsDir, 'MRRANK_report.html'), wrapHtml('MRRANK Report', html));
  }
}


(async () => {
  console.log('Detecting available releases...');
  const { current, previous } = await detectReleases();
  if (!current || !previous) {
    console.error('Need at least two releases in releases/');
    process.exit(1);
  }

  reportsDir = path.join(baseReportsDir, current);
  diffsDir = path.join(reportsDir, 'diffs');
  styBreakdownDir = path.join(reportsDir, 'sty_breakdowns');
  stySourceDiffDir = path.join(reportsDir, 'sty_source_diffs');
  configFile = path.join(reportsDir, 'config.json');

  const reportConfig = await loadReportConfig();

  const currentHashes = {
    lineCountDiff: hashOf(generateLineCountDiff.toString()),
    MRCONSO: hashOf(generateSABDiff.toString()),
    STYReports: hashOf(generateSTYReports.toString()),
    countReport: hashOf(generateCountReport.toString()),
    MRSABChange: hashOf(generateMRSABChangeReport.toString()),
    MRREL: hashOf(generateMRRELReport.toString()),
    MRDOC: hashOf(generateMRDOCReport.toString()),
    MRCOLS: hashOf(generateMRCOLSReport.toString()),
    MRFILES: hashOf(generateMRFILESReport.toString()),
    MRRANK: hashOf(generateMRRANKReport.toString()),
    wrapHtml: hashOf(wrapHtml.toString()),
    wrapDiffHtml: hashOf(wrapDiffHtml.toString()),
    preprocessFile: hashOf(fs.readFileSync(__filename, 'utf8'))
  };

  let lastConfig = null;
  try {
    lastConfig = JSON.parse(await fsp.readFile(configFile, 'utf-8'));
  } catch {}

  const sameReleases = lastConfig && lastConfig.current === current && lastConfig.previous === previous;
  const sameConfig = lastConfig && JSON.stringify(lastConfig.reportConfig || {}) === JSON.stringify(reportConfig);
  const lastHashes = (lastConfig && lastConfig.logicHashes) || {};
  const sameHashes = JSON.stringify(lastHashes) === JSON.stringify(currentHashes);
  if (sameReleases && sameConfig && sameHashes) {
    console.log('Report configuration and logic unchanged. Skipping regeneration.');
    return;
  }

  let needCounts = !sameReleases || lastHashes.lineCountDiff !== currentHashes.lineCountDiff;
  const diffFile = path.join(reportsDir, 'line-count-diff.json');
  if (!needCounts) {
    try {
      await fsp.access(diffFile);
      console.log('Existing line count diff found, skipping regeneration.');
    } catch {
      needCounts = true;
    }
  }

  if (needCounts) {
    console.log(`Processing line counts for ${current} vs ${previous}...`);
    try {
      await generateLineCountDiff(current, previous);
    } catch (err) {
      console.error('Failed generating line counts:', err.message);
    }
  }
  const runMRCONSO = !sameReleases || lastHashes.MRCONSO !== currentHashes.MRCONSO;
  const runSTYReports = !sameReleases || lastHashes.STYReports !== currentHashes.STYReports || !sameConfig;
  const runCount = !sameReleases || lastHashes.countReport !== currentHashes.countReport;
  const runMRSABChange = !sameReleases || lastHashes.MRSABChange !== currentHashes.MRSABChange;
  const runMRREL = !sameReleases || lastHashes.MRREL !== currentHashes.MRREL;
  const runMRDOC = !sameReleases || lastHashes.MRDOC !== currentHashes.MRDOC;
  const runMRCOLS = !sameReleases || lastHashes.MRCOLS !== currentHashes.MRCOLS;
  const runMRFILES = !sameReleases || lastHashes.MRFILES !== currentHashes.MRFILES;
  const runMRRANK = !sameReleases || lastHashes.MRRANK !== currentHashes.MRRANK;

  if (runMRCONSO) {
    console.log('Generating MRCONSO report...');
    try {
      await generateSABDiff(current, previous);
      console.log('MRCONSO report done.');
    } catch (err) {
      console.error('Failed MRCONSO report:', err.message);
    }
  } else {
    console.log('MRCONSO logic unchanged; skipping.');
  }

  if (runSTYReports) {
    console.log('Generating additional table reports...');
    try {
      await generateSTYReports(current, previous, reportConfig);
      console.log('Additional table reports done.');
    } catch (err) {
      console.error('Failed STY reports:', err.message);
    }
  } else {
    console.log('STY report logic unchanged; skipping.');
  }

  if (runCount) {
    console.log('Generating count reports...');
    try {
      await generateCountReport(current, previous, 'MRSAB.RRF', [1, 2], 'MRSAB');
      await generateCountReport(current, previous, 'MRDEF.RRF', [4], 'MRDEF');
      await generateCountReport(current, previous, 'MRSAT.RRF', [9], 'MRSAT');
      await generateCountReport(current, previous, 'MRHIER.RRF', [4], 'MRHIER');
      console.log('Count reports done.');
    } catch (err) {
      console.error('Failed count reports:', err.message);
    }
  } else {
    console.log('Count report logic unchanged; skipping MRSAB/MRDEF/MRSAT/MRHIER counts.');
  }

  if (runMRSABChange) {
    console.log('Generating MRSAB change report...');
    try {
      await generateMRSABChangeReport(current, previous);
      console.log('MRSAB change report done.');
    } catch (err) {
      console.error('Failed MRSAB change report:', err.message);
    }
  } else {
    console.log('MRSAB change logic unchanged; skipping.');
  }

  if (runMRREL) {
    console.log('Generating MRREL report...');
    try {
      await generateMRRELReport(current, previous);
      console.log('MRREL report done.');
    } catch (err) {
      console.error('Failed MRREL report:', err.message);
    }
  } else {
    console.log('MRREL logic unchanged; skipping.');
  }
  if (runMRDOC) {
    console.log('Generating MRDOC report...');
    try {
      await generateMRDOCReport(current, previous);
      console.log('MRDOC report done.');
    } catch (err) {
      console.error('Failed MRDOC report:', err.message);
    }
  } else {
    console.log('MRDOC logic unchanged; skipping.');
  }

  if (runMRCOLS) {
    console.log('Generating MRCOLS report...');
    try {
      await generateMRCOLSReport(current, previous);
      console.log('MRCOLS report done.');
    } catch (err) {
      console.error('Failed MRCOLS report:', err.message);
    }
  } else {
    console.log('MRCOLS logic unchanged; skipping.');
  }

  if (runMRFILES) {
    console.log('Generating MRFILES report...');
    try {
      await generateMRFILESReport(current, previous);
      console.log('MRFILES report done.');
    } catch (err) {
      console.error('Failed MRFILES report:', err.message);
    }
  } else {
    console.log('MRFILES logic unchanged; skipping.');
  }

  if (runMRRANK) {
    console.log('Generating MRRANK report...');
    try {
      await generateMRRANKReport(current, previous);
      console.log('MRRANK report done.');
    } catch (err) {
      console.error('Failed MRRANK report:', err.message);
    }
  } else {
    console.log('MRRANK logic unchanged; skipping.');
  }

  await fsp.mkdir(reportsDir, { recursive: true });
  await fsp.writeFile(
    configFile,
    JSON.stringify({ current, previous, reportConfig, logicHashes: currentHashes }, null, 2)
  );
  console.log('Reports generated in', reportsDir);
})().catch(err => {
  console.error(err);
  process.exit(1);
});

