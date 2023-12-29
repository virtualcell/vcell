import {Injectable} from '@angular/core';
import {Publication, PublicationResourceService} from '../../core/modules/openapi';
import {Observable, Subject} from 'rxjs';
import {map, switchMap, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class PublicationService {

  private refreshNeeded = new Subject();
  // filter: string;

  constructor(private pub_api: PublicationResourceService) {
  }

  getPublicationList(): Observable<Publication[]> {
    return this.refreshNeeded.pipe(
      switchMap(() => this.pub_api.getPublications()),
      map((data) => data)
    )
  }

  getPublicationById(id: number): Observable<Publication> {
    return this.pub_api.getPublicationById(id);
  }

  createPublication(publication: Publication) : Observable<number> {
    return this.pub_api.createPublication(publication).pipe(
      tap(() => this.refreshNeeded.next())
    )
  }

  deletePublication(id: number) : Observable<number> {
    return this.pub_api.deletePublication(id).pipe(
      tap(() => this.refreshNeeded.next())
    )
  }

  updatePublication(publication: Publication) : Observable<Publication> {
    return this.pub_api.updatePublication(publication).pipe(
      tap(() => this.refreshNeeded.next())
    )
  }

  public refresh() {
    this.refreshNeeded.next();
  }

}
