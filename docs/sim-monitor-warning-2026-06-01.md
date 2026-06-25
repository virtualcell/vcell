# Simulation Monitor Warning — 2026-06-01

Status: **investigating root cause** (symptom analysis complete; mitigation proposed)
Environment: **prod** (`vcell.cam.uchc.edu`)
Author: triaged via Loki + `kubectl` log analysis

## Trigger

The Nagios simulation monitor (`/api/v0/health?check-sim`) reported Critical
~2026-06-01 17:10 UTC:

```json
{"nagiosStatusName":"Critical","nagiosStatusCode":2,
 "message":"simulation failed (0): org.vcell.util.DataAccessException: Results are not availabe yet. Please try again later.",
 "elapsedTime_MS":56978,"tags":"DOWN"}
```

## What actually happened (symptom)

Not an outage and not data loss. The canary simulation **completed
successfully** but its results were not yet retrievable when the health check
read them, and the canary's end-to-end runtime has crept up to straddle the
monitor's tolerances.

### Evidence — canary runtimes are 50–69 s

End-to-end `JOB_WAITING → WORKEREVENT_COMPLETED` for each `vcellNagios` canary
(owner id `90825526`) over a 25-min window:

| Batch (UTC) | Durations (s) | Note |
|---|---|---|
| 16:56 | 69, 69, 63 | all over the 45 s Warning line |
| 17:01 | 51, 52, 50 | under |
| **17:10** | **61**, 55, 57 | 61 s run = the Critical alert (sim `314971234`) |
| 17:16 | 59, 59, 53 | flapping |

Every sim reached `WORKEREVENT_COMPLETED` — none failed. These are trivial
non-spatial ODE sims with near-zero compute; the ~60 s is almost entirely
**fixed pipeline/dispatch latency** (SLURM submit over SSH → queue wait →
Apptainer/SIF container start → result-file write → data-server availability).

### Why the Critical (the real mechanism)

In `vcell-api/src/main/java/org/vcell/rest/health/HealthService.java`:

- The status-wait loop is guarded by `SIMULATION_TIMEOUT = 8*60*1000` (8 min)
  — **this did not fire**; the sim completed.
- Immediately after completion, **line 250** does a single, no-retry result read:
  ```java
  // before declaring success, retrieve some data (time array is sufficient)
  RestDatabaseService.callGetDataSetTimeSeries(vcMessagingService, userLoginInfo,
      sim.getKey(), user, 0, new String[] { "t" });
  ```
  This throws `DataAccessException: Results are not availabe yet`
  (from `vcell-core/src/main/java/cbit/vcell/simdata/SimulationData.java`),
  caught at line 254 → reported Critical. `elapsedTime_MS` is merely *when* it
  happened, not a configured ceiling.

So there is a **race**: job status flips to completed before the result files
are retrievable from the data server, and the health check reads with zero grace.

### Related signal earlier the same day

Two genuine `sbatch` submission failures (SSH to `login.hpc.cam.uchc.edu`
timing out) at 14:21 (user `aca9pw`) and 16:07 (`vcellNagios`):
`HtcProxy: failed to submit SLURM job … Process timed out`. Same environment
slowness — the login node / SLURM scheduler is sluggish, inflating dispatch
time for every sim.

## Thresholds (all hardcoded — NOT configurable)

`HealthService.java` lines 37–40 are `private static final`. There is **no**
env/property wiring, and nothing in `vcell-fluxcd/kustomize/overlays/prod`
(or `base`) controls them. A change today requires a `vcell-api` rebuild +
redeploy, not an overlay edit.

| Constant | Value | Role |
|---|---|---|
| `SIMULATION_TIME_WARNING` | 45 s | elapsed > this → Warning |
| `SIMULATION_TIMEOUT` | 8 min | status-wait ceiling (did not fire) |
| `SIMULATION_LOOP_START_DELAY` | 60 s | startup delay |
| `SIMULATION_LOOP_SLEEP` | 5 min | between canary runs |

## Plan for action

### 1. Mitigation (stop the false Criticals) — `HealthService.java`
- **Add a grace/retry around the line-250 result read** (the actual fix):
  ```java
  long dataDeadline = System.currentTimeMillis() + RESULT_AVAILABILITY_GRACE; // 60_000
  while (true) {
      try { RestDatabaseService.callGetDataSetTimeSeries(...); break; }
      catch (DataAccessException e) {
          if (System.currentTimeMillis() > dataDeadline) throw e;
          Thread.sleep(3000);
      }
  }
  ```
  with `private static final long RESULT_AVAILABILITY_GRACE = 60*1000;`
