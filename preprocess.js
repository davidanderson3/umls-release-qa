const fs = require('fs');
const fsp = fs.promises;
const path = require('path');
const readline = require('readline');

const releasesDir = path.join(__dirname, 'releases');
const reportsDir = path.join(__dirname, 'reports');
const diffsDir = path.join(reportsDir, 'diffs');
const styBreakdownDir = path.join(reportsDir, 'sty_breakdowns');
const configFile = path.join(reportsDir, 'config.json');
const userConfigPath = path.join(__dirname, 'report-config.json');

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
  return `<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>${title}</title><link rel="stylesheet" href="../css/styles.css">${style}</head><body><h1>${title}</h1>${body}</body></html>`;
}

function wrapDiffHtml(title, body) {
  const style = '<style>table{width:100%;border-collapse:collapse;border:1px solid #ccc;margin-top:10px;font-size:0.9em}table th,table td{border:1px solid #ccc;padding:6px 10px;text-align:left}thead{background-color:#f2f2f2}</style>';
  return `<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>${title}</title><link rel="stylesheet" href="../../css/styles.css">${style}</head><body><h1>${title}</h1>${body}</body></html>`;
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
    result.push({ name, current: cur, previous: prev, diff, percent, link });
  }

  await fsp.mkdir(reportsDir, { recursive: true });
  const jsonPath = path.join(reportsDir, 'line-count-diff.json');
  await fsp.writeFile(jsonPath, JSON.stringify({ current, previous, files: result }, null, 2));

  let html = `<h3>Line Count Comparison (${current} vs ${previous})</h3>`;
  html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>File</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th><th>Report</th></tr></thead><tbody>';
  const unchanged = [];
  for (const f of result) {
    if (f.diff === 0) { unchanged.push(f.name); continue; }
    const diffClass = f.diff < 0 ? 'negative' : 'positive';
    const pct = isFinite(f.percent) ? f.percent.toFixed(2) : 'inf';
    const linkCell = f.link ? `<a href="${f.link}">view</a>` : '';
    html += `<tr><td>${f.name}</td><td>${f.previous ?? 0}</td><td>${f.current ?? 0}</td><td class="${diffClass}">${f.diff}</td><td>${pct}</td><td>${linkCell}</td></tr>`;
  }
  html += '</tbody></table>';
  if (unchanged.length) {
    html += `<p>Unchanged files: ${unchanged.join(', ')}</p>`;
  }
  const wrapped = wrapHtml('Line Count Comparison', html);
  await fsp.writeFile(path.join(reportsDir, 'line-count-diff.html'), wrapped);
}

