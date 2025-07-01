import pandas as pd
import argparse
import os
import numpy as np
import unicodedata

def read_and_count_mrconso(file_path):
    import csv

    column_names = [
        'CUI', 'LAT', 'TS', 'LUI', 'STT', 'SUI', 'ISPREF', 'AUI',
        'SAUI', 'SCUI', 'SDUI', 'SAB', 'TTY', 'CODE', 'STR',
        'SRL', 'SUPPRESS', 'CVF'
    ]

    rows = []
    bad_rows = []

    with open(file_path, encoding='utf-8') as f:
        reader = csv.reader(f, delimiter='|', quoting=csv.QUOTE_NONE)
        for i, row in enumerate(reader, 1):
            if len(row) < 18:
                bad_rows.append((i, row))
                continue
            rows.append(row[:18])

    if bad_rows:
        print(f"⚠️ {len(bad_rows)} malformed rows in {file_path}. Showing up to 5 examples:")
        for i, row in bad_rows[:5]:
            print(f"  Line {i}: {row}")
        if len(bad_rows) > 5:
            print(f"  ...and {len(bad_rows) - 5} more malformed rows.")

    df = pd.DataFrame(rows, columns=column_names)

    # Drop rows with missing AUI — they can't be tracked
    missing_aui_count = df['AUI'].isna().sum()
    if missing_aui_count:
        print(f"⚠️ {missing_aui_count} rows in {file_path} are missing AUI — skipping those.")
    df = df.dropna(subset=['AUI'])

    total_rows = df.shape[0]
    print(f"✅ Read {total_rows} usable rows (with AUI) from {file_path}")

    # Fill missing SAB and TTY for consistent grouping
    missing_sab = df['SAB'].isna().sum()
    missing_tty = df['TTY'].isna().sum()
    if missing_sab or missing_tty:
        print(f"⚠️ Found {missing_sab} missing SAB and {missing_tty} missing TTY — filling with 'MISSING'.")
        df['SAB'] = df['SAB'].fillna('MISSING')
        df['TTY'] = df['TTY'].fillna('MISSING')

    # Keep only columns used for comparison
    df = df[['SAB', 'TTY', 'CUI', 'AUI', 'STR']]

    # Count rows per SAB-TTY
    count_df = df.groupby(['SAB', 'TTY']).size().reset_index(name='num_rows').astype({'num_rows': 'int'})
    group_total = count_df['num_rows'].sum()
    if total_rows != group_total:
        print(f"⚠️ Mismatch: {total_rows} rows read vs {group_total} rows counted in groups.")
    else:
        print(f"✅ All rows accounted for in grouping: {group_total} rows.")

    return df, count_df

def save_differences(base_df, compare_df, sab, tty, folder):
    base_sub_df = base_df.query('SAB == @sab and TTY == @tty')[['CUI', 'AUI', 'STR']]
    compare_sub_df = compare_df.query('SAB == @sab and TTY == @tty')[['CUI', 'AUI', 'STR']]
    
    added_atoms = base_sub_df.loc[~base_sub_df['AUI'].isin(compare_sub_df['AUI'])]
    dropped_atoms = compare_sub_df.loc[~compare_sub_df['AUI'].isin(base_sub_df['AUI'])]
    
    merged = pd.merge(base_sub_df, compare_sub_df, on='AUI', suffixes=('_base', '_compare'))
    moved_atoms = merged[
        (merged['CUI_base'] != merged['CUI_compare']) &
        (merged['STR_base'] == merged['STR_compare'])
    ]
    
    file_link = ''
    
    if not added_atoms.empty or not dropped_atoms.empty or not moved_atoms.empty:
        file_link = os.path.join(folder, f"{sab}_{tty}_differences.html")
        with open(file_link, 'w') as f:
            if not added_atoms.empty:
                f.write('<h1>Added Atoms</h1>')
                f.write(added_atoms.to_html(index=False))
            if not dropped_atoms.empty:
                f.write('<h1>Dropped Atoms</h1>')
                f.write(dropped_atoms.to_html(index=False))
            if not moved_atoms.empty:
                f.write('<h1>Moved Atoms</h1>')
                f.write(moved_atoms.to_html(index=False))
    
    return file_link

