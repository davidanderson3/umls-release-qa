import difflib
import re

def normalize(line):
    # Remove leading/trailing whitespace and collapse multiple spaces
    return re.sub(r'\s+', ' ', line.strip())

def filter_diff(diff_lines):
    major_diffs = []
    for line in diff_lines:
        if line.startswith(('-', '+')):
            content = line[2:].strip()
            if content:  # Skip empty lines
                major_diffs.append(line)
    return major_diffs

with open(r'C:\Users\rewolinskija\Documents\umls-source-release-1\umls.nlm.nih.gov\releasedocs\versions\2025AA\C Release notes and other txt files\copyright_notice.txt') as f1, open(r'C:\Users\rewolinskija\Downloads\umls-2024AB-full\2024AB-full\Copyright_Notice.txt') as f2:
    lines1 = [normalize(line) for line in f1 if line.strip()]
    lines2 = [normalize(line) for line in f2 if line.strip()]

diff = difflib.unified_diff(lines1, lines2, fromfile='file1.txt', tofile='file2.txt', lineterm='')
filtered_diff = filter_diff(diff)

for line in filtered_diff:
    print(line)
