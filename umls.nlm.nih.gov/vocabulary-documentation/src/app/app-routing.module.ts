import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { SourcesComponent } from './sources/sources.component'
import { HomePageComponent } from './home-page/home-page.component'
import { NotFoundComponent } from './not-found/not-found.component'
import { FolderNameValidatorGuard } from './folder-name-validator.guard';


const routes: Routes = [
    { path: '', component: HomePageComponent },
  
    // Handle the folder name without a file
    { path: 'current/:folder_name', component: SourcesComponent, canActivate: [FolderNameValidatorGuard], pathMatch: 'full' },
  
    // Routes for specific files
    { path: 'current/:folder_name/index.html', component: SourcesComponent, canActivate: [FolderNameValidatorGuard], pathMatch: 'full' },
    { path: 'current/:folder_name/metadata.html', component: SourcesComponent, canActivate: [FolderNameValidatorGuard], pathMatch: 'full' },
    { path: 'current/:folder_name/stats.html', component: SourcesComponent, canActivate: [FolderNameValidatorGuard], pathMatch: 'full' },
    { path: 'current/:folder_name/metarepresentation.html', component: SourcesComponent, canActivate: [FolderNameValidatorGuard], pathMatch: 'full' },
    { path: 'current/:folder_name/sourcerepresentation.html', component: SourcesComponent, canActivate: [FolderNameValidatorGuard], pathMatch: 'full' },
  
    // 404 Page
    { path: 'not-found', component: NotFoundComponent },
  
    // Wildcard route for handling other invalid URLs
    { path: '**', component: NotFoundComponent }
  ];
  

@NgModule({
	imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'top' })],
	exports: [RouterModule]
})
export class AppRoutingModule { }

RouterModule.forRoot(routes, { enableTracing: true, initialNavigation: 'enabledBlocking' }) // <-- debugging purposes only
 // <-- debugging purposes only
