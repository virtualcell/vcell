# Model Explorer

The **Model Explorer** is a window for browsing every model you can open: the [VCell
database](VCellDatabase.md)'s BioModels, MathModels and geometries, and the [BioModels
Database](BioModelsNet.md). Its tree has the folders VCell's database tree has always had; selecting
anything in it shows its details beside it.

Along the top are a **Search** field, a **Filters** button that shows the search options under it
(where to look for the search text, the kind of model, its modeling approach, permissions, owner,
when it was saved, and the order), and **Refresh**, which reads the database's lists again. Beside
them, how many models and geometries are listed, and **Tree** and **Table**, which switch between
the two views of them.

At the left, the tree. BioModels and MathModels are listed together, each marked by its icon. Each
folder is arranged its own way:

- **My Models**: your models, by name.
- **Shared With Me**: models others have shared with you, by owner.
- **Public Models**:
  - **Published**: models published with a paper, by publication, newest first (named by first
    author, year and title).
  - **Curated**: curated models, by publication, newest first; then **Model Bricks**, small curated
    models made to be reused as parts of others.
  - **Uncurated**: other public models, by owner.
- **Tutorials**: the models VCell's tutorials use.
- **Geometries**: **My Geometries**, and those **Shared With Me** and **Public Geometries** by
  owner.
- **BioModels Database**: EBI's models, by name. Only the models VCell can import are listed, and
  they are found by their names and IDs only.

A model saved more than once has its versions listed under it, newest first, with when each was
saved and its permissions; the model itself stands for its latest version. Each folder and owner
says how many models it holds. **Published** and **Curated**, and each publication, count published
(or curated) versions instead, as VCell's database tree always has: a model published twice counts
twice. **Public Models** adds up its three folders.

The **Table** view lists the same models without the folders' hierarchy, one row each, with columns
for its **Name**, **Type**, the **Folders** the tree lists it in, **Owner**, when it was **Saved**,
how many **Versions** it has, its **Permissions**, its **Publication** and its **VCell ID**. Click a
column's heading to sort by it, and again to reverse the order. The search and filters narrow the
table as they do the tree (the BioModels Database's models are in the tree only). What's selected
stays selected when you switch between the views; opening, the right-click menu and comparing work
the same in both.

At the right, the details of what's selected:

- A model or version: its **Document** information (owner, when saved, permissions, VCell ID,
  versions, the VCell version it was saved with, and its description); its **BioModel**
  (applications and their types), **MathModel** (its kind, geometry and simulations) or **Geometry**
  (dimension, size, origin, and whether it's analytic or image-based); and its **Publications**,
  with authors, citation, date, and links to its DOI, PubMed entry and web page.
- A publication: its details, and the models published with it; click one to select it in the tree,
  or double-click to open it.
- A folder or owner: how many models it holds, and what the folder is for.
- A BioModels Database model: its ID, web page, and whether VCell can import it.

**Open** the Model Explorer from **File > Model Explorer...** or the **Model Explorer** button under
a model window's navigation tree (both select that model in it, if it's saved), or from the **Model
Explorer** button of the Welcome window. Right-click a model in the Welcome window's lists and
choose **Show in Model Explorer** to open it at that model.

**Open a model** by double-clicking it (or one of its versions), by pressing **Enter**, or with the
**Open** button in its details. A BioModels Database model is imported, as a new BioModel, the same
ways (its button is **Import**).

**Right-click** a model for the menu the Welcome window's lists have: Open, Export / Save to File,
Compare With, Copy Name and ID, and, for your own models, Permissions, Archive, Publish and Delete.

**Compare two models**: select both (Cmd-click on macOS, Ctrl-click elsewhere); a bar above the tree
names them, with a **Compare** button, and right-clicking either offers **Compare**. Two BioModels,
two MathModels or two geometries can be compared, including two versions of one model; the older
saved is the baseline. The comparison opens in VCell's **Compare Models** window.

**Compare With > Another Model...** (from a model's right-click menu here or in the Welcome window)
marks that model in the tree as **comparing**, and the bar above the tree asks for the model to
compare it with. Select it and press **Compare**, or double-click it (or press **Enter**);
**Cancel** or **Escape** stops.

**Search**: typing lists only the matching models, opening every folder down to them when there
aren't too many; **Escape** clears it. A model selected from elsewhere that the search or filters
hide clears them, to show it.

Closing the Model Explorer only hides it; it comes back as it was.
