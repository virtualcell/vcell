# VCell-AI

*An AI-powered web platform for discovering, analyzing, and exploring VCell biomodels in natural language — an LLM chatbot with tool-calling over the VCell API plus a Qdrant RAG knowledge base, built during Google Summer of Code 2025.*

**Group:** AI / Web · **PRs (non-bot):** 34 · **Releases:** 9 · **Active span:** 2025-06 → 2026-06 · **Key contributors:** KacemMathlouthi (GSoC student, 334 commits), Ezequiel-Valencia, vcellmike, jcschaff (mentor/maintainer side)

## Project background

VCell-AI is a monorepo with a **Next.js 15 / TypeScript / Tailwind / shadcn-ui frontend** and a **FastAPI / Python 3.12 backend**, fronted by **Qdrant** as a vector store and **Docker Compose** for local orchestration. The backend layers cleanly into `routes/` → `controllers/` → `services/`, with Pydantic schemas and a small `core/` for config, the OpenAI/Qdrant client singletons, and logging (`backend/app/core/`). Two capabilities define the product: (1) an **LLM chatbot with tool-calling** that lets the model reach into the live VCell biomodel database, and (2) a **RAG knowledge base** built by embedding VCell tutorial documents into Qdrant. Despite the "AI over biomodels" framing, the biomodel *data* is never ingested into the vector store — it is fetched on demand through tool calls against the VCell REST API; only tutorial/help documentation is embedded for retrieval.

**LLM provider (verified from code, not README):** OpenAI. The README's "OpenAI API, LangChain" is accurate but underspecified — the singleton (`backend/app/core/singleton.py`) wires **`langfuse.openai.AzureOpenAI`** for production (`PROVIDER=azure`) and a plain OpenAI-compatible `OpenAI` client (custom `base_url`) for local LLMs. Embeddings use Azure OpenAI `text-embedding-ada-002` (1536-dim, cosine). **No Anthropic/Claude anywhere** — `grep` for `anthropic|claude` returns nothing; the only providers in `pyproject.toml` are `openai`, `langchain`, `qdrant-client`, `langfuse`.

## Timeline

### Summer 2025 — the GSoC build (Jun–Aug, ~340 commits, KacemMathlouthi)

