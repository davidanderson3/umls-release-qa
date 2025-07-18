const express = require('express');
const path = require('path');
const fs = require('fs');
const readline = require('readline');
const { exec, spawn } = require('child_process');
const fsp = fs.promises;
const baseReportsDir = path.join(__dirname, 'reports');
const textsFile = path.join(__dirname, 'texts.json');
const defaultTexts = {
  title: 'UMLS Release QA',
  header: 'UMLS Release QA',
  runPreprocessButton: 'Run Reports',
  rerunAllButton: 'Re-run All Reports',
  note1: '',
  note2: '',
  note3: '',
  lineCountNotes: {},
  reportInstructions: {}
};

function wrapHtml(title, body, reportKey = '') {
  const style = '<style>table{width:100%;border-collapse:collapse;border:1px solid #ccc;margin-top:10px;font-size:0.9em}table th,table td{border:1px solid #ccc;padding:6px 10px;text-align:left}thead{background-color:#f2f2f2}</style>';
  const crumbs = '<nav class="breadcrumbs"><a href="line-count-diff.html">Line Count Comparison</a></nav>';
  const button = '<button id="rerun-report">Re-run Report</button><div id="rerun-status"></div>';
  const instructions = '<div id="instructions" class="note" contenteditable></div>';
  const rerunScript = `<script>document.getElementById('rerun-report').addEventListener('click',()=>{if(parent&&parent.runReports){parent.runReports(true);}else{location.reload();}});</script>`;
  const instrScript = reportKey ?
    `<script>
      const match = window.location.pathname.match(/^\\/([^/]+)/);
      const rel = match && match[1] !== 'reports' ? match[1] : '';
      async function loadInstr(){
        let val = '';
        try{
          const url = new URL('/api/texts', window.location);
          if(rel) url.searchParams.set('release', rel);
          const resp=await fetch(url);
          const data=resp.ok?await resp.json():{};
          val=(data.reportInstructions||{})['${reportKey}']||'';
        }catch{}
        document.getElementById('instructions').textContent=val;
      }
      async function saveInstr(){
        const val=document.getElementById('instructions').textContent;
        const payload={reportInstructions:{'${reportKey}':val}};
        try{
          const url = new URL('/api/texts', window.location);
          if(rel) url.searchParams.set('release', rel);
          await fetch(url,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(payload)});
        }catch{}
      }
      loadInstr();
      document.getElementById('instructions').addEventListener('blur',saveInstr);
    </script>`:'';
  return `<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>${title}</title><link rel="stylesheet" href="../../css/styles.css">${style}</head><body>${crumbs}<h1>${title}</h1>${button}${instructions}<div id="report-content">${body}</div><script src="../../js/sortable.js"></script>${rerunScript}${instrScript}</body></html>`;
}

function escapeHTML(str) {
  return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

const app = express();
const PORT = process.env.PORT || 8080;
const releasesDir = process.env.RELEASES_DIR || path.join(__dirname, 'releases');

app.use(express.json());
app.get('/:release', async (req, res, next) => {
  try {
    const { release } = req.params;
    const { releaseList } = await detectReleases();
    if (releaseList.includes(release)) {
      res.sendFile(path.join(__dirname, 'index.html'));
      return;
    }
  } catch {}
  next();
});
app.use(express.static(path.join(__dirname)));
// Dynamically generate the MRCONSO report HTML from the JSON summary so we can
// control ordering without modifying the preprocessing step.
app.get(['/reports/MRCONSO_report.html', '/:release/reports/MRCONSO_report.html'], async (req, res, next) => {
  try {
    const reportsDir = await getReportsDir(req.params.release);
    const jsonPath = path.join(reportsDir, 'MRCONSO_report.json');
    const data = JSON.parse(await fsp.readFile(jsonPath, 'utf-8'));
    const summary = Array.isArray(data.summary) ? data.summary.slice() : [];
    // Sort by SAB then TTY
    summary.sort((a, b) => {
      const sabCmp = a.SAB.localeCompare(b.SAB);
      return sabCmp !== 0 ? sabCmp : a.TTY.localeCompare(b.TTY);
    });
    const notable = summary.filter(r => r.include);

    let html = `<h3>MRCONSO SAB/TTY Differences (${data.current} vs ${data.previous})</h3>`;
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

    res.send(wrapHtml('MRCONSO Report', html, 'MRCONSO'));
  } catch (err) {
    next();
  }
});
app.use('/:release/reports', (req, res, next) => {
  const rel = req.params.release;
  detectReleases(rel)
    .then(({ current }) => {
      const dir = path.join(baseReportsDir, current || '');
      express.static(dir)(req, res, next);
    })
    .catch(next);
});

app.use('/reports', (req, res, next) => {
  detectReleases()
    .then(({ current }) => {
      const dir = path.join(baseReportsDir, current || '');
      express.static(dir)(req, res, next);
    })
    .catch(next);
});

async function detectReleases(selected) {
  let releaseList = [];
  let current = null;
  let previous = null;

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
      } catch { }
    }

    releaseList.sort().reverse();
    if (selected && releaseList.includes(selected)) {
      current = selected;
      const idx = releaseList.indexOf(selected);
      previous = releaseList[idx + 1] || null;
    } else {
      current = releaseList[0] || null;
      previous = releaseList[1] || null;
    }
  } catch { }

  return { current, previous, releaseList };
}

