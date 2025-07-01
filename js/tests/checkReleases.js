export const name = 'Check Release Folders';

export async function run({ ui }) {
  try {
    const resp = await fetch('/api/releases');
    if (!resp.ok) {
      ui.appendSummary(`<p style="color:red">Failed to check releases: ${resp.status}</p>`);
      return;
    }
    const data = await resp.json();
    const { currentExists, previousExists, releaseList } = data;

    ui.appendSummary(`
      <h3>Release Directory Check</h3>
      <p>Releases found: ${releaseList.join(', ') || 'none'}</p>
    `);

    if (currentExists && previousExists) {
      ui.appendSummary('<p style="color:green">✅ Both current and previous releases are present.</p>');
    } else {
      ui.appendSummary('<p style="color:red">❌ Missing releases. Ensure both "current" and "previous" directories exist in the releases folder.</p>');
    }
  } catch (err) {
    ui.appendSummary(`<p style="color:red">Error checking releases: ${err.message}</p>`);
  }
}
