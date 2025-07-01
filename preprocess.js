const fs = require('fs');
const fsp = fs.promises;
const path = require('path');
const readline = require('readline');

const releasesDir = path.join(__dirname, 'releases');
const reportsDir = path.join(__dirname, 'reports');
const diffsDir = path.join(reportsDir, 'diffs');

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
    result.push({ name, current: cur, previous: prev, diff: (cur ?? 0) - (prev ?? 0) });
  }

  await fsp.mkdir(reportsDir, { recursive: true });
  const jsonPath = path.join(reportsDir, 'line-count-diff.json');
  await fsp.writeFile(jsonPath, JSON.stringify({ current, previous, files: result }, null, 2));

  let html = `<h3>Line Count Comparison (${current} vs ${previous})</h3>`;
  html += '<table><thead><tr><th>File</th><th>Previous</th><th>Current</th><th>Change</th></tr></thead><tbody>';
  const unchanged = [];
  for (const f of result) {
    if (f.diff === 0) { unchanged.push(f.name); continue; }
    const style = f.diff < 0 ? ' style="color:red"' : '';
    html += `<tr><td>${f.name}</td><td>${f.previous ?? 0}</td><td>${f.current ?? 0}</td><td${style}>${f.diff}</td></tr>`;
  }
  html += '</tbody></table>';
  if (unchanged.length) {
    html += `<p>Unchanged files: ${unchanged.join(', ')}</p>`;
  }
  await fsp.writeFile(path.join(reportsDir, 'line-count-diff.html'), html);
}

function escapeHTML(str) {
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

async function readMRCONSO(file) {
  const rows = [];
  const counts = new Map();
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
      counts.set(key, (counts.get(key) || 0) + 1);
      rows.push({ SAB, TTY, CUI, AUI, STR });
    }
  } catch {
    return { rows: [], counts: new Map() };
  }
  return { rows, counts };
}

function buildDiffHTML(sab, tty, baseRows, prevRows) {
  const base = baseRows.filter(r => r.SAB === sab && r.TTY === tty);
  const prev = prevRows.filter(r => r.SAB === sab && r.TTY === tty);
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
  let html = '';
  if (added.length) {
    html += '<h1>Added Atoms</h1>';
    html += '<table><thead><tr><th>AUI</th><th>CUI</th><th>STR</th></tr></thead><tbody>';
    for (const r of added) {
      html += `<tr><td>${r.AUI}</td><td>${r.CUI}</td><td>${escapeHTML(r.STR)}</td></tr>`;
    }
    html += '</tbody></table>';
  }
  if (dropped.length) {
    html += '<h1>Dropped Atoms</h1>';
    html += '<table><thead><tr><th>AUI</th><th>CUI</th><th>STR</th></tr></thead><tbody>';
    for (const r of dropped) {
      html += `<tr><td>${r.AUI}</td><td>${r.CUI}</td><td>${escapeHTML(r.STR)}</td></tr>`;
    }
    html += '</tbody></table>';
  }
  if (moved.length) {
    html += '<h1>Moved Atoms</h1>';
    html += '<table><thead><tr><th>AUI</th><th>CUI (current)</th><th>CUI (previous)</th><th>STR</th></tr></thead><tbody>';
    for (const m of moved) {
      html += `<tr><td>${m.AUI}</td><td>${m.currentCUI}</td><td>${m.previousCUI}</td><td>${escapeHTML(m.STR)}</td></tr>`;
    }
    html += '</tbody></table>';
  }
  return html;
}

async function generateSABDiff(current, previous) {
  const currentFile = path.join(releasesDir, current, 'META', 'MRCONSO.RRF');
  const previousFile = path.join(releasesDir, previous, 'META', 'MRCONSO.RRF');
  const { rows: baseRows, counts: baseCounts } = await readMRCONSO(currentFile);
  const { rows: prevRows, counts: prevCounts } = await readMRCONSO(previousFile);

  await fsp.mkdir(diffsDir, { recursive: true });
  const summary = [];
  const allKeys = new Set([...baseCounts.keys(), ...prevCounts.keys()]);
  for (const key of allKeys) {
    const [sab, tty] = key.split('|');
    const currentCount = baseCounts.get(key) || 0;
    const previousCount = prevCounts.get(key) || 0;
    const difference = currentCount - previousCount;
    const percent = previousCount === 0 ? Infinity : (difference / previousCount * 100);
    const include = percent < 0 || percent > 5 || sab === 'SRC';
    let link = '';
    if (include) {
      const diffHtml = buildDiffHTML(sab, tty, baseRows, prevRows);
      if (diffHtml) {
        const fileName = `${sab}_${tty}_differences.html`;
        const filePath = path.join(diffsDir, fileName);
        await fsp.writeFile(filePath, diffHtml);
        link = `diffs/${fileName}`;
      }
      summary.push({ SAB: sab, TTY: tty, Previous: previousCount, Current: currentCount, Difference: difference, Percent: percent, link });
    }
  }

  let html = '<table><thead><tr><th>SAB</th><th>TTY</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th><th></th></tr></thead><tbody>';
  for (const row of summary) {
    const style = row.Difference < 0 ? ' style="color:red"' : '';
    const percentStr = isFinite(row.Percent) ? row.Percent.toFixed(2) : 'inf';
    const linkHtml = row.link ? `<a href="${row.link}">details</a>` : '';
    html += `<tr><td>${row.SAB}</td><td>${row.TTY}</td><td>${row.Previous}</td><td>${row.Current}</td><td${style}>${row.Difference}</td><td>${percentStr}</td><td>${linkHtml}</td></tr>`;
  }
  html += '</tbody></table>';
  await fsp.writeFile(path.join(reportsDir, 'SAB_TTY_count_differences.html'), html);
}

(async () => {
  const { current, previous } = await detectReleases();
  if (!current || !previous) {
    console.error('Need at least two releases in releases/');
    process.exit(1);
  }
  await generateLineCountDiff(current, previous);
  await generateSABDiff(current, previous);
  console.log('Reports generated in', reportsDir);
})();

