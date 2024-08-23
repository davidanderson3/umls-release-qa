import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, ParamMap } from '@angular/router';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { ContentService } from '../content.service';
import { ApiService } from '../services/api.service';
import { HttpClient } from '@angular/common/http';
import { ViewportScroller } from '@angular/common';
import { Title } from '@angular/platform-browser';
import { MatTableDataSource } from '@angular/material/table';
import { SourceModel } from '../source.model';

@Component({
  selector: 'app-sources',
  templateUrl: './sources.component.html',
  styleUrls: ['./sources.component.css']
})

export class SourcesComponent implements OnInit {

  htmlContent: { [key: string]: SafeHtml } = {}; // Stores HTML content for each tab
  folderName!: string;
  dynamicHeading: string = '';
  activeTab: string = 'synopsis'; // Default active tab
  displayTabs: string[] = []; // Array to hold the tabs to display
  dataSource!: MatTableDataSource<any>;
  sources!: SourceModel[];
  sourceData: any;

  constructor(
    private contentService: ContentService,
    private sanitizer: DomSanitizer,
    private route: ActivatedRoute,
    private apiService: ApiService,
    private http: HttpClient,
    private viewportScroller: ViewportScroller,
    private titleService: Title,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      // Extract the segments from the URL
      const urlSegments = this.route.snapshot.url.map(segment => segment.path);
  
      if (urlSegments.length > 1 && urlSegments[0] === 'current') {
        this.folderName = urlSegments[1];
        this.dynamicHeading = this.folderName.toUpperCase();
        this.titleService.setTitle(`UMLS - ${this.dynamicHeading}`);
        
        // First, fetch the source data before attempting to load any content
        this.fetchSourceData().then(() => {
          // Ensure `sourceData` is available, then load content
          if (urlSegments.length > 2) {
            const tabName = this.getTabNameFromUrl(urlSegments[2]);
            this.setActiveTab(tabName);
          } else {
            this.loadHtmlContent('synopsis'); // Load default 'synopsis' content
          }
  
          // Now check for additional file existence asynchronously (optional)
          this.checkFileExistence();
        }).catch(error => {
          console.error('Error fetching source data, cannot load content:', error);
        });
      } else {
        console.error('URL structure not as expected');
      }
    });
  }
  
  fetchSourceData(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.folderName) {
        this.apiService.getSourceByAbbreviation(this.folderName).subscribe(
          response => {
            this.sourceData = response; // Assign the fetched data to the sourceData property
            console.log('Fetched sourceData:', this.sourceData);
            resolve();
          },
          error => {
            console.error('Error fetching data for abbreviation:', error);
            reject(error);
          }
        );
      } else {
        console.error('No folderName specified');
        reject('No folderName specified');
      }
    });
  }
  

  checkFileExistence(): Promise<void> {
    return new Promise((resolve, reject) => {
      const filesToCheck = {
        sourcerepresentation: 'sourcerepresentation.html',
        metarepresentation: 'metarepresentation.html'
      };
      const checks = Object.entries(filesToCheck).map(([tabName, fileName]) => {
        const path = `assets/content/${this.folderName}/${fileName}`;
        return this.http.head(path).toPromise()
          .then(() => this.displayTabs.push(tabName))
          .catch((error) => console.log(`File ${fileName} does not exist for tab ${tabName}`));
      });
  
      Promise.all(checks).then(() => resolve());
    });
  }

  getTabNameFromUrl(path: string): string {
    switch (path) {
      case 'index.html': return 'synopsis';
      case 'metadata.html': return 'metadata';
      case 'stats.html': return 'statistics';
      case 'metarepresentation.html': return 'metarepresentation';
      case 'sourcerepresentation.html': return 'sourcerepresentation';
      // Add cases for other tabs if necessary
      default: return 'synopsis';
    }
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  
    // Only load content if it hasn't been loaded before (cache it after the first load)
    if (!this.htmlContent[tab]) {
      this.loadHtmlContent(tab).then(() => {
        console.log(`Content for ${tab} loaded and cached.`);
      });
    }
  
    // Scroll to top of the page when tab is set
    this.viewportScroller.scrollToPosition([0, 0]);
  
    // Navigate to the new URL with 'current' included
    let routePath: string;
    switch (tab) {
      case 'synopsis':
        routePath = 'index.html';
        break;
      case 'metadata':
        routePath = 'metadata.html';
        break;
      case 'statistics':
        routePath = 'stats.html';
        break;
      case 'metarepresentation':
        routePath = 'metarepresentation.html';
        break;
      case 'sourcerepresentation':
        routePath = 'sourcerepresentation.html';
        break;
      default:
        routePath = `${tab}.html`;
    }
    this.router.navigate(['/current', this.folderName, routePath]);
  }
  

  loadHtmlContent(tab: string): Promise<void> {
    return new Promise((resolve, reject) => {
      if (tab === 'metadata' && this.sourceData) {
        this.loadMetadataContent();
        resolve();
        return;
      }

      let filename;
      switch (tab) {
        case 'synopsis':
          filename = 'index';
          break;
        case 'statistics':
          filename = 'stats';
          break;
        case 'metarepresentation':
          filename = 'metarepresentation';
          break;
        case 'sourcerepresentation':
          filename = 'sourcerepresentation';
          break;
        default:
          filename = tab;
      }
  
      const filePath = `assets/content/${this.folderName}/${filename}.html`;
      console.log('Fetching content from:', filePath); // For debugging
  
      this.contentService.getHtmlContent(filePath).toPromise()
        .then(content => {
          this.htmlContent[tab] = this.sanitizer.bypassSecurityTrustHtml(content || '');
          resolve();
        })
        .catch(error => {
          console.error(`Error loading content for ${tab}:`, error);
          reject(error);
        });
    });
  }

  loadMetadataContent(): void {
    if (this.sourceData) {
      const formatField = (field: string) => {
        return field !== 'NONE' ? field.replace(/^;+/, '').replace(/;+/g, '<br>') : 'N/A';
      };
  
      const contentContactDetails = formatField(this.sourceData.contentContact);
      const licenseContactDetails = formatField(this.sourceData.licenseContact);
      const citationDetails = formatField(this.sourceData.citation);
  
      const metadataHtml = `
        <div>
          <table>
            <tr>
              <td><strong>Versioned Source Abbreviation:</strong></td>
              <td>${this.sourceData.vsab !== 'NONE' ? this.sourceData.vsab : ''}</td>
            </tr>
            <tr>
              <td><strong>Source Official Name:</strong></td>
              <td>${this.sourceData.sourceOfficialName !== 'NONE' ? this.sourceData.sourceOfficialName : ''}</td>
            </tr>
            <tr>
              <td><strong>Short Name:</strong></td>
              <td>${this.sourceData.shortName !== 'NONE' ? this.sourceData.shortName : ''}</td>
            </tr>
            <tr>
              <td><strong>Family:</strong></td>
              <td>${this.sourceData.family !== 'NONE' ? this.sourceData.family : ''}</td>
            </tr>
            <tr>
              <td><strong>Last Updated:</strong></td>
              <td>${this.sourceData.lastUpdated !== 'NONE' ? this.sourceData.lastUpdated : ''}</td>
            </tr>
            <tr>
              <td><strong>Restriction Level:</strong></td>
              <td>${this.sourceData.restrictionLevel !== 'NONE' ? this.sourceData.restrictionLevel : ''}</td>
            </tr>
            <tr>
              <td><strong>Language:</strong></td>
              <td>${this.sourceData.languageAbbreviation !== 'NONE' ? this.sourceData.languageAbbreviation : ''}</td>
            </tr>
            <tr>
              <td><strong>License Contact:</strong></td>
              <td>${licenseContactDetails}</td>
            </tr>
            <tr>
              <td><strong>Content Contact:</strong></td>
              <td>${contentContactDetails}</td>
            </tr>
            <tr>
              <td><strong>Citation:</strong></td>
              <td>${citationDetails}</td>
            </tr>
          </table>
        </div>
      `;
    
      this.htmlContent['metadata'] = this.sanitizer.bypassSecurityTrustHtml(metadataHtml);
    } else {
      console.error('No source data available to load metadata content');
    }
  }
  
  

  onAnchorClick(event: Event): void {
    const anchor = event.target as HTMLAnchorElement;
    if (anchor.hash) {
      event.preventDefault();
      const elementId = anchor.hash.slice(1); // Remove the '#' symbol
      this.viewportScroller.scrollToAnchor(elementId);
    }
  }
}
