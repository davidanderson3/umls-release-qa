const express = require('express');
const path = require('path');
const fs = require('fs');
const readline = require('readline');
const { exec, spawn } = require('child_process');
const fsp = fs.promises;
const reportsDir = path.join(__dirname, 'reports');
const configFile = path.join(reportsDir, 'config.json');
const textsFile = path.join(__dirname, 'texts.json');
const defaultTexts = {
  title: 'UMLS Release QA',
  header: 'UMLS Release QA',
  runPreprocessButton: 'Run Reports',
  compareLinesButton: 'Compare Line Counts'
};

const app = express();
const PORT = process.env.PORT || 8080;
const releasesDir = process.env.RELEASES_DIR || path.join(__dirname, 'releases');

app.use(express.json());
app.use(express.static(path.join(__dirname)));

async function detectReleases() {
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
    current = releaseList[0] || null;
    previous = releaseList[1] || null;
  } catch { }

  return { current, previous, releaseList };
}

app.get('/api/releases', async (req, res) => {
  const result = await detectReleases();
  res.json(result);
});

function safeStatSize(file) {
  return fsp.stat(file).then(s => s.size).catch(() => null);
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

async function loadTexts() {
  try {
    const data = await fsp.readFile(textsFile, 'utf-8');
    return { ...defaultTexts, ...JSON.parse(data) };
  } catch {
    return { ...defaultTexts };
  }
}

async function saveTexts(texts) {
  const merged = { ...defaultTexts, ...texts };
  await fsp.writeFile(textsFile, JSON.stringify(merged, null, 2));
  return merged;
}

app.get('/api/texts', async (req, res) => {
  const data = await loadTexts();
  res.json(data);
});

app.post('/api/texts', async (req, res) => {
  try {
    const newTexts = await saveTexts(req.body || {});
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
  const { current, previous } = await detectReleases();
  if (!current || !previous) {
    res.status(400).json({ error: 'Need at least two releases' });
    return;
  }
  try {
    const cfg = JSON.parse(await fsp.readFile(configFile, 'utf-8'));
    if (cfg.current === current && cfg.previous === previous) {
      res.json({ message: 'Preprocessing complete.' });
      return;
    }
  } catch (err) {
    console.error('Failed to load precomputed diff:', err.message);
  }
  const script = path.join(__dirname, 'preprocess.js');
  exec(`node --max-old-space-size=8192 ${script}`, { cwd: __dirname }, (error, stdout, stderr) => {
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

  const { current, previous } = await detectReleases();
  if (current && previous) {
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
  const child = spawn('node', ['--max-old-space-size=8192', script], { cwd: __dirname });

  child.stdout.on('data', chunk => {
    const data = chunk.toString().trim();
    if (data) {
      res.write(`data: ${data}\n\n`);
    }
  });

  child.stderr.on('data', chunk => {
    const data = chunk.toString().trim();
    if (data) {
      res.write(`data: ERROR: ${data}\n\n`);
    }
  });

  child.on('close', code => {
    res.write(`event: done\ndata: ${code}\n\n`);
    res.end();
  });
});

app.get('/api/line-count-diff', async (req, res) => {
  const { current, previous } = await detectReleases();
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

    let html = `<h3>Line Count Comparison (${current} vs ${previous})</h3>`;
    html += '<table><thead><tr><th>File</th><th>Previous</th><th>Current</th><th>Change</th><th>%</th><th>Status</th><th>Report</th></tr></thead><tbody>';
    const unchanged = [];
    for (const f of result) {
      if (f.diff === 0) { unchanged.push(f.name); continue; }
      const style = f.diff < 0 ? ' style="color:red"' : '';
      const pct = isFinite(f.percent) ? f.percent.toFixed(2) : 'inf';
      const linkCell = f.link ? `<a href="${f.link}">view</a>` : '';
      html += `<tr><td>${f.name}</td><td>${f.previous ?? 0}</td><td>${f.current ?? 0}</td><td${style}>${f.diff}</td><td>${pct}</td><td>${f.status}</td><td>${linkCell}</td></tr>`;
    }
    html += '</tbody></table>';
    if (unchanged.length) {
      html += `<p>Unchanged files: ${unchanged.join(', ')}</p>`;
    }
    await fsp.writeFile(path.join(reportsDir, 'line-count-diff.html'), html);
    await fsp.writeFile(configFile, JSON.stringify({ current, previous }, null, 2));
  } catch (err) {
    console.error('Error generating line count diff:', err.message);
    res.status(500).json({ error: err.message });
    return;
  }

  res.json({ current, previous, files: result });
});

app.get('/api/sab-diff', async (req, res) => {
  const { current, previous } = await detectReleases();
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
