# UMLS Release QA

This project generates HTML reports comparing two UMLS releases.

## Configuration

Report behavior can be configured by editing `report-config.json` in the project
root. Currently the only option is:

- `includeStyBreakdowns` – when `true` (default) the MRSTY report includes
  per-SAB breakdowns for semantic types and, for each STY/SAB pair, a list of
  added or removed CUIs. Set to `false` to skip these detailed tables.

When preprocessing runs it stores the last used configuration in
`reports/config.json`. If the current configuration and release selection match
what was used previously, preprocessing is skipped.

## Usage

1. Place at least two release directories under `releases/`.
2. Run `npm run preprocess` to generate reports with HTML output.
   Use `npm run preprocess:data` to generate only the JSON data without HTML.

Reports are saved to the `reports/` folder.
