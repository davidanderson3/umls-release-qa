const express = require('express');
const path = require('path');
const fs = require('fs');
const readline = require('readline');
const fsp = fs.promises;

const app = express();
const PORT = process.env.PORT || 8080;
const releasesDir = process.env.RELEASES_DIR || path.join(__dirname, 'releases');

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

app.get('/api/line-count-diff', async (req, res) => {
  const { current, previous } = await detectReleases();
  if (!current || !previous) {
    res.status(400).json({ error: 'Need at least two releases' });
    return;
  }

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
      result.push({ name, current: curCount, previous: prevCount });
    }
  } catch (err) {
    res.status(500).json({ error: err.message });
    return;
  }

  res.json({ current, previous, files: result });
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
  console.log(`Using releases directory: ${releasesDir}`);
});
