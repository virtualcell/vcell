import {Component, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {Publication} from "../../core/modules/openapi";
import {PublicationService} from "../publication-list/publication.service";
import {PublicationEditComponent} from "../publication-edit/publication-edit.component";
import {ActivatedRoute, Router} from "@angular/router";
import {isInteger} from "@ng-bootstrap/ng-bootstrap/util/util";
import {MatButtonModule} from "@angular/material/button";
import {MatDialog} from "@angular/material/dialog";
import {PublicationConfirmDialogComponent} from "../publication-confirm-dialog/publication-confirm-dialog.component";

@Component({
  selector: 'app-publication-detail',
  standalone: true,
  imports: [CommonModule, PublicationEditComponent, MatButtonModule],
  templateUrl: './publication-detail.component.html',
  styleUrl: './publication-detail.component.css'
})
export class PublicationDetailComponent implements OnInit {
  @Input() publication!: Publication;

  constructor(
    private publicationService: PublicationService,
    private router: Router,
    private route: ActivatedRoute,
    private dialog: MatDialog
  ) {}

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

  deletePublication(pub: Publication | undefined) {
    if (pub == undefined || pub.pubKey == undefined) {
      console.error("publication or publication key is undefined")
      return;
    }

    const dialogRef = this.dialog.open(PublicationConfirmDialogComponent);

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deletePublicationConfirmed(pub);
      }
    });
  }

  deletePublicationConfirmed(pub: Publication) {
    this.publicationService.deletePublication(pub.pubKey!).subscribe({
      next: () => {
        console.log("Publication deleted");
        this.router.navigate(['/publications']);
      },
      error: (err) => {
        console.error("Error deleting publication", err);
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
