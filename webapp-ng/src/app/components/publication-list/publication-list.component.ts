import {Component, OnInit, ViewChild} from '@angular/core';
import {Publication} from '../../core/modules/openapi';
import {PublicationService} from './publication.service';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {Observable} from 'rxjs';
import {AuthorizationService} from 'src/app/services/authorization.service';


@Component({
  selector: 'app-publication-list-component',
  templateUrl: './publication-list.component.html',
  styleUrl: './publication-list.component.less',
})
export class PublicationListComponent implements OnInit {
  publications = new MatTableDataSource<Publication>();
  // displayedColumns = [ "pubKey", "title", "authors", "year", "citation", "pubmedid", "doi", "endnoteid", "url",
  //   "wittid", "biomodelRefs", "mathmodelRefs", "date"];
  displayedColumns = [ "pubKey", "title", "authors", "year", "citation", "pubmedid",
    "biomodelRefs", "mathmodelRefs", "date"];
  isCurator$: Observable<boolean>;

  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private authorizationService: AuthorizationService, private publicationService: PublicationService) {
    this.isCurator$ = this.authorizationService.isCurator();
  }

  ngOnInit() {
    this.publicationService.getPublicationList().subscribe(data => {
      this.publications.data = data;
      this.publications.sort = this.sort;
      this.publications.paginator = this.paginator;
      console.log(data);
    });
    this.publicationService.refresh();
  }

  applyFilterTarget(eventTarget: EventTarget | null) {
    const filterValue = (eventTarget as HTMLInputElement).value;
    this.publications.filter = filterValue.toLowerCase();
  }

}
