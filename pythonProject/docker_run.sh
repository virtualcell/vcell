#!/bin/bash

arguments="$(echo -n "$@" | sed -E 's/(\s)+/ /g' | sed -E 's/(^(\s*))|((\s*)$)//g')" # convert any whitespace to spaces and strip ends

poetry run python -m vcell_opt.optService $arguments