- **Bump `SIMULATION_TIME_WARNING` 45 s → 90 s** so healthy-but-slow runs
  (50–69 s) stop flapping to Warning.

### 2. Optional — make thresholds ops-tunable
Wire `RESULT_AVAILABILITY_GRACE` / `SIMULATION_TIME_WARNING` to env vars read at
startup; then they can live in the prod overlay and future bumps need no rebuild.

### 3. Root cause (the real lever) — HPC dispatch latency
Mitigation only treats the symptom. Investigate why a trivial sim takes ~60 s:
SLURM scheduler / login-node load on `login.hpc.cam.uchc.edu`, SSH control-master
behavior, and Apptainer/SIF container cold-start time. Findings appended below.

### 4. Operational note — Loki retention
Prod sim pods (`submit`, `data`) retain only ~3 h in Loki; `kubectl logs`
reaches ~14 h. This made historical triage hard and is worth fixing separately.

## Root-cause investigation

### Method
Decomposed the canary lifecycle from Loki state events, then went to the HPC
side: the `submit` pod NFS-mounts the HPC scratch at `/htclogs`
(`cfs09:/vcell/htclogs`), and can `ssh` to `login.hpc.cam.uchc.edu` with the
batch key. Read the per-job `.slurm.log` / `.slurm.sub` and queried `sacct`.

### Finding 1 — it is NOT scheduling/queue latency
For the 17:10 Critical sim (`314971234`, SLURM job `2164633`):

```
sacct: Submit=13:10:55  Start=13:10:55  End=13:12:14  Elapsed=00:01:19  COMPLETED
```
`SLURM_JOB_START_TIME` = 17:10:55Z = the same second as dispatch. **Zero queue
wait** — the node (`mantis-040`) was allocated instantly. The entire ~56–79 s is
**on-node execution** for a sim that computes in well under a second.

### Finding 2 — the cost is per-job container launch overhead (the root cause)
The `.slurm.sub` runs **three sequential `singularity run --containall`
invocations** per simulation:

1. `JavaPreprocessor64` in the **batch** SIF (full JVM cold-start in container)
2. `SundialsSolverStandalone_x64` in the **solver** SIF (the actual ODE solve — trivial)
3. `JavaPostprocessor64` in the **batch** SIF again (full JVM cold-start in container)

Both SIFs are read over **NFS** (`155.37.250.155:/vcell` → `/share/apps/vcell3`,
a 2.3 PB filer at 61% use):

| SIF | Size |
|---|---|
| `vcell-batch_singularity_8.0.0.03.img` | **383 MB** (used twice: pre + post) |
| `vcell-solvers_singularity_v0.8.2.img` | **245 MB** |

So each trivial sim pays: 3× container instantiation (`--containall` namespace
setup + SIF mount/read from NFS) + 2× JVM cold-start. Compute is negligible.
Rough split from the timeline: JOB_COMPLETED is emitted by the solver wrapper at
~56 s (pre + solver), and postprocessing runs to End at 79 s (~23 s for the post
container alone) — i.e. on the order of **~20–30 s per `singularity run`**.

### Finding 3 — highly load-sensitive → the flapping
`sacct` for recent canary single-job runs shows the SAME trivial sim ranging
widely with HPC/NFS contention:

```
00:00:15  00:00:22  00:00:24  00:00:31  00:00:40   (recent)
00:01:19  (sim 314971234, the 17:10 Critical)
~00:01:09 (16:56 batch, 69 s)
```

This 15 s → 40 s → 79 s spread straddles the monitor's 45 s Warning and ~57 s
effective thresholds, which is exactly why it flaps OK/Warning/Critical. The
same NFS/login-node contention also explains the morning's `sbatch` SSH timeouts.

### Finding 4 (secondary) — broken `module load` on the compute node
The `.slurm.log` shows:
```
/var/spool/slurm/slurmd/job2164633/slurm_script: line 18: /usr/share/Modules/init/bash: No such file or directory
/var/spool/slurm/slurmd/job2164633/slurm_script: line 19: module: command not found
```
`source /usr/share/Modules/init/bash` and `module load singularity/vcell-3.10.0`
**fail** on `mantis-040` — environment-modules isn't initialized in the batch
shell. The job survives only because `singularity` happens to be on the default
PATH, so the **intended pinned singularity version is NOT being loaded**. Latent
config breakage; not the latency cause, but should be fixed (it means an
unmanaged singularity build is running the workload).

