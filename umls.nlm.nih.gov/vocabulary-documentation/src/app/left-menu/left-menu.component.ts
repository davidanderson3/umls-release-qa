import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ApiService } from '../services/api.service';

@Component({
  selector: 'app-left-menu',
  templateUrl: './left-menu.component.html',
  styleUrls: ['./left-menu.component.css']
})
export class LeftMenuComponent implements OnInit {
  sources: any[] = [];
  groupedSources: { [key: string]: any[] } = {};
  sortedKeys: string[] = [];

  constructor(private apiService: ApiService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
  const cachedSources = localStorage.getItem('cachedSources');
  const cachedGroupedSources = localStorage.getItem('cachedGroupedSources');

  if (cachedSources && cachedGroupedSources) {

    this.sources = JSON.parse(cachedSources);

    // Explicitly call these methods to ensure data integrity
    this.groupByFirstLetter();
    this.sortKeysAndSources();

    console.log('Cached Sources:', this.sources);
    console.log('Cached Grouped Sources:', this.groupedSources);
    console.log('Cached Sorted Keys:', this.sortedKeys);

    this.cdr.detectChanges();
  } else {
    this.apiService.getSources().subscribe(
      data => {
        console.log('Fetched from API:', data);
        this.sources = data;
        this.groupByFirstLetter();
        this.sortKeysAndSources();

        // Cache the results
        localStorage.setItem('cachedSources', JSON.stringify(this.sources));
        localStorage.setItem('cachedGroupedSources', JSON.stringify(this.groupedSources));

        console.log('Live Sources:', this.sources);
        console.log('Live Grouped Sources:', this.groupedSources);
        console.log('Live Sorted Keys:', this.sortedKeys);
      },
      error => {
        console.error('Error fetching sources:', error);
      }
    );
  }
}


  groupByFirstLetter(): void {
    this.sources.forEach((source) => {
      const firstLetter = source.abbreviation.charAt(0).toUpperCase();
      if (!this.groupedSources[firstLetter]) {
        this.groupedSources[firstLetter] = [];
      }
      this.groupedSources[firstLetter].push(source);
    });
  }

  sortKeysAndSources(): void {
    this.sortedKeys = Object.keys(this.groupedSources).sort();
    for (const key of this.sortedKeys) {
      this.groupedSources[key].sort((a, b) => a.abbreviation.localeCompare(b.abbreviation));
    }
  }

  onSourceClick(source: any): void {
    console.log('Clicked on source:', source);
  }

  objectKeys(obj: any): string[] {
    return this.sortedKeys;
  }
}
