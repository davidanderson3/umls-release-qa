import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private fileUrl = 'assets/MRSAB.RRF';

  constructor(private http: HttpClient) { }

  /**
   * Filters out rows that have fewer than 18 columns.
   * Rows are also excluded if the first column is empty *unless*
   * the 4th column (columns[3]) is 'SRC' or 'MTH'.
   */
  private parseRRF(data: string): Source[] {
    let lines = data.split('\n').filter(line => line.trim() !== '');

    lines = lines.filter(line => {
      const columns = line.split('|');
      
      // Exclude any row with fewer than 18 columns
      if (columns.length < 18) {
        return false;
      }

      // If the first column is empty and the 4th column isn't SRC or MTH, exclude it
      if (!columns[0].trim() && columns[3].trim() !== 'SRC' && columns[3].trim() !== 'MTH') {
        return false;
      }

      return true;
    });

    // Map each line into a Source object
    return lines.map((line, index) => {
      const columns = line.split('|');
      // Log the first few lines if you want to verify columns
      if (index < 5) {
        console.log('Kept line #', index, columns);
      }
      return {
        abbreviation: columns[3],
        vsab: columns[2],
        shortName: columns[23],
        languageAbbreviation: columns[19],
        restrictionLevel: columns[13],
        sourceOfficialName: columns[4],
        lastUpdated: columns[9],
        family: columns[5],
        licenseContact: columns[11],
        contentContact: columns[12],
        citation: columns[24]
      };
    });
  }

  /**
   * Fetch the raw text of MRSAB.RRF, parse it, and return the resulting array of Source objects.
   */
  getSources(): Observable<Source[]> {
    return this.http.get(this.fileUrl, { responseType: 'text' }).pipe(
      map((data: string) => this.parseRRF(data)),
      catchError(error => {
        console.error('Error fetching sources:', error);
        return of([]);
      })
    );
  }

  getSourceByAbbreviation(abbreviation: string): Observable<Source | null> {
    return this.getSources().pipe(
      map(sources => sources.find(source => source.abbreviation === abbreviation) || null),
      catchError(error => {
        console.error('Error in getSourceByAbbreviation:', error);
        return of(null);
      })
    );
  }

  getSourceAbbreviations(): Observable<string[]> {
    return this.getSources().pipe(
      map(sources => sources.map(source => source.abbreviation))
    );
  }
}

export interface Source {
  abbreviation: string;
  vsab: string;
  shortName: string;
  languageAbbreviation: string;
  restrictionLevel: string;
  sourceOfficialName: string;
  lastUpdated: string;
  family: string;
  licenseContact: string;
  contentContact: string;
  citation: string;
}