## Conclusion

The monitor warning is a **symptom of per-job Singularity container-launch
overhead** on the HPC nodes — 3 sequential `singularity run --containall`
launches (2 with JVM cold-start) per simulation, each reading a 245–383 MB SIF
over NFS — which for a trivial canary sim dominates wall-clock at ~15–80 s and
swings with NFS/login-node load. There is no scheduling delay and no real
failure; results simply lag, and the health check reads with zero grace.

### Recommended actions (priority order)
1. **Mitigate the false alerts now** (symptom): grace/retry around the
   `HealthService` line-250 result read + bump `SIMULATION_TIME_WARNING` to 90 s.
   (See "Plan for action" above.) Code-only change; not configurable via fluxcd.
2. **Reduce per-job overhead** (root cause, highest leverage):
   - Collapse the 3 container launches where possible (e.g. run pre/solve/post in
     a single container invocation, or keep an instance warm) to pay container +
     JVM startup once instead of three times.
   - Stage SIFs on **node-local disk** (or a faster tier) instead of reading
     245–383 MB over NFS on every launch.
3. **Fix `module load singularity/vcell-3.10.0`** on the compute nodes so the
   pinned, intended singularity version is used (HPC-ops / batch-script fix).
4. **Investigate HPC NFS / login-node contention** that inflates both the
   container launches and caused the morning's `sbatch` SSH timeouts.
5. **Loki retention** for prod sim pods (~3 h) — raise so future triage doesn't
   require `kubectl`/NFS archaeology.

### Open items for a finer breakdown
The `.slurm.log` has no internal timestamps, so the ~20–30 s/launch split is
inferred. A one-off instrumented canary (wrap each `singularity run` with
`date +%s.%N`) would attribute the time precisely across container-init vs
JVM-start vs NFS SIF-read.

---

## CONFIRMED ROOT CAUSE (instrumented, 2026-06-01 ~15:34 EDT)

Ran an instrumented diagnostic batch job (`VCELL_DIAG_TIMING`, SLURM job
`2165222`) on the same node (`mantis-040`) to attribute the per-launch cost.
The diagnostic reads the SIFs and uses node-local `/scratch` only — no sim data.
SLURM `Elapsed=00:10:09` (dominated by cold NFS reads). Results:

| Measurement | Time | Notes |
|---|---|---|
| `singularity --version` | 0.02 s | binary is `singularity-ce 4.0.1-jammy` on PATH |
| **NFS read batch SIF 383 MB — COLD** | **338.36 s** | **~1.1 MB/s** |
| NFS read batch SIF 383 MB — cached | 1.36 s | page cache |
| **NFS read solver SIF 245 MB — COLD** | **205.20 s** | **~1.2 MB/s** |
| container init `/bin/true`, batch, NFS (cached) | 2.2–3.1 s | ×3 |
| container + JVM `java -version`, batch, NFS (cached) | 3.7–4.8 s | ×3 |
| container init `/bin/true`, solver, NFS (cached) | ~2.0 s | ×2 |
| copy batch SIF NFS→`/scratch` (warm) | 2.06 s | |
| **container init, batch, NODE-LOCAL** | **0.15–0.25 s** | ~10× faster than NFS-cached |
| **container + JVM, batch, NODE-LOCAL** | **0.56 s** | ~7× faster than NFS-cached |

### Interpretation — it's the NFS SIF read, gated by page cache

1. **Cold read of a SIF over NFS is catastrophic: ~1 MB/s.** A 383 MB image
   takes **5.6 minutes** cold, 245 MB takes **3.4 minutes**. A node with a cold
   page cache would pay ~9 min just to read both images before any compute.
2. **Normal fast runs survive only on page cache.** `mantis-040` is a busy VCell
   node, so the SIFs usually stay warm → jobs run in ~15–80 s. When the cache is
   evicted (memory pressure from concurrent jobs / `--mem=8192M` each) or the
   node/SIF is freshly seen, the next job pays the cold read → the slow sims,
   the health-monitor flapping, and the worst-case timeouts.
3. **Even warm, NFS is ~10× slower than node-local.** singularity lazily reads
   image blocks over NFS on every launch: 2–5 s/launch on cached NFS vs
   0.15–0.56 s node-local. With 3 launches per sim that is ~10–15 s of pure
   overhead even in the best (warm) case, vs ~1 s node-local.
