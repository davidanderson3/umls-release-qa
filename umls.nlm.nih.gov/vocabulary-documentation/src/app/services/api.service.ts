import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private fileUrl = 'assets/MRSAB.RRF';
  private sourcesCache: any[] | null = null;

  constructor(private http: HttpClient) { }

  private setCache(sources: any[]): void {
    this.sourcesCache = sources;
  }

  private getCache(): any[] | null {
    return this.sourcesCache;
  }

  private parseRRF(data: string): any[] {
    console.log('Raw data:', data);  // Log raw data
    const lines = data.split('\n').filter(line => line.trim() !== '');
    const parsedData = lines.map(line => {
      const columns = line.split('|');
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
    console.log('Parsed data:', parsedData);  // Log parsed data
    return parsedData;
}

  getSources(): Observable<any> {
    const cachedData = this.getCache();
    if (cachedData) {
      return of(cachedData);
    }

    return this.http.get(this.fileUrl, { responseType: 'text' }).pipe(
      map((data: string) => {
        const transformedSources = this.parseRRF(data);
        this.setCache(transformedSources);
        return transformedSources;
      }),
      catchError(error => {
        console.error('Error fetching sources:', error);
        return of([]);
      })
    );
  }

  getSourceByAbbreviation(abbreviation: string): Observable<any> {
    return this.getSources().pipe(
      map(sources => {
        const found = sources.find((source: any) => source.abbreviation === abbreviation);
        if (found) {
          return found;
        } else {
          throw new Error('Abbreviation not found');
        }
      }),
      catchError(error => {
        console.error('Error in getSourceByAbbreviation:', error);
        return of(null);
      })
    );
  }
}
