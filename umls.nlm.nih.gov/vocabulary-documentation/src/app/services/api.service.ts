import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private fileUrl = 'assets/MRSAB.RRF';
  private sourcesCache: Source[] | null = null;
  private cacheVersionKey = 'cachedSourcesVersion';
  private currentDataVersion = '2024AA'; 

  constructor(private http: HttpClient) { }

  private setCache(sources: Source[]): void {
    this.sourcesCache = sources;
    localStorage.setItem('cachedSources', JSON.stringify(sources));
    localStorage.setItem(this.cacheVersionKey, this.currentDataVersion); // Store the current version
  }

  private getCache(): Source[] | null {
    const cachedVersion = localStorage.getItem(this.cacheVersionKey);
    if (cachedVersion === this.currentDataVersion) {
      const cachedSources = localStorage.getItem('cachedSources');
      return cachedSources ? JSON.parse(cachedSources) : null;
    }
    // If the versions don't match, invalidate the cache
    this.clearCache();
    return null;
  }

  private clearCache(): void {
    localStorage.removeItem('cachedSources');
    localStorage.removeItem(this.cacheVersionKey);
  }

  private parseRRF(data: string): Source[] {
    console.log('Raw data:', data);  // Log raw data
    const lines = data.split('\n').filter(line => line.trim() !== '');
    const parsedData: Source[] = lines.map(line => {
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

  getSources(): Observable<Source[]> {
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

  getSourceByAbbreviation(abbreviation: string): Observable<Source | null> {
    return this.getSources().pipe(
      map(sources => {
        const found = sources.find(source => source.abbreviation === abbreviation);
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

  getSourceAbbreviations(): Observable<string[]> {
    return this.getSources().pipe(
      map(sources => sources.map(source => source.abbreviation)) // Extract only abbreviations
    );
  }
}

interface Source {
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