4. **Same NFS/filer contention explains the morning `sbatch` SSH timeouts** —
   the filer (`155.37.250.155:/vcell`) delivering ~1 MB/s for large sequential
   reads is abnormal (a healthy 10 GbE NFS path should move 383 MB in seconds,
   not minutes) and points at filer/network contention or a degraded path.

### Revised recommendations (root-cause priority)

1. **Stage SIFs on node-local disk (highest leverage, fully in VCell's control).**
   Have `SlurmProxy`'s generated `.sub` copy each SIF to `/scratch/vcell` once
   and run the 3 containers from the local copy — or pre-stage SIFs to local
   disk per node at deploy time. This makes launches sub-second and **immune to
   the cold-NFS cliff**. (Copy still pays one NFS read, but once per job/node,
   not per-launch, and node-local execution removes the ~10× per-launch tax.)
   - Bonus: collapsing the 3 launches into 1 container invocation compounds the
     win (pay container/JVM start once).
2. **Raise with HPC ops: the `/vcell` NFS filer reads at ~1 MB/s.** This is the
   real environmental fault behind both the slow sims and the `sbatch` timeouts.
   Ask them to check filer load / network path / mount options for
   `155.37.250.155:/vcell` on the `mantis` nodes.
3. **Mitigate the false alerts now** (HealthService grace/retry + Warning bump) —
   unchanged from above; buys headroom while 1–2 are pursued.
4. **Fix `module load singularity/vcell-3.10.0`** on compute nodes (currently
   failing; PATH `singularity-ce 4.0.1` is used instead of the pinned version).
5. **Loki retention** for prod sim pods — unchanged.

### Evidence
Instrumented script and raw timing log captured in this analysis; diagnostic
artifacts removed from `/share/apps/vcell3/htclogs` after capture.

---

## NODE-LEVEL TRIANGULATION (2026-06-02 ~09:23 EDT)

Re-ran an NFS-throughput probe pinned to **each** node in the `vcell` partition
(`mantis-040`…`-044`), one at a time, using `dd … iflag=direct` to bypass the
page cache and measure the true NFS transfer rate per node. All five mount the
**same** filer (`155.37.250.155:/vcell`).

| Node | batch SIF O_DIRECT | solver SIF O_DIRECT | Verdict |
|---|---|---|---|
| mantis-040 | **5.55 MB/s** | **5.32 MB/s** | 🔴 degraded |
| mantis-041 | **5.75 MB/s** | **5.00 MB/s** | 🔴 degraded |
| mantis-042 | **5.81 MB/s** | **5.09 MB/s** | 🔴 degraded |
| mantis-043 | 278.99 MB/s | 345.23 MB/s | 🟢 healthy |
| mantis-044 | 285.87 MB/s | 162.20 MB/s | 🟢 healthy |

### Conclusion — intermittent NFS-read degradation (NOT the filer, NOT a static node fault)
The table above is a **single snapshot**. The degradation is **intermittent**,
not a fixed property of nodes `040–042`:
- `mantis-040` measured **~1.1 MB/s (2026-06-01)** then **~5.5 MB/s (2026-06-02)**.
- Operator observation (J. Schaff): **`mantis-041` has been seen both fast and
  slow** at different times.

So slow/fast **moves around over time**, which points at a **transient** cause
rather than three bad NICs:
- shared-uplink / switch congestion windows,
- a flapping link or intermittent packet loss / NFS-over-TCP retransmits,
- an NFS connection/session to the `vcell3` filer (`155.37.250.155`) that goes
  bad and later recovers,
- or intermittent contention on the `vcell3` filer itself.

What we *can* say firmly: the `vcell3` export is not globally broken (some nodes
read it at 160–345 MB/s), and the slow path is on the **client/network → vcell3
filer** side. Page cache masks it whenever a SIF is already resident (cached read
≈ multi-GB/s), so cache misses during a slow window are what produce the minutes-
long SIF reads → health-monitor flapping and worst-case sim/`sbatch` timeouts.

## RESOLUTION (2026-06-02 ~15:55 EDT) — admin fixed mantis-040

While debugging live, the HPC admin **fixed the `/share/apps/vcell3` mount on
`mantis-040`**. A re-run of 12 sequential single-stream O_DIRECT 200 MB reads
immediately after (job `2167480`) confirms the repair:

| | before fix | after fix |
|---|---|---|
| backend | `155.37.250.x` pool | **`155.37.241.28:/vcell`** |
| throughput | ~5 MB/s | **864–986 MB/s** (steady ~950) |
| per-read | 200 MB in ~38 s | 200 MB in ~0.22 s |

All 12 iterations returned `rc=0` and transferred the full 209,715,200 bytes
each — real network reads (not cache), confirming O_DIRECT is honored and the
~950 MB/s is genuine. ~150–200× faster than before, and the mount now resolves
to a different backend (`241.28`), consistent with the fix re-pointing/repairing
the client→filer path. This validates the diagnosis: an **infrastructure-side
network/mount/path fault, fixable without any VCell change.** The VCell-side
mitigations remain worthwhile as defense-in-depth against recurrence.

**`mantis-042` re-probed (job `2167510`, ~16:00 EDT):** also healthy —
**837–950 MB/s** (was ~5.30 MB/s), backend now `155.37.241.29:/vcell`.

**Pattern:** both repaired nodes now resolve `/share/apps/vcell3` to the
**`155.37.241.x`** backend (`.28`, `.29`); every *slow* reading was on the old
**`155.37.250.x`** pool. This indicates the fix **re-pointed the mounts off a
degraded backend/path (`250.x`) onto a healthy one (`241.x`)** — consistent with
the flow/path-dependent root cause.

> NOTE: still verify the remaining nodes (`mantis-041/-043/-044`) and the
> `vcell12` filer are off the degraded `250.x` path; the wandering degradation
> (see two-filer probe below) means any flow still on `250.x` may be affected.

## TWO-FILER PROBE (2026-06-02 ~14:38 EDT) — disproves node- and filer-specificity

Copied the 383 MB batch SIF onto the **vcell12** filer (a different scale-out
NAS, different backend IPs) and probed every node against **both** filers with
O_DIRECT, ~minutes apart from the single-filer run above.

| Node | vcell3 backend → rate | vcell12 backend → rate |
|---|---|---|
| mantis-040 | `155.37.250.145` → **5.31 MB/s** 🔴 | `155.37.250.85` → 168.71 MB/s 🟢 |
| mantis-041 | `155.37.250.157` → 385.40 MB/s 🟢 | `155.37.251.54` → 234.36 MB/s 🟢 |
| mantis-042 | `155.37.250.132` → **5.30 MB/s** 🔴 | `155.37.250.92` → 165.13 MB/s 🟢 |
| mantis-043 | `155.37.250.140` → 287.60 MB/s 🟢 | `155.37.251.54` → **4.61 MB/s** 🔴 |
| mantis-044 | `155.37.250.159` → 335.74 MB/s 🟢 | `155.37.250.89` → 266.15 MB/s 🟢 |

### What changed vs the earlier single-filer snapshot (~1 h before)
- **`mantis-041` flipped slow→fast on vcell3** (5.75 → **385 MB/s**). Confirms the
  operator observation: not node-bound.
- **`mantis-043` is now slow on vcell12** (4.61 MB/s) while fast on vcell3. The
  slowness **moved to a different node and a different filer.**

### The decisive clue — it's per-(client,server) *flow*, not node and not server
Each logical filer is a **pool of backend NAS IPs** (automount picks one per
mount; note vcell3 resolves to `.145/.157/.132/.140/.159` across nodes, vcell12
to `.85/.251.54/.92/.251.54/.89`). Critically:

> Backend **`155.37.251.54`** served **mantis-041 at 234 MB/s** but
> **mantis-043 at 4.61 MB/s** — same server IP, ~2 min apart, opposite results.

So it is **not** the client node (041 fast and slow at different times), **not**
the filer/server (same backend IP fast to one client, slow to another), and
**not** static. It tracks the **specific client↔server network flow**.

### Most likely root cause: a degraded link in the fabric + per-flow ECMP hashing
Each NFS mount is one TCP connection (one 5-tuple) that ECMP/LAG hashes onto **one
path** through the spine/leaf fabric. If one (or few) inter-switch link is
degraded — errored/CRC, congested, or auto-negotiated to a low rate — only the
flows hashed onto it are slow; everything else runs at line rate. As mounts
re-establish over time they re-hash, so the "slow set" of (node,filer) pairs
**wanders** — exactly the observed behavior.

Strong supporting signal: the slow rate is **suspiciously consistent at
~5 MB/s** (5.31, 5.30, 4.61; ~42 Mbit/s) across unrelated node/filer pairs. Random
load contention would vary; a fixed ~5 MB/s is the signature of a **specific
degraded link** (loss-limited TCP or a port stuck at a low negotiated speed),
not generic congestion.

### Revised HPC-ops ticket
> NFS reads from the `mantis` compute nodes to the scale-out NAS are
> **intermittently** throttled to a very consistent **~5 MB/s** on individual
> client↔backend TCP flows, while other flows run at 160–385 MB/s — at the same
> time, same nodes, both the `vcell3` and `vcell12` exports. The slow set wanders
> across (node, backend-IP) pairs over minutes; the **same backend IP**
> (`155.37.251.54`) was simultaneously fast to one node and ~5 MB/s to another.
> This is not a single bad node or filer. Please check the **inter-switch / LAG /
> ECMP links** in the path between the mantis nodes and the NAS for a degraded
> member link (CRC/error counters, link speed, packet loss, queue drops), and
> NFS-client `mountstats` retransmit counts on a slow flow. Repro on any node:
> `dd if=<≥200MB file on the export> of=/dev/null bs=1M count=200 iflag=direct`.

### Interim VCell-side mitigation
Because the degradation **wanders**, draining or `--exclude`-ing specific nodes
does **not** help (a "good" node can go slow minutes later). The robust VCell-side
shield is **node-local SIF staging** (Recommendation 1): copy each SIF to
`/scratch` once per job and run the containers locally, so a job touches NFS for
the image at most once instead of 3× per launch — and even that one copy can be
retried/short-circuited if a flow is in a slow window. This does not fix the
network fault but removes VCell's exposure to it. The real fix is the fabric.

---

## FULL-PARTITION RECHECK (2026-06-02 ~16:05 EDT) — all nodes healthy on vcell3

After the admin's fix, re-probed every node in the `vcell` partition (sequential,
single-stream, 12× O_DIRECT 200 MB reads each). **All five nodes are now healthy
and all resolve `/share/apps/vcell3` to the `155.37.241.x` backend:**

