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
// If --force is passed, do not skip regeneration when configuration and logic
// have not changed. This allows the UI "Re-run Report" button to always
// regenerate reports when requested.
const forceRun = process.argv.includes('--force');
const reportArg = process.argv.find(a => a.startsWith('--report='));
const singleReport = reportArg ? reportArg.slice('--report='.length) : null;

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

function wrapHtml(title, body, reportKey = '') {
  const style = '<style>table{width:100%;border-collapse:collapse;border:1px solid #ccc;margin-top:10px;font-size:0.9em}table th,table td{border:1px solid #ccc;padding:6px 10px;text-align:left}thead{background-color:#f2f2f2}</style>';
  const crumbs = '<nav class="breadcrumbs"><a href="line-count-diff.html">Line Count Comparison</a></nav>';
  const button = '<button id="rerun-report">Re-run Report</button><div id="rerun-status"></div>';
  const instructions = '<div id="instructions" class="note" contenteditable></div>';
  const rerunScript = reportKey ?
    `<script>
      document.getElementById('rerun-report').addEventListener('click', () => {
        const out = document.getElementById('rerun-status');
        out.innerHTML = '';
        const container = document.getElementById('report-content');
        if (container) container.innerHTML = '';
        const append = t => { const pre = document.createElement('pre'); pre.textContent = t; out.appendChild(pre); };
        append('Running report...');
        const es = new EventSource('/api/run-report-stream?report=${reportKey}');
        es.onmessage = e => append(e.data);
        es.addEventListener('done', () => { es.close(); append('Done.'); location.reload(); });
        es.onerror = () => { es.close(); append('Error running report.'); };
      });
    </script>` :
    `<script>document.getElementById('rerun-report').addEventListener('click',()=>{if(parent&&parent.runReports){parent.runReports(true);}else{location.reload();}});</script>`;
  const instrScript = reportKey ?
    `<script>
      async function loadInstr(){
        try{const resp=await fetch('/api/texts');
          const data=resp.ok?await resp.json():{};
          const txt=(data.reportInstructions||{})['${reportKey}']||'';
          document.getElementById('instructions').textContent=txt;
        }catch{}
      }
      async function saveInstr(){
        const val=document.getElementById('instructions').textContent;
        const payload={reportInstructions:{'${reportKey}':val}};
        try{await fetch('/api/texts',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(payload)});}catch{}
      }
      loadInstr();
      document.getElementById('instructions').addEventListener('blur',saveInstr);
    </script>` : '';
  return `<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>${title}</title><link rel="stylesheet" href="../../css/styles.css">${style}</head><body>${crumbs}<h1>${title}</h1>${button}${instructions}<div id="report-content">${body}</div><script src="../../js/sortable.js"></script>${rerunScript}${instrScript}</body></html>`;
}

