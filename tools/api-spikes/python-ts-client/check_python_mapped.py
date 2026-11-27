"""Same payloads, with Float64 mapped onto Python float (--schema-mappings Float64=float). Reports, does not fail."""
import json, math
from spike_client.models.time_series import TimeSeries
ts = TimeSeries.from_json('{"variable":"v","times":[0.0],"values":[[1.25,2.0],["NaN","Infinity"],["-Infinity",2.0]]}')
v = ts.values
print("strings parse to real floats:", math.isnan(v[1][0]) and v[1][1] == math.inf and v[2][0] == -math.inf, v)
try:
    TimeSeries.from_json('{"variable":"v","times":[0.0],"values":[[1.25,null]]}'); print("null accepted: True")
except Exception as e:
    print("null accepted: False  (nullable dropped by the generator)")
out = ts.to_json()
try:
    json.loads(out, parse_constant=lambda c: (_ for _ in ()).throw(ValueError(c))); print("to_json is strict JSON: True")
except ValueError:
    print("to_json is strict JSON: False  ->", out)
print("model_dump_json:", ts.model_dump_json(), " (non-finite become null)")
