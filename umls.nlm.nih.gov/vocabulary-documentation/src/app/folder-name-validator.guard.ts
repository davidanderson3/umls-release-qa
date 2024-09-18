import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ApiService } from './services/api.service';

@Injectable({
  providedIn: 'root'
})
export class FolderNameValidatorGuard implements CanActivate {

  constructor(private apiService: ApiService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> {
    const folderName = route.params['folder_name'];

    return this.apiService.getSourceAbbreviations().pipe(
      map(validFolders => {
        if (validFolders.includes(folderName)) {
          return true;  // Allow access if folder name (sourceAbbreviation) is valid
        } else {
          this.router.navigate(['/not-found']);  // Redirect to 404 page if invalid
          return false;
        }
      }),
      catchError(error => {
        console.error('Error validating folder name:', error);
        this.router.navigate(['/not-found']);  // Redirect to 404 page on error
        return of(false);
      })
    );
  }
}