async function getReportsDir(selected) {
  const { current } = await detectReleases(selected);
  return path.join(baseReportsDir, current || '');
}

async function getConfigFile(selected) {
  const dir = await getReportsDir(selected);
  return path.join(dir, 'config.json');
}

app.get('/api/releases', async (req, res) => {
  const rel = req.query.release;
  const result = await detectReleases(rel);
  res.json(result);
});

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

function getTextsFile(release = '') {
  return release ? path.join(__dirname, `texts-${release}.json`) : textsFile;
}

async function loadTexts(release = '') {
  const base = await (async () => {
    try { return JSON.parse(await fsp.readFile(textsFile, 'utf-8')); } catch { return {}; }
  })();
  if (!release) return { ...defaultTexts, ...base };
  try {
    const data = await fsp.readFile(getTextsFile(release), 'utf-8');
    return { ...defaultTexts, ...base, ...JSON.parse(data) };
  } catch {
    return { ...defaultTexts, ...base };
  }
}

async function saveTexts(texts, release = '') {
  const file = getTextsFile(release);
  let existing = {};
  try { existing = JSON.parse(await fsp.readFile(file, 'utf-8')); } catch {}
  const merged = { ...existing, ...texts };
  await fsp.writeFile(file, JSON.stringify(merged, null, 2));
  const base = release ? await loadTexts('') : {};
  return { ...defaultTexts, ...base, ...merged };
}

app.get('/api/texts', async (req, res) => {
  const rel = req.query.release || '';
  const data = await loadTexts(rel);
  res.json(data);
});