| Node | vcell3 backend | throughput (12 reads) | before fix |
|---|---|---|---|
| mantis-040 | `155.37.241.28` | 945–972 MB/s | ~5 MB/s 🔴 |
| mantis-041 | `155.37.241.18` | 823–958 MB/s | flapped 5↔385 |
| mantis-042 | `155.37.241.29` | 922–966 MB/s | ~5 MB/s 🔴 |
| mantis-043 | `155.37.241.21` | 892–957 MB/s | 288 (250.x) |
| mantis-044 | `155.37.241.30` | 802–970 MB/s | fast |

Every read returned `rc=0` with the full 209,715,200 bytes transferred — genuine
O_DIRECT network reads. The degraded `155.37.250.x` backend pool is no longer in
any node's path. **vcell3 is healthy cluster-wide.** (The `vcell12` export was not
re-tested post-fix and should be confirmed separately.)

## COLLATERAL: user sim failed during the maintenance window

**Sim `315067575` (user `qiuxh66`), SLURM job `2167516` (`V_REL_315067575_0_16`),
mantis-040, 2026-06-02 12:01:26 EDT** — failed instantly. This is a **side-effect
of the maintenance**, not the NFS-throughput issue and not a model error.

`sacct`: `Elapsed 00:00:00`, `ExitCode 127:0`, `FAILED`. The `.slurm.log` shows
Singularity aborting at container creation:

