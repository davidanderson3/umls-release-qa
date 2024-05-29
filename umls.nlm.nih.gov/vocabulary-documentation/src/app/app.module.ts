import { NgModule } from '@angular/core'
import { BrowserModule } from '@angular/platform-browser'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'
import { HttpClientModule } from '@angular/common/http'

import { MatTableModule } from '@angular/material/table'
import { MatPaginatorModule } from '@angular/material/paginator'
import { MatSortModule } from '@angular/material/sort'
import { MatButtonModule } from '@angular/material/button'
import { MatIconModule } from '@angular/material/icon'
import { MatExpansionModule } from '@angular/material/expansion'

import { AppComponent } from './app.component'
import { FormsModule } from '@angular/forms'
import { RouterModule, Routes } from '@angular/router'
import { SourcesComponent } from './sources/sources.component'
import { AppRoutingModule } from './app-routing.module'
import { HomePageComponent } from './home-page/home-page.component'
import { LeftMenuComponent } from './left-menu/left-menu.component'
import { HeaderComponent } from './header/header.component'
import { FooterComponent } from './footer/footer.component'

const routes: Routes = [
	// ... other routes
	{ path: ':folder_name/index.html', component: SourcesComponent }
]


@NgModule({
	declarations: [
		AppComponent,
		SourcesComponent,
		HomePageComponent,
		LeftMenuComponent,
		HeaderComponent,
		FooterComponent
	],
	imports: [
		BrowserModule,
		BrowserAnimationsModule,
		HttpClientModule,
		MatTableModule,
		MatPaginatorModule,
		MatSortModule,
		MatButtonModule,
		MatIconModule,
		FormsModule,
		RouterModule.forRoot(routes, {
    initialNavigation: 'enabledBlocking'
}),
		AppRoutingModule,
		MatExpansionModule
	],
	providers: [],
	bootstrap: [AppComponent]
})
export class AppModule { }