function escapeHTML(str) {
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
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

// Gather rows for a single SAB|TTY combination. This avoids keeping data
// for all keys in memory at once, which can lead to excessive usage on large
// files. Only rows matching the target key are returned.
async function gatherRowsForKey(file, key) {
  const rows = [];
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
      if (`${SAB}|${TTY}` === key) {
        rows.push({ SAB, TTY, CUI, AUI, STR });
      }
    }
  } catch {
    return [];
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
  let html = `<h3>${data.sab} ${data.tty} Differences</h3>`;

  if (data.added && data.added.length) {
    html += `<h4>Added (${data.added.length})</h4>`;
    html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>CUI</th><th>AUI</th><th>STR</th></tr></thead><tbody>';
    for (const row of data.added) {
      html += `<tr><td>${row.CUI}</td><td>${row.AUI}</td><td>${escapeHTML(row.STR)}</td></tr>`;
    }
    html += '</tbody></table>';
  }

  if (data.dropped && data.dropped.length) {
    html += `<h4>Dropped (${data.dropped.length})</h4>`;
    html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>CUI</th><th>AUI</th><th>STR</th></tr></thead><tbody>';
    for (const row of data.dropped) {
      html += `<tr><td>${row.CUI}</td><td>${row.AUI}</td><td>${escapeHTML(row.STR)}</td></tr>`;
    }
    html += '</tbody></table>';
  }

  if (data.moved && data.moved.length) {
    html += `<h4>Moved (${data.moved.length})</h4>`;
    html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>AUI</th><th>Previous CUI</th><th>Current CUI</th><th>STR</th></tr></thead><tbody>';
    for (const row of data.moved) {
      html += `<tr><td>${row.AUI}</td><td>${row.previousCUI}</td><td>${row.currentCUI}</td><td>${escapeHTML(row.STR)}</td></tr>`;
    }
    html += '</tbody></table>';
  }

  return wrapDiffHtml(`${data.sab} ${data.tty} Differences`, html);
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
    const entry = { SAB: sab, TTY: tty, Previous: previousCount, Current: currentCount, Difference: difference, Percent: percent, link: '' };
    if (include) detailKeys.add(key);
    summary.push(entry);
  }

  // Gather rows for each SAB/TTY individually to keep memory usage low.
  if (detailKeys.size) {
    for (const entry of summary) {
      const key = `${entry.SAB}|${entry.TTY}`;
      if (!detailKeys.has(key)) continue;
      const baseRows = await gatherRowsForKey(currentFile, key);
      const prevRows = await gatherRowsForKey(previousFile, key);
      const diffData = buildDiffData(entry.SAB, entry.TTY, baseRows, prevRows);
      if (diffData) {
        const fileName = `${entry.SAB}_${entry.TTY}_differences.json`;
        const filePath = path.join(diffsDir, fileName);
        await fsp.writeFile(filePath, JSON.stringify(diffData, null, 2));
        const htmlName = fileName.replace(/\.json$/, '.html');
        await fsp.writeFile(path.join(diffsDir, htmlName), diffDataToHtml(diffData));
        entry.link = `diffs/${fileName}`;
      }
    }
  }

  const summaryPath = path.join(reportsDir, 'MRCONSO_report.json');
  await fsp.writeFile(summaryPath, JSON.stringify({ current, previous, summary }, null, 2));

  let html = `<h3>MRCONSO SAB/TTY Differences (${current} vs ${previous})</h3>`;
  html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>SAB</th><th>TTY</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th><th>Diff</th></tr></thead><tbody>';
  for (const row of summary) {
    const diffClass = row.Difference < 0 ? 'negative' : 'positive';
    const pct = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
    const linkCell = row.link ? `<a href="${row.link.replace(/\.json$/, '.html')}">view</a>` : '';
    html += `<tr><td>${row.SAB}</td><td>${row.TTY}</td><td>${row.Previous}</td><td>${row.Current}</td><td class="${diffClass}">${row.Difference}</td><td>${pct}</td><td>${linkCell}</td></tr>`;
  }
  html += '</tbody></table>';
  const wrapped = wrapHtml('MRCONSO Report', html);
  await fsp.writeFile(path.join(reportsDir, 'MRCONSO_report.html'), wrapped);
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

function sanitizeComponent(str) {
  return str.replace(/\s+/g, '_').replace(/[^A-Za-z0-9_-]/g, '');
}