```
WARNING: skipping mount of /share/apps/vcell12/users: stat /share/apps/vcell12/users: no such file or directory
FATAL:   container creation failed: mount /share/apps/vcell12/users->/share/apps/vcell12/users error:
         mount source /share/apps/vcell12/users doesn't exist
returned 255
```

VCell's monitor surfaced this as `WORKEREVENT_FAILURE: solver stopped
unexpectedly, Fail found by monitor`. Root cause: every job's `.slurm.sub`
**unconditionally bind-mounts** `/share/apps/vcell12/users` into the container,
but that path was **temporarily absent on mantis-040** while the mounts were being
repaired → instant container-creation failure. The job auto-retried (task 0 →
task 16, jobs `2167485` … `2167516`), all landing in the maintenance window.

- **Remediation:** transient — **resubmit `315067575`** now that mounts are fixed.
- **Robustness gap (worth fixing):** an unconditional bind-mount of a network path
  means *any* node where that mount is briefly absent will hard-fail every sim
  scheduled there, with an opaque "solver stopped unexpectedly" message. Consider
  (a) making the `vcell12` bind optional / pre-flighting the mount in the `.sub`
  before `singularity run`, and (b) draining nodes during mount maintenance so
  user jobs don't land on them mid-repair.

## COLLATERAL: sbatch submission timeout under a submission burst

**Sim `315070650` (user `schaff`), task 16 (`V_REL_315070650_0_16`), 2026-06-02
16:23:05 UTC** — `JOB_FAILED: unexpected response from 'sbatch'`. A third,
distinct failure mode (not NFS throughput, not the `vcell12` bind-mount).

Timeline:
- 16:10 — an earlier run (task 0) **completed normally** (~5 s). The model is fine.
- 16:22:54 — re-dispatched as task 16, part of a **burst of ~27 submissions in
  that one minute** (16:22 had 27 `submit job` events; 16:10 had 31).
- 16:23:05 — the `sbatch` submission **timed out**:

```
Executable:      Unexpected error: Process timed out
   (ssh -i … -o ControlMaster=auto -o ControlPersist=1m vcell@login.hpc.cam.uchc.edu
    "sbatch /share/apps/vcell3/htclogs/V_REL_315070650_0_16.slurm.sub")
