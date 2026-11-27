#!/usr/bin/env python3
"""Row index of the last output point at or before a given time.

Used by _common.sh's result_row_at_time. Kept as its own file because the search needs a
loop over the bridge, and a shell function that shells out per probe is slower and much
harder to read than the nine HTTP calls this makes.
"""
import json
import sys
import urllib.parse
import urllib.request

path, rows, target = sys.argv[1], int(sys.argv[2]), float(sys.argv[3])


def time_at(row):
    query = urllib.parse.urlencode({"path": path, "row": row, "column": 0})
    with urllib.request.urlopen("http://127.0.0.1:9123/readCell?" + query) as response:
        return float(json.load(response)["value"])


last = time_at(rows - 1)
if last < target:
    sys.exit("FATAL: the run ends at %g, before t=%g" % (last, target))

low, high = 0, rows - 1
while low < high:
    mid = (low + high + 1) // 2
    if time_at(mid) <= target:
        low = mid
    else:
        high = mid - 1

sys.stderr.write("  t=%g is row %d (t=%.6g)\n" % (target, low, time_at(low)))
print(low)
