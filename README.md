# UMLS Release QA

This project generates HTML reports comparing two UMLS releases.

## Usage

1. Place at least two release directories under `releases/`.
2. Run `npm run process` to generate reports with HTML output.
   Add `-- --force` to rerun reports even if no logic changes were detected.
   The web UI's **Re-run All Reports** button always uses this option to
   regenerate every report.

Reports are saved to a versioned subfolder under `reports/` named after the
current release (for example `reports/2025AA/`).
The preprocessing step generates HTML and JSON reports for several UMLS tables
including MRCONSO, MRREL, MRSTY, MRDEF, MRSAB, MRSAT, MRDOC, MRCOLS, MRFILES,
and MRRANK.
The MRFILES report lists added, dropped, and size-changed files in sortable
tables for easier review. Size changes now include a percentage column to show
relative growth or shrinkage.
Counts in the MRDEF report are sorted alphabetically by SAB.
MRRANK reports highlight added or removed rows with a color-coded diff view.

### Viewing Reports

Start the server with `npm start` and navigate to `http://localhost:8080/<release>`
where `<release>` is the version you want to view (for example `2025AA`). You can
link directly to a specific report by including the `report` query parameter, e.g.
`http://localhost:8080/2025AA?report=diffs%2FNCI_CCN_differences.html`.

### Editing Notes

Page headers, button text, and report notes can be edited directly in the
browser. Changes are saved to small JSON files on the server so they persist
across sessions without requiring a git commit. When a specific release is
viewed (e.g. `/2025AA`), edits are stored in `texts-<release>.json` alongside the
default `texts.json`.
