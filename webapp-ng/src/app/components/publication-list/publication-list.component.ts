import {Component, OnInit, ViewChild} from '@angular/core';
import {Observable} from "rxjs";
import {Publication} from "../../core/modules/openapi";
import {PublicationService} from "./publication.service";
import {AsyncPipe, NgFor} from "@angular/common";
import {MatTableDataSource} from '@angular/material/table';
import {map} from 'rxjs/operators';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';


@Component({
  selector: 'app-publication-list-component',
  templateUrl: './publication-list.component.html',
  styleUrl: './publication-list.component.less',
})
export class PublicationListComponent implements OnInit {
  publications = new MatTableDataSource<Publication>();
  displayedColumns = [ "pubKey", "title", "authors", "year", "citation", "edit", "delete" ];
  //, "pubmedid", "doi", "endnoteid", "url", "wittid", "biomodelRefs", "mathmodelRefs", "date"];

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private publicationService: PublicationService) {
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

  onSave() {
    const pub: Publication = {
      title: "string",
      authors: [
        "string"
      ],
      year: 0,
      citation: "string",
      pubmedid: "string",
      doi: "string",
      endnoteid: 0,
      url: "string",
      wittid: 0,
      biomodelRefs: [

      ],
      mathmodelRefs: [

      ],
      date: "2022-03-10"
    };
    this.publicationService.createPublication(pub).subscribe((pub) => {
      console.log(pub);
    });
  }

  onEdit(pub) {
    // nothing to do
    console.log("editing publication "+pub.title);
  }

  onDelete(pub) {
    // nothing to do
    this.publicationService.deletePublication(pub.pubKey).subscribe((pub) => {
      console.log(pub);
    });

  }

  applyFilter(filterText: string) {
    this.publications.filter = filterText.toLowerCase();
  }

  applyFilterTarget(eventTarget: EventTarget) {
    const filterValue = (eventTarget as HTMLInputElement).value;
    this.publications.filter = filterValue.toLowerCase();
  }
}
