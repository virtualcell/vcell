import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Publication} from "../../core/modules/openapi";
import {PublicationService} from "../publication-list/publication.service";
import {PublicationEditComponent} from "../publication-edit/publication-edit.component";
import {Router} from "@angular/router";

@Component({
  selector: 'app-publication-new',
  standalone: true,
  imports: [CommonModule, PublicationEditComponent],
  templateUrl: './publication-new.component.html',
  styleUrl: './publication-new.component.css'
})
export class PublicationNewComponent {
  newPublication: Publication;

  constructor(private publicationService: PublicationService, private router: Router) {
    this.newPublication = this.onNew();
  }

  onNew(): Publication {
    return {
      title: "title",
      authors: [
        "string"
      ],
      year: 2023,
      citation: "citation",
      pubmedid: "pubmed-id",
      doi: "doi",
      endnoteid: 0,
      url: "url",
      wittid: 0,
      biomodelRefs: [],
      mathmodelRefs: [],
      date: "2023-12-28"
    };
  }

  saveNewPublication(pub: Publication) {
    // Call the service to save the publication
    this.publicationService.createPublication(pub).subscribe({
      next: () => {
        console.log("saved publication");
        this.router.navigate(['/publications']);
      },
      error: (err) => {
        console.error("Error saving publication", err);
      }
    });
  }

  cancel() {
    console.log("Creation cancelled");
    this.router.navigate(['/publications']);
  }

}
