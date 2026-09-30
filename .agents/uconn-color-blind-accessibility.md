# UConn Color-Blind Accessibility Requirements for VCell

## Executive Summary

The Virtual Cell (VCell) modeling and simulation platform—developed and hosted at the University of Connecticut Health Center (UCHC)—is governed by UConn's institutional accessibility policies, Connecticut state law, and federal disability regulations. On March 4, 2026, UConn updated and reaffirmed its **Digital Accessibility Policy** (effective March 10, 2026), mandating that all information and communication technology (ICT) across all UConn campuses meet the **Web Content Accessibility Guidelines (WCAG) 2.1, Level AA** technical standard, in compliance with Title II of the Americans with Disabilities Act (ADA) and Sections 504 and 508 of the Rehabilitation Act of 1973. UConn sets an institutional compliance deadline of **April 26, 2027** for all digital offerings, requiring all new content and digital solutions created on or after **April 24, 2024** to be accessible at launch.

For users with color vision deficiencies (CVD)—affecting approximately 8% of males and 0.5% of females, primarily manifesting as red-green color blindness (deuteranopia and protanopia)—the core mandate across UConn and WCAG 2.1 AA is twofold:
1. **No reliance on color alone:** Color must never be the sole visual mechanism used to communicate information, indicate state, prompt an action, or distinguish visual components. Every color cue must be accompanied by a redundant non-color discriminator (such as text, icons, line styles, patterns, or glyph shapes).
2. **Strict contrast thresholds:** Text and essential visual elements must achieve quantified luminance contrast ratios against their backgrounds: **4.5:1** for regular text (<18 pt or <14 pt bold), **3:1** for large text (≥18 pt or ≥14 pt bold), and **3:1** for essential user interface components (borders, toggles, focus indicators) and graphical objects (chart series, plot lines, diagram nodes).

An extensive inspection of the VCell repository across the Java desktop Swing client (`vcell-client`), core library (`vcell-core`, `vcell-util`), Angular web application (`webapp-ng`), and web-based visualization tools (`webapp-viewer`) identified widespread accessibility risks. Key findings include:
- **Scientific Visualizations:** Both the desktop viewer (`DisplayAdapterService.java`) and the web viewer (`viewer.js`) default to traditional "blue-to-red" rainbow/jet colormaps that sweep through green, yellow, and red. These colormaps lack monotonic luminance and are notoriously uninterpretable for deuteranopic and protanopic individuals.
- **2D Plotting and Legends:** 2D line plots (`Plot2DPanel.java`, `PlotRenderers.java`) and their legends (`PlotPane.java`, `viewer.js`) render curves exclusively as solid strokes distinguished only by line color. Data point markers (when enabled) are restricted to circles of uniform shape.
- **Color Palettes:** Although `ColorUtil.java` defines an accessible `COLORBLIND20` palette, it remains completely unused in production code. Instead, components hard-code `TABLEAU20` or `DARK20`, which contain confusing red-green color pairs.
- **Form Validation and Error States:** Input errors across multiple panels (`OutputOptionsPanel.java`, `MeshSpecificationPanel.java`, `TableCellEditorAutoCompletion.java`) rely solely on setting a red border (`GuiConstants.ProblematicTextFieldBorder`) or red text (`ConstraintTableCellRenderer.java`) without inline text descriptions or error icons.
- **Web UI Contrast Failures:** Several status badges in `webapp-ng` (`publication-edit.component.css`) and interactive controls in `webapp-viewer` exhibit contrast ratios as low as 1.98:1 and 2.79:1, well below the required 4.5:1 threshold.
- **Desktop Look & Feel:** Hardcoded `setBackground(Color.white)` calls across dozens of Swing panels without corresponding foreground overrides create severe contrast breakages under OS High Contrast or Dark Mode themes.

This document serves as an authoritative reference detailing UConn's requirements, standards-derived criteria, best practices, repository evidence, and a prioritized remediation roadmap.

---

## Sources and Standards

The analysis in this reference is derived strictly from official university publications, governing legal frameworks, and authoritative international standards:

1. **UConn Guidelines & Standards (ITS IT Accessibility):**  
   https://accessibility.its.uconn.edu/guidelines-standards/  
   Establishes UConn's top accessibility standards, adopting WCAG 2.1 Level AA and outlining baseline contrast requirements and dual-coding requirements (labels/icons with color).

2. **UConn Digital Accessibility Policy (Policy Owner: Information Technology Services; Approved: March 4, 2026; Effective: March 10, 2026):**  
   https://policy.uconn.edu/2019/08/02/digital-accessibility-policy/  
   Extends across all UConn campuses (including UConn Health / UCHC) to the procurement, development, implementation, and maintenance of all digital information, communication, content, and technology.

3. **UConn Policy Procedures (ITS IT Accessibility):**  
   https://accessibility.its.uconn.edu/policy-procedures/  
   Details standards adoption (ADA Title II, Section 504, Section 508, WCAG 2.1 Level AA, CT Universal Website Accessibility Policy), compliance deadlines (April 26, 2027 institutional deadline; April 24, 2024 launch threshold for new content), procurement requirements (US Access Board 508 Standards for software and desktop applications), and Equally Effective Access Accommodation Plans (EEAAP).

4. **UConn IT Accessibility Self-Paced Learning – Colors:**  
   https://accessibility.its.uconn.edu/self-paced-learning/colors/  
   Provides explicit institutional rules on color contrast (4.5:1 and 3:1), avoidance of red/green and red/black pairings, prohibition of color alone to convey meaning, text-on-image contrast, and required testing tools.

5. **UConn Content Guides – Essential Practices:**  
   https://accessibility.its.uconn.edu/content-guides/  
   Provides explicit developer and content-creator guidance on color contrast, descriptive hyperlinks (underlines + color), form feedback (combination of color, icons, and text), and visual distinction of form inputs.

6. **UConn Brand Standards – Accessible Color Combinations:**  
   https://brand.uconn.edu/visual-identity/uconn-accessible-color-combinations/  
   Authoritative color matrix published by UConn University Communications and UConn Health detailing compliant vs. prohibited (greyed-out) foreground/background color combinations.

7. **State of Connecticut Universal Website Accessibility Policy:**  
   https://portal.ct.gov/-/media/sitecore-center/accessibility/it-accessibility-policy-final-for-release-07142025.pdf  
   State government accessibility policy adopting WCAG 2.1 Level AA and Section 508 across Connecticut public institutions.

8. **W3C Web Content Accessibility Guidelines (WCAG) 2.1 (W3C Recommendation 05 June 2018):**  
   https://www.w3.org/TR/WCAG21/  
   Governing technical standard for digital accessibility:
   - Success Criterion 1.4.1: Use of Color (Level A)
   - Success Criterion 1.4.3: Contrast (Minimum) (Level AA)
   - Success Criterion 1.4.11: Non-text Contrast (Level AA)
   - Success Criterion 2.4.7: Focus Visible (Level AA)
   - Success Criterion 3.3.1: Error Identification (Level A)
   - Success Criterion 3.3.2: Labels or Instructions (Level A)

9. **U.S. Access Board Information and Communication Technology (ICT) Standards (Section 508 / 36 CFR Part 1194):**  
   https://www.access-board.gov/ict/  
   Specifically Chapter 5 (Software Applications and Operating Systems) and Section 502 (Interoperability with Assistive Technology), applicable to desktop software such as the VCell Swing desktop client.

---

## Requirements

To ensure clear engineering boundaries, requirements are strictly segregated into:
1. **Explicitly Stated by UConn** (institutional policy and training mandates)
2. **Derived from Standards UConn Explicitly Adopts** (WCAG 2.1 AA and Section 508 legal requirements)
3. **Best-Practice Recommendations** (scientific visualization and ergonomics guidance)

```
+-----------------------------------------------------------------------------------------+
|                               Accessibility Governance                                  |
|                                                                                         |
|  [Tier 1: Explicit UConn Policies]       [Tier 2: Adopted Standards]                    |
|  - Digital Accessibility Policy          - WCAG 2.1 Level AA (ADA Title II / CT State)  |
|  - No red/green or red/black combos      - SC 1.4.1 Use of Color (Level A)              |
|  - Required non-color icons/labels       - SC 1.4.3 Text Contrast (4.5:1 / 3:1) (AA)    |
|  - Mandatory footer accessibility link   - SC 1.4.11 Non-Text Contrast (3:1) (AA)       |
|  - UConn Health Brand Color Matrix       - Section 508 Chapter 5 (Desktop Software)     |
|                                                                                         |
|                    [Tier 3: Scientific Best Practices]                                  |
|                    - CVD-Safe Continuous Colormaps (Viridis/Cividis)                    |
|                    - Redundant Line Styles (Dash/Dot/Thickness)                         |
|                    - Redundant Point Markers (Circle/Square/Delta)                      |
|                    - CVD Palettes (Okabe-Ito, ColorBrewer)                              |
+-----------------------------------------------------------------------------------------+
```