def main(args):
    base_mrconso_path = args.base_mrconso_path
    compare_mrconso_path = args.compare_mrconso_path
    
    base_df, base_counts = read_and_count_mrconso(base_mrconso_path)
    compare_df, compare_counts = read_and_count_mrconso(compare_mrconso_path)
    
    diff_df = pd.merge(base_counts, compare_counts, on=['SAB', 'TTY'], how='outer', 
                         suffixes=('_Current', '_Previous'))
    diff_df.rename(columns={'num_rows_Current': 'Current', 'num_rows_Previous': 'Previous'}, inplace=True)
    diff_df.fillna(0, inplace=True)
    diff_df = diff_df.astype({'Current': 'int', 'Previous': 'int'})
    
    diff_df['Difference'] = diff_df['Current'] - diff_df['Previous']
    diff_df['Percent'] = diff_df.apply(lambda row: (row['Difference'] / row['Previous'] * 100) 
                                       if row['Previous'] != 0 else np.inf, axis=1)
    
    # Filter rows where percent difference is negative or greater than 5%
    diff_df = diff_df[(diff_df['Percent'] < 0) | (diff_df['Percent'] > 5)]
    
    folder = 'diffs'
    if not os.path.exists(folder):
        os.makedirs(folder)
    
    diff_df['Difference Link'] = ''
    
    for index, row in diff_df.iterrows():
        sab, tty = row['SAB'], row['TTY']
        print(f"Processing {sab}-{tty}...")
        file_link = save_differences(base_df, compare_df, sab, tty, folder)
        if file_link:
            diff_df.at[index, 'Difference Link'] = f'<a href="{file_link}">View Differences</a>'

    # ✅ Ensure SAB=SRC rows are always included
    src_rows = pd.merge(base_counts, compare_counts, on=['SAB', 'TTY'], how='outer', 
                        suffixes=('_Current', '_Previous'))
    src_rows.rename(columns={'num_rows_Current': 'Current', 'num_rows_Previous': 'Previous'}, inplace=True)
    src_rows.fillna(0, inplace=True)
    src_rows = src_rows.astype({'Current': 'int', 'Previous': 'int'})
    src_rows['Difference'] = src_rows['Current'] - src_rows['Previous']
    src_rows['Percent'] = src_rows.apply(lambda row: (row['Difference'] / row['Previous'] * 100)
                                         if row['Previous'] != 0 else np.inf, axis=1)

    existing_keys = set(diff_df[['SAB', 'TTY']].apply(tuple, axis=1))
    src_only = src_rows[(src_rows['SAB'] == 'SRC') & ~src_rows[['SAB', 'TTY']].apply(tuple, axis=1).isin(existing_keys)]

    src_only['Difference Link'] = ''
    for index, row in src_only.iterrows():
        sab, tty = row['SAB'], row['TTY']
        print(f"Ensuring SRC-{tty} is included...")
        file_link = save_differences(base_df, compare_df, sab, tty, folder)
        if file_link:
            src_only.at[index, 'Difference Link'] = f'<a href="{file_link}">View Differences</a>'

    diff_df = pd.concat([diff_df, src_only], ignore_index=True)

    print("Saving summary HTML...")
    diff_df.to_html('SAB_TTY_count_differences.html', escape=False, index=False)
    print("Done.")

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Compare two MRCONSO files.')
    parser.add_argument('base_mrconso_path', type=str, help='Path to the base MRCONSO.RRF file')
    parser.add_argument('compare_mrconso_path', type=str, help='Path to the MRCONSO.RRF file to compare')
    args = parser.parse_args()
    main(args)
