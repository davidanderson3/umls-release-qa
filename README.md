# UMLS Release QA

This project generates HTML reports comparing two UMLS releases.

## Configuration

Report behavior can be configured by editing `report-config.json` in the project
root. Currently the only option is:

- `includeStyBreakdowns` – when `true` (default) the MRSTY report includes
  per-SAB breakdowns for semantic types and, for each STY/SAB pair, a list of
  added or removed CUIs. Set to `false` to skip these detailed tables.

When preprocessing runs it stores the last used configuration in
`reports/<release>/config.json` (where `<release>` is the latest release).
If the current configuration and release selection match
what was used previously, preprocessing is skipped.

## Usage

1. Place at least two release directories under `releases/`.
2. Run `npm run preprocess` to generate reports with HTML output.
   Use `npm run preprocess:data` to generate only the JSON data without HTML.

Reports are saved to a versioned subfolder under `reports/` named after the
current release (for example `reports/2025AA/`).
The preprocessing step generates HTML and JSON reports for several UMLS tables
including MRCONSO, MRREL, MRSTY, MRDEF, MRSAB, MRSAT, MRDOC, MRCOLS, MRFILES,
and MRRANK.
The MRFILES report lists added, dropped, and size-changed files in sortable
tables for easier review. Size changes now include a percentage column to show
relative growth or shrinkage.

### Viewing Reports

Start the server with `npm start` and navigate to `http://localhost:8080/<release>`
where `<release>` is the version you want to view (for example `2025AA`). You can
link directly to a specific report by including the `report` query parameter, e.g.
`http://localhost:8080/2025AA?report=diffs%2FNCI_CCN_differences.html`.
