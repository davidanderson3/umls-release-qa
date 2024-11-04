import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private fileUrl = 'assets/MRSAB.RRF';
  private sourcesCache: Source[] | null = null;
  private cacheVersionKey = 'cachedSourcesVersion';
  private cacheDataKey = 'cachedSources';

  constructor(private http: HttpClient) { }

  private setCache(sources: Source[], version: string): void {
    this.sourcesCache = sources;
    localStorage.setItem(this.cacheDataKey, JSON.stringify(sources));
    localStorage.setItem(this.cacheVersionKey, version);
  }

  private getCache(): Source[] | null {
    const cachedSources = localStorage.getItem(this.cacheDataKey);
    return cachedSources ? JSON.parse(cachedSources) : null;
  }

  private clearCache(): void {
    localStorage.removeItem(this.cacheDataKey);
    localStorage.removeItem(this.cacheVersionKey);
  }

  private parseRRF(data: string): Source[] {
    const lines = data.split('\n').filter(line => line.trim() !== '');
    return lines.map(line => {
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
  }

  private generateVersion(data: string): string {
    // Simple hash function to create a unique version based on the data content
    let hash = 0;
    for (let i = 0; i < data.length; i++) {
      const char = data.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash;
    }
    return hash.toString();
  }

  getSources(): Observable<Source[]> {
    const cachedData = this.getCache();
    const cachedVersion = localStorage.getItem(this.cacheVersionKey);

    if (cachedData && cachedVersion) {
      return of(cachedData); // Return cached data if it exists and version matches
    }

    // Fetch new data if no cache or version mismatch
    return this.http.get(this.fileUrl, { responseType: 'text' }).pipe(
      map((data: string) => {
        const transformedSources = this.parseRRF(data);
        const newVersion = this.generateVersion(data);
        this.setCache(transformedSources, newVersion); // Cache new data and version
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
