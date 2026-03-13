# VCell Publications and Model Curation

## What is a Publication in VCell?

A VCell publication links a scientific paper to the computational models (BioModels and MathModels) that support it. Publications serve two purposes:

1. **Discovery** - Users can browse publications to find curated, peer-reviewed models relevant to their research.
2. **Curation** - Publications drive the process of reviewing, approving, and permanently preserving models in the VCell database.

## How Users Find Published Models

In the VCell desktop client, the **Database** panel (lower left of any document window) organizes public models into three folders:

- **Published** - Models created by users and linked to a scientific publication. Grouped by publication citation.
- **Curated** - Models created by the VCell team or collaborators to reproduce results described in a publication.
- **Uncurated** - Public models that have not been peer-reviewed or linked to a publication.

Published and Curated models represent the highest quality tier - they have been reviewed, are permanently preserved, and are linked to their corresponding papers.

## How to Publish a VCell Model (Author Instructions)

When your paper is accepted and you want to associate your VCell model with the publication, follow these steps:

### 1. Make Your Model Public

Change your model's permissions in the VCell Database:
- Navigate to **File > Permissions...**
- Select **Public**
- Click **OK**

The model will initially appear in the **Uncurated** public models folder. You can also share with specific users by selecting **Grant Access to Specific Users** and adding their VCell usernames.

### 2. Reference VCell in Your Publication

Include your username and model name so readers can find your model:

> "The Virtual Cell Model, *[modelname]* by user *[username]*, can be accessed within the VCell software (available at https://vcell.org)."

### 3. Acknowledge the Funding Source

Include this acknowledgment in your manuscript:

> "The Virtual Cell is supported by NIH Grant R24 GM137787 from the National Institute for General Medical Sciences."

### 4. Cite the Required VCell Papers

All publications using VCell must cite:
- Schaff, J., C. C. Fink, B. Slepchenko, J. H. Carson, and L. M. Loew. 1997. A general computational framework for modeling cellular structure and function. Biophysical journal 73:1135-1146. PMC1181013 PMID: 9284281 DOI: 10.1016/S0006-3495(97)78146-3
- Cowan, A. E., Moraru, II, J. C. Schaff, B. M. Slepchenko, and L. M. Loew. 2012. Spatial modeling of cell signaling networks. Methods Cell Biol 110:195-221. PMC3519356 PMID: 22482950 DOI: 10.1016/B978-0-12-388403-9.00008-4

Additional citations for specialized model types:
- **Rule-Based models**: 
  - Blinov, M. L., J. C. Schaff, D. Vasilescu, Moraru, II, J. E. Bloom, and L. M. Loew. 2017. Compartmental and Spatial Rule-Based Modeling with Virtual Cell. Biophysical journal 113:1365-1372. PMC5627391 PMID: 28978431 DOI: 10.1016/j.bpj.2017.08.022 
- **Spatial Hybrid Deterministic-Stochastic models**: 
  - Schaff, J. C., F. Gao, Y. Li, I. L. Novak, and B. M. Slepchenko. 2016. Numerical Approach to Spatial Deterministic-Stochastic Models Arising in Cell Biology. PLoS Comput Biol 12:e1005236. PMC5154471 PMID: 27959915 DOI: 10.1371/journal.pcbi.1005236

### 5. Submit Your Publication Information

Complete the [VCell Publication Submission Form](https://vcell.org/publish-a-vcell-model) with:
- Your name, email, and VCell username
- Article title, authors, and journal citation
- Publication date
- Model type (BioModel or MathModel) and model name

Upon approval by the VCell team, your model will be archived, listed on the VCell website, and protected from modification or deletion.

### Archiving Without Publishing (Deprecated)

Previously, authors could archive models independently through the VCell desktop client by right-clicking a model in the Database panel and selecting **Archive**. Archiving protected a model from deletion or alteration but did not link it to a publication. This option was removed because it caused confusion among users about the distinction between archiving and publishing.

## Model Lifecycle

Every model in VCell has a **curation status** (`VersionFlag`) that tracks where it is in the curation lifecycle:

```
  Current (0)  -->  Archived (1)  -->  Published (3)
   (default)       (protected)        (protected + public)
```

| Status | Who can see it | Can it be deleted? | Typical meaning |
|--------|---------------|-------------------|-----------------|
| **Current** | Depends on access settings | Yes | Work in progress or uncurated model |
| **Archived** | Depends on access settings | No (admin only) | Preserved for reference, may or may not be linked to a publication |
| **Published** | Everyone (public) | No (admin only) | Curated, peer-reviewed, linked to a publication |

Curation status is independent from access control. A model can be publicly visible but still in "Current" status (not yet curated), or it could be archived but only visible to certain users.

## Access Control (GroupAccess)

Access control determines who can view a model. It is a separate concept from curation status.

| Level | Description |
|-------|-------------|
| **Private** | Only the model owner can see it (default) |
| **Shared** | The owner and specific named users can see it |
| **Public** | Everyone can see it |

When a model is published through the curation workflow, its access is automatically set to Public. However, a model author can independently make their model public at any time without it being "published" in the curation sense.

## Curator Workflow

Curators are VCell team members with the `publicationEditors` role. Curation is performed through the Angular webapp at [vcell.cam.uchc.edu](https://vcell.cam.uchc.edu). Curators are responsible for:

- Creating and editing publication records (title, authors, DOI, PubMed ID, etc.)
- Linking the correct model versions to each publication
- Reviewing models for correctness and completeness
- **Publishing** models - the act that makes them permanently public and protected from deletion

### What Happens When a Curator Publishes Models

When a curator clicks "Publish Selected Models" in the webapp (or calls the publish API), two things happen atomically for each selected model:

1. **Access becomes public** - The model's access control is set to `GroupAccessAll`, making it visible to all VCell users.
2. **Status becomes Published** - The model's `VersionFlag` is set to `Published`, protecting it from deletion.

This is an intentionally irreversible operation through normal means. Published models are part of the scientific record and should not be casually removed.

### Selective Publishing

Curators can choose to publish all models linked to a publication at once, or selectively publish individual models. This is useful when a publication links to multiple models but only some are ready for curation.

## For Developers

See [publications-api.md](publications-api.md) for the REST API reference, data models, and source file locations.
