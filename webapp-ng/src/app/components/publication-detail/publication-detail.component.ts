import {Component, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {Publication} from "../../core/modules/openapi";
import {PublicationService} from "../publication-list/publication.service";
import {PublicationEditComponent} from "../publication-edit/publication-edit.component";
import {ActivatedRoute, Router} from "@angular/router";
import {isInteger} from "@ng-bootstrap/ng-bootstrap/util/util";

@Component({
  selector: 'app-publication-detail',
  standalone: true,
  imports: [CommonModule, PublicationEditComponent],
  templateUrl: './publication-detail.component.html',
  styleUrl: './publication-detail.component.css'
})
export class PublicationDetailComponent implements OnInit {
  @Input() publication!: Publication;

  constructor(
    private publicationService: PublicationService,
    private router: Router,
    private route: ActivatedRoute)
  {}

  saveUpdatedPublication(pub: Publication) {
    this.publicationService.updatePublication(pub).subscribe({
      next: () => {
        console.log("Publication updated");
        this.router.navigate(['/publications']);
      },
      error: (err) => {
        console.error("Error updating publication", err);
      }
    });
  }

  cancel() {
    console.log("Update cancelled");
    this.router.navigate(['/publications']);
  }

  ngOnInit(): void {
    const pubId = this.route.snapshot.paramMap.get('pubId');
    if (pubId && Number.isInteger(parseInt(pubId))) {
      this.publicationService.getPublicationById(parseInt(pubId)).subscribe({
        next: (publication) => {
          this.publication = publication;
        },
        error: (err) => {
          console.error('Error fetching publication', err);
        }
      });
    }
  }

}
