from difflib import Differ

with open(r'C:\Users\rewolinskija\Documents\umls-source-release-1\umls.nlm.nih.gov\releasedocs\versions\2025AA\C Release notes and other txt files\copyright_notice.txt') as f1, open(r'C:\Users\rewolinskija\Downloads\umls-2024AB-full\2024AB-full\Copyright_Notice.txt') as f2:
    lines1 = [line for line in f1 if line.strip()]
    lines2 = [line for line in f2 if line.strip()]

differ = Differ()
diff = differ.compare(lines1, lines2)
print('\n'.join(diff))
