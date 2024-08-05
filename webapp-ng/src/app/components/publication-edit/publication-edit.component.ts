import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Publication } from 'src/app/core/modules/openapi/model/publication';
import {PublicationService} from "../publication-list/publication.service";
import {FormsModule} from "@angular/forms"; // Adjust the import path as necessary

@Component({
  selector: 'app-publication-edit',
  templateUrl: './publication-edit.component.html',
  standalone: true,
  imports: [
    FormsModule
  ],
  styleUrls: ['./publication-edit.component.css']
})
export class PublicationEditComponent {
  @Input() publication!: Publication;// The publication to edit
  @Output() save = new EventEmitter<Publication>(); // Event to emit when the publication is saved
  @Output() cancel = new EventEmitter<void>(); // Event to emit when the edit is canceled

  constructor(private publicationService: PublicationService) {
  }

  onSave() {
    this.save.emit(this.publication);
  }

  onCancel() {
    this.cancel.emit();
  }

  saveEdit(pub: Publication) {
    // Call the service to save the publication
    this.publicationService.updatePublication(pub).subscribe({
      next: () => {
        // show message that the record was saved
        console.log("Publication saved");
      },
      error: (err) => {
        // show error message
        console.error("Error saving publication", err);
      }
    });
  }

  onDelete(pub: Publication) {
    // nothing to do
    this.publicationService.deletePublication(pub.pubKey!).subscribe((pub) => {
      console.log(pub);
    });

  }


}
