import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Publication} from 'src/app/core/modules/openapi/model/publication';
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {BioModel, BiomodelRef, BioModelResourceService, PublicationResourceService} from "../../core/modules/openapi";
import {HttpResponse} from "@angular/common/http";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatSnackBar} from "@angular/material/snack-bar";
import {AuthorizationService} from "../../services/authorization.service";

@Component({
  selector: 'app-publication-edit',
  templateUrl: './publication-edit.component.html',
  standalone: true,
  imports: [CommonModule, FormsModule, MatInputModule, MatButtonModule, MatCardModule, MatIconModule, MatCheckboxModule],
  styleUrls: ['./publication-edit.component.css']
})
export class PublicationEditComponent implements OnInit {
  @Input() publication!: Publication;
  @Output() save = new EventEmitter<Publication>();
  @Output() cancel = new EventEmitter<void>();
  @Output() published = new EventEmitter<void>();
  newBiomodelKey: number | undefined;
  newMathmodelKey: number | undefined;
  selectedBiomodelKeys = new Set<number>();
  selectedMathmodelKeys = new Set<number>();
  showPublishControls = false;

  constructor(
    private bioModelService: BioModelResourceService,
    private publicationResourceService: PublicationResourceService,
    private authorizationService: AuthorizationService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit() {
    this.authorizationService.isCurator().subscribe(isCurator => {
      this.showPublishControls = isCurator;
      if (isCurator) {
        this.selectAllModels();
      }
    });
  }

  private selectAllModels() {
    this.selectedBiomodelKeys.clear();
    this.selectedMathmodelKeys.clear();
    if (this.publication?.biomodelRefs) {
      for (const ref of this.publication.biomodelRefs) {
        if (ref.bmKey != null) this.selectedBiomodelKeys.add(ref.bmKey);
      }
    }
    if (this.publication?.mathmodelRefs) {
      for (const ref of this.publication.mathmodelRefs) {
        if (ref.mmKey != null) this.selectedMathmodelKeys.add(ref.mmKey);
      }
    }
  }

  toggleBiomodelSelection(key: number) {
    if (this.selectedBiomodelKeys.has(key)) {
      this.selectedBiomodelKeys.delete(key);
    } else {
      this.selectedBiomodelKeys.add(key);
    }
  }

  toggleMathmodelSelection(key: number) {
    if (this.selectedMathmodelKeys.has(key)) {
      this.selectedMathmodelKeys.delete(key);
    } else {
      this.selectedMathmodelKeys.add(key);
    }
  }

  getVersionFlagLabel(versionFlag: number | undefined): string {
    switch (versionFlag) {
      case 3: return 'Published';
      case 1: return 'Archived';
      case 0: return 'Current';
      default: return 'Unknown';
    }
  }

  getVersionFlagClass(versionFlag: number | undefined): string {
    switch (versionFlag) {
      case 3: return 'badge-published';
      case 1: return 'badge-archived';
      case 0: return 'badge-current';
      default: return 'badge-unknown';
    }
  }

  getPrivacyLabel(privacy: number | undefined): string {
    if (privacy === 0) return 'Public';
    if (privacy === 1) return 'Private';
    if (privacy != null && privacy > 1) return 'Shared';
    return 'Unknown';
  }

  getPrivacyClass(privacy: number | undefined): string {
    if (privacy === 0) return 'badge-public';
    if (privacy === 1) return 'badge-private';
    if (privacy != null && privacy > 1) return 'badge-shared';
    return 'badge-unknown';
  }

  publishModels() {
    if (!this.publication?.pubKey) return;
    const biomodelKeys = Array.from(this.selectedBiomodelKeys);
    const mathmodelKeys = Array.from(this.selectedMathmodelKeys);
    if (biomodelKeys.length === 0 && mathmodelKeys.length === 0) return;

    if (!confirm('Are you sure you want to publish the selected models? This will set them to public access with Published status.')) {
      return;
    }

    this.publicationResourceService.publishBioModels(this.publication.pubKey, { biomodelKeys, mathmodelKeys }).subscribe({
      next: () => {
        this.snackBar.open('Models published successfully', 'Dismiss', { duration: 5000 });
        this.published.emit();
      },
      error: (err: any) => {
        this.snackBar.open('Error publishing models: ' + (err.error?.message || err.message), 'Dismiss', { duration: 5000 });
        console.error('Error publishing models', err);
      }
    });
  }

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
          versionFlag: biomodel.versionFlag,
          privacy: biomodel.privacy
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
