import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {Publication} from "../core/modules/openapi";
import {PublicationService} from "../services/publication.service";
import {AsyncPipe, NgFor} from "@angular/common";

@Component({
  selector: 'app-publication-list-component',
  standalone: true,
  imports: [
    AsyncPipe,
    NgFor
  ],
  templateUrl: './publication-list-component.component.html',
  styleUrl: './publication-list-component.component.less'
})
export class PublicationListComponentComponent implements OnInit {
  publications$: Observable<Publication[]> = new Observable<Publication[]>();

  constructor(private publicationService: PublicationService) {
  }

  ngOnInit() {
    this.publications$ = this.publicationService.getPublicationList();
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
}
