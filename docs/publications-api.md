# Publications and Model Curation

## Overview

Publications in VCell link scientific papers to the computational models (BioModels and MathModels) that were used or described in those papers. The publication system also drives the **curation workflow**, which controls model visibility and permanence.

## Domain Concepts

### Publication

A publication record represents a scientific paper and its metadata:

- **title**, **authors**, **year**, **citation** - bibliographic information
- **pubmedid**, **doi**, **url** - external identifiers and links
- **biomodelRefs** - array of BioModel references linked to this publication
- **mathmodelRefs** - array of MathModel references linked to this publication

Publications are managed by curators (users with the `publicationEditors` special claim).

### Model References (BiomodelRef / MathmodelRef)

Model references are lightweight pointers to BioModels or MathModels that are associated with a publication. Each reference includes:

- **key** (`bmKey` / `mmKey`) - the model's database identifier
- **name** - model name
- **ownerName** / **ownerKey** - the model owner
- **versionFlag** - the model's curation status (see below)

### VersionFlag (Curation Status)

`VersionFlag` represents a model's position in the curation lifecycle. It is **not** access control - that is handled separately by `GroupAccess`.

| State | Int Value | Description |
|-------|-----------|-------------|
| **Current** | 0 | Default state. Model is in active development. Can be modified or deleted. |
| **Archived** | 1 | Model has been archived. Cannot be deleted without admin intervention. |
| **Published** | 3 | Model has been curated and published. Cannot be deleted without admin intervention. |

**Key constraint**: Archived and Published models cannot be deleted through the normal API. This ensures the permanence of curated scientific models.

### GroupAccess (Access Control)

`GroupAccess` controls who can view a model. It is independent of `VersionFlag`.

| Type | GroupID | Description |
|------|---------|-------------|
| **GroupAccessAll** | 0 | Public - visible to everyone |
| **GroupAccessNone** | 1 | Private - visible only to the owner |
| **GroupAccessSome** | (hash) | Shared with a specific group of users |

## Curation Workflow

When a curator publishes models linked to a publication, two things happen atomically for each model:

1. **Access is set to public**: `GroupAccess` is changed to `GroupAccessAll` (groupid = 0)
2. **Status is set to published**: `VersionFlag` is changed to `Published` (value = 3)

This is performed by `PublicationService.publishDirectly()`, which delegates to `DbDriver.publishDirectly()` to update the `vc_biomodel` and `vc_mathmodel` database tables.

### Selective Publishing

Curators can publish all models linked to a publication at once, or selectively choose which models to publish by providing specific model keys in the `PublishModelsRequest`.

## REST API

Base path: `/api/v1/publications`

| Method | Path | Operation | Auth | Description |
|--------|------|-----------|------|-------------|
| GET | `/` | getPublications | Public | List all publications |
| GET | `/{id}` | getPublicationById | Public | Get a single publication |
| POST | `/` | createPublication | Curator | Create a new publication |
| PUT | `/` | updatePublication | Curator | Update publication metadata and model links |
| DELETE | `/{id}` | deletePublication | Curator | Delete a publication |
| PUT | `/{id}/publish` | publishBioModels | Curator | Publish models linked to a publication |

### Publish Endpoint

`PUT /api/v1/publications/{id}/publish`

Request body (`PublishModelsRequest`, optional):
```json
{
  "biomodelKeys": [12345, 67890],
  "mathmodelKeys": [11111]
}
```

- If the request body is null or keys arrays are empty, **all** models linked to the publication are published.
- If specific keys are provided, only those models are published.

## Key Source Files

| File | Purpose |
|------|---------|
| `handlers/PublicationResource.java` | REST endpoint definitions |
| `services/PublicationService.java` | Business logic for publication CRUD and publishing |
| `models/Publication.java` | Publication REST model |
| `models/BiomodelRef.java` | BioModel reference REST model |
| `models/MathmodelRef.java` | MathModel reference REST model |
| `models/PublishModelsRequest.java` | Request body for selective publishing |
| `vcell-core/.../VersionFlag.java` | Curation status enum |
| `vcell-core/.../GroupAccess.java` | Access control base class |
| `vcell-server/.../DbDriver.java` | Database-level publish logic |
