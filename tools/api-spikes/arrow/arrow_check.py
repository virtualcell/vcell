"""Arrow part of the spike (style guide 2.3/2.4): null vs NaN/+-Inf survive an IPC stream, endianness is recorded.

  python arrow_check.py write <file>   write the reference stream
  python arrow_check.py read  <file>   read a stream (e.g. one Java wrote) and check it
"""
import math, sys
import pyarrow as pa

VALUES = [1.25, None, float("nan"), float("inf"), float("-inf"), 2.0]

def write(path):
    table = pa.table({"time": pa.array([0.0, 0.5, 1.0, 1.5, 2.0, 2.5], pa.float64()),
                      "p0": pa.array(VALUES, pa.float64())},
                     metadata={"variable": "Ran_cyt", "points": "[[1.0,2.5]]"})
    with pa.OSFile(path, "wb") as f, pa.ipc.new_stream(f, table.schema) as w:
        w.write_table(table)

def read(path):
    fails = 0
    def check(what, ok, detail=""):
        nonlocal fails
        print(("PASS " if ok else "FAIL ") + what + ("" if ok else f"   <- {detail}")); fails += 0 if ok else 1
    with pa.OSFile(path, "rb") as f:
        reader = pa.ipc.open_stream(f)
        table = reader.read_all()
    col = table.column("p0").combine_chunks()
    vals = col.to_pylist()
    check("null stays null (validity bitmap), distinct from NaN", vals[1] is None and col.null_count == 1, vals)
    check("NaN stays NaN (a valid, non-null value)", math.isnan(vals[2]), vals)
    check("+Infinity / -Infinity survive", vals[3] == math.inf and vals[4] == -math.inf, vals)
    check("finite values are bit-exact", vals[0] == 1.25 and vals[5] == 2.0, vals)
    md = {k.decode(): v.decode() for k, v in (table.schema.metadata or {}).items()}
    check("schema metadata carries the request echo", md.get("variable") == "Ran_cyt", md)
    check("native endianness is little (this platform)", sys.byteorder == "little", sys.byteorder)
    try:
        import numpy as np
        arr = col.to_numpy(zero_copy_only=False)
        check("to NumPy: null becomes NaN only on explicit conversion (validity kept in Arrow)", np.isnan(arr[1]), arr)
    except ImportError:
        print("(numpy not installed: skipped NumPy conversion)")
    print("ALL PASSED" if fails == 0 else f"{fails} FAILED")
    return fails

if __name__ == "__main__":
    if sys.argv[1] == "write": write(sys.argv[2])
    else: sys.exit(1 if read(sys.argv[2]) else 0)
