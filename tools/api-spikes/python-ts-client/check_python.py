"""Checks the generated (strict) Python client against style guide 2.3: null = missing, non-finite = strings."""
import json, math, sys
from spike_client.models.time_series import TimeSeries

payload = '{"variable":"Ran_cyt","times":[0.0,0.5],"values":[[1.25,null],["NaN","Infinity"],["-Infinity",2.0]]}'
failures = 0
def check(what, ok, detail=""):
    global failures
    print(("PASS " if ok else "FAIL ") + what + ("" if ok else f"   <- {detail}"))
    failures += 0 if ok else 1

try:
    ts = TimeSeries.from_json(payload)
except Exception as e:
    check("strict client parses a TimeSeries with null and non-finite strings", False, repr(e)[:300]); ts = None
if ts is not None:
    check("strict client parses a TimeSeries with null and non-finite strings", True)
    def val(x):  # the generated Float64 may be a wrapper (anyOf) or a plain value
        return getattr(x, "actual_instance", x)
    v = [[val(x) for x in row] for row in ts.values]
    print("   parsed values:", v, " types:", [[type(x).__name__ for x in row] for row in v])
    check("finite number arrives as a number", v[0][0] == 1.25, v[0][0])
    check("null arrives as None (missing)", v[0][1] is None, v[0][1])
    nan = v[1][0]
    check("\"NaN\" usable as a float (math.isnan(float(x)))", math.isnan(float(nan)), nan)
    check("\"Infinity\"/\"-Infinity\" usable as floats", float(v[1][1]) == math.inf and float(v[2][0]) == -math.inf, (v[1][1], v[2][0]))
    out = ts.to_json()
    check("client writes the payload back as valid JSON (no bare NaN tokens)", "NaN" not in out.replace('"NaN"', ""), out)
print("ALL PASSED" if failures == 0 else f"{failures} FAILED")
sys.exit(1 if failures else 0)
