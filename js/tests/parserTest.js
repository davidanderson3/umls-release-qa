// js/tests/parserTest.js
import { parseMRCONSO } from '../modules/parser.js';

export const name = 'Parser';
export async function run({ baseFile, compareFile, ui }) {
    // example: count rows, show in summary
    const baseCount = await parseMRCONSO(baseFile);
    const compareCount = await parseMRCONSO(compareFile);

    ui.appendSummary(`
    <tr>
      <td>Parser</td>
      <td>${baseCount} rows in base</td>
      <td>${compareCount} rows in compare</td>
    </tr>
  `);
}
