import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Publication} from 'src/app/core/modules/openapi/model/publication';
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {BioModel, BiomodelRef, BioModelResourceService} from "../../core/modules/openapi";
import {HttpResponse} from "@angular/common/http";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {MatSnackBar} from "@angular/material/snack-bar"; // Adjust the import path as necessary

@Component({
  selector: 'app-publication-edit',
  templateUrl: './publication-edit.component.html',
  standalone: true,
  imports: [CommonModule, FormsModule, MatInputModule, MatButtonModule, MatCardModule, MatIconModule],
  styleUrls: ['./publication-edit.component.css']
})
export class PublicationEditComponent {
  @Input() publication!: Publication;// The publication to edit
  @Output() save = new EventEmitter<Publication>(); // Event to emit when the publication is saved
  @Output() cancel = new EventEmitter<void>(); // Event to emit when the edit is canceled
  newBiomodelKey: number | undefined;
  newMathmodelKey: number | undefined;

  constructor(private bioModelService: BioModelResourceService, private snackBar: MatSnackBar) { }

  addBiomodelRef(key: number | undefined) {
    console.log("addBiomodelRef, key: " + key);
    if (!key) {
      return;
    }
    this.bioModelService.getBioModel(key.toString(), "response").subscribe({
      next: (biomodelResponse: HttpResponse<BioModel>) => {
        if (biomodelResponse.status !== 200) {
          this.snackBar.open("Error fetching biomodel", "Dismiss", {duration: 5000});
          console.error("Error fetching biomodel", biomodelResponse);
          return;
        }
        if (!this.publication.biomodelRefs) {
          this.publication.biomodelRefs = [];
        }
        const biomodel = biomodelResponse.body as BioModel;
        const biomodelRef : BiomodelRef = {
          bmKey: biomodel.bmKey!=null ? Number.parseInt(biomodel.bmKey) : -1,
          name: biomodel.name,
          ownerName: biomodel.ownerName,
          ownerKey: biomodel.ownerKey!=null ? Number.parseInt(biomodel.ownerKey) : -1,
          versionFlag: biomodel.privacy
        };
        this.publication.biomodelRefs.push(biomodelRef);
        this.newBiomodelKey = undefined;
      },
      error: (err: any) => {
        this.snackBar.open("Error fetching biomodel", "Dismiss", {duration: 5000});
        console.error("Error fetching biomodel", err);
      }
    });
  }

  removeBiomodelRef(index: number) {
    console.log("removeBiomodelRef, index: " + index);
    if (!this.publication.biomodelRefs) {
      return;
    }
    this.publication.biomodelRefs.splice(index, 1);
  }

  addMathmodelRef(key: number | undefined) {
    if (!key) {
      return;
    }
    if (!this.publication.mathmodelRefs) {
      this.publication.mathmodelRefs = [];
    }
    this.snackBar.open("adding MathModels not yet implemented - requires API changes", "Dismiss", {duration: 5000});
    console.error("add MathmodelRef not yet implemented");
    // this.publicationService.getMathmodelRef(key).subscribe((mathmodelRef: MathmodelRef) => {
    //   this.publication.mathmodelRefs.push(mathmodelRef);
    // });
  }

  removeMathmodelRef(index: number) {
    if (!this.publication.mathmodelRefs) {
      return;
    }
    this.publication.mathmodelRefs.splice(index, 1);
  }

  onSave() {
    this.save.emit(this.publication);
  }

  onCancel() {
    this.cancel.emit();
  }

}
