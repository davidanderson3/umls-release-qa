const express = require('express');
const path = require('path');
const fs = require('fs');

const app = express();
const PORT = process.env.PORT || 8080;

// Serve static files from repo root
app.use(express.static(path.join(__dirname)));

// Check releases folder for current and previous directories
app.get('/api/releases', (req, res) => {
  const releasesDir = path.join(__dirname, 'releases');
  let currentExists = false;
  let previousExists = false;
  let releaseList = [];

  if (fs.existsSync(releasesDir)) {
    releaseList = fs.readdirSync(releasesDir).filter((file) => {
      return fs.statSync(path.join(releasesDir, file)).isDirectory();
    });
    currentExists = releaseList.includes('current');
    previousExists = releaseList.includes('previous');
  }

  res.json({ currentExists, previousExists, releaseList });
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
