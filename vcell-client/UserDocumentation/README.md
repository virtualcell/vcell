# VCell user help

The help users open from **Help > VCell Help**, the Welcome window's **Learn VCell** button and
the Help buttons in dialogs. It is plain Markdown: edit a page, rebuild the client (or just
`mvn compile -pl vcell-client`), and reopen the help.

```
UserDocumentation/
  toc.md            the contents tree, in order
  pages/<Section>/  one .md file per page
  images/           every image a page shows
  image-sources/    .psd and original captures; not shipped
```

## Pages

- **A page's id is its file name** without `.md` (`pages/Introduction/FieldData.md` is
  `FieldData`). Ids must be unique across all sections. Code opens a page by id, through a
  constant in `org.vcell.documentation.HelpIds`, so rename a page and its constant together.
- **Name files with letters, digits, `_` and `-` only**, with no spaces. This applies to pages
  and images: the name becomes an id in code and part of a web address.
- **The first line is the title**: `# Field Data`.
- Write ordinary [CommonMark](https://commonmark.org/help/): paragraphs, `**bold**`, `*italic*`,
  lists, `## headings`, tables, and links.
- **Link to another page with its relative path**, the way GitHub would follow it:
  `[Geometry](../BioModelApplications/Geometry/GeometryOverview.md)`. To link to a heading, add
  its anchor: `[reaction rules](../Appendix/Definitions.md#reaction-rules)`. An anchor is the
  heading text, lower-cased, with spaces turned into dashes. The heading's own text, as
  Obsidian writes it (`Definitions.md#Reaction%20Rules`), works too.
- **Glossary terms** live on `pages/Appendix/Definitions.md`, one `##` heading each.
- **Images** go in `images/` and are linked relatively: `![](../../images/FieldDataManager.png)`.
  An image inside a sentence stays inline (good for toolbar icons); on its own line it is a
  block. Keep images under about 500 KB. They all ship inside the client.
- Text in `<!-- ... -->` is a note for authors and never shown.

## Contextual help

VCell opens these pages from its windows, so a page with a `HelpIds` constant is one a reader
reaches without looking for it:

- **Help > VCell Help** (or **F1**) opens the page about the window in front. In a model window,
  that is the page about the view open in it: the tree node, and the tab open there, such as
  *Reactions Table* or *Events*. Code says which page explains a panel with
  `HelpContext.setPage`.
- **A small round "?"** opens the page about one part of a window, such as the Object Properties,
  Annotations and Problems tabs under a model's view (the page follows the open tab and the
  selected object). Child windows and dialogs with a page get a "?" in their top-right corner,
  since they have no menu bar.
- **"VCell Help" in an explanation**: VCell's short explanations (a solver's options, the
  diagram controls) end with a button or link to the page that says more.

The model window's mapping lives in `DocumentEditorHelp`. Two Fast tests keep every window linked
to a page, so a new window or view fails them until it has one:

- `DocumentEditorHelpTest`: every model-window view and tree node names a page that exists.
- `HelpCoverageTest`: every window class says its page (`HelpContext.setPage`), and every child
  window says the page of what it shows just before it opens, or says where that is done with a
  `// help:` comment. Windows no page should explain (progress popups, developer tools) are
  listed in the test with the reason.

Where no page exists yet, add one. Pages that start with an **AI-drafted page** note were written
from the source code and await review by the VCell team
([#2226](https://github.com/virtualcell/vcell/issues/2226)); remove the note once a page is checked.

## Contents

`toc.md` is a nested list of links. Indent two spaces per level. The help opens on these
contents (as does its **Home** button), shown as a page with no page selected. A page can appear
more than once. A page that is not in the contents must be linked from a page that is, or the
test below fails.

## Editing in Obsidian

[Obsidian](https://obsidian.md) (free) gives you a graph of how pages link, backlinks, and link
updates when you rename or move a page. Use **Open folder as vault** on this
`UserDocumentation` folder, not a new vault inside it. The vault's settings are committed in
`.obsidian/app.json`: standard Markdown links (not `[[wikilinks]]`), relative paths, links
updated on rename, and pasted images saved to `images/`. Your own layout and plugins stay out of
git.

- **Graph (⌘G / Ctrl+G):** every page hangs off `toc.md`. Type `-file:toc` in the graph's filter
  to see how the pages themselves link.
- **Backlinks:** almost every page links to the glossary, so `Definitions` has dozens. Type in
  the backlinks panel's search box to narrow them.
- **Renaming a page** updates the links to it. It does not change link text, such as the label
  in `toc.md`, and it changes the page's id, so check `HelpIds`.
- **Stick to standard Markdown.** Obsidian also reads its own syntax: `[[wikilinks]]`,
  `![[embeds]]`, `> [!note]` callouts, `==highlights==`, `%%comments%%` and `^block` references.
  VCell shows all of these as literal text, so `HelpBookTest` rejects them. Picking a page or
  heading from the `[[` suggestions is fine: with this vault's settings Obsidian inserts a
  standard link.
- **Pasted images** get names like `Pasted image 2026….png`. Rename them to something
  meaningful, with no spaces, before committing.

## Checking your change

**In VCell, without building anything:** open the help, choose **View > Show Help from a
Source Folder...** in its menu bar and pick this `UserDocumentation` folder. The help then shows your copy, as
VCell will show it, and reloads a second or two after you save a file. A banner names the folder,
and a **Problems** tab beside the contents lists everything below, each with its file and line;
choose one to open the page. The tab counts errors/warnings, as in "Problems (2/36)". With a
page open it lists only that page's problems; **Home** lists them all. Its **Errors** and
**Warnings** boxes hide either kind. **Use Built-in Help** goes back. VCell remembers the folder until you
do.

The same checks run in `HelpBookTest` (in the Fast group), and in CI. They report, as **errors**, a
broken link, anchor or image, an image name whose case differs from the file (macOS forgives that,
but the client jar does not), a page nothing links to, a file name with spaces or other characters,
a link (shortcut) in the folder, raw HTML, a link that is not http, https or mailto, an image too
large to show, Obsidian-only syntax, and a `HelpIds` constant with no page. Errors fail the test,
CI and the web export.

They also report two **warnings**, which fail nothing: an image in `images/` that no page shows,
and an image file over 500 KB. Both still ship inside VCell, so delete or shrink them when you can.
The Problems tab marks errors with a red cross and warnings with a yellow exclamation mark, and
the test lists the warnings in its output:

```bash
mvn test -pl vcell-client -Dtest='HelpBookTest,HelpSecurityTest'
```

`tools/debug-bridge/doc-scaffold.py` drafts a page from a recorded UI session.

## What the help will not show

A help folder can come from anywhere, so the help treats its content as untrusted, the built-in
copy included. It shows only Markdown: **raw HTML is dropped** (comments stay as author notes),
links open only for `https://`, `http://` and `mailto:`, and images come only from plain files in
this folder (PNG, JPEG or GIF, at most 8 MB and 10,000 pixels a side). Files must have plain
names, and links (symlinks or shortcuts) are not followed. A page larger than 256 KB is not read.
Anything else is shown as its text, and listed as a problem.

## The web copy

`mvn -pl vcell-client -am install -DskipTests -Pweb-help` writes the same pages as static HTML
to `vcell-client/target/webhelp`. The **webhelp-deploy** workflow publishes that to
https://vcell.org/webstart/VCell_Tutorials/VCell_Help/index.html.
