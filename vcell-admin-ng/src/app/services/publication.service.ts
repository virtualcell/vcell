import { Injectable } from '@angular/core';
import {PublicationResourceService} from "../core/modules/openapi";

@Injectable({
  providedIn: 'root'
})
export class PublicationService {

  constructor(private pub_api: PublicationResourceService) { }

  getPublicationList() {
    return this.pub_api.getPublications();
  }

  getPublicationById(id: number) {
    return this.pub_api.getPublicationById(id);
  }

  createPublication(publication: any) {
    return this.pub_api.createPublication(publication);
  }

  deletePublication(id: number) {
    return this.pub_api.deletePublication(id);
  }
}
