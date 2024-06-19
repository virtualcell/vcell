import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Publication } from 'src/app/core/modules/openapi/model/publication';
import {BiomodelRef, MathmodelRef} from "../../core/modules/openapi"; // Adjust the import path as necessary

@Component({
  selector: 'app-publication-edit',
  templateUrl: './publication-edit.component.html',
  styleUrls: ['./publication-edit.component.css']
})
export class PublicationEditComponent {
  @Input() publication!: Publication;// The publication to edit
  @Output() save = new EventEmitter<Publication>(); // Event to emit when the publication is saved
  @Output() cancel = new EventEmitter<void>(); // Event to emit when the edit is canceled

  onSave() {
    this.save.emit(this.publication);
  }

  onCancel() {
    this.cancel.emit();
  }

  getBmKeys(biomodelRefs: Array<BiomodelRef> | undefined): string {
    if (!biomodelRefs) return "";
    return biomodelRefs?.map(bm => bm.bmKey?.toString()).join(", ");
  }

  setBmRefsFromBmKeys(bmKeyString: string[]) {
    this.publication.biomodelRefs = bmKeyString.map(bmKey => {
      return {bmKey: parseInt(bmKey)};
    });
  }

  getMmKeys(mathmodelRefs: Array<MathmodelRef> | undefined): string {
    if (!mathmodelRefs) return "";
    return mathmodelRefs?.map(mm => mm.mmKey?.toString()).join(", ");
  }

  setMmRefsFromMmKeys(mmKeyString: string[]) {
    this.publication.mathmodelRefs = mmKeyString.map(mmKey => {
      return {mmKey: parseInt(mmKey)};
    });
  }
}