The substance of this project is a concentrated single-student summer build. The GitHub *PR* history is misleading: the first PR against the deployed `main` branch lands **2025-08-08** (#1), but `git log` shows the real work started **2025-06-02** ("Backend Structure Init (FastAPI, Poetry, Uvicorn)", "FastAPI Entry Point") and ran at 140 / 101 / 101 commits across June / July / August. The student developed on a `development` branch (and a personal fork) and merged into the org repo in large batches near the end, so most architectural decisions live in direct commits rather than reviewed PRs.

The June commits laid down the core:
- A **VCell DB API wrapper** service/controller/router (`Feat: VCell DB API Wrapper Service...`, 2025-06-03) exposing biomodels, simulations, VCML/SBML files (returning file *contents*, not download URLs), diagrams, and publications — these are `backend/app/routes/vcelldb_router.py`.
- The **LLM tool-calling layer** (2025-06-05): `Feat: Singleton definition of open ai client`, `Feat: Created Utils for tools definition and tools execution`, `Feat: LLM Call with tool use`, then the `LLMs Querying Controller and Router`. The dispatch is a hand-rolled OpenAI function-calling loop in `backend/app/services/llms_service.py`: build messages with `SYSTEM_PROMPT`, call `chat.completions.create(tools=…, tool_choice="auto")`, execute each returned tool via `execute_tool(name, args)` (`backend/app/utils/tools_utils.py`), append `role:"tool"` results, then a second completion to synthesize the answer. The five tools are `fetch_biomodels`, `fetch_simulation_details`, `get_vcml_file`, `search_vcell_knowledge_base` (the RAG entry point), and `fetch_publications`.
- The **RAG knowledge base** (`backend/app/services/knowledge_base_service.py` + `backend/populate_db.ipynb`): documents are converted with **`markitdown`**, chunked with LangChain's `RecursiveCharacterTextSplitter`, embedded via Azure OpenAI, and upserted into Qdrant. The notebook's source set is explicit — VCell tutorial **HTML pages and PDFs** from `vcell.org/webstart/VCell_Tutorials/` — confirming the KB is *documentation*, not biomodel content.

The frontend started as **Vite + React** (`Frontend Structure Init (Typescript, React, Vite, Tailwindcss)`, 2025-06-02) and was migrated to **Next.js** during the summer (the Next.js app-router structure and `Next JS CORS Update` appear by late August).

The August PRs are the merge-up and polish: [#1](https://github.com/virtualcell/VCell-AI/pull/1) fixed Docker/frontend build, startup automation, unified layouts and "optimized LLM requests"; [#7](https://github.com/virtualcell/VCell-AI/pull/7) added **conversation-history support** (endpoints now take a `[{role,content}]` list for full context) and **Langfuse observability** (hence the `langfuse.openai` wrapper); [#13](https://github.com/virtualcell/VCell-AI/pull/13) added tests for the DB and LLM services and hardened VCell DB connectivity; [#14](https://github.com/virtualcell/VCell-AI/pull/14)/[#15](https://github.com/virtualcell/VCell-AI/pull/15) revamped the chat UI, onboarding modal, and merged the analysis and search pages.

### Aug–Oct 2025 — releases and deployment plumbing

Nine GitHub releases ([v0.1.0](https://github.com/virtualcell/VCell-AI/releases/tag/v0.1.0) → 0.1.6.2) were cut **2025-08-28 → 2025-10-29**, but they are not feature milestones — they are a rapid sequence of **deployment fixes**: "Initial Creation", "Fix Docker Image Creation", "Remove Backends Reliance on Built in .Env File", "NextJS CORS Update", "Build Before Start", "Remove CORS Config", "Add Env to Frontend" (six of these on Aug 28–Sep 3, debugging the containerized deploy), then two stragglers on Oct 29: "Update OpenAI Client" and "Remove Date Parameters from Queries". Release/Docker mechanics live in `.github/workflows/build_containers.yml`, which builds and pushes `ghcr.io/virtualcell/vcell-ai-backend` and the frontend image on tag push. These releases bear the fingerprints of the maintainer side (vcellmike / Ezequiel-Valencia handling infra) rather than new student development. After Oct 2025 the GSoC engagement effectively ends — commit volume drops to a trickle.

### Mar 2026 — GSoC-2026 applicant wave (community PRs)

A burst of ~28 small PRs lands **2026-03-04 → 2026-03-31** from a dozen new contributors (kushvinth, M-V-RAGHUPATHI-SAI, androemeda, vivekdevaa124, Asmitha7intech, and others). These are explicitly **GSoC 2026 proposal-preparation contributions** — [#61](https://github.com/virtualcell/VCell-AI/pull/61) states it directly ("raised as part of GSoC proposal preparation to demonstrate that bnglViz can be successfully integrated"). They are mostly UI fixes, accessibility (aria-labels, password suggestions), mobile responsiveness ([#55](https://github.com/virtualcell/VCell-AI/pull/55)), a landing-page redesign ([#63](https://github.com/virtualcell/VCell-AI/pull/63)), and a few substantive ones: **Poetry→uv migration** ([#42](https://github.com/virtualcell/VCell-AI/pull/42)), **graceful prompt-based fallback for local LLMs without tool-calling** ([#46](https://github.com/virtualcell/VCell-AI/pull/46)), **standardized API response envelope across all endpoints** ([#51](https://github.com/virtualcell/VCell-AI/pull/51)), and **BNGL (BioNetGen) visualization** via the `bnglViz` library ([#61](https://github.com/virtualcell/VCell-AI/pull/61), +2960 lines). One PR ([#58](https://github.com/virtualcell/VCell-AI/pull/58)) deleted 31,788 lines (likely an erroneous checked-in build/lockfile) and was superseded by [#59](https://github.com/virtualcell/VCell-AI/pull/59).

### May–Jun 2026 — maintainer-led consolidation

Two large maintainer PRs close the window. [#66](https://github.com/virtualcell/VCell-AI/pull/66) (jcschaff, +1667/−254, "brings 76 commits from `development` into `main`") is the most significant post-GSoC change: **BMDB (BioModels.org) integration** — a parallel `bmdb_router`/`controller`/`schema` + tools and a dual-database UI (VCDB/BMDB checkboxes); a **system-prompt split** (monolithic prompt carved into base `SYSTEM_PROMPT` + per-DB `BMDB_SYSTEM_PROMPT`/`VCDB_SYSTEM_PROMPT`); and **LLM speed-ups** — `should_use_tools()` skips tool round-trips for chitchat, `select_tools_for_prompt()` regex-routes prompts to `DB_TOOLS/KB_TOOLS/PUB_TOOLS` subsets, `asyncio.gather` runs tool calls concurrently, and default rows dropped 1000→25. Notably it was opened as a **draft with a long, honest list of known issues** (debug prints left in, a `bmkeys=[]` reset bug inside the tool loop, a no-tools fast path returning the message object instead of `.content`, a wrong-host connectivity probe hitting `vcell.cam.uchc.edu` before biomodels.org) — a good window into the code's review state. Finally [#67](https://github.com/virtualcell/VCell-AI/pull/67) (androemeda, +1802/−567) adds **Auth0 authentication** end to end (`core/auth.py`, users router/service, frontend `lib/auth0.ts`, middleware, sign-in/sign-up pages), matching the README's stated auth stack.

## Notable PRs / commits

| Change | Date | Author | Why it matters |
|---|---|---|---|
| `Feat: VCell DB API Wrapper` (commit `e2007a65`) | 2025-06-03 | KacemMathlouthi | Foundation: typed FastAPI wrapper over the VCell biomodel/simulation/VCML/SBML/publication API |
| `Feat: LLM Call with tool use` (commit `fe354cfd`) | 2025-06-05 | KacemMathlouthi | The core OpenAI function-calling loop; tools defined in `tools_utils.py` |
| `populate_db.ipynb` + `knowledge_base_service.py` | 2025-06 | KacemMathlouthi | RAG ingestion: markitdown → LangChain chunking → Azure OpenAI embeddings → Qdrant (sources = VCell tutorial HTML/PDF) |
| [#1](https://github.com/virtualcell/VCell-AI/pull/1) | 2025-08-08 | KacemMathlouthi | First merge to `main`: Docker/build fixes, startup automation, unified layouts |
| [#7](https://github.com/virtualcell/VCell-AI/pull/7) | 2025-08-13 | KacemMathlouthi | Conversation history + Langfuse observability (drives `langfuse.openai` client) |
| [#13](https://github.com/virtualcell/VCell-AI/pull/13) | 2025-08-14 | KacemMathlouthi | Tests for DB/LLM services; VCell DB connectivity hardening |
| [v0.1.0](https://github.com/virtualcell/VCell-AI/releases/tag/v0.1.0)–0.1.6.2 | 2025-08-28→10-29 | vcellmike / Ezequiel-Valencia | Nine deployment-fix releases (Docker, CORS, env, OpenAI client) — not feature milestones |
| [#42](https://github.com/virtualcell/VCell-AI/pull/42) | 2026-03-12 | kushvinth | Poetry → uv migration (PEP 621, `uv.lock`, CI/Docker) |
| [#46](https://github.com/virtualcell/VCell-AI/pull/46) | 2026-03-08 | androemeda | Prompt-based tool fallback for local LLMs lacking native tool-calling |
| [#51](https://github.com/virtualcell/VCell-AI/pull/51) | 2026-03-12 | androemeda | Standardized API response envelope across all backend endpoints |
| [#61](https://github.com/virtualcell/VCell-AI/pull/61) | 2026-03-27 | androemeda | BNGL (BioNetGen) visualization via `bnglViz` (+2960 lines); explicit GSoC-2026 proposal demo |
| [#66](https://github.com/virtualcell/VCell-AI/pull/66) | 2026-05-06 | jcschaff | BMDB/BioModels.org integration, system-prompt split, LLM speed-ups, dual-DB UI (76 dev commits) |
| [#67](https://github.com/virtualcell/VCell-AI/pull/67) | 2026-06-22 | androemeda | Auth0 authentication end to end (backend + Next.js) |

## Key contributors

- **KacemMathlouthi** (334 commits) — the GSoC 2025 student; built essentially the entire platform: VCell API wrapper, LLM tool-calling layer, RAG/Qdrant pipeline, both frontend iterations (Vite→Next.js), conversation history, Langfuse.
- **Ezequiel-Valencia** (10) / **vcellmike** (3) — maintainer/infra side; deployment, container images, and the v0.1.x release sequence.
- **jcschaff** (mentor/maintainer) — post-GSoC consolidation, notably the BMDB-integration PR [#66](https://github.com/virtualcell/VCell-AI/pull/66).
- **GSoC-2026 applicant cohort** (androemeda, kushvinth, M-V-RAGHUPATHI-SAI, vivekdevaa124, Asmitha7intech, et al.) — a March 2026 wave of small UI/accessibility/feature PRs submitted as proposal-preparation work; androemeda continued into the larger BNGL and Auth0 PRs.

## Tech & stack notes

- **Frontend:** Next.js 15, TypeScript (300 KB, the largest language), Tailwind CSS, shadcn/Radix UI, React-Markdown + KaTeX for math rendering. (Originally Vite/React, migrated to Next.js in summer 2025.)
- **Backend:** FastAPI, Python 3.12, Pydantic (v2) settings. Dependency management **Poetry → uv** (migrated [#42](https://github.com/virtualcell/VCell-AI/pull/42)).
- **LLM:** OpenAI — Azure OpenAI in prod, OpenAI-compatible client for local LLMs; manual function-calling loop; `text-embedding-ada-002` embeddings. **Langfuse** for tracing. **LangChain** used only for text splitting. **No Anthropic/Claude.**
- **Vector DB:** Qdrant (cosine, 1536-dim), populated from VCell tutorial HTML/PDF via `markitdown` + LangChain chunking.
- **Auth:** Auth0 (added [#67](https://github.com/virtualcell/VCell-AI/pull/67)).
- **CI/CD:** `.github/workflows/ci.yml` (pytest on push to `main`) and `build_containers.yml` (on tag push: build + push `ghcr.io/virtualcell/vcell-ai-*` images). The notebook `backend/populate_db.ipynb` accounts for the large Jupyter byte-count in the language stats.
- **Maturity/status:** A working GSoC-2025 prototype, deployed via container images, that has not reached a stable `1.0`. Releases froze at `0.1.6.2` (Oct 2025); the main branch saw two maintainer consolidation PRs in 2026 (BMDB, Auth0) plus a March 2026 wave of small GSoC-applicant contributions. By the maintainer's own [#66](https://github.com/virtualcell/VCell-AI/pull/66) review notes, the code carries debug-print and correctness rough edges and thin test coverage on the load-bearing routing logic — i.e. early-stage, actively iterating, not production-hardened.
