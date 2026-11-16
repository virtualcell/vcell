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
- **The first line is the title**: `# Field Data`.
- Write ordinary [CommonMark](https://commonmark.org/help/): paragraphs, `**bold**`, `*italic*`,
  lists, `## headings`, tables, and links.
- **Link to another page with its relative path**, the way GitHub would follow it:
  `[Geometry](../BioModelApplications/Geometry/GeometryOverview.md)`. To link to a heading, add
  its anchor: `[reaction rules](../Appendix/Definitions.md#reaction-rules)`. An anchor is the
  heading text, lower-cased, with spaces turned into dashes.
- **Glossary terms** live on `pages/Appendix/Definitions.md`, one `##` heading each.
- **Images** go in `images/` and are linked relatively: `![](../../images/FieldDataManager.png)`.
  An image inside a sentence stays inline (good for toolbar icons); on its own line it is a
  block. Keep images under about 500 KB. They all ship inside the client.
- Text in `<!-- ... -->` is a note for authors and never shown.

## Contents

`toc.md` is a nested list of links. Indent two spaces per level. The first entry is the home
page (`HelpIds.HOME`). A page can appear more than once. A page that is not in the contents
must be linked from a page that is, or the test below fails.

## Checking your change

`HelpBookTest` (in the Fast group) fails on a broken link, anchor or image, an image name whose
case differs from the file (macOS forgives that, but the client jar does not), a page nothing
links to, a duplicate id, or a `HelpIds` constant with no page:

```bash
mvn test -pl vcell-client -Dtest=HelpBookTest
```

To see the result, open the client's help. `tools/debug-bridge/doc-scaffold.py` drafts a page
from a recorded UI session.

## The web copy

`mvn -pl vcell-client -am install -DskipTests -Pweb-help` writes the same pages as static HTML
to `vcell-client/target/webhelp`. The **webhelp-deploy** workflow publishes that to
https://vcell.org/webstart/VCell_Tutorials/VCell_Help/index.html.
