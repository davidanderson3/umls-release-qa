// js/main.js
import tests from '../tests/index.js';

// Element references
const metaInput = document.getElementById('metaFolder');
const baseInput = document.getElementById('baseFile');
const compareInput = document.getElementById('compareFile');
const moduleList = document.getElementById('module-list');
const btnAll = document.getElementById('run-all');
const btnSel = document.getElementById('run-selected');
const summaryEl = document.getElementById('summary');
const detailsEl = document.getElementById('details');

// Build the checkbox list of modules/tests
tests.forEach((test, idx) => {
    const li = document.createElement('li');
    li.innerHTML = `
    <label>
      <input type="checkbox" data-idx="${idx}" checked>
      ${test.name}
    </label>
  `;
    moduleList.append(li);
});

// UI helper for appending/clearing
const ui = {
    clear() {
        summaryEl.innerHTML = '';
        detailsEl.innerHTML = '';
    },
    appendSummary(html) {
        summaryEl.insertAdjacentHTML('beforeend', html);
    },
    appendDetails(html) {
        detailsEl.insertAdjacentHTML('beforeend', html);
    }
};

// Core runner
async function runTests(selectedIdxs) {
    ui.clear();

    const metaFiles = Array.from(metaInput.files);
    const baseFile = baseInput.files[0];
    const compareFile = compareInput.files[0];

    for (let idx of selectedIdxs) {
        const test = tests[idx];
        try {
            await test.run({ metaFiles, baseFile, compareFile, ui });
        } catch (err) {
            console.error(`Error in ${test.name}:`, err);
            ui.appendDetails(`
        <h3 style="color:red">Error in ${test.name}</h3>
        <pre>${err.message}</pre>
      `);
        }
    }
}

// Button event hookups
btnAll.addEventListener('click', () => {
    const allIdxs = tests.map((_, i) => i);
    runTests(allIdxs);
});

btnSel.addEventListener('click', () => {
    const checkedIdxs = Array.from(
        moduleList.querySelectorAll('input[type="checkbox"]:checked')
    ).map(cb => Number(cb.dataset.idx));
    runTests(checkedIdxs);
});
