import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Publication} from 'src/app/core/modules/openapi/model/publication';
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button"; // Adjust the import path as necessary

@Component({
  selector: 'app-publication-edit',
  templateUrl: './publication-edit.component.html',
  standalone: true,
  imports: [CommonModule, FormsModule, MatInputModule, MatButtonModule],
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

}
