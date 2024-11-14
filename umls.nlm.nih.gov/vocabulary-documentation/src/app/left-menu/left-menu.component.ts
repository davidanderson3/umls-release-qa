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
    // Fetch sources from ApiService, relying on its caching mechanism
    this.apiService.getSources().subscribe(
      data => {
        this.sources = data;
        this.groupByFirstLetter();
        this.sortKeysAndSources();

        console.log('Fetched and processed Sources:', this.sources);
        console.log('Grouped Sources:', this.groupedSources);
        console.log('Sorted Keys:', this.sortedKeys);

        this.cdr.detectChanges(); // Trigger change detection to update the view
      },
      error => {
        console.error('Error fetching sources:', error);
      }
    );
  }

  groupByFirstLetter(): void {
    this.groupedSources = {}; // Reset before grouping
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
