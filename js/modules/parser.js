// Simple MRCONSO parser used by tests
// Reads a File object and returns the number of rows
export async function parseMRCONSO(file) {
  const text = await file.text();
  // Split by newline and filter out empty lines
  return text.split(/\r?\n/).filter(Boolean).length;
}
