import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private apiUrl = 'https://uts-ws.nlm.nih.gov/rest/metadata/2023AB/sources';
  private sourcesCache: any[] | null = null;

  constructor(private http: HttpClient) { }

  private setCache(sources: any[]): void {
    this.sourcesCache = sources;
  }

  private getCache(): any[] | null {
    return this.sourcesCache;
  }

  getSources(): Observable<any> {
    // Check if the data is already cached
    const cachedData = this.getCache();
    if (cachedData) {
      return of(cachedData);
    }

    // Otherwise, fetch from the API
    return this.http.get(this.apiUrl).pipe(
      map((data: any) => {
        const transformedSources = data.result.map((source: any) => {
          return {
            abbreviation: source.abbreviation,
            shortName: source.shortName,
            languageAbbreviation: source.language?.expandedForm,
            restrictionLevel: source.restrictionLevel,
            expandedForm: source.expandedForm,
            family: source.family,
          };
        });
        // Cache the data
        this.setCache(transformedSources);
        return transformedSources;
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