CommandService:  failed, exhausted all hostnames
HtcProxy:        failed to submit SLURM job '…V_REL_315070650_0_16.slurm.sub'
→ JOB_FAILED: unexpected response from 'sbatch'
```
Stack: `Executable.monitorProcess` (`Executable.java:333`) ←
`CommandServiceSshNative.command` (`:120`) ← `SlurmProxy.submitJob`
(`SlurmProxy.java:1042`) ← `HtcSimulationWorker.submit2PBS` (`:648`).

### Root cause — serialized SSH→sbatch submit path overrun by the burst
VCell submits every job by `ssh`-ing to `login.hpc.cam.uchc.edu` and running
`sbatch` (one SSH command per job, `ControlMaster=auto ControlPersist=1m`). A
burst of ~27 concurrent submissions congests that channel and the per-command
timeout fires for the unlucky ones. **Exactly 2 of the ~27 failed identically at
the same instant** (~7% of the burst):

| Time (UTC) | Job |
|---|---|
| 16:23:02 | `V_REL_315070644_3_16` |
| 16:23:05 | `V_REL_315070650_0_16` |

This is the same failure *class* as the 2026-06-01 morning `sbatch` SSH timeouts
(14:21 `aca9pw`, 16:07 `vcellNagios`), now with a clear trigger: a user's own
concurrent-submission burst rather than ambient login-node load.

### Remediation
- **Transient — resubmit** `315070650` / `315070644`; they go through outside the burst.
- **Systemic (if recurring):** the submit path is a single serialized SSH→`sbatch`
  channel with a tight per-command timeout and limited retry ("exhausted all
  hostnames"). Options: submission throttling/queue pacing, a longer `sbatch`
  submission timeout + retry-with-backoff in `SlurmProxy`/`HtcSimulationWorker`,
  or a more robust submission mechanism. Compounds the per-job container-launch
  overhead already documented above.

## Failure-mode summary (2026-06-01 → 06-02)

Three independent failure modes surfaced during this investigation; only the
first is the original monitor warning, the other two are maintenance/load
collateral:

| # | Failure mode | Trigger | Status / fix |
|---|---|---|---|
| 1 | Canary monitor flaps Critical/Warning | per-job 3× container launch + cold NFS SIF read straddling the ~57 s health budget | NFS read fault fixed (infra); VCell mitigations (HealthService grace/retry, node-local SIF staging) still recommended |
| 2 | `solver stopped unexpectedly` (instant, exit 255) | `/share/apps/vcell12/users` bind-mount absent on a node mid-maintenance | transient (resubmit); make the bind optional / drain nodes during mount maintenance |
| 3 | `unexpected response from 'sbatch'` | SSH→`sbatch` submit path timed out under a ~27-job submission burst | transient (resubmit); throttle submissions / loosen timeout + retry |

---

## CONFIRMED ROOT CAUSE of the recurring `check=sim` incidents: Kubernetes DNS (2026-06-05 → 06-10)

The recurring Better Stack "Critical" incidents (failure mode #3, "unexpected
response from 'sbatch'") were investigated to ground truth. **It is not the HPC
backends, not auth, not load — it is the `submit` pod's DNS resolver timing out.**

### Evidence chain (each step ruled out a hypothesis)
1. **The incidents are real and recurring.** 06-05: 7 canary failures; 06-06: 9,
   **including 2 real-user (`aca9pw`) sims**. All identical:
   `ssh … "sbatch …"` → `ExecutableException: Process timed out` at the **10 s**
   submit timeout (`vcell.ssh.cmd.cmdtimeout`, `CommandServiceSshNative.java:120`).
2. **`login.hpc.cam.uchc.edu` is an L4 VIP (155.37.254.200) over a 10-node pool
   `mantis-sub-1..10`.** Per-connection rebalancing confirmed.
3. **A direct one-shot probe of all 10 backends:** all healthy (0.2–1.0 s). No
   single sick node.
4. **A 6-hour continuous watcher (06-07, 2100 samples):** ~20% of cold ssh
   connections were ≥1 s and **~11% ≥5 s, ~1% >10 s — spread evenly across all
   10 backends** (each 16–30 slow events). Systemic, not node-specific. No hard
   failures (all rc=0) — pure connection-setup latency.
5. **GSSAPI ruled out:** `-o GSSAPIAuthentication=no` did **not** remove the ~5 s
   spikes (still 3/30 slow).
6. **Verbose ssh of a slow connection:** the **5.1 s stall is at ssh startup,
   before connecting** (during `/etc/ssh/ssh_config … Applying options for *`).
7. **DNS isolated (`getent hosts` only, no ssh):** **13 of 80 lookups took
   ~5.02–5.08 s** — exactly the `resolv.conf` 5 s timeout. Smoking gun.

### The mechanism
The `submit` pod's `/etc/resolv.conf`:
```
nameserver 10.43.0.10            # single kube-dns/CoreDNS ClusterIP
options ndots:5
search prod.svc.cluster.local svc.cluster.local cluster.local cam.uchc.edu uchc.edu
```
This is the **classic Kubernetes "5-second DNS timeout"**: glibc sends the A and
AAAA queries in parallel over one UDP socket to the single DNS ClusterIP; a kernel
**conntrack/DNAT race drops one response**, and glibc waits the full **5 s**
before retrying. **`ndots:5` amplifies it** — VCell uses FQDNs
(`login.hpc.cam.uchc.edu`, `mantis-sub-N…`), so every lookup first tries the
cluster search domains (several queries), each a chance to hit the race.

**How it becomes a failed submission:** a single 5 s DNS stall (~5–6 s total) is
under the 10 s submit timeout and succeeds. But when two 5 s stalls stack (A/AAAA
race + the ndots search-list multiplying queries), total exceeds **10 s** → the
`sbatch` ssh times out → `JOB_FAILED` → `check=sim` Critical. ~1% of cold
submissions, which matches the observed incident rate.

### Fixes (ranked; all configuration, no code or image rebuild)
1. **`dnsConfig.options: single-request-reopen`** on `submit` + `sched` (the pods
   that ssh to HPC). Sends A and AAAA on separate sockets, eliminating the
   conntrack race. Zero change to resolution semantics. **Primary fix.**
2. **Lower `ndots` to ~2** via `dnsConfig` — VCell talks to FQDNs, so `ndots:5`
   wastes cluster-search queries per lookup; lowering cuts latency and race
   exposure. (Verify short in-cluster service names like `activemqint` still
   resolve — they will, 0 dots < 2 still appends search domains.)
3. **NodeLocal DNSCache** (cluster-wide) — per-node cache with TCP upstream; the
   robust permanent fix. Larger, infra-side change.
4. **App-side defense in depth:** retry-with-fresh-connection (re-resolves DNS;
   ~16%² ≈ 2.5% double-failure → ~6× fewer) and/or a modestly larger submit
   timeout. Helpful but secondary to fixing DNS.

Draft for (1)+(2) lives in `vcell-fluxcd` (kustomize patch on `submit`/`sched`).

### Corrections to earlier sections
- The "node-level" and "two-filer" NFS sections above (06-01/06-02) were a
  *separate, real* fault (the `155.37.250.x` NFS path), since fixed. The
  *submission-timeout* incidents (06-05+) are this DNS issue — unrelated to NFS.
- Earlier hypotheses in failure mode #3 (login-node load, single submit host)
  are superseded: the trigger is pod DNS, and "retry to a different backend"
  helps only probabilistically because all backends are equally affected.
