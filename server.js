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

// Detect available releases and return the two most recent
app.get('/api/releases', async (req, res) => {
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
  } catch (err) {
    // ignore if releasesDir doesn't exist
  }

  res.json({ current, previous, releaseList });
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
  console.log(`Using releases directory: ${releasesDir}`);
});
