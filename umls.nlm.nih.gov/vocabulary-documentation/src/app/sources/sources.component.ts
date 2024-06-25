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
      
      // Assuming the structure is ['current', 'folder_name', ...]
      // Check if the first segment is 'current' and extract the folder name accordingly
      if (urlSegments.length > 1 && urlSegments[0] === 'current') {
        this.folderName = urlSegments[1];
        this.dynamicHeading = this.folderName.toUpperCase();
        this.fetchSourceData();
        this.titleService.setTitle(`UMLS - ${this.dynamicHeading}`);
        this.checkFileExistence().then(() => {
          // Logic for setting the active tab
          if (urlSegments.length > 2) {
            const tabName = this.getTabNameFromUrl(urlSegments[2]);
            this.setActiveTab(tabName);
          } else {
            this.loadHtmlContent('synopsis'); // Load default tab content
          }
        });
      } else {
        console.error('URL structure not as expected');
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

  fetchSourceData(): void {
    if (this.folderName) {
      this.apiService.getSourceByAbbreviation(this.folderName).subscribe(
        response => {
          this.sourceData = response; // Assigning the fetched data to the sourceData property
          if (this.activeTab === 'metadata') {
            this.loadMetadataContent();
          }
        },
        error => {
          console.error('Error fetching data for abbreviation:', error);
        }
      );
    } else {
      console.error('No folderName specified');
    }
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
    if (tab === 'metadata') {
      this.loadMetadataContent();
    } else {
      if (!this.htmlContent[tab]) {
        this.loadHtmlContent(tab);
      }
  
      // Find the title div element with the ID 'source-name' and scroll to its position
      const titleElement = document.getElementById('source-name');
      if (titleElement) {
        const titlePosition = titleElement.getBoundingClientRect().top + window.pageYOffset - 10; // 10px offset for a small gap
        window.scrollTo({ top: titlePosition, behavior: 'smooth' });
      }
    }

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
      const contentContactDetails = this.sourceData.contentContact ? `
      ${this.sourceData.contentContact.name}<br>
      ${this.sourceData.contentContact.organization}<br>
      ${this.sourceData.contentContact.address1}<br>
      ${this.sourceData.contentContact.address2 ? this.sourceData.contentContact.address2 + '<br>' : ''}
      ${this.sourceData.contentContact.city}, ${this.sourceData.contentContact.stateOrProvince} ${this.sourceData.contentContact.zipCode}<br>
      ${this.sourceData.contentContact.email}
    ` : 'N/A';

    const licenseContactDetails = this.sourceData.licenseContact ? `
      ${this.sourceData.licenseContact.name}<br>
      ${this.sourceData.licenseContact.organization}<br>
      ${this.sourceData.licenseContact.address1}<br>
      ${this.sourceData.licenseContact.address2 ? this.sourceData.licenseContact.address2 + '<br>' : ''}
      ${this.sourceData.licenseContact.city}, ${this.sourceData.licenseContact.stateOrProvince} ${this.sourceData.licenseContact.zipCode}<br>
      ${this.sourceData.licenseContact.email}
    ` : 'N/A';
      const metadataHtml = `
        <div>
          
          <table>
          <tr>
          <td><strong>Source Official Name:</strong></td>
          <td>${this.sourceData.preferredName}</td>
        </tr>
            <tr>
              <td><strong>Short Name:</strong></td>
              <td>${this.sourceData.shortName}</td>
            </tr>
            <tr>
            <td><strong>Family:</strong></td>
            <td>${this.sourceData.family}</td>
            </tr>
            <tr>
            <td><strong>Restriction Level:</strong></td>
            <td>${this.sourceData.restrictionLevel}</td>
          </tr>
            <tr>
              <td><strong>Language:</strong></td>
              <td>${this.sourceData.languageAbbreviation}</td>
            </tr>
            <tr>
            <td><strong>Content Contact:</strong></td>
            <td>${contentContactDetails}</td>
          </tr>
          <tr>
            <td><strong>License Contact:</strong></td>
            <td>${licenseContactDetails}</td>
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

  private scrollToElement(elementId: string): void {
    const element = document.getElementById(elementId);
    if (element) {
      const elementPosition = element.getBoundingClientRect().top + window.pageYOffset;
      const offsetPosition = elementPosition - 10; // Adjust the offset as needed
      window.scrollTo({
        top: offsetPosition,
        behavior: 'smooth'
      });
    } else {
      console.log("Element not found for ID:", elementId);
    }
  }
}
