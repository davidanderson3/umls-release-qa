const express = require('express');
const path = require('path');
const fs = require('fs');
const fsp = fs.promises;

const app = express();
const PORT = process.env.PORT || 8080;

// Serve static files from repo root
app.use(express.static(path.join(__dirname)));

// Detect available releases and return the two most recent
app.get('/api/releases', async (req, res) => {
  const releasesDir = path.join(__dirname, 'releases');
  let releaseList = [];
  let current = null;
  let previous = null;

  try {
    await fsp.access(releasesDir);
    const entries = await fsp.readdir(releasesDir);
    for (const entry of entries) {
      const full = path.join(releasesDir, entry);
      const stat = await fsp.stat(full);
      if (stat.isDirectory() && fs.existsSync(path.join(full, 'META'))) {
        releaseList.push(entry);
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
});
