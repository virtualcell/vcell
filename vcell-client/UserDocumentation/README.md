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

## Contents

`toc.md` is a nested list of links. Indent two spaces per level. The first entry is the home
page (`HelpIds.HOME`). A page can appear more than once. A page that is not in the contents
must be linked from a page that is, or the test below fails.

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

`HelpBookTest` (in the Fast group) fails on a broken link, anchor or image, an image name whose
case differs from the file (macOS forgives that, but the client jar does not), a page nothing
links to, a duplicate id, Obsidian-only syntax, or a `HelpIds` constant with no page:

```bash
mvn test -pl vcell-client -Dtest=HelpBookTest
```

To see the result, open the client's help. `tools/debug-bridge/doc-scaffold.py` drafts a page
from a recorded UI session.

## The web copy

`mvn -pl vcell-client -am install -DskipTests -Pweb-help` writes the same pages as static HTML
to `vcell-client/target/webhelp`. The **webhelp-deploy** workflow publishes that to
https://vcell.org/webstart/VCell_Tutorials/VCell_Help/index.html.
