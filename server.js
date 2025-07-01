const express = require('express');
const path = require('path');
const fs = require('fs');
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

app.get('/api/file-size-diff', async (req, res) => {
  const { current, previous } = await detectReleases();
  if (!current || !previous) {
    res.status(400).json({ error: 'Need at least two releases' });
    return;
  }

  const currentMeta = path.join(releasesDir, current, 'META');
  const previousMeta = path.join(releasesDir, previous, 'META');
  const result = [];

  try {
    const currFiles = await fsp.readdir(currentMeta);
    const prevFiles = await fsp.readdir(previousMeta);
    const allFiles = Array.from(new Set([...currFiles, ...prevFiles]));

    for (const name of allFiles) {
      const curSize = await safeStatSize(path.join(currentMeta, name));
      const prevSize = await safeStatSize(path.join(previousMeta, name));
      if (curSize === null && prevSize === null) continue;
      result.push({ name, current: curSize, previous: prevSize });
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
