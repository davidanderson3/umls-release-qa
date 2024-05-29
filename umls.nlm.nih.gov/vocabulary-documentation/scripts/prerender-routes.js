const sources = require('./sources.json');
const fs = require('fs');

// Define the file path
const filePath = '../routes.txt';

// Create a write stream
const writeStream = fs.createWriteStream(filePath);

// Function to write lines to the file
const writeLine = (line) => {
  writeStream.write(line + '\n', 'utf8', (err) => {
    if (err) {
      console.error('Error writing to file:', err);
    } else {
      console.log('Line written:', line);
    }
  });
};

// Example lines to write
const lines = [];
lines.push('/');
sources.result.forEach(s => {
  lines.push(`/current/${s.abbreviation}`);
  lines.push(`/current/${s.abbreviation}/metadata.html`);
  lines.push(`/current/${s.abbreviation}/stats.html`);
  lines.push(`/current/${s.abbreviation}/sourcerepresentation.html`);
  lines.push(`/current/${s.abbreviation}/metarepresentation.html`);
});

// Write each line to the file
lines.forEach(line => writeLine(line));

// Close the stream when done
writeStream.end(() => {
  console.log('All lines written to file.');
});
