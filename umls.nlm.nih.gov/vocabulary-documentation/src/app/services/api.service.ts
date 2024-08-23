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

  constructor(private http: HttpClient) { }

  private setCache(sources: Source[]): void {
    this.sourcesCache = sources;
  }

  private getCache(): Source[] | null {
    return this.sourcesCache;
  }

  private parseRRF(data: string): Source[] {
    console.log('Raw data:', data);  // Log raw data
    const lines = data.split('\n').filter(line => {
      const columns = line.split('|');
      return columns[0].trim() !== ''; // Ensure the first column is not empty
    });
    
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
