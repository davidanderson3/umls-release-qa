const express = require('express');
const path = require('path');
const fs = require('fs');
const fsp = fs.promises;

const app = express();
const PORT = process.env.PORT || 8080;
// Allow overriding the releases directory for flexibility
const releasesDir = process.env.RELEASES_DIR ||
  path.join(__dirname, 'releases');

// Serve static files from repo root
app.use(express.static(path.join(__dirname)));

async function detectReleases() {
  const releaseList = [];
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
          const hasMeta = subEntries.some(
            (d) => d.toLowerCase() === 'meta'
          );
          if (hasMeta) {
            releaseList.push(entry);
          }
        }
      } catch {
        // ignore unreadable entries
      }
    }
    releaseList.sort().reverse();
    current = releaseList[0] || null;
    previous = releaseList[1] || null;
  } catch {
    // ignore if releasesDir doesn't exist
  }

  return { current, previous, releaseList };
}

// Detect available releases and return the two most recent
app.get('/api/releases', async (req, res) => {
  const result = await detectReleases();
  res.json(result);
});

function safeStatSize(file) {
  return fsp.stat(file).then(s => s.size).catch(() => null);
}

// Compare file sizes in META between current and previous release
app.get('/api/file-size-diff', async (req, res) => {
  const { current, previous } = await detectReleases();
  if (!current || !previous) {
    return res.status(400).json({ error: 'Need at least two releases' });
  }

  const currentMeta = path.join(releasesDir, current, 'META');
  const previousMeta = path.join(releasesDir, previous, 'META');
  const result = [];

  try {
    const currFiles = await fsp.readdir(currentMeta);
    const prevFiles = await fsp.readdir(previousMeta);
    const all = Array.from(new Set([...currFiles, ...prevFiles]));

    for (const name of all) {
      const curSize = await safeStatSize(path.join(currentMeta, name));
      const prevSize = await safeStatSize(path.join(previousMeta, name));
      if (curSize === null && prevSize === null) continue;
      result.push({ name, current: curSize, previous: prevSize });
    }
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }

  res.json({ current, previous, files: result });
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
  console.log(`Using releases directory: ${releasesDir}`);
});