function wrapDiffHtml(title, body, parentTitle = '', parentLink = '', reportKey = '') {
  const style = '<style>table{width:100%;border-collapse:collapse;border:1px solid #ccc;margin-top:10px;font-size:0.9em}table th,table td{border:1px solid #ccc;padding:6px 10px;text-align:left}thead{background-color:#f2f2f2}</style>';
  let crumbs = '<nav class="breadcrumbs"><a href="../line-count-diff.html">Line Count Comparison</a>';
  if (parentTitle && parentLink) {
    crumbs += ` &gt; <a href="${parentLink}">${parentTitle}</a>`;
  }
  crumbs += '</nav>';
  const button = '<button id="rerun-report">Re-run Report</button><div id="rerun-status"></div>';
  const instructions = '<div id="instructions" class="note" contenteditable></div>';
  const rerunScript = reportKey ?
    `<script>
      document.getElementById('rerun-report').addEventListener('click', () => {
        const out = document.getElementById('rerun-status');
        out.innerHTML = '';
        const container = document.getElementById('report-content');
        if (container) container.innerHTML = '';
        const append = t => { const pre = document.createElement('pre'); pre.textContent = t; out.appendChild(pre); };
        append('Running report...');
        const es = new EventSource('/api/run-report-stream?report=${reportKey}');
        es.onmessage = e => append(e.data);
        es.addEventListener('done', () => { es.close(); append('Done.'); location.reload(); });
        es.onerror = () => { es.close(); append('Error running report.'); };
      });
    </script>` :
    `<script>document.getElementById('rerun-report').addEventListener('click',()=>{if(parent&&parent.runReports){parent.runReports(true);}else{location.reload();}});</script>`;
  const instrScript = reportKey ?
    `<script>
      async function loadInstr(){
        try{const resp=await fetch('/api/texts');
          const data=resp.ok?await resp.json():{};
          const txt=(data.reportInstructions||{})['${reportKey}']||'';
          document.getElementById('instructions').textContent=txt;
        }catch{}
      }
      async function saveInstr(){
        const val=document.getElementById('instructions').textContent;
        const payload={reportInstructions:{'${reportKey}':val}};
        try{await fetch('/api/texts',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(payload)});}catch{}
      }
      loadInstr();
      document.getElementById('instructions').addEventListener('blur',saveInstr);
    </script>` : '';
  return `<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>${title}</title><link rel="stylesheet" href="../../../css/styles.css">${style}</head><body>${crumbs}<h1>${title}</h1>${button}${instructions}<div id="report-content">${body}</div><script src="../../../js/sortable.js"></script>${rerunScript}${instrScript}</body></html>`;
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
  const wrapped = wrapHtml('Line Count Comparison', html, 'line-count-diff');
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

// Like linesToHtmlTable but allows custom header labels
function linesToHtmlTableWithHeaders(lines, headers = []) {
  if (!lines.length) return '';
  const firstParts = lines[0].split('|');
  if (firstParts[firstParts.length - 1] === '') firstParts.pop();
  let html = '<table><thead><tr>';
  for (let i = 0; i < firstParts.length; i++) {
    const label = headers[i] || (i + 1);
    html += `<th>${escapeHTML(String(label))}</th>`;
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

function computeMRRANKDiff(prevLines, curLines) {
  const prevMap = new Map();
  for (const line of prevLines) {
    const key = line.split('|').slice(1).join('|');
    if (!prevMap.has(key)) prevMap.set(key, line);
  }
  const curMap = new Map();
  for (const line of curLines) {
    const key = line.split('|').slice(1).join('|');
    if (!curMap.has(key)) curMap.set(key, line);
  }
  const added = [];
  const removed = [];
  for (const [key, line] of curMap) {
    if (!prevMap.has(key)) added.push(line);
  }
  for (const [key, line] of prevMap) {
    if (!curMap.has(key)) removed.push(line);
  }
  added.sort();
  removed.sort();
  return { added, removed };
}

function rowsToDiffTable(added, removed) {
  if (!added.length && !removed.length) return '';
  let html = '<table class="diff"><tbody>';
  const max = Math.max(added.length, removed.length);
  for (let i = 0; i < max; i++) {
    if (removed[i]) html += `<tr><td class="diff-remove">- ${escapeHTML(removed[i])}</td></tr>`;
    if (added[i]) html += `<tr><td class="diff-add">+ ${escapeHTML(added[i])}</td></tr>`;
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
      const SAB = parts[11] || '-';
      const TTY = parts[12] || '-';
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
        keyParts.push(parts[idx] || '-');
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
        keyParts.push(parts[idx] || '-');
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
        keyParts.push(parts[idx] || '-');
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
      const SAB = parts[11] || '-';
      const TTY = parts[12] || '-';
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
    '../MRCONSO_report.html',
    'MRCONSO'
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
    html += '<table><thead><tr><th>CUI</th><th>Names</th></tr></thead><tbody>';
    for (const r of data.removed) {
      const nameLines = (r.Atoms || [])
        .map(a => escapeHTML(a.STR || ''))
        .join('<br>');
      html += `<tr><td>${r.CUI}</td><td>${nameLines}</td></tr>`;
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
    '../MRSTY_report.html',
    'MRSTY'
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

  summary.sort((a, b) => {
    if (a.SAB !== b.SAB) return a.SAB.localeCompare(b.SAB);
    return a.TTY.localeCompare(b.TTY);
  });

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
    html += '<h4>Notable Changes (any decrease, increase over 5%, or SAB=SRC)</h4>';
    html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>SAB</th><th>TTY</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th><th>Diff</th></tr></thead><tbody>';
    for (const row of notable) {
      const diffClass = row.Difference < 0 ? 'negative' : 'positive';
      const pct = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
      const linkCell = row.link ? `<a href="${row.link.replace(/\.json$/, '.html')}">view</a>` : '';
      html += `<tr><td>${row.SAB}</td><td>${row.TTY}</td><td>${row.Previous}</td><td>${row.Current}</td><td class="${diffClass}">${row.Difference}</td><td>${pct}</td><td>${linkCell}</td></tr>`;
    }
    html += '</tbody></table>';
  }

  html += '<h4>All Changes</h4>';
  html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>SAB</th><th>TTY</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th><th>Diff</th></tr></thead><tbody>';
  for (const row of summary) {
    const diffClass = row.Difference < 0 ? 'negative' : 'positive';
    const pct = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
    const linkCell = row.link ? `<a href="${row.link.replace(/\.json$/, '.html')}">view</a>` : '';
    html += `<tr><td>${row.SAB}</td><td>${row.TTY}</td><td>${row.Previous}</td><td>${row.Current}</td><td class="${diffClass}">${row.Difference}</td><td>${pct}</td><td>${linkCell}</td></tr>`;
  }
  html += '</tbody></table>';
  const wrapped = wrapHtml('MRCONSO Report', html, 'MRCONSO');
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
      const SAB = parts[11] || '-';
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
  const map = computeSTYSABCUIMap(styMap, cuiSabMap);
  const counts = new Map();
  for (const [key, set] of map) {
    counts.set(key, set.size);
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

async function gatherCuiSabAtoms(file, pairs) {
  const result = new Map();
  if (!pairs.size) return result;
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const line of rl) {
      const parts = line.split('|');
      if (parts.length < 15) continue;
      const cui = parts[0];
      const aui = parts[7];
      const sab = parts[11] || '-';
      const str = parts[14];
      const key = `${cui}|${sab}`;
      if (pairs.has(key)) {
        if (!result.has(key)) result.set(key, []);
        result.get(key).push({ AUI: aui, STR: str });
      }
    }
  } catch {}
  return result;
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

  // Track concepts that were removed entirely in the current release
  const prevCUIs = new Set(styPrevMap.keys());
  const curCUIs = new Set(styCurMap.keys());
  const droppedCUIs = new Set([...prevCUIs].filter(cui => !curCUIs.has(cui)));

  // Map of STY|SAB -> set of dropped CUIs for that pair
  const droppedStySabMap = new Map();
  if (droppedCUIs.size) {
    for (const cui of droppedCUIs) {
      const stys = styPrevMap.get(cui) || new Set();
      const sabs = sabPrevMap.get(cui) || new Set();
      for (const sty of stys) {
        for (const sab of sabs) {
          const key = `${sty}|${sab}`;
          if (!droppedStySabMap.has(key)) droppedStySabMap.set(key, new Set());
          droppedStySabMap.get(key).add(cui);
        }
      }
    }
  }

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
        const curSet = curSabCUIs.get(key) || new Set();
        const prevSet = prevSabCUIs.get(key) || new Set();
        const c = curSet.size;
        const p = prevSet.size;
        const d = c - p;
        let link2 = '';
        if (d !== 0) {
          const pp = p === 0 ? Infinity : (d / p * 100);
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
              '../MRSTY_report.html',
              'MRSTY'
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
    const pairSet = new Set();
    for (const { diffData } of diffEntries) {
      for (const cui of diffData.removed) {
        pairSet.add(`${cui}|${diffData.sab}`);
      }
    }
    const atomMap = await gatherCuiSabAtoms(consoPrevFile, pairSet);
    for (const { jsonDiff, htmlDiff, diffData } of diffEntries) {
      diffData.added = diffData.added.map(cui => ({ CUI: cui, Name: curNames.get(cui) || '' }));
      diffData.removed = diffData.removed.map(cui => ({
        CUI: cui,
        Name: prevNames.get(cui) || '',
        Atoms: atomMap.get(`${cui}|${diffData.sab}`) || []
      }));
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
      await fsp.writeFile(path.join(reportsDir, 'MRSTY_report.html'), wrapHtml(title, html, 'MRSTY'));
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

  if (tableName === 'MRHIER') {
    summary.sort((a, b) => a.Key.localeCompare(b.Key));
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
  let removedInfo = null;
  if (tableName === 'MRHIER') {
    removedInfo = await generateMRHIERRemovalReport(current, previous);
    if (removedInfo && removedInfo.count) {
      const link = removedInfo.link.replace(/\.json$/, '.html');
      html += `<p>${removedInfo.count} nodes were removed. <a href="${link}">View removed nodes</a></p>`;
    }
  }
  const wrapped = wrapHtml(`${tableName} Report (${current} vs ${previous})`, html, tableName);
  if (generateHtml) {
    await fsp.writeFile(path.join(reportsDir, `${tableName}_report.html`), wrapped);
  }
}

async function collectRSABGroups(file) {
  const withVcui = new Map();
  const withoutVcui = new Map();
  try {
    const rl = readline.createInterface({ input: fs.createReadStream(file) });
    for await (const line of rl) {
      const parts = line.split('|');
      if (parts.length > 3) {
        const vcui = parts[0];
        const rsab = parts[3];
        if (!rsab) continue;
        const target = vcui ? withVcui : withoutVcui;
        if (!target.has(rsab)) target.set(rsab, []);
        target.get(rsab).push(line);
      }
    }
  } catch {}
  return { withVcui, withoutVcui };
}

async function generateMRSABChangeReport(current, previous) {
  const currentFile = path.join(releasesDir, current, 'META', 'MRSAB.RRF');
  const previousFile = path.join(releasesDir, previous, 'META', 'MRSAB.RRF');

  const cur = await collectRSABGroups(currentFile);
  const prev = await collectRSABGroups(previousFile);

  const addedVcui = [...cur.withVcui.keys()].filter(x => !prev.withVcui.has(x)).sort();
  const droppedVcui = [...prev.withVcui.keys()].filter(x => !cur.withVcui.has(x)).sort();
  const addedNoVcui = [...cur.withoutVcui.keys()].filter(x => !prev.withoutVcui.has(x)).sort();
  const droppedNoVcui = [...prev.withoutVcui.keys()].filter(x => !cur.withoutVcui.has(x)).sort();

  const addedVcuiLines = addedVcui.flatMap(r => cur.withVcui.get(r));
  const droppedVcuiLines = droppedVcui.flatMap(r => prev.withVcui.get(r));
  const addedNoVcuiLines = addedNoVcui.flatMap(r => cur.withoutVcui.get(r));
  const droppedNoVcuiLines = droppedNoVcui.flatMap(r => prev.withoutVcui.get(r));

  const jsonData = {
    current,
    previous,
    vcuiNotNull: { added: addedVcuiLines, dropped: droppedVcuiLines },
    vcuiNull: { added: addedNoVcuiLines, dropped: droppedNoVcuiLines }
  };
  await fsp.writeFile(
    path.join(reportsDir, 'MRSAB_report.json'),
    JSON.stringify(jsonData, null, 2)
  );

  let html = `<h3>MRSAB RSAB Changes (${current} vs ${previous})</h3>`;
  html += '<h4>VCUI not null</h4>';
  if (addedVcuiLines.length) {
    html += `<p>Added rows (${addedVcuiLines.length})</p>`;
    html += linesToHtmlTable(addedVcuiLines);
  }
  if (droppedVcuiLines.length) {
    html += `<p>Dropped rows (${droppedVcuiLines.length})</p>`;
    html += linesToHtmlTable(droppedVcuiLines);
  }
  if (!addedVcuiLines.length && !droppedVcuiLines.length) {
    html += '<p>No changes.</p>';
  }

  html += '<h4>VCUI null</h4>';
  if (addedNoVcuiLines.length) {
    html += `<p>Added rows (${addedNoVcuiLines.length})</p>`;
    html += linesToHtmlTable(addedNoVcuiLines);
  }
  if (droppedNoVcuiLines.length) {
    html += `<p>Dropped rows (${droppedNoVcuiLines.length})</p>`;
    html += linesToHtmlTable(droppedNoVcuiLines);
  }
  if (!addedNoVcuiLines.length && !droppedNoVcuiLines.length) {
    html += '<p>No changes.</p>';
  }

  if (
    !addedVcuiLines.length &&
    !droppedVcuiLines.length &&
    !addedNoVcuiLines.length &&
    !droppedNoVcuiLines.length
  ) {
    html += '<p>No MRSAB changes.</p>';
  }

  const wrapped = wrapHtml('MRSAB Report', html, 'MRSAB');
  if (generateHtml) {
    await fsp.writeFile(path.join(reportsDir, 'MRSAB_report.html'), wrapped);
  }
}

async function generateMRSATReport(current, previous) {
  const currentFile = path.join(releasesDir, current, 'META', 'MRSAT.RRF');
  const previousFile = path.join(releasesDir, previous, 'META', 'MRSAT.RRF');
  const curCounts = await readCountsByIndices(currentFile, [9, 8]);
  const prevCounts = await readCountsByIndices(previousFile, [9, 8]);
  const summary = [];
  const keys = new Set([...curCounts.keys(), ...prevCounts.keys()]);
  for (const key of keys) {
    const currentCount = curCounts.get(key) || 0;
    const previousCount = prevCounts.get(key) || 0;
    const diff = currentCount - previousCount;
    const pct = previousCount === 0 ? Infinity : (diff / previousCount * 100);
    const [sab, atn] = key.split('|');
    summary.push({
      SAB: sab,
      ATN: atn,
      Previous: previousCount,
      Current: currentCount,
      Difference: diff,
      Percent: pct
    });
  }

  summary.sort((a, b) => {
    if (a.SAB !== b.SAB) return a.SAB.localeCompare(b.SAB);
    return a.ATN.localeCompare(b.ATN);
  });

  const jsonPath = path.join(reportsDir, 'MRSAT_report.json');
  await fsp.writeFile(jsonPath, JSON.stringify({ current, previous, summary }, null, 2));

  let html = '';
  const changed = summary.filter(r => Math.abs(r.Percent) >= 5);
  if (changed.length) {
    html += '<h4>Entries with a change of at least 5% (increase or decrease)</h4>';
    html += '<table><thead><tr><th>SAB</th><th>ATN</th><th>Prev</th><th>Curr</th><th>%</th><th>Diff</th></tr></thead><tbody>';
    for (const row of changed) {
      const pctTxt = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
      const diffClass = row.Difference < 0 ? 'negative' : 'positive';
      html += `<tr><td>${escapeHTML(row.SAB)}</td><td>${escapeHTML(row.ATN)}</td><td>${row.Previous}</td><td>${row.Current}</td><td>${pctTxt}</td><td class="${diffClass}">${row.Difference}</td></tr>`;
    }
    html += '</tbody></table>';
  }

  html += '<h4>All Changes</h4>';
  html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>SAB</th><th>ATN</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th></tr></thead><tbody>';
  for (const row of summary) {
    const diffClass = row.Difference < 0 ? 'negative' : 'positive';
    const pctTxt = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
    html += `<tr><td>${escapeHTML(row.SAB)}</td><td>${escapeHTML(row.ATN)}</td><td>${row.Previous}</td><td>${row.Current}</td><td class="${diffClass}">${row.Difference}</td><td>${pctTxt}</td></tr>`;
  }
  html += '</tbody></table>';
  const title = `MRSAT Report (${current} vs ${previous})`;
  const wrapped = wrapHtml(title, html, 'MRSAT');
  if (generateHtml) {
    await fsp.writeFile(path.join(reportsDir, 'MRSAT_report.html'), wrapped);
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
      const sab = parts[10] || '-';
      const rel = parts[3] || '-';
      const rela = parts[7] || '-';
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
    '../MRREL_report.html',
    'MRREL'
  );
}

async function generateMRRELReport(current, previous) {
  const currentFile = path.join(releasesDir, current, 'META', 'MRREL.RRF');
  const previousFile = path.join(releasesDir, previous, 'META', 'MRREL.RRF');
  const baseCounts = await readCountsByIndices(currentFile, [10, 3, 7]);
  const prevCounts = await readCountsByIndices(previousFile, [10, 3, 7]);
  await fsp.mkdir(diffsDir, { recursive: true });
  const summary = [];
  const diffKeys = [];
  const keys = new Set([...baseCounts.keys(), ...prevCounts.keys()]);
  for (const key of keys) {
    const currentCount = baseCounts.get(key) || 0;
    const previousCount = prevCounts.get(key) || 0;
    const diff = currentCount - previousCount;
    const pct = previousCount === 0 ? Infinity : (diff / previousCount * 100);
    const [sab, rel, rela] = key.split('|');
    const entry = { SAB: sab, REL: rel, RELA: rela, Previous: previousCount, Current: currentCount, Difference: diff, Percent: pct, link: '' };
    if (Math.abs(pct) >= 5) diffKeys.push(key);
    summary.push(entry);
  }

  summary.sort((a, b) => {
    if (a.SAB !== b.SAB) return a.SAB.localeCompare(b.SAB);
    if (a.REL !== b.REL) return a.REL.localeCompare(b.REL);
    return a.RELA.localeCompare(b.RELA);
  });

  if (diffKeys.length) {
    const baseRowsMap = await gatherMRRELRows(currentFile, diffKeys);
    const prevRowsMap = await gatherMRRELRows(previousFile, diffKeys);

    const allCurAUIs = new Set();
    const allCurCUIs = new Set();
    const allPrevAUIs = new Set();
    const allPrevCUIs = new Set();
    const diffInfos = [];

    for (const entry of summary) {
      const key = `${entry.SAB}|${entry.REL}|${entry.RELA}`;
      if (!diffKeys.includes(key)) continue;
      const baseRows = baseRowsMap.get(key) || [];
      const prevRows = prevRowsMap.get(key) || [];
      const diffData = buildMRRELDiffData(key, baseRows, prevRows);
      if (!diffData) continue;

      for (const r of diffData.added) {
        if (r.AUI1) allCurAUIs.add(r.AUI1); else allCurCUIs.add(r.CUI1);
        if (r.AUI2) allCurAUIs.add(r.AUI2); else allCurCUIs.add(r.CUI2);
      }
      for (const r of diffData.dropped) {
        if (r.AUI1) allPrevAUIs.add(r.AUI1); else allPrevCUIs.add(r.CUI1);
        if (r.AUI2) allPrevAUIs.add(r.AUI2); else allPrevCUIs.add(r.CUI2);
      }

      const safe = key.replace(/[^A-Za-z0-9_-]/g, '_');
      diffInfos.push({ entry, diffData, safe });
    }

    const curNames = await collectConsoNames(
      path.join(releasesDir, current, 'META', 'MRCONSO.RRF'),
      allCurAUIs,
      allCurCUIs
    );
    const prevNames = await collectConsoNames(
      path.join(releasesDir, previous, 'META', 'MRCONSO.RRF'),
      allPrevAUIs,
      allPrevCUIs
    );

    for (const { entry, diffData, safe } of diffInfos) {
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
    html += '<h4>Entries with a change of at least 5% (increase or decrease)</h4>';
    html += '<table><thead><tr><th>SAB</th><th>REL</th><th>RELA</th><th>Prev</th><th>Curr</th><th>%</th><th>Diff</th></tr></thead><tbody>';
    for (const row of changed) {
      const pctTxt = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
      const diffClass = row.Difference < 0 ? 'negative' : 'positive';
      const linkCell = row.link ? `<a href="${row.link.replace(/\.json$/, '.html')}">view</a>` : '';
      html += `<tr><td>${escapeHTML(row.SAB)}</td><td>${escapeHTML(row.REL)}</td><td>${escapeHTML(row.RELA)}</td><td>${row.Previous}</td><td>${row.Current}</td><td>${pctTxt}</td><td class="${diffClass}">${row.Difference}</td><td>${linkCell}</td></tr>`;
    }
    html += '</tbody></table>';
  }

  html += '<h4>All Changes</h4>';
  html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>SAB</th><th>REL</th><th>RELA</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th><th>Diff</th></tr></thead><tbody>';
  for (const row of summary) {
    const diffClass = row.Difference < 0 ? 'negative' : 'positive';
    const pctTxt = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
    const linkCell = row.link ? `<a href="${row.link.replace(/\.json$/, '.html')}">view</a>` : '';
    html += `<tr><td>${escapeHTML(row.SAB)}</td><td>${escapeHTML(row.REL)}</td><td>${escapeHTML(row.RELA)}</td><td>${row.Previous}</td><td>${row.Current}</td><td class="${diffClass}">${row.Difference}</td><td>${pctTxt}</td><td>${linkCell}</td></tr>`;
  }
  html += '</tbody></table>';
  const title = `MRREL Report (${current} vs ${previous})`;
  const wrapped = wrapHtml(title, html, 'MRREL');
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
      html += `<h4>Added (${added.length})</h4>`;
      html += linesToHtmlTable(added);
    }
    if (removed.length) {
      html += `<h4>Removed (${removed.length})</h4>`;
      html += linesToHtmlTable(removed);
    }
  }
  if (generateHtml) {
    await fsp.writeFile(path.join(reportsDir, 'MRDOC_report.html'), wrapHtml('MRDOC Report', html, 'MRDOC'));
  }
}

async function generateMRCOLSReport(current, previous) {
  const currentFile = path.join(releasesDir, current, 'META', 'MRCOLS.RRF');
  const previousFile = path.join(releasesDir, previous, 'META', 'MRCOLS.RRF');
  const curMap = await readLineMapByIndices(currentFile, [0, 1, 6]);
  const prevMap = await readLineMapByIndices(previousFile, [0, 1, 6]);
  const curKeys = Array.from(curMap.keys());
  const prevKeys = Array.from(prevMap.keys());
  const addedRows = [];
  const removedRows = [];
  for (const k of curKeys) {
    if (!prevMap.has(k)) addedRows.push(...curMap.get(k));
  }
  for (const k of prevKeys) {
    if (!curMap.has(k)) removedRows.push(...prevMap.get(k));
  }
  const jsonPath = path.join(reportsDir, 'MRCOLS_report.json');
  await fsp.writeFile(
    jsonPath,
    JSON.stringify({ current, previous, added: addedRows, removed: removedRows }, null, 2)
  );

  let html = `<h3>MRCOLS Differences (${current} vs ${previous})</h3>`;
  if (!addedRows.length && !removedRows.length) {
    html += '<p>No differences found.</p>';
  } else {
    if (addedRows.length) {
      html += `<h4>Added (${addedRows.length})</h4>`;
      html += linesToHtmlTableWithHeaders(addedRows, [
        'COL',
        'DES',
        'REF',
        'MIN',
        'AV',
        'MAX',
        'FIL',
        'DTY'
      ]);
    }
    if (removedRows.length) {
      html += `<h4>Removed (${removedRows.length})</h4>`;
      html += linesToHtmlTableWithHeaders(removedRows, [
        'COL',
        'DES',
        'REF',
        'MIN',
        'AV',
        'MAX',
        'FIL',
        'DTY'
      ]);
    }
  }
  if (generateHtml) {
    await fsp.writeFile(
      path.join(reportsDir, 'MRCOLS_report.html'),
      wrapHtml('MRCOLS Report', html, 'MRCOLS')
    );
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
    await fsp.writeFile(path.join(reportsDir, 'MRFILES_report.html'), wrapHtml('MRFILES Report', html, 'MRFILES'));
  }
}

async function generateMRHIERRemovalReport(current, previous) {
  const curFile = path.join(releasesDir, current, 'META', 'MRHIER.RRF');
  const prevFile = path.join(releasesDir, previous, 'META', 'MRHIER.RRF');
  const curLines = new Set(await readAllLines(curFile));
  const prevLines = await readAllLines(prevFile);
  const removedLines = prevLines.filter(l => !curLines.has(l));
  if (!removedLines.length) {
    return { count: 0, link: '' };
  }
  const auis = new Set();
  for (const line of removedLines) {
    const parts = line.split('|');
    if (parts[1]) auis.add(parts[1]);
  }
  const names = await collectConsoNames(
    path.join(releasesDir, previous, 'META', 'MRCONSO.RRF'),
    auis,
    new Set()
  );
  const removed = removedLines.map(line => {
    const p = line.split('|');
    return {
      CUI: p[0] || '',
      AUI: p[1] || '',
      SAB: p[4] || '',
      PTR: p[7] || p[6] || '',
      STR: names.byAUI.get(p[1]) || ''
    };
  });
  await fsp.mkdir(diffsDir, { recursive: true });
  const jsonName = 'MRHIER_removed_nodes.json';
  const jsonPath = path.join(diffsDir, jsonName);
  await fsp.writeFile(jsonPath, JSON.stringify({ current, previous, removed }, null, 2));
  if (generateHtml) {
    const htmlName = jsonName.replace(/\.json$/, '.html');
    let html = `<h3>Removed MRHIER Nodes (${current} vs ${previous})</h3>`;
    html += '<table><thead><tr><th>CUI</th><th>AUI</th><th>Name</th><th>SAB</th><th>PTR</th></tr></thead><tbody>';
    for (const r of removed) {
      html += `<tr><td>${r.CUI}</td><td>${r.AUI}</td><td>${escapeHTML(r.STR)}</td><td>${escapeHTML(r.SAB)}</td><td>${escapeHTML(r.PTR)}</td></tr>`;
    }
    html += '</tbody></table>';
    await fsp.writeFile(path.join(diffsDir, htmlName),
      wrapDiffHtml('MRHIER Removed Nodes', html, 'MRHIER Report', '../MRHIER_report.html', 'MRHIER'));
  }
  return { count: removed.length, link: `diffs/${jsonName}` };
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
  const curLines = await readAllLines(curFile);
  const prevLines = await readAllLines(prevFile);
  const { added: addedRows, removed: removedRows } = computeMRRANKDiff(prevLines, curLines);

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
  await fsp.writeFile(
    jsonPath,
    JSON.stringify({ current, previous, summary, addedRows, removedRows }, null, 2)
  );

  let html = `<h3>MRRANK Order Changes (${current} vs ${previous})</h3>`;
  if (!summary.length && !addedRows.length && !removedRows.length) {
    html += '<p>No differences found.</p>';
  } else {
    html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>SAB</th><th>Previous Order</th><th>Current Order</th><th>Added</th><th>Removed</th></tr></thead><tbody>';
    for (const row of summary) {
      const addedTxt = row.added.join(', ');
      const remTxt = row.removed.join(', ');
      html += `<tr><td>${escapeHTML(row.SAB)}</td><td>${escapeHTML(row.previousOrder.join(', '))}</td><td>${escapeHTML(row.currentOrder.join(', '))}</td><td>${escapeHTML(addedTxt)}</td><td>${escapeHTML(remTxt)}</td></tr>`;
    }
    html += '</tbody></table>';
    if (addedRows.length || removedRows.length) {
      html += '<h4>Row Changes</h4>';
      html += rowsToDiffTable(addedRows, removedRows);
    }
  }
  if (generateHtml) {
    await fsp.writeFile(path.join(reportsDir, 'MRRANK_report.html'), wrapHtml('MRRANK Report', html, 'MRRANK'));
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

  if (singleReport) {
    try {
      switch (singleReport) {
        case 'line-count-diff':
          await generateLineCountDiff(current, previous);
          break;
        case 'MRCONSO':
          await generateSABDiff(current, previous);
          break;
        case 'MRSTY':
          await generateSTYReports(current, previous, reportConfig);
          break;
        case 'MRSAB':
          await generateMRSABChangeReport(current, previous);
          break;
        case 'MRDEF':
          await generateCountReport(current, previous, 'MRDEF.RRF', [4], 'MRDEF');
          break;
        case 'MRSAT':
          await generateMRSATReport(current, previous);
          break;
        case 'MRHIER':
          await generateCountReport(current, previous, 'MRHIER.RRF', [4], 'MRHIER');
          break;
        case 'MRREL':
          await generateMRRELReport(current, previous);
          break;
        case 'MRDOC':
          await generateMRDOCReport(current, previous);
          break;
        case 'MRCOLS':
          await generateMRCOLSReport(current, previous);
          break;
        case 'MRFILES':
          await generateMRFILESReport(current, previous);
          break;
        case 'MRRANK':
          await generateMRRANKReport(current, previous);
          break;
        default:
          console.error('Unknown report:', singleReport);
          process.exit(1);
      }
    } catch (err) {
      console.error('Error generating report:', err.message);
      process.exit(1);
    }
    return;
  }

  const currentHashes = {
    lineCountDiff: hashOf(generateLineCountDiff.toString()),
    MRCONSO: hashOf(generateSABDiff.toString()),
    STYReports: hashOf(generateSTYReports.toString()),
    countReport: hashOf(generateCountReport.toString()),
    MRSAT: hashOf(generateMRSATReport.toString()),
    MRSABChange: hashOf(generateMRSABChangeReport.toString()),
    MRREL: hashOf(generateMRRELReport.toString()),
    MRDOC: hashOf(generateMRDOCReport.toString()),
    MRCOLS: hashOf(generateMRCOLSReport.toString()),
    MRFILES: hashOf(generateMRFILESReport.toString()),
    MRHIERRemoval: hashOf(generateMRHIERRemovalReport.toString()),
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
  if (!forceRun && sameReleases && sameConfig && sameHashes) {
    console.log('Report configuration and logic unchanged. Skipping regeneration.');
    return;
  }

  let needCounts = forceRun || !sameReleases || lastHashes.lineCountDiff !== currentHashes.lineCountDiff;
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
  const runMRCONSO = forceRun || !sameReleases || lastHashes.MRCONSO !== currentHashes.MRCONSO;
  const runSTYReports = forceRun || !sameReleases || lastHashes.STYReports !== currentHashes.STYReports || !sameConfig;
  const runCount = forceRun || !sameReleases ||
    lastHashes.countReport !== currentHashes.countReport ||
    lastHashes.MRHIERRemoval !== currentHashes.MRHIERRemoval;
  const runMRSAT = forceRun || !sameReleases || lastHashes.MRSAT !== currentHashes.MRSAT;
  const runMRSABChange = forceRun || !sameReleases || lastHashes.MRSABChange !== currentHashes.MRSABChange;
  const runMRREL = forceRun || !sameReleases || lastHashes.MRREL !== currentHashes.MRREL;
  const runMRDOC = forceRun || !sameReleases || lastHashes.MRDOC !== currentHashes.MRDOC;
  const runMRCOLS = forceRun || !sameReleases || lastHashes.MRCOLS !== currentHashes.MRCOLS;
  const runMRFILES = forceRun || !sameReleases || lastHashes.MRFILES !== currentHashes.MRFILES;
  const runMRRANK = forceRun || !sameReleases || lastHashes.MRRANK !== currentHashes.MRRANK;

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
      await generateCountReport(current, previous, 'MRHIER.RRF', [4], 'MRHIER');
      console.log('Count reports done.');
    } catch (err) {
      console.error('Failed count reports:', err.message);
    }
  } else {
    console.log('Count report logic unchanged; skipping MRSAB/MRDEF/MRHIER counts.');
  }

  if (runMRSAT) {
    console.log('Generating MRSAT report...');
    try {
      await generateMRSATReport(current, previous);
      console.log('MRSAT report done.');
    } catch (err) {
      console.error('Failed MRSAT report:', err.message);
    }
  } else {
    console.log('MRSAT logic unchanged; skipping.');
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

