import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { SourcesComponent } from './sources/sources.component'
import { HomePageComponent } from './home-page/home-page.component'


const routes: Routes = [
    // ... other routes
    { path: '', component: HomePageComponent },
    { path: 'current/:folder_name', component: SourcesComponent },
    { path: 'current/:folder_name/index.html', component: SourcesComponent },
    { path: 'current/:folder_name/metadata.html', component: SourcesComponent },
    { path: 'current/:folder_name/stats.html', component: SourcesComponent },
    { path: 'current/:folder_name/metarepresentation.html', component: SourcesComponent },
    { path: 'current/:folder_name/sourcerepresentation.html', component: SourcesComponent },
    // ... potentially other routes
];

@NgModule({
	imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'top' })],
	exports: [RouterModule]
})
export class AppRoutingModule { }

RouterModule.forRoot(routes, { enableTracing: true, initialNavigation: 'enabledBlocking' }) // <-- debugging purposes only
 // <-- debugging purposes only