async function generateSTYReports(current, previous, reportConfig = {}) {
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

  await fsp.mkdir(styBreakdownDir, { recursive: true });

  const summary = [];
  const allSTYs = new Set([...curCounts.keys(), ...prevCounts.keys()]);
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
        const c = curSabCounts.get(`${sty}|${sab}`) || 0;
        const p = prevSabCounts.get(`${sty}|${sab}`) || 0;
        const d = c - p;
        if (d !== 0) {
          const pp = p === 0 ? Infinity : (d / p * 100);
          detail.push({ SAB: sab, Previous: p, Current: c, Difference: d, Percent: pp });
        }
      }
      if (detail.length) {
        const safe = sanitizeComponent(sty);
        const jsonName = `${safe}_SAB_breakdown.json`;
        const htmlName = jsonName.replace(/\.json$/, '.html');
        await fsp.writeFile(path.join(styBreakdownDir, jsonName), JSON.stringify({ current, previous, sty, detail }, null, 2));
        let html = `<h3>${escapeHTML(sty)} by SAB (${current} vs ${previous})</h3>`;
        html += '<table><thead><tr><th>SAB</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th></tr></thead><tbody>';
        for (const row of detail) {
          const diffClass = row.Difference < 0 ? 'negative' : 'positive';
          const pctTxt = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
          html += `<tr><td>${row.SAB}</td><td>${row.Previous}</td><td>${row.Current}</td><td class="${diffClass}">${row.Difference}</td><td>${pctTxt}</td></tr>`;
        }
        html += '</tbody></table>';
        await fsp.writeFile(path.join(styBreakdownDir, htmlName), wrapDiffHtml(`${sty} by SAB`, html));
        link = `sty_breakdowns/${jsonName}`;
      }
    }
    summary.push({ Key: sty, Previous: previousCount, Current: currentCount, Difference: diff, Percent: pct, link });
  }

  const summaryPath = path.join(reportsDir, 'MRSTY_report.json');
  await fsp.writeFile(summaryPath, JSON.stringify({ current, previous, summary }, null, 2));

  let html = `<h3>MRSTY Report (${current} vs ${previous})</h3>`;
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
  await fsp.writeFile(path.join(reportsDir, 'MRSTY_report.html'), wrapHtml('MRSTY Report', html));
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

  let html = `<h3>${tableName} Report (${current} vs ${previous})</h3>`;
  html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>Key</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th></tr></thead><tbody>';
  for (const row of summary) {
    const diffClass = row.Difference < 0 ? 'negative' : 'positive';
    const pctTxt = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
    html += `<tr><td>${escapeHTML(row.Key)}</td><td>${row.Previous}</td><td>${row.Current}</td><td class="${diffClass}">${row.Difference}</td><td>${pctTxt}</td></tr>`;
  }
  html += '</tbody></table>';
  const wrapped = wrapHtml(`${tableName} Report`, html);
  await fsp.writeFile(path.join(reportsDir, `${tableName}_report.html`), wrapped);
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
  const currentSABs = await readSABs(currentFile);
  const previousSABs = await readSABs(previousFile);

  const added = [...currentSABs].filter(s => !previousSABs.has(s)).sort();
  const dropped = [...previousSABs].filter(s => !currentSABs.has(s)).sort();

  const jsonData = { current, previous, added, dropped };
  await fsp.writeFile(path.join(reportsDir, 'MRSAB_report.json'), JSON.stringify(jsonData, null, 2));

  let html = `<h3>MRSAB Added/Dropped (${current} vs ${previous})</h3>`;
  if (added.length) {
    html += `<h4>Added (${added.length})</h4><ul>`;
    for (const sab of added) html += `<li>${escapeHTML(sab)}</li>`;
    html += '</ul>';
  }
  if (dropped.length) {
    html += `<h4>Dropped (${dropped.length})</h4><ul>`;
    for (const sab of dropped) html += `<li>${escapeHTML(sab)}</li>`;
    html += '</ul>';
  }
  if (!added.length && !dropped.length) {
    html += '<p>No SAB changes.</p>';
  }
  const wrapped = wrapHtml('MRSAB Report', html);
  await fsp.writeFile(path.join(reportsDir, 'MRSAB_report.html'), wrapped);
}

(async () => {
  console.log('Detecting available releases...');
  const { current, previous } = await detectReleases();
  if (!current || !previous) {
    console.error('Need at least two releases in releases/');
    process.exit(1);
  }

  const reportConfig = await loadReportConfig();

  let lastConfig = null;
  try {
    lastConfig = JSON.parse(await fsp.readFile(configFile, 'utf-8'));
  } catch {}

  const sameReleases = lastConfig && lastConfig.current === current && lastConfig.previous === previous;
  const sameConfig = lastConfig && JSON.stringify(lastConfig.reportConfig || {}) === JSON.stringify(reportConfig);

  if (sameReleases && sameConfig) {
    console.log('Report configuration unchanged. Skipping regeneration.');
    return;
  }

  let needCounts = true;
  const diffFile = path.join(reportsDir, 'line-count-diff.json');
  try {
    await fsp.access(diffFile);
    console.log('Existing line count diff found, skipping regeneration.');
    needCounts = false;
  } catch {}

  if (needCounts) {
    console.log(`Processing line counts for ${current} vs ${previous}...`);
    try {
      await generateLineCountDiff(current, previous);
    } catch (err) {
      console.error('Failed generating line counts:', err.message);
    }
  }
  console.log('Generating MRCONSO report...');
  await generateSABDiff(current, previous);
  console.log('MRCONSO report done.');
  console.log('Generating additional table reports...');
  await generateSTYReports(current, previous, reportConfig);
  await generateCountReport(current, previous, 'MRSAB.RRF', [3], 'MRSAB');
  await generateMRSABChangeReport(current, previous);
  await generateCountReport(current, previous, 'MRDEF.RRF', [4], 'MRDEF');
  await generateCountReport(current, previous, 'MRREL.RRF', [3], 'MRREL');
  await generateCountReport(current, previous, 'MRSAT.RRF', [9], 'MRSAT');
  await fsp.mkdir(reportsDir, { recursive: true });
  await fsp.writeFile(
    configFile,
    JSON.stringify({ current, previous, reportConfig }, null, 2)
  );
  console.log('Reports generated in', reportsDir);
})().catch(err => {
  console.error(err);
  process.exit(1);
});

