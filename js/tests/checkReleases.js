export const name = 'Check Release Folders';

export async function run({ ui }) {
  try {
    const resp = await fetch('/api/releases');
    if (!resp.ok) {
      ui.appendSummary(`<p style="color:red">Failed to check releases: ${resp.status}</p>`);
      return;
    }
    const data = await resp.json();
    const { current, previous, releaseList } = data;


    ui.appendSummary(`
      <h3>Release Directory Check</h3>
      <p>Releases found: ${releaseList.join(', ') || 'none'}</p>
    `);

    if (current && previous) {
      ui.appendSummary(`<p style="color:green">✅ Current release: ${current}, Previous release: ${previous}</p>`);
    } else {
      ui.appendSummary('<p style="color:red">❌ Could not find at least two releases (with META folders) in the releases directory.</p>');
    }
  } catch (err) {
    ui.appendSummary(`<p style="color:red">Error checking releases: ${err.message}</p>`);
  }
}
