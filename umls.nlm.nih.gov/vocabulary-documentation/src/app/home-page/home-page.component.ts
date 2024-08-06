import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { ApiService } from '../services/api.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort, Sort, MatSortable } from '@angular/material/sort';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit, AfterViewInit {
  sources!: { abbreviation: string; shortName: string; lastUpdated: string; languageAbbreviation: string; restrictionLevel: string; expandedForm: string; family: string }[];
  displayedColumns: string[] = ['abbreviation', 'shortName', 'lastUpdated','languageAbbreviation', 'restrictionLevel'];
  dataSource: MatTableDataSource<any>;
  searchText: string = '';
  showTranslations: boolean = false;
  filteredDataSource: MatTableDataSource<any>;

  @ViewChild(MatSort) sort!: MatSort;

  constructor(private apiService: ApiService, private router: Router, private titleService: Title) {
    this.dataSource = new MatTableDataSource<any>();
    this.filteredDataSource = new MatTableDataSource<any>();
  }

  ngOnInit() {
    this.apiService.getSources().subscribe((data: any) => {
      this.sources = data;
      this.dataSource.data = this.sources;
      this.filteredDataSource.data = this.sources;
      this.applyFilter();
      this.titleService.setTitle('UMLS Source Vocabulary Documentation');
    });
  }

  ngAfterViewInit() {
    this.filteredDataSource.sort = this.sort;
    const sortState: MatSortable = {
      id: 'abbreviation',
      start: 'asc',
      disableClear: true
    };
    this.filteredDataSource.sort.sort(sortState);
  }

  applyFilter() {
    const filterValue = this.searchText.trim().toLowerCase();
    this.filteredDataSource.data = this.dataSource.data.filter(row => {
      const textMatch = filterValue ? row.abbreviation.toLowerCase().includes(filterValue) || row.shortName.toLowerCase().includes(filterValue) : true;
      const langMatch = this.showTranslations ? true : row.languageAbbreviation === 'ENG';
      return textMatch && langMatch;
    });
  }

  sortData(event: Sort) {
    const sortState: Sort = { ...event };

    if (!sortState.direction) {
      sortState.direction = 'asc';
    }

    const matSortable: MatSortable = {
      id: sortState.active,
      start: sortState.direction,
      disableClear: true
    };

    this.filteredDataSource.sort = this.sort;
    this.filteredDataSource.sort.sort(matSortable);
  }
}
