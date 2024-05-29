const express = require('express');
const path = require('path');

const app = express();
const port = 3101;

// Define the path to the public directory
const publicDirectory = path.join(__dirname, '../dist/vocabulary-documentation/browser');

// Serve static files from the public directory
app.use(express.static(publicDirectory));

// Start the server
app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