### 1. Explicitly Stated by UConn

#### Requirement UCONN-01: Prohibition of Color Alone to Convey Meaning
- **Rule:** Avoid using color alone to convey information; add labels, text, or icons in addition to color to communicate meaning.
- **Source:** UConn IT Accessibility Guidelines & Standards; UConn Colors Training (https://accessibility.its.uconn.edu/self-paced-learning/colors/).
- **Required vs. Recommended:** **Strict Requirement** across all UConn digital content and applications.
- **Practical Interpretation:** Any UI element, status indicator, tree node, plot curve, or table cell that changes color to signify state (e.g., error, active, completed, inconsistent, selected) must include a concomitant shape, glyph, text descriptor, or icon.
- **Implication for VCell:** Status dots (e.g. `StatusIcon.java`), table row highlights, and simulation console outputs must not rely solely on color to differentiate between success, failure, stopped, or warning states.

#### Requirement UCONN-02: Avoidance of Red/Green and Red/Black Combinations
- **Rule:** "Avoid using Red and Black or Red and Green color combinations. Someone with red/green color blindness will struggle to differentiate between red and green."
- **Source:** UConn IT Accessibility Colors Module (https://accessibility.its.uconn.edu/self-paced-learning/colors/).
- **Required vs. Recommended:** **Strict Institutional Requirement / Rule of Practice**.
- **Practical Interpretation:** Never place red text on green or black backgrounds, green text on red backgrounds, or use red and green as the primary opposing states (e.g., pass/fail, positive/negative, start/stop) without secondary discriminators. Red on dark/black backgrounds also suffers from protanopic luminance loss (red appears black/dim).
- **Implication for VCell:** Scientific colormaps (blue-to-red via green/yellow), SpringSaLaD particle colors (pairing RED, GREEN, LIME, MAROON), and comparison charts must eliminate direct red/green oppositions.

#### Requirement UCONN-03: Text Contrast Minimum Thresholds
- **Rule:** Maintain a minimum contrast ratio of **4.5:1** between text and background for font sizes smaller than 18 pt (or 14 pt bold), and **3:1** for font sizes 18 pt and larger (or 14 pt bold).
- **Source:** UConn Guidelines & Standards (Rule 01); UConn Content Guides (https://accessibility.its.uconn.edu/content-guides/).
- **Required vs. Recommended:** **Strict Requirement**.
- **Practical Interpretation:** All labels, button text, table contents, axis annotations, and badge labels must meet mathematical contrast thresholds under standard relative luminance calculation:
  $$\text{Contrast Ratio} = \frac{L_1 + 0.05}{L_2 + 0.05}$$
  where $L_1$ is the relative luminance of the lighter color and $L_2$ is the relative luminance of the darker color ($0.0 \le L \le 1.0$).
- **Implication for VCell:** Pure red (`#FF0000`, $L \approx 0.2126$) on pure white (`#FFFFFF`, $L = 1.0$) yields a contrast ratio of $1.05 / 0.2626 \approx \mathbf{4.0:1}$, which fails the 4.5:1 normal text threshold. Status badges in `webapp-ng` using white text on orange (`#ff9800`) or light green (`#4caf50`) yield ratios of ~1.98:1 to 2.79:1 and are non-compliant.

#### Requirement UCONN-04: Descriptive Hyperlinks and Multi-Cue Differentiation
- **Rule:** Links ideally have their own distinct color from surrounding text accompanied by an underline.
- **Source:** UConn Content Guides (Section 5: Descriptive Hyperlinks).
- **Required vs. Recommended:** **Strict Requirement** (when within body copy; standard practice across web pages).
- **Practical Interpretation:** Hyperlinks embedded in blocks of text cannot be distinguished solely by font color. They must either feature an underline or achieve a 3:1 contrast ratio against the surrounding body text AND provide a non-color visual change on hover and keyboard focus.
- **Implication for VCell:** Web links in `webapp-ng` and web links panels in `vcell-client` (e.g. `BioModelPropertiesPanel.java`) must render with underlines or distinct non-color cues.

#### Requirement UCONN-05: Mandatory Accessibility Portal Link
- **Rule:** All University websites and web applications must provide a link to the central UConn Accessibility portal (`https://accessibility.uconn.edu/`), typically in the persistent footer replicated across pages.
- **Source:** UConn Policy Procedures (University Websites section).
- **Required vs. Recommended:** **Strict Requirement**.
- **Practical Interpretation:** The web application footer must include an explicit accessibility link pointing to institutional compliance and accommodation resources.
- **Implication for VCell:** `webapp-ng/src/app/components/footer/footer.component.html` currently lacks this link and must be updated.

#### Requirement UCONN-06: Conformity with UConn Health Accessible Color Matrix
- **Rule:** Use only approved foreground/background pairings specified in the UConn Health brand matrix; do not use greyed-out pairings that fail 4.5:1 contrast.
- **Source:** UConn Brand Standards – Visual Identity: Accessible Color Combinations (https://brand.uconn.edu/visual-identity/uconn-accessible-color-combinations/).
- **Required vs. Recommended:** **Institutional Standard**.
- **Practical Interpretation:** Standard UConn Health palette colors (Navy Blue `#000E2F`, Blue `#004369`, Dark Grey `#7E868C`, Red `#BE2D2D`, etc.) must not be combined in ways that fail WCAG AA. Specifically, Red `#BE2D2D` text fails on both white and black backgrounds for normal body text; Dark Grey `#7E868C` fails on black and white.

---

### 2. Derived from Standards UConn Explicitly Adopts (WCAG 2.1 AA & Section 508)

#### Requirement STD-01: WCAG 1.4.1 Use of Color (Level A)
- **Rule:** "Color is not used as the only visual means of conveying information, indicating an action, prompting a response, or distinguishing a visual element."
- **Source:** W3C WCAG 2.1 (https://www.w3.org/TR/WCAG21/#use-of-color); adopted by UConn Digital Accessibility Policy.
- **Required vs. Recommended:** **Mandatory Legal Standard**.
- **Practical Interpretation:** Applies comprehensively to software UI, forms, tables, graphs, and simulation outputs. When an element communicates state, selection, error, or data identity:
  - If a plot curve is colored, its line pattern (solid, dashed, dotted) or point markers (squares, triangles, circles) must also differ.
  - If a form field is invalid, a text error message or exclamation icon must accompany the red border.
  - If a table cell indicates inconsistency or failure, text badges or icons must accompany the cell color.
- **Implication for VCell:** All 2D plot panels, simulation status tables, and molecular property editors in VCell are subject to this criterion.

#### Requirement STD-02: WCAG 1.4.3 Contrast (Minimum) (Level AA)
- **Rule:** The visual presentation of text and images of text has a contrast ratio of at least:
  - **4.5:1** for normal text (<18 pt regular font or <14 pt bold font).
  - **3:1** for large text (≥18 pt regular font or ≥14 pt bold font).
  - *Exceptions:* Inactive (disabled) UI components, purely decorative text, logos/brand names.
- **Source:** W3C WCAG 2.1 (https://www.w3.org/TR/WCAG21/#contrast-minimum).
- **Required vs. Recommended:** **Mandatory Legal Standard**.
- **Practical Interpretation:** Calculated against the actual rendered background behind the text. If a panel has a dynamic background or gradient, contrast must be maintained across the entire text bounding area.
- **Implication for VCell:** Applied to all text in Swing components, table renderers, console output, web application cards, and visualization overlays.

#### Requirement STD-03: WCAG 1.4.11 Non-text Contrast (Level AA)
- **Rule:** The visual presentation of the following have a contrast ratio of at least **3:1** against adjacent color(s):
  1. **User Interface Components:** Visual information required to identify user interface components and states (button boundaries, input field borders, radio button/checkbox states, focus indicators, selection highlights).
  2. **Graphical Objects:** Parts of graphics required to understand the content, such as chart lines, bar segments, scatter plot points, pie slices, and diagram connectors.
  - *Exception:* When a particular presentation of graphics is essential to the information being conveyed (e.g. real-world biological microscopy imagery where pixel values represent physical measurements).
- **Source:** W3C WCAG 2.1 (https://www.w3.org/TR/WCAG21/#non-text-contrast); added in WCAG 2.1.
- **Required vs. Recommended:** **Mandatory Legal Standard**.
- **Practical Interpretation:**
  - Form input borders must contrast at least 3:1 against the surrounding container background so low-vision users can identify input bounds.
  - Adjacent segments in charts/plots must contrast at least 3:1 against each other OR be separated by a 3:1 contrasting boundary line.
  - Focus rings must have at least 3:1 contrast against both the focused element and the background.
- **Implication for VCell:** 2D plot traces in `Plot2DPanel.java` and `webapp-viewer` must maintain 3:1 contrast against the plot background canvas (white or dark). Discrete subvolume regions in geometry viewers must have high-contrast boundary strokes.

#### Requirement STD-04: WCAG 2.4.7 Focus Visible (Level AA)
- **Rule:** Any keyboard-operable user interface must have a mode of operation where the keyboard focus indicator is visible.
- **Source:** W3C WCAG 2.1 (https://www.w3.org/TR/WCAG21/#focus-visible).
- **Required vs. Recommended:** **Mandatory Legal Standard**.
- **Practical Interpretation:** As users tab through the interface, the focused element must show a distinct visual ring or highlight. In conjunction with SC 1.4.11, this focus indicator must have at least 3:1 contrast against adjacent backgrounds.
- **Implication for VCell:** Custom canvas elements and custom Swing components (`AbstractPlotPanel`, `SpringSaladViewerCanvas`, `ScrollTable`) must not suppress focus rings or rely on low-contrast outline colors.

#### Requirement STD-05: WCAG 3.3.1 Error Identification (Level A) & 3.3.2 Labels or Instructions (Level A)
- **Rule:** If an input error is automatically detected, the item that is in error is identified and the error is described to the user in text. Labels or instructions must be provided when content requires user input.
- **Source:** W3C WCAG 2.1 (https://www.w3.org/TR/WCAG21/#error-identification, https://www.w3.org/TR/WCAG21/#labels-or-instructions).
- **Required vs. Recommended:** **Mandatory Legal Standard**.
- **Practical Interpretation:** When a user enters an invalid parameter (e.g., negative diffusion coefficient, invalid time step, syntax error in math expression), the system cannot merely paint the text box border red. It must describe the specific error in plain text adjacent to the control or in an accessible notification banner.
- **Implication for VCell:** Replaces silent red border highlighting (`ProblematicTextFieldBorder`) with explicit, accessible error messages.

#### Requirement STD-06: Section 508 Chapter 5 (Software) & Section 502 (Interoperability with Assistive Technology)
- **Rule:** Desktop applications must not disrupt or disable operating system accessibility features (such as High Contrast Mode, inverted colors, or system font scaling) and must expose component roles, states, and boundaries via standard accessibility APIs (Java Accessibility API / JAA).
- **Source:** 36 CFR Part 1194, Section 508 / US Access Board Guide 508 Standards for Software Applications; cited in UConn Policy Procedures.
- **Required vs. Recommended:** **Mandatory Legal Standard** for software procured or developed by UConn.
- **Practical Interpretation:** Custom Swing components must not hardcode static colors that clash with OS high contrast themes (e.g. painting hardcoded white backgrounds while the OS sets light text).
- **Implication for VCell:** `VCellLookAndFeel.java` and individual panel constructors must respect system UI color properties (`UIManager.getColor(...)`) rather than hardcoding static `java.awt.Color` constants.

---

### 3. Best-Practice Recommendations Rather Than Strict Requirements

#### Recommendation REC-01: Perceptually Uniform, CVD-Safe Scientific Colormaps
- **Guidance:** Replace non-monotonic rainbow/jet colormaps with perceptually uniform colormaps designed specifically for accessibility across all forms of color vision deficiency (deuteranopia, protanopia, tritanopia, and monochromacy).
- **Recommended Colormaps:**
  - **Sequential / Continuous:** **Viridis**, **Cividis** (specifically mathematically optimized for deuteranopia/protanopia), **Batlow**, or **Plasma**.
  - **Diverging:** Blue-White-Orange, Purple-White-Green, or Paul Tol's BuRd scheme (instead of Red-Green).
  - **Categorical:** **Okabe-Ito** (8-color colorblind-safe palette) or Paul Tol's Bright/Muted palettes.
- **Rationale:** Rainbow colormaps distort scientific data interpretation by introducing false gradient artifacts (banding) and rendering high and low data points indistinguishable for CVD users.
- **Status:** **Best-Practice Scientific Recommendation** (exceeds minimal WCAG text contrast rules to solve domain-specific data visualization accessibility).

#### Recommendation REC-02: Redundant Visual Coding for Multi-Series Plots
- **Guidance:** In multi-series 2D plots and graphs, vary both line stroke dash patterns and point marker symbols in addition to color:
  - Series 1: Solid stroke (`-`), Circle marker (`●`)
  - Series 2: Dashed stroke (`--`), Square marker (`■`)
  - Series 3: Dotted stroke (`···`), Up-Triangle marker (`▲`)
  - Series 4: Dash-dot stroke (`-·-`), Diamond marker (`◆`)
  - Series 5: Long-dash stroke (`—`), Cross marker (`✚`)
- **Rationale:** Allows complete data disambiguation in grayscale printouts, photocopies, or severe color blindness where hue perception is zero.
- **Status:** **Best Practice**.

#### Recommendation REC-03: Direct Curve Labeling
- **Guidance:** Where space permits, place text labels directly adjacent to curves or at the terminal end of series lines rather than relying exclusively on a detached legend box.
- **Rationale:** Minimizes cognitive load and eliminates the need to cross-reference ambiguous swatches.
- **Status:** **Best Practice**.

#### Recommendation REC-04: User-Selectable Accessibility Theme Settings
- **Guidance:** Provide an explicit application-level preference setting in VCell allowing users to select their visualization mode:
  - "Default Scientific Palette"
  - "Colorblind-Safe Palette (Cividis / Okabe-Ito)"
  - "High-Contrast Monochrome"
- **Rationale:** Empowers users with specific visual impairments to select the optimal perceptual profile without degrading custom workflows for other users.
- **Status:** **Best Practice**.

---

## VCell Repository Findings

Inspection of `vcell-client`, `vcell-core`, `vcell-util`, `webapp-ng`, and `webapp-viewer` revealed significant accessibility risks across multiple functional domains.

```
+-------------------------------------------------------------------------------------------------+
|                                 VCell Codebase Audit Overview                                   |
|                                                                                                 |
|   Module          Component/File                      Issue Summary                  Status     |
|  -------------   ---------------------------------   -----------------------------  ----------  |
|  vcell-core      DisplayAdapterService.java          Rainbow/Jet & HSB colormaps    Non-Compl   |
|  vcell-client    Plot2DPanel.java                    Uniform solid stroke lines     Non-Compl   |
|  vcell-client    PlotPane.java (LineIcon)            Legend swatches color-only     Non-Compl   |
|  vcell-util      ColorUtil.java (COLORBLIND20)       Palette exists but unused      Needs Verif |
|  vcell-client    MoleculeVisualizationPanel.java     Hardcoded TABLEAU20 palette    Non-Compl   |
|  vcell-core      Colors.java (SpringSaLaD)           Opposing Red/Green/Lime pairs  Non-Compl   |
|  vcell-client    SpringSaladViewerCanvas.java        3D spheres color-only          Non-Compl   |
|  vcell-core      GuiConstants.java                   ProblematicTextFieldBorder     Non-Compl   |
|  vcell-client    OutputOptionsPanel.java             Silent red-border validation   Non-Compl   |
|  vcell-client    ConstraintTableCellRenderer.java    Red text on white for errors   Non-Compl   |
|  vcell-client    StatusIcon.java                     Identical geometry for states  Non-Compl   |
|  vcell-client    SimulationConsolePanel.java         Color.RED for Error & Warning  Non-Compl   |
|  webapp-ng       publication-edit.component.css      Badges fail 4.5:1 text ratio   Non-Compl   |
|  webapp-ng       footer.component.html               Missing UConn A11y link        Non-Compl   |
|  webapp-viewer   viewer.js (vtkLookupTable)          Rainbow colormap in WebGL      Non-Compl   |
|  webapp-viewer   index.html (CSS styles)             #2a7 & #d70 contrast failures  Non-Compl   |
|  vcell-client    VCellLookAndFeel.java / Panels      Hardcoded Color.white bgs      Non-Compl   |
+-------------------------------------------------------------------------------------------------+
```

### Finding F-01: Spatial Simulation Colormap (Rainbow / Jet)
- **File Path:** `vcell-core/src/main/java/cbit/image/DisplayAdapterService.java`
- **Component / Method:** `DisplayAdapterService.createBlueRedColorModel0(boolean bSpecial)` (lines 226–261)
- **Implementation Detail:** Generates a 256-entry colormap by interpolating from dark blue $\to$ blue $\to$ cyan $\to$ green $\to$ yellow $\to$ red across 1,148 intermediate steps. This model is registered as `DisplayAdapterService.BLUERED` and serves as the primary colormap for PDE simulation results, spatial slice viewers, and surface viewers throughout the desktop client.
- **Relevant Requirement:** UCONN-01 (No color alone), UCONN-02 (Avoid red/green combinations), STD-01 (WCAG 1.4.1), REC-01 (CVD-safe colormaps).
- **Assessment:** **Potentially Non-Compliant / High CVD Accessibility Barrier**.
- **Analysis:** Despite the name `BlueRed`, the colormap contains a full rainbow progression with green and yellow in the middle. Users with deuteranopia or protanopia cannot distinguish mid-range green/cyan values from high-range red/yellow values. Furthermore, the non-monotonic luminance profile creates false visual edges in smooth concentration gradients.
- **Recommended Remediation:**
  1. Add accessible colormaps (**Viridis**, **Cividis**, and a high-contrast grayscale) to `DisplayAdapterService`.
  2. Provide a UI selector in `DisplayAdapterServicePanel` enabling users to toggle between "Classic (Rainbow)", "Perceptually Uniform (Viridis)", and "Colorblind-Optimized (Cividis)".
  3. Default new installations or provide an explicit user preference for CVD-safe rendering.

---

### Finding F-02: Discrete Geometry SubVolume & CSG Colormaps
- **File Path:** `vcell-core/src/main/java/cbit/image/DisplayAdapterService.java`
- **Component / Method:** `DisplayAdapterService.createContrastColorModel()` (lines 290–299)
- **Implementation Detail:** Synthesizes a discrete 256-color palette by cycling hue in increments of tenths (`hue = (i % 10) / 10.0`), alternating saturation between 1.0 and 0.5 (`1.0 - (i % 2) * 0.5`), and alternating brightness (`1.0 - (i % 3) * 0.25`). Used by:
  - `cbit.vcell.geometry.gui.CSGObjectTreeCellRenderer` (lines 59–70)
  - `cbit.vcell.geometry.gui.GeometrySubVolumeTableCellRenderer` (lines 28–46)
  - `cbit.vcell.mapping.gui.StructureMappingTableModel` (lines 433–465)
  - `cbit.vcell.geometry.gui.GeometryViewer` (lines 607–611)
- **Relevant Requirement:** UCONN-01 (No color alone), STD-01 (WCAG 1.4.1), STD-03 (WCAG 1.4.11 Non-text Contrast).
- **Assessment:** **Potentially Non-Compliant**.
- **Analysis:** Cycling hue in tenths generates adjacent colors at hues 0.0 (red), 0.1 (orange), 0.2 (yellow), 0.3 (yellow-green), and 0.4 (green). Under protanopia and deuteranopia, these hues collapse to indistinguishable olive/tan shades. When subvolumes are displayed side-by-side in 2D/3D geometry views without distinctive outlines or texture patterns, users cannot reliably differentiate biological compartments.
- **Recommended Remediation:**
  1. Replace the procedural HSB formula for the primary 8–10 handles with the **Okabe-Ito** palette.
  2. In tree and table renderers (`GeometrySubVolumeTableCellRenderer`), display subvolume index badges or geometric hatch patterns alongside the color swatch.
  3. In geometry segmentations, ensure boundary contour lines with 3:1 contrast demarcate adjacent subvolumes.

---

### Finding F-03: 2D Plot Curve Rendering and Legends
- **File Path:** `vcell-client/src/main/java/cbit/plot/gui/Plot2DPanel.java` & `PlotPane.java`
- **Component / Method:**
  - `Plot2DPanel.drawLinePlot(PlotData, int, Graphics2D, int)` (lines 1342–1387)
  - `Plot2DPanel.getPlotColor(int)` (lines 1788–1795)
  - `PlotPane.LineIcon` (lines 55–68)
- **Implementation Detail:**
  - Every continuous curve is drawn using an identical solid stroke: `g.setStroke(lineBS_15);` (where `lineBS_15 = new BasicStroke(1.5f)`).
  - Data point nodes (when enabled) are drawn solely as uniform circles: `circle = new Ellipse2D.Double(...)`.
  - Curve colors are assigned dynamically via `autoContrastColors[index]`.
  - The legend (`PlotPane.java`) renders each series with `LineIcon`, drawing a 50 px solid horizontal bar in `lineColor` next to the variable name.
- **Relevant Requirement:** UCONN-01 (No color alone), STD-01 (WCAG 1.4.1 Use of Color), STD-03 (WCAG 1.4.11 Graphical Objects).
- **Assessment:** **Non-Compliant**.
- **Analysis:** Color is the *sole* visual indicator differentiating plot curves. A color-blind user who cannot distinguish two series lines (e.g., green vs. orange or red vs. brown) has no secondary visual cue (stroke dash, marker shape, line thickness, or direct label) to associate a curve in the plot with its entry in the legend.
- **Recommended Remediation:**
  1. Introduce an array of predefined `Stroke` patterns (e.g. solid, dashed `4 4`, dotted `2 2`, dash-dot `6 2 2 2`) mapped cyclically to series indices.
  2. Vary point marker glyphs (circle, square, delta, diamond, plus).
  3. Update `LineIcon` in `PlotPane.java` to draw the corresponding stroke pattern and point marker rather than a plain solid bar.

---

### Finding F-04: Specialized Plot Renderers (Avg, Band, Bubble, Bar)
- **File Path:** `vcell-client/src/main/java/cbit/plot/gui/PlotRenderers.java`
- **Component / Method:**
  - `PlotRenderers.AvgRenderer.draw(...)` (lines 59–122)
  - `PlotRenderers.BandRenderer.draw(...)` (lines 170–215)
  - `PlotRenderers.BubbleRenderer.draw(...)` (lines 380–435)
- **Implementation Detail:** Used in `MoleculePlotPanel` and `ClusterPlotPanel`. `AvgRenderer` renders polylines using a single solid stroke `new BasicStroke(CURVE_STROKE, ...)` and filled circles for nodes. `BandRenderer` renders error/confidence intervals using semi-transparent polygon fills. `BubbleRenderer` draws filled circles whose radii scale with values.
- **Relevant Requirement:** UCONN-01 (No color alone), STD-01 (WCAG 1.4.1), STD-03 (WCAG 1.4.11).
- **Assessment:** **Potentially Non-Compliant**.
- **Analysis:** Multiple average curves or cluster bands overlapping in the same coordinate space rely purely on hue and fill opacity for differentiation. Dimming non-hovered series (lines 80–84: `DIMMED_LINE_ALPHA`) provides interactive feedback on mouseover, but static rendering and print exports lack non-color differentiation.
- **Recommended Remediation:** Add configurable stroke patterns to `AvgRenderer` and optional cross-hatch or stippled fills to `BandRenderer`.

---

### Finding F-05: Color Palettes in ColorUtil and Production Hardcoding
- **File Path:** `vcell-util/src/main/java/org/vcell/util/ColorUtil.java` & `vcell-client/src/main/java/cbit/vcell/solver/ode/gui/MoleculeVisualizationPanel.java`
- **Component / Method:**
  - `ColorUtil.COLORBLIND20` (lines 150–171)
  - `MoleculeVisualizationPanel.initializeGlobalPalette()` (lines 423–436)
  - `ClusterVisualizationPanel.initializeGlobalPalette()` (lines 206–219)
- **Implementation Detail:** `ColorUtil.java` defines an explicit 20-color colorblind-safe palette (`COLORBLIND20`) alongside `TABLEAU20`, `LIGHT20`, `DARK20`, and `OTHERS20`. However, repository-wide search shows `COLORBLIND20` is **completely unreferenced** in production UI code. Instead:
  - `MoleculeVisualizationPanel` explicitly executes: `globalPalette.addAll(Arrays.asList(ColorUtil.TABLEAU20));`
  - `ClusterVisualizationPanel` explicitly executes: `globalPalette.addAll(Arrays.asList(ColorUtil.DARK20));`
- **Relevant Requirement:** UCONN-02 (Avoid red/green combinations), REC-01 (CVD-Safe Palettes).
- **Assessment:** **Potentially Non-Compliant / Unused Accessible Capability**.
- **Analysis:** `TABLEAU20` pairs saturated red (`Color(214, 39, 40)`) with green (`Color(44, 160, 44)`), light red with light green, and brown with gray. `DARK20` similarly pairs dark red with dark green. The existing `COLORBLIND20` palette was developed specifically to solve this issue but was never hooked into the visualization panels.
- **Recommended Remediation:**
  1. Switch `MoleculeVisualizationPanel` and `ClusterVisualizationPanel` to initialize from `ColorUtil.COLORBLIND20`.
  2. Audit `COLORBLIND20` against Okabe-Ito and Paul Tol standards to ensure all 20 entries maintain at least 3:1 non-text contrast against white and black canvases.

---

### Finding F-06: SpringSaLaD Particle Palette and 3D Canvas
- **File Path:** `vcell-core/src/main/java/org/vcell/util/springsalad/Colors.java` & `vcell-client/src/main/java/cbit/vcell/solver/ode/gui/SpringSaladViewerCanvas.java`
- **Component / Method:**
  - `Colors.COLORARRAY` / `Colors.COLORNAMES` (lines 19–95)
  - `SpringSaladViewerCanvas.makeBaseSprite()` & `colorForName(String)` (lines 535–566)
- **Implementation Detail:** Defines 28 named colors for particle simulation sites: `RED`, `BLUE`, `LIME` (Color.GREEN), `ORANGE`, `CYAN`, `MAGENTA`, `PINK`, `YELLOW`, `GREEN` (`0, 128, 0`), `MAROON` (`128, 0, 0`), `OLIVE` (`128, 128, 0`), `CRIMSON` (`220, 20, 60`), `DARKGREEN` (`0, 100, 0`). In `SpringSaladViewerCanvas.java`, particles are rendered as 3D shaded spheres against a solid black background (`#000000`) based strictly on their assigned color name.
- **Relevant Requirement:** UCONN-01 (No color alone), UCONN-02 (Avoid red/green combinations), STD-01 (WCAG 1.4.1).
- **Assessment:** **Non-Compliant**.
- **Analysis:** Red, green, lime, maroon, olive, crimson, and dark green are all concurrent choices in the simulation palette. In 3D particle trajectories, identical spheres of green and red floating in space cannot be distinguished by deuteranopic or protanopic users. No particle numbers, surface textures, glyphs, or geometric shapes differentiate the types.
- **Recommended Remediation:**
  1. Add geometric shape options for particles (spheres, cubes, tetrahedra, octahedra, tori).
  2. Introduce surface pattern textures (stripes, dots, checkerboards) on sphere sprites.
  3. Provide an interactive tooltip or HUD readout identifying the molecular type and site name under the cursor.

---

### Finding F-07: Form Input Validation Error Highlighting
- **File Path:**
  - `vcell-core/src/main/java/cbit/vcell/client/constants/GuiConstants.java` (line 125)
  - `vcell-client/src/main/java/cbit/vcell/solver/ode/gui/OutputOptionsPanel.java` (lines 573–582, 858–863)
  - `vcell-client/src/main/java/cbit/vcell/math/gui/MeshSpecificationPanel.java` (lines 428–433)
  - `vcell-client/src/main/java/cbit/gui/TableCellEditorAutoCompletion.java` (lines 118–121)
- **Component / Method:** `GuiConstants.ProblematicTextFieldBorder`
- **Implementation Detail:** `GuiConstants.ProblematicTextFieldBorder` is defined as `BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.red), BorderFactory.createEmptyBorder(2, 2, 2, 2))`. When input validation fails:
  ```java
  // OutputOptionsPanel.java line 574
  } else {
      getOutputTimesTextField().setBorder(GuiConstants.ProblematicTextFieldBorder);
      javax.swing.SwingUtilities.invokeLater(() -> getOutputTimesTextField().requestFocus());
  }
  ```
  `TableCellEditorAutoCompletion.java` similarly executes `((JComponent)getComponent()).setBorder(new LineBorder(Color.red));`.
- **Relevant Requirement:** UCONN-01 (No color alone), STD-01 (WCAG 1.4.1), STD-05 (WCAG 3.3.1 Error Identification).
- **Assessment:** **Non-Compliant**.
- **Analysis:** Invalid fields are marked exclusively by changing the border color to red. No companion error text is rendered on the panel, no warning icon (e.g. `!`) is injected, and no assistive technology alert is triggered. A color-blind user who cannot distinguish the red border from a standard dark gray or black border receives zero feedback as to why the input was rejected or that an error exists.
- **Recommended Remediation:**
  1. Implement an error banner or inline message label adjacent to the input field displaying explicit textual feedback (e.g., "Error: Ending time must exceed starting time").
  2. Include an error icon (e.g., warning triangle) inside or beside the text box.
  3. Set accessible description / accessible name properties on the component for screen reader interoperability.

---

### Finding F-08: General Constraint Table Error Cell Rendering
- **File Path:** `vcell-client/src/main/java/cbit/vcell/constraints/gui/ConstraintTableCellRenderer.java`
- **Component / Method:** `ConstraintTableCellRenderer.getTableCellRendererComponent(...)` (lines 50–71)
- **Implementation Detail:**
  ```java
  if (!getGeneralConstraintsTableModel().getConstraintContainerImpl().getConsistent(generalConstraint)) {
      if (isSelected){
          setForeground(java.awt.Color.white);
          setBackground(java.awt.Color.red);
      } else {
          setForeground(java.awt.Color.red);
          setBackground(java.awt.Color.white);
      }
  } else { ... }
  ```
- **Relevant Requirement:** UCONN-01 (No color alone), UCONN-03 (Text Contrast 4.5:1), STD-01 (WCAG 1.4.1), STD-02 (WCAG 1.4.3).
- **Assessment:** **Non-Compliant**.
- **Analysis:**
  - *Color as sole indicator:* Inconsistent constraints are indicated *purely* by switching text foreground to `Color.red` (unselected) or background to `Color.red` (selected). There is no icon, no "[Inconsistent]" text badge, and no prefix.
  - *Contrast failure:* Pure red (`#FF0000`, $L \approx 0.2126$) against pure white (`#FFFFFF`, $L = 1.0$) has a contrast ratio of:
    $$\frac{1.0 + 0.05}{0.2126 + 0.05} = \frac{1.05}{0.2626} = \mathbf{3.998:1} \approx 4.0:1$$
    This is strictly below the mandatory **4.5:1** WCAG AA threshold for body text. Both normal text and selected text fail contrast.
- **Recommended Remediation:**
  1. Add an explicit text column or prefix indicating status: `[Violated]` or `[Inconsistent]`.
  2. Include a warning icon in the cell.
  3. Replace `#FF0000` with a compliant dark red (e.g., `#B71C1C` or `#A30000`, luminance $\le 0.12$), which achieves $\ge 5.8:1$ contrast against white.

---

### Finding F-09: Scheduler and Simulation Status Icons
- **File Path:** `vcell-client/src/main/java/org/vcell/util/gui/StatusIcon.java` & `vcell-client/src/main/java/cbit/vcell/client/desktop/ViewJobsPanel.java`
- **Component / Method:** `StatusIcon.paintIcon(Component, Graphics, int, int)` (lines 39–96)
- **Implementation Detail:** `StatusIcon` renders an $11 \times 10$ pixel rectangle for 7 scheduler statuses: `WAITING`, `QUEUED`, `DISPATCHED`, `RUNNING`, `COMPLETED`, `STOPPED`, `FAILED`. In every state, it executes `g.fillRect(x, y, width, height)` and `g2.drawRect(x, y, width, height)`. Only `insideColor` and `borderColor` vary (e.g. green for completed, red for failed, cyan/blue for running, yellow/orange for stopped).
- **Relevant Requirement:** UCONN-01 (No color alone), STD-01 (WCAG 1.4.1).
- **Assessment:** **Requires Visual Verification / Potentially Non-Compliant in Standalone Use**.
- **Analysis:** In `ViewJobsPanel.java:1040`, the table cell renderer (`statusCellRenderer`) calls `setText((String)value)` with the textual description alongside the icon, which provides compliant dual-coding in the table view. However, in the filter checkbox bar (`ViewJobsPanel.java:762–801`), standalone `JLabel` instances display `StatusIcon` with `label.setText(" ")` (empty string). In standalone contexts, the icon's geometry is 100% identical, relying strictly on color to convey status.
- **Recommended Remediation:**
  1. Differentiate the geometric icon shapes:
     - `COMPLETED`: Checkmark ($\checkmark$) or solid circle
     - `FAILED`: Cross ($\times$) or octagon
     - `RUNNING`: Rightward arrow ($\blacktriangleright$) or rotating ring
     - `STOPPED`: Square ($\blacksquare$)
     - `QUEUED` / `WAITING`: Hourglass or pause bars ($\mathbf{I}\mathbf{I}$)
  2. Never instantiate `StatusIcon` on a `JLabel` with empty text unless an accessible name/tooltip is explicitly bound.

---

### Finding F-10: Simulation Console Keyword Highlighting
- **File Path:** `vcell-client/src/main/java/cbit/vcell/client/desktop/biomodel/SimulationConsolePanel.java`
- **Component / Method:** `SimulationConsolePanel.appendToConsole(...)` (lines 117–150)
- **Implementation Detail:**
  ```java
  case TaskStopped:
      StyleConstants.setForeground(keyWord, Color.RED);
      StyleConstants.setBold(keyWord, true);
      doc.insertString(doc.getLength(), "  " + string + "\n", keyWord);
      break;
  case Error:
      StyleConstants.setForeground(keyWord, Color.RED);
      StyleConstants.setBold(keyWord, true);
      doc.insertString(doc.getLength(), string + "\n", keyWord);
      break;
  case Warning:
      StyleConstants.setForeground(keyWord, Color.RED);
      doc.insertString(doc.getLength(), string + "\n", keyWord);
      break;
  ```
- **Relevant Requirement:** UCONN-01 (No color alone), UCONN-03 (Text Contrast 4.5:1), STD-01 (WCAG 1.4.1), STD-02 (WCAG 1.4.3).
- **Assessment:** **Potentially Non-Compliant**.
- **Analysis:**
  - `Color.RED` on white background fails the 4.5:1 contrast requirement (~4.0:1).
  - Both `Error` and `Warning` share the exact same hue (`Color.RED`). If the log message text does not inherently begin with the literal word "ERROR:" or "WARNING:", the distinction between an error and a warning rests solely on bolding vs. normal weight in identical red text.
- **Recommended Remediation:**
  1. Use compliant high-contrast colors: Dark Red (`#990000`, 7.0:1) for Error; Dark Amber/Brown (`#7A4B00`, 6.0:1) for Warning; Dark Blue (`#003399`, 8.5:1) for TaskStopped.
  2. Prepend explicit textual tags to console messages: `[ERROR]`, `[WARNING]`, `[STOPPED]`.

---

### Finding F-11: Angular Web Application Status Badges
- **File Path:** `webapp-ng/src/app/components/publication-edit/publication-edit.component.css`
- **Component / Class:** Version flag and privacy badges (lines 43–85)
- **Implementation Detail:**
  ```css
  .badge-published { background-color: #4caf50; color: white; }
  .badge-archived  { background-color: #ff9800; color: white; }
  .badge-current   { background-color: #9e9e9e; color: white; }
  .badge-unknown   { background-color: #e0e0e0; color: #666; }
  .badge-public    { background-color: #2196f3; color: white; }
  .badge-private   { background-color: #f44336; color: white; }
  .badge-shared    { background-color: #9c27b0; color: white; }
  ```
- **Relevant Requirement:** UCONN-03 (Text Contrast 4.5:1), STD-02 (WCAG 1.4.3).
- **Assessment:** **Non-Compliant (Severe Contrast Failures)**.
- **Contrast Calculations:**
  - `.badge-published` (`white` on `#4caf50`): Relative luminance of `#4caf50` is $0.326$. Contrast ratio is $\mathbf{2.79:1}$ (Fails 4.5:1 normal text; fails 3:1 large text).
  - `.badge-archived` (`white` on `#ff9800`): Relative luminance of `#ff9800` is $0.481$. Contrast ratio is $\mathbf{1.98:1}$ (Severe failure; completely unreadable for low vision).
  - `.badge-current` (`white` on `#9e9e9e`): Relative luminance is $0.350$. Contrast ratio is $\mathbf{2.62:1}$ (Fails 4.5:1).
  - `.badge-unknown` (`#666666` on `#e0e0e0`): Luminance ratio yields $\mathbf{4.44:1}$ (Marginal failure below 4.5:1).
  - `.badge-public` (`white` on `#2196f3`): Luminance ratio yields $\mathbf{3.00:1}$ (Fails 4.5:1 normal text).
  - `.badge-private` (`white` on `#f44336`): Luminance ratio yields $\mathbf{3.75:1}$ (Fails 4.5:1 normal text).
- **Recommended Remediation:**
  Update CSS variables to compliant shades meeting $\ge 4.5:1$:
  - `.badge-published`: `background-color: #2e7d32; color: #ffffff;` (4.64:1) OR `background-color: #e8f5e9; color: #1b5e20;` (8.15:1).
  - `.badge-archived`: `background-color: #e65100; color: #ffffff;` (4.52:1) OR `background-color: #fff3e0; color: #bf360c;` (7.20:1).
  - `.badge-current`: `background-color: #616161; color: #ffffff;` (4.69:1).
  - `.badge-public`: `background-color: #1565c0; color: #ffffff;` (5.37:1).
  - `.badge-private`: `background-color: #c62828; color: #ffffff;` (5.88:1).

---

### Finding F-12: Web Application Footer Accessibility Link
- **File Path:** `webapp-ng/src/app/components/footer/footer.component.html`
- **Component / Template:** `<footer>` container (lines 1–7)
- **Implementation Detail:** Footer currently renders only:
  ```html
  <footer class="bg-light p-3 text-center">
    <div class="logo"></div>
    <p>Sample project provided by <a href="https://auth0.com">Auth0</a></p>
  </footer>
  ```
- **Relevant Requirement:** UCONN-05 (Mandatory Accessibility Portal Link).
- **Assessment:** **Non-Compliant**.
- **Analysis:** Fails UConn's explicit institutional mandate requiring a direct link to `https://accessibility.uconn.edu/` in the footer of every web application.
- **Recommended Remediation:** Add official UConn compliance and accessibility links to the footer:
  ```html
  <footer class="bg-light p-3 text-center">
    <div class="logo"></div>
    <p>&copy; University of Connecticut | <a href="https://accessibility.uconn.edu/">Accessibility at UConn</a> | <a href="https://policy.uconn.edu/">Policies</a></p>
  </footer>
  ```

---

### Finding F-13: WebGL Visualization Colormaps & Plot Series (webapp-viewer)
- **File Path:** `webapp-viewer/viewer.js` & `webapp-viewer/index.html`
- **Component / Function:**
  - `viewer.js:789–794`: `vtkLookupTable` with `setHueRange(0.66667, 0.0)`
  - `viewer.js:2325–2336`: `KYMO_LUT` kymograph colormap
  - `viewer.js:1478`: `const SERIES_COLORS = ['#2a7', '#d70', '#07c', '#c2c', '#a33', '#578', '#e6b800', '#0aa', '#85f', '#b60', '#6a0', '#f58']`
  - `viewer.js:2906–2933`: `renderStatsPlot(...)`
  - `index.html:41, 43, 62, 89, 91`: UI CSS styling rules
- **Implementation Detail:**
  - Ported directly from the desktop viewer, `viewer.js` configures the VTK.js lookup table to map low $\to$ high as blue $\to$ red (0.66667 $\to$ 0.0 hue range) across a rainbow spectrum.
  - `SERIES_COLORS` pairs `#2a7` (green) with `#d70` (orange), `#a33` (red), `#b60` (brown), and `#6a0` (olive green).
  - In `renderStatsPlot`, curve polylines are rendered with solid strokes and identified in the legend solely by a colored square swatch (`span.swatch`).
  - `index.html` sets `.readout em { color: #2a7; }` and `button[aria-pressed=true] { background: #2a7; color: #fff; }`.
- **Relevant Requirement:** UCONN-01 (No color alone), UCONN-02 (Avoid red/green combinations), UCONN-03 (Text Contrast 4.5:1), STD-01 (WCAG 1.4.1), STD-02 (WCAG 1.4.3).
- **Assessment:** **Non-Compliant**.
- **Analysis:**
  - The 3D field viewer and kymograph reproduce the inaccessible rainbow colormap.
  - In `SERIES_COLORS`, green (`#2a7`) and red (`#a33`) look nearly identical under deuteranopia. The stats curves have no dash patterns, point markers, or labels.
  - Text contrast of `#2a7` against white in `.readout em` is only $\mathbf{2.97:1}$ (fails 4.5:1).
  - In dark mode, `.status.err { color: #c0392b; }` against a `#121212` background yields only $\mathbf{2.44:1}$ (fails 4.5:1).
  - `.kymo-panel .stale` (white text on `#d70`) yields only $\mathbf{3.18:1}$ (fails 4.5:1).
- **Recommended Remediation:**
  1. Add Viridis/Cividis colormaps to VTK.js and the kymograph canvas.
  2. Add dashed strokes (`stroke-dasharray`) to `renderStatsPlot` and probe traces.
  3. Replace `#2a7` in light mode with `#157347` (4.53:1 contrast against white).
  4. Ensure `.status.err` uses `#ff6b6b` in dark mode to achieve $\ge 5.0:1$ contrast.

---

### Finding F-14: Hardcoded Swing Colors & OS High-Contrast Breakdown
- **File Path:**
  - `vcell-client/src/main/java/cbit/vcell/client/VCellLookAndFeel.java`
  - `vcell-client/src/main/java/cbit/vcell/client/desktop/biomodel/BioPaxRelationshipPanel.java` (line 139)
  - `vcell-client/src/main/java/cbit/vcell/client/desktop/biomodel/ConversionPanel.java` (line 144)
  - `vcell-client/src/main/java/cbit/vcell/client/desktop/biomodel/PhysiologyRelationshipPanel.java` (line 138)
  - `vcell-client/src/main/java/cbit/vcell/client/desktop/biomodel/ReactionPropertiesPanel.java` (lines 253, 277, 341, 402)
  - `vcell-client/src/main/java/cbit/vcell/client/desktop/biomodel/ReactionRuleKineticsPropertiesPanel.java` (lines 182, 232)
  - `vcell-client/src/main/java/cbit/vcell/client/desktop/biomodel/DocumentEditor.java` (lines 534, 548)
- **Component / Method:** Swing panel constructors and initialization methods
- **Implementation Detail:** Dozens of Swing panels and check boxes explicitly execute `component.setBackground(Color.white);` without explicitly setting foreground color or checking system Look and Feel properties. Concurrently, `VCellLookAndFeel.java` forces default macOS fonts to shrink by 2 points: `defaultFont.deriveFont(defaultFont.getSize2D() - 2)`.
- **Relevant Requirement:** STD-02 (WCAG 1.4.3 Text Contrast), STD-06 (Section 508 Chapter 502 High Contrast Interoperability).
- **Assessment:** **Potentially Non-Compliant / Accessibility Fragility**.
- **Analysis:** When an operating system is configured in Dark Mode or High Contrast Dark Mode, the desktop environment supplies light or white foreground colors for labels, checkboxes, and text. Because these panels force the background to pure `Color.white` while letting child components inherit system foregrounds, text renders as white-on-white (completely invisible) or light gray on white. Additionally, artificially reducing font sizes on macOS exacerbates readability barriers for low-vision users.
- **Recommended Remediation:**
  1. Remove hardcoded `setBackground(Color.white)` calls; inherit background colors from `UIManager.getColor("Panel.background")`.
  2. If a white canvas is strictly required (e.g., schematic diagram editing), explicitly lock the foreground to `Color.black` or a high-contrast dark color.
  3. Re-evaluate font derivation in `VCellLookAndFeel.java` to prevent unnecessary font shrinkage.

---

## Scientific Visualization Considerations

Scientific visualizations in VCell present specialized accessibility challenges because color is frequently used to encode continuous scalar fields (chemical concentrations, electric potentials, flux rates) and discrete spatial geometries (compartment segmentations).

```
+--------------------------------------------------------------------------------------------------+
|                            Scientific Colormap Accessibility Comparison                          |
|                                                                                                  |
|   Colormap          Luminance Profile       CVD Safe (Deuteranopia / Protanopia)     VCell Status|
|  ---------------   ----------------------  ---------------------------------------  ------------ |
|  Rainbow / Jet     Non-monotonic (peaks)   FAIL: Confuses green/red/yellow          Current Def  |
|  Viridis           Monotonically increasing PASS: Distinct blue/teal/yellow levels   Recommended |
|  Cividis           Monotonically increasing PASS: Optimized mathematically for CVD   Recommended |
|  Batlow            Uniform gradient        PASS: Retains perceptual metric order    Recommended |
|  Okabe-Ito (Disc)  Discrete high contrast  PASS: Universally separable 8 colors     Recommended |
+--------------------------------------------------------------------------------------------------+
```

### 1. Continuous Scalar Fields (PDE Simulations & Kymographs)
- **The Problem with Rainbow (Jet):** The current `BLUERED` colormap in `DisplayAdapterService.java` and `vtkLookupTable` in `viewer.js` ranges from blue through cyan, green, yellow, and red.
  1. In deuteranopia and protanopia, the red and green ends of the spectrum collapse to very similar yellow-brown hues.
  2. The luminance curve is non-monotonic: it rises sharply in cyan, dips in green, peaks in yellow, and drops in red. This produces artificial "bands" where no actual numerical plateau exists, misleading researchers.
- **Remediation Strategy:**
  - Adopt **Viridis** as the primary default continuous colormap. Viridis progresses monotonically from dark purple through teal to bright yellow, ensuring that perceived brightness correlates strictly with scalar magnitude even in total color blindness (achromatopsia).
  - Adopt **Cividis** as an explicit colorblind-optimized option. Cividis is mathematically optimized to ensure identical perceived delta-$E$ differences for normal trichromats and individuals with red-green CVD.
  - Maintain an accessible diverging colormap (such as Blue-White-Orange or Purple-White-Green) for variables that transition across a zero threshold.

### 2. Discrete Compartments and SubVolumes (CSG & Segmentations)
- **The Problem with Procedural HSB:** `createContrastColorModel` cycles hue in steps of 0.1 ($36^\circ$). Adjacent compartments often end up with hues that are indistinguishable for CVD users (e.g., green vs. orange).
- **Remediation Strategy:**
  - For models with $\le 8$ compartments, use the **Okabe-Ito** discrete palette:
    1. Black (`#000000`)
    2. Orange (`#E69F00`)
    3. Sky Blue (`#56B4E9`)
    4. Bluish Green (`#009E73`)
    5. Yellow (`#F0E442`)
    6. Blue (`#0072B2`)
    7. Vermillion (`#D55E00`)
    8. Reddish Purple (`#CC79A7`)
  - In 2D and 3D visual segmentations, draw distinct 1–2 px boundary contours around subvolume borders so that adjacent domains are separated by high-contrast edges even if their fill colors appear similar.

### 3. 2D Multi-Curve Plots & Trajectories
- **The Problem with Hue-Only Curves:** When 5–10 variables are plotted over time in `Plot2DPanel` or `viewer.js`, thin 1.5 px lines cross and overlap. Color-blind users cannot trace individual trajectories across crossings when lines share perceived hues.
- **Remediation Strategy:**
  - Combine color with **stroke dash patterns**:
    - Trace 0: Solid
    - Trace 1: Dashed (`6, 4`)
    - Trace 2: Dotted (`2, 3`)
    - Trace 3: Dash-dot (`6, 3, 2, 3`)
    - Trace 4: Long dash (`10, 5`)
  - When data nodes are rendered, cycle **marker shapes** (circle, square, upward triangle, diamond, downward triangle, cross).
  - Ensure the plot legend renders both the stroke pattern and marker glyph inside the swatch key.

### 4. 3D Particle Visualizations (SpringSaLaD)
- **The Problem with Colored Spheres on Black Backgrounds:** Particle sites are identified solely by one of 28 color names. Under protanopia, red, green, maroon, and olive spheres floating in a 3D volume cannot be distinguished.
- **Remediation Strategy:**
  - Introduce procedural glyph variations or surface markings on particle sprites.
  - Allow users to click or hover over a particle to display a persistent textual annotation or HUD overlay indicating the particle's molecular identity and state.

---

## Implementation Checklist

This checklist is designed for VCell software engineers and code reviewers to verify accessibility compliance during future feature development and UI refactoring.

### General & Color-Blind Principles
- [ ] **Dual-Coding / Non-Color Cues:** Color is never the sole visual indicator of status, state, selection, or hierarchy (WCAG 1.4.1 / UConn UCONN-01).
- [ ] **No Problematic Pairings:** UI elements avoid opposing Red/Green or Red/Black color combinations without redundant text/icons (UConn UCONN-02).
- [ ] **Contrast - Normal Text:** All text smaller than 18 pt (or 14 pt bold) achieves a contrast ratio of at least **4.5:1** against its background (WCAG 1.4.3 / UConn UCONN-03).
- [ ] **Contrast - Large Text:** All text 18 pt or larger (or 14 pt bold) achieves a contrast ratio of at least **3:1** against its background (WCAG 1.4.3 / UConn UCONN-03).
- [ ] **Contrast - UI Components:** Interactive control boundaries, toggles, checkmarks, and active indicators achieve at least **3:1** contrast against adjacent backgrounds (WCAG 1.4.11).
- [ ] **Contrast - Focus Visible:** Keyboard focus indicators are clearly visible and achieve at least **3:1** contrast against adjacent colors (WCAG 2.4.7 / 1.4.11).

### Forms & Input Validation
- [ ] **Form Error Identification:** When an input field fails validation, an explicit text error message or warning icon is displayed; the field is not merely outlined in red (WCAG 3.3.1 / UConn Content Guides).
- [ ] **Field Boundary Visibility:** Form input field borders achieve at least **3:1** contrast against surrounding container backgrounds (WCAG 1.4.11).
- [ ] **Required Fields:** Required fields are indicated with text or an asterisk accompanied by an explicit legend or descriptor (UConn Content Guides).

### Scientific Plots, Charts & Visualizations
- [ ] **Plot Line Discrimination:** In multi-series 2D line plots, curves vary in stroke pattern (solid, dashed, dotted, dash-dot) or thickness, not solely in hue (WCAG 1.4.1 / REC-02).
- [ ] **Point Markers:** When data points are plotted, different series use distinct marker shapes (circle, square, triangle, diamond), not solely color (REC-02).
- [ ] **Plot Legend Dual-Coding:** Legend keys display the corresponding stroke pattern and marker symbol alongside the color swatch (WCAG 1.4.1).
- [ ] **CVD-Safe Colormaps:** Continuous scalar fields default to or support perceptually uniform, monotonic colormaps (Viridis, Cividis) rather than rainbow/jet (REC-01).
- [ ] **Discrete Categorical Palettes:** Geometry subvolumes and compartment trees utilize colorblind-safe discrete palettes (e.g. Okabe-Ito) rather than unconstrained HSB cycles (REC-01).
- [ ] **SubVolume Contours:** Discrete spatial regions feature high-contrast boundary lines separating adjacent compartments (WCAG 1.4.11).

### Web Applications (`webapp-ng` & `webapp-viewer`)
- [ ] **Badge & Chip Contrast:** Status chips and version badges meet 4.5:1 text contrast in both light and dark modes (WCAG 1.4.3).
- [ ] **Footer Compliance:** The web application footer contains a direct hyperlink to `https://accessibility.uconn.edu/` (UConn Policy Procedures).
- [ ] **Link Differentiation:** Body hyperlinks feature an underline or maintain a 3:1 contrast against surrounding body text with an additional hover indicator (UConn Content Guides).

### Desktop Swing Client (`vcell-client`)
- [ ] **High Contrast Interoperability:** Custom Swing components avoid hardcoding `setBackground(Color.white)` without respecting system Look and Feel properties (`UIManager.getColor(...)`) (Section 508 Chapter 5).
- [ ] **Font Sizing Integrity:** System font scales are respected and not artificially reduced across the client interface.
- [ ] **Table Status Cells:** Table cells conveying state combine text labels or distinct icons with cell background highlights.

---

## Items Requiring Manual Verification

Static source code analysis can identify color definitions and procedural logic, but several runtime and environmental factors cannot be conclusively validated through code inspection alone:

1. **Operating System High Contrast & Dark Mode Rendering:**
   - *Scope:* The desktop Swing client across Windows, macOS, and Linux.
   - *Why Manual:* Java Swing's interaction with host OS themes (e.g. Windows High Contrast Mode, macOS Dark Appearance) varies by JDK version and desktop manager. Manual verification is required to confirm that text remains visible and that panels hardcoding white backgrounds do not render white text on white backgrounds.

2. **VTK WebAssembly / WebGL Color Transfer Accuracy:**
   - *Scope:* 3D spatial field rendering in `webapp-viewer` and VTK.js.
   - *Why Manual:* WebGL fragment shaders and GPU color interpolation can alter the actual screen-rendered luminance of colormaps. Visual inspection with color blindness simulation tools (e.g. Sim Daltonism, Color Oracle) is needed to verify that scalar field features remain legible under deuteranopia and protanopia.

3. **Semi-Transparent Layer Blending:**
   - *Scope:* Translucent confidence intervals in `BandRenderer.java` (`PlotRenderers.java`), particle sphere specular shading in `SpringSaladViewerCanvas.java`, and ROI overlays in `OverlayImageDisplayJAI.java`.
   - *Why Manual:* Alpha-blended layers composite dynamically against variable underlying content. Final contrast ratios must be measured with an eyedropper tool (such as Colour Contrast Analyser) over actual rendered simulation scenes.

4. **Dynamic Table Cell Selection and Focus Outlines:**
   - *Scope:* Complex Swing tables (`ViewJobsPanel`, `BioModelNodeEditableTree`, `SimulationStatusDetailsPanel`).
   - *Why Manual:* Selection colors and keyboard focus rings dynamically invert foreground and background colors. Verification must ensure that selection backgrounds do not extinguish text contrast or obliterate status icons.

5. **Physical Print and Grayscale Export:**
   - *Scope:* Exporting 2D plots and simulation snapshots to PDF, PNG, or postscript formats.
   - *Why Manual:* Exported files are frequently printed on black-and-white printers or included in scientific publications. Verifying that exported plots remain interpretable when converted to grayscale requires visual testing.

---

## Recommended Next Steps

To achieve full compliance with UConn's Digital Accessibility Policy and WCAG 2.1 Level AA ahead of the April 2027 institutional deadline, the VCell team should execute the following prioritized roadmap:

### Phase 1: Immediate High-Impact Remediations (1–3 Months)
1. **Fix Web Application Badge Contrast:**  
   Update `webapp-ng/src/app/components/publication-edit/publication-edit.component.css` to replace low-contrast badge colors (`#4caf50`, `#ff9800`, `#9e9e9e`, `#2196f3`) with compliant shades meeting $\ge 4.5:1$ text contrast.
2. **Add UConn Accessibility Footer Link:**  
   Update `webapp-ng/src/app/components/footer/footer.component.html` to include the mandatory link to `https://accessibility.uconn.edu/`.
3. **Connect Unused `COLORBLIND20` Palette:**  
   In `vcell-client`, update `MoleculeVisualizationPanel.java` and `ClusterVisualizationPanel.java` to initialize `globalPalette` from `ColorUtil.COLORBLIND20` instead of `TABLEAU20` and `DARK20`.
4. **Remediate Constraint Table Contrast:**  
   Update `ConstraintTableCellRenderer.java` to use a compliant dark red (`#B71C1C`) achieving $\ge 4.5:1$ contrast against white, and prepend a textual prefix (e.g. `[Inconsistent]`) to eliminate color-only communication.

### Phase 2: Scientific Visualization & Plotting Upgrades (3–6 Months)
1. **Implement CVD-Safe Colormaps (Viridis & Cividis):**  
   Add Viridis and Cividis LUT generators to `DisplayAdapterService.java` (desktop) and `viewer.js` (web). Expose colormap selection in `DisplayAdapterServicePanel` and default new views to Viridis.
2. **Add Redundant Stroke Patterns & Symbols to 2D Plots:**  
   Refactor `Plot2DPanel.drawLinePlot(...)` and `PlotRenderers.java` to support cyclic stroke dash arrays and diverse node marker shapes. Update `LineIcon` in `PlotPane.java` to render stroke dashes and symbols in the legend.
3. **Replace Procedural SubVolume HSB with Okabe-Ito:**  
   Refactor `DisplayAdapterService.createContrastColorModel()` to map initial discrete indices to the Okabe-Ito palette, ensuring distinct compartment colors in geometry viewers and cell renderers.

### Phase 3: Form Validation & Platform Hardening (6–12 Months)
1. **Replace Silent Red-Border Validation with Accessible Feedback:**  
   Refactor panels utilizing `GuiConstants.ProblematicTextFieldBorder` (`OutputOptionsPanel`, `MeshSpecificationPanel`, `TableCellEditorAutoCompletion`) to provide companion text error messages and warning icons.
2. **Eliminate Hardcoded Swing Backgrounds:**  
   Audit and refactor Swing panels that hardcode `setBackground(Color.white)` without setting foregrounds, migrating them to standard `UIManager` system palette references to prevent High Contrast / Dark Mode breakage.
3. **Establish CI Accessibility Testing:**  
   Integrate automated accessibility linting (e.g., `axe-core` in Angular unit tests / Cypress e2e) in `.github/workflows/ci.yml` to automatically catch color contrast and link attribute regressions on web pull requests.
