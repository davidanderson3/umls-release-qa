const express = require('express');
const path = require('path');
const fs = require('fs');

const app = express();
const PORT = process.env.PORT || 8080;

// Serve static files from repo root
app.use(express.static(path.join(__dirname)));

// Detect available releases and return the two most recent
app.get('/api/releases', (req, res) => {
  const releasesDir = path.join(__dirname, 'releases');
  let releaseList = [];
  let current = null;
  let previous = null;

  if (fs.existsSync(releasesDir)) {
    releaseList = fs.readdirSync(releasesDir)
      .filter((file) => {
        const full = path.join(releasesDir, file);
        return (
          fs.statSync(full).isDirectory() &&
          fs.existsSync(path.join(full, 'META'))
        );
      })
      .sort()
      .reverse();

    current = releaseList[0] || null;
    previous = releaseList[1] || null;
  }

  res.json({ current, previous, releaseList });
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