app.post('/api/texts', async (req, res) => {
  const rel = req.query.release || '';
  try {
    const newTexts = await saveTexts(req.body || {}, rel);
    res.json(newTexts);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Allow both POST and GET so users don't accidentally receive a 404 when
// they visit the endpoint directly. GET simply informs them how to run the
// preprocessing while POST actually performs it.
app.get('/api/preprocess', (req, res) => {
  res.status(405).json({ error: 'Use POST to run preprocessing.' });
});

app.post('/api/preprocess', async (req, res) => {
  const rel = req.query.release;
  const force = req.query.force === '1' || req.query.force === 'true';
  const { current, previous } = await detectReleases(rel);
  const reportsDir = await getReportsDir(rel);
  const configFile = await getConfigFile(rel);
  if (!current || !previous) {
    res.status(400).json({ error: 'Need at least two releases' });
    return;
  }
  if (!force) {
    try {
      const cfg = JSON.parse(await fsp.readFile(configFile, 'utf-8'));
      if (cfg.current === current && cfg.previous === previous) {
        res.json({ message: 'Preprocessing complete.' });
        return;
      }
    } catch (err) {
      console.error('Failed to load precomputed diff:', err.message);
    }
  }
  const script = path.join(__dirname, 'preprocess.js');
  const cmd = `node --max-old-space-size=8192 ${script}${force ? ' --force' : ''}`;
  exec(cmd, { cwd: __dirname }, (error, stdout, stderr) => {
    if (error) {
      // Detect the common "not enough releases" message and return a 400
      const msg = (stderr || error.message || '').trim();
      if (msg.includes('Need at least two releases')) {
        res.status(400).json({ error: msg });
      } else {
        res.status(500).json({ error: msg });
      }
      return;
    }
    res.json({ message: 'Preprocessing complete.' });
  });
});

app.get('/api/preprocess-stream', async (req, res) => {
  res.setHeader('Content-Type', 'text/event-stream');
  res.setHeader('Cache-Control', 'no-cache');
  res.setHeader('Connection', 'keep-alive');
  res.flushHeaders();
  res.write(': connected\n\n');

  const rel = req.query.release;
  const force = req.query.force === '1' || req.query.force === 'true';
  const { current, previous } = await detectReleases(rel);
  const configFile = await getConfigFile(rel);
  const reportsDir = await getReportsDir(rel);
  if (current && previous && !force) {
    try {
      const cfg = JSON.parse(await fsp.readFile(configFile, 'utf-8'));
      if (cfg.current === current && cfg.previous === previous) {
        res.write(`event: done\ndata: 0\n\n`);
        res.end();
        return;
      }
    } catch {
      try {
        const alt = JSON.parse(await fsp.readFile(path.join(reportsDir, 'line-count-diff.json'), 'utf-8'));
        if (alt.current === current && alt.previous === previous) {
          res.write(`event: done\ndata: 0\n\n`);
          res.end();
          return;
        }
      } catch {}
    }
  }

  const script = path.join(__dirname, 'preprocess.js');
  const args = ['--max-old-space-size=8192', script];
  if (force) args.push('--force');
  const child = spawn('node', args, { cwd: __dirname });

  child.stdout.on('data', chunk => {
    const data = chunk.toString().trim();
    if (data) {
      console.log(data);
      res.write(`data: ${data}\n\n`);
    }
  });

  child.stderr.on('data', chunk => {
    const data = chunk.toString().trim();
    if (data) {
      console.error(data);
      res.write(`data: ERROR: ${data}\n\n`);
    }
  });

  child.on('close', code => {
    res.write(`event: done\ndata: ${code}\n\n`);
    res.end();
  });
});

app.get('/api/run-report-stream', async (req, res) => {
  res.setHeader('Content-Type', 'text/event-stream');
  res.setHeader('Cache-Control', 'no-cache');
  res.setHeader('Connection', 'keep-alive');
  res.flushHeaders();
  res.write(': connected\n\n');

  const report = req.query.report;
  if (!report) {
    res.write('data: ERROR: Missing report parameter\n\n');
    res.write('event: done\ndata: 1\n\n');
    res.end();
    return;
  }

  const script = path.join(__dirname, 'preprocess.js');
  const args = ['--max-old-space-size=8192', script, `--report=${report}`];
  const child = spawn('node', args, { cwd: __dirname });

  child.stdout.on('data', chunk => {
    const data = chunk.toString().trim();
    if (data) {
      console.log(data);
      res.write(`data: ${data}\n\n`);
    }
  });

  child.stderr.on('data', chunk => {
    const data = chunk.toString().trim();
    if (data) {
      console.error(data);
      res.write(`data: ERROR: ${data}\n\n`);
    }
  });

  child.on('close', code => {
    res.write(`event: done\ndata: ${code}\n\n`);
    res.end();
  });
});

app.get('/api/line-count-diff', async (req, res) => {
  const rel = req.query.release;
  const { current, previous } = await detectReleases(rel);
  const reportsDir = await getReportsDir(rel);
  const configFile = await getConfigFile(rel);
  if (!current || !previous) {
    res.status(400).json({ error: 'Need at least two releases' });
    return;
  }

  const precomputed = path.join(reportsDir, 'line-count-diff.json');
  try {
    const data = JSON.parse(await fsp.readFile(precomputed, 'utf-8'));
    for (const file of data.files || []) {
      if (file.link) {
        try {
          await fsp.access(path.join(reportsDir, file.link));
          file.status = 'ready';
        } catch {
          file.status = 'missing';
        }
      } else {
        file.status = 'n/a';
      }
    }
    await fsp.mkdir(reportsDir, { recursive: true });
    await fsp.writeFile(configFile, JSON.stringify({ current: data.current, previous: data.previous }, null, 2));
    res.json(data);
    return;
  } catch {}

  const currentMeta = path.join(releasesDir, current, 'META');
  const previousMeta = path.join(releasesDir, previous, 'META');
  const result = [];

  try {
    const currFiles = await listFiles(currentMeta).catch(() => []);
    const prevFiles = await listFiles(previousMeta).catch(() => []);
    const allFiles = Array.from(new Set([...currFiles, ...prevFiles]));

    for (const name of allFiles) {
      const curCount = await safeLineCount(path.join(currentMeta, name));
      const prevCount = await safeLineCount(path.join(previousMeta, name));
      if (curCount === null && prevCount === null) continue;
      const diff = (curCount ?? 0) - (prevCount ?? 0);
      const percent = prevCount === 0 || prevCount === null ? Infinity : (diff / prevCount * 100);
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
      let status = 'n/a';
      if (link) {
        try {
          await fsp.access(path.join(reportsDir, link));
          status = 'ready';
        } catch {
          status = 'missing';
        }
      }
      result.push({ name, current: curCount, previous: prevCount, diff, percent, link, status });
    }

    await fsp.mkdir(reportsDir, { recursive: true });
    await fsp.writeFile(precomputed, JSON.stringify({ current, previous, files: result }, null, 2));

    const texts = await loadTexts(current);
    const notes = texts.lineCountNotes || {};
    let html = `<h3>Line Count Comparison (${current} vs ${previous})</h3>`;
    html += '<table style="border:1px solid #ccc;border-collapse:collapse"><thead><tr><th>File</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th><th>Status</th><th>Report</th><th>Notes</th></tr></thead><tbody>';
    const unchanged = [];
    for (const f of result) {
      if (f.diff === 0) { unchanged.push(f.name); continue; }
      const style = f.diff < 0 ? ' style="color:red"' : '';
      const pct = isFinite(f.percent) ? f.percent.toFixed(2) : 'inf';
      const linkCell = f.link ? `<a href="${f.link}">view</a>` : '';
      const note = escapeHTML(notes[f.name] || '');
      html += `<tr><td>${f.name}</td><td>${f.previous ?? 0}</td><td>${f.current ?? 0}</td><td${style}>${f.diff}</td><td>${pct}</td><td>${f.status}</td><td>${linkCell}</td><td class="editable line-note" data-file="${escapeHTML(f.name)}">${note}</td></tr>`;
    }
    html += '</tbody></table>';
    if (unchanged.length) {
      html += `<p>Unchanged files: ${unchanged.join(', ')}</p>`;
    }
    html += `<script type="module">
      const relMatch = window.location.pathname.match(/^\\/([^/]+)/);
      const rel = relMatch && relMatch[1] !== 'reports' ? relMatch[1] : '';
      async function load() {
        let notes = {};
        try {
          const url = new URL('/api/texts', window.location);
          if (rel) url.searchParams.set('release', rel);
          const resp = await fetch(url);
          const data = resp.ok ? await resp.json() : {};
          notes = data.lineCountNotes || {};
        } catch {}
        document.querySelectorAll('td[data-file]').forEach(td => {
          td.textContent = notes[td.dataset.file] || td.textContent;
          td.contentEditable = true;
          td.addEventListener('blur', save);
        });
      }
      async function save() {
        const notes = {};
        document.querySelectorAll('td[data-file]').forEach(td => {
          notes[td.dataset.file] = td.textContent;
        });
        try {
          const url = new URL('/api/texts', window.location);
          if (rel) url.searchParams.set('release', rel);
          await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ lineCountNotes: notes })
          });
        } catch {}
      }
      load();
    </script>`;
    const wrapped = wrapHtml('Line Count Comparison', html, 'line-count-diff');
    await fsp.writeFile(path.join(reportsDir, 'line-count-diff.html'), wrapped);
    await fsp.writeFile(configFile, JSON.stringify({ current, previous }, null, 2));
  } catch (err) {
    console.error('Error generating line count diff:', err.message);
    res.status(500).json({ error: err.message });
    return;
  }

  res.json({ current, previous, files: result });
});

app.get('/api/sab-diff', async (req, res) => {
  const rel = req.query.release;
  const { current, previous } = await detectReleases(rel);
  const reportsDir = await getReportsDir(rel);
  if (!current || !previous) {
    res.status(400).json({ error: 'Need at least two releases' });
    return;
  }

  const precomputed = path.join(reportsDir, 'MRCONSO_report.json');
  try {
    const data = await fsp.readFile(precomputed, 'utf-8');
    res.setHeader('Content-Type', 'application/json');
    res.send(data);
    return;
  } catch {}

  res.status(404).json({ error: 'Report not found. Run preprocessing.' });
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
  console.log(`Using releases directory: ${releasesDir}`);
});
