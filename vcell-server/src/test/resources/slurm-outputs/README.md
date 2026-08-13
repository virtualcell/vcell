# Captured slurm command output

Real output from mantis, produced by the exact commands `SlurmProxy` issues:

```
sacct  -P -u <user> -j <ids> -o jobid%25,jobname%25,state%13
squeue -p <partition> -u <user> -O jobid:25,name:25,state:13,batchhost
```

`SlurmOutputParsingTest` parses each of these. **When a slurm output is found that does not
parse correctly, capture it here and add a case to that test** — that is the point of the
directory. Keep the output verbatim, including trailing whitespace and header, so the fixture
stays a faithful record of what slurm actually emitted.

| file | what it captures |
|---|---|
| `sacct-multistep-running.txt` | a SpringSaLaD multirun: parent + `.batch` + `.extern` + 20 numbered steps named `singularity` |
| `sacct-simple-completed.txt` | an ordinary finished job: parent + `.batch` + `.extern` |
| `sacct-no-matching-jobs.txt` | header only — sacct still prints it when nothing matches |
| `sacct-empty-output.txt` | genuinely empty, e.g. the command failed |
| `sacct-cancelled-by-user.txt` | `CANCELLED by 54321` — the state field contains spaces |
| `sacct-terminal-states.txt` | the scheduler-side kills the monitor must report |
| `squeue-running.txt` | live queue: parent allocations only, no step lines |
| `squeue-empty.txt` | header only |
